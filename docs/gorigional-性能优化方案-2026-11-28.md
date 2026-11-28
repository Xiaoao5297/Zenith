# gorigional 地形生成性能优化方案

> 日期：2026-11-28
> 范围：`src/pocketmine/level/generator/gorigional/` 及其调用的区块格式、任务与光照代码
> 状态：只读分析，尚未修改任何代码、配置或世界
> 类型：重构计划

## 1. 结论

当前最值得做的不是立刻重写 GenLayer 或地形算法，而是先把**没有计时的部分**测出来，再做三类低风险优化：

1. 补齐 `populate` 开头群系查询、`decorate` 子项，以及 `populate TOTAL` 之后的高度图、天光、序列化计时。
2. 对暖群系跳过无意义的结冰/积雪检查，并让 `replaceBiomeBlocks()` 已经算出的 16×16 群系直接复用给 `WorldChunkManager`。
3. 处理 `populateSkyLight()` 的逐格 `setChanged()` 和重复高度图重算。

GenLayer、方块批量写入、任务序列化属于结构性优化，应该等前两步的基准数据出来后再决定，避免为了少量收益破坏 Java 1.7.10 对齐。

本轮不修改代码，以下收益都是基于现有日志和代码路径的估算，不是优化后的实测结果。

## 2. 现有证据

### 2.1 日志样本

用户提供的两块区块都生成在 **savanna（群系 35）**，来自 8 个异步 worker 的运行日志。单块墙钟时间包含 4 核抢占、日志 I/O 和备份干扰，不能直接当作 CPU 剖面。`debug.generator-timing: true` 本身每块约 20 行 `info` 日志，`sprintf` 和日志锁也会放大耗时；正式基准应关闭该开关或换用空日志器。

| 阶段 | chunk(7,12) | chunk(6,12) | 说明 |
|---|---:|---:|---|
| `generate TOTAL` | 152.1 ms | 194.1 ms | 单块生成总计 |
| `terrain` | 80.5 ms | 84.2 ms | 包含下面三个子阶段 |
| ├ `biome-sample` | 51.2 ms | 52.0 ms | 10×10 GenLayer 查询 |
| ├ `heightmap/noise` | 20.7 ms | 23.0 ms | 五个噪声区域 |
| └ `density/fill` | 8.1 ms | 8.5 ms | 插值并写方块 |
| `surface/biomes` | 52.8 ms | 79.6 ms | 16×16 列 × 256 层 |
| `caves` | 13.3 ms | 9.4 ms | 已比 Phase 12 明显下降 |
| `ravines` | 1.7 ms | 1.7 ms | 稳定 |
| 四个结构 prepare | 2.8 ms | 15.0 ms | 只记录结构起点 |
| `populate TOTAL` | 112.4 ms | 441.9 ms | 第二块明显是结构/装饰离群值 |
| ├ `struct-populate` | 4.5 ms | 42.0 ms | 真正写结构方块 |
| ├ `dungeons x8` | 1.9 ms | 0.4 ms | 通常很小 |
| ├ `decorate` | 59.3 ms | 179.2 ms | 一个总计时器，内部未拆 |
| ├ `freeze/snow` | 17.1 ms | 59.6 ms | 暖群系仍在逐列检查 |
| └ 未计入部分 | 29.6 ms | 160.8 ms | 主要是 populate 开头的群系查询 |

`terrain` 已经包含 `biome-sample`、`heightmap/noise`、`density/fill`，不能重复相加。

### 2.2 未计入的 populate 开头

`Gorigional::populateChunk()` 在第一个计时器之前执行：

```php
GenLayer::newEpoch();

$x = $chunkX * 16;
$z = $chunkZ * 16;

$ints = $this->biomeGen->getInts($x + 16, $z + 16, 1, 1);
$biomeID = $ints[0] & 0xFF;
```

这段代码位于 `Gorigional.php` 约 570–601 行。单独探测同一段代码，chunk(6,12) 约 97.9 ms，chunk(7,12) 约 18.1 ms，和日志中 29.6 / 160.8 ms 的缺口方向一致。它是当前 `populate` 最大的未解释成本之一。

