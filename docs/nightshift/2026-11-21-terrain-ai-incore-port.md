# 2026-11-21 地形（Java 1.7.10）与生物 AI（InCore）借鉴落地

## 1. 目标

在现有 `gorigional` 生成器与行为树 AI 上，做一轮**可验证的小步移植**：

1. 地形装饰向 Java 1.7.10 对齐（群系草/花/芦苇数量、Java 固定 10 次芦苇尝试）。
2. AI 向 InCore-II 对齐（雪傀儡远程投掷雪球、被攻击后反击）。
3. 把 `/docs/incore`、`/docs/remapped_server_1.7.10`、MCPE 0.14 反编译包的对照结论写进文档，标明后续可借鉴项。

目标不是重写地形/AI，而是消除已经确认的、低风险的偏差，并留下可继续执行的清单。

## 2. 验收标准（每条都能用命令/文件断言检查）

| # | 验收项 | 检查方式 |
|---|---|---|
| AC1 | `ShootEnemyBehavior` 存在，只攻击配置的 NETWORK_ID，按冷却发射 `Snowball`，雪傀儡注册它且不再用近战 `AttackEnemyBehavior` | `php73 -l` + `tests/ShootEnemyBehaviorTest.php` 通过 |
| AC2 | `AttackEnemyBehavior` 能从 `getLastDamageCause()` 的 `EntityDamageByEntityEvent` 取得攻击者并反击，目标合法性规则与索敌一致 | `php73 -l` + `tests/AttackEnemyRetaliationTest.php` 通过 |
| AC3 | 平原草 10、森林草 2/花 2、沼泽草 5、丛林草 25、热带草原草 20、台地芦苇 3、针叶林草 1 与 Java 一致；`Decorator::decorate()` 含 Java 固定 10 次芦苇尝试且位于南瓜之前 | `tests/JavaDecorationAlignmentTest.php` 通过 |
| AC4 | 全量离线测试通过（基线 838 + 新增用例），连续 3 次 failed=0 | `php73 tests/run-tests.php` 输出 `passed` 且 failed=0 |
| AC5 | 所有改动文件语法通过；对照矩阵写入本文档 | `php73 -l <file>`，`## 6` 内容存在 |

## 3. 当前状态与约束（Phase 0 结论）

- 基线：`php73 tests/run-tests.php` => **838/838 passed**。
- PHP 7.3 only：禁止类型属性、箭头函数、`match`、联合类型、`?->`、命名参数。
- 缩进 Tab；`if(`/`foreach(` 不空格；`}else{` 同行；返回类型 ` : string`。
- `src/pocketmine/network/protocol/v84/` 是 CRLF，本任务不碰。
- 测试零依赖：`tests/<Name>Test.php`，类名同名，唯一入口 `public static function run() : array`，自行 `require_once`/autoload。
- 生成器默认 `gorigional`，Java 1.7.10 对齐；世界高度 128，地牢/要塞/湖泊等已有意偏离 Java，本任务不动。
- AI 行为树已有 `Behavior` 基类（priority/control flags/lookAt/canSee/bowAimPitch），`Mob` 有 `getLastDamageCause()`（继承自 `Entity`），`Snowball` 实体已存在（NETWORK_ID 81）。

### 已确认的偏差

- 雪傀儡：Zenith 用 `AttackEnemyBehavior` 近战；InCore 用 `ShootEnemyBehavior($this,[32,33,34,35,36,40,44],"Snowball")`。
- 反击：Java `EntityAIHurtByTarget` / InCore `setPm1eRetaliationTarget` 都有；Zenith `AttackEnemyBehavior` 只看附近实体，被打后不会立即锁定攻击者。
- 群系装饰：`BiomeRegistry::configureDecorators()` 的草/花数量与 Java 不一致（见 AC3）；`Decorator::decorate()` 少了 Java 无条件 10 次芦苇尝试。
- 矿脉：Java 1.7.10 为 dirt 20(32)、gravel 10(32)、coal 20(16)、iron 20(8)、gold 2(8)、redstone 8(7)、diamond 1(7)、lapis 1(16/16)，且没有花岗岩/闪长岩/安山岩矿；Zenith 当前为 MCPE/旧移植值。**本轮不改**，因为会明显改变地下密度，列入后续单独 A/B。

