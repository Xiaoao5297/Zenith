# 生物 AI 系统 Bug 报告

> 分析对象：`src/pocketmine/entity/ai/` 及实体侧行为注册
> 分析日期：2026-08-01
> 状态：仅报告，未修改代码
> 严重级别：🔴 严重 / 🟡 中等 / 🟢 次要

---

## 🔴 #1 A* 寻路失败后直线兜底永不触发，生物直接静止

- **位置**：`src/pocketmine/entity/ai/navigation/PathNavigate.php`（`moveTo` 82-86、`recalculatePath` 212-240）

**是什么**：当 `findPath()` 找不到路径时，期望的"第一次失败重试、第二次失败走直线 `fallbackToStraight()`"逻辑永远不会执行，生物会原地完全不动。

**为什么**：
- `moveTo()` 在 IDLE 态先 `retryCount = 0` 再调用 `recalculatePath()`（内部 `++retryCount` 后为 1）。
- `findPath()` 返回 null 时，因 `retryCount < 2` 直接 return，此时 `path` 仍为 null、状态留在 PATH_FOLLOWING。
- 下一 tick `updatePathFollowing()` 检测到 `path === null` 立即 `clearPath()`（retryCount 归零、状态回 IDLE）。
- 于是每次移动都从 0 重试，`retryCount >= 2` 的分支永远到不了，`fallbackToStraight()` 成为死代码。

**怎么做**：
- 方案 A（最小改动）：`recalculatePath()` 失败且 `retryCount < 2` 时不要 return，而是保留状态等下一次 `update()` 重试；或在 `moveTo`/`update` 中不要把 `path === null` 直接当"结束"，改为触发重试计数。
- 方案 B（推荐）：将失败决策从 `recalculatePath` 移到状态机——`update()` 中检测到 `path === null` 时递增 `retryCount`，`retryCount >= 2` 才走 `fallbackToStraight()`；`clearPath()` 仍负责复位。
- 补充：`fallbackToStraight()` 的 `updateFallback()` 应使用与 `moveToward` 一致的方向计算（见 #3）。

---

## 🔴 #2 被动生物恐慌仅持续 2 tick（0.1 秒），逃跑失效

- **位置**：`PanicBehavior::__construct`、`StrollBehavior::canContinue`；实体注册处如 `Cow.php:29`、`Sheep.php:30`、`Pig.php:35`、`Chicken.php:29`、`Villager.php:35` 等 11 处

**是什么**：所有被动生物（牛/羊/猪/鸡/村民/末影人/豹猫/蘑菇牛/兔子等）受击后恐慌逃跑只持续 0.1 秒就停下。

**为什么**：
- `PanicBehavior::__construct(Mob $entity, float $speed = 0.55, int $timeout = 60)` 第 3 参 `timeout` 单位是 **tick**。
- 实体注册写的是 `new PanicBehavior($this, 0.25, 2.0)`，`2.0` 被类型强转成 `2`（tick）。
- `StrollBehavior::canContinue()` 中 `$this->timeLeft-- <= 0` 使 2 tick 后行为结束。开发者本意很可能是 2 秒（≈40 tick）。

**怎么做**：
- 将 11 处注册的 `2.0` 改为 `40`（2 秒）：`new PanicBehavior($this, 0.25, 40)`。
- 或为 `PanicBehavior` 增加秒→tick 换算的注释/校验，避免再次误用。
- 建议同时把 `timeout` 参数类型从 `int` 改为 `float` 并显式 `(int)($timeout * 20)`，从单位上杜绝歧义。

---

## 🟡 #3 寻路方向向量受 pitch 影响，生物上/下坡时水平移动减速

- **位置**：`PathNavigate.php:190-196`（`moveToward`）；对比 `PathNavigate.php:171`（`updateFallback`）

**是什么**：生物沿寻路点移动时，水平速度会被 `cos(pitch)` 缩放。瞄准高度差大的目标（`lookAt` 设置 pitch）时移动明显变慢，与 `updateFallback()` 行为不一致。

