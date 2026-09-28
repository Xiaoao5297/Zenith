# 代码审查报告 — v0.3.1-beta

- 审查对象：commit `8e038c7`（`git diff 68d59ee..8e038c7`，88 文件，约 +19049 / -149）
- 审查日期：2026-09-25
- 审查范围：安全加固 / 原子写 / 内置备份 / config 迁移 / 区块丢失修复 / 自然结构生成 / 多世界 gamerule / 村民交易 / NBT 限深 / 网络批处理 / Arrow 补齐

## 结论摘要

| 严重度 | 数量 |
| ------ | ---- |
| Critical | 0 |
| High | 2 |
| Medium | 10 |
| Low | 8 |
| **合计** | **20** |

无 Critical 级问题。最需要优先处理的是 NBT 深度限制被绕过（DoS 面）与静态 `$readDepth` 的并发/重入污染。

---

## High

### H1. NBT 深度限制可被 ListTag 嵌套绕过
- 文件：`src/pocketmine/nbt/NBT.php:511`（配合 `ListTag.php:169,174`）
- 描述：`MAX_READ_DEPTH` 只在 `NBT::readTag()` 里递增，但 `ListTag::read()` 对嵌套的 ListTag/CompoundTag 是直接调用 `$tag->read($nbt)` 递归，未经过 `readTag()`，导致 list 套 list 的嵌套层级从不计数，深度上限被完全绕过。
- 影响：恶意/损坏的 NBT 可构造极深嵌套，触发栈溢出或 CPU/内存 DoS。
- 建议：让元素读取统一走 `readTag()`，或在 `ListTag::read()` 内加同样的深度计数与上限判断。

### H2. `$readDepth` 由实例变量改为 static，跨实例污染
- 文件：`src/pocketmine/nbt/NBT.php:74`
- 描述：`$readDepth` 从实例属性改成 `static`，被所有 NBT 实例共享（含 `Item::$cachedParser`）。虽然 `read()` 会重置，但两处解析器重入/并发使用时计数会互相污染。
- 影响：深度限制可能被错误重置或错误触发，破坏限深功能。
- 建议：保持每实例计数（或按引用传递），不要用全局 static。

---

## Medium

### M1. `/backup list/clean` 的 tier 参数可路径穿越
- 文件：`src/pocketmine/command/defaults/BackupCommand.php:53-58`；`src/pocketmine/utils/BackupManager.php:301,355,372`
- 描述：`/backup list <tier>` 与 `/backup clean <tier>` 把未经校验的 `$tier` 直接拼进 `backupRoot . $tier`，`../` 可任意列举/删除任意 `YYYYMMDD-HHMMSS` 命名的目录。
- 影响：任意目录枚举与删除（限已拥有权限的操作者，但可越界到备份目录之外）。
- 建议：用 `runBackup()` 中同样的 `[A-Za-z0-9_-]` 正则校验 tier，并拒绝不在 `getTiers()` 内的值。

### M2. 备份目录/文件权限 0777，快照内容可被本地用户读取
- 文件：`src/pocketmine/scheduler/BackupTask.php:32,54`；`src/pocketmine/utils/BackupManager.php:251,261`
- 描述：快照目录与文件以 `0777`（受 umask 后约为 0755）创建，玩家数据 / config / `server.properties`（含 `rcon.password`、数据库凭据）可被同机任意用户读取。
- 影响：敏感配置泄露。
- 建议：快照目录与根目录用 `0700` 创建并 `chmod`，不要用 `0777`。

### M3. `saveAdvancedConfig()` 硬编码写回旧 genisys.yml 路径
- 文件：`src/pocketmine/Server.php:3608`
- 描述：写入固定 `$this->dataPath . "genisys.yml"`，而 `advancedConfig` 是从 `getConfigFile("genisys.yml")`（迁移后为 `config/genisys.yml`）加载的，导致 gamerule 持久化写到旧根文件（或回退 `save(false)` 丢失注释），配置改动丢失。
- 影响：多世界 gamerule 修改无法正确持久化。
- 建议：改为 `$this->getConfigFile("genisys.yml")`。