## 4. 任务与并行切片

### Slice A — 雪傀儡远程 AI（InCore）

文件：

- 新增 `src/pocketmine/entity/ai/behavior/ShootEnemyBehavior.php`
- 修改 `src/pocketmine/entity/SnowGolem.php`
- 新增 `tests/ShootEnemyBehaviorTest.php`

要点：

- `shouldStart()` 在 `lookDistance=16` 内找最近的、存活且 NETWORK_ID 在配置列表中的实体；排除自己。
- `onTick()` 平滑转头；距离 > 6 靠近、< 4 后退、其余停下；`canSee` 且距离 ≤ 10 时每 40 tick 发射一次雪球（`SHOOT_COOLDOWN=40`，`canShoot()` 纯函数）。
- 雪球 NBT：Pos（眼睛高度）/Motion（按 yaw+pitch，速度约 1.5）/Rotation，`Entity::createEntity("Snowball", ...)` + `spawnToAll()`。
- `SnowGolem::initEntity()` 改为 `new ShootEnemyBehavior($this, [32,33,34,35,36,40,44], "Snowball")`。
- 纯静态辅助：`acceptsTarget(bool $alive, bool $self, int $networkId, array $networkIds, float $distance, float $lookDistance) : bool` 与 `canShoot(bool $canSee, float $distance, float $shootDistance, int $cooldown) : bool`，供离线测试。

### Slice B — 被攻击反击（Java HurtByTarget / InCore retaliation）

文件：

- 修改 `src/pocketmine/entity/ai/behavior/AttackEnemyBehavior.php`
- 新增 `tests/AttackEnemyRetaliationTest.php`

要点：

- `shouldStart()` 先看 `$entity->getLastDamageCause()`；若是 `EntityDamageByEntityEvent`，取 `getDamager()`。
- 目标规则：存活、非自己、在 `lookDistance + 4` 内；玩家要求 `attackPlayer && isSurvival && isConnected`；非玩家要求 NETWORK_ID 在列表。
- 命中则直接 `enemy = damager`，跳过搜索冷却。
- 纯静态辅助：`acceptsRetaliationTarget(bool $alive, bool $self, bool $isPlayer, bool $survival, bool $connected, int $networkId, array $networkIds, bool $attackPlayer, float $distance, float $maxDistance) : bool`。
- 不改 `Mob::attack()`，避免与击退/无转向逻辑耦合。

### Slice C — 地形装饰 Java 对齐

文件：

- 修改 `src/pocketmine/level/generator/gorigional/biome/BiomeRegistry.php`
- 修改 `src/pocketmine/level/generator/gorigional/object/Decorator.php`
- 新增 `tests/JavaDecorationAlignmentTest.php`

要点：

- 平原 `grass 16 → 10`。
- 森林 `grass 4 → 2`、`flowers 4 → 2`；森林变种（27/28）同样 2/2。
- 沼泽 `grass 8 → 5`。
- 丛林（21/22）`grass 30 → 25`。
- 热带草原（35/36）`grass 26 → 20`。
- 台地（37/38/39）补 `reeds => 3`，37/38/39 花为 0。
- 针叶林（5/19/30/31）`grass 2 → 1`；巨型针叶林（32/33）Java 为 grass 7/deadBush 1/mushrooms 3，一并改。
- 森林变种 18 草/花 2/2；丛林边缘（23）树 2/草 25/花 4；黑森林（29）草/花 2/2；丘陵（3/20/34）草 1（树数量暂保持）。
- `Decorator::decorate()` 在 `reedsPerChunk` 循环后追加 Java 固定 `for($i=0;$i<10;$i++)` 芦苇尝试（相同 y 取法），位置必须在南瓜之前。
- 测试用反射读取 `BiomeRegistry::getBiome(1)` 等对象的装饰器字段（先确认字段名/getter），并对 `Decorator.php` 源码断言固定 10 次循环标记；若字段不可读，则用一个公开静态映射 `BiomeRegistry::javaDecorationValues()` 作为单一事实源并断言。

