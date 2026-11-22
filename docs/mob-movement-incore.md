# 生物移动：InCore 地面 AI 实验（2026-11-22 已回退）

> 记录时间：2026-11-21
> 分支：`feat/incore-port`
> 参考：`docs/incore/pocketmine/entity/Mob.php`（只读参考树）

## 0. 后续变更（2026-11-22）

**本文是 2026-11-21 地面 AI 实验的历史记录，已被取代，不要按本文描述理解当前实现。**

`033531d`（InCore 风格地面 AI）已回退，改为恢复 A* + `MoveControl`：

- `Mob::$stepHeight` 恢复为 `1.0`；1 格台阶由 `Entity::move()` 自动抬升，
  **不再用 0.42 的跳跃**（0.42 只作为 `stepHeight` 不足时的兜底保留）。
- `PathNavigate` 重新对地面生物跑 A*（`moveTo/update/clearPath/hasPath/isDone` 不再转发地面目标）。
- 删除 `Mob::usesGroundAi()` / `tickGroundAi()` / 地面漫游与击退计时等地面 AI 代码，
  `Creature` / `Blaze` 的对应分支一并移除。
- 保留 `Entity` 的部分方块站立修复（半砖/楼梯不下沉），它与地面 AI 无关。
- 测试：删除 `tests/GroundAiTest.php`，新增 `tests/AStarNavigationTest.php`。
- 同日追加：`PathNavigate` 每 tick 从**实体当前位置**做视线拉直（对齐 Java
  `PathNavigate.func_75508_h()`），`MoveControl` 改为沿身体 yaw 施加 motion
  （对齐 Java `EntityMoveHelper` 的“只转 yaw + 设速度”），解决 A* 拐角处
  “身体还没转、速度已经横着滑出去”的抖动。新增
  `tests/PathFollowingSmoothnessTest.php` 做逐 tick 模拟。
- 同日再追加：`Mob::onUpdate()` 对存活生物返回 `true`。此前它跟随
  `Entity::entityBaseTick()` 返回 `false`，`Level::doTick()` 会把生物移出
  `updateEntities`，只能等随机区块 tick 再调度，表现为移动中的生物固定间隔
  “弹一下”；InCore 在移动后直接 `$hasUpdate = true`。新增
  `tests/MobTickSchedulingTest.php`。

回退原因（用户实测反馈）：

1. 生物上一格方块时跳得过高（0.42 跳跃 + `stepHeight = 0.6`，观感远超正常台阶）。
2. 移动/转向「一跳一跳」，起步与停止有明显顿挫。
3. 鸡在行走时因为每次台阶都要跳跃而**持续扇翅膀**。

试验结论：InCore 的「无路径直线施力」在无寻路的旧版场景下不能替代 A*，
保留的部分方块站立修复才是本轮真正需要的能力。当前行为见
`docs/manual-verification.md` 5.16 与 `docs/nightshift/2026-11-22-mob-astar-movement.md`。

本文以下章节保留原样作为历史记录；其中涉及「当前实现」的表述均已不成立。

## 1. 背景

此前生物移动用「A* 寻路（`PathNavigate`）+ 每 tick 重设 motion（`MoveControl`）」两层自己实现的框架。
实际观感是：走路一顿一顿、贴墙会停、台阶前反复跳、转向和移动方向不同步。用户反馈
「你可以看一下 incore，它的生物移动是没问题的」，因此这一版把地面生物移动整体换成
InCore 的做法：**不用 A\*，由生物自己按目标方向持续施力，遇到台阶/悬崖/栅栏再做局部决策**。

## 2. 旧实现与 InCore 的差别

