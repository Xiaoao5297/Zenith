# InCore-II 可借鉴清单

本文记录 `docs/incore-II/`（InCore-II 2.3 源码树，LGPLv3）与 Zenith 0.4.0 的静态比对结果，按「收益 / 工作量 / 风险」分级，供后续移植排期使用。

- 比对对象：`docs/incore-II/pocketmine/`（1568 文件）与 `src/pocketmine/`（1539 文件）
- 比对方式：目录/文件集合差异、逐文件行数差、类方法集合差
- 结论仅基于静态阅读，未在服务器上运行 InCore-II

## 结论速览

| 优先级 | 模块 | InCore-II 规模 | 工作量 | 风险 | 建议 |
|---|---|---|---|---|---|
| A1 | Anti-Xray | 176 行 + 钩子 | 小 | 低 | 先做 |
| A2 | 配置编码修复 | ~200 行 | 小 | 低 | 先做 |
| A3 | 洞穴 + 峡谷生成 | 734 行 | 中 | 中 | 做（限 normal 生成器） |
| A4 | 命令目标选择器 | 872 行 | 中 | 中低 | 做 |
| A5 | 下界传送门 | 588 行 | 中 | 中 | 评估维度支持后做 |
| B1 | 活塞体系 | ~1300 行 | 大 | 高 | 分两期 |
| B2 | 拴绳与马具 | ~500 行 | 中 | 中 | 做 |
| B3 | 自然刷怪规则 | 少量 | 小 | 中 | 做 |
| B4 | 结构补全 | 分散 | 中 | 中 | 择机 |
| B5 | 0.11 玩法修补 | 分散 | 中 | 中 | 仅 v11 需要 |
| B6 | 光照更新 | ~400 行 | 大 | 高 | 暂缓 |
| C1 | Java 版互通 | 3013 行 | 特大 | 高 | 不排期 |
| C2 | 0.8–0.10 协议 | 2300+ 行 | 特大 | 高 | 不排期 |
| C3 | 假人系统 | 分散 | 大 | 中 | 不排期 |
| C4 | PHP 8 插件兼容 | 1034 行 | 中 | 低 | 升级 PHP 8 时再说 |

---

## A. 低风险高收益

### A1 Anti-Xray（防透视）

**源**：`docs/incore-II/pocketmine/level/AntiXrayObfuscator.php`（176 行）

**能力**：直接对已序列化的 `FullChunkData` 负载做字节级混淆，不改变 Level 内部数据结构。

- 两种模式：`MODE_OBFUSCATOR`（`"obfuscator"`，把矿脉位置写成随机「魔法方块」）、`MODE_HIDDEN`（`"hidden"`，把配置内的矿物替换成 `fakeBlock`，默认石头）
- 只处理区块中部的 14×14 列，跳过 y 段上下边界（`sectionY === 0 / 15`），避免破坏基岩层与建筑高度
- 依赖负载布局：`BLOCK_ID_BYTES = 32768`、`BLOCK_DATA_BYTES = 16384`、`MIN_PAYLOAD_BYTES = 49152`
- 公开 API：`obfuscateChunkPayload($payload, array $options)`、`isFilterBlockId()`、`getDefaultOreIds()`、`getDefaultFilterIds()`
- 选项键：`mode`、`scanChunkHeightLimit`（1–15，默认 4）、`fakeBlock`、`ores`、`filters`

**Zenith 落点**

- 新增 `src/pocketmine/level/AntiXrayObfuscator.php`
- 在 `src/pocketmine/level/Level.php:3185 chunkRequestCallback()` 组包后、返回前调用
- 配置新增 `pocketmine.yml` 的 `antixray:` 段（`mode`、`scan-chunk-height-limit`、`fake-block`、`ores`、`filters`）
- 注意：Zenith 没有 `Server::getConfigValueWithAliases()`，直接读 `Config`，不要照搬 InCore-II 的别名逻辑

**风险**

- 必须先确认 Zenith 三种协议族（core 70 / v11 / v84）的区块负载布局一致，`v84` 的序列化入口不同，需单独验证
- 混淆只影响下发给客户端的副本，不影响存档；但如果插件读取 `chunkRequestCallback` 的 `$payload`，会看到混淆后的数据
- 建议加一条「关闭时原样返回」的开关，便于排查区块显示问题

**验证**：单测覆盖 `obfuscateChunkPayload()` 的短负载、非矿方块、边界 y；上服后用 X-Ray 资源包抽查。

### A2 配置编码修复

**源**：`docs/incore-II/pocketmine/utils/Config.php`（880 行，Zenith 503 行）

**能力**：处理 `server.properties` / `pocketmine.yml` 的各种编码异常：

