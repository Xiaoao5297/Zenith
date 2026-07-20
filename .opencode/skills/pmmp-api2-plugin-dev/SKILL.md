---
name: pmmp-api2-plugin-dev
description: "PocketMine-MP 插件开发技能，专门面向 API 版本 2（兼容 PHP 7.x，对应 Minecraft 基岩版/PE 0.12-0.15 时代）。当用户要编写、维护、调试或构建旧版 PocketMine-MP（API 2、api 2.x、mcpe-protocol、PHP 7）的插件时使用。覆盖插件结构、生命周期、事件系统（@EventHandler）、命令、配置、任务调度（PluginTask）、玩家与世界（Level）、物品与方块（Item::get），并提供可运行的起步脚手架。"
---

# PMMP API 2 插件开发（PHP 7.x · MCPE 0.x）

## 概述

本技能用于开发 **PocketMine-MP API 版本 2** 的插件。API 2 对应非常早期的基岩版时代：

- **API 版本**：`2.0.0` – `2.1.0`（plugin.yml 中写 `api: 2.1.0` 即可覆盖整个 2.x 区间）
- **运行环境**：PHP 5.6 – 7.2（本技能以 **PHP 7** 为实际可编译/可运行目标）
- **游戏版本**：Minecraft PE / 基岩版 **0.12 – 0.15**（mcpe-protocol 约 27–46）
- **服务端**：PocketMine-MP 2.x 分支

## 何时使用本技能

- 用户要求为旧版 PocketMine-MP 编写、修改或调试插件，并提及 `api: 2.x` / `mcpe-protocol` / `PHP 7` / 「MC 0.几」。
- 用户已有 API 2 插件代码，需要补功能、修 bug、或把它整理成规范结构。
- 用户遇到「这段代码在 API 2 上报错」之类的兼容性问题（很多现代教程用的是 API 3/4/5 写法）。
- 需要区分 API 2 与现代 API 的差异（见 `references/api2-vs-modern.md`）。
- 需要从零搭建一个 API 2 插件（直接复制 `assets/plugin-template/`）。

本技能**不适用**于 API 3/4/5（World、Task、VanillaItems、ClosureTask、@priority 等是后期特性）。
当目标版本是 API 3+ 时，应提示用户改用对应版本的文档，避免混用。

## 环境约束（务必遵守）

1. **事件注解用 `@EventHandler`**，不是 `@priority`：
   `@EventHandler(priority = EventPriority::NORMAL, ignoreCancelled = false)`。
   `@priority` / `@ignoreCancelled` / `@notHandler` 的简写是 API 3.0.0+ 才有的。
2. **任务继承 `PluginTask`**（来自 `pocketmine\scheduler\PluginTask`），不是 `Task`；
   通过 `getOwner()` 取回插件实例。`PluginTask` 在 API 3.0.0 被移除。
3. **世界用 `Level`**（`pocketmine\level\Level`），`Position->getLevel()`，
   `Server->getLevelByName()`；不要用 `World` / `WorldManager`（API 4 才改名）。
4. **物品用 `Item::get($id, $meta = 0, $count = 1)`**；
   不要用 `VanillaItems::X()` / `ItemFactory`（API 3+ 引入）。
5. **无内置 Form API**：表单需依赖外部 `FormAPI` 插件（jojoe77777/FormAPI）。
6. **无 `ClosureTask`**（API 3+），简单任务也要写成 `PluginTask` 子类。
7. 命名空间约定：`src/<作者>/<插件名>/*.php`，命名空间 `<作者>\<插件名>`。
   服务端按 PSR-4 风格从 `src/` 映射类，无需 composer。

## 标准目录结构

```
MyPlugin/
├── plugin.yml              # 插件清单（必填）
├── resources/              # 内嵌资源（可选）
│   └── config.yml          # 默认配置
└── src/
    └── Author/
        └── MyPlugin/
            ├── Main.php          # 主类（必填，继承 PluginBase）
            ├── EventListener.php # 事件监听器（实现 Listener）
            ├── commands/
            │   └── MyCommand.php # 命令类（继承 PluginCommand / Command）
            └── tasks/
                └── MyTask.php    # 定时任务（继承 PluginTask）
```

起步时直接复制 `assets/plugin-template/` 整个目录，再改名字、命名空间与业务逻辑。

## 核心工作流

### 1. 创建插件
1. 复制 `assets/plugin-template/` → 服务端 `plugins/<PluginName>/`。
2. 改 `plugin.yml` 的 `name`、`main`、`version`、`api` 等字段（完整字段见 `references/plugin-manifest.md`）。
3. 改 `src/Author/PMPluginDemo/` 目录名为自己的 `<作者>/<插件名>`，并改各文件顶部 `namespace`。
4. 在 `Main::onEnable()` 中注册事件与命令（见 `references/event-system.md` 与 `references/commands.md`）。

