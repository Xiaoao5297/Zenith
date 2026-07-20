# 事件系统（API 2）

## 监听器与 @EventHandler

1. 监听器类 `implements pocketmine\event\Listener`。
2. 每个处理方法接收**一个**事件对象作为参数，类型即要监听的事件。
3. 方法必须是 `public`、非 `static`，返回类型建议 `void`。方法名任意（约定以 `on` 开头）。
4. 方法上方加 **`@EventHandler` 注解**（API 2 语法）：

```php
namespace Author\PMPluginDemo;

use pocketmine\event\Listener;
use pocketmine\event\player\PlayerJoinEvent;
use pocketmine\event\EventPriority;
use pocketmine\utils\TextFormat;

class EventListener implements Listener {

    /** @var \Author\PMPluginDemo\Main */
    private $plugin;

    public function __construct(\Author\PMPluginDemo\Main $plugin) {
        $this->plugin = $plugin;
    }

    /**
     * @EventHandler(priority = EventPriority::NORMAL, ignoreCancelled = false)
     */
    public function onJoin(PlayerJoinEvent $event) : void {
        $player = $event->getPlayer();
        $player->sendMessage(TextFormat::GREEN . "欢迎来到服务器，" . $player->getName() . "！");
    }
}
```

> ⚠️ API 2 用 `@EventHandler(priority = ..., ignoreCancelled = ...)`。
> 现代 API 3+ 的 `@priority NORMAL` / `@ignoreCancelled` 简写**在 API 2 不生效**，
> 请始终使用 `@EventHandler(...)` 形式。

## 事件优先级（EventPriority）

执行顺序（早 → 晚）：

```
LOWEST → LOW → NORMAL(默认) → HIGH → HIGHEST → MONITOR
```

| 优先级 | 用途 | 可取消 | 可修改 |
|--------|------|--------|--------|
| LOWEST | 最先处理，便于其他插件再加工 | 是 | 是 |
| LOW | 早期处理 | 是 | 是 |
| NORMAL | 默认 | 是 | 是 |
| HIGH | 较晚处理，可覆盖前者 | 是 | 是 |
| HIGHEST | 最后修改机会 | 是 | 是 |
| MONITOR | 仅观测最终状态，**不可修改/取消** | 否 | 否 |

`EventPriority` 常量位于 `pocketmine\event\EventPriority`：`LOWEST, LOW, NORMAL, HIGH, HIGHEST, MONITOR`。

## 注册监听器

在 `Main::onEnable()` 中：

```php
$this->getServer()->getPluginManager()->registerEvents(new EventListener($this), $this);
```

插件禁用时监听器会被自动注销。

## 可取消事件（Cancellable）

实现 `pocketmine\event\Cancellable` 的事件可取消：

```php
use pocketmine\event\block\BlockBreakEvent;

/**
 * @EventHandler(priority = EventPriority::HIGHEST)
 */
public function onBreak(BlockBreakEvent $event) : void {
    if ($this->isProtected($event->getBlock()->getPosition())) {
        $event->setCancelled();            // 取消事件
        $event->getPlayer()->sendMessage("此处禁止破坏");
    }
}
```

- 查询：`$event->isCancelled()`
- 取消：`$event->setCancelled()` 或 `setCancelled(true)`
- `ignoreCancelled = true` 的处理器才会收到已被取消的事件。

> 注意：API 2 中为事件实现自定义可取消性需直接实现 `Cancellable` 接口的两个方法
> （`CancellableTrait` 是 API 3 才加入的）。

## 自定义事件

```php
namespace Author\PMPluginDemo\event;

use pocketmine\event\Event;
use pocketmine\event\Cancellable;
use pocketmine\Player;

class PlayerRankUpEvent extends Event implements Cancellable {
    private $player;
    private $cancelled = false;

    public function __construct(Player $player) { $this->player = $player; }
    public function getPlayer() : Player { return $this->player; }
    public function isCancelled() : bool { return $this->cancelled; }
    public function setCancelled($force = true) : void { $this->cancelled = (bool) $force; }
}
```

触发：

```php
$ev = new PlayerRankUpEvent($player);
$this->getServer()->getPluginManager()->callEvent($ev); // API 2 用 callEvent()
if (!$ev->isCancelled()) {
    // 继续处理
}
```

> API 3+ 用 `$event->call()`；API 2 用 `getPluginManager()->callEvent($event)`。

## 全部事件命名空间

| 命名空间 | 内容 |
|----------|------|
| `pocketmine\event\block` | 方块破坏/放置/点燃等 |
| `pocketmine\event\entity` | 实体伤害/死亡/生成/爆炸等 |
| `pocketmine\event\player` | 加入/退出/聊天/移动/交互/命令等 |
| `pocketmine\event\inventory` | 库存点击/交易等 |
| `pocketmine\event\level` (或 `world`) | 世界加载/方块更新/掉落物等 |
| `pocketmine\event\server` | 服务器启动/命令/数据包等 |
| `pocketmine\event\plugin` | 插件启用/禁用 |
| `pocketmine\event\weather` | 天气（部分构建） |

常用事件示例：`PlayerJoinEvent`, `PlayerQuitEvent`, `PlayerChatEvent`, `PlayerMoveEvent`,
`PlayerInteractEvent`, `BlockBreakEvent`, `BlockPlaceEvent`, `EntityDamageEvent`,
`EntityDamageByEntityEvent`, `PlayerCommandPreprocessEvent`, `PlayerDeathEvent`。
