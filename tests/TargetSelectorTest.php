<?php

/**
 * TargetSelector 纯解析/校验测试（不启动服务器）。
 *
 * 覆盖从 InCore-II 移植到 Zenith 的选择器语法层：
 *  - @a/@p/@r/@e/@s/@initiator 的 token 识别；
 *  - 参数切分（scores 的花括号内不切分）与参数表构建；
 *  - 未知参数 / type+family 冲突 / 非法 scores 的报错；
 *  - Zenith 无对应实现的参数（scores/tag/family）必须被识别为“不支持”；
 *  - 坐标偏移、取反前缀、游戏模式 token 的解析。
 *
 * 依赖：pocketmine\command\TargetSelector（仅静态方法，不触碰 Server/Player 实例）。
 */

use pocketmine\command\TargetSelector;

require_once dirname(__DIR__) . "/src/pocketmine/command/TargetSelector.php";

class TargetSelectorTest{

	public static function run() : array{
		return array_merge(
			self::testSelectorToken(),
			self::testBasicParsing(),
			self::testArgumentSplitting(),
			self::testUnknownArgument(),
			self::testConflictingArguments(),
			self::testUnsupportedArguments(),
			self::testScoresSyntax(),
			self::testGamemodeTokens(),
			self::testCoordinateValues(),
			self::testNegatedValues(),
			self::testMalformedSelectors()
		);
	}

	private static function testSelectorToken() : array{
		$out = [];
		$out[] = ["selector_all_players", TargetSelector::isSelectorToken("@a"), "@a"];
		$out[] = ["selector_nearest", TargetSelector::isSelectorToken("@p"), "@p"];
		$out[] = ["selector_random", TargetSelector::isSelectorToken("@r"), "@r"];
		$out[] = ["selector_all_entities", TargetSelector::isSelectorToken("@e"), "@e"];
		$out[] = ["selector_self", TargetSelector::isSelectorToken("@s"), "@s"];
		$out[] = ["selector_initiator", TargetSelector::isSelectorToken("@initiator"), "@initiator"];
		$out[] = ["selector_with_args", TargetSelector::isSelectorToken("@e[type=zombie]"), "@e[type=zombie]"];
		$out[] = ["selector_plain_name_rejected", !TargetSelector::isSelectorToken("Steve"), "plain player name"];
		$out[] = ["selector_unknown_tag_rejected", !TargetSelector::isSelectorToken("@x"), "@x"];
		$out[] = ["selector_non_string_rejected", !TargetSelector::isSelectorToken(null), "null input"];
		return $out;
	}

	private static function testBasicParsing() : array{
		$out = [];

		$parsed = TargetSelector::parseSelector("@a");
		$out[] = ["parse_type", $parsed["type"] === "a", "type=" . $parsed["type"]];
		$out[] = ["parse_no_arguments", count($parsed["arguments"]) === 0, "args=" . count($parsed["arguments"])];

		$parsed = TargetSelector::parseSelector("@p[name=Steve,r=10]");
		$out[] = ["parse_argument_name", isset($parsed["arguments"]["name"]) && $parsed["arguments"]["name"][0] === "Steve", "name=Steve"];
		$out[] = ["parse_argument_radius", isset($parsed["arguments"]["r"]) && $parsed["arguments"]["r"][0] === "10", "r=10"];
		$out[] = ["parse_argument_count", count($parsed["arguments"]) === 2, "args=" . count($parsed["arguments"])];

		$parsed = TargetSelector::parseSelector("@a[]");
		$out[] = ["parse_empty_brackets", $parsed["type"] === "a" && count($parsed["arguments"]) === 0, "@a[]"];

		return $out;
	}

	private static function testArgumentSplitting() : array{
		$out = [];

		$split = TargetSelector::splitArguments("name=Steve,r=10,m=!creative");
		$out[] = ["split_plain", count($split) === 3, implode("|", $split)];

		$split = TargetSelector::splitArguments("scores={a=1..5,b=!3},r=10");
		$out[] = ["split_respects_braces", count($split) === 2, implode("|", $split)];
		$out[] = ["split_keeps_scores_body", $split[0] === "scores={a=1..5,b=!3}", $split[0]];

		$arguments = TargetSelector::parseArgumentMap("type=zombie,type=skeleton");
		$out[] = ["parse_argument_map_multi_value", isset($arguments["type"]) && count($arguments["type"]) === 2, "type values=" . count($arguments["type"])];

		return $out;
	}

	private static function testUnknownArgument() : array{
		return [
			["unknown_argument_rejected", self::throws(function(){
				TargetSelector::parseArgumentMap("nope=1");
			}), "nope=1"],
			["unknown_argument_message", self::throwsMessage(function(){
				TargetSelector::parseArgumentMap("nope=1");
			}, "Unknown selector argument"), "message contains 'Unknown selector argument'"]
		];
	}

	private static function testConflictingArguments() : array{
		return [
			["type_and_family_conflict", self::throwsMessage(function(){
				TargetSelector::parseSelector("@e[type=zombie,family=undead]");
			}, "cannot be used together"), "type + family"]
		];
	}

