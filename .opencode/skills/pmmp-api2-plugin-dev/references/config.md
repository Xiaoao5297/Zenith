# 配置读写（Config 类，API 2）

## 默认配置

在 `resources/config.yml` 提供默认内容，`onEnable()` 中：

```php
$this->saveDefaultConfig();              // resources/config.yml -> 数据目录/config.yml（仅当不存在）
$config = $this->getConfig();            // 直接拿 Config 对象
```

也可读取 `resources/` 其他文件：

```php
$this->saveResource("messages.yml");     // 复制到数据目录
```

## 创建/读取任意 Config

```php
use pocketmine\utils\Config;

$path = $this->getDataFolder() . "data.yml";
$config = new Config($path, Config::YAML, [
    "max-players" => 100,
    "enable-pvp"  => true,
    "spawn"       => ["x" => 0, "y" => 64, "z" => 0],
]);
```

## 支持的格式（构造函数第二参数）

| 常量 | 值 | 文件类型 |
|------|----|---------|
| `Config::DETECT` | -1 | 按扩展名自动识别 |
| `Config::PROPERTIES` | 0 | `.properties` / `.cnf` |
| `Config::JSON` | 1 | `.json` |
| `Config::YAML` | 2 | `.yml` / `.yaml` |
| `Config::SERIALIZED` | 4 | PHP 序列化 `.sl` |
| `Config::ENUM` | 5 | 行列表 `.txt` / `.list` |

> API 2 的 `get()` 返回 `mixed`（无强类型的 `getInt`/`getString`/`getBool`——那是 API 3+ 才加的）。
> 读取嵌套键用 `getNested("spawn.x")`。

## 常用方法

| 方法 | 说明 |
|------|------|
| `get($k, $default)` | 取值，键不存在返回 `$default`。 |
| `getNested($k, $default)` | 点号路径取值，如 `getNested("spawn.x")`。 |
| `getAll()` | 返回全部数据数组。 |
| `set($k, $v)` | 设置值（内存）。 |
| `setNested($k, $v)` | 点号路径设置。 |
| `setAll(array $v)` | 整体替换。 |
| `exists($k)` | 键是否存在。 |
| `remove($k)` | 删除键。 |
| `removeNested($k)` | 删除嵌套键。 |
| `save()` | 写回磁盘（必须调用才持久化）。 |
| `reload()` | 从磁盘重新加载。 |

## 示例

```php
$max = $this->getConfig()->get("max-players", 100);
$x = $this->getConfig()->getNested("spawn.x", 0);

$this->getConfig()->set("last-startup", time());
$this->getConfig()->save();
```

> 修改后务必 `save()`，否则重启后丢失。
