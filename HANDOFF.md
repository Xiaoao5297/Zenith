# 交接文档（Handoff）

> 面向下一个对话。本次重点：**把 SCAXE-GO-CE 的 gorigional 地形生成器移植进核心，并转向以 Java 1.7.10 反编译源码为权威参考做校正**。
> 先读本文件，再读 `docs/SCAXE-GO-CE/`（Go 参考）与 `docs/remapped_server_1.7.10/`（Java 权威参考）。

## 1. 项目背景

- 仓库：`/data/data/com.termux/files/home/GitHub/Incore-Pro`（PocketMine-MP 分支核心，品牌 Zenith / 曾用名 Incore-Pro）
- 远程已迁移：`https://github.com/Xiaoao5297/Zenith.git`（旧 `Incore-Pro.git` 会重定向）
- 目标客户端：MCPE 0.11–0.15；PHP 7.3/7.4（**必须兼容 7.3，不能用 PHP 8 语法**）
- 命名空间：`pocketmine\`
- 参考源码：
  - `docs/lycore/lycore/`：lycore 完整源码，移植时对照
  - `docs/SCAXE-GO-CE/`：Go 写的 0.14 服务端（gorigional 地形生成器来源，但**有 bug**）
  - `docs/remapped_server_1.7.10/`：**Minecraft Java 1.7.10 服务端反编译版**，世界生成权威参考
- 另有一份测试拷贝 `~/GitHub/test/Incore-Pro`（用户处可能已改名为 `~/GitHub/test/Zenith`），**只改工作目录，不要动测试拷贝**
- 未跟踪文件 `src/pocketmine/utils/ConsoleLineEditor.php.bak` 与工作无关

## 2. 分支 / 提交现状

- 当前分支：`feat/gorigional-generator`，已推送（`origin/feat/gorigional-generator`）
- 本会话提交（新→旧）：
  - `b543a13 fix(generator): 修复 Pond populator 参数不匹配导致区块填充崩溃`
  - `a559fbb perf(generator): 内联 ImprovedNoise 热循环`
  - `5fb8ed8 perf(generator): JavaRandom.next 48 位 LCG 步进特化`
  - `d8441a2 perf(generator): 装饰器方块访问缓存最近区块`
  - `fd0cfed perf(generator): layer 结果按 (x,z,w,d) 记忆化`
  - `7fae322 feat(generator): gorigional 接入村庄（复用核心 VillagePopulator）`
  - `496f31d feat(generator): 移植 gorigional 要塞与分散结构`
  - `8bfed04 feat(generator): 移植 gorigional 结构框架与矿井`
  - `ae77afc feat(generator): 移植 gorigional 树木与群系特化装饰`
  - `45a79fd feat(generator): 移植 gorigional 生物群系装饰（矿物/植被）`
  - `c5b76ef feat(generator): 移植 gorigional 洞穴与峡谷生成`
  - `0d64a49 feat(generator): 移植 SCAXE-GO-CE 地形生成器（地形+生物群系）`
- 更早：`main` 上 `ad527e6 feat(item): 创造模式加入带附魔的附魔书` 等

## 3. 已完成（gorigional 生成器，P1–P4）

新增 `src/pocketmine/level/generator/gorigional/`（约 100 个 PHP 文件）：

- **P1** `Int64`/`JavaRandom`、`noise/`（ImprovedNoise/OctavesNoise/SimplexNoise/PerlinSimplexGenerator）、`layer/`（完整 GenLayer 链 + LayerFactory）、`biome/`（BaseBiome/MesaBiome/BiomeRegistry）、`Gorigional.php`（generateHeightmap/setBlocksInChunk/replaceBiomeBlocks）
- **P2** `JavaMath`、`structure/MapGenBase|MapGenCaves|MapGenRavine`
- **P3** `object/`（Ore/Sand/Clay/Bush/Grass/DoublePlant/Vines/Lake/Dungeon/Decorator/全部树木等）、群系特化装饰
- **P4** 结构框架（BoundingBox/StructureStart/StructureComponentBase）、矿井、要塞、分散结构（沙漠/丛林神庙、沼泽小屋）；村庄复用核心 `normal/populator/VillagePopulator`
- 注册：`Server.php` 中 `Generator::addGenerator(Gorigional::class, "gorigional")`；世界配置 `generator: gorigional`

## 4. 性能优化（P5，已做）

| 优化 | 效果 |
|---|---|
| layer 结果按 `(x,z,w,d)` + epoch 记忆化（`GenLayer::getInts` final 包装，各层 `getIntsInternal`） | 49 区块 generate 47.2s→14.4s |
| `JavaRandom::next` 48 位 LCG 步进特化（3 次乘法替代 16 分肢） | generate 0.965→0.69s/块 |
| `ImprovedNoise::populateNoiseArray` 内联 lerp/grad | 噪声 102.6→78.2ms/块 |
| `ChunkManagerAdapter` 缓存最近区块 | 装饰 215→145ms |

优化后输出与优化前**逐块一致**（方块计数、群系分布、高度范围）。
设备（Termux/Android）降频导致基准波动 2–3×，A/B 需同进程背靠背测。

## 5. 关键 bug 修复

- **Pond populator 崩溃**（`b543a13`）：核心 `populator/Pond` 旧版调用 `object\Pond::canPlaceObject($level,$x,$y,$z)`，但签名要求 `Vector3` 且 `object\Pond` 是空壳。已按 `docs/lycore` 移植正确实现（水/岩浆泉 + 区域地表岩浆湖）。
- gorigional 移植中已修的 Go bug：`GenTerrainBlocks` 的 x/z 写反；变种群系回退（临时方案）。

## 6. ★ Java 1.7.10 参考对照结论（下一步重点）

`docs/remapped_server_1.7.10/` 是权威参考，关键类：`net/minecraft/world/gen/ChunkProviderGenerate.java`、`.../NoiseGenerator*.java`、`.../layer/*`、`.../biome/BiomeGenBase.java` + 各 biome、`.../structure/MapGen*`。

### 6.1 已一致（可放心）
- 噪声：`NoiseGeneratorImproved`/`Octaves` 与我们的实现一致；`NoiseGeneratorPerlin` 内部就是 Simplex，`PerlinSimplexGenerator` 正确。
- `GenLayer.func_75901_a` 与 `LayerFactory::initializeAll` 完全一致（默认 `b0=4`）。
- `GenLayerBiome` 的 warm/medium/cold/ice 列表一致。
- 高度图 `func_147423_a`、setBlocks `func_147424_a`、表层 `func_147422_a` 参数一致。

### 6.2 明确的 bug（改动小、影响大，**优先修**）
1. **populate 取错生物群系层**：Java `ChunkProviderGenerate.func_73153_a` 用 `world.getBiome(k+16,l+16)` → `WorldChunkManager.func_76935_a` → `BiomeCache` → **voronoi(1:1)**。我们 `Gorigional::populateChunk` 用 `$this->genLayer`（**riverMix，1:4**）。→ 湖泊/地牢/装饰群系判断全错。修法：改用 `biomeGen`（voronoi）在 `(chunkX*16+16, chunkZ*16+16)`；`GenLayerBiomeSource`（分散结构）同样应包 voronoi。
2. **变种群系未注册**：Java `BiomeGenBase.func_150566_k()` 注册约 21 个变种（129/130/131/132/133/134/140/149/151/155/156/157/158/160/161/162/163/164/165/166/167），`BiomeGenMutated` 高度 = 父群系 **+0.1 / +0.2**。我们只注册 129/130/140，其余回退基础群系（高度差 0.1/0.2）。修法：按 Java 注册全部变种并加高度偏移。
3. **`GenLayerHills` 的 riverLayer 初始化——我们之前改错了**：Java **没有**覆写 `func_75905_a`，riverLayer（`genlayer3`）世界种子保持 0；Go 也是。我们先前"修复"加了初始化，**反而偏离原版，应回退**（`GenLayerHills::initWorldGenSeed`）。

### 6.3 表层算法差异（`BiomeGenBase.func_150560_b`）
4. **缺冰**：Java 水下表层若 `func_150564_a(...) < 0.15` 放冰，我们没有。
5. **深沙未转砂岩**：Java `block1 = Blocks.sandstone`，我们保留沙。
6. **额外深度条件**：Java 无 `depth > 1`，我们多加了。

### 6.4 群系边界判定差异
7. **`func_151616_a`**：Java 用 `func_150562_l()`（实际类，如所有 `BiomeGenForest` 子类同类）比较；我们用自定义 `getBiomeClass`（把 mesa/jungle/taiga/ocean 归组）。分组不同 → `BiomeEdge`/`Hills` 边界结果不同。

### 6.5 湖泊与结构
8. **水湖概率**：Java `nextInt(4)`（1/4），我们 `nextBoundedInt(80)`（1/80），差 20 倍。（排除 Desert/DesertHills 与 Java 一致。）
9. **结构相位**：Java 村庄/要塞/矿井/神庙在 `func_73154_d`（区块生成）+ `func_73153_a`（填充）两阶段，用 `MapGenVillage/Stronghold/Mineshaft/ScatteredFeature`；我们只在 populate，且村庄复用核心 PNX 实现（算法不同，且冒烟环境未能确认其写入方块）。

## 7. 下一步建议

1. **以 Java 1.7.10 为唯一权威**，先修 6.2 的三条 bug（小改动、大收益），再修 6.3/6.4。
2. **验证**：用 **cubiomes**（支持 `MC_1_7`）或直接跑这个 Java 服务端，对同种子生成区块，与 PHP 输出**逐块 diff**（方块、群系、高度图、结构坐标）。
3. **结构**按 Java `MapGen*` 重移植，替换核心 PNV 村庄。
4. 若性能仍不达标，再考虑 P5 剩余项（洞穴、genTerrainBlocks 的 65536 次 RNG 保真难优化）。

## 8. 关键约定

- 移植文件：改命名空间/导入到 `pocketmine\`；完整复制的保留原版权头并加"移植自 xxx"注释
- **不要"自己实现"，优先对照 `docs/` 移植**；**每次工作完成后提交**（用户要求）；用户说推送才推送
- 不要写多余注释；只改工作目录；不要动测试拷贝
- 验证用 `php -l` + `/data/data/com.termux/files/home/.cache/opencode/tmp/` 下的冒烟脚本（如 `gorigional_smoke.php`、`gorigional_region.php`、`gorigional_populate2.php`、`gorigional_bench.php`、`gorigional_profile4.php`）

## 9. 关键路径速查

- gorigional 生成器：`src/pocketmine/level/generator/gorigional/`
  - 主类：`Gorigional.php`（generateChunk/populateChunk，含 `GenLayer::newEpoch()`）
  - 层：`layer/`（基类 `GenLayer.php` 含记忆化包装）
  - 噪声：`noise/`
  - 群系：`biome/BiomeRegistry.php`、`BaseBiome.php`
  - 结构：`structure/`
  - 装饰/对象：`object/`
- 生成器基类：`src/pocketmine/level/generator/Generator.php`
- 生成器注册：`src/pocketmine/Server.php`（`registerGenerators`，约 2169 行）
- Java 权威参考：`docs/remapped_server_1.7.10/net/minecraft/world/gen/`、`.../world/biome/`
- Go 参考：`docs/SCAXE-GO-CE/pkg/level/generator/gorigional/`
