# 物品与方块（Item / Block，API 2）

## 获取物品：Item::get()

API 2 用 `Item::get($id, $meta = 0, $count = 1)`：

```php
use pocketmine\item\Item;

$item = Item::get(Item::DIAMOND_SWORD);          // 用 Item 常量
$item = Item::get(278, 0, 1);                    // 等价：钻石剑（旧 PE 物品 ID）
$item = Item::get(388, 3, 1);                    // ID + meta(伤害值/附加值) + 数量
```

- 第一个参数：物品 ID（可用 `Item` 类常量，如 `Item::DIAMOND_SWORD`、`Item::STONE`、
  `Item::APPLE`，或历史 PE 数字 ID）。
- 第二个参数：meta / damage（附加值，如染色、耐久方向）。
- 第三个参数：数量。

> ⚠️ 现代 API 的 `VanillaItems::DIAMOND_SWORD()` 与 `ItemFactory` 是 **API 3+** 才引入，
> API 2 **不可用**。统一用 `Item::get()`。

## 物品常用操作

```php
use pocketmine\utils\TextFormat;

$item->setCustomName(TextFormat::AQUA . "神剑");
$item->setLore(["一行描述", "二行描述"]);          // lores
$item->setCount(16);
$item->getCount();
$item->getId();
$item->getDamage();                                // meta
$name = $item->getName();
```

### 自定义 NBT（命名标签）

```php
$nbt = $item->getNamedTag();
$nbt->setInt("ItemLevel", 5);
$item->setNamedTag($nbt);                          // 写回
// 读取：
if ($item->getNamedTag()->hasTag("ItemLevel")) {
    $lvl = $item->getNamedTag()->getInt("ItemLevel");
}
```

## 给玩家物品

```php
$player->getInventory()->addItem($item);
$player->getInventory()->setItem(0, $item);        // 指定槽位
$player->getInventory()->contains($item);
$player->getInventory()->removeItem($item);
```

## 方块（Block）

API 2 中区块/方块操作在世界（Level）上进行：

```php
use pocketmine\level\Position;
use pocketmine\block\Block;

$block = $player->getLevel()->getBlock($player->getPosition());  // 脚下方块
$player->getLevel()->setBlock($player->getPosition(), Block::get(Block::STONE));
```

- `Block::get($id, $meta = 0)` 取得方块实例（与 `Item::get` 类似）。
- 也可用 `Item` 转 `Block`：`$item->getBlock()`。

## 通过事件操作方块/物品

```php
use pocketmine\event\block\BlockBreakEvent;
use pocketmine\event\player\PlayerInteractEvent;

/**
 * @EventHandler(priority = EventPriority::NORMAL)
 */
public function onBreak(BlockBreakEvent $event) : void {
    $block = $event->getBlock();
    $drops = $event->getDrops();   // 掉落物数组（Item[]）
    // $event->setDrops([...]); 修改掉落
}

/**
 * @EventHandler(priority = EventPriority::NORMAL)
 */
public function onInteract(PlayerInteractEvent $event) : void {
    $item = $event->getItem();     // 手中物品
    $block = $event->getBlock();   // 点击的方块
    $face = $event->getFace();     // 点击面（BlockFace 常量）
}
```

> 注意具体物品 ID 数值随 MCPE 协议版本变化；尽量用 `Item` / `Block` 类常量以提高可读性。
> 若需精确对照旧版 ID，参考对应 MCPE 版本的 `Item`/`Block` 源码常量表。
