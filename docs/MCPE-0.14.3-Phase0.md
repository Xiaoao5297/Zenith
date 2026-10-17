# MCPE 0.14.3 地形改造 — Phase 0 报告

日期：2026-10-17
状态：Phase 0 完成（只做资料核对与基线准备，未修改生成代码）

> 目标说明：不要求“种子 -> 地形”与 MCPE 逐字节 1:1 对齐，目标是地形观感接近原生 MCPE 0.14.3；同一 seed 的地形只要和原版大致相似即可，允许随机流和局部结果存在差异。

## 1. 资料与解压位置

- 新的反编译包：`/storage/emulated/0/libminecraftpe_decompiled_full (2).zip`
- Phase 0 解压目录：`/data/data/com.termux/files/usr/tmp/opencode/mcpe014_v2/`
- 旧版解压目录：`/data/data/com.termux/files/usr/tmp/opencode/mcpe014/`
- 当前仓库 HEAD：`d642e93`
- 当前未提交/未跟踪文件保持原样，未改动。

新版仍是反编译伪代码，不是可编译源码；但函数签名、结构体偏移和部分局部变量名比旧版清楚，足以核对常量与调用顺序。

## 2. 新版反编译的改进

新版把函数按类分文件，并给出更完整的签名，例如：

- `RandomLevelSource::loadChunk(LevelChunk&) -> bool`
- `RandomLevelSource::buildSurfaces(LevelChunk*, ChunkPos const&)`
- `RandomLevelSource::getHeights(LevelChunk*, float*, Biome**, int, int, int)`
- `LargeFeature::apply(ChunkSource*, LevelChunk&, BiomeSource*, Random&)`
- `VillageFeature::isFeatureChunk(BiomeSource*, Random&, ChunkPos const&)`

关键常量与旧版一致，没有发现算法层面的变化。

## 3. 已确认的关键事实

### 3.1 随机数

`core/Random.cpp`：

- MCPE 使用标准 MT19937。
- 种子初始化：`mt[0] = seed`，`mt[i] = (1812433253 * (mt[i-1] ^ (mt[i-1] >> 30)) + i)`。
- 对象偏移：`+0x0` 保存 seed 副本，`+0x4..0x9c3` 为 624 个状态字，`+0x9c4` 为 index，初始 624。
- `nextInt(bound)` 是 `genrand_int32() % bound`，**没有 Java 的 rejection 逻辑**。
- `nextFloat/nextDouble` 等价于 `genrand_int32() * 2^-32`。
- 生物群系 Layer 仍使用 Java 64 位 LCG，不能用 MT 替换。

### 3.2 噪声初始化

`RandomLevelSource::RandomLevelSource`：

顺序固定为：

1. `PerlinNoise(16)` → `+0x20` minLimit
2. `PerlinNoise(16)` → `+0x24` maxLimit
3. `PerlinNoise(8)` → `+0x28` main
4. `PerlinSimplexNoise(4)` → `+0x2c` surface
5. `PerlinNoise(10)` → `+0x30` scale
6. `PerlinNoise(16)` → `+0x34` depth
7. `PerlinNoise(8)` → `+0x38` forest

全部由同一个 MT 连续初始化。`+0x30` 和 `+0x38` 在 `getHeights` 中未被引用，但必须创建，以保持随机流和结构一致。

### 3.3 高度图

`RandomLevelSource::getHeights`：

- depth：5×5
- main/min/max：5×17×5
- 群系数组：10×10
- 25 邻域权重：`10 / sqrt(i*i + j*j + 0.2)`
- `avgScale = avgScale * 0.9 + 0.1`
- `avgDepth = (avgDepth * 4 - 1) / 8`
- depth noise：`/8000`，负值 `*0.3`，`*3-2`，负值 `/2 → clamp(-1) → /1.4 → /2`，正值 clamp(1) 后 `/8`
- 最终密度：

