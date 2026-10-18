# MCPE 0.14.3 审查修复（Phase 11）

本轮根据三个独立 subagent 的审查结果，修复核心生成、群系/装饰、结构三部分的问题。
目标仍是地形观感接近 MCPE 0.14.3，不要求种子 1:1。

## 11.1 核心生成

- `generateChunk` 的区块随机流改为 MCPE 32 位区块种子：

  ```php
  $seed = (($chunkX * 0x14609048) + ($chunkZ * 0x7EBE2D5)) & 0xFFFFFFFF;
  $rnd = new McpeRandom($seed);
  ```

  表层方块不再使用 Java LCG。

- `populateChunk` 在湖泊之后、结构之前重新播种同一个区块 MT，与 MCPE `postProcess` 一致。
- `getSpawn()` 从 y=127 向下扫描，并检查上方两格不越界。
- min limit 噪声使用 `855.515` 的 Y 缩放和 `256` 的除数；max limit 保持 `684.412/512`。
- `MutatedBiome` 从父群系复制 `temperature` / `rainfall`，修复变种群系被全部当成冷群系、温水变种结冰和 Undefined property notice。

## 11.2 群系与装饰

- Cold Beach（26）补沙：`surface(26, SAND, 0, SAND)`。
- Forest Hills（18）使用与 Forest（4）相同的白桦/大橡树/橡树混合。
- MegaJungle 树冠宽度改为 `width + 1 - $j`，不再下窄上宽。
- MegaPine：
  - 高度计算后检查 `groundY + height + 1 <= 128`。
  - podzol 改为 `Block::PODZOL`，不再写 `dirt:2`。
- SpruceTree、BigOakTree 增加 128 世界高检查。
- Mesa：
  - 白带数量改为 `nextBoundedInt(3) + 3`（3..5）。
  - y < 62 使用硬化黏土。
  - 红沙只保留顶层，填充层使用染色黏土/硬化黏土。
- `ObjectChunkManager` 新增 `getTopSolidOrLiquidBlock()`；`ChunkManagerAdapter` 从 127 向下返回最高非空气方块（水也算）。
- Decorator 的沙/黏土/砂砾斑块改用该接口，修复水下斑块永不生成的问题。
- Dungeon 不再在 y=1 时把 y=0 的基岩当成空气清掉。
- DoublePlant 边界改为 `<127`；Reed/Cactus/Waterlily/DeadBush/Pumpkin/Vines 改为 `<128`；Ore 改为 `<128`。
- 新增 `object/BigMushroom.php`，Decorator 增加 `bigMushroomsPerChunk` 循环；
  巨型蘑菇会检查地面、空间和 128 高度。

## 11.3 结构

- `replaceAirAndLiquidDownwards()` 改为真正向下填充，直到实心方块或 y<=1，避免村庄房屋/水井/田地悬空。
- `getMetadataWithOffset()` 增加木门/铁门旋转：

  | coordBaseMode | meta |
  |---|---|
  | 0 | 原值 |
  | 1 | `(meta + 1) & 3` |
  | 2 | `(meta + 2) & 3` |
  | 3 | `(meta + 3) & 3` |

- 要塞门上半部分写 `meta | 8`，不再上下半都写 0。
- 矿洞：
  - 蜘蛛走廊刷怪笼写入 `CaveSpider::NETWORK_ID`（40）。
  - 矿车箱改为箱子 + `mineshaft` 战利品。
  - 房间西/东入口使用连接走廊的 Z 范围，不再按整个房间挖洞。
- StrongholdCorridor 根据 `coordBaseMode` 选择 XSize/ZSize 作为长度。
- 村庄平均地面高度使用 `getHeight() + 1`；村庄道路使用 `getHeight()`，避免整体低一格。
- ScatteredFeature 从 127 向下扫描、只接受 solid 方块并返回 `y+1`，避免神庙落在树叶/植物上。
- 战利品：
  - 新增 `village`、`mineshaft` 表。
  - 铁匠箱接入 `village` 表。
  - 沼泽变种 134 与 6 都生成沼泽小屋。
  - 要塞传送门房间刷怪笼实体 ID 改为 39（Silverfish）。
- 群系边界：
  - `getBiomeClass()` 补齐 Hills、Desert、Snow、Mushroom、Beach、Savanna、River 分组。
  - `GenLayerShore` 对 `id & 0x7F` 归一化，变种群系边界按父群系处理。
- 结构边界与缓存：
  - `MapGenStructure` 区块包围盒 y 上限改为 127。
  - `StructureStart` 删除 `addComponentParts()` 返回 false 的组件，避免反复重试。
  - 要塞群系采样范围扩到 `-32..32`。

## 11.4 异步箱子/刷怪笼

异步 `PopulationTask` 中的区块没有 `LevelProvider`，直接创建 Tile 会失败。

修复链路：

1. `GeneratorWorld::setChestContents()` / `setSpawnerEntity()` 在 provider 不可用时写入 deferred 队列。
2. `Gorigional::takeDeferredTiles()` 暴露并清空队列。
3. `PopulationTask::run()` 在 populate 后收集队列。
4. `PopulationTask::onCompletion()` 重建区块并调用 `applyDeferredTiles()`：
   - 用 `Tile::createTile()` 创建 Chest / MobSpawner。
   - 箱子按槽位写入物品。
   - 刷怪笼写入 `EntityId`。

SimpleChunkManager 单测：seed 71、丛林神庙区块 13,3 返回 `chests=2`，首个箱子包含
铁砧、骨头、钻石马铠等战利品。

## 11.5 验证

- gorigional 全部 PHP 文件及 `PopulationTask.php` 的 `php -l` 通过。
- seed 16 平原：populate 0.31s，`31=32`、`38=9`。
- seed 71 丛林：`17=237`、`18=947`、`106=587`。
- seed 14 冷针叶林：`17=59`、`18=357`、`13=92`。
- seed 84 Mesa：`12=256`、`13=15`。
- seed 30 村庄 15,13：44 个组件，路径/房屋/火把正常。
- 要塞启用路径 seed 1：位置 `-46,19`、`-20,-64`、`58,13`；首个要塞 142 个组件。
- BigMushroom 合成测试：`ok=1`，`99=52`。
- 丛林神庙 deferred 测试：`chests=2`，首个箱子含铁砧、骨头、钻石马铠等物品。
- 所有改动文件及 `PopulationTask.php` 的 `php -l` 通过。

## 11.6 已知限制

- 要塞部件几何仍是 Java 1.7.10 级别，不是 MCPE 逐块复刻。
- 村庄原点仍为区块 `+8`，MCPE 的精确原点待进一步核对。
- 湖泊岩浆外壳转石头、天空光判断尚未实现。
- 结构 `structureMap` / `checkedChunks` 仍无空间索引和回收策略。
- 大蘑菇为简化形状，不是 MCPE 逐块树冠。
