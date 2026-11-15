<?php

/**
 * 语言文件目录布局测试（lang/<code>/messages.ini + lang/<code>/installer.ini）。
 *
 * Dependency note: BaseLang 只依赖 \pocketmine\PATH 常量，不需要 Server/Logger；
 * InstallerLang 是纯 PHP 类，直接 require 即可。两者都能脱离 PocketMine 运行时实例化，
 * 所以这里不启动服务器、不引入事件类（TextContainer 只出现在未调用的方法签名里）。
 */
class LangPathTest{

	/** @var string */
	private static $root;

	public static function run() : array{
		self::$root = \dirname(__DIR__);

		if(!\defined("pocketmine\\PATH")){
			\define("pocketmine\\PATH", self::$root . "/");
		}

		require_once self::$root . "/src/pocketmine/lang/BaseLang.php";
		require_once self::$root . "/src/pocketmine/wizard/InstallerLang.php";

		return \array_merge(
			self::testRepresentativeFiles(),
			self::testLayoutCoverage(),
			self::testOldDirectoriesRemoved(),
			self::testBaseLangTranslation(),
			self::testBaseLangFallback(),
			self::testAvailableLanguages(),
			self::testMessagesIniParses(),
			self::testInstallerLang()
		);
	}

	private static function langDir(string $code) : string{
		return self::$root . "/src/pocketmine/lang/" . $code;
	}

	private static function testRepresentativeFiles() : array{
		$results = [];
		foreach(["eng", "chs", "zho"] as $code){
			$dir = self::langDir($code);
			$results[] = [$code . "_messages_exists", \is_file($dir . "/messages.ini"), $code . "/messages.ini"];
			$results[] = [$code . "_installer_exists", \is_file($dir . "/installer.ini"), $code . "/installer.ini"];
			$results[] = [$code . "_files_nonempty", \is_file($dir . "/messages.ini") and \filesize($dir . "/messages.ini") > 0
				and \is_file($dir . "/installer.ini") and \filesize($dir . "/installer.ini") > 0, "两个文件都非空"];
		}

		return $results;
	}

	private static function testLayoutCoverage() : array{
		$codes = [];
		foreach((array) \glob(self::$root . "/src/pocketmine/lang/*/messages.ini") as $file){
			$codes[] = \basename(\dirname($file));
		}
		\sort($codes);

		$missing = [];
		foreach($codes as $code){
			if(!\is_file(self::langDir($code) . "/installer.ini")){
				$missing[] = $code;
			}
		}

		return [
			["language_count", \count($codes) === 16, "发现 " . \count($codes) . " 个语言目录"],
			["installer_parity", \count($missing) === 0, "缺少 installer.ini 的语言：" . \implode(",", $missing)],
			["codes_sorted", $codes === ["chs", "deu", "eng", "fra", "gen", "ita", "jpn", "kor", "nld", "pol", "por", "rus", "spa", "swe", "vie", "zho"], \implode(",", $codes)],
		];
	}

	private static function testOldDirectoriesRemoved() : array{
		return [
			["old_locale_dir_gone", !\is_dir(self::$root . "/src/pocketmine/lang/locale"), "lang/locale 已删除"],
			["old_installer_dir_gone", !\is_dir(self::$root . "/src/pocketmine/lang/Installer"), "lang/Installer 已删除"],
			["no_stray_ini_in_lang_root", \count((array) \glob(self::$root . "/src/pocketmine/lang/*.ini")) === 0, "lang 根目录不再有 .ini"],
		];
	}

	private static function testBaseLangTranslation() : array{
		$eng = new \pocketmine\lang\BaseLang("eng");
		$chs = new \pocketmine\lang\BaseLang("chs");

		$engStart = $eng->get("pocketmine.server.start");
		$chsStart = $chs->get("pocketmine.server.start");

		return [
			["eng_get_lang", $eng->getLang() === "eng", "getLang() = " . $eng->getLang()],
			["eng_translation", \strpos($engStart, "Starting Minecraft") !== false, "pocketmine.server.start = " . $engStart],
			["eng_name", $eng->getName() === "English", "language.name = " . $eng->getName()],
			["chs_translation", \strpos($chsStart, "正在启动") !== false, "chs pocketmine.server.start = " . $chsStart],
			["chs_translation_not_raw_key", $chsStart !== "pocketmine.server.start", "取到的是译文而不是键名"],
			["translate_string_params", \strpos($eng->translateString("pocketmine.server.start", ["1.2.3"]), "1.2.3") !== false, "translateString 参数替换"],
		];
	}

