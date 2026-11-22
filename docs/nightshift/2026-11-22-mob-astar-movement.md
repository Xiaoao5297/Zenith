# 2026-11-22 生物移动：恢复 A* 与正常台阶高度

## 1. 目标

用户反馈（原文）：

> 1.生物（现在以猪为例子，其他生物也一样）跳上一个方块的距离过高，建议改成正常跳跃距离（参照java/mcpe/incore或cn.minecraft.wiki）
> 2.生物转向/移动会一跳一跳的。
> 3.换回A*
> 4.因为1.导致鸡在行走的时候因为跳跃会一直扇翅膀

本轮把 `033531d`（InCore 风格地面 AI）替换掉的地面移动恢复为「A* 寻路 + MoveControl」，
并把 `Mob::$stepHeight` 恢复为 1.0：1 格台阶由 `Entity::move()` 的自动抬升完成，
不再触发 0.42 的跳跃。这样同时解决「跳得过高」「一走一跳」「鸡一直扇翅膀」，
并保留 `Entity` 的部分方块站立修复（半砖/楼梯不下沉）。

## 2. 验收标准

| 编号 | 标准 | 验证方式 |
|---|---|---|
| AC1 | 地面生物不再有 Ground AI：`src/` 中不存在 `usesGroundAi`、`tickGroundAi`、`setGroundFollowTarget`、`getGroundBaseSpeed` 等符号；`Mob::$stepHeight === 1.0`；`PathNavigate` 的 `moveTo/update/clearPath/hasPath/isDone` 不再按 Ground AI 分支 | `grep -rn`、反射读取默认属性、代码审阅 |
| AC2 | 1 格台阶由物理自动抬升：真实 `Entity::move()` 模拟中 `jumps === 0`、`maxY <= 65.001`、不倒退、能站上台阶；平地不跳不倒退；2 格墙挡住且不跳 | `tests/MobStepTest.php` 全部 PASS |
| AC3 | `needsStepJump()` 在默认 `stepHeight = 1.0` 时对 1 格台阶返回 false；显式 0.6 时才返回 true（兜底语义保留） | `tests/MobMovementTest.php` 全部 PASS |
| AC4 | A* 寻路可离线验证：平地路径到达目标、绕墙、上一格台阶三类场景均生成正确路径；`PathNavigate` 源码不含 `0.42` 跳跃 | 新增 `tests/AStarNavigationTest.php` 全部 PASS |
| AC5 | 全量测试全绿（删除 `GroundAiTest`，新增 A* 测试后总数不少于 938） | `php73 tests/run-tests.php` |
| AC6 | 所有改动/新增 PHP 文件 `php73 -l` 通过，`git diff --check` 干净；文档记录验收与风险 | 命令输出 + 本文 `## 7. 验收` / `## 8. 总结` |

## 3. 当前状态与约束

- 当前 HEAD：`093e98b`，分支 `feat/incore-port`，全量测试 `938/938`。
- `033531d` 引入 Ground AI：`Mob::$stepHeight = 0.6`、`tickGroundAi()`、`PathNavigate` 转发地面目标、
  `Creature` 对地面生物跳过物理；`Blaze` 覆写 `usesGroundAi() === false`。
- `033531d^` 的行为：`$stepHeight = 1.0`，`Mob::onUpdate()` 始终走 navigator → MoveControl → parent，
  1 格台阶由 `Entity::move()` 自动抬升，无 0.42 跳跃。
- 部分方块站立（`recoverPartialBlockStandingHeight()`）与 `Entity::move()` 台阶逻辑是后续修复，
  不属于 Ground AI，需保留。
- 约束：Tab 缩进、PHP 7.3（禁止 PHP 8 语法）、不得 `git add -A`、不得改动 `HANDOFF.md` / `docs/incore/`、
  `src/pocketmine/network/protocol/v84/` 为 CRLF 不动。
