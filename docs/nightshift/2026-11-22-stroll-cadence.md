# 生物移速过快 / 无休止漫步修复

> 分支：`feat/incore-port`
> 基线：`e48e850`（每 tick 更新调度修复）
> 日期：2026-11-22

## 1. 目标

每 tick 更新修复后，用户反馈生物“移动速度很快，跟鬼畜一样一直漫无目的地行走”。
要求把被动生物的移动速度和漫步节奏拉回 Java 1.7.10 / MCPE 0.14 的正常观感：
走一段、停一段，而不是一直走。

## 2. 根因

`StrollBehavior`（随机漫步）的速度与启动概率是按**旧的非每 tick 更新**调过的：

- 默认速度 `0.40`，经 `MoveControl::normalizeSpeed()` 陆上 ×0.6 后为 **0.24 格/tick**。
  旧代码里 Mob 只在随机区块 tick 时更新（平均约 6 tick 一次），所以实际平均速度只有
  约 0.04 格/tick；现在每 tick 更新后，速度直接放大到 0.24，当然显得飞快。
- `shouldStart()` 是 `mt_rand(0, 40) === 0`，即 **1/41 每 tick**。
  旧代码每 6 tick 才调用一次，现在每 tick 调用，启动频率约提高 6 倍。
- `canContinue()` 到达目标后**立刻 pickTarget() 再挑一个点**，在 120 tick 超时内
  连续走 4–5 个目标；结束后又很快以 1/41 概率重新开始，所以几乎不停。

对照原版：

- Java `EntityAIWander`：`nextInt(120) != 0` 就不启动；`continueExecuting` 是
  `!navigator.noPath()`，**路径走完就结束**。
- MCPE 0.14 `RandomStrollGoal::canUse()`：随机数能被 0x78（120）整除才启动；
  `canContinueToUse()` 返回 `!navigation->isDone()`。
- MCPE 的 `RandomStrollGoal` 构造参数：牛/羊 0.8、猪/鸡 1.0（相对速度），
  不是 Zenith 当前的 0.40 直传；本轮把默认速度降到 0.25，实际 0.15 格/tick，
  与 InCore `getPm1eBaseSpeed()` 的陆地基础速度一致。

## 3. 改动

### 3.1 `src/pocketmine/entity/ai/behavior/StrollBehavior.php`

- `START_CHANCE = 120`；`shouldStart()` 改为 `mt_rand(0, 119) === 0`。
- 新增纯函数 `shouldStartRoll(int $roll) : bool`，返回 `$roll === 0`，便于离线测试。
- 默认速度 `0.40 -> 0.25`：经 `MoveControl` ×0.6 后为 **0.15 格/tick**，
  与 InCore `getPm1eBaseSpeed()` 的陆地基础速度一致。
- `canContinue()` 不再在到达时刷新目标，改为：
  ```php
  return !$this->entity->getNavigator()->isDone();
  ```
  路径走完/被清空就结束行为，之后重新等待 1/120 的启动概率。
- `FALLBACK` 状态仍算“未完成”（`PathNavigate::isDone()` 对 FALLBACK 返回 false），
  直线兜底时不会提前结束。

### 3.2 `src/pocketmine/entity/ai/behavior/RandomLookAroundBehavior.php`

- 原版 `EntityAILookIdle` 的启动概率是 `0.02`（1/50），改为 `START_CHANCE = 50`。
- `onTick()` 不再直接改 `entity->yaw`，改为只改 `headYaw`（经 `Entity::clampHeadYaw` 夹在身体两侧 75° 内）。
  身体 yaw 由 A*/移动层决定，避免边走边随机扭身体走出蛇形路线。

### 3.3 测试

新增 `tests/StrollBehaviorTest.php`（9 项）和 `tests/RandomLookAroundBehaviorTest.php`（3 项）：

- `shouldStartRoll(0)` true；`1`/`119` false；`START_CHANCE === 120`。
- 默认速度 0.25、实际 0.15 格/tick、timeout 120。
- 导航已结束（`STATE_IDLE`）时 `canContinue()` 为 false——**不会在到达后刷新目标继续走**。
- `STATE_FALLBACK` 时仍为 true。
- 张望行为：启动概率 1/50；`onTick()` 后身体 yaw 不变、`headYaw` 改变。

## 4. 验收标准

- **AC1**：`StrollBehavior::START_CHANCE === 120`，只有 roll=0 启动。
- **AC2**：默认速度 0.25；经 MoveControl 后 0.15 格/tick（InCore 陆地基础速度）。
- **AC3**：导航结束时 `canContinue()` 返回 false；FALLBACK 时返回 true。
- **AC4**：全量 `php73 tests/run-tests.php` 全绿（当前 983/983）。
- **AC5**：改动 PHP 文件 `php73 -l` 通过，`git diff --check` 干净。
- **AC6**：文档记录根因、原版对照、证据与上服观察项。