**为什么**：
- `moveToward()` 用 `$this->entity->getDirectionVector()` 求方向，其水平分量为 `-cos(pitch)·sin(yaw)` / `cos(pitch)·cos(yaw)`。
- 代码只做了 `$dir->y = 0`（清零垂直分量），没有重新归一化，也没有剔除 `cos(pitch)` 缩放。
- 对比 `updateFallback()` 直接用 `new Vector3($dx, 0, $dz)` 归一化向量，方向计算正确。

**怎么做**：
- `moveToward()` 改为与 `updateFallback()` 一致：直接用 `waypoint` 与当前位置的 `dx/dz` 构造水平归一化向量，不依赖 `getDirectionVector()`。
- 若仍需保留 `yaw` 朝向更新，可在设置 `yaw` 后单独计算方向向量。

---

## 🟡 #4 共享临时向量被污染

- **位置**：`PathNavigate.php:193-194`、`Behavior.php:86-87`

**是什么**：修改了 Entity 共享的 `temporalVector`，可能影响同 tick 内其它依赖它的调用结果。

**为什么**：
- `getDirectionVector()` 返回 `$this->temporalVector->setComponents(...)`，即 Entity 内复用的临时对象。
- `PathNavigate` 与 `Behavior::moveForward` 都执行 `$dir->y = 0`，直接篡改共享对象。
- 同一 tick 内 `getBlock`/`getPosition` 等使用 `temporalVector` 的调用可能读到被改动后的值，产生随机性位移/碰撞判断错误。

**怎么做**：
- 方向计算改用 `new Vector3($x, 0, $z)` 独立对象（与 #3 一并修复），或调用后立即 `clone`。
- 检查其它对 `temporalVector` 的读写是否有同样的覆盖风险，统一改为局部新建 `Vector3`。

---

## 🟡 #5 移动步长过小，生物整体速度远低于原版

- **位置**：`PathNavigate.php:196`、`PathNavigate.php:172`；各 Behavior 的 `moveForward`/`moveTo` 参数

**是什么**：生物移动约 0.4 格/秒（僵尸 `speed=0.65` 时），明显慢于原版（约 1.2 格/秒），与"降速到原版"目标相悖。

**为什么**：
- 步长公式 `step = speed * 0.55 * 0.06 ≈ speed * 0.033`。
- `speed=0.65` → `step ≈ 0.021` 格/tick ≈ 0.43 格/秒。
- 这是 `apply.py`/commit 71877b8 里"speed multiplier 0.28→0.033"过度下调的结果，多个系数（0.55、0.04/0.06、各行为默认 speed）叠加。

**怎么做**：
- 将 `PathNavigate` 两处步长系数提升至原版水平，例如 `speed * 0.75 * (water ? 0.4 : 0.6)` 并做归一化处理。
- 或统一由 `ai.yml`/`aiConfig` 暴露"全局速度系数"，避免逐个行为手调。
- 修正后需实测：普通走路约 0.8-1.2 格/秒、攻击追敌 1.2-1.6、恐慌 1.8-2.0。

---

## 🟡 #6 `ai.enable` 默认关闭，行为系统默认不生效

- **位置**：`Server.php:1655`（`$this->aiEnabled = ... "ai.enable" 默认 false`）、`Mob.php:33`

**是什么**：默认配置下所有生物 `behaviorsEnabled=false`，AI 行为全部不执行（只保留基础物理）。

**为什么**：`getAdvancedProperty("ai.enable", false)` 默认值取 `false`，未在仓库默认配置中开启。

**怎么做**：若该 AI 系统已稳定，将默认值改为 `true`；否则在默认配置文件（`resources/*.yml`）中显式开启，并在 README/文档说明。注意开启前先修复 #1-#5，否则生物会出现"静止/极慢"问题。

---

## 🟢 #7 史莱姆头顶碰撞判断用了 `and`，会顶进 2 格高墙

- **位置**：`SlimeBehavior.php:65`（`$colliding = ($blockUpUp->isSolid() and $blockUp->isSolid());`）

**是什么**：史莱姆遇到 2 格高的墙时不会转向，会持续顶墙。

