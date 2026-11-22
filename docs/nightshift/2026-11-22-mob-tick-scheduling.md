# 生物固定间隔“弹跳”：每 tick 更新调度修复

> 分支：`feat/incore-port`
> 基线：`70eb384`（A* 平滑转向已提交）
> 日期：2026-11-22

## 1. 目标

用户反馈：A* 平滑转向修复后，问题仍在；表现为**固定时间（约 0.5 秒）所有移动中的生物一起“弹/跳”一下**，
站住不动的生物不会。要求定位根因并修复，使移动中的生物连续、平滑地更新。

## 2. 根因

不是 A* 路径形状，也不是台阶跳跃，而是 **活着的 Mob 没有留在 Level 的每 tick 更新队列里**。

调用链：

1. `Mob::onUpdate()` 末尾返回 `parent::onUpdate()` 的结果。
2. `Creature::onUpdate()` / `Living` / `Entity::onUpdate()` 返回 `$hasUpdate`；
   普通存活 tick 中 `Entity::entityBaseTick()` 在 `Entity.php:990` 初始化为 `false`，
   `:1034` 原样返回；`Entity::onUpdate()` 在 `:1112` 返回它，因此
   `Mob::onUpdate()` 也返回 `false`。
3. `Level::doTick()` 的实体循环：
   ```php
   foreach($this->updateEntities as $id => $entity){
       if($entity->closed or !$entity->onUpdate($currentTick)){
           unset($this->updateEntities[$id]);
       }
   }
   ```
   （`Level.php:861-865`）返回 `false` 就把生物从更新队列移除。
4. 之后只有 `Level::tickChunks()`（每 2 tick 一次，`Level.php:883`）随机选中区块时，
   才对该区块实体 `scheduleUpdate()`（`Level.php:1094-1096`）。当前
   `resources/world.yml` 的 `chunk-ticking` 是 `per-tick: 16`、`tick-radius: 3`
   （`Level.php:472-473` 的 40/4 只是配置缺失时的代码兜底），所以同一只生物通常
   隔若干 tick 才被重新加入队列并更新一次。
5. 结果：移动中的生物“更新一次 → 停几 tick → 再更新一次”，客户端插值看到固定间隔的
   位移爆发，表现就是所有移动生物同步“弹一下”。站住的生物没有位移，所以看不出来。

对照 InCore：`docs/incore/pocketmine/entity/Mob.php` 的地面移动在 `move()` 后直接
`$hasUpdate = true;`（`onUpdatePm1e` 尾部，约 `:386`），因此它每 tick 都留在更新队列，
不会出现这种固定间隔的弹跳。这正是用户说“InCore 没有”的原因。

> 说明：2026-11-22 早些时候的 A* 修复（motion 跟随 yaw、每 tick 视线拉直）解决的是
> 拐角横滑/转向不同步，是另一个真实问题；它不能修复本次的更新调度问题。

## 3. 改动

### 3.1 `src/pocketmine/entity/Mob.php`

`onUpdate()` 末尾：

```php
$return = parent::onUpdate($currentTick);
...
// 活着的生物必须每 tick 留在更新队列里，与 InCore / 原版一致。
return $return || !$this->closed;
```

- 存活 Mob 即使 `Entity::entityBaseTick()` 返回 `false`，也返回 `true`，不再被
  `Level::doTick()` 移除。
- 死亡/已关闭路径保持原样：死亡分支仍交给 `Creature`/`Entity` 处理 `deadTicks` 与
  despawn；`closed` 时返回 `false`。
- 该修改只影响 `Mob` 及其子类，不影响 `Player`/`Human`。

### 3.2 测试

新增 `tests/MobTickSchedulingTest.php`（3 项）：

- `alive_mob_returns_true`：桩 Level 下真实调用 `Mob::onUpdate(1)`，必须返回 `true`。
- `alive_mob_returns_true_next_tick`：第 2 tick 仍返回 `true`。
- `mob_stays_in_update_queue`：模拟 `Level::doTick()` 的 `updateEntities` 移除逻辑，
  连续 5 tick 后实体仍在队列中。

## 4. 验收标准

- **AC1**：`Mob::onUpdate()` 对存活生物连续返回 `true`。
- **AC2**：模拟 Level 更新队列 5 tick 后，生物没有被移除。
- **AC3**：全量 `php73 tests/run-tests.php` 全绿（当前 971/971）。
- **AC4**：改动的 PHP 文件 `php73 -l` 通过，`git diff --check` 干净。
- **AC5**：文档记录根因、修复、证据与上服确认项。

