# A* 行走/转向“一跳一跳”修复

> 分支：`feat/incore-port`
> 基线：`cb049f9`（A* 已恢复，但转向与移动仍不同步）
> 日期：2026-11-22

## 1. 目标

用户反馈：换回 A* 后，生物走路/转向仍然“一跳一跳”，而 InCore 不会。
本轮不更换寻路算法，定位并修掉 A* 路径跟随中造成抖动/滑步的代码，保持：

- 1 格台阶由 `Entity::move()` 自动抬升，不产生 0.42 跳跃；
- 拐角处身体转向与水平速度方向一致；
- 开阔地形尽早拉直 A* 折线，不在每个路点停顿；
- 鸡不会因为行走中的假跳跃持续扇翅。

## 2. 根因

不是 A* 网格本身，而是 **路径跟随层的运动方向与身体朝向脱节**，以及
**视线拉直（string pulling）执行频率过低/起点不对**。

### 2.1 motion 与 yaw 各走各的

`PathNavigate::moveToward()` 每 tick 只把身体 yaw 朝路点转，最多 30°：

```php
$this->turnToward(-atan2($dx, $dz) * (180 / M_PI));
```

但旧的 `MoveControl::tick()` 直接按“实体当前位置 -> 路点”的向量写 motion：

```php
$direction = new Vector3($dx / $horizontalDist, 0, $dz / $horizontalDist);
$this->entity->moveInDirection($direction, $step);
```

A* 路径是四方向网格，拐角是 90°。到拐角时水平速度瞬间翻转，身体却要 3 tick
才转过去；客户端看到的是“人先横着滑出去，身体再慢慢转”，配合每 tick 速度重写，
就是用户感知的“一跳一跳”。

Java 1.7.10 的 `EntityMoveHelper.func_75641_c()` 不写 motion，只做两件事：
把 yaw 以每 tick 最多 30° 转向目标，并设置标量移动速度
（`EntityMoveHelper.java:39-56`）。实体本身沿 yaw 前进，因此 **身体和速度方向永远一致**。
InCore 也是同一 tick 内同时设置 yaw 和 motion，不存在这个错位。

### 2.2 拉直只发生在“推进路点”时

旧 `tryShortcut()` 只在 `$advanced` 为真时调用，而且起点是**路径点**而不是实体当前位置：

```php
if($advanced){
    $this->tryShortcut();
}
...
$from = $points[$index];
```

Java `PathNavigate.func_75501_e()` 每 tick 都先调用 `func_75508_h()`
（`PathNavigate.java:126-131`），`func_75508_h()` 用**实体当前位置**向回找最远可视路点
（`PathNavigate.java:143` 起）。因此 Java 的 A* 折线在行进过程中持续被拉直；
旧实现要等走进路点半径才拉一次，90° 折线会保留到拐角，进一步放大 2.1 的错位。

### 2.3 台阶不是 0.42 跳跃

`Mob::$stepHeight = 1.0` 后，1 格台阶由 `Entity::move()` 的自动抬升完成；
`tests/PathFollowingSmoothnessTest` 的台阶场景记录到 `jumps=0`、`maxY=65.000`。
垂直方向仍是约 12 tick 的平滑抬升，不是原版的 0.42 跳跃。

## 3. 改动

### 3.1 `src/pocketmine/entity/ai/control/MoveControl.php`

- `tick()` 不再直接朝路点写 motion，改为沿身体 yaw 前进：
  ```php
  $direction = self::directionFromYaw($this->entity->yaw ?? 0.0);
  ```
- 新增纯函数 `directionFromYaw(float $yaw) : Vector3`：
  yaw 0 -> +Z，yaw -90 -> +X，与 `Entity::getDirectionVector()` 的水平分量一致。
- 这样 `PathNavigate` 的 30°/tick 转向直接约束速度方向，拐角变成弧线而非横滑。

### 3.2 `src/pocketmine/entity/ai/navigation/PathNavigate.php`

- 新增 `private const SHORTCUT_LOOKAHEAD = 12;`。
- `updatePathFollowing()` 每 tick 在推进路点后都执行：
  ```php
  // 原版每 tick 都从实体当前位置做一次视线拉直（PathNavigate.func_75508_h）。
  $this->tryShortcut($pos);
  ```
- `tryShortcut(Vector3 $from)` 改为接受**实体当前位置**：
  - 先尝试路径终点，开阔地形一 tick 拉直；
  - 否则从 `index + 12` 往回找最远可视点；
  - 不再要求路径至少 3 个点。
