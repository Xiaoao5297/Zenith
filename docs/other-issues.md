# 其他问题：安全加固、数据原子写、内置自动备份、config 配置目录

> 分支：`other-issues`
> 基线：`chunk-fix`

本分支是与"区块消失"无关的其它修复，分四块。

## 一、安全漏洞修复

| 编号 | 问题 | 位置 | 修复 |
|---|---|---|---|
| S-1 | netsh 命令注入：IP 直接拼接进 `passthru()` | `network/RakLibInterface.php` | `filter_var($address, FILTER_VALIDATE_IP)` 校验 + `escapeshellarg()` |
| S-2 | 玩家名路径穿越：客户端可提交含 `/ \ ..` 的用户名，用于 `players/<name>.dat` | `Player.php`、`Server.php` | 新增 `Utils::isValidPlayerName()` 白名单 `[A-Za-z0-9_\-.\+@]`、≤20、拒绝 `.`/`..`；登录时非法名踢出；读写存档再防护一层 |
| S-3 | 世界名路径穿越 | `Server::loadLevel/generateLevel/isLevelGenerated` | `Utils::isValidLevelName()` 校验 |
| S-4 | NBT 递归 DoS：客户端可通过告示牌/物品 NBT 提交深层嵌套 | `nbt/NBT.php` | 增加 `MAX_READ_DEPTH = 64` 深度上限 |
| S-5 | HTTP 工具关闭 SSL 校验 | `utils/Utils.php` | `CURLOPT_SSL_VERIFYPEER` 改为 `true` |
| S-6 | 不安全反序列化 | `utils/Config.php`、`level/generator/GeneratorRegisterTask.php`、`network/rcon/RCONInstance.php` | `unserialize(..., ["allowed_classes" => false])` |
| S-7 | 批量包解析越界 | `network/Network.php` | 校验 `$pkLen` 与缓冲区长度 |

## 二、数据写入原子化（防止崩溃写坏文件）

问题：原来所有关键文件都用 `file_put_contents()` 直接覆盖，进程在写入途中被 kill 会留下半截文件；
玩家 `.dat` 损坏后会被当成"新玩家"直接重置（背包/坐标/经验全丢）。

修复：
- 新增 `Utils::atomicWriteFile()`：写临时文件 → `fflush` → `rename()` 原子覆盖；附带陈旧临时文件清理。
- 应用到：玩家 `.dat`（`Server::saveOfflinePlayerData`）、插件配置（`Config::save`）、
  世界 `level.dat`（`BaseLevelProvider::saveLevelData`）、异步写（`FileWriteTask`）。
- 登录时的玩家数据保存改为同步，去掉一路异步并发写。

实测：直接覆盖写在"写入过程中 kill -9"下 60/60 残缺；原子写 0/60 残缺。

## 三、核心内置自动备份（替代外部 backup.sh）

新增模块：
- `utils/BackupManager.php`：分时段调度、硬链接快照、保留策略、磁盘保护。
- `scheduler/BackupTask.php`：异步执行，不卡主线程。
- `command/defaults/BackupCommand.php` + 命令注册。

配置 `config/backup.yml`（带中文注释模板，与 backup.sh 一致）：
```
1min / 5min / 30min / 1h / 1day / 1month，keep 3/3/3/4/6/6
tick: 30   min-free-mb: 2000
```
命令（op / 控制台）：`/backup`(status)、`list [时段]`、`run [时段]`、`clean [时段]`、`reload`、`config`。

快照使用硬链接复用未变化文件，只占变化部分的空间。实测一次快照 4586 个文件中 4577 个直接硬链接。

## 四、核心配置目录 config/

- 新增 `Server::getConfigPath()` / `getConfigFile()`：**`config/` 优先 → 根目录回退 → 新文件放 `config/`**。
- 新增 `Server::migrateLegacyConfigs()`：启动时自动把根目录旧核心配置迁移到 `config/`
  （纯 PHP、跨平台；`rename` 失败退回 `copy+unlink`；已存在文件不覆盖）。
- 迁移项：`pocketmine.yml`、`genisys.yml`、`server.properties`、`ops.txt`、`white-list.txt`、
  `banned-players.txt`、`banned-ips.txt`、`banned-cids.txt`、`permissions.yml`、`backup.yml`。
- 同步更新：`PocketMine.php`（向导判断）、`wizard/Installer.php`、`CrashDump.php`、
  `BackupManager`（备份包含 `config/`）。

> 注：`anticheat*.yml`、`lycore.yml`、`server.yml` 未纳入（疑似插件生成，核心未找到加载代码）。

## 部署注意

- 内置备份与旧 `backup.sh` 使用同一套 `backups/` 与 `.state`，**必须停用 backup.sh**，两者不能同时运行。
- 旧服务器直接替换 `src` 后启动即可：首次启动会自动迁移配置到 `config/`。