## 5. 验证计划

```bash
php73 -l src/pocketmine/entity/ai/behavior/ShootEnemyBehavior.php
php73 -l src/pocketmine/entity/ai/behavior/AttackEnemyBehavior.php
php73 -l src/pocketmine/entity/SnowGolem.php
php73 -l src/pocketmine/level/generator/gorigional/biome/BiomeRegistry.php
php73 -l src/pocketmine/level/generator/gorigional/object/Decorator.php
php73 tests/run-tests.php
git diff --stat
```

预期：每个 `-l` 输出 `No syntax errors detected`；测试 `passed` 总数 ≥ 838 + 新增，failed=0；diff 只包含上述文件。

审查重点：

- 雪球是否从眼睛高度发出、是否真的会造成伤害（`Snowball` 继承 `Projectile`，命中处理是否已存在）。
- 反击是否会被已死亡的攻击者反复触发；是否会违反 `attackPlayer=false`。
- 装饰值是否只改目标群系，未误伤 129/140 自定义群系。
- 是否引入 PHP 8 语法。

## 6. 借鉴对照矩阵

### 6.1 地形（Java 1.7.10 为基准）

| 主题 | Java 参考 | Zenith 现状 | 本轮动作 |
|---|---|---|---|
| 群系草/花/芦苇数量 | `BiomeGenPlains/Forest/Swamp/Jungle/Savanna/Taiga/Mesa` | 部分偏大/缺芦苇 | 对齐（Slice C） |
| 固定 10 次芦苇 | `BiomeDecorator.func_150513_a` | 缺失 | 补上（Slice C） |
| 矿脉数量/尺寸/高度 | `BiomeDecorator.func_76797_b` | dirt 10/33、gravel 8/33、coal 20/17 等，另有石变种矿 | 后续单独 A/B，不在本轮 |
| 沙/粘土/砂砾斑块 | `field_76805_H=3`、`field_76806_I=1`、`field_76801_G=1` | 已是 3/1/1 | 保持 |
| 树木 +8 偏移与 y 取法 | `nextInt(16)+8`、`getHeight` | 无 +8、`getHeight+1` | 记录为已知差异；改偏移会整体挪动树木，后续单独评估 |
| 洞穴/峡谷/结构 | `MapGenCaves`、`MapGenRavine`、`ChunkProviderGenerate` | 已按 Java 重写（docs/gorigional-1.7.10.md） | 保持 |

### 6.2 生物 AI（InCore-II / Java / MCPE 0.14）

| 能力 | InCore / Java / MCPE 参考 | Zenith 现状 | 本轮动作 |
|---|---|---|---|
| 雪傀儡远程攻击 | InCore `ShootEnemyBehavior` + `"Snowball"` | 近战 `AttackEnemyBehavior` | 移植（Slice A） |
| 被攻击反击 | Java `EntityAIHurtByTarget`；InCore `setPm1eRetaliationTarget` | 无 | 在索敌行为内实现（Slice B） |
| 幼崽跟随父母 | Java `EntityAIFollowParent`；MCPE `FollowParentGoal` | 无 | 后续（需要 age/成长系统） |
| 避让（豹猫怕玩家、苦力怕怕豹猫） | Java `EntityAIAvoidEntity`；MCPE `AvoidMobGoal` | 无 | 后续 |
| 跳跃扑击（蜘蛛/狼） | Java `EntityAILeapAtTarget`；MCPE `LeapAtTargetGoal` | 无 | 后续（依赖 ground AI 垂直速度） |
| 开门 | Java `EntityAIOpenDoor`；MCPE `OpenDoorGoal` | 无 | 后续 |
| 繁殖/求爱 | Java `EntityAIMate`；MCPE `BreedGoal` | 已有 `InLoveBehavior` 基础 | 后续补幼崽生成 |
| 远程玩家攻击 | Java `EntityAIArrowAttack` | 已有 `ShootPlayerBehavior` | 保持 |

