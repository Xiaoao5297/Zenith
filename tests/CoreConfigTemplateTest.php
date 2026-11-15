<?php

/**
 * 多语言核心配置模板测试。
 *
 * Dependency note: 只需要 Utils.php + Config.php (Config 只在保存失败时才碰
 * Server / MainLogger)，不启动 PocketMine 运行时，纯读 resources/ 下的 yml。
 *
 * 覆盖: 16 个语言目录与 gameplay.yml / world.yml 都存在；每个文件都能被
 * Config::YAML 解析；键路径集合与非注释行与默认模板完全一致；非 chs 文件的
 * 注释行不含中文字符；deu / jpn 的注释确实与 eng 不同。
 */
class CoreConfigTemplateTest{

	const LANGS = ["chs", "deu", "eng", "fra", "gen", "ita", "jpn", "kor", "nld", "pol", "por", "rus", "spa", "swe", "vie", "zho"];

	const FILES = ["gameplay.yml", "world.yml"];

	public static function run() : array{
		require_once \dirname(__DIR__) . "/src/pocketmine/utils/Utils.php";
		require_once \dirname(__DIR__) . "/src/pocketmine/utils/Config.php";

		return \array_merge(
			self::testLanguageDirectories(),
			self::testTemplateParseable(),
			self::testKeySetsMatch(),
			self::testKeyLinesIdentical(),
			self::testNoChineseComments(),
			self::testTranslationsReplaced()
		);
	}

	private static function resourcesPath() : string{
		return \dirname(__DIR__) . "/src/pocketmine/resources";
	}

	private static function load(string $path){
		return new \pocketmine\utils\Config($path, \pocketmine\utils\Config::YAML);
	}

	/**
	 * 把嵌套配置拍平成排序后的键路径列表；空数组按叶子处理。
	 */
	private static function flattenKeys(array $data, string $prefix = "") : array{
		$keys = [];
		foreach($data as $key => $value){
			$path = $prefix === "" ? (string) $key : $prefix . "." . $key;
			$keys[] = $path;
			if(\is_array($value) && \count($value) > 0){
				$keys = \array_merge($keys, self::flattenKeys($value, $path));
			}
		}
		\sort($keys);

		return $keys;
	}

	/**
	 * 去掉注释行与空行，剩下的就是键/值行 (缩进与顺序都必须一致)。
	 */
	private static function keyLines(string $path) : array{
		$lines = \explode("\n", (string) \file_get_contents($path));
		$result = [];
		foreach($lines as $line){
			if($line === "" or \preg_match('/^\s*#/', $line) === 1){
				continue;
			}
			$result[] = $line;
		}

		return $result;
	}

	/**
	 * 取某个键上方紧邻的注释块 (从键行往上收集连续注释行)。
	 */
	private static function commentAbove(string $path, string $keyLinePattern) : array{
		$lines = \explode("\n", (string) \file_get_contents($path));
		for($i = 0; $i < \count($lines); $i++){
			if(\preg_match($keyLinePattern, $lines[$i]) !== 1){
				continue;
			}
			$comments = [];
			for($j = $i - 1; $j >= 0 and \preg_match('/^\s*#/', $lines[$j]) === 1; $j--){
				\array_unshift($comments, $lines[$j]);
			}

			return $comments;
		}

		return [];
	}

	private static function testLanguageDirectories() : array{
		$results = [];
		$root = self::resourcesPath();
		foreach(self::LANGS as $lang){
			$dir = $root . "/" . $lang;
			$missing = [];
			if(!\is_dir($dir)){
				$missing[] = "目录";
			}else{
				foreach(self::FILES as $file){
					if(!\is_file($dir . "/" . $file)){
						$missing[] = $file;
					}
				}
			}
			$results[] = ["dir_" . $lang, \count($missing) === 0, \count($missing) === 0 ? "存在 gameplay.yml 与 world.yml" : "缺少: " . \implode(", ", $missing)];
		}

		return $results;
	}

	private static function testTemplateParseable() : array{
		$results = [];
		$root = self::resourcesPath();
		foreach(self::LANGS as $lang){
			foreach(self::FILES as $file){
				$path = $root . "/" . $lang . "/" . $file;
				if(!\is_file($path)){
					$results[] = ["parse_" . $lang . "_" . $file, false, "文件不存在"];

					continue;
				}
				try{
					$config = self::load($path);
					$ok = $config->check() === true and \count($config->getAll()) > 0;
					$detail = $ok ? "Config::YAML 解析成功" : "Config::YAML 解析失败";
				}catch(\Throwable $e){
					$ok = false;
					$detail = "解析抛出异常: " . $e->getMessage();
				}
				$results[] = ["parse_" . $lang . "_" . $file, $ok, $detail];
			}
		}

		return $results;
	}

