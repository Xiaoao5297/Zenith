# MCPE 0.14.3 表面与群系细节（Phase 7）

## 7.1 针叶林表层

新增 `biome/TaigaBiome.php`，按 MCPE `TaigaBiome::buildSurfaceAt`：

- 普通针叶林（ID 5/19）保持草/土。
- 冷针叶林（ID 30/31）和 Mega Taiga（ID 32/33）：
  - 噪声 `> 1.75`：top 为粗泥（dirt meta 1）。
  - 噪声 `> -0.95`：top 为灰化土（podzol）。
  - 其他情况：草/土。

验证：

- seed 14（Cold Taiga）：`podzol=195`、`snow_layer=126`、树 `17/18` 正常。
- seed 19（Mega Taiga）：`podzol=66`，树 `17=229`、`18=448`。
- seed 93（Mega Taiga Hills）：树和表层正常。

## 7.2 寒冷群系积雪

`BaseBiome::snowDecorate()` 在以下群系的地表放置雪层：

- ID 12 Ice Plains
- ID 13 Ice Mountains
- ID 30/31 Cold Taiga
- ID 140 Ice Plains Spikes

只在该列最高方块为空气、且下方是草/土/石头/雪块/灰化土时放置 `SNOW_LAYER`，不会在树叶和水面上积雪。

验证：

- seed 42（Ice Plains）：`snow_layer=256`。
- seed 59（Ice Plains Spikes）：`snow_layer=256`、`snow_block=256`。
- seed 326（Ice Mountains）：雪层正常。

## 7.3 群系边界与变种

- `GenLayerBiomeEdge::getBiomeClass()` 已把森林类（4/18/27/28/29）和针叶林类（5/19/30/31/32/33）分组。
- `BiomeRegistry::registerMutations()` 已覆盖丘陵层会产生的 `id + 128` 变种，包括 129/130/131/132/133/134/140/149/151/155/156/157/158/160/161/162/163/164/165/166/167。
- 变种群系通过 `MutatedBiome` 继承父群系的高度和装饰，默认高度偏移 +0.1/+0.2。

## 7.4 其他群系

- 沙滩（16）与冷沙滩（26）继续使用沙子表层，石滩（25）使用石头表层。
- Mesa 的 37/38/39 已按 MCPE 色带和变种高度生成。
- 沼泽水塘、睡莲、甘蔗和藤蔓在 seed 7 验证：`water=158`、`lily=11`、`reeds=4`、`vines=96`。

## 7.5 验证

- 涉及文件 `php -l` 全部通过。
- seed 14/19/42/59/93/326 的 3×3 生成 + populate 均无异常，耗时 0.08–0.17s。
