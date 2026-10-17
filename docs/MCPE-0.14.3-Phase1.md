# MCPE 0.14.3 地形改造 — Phase 1 记录

日期：2026-10-17
提交：`3d6c01c`（Phase 1: use MCPE MT19937 noise and fix biome surfaces）

> 目标说明：不要求“种子 -> 地形”与 MCPE 逐方块 1:1 对齐，目标是地形观感接近原生 MCPE 0.14.3；同一 seed 的地形只要和原版大致相似即可。

## 1. 随机数

新增：

- `RandomSource`：统一 `nextBoundedInt` / `nextDouble` / `nextFloat` 接口。
- `McpeRandom`：标准 MT19937，`nextInt()` 返回 32 位无符号值，`nextBoundedInt(n)` 直接取模，无 Java rejection。
- `JavaRandom` 实现 `RandomSource`，保留给仍使用 Java LCG 的旧代码。

验证：标准 seed 5489 的前 5 个输出与 MT19937 参考值一致：

```text
3499211612, 581869302, 3890346734, 3586334585, 545404204
```

## 2. 噪声

`Gorigional::init()` 改为用 `McpeRandom` 连续初始化 7 个噪声：

```text
PerlinNoise(16)
PerlinNoise(16)
PerlinNoise(8)
PerlinSimplexNoise(4)
PerlinNoise(10)
PerlinNoise(16)
PerlinNoise(8)   // forestNoise，getHeights 不使用但必须消费随机流
```

`ImprovedNoise`、`OctavesNoise`、`PerlinSimplexGenerator`、`SimplexNoise` 的构造参数改为 `RandomSource`。

## 3. 高度与表层参数

- 高度密度：`6.0` → `12.0`，即 `(sliceY - base) * STRETCH_Y / avgScale`。
- 表层噪声缩放：`0.0625` → `0.125`（MCPE `0x3e000000`）。
- 表层循环：`255..0` → `127..0`，符合 MCPE 世界高 128。
- 基岩：每列只消费一次随机，连续放 `2..5` 层，不再逐 y 调随机。

## 4. 群系表层方块

`BaseBiome` 新增：

```php
public $topBlock = Block::GRASS;
public $topMeta = 0;
public $fillerBlock = Block::DIRT;
```

`BiomeRegistry` 配置特殊群系：

| 群系 | top | filler |
|---|---|---|
| 沙漠 / 沙漠丘陵 | sand | sand |
| 沙滩 | sand | sand |
| 石滩 | stone | stone |
| 蘑菇岛 / 蘑菇岛岸 | mycelium | dirt |
| Mesa 37/38/39 | sand meta 1 | hardened clay |
| 冰原尖峰 140 | snow block | dirt |

`BaseBiome::genTerrainBlocks` 已按 MCPE 调整：

- y 59..64 使用本群系 top/filler。
- y<63 且 top 为空气时，温度 <0.15 放冰，否则放水。
- 沙转砂岩的额外深度为 `(rand & 3) + max(0, y-63)`，去掉了原先错误的 `depth > 1` 条件。
- `MesaBiome` 循环改为 127..0，并补上基岩层。

## 5. 测试

- 所有改动文件 `php -l` 通过。
- 4 区块 smoke test：约 2.05s。
- chunk(0,0) 方块计数：

```text
0=13872 1=17422 2=250 3=338 7=886
```

- 合成石柱表层测试：

```text
Desert: sand -> sandstone
Ice Plains: grass -> dirt -> stone
Mushroom Island: mycelium -> dirt -> stone
Beach: sand -> sandstone
Stone Beach: stone
Mesa: sand -> stained clay -> stone
Ice Plains Spikes: snow block -> dirt -> stone
```

## 6. 尚未完成

- `postProcess` 顺序、MCPE 湖泊概率、地牢 y 范围、冷水结冰。
- 结构统一使用 MCPE 的 `LargeFeature::apply` 播种；32 位无符号截断。
- 村庄 40/28 间距、要塞重新启用。
- 装饰阶段用 MT/区块种子替换 JavaRandom 的细节。

## 7. 工作区

本次提交只包含 Phase 1 相关文件。以下预先存在的修改/未跟踪文件未纳入提交：

- `structure/GeneratorWorld.php`
- `structure/StructureComponentBase.php`
- `structure/WorldAccess.php`
- 16 个 `Village*.php`
- `docs/SCAXE-GO-CE/`
- `docs/remapped_server_1.7.10/`
- `ConsoleLineEditor.php.bak`