- `stripUtf8Bom()`：去掉 Windows 记事本写入的 BOM
- `repairUtf8MojibakePropertiesContent()`：修复 UTF-8 被按 GBK/ISO-8859-1 读出的乱码
- `decodePropertiesString()` / `convertEncodedPropertiesContent()`：还原 `\uXXXX`、`\n` 等转义
- `encodeUnicodeCodepoint()`、`isValidUtf8()`、`getPropertiesEncodingGuesses()`：编码探测
- `shouldSkipPropertiesAutoSave()`：内容未变时不回写，避免把用户文件重写成另一种编码

**Zenith 落点**：`src/pocketmine/utils/Config.php` 增加上述方法，在 `load()` / `save()` 路径接入。

**风险**：低，纯字符串处理。建议补一个 `tests/ConfigEncodingTest.php`（BOM、GBK 乱码、`\u4e2d`、纯 ASCII 四类输入）。

### A3 洞穴与峡谷生成

**源**：`docs/incore-II/pocketmine/level/generator/populator/Cave.php`（734 行，Zenith 同路径仅 178 行）

**InCore-II 独有方法**：`carveCanyon()`、`carveCanyonChunk()`、`applyCanyonFeature()`、`carveEllipsoid()`、`createTunnel()`、`getBiomeSurfaceReplacement()`、`getDesertCaveReplacement()`

**价值**：Zenith 的洞穴只有基础隧道，没有峡谷（ravine）与生物群系表层替换，地下结构单调。

**Zenith 落点**：`src/pocketmine/level/generator/populator/Cave.php`；`gorigional` 生成器里的 `structure/MapGenRavine.php` 是另一套实现，需决定是统一还是并存。

**风险**：中。世界生成改动一旦有偏差，老存档边缘区块会出现接缝；建议只对 `normal` 生成器启用，并用固定种子对比生成结果。

### A4 命令目标选择器

**源**：`docs/incore-II/pocketmine/command/TargetSelector.php`（872 行）

**能力**：支持 `@a`、`@p`、`@r`、`@e` 及参数 `type`、`name`、`r`/`rm`、`m`、`c`、`l`/`lm`、`x/y/z`、`dx/dy/dz`、`scores`、`tag` 等，返回 `Player[]` / `Entity[]`。

**Zenith 现状**：无选择器，`tp`、`give`、`kill`、`effect` 等命令只接受玩家名。

**落点**：新增 `src/pocketmine/command/TargetSelector.php`，在 `SimpleCommandMap` 解析参数前拦截以 `@` 开头的目标，命令侧改为接收实体列表。

**风险**：中低，但影响面广（所有带目标参数的命令）。建议先只接入 `tp` / `kill` / `effect` 三个命令验证，再推广。

### A5 下界传送门

**源**：`docs/incore-II/pocketmine/utils/NetherPortalHelper.php`（588 行）

**能力**：检测黑曜石框架、点亮/熄灭、搜索目标侧落点、传送冷却、跨世界切换。

**Zenith 现状**：只有 `block/Portal.php`（末地传送门方块）与 `EndPortalFrame.php`，没有下界传送逻辑。

**风险**：中。需要先确认 Zenith 的维度模型——如果只有单世界 `level.yml` 列表，没有「下界」概念，就要先补维度层，成本会翻倍。

---

## B. 中等工作量

### B1 活塞体系

**源**：`block/PistonBase.php`（897）、`Piston.php`（37）、`PistonHead.php`、`StickyPiston.php`、`MovingBlock.php`、`tile/PistonArm.php`（296）、`level/sound/PistonInSound.php`、`PistonOutSound.php`

**依赖**：`Entity::canBePushedByPiston()`、`onPushByPiston()`，以及方块更新的调度顺序。

**风险**：高。涉及红石时序、实体推动、`MovingBlock` 动画包、粘液块连锁。建议分两期：先静态推动（方块瞬移），再补 `MovingBlock` 平滑动画。

### B2 拴绳与马具

**源**：`item/Lead.php`、`Saddle.php`、`NameTag.php`、`CarrotOnAStick.php`、4 种马铠；`entity/LeashKnot.php`（191 行）；`Entity` 的 `getLeashHolder()` / `setLeashedTo()` / `unleash()` / `isLeashed()` / `restoreLeashFromNBT()`

**价值**：补齐骑乘与命名玩法，且是原版常见交互。

**风险**：中。需要实体 NBT 持久化（拴绳关系在重登后不能丢）与客户端包字段；`NameTag` 还要处理铁砧改名。

### B3 自然刷怪规则

