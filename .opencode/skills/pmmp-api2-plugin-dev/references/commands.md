# 命令系统（API 2）

## 方式 A：plugin.yml + onCommand（简单）

在 `plugin.yml` 声明命令，主类覆写 `onCommand()`：

```yaml
# plugin.yml
commands:
  greet:
    description: "向玩家问好"
    usage: "/greet [名字]"
    aliases:
      - hi
      - hello
    permission: pmplugindemo.command.greet
    permission-message: "你没有权限使用此命令"
```

```php
use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\Player;

public function onCommand(CommandSender $sender, Command $command, $label, array $args) : bool {
    switch ($command->getName()) {
        case "greet":
            $name = isset($args[0]) ? $args[0] : $sender->getName();
            $sender->sendMessage("你好，" . $name . "！");
            return true;            // 成功
        // default: 返回 false 会向玩家显示 usage
    }
    return false;                  // 显示 usage 消息
}
```

> API 2 的 `onCommand` 签名：`(CommandSender $sender, Command $command, $label, array $args)`。
> 注意 `$label` 无类型声明（现代 API 为 `string $label`）。
> **必须返回 `bool`**：`true`=已处理，`false`=显示 usage。

## 方式 B：PluginCommand 子类（推荐，可复用/易测试）

```php
namespace Author\PMPluginDemo\commands;

use pocketmine\command\PluginCommand;
use pocketmine\command\CommandSender;
use pocketmine\Player;
use pocketmine\utils\TextFormat;
use Author\PMPluginDemo\Main;

class GreetCommand extends PluginCommand {

    /** @var Main */
    private $plugin;

    public function __construct(Main $plugin) {
        parent::__construct("greet", $plugin);
        $this->setDescription("向玩家问好");
        $this->setUsage("/greet [名字]");
        $this->setAliases(["hi", "hello"]);
        $this->setPermission("pmplugindemo.command.greet");
        $this->plugin = $plugin;
    }

    public function execute(CommandSender $sender, $commandLabel, array $args) : bool {
        if (!$this->testPermission($sender)) {
            return false;
        }
        if (!$sender instanceof Player) {
            $sender->sendMessage(TextFormat::RED . "请在游戏内使用");
            return false;
        }
        $name = isset($args[0]) ? $args[0] : $sender->getName();
        $sender->sendMessage(TextFormat::GREEN . "你好，" . $name . "！");
        return true;
    }
}
```

在 `onEnable()` 注册：

```php
use pocketmine\command\PluginCommand;
$this->getServer()->getCommandMap()->register($this->getName(), new GreetCommand($this));
```

## CommandSender 与判断来源

- `pocketmine\command\CommandSender`：命令发送者基类。
- `pocketmine\Player`：玩家（游戏内）。
- `pocketmine\command\ConsoleCommandSender`：控制台。
- 判断：`if ($sender instanceof Player) { ... }`

## 权限

plugin.yml 声明（API 2 不强制，但推荐）：

```yaml
permissions:
  pmplugindemo.command.greet:
    description: "允许使用 /greet"
    default: true          # true | false | op
```

`default` 取值：
- `true`：所有人默认拥有
- `false`：默认无
- `op`：仅管理员（OP）

代码检查：

```php
if ($player->hasPermission("pmplugindemo.command.greet")) { ... }
```

## 广播消息

```php
$this->getServer()->broadcastMessage("服务器公告");
```

## 控制台执行命令

```php
$this->getServer()->dispatchCommand($this->getServer()->getConsoleSender(), "say hello");
```
