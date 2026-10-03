<?php

/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

namespace lycore\level;

use lycore\block\Block;

final class AntiXrayObfuscator{

	public const MODE_OBFUSCATOR = "obfuscator";
	public const MODE_HIDDEN = "hidden";

	private const BLOCK_ID_BYTES = 32768;
	private const BLOCK_DATA_BYTES = 16384;
	private const MIN_PAYLOAD_BYTES = self::BLOCK_ID_BYTES + self::BLOCK_DATA_BYTES;

	private const MAGIC_BLOCKS = [
		Block::GOLD_ORE,
		Block::IRON_ORE,
		Block::COAL_ORE,
		Block::LAPIS_ORE,
		Block::DIAMOND_ORE,
		Block::REDSTONE_ORE,
		Block::EMERALD_ORE,
		Block::NETHER_QUARTZ_ORE,
	];

	private static $ore = [
		Block::GOLD_ORE => true,
		Block::IRON_ORE => true,
		Block::COAL_ORE => true,
		Block::LAPIS_ORE => true,
		Block::DIAMOND_ORE => true,
		Block::REDSTONE_ORE => true,
		Block::GLOWING_REDSTONE_ORE => true,
		Block::EMERALD_ORE => true,
		Block::NETHER_QUARTZ_ORE => true,
	];

	private static $filter = [
		0 => true, 6 => true, 8 => true, 9 => true, 10 => true, 11 => true,
		18 => true, 20 => true, 26 => true, 27 => true, 28 => true, 29 => true,
		30 => true, 31 => true, 32 => true, 33 => true, 34 => true, 37 => true,
		38 => true, 39 => true, 40 => true, 44 => true, 50 => true, 51 => true,
		52 => true, 53 => true, 54 => true, 55 => true, 59 => true, 60 => true,
		63 => true, 64 => true, 65 => true, 66 => true, 67 => true, 68 => true,
		69 => true, 70 => true, 71 => true, 72 => true, 75 => true, 76 => true,
		77 => true, 78 => true, 79 => true, 81 => true, 83 => true, 85 => true,
		88 => true, 90 => true, 92 => true, 93 => true, 94 => true, 95 => true,
		96 => true, 101 => true, 102 => true, 104 => true, 105 => true,
		106 => true, 107 => true, 108 => true, 109 => true, 111 => true,
		113 => true, 114 => true, 115 => true, 116 => true, 117 => true,
		118 => true, 119 => true, 120 => true, 122 => true, 126 => true,
		127 => true, 128 => true, 130 => true, 131 => true, 132 => true,
		134 => true, 135 => true, 136 => true, 138 => true, 139 => true,
		140 => true, 141 => true, 142 => true, 143 => true, 144 => true,
		145 => true, 146 => true, 147 => true, 148 => true, 149 => true,
		150 => true, 151 => true, 154 => true, 156 => true, 158 => true,
		160 => true, 161 => true, 163 => true, 164 => true, 165 => true,
		166 => true, 167 => true, 171 => true, 175 => true, 176 => true,
		177 => true, 178 => true, 180 => true, 182 => true, 183 => true,
		184 => true, 185 => true, 186 => true, 187 => true, 190 => true,
		191 => true, 193 => true, 194 => true, 195 => true, 196 => true,
		197 => true, 198 => true, 199 => true, 200 => true, 202 => true,
		203 => true, 204 => true, 205 => true, 207 => true, 208 => true,
		218 => true, 230 => true, 238 => true, 239 => true, 240 => true,
		241 => true, 244 => true, 250 => true, 253 => true, 254 => true,
	];

	private function __construct(){
	}

	public static function obfuscateChunkPayload($payload, array $options = []){
		if(!is_string($payload) or strlen($payload) < self::MIN_PAYLOAD_BYTES){
			return $payload;
		}

		$mode = isset($options["mode"]) ? strtolower((string) $options["mode"]) : self::MODE_OBFUSCATOR;
		if($mode !== self::MODE_HIDDEN){
			$mode = self::MODE_OBFUSCATOR;
		}

		$scanChunkHeightLimit = isset($options["scanChunkHeightLimit"]) ? (int) $options["scanChunkHeightLimit"] : 4;
		$scanChunkHeightLimit = max(1, min(15, $scanChunkHeightLimit));
		$maxY = min(126, (($scanChunkHeightLimit + 1) << 4) - 1);
		$fakeBlock = isset($options["fakeBlock"]) ? ((int) $options["fakeBlock"] & 0xff) : Block::STONE;
		$ores = isset($options["ores"]) && is_array($options["ores"]) ? self::normalizeIdSet($options["ores"]) : self::$ore;
		$filters = isset($options["filters"]) && is_array($options["filters"]) ? self::normalizeIdSet($options["filters"]) : self::$filter;

		for($x = 1; $x < 15; ++$x){
			for($z = 1; $z < 15; ++$z){
				for($y = 1; $y <= $maxY; ++$y){
					$sectionY = $y & 0x0f;
					if($sectionY === 0 or $sectionY === 15){
						continue;
					}

					$index = self::chunkBlockIndex($x, $y, $z);
					if(!self::canObfuscate($payload, $index, $filters)){
						continue;
					}

					$id = -1;
					if($mode === self::MODE_OBFUSCATOR){
						$id = self::MAGIC_BLOCKS[$index & 0x07];
					}elseif(isset($ores[ord($payload[$index])])){
						$id = $fakeBlock;
					}

					if($id !== -1){
						$payload[$index] = chr($id);
						self::setBlockMeta($payload, $index, 0);
					}
				}
			}
		}

		return $payload;
	}

	public static function isFilterBlockId($id, array $filters = null){
		$filters = $filters === null ? self::$filter : self::normalizeIdSet($filters);
		return isset($filters[(int) $id & 0xff]);
	}

	public static function getDefaultOreIds(){
		return array_keys(self::$ore);
	}

	public static function getDefaultFilterIds(){
		return array_keys(self::$filter);
	}

	private static function canObfuscate($payload, $index, array $filters){
		return !isset($filters[ord($payload[$index + 2048])])
			and !isset($filters[ord($payload[$index - 2048])])
			and !isset($filters[ord($payload[$index + 128])])
			and !isset($filters[ord($payload[$index - 128])])
			and !isset($filters[ord($payload[$index + 1])])
			and !isset($filters[ord($payload[$index - 1])]);
	}

	private static function normalizeIdSet(array $ids){
		$result = [];
		foreach($ids as $key => $value){
			if(is_bool($value)){
				if($value){
					$result[(int) $key & 0xff] = true;
				}
				continue;
			}
			$result[(int) $value & 0xff] = true;
		}
		return $result;
	}

	private static function setBlockMeta(&$payload, $index, $meta){
		$dataIndex = self::BLOCK_ID_BYTES + ($index >> 1);
		$dataByte = ord($payload[$dataIndex]);
		if(($index & 1) === 0){
			$payload[$dataIndex] = chr(($dataByte & 0xf0) | ($meta & 0x0f));
		}else{
			$payload[$dataIndex] = chr((($meta & 0x0f) << 4) | ($dataByte & 0x0f));
		}
	}

	private static function chunkBlockIndex($x, $y, $z){
		return ($x << 11) | ($z << 7) | $y;
	}
}
