<?php

/**
 * Config properties 编码健壮性测试。
 *
 * Dependency note: Config 只依赖同命名空间的 Utils（save() 走 atomicWriteFile），
 * Server / FileWriteTask / MainLogger 只在异步或异常分支里用到，测试不会走到，
 * 所以这里直接 require Utils.php + Config.php，不需要启动 PocketMine 运行时。
 *
 * 覆盖：UTF-8 BOM、CRLF、GBK 旧文件、GBK 乱码修复、\uXXXX 与代理对、常用转义、
 * 纯 ASCII 不重写、非法 UTF-8 回退、码位编码、内容未变时跳过写入、解析失败不覆盖原文件。
 */
class ConfigEncodingTest{

	/** @var string|null */
	private static $dir = null;

	public static function run() : array{
		require_once \dirname(__DIR__) . "/src/pocketmine/utils/Utils.php";
		require_once \dirname(__DIR__) . "/src/pocketmine/utils/Config.php";

		self::$dir = \sys_get_temp_dir() . "/zenith-config-encoding-" . \getmypid();
		if(!\is_dir(self::$dir)){
			@\mkdir(self::$dir, 0777, true);
		}

		$results = \array_merge(
			self::testUtf8Bom(),
			self::testUtf8BomWithCrlf(),
			self::testGbkLegacyFile(),
			self::testGbkMojibakeRepair(),
			self::testValidCjkNotMojibake(),
			self::testUnicodeEscape(),
			self::testUnicodeSurrogatePair(),
			self::testCommonEscapes(),
			self::testPureAsciiNotRewritten(),
			self::testInvalidUtf8Fallback(),
			self::testEncodeUnicodeCodepoint(),
			self::testUnchangedContentSkip(),
			self::testUnparseableFileNotOverwritten(),
			self::testUtf16BomFile()
		);

		self::cleanup();

		return $results;
	}

	/**
	 * 写一个临时 properties 文件并返回路径。
	 */
	private static function writeFile(string $name, string $content) : string{
		$path = self::$dir . "/" . $name;
		\file_put_contents($path, $content);

		return $path;
	}

	/**
	 * @return \pocketmine\utils\Config
	 */
	private static function load(string $path, array $default = []){
		return new \pocketmine\utils\Config($path, \pocketmine\utils\Config::PROPERTIES, $default);
	}

	private static function hasMultibyteSupport() : bool{
		return \function_exists("iconv") or \function_exists("mb_convert_encoding");
	}

	private static function testUtf8Bom() : array{
		$path = self::writeFile("bom.properties", "\xEF\xBB\xBF" . "server-port=19132\r\n");
		$config = self::load($path);

		return [
			["bom_stripped_key", $config->get("server-port") === "19132", "BOM 后的键值对被正确解析"],
			["bom_file_rewritten", \substr(\file_get_contents($path), 0, 3) !== "\xEF\xBB\xBF", "BOM 被去掉后写回磁盘"],
		];
	}

	private static function testUtf8BomWithCrlf() : array{
		$path = self::writeFile("bom-crlf.properties", "\xEF\xBB\xBF" . "white-list=on\r\nmotd=hello\r\n");
		$config = self::load($path);

		return [
			["bom_crlf_bool", $config->get("white-list") === true, "on 被解析为布尔 true"],
			["bom_crlf_value", $config->get("motd") === "hello", "CRLF 行尾的第二行被正确解析"],
			["bom_crlf_no_stray_cr", $config->get("motd") !== "hello\r", "值里没有残留 \\r"],
		];
	}

	private static function testGbkLegacyFile() : array{
		if(!self::hasMultibyteSupport()){
			return [["gbk_file", true, "跳过：没有 iconv / mbstring"]];
		}

		$raw = \function_exists("mb_convert_encoding")
			? \mb_convert_encoding("你好=世界\r\n", "GBK", "UTF-8")
			: @\iconv("UTF-8", "GBK", "你好=世界\r\n");
		if(!\is_string($raw) or $raw === ""){
			return [["gbk_file", true, "跳过：当前环境无法生成 GBK 字节"]];
		}

		$path = self::writeFile("gbk.properties", $raw);
		$config = self::load($path);
		$onDisk = \file_get_contents($path);

		return [
			["gbk_key", $config->get("你好") === "世界", "GBK 键和值都被重解码为 UTF-8"],
			["gbk_file_now_utf8", \preg_match('//u', $onDisk) === 1, "修复后的文件是合法 UTF-8"],
			["gbk_file_rewritten", \strpos($onDisk, "你好=世界") !== false, "修复结果被自动写回磁盘"],
		];
	}

	private static function testGbkMojibakeRepair() : array{
		if(!\function_exists("mb_convert_encoding")){
			return [["mojibake_repair", true, "跳过：没有 mbstring"]];
		}

		// UTF-8 的"测试"被当成 GB18030 读出来的经典乱码：娴嬭瘯
		$mojibake = \mb_convert_encoding("测试", "UTF-8", "GB18030");
		if($mojibake === "测试" or $mojibake === ""){
			return [["mojibake_repair", true, "跳过：当前 mbstring 无法复现乱码样本"]];
		}

		$path = self::writeFile("mojibake.properties", "greet=" . $mojibake . "\r\n");
		$config = self::load($path);

		return [
			["mojibake_repaired", $config->get("greet") === "测试", "合法 UTF-8 的 GBK 乱码被修复为原字符"],
		];
	}