### 2.3 代码路径上的固定成本

- `setBlocksInChunk()` 每块调用一次 `genLayer->getInts(chunkX*4-2, chunkZ*4-2, 10, 10)`，对应 51–52 ms 的 `biome-sample`。
- `replaceBiomeBlocks()` 每块调用一次 `biomeGen->getInts(chunkX*16, chunkZ*16, 16, 16)`，再对 16×16 列执行 `genTerrainBlocks()`。
- `BaseBiome::genTerrainBlocks()` 的循环是 `y = 255..0`。`y >= 128` 时不读不写方块，但仍要消耗 `nextBoundedInt(5)`，这是 Java 随机流对齐的必要成本。
- `freezeChunk()` 对 `x+8..x+23`、`z+8..z+23` 的 256 列各执行一次 `getHeight()`，并调用 `canBlockFreeze()` 与 `canSnowAt()`；两者各自再查一次群系温度。
- `Decorator::decorate()` 包含 82 条矿脉、树、花草、蘑菇、芦苇、南瓜、仙人掌、50 个水泉和 20 个岩浆泉，目前只有一个总计时器。
- `PopulationTask` 在 `populateChunk()` 之后还执行 `recalculateHeightMap()`、`populateSkyLight()`、`setLightPopulated()`、`setPopulated()`、`toFastBinary()`。这些都不在 `populate TOTAL` 内。
- `populateSkyLight()` 每列最多写 128 次 `setBlockSkyLight()`，而 `mcregion/Chunk::setBlockSkyLight()` 每次都会 `setChanged()`。

## 3. 优化顺序

### 阶段 0：先补计时，不改变行为

这一阶段只增加计时点，风险最低，也是后续所有判断的前提。

| 计时点 | 位置 | 目的 |
|---|---|---|
| `populate-biome` | `populateChunk()` 开头 1×1 查询前后 | 确认 29.6 / 160.8 ms 缺口的实际占比 |
| `decorate-ores` | `Decorator::decorate()` 矿石段 | 区分 82 条矿脉与其他特征 |
| `decorate-trees` | 树、大蘑菇、花草段 | 观察树密度高的群系 |
| `decorate-plants` | 芦苇、南瓜、仙人掌、蘑菇 | 观察植被型群系 |
| `decorate-springs` | 50 水泉 + 20 岩浆泉 | 泉水是固定大循环 |
| `post-heightmap` | `PopulationTask` 的 `recalculateHeightMap()` | 高度图重算成本 |
| `post-skylight` | `populateSkyLight()` | 天光写入成本 |
| `post-serialize` | `toFastBinary()` | 任务序列化成本 |

同时把 `logTiming()` 的内存统计从 `memory_get_usage(true)` 改为 `memory_get_usage(false)`。当前 2 MB 粒度会大量输出 `+0.00 MB`，看不到真实增量。

### 阶段 1：低风险、结果不变的优化

#### 1.1 暖群系提前跳过结冰/积雪

`freezeChunk()` 对 savanna 仍然逐列计算温度，但 savanna 的温度是 1.2，整个 128 高世界都不可能降到 0.15 以下。

建议在每列只取一次群系，并用温度上界提前返回：

```text
如果 biome.temperature 明显高于 0.15 能达到的上限：
    跳过该列的 canBlockFreeze 和 canSnowAt
```

保守做法是取 `temperature > 0.3` 才跳过；这样即使考虑高度降温和噪声，也不会误删寒冷群系的冰/雪。对这两块 savanna，`freeze/snow` 的 17.1 / 59.6 ms 大部分可以消失。

同一列的两个检查还应复用同一个群系对象和同一次温度噪声。`getTemperatureAt()` 对 `y > 64` 每次都重新取 Perlin 噪声；在 `top > 64` 时先算 `top` 的温度，再用 `temp(top+1) = temp(top) - 0.05/30` 推导，可以少一次噪声查询，同时保持数值一致。

预期：暖群系 `freeze/snow` 接近 0；寒冷群系仍有成本，但会少一次群系查找和一次噪声查询。

