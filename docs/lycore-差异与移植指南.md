# Zenith 核心 vs protocol/lycore 差异分析与移植指南

> 本文档对比当前核心（Zenith / pocketmine 命名空间）与 `protocol/0.11-0.16.zip` 中的参考核心 **lycore**（`lycore\` 命名空间），说明差异点、成因，以及如何将其中的功能移植到当前核心。

---

## 1. 总体概况

| 维度 | lycore（protocol 参考） | 当前核心（Zenith） |
|---|---|---|
| 版本 | v1.2 | 0.3.0 |
| 代码量（PHP） | 184,583 行 / 1313 文件 | 124,223 行 / 1204 文件 |
| PHP 要求 | **>= 8.0**（源码实际兼容 7.x） | **>= 7.3** |
| 命名空间 | `lycore\` | `pocketmine\` |
| 支持客户端 | 0.11 ~ 0.15.x | 0.11 ~ 0.14.x |
| 独有功能 | 村民交易、Bot、多世界 gamerule、大型结构生成、大量协议兼容 | 新版 entity/ai、反作弊、Web 面板 |

**结论**：当前核心是 lycore 的 **PHP 7.3 精简移植版**——完整继承了协议互操作层，但移除了 0.15 支持、大型结构生成、Bot/交易等高级玩法，同时用新 AI 系统和反作弊做了替换。

---

## 2. 差异总览（按模块）

| 模块 | 差异程度 | 说明 |
|---|---|---|
| `network/protocol/ProtocolCompatibility.php` | 极小（30 行） | 仅 namespace/use 不同，互操作层已移植 |
| `network/protocol/Info.php` | 小 | 协议版本分组不同，缺 `RIDER_JUMP_PACKET` |
| `PocketMine.php` | 小（224 行 diff） | 品牌名、常量、PHP 版本检查、时区检测 |
| `Server.php` | 大（-1606 行） | 少了 ~40 个方法（gamerule/传送门/Bot/配置） |
| `Player.php` | 极大（-3307 行） | lycore 有 296 方法，当前 152；少 ~180 个协议兼容方法 |
| 实体（entity/） | 大 | lycore 多 Horse/Husk/Stray/Bot/trade 等 15 个；AI 体系不同 |
| 世界生成（level/generator/） | 大 | lycore 有村庄/要塞/林地府邸等结构生成 |

---

## 3. 各模块详细差异与移植方法

### 3.1 基础层：命名空间与 PHP 版本

**差异**：全部 `lycore\` → `pocketmine\`；PHP 门槛 8.0 → 7.3；`src/lycore/` 路径 → `src/pocketmine/`。

**原因**：当前核心目标是兼容 PHP 7.3 的旧版 MCPE 服务器。

**注意**：lycore 源码中 **0 处 PHP 8 新语法**（无 `fn/match/readonly`），迁移时基本不会遇到语法问题。但 lycore 用 `Php8FatalRecovery` 自动修复插件语法，这在 PHP 7.3 下无意义，不移植。

**改法**：
- 全局替换 `lycore\` → `pocketmine\`、`\lycore\` → `\pocketmine\`
- 复制时保留 `PocketMine.php` 中的 PHP 7.3 检查逻辑（当前版本）
- `getIncoreApi()` / `getAIHolder()` 等 lycore 特有方法无需保留

---

### 3.2 协议版本支持（Info.php）

**差异**：
- lycore：V011 = `[27,29-33]`，V012 = `[34]`，V014 = `[41-46,60,70]`，V015 = `[81-84]`；共 62 个包 ID
- 当前：V011 = `[21-27]`，V012 = `[28-34]`，V013 = `[37-39]`，V014 = `[41-70]`；共 61 个包 ID

**差异原因**：按 MinecraftWiki 分组完整收录（当前做法）vs 实测抽样分组（lycore 做法）。

**要点**：
- 当前核心**多收**了 0.11 的 `21-26`、0.12 的 `28-33` 等协议
- 当前核心**不支持 0.15**（V015 为空数组）
- 当前核心**缺** `RIDER_JUMP_PACKET = 0xcc` 常量及 `RiderJumpPacket.php`

**改法**（若需支持 0.15）：
- 在 `Info.php` 补 `RIDER_JUMP_PACKET = 0xcc`
- 从 lycore 复制 `network/protocol/RiderJumpPacket.php`
- 若接受更多 0.15 协议，在 `V015_PROTOCOLS` 填入 `[81,82,83,84]` 并在 `ACCEPTED_PROTOCOLS` 追加——需评估 0.15 客户端是否存在且值得支持

---

### 3.3 Server.php（-1606 行，约 72 个方法缺失）

lycore 独有、当前核心删除的方法按功能分组：

#### A. 配置别名系统（约 14 个方法）⭐ 推荐移植
- `getConfigIntList` / `getConfigStringList` / `getConfigValueWithAliases`
- `getConfigIntWithAliases` / `getConfigBooleanWithAliases` / `configValueEquals`
- `parseConfigBoolean` / `parseConfigIntList` / `parseConfigStringList`
- `normalizeVersionDisplay` / `ensureServerPropertiesLayout` / `serverPropertiesNeedLegacyProtocolMigration`
- `ensureVersionDisplayPropertyPosition` / `ensureProtocolPropertiesPosition` / `ensurePortalWorldPropertyPosition`

**是什么**：解析 `server.properties` 时支持**多个别名键 + 命令行参数覆盖 + 类型校验**的统一入口。例如一个配置项可以有新旧两种键名，读取时自动兼容。

**改法**：纯内部逻辑，无外部依赖。整段复制，改命名空间即可。注意将当前核心的 `getProperty()` 调用替换为这些别名方法（`configValueEquals` 等辅助方法一并复制）。

#### B. lycore.yml 高级配置模板（约 12 个方法）⭐ 推荐移植
- `loadAdvancedConfig` / `migrateAdvancedConfigTemplate` / `migrateLegacyAdvancedConfig`
- `mergeAdvancedConfigData` / `writeAdvancedConfigDataPreservingComments`
- `appendMissingAdvancedConfigYamlChildren` / `getAdvancedConfigPathKey`
- `getAdvancedConfigDataPath` / `skipAdvancedConfigYamlChildren`
- `emitAdvancedConfigYamlEntry` / `isAdvancedConfigList` / `formatAdvancedConfigYamlScalar`
- `saveAdvancedConfig` / `getAdvancedProperty`

**是什么**：在 `server.properties` 之外维护一份 `lycore.yml`（当前核心对应 `genisys.yml`），支持**保留注释的 YAML 合并升级**——配置模板更新时把缺失项补进去，不覆盖已有设置。

**改法**：
- 将 `lycore.yml` 改名为当前核心的 `genisys.yml`（当前 `Server.php` 已加载它）
- `getAdvancedProperty` 是 Bot/传送门/世界行为开关的总入口，先移植它
- YAML 处理依赖 `pocketmine\utils\Config`，接口一致可直接用

#### C. 多世界 gamerule（约 20 个方法）⭐⭐ 推荐移植
- `getWorldGamerule` / `setWorldGamerule` / `persistWorldGamerule` / `persistWorldGameruleValue`
- `getWorldGameruleOverride` / `normalizeGameruleValue` / `getSupportedGamerules`
- `isWorldBehaviorDisabled` / `isWorldCreeperBlockDamageDisabled` / `isWorldCropGrowthDisabled`
- `isWorldDaylightCycleDisabled` / `isWorldHungerHealthRegenerationDisabled`
- `isWorldKeepExperienceEnabled` / `isWorldKeepInventoryEnabled`
- `isWorldMobDeathDropsAndExperienceDisabled` / `isWorldNaturalMobSpawnDisabled`
- `isWorldNonLivingEntityDropsDisabled` / `isWorldTntBlockDamageDisabled`

**是什么**：按世界（level）单独控制 gamerule 的开关，实现"多世界自定义 + 高频红石可控开关"（当前核心 TODO 中明确列出的目标）。

**改法**：
- 依赖 `getWorldConfigName` / `normalizeWorldName` / `normalizeWorldNameList`（同组一起搬）
- 写入位置可放在 `genisys.yml` 的世界区块下
- 接入点：在各 `isWorldXxxDisabled` 返回处调用 `getWorldGamerule` 即可

#### D. 传送门世界（约 4 个方法）⚠️ 依赖 Player 协议方法
- `getPortalWorldName` / `isPortalWorld` / `isNetherPortalWorld`
- 配合 Player 侧 `teleportThroughNetherPortal` / `armNetherPortalCooldownAfterTransfer` 等

**是什么**：跨世界下界传送门，传送后冷却 3 秒防止来回闪传。

**改法**：Server 侧方法可搬，但 Player 侧需要 `updateNetherPortalTimer` 等（见 3.5）。**建议暂缓**。

#### E. Bot 系统（约 6 个方法 + 4 个文件）⚠️ 工程量最大
- Server 侧：`isBotEnabled` / `initializeBotStorage`
- 实体侧：`Bot.php`（3170 行 / 181 方法）、`BotTypeManager.php`、`BotCombatBehavior.php`、`BotMovementAI.php`
- 指令：`command/defaults/BotCommand.php`

**是什么**：AI 机器人（PVP 陪练），会战斗/搭路/挖障碍/捡物品/喝药/丢珍珠，配置化类型管理。

**改法**：详见本文档第 4 节"不建议直接移植"。

#### F. 其他独有方法
- `checkDos` / `checkGameSpeed` / `sleepUntil` / `resolveAsyncWorkerCount` / `isPlayerCollideEnabled` / `loadAntiCheatProperties` / `handlePacket`
- `addRecipeToPacket` / `addRecipeToProtocolPacket` / `buildRecipeList` / `getCraftingRecipeSignature` / `getRecipeItemSignature`

**说明**：`addRecipeToProtocolPacket` / `buildRecipeList` 是 lycore 的按协议配方表构建（当前核心已用 `buildRecipeListForProtocol` 实现了同功能，**无需重复移植**）。`loadAntiCheatProperties` 当前核心用 `pocketmine.yml` 配置即可。

---

### 3.4 Player.php（-3307 行，约 180 个协议兼容方法缺失）⚠️ 最大差异

lycore 的 Player.php 有 **296 个方法**，当前核心仅 **152 个**。缺失的主要是**协议兼容方法**（约 180 个），按前缀分组：

#### A. `Protocol011*`（约 70 个方法）⚠️ 不建议直接移植
- 铁砧：`handleProtocol011AnvilInteraction` / `tryProtocol011AnvilRepair` / `resolveProtocol011AnvilEnchantment` / `finishProtocol011AnvilRepair` / `expireProtocol011AnvilSession` ...
- 附魔台：`handleProtocol011EnchantingTableInteraction` / `getProtocol011EnchantingTableCategory` ...
- 合成：`handleProtocol011CraftingContentPacket` / `findProtocol011CraftingRecipe` / `matchProtocol011ShapedCraftingRecipe` / `normalizeProtocol011CraftingSlots` ...
- 自动疾跑：`loadProtocol011AutoSprintState` / `shouldUseProtocol011AutoSprintSpeed` / `toggleProtocol011AutoSprint` ...
- 食物/生命：`sendProtocol011Health` / `consumeProtocol011FoodInHand` ...
- 附魔：`getProtocol011AvailableEnchantments` / `isProtocol011ConflictingEnchantment` / `getProtocol011EnchantmentLevelName` ...
- 玩家伪装/实体交互：`tryInteractWithLookedAtHorse/Pig/Villager` ...

**是什么**：为 0.11 客户端（协议 21-34）补齐核心缺失的协议交互。lycore 把整套逻辑**内联在 Player 类中**，通过 `handleDataPacket` 分支调用。

**改法（关键架构差异）**：
- **lycore 是内联式**：每个协议交互一个方法，在 `handleDataPacket` 里 `if(isProtocol011Player()){ ... }` 分支处理
- **当前核心是集中式**：`dataPacket()` 调用 `DataPacketManager::parsePacket()` 统一转换出站包（Player.php:1184），入站由 `Network::processBatch()` 处理（Player.php:2570）
- **直接搬会冲突**：两套逻辑并存会互相覆盖。若移植，必须把这些方法**重写为 DataPacketManager 的钩子**，工作量接近重写，**不建议现在做**

#### B. `Protocol013*` / `Protocol015*` / `V84*`（约 60 个方法）
- `sanitizeProtocol013Item` / `normalizeProtocol013RecipeInput` / `isProtocol013HiddenItem`
- `resetProtocol015WorldReadyState` / `isMinimalProtocol015BootstrapBatch` ...

**是什么**：0.13（37-39）和 0.15（81-84）协议的特殊包处理。当前核心 0.13 部分处理已在 DataPacketManager 中完成，0.15 不支持。

**改法**：0.13 相关若必须可参考 DataPacketManager 现状按钩子方式补齐；0.15 因当前核心不支持协议，无需移植。

#### C. 通用工具方法（部分 ⭐ 推荐移植）
- `getLookedAtEntity` / `isUndeadEntity` / `isArthropodEntity`（附魔"亡灵杀手/节肢杀手"判定）
- `sendDimensionSpawnStatus` / `shouldDeferV84Packet` / `getAdventureSettingsFlags`
- `getChunkOrderCenter` / `getDeathMessageKillerHealth` / `getDeathMessageMobName`

**改法**：这类方法是纯逻辑辅助，无协议耦合，可挑选需要的直接复制改命名空间。

---

### 3.5 实体差异（entity/）

#### lycore 有、当前核心没有的实体：
| 实体 | 行数 | 说明 | 移植难度 |
|---|---|---|---|
| `Horse` | ~? | 马（可骑乘，对应 TODO 目标） | ⭐ 简单 |
| `Husk` / `Stray` | 小 | 尸壳 / 流髑变种 | ⭐ 简单 |
| `Fireball` / `SmallFireball` | 小 | 烈焰人火球（附 `ShootPlayerBehavior` 联动） | ⭐ 简单 |
| `LeashKnot` | 小 | 拴绳结 | ⭐ 简单 |
| `VillagerTradeFactory` / `VillagerTradeOffer` | 141+263 | 村民交易内容/执行 | ⭐ 简单 |
| `VillagerTradeInventory` | ~500 | 交易窗口 UI | ⭐ 简单（依赖见下） |
| `AgeableSpawnHelper` / `NaturalMobSpawnRules` / `VanillaMobEquipment` | 中 | 刷怪/装备辅助 | ⭐ 简单 |
| `Bot.php` / `BotTypeManager` / `BotCombatBehavior` / `BotMovementAI` | 4150 总 | AI 机器人 | ⚠️ 复杂 |

#### 村民交易移植清单（最推荐的入门移植）：
1. 复制 `entity/trade/VillagerTradeFactory.php` + `VillagerTradeOffer.php`（改 namespace）
2. 复制 `inventory/VillagerTradeInventory.php`（含 `VillagerTradeMenuHolder`）
3. 扩展现有 `entity/Villager.php`：加 `tradeOffers` 属性、`openTradeWindow()`、`ensureTradeData()`、`saveTradeOffersToNBT()` 等
4. Player.php 两处接入：
   - InteractPacket 分支（当前 Player.php:3434 附近）：加 `if($target instanceof Villager){ $target->openTradeWindow($this); }`
   - ContainerSetSlotPacket 分支（当前 Player.php:3970 附近）：加 `if($inv instanceof VillagerTradeInventory){ $inv->handlePlayerClick($this, $packet->slot); }`
5. **简化关窗**：lycore 的 `closeLegacyVillagerTradeWindowLikeSimpleMenu` 是 0.12/0.13 专用，当前核心直接走通用 `removeWindow()` 即可
6. 依赖检查：`CustomInventory` / `InventoryType::CHEST` / `CallbackTask` / `ProtocolCompatibility::isProtocol012/013` 当前核心均已有

---

### 3.6 世界生成差异（level/generator/）

#### lycore 有、当前核心没有的结构生成：
- **村庄**：`normal/object/PnxVillageStructure`、`PnxVillagePools`、`VillageTemplates`、`VillageSmithyChestLoot` + `normal/populator/VillagePopulator` + `biome/VillageBiome`
- **要塞**：`object/Stronghold`、`StrongholdLoot` + `populator/Stronghold`
- **林地府邸**：`object/WoodlandMansion`、`WoodlandMansionLoot` + `populator/WoodlandMansion`
- **劫掠者前哨站**：`object/PillagerOutpost`、`PillagerOutpostLoot` + `populator/PillagerOutpost`
- **废弃传送门**：`object/RuinedPortal`、`RuinedPortalLoot` + `populator/RuinedPortal`
- **沙漠神殿 / 丛林神庙 / 化石 / 矿井**：`object/DesertTempleLoot`、`JungleTemple`、`JungleTempleLoot`、`MineshaftLoot`、`Fossil` + `populator/Fossil`、`DesertStructures`
- **下界堡垒**：`hell/object/NetherFortressPieces`、`NetherFortressLoot` + `hell/populator/NetherFortressPopulator`
- **末地**：`ender/EnderPilar`、`EndBiome`（当前核心有 `ender/Ender.php` 生成器但缺柱/生物群系）
- **空岛**：当前核心有 `VoidGS.php`，lycore 是 `VoidGenerator.php`（功能等价）

**改法**：
- 复制对应 `object/` + `populator/` 目录下文件，改 namespace
- 在 `normal/populator/Populator.php`（或对应生成器）中注册新 populator
- 注意 `PnxVillageStructure` 文件名带 Pnx 前缀，`PnxVillagePools` 是数据池文件，需核对类名引用一致
- 这些是纯生成逻辑，独立性强，**推荐整体移植**（对应 TODO"自然生成结构"目标）

---

## 4. 不建议直接移植的功能

| 功能 | 原因 | 替代方案 |
|---|---|---|
| **Player 的 180+ 内联协议方法** | 架构冲突：lycore 内联 vs 当前集中式 DataPacketManager，直接搬会互相覆盖 | 按需改写为 DataPacketManager 钩子 |
| **Bot 机器人系统**（4150 行） | 依赖 lycore 旧 `entity/behavior`（AIHolder）+ `getPm1eGroundAi/setPm1eFollowTarget` 等底层移动接口，当前核心已是新 `entity/ai/` 架构，冲突大 | 若想要，用新版 Behavior 重写 BotCombat 逻辑 |
| **Php8FatalRecovery 插件修复器** | 专门修 PHP 8 语法，当前核心跑 PHP 7.3 | 无需 |
| **0.15 协议支持** | 当前核心协议分组已定死到 0.14，加 0.15 需重新验证整个互操作层 | 除非明确有 0.15 客户端需求 |

---

## 5. 移植优先级建议

| 优先级 | 功能 | 工程量 | 对应 TODO 项 |
|---|---|---|---|
| ⭐⭐⭐ | 村民交易系统（trade/ + Villager + Player 接入） | 小（半天） | 村民交易：完整的交易互动系统 |
| ⭐⭐⭐ | Server 多世界 gamerule + 配置别名 | 中 | 多世界配置、Gamerule 指令 |
| ⭐⭐ | 世界结构生成（村庄/要塞/府邸等） | 中 | 地狱堡垒与村庄自然生成 |
| ⭐⭐ | 新实体（Horse/Husk/Stray/Fireball） | 小-中 | 猪支持骑乘等生物互动 |
| ⭐ | 通用 Player 工具方法（getLookedAtEntity 等） | 小 | - |
| ⚠️ | Bot 系统 / Player 内联协议方法 | 大 | - |

---

## 6. 移植通用步骤（对每个文件）

1. **复制文件**：从 `protocol/0.11-0.16.zip` 解压出的 `lycore/` 中取目标文件到对应 `src/pocketmine/` 目录
2. **替换命名空间**：`namespace lycore\...` → `namespace pocketmine\...`，所有 `use lycore\` → `use pocketmine\`，`\lycore\` → `\pocketmine\`
3. **核对引用**：用 grep 检查被复制文件 `use` 的每个类在当前核心是否存在（多数存在，少数如 `getPm1eGroundAi` 之类需改写）
4. **去除 lycore 特化**：删除对 `Bot`、`lycore.yml`、`Php8FatalRecovery`、0.15 协议的引用
5. **注册/接入**：实体需在 `Server.php`（`Entity::registerEntity` 调用处）注册；populator 需挂到生成器
6. **验证**：`php -l` 语法检查 + 启动服务器测试对应协议版本客户端

---

*文档生成日期：2026-08-23*