```text
d1 = 12.0 / avgScale * (sliceY - base)
base = 8.5 + ((avgDepth + depth * 0.2) * 4.25)
```

当前 PHP 使用 `STRETCH_Y * 128 / 256 = 6`，这是主要地形偏差之一。**应改为 12.0**。

### 3.4 表层生成

`RandomLevelSource::buildSurfaces`：

- 表层噪声按 16×16 取样。
- 缩放值是 `0x3e000000`，即 **0.125**；不是 Java 的 0.0625，也不是 0.03125。
- 当前 PHP 使用 0.0625，需要改为 0.125。

`Biome::_placeBedrock`：

- 基岩层数 `(genrand & 3) + 2`，即 2..5 层。
- 当前 PHP 是逐 y 调 `nextBoundedInt(5)`，既慢又不一致。

`Biome::buildSurfaceAt`：

- `depth = (int)(noise / 3.0 + 3.0 + genrandFloat * 0.25)`
- top/filler/underwater 来自 `Biome + 0x44/0x45/0x46`
- y 在 59..64 附近切换沙滩方块
- y<63 且 top 为空气时：
  - 温度 <0.15 → 冰
  - 否则 → 水/岩浆
- 沙子转砂岩时额外深度 `genrand & 3`，且 y>=63 时再加 `y - 63`
- 表层从 y=127 向下处理，不是 255

### 3.5 loadChunk

`RandomLevelSource::loadChunk`：

- MT 种子：`chunkX * 0x14609048 + chunkZ * 0x7ebe2d5`
  - 即 Java 的 `341873128712 / 132897987541`，但使用 MT。
- 顺序：
  1. `fillBiomeData`
  2. `prepareHeights`
  3. `buildSurfaces`
  4. `LargeFeature::apply`（洞穴）
  5. 第二个 feature（峡谷）
  6. `recalcHeightmap`

### 3.6 postProcess

`RandomLevelSource::postProcess` 的实际顺序：

1. 湖泊
2. VillageFeature
3. MineshaftFeature
4. StrongholdFeature
5. RandomScatteredLargeFeature
6. 用区块种子重置 MT
7. 8 次 MonsterRoomFeature（地牢）
8. 冰冻（温度 <0.15 放冰）
9. `biome.decorate`
10. `_fixWaterAlongEdges`

湖泊逻辑：

- 沙漠/沙漠丘陵：只尝试 1/8 岩浆湖
- 其他群系：3/4 概率尝试 1/8 岩浆湖，1/4 概率直接水湖
- 水湖：`y = genrand & 0x7f`，要求 y>59
- 岩浆湖：`y = genrand % (genrand % 120 + 8)`，要求 y>59 且（y<64 或 `genrand % 10 == 0`）
- x/z：`genrand % 9 + 3 + chunkMin`

**`getTimeMs()` 异常已排除**：

`postProcess` 开头确实构造了一个时间种子的 MT，但在任何 `genrand` 调用之前，代码就用 `Level::getSeed()` 和 chunkSeed 重新初始化了同一个 MT。时间种子块是死代码/构造副作用，不影响结果。装饰 RNG 仍是确定性的。

### 3.7 结构

`LargeFeature::apply`：

- 用 `BiomeSource + 0x30` 的世界种子初始化 MT。
- 取两个随机数：`i = (r1 >> 2) * 2 + 1`，`j = (r2 >> 2) * 2 + 1`。
- 搜索半径 `this+4 = 8`，即 ±8 区块。
- 每个候选区块：
  - `chunkSeed = worldSeed ^ (localChunkX * i + localChunkZ * j)`
  - 用该种子重建 MT
  - 调用 `isFeatureChunk/createStructureStart`

**32 位截断**：

MCPE 的 `Random` 种子和结构坐标乘法都是 32 位无符号溢出。新反编译中的常量已经表现为 32 位截断值，例如村庄：