- 已知无关脏文件：`HANDOFF.md`、`docs/incore/`（未跟踪，保留）。

## 4. 任务与切片（文件级，互不重叠）

### 切片 A：移除 Ground AI，恢复 A* 主循环（impl A）
文件：
- `src/pocketmine/entity/Mob.php`
- `src/pocketmine/entity/Creature.php`
- `src/pocketmine/entity/Blaze.php`
- `src/pocketmine/entity/ai/navigation/PathNavigate.php`

内容：
1. `Mob`：`$stepHeight = 1.0` 并更新注释；删除 `usesGroundAi()`、全部 `ground*` 字段/方法、
   `tickGroundAi()`、`refreshGroundWanderTarget()`、`checkGroundJump()`、`stopGroundMovement()`、
   `getGroundLiquidType()`、`getGroundBaseSpeed()`、`groundSpeedMultiplier()`；删除 `onUpdate()` 的
   Ground AI 分支；删除 `attack()` 覆写及 `EntityDamageEvent` import。保留 `moveInDirection()`、
   `needsStepJump()`、`pickGroundY()`、`pushAwayFromPlayers()` 与行为/AI 代码。
2. `Creature`：删除 `if($this instanceof Mob and $this->usesGroundAi()) return parent::onUpdate(...)`；
   保留部分方块站立恢复与现有物理。
3. `Blaze`：删除 `usesGroundAi()` 覆写及其注释。
4. `PathNavigate`：删除 `moveTo()`、`update()`、`clearPath()`、`hasPath()`、`isDone()` 中的
   `usesGroundAi()` 分支，恢复 A* 状态机为所有地面生物的唯一路径。

### 切片 B：更新台阶/移动测试（impl B）
文件：
- `tests/MobStepTest.php`
- `tests/MobMovementTest.php`
- 删除 `tests/GroundAiTest.php`

内容：
- `MobStepTest` 头注释改为「stepHeight = 1.0，1 格台阶自动抬升，不跳」；
  1 格场景断言 `jumps === 0`、`maxY <= 65.001`，保留能上台/不倒退；2 格墙断言不变。
- `MobMovementTest`：默认 `stepHeight` 断言 1.0；`needsStepJump(64,65,true,false)` 为 false；
  保留显式 0.6 时 true 的兜底用例；移除 InCore 0.6 文案。
- 删除 `GroundAiTest.php`（Ground AI 已移除）。

### 切片 C：新增 A* 离线测试（impl C）
文件：
- 新增 `tests/AStarNavigationTest.php`

内容：用 `ReflectionClass::newInstanceWithoutConstructor()` 构造 `PathNavigate`，
注入 `NodeEvaluator`（桩 `Level`）及 `horizontalRange/verticalRange/maxIterations/waterCost`，
反射调用私有 `findPath()`。至少覆盖：
1. 平地 `(0.5,64,0.5) -> (5.5,64,0.5)` 路径非空、终点正确、每个点可站立；
2. 中间 3 格高墙（x=3，z=-2..2），路径绕行且终点正确；
3. 地面从 x=2 起抬高 1 格，路径终点 y=65；
4. `PathNavigate.php` 源码不含 `0.42`（跳跃只由 `Mob::moveInDirection` 兜底）。

### 切片 D：文档（impl D）
文件：
- `docs/mob-movement-incore.md`
- `docs/manual-verification.md`
- 本文 `docs/nightshift/2026-11-22-mob-astar-movement.md`

内容：在 `mob-movement-incore.md` 顶部标注「已被 A* 恢复取代」，保留历史记录并说明原因；
`manual-verification.md` §5.16 改为 A* + 1 格台阶不跳、不扇翅；本文补验收命令输出与风险。

## 5. 验证计划

```bash
cd /home/xiaoao/work/github/Zenith
php73 -l <每个改动/新增 PHP 文件>
php73 tests/run-tests.php
git diff --check
grep -rn "usesGroundAi\|tickGroundAi\|setGroundFollowTarget\|groundSpeedMultiplier\|getGroundBaseSpeed" src tests
```

