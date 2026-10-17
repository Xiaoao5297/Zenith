# MCPE 0.14.3 地形改造 — Phase 4 报告

日期：2026-10-17
状态：Phase 4 完成（要塞已接入生成流程）

## 1. 目标

继续按照“**地形观感接近原生 MCPE 0.14.3，不要求种子逐块 1:1**”推进。

本阶段只做要塞：

- 每个世界 3 个要塞
- 半径 40–55 区块起步
- 找到后角度 +108°、半径 +8
- 没找到则角度 +45°、半径 +4
- 要塞部件与 Java 1.7.10 的 Stronghold 家族对齐
- 终点包含末地传送门房间

## 2. 新增文件

均在 `src/pocketmine/level/generator/gorigional/structure/`：

| 文件 | 作用 |
|---|---|
| `MapGenStronghold.php` | MCPE 三要塞选址、区块匹配 |
| `StrongholdStart.php` | 要塞起点、组件展开、整体下移 |
| `StrongholdPiece.php` | 要塞部件基类、门、随机石砖、子部件注册 |
| `StrongholdPieces.php` | 权重表与部件工厂 |
| `StrongholdStairs2.php` | 起始楼梯 |
| `StrongholdStraight.php` | 直走廊 |
| `StrongholdCorridor.php` | 兜底短走廊 |
| `StrongholdPrison.php` | 监牢 |
| `StrongholdLeftTurn.php` | 左转 |
| `StrongholdRightTurn.php` | 右转 |
| `StrongholdCrossing.php` | 五岔路口 |
| `StrongholdRoomCrossing.php` | 大房间 |
| `StrongholdStairs.php` | 向下楼梯 |
| `StrongholdStairsStraight.php` | 长直楼梯 |
| `StrongholdChestCorridor.php` | 带箱子的走廊 |
| `StrongholdLibrary.php` | 图书馆 |
| `StrongholdPortalRoom.php` | 末地传送门房间 |

## 3. 选址逻辑

`MapGenStronghold` 在首次需要时懒计算位置：

```text
angle = nextFloat() * 2π
radius = 40 + (nextInt() & 0xf)
```

然后循环：

- 候选区块 `(round(cos(angle) * radius), round(sin(angle) * radius))`
- 在中心与四角采样群系
- 全部属于陆地群系才接受
- 接受后 `angle += 108°`，`radius += 8`
- 拒绝后 `angle += 45°`，`radius += 4`
- 最多 128 次尝试，目标 3 个

陆地群系排除海洋、河流、沙滩、蘑菇岛，保留平原、森林、沙漠、针叶林、沼泽、热带草原、丘陵、冰原等。

## 4. 部件展开

`StrongholdStart`：

1. `StrongholdPieces::reset()` 重置权重。
2. 在 `(chunkX*16+2, 64, chunkZ*16+2)` 创建 `StrongholdStairs2`。
3. 调用起点的 `buildComponent()`。
4. 循环从 `pendingPieces` 随机取部件继续展开。
5. 最多 120 个组件，避免无限膨胀。
6. `updateBoundingBox()` 后 `placeAtHeight($world, $rnd, 5)`。

权重：

```text
Straight 40
Prison 5/5
LeftTurn 20
RightTurn 20
RoomCrossing 10/6
StairsStraight 5/5
Stairs 5/5
Crossing 5/4
ChestCorridor 5/4
Library 10/2
PortalRoom 20/1
```

## 5. 传送门房间

`StrongholdPortalRoom` 生成：

- 11×8×16 房间
- 入口台阶
- 水平 3×3 末地传送门方块（ID 119）
- 5×5 末地传送门框架外圈（ID 120）
- 前方岩浆池
- 刷怪笼（ID 52）

早期实现把框架放在局部 `z=14` 的整面墙上，旋转后会落到包围盒外，实际世界看不到。已改为水平传送门，世界测试中确认出现 `119=9`、`120=16`。

## 6. 重要修复：结构写入包围盒

`MapGenStructure::generateStructure` 原先使用：

```php
$k = ($chunkX << 4) + 8;
$l = ($chunkZ << 4) + 8;
```

导致结构只在区块东/南半边写入，西/北半边丢失。已改为：

```php
$k = $chunkX << 4;
$l = $chunkZ << 4;
```

这个修复影响所有结构：村庄、矿洞、要塞、神庙。

## 7. 验证

所有新增和修改文件 `php -l` 通过。

seed 1：

- 3 个要塞位置：`-29,-51`、`66,-14`、`-8,75`
- 首要塞 prepare 约 0.021s
- 139 个组件
- bbox `-542,33,-817 .. -375,59,-728`
- 组件包含 Stairs2、Straight、PortalRoom、RightTurn、RoomCrossing、LeftTurn、StairsStraight、Crossing、Library、Prison、Stairs、ChestCorridor
- 7×7 区块生成约 8.25s
- 中心区块 populate 约 0.153s
- 中心方块统计含 `98=245`、`109=11`，石砖与台阶正常

