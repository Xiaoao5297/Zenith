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

## 5.2 验证

- `ExtremeHillsBiome.php`、`SwampBiome.php`、`BiomeRegistry.php`、`Gorigional.php` 的 `php -l` 全部通过。
- seed 3（Extreme Hills+）：populate 约 0.09s，草/土正常，没有错误石头山顶。
- seed 7（Swamp）：树木、藤蔓、甘蔗等装饰正常。

## 5.3 待办

- Mesa 表层的黏土带/染色黏土仍未重写。
- 丛林 21/22/23 的专属装饰待验证。
- 群系边界（BiomeEdge）与变种群系注册仍可继续细化。
