# 任务调度（PluginTask / TaskScheduler，API 2）

## 时间单位

PocketMine 以 **tick** 计时，1 秒 = 20 tick（TPS=20）。
`秒数 * 20 = tick 数`（如 30 秒 = 600 tick）。

> ⚠️ API 2 用 `PluginTask`（来自 `pocketmine\scheduler\PluginTask`），
> 不是现代 API 的 `Task`。`PluginTask` 在 API 3.0.0 被移除，请勿混用。

## 创建任务（继承 PluginTask）

```php
namespace Author\PMPluginDemo\tasks;

use pocketmine\scheduler\PluginTask;
use Author\PMPluginDemo\Main;

class BroadcastTask extends PluginTask {

    /** @var Main */
    private $plugin;

    public function __construct(Main $plugin) {
        parent::__construct($plugin);   // PluginTask 需要 Plugin 所有者
        $this->plugin = $plugin;
    }

    /**
     * @param int $currentTick
     */
    public function onRun(int $currentTick) : void {
        foreach ($this->plugin->getServer()->getOnlinePlayers() as $player) {
            $player->sendMessage("欢迎来到服务器！");
        }
    }

    public function onCancel() : void {
        // 任务被取消时清理（可选）
    }
}
```

- `parent::__construct($plugin)` 保存所有者，之后可用 `$this->getOwner()` 取回插件。
- `onRun(int $currentTick)` 是 timer 触发时执行的逻辑。

## 调度方法（通过插件的调度器）

```php
$this->getScheduler()->scheduleTask(new BroadcastTask($this));                  // 立即（下一 tick）执行一次
$this->getScheduler()->scheduleDelayedTask(new BroadcastTask($this), 100);      // 延迟 5 秒执行一次
$this->getScheduler()->scheduleRepeatingTask(new BroadcastTask($this), 600);    // 每 30 秒重复
$this->getScheduler()->scheduleDelayedRepeatingTask(new BroadcastTask($this), 60, 20); // 延迟 3 秒后每 1 秒重复
```

| 方法 | 参数 | 说明 |
|------|------|------|
| `scheduleTask(Task, )` | — | 下一 tick 执行一次 |
| `scheduleDelayedTask(Task, $delay)` | delay: tick | 延迟后执行一次 |
| `scheduleRepeatingTask(Task, $period)` | period: tick | 每 period tick 重复 |
| `scheduleDelayedRepeatingTask(Task, $delay, $period)` | delay+period | 延迟后开始重复 |
| `cancelAllTasks()` | — | 取消本插件全部任务 |

## 取消任务

方式 1（在任务内部，推荐）：

```php
$this->getHandler()->cancel();
```

方式 2（持 taskId）：

```php
$this->getOwner()->getScheduler()->cancelTask($this->getTaskId());
```

方式 3（在插件禁用时统一清理）：

```php
protected function onDisable() : void {
    $this->getScheduler()->cancelAllTasks();
}
```

> 插件被禁用时，其所有任务会被自动取消；显式 `cancelAllTasks()` 属好习惯。

## 注意事项

- **无 `ClosureTask`**（API 3+ 才有）。简单任务也要写成 `PluginTask` 子类。
- 任务运行在**主线程**。耗时操作（大文件 IO、网络请求）会卡服；如需异步，用
  `pocketmine\scheduler\AsyncTask`（高级，注意异步任务内不能访问 Server/Player/Level 实例，
  需通过 `onCompletion()` 回主线程处理结果）。
- 不要在事件回调中直接做可能破坏连接状态的操作（如 `kick`），改用 `scheduleTask` 延到下个 tick。