	private static function testKeySetsMatch() : array{
		$results = [];
		$root = self::resourcesPath();
		foreach(self::FILES as $file){
			$expected = self::flattenKeys(self::load($root . "/" . $file)->getAll());
			foreach(self::LANGS as $lang){
				$path = $root . "/" . $lang . "/" . $file;
				if(!\is_file($path)){
					$results[] = ["keys_" . $lang . "_" . $file, false, "文件不存在"];

					continue;
				}
				$actual = self::flattenKeys(self::load($path)->getAll());
				$missing = \array_diff($expected, $actual);
				$extra = \array_diff($actual, $expected);
				$ok = \count($missing) === 0 and \count($extra) === 0;
				$detail = $ok ? \count($expected) . " 个键路径一致" : "缺少: [" . \implode(", ", \array_slice($missing, 0, 5)) . "] 多出: [" . \implode(", ", \array_slice($extra, 0, 5)) . "]";
				$results[] = ["keys_" . $lang . "_" . $file, $ok, $detail];
			}
		}

		return $results;
	}

	private static function testKeyLinesIdentical() : array{
		$results = [];
		$root = self::resourcesPath();
		foreach(self::FILES as $file){
			$expected = self::keyLines($root . "/" . $file);
			foreach(self::LANGS as $lang){
				$path = $root . "/" . $lang . "/" . $file;
				$actual = \is_file($path) ? self::keyLines($path) : [];
				$ok = $actual === $expected;
				$results[] = ["values_" . $lang . "_" . $file, $ok, $ok ? "键名/顺序/值/缩进与默认模板逐行一致" : "键值行与默认模板不一致 (" . \count($actual) . " vs " . \count($expected) . " 行)"];
			}
		}

		return $results;
	}

	private static function testNoChineseComments() : array{
		$results = [];
		$root = self::resourcesPath();
		foreach(self::LANGS as $lang){
			if($lang === "chs"){
				continue;
			}
			$offenders = [];
			foreach(self::FILES as $file){
				$path = $root . "/" . $lang . "/" . $file;
				if(!\is_file($path)){
					$offenders[] = $file . "(缺失)";

					continue;
				}
				$lines = \explode("\n", (string) \file_get_contents($path));
				foreach($lines as $i => $line){
					if(\preg_match('/^\s*#/', $line) !== 1){
						continue;
					}
					if(\preg_match('/[\x{4e00}-\x{9fff}]/u', $line) === 1){
						$offenders[] = $file . ":" . ($i + 1);
					}
				}
			}
			$ok = \count($offenders) === 0;
			$results[] = ["no_chinese_" . $lang, $ok, $ok ? "注释行不含中文字符" : "仍含中文注释: " . \implode(", ", $offenders)];
		}

		return $results;
	}

	private static function testTranslationsReplaced() : array{
		$root = self::resourcesPath();
		$pattern = '/^\s+hunger:\s/';
		$eng = self::commentAbove($root . "/eng/gameplay.yml", $pattern);
		$deu = self::commentAbove($root . "/deu/gameplay.yml", $pattern);
		$jpn = self::commentAbove($root . "/jpn/gameplay.yml", $pattern);
		$chs = self::commentAbove($root . "/chs/gameplay.yml", $pattern);

		return [
			["translation_eng_baseline", \count($eng) === 1 and $eng !== $deu and $eng !== $jpn, "eng 的 player.hunger 注释与 deu / jpn 不同"],
			["translation_deu_hunger", \count($deu) === 1 and $deu[0] !== $eng[0], "deu 的 player.hunger 注释已替换: " . (isset($deu[0]) ? \trim($deu[0]) : "缺失")],
			["translation_jpn_hunger", \count($jpn) === 1 and $jpn[0] !== $eng[0], "jpn 的 player.hunger 注释已替换: " . (isset($jpn[0]) ? \trim($jpn[0]) : "缺失")],
			["translation_chs_hunger", \count($chs) === 1 and \preg_match('/[\x{4e00}-\x{9fff}]/u', $chs[0]) === 1, "chs 保留中文注释: " . (isset($chs[0]) ? \trim($chs[0]) : "缺失")],
		];
	}

}