### M4. 备份去重按 size+mtime（秒级）判定，误判导致快照损坏
- 文件：`src/pocketmine/scheduler/BackupTask.php:80-82`
- 描述：size 相等且 mtime（1 秒分辨率）相等即视为同一文件并 hardlink 去重；两个恰好同 size 同秒修改的不同文件会被错误硬链接，快照内容被污染。
- 影响：备份不可信。
- 建议：去重前先 hash 内容（或比较 inode+ctime）再 `link()`。

### M5. `copyTree()` 跟随符号链接，可被 symlink 攻击
- 文件：`src/pocketmine/scheduler/BackupTask.php:52,88`
- 描述：用 `is_dir()`/`copy()` 递归，会跟随软链；在 `worlds/plugins/resource_packs` 内植入 symlink 可使备份复制任意文件/目录到全局可读的备份目录。
- 影响：任意文件读取（经备份目录）。
- 建议：用 `is_link()` 拒绝软链，递归时关闭 follow 标志。

### M6. 人口任务 `hasChanged` 竞态导致区块被静默丢弃
- 文件：`src/pocketmine/level/generator/PopulationTask.php:202`（配合 `Level.php:1134`）
- 描述：竞态守卫用 `hasChanged()` 与快照布尔比较，但周期 `Level::saveChunks()` 会 `setChanged(false)`，人口期间被保存的区块 true→false，其人口结果被静默丢弃（区块未人口化）。
- 影响：区块可能永久缺少人口生成。
- 建议：改用单调递增的每区块 revision（编辑时自增，保存不重置），而非 `hasChanged` 标志。

### M7. 损坏区块隔离用 `@copy` 抑制错误，失败即丢失唯一备份
- 文件：`src/pocketmine/level/format/mcregion/McRegion.php:222`
- 描述：损坏区块加载时 `@copy()` 到隔离区并抑制错误，随后直接清空并重新生成覆盖线上数据；`@mkdir`/`@copy` 失败时唯一备份已丢。
- 影响：损坏区块可能永久消失（与本次要修的目标相反）。
- 建议：检查复制是否成功，成功隔离前不得覆盖/重建，失败时记录硬错误。

### M8. 人口失败后 `registerGenerator()` 重调度到所有 worker
- 文件：`src/pocketmine/level/generator/PopulationTask.php:169`
- 描述：失败时 `onCompletion()` 调 `registerGenerator()`，向每个 worker 重调度 `GeneratorRegisterTask` 并重置各自生成器/管理器，可能冲掉同 world 其他区块正在生成/人口化的进行中状态。
- 影响：并发生成状态被破坏。
- 建议：仅重置受影响 worker 的生成器状态（或等该 world 无任务时再延迟重注册）。

### M9. 村民交易 `fromNBT()` 信任 NBT 值
- 文件：`src/pocketmine/entity/trade/VillagerTradeOffer.php:238`
- 描述：直接信任 NBT 提供的 id/damage/count/maxUses/uses，无校验；从区块数据加载的村民可暴露任意（超大/负数/稀有）交易，实现免费或复制物品。
- 影响：物品刷取 / 经济破坏。
- 建议：加载时校验 id 在 `Item::$list` 内、count 夹紧到最大堆叠、`uses <= maxUses`。

### M10. 卖出物品空间判断与实际落物范围不一致
- 文件：`src/pocketmine/entity/trade/VillagerTradeOffer.php:163`（配合 `:114`）
- 描述：`canAddSellAfterRemovingCosts()` 按全部 `getSize()`（36）格判断空间，而 `execute()->addItem()` 只填 `getSize()-getHotbarSize()`（27）格，且忽略 `addItem` 返回值；买 A/B 被扣后卖出物品被静默丢弃。
- 影响：玩家交易时物品凭空消失。
- 建议：空间判断与 `addItem()` 的槽位范围（size - hotbarSize）保持一致，并处理 `addItem` 剩余。

---

## Low

### L1. `prune()` 负 keep 值导致死循环
- 文件：`src/pocketmine/utils/BackupManager.php:353`
- 描述：`while(count($snaps) > $keep)`，若 `keep` 为负则对空数组 `array_shift`，主线程死循环。
- 建议：`keep = max(0, $keep)`。

### L2. 备份失败时误删上一份正常快照
- 文件：`src/pocketmine/utils/BackupManager.php:291-294`
- 描述：失败时 `onTaskComplete()` 删 `$snaps[count-1]`；若 `BackupTask` 的 `mkdir($dest)` 未成功（未新建快照目录），会误删上一份正常快照。
- 建议：仅当该 `$dest` 目录存在且晚于上一份快照时才删除。

