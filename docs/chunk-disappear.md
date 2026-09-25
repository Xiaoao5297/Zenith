# 区块随机消失（吞区块）问题分析与修复

> 分支：`chunk-disappear`
> 基线：`chunk-fix`（已包含 PopulationTask 异步覆盖、toBinary 写方块等修复）

## 现象

- 玩家建筑所在区块**随机、永久消失**，变成空洞或原始地形，重进/重启都不恢复。
- 控制台平时看不到明显报错，发生时间不可预测。
- 所有世界都出现过（world / nether / skyworld / zc / zy / shop）。
- 玩家报障时间点高度集中在崩溃/重启之后：
  - `2026-08-30 12:02` 日志出现 `Corrupted chunk detected`，`13:21~14:24` 玩家大面积报告区块消失。
  - `2026-09-05 09:59、10:00` 连续两次崩溃，`11:21` 报告"区块没了一堆"。

## 根因

### 1. 崩溃：属性表被置空后仍被访问（use-after-close）

崩溃调用链：

```
Player::sendNextChunk() -> Player::doFirstSpawn()
  -> Player::setFood() -> $this->getAttributeMap()->getAttribute(HUNGER)->setValue()
  -> 致命错误: Call to a member function getAttribute() on null
```

- `Entity::close()` 会把 `$this->attributeMap` 置为 `null`（`Entity.php`）。
- `sendNextChunk()` 开头只判断 `$this->connected === false`，没有判断 `$this->closed`。
- 玩家在登录/加载区块窗口内掉线、重连或重复登录时，`PlayerJoinEvent` 期间也可能调用效果/饥饿逻辑，
  于是对已关闭实体的空属性表取值，抛出致命错误，进程崩溃。
- `crashdumps/` 中 34 个崩溃均为该错误（8/16–9/5）。另有一段历史是 `Float` 类名的 NBT 兼容崩溃。

### 2. 崩溃后果：region 半写损坏 + 损坏区块被静默重建

- 崩溃走的是 `forceShutdown()` + `@kill(getmypid()); exit(1)`，不是正常保存流程。
- `.mcr` 是**原地写入**（先写数据、后写位置索引），崩溃半写会导致区块损坏。
- `RegionLoader::readChunk()` 校验失败时返回 `null`，但 `chunkExists()` 只查看位置表仍判定"已生成"。
- `McRegion::loadChunk()` 在 `$create=true` 时把损坏区块当作"未生成"，用空区块替代并重新生成，
  随后写回存档，**覆盖掉玩家的真实建筑** —— 这就是"区块永久消失/变原始地形"的直接原因。

## 修复内容

1. **属性访问空值保护**
   - `Player::setFood()`：`getAttributeMap()` 为 null 时安全跳过。
   - `Human::getFood()/setFood()/getMaxFood()/addFood()`：属性表或饥饿属性为 null 时安全返回。
   - `Effect::add()`（SPEED/SLOWNESS）：移动速度属性为 null 时安全返回。
2. **堵住 use-after-close**
   - `Player::doFirstSpawn()`：`$this->closed` 或 `!isOnline()` 时直接返回。
   - `Player::sendNextChunk()`：增加 `$this->closed` 判断。
3. **损坏区块不再静默丢弃**
   - `McRegion::loadChunk()`：重建损坏区块前，先把整个 region 文件复制到
     `corrupt-quarantine/`，并输出明确 ERROR 日志（含区块坐标），便于事后抢救。
4. **region 写入健壮性**
   - `RegionLoader::saveChunk()`：写完数据后 `fflush` 再写位置索引。
   - `RegionLoader::__destruct()`/`close()`：增加 `closed` 标志，避免旧实例回写覆盖。
   - `RegionLoader` 构造函数：只有文件确实为空时才 `createBlank()`，不再可能误清空非空 region。
   - `Level::saveChunks()`：检查 `saveChunk()` 返回值，失败时保留 `changed` 标志并告警。
   - `McRegion::unloadChunks()`：关闭前补存仍 `hasChanged` 的区块。

> 说明：`chunk-fix` 已修复 PopulationTask 异步写回覆盖主线程区块的问题；本分支补充的是
> "崩溃导致 region 损坏 → 损坏区块被重建覆盖" 这一条剩余路径。

## 复现与验证

- 测试服 `/home/xiaoao/sxsx/PocketMine-test`（端口 19133）运行本分支核心：
  - 启动 0 CRITICAL / 0 损坏。
  - 玩家正常进出游戏、传送、登录（触发原崩溃路径）均不再崩溃。
- 原子写压力测试（另见 other-issues 分支）证明崩溃/被 kill 时不再产生半截文件。

## 建议

- 正式服建议额外关闭 `genisys.yml` 的 `async-chunk-request`，降低竞态。
- 定期用只读脚本扫描 `worlds/*/region/*.mcr`，发现 `corrupt-quarantine/` 目录即说明又有损坏事件。
