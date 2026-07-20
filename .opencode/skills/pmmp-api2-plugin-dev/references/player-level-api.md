# 玩家、世界与坐标（API 2）

## Player（pocketmine\Player）

常用方法（API 2 安全可用）：

| 方法 | 说明 |
|------|------|
| `getName()` | 玩家名。 |
| `getDisplayName()` | 显示名（含前缀）。 |
| `sendMessage($msg)` | 发送聊天消息。 |
| `sendPopup($msg)` | 屏幕中央弹窗（短暂）。 |
| `sendTip($msg)` | 屏幕右上角提示（短暂）。 |
| `getInventory()` | 返回 `PlayerInventory`（主背包）。 |
| `getHealth()` / `getMaxHealth()` / `setHealth($h)` | 生命值。 |
| `getPosition()` / `getLocation()` | 坐标（Position / Location）。 |
| `getLevel()` | 返回当前 `Level`（世界）。 |
| `teleport($target)` | 传送到 Position/Location/Player。 |
| `getDirection()` / `getYaw()` / `getPitch()` | 朝向。 |
| `kill()` / `attack($dmg)` | 击杀/受伤。 |
| `kick($reason = "")` | 踢出。 |
| `isOp()` / `hasPermission($node)` | 权限判断。 |
| `addAttachment($plugin, $perm, $val)` | 附加临时权限。 |
| `getAddress()` / `getPing()` | 网络信息（视构建）。 |
| `getInventory()->addItem(Item ...)` | 给物品。 |

> ⚠️ 以下为 **API 3+** 才有的方法，API 2 不可用：`addTitle()`（标题）、
> `getArmorInventory()` 的分离式护甲栏处理、`getXpLevel()` 部分变动等。
> 涉及标题/表单请借助外部插件（FormAPI）。

## Level（世界，pocketmine\level\Level）

API 2 中世界叫 `Level`，**不是** `World`（API 4 才改名）。

| 方法 | 说明 |
|------|------|
| `getName()` | level.dat 中的显示名 |
| `getFolderName()` | 文件夹名（唯一标识，推荐用这个） |
| `getBlock(Position $pos)` | 取方块 |
| `setBlock(Position $pos, Block $block)` | 设方块 |
| `getPlayers()` | 世界内玩家 |
| `save($force = false)` | 保存世界 |

## 坐标：Position 与 Location

- `pocketmine\level\Position`：x, y, z + Level。
- `pocketmine\level\Location`：Position + yaw/pitch（传送/生成用）。

```php
use pocketmine\level\Position;

$level = $this->getServer()->getLevelByName("world");   // 按文件夹名取世界
$pos = new Position(100, 64, 200, $level);
$player->teleport($pos);
```

## Server 世界相关方法（API 2）

| 方法 | 说明 |
|------|------|
| `getDefaultLevel()` | 默认世界 |
| `getLevelByName($name)` | 按名取已加载世界 |
| `getLevels()` | 已加载世界数组 `Level[]` |
| `getLevel($id)` | 按 ID 取世界 |
| `loadLevel($name)` / `unloadLevel(Level)` | 加载/卸载世界 |
| `isLevelLoaded($name)` | 是否已加载 |
| `getOnlinePlayers()` | 在线玩家数组 |
| `getPlayer($name)` / `getPlayerExact($name)` | 按名取玩家 |
| `broadcastMessage($msg)` | 全服广播 |

> `Level`/`getLevelByName`/`getDefaultLevel` 是 API 2 写法；
> API 4+ 对应 `World` / `WorldManager`。**不要**在 API 2 代码里用 `WorldManager`。
