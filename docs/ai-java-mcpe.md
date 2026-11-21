# 生物 AI：向 Java 1.7.10 与 MCPE 0.14 借鉴什么

> 生成时间：2026-11-21
> 分支：`feat/incore-port`
> 参考来源：
> - Java 1.7.10 反编译树：`docs/remapped_server_1.7.10/net/minecraft/`（只读参考，276 个 `.java`）
> - MCPE 0.14 反编译：`libminecraftpe_decompiled_full.zip`（Ghidra 输出，`entity/ai/`、`entity/mobs/`）

---

## 1. 一句话结论

Zenith 的 AI 缺的是**框架层**：原版把「选目标」「执行行为」「怎么移动」「怎么寻路」「身体朝向 vs 头部朝向」拆成五层，我们现在把它们揉在三个类里。最近的「倒退着跑 / 边走边跳 / 站着不动」都是这个结构差异的外在表现，而不是某个行为写错了。

---

## 2. 三方结构对照

| 层 | Java 1.7.10 | MCPE 0.14 | Zenith 现状 |
|---|---|---|---|
| 任务调度 | `EntityAITasks`：`taskEntries` + `targetEntries` 两个列表，`mutexBits` 互斥 | `entity/ai/GoalSelector.cpp` + `Goal.cpp` | `Mob::$behaviors` 单列表 + `CONTROL_MOVE/LOOK/JUMP/TARGET` |
| 目标选择 | 独立的 `EntityAINearestAttackableTarget` / `EntityAIHurtByTarget`（target 列表） | `TargetGoal.cpp`、`NearestAttackableTargetGoal.cpp`、`HurtByTargetGoal.cpp` | 行为内部自己找敌人（`AttackEnemyBehavior`） |
| 移动控制 | `EntityMoveHelper`（forward/strafe、打滑、撞墙起跳）、`EntityJumpHelper` | `MoveControl.cpp`、`JumpControl.cpp`、`MoveToBlockGoal.cpp` | `Mob::moveInDirection()` 每 tick 直接写 `motionX/Z` |
| 身体/头部朝向 | `renderYawOffset`（身体）vs `rotationYawHead`（头），`EntityLookHelper` 每 tick 最多 10° | `LookControl.cpp`：`yHeadRot`(0x38) / `yBodyRot`(0x178)，`BodyControl.cpp` 客户端插值 | **已实现** `Entity::$headYaw`（`18be0e1`） |
| 寻路 | `PathNavigate` + `PathFinder` + `PathPoint`（节点类型：走/水/岩浆/门/栅栏/危险） | `PathNavigation.cpp`、`PathFinder.cpp`、`WallClimberPathNavigation.cpp`（蜘蛛） | `PathNavigate` + `NodeEvaluator::isWalkable()`（布尔） |
| 视线判定 | `EntitySenses.canSee()` | `Mob::isLookingAtAnEntity` | `Behavior::canSee()`（射线，简化版） |

### 2.1 关键证据

**恐慌（Java）** `docs/remapped_server_1.7.10/net/minecraft/entity/ai/EntityAIPanic.java`

```java
public EntityAIPanic(EntityCreature e, double speed){ ...; this.func_75248_a(1); }   // 1 = MOVE
Vec3 v = RandomPositionGenerator.func_75463_a(entity, 5, 4);                        // 半径 5，垂直 4
public boolean func_75253_b(){ return !entity.getNavigator().noPath(); }            // 跑完路径才结束
```

**闲逛（Java）** `EntityAIWander.java`

```java
this.func_75248_a(1);                       // 只占 MOVE
if (entity.getRNG().nextInt(120) != 0) return false;   // 1/120 起走
Vec3 v = RandomPositionGenerator.func_75463_a(entity, 10, 7);  // 半径 10，垂直 7
```

**头部朝向（Java）** `EntityLookHelper.java:45-62`

```java
entity.rotationYawHead = 插值(entity.rotationYawHead, f, deltaYaw);            // 有目标
entity.rotationYawHead = 插值(entity.rotationYawHead, entity.renderYawOffset, 10.0F); // 无目标，10°/tick 回正
```

