# gorigional 与 Java 1.7.10 / MCPE 0.14 的差异对照

> 结论：`gorigional` 已按 `docs/remapped_server_1.7.10` 的
> `net/minecraft/world/gen/` 对齐——高度场、区块随机数、洞穴、峡谷、基岩、村庄。
> 0.14 MCPE 原版（`libminecraftpe` 反编译）**不是** 1.7.10，两者数值不同，见下表。
>
> 硬限制：Java 1.7.10 是 256 高，本项目的区块格式与 0.14 客户端只支持 128 高，
> 所以目标只能是"**y<128 内尽量逐块等于 Java**"，y>127 永远做不到。

## 1. 三方对照

| 项目 | Java 1.7.10（现采用） | MCPE 0.14 反编译 | 旧 Zenith（= MCPE） |
|---|---|---|---|
| min limit 噪声 Y 缩放 | `684.412` | `855.515` | `855.515` |
| min / max limit 除数 | `512` / `512` | `256` / `512` | `256` / `512` |
| 垂直项系数 | `12 × 128 / 256 = 6` | `12` | `12` |
| 垂直采样点 | 33（256 高） | 17（128 高） | 17 |
| 顶部收尾 | `j2 > 29`（y>232） | `y>13`（y>112） | 同 MCPE |
| 地表噪声缩放 | `0.0625` | `0.125` | `0.125` |
| 区块随机数 | `new Random(x*341873128712 + z*132897987541)`（64 位） | `x*0x14609048 + z*0x7EBE2D5` → MT19937 | 同 MCPE |
| populate 播种 | 世界种子取两个**奇数**乘子 `/2*2+1`，一条随机流贯穿全流程 | `>>2` 偏移，且中途多次重播种 | 同 MCPE |
| 洞穴 | `MapGenCaves`：`nextInt(7)!=0` 则一个都不生成；隧道参数见 Java | `LargeCaveFeature`：`nextInt()%15!==0` 跳过 | 同 MCPE |
| 峡谷 | `MapGenRavine`，每候选原点 `nextInt(50)==0`（1/50） | 无独立入口 | 类存在但未调用 |
| 基岩层 | y=0 固定，`y <= nextInt(5)`（1..5 层） | `2 + nextInt(4)`（2..5 层） | 同 MCPE |
| 村庄 | 32 区块网格 / 8 间隔，种子含 `10387312` | 40 网格 / 28 间隔，MCPE 专用常量 | 同 MCPE |
| 世界高度 | 256 | 128 | 128 |

反编译证据（Ghidra）：

- `world/RandomLevelSource.cpp:345-351`：min limit `684.412 / 855.515 / 684.412`，max limit `684.412³`。
- `world/RandomLevelSource.cpp:438-441`：`12.0 / ((depth)*0.9+0.1)`，且没有 `128/256`。
- `world/RandomLevelSource.cpp:442,452-454`：`/256`（`0.00390625`）与 `/512`（`0.001953125`）两种除数。
- `world/RandomLevelSource.cpp:1230`：区块种子 `x*0x14609048 + z*0x7ebe2d5`。
- `world/RandomLevelSource.cpp:1177`（buildSurfaces）：地表噪声缩放 `0x3e000000` = `0.125`。

## 2. 为什么要改

128 格世界下，MCPE 的常量把山地压平了：固定种子、同一群系集合的 256×256 采样中，
hills 的高度标准差只有 **5.81**、最高 101；换成 Java 常量后是 **9.08**、最高 113，
山脊和落差明显更接近 PC 1.7.10 的观感。plains 的标准差也从 2.54 升到 3.12。

随机数、洞穴、峡谷同理：MCPE 用的是自定义的 MT19937 + 另一套雕刻参数，
生成出来的洞穴走向、村庄位置、基岩厚度都和 PC 版对不上。

## 3. 改了什么（A/B/C 三批）

### A. 高度场 + 基岩 + 峡谷

- `level/generator/gorigional/Gorigional.php`
  - `LOWER_LIMIT_SCALE` 256 → `512`，删除 `MIN_LIMIT_HEIGHT_SCALE`（855.515）；
  - min limit 噪声 Y 缩放改用 `HEIGHT_SCALE`（684.412）；
  - 垂直项 `(l1-d0) * 12 * MAX_HEIGHT / 256 / d9`；
  - 顶部收尾阈值改为 Java 的 `l1 > 29`（17 个采样点时不会触发）；
  - 地表噪声 `0.125` → `0.0625`；
  - `generateChunk()` 末尾恢复 `$this->ravines->generateChunk(...)`。
- `biome/BaseBiome.php`、`biome/MesaBiome.php`
  - 基岩改为 `$bedrockTop = $r->nextBoundedInt(5); for($y = 0; $y <= $bedrockTop; $y++)`。
- `structure/MapGenRavine.php`
  - 入口概率 `nextFloat() < 0.02` → `nextBoundedInt(50) === 0`。