风险：低。必须用寒冷种子验证冰/雪方块完全不变。

#### 1.2 复用 `replaceBiomeBlocks()` 的群系数组

`replaceBiomeBlocks()` 已经拿到 16×16 的 `biomeIDs`，但没有写入 `WorldChunkManager` 的区块缓存。随后 `populateChunk()` 又用 1×1 的 `biomeGen->getInts()` 重新走一遍 GenLayer。

建议增加一个“预填充缓存”的入口，例如：

```text
WorldChunkManager::primeBiomeCache(chunkX, chunkZ, biomeIDs)
```

在 `replaceBiomeBlocks()` 写完 `biomeIDs` 后调用；`populateChunk()` 改为通过 `GeneratorWorld->getBiome($x + 16, $z + 16)` 读取。若邻居区块已经生成，缓存命中；若未生成，仍回退到原来的 GenLayer 查询。

这样做的输出应与现在完全一致，因为缓存的 16×16 数组和 `populate` 的 1×1 查询来自同一个 `biomeGen`。

预期：消除 18–98 ms 的 `populate` 开头查询，并让 `freezeChunk()` 的群系查找也命中缓存。

风险：低到中。需要验证 3×3 人口任务中邻居未生成的边界情况，不能把未生成的区块缓存成空数组。

#### 1.3 `populateSkyLight()` 去掉重复高度图重算

`PopulationTask` 的顺序是：

```text
populateChunk()
recalculateHeightMap()
populateSkyLight()
```

`populateSkyLight()` 只写天光，不改方块，但它末尾又调用一次 `getHighestBlockAt(..., false)` 重算高度图。对于“先 recalculate 再 populateSkyLight”的调用路径，这次重算是冗余的。

建议给 `populateSkyLight()` 增加“高度图已是最新”的参数，或拆出一个不重算高度图的内部方法。不要直接删除公共方法里的重算，因为其他调用路径可能依赖它。

预期：每块少 256 次列扫描；具体收益要先由 `post-skylight` 计时确认。

风险：中。必须对比天光数组和高度图数组，确保完全一致。

#### 1.4 天光批量写入

`mcregion/Chunk::setBlockSkyLight()` 每次调用都会 `setChanged()`。`populateSkyLight()` 对每列最多写 128 次，等于每块最多 32768 次 mutationCounter 自增。

建议增加“批量天光写入”路径：先写完整列/完整区块的天光数据，最后只 `setChanged()` 一次。不要在生成过程中逐格调用公共 `setBlockSkyLight()`。

预期：取决于 `post-skylight` 的实际占比，可能是 `populate TOTAL` 之后最大的一项。

风险：中。天光格式是半字节数组，批量写入必须保持 `(x<<10)|(z<<6)|(y>>1)` 的布局和奇偶半字节顺序。

#### 1.5 扩大 `ChunkManagerAdapter` 的区块缓存

当前只缓存最近一个 chunk。装饰器和结构会跨区块读写，树木、矿脉、湖泊和结构很容易在相邻 chunk 之间来回切换。

建议改为固定 3×3（或 5×5）区块缓存，按 `(cx,cz)` 取，满了用简单的 FIFO/LRU 淘汰。不要无限增长。

预期：减少 `SimpleChunkManager->getChunk()` 的重复查找；具体收益需要 `decorate` 子计时确认。

风险：低。缓存只是查找加速，不改变方块读写语义。

### 阶段 2：结构性优化，先做基准再决定

#### 2.1 GenLayer tile 缓存

`biome-sample` 稳定 51–52 ms，是 `generate` 中最大的一块。每块查询 10×10 个 1:4 群系单元；相邻区块的查询区域有重叠，但 `getInts()` 只按精确矩形缓存，且 `newEpoch()` 每块清空，所以跨区块基本不复用。

可选方案：

- 按固定 tile（例如 32×32 或 64×64 个 1:4 单元）计算一次，再从 tile 切片给多个区块使用。
- 保留有界 LRU，而不是每块 `newEpoch()` 全清。
- 只缓存最上层 `riverMix`/`voronoi` 的结果，不缓存整条链。