预期：
- 全量测试全绿；`MobStepTest` 的 1 格场景 `jumps=0`、`maxY<=65.001`；
- grep 无 Ground AI 符号（文档历史段落允许出现，`src/`/`tests/` 必须为空）；
- `Mob` 默认 `stepHeight` 为 1.0；
- A* 三类路径均非空且终点正确。

## 6. 范围外

- 不调整 A* 启发式、邻居生成、MoveControl 速度系数（除非测试暴露真实回归）。
- 不改 Java/MCPE 的 0.42 跳跃常量本身；它只作为 `stepHeight` 不足时的兜底。
- 不处理飞行/水生生物的专用移动逻辑。
- 不改网络协议、资源、配置默认值。

## 7. 验收

验收命令均在 `/home/xiaoao/work/github/Zenith`、PHP 7.3.33 下执行。

### AC1 Ground AI 符号与 stepHeight

```
$ grep -rn "usesGroundAi\|tickGroundAi\|setGroundFollowTarget\|clearGroundFollowTarget\|getGroundFollowTarget\|groundSpeedMultiplier\|getGroundBaseSpeed\|groundKnockbackTicks\|groundNoRotateTicks" src tests
(no output)

$ grep -n "stepHeight" src/pocketmine/entity/Mob.php | head -3
28:	 * 不触发 0.42 跳跃，0.42 只在 stepHeight 不足时兜底。
30:	protected $stepHeight = 1.0;
```

`PathNavigate` 的五个状态方法已无 Ground AI 分支；`Blaze` 不再覆写；`Mob` 的
`EntityDamageEvent` import 与 `attack()` ground 计时一并删除。**PASS**

### AC2 1 格台阶物理模拟

```
PASS flat_ground_advances - 20 tick 后 x=4.7
PASS flat_ground_never_jumps - jumps=0
PASS flat_ground_never_reverses - max backward=0
PASS walks_up_one_block_step - 40 tick 后 x=8.9
PASS stands_on_step_top - y=65
PASS step_climb_never_jumps - jumps=0
PASS step_climb_never_reverses - max backward=0
PASS step_climb_height_is_bounded - maxY=65
PASS two_block_wall_blocks - x=1.7
PASS two_block_wall_never_jumps - jumps=0
10 cases, 0 failed
```

独立验证还在 `/tmp` 副本把 `stepHeight` 改回 `0.6` 做变异测试，得到
`FAIL step_climb_never_jumps - jumps=1`、`FAIL step_climb_height_is_bounded - maxY=65.16`，
说明该测试确实能抓住回归。**PASS**

### AC3 needsStepJump 默认值

```
22 cases, 0 failed
```

包含 `no_jump_on_one_block`（默认 1.0）、`jump_when_step_height_too_low`（显式 0.6）、
`mob_step_height_is_one`（反射读默认值 = 1.0）。**PASS**

### AC4 A* 离线路径

```
PASS flat_path_found - 路点数 = 5
PASS flat_endpoint - 终点 = (5.5, 64, 0.5)
PASS wall_path_found - 路点数 = 11
PASS wall_path_goes_around - 最大 |z-0.5| = 3
PASS wall_path_never_crosses_wall - 路点不应落在墙体内
PASS step_path_found - 路点数 = 5
PASS step_endpoint_y65 - 终点 = (5.5, 65, 0.5)
PASS step_path_vertical_steps - 相邻路点最大垂直跨度 = 1
PASS same_start_and_goal_returns_null
PASS unwalkable_goal_returns_null
PASS navigator_has_no_own_jump - PathNavigate.php 不应包含 0.42 跳跃常量
16 cases, 0 failed
```

**PASS**

### AC5 全量测试