```text
seed = worldSeed
     + chunkX * 0x9939f508   // 341873128712 mod 2^32
     + chunkZ * 0xf1565bd5   // 132897987541 mod 2^32
     + 0x9e7f70             // 10387312
```

PHP 当前用 `Int64` 做 64 位运算，必须改为 32 位掩码运算。

`VillageFeature::isFeatureChunk`：

- 网格 40，偏移 28。
- 公式是 `rand % 28 + floorDiv(chunkCoord, 40) * 40 == chunkCoord`。
- 不是简单的“每格前 28 个有效”，负数坐标要用 floor division。

`MineshaftFeature`：

- `nextDouble() < 0.004`
- 且 `rand % 80 < max(abs(cx), abs(cz))`

`StrongholdFeature`：

- 0.14.3 确实生成要塞。
- 每个世界 3 个。
- 半径 `(genrand & 0xf) + 40` 区块。
- 角度步进 45°，找到后改 108°。
- 在 ±8 区块内找有效群系。
- `isFeatureChunk` 只对预计算的 3 个位置返回 true。

## 4. 对旧结论的修正

| 项目 | 旧结论 | Phase 0 修正 |
|---|---|---|
| 表层噪声缩放 | 0.03125 或 0.0625 | **0.125** |
| 高度密度 | 6 | **12.0** |
| postProcess 时间种子 | 可能是非确定性 | **死代码，实际仍用世界种子/chunkSeed** |
| 村庄间距 | “每格前 28 有效” | **`rand % 28 + floorDiv(chunk,40)*40`** |
| 结构种子运算 | 可继续用 64 位 | **必须按 32 位无符号截断** |
| 要塞 | 0.14 可能没有 | **确实生成，3 个/世界** |

## 5. MT19937 在 PHP 下的性能

测试环境：PHP 7.3.33 CLI，Android/Termux，8 核。

纯 PHP 实现，500 万次调用：

| 实现 | 每次耗时 | 吞吐 |
|---|---:|---:|
| MT `nextInt` | ~1522 ns | ~0.66 M/s |
| MT `nextDouble` | ~1754 ns | ~0.57 M/s |
| Java LCG `nextInt` | ~1179 ns | ~0.85 M/s |
| Java LCG `nextDouble` | ~1879 ns | ~0.53 M/s |
| 原生 `mt_rand` | ~261 ns | ~3.83 M/s |

结论：

- MT 不会成为主要瓶颈。
- MT 的 `nextDouble` 比 Java LCG 更快，因为只消费一个 32 位值；Java 需要两个。
- 单区块实际随机数消耗：
  - 噪声初始化：约 2 万次，仅世界初始化一次
  - 表层：每列约 2 次，约 500 次/区块
  - 装饰：数千次/区块
  - 结构：很少
- 按 1 万次/区块计算，约 15–18 ms；相比之下，PHP 的 Perlin 噪声循环通常远高于此。
- 原生 `mt_rand` 虽然快 6 倍，但只返回 31 位、使用全局状态，无法精确复现 MCPE 的 32 位流，不建议使用。
- 后续如果确实需要优化，可考虑：
  - 内联 `nextInt`，减少方法调用
  - 用 packed array / `SplFixedArray` 做对比测试
  - 只在区块级复用 Random，避免频繁构造

## 6. Phase 0 结论

可以进入 Phase 1。Phase 1 的前置条件已经满足：

- 7 个噪声的顺序和种子流已确认
- MT19937 的精确语义已确认
- `getTimeMs` 异常已排除
- 表层缩放、密度系数、结构 32 位运算等关键差异已定位

Phase 1 将只做：

1. `McpeRandom`（MT19937）
2. MCPE 版 `ImprovedNoise` / `PerlinNoise` / `PerlinSimplexNoise`
3. `Gorigional::init()` 的 7 噪声初始化顺序

不触碰 Layer、结构和村庄逻辑。
