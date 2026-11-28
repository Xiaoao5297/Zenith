# 夜班记录：击退消失与“踩台阶后陷进地里”

> 日期：2026-11-28
> 分支：`feat/incore-port`
> 相关测试：`tests/MobKnockbackTest.php`（13 项）
> 上服验证：`docs/manual-verification.md` 5.17

## 1. 现象

用户在测试服反馈两件事：

1. 动物不再乱弹跳了，但**攻击生物几乎没有击退**——生物只在原地抖一下，不后退。
2. 生物有时会**半个身子陷进地面**，尤其是走上一格台阶后马上停下的时候。

## 2. 根因一：击退 motion 被移动层每 tick 覆盖

`Living::knockBack()` 只做一次 `setMotion()`。但同一 tick 后续还有多个环节会重写 motion：

| 环节 | 行为 |
|---|---|
| `MoveControl::tick()` | 用 AI 目标速度覆盖 `motionX/motionZ` |
| `Creature::onUpdate()` | `onGround` 时把 `motionY` 直接设为 0 |
| `Entity::checkGroundState()` | 地面支撑分支里 `motionY = 0` |
| `Entity::recoverPartialBlockStandingHeight()` | 修正半砖站立高度时 `motionY = 0` |

结果：击退写入的速度在同一个 tick 内就被抹平，客户端几乎看不到位移。

### 修复：10 tick 击退窗口

- `Entity` 新增 `protected $knockbackTicks = 0`，`entityBaseTick()` 每 tick 递减。
- `Living::knockBack()` 在 `setMotion()` 后置 `knockbackTicks = max(当前, 10)`（约 0.5 秒）。
- 窗口内：
  - `Mob::moveInDirection()` 直接返回，不覆盖 motion；
  - `MoveControl::tick()` 直接返回；
  - `Creature` 即使贴地也继续施加重力，让 0.4 的竖直击退速度形成抛物线；
  - 落地反弹（`motionY *= -0.5`）暂缓；
  - `checkGroundState()` / `recoverPartialBlockStandingHeight()` 不再清零 `motionY`。
- 水平摩擦保持原样（地面乘方块摩擦，空中 0.98），击退距离约 1–3 格。

## 3. 根因二：`ySize` 在零位移分支不再衰减

`Entity::move()` 的台阶逻辑：

- 成功抬升 1 格时 `ySize += 0.5`，随后 `y = boundingBox->minY - ySize`（模型先“视觉下移”，再随移动逐渐收敛）；
- 正常情况下每次真实移动都会 `ySize *= 0.4`，几个 tick 就归零。

但如果生物踩上台阶后**立刻停下**，后续调用是 `move(0, 0, 0)`，走的是提前返回分支，不衰减 `ySize`：

```
t=6  踏上台阶: y = 64.5, 脚底 = 65.0, ySize = 0.5
t=7..12 静止:  y = 64.5, ySize = 0.5   ← 永远低半格
```

客户端按服务端坐标绘制，于是模型陷进地面半格。

### 修复：静止时也收敛

`move()` 的零位移分支（非玩家）在 `recoverPartialBlockStandingHeight()` 之后追加：

```php
if($this->ySize != 0.0){
    $this->ySize *= 0.4;
    if(abs($this->ySize) < 0.01){
        $this->ySize = 0.0;
    }
    $this->y = $this->boundingBox->minY - $this->ySize;
}
```

这样即使生物原地不动，`ySize` 也会在数 tick 内归零。

## 4. 测试

`tests/MobKnockbackTest.php` 自带一个最小 `Level` 桩与绕过构造函数的 `Mob` 桩，覆盖：

- 击退窗口开启且长度为 10 tick；
- 击退立即产生水平速度与向上的竖直速度；
- 击退期间 `moveInDirection()` / `MoveControl::tick()` 不覆盖 motion；
- 20 tick 内水平位移 > 0.5 格、最高脚底比起点高 0.3 格以上；
- 窗口结束后 AI 恢复推进；
- 台阶抬升后立刻停下会出现临时 `y` 偏移，静止 8 tick 内收敛到 0.01 以内。

全量：`php73 tests/run-tests.php` → **1018/1018 passed**。

## 5. 上服验证要点（5.17）

1. 空手打牛/猪/僵尸：应后退约 1 格并有轻微上抛。
2. 连续攻击：每次都生效，不被寻路吞掉。
3. 约 0.5 秒后生物恢复寻路/追击。
4. 走上一格台阶后立刻停下：模型停在台阶顶面，不陷地。
5. 反复上下台阶并停留：不应越站越下沉。
6. 弓箭/雪球：同样有位移与短促上抛。

## 6. 已知限制

- 击退窗口是固定 10 tick，未按伤害/击退强度缩放（`knockBack` 的 base 仍由调用方决定）。
- 玩家击退也走同一套 counter，但玩家物理由客户端模拟，窗口只影响服务端对 `motionY` 的清零保护。
- 击退期间 AI 完全暂停水平推进，会出现约 0.5 秒的“僵直”，与原版观感一致。
