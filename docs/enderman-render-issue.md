# 末影人“黑色长刺”渲染问题

> 记录时间：2026-11-21
> 分支：`feat/incore-port`
> 相关截图：`image1.jpg` – `image3.jpg`（玩家提供，未入库）

## 1. 现象

玩家第一人称视角下，末影人（Enderman）附近出现大量**黑色细长三角面**，所有三角形的尖端都精确汇聚在屏幕准心；远处同时能看到一只外形正常的末影人。近看时还会出现一整块横向躺倒、带紫色“眼睛”的黑色几何体。

不是贴图错误，也不是区块 mesh 错误：画面里出现的是**实体模型**，只是它离相机太近。

## 2. 结论

两个独立问题叠加：

1. **相机进入了末影人模型内部**。客户端末影人模型尺寸是 `0.6 × 2.9`（`entity/mobs/EnderMan.cpp` 里的 `Entity::setSize(this, 0.6, 2.9)`），而服务端的实体碰撞**完全不包含实体**（`Entity::move()` 全部调用 `getCollisionCubes(..., false)`，`Player::canCollideWith()` 返回 `false`），所以玩家可以直接走进怪物身体。第一人称相机高度 1.62 落在模型内部后，模型被近裁剪面切开，就表现为从屏幕边缘汇聚到准心的黑色三角——尖端汇聚点就是投影主点。
2. **同一个实体被重复 `AddEntity`**。服务端每个实体子类（Zombie/Skeleton/Cow/Enderman/…）的 `spawnTo()` 都无条件构造并发送 `AddEntityPacket`，再由 `parent::spawnTo()` 只标记 `hasSpawned`。而 `Player::sendChunk()`（每次重发区块）与玩家重生循环都会再次调用 `spawnTo()`，客户端因此可能保留一个位置/朝向未更新的“孤儿模型”，进一步放大黑色几何。

同时修掉一个真实的协议 bug（见第 5 节）：64 位长整型元数据的线上类型号写错，导致其后的元数据整体错位。

## 3. 已排除的可能

| 猜测 | 结论 |
|---|---|
| 元数据索引冲突（LEAD 23/24 覆盖末影人数据） | 排除。客户端 `SynchedEntityData::assignValues()` 只更新实体已 define 的索引，末影人只定义 `0x10/0x11/0x12`，23/24 被直接忽略 |
| 末影人手持方块渲染成黑色 mesh | 排除。客户端只在 `getCarryingBlock().id != 0` 时渲染方块，服务端全仓库没有调用 `setBlockInHand()` |
| `isCreepy`（颤抖）导致位置抖动 | 排除。0.14 里 `EnderMan::hurt()` 只在服务端设置 creepy，我们的 `Enderman.php` 没有覆写 `hurt()`，该值恒为 0 |
| `MoveEntityPacket` 字段顺序错误（yaw/headYaw/pitch 错位导致生物躺倒） | 排除。反编译确认内部 `rotation[0]=pitch`、`rotation[1]=yaw`，线上顺序就是 `x, y, z, yaw, headYaw, pitch`，与服务端编码一致 |
| `y + getEyeHeight()` 把生物抬高 | 排除。`Entity::$eyeHeight` 对普通生物为 `null`，`y + null = y`；只有玩家设置 1.62 |
| 模型动画把四肢拉长 | 排除。`EnderManModel::setupAnim()` 把四肢旋转减半并 clamp 到 ±0.4，不会产生巨大几何 |

## 4. 修复

### 4.1 生物不再与玩家重叠（主修复）

`src/pocketmine/entity/Mob.php`：

- 新增 `const PLAYER_CLEARANCE = 0.4`，与玩家的最小中心距为
  `(生物宽 + 玩家宽) / 2 + 0.4`（末影人即 `0.85`，大于客户端模型宽度 `0.6`）。