- `recalculatePath()` 建好路径后也从实体当前位置拉直一次。

### 3.3 测试

- `tests/MoveControlTest.php`：27 项。新增 `directionFromYaw` 四个方向用例，以及
  `motion_follows_yaw_not_target`、`motion_matches_body_yaw`，证明 motion 不再直接指向路点。
- `tests/AStarNavigationTest.php`：20 项。新增两点路径拉直、从当前位置拉直、拉直不选墙内点、
  每 tick 拉直的源码约束。
- 新增 `tests/PathFollowingSmoothnessTest.php`：19 项。用真实 `PathNavigate` +
  `MoveControl` + `Entity::move()` 跑逐 tick 模拟，覆盖平地斜线、轴向折线、绕墙、1 格台阶。

## 4. 验收标准

- **AC1**：`MoveControl` 的 motion 方向等于身体 yaw；`directionFromYaw(0)` 为 +Z，
  `directionFromYaw(-90)` 为 +X。可由单元测试和反射/纯函数断言。
- **AC2**：`PathNavigate` 每 tick 从实体当前位置拉直；不再存在 `if($advanced)` 才拉直的分支。
- **AC3**：逐 tick 模拟中，斜线、轴向、绕墙、台阶四类路径：
  - 终点距离 < 1.2；
  - `maxDirectionError < 0.01°`；
  - 到达前 `zeroMoveTicks == 0`；
  - 单 tick yaw 变化 <= 30.001°。
- **AC4**：1 格台阶模拟：`jumps == 0`、`maxY <= 65.001`、终点 y >= 65。
- **AC5**：全量 `php73 tests/run-tests.php` 全绿（当前 968/968）。
- **AC6**：所有改动/新增 PHP 文件 `php73 -l` 通过，`git diff --check` 干净，
  文档记录根因、验收证据与剩余风险。

## 5. 验证命令

```bash
cd /home/xiaoao/work/github/Zenith
php73 -r 'require "tests/MoveControlTest.php"; ...'
php73 -r 'require "tests/AStarNavigationTest.php"; ...'
php73 -r 'require "tests/PathFollowingSmoothnessTest.php"; ...'
php73 tests/run-tests.php
php73 -l src/pocketmine/entity/ai/control/MoveControl.php
php73 -l src/pocketmine/entity/ai/navigation/PathNavigate.php
php73 -l tests/MoveControlTest.php
php73 -l tests/AStarNavigationTest.php
php73 -l tests/PathFollowingSmoothnessTest.php
git diff --check
```

## 6. 范围外

- 不把 A* 改成 8 方向邻接；Java 原版也是 4 方向 + 每 tick 视线拉直。
- 不改 `MoveControl` 的陆上/水中速度系数。
- 不改台阶自动抬升的物理实现；0.42 跳跃仍只作为 `stepHeight` 不足时的兜底。
- 不改击退逻辑；受击后 `MoveControl` 仍可能继续推进，需上服确认击退距离。
- 不改飞行/水生生物的专用移动逻辑。

## 7. 验收

以下命令均在 `/home/xiaoao/work/github/Zenith`、PHP 7.3.33 下执行。

### AC1 motion 跟随身体 yaw

```
$ php73 -r 'require "tests/MoveControlTest.php"; ...'
27 passed, 0 failed, 27 cases
```

关键用例：

```
PASS direction_yaw_zero - yaw=0 方向=(0,0,1)
PASS direction_yaw_minus_90 - yaw=-90 方向=(1,0,0)
PASS motion_follows_yaw_not_target - 目标在 +X 但 yaw=0 时只能朝 +Z
PASS motion_matches_body_yaw - yaw=-90 时朝 +X
```

源码：`MoveControl.php:118` 使用 `directionFromYaw($this->entity->yaw)`；
`:140` 起为纯函数。**PASS**

### AC2 每 tick 从当前位置拉直

```
$ php73 -r 'require "tests/AStarNavigationTest.php"; ...'
20 passed, 0 failed, 20 cases
```

关键用例：

```
PASS shortcut_two_point_path - 两点路径也能直接选终点
PASS shortcut_uses_current_position - 从实体当前位置回看，选中最后一个点
PASS shortcut_never_selects_wall_point - 拉直不会选中墙内路点
PASS shortcut_runs_every_tick - 源码每 tick 调用 tryShortcut($pos)
```

源码：`PathNavigate.php:193-195` 每 tick 调用 `tryShortcut($pos)`；
`tryShortcut()` 在 `:313`，`recalculatePath()` 在 `:403` 也拉直一次；
已无 `if($advanced)` 分支。**PASS**