### L3. `tick()` 用未校验的配置 tier 名拼状态文件路径
- 文件：`src/pocketmine/utils/BackupManager.php:216`
- 描述：`tick()` 用配置里原始的 tier 名构造 `stateDir . $name . ".last"` 并 `listSnapshots($name)`，构造的 `backup.yml` 可触发状态文件写入路径穿越。
- 建议：对配置键应用同一 tier 校验。

### L4. 剩余空间阈值整数溢出
- 文件：`src/pocketmine/utils/BackupManager.php:254`
- 描述：`minFreeMb * 1024 * 1024` 按 int 计算，32 位 PHP 上大值溢出，静默禁用空间保护，可致磁盘写满 DoS。
- 建议：用 float 计算阈值（或夹紧 `minFreeMb`）。

### L5. VillageBiome WaterPit 数量笔误
- 文件：`src/pocketmine/level/generator/normal/biome/VillageBiome.php:49`
- 描述：`WaterPit` 生成器 `baseAmount = 9999`（笔误），村庄生物群系区块人口化时会触发约 1 万次水坑生成尝试（60 万+ 方块读取）。
- 建议：改为合理值（如 1）或移除 WaterPit。

### L6. `StructureLoot` 全零权重时取模零
- 文件：`src/pocketmine/level/generator/object/StructureLoot.php:96`
- 描述：`chooseEntry()` 调 `nextBoundedInt($totalWeight)`，若某战利品池权重全为零会变成 `nextInt()%0`，除零致命错误。
- 建议：采样前守卫 `$totalWeight <= 0` 返回回退条目。

### L7. `Stronghold::chunkHash()` 负坐标溢出，破坏种子一致性
- 文件：`src/pocketmine/level/generator/normal/object/Stronghold.php:123`
- 描述：`(($x & 0xffffffff) << 32)` 对负坐标溢出为负 int，且与 `Level::chunkHash()` 的 `|` 混用，破坏与其它结构生成器在原点附近的确定性种子一致性。
- 建议：复用 `Level::chunkHash()`，或两半统一掩码到 32 位。

### L8. `processBatch` 边界检查为冗余重复
- 文件：`src/pocketmine/network/Network.php:252`
- 描述：新增的 `($offset + $pkLen) > $len` 与既有 `$pkLen > ($len - $offset)` 完全重复，该修复为 no-op（死代码）。
- 建议：删除重复，或补上读取 4 字节长度头之前缺少的 `substr` 长度守卫。

---

## 其它说明（非缺陷）

- `/backup` 权限 `pocketmine.command.backup` 已在命令上检查 `testPermission()`，但**未在 `DefaultPermissions.php` 注册**，实际按 OP（DEFAULT_OP）生效——当前是"限制"而非漏洞，但未注册的权限字符串是潜在隐患。
- `Config::SERIALIZED` 改用 `unserialize(..., ["allowed_classes" => false])`、`Utils::getURL/postURL` 开启 `CURLOPT_SSL_VERIFYPEER` 均为正确修复。
- `RakLibInterface::blockAddress` 的转义 + `filter_var` 为真实改进，未发现注入残留。
- `Arrow.php` 补齐为平凡且安全。
- 结构生成（Nether 要塞/林地府邸/村庄）均有边界（`MAX_PIECES`/`MAX_DEPTH=7`/网格遍历）且 `WoodlandMansionSimpleGrid` 有越界守卫，未见无限递归。
- `/gamerule` 使用 OP 权限与按世界配置键，未发现跨世界泄露或鉴权绕过。

---

## 总体结论

本提交整体方向正确：安全加固、原子写、内置备份、区块修复与新增玩法基本可用，且无 Critical 级漏洞。需在下次迭代优先处理 **H1/H2（NBT 限深绕过与 static 污染）**，以及备份模块的一组 Medium 问题（M1–M5 路径穿越 / 0777 权限 / 符号链接 / 去重误判 / 硬编码写回路径），并修复村民交易的两处物品经济问题（M9/M10）。备份模块因为是"安全加固"自身引入的新代码，建议上线前完成 M1–M5、L1–L4 的收敛。