**为什么**：需要挡住史莱姆的条件是"上方任一格是固体"，应使用 `or`；现在要求 **两格都** 是固体才阻挡，宽松了判定。

**怎么做**：`and` → `or`；并按史莱姆 `getSize()` 动态计算所需头顶空间（大于 1 格的史莱姆应检查更多层）。

---

## 🟢 #8 繁殖幼体生成在父体同一坐标

- **位置**：`InLoveBehavior.php:98-102`

**是什么**：繁殖产生的幼体与父体坐标完全重叠，可能互相卡进方块或直接穿模。

**为什么**：`$entity->setPosition($this->entity->getPosition())` 直接用了父体位置。

**怎么做**：在父体周围随机偏移（如 `x/z ± 0.3~0.8`），并确保目标格可站立；同时 `onEnd()` 重置 `inLovetime`。

---

## 🟢 #9 洞穴蜘蛛 / 猪人攻击目标错误（NETWORK_ID 用错）

- **位置**：`CaveSpider.php:31`、`PigZombie.php:32`（`new AttackEnemyBehavior($this, [20], true)`）

**是什么**：洞穴蜘蛛与猪人把 **僵尸（NETWORK_ID=20）** 当作攻击目标，而不是玩家/其它敌对生物。

**为什么**：`[20]` 实为 `Zombie::NETWORK_ID`（`Spider=35`、`CaveSpider=40`）。注册时直接复制了僵尸的值。

**怎么做**：
- 若意图是"攻击玩家"：改为 `new AttackEnemyBehavior($this, [], true)`（仅 `attackPlayer`）。
- 若意图是"攻击特定敌对生物"：填入正确 NETWORK_ID（如僵尸 `20`、骷髅 `34`、蜘蛛 `35` 等）。
- 同时见 #10：应排除自身。

---

## 🟢 #10 `AttackEnemyBehavior` 未排除自身，可能自攻

- **位置**：`AttackEnemyBehavior.php:41-49`（`shouldStart`）

**是什么**：若生物自身的 NETWORK_ID 出现在攻击列表中，会把自己当成最近敌人（距离 0）。

**为什么**：遍历 `level->getEntities()` 时未过滤 `$entity === $this->entity`。

**怎么做**：循环内加 `if($entity === $this->entity or !$entity->isAlive()) continue;`。

---

## 🟢 #11 随机环视转向跨 0 时抖动

- **位置**：`RandomLookAroundBehavior.php:54-55`

**是什么**：头部在转向目标角度跨越 0° 时方向突变，来回摆动。

**为什么**：`rotation -= 10` 固定每 tick 减 10，`signRot()` 按当前 `rotation` 符号决定转向方向；当 `rotation` 从正变负经过 0 时，方向瞬间反向。

**怎么做**：
- 改为记录目标 yaw 与起始 yaw，按目标方向插值（线性逼近目标角度），避免符号翻转。
- 或当 `abs($rotation) < 10` 时一次转到位并结束。

---

## 附：顺带发现

- **`stuckTicks` 被 `updatePathFollowing` 与 `moveToward` 共用**（阈值 5 与 5/8 混用），卡住判定会互相干扰，可能提前/延迟触发重算路径。
- **`AttackEnemyBehavior::onTick` 攻击窗口**：`distance >= 1.5` 追击，`elseif attackCooldown<=0` 攻击；距离 <1.5 且冷却未到时会空过一个 tick（可接受，但可加 else 分支保持凝视）。
- **`PathNavigate::updatePathFollowing` 的 `$dist < 0.8` 阈值**：寻路点判定进入范围偏大，生物可能"提前转弯"，建议按实体尺寸缩放。

---

## 修复优先级建议

| 优先级 | Bug | 影响 |
|---|---|---|
| P0 | #1 A* fallback 死代码 | 生物静止，AI 不可用 |
| P0 | #2 Panic 2 tick | 逃跑失效 |
| P1 | #3 方向向量 + #4 共享向量 | 移动异常/潜在随机 bug |
| P1 | #5 步长过小 | 整体速度过慢 |
| P2 | #6-#11 | 体验与细节问题 |