| 项目 | 旧实现（Zenith） | 现在（InCore 风格） |
|---|---|---|
| 路径 | A* 网格搜索 + 捷径 + 卡住重算 | 无路径，直线朝目标走 |
| 施力 | `MoveControl` 每 tick 写一次 motion，陆上 ×0.6 | `Mob::tickGroundAi()` 直接算 motion，陆上基础 0.15 |
| 朝向 | `PathNavigate::turnToward()` 沿路点转，每 tick 最多 30° | 每 tick 直接朝目标 `yaw = -atan2(dx/diff, dz/diff)`，击退/受击后 10 tick 不转向 |
| 速度来源 | 行为传 `speed`（约 0.35–0.40） | `speed / 0.25` 映射成倍率（0.40→1.6、0.35→1.4，钳制 0.4–2.0） |
| 台阶 | `stepHeight = 1.0`，靠 `Entity::move()` 抬升 | `stepHeight = 0.6`，整格台阶用 0.42 跳跃（同原版） |
| 悬崖 | A* 邻居不可走就绕 | 前方两格都空 → 停 10 tick |
| 栅栏 | 视为普通障碍，可能反复跳 | 明确识别 Fence/FenceGate/StoneWall/NetherBrickFence，停下不清 motion |
| 半砖/台阶站立 | 依赖 `onGround`，脚底可能悬空 | `Entity` 增加部分方块（<1 格高）脚底吸附 |
| 漫游 | 行为随机选点，A* 过去 | 无行为目标时每 80–200 tick 随机换目标（±30 格），1% 概率原地停留 80–200 tick |
| 击退 | `Living::knockBack()` 写入 motion，但下一 tick 被 MoveControl 覆盖 | 受击后 10 tick 内不主动施力、不转向，让击退完整生效 |

## 3. 移植内容

### 3.1 `Mob`（`src/pocketmine/entity/Mob.php`）

- `$stepHeight = 0.6`。
- `usesGroundAi() : bool`：默认 `true`；飞行/水生生物覆写为 `false`
  （目前 `Blaze` 已覆写，其余飞行生物仍走旧路径）。
- 地面目标：`setGroundFollowTarget(Vector3 $target, float $speed = 0.35)`、
  `clearGroundFollowTarget()`、`getGroundFollowTarget()`。
- 速度：`static groundSpeedMultiplier(float $speed)` → `clamp($speed / 0.25, 0.4, 2.0)`；
  `static getGroundBaseSpeed(?string $liquid)` → 陆地 0.15 / 水 0.05 / 岩浆 0.02。
- `tickGroundAi(int $tickDiff)`：完整移植 InCore `tickPm1eGroundAi` 的流程
  （计时器 → 刷新漫游目标 → 朝目标转向/设定 motion → 液体浮力或重力 → `move()` →
  半砖支撑恢复 → 方块摩擦 → `onGround` 时 `motionY *= -0.5`）。
- `checkGroundJump()`：探测前方一格与上方两格；悬崖/高墙/栅栏 → 停 10 tick；
  1 格高的可站立方块 → `motionY = max(motionY, 0.42)`。
- `refreshGroundWanderTarget()`：没有行为目标时的随机漫游。
- `attack()`：受到伤害且未被取消时设置 `groundKnockbackTicks = 10`、`groundNoRotateTicks = 10`。
- `onUpdate()`：`usesGroundAi()` 为真时走 `tickGroundAi()` + `parent::onUpdate()`，
  旧的 navigator/MoveControl 路径只留给非地面生物。

### 3.2 `PathNavigate`

地面 AI 下退化成「目标存储器」：

- `moveTo()` → `entity->setGroundFollowTarget($target, $speed)`，不再跑 A*；
- `update()` 空实现；
- `clearPath()` / `hasPath()` / `isDone()` / `getTarget()` 都基于地面目标；
- 原 A* 代码保留，供 `usesGroundAi() === false` 的生物使用。

### 3.3 `Entity`：部分方块站立

新增两个 epsilon 常量与 8 个方法：

- `THIN_BLOCK_GROUND_EPSILON = 0.001`、`PARTIAL_BLOCK_GROUND_EPSILON = 0.126`；
- `hasGroundSupport()`、`findPartialBlockGroundSupportY()`、`shouldSnapEntityFootToSupportTop()`、
  `entityFootprintIntersectsGroundSupport()`、`snapToPartialBlockGroundSupport()`、
  `recoverPartialBlockStandingHeight()`、`findEmbeddedPartialBlockGroundSupportY()`、
  `canOccupyPartialBlockStandingY()`。

