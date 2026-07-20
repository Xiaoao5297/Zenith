# plugin.yml 参考（API 2）

`plugin.yml` 是插件清单文件，服务端据此决定是否加载插件。位于插件根目录。

## 字段总览

### 必填字段
- `name` (string)：插件名。可含字母、数字、连字符、点、下划线（空格不推荐）。
- `version` (string)：版本号。建议三段式语义版本，如 `1.0.0`。
- `main` (string)：主类完整限定名（命名空间\类名）。该类必须：
  - 非抽象
  - 实现 `pocketmine\plugin\Plugin`（通常通过继承 `pocketmine\plugin\PluginBase`）
- `api` (string **或** string[])：兼容的 API 版本。**在 API 2 中通常是单个字符串** `api: 2.1.0`（覆盖 2.0.0–2.1.0）。
  > 数组形式 `api: [3.0.0, 4.0.0]` 是 API 3+ 才普遍使用的写法；API 2 时代清单多为单值。
  > 服务端按「主版本号」匹配：主版本不同则拒绝加载。

### 可选字段
- `description` (string)：简短描述。
- `author` (string) / `authors` (string[])：作者。两者都写则合并。
- `website` (string)：插件网站。
- `prefix` (string)：日志前缀，默认用插件名。
- `load` (string)：启动阶段何时调用 `onEnable()`。
  - `STARTUP`：任意世界加载前
  - `POSTWORLD`（默认）：所有声明的世界加载后
- `depend` (string|string[])：硬依赖。缺失则插件不加载。
- `softdepend` (string|string[])：软依赖。存在则先于本插件加载。
- `loadbefore` (string|string[])：本插件需先于这些插件加载。
- `mcpe-protocol` (int|int[])：兼容的 MCPE 网络协议号。不匹配则加载失败。（API 2 多为 27–46 区间，依具体小版本而定。）
- `extensions` (array)：插件所需的 PHP 扩展。缺失则拒绝加载。
- `commands` (array)：在 `onCommand()` 中实现的命令声明（见 `commands.md`）。
- `permissions` (array)：插件定义的权限（见 `commands.md`）。

## 完整示例（API 2）

```yaml
name: PMPluginDemo
version: 1.0.0
api: 2.1.0
main: Author\PMPluginDemo\Main
description: 一个 API 2 起步示例插件
author: Author
website: https://example.com
prefix: Demo
load: POSTWORLD
mcpe-protocol: [40, 41, 42]

commands:
  greet:
    description: "向玩家问好"
    usage: "/greet [名字]"
    aliases:
      - hi
      - hello

permissions:
  pmplugindemo.command.greet:
    description: "允许使用 /greet 命令"
    default: true
```

## 注意事项
- API 2 中 `commands` 下的命令**不强制要求** `permission` 字段（现代 API 5 才强制）。但建议总是显式声明权限以保持规范。
- `api` 只写「最小所需版本」即可，不必罗列所有小版本。
- 修改 `main` 后务必同步修改主类里的 `namespace` 与类名，否则服务端找不到主类（报错 `Could not find main class`）。