**源**：`entity/NaturalMobSpawnRules.php`、`entity/AgeableSpawnHelper.php`

**价值**：按群系/光照/高度限制刷怪，替代 Zenith 目前较粗的刷怪判断。

**风险**：中。要与现有 `Level::generateChunk` 的刷怪点、生物上限协同，改动后需观察 TPS。

### B4 结构补全

**源**（分散在 `Level.php` 与 `level/generator/.../structure/`）：

- `createDeferredVillageMobNBT()`：村庄生物延迟生成（避免区块未就绪）
- `createDeferredPillagerOutpostZombieArmor()`：前哨站僵尸装备
- `Temple.php` 的发射器填充（Zenith 版 66 行 vs InCore-II 288 行）

**风险**：中低，但需要与 `gorigional` 的结构生成器对齐，不能直接复制 `Level` 中的调用点。

### B5 0.11 旧客户端玩法修补

**源**：`Player.php` 中 `applyProtocol011*` / `*Protocol011*` 系列（约 20 个方法），覆盖：

- 铁砧：`applyProtocol011AnvilBook`、`canProtocol011ApplyEnchantment`、`consumeProtocol011AnvilMaterialFromSlot`、`expireProtocol011AnvilSession`
- 附魔台：`canProtocol011UseAsEnchantTarget`、`expireProtocol011EnchantingTableSession`
- 合成：`findProtocol011CraftingRecipe`、`cloneProtocol013RecipeWithId`
- 自动冲刺、手持进食：`applyProtocol011AutoSprint`、`consumeProtocol011FoodInHand`

**价值**：只对 `v11`（0.11–0.13）协议族有意义；Zenith 目前这些交互在旧客户端上不完整。

**风险**：中，逻辑集中在 `Player.php`（已是 8312 行），继续加会恶化可维护性，建议先抽到独立的 `protocol/v11/` 辅助类。

### B6 光照更新

**源**：`level/light/LightUpdate.php`（192）、`SkyLightUpdate.php`

**风险**：高。需要与 `Level` 的光照数组、区块序列化、`executeQueuedLightUpdates()` 全链路对齐；Zenith 目前没有 `level/light/` 目录。收益是光照传播正确性，但回归面极大，暂缓。

---

## C. 大工程（不建议短期排期）

| 模块 | 规模 | 说明 |
|---|---|---|
| Java 版互通 | 3013 行 | `network/java/`：`JavaInterface`(396)、`JavaSession`(643)、`translator/Translator`(604)、`ChunkConverter`(350)、`ItemConverter`、`EntityConverter`、`BlockConverter`；另需 `v14` 包族与 `DataPacketManager`(2463) |
| 0.8–0.10 协议 | 2300+ 行 | `network/protocol/ProtocolCompatibility.php`：`LEGACY_V080_BLOCK_ID_MAP`、`LEGACY_V010_BLOCK_ID_MAP`、`LEGACY_011_BLOCK_ID_MAP`；Zenith 已稳定支持 0.11–0.15，向下兼容收益低 |
| 假人系统 | 分散 | `command/defaults/BotCommand.php`、`Bot.php`、`BotTypeManager.php`、`behavior/BotMovementAI.php`、`BotCombatBehavior.php` |
| PHP 8 插件兼容 | 1034 行 | `plugin/Php8FatalRecovery.php`(537) + `plugin/PluginSourceCompatibility.php`(497)，处理 `$s{0}` 等旧语法；当前运行 PHP 7.3，用不上 |

---

## 不建议移植

| 项 | 原因 |
|---|---|
| Mob AI / 寻路 | InCore-II 用 `Pm1e` 移动模型（`checkPm1eJump`、`getPm1eMoveTarget`、`refreshPm1eWanderTarget` 等），与 Zenith 的 goal 体系（`canStartBehavior`、`tickBehaviors`、`getNavigator`）结构性冲突；Zenith 刚完成 navigator 重构。仅 `Mob::knockBack()` 可单独参考 |
| Synapse 多服 | Zenith 已按 TRIM-001/002/005 删除 `src/synapse/`，不回头 |
| `ProtocolCompatibility.php` 全量 | 与 Zenith 的 `network/compat/` 分层重复，且依赖 `v14` 包族，迁移成本高于收益 |
| `recipes.json` 差异 | 仅压缩格式不同（1 行 vs 13669 行），内容等价 |
| `DataPacketManager.php`（2463 行） | InCore-II 用中心化包管理器；Zenith 是 `network/compat/PacketCodecRegistry` + translator 分层，替换等于重写网络层 |

---

## 许可证与移植规范