作用：站在半砖、楼梯、耕地上时，脚底不会因为 `onGround` 判定失败而持续下落；
`move()` 在 `dy <= 0` 与「零位移」时也会尝试恢复站立高度。

### 3.4 `Block` / `Stair`

- `Block::getCollisionBoxes(AxisAlignedBB $bb)`：返回与世界坐标相交的碰撞盒
  （默认整块，子类可覆写）。
- `Stair::getCollisionBoxes()`：按朝向/上下半砖返回两段碰撞盒。

这两者让「脚底支撑」与「台阶高度差」都能用真实几何计算，而不是只判断方块 id。

### 3.5 `Creature`

- 地面 AI 生物直接 `return parent::onUpdate()`，物理完全交给 `Mob::tickGroundAi()`，
  避免两处同时施加重力/摩擦。
- 其他生物（飞行、玩家等）使用 InCore 的物理顺序：落地时 `motionY = 0`、
  `move()` 前后恢复部分方块站立高度、`onGround` 时 `motionY *= -0.5`。

## 4. 验证（历史记录）

当时的离线结果（`php73 tests/run-tests.php` 838/838 passed）；下列用例多数已随地面 AI 删除而变更或移除：

```bash
php73 tests/run-tests.php     # 838/838 passed（2026-11-21）
```

- `tests/GroundAiTest.php`（16 项）：基础速度、速度倍率、默认开关、`PathNavigate` 转发、
  平地走向目标/朝向/高度、到达后停下、1 格台阶能跳上去、无目标会漫游。
  **2026-11-22 已删除。**
- `tests/MobStepTest.php`：平地/1 格台阶/2 格墙的真实 `Entity::move()` 物理模拟；
  现在断言 `stepHeight = 1.0`、1 格台阶 `jumps === 0`。
- `tests/MobMovementTest.php`：当时断言 `stepHeight = 0.6`、1 格台阶需要跳跃；
  现在改为默认 1.0、1 格台阶不跳（显式 0.6 的兜底用例保留）。
- `tests/MoveControlTest.php`：旧的 MoveControl 仍对非地面生物生效（现在重新成为地面生物的主路径）。
- `tests/AStarNavigationTest.php`（2026-11-22 新增）：平地、绕墙、1 格台阶三类 A* 路径。
- `tests/PathFollowingSmoothnessTest.php`（2026-11-22 新增）：真实
  `PathNavigate + MoveControl + Entity::move()` 逐 tick 模拟；断言 motion 与 yaw 一致、
  yaw 每 tick <= 30°、拉直不选墙内点、到达前无零位移 tick、1 格台阶不跳。

上服见 `docs/manual-verification.md` 5.16。

## 5. 当时的已知差异与限制（历史，部分已失效）

> 第 1–3 条随 2026-11-22 回退已不成立：地面生物重新走 A*，漫游回到行为侧，击退也不再要求 10 tick 僵直。
> 其余条目仍可作为历史背景阅读。

1. **没有寻路**：生物只会朝目标直线走，遇到凹形地形/大坑会停住或绕不过去
   （InCore 本身也是这个行为；原版 Java 有 A*，但那是 1.8 之后的事）。
   **（已失效：A* 已恢复。）**
2. 漫游目标是 `±30` 格内的随机点，可能选到墙后/水下；超时（80–200 tick）后会重选。
   **（已失效：漫游重新由行为目标 + A* 驱动。）**
3. 击退后的 10 tick 不主动移动，视觉上会有短暂「僵直」，这是为了让击退距离完整。
   **（已失效：回退后不再有该计时，击退期间 A* 可能继续推进；击退距离仍需完整。）**
4. 飞行/水生生物（Bat/Ghast/Squid 等）仍走旧的 `PathNavigate` + `MoveControl` 路径，
   本次只改了地面生物；后续可按同样方式补 `usesGroundAi() === false` 的专用控制。
5. 客户端末影人模型高度 2.9 与服务端碰撞高度 1.8 不同，这是客户端硬编码；
   `Mob::pushAwayFromPlayers()` 只保证相机不进入模型，不改变碰撞箱。