### AC3 逐 tick 平滑性

```
$ php73 -r 'require "tests/PathFollowingSmoothnessTest.php"; ...'
19 passed, 0 failed, 19 cases
```

```
PASS diagonal_reaches_target - 80 tick 后距目标 = 0.34
PASS diagonal_shortcut_early - 路径在第 1 tick 被拉直到终点
PASS diagonal_no_zero_move_ticks - 零位移 tick = 0
PASS diagonal_motion_matches_yaw - 最大方向误差 = 0 度
PASS diagonal_yaw_step_bounded - 单 tick 最大 yaw 变化 = 0 度
PASS axis_reaches_target - 80 tick 后距目标 = 0.39
PASS axis_motion_matches_yaw - 最大方向误差 = 0 度
PASS axis_no_zero_move_ticks - 零位移 tick = 0
PASS axis_yaw_turns_gradually - 单 tick 最大 yaw 变化 = 30 度
PASS wall_reaches_target - 160 tick 后距目标 = 0.36
PASS wall_motion_matches_yaw - 最大方向误差 = 0 度
PASS wall_no_zero_move_ticks - 零位移 tick = 0
PASS wall_yaw_step_bounded - 单 tick 最大 yaw 变化 = 30 度
```

**PASS**

### AC4 1 格台阶不跳

```
PASS step_reaches_target - 100 tick 后距目标 = 0.41
PASS step_climbs_to_top - 终点 y = 65
PASS step_never_jumps - 0.42 跳跃次数 = 0
PASS step_height_bounded - 最大 y = 65
PASS step_no_zero_move_ticks - 零位移 tick = 0
```

**PASS**

### AC5 全量测试

```
$ php73 tests/run-tests.php 2>&1 | tail -3
PASS TemptGoalTest::registered_chicken - Chicken 应注册 TemptGoal([295, 361, 362])

968/968 passed
```

基线 939，新增/扩展 29 项；全部通过。**PASS**

### AC6 语法、diff 与文档

```
$ php73 -l src/pocketmine/entity/ai/control/MoveControl.php
$ php73 -l src/pocketmine/entity/ai/navigation/PathNavigate.php
$ php73 -l tests/MoveControlTest.php
$ php73 -l tests/AStarNavigationTest.php
$ php73 -l tests/PathFollowingSmoothnessTest.php
No syntax errors detected  (5/5)

$ git diff --check
(no output, exit 0)
```

`HANDOFF.md`、`docs/incore/` 仍为改动前未跟踪状态，未触碰。**PASS**

## 8. 总结

### 根因

A* 网格没有错；错在路径跟随层：

1. `MoveControl` 把 motion 直接写向路点，而 `PathNavigate` 每 tick 只让身体 yaw 转 30°。
   拐角处速度瞬间翻转、身体滞后，客户端看到横滑/急停，就是“一跳一跳”。
2. `tryShortcut()` 只在推进路点时执行，且从路径点而不是实体当前位置回看，
   A* 的 90° 折线保留到拐角才被处理，放大了第 1 点。

### 修复

- `MoveControl` 沿身体 yaw 施力，对齐 Java `EntityMoveHelper`（只转 yaw + 设标量速度）。
- `PathNavigate` 每 tick 从实体当前位置做视线拉直，对齐 Java `PathNavigate.func_75508_h()`。
- 台阶高度仍为 1.0，1 格台阶自动抬升；没有重新引入 0.42 跳跃。

### 剩余风险

1. **手动观感**：转向弧线、真实地形（半砖/楼梯/栅栏/悬崖）、鸡翅膀动画仍需上服确认。
   `PathFollowingSmoothnessTest` 只覆盖平地桩、单面墙和单格台阶。
2. **性能**：`tryShortcut()` 每 tick 先尝试路径终点，长路径会做逐 0.5 格采样；
   大规模生物群时需观察 tick 开销。
3. **击退**：`MoveControl` 每 tick 仍会重写水平 motion，受击后的击退距离可能被寻路削弱；
   Ground AI 曾有 10 tick 抑制，本轮未恢复，需上服确认。
4. **A* 回退路径**：`STATE_FALLBACK` 仍会在无完整路径时直线推进，属于既有设计。
5. **实体高度/宽度差异**：逐 tick 模拟使用 0.6×1.8 的测试生物；大型生物（马等）
   的拐角半径和贴墙距离需上服观察。

## 9. Blockers

无。
