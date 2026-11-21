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
 * 实体元数据（synched entity data）编码测试。
 *
 * 背景：Zenith 内部把 64 位长整型记为 Entity::DATA_TYPE_LONG = 8，
 * 但线上元数据头只有 3 bit 的 type 字段（0..7），LONG 的线上编号是 7。
 * 之前直接把 8 左移 5 位写进 header，chr() 把 0x100 截成 0，
 * 客户端会当成 BYTE 解析，导致这条长整型之后的元数据全部错位
 * （拴绳、箭的 shooter id、以及任何后设置的属性都会丢失）。
 */
spl_autoload_register(function($class){
	$prefix = "pocketmine\\";
	if(strpos($class, $prefix) !== 0){
		return;
	}
	$path = dirname(__DIR__) . "/src/pocketmine/" . str_replace("\\", "/", substr($class, strlen($prefix))) . ".php";
	if(is_file($path)){
		require_once $path;
	}
});

class MetadataEncodingTest{

	public static function run() : array{
		$results = [];

		$longOnly = \pocketmine\utils\Binary::writeMetadata([
			23 => [\pocketmine\entity\Entity::DATA_TYPE_LONG, -1]
		]);

		$results[] = self::check(
			"long_header_uses_wire_type_7",
			$longOnly !== "" and ord($longOnly[0]) === (7 << 5 | 23),
			"索引 23 的 LONG 头应为 0xF7（wire type 7），实际 0x" . dechex(ord($longOnly[0]))
		);
		$results[] = self::check(
			"long_payload_is_eight_bytes",
			substr($longOnly, 1, 8) === str_repeat("\xff", 8),
			"LONG 必须写满 8 字节"
		);
		$results[] = self::check(
			"long_terminator",
			substr($longOnly, 9) === "\x7f",
			"元数据应以 0x7F 结束"
		);

		// 内部 type 8 与已被 MetadataMapping 转成线上 7 的输入必须产生完全相同的字节。
		$longWire = \pocketmine\utils\Binary::writeMetadata([
			23 => [7, -1]
		]);
		$results[] = self::check(
			"wire_and_internal_long_match",
			$longWire === $longOnly,
			"内部 8 与线上 7 的编码结果应一致"
		);

		// 关键回归：长整型之后的属性不能被吞掉。
		$encoded = \pocketmine\utils\Binary::writeMetadata([
			23 => [\pocketmine\entity\Entity::DATA_TYPE_LONG, 123456789],
			30 => [\pocketmine\entity\Entity::DATA_TYPE_BYTE, 7]
		]);
		$decoded = \pocketmine\utils\Binary::readMetadata($encoded, true);
		// readMetadata($value, true) 返回 [值, 类型]，所以下标 0 是值、下标 1 是类型。
		$results[] = self::check(
			"long_round_trip",
			isset($decoded[23]) and (int) $decoded[23][0] === 123456789,
			"LONG 值应能原样读回"
		);
		$results[] = self::check(
			"long_reports_internal_type",
			isset($decoded[23]) and (int) $decoded[23][1] === \pocketmine\entity\Entity::DATA_TYPE_LONG,
			"读回的类型应统一成内部 DATA_TYPE_LONG(8)"
		);
		$results[] = self::check(
			"metadata_after_long_survives",
			isset($decoded[30]) and (int) $decoded[30][0] === 7,
			"LONG 之后的索引 30 必须仍能正确解析（旧实现会整体错位）"
		);

		// 默认实体元数据里 23/24 是最常见的组合，确保 24 不会被 23 吃掉。
		$defaults = \pocketmine\utils\Binary::writeMetadata([
			0 => [\pocketmine\entity\Entity::DATA_TYPE_BYTE, 0],
			23 => [\pocketmine\entity\Entity::DATA_TYPE_LONG, -1],
			24 => [\pocketmine\entity\Entity::DATA_TYPE_BYTE, 0]
		]);
		$decodedDefaults = \pocketmine\utils\Binary::readMetadata($defaults, true);
		$results[] = self::check(
			"lead_holder_and_lead_both_readable",
			isset($decodedDefaults[23], $decodedDefaults[24]) and (int) $decodedDefaults[24][0] === 0,
			"索引 23(LONG) 不能吞掉索引 24(BYTE)"
		);

		return $results;
	}

	/**
	 * @param string $name
	 * @param bool   $pass
	 * @param string $detail
	 *
	 * @return array
	 */
	private static function check(string $name, bool $pass, string $detail) : array{
		return [$name, $pass, $detail];
	}
}
