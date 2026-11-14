# InCore-II 移植代码审查与修复记录

审查范围：`main@ac6b539..feat/incore-port`，逐文件与 `docs/incore-II/pocketmine/<路径>` 对比。
结论：无 P0。P1 三项已全部修复，P2 中影响功能完整性的项已修，剩余项记录在末尾。

## 已修复

### P1-1 Config 把合法中文误判为乱码并写盘
- 位置：`src/pocketmine/utils/Config.php`（`repairUtf8MojibakePropertiesContent`）
- 现象：合法 UTF-8 中文按 GB18030 重编码后若仍是合法 UTF-8 就被当成乱码替换，例如 `一 → һ`、`专 → ר`、`业 → ҵ`，且 `$forceAutoSaveOnLoad` 会立即写盘。
- 修复：新增两道护栏——结果含西里尔/希伯来/阿拉伯等区段时拒绝；原文含 CJK 而结果不含 CJK 时拒绝。
- 测试：`tests/ConfigEncodingTest.php` 新增 `testValidCjkNotMojibake()`，覆盖 一/专/业/你好世界/服务器（值 + 磁盘内容均不变）。

### P1-2 LeashKnot 落盘导致区块重载时绳结无限增长
- 位置：`Entity::canBeSaved()`（新增，默认 true）、`LeashKnot::canBeSaved()`（false）、`anvil/mcregion/leveldb` 的 `Chunk` 保存循环。
- 原因：`LeashKnot` 已注册且 `getSaveId()` 可用，会随区块落盘；而动物先于绳结恢复时 `getOrCreate()` 又新建一个，每次重载 +1。
- 修复：绳结不落盘，改由被拴实体的 `Leash` NBT 在载入时按栅栏坐标重建（`LeashKnot::getOrCreate()`），与文件头注释描述的设计一致。

### P1-3 0.15 客户端收不到 DATA_LEAD_HOLDER
- 位置：`src/pocketmine/network/compat/mappings/MetadataMapping.php`
- 原因：内部 `DATA_TYPE_LONG = 8`，协议 LONG = 7；`isValidMetadataProperty()` 上限是 7、`normalizeMetadataProperty()` 要求类型相等，索引 23 被整条丢弃。
- 修复：校验上限放宽到 8；期望 LONG 且实际为 8 时改写为协议类型 7。
- 测试：新增 `tests/MetadataLeadTest.php`（5 用例），并确认 0.14 仍移除拴绳元数据。

### P2 功能完整性
- 女巫未注册：`Server::registerEntities()` 补 `Witch`，避免生成池空 roll、府邸 evoker 标记丢失。
- 粘液块：补 `SlimeBlock::canStickBlocks()`，活塞粘液分支不再全是死代码。
- 拴绳复制：`Lead::onActivate()` 挂栅栏成功后消耗一根（创造模式除外）；未拴上任何实体时关闭刚建的绳结；`Player` 的 INTERACT 分支限定 `Mob`。
- 传送门：目标世界不存在时不再清零 `portalTime`，避免玩家反复重站。
- 延迟结构生物：关闭刷怪时也消费标记（extraData 不落盘）；村民不再随机变成幼年。

## 已知未修（后续排期）

| 项 | 现象 | 建议 |
|---|---|---|
| 洞穴生成耗时 | 单区块 73–185 ms，跑在异步生成任务里 | 按源区块缓存结果，或区域批量生成 |
| 神庙跨区块 | 足迹可能触及 chunk+2，被 3×3 人口窗口裁掉约 1/5 | 收紧 `CANDIDATE_SPAN` 或扩到 5×5 窗口 |
| `LeashKnot` 全实体扫描 | `close()`/`getDrops()` 遍历 `getEntities()`，大批量时 O(n²) | 反向索引 |
| 天空光更新 | 改一个方块扫整列并逐个入队 | 按列去重 |
| `PistonArm` 重载 | 动画中卸载可能停在 EXTENDING | `readNBT()` 时补 `scheduleUpdate()` |
| `pocketmine\tile\MovingBlock` | 该类未移植，相关 `instanceof` 恒为 false | 实现移动方块 tile 或删除残留分支 |
| 传送门双开关 | `nether.enabled` 只控点火，传送还需 `nether.allow-nether` | 统一为 OR 并写进文档 |
| 活塞 `isSolid()` | 水/沙穿过活塞、活塞上不能站实体（上游同） | 上服实测后再定 |
