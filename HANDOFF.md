# 交接文档（Handoff）

> 面向下一个对话。当前重点是 **移植 SCAXE-GO-CE 的地形生成器**。先读本文件，再读 `docs/SCAXE-GO-CE/`。

## 1. 项目背景

- 仓库：`/data/data/com.termux/files/home/GitHub/Incore-Pro`（PocketMine-MP 分支核心，品牌 Zenith / 曾用名 Incore-Pro）
- 目标客户端：MCPE 0.11–0.15；PHP 7.3/7.4（**必须兼容 7.3，不能用 PHP 8 语法**）
- 命名空间：`pocketmine\`
- 参考源码：
  - `docs/lycore/lycore/`：lycore 完整源码（`lycore\` 命名空间），移植时对照它
  - `docs/SCAXE-GO-CE/`：Go 写的 0.14 服务端，**地形生成器与 0.14 原版同源**（本次重点）
- 另有一份测试拷贝 `~/GitHub/test/Incore-Pro`，**只改工作目录，不要动测试拷贝**
- 工作目录里的未跟踪文件 `src/pocketmine/utils/ConsoleLineEditor.php.bak` 与工作无关

## 2. 分支 / 提交现状

- 当前分支：`main`，与 `origin/main` 同步（已推送），HEAD = `ad527e6`
- 最近提交（新→旧）：
  - `ad527e6 feat(item): 创造模式加入带附魔的附魔书`
  - `7c12c67 feat(block/inventory): 修复流体向下流动并移植完整铁砧`
  - `43e0bdd feat(redstone): 移植 lycore 红石查询引擎与完整比较器/观察者`
  - `c74de96 feat(gameplay): 补全马骑乘输入与药水箭`
  - `6854669 feat(gameplay): 移植 lycore 生物装备/火焰弹/变种/马与红石补全`
  - `f181929 fix(anticheat): setLastSample 允许传入 null 采样`
  - `6f1cde6 feat(entity): 箱子矿车支持物品栏与战利品`
  - `340ac80 feat(level): 移植 lycore 结构生成与战利品消费`
- 未跟踪：`docs/SCAXE-GO-CE/`（参考源码）、`src/pocketmine/utils/ConsoleLineEditor.php.bak`

## 3. 已完成（简）

- 反作弊重构、lycore 结构生成/战利品、箱子矿车虚拟库存
- 生物装备（VanillaMobEquipment）、火焰弹、Husk/Stray、Horse 骑乘、药水箭
- 红石查询引擎 + 比较器/观察者/加权压力板、流体向下流动、完整铁砧、创造栏附魔书
- PHP 8.1 适配**已放弃**（pthreads 不支持 8.1，改动过大），继续用 PHP 7.3/7.4

---

## 4. 下一任务：移植 SCAXE-GO-CE 地形生成器（重点）

### 4.1 参考是什么

`docs/SCAXE-GO-CE/pkg/level/generator/gorigional/` 是 **Java 1.7 风格的 `ChunkGeneratorOverworld`**（MCPE 0.14 地形同源）。参数与 Java 1.7 一致：
`coordinateScale=684.412`、`heightScale=684.412`、`lowerLimitScale/upperLimitScale=512`、`depthNoiseScaleX/Z=200`、`mainNoiseScaleX/Z=80`、`mainNoiseScaleY=160`、`baseSize=8.5`、`stretchY=12`、`seaLevel=63`、`biomeSize=4`、`MaxHeight=128`。

**许可证：SCAXE-GO-CE 是 AGPL-3.0**，本核心是 LGPL/GPL 混合。翻译进来会让整体受 AGPL 约束（含"网络使用需提供源码"）。自用可，分发/开服需留意。

### 4.2 模块清单（`gorigional/` 约 6655 行 Go）

- `generator.go`：主生成器。`generateHeightmap`（5×5 水平点、垂直点 = MaxHeight/8+1，生物群系高度加权）、`SetBlocksInChunk`（体素三线性插值，>0 石、<海平面水）、`replaceBiomeBlocks`（表层）、`PopulateChunk`（矿井/村庄/要塞/神殿/湖泊/地牢/装饰）。
- `noise/`：`improved.go`（`ImprovedNoise`）、`octaves.go`（`OctavesNoise`）、`perlin_simplex.go`、`simplex.go`、`grass_color_noise.go`。
- `layer/`：约 20 个 `GenLayer`：`InitializeAll(seed)` 构建栈，返回 `[riverMix, voronoi, riverMix]`。含 `island/zoom/add_island/edge/deep_ocean/biome/biome_edge/hills/river/shore/smooth/voronoi/rare_biome/add_mushroom/add_snow/remove_ocean`。
- `structure/`：`map_gen_caves`、`map_gen_ravine`、`map_gen_mineshaft`、`map_gen_stronghold`、`map_gen_village`、`map_gen_scattered_feature` + `*_pieces`、`bounding_box`、`map_gen_base`。
- `biome/`（`pkg/level/generator/biome/`）：`biome.go`（接口 + `BaseBiome` + `GenTerrainBlocks` + 草色）、`registry.go`（ID→Biome）、`selector.go`、`decorator.go`、各 biome 文件、`biomes.go`。
- `biome_height.go`（`pkg/level/generator/`）：Java 高度/起伏表 + `GetBiomeHeight`。
- `object/`（`pkg/level/generator/object/`）：树/湖/矿/地牢/植被/藤蔓/睡莲等特征。

### 4.3 核心已有可复用/对照

- `level/generator/noise/glowstone|bukkit`：八度噪声（相近，但非 Java `ImprovedNoise` 逐位一致；要精确就移植 Go 的 `improved.go`/`octaves.go`）
- `level/generator/normal/object/`（树等）、`level/generator/populator/`
- 结构：`normal/populator/Mineshaft`、`normal/object/Stronghold`、`Dungeon`、`DesertStructures`、`JungleTemple`、`PillagerOutpost`、`WoodlandMansion`、`RuinedPortal`（此前从 lycore 移植）
- biome 类：`level/generator/normal/biome/`（ID/定义与 Java 不同，需映射或新建）

### 4.4 移植计划（建议新生成器 opt-in，不动现有 `Normal`）

新增类 `pocketmine\level\generator\gorigional\Gorigional`，在 `Server::registerGenerators()`（`src/pocketmine/Server.php` 约 2169 行，现有 `Normal`/`Flat`/`Nether`… 注册处）加 `Generator::addGenerator(Gorigional::class, "gorigional")`，世界配置选 `gorigional`。验证 OK 再考虑设默认。

- **P1（先做）**：`noise` + `layer` + `generateHeightmap`/`SetBlocksInChunk` + `replaceBiomeBlocks` → 先出「地形 + 生物群系」。
- **P2**：`map_gen_caves` + `map_gen_ravine`。
- **P3**：biome 装饰/特征（尽量复用核心 `normal/object`）。
- **P4**：结构（复用现有 Mineshaft/Stronghold/Village；移植 scattered feature 等）。
- **P5**：性能优化 + 与现有 `Normal` 对照验证。

### 4.5 适配要点

- **接口**：核心 `Generator`（`src/pocketmine/level/generator/Generator.php`）抽象方法：`__construct(array $settings=[])`、`init(ChunkManager $level, Random $random)`、`generateChunk($chunkX,$chunkZ)`、`populateChunk($chunkX,$chunkZ)`、`getSettings()`、`getName()`、`getSpawn()`；`getWaterHeight()` 可覆盖。Go 的 `GenerateChunk`/`PopulateChunk` 对应之。
- **Chunk API**：Go `world.Chunk`/`ChunkManager` ↔ 核心 `FullChunk`/`ChunkManager`（`setBlockId/setBlock/getBlockId/getBiomeId/setBiomeId` 等）。核心区块坐标是 `(x, y, z)`；**注意 Go 里 `GenTerrainBlocks` 用了 `chunk.SetBlock(chunkZ, y, chunkX)`（x/z 顺序反了）**，移植时统一成核心顺序。
- **方块 ID**：Go 用数字（1 石、2 草、3 泥、7 基岩、9 水、12 沙、13 砾、10/11 岩浆…，海平面 63）→ 映射到核心 `pocketmine\block\Block` 常量。
- **生物群系 ID**：Java 0–39、140 → 核心 biome 类（`Biome::getBiome`）或新建。
- **性能**：PHP 远慢于 Go。必须预计算/缓存噪声、减少对象分配，走现有异步生成管线（`GenerationTask`/`PopulationTask`）。参考现有 `Generator::getFastNoise3D` 的做法。
- **随机数**：Go 用 `math/rand` 的 `Random`（Java LCG）。核心有 `pocketmine\utils\Random`（同为 Java LCG），可直接对应；`layer` 的 `BaseLayer` 混种算法要原样翻译。

### 4.6 验证方式

- 逐文件 `php -l`
- 冒烟脚本放 `/data/data/com.termux/files/home/.cache/opencode/tmp/`（已有大量样例：`port_smoke.php`、`mineshaft_smoke.php` 等，自建 autoload 加载类并实例化）
- 地形建议做「同种子生成、与现有 `Normal` 对比区块/高度图」的脚本

## 5. 关键约定

- 移植文件：改命名空间/导入到 `pocketmine\`；完整复制的保留原版权头并加"移植自 xxx"注释；原文件无头的补说明
- **不要"自己实现"，优先对照 `docs/` 移植**
- 不要写多余注释；只改工作目录
- **每次工作完成后提交**（用户要求）；用户说推送才推送
- 不要动测试拷贝 `~/GitHub/test/Incore-Pro`

## 6. 关键路径速查

- 生成器基类：`src/pocketmine/level/generator/Generator.php`
- 现有主世界生成器：`src/pocketmine/level/generator/normal/Normal.php`
- 生成器注册：`src/pocketmine/Server.php`（约 2169 行 `registerGenerators`）
- Go 参考：`docs/SCAXE-GO-CE/pkg/level/generator/gorigional/`、`.../biome/`、`.../object/`、`.../biome_height.go`
