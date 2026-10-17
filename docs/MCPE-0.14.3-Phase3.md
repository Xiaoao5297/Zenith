# MCPE 0.14.3 地形改造 — Phase 3 报告

日期：2026-10-17
状态：Phase 3 完成（结构统一播种、村庄、ScatteredFeature）
目标：地形观感接近 MCPE 0.14.3，不要求种子逐块 1:1。

## 1. 结构统一播种

`MapGenBase::generate()` 改为 MCPE `LargeFeature::apply` 的方式：

- 用世界种子初始化 MT19937；
- 取两个随机数，得到两个奇数乘子：
  - `i = ((r1 >> 2) * 2 + 1) & 0xffffffff`
  - `j = ((r2 >> 2) * 2 + 1) & 0xffffffff`
- 对当前区块 ±8 范围逐区块计算：
  - `seed = worldSeed ^ ((x * i + z * j) & 0xffffffff)`
- 所有乘法和种子按 32 位无符号截断。

`MapGenStructure`：

- 删除 `recursiveGenerate` 中多余的 `nextInt()`，MCPE 没有这次消费。
- 新增负结果缓存 `checkedChunks`，避免每个区块重复扫描 ±8 范围。
- 4 区块生成从约 3.1s 降到约 2.3s。

## 2. 村庄

新增 `MapGenVillage.php`、`VillageStart.php`，并接入 `Gorigional`。

### 2.1 间距与位置

- 网格：`SPACING = 40`，`SEPARATION = 28`。
- 允许群系：平原 1、沙漠 2、热带草原 35。
- 负坐标先减 39，再使用 C++ 的整数除法（向零截断）。
  - 注意：这里不能用 floorDiv，否则 `-59 / 40` 会得到 `-80` 而不是 `-40`。
- 村庄种子：

```text
seed = worldSeed
     + adjustedX * 0x9939F508
     + adjustedZ * 0xF1565BD5
     + 0x9E7F70
```

- 用 MT19937 取两个数，判定：

```text
r1 % 28 + gridX == chunkX
r2 % 28 + gridZ == chunkZ
```

### 2.2 组件展开

- 以 `VillageWellStart` 为中心。
- 房屋列表优先，房屋为空时才取道路。
- 保留 256 次防御上限，避免异常种子下无限展开。
- seed 30、区块 `15,13`：44 个组件，prepare 约 1.2–1.4s，包含水井、道路、Hall、Church、House、Field、Torch 等。
- 村庄布局使用村庄自己的种子流，而不是 `MapGenBase` 的区块种子。

### 2.3 群系检查

`WorldChunkManager::isBiomeAllowed()`：

- 在半径内以 4 格步长采样；
- 全部属于允许群系才返回 true；
- 对应 MCPE `BiomeSource::containsOnly` / Java `areBiomesViable`。

## 3. ScatteredFeature

`MapGenScatteredFeature` 改为使用 `MapGenBase` 已经播种好的 `$this->rand`，不再使用 Java 的 `getRandomAt`。

- 间距仍为 32，最小间隔 8。
- 允许群系改为 MCPE 的：
  - 沙漠 2
  - 沙漠丘陵 17
  - 沼泽 6
  - 沼泽变种 134
- 不再生成丛林神庙（MCPE 0.14.3 的 ScatteredFeature 只检查沙漠/沼泽）。

## 4. 未完成：要塞

Phase 3 没有移植要塞。原因：

- 仓库里没有 `StrongholdStart` / `StrongholdPiece`。
- SCAXE 的 Go 实现是 Java 1.7.10 的 128 要塞版本，且包含调试输出，不适合直接作为 MCPE 0.14.3 的 3 要塞版本。
- MCPE 的 `StrongholdFeature` 需要单独实现：
  - 3 个要塞；
  - 半径 `(rand & 0xf) + 40`；
  - 角度 45° / 108°；
  - ±8 区块搜索；
  - `StrongholdStart` 与部件生成。

建议放到 Phase 4 单独做。

## 5. 验证

- 所有修改文件 `php -l` 通过。
- 4 区块生成约 2.3s。
- seed 30 村庄：
  - 区块 `15,13` 可生成；
  - 44 个组件；
  - 实际 populate 能写出圆石、砂砾、火把、栅栏等村庄方块。
- 负坐标公式用独立脚本验证：
  - `-114,90`、`-93,-19`、`-66,-95` 等候选位置能被正确计算；
  - 这些位置当前群系不允许，因此没有生成村庄，属正常。

## 6. 文件

修改：

- `Gorigional.php`
- `structure/GeneratorWorld.php`
- `structure/MapGenBase.php`
- `structure/MapGenStructure.php`
- `structure/StructureComponentBase.php`
- `structure/World.php`
- `structure/WorldAccess.php`
- `structure/WorldChunkManager.php`
- `structure/MapGenScatteredFeature.php`

新增：

- `structure/MapGenVillage.php`
- `structure/VillageStart.php`
- 其余 `Village*.php` 组件
