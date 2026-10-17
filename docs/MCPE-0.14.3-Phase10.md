# MCPE 0.14.3 回归与收尾（Phase 10）

## 10.1 回归矩阵

| 场景 | 种子/位置 | 结果 |
|---|---|---|
| 平原装饰 | seed 16, 0,0 | `31=32`、`38=9`，populate 0.08s |
| 丛林 | seed 71, 0,0 | `17=230`、`18=816`、`103=4`、`106=519` |
| Mesa | seed 84, 0,0 | `12=987`、`13=15`，染色黏土正常 |
| 冷针叶林 | seed 14, 0,0 | 树 59/357，podzol、雪层正常 |
| 冰原 | seed 42, 0,0 | 雪层 256 列 |
| 负坐标平原 | seed 16, -3,-3 | 正常生成 |
| 负坐标丛林 | seed 71, -5,2 | `17=118`、`18=730`、`106=454` |
| 湖泊/海洋 | seed 30, r=1 | water=2198、lava=8、surfaceWaterCols=615 |
| 村庄 | seed 30, 15,13 | 44 组件，路径/房屋/火把正常 |
| 跨区块村庄 | seed 30, x=12..18, z=11..19 | 方块跨多个区块，5.78s |
| 要塞启用路径 | seed 1 | 3 个位置，首要塞 145 组件 |
| 要塞默认关闭 | SimpleChunkManager | `stronghold === null` |

## 10.2 性能

5×5 区块（25 个）实测：

```text
generate 5.80s (0.232s/chunk)
populate 2.43s (0.097s/chunk)
peak mem 12MB
```

与 Phase 9 基本持平；版权头未引入运行时开销。

## 10.3 要塞配置

15 个 `genisys_*.yml` 的 `world:` 段均有：

```yaml
generate-stronghold: false
```

默认关闭；开启后 `Gorigional::init()` 才创建 `MapGenStronghold`。

## 10.4 版权头

- `src/pocketmine/level/generator/gorigional/` 下 135 个 PHP 文件全部带 Zenith LGPL 头。
- 本次补齐 132 个文件；已有头的 3 个为 `Gorigional.php`、`biome/TaigaBiome.php`、`structure/MapGenCaves.php`。
- gorigional 内未发现 lycore 来源文件，因此没有使用 lycore 头。

## 10.5 提交记录

```text
43e3067 Add Zenith license headers to gorigional
d79953a Phase 9: normalize biome edges and variant layers
f6c2fb6 Phase 8: complete temple loot and cross-chunk structures
1f7c10d Phase 7: add taiga surfaces and snow cover
348e58f Phase 6: align MCPE cave rarity and disable ravines
fba602f Phase 5.4: align biome edges and variant decorators
496b3cf Phase 5.3: fix specialized biome decoration placement
1003bb4 Phase 5.2: improve mesa bands and jungle decoration
0c2626e Phase 5.1: add MCPE extreme hills and swamp surfaces
5885396 Config: make strongholds optional
```

## 10.6 已知限制

- 目标仍是观感接近 MCPE 0.14.3，不追求种子逐块对齐。
- 村庄组件数可能达到 44 个，未人为压缩。
- 要塞部件几何参考 Java 1.7.10，不是 MCPE 逐块复刻。
- 湖泊为 MCPE 椭球 + 壳层近似。
- 未推送；`docs/SCAXE-GO-CE/`、`docs/remapped_server_1.7.10/`、`session.json`、`ConsoleLineEditor.php.bak` 仍未跟踪。