### 2. 实现事件监听
- 让监听器 `implements pocketmine\event\Listener`。
- 每个处理方法：`public function onXxx(<具体事件> $event) : void`，参数类型即要监听的事件。
- 方法上方加 `@EventHandler(priority = EventPriority::NORMAL, ignoreCancelled = false)`。
- 在 `onEnable()` 中：`$this->getServer()->getPluginManager()->registerEvents(new EventListener($this), $this);`
- 可取消的事件调用 `$event->setCancelled()` / 查询 `$event->isCancelled()`。
- 详情见 `references/event-system.md`（含全部事件命名空间与优先级表）。

### 3. 实现命令
- 方式 A（简单）：在 `plugin.yml` 的 `commands:` 块声明，主类覆写 `onCommand()`。
- 方式 B（推荐，可复用）：建 `PluginCommand` 子类，`onEnable()` 里 `$this->getServer()->getCommandMap()->register($this->getName(), $cmd);`
- `onCommand(CommandSender $sender, Command $command, $label, array $args) : bool` 必须返回 `bool`。
- 详情见 `references/commands.md`。

### 4. 读写配置
- `resources/config.yml` 提供默认值；`$this->saveDefaultConfig()` 把它落到数据目录。
- 用 `new Config($this->getDataFolder() . "config.yml", Config::YAML)` 或 `$this->getConfig()` 读取。
- 详情见 `references/config.md`。

### 5. 定时/异步任务
- 建 `PluginTask` 子类，`onRun(int $currentTick) : void` 写逻辑，构造函数收 `$plugin`。
- 调度：`$this->getScheduler()->scheduleRepeatingTask(new MyTask($this), $periodTicks);`
- 取消：`$this->getHandler()->cancel();` 或 `$this->getOwner()->getScheduler()->cancelTask($this->getTaskId());`
- 详情见 `references/tasks.md`。

### 6. 操作玩家与世界
- 玩家：`pocketmine\Player`，常用方法见 `references/player-level-api.md`。
- 世界/坐标：`pocketmine\level\Level`、`pocketmine\level\Position`、`pocketmine\level\Location`。
- 传送：`$player->teleport(new Position($x, $y, $z, $level));`
- 详情见 `references/player-level-api.md`。

### 7. 操作物品与方块
- 物品：`Item::get($id, $meta, $count)`，`$player->getInventory()->addItem($item)`。
- 详情见 `references/items-blocks.md`。

### 8. 构建与发布
- 用 DevTools 插件：`/makeplugin <PluginName>` 打成 `.phar`（需匹配 API 2 的 DevTools 版本）。
- 详情见 `references/building.md`。

## 关键 API 速查（仅 API 2 语义）

| 主题 | API 2 写法 | 现代写法（勿混用） |
|------|-----------|-------------------|
| 事件注解 | `@EventHandler(priority = EventPriority::NORMAL)` | `@priority NORMAL` |
| 任务基类 | `PluginTask` + `getOwner()` | `Task` |
| 世界 | `Level` / `getLevelByName()` | `World` / `WorldManager` |
| 取物品 | `Item::get(278, 0, 1)` | `VanillaItems::DIAMOND_SWORD()` |
| 表单 | 外部 FormAPI 插件 | 内置 `forms/` |
| 闭包任务 | 不支持 | `ClosureTask` |

完整差异表见 `references/api2-vs-modern.md`。

## 资源

### references/（按需加载到上下文）
- `plugin-manifest.md` — plugin.yml 全部字段与示例（API 2）。
- `lifecycle.md` — PluginBase 生命周期（onLoad / onEnable / onDisable）与可用方法。
- `event-system.md` — 事件系统、@EventHandler、优先级、Cancellable、自定义事件、全部事件命名空间。
- `commands.md` — 命令 API（plugin.yml 与 PluginCommand 两种方式）。
- `config.md` — Config 类多格式读写。
- `tasks.md` — PluginTask 与 TaskScheduler。
- `player-level-api.md` — Player、Level、Position/Location、传送。
- `items-blocks.md` — Item / Block API 2 用法。
- `building.md` — DevTools 构建、目录装配、发布到 Poggit。
- `api2-vs-modern.md` — API 2 与现代 API（3/4/5）的关键差异对照。

### assets/（直接产出用）
- `plugin-template/` — 一个完整可运行的 API 2 起步插件（主类 + 监听器 + 命令 + 任务 + 配置）。