**头部朝向（MCPE）** `entity/mobs/LookControl.cpp` 的 `LookControl::tick`：结构体同时有 `yHeadRot`(0x38) 与 `yBodyRot`(0x178)，角度先归一化到 `[-180,180]`，再按 `±10.0` 度限速；`BodyControl.cpp` 只有 `clientTick`，说明身体朝向由客户端插值。

**协议** `src/pocketmine/network/protocol/MoveEntityPacket.php` 早就带三个角度：

```php
$this->putFloat($d[4]); //yaw        （身体）
$this->putFloat($d[5]); //headYaw    （头）
$this->putFloat($d[6]); //pitch
```

所以双角度不需要改协议，只需要 `Entity` 存两份。

---

## 3. 已经借鉴并落地的

| 改动 | 提交 | 说明 |
|---|---|---|
| 身体/头部朝向分离 | `18be0e1` | `Entity::$headYaw`；`Behavior::lookAt()` 只写头；`Mob::onUpdate()` 每 tick 以 10°/tick 回正；恐慌恢复只占 MOVE |
| 台阶跳跃判定 | `d045a91` | `Mob::needsStepJump()` 用脚底实际高度差，修「所有生物边走边跳」 |
| 恐慌与闲逛修正 | `3bb8d31` | 恐慌定期重选逃跑点、按 A* 绕障碍；闲逛不再因 `navigator->isDone()` 立刻结束 |

### 3.1 双角度实现要点（复述，便于后续对比）

- `Entity::updateMovement()` 广播 `$this->yaw, $this->pitch, $this->headYaw`；非生物的 `headYaw` 为 `null` 时 `Level::addEntityMovement()` 回退成 `yaw`。
- `Entity::clampHeadYaw($body, $head, 75.0)`：头最多拧到身体两侧 75°。
- `Entity::turnTowardAngle($cur, $target, $maxStep)`：限速转向。
- `PathNavigate` 只写身体 `yaw`，不再受 `isLookControlled()` 门控。
- 骷髅射击改用 `headYaw` 瞄准（`ShootPlayerBehavior::shoot()`）。

---

## 4. 可直接照抄的 Goal 清单（MCPE 0.14）

名字就是 `entity/ai/` 下的文件名，可直接作为移植单元。

### 4.1 战斗与目标

`MeleeAttackGoal` `RangedAttackGoal` `SpiderAttackGoal` `SpiderTargetGoal` `SwellGoal`（已有） `LeapAtTargetGoal` `OcelotAttackGoal` `SilverfishMergeWithStoneGoal` `SilverfishWakeUpFriendsGoal` `NearestAttackableTargetGoal` `HurtByTargetGoal` `PigZombieAngerTargetGoal` `PigZombieHurtByOtherGoal` `OwnerHurtByTargetGoal` `OwnerHurtTargetGoal` `DefendVillageTargetGoal`

### 4.2 生存

`FloatGoal`（水上漂浮）`FleeSunGoal` / `RestrictSunGoal`（白天找阴凉）`MoveIndoorsGoal` `AvoidMobGoal` `AvoidMobTypeGoal` `PanicGoal` `RabbitPanicGoal`

### 4.3 繁殖与互动

`BreedGoal` `MakeLoveGoal` `RecieveLoveGoal` `FollowParentGoal` `TemptGoal`（手持小麦/种子引诱）`PlayGoal` `BegGoal`（狼讨食）`SitGoal` `FollowOwnerGoal` `FindMountGoal` `MountPathingGoal` `EatBlockGoal`

### 4.4 村庄与方块交互

`DoorInteractGoal` `OpenDoorGoal` `RestrictOpenDoorGoal` `BreakDoorGoal`（困难模式僵尸破门）`MoveThroughVillageGoal` `MoveTowardsRestrictionGoal` `HarvestFarmBlockGoal` `RaidGardenGoal` `MoveToBlockGoal` `MoveTowardsTargetGoal` `EndermanTakeBlockGoal` `EndermanLeaveBlockGoal`

### 4.5 杂项

`LookAtMobGoal` `RandomLookAroundGoal`（已有）`RandomStrollGoal`（已有）
鱿鱼：`SquidDiveGoal` `SquidFleeGoal` `SquidIdleGoal` `SquidMoveAwayFromGroundGoal` `SquidOutOfWaterGoal` `SquidMoveControl`
史莱姆：`SlimeAttackGoal` `SlimeFloatGoal` `SlimeKeepOnJumpingGoal` `SlimeRandomDirectionGoal` `SlimeMoveControl`（我们只有一个 `SlimeBehavior`）

