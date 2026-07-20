# 插件生命周期与 PluginBase（API 2）

## 生命周期回调

主类继承 `pocketmine\plugin\PluginBase`，可覆写以下方法：

- `onLoad()`：插件被加载时（很早，世界尚未就绪）。较少用。
- `onEnable()`：**最核心**。插件启用时调用，**在这里注册事件、命令、任务、读取配置**。
  若返回 `false` 或抛出异常，插件会被标记为禁用。
- `onDisable()`：插件被禁用/服务器关闭时调用。用于清理（如 `cancelAllTasks()`、
  保存数据）。任务会在插件禁用时自动取消，但显式清理更稳妥。

```php
namespace Author\PMPluginDemo;

use pocketmine\plugin\PluginBase;
use pocketmine\event\Listener;
use Author\PMPluginDemo\EventListener;

class Main extends PluginBase implements Listener {

    public function onEnable() : void {
        // 保存默认配置（resources/config.yml -> 数据目录/config.yml）
        $this->saveDefaultConfig();

        // 注册事件监听器
        $this->getServer()->getPluginManager()->registerEvents(new EventListener($this), $this);

        // 注册命令（见 commands.md）
        // 调度任务（见 tasks.md）

        $this->getLogger()->info("PMPluginDemo 已启用");
    }

    public function onDisable() : void {
        $this->getLogger()->info("PMPluginDemo 已禁用");
    }
}
```

## PluginBase 常用方法

| 方法 | 说明 |
|------|------|
| `getDataFolder()` | 插件数据目录（含末尾斜杠），读写文件用。 |
| `getLogger()` | 返回 `PluginLogger`，`info()` / `warning()` / `error()` / `debug()`。 |
| `getConfig()` | 返回已 `saveDefaultConfig()` 的 `Config` 对象。 |
| `saveDefaultConfig()` | 把 `resources/config.yml` 复制到数据目录（不存在时）。 |
| `saveResource($name, $replace=false)` | 复制 `resources/` 下其他文件到数据目录。 |
| `getResource($name)` | 以流形式读取 `resources/` 内嵌资源。 |
| `getServer()` | 返回 `pocketmine\Server` 单例。 |
| `getScheduler()` | 返回本插件的 `TaskScheduler`。 |
| `getDescription()` | 返回 `PluginDescription`（读取 plugin.yml 内容）。 |
| `getFile()` | 插件 .phar 或源码目录路径。 |
| `isEnabled()` | 是否已启用。 |
| `getServer()->getPluginManager()` | 插件管理器（注册事件/获取其他插件）。 |
| `getServer()->getCommandMap()` | 命令映射（注册命令）。 |

## 依赖其他插件

```php
$eco = $this->getServer()->getPluginManager()->getPlugin("EconomyAPI");
if ($eco === null || !$eco->isEnabled()) {
    $this->getLogger()->error("缺少 EconomyAPI，插件禁用");
    $this->setEnabled(false); // 或 throw new \Exception(...)
    return;
}
```

## 命名空间与类加载
- 目录：`src/Author/PMPluginDemo/Main.php`
- 文件顶部：`namespace Author\PMPluginDemo;`
- 服务端以 PSR-4 风格从 `src/` 自动映射，无需 composer / autoload 配置。
- 同目录下的其他类使用相同前缀命名空间即可互相 `use`。
