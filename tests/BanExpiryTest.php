<?php

/**
 * SEC-011 回归测试：BanEntry 过期判定与序列化。
 *
 * 修复前 hasExpired() 会把 expirationDate 改写成 null（副作用），
 * 导致"限时封禁"在第一次判定后变成永久有效/失效错乱。本测试锁定：
 *  - hasExpired 纯查询、可重复调用、不修改 expirationDate；
 *  - 未来时间未过期、过去时间已过期、无过期时间永不过期；
 *  - getString/fromString 往返保留字段与转义（| 转义、换行转空格）；
 *  - Forever / 空过期字段解析为无过期；
 *  - 过短或空行返回 null；名字统一小写。
 *
 * 依赖：pocketmine\permission\BanEntry（仅依赖 \DateTime，可独立加载）。
 */

use pocketmine\permission\BanEntry;

require_once dirname(__DIR__) . "/src/pocketmine/permission/BanEntry.php";

class BanExpiryTest{

	public static function run() : array{
		return array_merge(
			self::testExpiryBasics(),
			self::testNoSideEffect(),
			self::testParseExpiry(),
			self::testRoundtrip(),
			self::testInvalidInput()
		);
	}

	private static function makeEntry($expires = null){
		$entry = new BanEntry("TestPlayer");
		$entry->setSource("console");
		$entry->setReason("unit test");
		if($expires !== null){
			$entry->setExpires($expires);
		}
		return $entry;
	}

	private static function testExpiryBasics() : array{
		$out = [];

		$entry = self::makeEntry();
		$out[] = ["no_expiry_never_expires", $entry->hasExpired() === false, "expirationDate = null"];

		$entry = self::makeEntry(new \DateTime("+1 hour"));
		$out[] = ["future_not_expired", $entry->hasExpired() === false, "+1 hour"];

		$entry = self::makeEntry(new \DateTime("-1 hour"));
		$out[] = ["past_expired", $entry->hasExpired() === true, "-1 hour"];

		// 非 DateTime 参数必须被忽略，不能把字段搞坏
		$entry = self::makeEntry();
		$entry->setExpires("not a date");
		$out[] = ["set_expires_ignores_non_datetime", $entry->getExpires() === null and $entry->hasExpired() === false, "string argument ignored"];

		return $out;
	}

	private static function testNoSideEffect() : array{
		$entry = self::makeEntry(new \DateTime("-2 hours"));
		$before = $entry->getExpires()->format(BanEntry::$format);

		$first = $entry->hasExpired();
		$second = $entry->hasExpired();
		$third = $entry->hasExpired();

		$expires = $entry->getExpires();
		$after = $expires === null ? "(null)" : $expires->format(BanEntry::$format);

		return [
			["has_expired_repeatable", $first === true and $second === true and $third === true, "3 consecutive calls"],
			["has_expired_no_mutation", $expires instanceof \DateTime and $before === $after, "before=" . $before . " after=" . $after]
		];
	}

	private static function testParseExpiry() : array{
		$out = [];
		$past = (new \DateTime("-3 hours"))->format(BanEntry::$format);

		$forever = BanEntry::fromString("Banned|2020-01-01 00:00:00 +0000|console|Forever|griefing");
		$out[] = ["parse_forever", $forever instanceof BanEntry and $forever->getExpires() === null and $forever->hasExpired() === false, "Forever keyword"];

		$empty = BanEntry::fromString("Banned|2020-01-01 00:00:00 +0000|console||griefing");
		$out[] = ["parse_empty_expiry", $empty instanceof BanEntry and $empty->getExpires() === null and $empty->hasExpired() === false, "empty field"];

		$expired = BanEntry::fromString("Banned|2020-01-01 00:00:00 +0000|console|" . $past . "|griefing");
		$out[] = ["parse_past_expiry", $expired instanceof BanEntry and $expired->hasExpired() === true, "expires=" . $past];

		return $out;
	}

	private static function testRoundtrip() : array{
		$entry = self::makeEntry(new \DateTime("+2 days"));
		$entry->setSource("console|pipe");
		$entry->setReason("line1\nline2|tail");

		$parsed = BanEntry::fromString($entry->getString());
		$out = [];

		$out[] = ["roundtrip_name", $parsed->getName() === "testplayer", $parsed->getName()];
		$out[] = ["roundtrip_source_pipe", $parsed->getSource() === "console|pipe", $parsed->getSource()];
		$out[] = ["roundtrip_reason_escaping", $parsed->getReason() === "line1 line2|tail", $parsed->getReason()];
		$out[] = [
			"roundtrip_expires",
			$parsed->getExpires() instanceof \DateTime
				and $parsed->getExpires()->format(BanEntry::$format) === $entry->getExpires()->format(BanEntry::$format)
				and $parsed->hasExpired() === false,
			(string) $parsed->getExpires()->format(BanEntry::$format)
		];

		return $out;
	}

	private static function testInvalidInput() : array{
		return [
			["parse_empty_returns_null", BanEntry::fromString("") === null, "empty string"],
			["parse_single_char_null", BanEntry::fromString("x") === null, "length < 2"],
			["name_lowercased", (new BanEntry("MiXeD"))->getName() === "mixed", "MiXeD -> mixed"]
		];
	}
}