## 5. 验证命令

```bash
cd /home/xiaoao/work/github/Zenith
php73 -r 'require "tests/StrollBehaviorTest.php"; ...'
php73 tests/run-tests.php
php73 -l src/pocketmine/entity/ai/behavior/StrollBehavior.php
php73 -l tests/StrollBehaviorTest.php
git diff --check
```

## 6. 范围外

- 不改 `MoveControl` 的 0.6 陆上系数；只调整漫步行为传入的速度。
- 不改攻击/恐慌/繁殖/ tempt 的速度；它们的使用场景不同，需要单独实测。
- 不改 A*、每 tick 更新调度、台阶逻辑。

## 7. 验收

### AC1 启动概率 1/120

```
PASS start_roll_zero - roll=0 应启动
PASS start_roll_one - roll=1 不应启动
PASS start_roll_max - roll=119 不应启动
PASS start_chance_is_120 - 启动概率应为 1/120
```

源码：`StrollBehavior.php:11` `START_CHANCE = 120`；`:44-45` 使用
`mt_rand(0, 119)`。**PASS**

### AC2 速度 0.25 -> 0.15 格/tick

```
PASS default_speed_is_025 - 默认速度 = 0.25
PASS move_speed_is_015 - 陆上实际速度 = 0.15
PASS timeout_is_120 - 超时 = 120
```

`MoveControl::normalizeSpeed(0.25, false) = 0.15`；
与 InCore `getPm1eBaseSpeed()` 的陆地 0.15 一致。**PASS**

### AC3 路径结束即停

```
PASS ends_when_navigation_done - 导航结束时应结束漫步
PASS continues_during_fallback - FALLBACK 直线移动时仍继续
```

`canContinue()` 只检查超时与 `!navigator->isDone()`，不再在到达后刷新目标。**PASS**

### AC3.1 张望只转头

```
PASS start_chance_is_50 - 原版 EntityAILookIdle 为 0.02（1/50）
PASS keeps_body_yaw - 身体 yaw 应保持 10，实际 = 10
PASS turns_head - 头应转向目标，实际 = 0
```

**PASS**

### AC4 全量测试

```
$ php73 tests/run-tests.php 2>&1 | tail -3
PASS TemptGoalTest::registered_chicken - Chicken 应注册 TemptGoal([295, 361, 362])

983/983 passed
```

基线 971 + 12 项。**PASS**

### AC5 语法与 diff

```
$ php73 -l src/pocketmine/entity/ai/behavior/StrollBehavior.php
No syntax errors detected
$ php73 -l tests/StrollBehaviorTest.php
No syntax errors detected
$ git diff --check
(no output, exit 0)
```

**PASS**

### AC6 文档

本文件、`docs/mob-movement-incore.md` 第 0 节、`docs/manual-verification.md` 5.16
已同步。**PASS**

## 8. 总结

### 根因

每 tick 调度修复后，`StrollBehavior` 暴露了两个按旧的低频更新调出来的参数：

1. 默认速度 0.40（实际 0.24 格/tick）比 InCore 陆地基础 0.15 快 60%。
2. 1/41 的每 tick 启动概率，加上到达后立刻刷新目标，导致生物几乎一直在走。

### 修复

- 速度 0.40 -> 0.25（实际 0.15 格/tick，与 InCore 陆地基础速度一致）。
- 启动概率 1/41 -> 1/120，对齐 Java `EntityAIWander` / MCPE `RandomStrollGoal`。
- 随机张望：只改 `headYaw`，不再改身体 `yaw`；启动概率对齐原版 1/50。
- 到达路径终点就结束行为，之后重新等待随机启动，不再连续刷新目标。

### 关于“直接照搬 InCore”

InCore 的 `StrollBehavior` 实际上没有被 Cow/Pig/Sheep 注册（这些生物走的是
`Mob::tickPm1eGroundAi()` 的随机目标 + 0.15 基础速度）。因此本轮照搬的是它真正生效的
**陆地速度 0.15 格/tick**；漫步节奏则采用原版/MCPE 的 1/120 + 到点即停，避免 InCore
ground AI 那种持续选点的“永动机”观感，同时保留用户要求的 A*。

### 剩余风险

1. **上服确认**：被动生物应走一段、停一段；不再出现快速持续乱走。
2. 其它行为（攻击 0.325、恐慌 0.55、Tempt 1.0）仍是直接传速度，未在本轮校准；
   若上服发现追击/吸引速度异常，需要单独按 InCore multiplier 模型调整。
3. A* 仍负责路径；大幅地形或复杂障碍下的观感需人工确认。
4. 张望行为只改头部后，站住时身体不再跟着转；若希望身体也缓慢转向，需要单独的 LookControl 层。

## 9. Blockers

无。
