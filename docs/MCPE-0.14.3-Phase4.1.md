# MCPE 0.14.3 地形改造 — Phase 4.1 报告

日期：2026-10-17
状态：Phase 4.1–4.4、4.6 完成；4.3/4.5 为近似实现

## 1. 要塞组件硬限制（4.1）

`StrongholdPieces` 增加：

- `type > 50` 时不再生成子部件。
- 子部件坐标必须满足：
  - `abs(x - start.minX) <= 112`
  - `abs(z - start.minZ) <= 112`
- 同一部件类型不能连续重复。
- `Library` 需要 `type > 5`，`PortalRoom` 需要 `type > 6`，避免过早抽到终点房间。

实现位置：

- `StrongholdPieces::$startBox`
- `StrongholdPieces::setStartBox()`
- `StrongholdPieces::canCreateAt()`
- `StrongholdPieces::available()`
- `StrongholdPieces::$lastClass`

`StrongholdStart` 在构造起点楼梯后设置 `startBox`。

效果：

- seed 1 首要塞从 335 个组件降到 145 个。
- 不再出现只有 2 个组件就结束的要塞。
- bbox 控制在起点周围约 ±112 格内。

## 2. 要塞 16×16 选址扫描（4.2）

`MapGenStronghold::buildPositions()` 改为：

1. 用 MT 取初始角度和半径。
2. 中心用整数截断：`(int)(cos(angle) * radius)`。
3. 在中心周围 `dx/dz = -8..7` 的 16×16 个候选区块中扫描。
4. 每个候选用 `isValidArea()`：
   - 以区块中心为基准
   - 在 `-24..24` 范围每 4 格采样，共 13×13 = 169 个点
   - 全部属于陆地群系才接受
5. 找到后角度 +108°、半径 +8；否则角度 +45°、半径 +4。

seed 1 的位置变为：

```text
-48,24  biome 3
-20,-65 biome 6
58,6    biome 22
```

## 3. 战利品与刷怪笼（4.4）

### 接口

`WorldAccess` 新增：

```php
setChestContents($x, $y, $z, array $items)
setSpawnerEntity($x, $y, $z, $entityId)
```

`ChunkManagerAdapter` 新增 `getChunkAt($x, $z)`。

`GeneratorWorld` 在真实 `Level` 下通过 `Tile::createTile()` 写入箱子/刷怪笼 NBT；测试用的 `SimpleChunkManager` 下自动 no-op。

### 战利品

`StructureComponentBase::generateChest()` 支持可选 `$loot` 参数。

要塞战利品表包含：

- 面包、苹果
- 铁锭、金锭
- 末影珍珠、钻石
- 红石、煤
- 铁剑、铁镐
- 金苹果

每次 3–6 个堆叠，随机槽位不重复。

箱子走廊、监牢、大房间的箱子已接入 `'stronghold'` 表。

### 刷怪笼

`StrongholdPortalRoom` 的刷怪笼现在会写入 `EntityId = 54`（蠹虫）。

## 4. 装饰阶段 MT 化（4.6）

`McpeRandom` 改为继承 `JavaRandom`，并覆盖全部随机方法；新增 `nextLong()` 供仍按 `JavaRandom` 类型声明的装饰代码使用。

`Gorigional::populateChunk()` 现在：

```php
$seedRnd = new McpeRandom($this->seed);
$k = (($seedRnd->nextInt() >> 2) << 1) + 1;
$l = (($seedRnd->nextInt() >> 2) << 1) + 1;
$seed = (($this->seed & 0xFFFFFFFF)
       ^ ((($chunkX * $k + $chunkZ * $l) & 0xFFFFFFFF))) & 0xFFFFFFFF;
$rnd = new McpeRandom($seed);
```

湖泊、结构、地牢、结冰、装饰全部使用同一条 MT 随机流。

## 5. 4.3 要塞几何

已完成结构级对齐：

- 房间尺寸
- 连接方向
- 门洞位置
- 112 格和 type 限制

逐块细节仍参考 Java 1.7.10，不是 MCPE 的逐方块复刻。

## 6. 4.5 湖形

湖泊已经改用 MT 随机流，但形状仍沿用现有的 4–7 个 3D 椭球叠加，没有完整移植 MCPE `LakeFeature::_check` 的壳层验证。

观感上仍是自然湖泊，但边缘细节与 MCPE 不完全一致。

## 7. 验证

所有修改文件 `php -l` 通过。

- seed 1 要塞位置：`-48,24`、`-20,-65`、`58,6`
- 首要塞 prepare 约 0.04s，145 个组件
- populate 区块 `-47,28` 出现 `119=9`、`120=16`（末地传送门）
- populate 区块 `-48,28` 出现 `52=1`（刷怪笼）、`54=1`（箱子）、`101=36`（铁栏杆）
- seed 30 村庄仍正常：44 个组件，prepare 约 0.68s
- 3×3 生成 + 中心 populate 约 0.11s（测试区块为海洋，装饰较少）

## 8. 尚未完成

- MCPE `LakeFeature` 的精确壳层和边缘衰减。
- 要塞部件逐块几何复刻。
- 真实 `Level` 下箱子 NBT 的完整冒烟测试（当前只在 `SimpleChunkManager` 下验证无崩溃）。