预期上限：如果 tile 命中率高，`biome-sample` 可下降 20%–40%。但这会改变缓存的边界行为，必须用固定种子逐方块对比。

风险：高。GenLayer 的随机流和坐标语义很容易在切片时出错；不建议作为第一项改动。

#### 2.2 方块批量写入

`setBlocksInChunk()` 的 `density/fill` 只有 8 ms，暂时不是瓶颈。`surface/biomes` 的 53–80 ms 更值得关注，但它的成本一半来自 256 层循环和 `nextBoundedInt`，不全是 `setBlock`。

如果后续确认 `setBlock` 占比高，可以考虑按列构造临时数组再一次性写入，或直接操作 `mcregion` 的 `blocks` 字符串。不要把这项和 GenLayer 优化绑在一起。

风险：高。会绕过 `FullChunk` 的变更通知和边界检查，容易引入区块不同步。

#### 2.3 PopulationTask 序列化与邻居策略

`PopulationTask` 构造时把中心 + 8 邻居 `toFastBinary()`，运行后再反序列化 9 个区块。8 worker 并发时这部分内存和 CPU 都不小，但日志完全没有覆盖。

先加 `post-serialize` 计时。如果占比低，不要动；如果占比高，再评估是否只传中心区块、邻居按需读取，或减少不必要的邻居写回。

风险：高。邻居写回影响跨区块结构、树木和湖泊，不能只看单块输出。

## 4. 建议的落地顺序

1. **只加计时**：`populate-biome`、`decorate-*`、`post-heightmap`、`post-skylight`、`post-serialize`，并把内存统计改为 `false`。
2. **暖群系 freeze 跳过 + 单列温度复用**：收益明确，风险低。
3. **群系缓存预填充**：消除 `populate` 开头的 GenLayer 查询，风险低到中。
4. **天光批量写入 / 去掉重复高度图重算**：先看 `post-skylight` 数据，再决定是否做。
5. **3×3 ChunkManagerAdapter 缓存**：低风险，可与第 2 或第 3 项一起做。
6. **GenLayer tile 缓存、方块批量写入、任务序列化**：等前五项完成并有稳定基准后再评估。

## 5. 验收与风险

### 5.1 必须保持不变的输出

优化不能改变以下任何一项：

- 同种子、同坐标的整区块方块 ID 与 meta 数组。
- 群系 ID、群系颜色、高度图。
- 洞穴、峡谷、湖泊、地牢、村庄、要塞、散落结构的方块布局。
- 冰、雪、天光数据。
- 结构起点和 `StrongholdStart` 的传送门存在性。

### 5.2 基准方法

- 固定 PHP 版本、opcache 设置和设备，只跑单 worker，避免 8 worker 抢占污染数据。
- 每项优化至少跑 3 次，取中位数；同时记录 `generate`、`populate`、`post-*` 和总墙钟。
- 使用 `seed=12345` 的固定区块集，以及至少一个寒冷种子、一个 Mesa 种子、一个含村庄/要塞的种子。
- 现有临时 harness 可继续使用：`gen_test.php`、`gen_nopop.php`、`gen_multi.php`、`stronghold_test.php`。

### 5.3 停止条件

出现以下任一情况就停止该项优化，不回退到“看起来差不多”：

- 整区块方块哈希变化，且变化不能解释为已知的 Java 对齐修正。
- 寒冷群系的冰/雪消失或位置改变。
- 天光数组出现差异。
- 跨区块结构在邻居边界被截断。
- 为了性能删除 Java 要求的随机数消耗。

## 6. 本轮没有做的事

- 没有修改任何 PHP 源码、配置、世界或测试。
- 没有重启服务器、没有重新生成世界、没有改动部署目录。
- 没有给出优化后的实测数字；第 3 节的收益都是代码路径推算，必须由阶段 0 的计时确认。
- 没有把 `decorate` 的内部顺序、GenLayer 缓存策略或天光格式当作已决定方案；这些都需要单独的对照测试。
