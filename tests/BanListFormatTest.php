<?php

use pocketmine\permission\BanEntry;

/**
 * BanList / BanEntry serialization format tests.
 *
 * Dependency note: BanEntry only depends on \DateTime, so it is included
 * directly and tested for real (no PocketMine runtime needed). BanList itself
 * pulls in pocketmine\Server / utils\MainLogger, so it is NOT loaded here;
 * instead the field-splitting regex used by BanList::load() ->
 * BanEntry::fromString() is exercised exactly as written in BanEntry.php:
 *
 *     preg_split('/(?<!\\\\)\|/', trim($str))
 *
 * If the escaping in BanEntry::getString()/fromString() or the regex in
 * BanList.php changes, these tests must be updated to match.
 */
class BanListFormatTest{

	public static function run() : array{
		require_once \dirname(__DIR__) . "/src/pocketmine/permission/BanEntry.php";

		return \array_merge(
			self::testRoundtrip(),
			self::testReasonWithPipes(),
			self::testSourceWithPipes(),
			self::testNameLowercased(),
			self::testSplitSkipsEscapedPipe(),
			self::testBadLineShort(),
			self::testBadLineEmpty(),
			self::testForeverExpiration()
		);
	}

	private static function testRoundtrip() : array{
		$entry = new BanEntry("Steve");
		$entry->setSource("console");
		$entry->setReason("banned by an operator");
		$line = $entry->getString();
		$parsed = BanEntry::fromString($line);

		return [
			["roundtrip_line", self::isString($line) and strpos($line, "|") !== false, "getString() produced a pipe-delimited line"],
			["roundtrip_name", $parsed !== null and $parsed->getName() === "steve", "fromString() kept the (lowercased) name"],
			["roundtrip_source", $parsed !== null and $parsed->getSource() === "console", "fromString() kept the source"],
			["roundtrip_reason", $parsed !== null and $parsed->getReason() === "banned by an operator", "fromString() kept the reason"],
			["roundtrip_created", $parsed !== null and $parsed->getCreated() instanceof \DateTime, "fromString() parsed the creation date"],
			["roundtrip_expires", $parsed !== null and $parsed->getExpires() === null and !$parsed->hasExpired(), "no expiration parsed, not expired"],
		];
	}

	private static function testReasonWithPipes() : array{
		$entry = new BanEntry("pipes");
		$entry->setReason("griefing | stealing");
		$parsed = BanEntry::fromString($entry->getString());

		// getString() escapes | as \|, fromString() must undo it
		return [
			["reason_pipe_escape", strpos($entry->getString(), "griefing \\| stealing") !== false, "getString() escaped the pipe in reason"],
			["reason_pipe_roundtrip", $parsed !== null and $parsed->getReason() === "griefing | stealing", "fromString() restored the pipe in reason"],
		];
	}

	private static function testSourceWithPipes() : array{
		$entry = new BanEntry("sourced");
		$entry->setSource("op|one");
		$parsed = BanEntry::fromString($entry->getString());

		return [
			["source_pipe_roundtrip", $parsed !== null and $parsed->getSource() === "op|one", "escaped pipe in source survives roundtrip"],
		];
	}

	private static function testNameLowercased() : array{
		$entry = new BanEntry("MiXeDCase");
		return [
			["name_lowercased", $entry->getName() === "mixedcase", "BanEntry lowercases names like BanList lookups do"],
		];
	}

	/**
	 * Direct check of the splitting regex copied from BanEntry::fromString():
	 * an escaped \| must not produce a split point, unescaped | must.
	 */
	private static function testSplitSkipsEscapedPipe() : array{
		$parts = \preg_split('/(?<!\\\\)\|/', "a\\|b|c");
		return [
			["split_regex_escaped_pipe", $parts === ["a\\|b", "c"], "preg_split('/(?<!\\\\\\\\)\\|/') kept the escaped pipe intact (regex copied from BanEntry.php)"],
		];
	}

	private static function testBadLineShort() : array{
		return [
			["bad_line_too_short", BanEntry::fromString("x") === null, "fromString() returns null for lines shorter than 2 chars (BanList::load() skips them)"],
		];
	}

	private static function testBadLineEmpty() : array{
		return [
			["bad_line_empty", BanEntry::fromString("") === null, "fromString() returns null for empty lines"],
		];
	}

	private static function testForeverExpiration() : array{
		$entry = new BanEntry("forever");
		$line = $entry->getString();
		// getString() writes "Forever"; feed a line with a literal forever expiry
		$parts = \explode("|", $line);
		$parts[3] = "Forever";
		$parsed = BanEntry::fromString(\implode("|", $parts));

		return [
			["forever_expiration", $parsed !== null and $parsed->getExpires() === null, "fromString() treats 'Forever' as no expiration"],
		];
	}

	private static function isString($value) : bool{
		return \is_string($value);
	}
}