	private static function testUnsupportedArguments() : array{
		$out = [];

		$parsed = TargetSelector::parseSelector("@a[scores={obj=1..5}]");
		$unsupported = TargetSelector::unsupportedArguments($parsed["arguments"]);
		$out[] = ["scores_parsed_but_unsupported", isset($unsupported["scores"]), "reason=" . (isset($unsupported["scores"]) ? $unsupported["scores"] : "none")];

		$parsed = TargetSelector::parseSelector("@e[tag=quest]");
		$unsupported = TargetSelector::unsupportedArguments($parsed["arguments"]);
		$out[] = ["tag_parsed_but_unsupported", isset($unsupported["tag"]), "reason=" . (isset($unsupported["tag"]) ? $unsupported["tag"] : "none")];

		$parsed = TargetSelector::parseSelector("@e[family=undead]");
		$unsupported = TargetSelector::unsupportedArguments($parsed["arguments"]);
		$out[] = ["family_parsed_but_unsupported", isset($unsupported["family"]), "reason=" . (isset($unsupported["family"]) ? $unsupported["family"] : "none")];

		$parsed = TargetSelector::parseSelector("@a[r=10,m=!creative]");
		$out[] = ["supported_arguments_not_flagged", count(TargetSelector::unsupportedArguments($parsed["arguments"])) === 0, "r + m"];

		return $out;
	}

	private static function testScoresSyntax() : array{
		$out = [];

		$conditions = TargetSelector::parseScoreConditions("{obj=1..5,name=!3}");
		$out[] = ["scores_range", $conditions["obj"]["condition"] === "1..5" && !$conditions["obj"]["negated"], "obj=1..5"];
		$out[] = ["scores_negated", $conditions["name"]["condition"] === "3" && $conditions["name"]["negated"], "name=!3"];
		$out[] = ["scores_empty_object", count(TargetSelector::parseScoreConditions("{}")) === 0, "{}"];

		$out[] = ["scores_missing_braces", self::throwsMessage(function(){
			TargetSelector::parseScoreConditions("obj=1");
		}, "Malformed scores"), "obj=1"];

		$out[] = ["scores_missing_equals", self::throwsMessage(function(){
			TargetSelector::parseScoreConditions("{obj}");
		}, "Malformed scores"), "{obj}"];

		return $out;
	}

	private static function testGamemodeTokens() : array{
		$out = [];
		$tokens = [
			"s" => 0,
			"survival" => 0,
			"0" => 0,
			"c" => 1,
			"creative" => 1,
			"1" => 1,
			"a" => 2,
			"adventure" => 2,
			"2" => 2,
			"spectator" => 3,
			"3" => 3
		];
		foreach($tokens as $token => $expected){
			$actual = TargetSelector::parseGamemodeToken($token, 0);
			$out[] = ["gamemode_" . $token, $actual === $expected, $token . " => " . $actual];
		}

		$out[] = ["gamemode_default_fallback", TargetSelector::parseGamemodeToken("default", 1) === 1, "default => 1"];
		$out[] = ["gamemode_unknown_rejected", self::throwsMessage(function(){
			TargetSelector::parseGamemodeToken("nope", 0);
		}, "Unknown gamemode token"), "nope"];

		return $out;
	}

	private static function testCoordinateValues() : array{
		return [
			["coordinate_absolute", TargetSelector::parseCoordinateValue("12.5", 100.0) === 12.5, "12.5"],
			["coordinate_tilde_only", TargetSelector::parseCoordinateValue("~", 100.0) === 100.0, "~"],
			["coordinate_tilde_offset", TargetSelector::parseCoordinateValue("~5", 100.0) === 105.0, "~5"],
			["coordinate_tilde_negative", TargetSelector::parseCoordinateValue("~-5", 100.0) === 95.0, "~-5"]
		];
	}

	private static function testNegatedValues() : array{
		return [
			["negated_true", TargetSelector::isNegatedValue("!creative"), "!creative"],
			["negated_false", !TargetSelector::isNegatedValue("creative"), "creative"],
			["negated_empty", !TargetSelector::isNegatedValue(""), "empty string"]
		];
	}

	private static function testMalformedSelectors() : array{
		return [
			["malformed_missing_brace", self::throwsMessage(function(){
				TargetSelector::parseSelector("@a[r=10");
			}, "Malformed entity selector token"), "@a[r=10"],
			["malformed_type_token", self::throwsMessage(function(){
				TargetSelector::parseSelector("@z");
			}, "Malformed entity selector token"), "@z"],
			["multiple_values_rejected", self::throwsMessage(function(){
				TargetSelector::parseSelector("@a[scores={a=1},scores={b=2}]");
			}, "Multiple arguments are not allowed"), "scores twice"]
		];
	}

	private static function throws(callable $callback) : bool{
		try{
			$callback();
		}catch(\InvalidArgumentException $e){
			return true;
		}
		return false;
	}

	private static function throwsMessage(callable $callback, $needle) : bool{
		try{
			$callback();
		}catch(\InvalidArgumentException $e){
			return strpos($e->getMessage(), $needle) !== false;
		}
		return false;
	}
}