## 5. 验证命令

```bash
cd /home/xiaoao/work/github/Zenith
php73 -r 'require "tests/MobTickSchedulingTest.php"; ...'
php73 tests/run-tests.php
php73 -l src/pocketmine/entity/Mob.php
php73 -l tests/MobTickSchedulingTest.php
git diff --check
```

## 6. 范围外

- 不修改 `Level::tickChunks()` 的随机区块调度；修复点在于让 Mob 在每 tick 的实体循环中
  保持更新，而不是依赖区块 tick。
- 不修改 A* / `MoveControl` / 台阶高度；这些已在 `70eb384` 中处理。
- 不修改其它实体（Item、TNT、矿车等）的更新返回语义。

## 7. 验收

### AC1 存活 Mob 连续返回 true

```
$ php73 -r 'require "tests/MobTickSchedulingTest.php"; ...'
PASS alive_mob_returns_true - 活着的生物第 1 tick 应返回 true
PASS alive_mob_returns_true_next_tick - 活着的生物第 2 tick 仍应返回 true
PASS mob_stays_in_update_queue - 5 tick 后 updateEntities 仍有生物，实际 = 1
3 passed, 0 failed, 3 cases
```

`Mob.php` 末尾为 `return $return || !$this->closed;`。**PASS**

### AC2 模拟 Level 更新队列

`mob_stays_in_update_queue` 使用与 `Level::doTick()` 相同的
`if($entity->closed or !$entity->onUpdate($t)) unset(...)` 逻辑，
连续 5 tick 后队列中仍有 1 个生物。**PASS**

### AC3 全量测试

```
$ php73 tests/run-tests.php 2>&1 | tail -3
PASS TemptGoalTest::registered_chicken - Chicken 应注册 TemptGoal([295, 361, 362])

971/971 passed
```

基线 968 + 新增 3 项。**PASS**

### AC4 语法与 diff

```
$ php73 -l src/pocketmine/entity/Mob.php
No syntax errors detected in src/pocketmine/entity/Mob.php
$ php73 -l tests/MobTickSchedulingTest.php
No syntax errors detected in tests/MobTickSchedulingTest.php
$ git diff --check
(no output, exit 0)
```

**PASS**

### AC5 文档

根因、InCore 对照、改动与上服确认项已记录在本文件；
`docs/mob-movement-incore.md` 第 0 节与 `docs/manual-verification.md` 5.16 已同步。**PASS**

## 8. 总结

### 真正根因

`Mob::onUpdate()` 跟随 `Entity::entityBaseTick()` 返回 `false`，`Level::doTick()`
据此把生物移出 `updateEntities`。之后只有 `Level::tickChunks()` 随机选中区块时
才重新 `scheduleUpdate()`，所以移动中的生物是“隔几 tick 更新一次”的爆发式移动；
客户端插值/动画表现为固定间隔的同步弹跳。站住的生物没有位移，因此不弹。

InCore 的 `onUpdatePm1e()` 在移动后直接 `$hasUpdate = true`，生物每 tick 都在队列里，
所以 InCore 没有这个现象。此前 A* 的 motion/yaw 修复解决的是拐角横滑，不是这个根因。

### 修复

`Mob::onUpdate()` 对存活且未关闭的生物返回 `true`；死亡/关闭路径不变。
新增 `tests/MobTickSchedulingTest.php` 验证连续返回值和 5 tick 队列稳定性。

### 剩余风险

1. **上服确认**：需观察同一群移动生物是否不再同步弹跳、速度是否恢复为每 tick 连续推进。
   离线测试只验证更新队列语义，不验证客户端插值动画。
2. **性能**：所有存活生物恢复为每 tick 更新，生物数量大时实体 tick 开销会上升；
   这是正确性修复，需要观察 TPS。
3. **死亡/关闭边界**：死亡分支仍由 `Creature`/`Entity` 处理 `deadTicks` 与 despawn；
   离线测试未覆盖死亡 mid-tick 的完整生命周期。
4. **其它实体**：Item、TNT、矿车等仍沿用各自返回语义，本次未改；若它们也有固定间隔
   更新问题，需要单独评估。

## 9. Blockers

无。
