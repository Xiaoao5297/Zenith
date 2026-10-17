# MCPE 0.14.3 群系边界与性能（Phase 9）

## 9.1 变种群系

- `BiomeRegistry` 补充 18（Forest Hills）、19（Taiga Hills）、31（Cold Taiga Hills）的变种注册，ID 分别为 146、147、159。
- 变种群系高度继续使用父群系 +0.1 / +0.2 的默认规则。
- 扫描 seed 1..40 的 64×64 群系区域，确认出现 129（Sunflower Plains）、146（Forest Hills M）、156（Birch Forest Hills M）。

## 9.2 群系边界

- `GenLayerBiomeEdge` 在比较群系类别和温度前，先把 128..255 的变种 ID 归一化到父群系。
- 这样 `Forest Hills M` 与 `Forest`、`Birch Forest Hills M` 与 `Birch Forest` 会被视为同类，减少变种群系边界处的错误突变。
- `GenLayerHills` 的突变映射补充 `Forest Hills`、`Taiga Hills`、`Cold Taiga Hills`，使这些丘陵也能生成对应变种。

## 9.3 性能

本机 PHP 7.3 CLI、seed 12345、半径 2 的 5×5 区块：

```text
25 chunks generate = 5.82s   (约 0.23s/区块)
25 chunks populate = 2.48s   (约 0.10s/区块)
peak memory = 12 MB
```

- 结构负结果缓存继续生效，重复扫描 ±8 区块不再重复创建结构。
- 群系层记忆化、最近区块缓存保持启用。

## 9.4 验证

- `BiomeRegistry.php`、`GenLayerHills.php`、`GenLayerBiomeEdge.php` 的 `php -l` 通过。
- seed 98（Mesa 变种）生成红沙、染色黏土，未出现群系回退到海洋。
- 森林、针叶林、变种群系的装饰回归正常。