	private static function testBaseLangFallback() : array{
		$unknown = new \pocketmine\lang\BaseLang("xx");

		return [
			["fallback_lang_name", $unknown->getLang() === "eng", "未知语言回退到 eng（getLang() = " . $unknown->getLang() . "）"],
			["fallback_translation", \strpos($unknown->get("pocketmine.server.start"), "Starting Minecraft") !== false, "未知语言仍能取到 eng 译文"],
			["fallback_constant", \pocketmine\lang\BaseLang::getFallbackLanguage() === "eng", "FALLBACK_LANGUAGE = eng"],
		];
	}

	private static function testAvailableLanguages() : array{
		$available = \pocketmine\lang\BaseLang::getAvailableLanguages();

		return [
			["available_is_array", \is_array($available), "返回数组"],
			["available_contains_eng", \in_array("eng", $available, true), "包含 eng"],
			["available_contains_chs", \in_array("chs", $available, true), "包含 chs"],
			["available_contains_zho", \in_array("zho", $available, true), "包含 zho"],
			["available_count", \count($available) === 16, "共 " . \count($available) . " 个可用语言"],
			["available_unique", \count($available) === \count(\array_unique($available)), "没有重复项"],
		];
	}

	private static function testMessagesIniParses() : array{
		$results = [];
		foreach(["eng", "chs", "zho"] as $code){
			$path = self::langDir($code) . "/messages.ini";
			$lines = \explode("\n", (string) \file_get_contents($path));

			$pairs = 0;
			$bad = [];
			foreach($lines as $line){
				$line = \trim($line);
				if($line === "" or $line[0] === "#"){
					continue;
				}
				$t = \explode("=", $line);
				if(\count($t) < 2 or \trim($t[0]) === ""){
					$bad[] = $line;
					continue;
				}
				++$pairs;
			}

			$results[] = [$code . "_ini_parses", \count($bad) === 0, "坏行：" . \implode(" | ", \array_slice($bad, 0, 3))];
			$results[] = [$code . "_ini_key_count", $pairs > 300, $pairs . " 个键值对"];
		}

		return $results;
	}

	private static function testInstallerLang() : array{
		$eng = new \pocketmine\wizard\InstallerLang("eng");
		$chs = new \pocketmine\wizard\InstallerLang("chs");

		$engWelcome = $eng->get("welcome_to_pocketmine");
		$chsWelcome = $chs->get("welcome_to_pocketmine");

		$ref = new \ReflectionProperty(\pocketmine\wizard\InstallerLang::class, "langfile");
		$ref->setAccessible(true);
		$engFile = $ref->getValue($eng);
		$chsFile = $ref->getValue($chs);

		$prefixDone = new \pocketmine\wizard\InstallerLang("ch");

		return [
			["installer_eng_translation", \strpos($engWelcome, "Welcome to PocketMine") !== false, "eng welcome = " . \substr($engWelcome, 0, 40)],
			["installer_chs_translation", \strpos($chsWelcome, "欢迎") !== false, "chs welcome 取到译文"],
			["installer_eng_path", $engFile === self::$root . "/src/pocketmine/lang/eng/installer.ini", "解析到 " . $engFile],
			["installer_chs_path", $chsFile === self::$root . "/src/pocketmine/lang/chs/installer.ini", "解析到 " . $chsFile],
			["installer_prefix_scan", $prefixDone->getLang() === "chs" and \strpos($prefixDone->get("welcome_to_pocketmine"), "欢迎") !== false, "前缀 ch 命中 chs（DirectoryIterator 扫描新布局）"],
		];
	}

}