其他种子：

- seed 2：3 个位置，首要塞 157 个组件
- seed 3：3 个位置，首要塞仅 5 个组件，但仍包含传送门
- seed 42：149 个组件
- seed 12345：6 个组件
- 均无死循环

## 8. Phase 4.1–4.6 后续更新

### 4.1 组件硬限制

- `type > 50` 停止派生。
- 子部件限制在起点 `±112` 格内。
- 禁止连续生成同类型部件。
- `Library`、`PortalRoom` 增加最低 type 限制。
- seed 1 首要塞组件从 335 降到 145。

### 4.2 16×16 选址

- 在角度中心周围 `-8..+7` 共 256 个候选区块中搜索。
- 每个候选按 4 格步长采样 13×13 个群系点。
- seed 1 位置更新为 `-48,24`、`-20,-65`、`58,6`。

### 4.4 战利品与刷怪笼

- 要塞箱子接入简化 1.7.10 战利品表。
- 传送门房间刷怪笼写入蠹虫 `EntityId = 54`。
- 真实 `Level` 下通过 `Tile::createTile()` 写入 NBT；SimpleChunkManager 测试环境自动跳过。

### 4.5 湖形

- 湖泊继续使用 16×16×8 的 4–7 个椭球叠加。
- 椭球数量改为 MCPE 的 `(genrand & 3) + 4`，所有浮点采样改为 `nextFloat()`。
- 增加 MCPE `LakeFeature::_check` 的壳层检查（检查湖体外、但与湖体相邻的格子）：
  - 外壳下半部必须是实心或同种液体。
  - 外壳上半部不能是水/岩浆。
- 填充时下半部放湖液体、上半部放空气，避免湖水悬空或从洞穴泄漏。
- 湖面下的泥土转为草方块，贴近 MCPE 的湖岸处理。
- 随机数改为 MT，与地牢、结构、装饰共用同一条流。

### 4.6 装饰 MT 化

- `McpeRandom` 继承 `JavaRandom`，覆盖随机方法并补 `nextLong()`。
- populate 的区块种子改为 MCPE MT 算法。
- 湖泊、结构、地牢、结冰、装饰共用同一条 MT 随机流。

### 4.7 装饰优化

- 装饰坐标从 `nextBoundedInt(16) + 8` 改为 `nextBoundedInt(16)`，与 MCPE `BiomeDecorator` 的 `genrand & 0xf` 一致。
- `populateChunk` 的群系取样从 `x + 16, z + 16` 改为 `x + 8, z + 8`（区块中心），避免用错 decorator。
- 花、草、枯木、睡莲、蘑菇、甘蔗、南瓜、仙人掌和树统一使用该列 `getHeight() + 1`；Grass/Bush 在 x/z 偏移后重新取该列高度。
- 删除 Java 硬编码的额外 10 次甘蔗和额外蘑菇。
- 循环次数：Grass/Bush 128/64 → 8，Reed/Cactus/Waterlily → 4，Pumpkin → 8。
- 矿石 dirt/gravel 高度 256 → 128；涌泉限制在 8..127。
- 验证：
  - seed 16 平原：`31=32`、`38=9`
  - seed 11 森林：`17=47`、`18=402`、`31=5`、`37/38=9`
  - seed 7 沼泽：`17=8`、`18=136`、`83=4`、`106=96`
  - seed 19 Mega Taiga：`17=251`、`18=524`、`31=10`、`37=4`
- 注意：seed 4 的群系 24 是 Deep Ocean，不是丛林；测试丛林需另找 21/22/23。

### 4.8 要塞自然生成配置

- MCPE 0.14 原版默认不自然生成要塞，只有特殊种子才会出现。
- 在 15 个 `src/pocketmine/resources/genisys_*.yml` 的 `world:` 段新增：

```yaml
generate-stronghold: false
```

- 默认值为 `false`。开启方式：

```yaml
world:
 generate-stronghold: true
```

- `Gorigional::init()` 只在真实 `Level` 且配置开启时创建 `MapGenStronghold`；测试用的 `SimpleChunkManager` 默认关闭。
- `generateChunk()` / `populateChunk()` 已加空值保护，关闭时完全跳过要塞的 prepare 和写入。

## 9. 已知限制

- 要塞部件布局参考 Java 1.7.10，几何细节不是 MCPE 的逐块复刻。
- 选址使用 13×13 群系采样，不是 MCPE 的 BiomeSource 回调全量判定。
- 组件数量仍可能较大（145 左右），完整 7×7 生成约 8s。
- 战利品表是简化版，不是完整 1.7.10 权重表。
- 湖形是 MCPE 的椭球 + 壳层近似，不是逐行复刻。
- 仍未推送。