---

## 5. AI 之外值得借鉴的

| 项 | Java / MCPE 位置 | 现状 |
|---|---|---|
| 视线与「被盯着」判定 | `EntitySenses.canSee()`、`Mob::isLookingAtAnEntity` | `Behavior::canSee()` 简化射线；`Living::getLineOfSight()` 仍用身体朝向 |
| 跳跃力度 | `Mob::getJumpPower` | 全局硬编码 0.42 / 0.12 |
| 打滑与摩擦 | `Mob::applyFinalFriction`（冰、灵魂沙） | `Mob::onUpdate` 里统一摩擦 |
| 远程攻击统一入口 | `Mob::performRangedAttack` | 骷髅单独实现 |
| 死亡掉落与装备 | `dropRareDeathLoot`、`getEquipmentCount`、`getArmorCoverPercentage` | 部分硬编码 |
| 生成规则 | `BiomeGenBase.spawnList` + `EntityLiving.canSpawnHere` | `NaturalMobSpawnRules` 简化版 |

---

## 6. 推进路线

### 批 1：移动层（风险最低，观感收益最大）

1. 新增 `entity/ai/control/MoveControl`：行为只声明「目标 + 速度」，控制器算 `forward/strafe`、撞墙起跳、水面上浮，替代 `moveInDirection()` 里直写 motion。
2. 新增 `FloatGoal`，替换 `Behavior::swimming()` 的手动 `motionY = 0.12`。
3. `FleeSunGoal` / `RestrictSunGoal`：僵尸、骷髅白天找阴凉。
4. `TemptGoal`：手持小麦/种子可引诱牛羊猪鸡。
5. 验收：`docs/manual-verification.md` 新增小节（走动不抖、白天不晒、能引动物）。

### 批 2：目标层分离

1. `Mob` 增加 `$targetBehaviors`（对应 Java `targetEntries`）。
2. `NearestAttackableTargetGoal`、`HurtByTargetGoal` 独立成目标行为。
3. `MeleeAttackGoal`、`RangedAttackGoal` 只负责「已在攻击目标内」的执行。
4. 验收：被打必反击、同类互助、目标丢失后合理放弃。

### 批 3：寻路与世界交互

1. `NodeEvaluator` 引入节点类型（可走 / 水 / 岩浆 / 危险 / 门 / 栅栏 / 跳跃）。
2. `OpenDoorGoal`、`BreakDoorGoal`、`MoveThroughVillageGoal`。
3. `WallClimberPathNavigation`（蜘蛛爬墙）。
4. 验收：村民能进屋、僵尸困难模式能破门、蜘蛛能爬墙。

---

## 7. 已知差异与风险

1. **单 yaw 的历史包袱**：`headYaw` 是新增字段，任何直接读 `$entity->yaw` 当作「看向哪」的旧代码都要复查；目前已知 `Living::getLineOfSight()` 仍用身体朝向，尚未改。
2. **协议兼容**：`v11` / `v84` 包族都使用 `headYaw` 字段（`MoveEntityPacket.php:34`、`MoveEntityPacketV84.php:36`），双角度对三个协议族都有效，不需要额外翻译。
3. **MCPE 与 Java 的差异**：MCPE 0.14 的 `BodyControl` 只在客户端跑（服务端下发身体 + 头部角度），Java 的服务端会自己插值身体；Zenith 走的是「服务端算身体 + 头部」的折中方案。
4. **测试局限**：AI 行为依赖 `Level` / `Server` 运行时，离线只能测纯函数（如 `needsStepJump`、`clampHeadYaw`、候选点打分）与源码约束；观感必须上服验证。

---

## 8. 怎么验证

```bash
php73 -l src/pocketmine/entity/Entity.php
php73 tests/run-tests.php          # 当前 706/706
```

上服检查项见 `docs/manual-verification.md`；AI 相关至少覆盖：走动平滑、被打后转身逃跑且头回头、平时自行游荡、骷髅弹道对准玩家。