### B. 随机数与播种

- `Gorigional::chunkSeed($x, $z)`：`x*341873128712 + z*132897987541`（`Int64` 环绕，**不含世界种子**）。
- `Gorigional::populateSeed($x, $z, $worldSeed)`：Java 的
  `setSeed(worldSeed) → nextLong()/2*2+1 ×2 → x*i1 + z*j1 ^ worldSeed`。
- `generateChunk()` 用 `JavaRandom(JavaRandom::chunkSeed(...))`；`populateChunk()` 用
  `JavaRandom(populateSeed(...))`，并删掉湖泊/结构/装饰之间的多次 `setSeed`——一条流贯穿
  `湖泊 → 村庄 → 废矿 → 要塞 → 遗迹 → 8 次地牢 → decorate`。
- `structure/MapGenBase.php` 改为 Java：`setSeed(worldSeed)` → 两次 `nextLong()` →
  每个候选原点 `Int64::mul($x,$l) ^ Int64::mul($z,$i1) ^ worldSeed`。
- 群系噪声初始化 `McpeRandom` → `JavaRandom`（`Gorigional::init()`）；`SwampBiome` 的固定噪声
  `new McpeRandom(0x5A17)` → `new JavaRandom(0x5A17)`。
- `structure/MapGenVillage.php`：`SPACING 40 → 32`、`SEPARATION 28 → 8`，
  种子改为 `i1*341873128712 + j1*132897987541 + 10387312`（Java `World.func_72843_D`）。

### C. 洞穴

`structure/MapGenCaves.php` 整份重写为 Java 1.7.10 `MapGenCaves`：

- 数量 `nextInt(nextInt(nextInt(15)+1)+1)`，然后 `if(nextInt(7) != 0) 数量 = 0`（约 1/7 保留）；
- 起点 `x*16+nextInt(16)`、`nextInt(nextInt(120)+8)`、`z*16+nextInt(16)`；
- `nextInt(4)==0` 先挖一个房间（`addRoom`），并 `+nextInt(4)` 条分叉；
- 隧道参数 `1.5 + sin(step*π/len)*size`、`(nextFloat()-0.5)*2/8` 俯仰、`nextInt(10)==0` 放大、
  `nextInt(6)==0` 用 0.92 收缩（否则 0.7）、`size>1` 时在 `branchStep` 处一分为二；
- 遇到水/岩浆直接放弃（Java 的 `flag2`）、石头/泥土/草方块才挖、`y<10` 放岩浆、
  挖穿草方块时把下方泥土换成该群系的表层方块；
- 世界高度 128：y 钳到 `1..120`（Java 是 `1..248`）。

## 4. 仍然与 Java 不同的地方（有意保留）

| 项目 | 现状 | 原因 |
|---|---|---|
| 世界高度 | 128 | 区块格式 + 0.14 客户端，y>127 不可达 |
| 要塞选点 | MCPE 环状算法（`MapGenStronghold` 仍用 `McpeRandom`） | Java 版要塞依赖 256 高的环状分布与 3 个固定要塞，移植成本高、收益低 |
| 地牢 | y 用 `nextBoundedInt(128)` | Java 是 `nextInt(256)`，128 高世界会生成在空中 |
| 湖泊 | MCPE 的 1/4 水湖 + 1/8 岩浆湖分支 | 放置偏移与 Java 的 `+8` 不同，但观感接近 |
| 粘土色带 | `MesaBiome` 用 `McpeRandom($seed & 0xFFFFFFFF)` | Zenith 自有实现，不属于 1.7.10 |
| 结构内部布局 | 村庄/废矿/遗迹的部件 RNG 仍是原移植实现 | 位置已按 Java 对齐，部件细节未逐一比对 |

## 5. 升级注意

- 生成器按世界选择（`level.dat` 的 `generatorName`），**已用旧 `gorigional` 生成过的世界，
  新生成的区块会和旧区块有明显高度接缝**，洞穴也会在新旧边界断开。要统一观感请新建世界。
- 村庄间距从 40 改为 32、洞穴算法整体变化，同一 seed 生成的世界与旧版本**不再一致**。
- `level-type` 未改名的旧世界照常加载，不会自动重新生成。

## 6. 验证

```bash
php73 -l src/pocketmine/level/generator/gorigional/Gorigional.php
php73 tests/run-tests.php
```

相关测试：

- `tests/GorigionalConstantsTest.php`：高度场常量（684.412 / 512 / 12 / 128、垂直系数 6.0、17 个采样点）。
- `tests/GorigionalChunkTest.php`：区块种子公式（含负坐标）、populate 种子、真实生成一个区块后
  基岩 1..5 层、y=0 恒为基岩、石头/洞穴空气存在、同种子生成结果逐字节一致。

地形观感与洞穴/峡谷/村庄需上服确认，见 `docs/manual-verification.md` 5.12。
