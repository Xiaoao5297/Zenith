# API 2 与现代 API（3 / 4 / 5）的关键差异

> 把现代教程照搬到 API 2 会大量报错。下表列出最易踩坑的差异。

| 主题 | API 2（本技能） | 现代 API 3/4/5 | 影响 |
|------|----------------|---------------|------|
| 事件注解 | `@EventHandler(priority = EventPriority::NORMAL, ignoreCancelled = false)` | `@priority NORMAL` / `@ignoreCancelled` / `@notHandler` | API 2 必须用 `@EventHandler(...)` |
| 任务基类 | `PluginTask`（有 `getOwner()`） | `Task`（`PluginTask` 已移除） | API 2 用 `PluginTask` |
| 任务构造 | `parent::__construct($plugin)` | 构造函数自行存 `$plugin` | 写法不同 |
| 闭包任务 | 不支持 | `ClosureTask` | API 2 需写类 |
| 世界类 | `Level`（`pocketmine\level`） | `World`（`pocketmine\world`） | 命名空间与类名 |
| 取世界 | `getLevelByName()` / `getLevels()` / `getDefaultLevel()` | `WorldManager::getWorldByName()` 等 | Server 方法名 |
| 坐标 | `Position`/`Location`（`level` 命名空间） | `world` 命名空间 | use 语句 |
| 物品工厂 | `Item::get($id, $meta, $count)` | `VanillaItems::X()` / `ItemFactory::` | API 2 用 `Item::get` |
| 取消事件 | `$event->setCancelled()` | 同，但 `CancellableTrait` 简化自定义事件 | 自定义事件实现方式 |
| 触发事件 | `getPluginManager()->callEvent($e)` | `$event->call()` | 写法不同 |
| 表单 | 外部 FormAPI 插件 | 内置 `pocketmine\form\Form` | API 2 需依赖插件 |
| 配置强类型 | 无（`get()` 返回 mixed） | `getInt`/`getString`/`getBool` | API 3+ 才有 |
| 玩家标题 | 无 `addTitle()` | `addTitle()` | API 2 用 sendPopup/sendTip |
| 插件清单 api | 多为单值 `api: 2.1.0` | 数组 `api: [5.0.0]` | 格式习惯 |
| 命令权限 | 非强制 | API 5 强制命令需 permission | API 2 可省略 |

## 典型报错与修正

- `Undefined class 'Task'` → API 2 应 `use pocketmine\scheduler\PluginTask;`
- `Call to undefined method ...::getOwner()` → 你用的是 `Task`（现代），API 2 用 `PluginTask`
- `@priority` 不生效 → 改为 `@EventHandler(priority = ...)`

## 设备/环境提示

- API 2 的 PMMP 2.x 运行于 **PHP 5.6 – 7.2**，本技能以 **PHP 7** 为实际开发与测试目标。
- MCPE 协议：API 2 对应 MCPE **0.12 – 0.15**（protocol 约 27–46）。
- 32 位设备不被支持。
