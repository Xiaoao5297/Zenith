# AGENTS.md

Zenith — PocketMine-MP 分叉（Genisys / SCAXE 血统），纯 PHP 实现，无 Composer、无 PHPUnit、无构建步骤。

## 环境与命令

- PHP **7.3 / 7.4**（开发机为 7.3.33）。禁止 PHP 8 语法：类型属性、箭头函数、`?->`、命名参数、联合类型、`match`。
- 全量离线测试：`php tests/run-tests.php`（当前 66 个用例，必须全绿）。
- 改完任一 PHP 文件执行 `php -l <file>`。
- 启动开发服：`./start.sh`。它按 `bin/php`、`/usr/local/php73/bin/php` 等顺序找 7.3/7.4，没有 phar 时回退到 `src/pocketmine/PocketMine.php`。
- 仓库没有 CI、没有代码生成、没有依赖锁文件，别去找 `composer install`。

## 测试约定（零依赖）

- 新增用例放 `tests/<Name>Test.php`，类名与文件名一致，唯一入口 `public static function run() : array`；返回 `[[name, pass, detail], ...]`。
- 测试文件自己 `require_once` 需要的源码文件，没有 autoloader、不启动 PocketMine 运行时；新用例必须能脱离服务器跑。
- 运行器不支持按用例过滤（会 glob 全部 `*Test.php`），定位失败请 grep 输出。

## 代码风格

- 缩进用 **Tab**；`}else{`、`}catch(...){` 同行；返回类型写作 ` : string`；类名后大括号同行。
- 新代码允许中文 docblock/注释（`src/pocketmine/anticheat/` 是范例）。
- 行尾符：`src/pocketmine/network/protocol/v84/` 下 56 个文件是 **CRLF**，其余 LF。编辑时不要用会统一换行的工具（优先 `edit` 工具或按字节读写的脚本）。

## 许可证（最容易改错的地方）

- Zenith 自有文件头（ASCII banner + `@author Xiaoao` + Zenith 链接）= **GPLv3**；根目录 `LICENSE` 为 GPLv3 全文。
- 上游文件必须保留原头，**不要批量改写**：PocketMine Team（LGPL）、iTX Technologies（Genisys）、LY Core / U core、Glowstone、Sunch233、TuranicTeam。
- 本仓库同时存在 LGPL 与 GPL 文件是预期状态，不是遗漏。

## 目录边界

- `src/pocketmine/` 核心；`src/raklib/` 网络层；`src/spl/` SPL 兼容层。
- 多协议：核心协议 70（0.14.x），另有 v11（0.11–0.13）与 v84（0.15）包族。`src/pocketmine/network/compat/` 是编解码/翻译层（`PacketCodecRegistry`、`BatchCodec`、`translators/`），`network/protocol/<family>/` 是各包族实现。改包结构要同时考虑入站转换与出站转换。
- 反作弊：`src/pocketmine/anticheat/check/`（Fly/Speed/NoFall/Reach/…），统一经 `ViolationManager` 出 alert/punish；阈值在 `src/pocketmine/resources/anticheat.yml`，提示文案在 `anticheat_messages.yml`。
- `docs/audit-2026-11-01.md`：审计条目（是什么/为什么/怎么做/怎么测），修完对应问题要补记。
- `docs/manual-verification.md`：无法离线验证、需上服人工确认的项。
- `docs/lycore/`、`docs/SCAXE-GO-CE/`、`docs/remapped_server_1.7.10/`：**只读上游参考树**（约 1400 / 470 / 3890 个文件），不要格式化、不要顺手批量移植；移植规则见 `docs/lycore-差异与移植指南.md`。
- `.opencode/skills/pmmp-api2-plugin-dev/`：插件 API 2 语法参考（PHP 7 时代的 PocketMine 插件写法）。

## Git 与协作

- 长期工作在新分支（如 `fix/audit-critical-1`）。未经明确要求**不要合并 main，也不要 force-push**。
- `.gitignore` 故意忽略 `*.phar` 与 `*.bak`（HYG-001/002），不要新增这类产物。
- 不要用 `git add -A` / `git add .`：`docs/` 下有巨型参考树，只暂存明确路径。
- 在 Android/Termux 上 `link()` 被系统拒绝，备份日志会出现「链接:0 复制:N」并全量复制，这是环境限制不是回归。
