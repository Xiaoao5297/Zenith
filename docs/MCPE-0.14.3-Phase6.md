# MCPE 0.14.3 Phase 6：洞穴与峡谷

## 6.1 洞穴随机流

- `MapGenCaves` 内部随机流改为 `McpeRandom`（MT19937）。
- 区块级生成阈值改为 MCPE `LargeCaveFeature::addFeature` 的 `1/15`，不再使用 Java 的 `1/7`。
- 隧道/房间种子改为 `nextInt() >> 1`，局部随机流也用 MT。
- 世界高度仍限制在 0..127。

## 6.2 峡谷

- MCPE 0.14.3 反编译中没有独立的 RavineFeature；`RandomLevelSource::loadChunk` 只调用一次 `LargeCaveFeature::apply`。
- 因此 `Gorigional::generateChunk()` 不再调用 `MapGenRavine`。
- `MapGenRavine` 类保留，便于以后需要时重新启用，但默认不参与生成。

## 6.3 验证

- `MapGenCaves.php`、`Gorigional.php` 的 `php -l` 通过。
- seed 11：中心区块 y=5..54 空气 259 格。
- seed 16：中心区块 y=5..54 空气 82 格。
- seed 30、84：该中心区块没有洞穴，符合 1/15 的随机分布。
- seed 16/11/30 的装饰与表层统计无异常。
