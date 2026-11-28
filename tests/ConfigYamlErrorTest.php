<?php

/*
 *   _  __   __  _   _  ___  _  _  _
 *  | |/ /  / / | | | ||_ _|| \| || |
 *  | ' /  / /  | |_| | | | | .` || |__
 *  |_|\_\ /_/   \___/ |___||_|\_||____|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 */

/**
 * Config YAML 解析错误提示测试。
 *
 * 背景：debug.yml 里 `commands` 少写一个空格时，yaml_parse 只会输出一行
 * 英文 PHP Warning，然后静默回退到默认值，用户看到的是「配置文件没有生效」。
 * 现在 Config 捕获警告并通过 getLoadError() 暴露带文件名/行号的中文提示，
 * 同时保证解析失败时不会把用户写错的文件自动覆盖成默认内容。
 */
class ConfigYamlErrorTest{

	/** @var string|null */
	private static $dir = null;

	public static function run() : array{
		require_once \dirname(__DIR__) . "/src/pocketmine/utils/Utils.php";
		require_once \dirname(__DIR__) . "/src/pocketmine/utils/Config.php";

		self::$dir = \sys_get_temp_dir() . "/zenith-config-yaml-" . \getmypid();
		if(!\is_dir(self::$dir)){
			@\mkdir(self::$dir, 0777, true);
		}

		return \array_merge(
			self::testValidYaml(),
			self::testIndentError(),
			self::testNoPhpWarningLeaks(),
			self::testMappingColonHint(),
			self::testErrorDoesNotOverwriteFile()
		);
	}

	private static function file(string $name) : string{
		return self::$dir . "/" . $name;
	}

	private static function testValidYaml() : array{
		$file = self::file("valid.yml");
		\file_put_contents($file, "debug:\n  level: 2\n");
		$config = new \pocketmine\utils\Config($file, \pocketmine\utils\Config::YAML, [
			"debug" => ["level" => 1]
		]);

		return [
			[
				"valid_yaml_has_no_error",
				$config->getLoadError() === null,
				"合法 YAML 不应产生 loadError"
			],
			[
				"valid_yaml_value_is_loaded",
				$config->getNested("debug.level") === 2,
				"应读到文件里的 level=2"
			]
		];
	}

	private static function testIndentError() : array{
		$file = self::file("bad-indent.yml");
		\file_put_contents($file, "debug:\n  # 注释\n  level: 2\n # 注释\n commands: true\n");
		$config = new \pocketmine\utils\Config($file, \pocketmine\utils\Config::YAML, [
			"debug" => ["level" => 1, "commands" => false]
		]);
		$error = (string) $config->getLoadError();

		return [
			[
				"indent_error_reported",
				$error !== "",
				"缩进不一致时应产生 loadError"
			],
			[
				"indent_error_has_line",
				\strpos($error, "第 5 行") !== false,
				"错误信息应包含行号，实际: " . $error
			],
			[
				"indent_error_has_hint",
				\strpos($error, "缩进") !== false,
				"错误信息应提示缩进，实际: " . $error
			],
			[
				"indent_error_has_file",
				\strpos($error, "bad-indent.yml") !== false,
				"错误信息应包含文件名，实际: " . $error
			],
			[
				"indent_error_falls_back_to_default",
				$config->getNested("debug.commands") === false,
				"解析失败应回退到默认值"
			]
		];
	}

	private static function testNoPhpWarningLeaks() : array{
		$file = self::file("no-warning.yml");
		\file_put_contents($file, "debug:\n  level: 2\n commands: true\n");
		$warnings = [];
		\set_error_handler(function($severity, $message) use (&$warnings){
			$warnings[] = $message;
			return true;
		});
		new \pocketmine\utils\Config($file, \pocketmine\utils\Config::YAML, []);
		\restore_error_handler();

		return [
			[
				"no_php_warning_leaks",
				\count($warnings) === 0,
				"解析失败不应再向控制台抛 PHP Warning，实际: " . \implode(" | ", $warnings)
			]
		];
	}

	private static function testMappingColonHint() : array{
		$file = self::file("bad-colon.yml");
		\file_put_contents($file, "debug:\n  name: a: b\n");
		$config = new \pocketmine\utils\Config($file, \pocketmine\utils\Config::YAML, []);
		$error = (string) $config->getLoadError();

		return [
			[
				"mapping_colon_hint",
				\strpos($error, "冒号") !== false,
				"值里多写冒号时应提示加空格/引号，实际: " . $error
			]
		];
	}

	private static function testErrorDoesNotOverwriteFile() : array{
		$file = self::file("keep-invalid.yml");
		$content = "debug:\n  level: 2\n commands: true\n";
		\file_put_contents($file, $content);
		new \pocketmine\utils\Config($file, \pocketmine\utils\Config::YAML, [
			"debug" => ["level" => 1, "commands" => false]
		]);

		return [
			[
				"parse_error_keeps_original_file",
				\file_get_contents($file) === $content,
				"解析失败时不能把用户写错的文件覆盖成默认值"
			]
		];
	}
}
