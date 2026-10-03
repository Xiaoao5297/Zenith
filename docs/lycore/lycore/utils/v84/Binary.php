<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝
 * ╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 *  ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: U core
 *
 * @Links:
 *  > LY Core
 *  > LY Core Project
*/

namespace lycore\utils\v84;

use lycore\entity\Entity;

class Binary extends \lycore\utils\Binary{
	private const LEGACY_DATA_TYPE_LONG = 7;

	public static function writeMetadata(array $data){
		$m = "";
		foreach($data as $bottom => $d){
			if(!is_array($d) or count($d) < 2 or $bottom < 0 or $bottom > 31){
				continue;
			}

			$type = $d[0];
			$value = $d[1];
			$wireType = $type;
			$payload = null;

			switch($type){
				case Entity::DATA_TYPE_BYTE:
					$payload = self::writeByte($value);
					break;
				case Entity::DATA_TYPE_SHORT:
					$payload = self::writeLShort($value);
					break;
				case Entity::DATA_TYPE_INT:
					$payload = self::writeLInt($value);
					break;
				case Entity::DATA_TYPE_FLOAT:
					$payload = self::writeLFloat($value);
					break;
				case Entity::DATA_TYPE_STRING:
					$value = (string) $value;
					$payload = self::writeLShort(strlen($value)) . $value;
					break;
				case Entity::DATA_TYPE_SLOT:
					if(!is_array($value) or count($value) < 3){
						break;
					}
					$payload = self::writeLShort($value[0]) . self::writeByte($value[1]) . self::writeLShort($value[2]);
					break;
				case Entity::DATA_TYPE_POS:
					if(!is_array($value) or count($value) < 3){
						break;
					}
					$payload = self::writeLInt($value[0]) . self::writeLInt($value[1]) . self::writeLInt($value[2]);
					break;
				case Entity::DATA_TYPE_LONG:
					$wireType = self::LEGACY_DATA_TYPE_LONG;
					$payload = self::writeLLong($value);
					break;
			}

			if($payload === null or $wireType > 7){
				continue;
			}

			$m .= chr(($wireType << 5) | ($bottom & 0x1f)) . $payload;
		}
		$m .= "\x7f";

		return $m;
	}

	public static function readMetadata($value, $types = false){
		$offset = 0;
		$m = [];
		if(!isset($value[$offset])){
			return $m;
		}

		$b = ord($value[$offset]);
		++$offset;
		while($b !== 127 and isset($value[$offset])){
			$bottom = $b & 0x1f;
			$type = $b >> 5;
			$storedType = $type;
			switch($type){
				case Entity::DATA_TYPE_BYTE:
					$r = self::readByte($value[$offset]);
					++$offset;
					break;
				case Entity::DATA_TYPE_SHORT:
					$r = self::readLShort(substr($value, $offset, 2));
					$offset += 2;
					break;
				case Entity::DATA_TYPE_INT:
					$r = self::readLInt(substr($value, $offset, 4));
					$offset += 4;
					break;
				case Entity::DATA_TYPE_FLOAT:
					$r = self::readLFloat(substr($value, $offset, 4));
					$offset += 4;
					break;
				case Entity::DATA_TYPE_STRING:
					$len = self::readLShort(substr($value, $offset, 2));
					$offset += 2;
					$r = substr($value, $offset, $len);
					$offset += $len;
					break;
				case Entity::DATA_TYPE_SLOT:
					$r = [];
					$r[] = self::readLShort(substr($value, $offset, 2));
					$offset += 2;
					$r[] = ord($value[$offset]);
					++$offset;
					$r[] = self::readLShort(substr($value, $offset, 2));
					$offset += 2;
					break;
				case Entity::DATA_TYPE_POS:
					$r = [];
					for($i = 0; $i < 3; ++$i){
						$r[] = self::readLInt(substr($value, $offset, 4));
						$offset += 4;
					}
					break;
				case self::LEGACY_DATA_TYPE_LONG:
					$r = self::readLLong(substr($value, $offset, 8));
					$offset += 8;
					$storedType = Entity::DATA_TYPE_LONG;
					break;
				default:
					return [];
			}

			$m[$bottom] = $types === true ? [$r, $storedType] : $r;
			if(!isset($value[$offset])){
				break;
			}
			$b = ord($value[$offset]);
			++$offset;
		}

		return $m;
	}
}