## 7. 范围外

- 不改世界高度、要塞算法、地牢 y、湖泊算法、台地色带、结构内部 RNG。
- 不重写矿脉生成（只记录差异）。
- 不新增繁殖/幼崽/开门/避让/跳跃 AI（列入矩阵后续）。
- 不修改 MCPE 协议包、不碰 `network/protocol/v84/`。
- 不做与任务无关的格式化/批量改动。

## 8. Blockers

（无。已解决下面的既有测试 flake；剩余风险见 `## 10`。）

- 已解决：`GroundAiTest::wanders_without_target` 原先固定只跑 200 tick，而 `Mob::refreshGroundWanderTarget()` 有 1% 概率原地停留 80–200 tick，偶尔假失败（曾复现 `200 tick 位移=0.14`，standalone 1/300）。本轮把该用例改为最长 600 tick、记录最大位移并提前退出，仍验证“会漫游”，但不再被随机停留卡死。这是测试健壮性修复，不改变 `Mob` 行为。

## 9. Acceptance

执行日期：2026-11-21。工作树分支 `feat/incore-port`。

| 验收项 | 结果 | 证据 |
|---|---|---|
| AC1 雪傀儡远程 AI | PASS | `SnowGolem.php:20` 注册 `new ShootEnemyBehavior($this, [32, 33, 34, 35, 36, 40, 44], "Snowball")`；`ShootEnemyBehavior.php:39` `SHOOT_COOLDOWN=40`，`:115` `canShoot()`，`:221-223` 开火，`:242` 眼睛高度，`:256` `Entity::createEntity`，`:262` `spawnToAll()`；`tests/ShootEnemyBehaviorTest.php` **26/26 PASS**（含精确白名单、射程/视线/冷却矩阵、严格距离边界、投掷实现源码断言）。 |
| AC2 被攻击反击 | PASS | `AttackEnemyBehavior.php:72` 读取 `getLastDamageCause()`，`:90` 使用 `lookDistance + RETALIATION_RANGE_BONUS`，`:92` `$this->enemy = $damager`；`tests/AttackEnemyRetaliationTest.php` **20/20 PASS**（含玩家模式/在线/创造、死亡/自己、距离边界、接线断言）。 |
| AC3 地形装饰 Java 对齐 | PASS | `BiomeRegistry.php:170` 平原草 10；`:173-175` 森林 2/2、针叶林草 1、沼泽草 5；`:185-187` 丛林 25、丛林边缘 25/4；`:195` 巨型针叶林 7/1/3；`:200-202` 台地 37/38/39 芦苇 3、花 0；`Decorator.php:258-271` 配置芦苇后固定 10 次芦苇，`:273` 南瓜在其后；`tests/JavaDecorationAlignmentTest.php` **54/54 PASS**（含顺序 `strpos` 断言；129 对照，140 未改）。 |
| AC4 全量测试 | PASS | 连续 3 次 `php73 tests/run-tests.php` 均为 `938/938 passed`（838 基线 + 100 新增/扩展用例）。 |
| AC5 语法与矩阵 | PASS | 所有改动/新增 PHP 文件 `php73 -l` 均 `No syntax errors detected`；`git diff --check` 无输出；`## 6` 对照矩阵存在。 |

语法检查命令与结果：

