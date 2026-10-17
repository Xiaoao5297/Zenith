# MCPE 0.14.3 地形改造 — Phase 2

日期：2026-10-17
状态：Phase 2 完成

## 1. 目标

按 MCPE 0.14.3 `RandomLevelSource::postProcess` 重排装饰阶段，并修正：

- 湖泊概率与坐标范围
- 地牢生成高度
- 寒冷群系水面结冰
- `decorate` 前的随机数重置

不追求逐种子 1:1 对齐，优先接近原版 MCPE 0.14 的观感。

## 2. 反编译依据

新版资料：`/storage/emulated/0/libminecraftpe_decompiled_full (3).zip`

参考文件：

- `world/RandomLevelSource.cpp` — `postProcess`
- `world/LakeFeature.cpp`
- `world/MonsterRoomFeature.cpp`
- `STRUCT_OFFSETS.md` / `SIGNATURES.md`

`postProcess` 的实际顺序：

1. 湖泊
2. Village
3. Mineshaft
4. Stronghold
5. RandomScatteredLargeFeature
6. 用区块种子重置 MT
7. 8 次地牢尝试
8. 16×16 结冰
9. `biome.decorate`

## 3. 湖泊

MCPE 的逻辑与 Java 1.7.10 差别很大：

```text
沙漠 / 沙漠丘陵：
    1/8 概率尝试岩浆湖

其他群系：
    1/4 概率直接尝试水湖
    3/4 概率再以 1/8 尝试岩浆湖
```

坐标：

- 水湖：
  - `x = chunkMinX + rand % 9 + 3`
  - `y = rand & 0x7f`
  - `z = chunkMinZ + rand % 9 + 3`
- 岩浆湖：
  - `x = chunkMinX + rand % 9 + 3`
  - `y = rand % (rand % 120 + 8)`
  - `z = chunkMinZ + rand % 9 + 3`
  - 要求 `y > 59`
  - `y >= 64` 时还要求 `rand % 10 == 0`

当前实现位于 `Gorigional::generateLakes()` 和 `tryLavaLake()`。

海洋/河流仍然跳过湖泊，避免在海底生成空气腔。

## 4. 地牢

MCPE 每次 `postProcess` 尝试 8 次：

```text
x = chunkMinX + (rand & 0xf) + 8
y = rand & 0x7f
z = chunkMinZ + (rand & 0xf) + 8
```

对应 PHP：

```php
$i3 = $rnd->nextBoundedInt(16) + 8;
$l3 = $rnd->nextBoundedInt(128);
$l1 = $rnd->nextBoundedInt(16) + 8;
```

地牢高度不再使用 0..255。

## 5. 结冰

MCPE 在装饰前遍历 16×16 列：

- 取每列最高的可受雨方块
- 若生物群系温度 < 0.15 且该方块是水，则在其位置放冰

当前实现：

```php
$biome = BiomeRegistry::getBiome($biomeIDs[$j * 16 + $i] & 0xFF);
if($biome->temperature >= 0.15){
    continue;
}
// 从 y=127 向下找第一个非空气方块
// 如果是水，替换为 ICE
```

## 6. 装饰前重置随机数

MCPE 在 `biome.decorate` 前重新用区块种子初始化 MT。

当前实现：

```php
$rnd->setSeed($seed);
$b->decorate($manager, $rnd, new BlockPos($x, 0, $z));
```

这样地牢/湖泊消耗的随机数不会影响树木、花草的分布。

## 7. 顺序调整

`populateChunk` 现在是：

```text
湖泊
Village
Mineshaft
Scattered
8 次地牢
结冰
decorate
```

要塞尚未重新启用，留到结构阶段处理。

## 8. 测试

- `php -l` 通过。
- 3×3 区块生成 + 中心区块 populate：
  - seed 1：0.296s，海洋，水 3721，岩浆 4
  - seed 2：0.340s，沙漠，正常
  - seed 3：0.324s，沙漠丘陵，岩浆 47
  - seed 14：0.359s，冷针叶林，正常
  - seed 42：0.353s，冰原，岩浆 88
- 结冰单测：冷群系 16×16 水面全部变为冰（256/256）。
- 4 区块生成约 2.1s，与 Phase 1 持平。

## 9. 仍未完成

- MCPE `LakeFeature` 的精确形状（当前仍用 Java 版 Lake）。
- 结构统一使用 MCPE `LargeFeature::apply` 播种和 32 位截断。
- 村庄 40/28 间距。
- 要塞重新启用。
- 装饰阶段完全改用 MT/区块种子（当前仍沿用原有 JavaRandom 区块种子）。