- InCore-II 文件头为 **LGPLv3**，作者 `The_Bird, GoodCat456872`，来源 `https://github.com/QwQ12222/InCore-II`。GPLv3 项目可以吸收 LGPLv3 代码。
- 移植必须保留原作者署名与来源，不得改写成 Zenith 自有 GPLv3 头。
- 建议格式（新增/改写文件顶部）：

```php
/*
 * Ported from InCore-II (LGPLv3)
 * Original authors: The_Bird, GoodCat456872
 * Source: https://github.com/QwQ12222/InCore-II
 */
```

- 纯新增文件可以「Zenith 头 + 上述来源注释」并存；整文件复制时保留原头。
- 不要批量改写 `docs/incore-II/` 下的参考树，它是只读比对源。

---

## 建议路线

1. **阶段一（低风险，1–2 天）**：A2 配置编码修复 → A1 Anti-Xray。两者都不碰世界数据，可独立回滚。
2. **阶段二（玩法补全）**：A4 目标选择器 → A3 洞穴峡谷 → B3 自然刷怪。
3. **阶段三（重交互）**：B1 活塞（分两期）→ B2 拴绳马具 → B4 结构补全。
4. **待评估**：A5 下界传送门（取决于是否引入维度）、B5 0.11 玩法修补（取决于是否继续支持旧客户端）。
5. **不排期**：B6 光照、C1–C4。

每项移植都应附带 `tests/` 下的离线用例；无法离线验证的（区块显示、客户端交互）写入 `docs/manual-verification.md`。

---

## 附录 A：只在 InCore-II 的目录（文件数）

| 目录 | 独有文件数 | 说明 |
|---|---|---|
| `pocketmine/network` | 163 | `java/`、`protocol/v14/`、`DataPacketManager` 等 |
| `pocketmine/entity` | 22 | Bot、刷怪规则、额外生物 |
| `pocketmine/level` | 12 | Anti-Xray、光照更新、结构辅助 |
| `pocketmine/item` | 11 | 拴绳/马具/命名牌等 |
| `pocketmine/resources` | 7 | 配置与语言补充 |
| `pocketmine/block` | 6 | 活塞系列、绊线钩 |
| `pocketmine/command` | 6 | TargetSelector、BotCommand |
| `synapse/*` | 31 | 多服系统（不移植） |
| `spl/stubs` | 4 | |
| `pocketmine/tile` | 3 | 活塞臂等 |
| `pocketmine/utils` | 2 | NetherPortalHelper、编码工具 |
| `pocketmine/plugin` | 2 | PHP 8 兼容 |

## 附录 B：只在 Zenith 的目录（文件数）

| 目录 | 独有文件数 | 说明 |
|---|---|---|
| `level` | 140 | `gorigional` 世界生成器 |
| `lang` | 21 | 多语言 |
| `resources` | 19 | 反作弊等配置 |
| `anticheat` | 18 | Zenith 独立反作弊体系 |
| `entity` | 16 | goal 行为树 |
| `network` | 14 | `compat/` 编解码层 |

## 附录 C：行数差最大的共同文件

| 差值 | InCore-II | Zenith | 文件 |
|---|---|---|---|
| 13668 | 1 | 13669 | `resources/recipes.json`（压缩格式差异） |
| 2811 | 8312 | 5501 | `Player.php` |
| 2367 | 2463 | 96 | `network/DataPacketManager.php` |
| 1979 | 2323 | 344 | `network/protocol/ProtocolCompatibility.php` |
| 1417 | 5174 | 3757 | `Level.php` |
| 1310 | 5256 | 3946 | `Server.php` |
| 556 | 734 | 178 | `level/generator/populator/Cave.php` |
| 461 | 532 | 71 | `entity/Pig.php` |
| 444 | 903 | 459 | `entity/MinecartHopper.php` |
| 431 | 2335 | 1904 | `entity/Entity.php` |
| 389 | 686 | 297 | `entity/Mob.php` |
| 385 | 438 | 53 | `entity/Wolf.php` |
| 377 | 880 | 503 | `utils/Config.php` |
| 370 | 520 | 150 | `entity/FishingHook.php` |
| 340 | 399 | 59 | `entity/Creature.php` |
| 304 | 2085 | 1781 | `inventory/CraftingManager.php` |
| 277 | 739 | 462 | `entity/Horse.php` |
| 259 | 335 | 76 | `entity/Enderman.php` |
| 232 | 276 | 44 | `block/TripwireHook.php` |
| 220 | 288 | 66 | `level/generator/normal/populator/Temple.php` |
| 220 | 355 | 135 | `entity/Boat.php` |
| 160 | 207 | 47 | `entity/Ghast.php` |
| 157 | 109 | 266 | `block/Redstone.php` |