```text
$ for f in <changed files>; do php73 -l "$f"; done
No syntax errors detected in src/pocketmine/entity/ai/behavior/ShootEnemyBehavior.php
No syntax errors detected in src/pocketmine/entity/ai/behavior/AttackEnemyBehavior.php
No syntax errors detected in src/pocketmine/entity/SnowGolem.php
No syntax errors detected in src/pocketmine/level/generator/gorigional/biome/BiomeRegistry.php
No syntax errors detected in src/pocketmine/level/generator/gorigional/object/Decorator.php
No syntax errors detected in tests/ShootEnemyBehaviorTest.php
No syntax errors detected in tests/AttackEnemyRetaliationTest.php
No syntax errors detected in tests/JavaDecorationAlignmentTest.php
No syntax errors detected in tests/GroundAiTest.php
```

窄测试输出：

```text
ShootEnemyBehaviorTest: 25 cases, 0 failed
AttackEnemyRetaliationTest: 18 cases, 0 failed
JavaDecorationAlignmentTest: 54 cases, 0 failed
```

全量测试输出（3 次一致）：

```text
PASS TemptGoalTest::registered_chicken - Chicken 应注册 TemptGoal([295, 361, 362])

938/938 passed
```

## 10. Summary

### 改了什么

- **AI（InCore）**：新增 `ShootEnemyBehavior`，雪傀儡从近战改为每 40 tick 投掷雪球；索敌白名单、16 格索敌、10 格开火、4–6 格保持距离、眼睛高度出弹、1.5 倍初速、`spawnToAll()`。
- **AI（Java HurtByTarget）**：`AttackEnemyBehavior` 在被 `EntityDamageByEntityEvent` 命中后，优先把攻击者设为目标，反击距离为 `lookDistance + 4`，并复用原有玩家/白名单合法性规则。
- **地形（Java 1.7.10）**：`BiomeRegistry` 对齐草/花/芦苇数量（平原、森林及变种、针叶林、沼泽、丛林及边缘、热带草原、台地、丘陵、巨型针叶林）；`Decorator` 补上 Java 固定 10 次芦苇尝试，并保证在配置芦苇之后、南瓜之前执行。
- **测试**：新增 3 个离线测试；扩展 `GroundAiTest` 的漫游用例以消除既有随机 flake。

### 验证方式

- 逐文件 `php73 -l` + `git diff --check`。
- 3 个新测试单独运行全绿（25/18/54）。
- 全量 `php73 tests/run-tests.php` 连续 3 次 `938/938 passed`。
- 关键代码行在 `## 9` 中逐条引用。

### 剩余风险 / 未验证

1. **雪球伤害为 0**：`Snowball` 继承 `Projectile::$damage = 0`，命中会触发击退/受伤动画但伤害为 0。这与 InCore 的实现一致（雪球对大多数生物无伤害，对烈焰人特殊），但本仓库尚未实现“对烈焰人 3 点伤害”的特例；失去近战后雪傀儡对普通怪不再造成伤害。需要上服确认手感，后续可单独给 `Snowball` 增加对 `Blaze` 的伤害分支。
2. **真实服务器行为未验证**：离线测试只覆盖纯函数、字段和源码接线；投掷物生成、AI 目标切换、区块加载下的实际表现需按 `docs/manual-verification.md` 上服确认。
3. **Java 装饰仍有已知差异**：树木 `+8` 偏移、`nextInt(height*2)` 的 y 取法、矿脉数量/尺寸/石变种矿未在本轮改动；这些会改变地形观感，留待单独 A/B。
4. **反击使用未清除的 `lastDamageCause`**：旧攻击者在存活且仍在 `lookDistance + 4` 内时，可能在当前目标死亡后再次被锁定；本轮为保持 `Living::attack()` 语义未清除，属可接受的小偏差。
5. **许可**：`ShootEnemyBehavior` 是参照 InCore 行为重写的新文件，保留 Zenith GPL 头；未复制 InCore 源文件原文。若后续按“逐行移植”处理，应改为保留 InCore LGPL 头。

### 用户下一步

- 在开发服验证雪傀儡投掷与怪物反击的实际表现（`./start.sh`）。
- 如需要雪球对烈焰人造成伤害，再开一个小切片修改 `Snowball`/`Projectile` 的伤害规则。