```
$ php73 tests/run-tests.php 2>&1 | tail -3
PASS TemptGoalTest::registered_chicken - Chicken 应注册 TemptGoal([295, 361, 362])

939/939 passed
```

独立验证连续跑 3 次，均为 `939/939 passed`；`AStarNavigationTest` 单独重复 5 次均
`16 passed, 0 failed`。数量关系：`938 - 16 (GroundAiTest) + 1 (MobMovement 新增兜底用例)
+ 16 (AStarNavigationTest) = 939`。**PASS**

### AC6 语法、diff 与文档

```
$ php73 -l src/pocketmine/entity/Mob.php
$ php73 -l src/pocketmine/entity/Creature.php
$ php73 -l src/pocketmine/entity/Blaze.php
$ php73 -l src/pocketmine/entity/ai/navigation/PathNavigate.php
$ php73 -l tests/MobStepTest.php
$ php73 -l tests/MobMovementTest.php
$ php73 -l tests/AStarNavigationTest.php
No syntax errors detected  (7/7)

$ git diff --check
(no output, exit 0)
```

PHP 8 语法扫描无命中（仅 docblock `@return int|null` 误报）；`HANDOFF.md`、`docs/incore/`
仍为改动前未跟踪状态，未被本任务触碰。**PASS**

## 8. 总结

### 改了什么

- 删除 `033531d` 引入的 Ground AI：`Mob` 的地面字段/方法、`PathNavigate` 的转发分支、
  `Creature` 的提前返回、`Blaze` 的覆写全部移除。
- `Mob::$stepHeight` 恢复 1.0：1 格台阶由 `Entity::move()` 自动抬升，不再发 0.42 跳跃；
  0.42 只在 `stepHeight` 不足时兜底。
- 地面生物重新走 A* → `MoveControl` → `Creature` 物理；保留部分方块站立恢复。
- 删除 `tests/GroundAiTest.php`，更新 `MobStepTest` / `MobMovementTest`，新增
  `tests/AStarNavigationTest.php`（16 项，含平地、绕墙、上台阶、边界、无 0.42）。
- 文档：`docs/mob-movement-incore.md` 顶部标注该文为已回退的历史实验；
  `docs/manual-verification.md` 5.16 改为 A* + 平滑上一格、鸡不持续扇翅。

### 验证方式

- 全量 `php73 tests/run-tests.php`：939/939，连续 3 次。
- 三个相关测试单独运行全绿；对 `stepHeight=0.6` 的变异测试能被 `MobStepTest` 捕获。
- 全部改动 PHP 文件 lint 通过，`git diff --check` 干净。
- 独立 verifier 逐条核对 AC1–AC5，均 PASS；AC6 在本节与 `## 7` 补齐后关闭。

### 剩余风险

1. **击退控制**：A* 的 `MoveControl` 每 tick 会重写水平 motion，受击后的击退距离可能被
   下一 tick 的寻路推进削弱；Ground AI 曾有 10 tick 抑制，本次按“恢复旧 A*”未保留。
   离线无法验证观感，需上服确认；若击退明显变短，可后续在 `MoveControl` 加受伤短计时。
2. **手动观感**：转向平滑度、真实地形（半砖/楼梯/栅栏/悬崖）与鸡翅膀动画只能在服务器
   客户端观察；离线测试覆盖物理与路径，不覆盖客户端动画。
3. **A* 回退路径**：`PathNavigate::STATE_FALLBACK` 在找不到完整路径时仍会直线推进，
   这不是 Ground AI，但也不是严格意义的“全程 A*”；属于既有设计。
4. **玩家 stepHeight**：`Player::$stepHeight = 0.6` 与本任务无关，未改动。

### 后续人工确认

按 `docs/manual-verification.md` 5.16 上服逐条观察，重点：
1 格台阶不再起跳、猪/牛行走不顿挫、鸡行走不扇翅、僵尸绕墙平滑、击退距离是否可接受。

## 9. Blockers

无。所有验收命令已通过。