- `onUpdate()` 在 `parent::onUpdate()` 之后调用 `pushAwayFromPlayers()`。
- `pushAwayFromPlayers()`：遍历同世界玩家，先按 3 格与垂直区间快速筛，再用
  `separationPush()` 计算推开方向与距离，最后调用 `$this->move(...)` 推开——
  **走正常碰撞流程**，被墙挡住时不会把生物塞进方块里。
- 推开后消去生物朝玩家内部的速度分量，避免下一 tick 又贴回去。
- `separationPush()` 是纯函数，中心完全重合时返回固定方向，不会出现除零/NaN。

### 4.2 防止重复 `AddEntityPacket`

- `src/pocketmine/entity/Entity.php` 新增 `trySpawnTo(Player $player)`：
  只有该玩家还没生成过这个实体时才调用 `spawnTo()`。
- `src/pocketmine/Player.php` 的 `sendChunk()` 与重生循环两处改用 `trySpawnTo()`。
- 实体子类自己的 `spawnTo()` 保持不变（直接调用仍可用）。

### 4.3 出站实体状态 NaN/INF 防护

`src/pocketmine/level/Level.php`：

- `addEntityMovement()` / `addEntityMotion()` 发送前检查坐标、旋转、motion 是否全部有限；
  非有限值记一条 debug 日志并丢弃该条更新。
- `headYaw === null` 时先替换为 `yaw` 再校验。

这是防御性修复：一旦将来某个 AI/物理路径算出 NaN，也不会污染客户端矩阵。

### 4.4 元数据 LONG 类型号（协议 bug）

`Binary::writeMetadata()` 原先直接用内部类型号 `Entity::DATA_TYPE_LONG = 8` 写
3 bit 的 type 字段：`8 << 5 = 256`，被 `chr()` 截成 `0`，客户端会按 **BYTE** 解析，
于是这条 long 之后的所有元数据全部错位（拴绳、箭的 shooter id 等都会丢）。

- 新增 `Binary::METADATA_TYPE_LONG_WIRE = 7`（线上编号）。
- `writeMetadata()` 把内部 `8` 与线上 `7` 统一按 `7` 编码，写满 8 字节。
- `readMetadata()` 读到线上 `7` 时转回内部 `8`，并按 8 字节读取（原来只读 4 字节）。
- `network/compat/translators/V015Translator.php` 的跳过逻辑同步改为按 `7` 匹配。

## 5. 验证

离线：

```bash
php73 tests/run-tests.php     # 823/823 passed
```

新增/更新：

- `tests/MobSeparationTest.php`（19 项）：推开方向、最小距离、零距离不产生 NaN、
  推送走碰撞接口、`PLAYER_CLEARANCE` 覆盖客户端模型宽度、`trySpawnTo` 接入点数量。
- `tests/MetadataEncodingTest.php`（8 项）：LONG 头为 `0xF7`、8 字节 payload、
  内部 8 与线上 7 编码一致、LONG 之后的下一个属性仍能解析、默认 23/24 都能读回。

上服（另见 `docs/manual-verification.md` 5.15）：

1. 走到末影人身体里：不应再出现汇聚到准心的黑色三角，生物会被缓慢推开。
2. 让末影人贴脸站立（它自己走过来）：相机不应进入模型。
3. 远处观察末影人：模型高度/比例应正常，不会躺倒或拉伸。
4. 反复走远再靠近、死亡重生，不应出现“多出来的黑色残影”。

## 6. 已知限制

- 客户端模型尺寸 `0.6 × 2.9` 是硬编码的，服务端 `Enderman.php` 的 `width/height` 是碰撞尺寸，
  两者不必相等；本修复只保证相机不会进入模型，不改变碰撞箱。
- 推开速度受 `Entity::move()` 与方块碰撞限制，生物被卡在墙角时推开会变慢（但不会卡进方块）。
- 该问题不是末影人独有：所有体型大于碰撞箱的生物都可能出现，本次修复对所有 `Mob` 生效。
