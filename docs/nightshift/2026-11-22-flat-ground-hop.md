# 平地移动时的周期性“跳一下”排查与修复

日期：2026-11-22
分支：`feat/incore-port`

## 1. 目标

定位并修复“所有移动中的生物约每 0.5 秒同步跳一下，静止生物不跳”的问题。用户确认现象发生在平地；台阶、半砖、高低不平地形尚未单独测试。

验收标准：

- AC1：移动中的生物，`MoveEntityPacket` 的 y 使用脚底坐标；玩家仍使用脚底 + 眼睛高度。
- AC2：核心协议与 v11 的 `MoveEntityPacket` 线序保持为 `eid, x, y, z, yaw, headYaw, pitch`；v84 保持其协议自身的线序。
- AC3：`checkGroundState()` 的两个地面支撑分支在把 `onGround` 置真时同步清零 `motionY`，避免 `Creature` 末尾的 `motionY *= -0.5` 把残留负速度翻成向上弹跳。
- AC4：新增离线回归测试覆盖 AC1/AC2/AC3 的关键行为。
- AC5：`php73 tests/run-tests.php` 全绿；改动文件 `php73 -l` 通过；`git diff --check` 干净。

## 2. 排查结论

### 2.1 先排除了服务端每 10 tick 的全局逻辑

用真实 `Mob::onUpdate -> Creature::onUpdate -> Entity::move -> Entity::updateMovement` 在平地跑 240 tick：

- 服务端 `y=64.000000` 每 tick 不变；
- `motionY=0`、`onGround=1`、`ySize=0`；
- 移动包每 tick 都有，运动包只在起步时发一次；
- 没有全局 10 tick 调度分支；`auto-tick-rate` 在实际服务器配置中为 `false`。

因此服务端物理在平地上没有周期性 Y 变化，问题在“移动包内容/客户端解释”，而不是服务端每 10 tick 跳一次。

### 2.2 MoveEntity 的 y 对生物用错了坐标系

`Entity::updateMovement()` 原来对所有实体发送：

```php
$this->y + $this->getEyeHeight()
```

但 MCPE 0.14 的 `MoveEntityPacketData` 使用 `Entity::getPos()`，对生物就是脚底 `mPos`；只有玩家有 1.62 的 `ridingHeight` 偏移。于是移动中的生物被客户端放到脚底 + 眼睛高度，静止生物不发移动包所以不受影响。这正好符合“只有移动时出问题”。

### 2.3 旋转顺序：实测证明原顺序是对的

曾根据反编译里 `Entity::setRot` 的参数推测线上顺序应为 `yaw, pitch, headYaw`，并在 `729c45d` 中把核心/v11 编码器改成该顺序。真实 0.14 客户端测试立即出现：

> 所有生物的头部转向方向都是错的，而且头还会乱转

说明客户端实际按 **`yaw, headYaw, pitch`** 解释这三个字段，原实现是正确的。该旋转顺序改动已在后续提交中完整回退；本文档不再把旋转顺序列为问题原因。

### 2.4 台阶/半砖的残留竖直速度

`Entity::checkGroundState()` 的两个支撑分支只设置 `onGround = true` 后直接返回，没有清零 `motionY`。如果这一 tick 开始时 `onGround=false` 且 `motionY=-0.08`，落回支撑面后 `motionY` 仍是 `-0.08`，随后 `Creature` 的 `if($this->onGround) $this->motionY *= -0.5;` 会把它变成 `+0.04`，并被 `SetEntityMotionPacket` 发给客户端。平地通常不会进入这个分支，但台阶、半砖、边缘会。

## 3. 改动

### `src/pocketmine/entity/Entity.php`

- `updateMovement()` 改为发送 `$this->getNetworkY()`。
- 新增：

```php
protected function getNetworkY() : float{
    return $this instanceof \pocketmine\Player ? $this->y + $this->getEyeHeight() : $this->y;
}
```

- `checkGroundState()` 的 `hasGroundSupport(0.01, false)` 分支和 `findPartialBlockGroundSupportY()` 分支在 `onGround = true` 后加 `motionY = 0`。

### `src/pocketmine/network/protocol/MoveEntityPacket.php`

- 旋转字段维持 `$d[4]` yaw、`$d[5]` headYaw、`$d[6]` pitch。
- 注释明确写出“0.14 客户端实测按 yaw, headYaw, pitch 解释”，防止再次被反编译推测误导。

### `src/pocketmine/network/protocol/v11/MoveEntityPacket.php`

- 与核心包一致：维持 `yaw, headYaw, pitch`；LY Core 的 LGPL 文件头保持不动。

### `tests/MoveEntityPacketTest.php`（新增）

覆盖：

- 核心协议与 v11 的 `yaw, headYaw, pitch` 线序；
- 生物 `getNetworkY()` 返回脚底；
- Player 仍保留眼睛高度偏移；
- `checkGroundState()` 两个支撑分支都清零 `motionY`。

### `tests/MobStepTest.php`

新增两个真实物理回归：

- `support_snap_zeroes_motion_y`；
- `support_snap_never_bounces_up`。

## 4. 验证

```text
$ php73 -l src/pocketmine/entity/Entity.php
No syntax errors detected in src/pocketmine/entity/Entity.php
$ php73 -l src/pocketmine/network/protocol/MoveEntityPacket.php
No syntax errors detected in src/pocketmine/network/protocol/MoveEntityPacket.php
$ php73 -l src/pocketmine/network/protocol/v11/MoveEntityPacket.php
No syntax errors detected in src/pocketmine/network/protocol/v11/MoveEntityPacket.php
$ php73 -l tests/MoveEntityPacketTest.php
No syntax errors detected in tests/MoveEntityPacketTest.php

$ php73 tests/run-tests.php
...
995/995 passed
```

`tests/MoveEntityPacketTest.php` 单独运行 10/10 通过；`tests/MobStepTest.php` 单独运行 12/12 通过。

## 5. 仍需上服确认

离线测试只能证明包字段和服务端物理，不能替代真实 0.14 客户端观察。请重点确认：

1. 平地行走的猪/牛/羊不再周期性上下弹；
2. 鸡行走时不再一直扇翅膀；
3. 生物头部朝向恢复正常，不再乱转；
4. 台阶、半砖、上下坡仍能正常通过，且没有新的向上弹跳；
5. 玩家自身移动高度没有变化。

## 6. 风险与范围

- 旋转顺序已回退；不要再依据 `Entity::setRot` 的参数数量推断 0.14 的线上字段顺序。
- v84 的 `MoveEntityPacketV84` 使用协议 84 自己的旋转字节序，未改动。
- `V015Translator` 仍按 `Level` 内部顺序 `[yaw, headYaw, pitch]` 映射到 v84 字段。
- 玩家 `Player.php:1882` 的移动包仍使用眼睛高度，符合玩家 `ridingHeight=1.62` 的约定。
- 这次修复没有改动 A*、MoveControl、StrollBehavior 或台阶高度逻辑。
