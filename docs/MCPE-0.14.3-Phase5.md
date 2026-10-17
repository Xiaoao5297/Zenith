# MCPE 0.14.3 地形细化（Phase 5）

## 5.1 群系表层

### Extreme Hills

新增 `biome/ExtremeHillsBiome.php`，按 MCPE `ExtremeHillsBiome::buildSurfaceAt` 的三种类型处理：

- ID 3（Extreme Hills，type 0）：表层噪声 `> 1.0` 时 top/filler 改为石头，否则草/土。
- ID 20（Extreme Hills Edge，type 1）：始终草/土。
- ID 34（Extreme Hills+，type 1）：始终草/土。

`BiomeRegistry` 已用 `ExtremeHillsBiome` 替换原来的 `BaseBiome`。

合成石柱测试：

- 噪声 `-2`：草/土
- 噪声 `0`：草/土
- 噪声 `1.5`：top/filler 均为石头

### Swamp

新增 `biome/SwampBiome.php`，对应 MCPE `SwampBiome::buildSurfaceAt`：

- 使用群系级 `PerlinSimplexNoise` 采样 `x * 0.25, z * 0.25`。
- 噪声为正时，从 y=127 向下找到第一个实心方块：
  - 若高度正好是 y=62 且不是静水，则改为静水。
  - 噪声 `< 0.12` 时在 y=63 放睡莲。
- 最后仍调用默认表层生成。

单测（平地、石头到 y=62）：

```text
water=1967 lily=328
```

说明水面和睡莲分支都能触发。具体区块是否出现水塘取决于噪声位置，seed 7 的中心沼泽区块恰好没有正噪声。


## 5.2 Mesa 表层

重写 `MesaBiome`：

- 黏土色带改为 MCPE 的 64 层生成算法，使用 `McpeRandom`。
- 色带默认值是 16（硬化黏土），不再错误地从 0（白色染色黏土）开始。
- 新增 `BiomeRegistry::refreshMesa($seed)`，在 `Gorigional::init()` 中按世界种子重新生成色带，不同世界不再共用固定 seed 12345。
- 表层按 MCPE/Java 的台地结构：
  - y > 66 + depth：按色带生成染色黏土/硬化黏土。
  - y 62..66 + depth：红沙顶层。
  - y < 62：硬化黏土填充，不再把红沙一直填到地下。
- 基岩改为每列一次 `2 + (genrand & 3)` 层。

验证：

- seed 84 是 Mesa（群系 37），中心区块出现红沙 12、硬化黏土 172、染色黏土 159。
- seed 84 与 seed 86 的色带数组不同，确认按世界种子刷新。
- 中心区块 populate 约 0.1s。

## 5.3 丛林装饰

修正 `BaseBiome::jungleDecorate()`：

- 西瓜和藤蔓的 x/z 坐标从 `+8` 偏移改为区块内 0..15，与 MCPE 装饰范围一致。
- 西瓜高度改为该列 `getHeightAtForBiome()`，不再用 `nextBoundedInt(256)` 随机落在地下。
- 藤蔓从该列地表高度开始，而不是固定 y=128。
- `BlockPatch` 在 x/z 偏移后重新取该列高度，修复西瓜几乎不生成的问题。

验证：

- seed 71（Jungle，21）：原木 17=230、树叶 18=816、藤蔓 106=515、西瓜 103=4。
- seed 61（Jungle Hills，22）：原木 17=137、树叶 18=594、藤蔓 106=159。
- populate 约 0.17s。

## 5.4 验证

- `ExtremeHillsBiome.php`、`SwampBiome.php`、`BiomeRegistry.php`、`Gorigional.php` 的 `php -l` 全部通过。
- seed 3（Extreme Hills+）：populate 约 0.09s，草/土正常，没有错误石头山顶。
- seed 7（Swamp）：树木、藤蔓、甘蔗等装饰正常。

## 5.5 待办

- 群系边界（BiomeEdge）已按 Java/MCPE 的群系类别分组；变种群系（id+128）已在 `BiomeRegistry::registerMutations()` 注册并验证可生成。
- 后续可继续微调变种 Mesa 的台地类型和丛林巨型树的树冠细节。
