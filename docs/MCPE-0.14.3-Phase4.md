# MCPE 0.14.3 地形改造 — Phase 4 报告

日期：2026-10-17
状态：Phase 4 完成（要塞已接入生成流程）

## 1. 目标

继续按照“**地形观感接近原生 MCPE 0.14.3，不要求种子逐块 1:1**”推进。

本阶段只做要塞：

- 每个世界 3 个要塞
- 半径 40–55 区块起步
- 找到后角度 +108°、半径 +8
- 没找到则角度 +45°、半径 +4
- 要塞部件与 Java 1.7.10 的 Stronghold 家族对齐
- 终点包含末地传送门房间

## 2. 新增文件

均在 `src/pocketmine/level/generator/gorigional/structure/`：

| 文件 | 作用 |
|---|---|
| `MapGenStronghold.php` | MCPE 三要塞选址、区块匹配 |
| `StrongholdStart.php` | 要塞起点、组件展开、整体下移 |
| `StrongholdPiece.php` | 要塞部件基类、门、随机石砖、子部件注册 |
| `StrongholdPieces.php` | 权重表与部件工厂 |
| `StrongholdStairs2.php` | 起始楼梯 |
| `StrongholdStraight.php` | 直走廊 |
| `StrongholdCorridor.php` | 兜底短走廊 |
| `StrongholdPrison.php` | 监牢 |
| `StrongholdLeftTurn.php` | 左转 |
| `StrongholdRightTurn.php` | 右转 |
| `StrongholdCrossing.php` | 五岔路口 |
| `StrongholdRoomCrossing.php` | 大房间 |
| `StrongholdStairs.php` | 向下楼梯 |
| `StrongholdStairsStraight.php` | 长直楼梯 |
| `StrongholdChestCorridor.php` | 带箱子的走廊 |
| `StrongholdLibrary.php` | 图书馆 |
| `StrongholdPortalRoom.php` | 末地传送门房间 |

## 3. 选址逻辑

`MapGenStronghold` 在首次需要时懒计算位置：

```text
angle = nextFloat() * 2π
radius = 40 + (nextInt() & 0xf)
```

然后循环：

- 候选区块 `(round(cos(angle) * radius), round(sin(angle) * radius))`
- 在中心与四角采样群系
- 全部属于陆地群系才接受
- 接受后 `angle += 108°`，`radius += 8`
- 拒绝后 `angle += 45°`，`radius += 4`
- 最多 128 次尝试，目标 3 个

陆地群系排除海洋、河流、沙滩、蘑菇岛，保留平原、森林、沙漠、针叶林、沼泽、热带草原、丘陵、冰原等。

## 4. 部件展开

`StrongholdStart`：

1. `StrongholdPieces::reset()` 重置权重。
2. 在 `(chunkX*16+2, 64, chunkZ*16+2)` 创建 `StrongholdStairs2`。
3. 调用起点的 `buildComponent()`。
4. 循环从 `pendingPieces` 随机取部件继续展开。
5. 最多 120 个组件，避免无限膨胀。
6. `updateBoundingBox()` 后 `placeAtHeight($world, $rnd, 5)`。

权重：

```text
Straight 40
Prison 5/5
LeftTurn 20
RightTurn 20
RoomCrossing 10/6
StairsStraight 5/5
Stairs 5/5
Crossing 5/4
ChestCorridor 5/4
Library 10/2
PortalRoom 20/1
```

## 5. 传送门房间

`StrongholdPortalRoom` 生成：

- 11×8×16 房间
- 入口台阶
- 水平 3×3 末地传送门方块（ID 119）
- 5×5 末地传送门框架外圈（ID 120）
- 前方岩浆池
- 刷怪笼（ID 52）

早期实现把框架放在局部 `z=14` 的整面墙上，旋转后会落到包围盒外，实际世界看不到。已改为水平传送门，世界测试中确认出现 `119=9`、`120=16`。

## 6. 重要修复：结构写入包围盒

`MapGenStructure::generateStructure` 原先使用：

```php
$k = ($chunkX << 4) + 8;
$l = ($chunkZ << 4) + 8;
```

导致结构只在区块东/南半边写入，西/北半边丢失。已改为：

```php
$k = $chunkX << 4;
$l = $chunkZ << 4;
```

这个修复影响所有结构：村庄、矿洞、要塞、神庙。

## 7. 验证

所有新增和修改文件 `php -l` 通过。

seed 1：

- 3 个要塞位置：`-29,-51`、`66,-14`、`-8,75`
- 首要塞 prepare 约 0.021s
- 139 个组件
- bbox `-542,33,-817 .. -375,59,-728`
- 组件包含 Stairs2、Straight、PortalRoom、RightTurn、RoomCrossing、LeftTurn、StairsStraight、Crossing、Library、Prison、Stairs、ChestCorridor
- 7×7 区块生成约 8.25s
- 中心区块 populate 约 0.153s
- 中心方块统计含 `98=245`、`109=11`，石砖与台阶正常

其他种子：

- seed 2：3 个位置，首要塞 157 个组件
- seed 3：3 个位置，首要塞仅 5 个组件，但仍包含传送门
- seed 42：149 个组件
- seed 12345：6 个组件
- 均无死循环

## 8. 已知限制

- 要塞部件布局参考 Java 1.7.10，几何细节不是 MCPE 的逐块复刻。
- 选址使用 5 点群系采样，不是 MCPE 的 BiomeSource 回调全量判定。
- 组件数量可能较大（139–157），单次要塞 prepare 仍很快，但完整 7×7 生成约 8s。
- 尚未做要塞内部的完整战利品表/刷怪行为，只保证方块结构。
- 仍未推送。
