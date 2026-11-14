# 代码风格约定

本仓库是 PocketMine-MP 的老式代码风格（1TBS + Tab），**不是 PSR-12 / PER-CS**。
新增或修改代码时，请复制周边文件的写法，不要引入新风格，也不要整文件重新格式化。

适用范围：`src/pocketmine/`、`src/raklib/`、`src/spl/` 以及后续新增的 PHP 代码。
`docs/` 下的上游参考树（`lycore/`、`SCAXE-GO-CE/`、`remapped_server_1.7.10/`）只读，不适用本文。

## 1. 缩进与空白

- 缩进一律用 **Tab**，不用空格。
- 控制关键字与左括号之间**不留空格**：`if(`、`elseif(`、`for(`、`foreach(`、`while(`、`switch(`、`catch(`。
- 二元运算符两侧留空格：`$a + $b`、`$a === $b`、`$a ? $b : $c`、`$a ?? $b`。
- 字符串连接 `.` 两侧留空格：`"prefix " . $name`。
- `->`、`::`、`!`、`++`/`--`（前缀）紧贴操作数：`$this->logger`、`self::MODE_ALERT`、`!$flag`、`++$i`。
- 数组箭头两侧留空格：`$a => $b`；默认参数：`function f(int $n = 0)`。
- 逗号后留一个空格，行尾不留空格。

## 2. 大括号（K&R / 1TBS）

左大括号跟在同一行；`else` / `elseif` / `catch` / `finally` 紧跟在右大括号后面，不换行。

```php
if($player->isOnline()){
	// ...
}elseif($player->isOp()){
	// ...
}else{
	// ...
}

try{
	// ...
}catch(\Throwable $e){
	// ...
}

public function getName() : string{
	return $this->name;
}
```

`switch` 的 `case` 相对 `switch` 缩进一级，分支体再缩进一级：

```php
switch($mode){
	case "alert":
		$this->alert();
		break;
	case "punish":
		$this->punish();
		break;
	default:
		throw new \InvalidArgumentException("unknown mode");
}
```

匿名函数同样写 `function() use ($x){`。

## 3. 声明

- 返回类型写作 `) : type{`（冒号两侧各一个空格，大括号前没有空格）。
- 参数类型与参数名之间留空格：`function send(string $message, int $id = 0)`。
- 成员可见性一律显式写出（`public` / `protected` / `private`）；静态方法写 `public static function`。
- 常量用 `const NAME = ...;`，名字全大写加下划线。
- 新代码用短数组 `[]`；老代码里的 `array(...)` 保持原样，不要顺手改。
- 类成员顺序：常量 → 属性 → 构造方法 → 其他方法；成员之间空一行。
- 属性用 docblock 标注类型：

```php
class ViolationManager{

	const MODE_ALERT = "alert";
	const MODE_PUNISH = "punish";

	/** @var AntiCheat */
	private $antiCheat;

	/** @var string */
	private $mode;

	public function __construct(AntiCheat $antiCheat, string $mode){
		$this->antiCheat = $antiCheat;
		$this->mode = $mode;
	}

	public function getMode() : string{
		return $this->mode;
	}
}
```

## 4. 命名

| 对象 | 规则 | 示例 |
|---|---|---|
| 类 / 接口 / Trait | PascalCase，文件名与类名一致 | `FlyCheck.php` → `class FlyCheck` |
| 方法 / 变量 | camelCase | `getDisplayName()`、`$lastMoveTick` |
| 常量 | UPPER_SNAKE_CASE | `MODE_PUNISH` |
| 协议包 | 包名 + 协议族后缀 | `AddEntityPacketV84` |
| 命令类 | 名称 + `Command` | `GamemodeCommand` |

## 5. 注释与文档

- **文件头许可证块禁止改动**（除作者本人明确要求）。Zenith 自有文件是 GPLv3，上游文件保留各自的 LGPL/GPL 头，见 `AGENTS.md`。
- 类、公开方法用 `/** ... */`；属性用 `/** @var Type */`。
- 新代码允许中文注释和中文 docblock（`src/pocketmine/anticheat/` 是范例）。
- 行内注释用 `//`，与代码之间留一个 Tab 或空格，写在被解释代码的上一行。

## 6. 换行符

- 默认 LF。
- `src/pocketmine/network/protocol/v84/` 下 56 个文件是 **CRLF**，必须保持原样。
- 编辑时优先用 `edit` 工具或按字节读写的脚本；不要用会自动统一换行的编辑器/格式化器处理这些文件。

## 7. 不要做的事

- 不要引入 PHP 8 语法：属性类型、箭头函数 `fn()`、空安全运算符 `?->`、命名参数、联合类型、`match`、构造器属性提升。
- 不要运行 `php-cs-fixer`、`phpcbf`、Prettier 之类工具“统一格式”，它们默认产出 PSR-12（空格缩进、`if (`、`} else {`），会制造上万行无意义 diff。
- 不要在没有功能变更时整文件重排 `use` 语句、对齐 `=>`、给数组补尾逗号。
- 不要在 `docs/` 的参考树里改代码或做格式化。

## 8. 提交前检查

```bash
php -l <改动的文件>          # 语法
php tests/run-tests.php      # 全量离线测试，必须全绿
git diff --stat              # 确认没有意外的大面积格式化
```

一个提交只做一件事：功能修复、重构、格式化分开提交。