	private static function testValidCjkNotMojibake() : array{
		$results = [];
		$cases = ["一", "专", "业", "你好世界", "服务器"];
		foreach($cases as $i => $value){
			$path = self::writeFile("valid-cjk-" . $i . ".properties", "motd=" . $value . "\r\n");
			$config = self::load($path);
			$onDisk = \file_get_contents($path);
			$results[] = ["valid_cjk_" . $i . "_value", $config->get("motd") === $value, "合法中文 " . $value . " 不被当成乱码"];
			$results[] = ["valid_cjk_" . $i . "_disk", \strpos($onDisk, "motd=" . $value) !== false, "磁盘内容未被改写"];
		}
		return $results;
	}

	private static function testUnicodeEscape() : array{
		$path = self::writeFile("escape-u.properties", "name=\\u4e2d\\u6587\r\n");
		$config = self::load($path);

		return [
			["unicode_escape_value", $config->get("name") === "中文", "\\u4e2d\\u6587 解码为 中文"],
		];
	}

	private static function testUnicodeSurrogatePair() : array{
		$path = self::writeFile("escape-u-pair.properties", "emoji=\\uD83D\\uDE00\r\n");
		$config = self::load($path);
		$value = $config->get("emoji");

		return [
			["unicode_surrogate_pair", $value === "\xF0\x9F\x98\x80", "代理对被合并成一个 UTF-8 码位（😀）"],
		];
	}

	private static function testCommonEscapes() : array{
		$path = self::writeFile("escapes.properties", "k=line1\\nline2\\tend\\\\tail\r\nbad=\\uZZZZ\r\n");
		$config = self::load($path);

		return [
			["escape_newline_tab", $config->get("k") === "line1\nline2\tend\\tail", "\\n \\t \\\\ 被还原，且不吞掉后续字符"],
			["escape_invalid_unicode", $config->get("bad") === "\\uZZZZ", "非法 \\u 转义原样保留"],
		];
	}

	private static function testPureAsciiNotRewritten() : array{
		$content = "#Properties Config file\r\n#Mon Jan 1 00:00:00 UTC 2024\r\na=1\r\nb=two\r\n";
		$path = self::writeFile("ascii.properties", $content);
		$config = self::load($path, ["a" => 1, "b" => "two"]);

		return [
			["ascii_values", $config->get("a") === "1" and $config->get("b") === "two", "纯 ASCII 内容解析正常"],
			["ascii_file_untouched", \file_get_contents($path) === $content, "内容没有变化时不重写文件"],
		];
	}

	private static function testInvalidUtf8Fallback() : array{
		$path = self::writeFile("invalid-utf8.properties", "name=\xC3\x28\r\nok=1\r\n");
		$config = self::load($path);

		return [
			["invalid_utf8_still_correct", $config->check() === true, "非法 UTF-8 不会让 load 失败"],
			["invalid_utf8_other_keys", $config->get("ok") === "1", "同一文件的其它键仍能解析"],
			["invalid_utf8_is_utf8_after_load", \preg_match('//u', (string) \file_get_contents($path)) === 1, "写回后的文件是合法 UTF-8"],
		];
	}

	private static function testEncodeUnicodeCodepoint() : array{
		$method = new \ReflectionMethod(\pocketmine\utils\Config::class, "encodeUnicodeCodepoint");
		$method->setAccessible(true);
		$config = self::load(self::writeFile("codepoint.properties", "a=1\r\n"));

		return [
			["codepoint_bmp", $method->invoke($config, 0x4E2D) === "中", "0x4E2D 编码为 中"],
			["codepoint_astral", $method->invoke($config, 0x1F600) === "\xF0\x9F\x98\x80", "0x1F600 编码为 4 字节 UTF-8"],
			["codepoint_ascii", $method->invoke($config, 0x41) === "A", "0x41 编码为 A"],
		];
	}

	private static function testUnchangedContentSkip() : array{
		$content = "#Properties Config file\r\n#Mon Jan 1 00:00:00 UTC 2024\r\na=1\r\n";
		$path = self::writeFile("skip-save.properties", $content);
		$config = self::load($path);
		$before = \md5_file($path);
		$config->save();
		$after = \md5_file($path);

		return [
			["unchanged_content_skip", $before === $after, "键值部分未变时 save() 不重写文件（注释头时间戳也不刷新）"],
		];
	}

	private static function testUnparseableFileNotOverwritten() : array{
		$content = "=orphan\r\n";
		$path = self::writeFile("orphan.properties", $content);
		$config = self::load($path, ["a" => 1]);

		return [
			["unparseable_file_kept", \file_get_contents($path) === $content, "解析不出条目时不拿默认值覆盖原文件"],
			["unparseable_defaults_applied", $config->get("a") === 1, "默认值仍然在内存中生效"],
		];
	}

	private static function testUtf16BomFile() : array{
		if(!\function_exists("mb_convert_encoding")){
			return [["utf16_bom", true, "跳过：没有 mbstring"]];
		}

		$raw = "\xFF\xFE" . \mb_convert_encoding("键=值\r\n", "UTF-16LE", "UTF-8");
		$path = self::writeFile("utf16.properties", $raw);
		$config = self::load($path);

		return [
			["utf16_bom_value", $config->get("键") === "值", "UTF-16LE BOM 文件被转换为 UTF-8 后解析"],
		];
	}

	private static function cleanup(){
		if(self::$dir === null or !\is_dir(self::$dir)){
			return;
		}
		foreach((array) \glob(self::$dir . "/*") as $file){
			@\unlink($file);
		}
		@\rmdir(self::$dir);
	}

}
