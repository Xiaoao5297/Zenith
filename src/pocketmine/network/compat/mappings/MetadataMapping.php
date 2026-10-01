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

namespace pocketmine\network\compat\mappings;

use pocketmine\entity\Entity;
use pocketmine\network\compat\ProtocolCapabilities;

/**
 * 实体元数据 (metadata) 的协议兼容映射。
 */
final class MetadataMapping{

	const DATA_TYPE_BYTE = 0;
	const DATA_TYPE_SHORT = 1;
	const DATA_TYPE_INT = 2;
	const DATA_TYPE_FLOAT = 3;
	const DATA_TYPE_STRING = 4;
	const DATA_TYPE_SLOT = 5;
	const DATA_TYPE_POS = 6;
	const DATA_TYPE_LONG = 7;

	const ENTITY_DATA_NAMETAG = 2;
	const ENTITY_DATA_SHOW_NAMETAG = 3;
	const ENTITY_DATA_POTION_ID = 16;
	const ENTITY_DATA_LEAD_HOLDER = 23;
	const ENTITY_DATA_LEAD = 24;

	const ENTITY_DATA_CREEPER_SWELL_DIRECTION = 16;
	const ENTITY_DATA_CREEPER_SWELL = 17;
	const ENTITY_DATA_CREEPER_SWELL_2 = 18;
	const ENTITY_DATA_CREEPER_POWERED = 19;

	const LEGACY_METADATA_TYPES = [
		0 => self::DATA_TYPE_BYTE,
		1 => self::DATA_TYPE_SHORT,
		2 => self::DATA_TYPE_STRING,
		3 => self::DATA_TYPE_BYTE,
	];

	const LEGACY_011_METADATA_TYPES = [
		0 => self::DATA_TYPE_BYTE,
		1 => self::DATA_TYPE_SHORT,
		2 => self::DATA_TYPE_STRING,
		3 => self::DATA_TYPE_BYTE,
		4 => self::DATA_TYPE_BYTE,
		7 => self::DATA_TYPE_INT,
		8 => self::DATA_TYPE_BYTE,
		14 => self::DATA_TYPE_BYTE,
		15 => self::DATA_TYPE_BYTE,
		16 => self::DATA_TYPE_BYTE,
		17 => null,
		20 => self::DATA_TYPE_INT,
	];

	const PROTOCOL_015_METADATA_TYPES = [
		0 => self::DATA_TYPE_BYTE,
		1 => self::DATA_TYPE_SHORT,
		2 => self::DATA_TYPE_STRING,
		3 => self::DATA_TYPE_BYTE,
		4 => self::DATA_TYPE_BYTE,
		7 => self::DATA_TYPE_INT,
		8 => self::DATA_TYPE_BYTE,
		14 => self::DATA_TYPE_BYTE,
		15 => self::DATA_TYPE_BYTE,
		16 => null,
		17 => null,
		18 => self::DATA_TYPE_BYTE,
		19 => null,
		20 => null,
		21 => self::DATA_TYPE_BYTE,
		23 => self::DATA_TYPE_LONG,
		24 => self::DATA_TYPE_BYTE,
	];

	private function __construct(){
	}

	public static function filterMetadataFor013(array $metadata) : array{
		return self::filterMetadataByTypeMap($metadata, self::LEGACY_METADATA_TYPES);
	}

	public static function filterMetadataForProtocol(int $protocol, array $metadata) : array{
		if(ProtocolCapabilities::isProtocol011($protocol)){
			$filtered = self::filterMetadataByTypeMap($metadata, self::LEGACY_011_METADATA_TYPES);
		}elseif(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol)){
			$filtered = self::filterMetadataFor013($metadata);
		}elseif(ProtocolCapabilities::isProtocol015($protocol)){
			$filtered = self::filterMetadataByTypeMap($metadata, self::PROTOCOL_015_METADATA_TYPES);
		}else{
			$filtered = $metadata;
		}

		if(!ProtocolCapabilities::isProtocol015($protocol)){
			if(class_exists(Entity::class, false)){
				unset($filtered[Entity::DATA_LEAD_HOLDER], $filtered[Entity::DATA_LEAD]);
			}else{
				unset($filtered[self::ENTITY_DATA_LEAD_HOLDER], $filtered[self::ENTITY_DATA_LEAD]);
			}
		}

		return $filtered;
	}

	public static function filterEntityMetadataForProtocol(int $protocol, int $entityType, array $metadata) : array{
		$filtered = self::filterMetadataForProtocol($protocol, $metadata);

		if($entityType === EntityMapping::ENTITY_TYPE_CREEPER){
			$filtered = self::keepCreeperMetadata($filtered, $metadata);
		}

		if(!ProtocolCapabilities::isProtocol015($protocol) and $entityType === EntityMapping::ENTITY_TYPE_ARROW){
			unset($filtered[self::ENTITY_DATA_POTION_ID]);
		}elseif(ProtocolCapabilities::isProtocol015($protocol) and
				$entityType !== EntityMapping::ENTITY_TYPE_ARROW and
				$entityType !== 86 and
				isset($filtered[self::ENTITY_DATA_POTION_ID]) and
				self::metadataPropertyHasType($filtered[self::ENTITY_DATA_POTION_ID], self::DATA_TYPE_SHORT)){
			unset($filtered[self::ENTITY_DATA_POTION_ID]);
		}

		return $filtered;
	}

	private static function keepCreeperMetadata(array $filtered, array $metadata) : array{
		foreach([
			self::ENTITY_DATA_CREEPER_SWELL_DIRECTION,
			self::ENTITY_DATA_CREEPER_SWELL,
			self::ENTITY_DATA_CREEPER_SWELL_2,
			self::ENTITY_DATA_CREEPER_POWERED,
		] as $index){
			if(!isset($metadata[$index]) or !self::isValidMetadataProperty($metadata[$index])){
				continue;
			}

			$normalized = self::normalizeMetadataProperty($index, $metadata[$index], self::DATA_TYPE_BYTE);
			if($normalized !== null){
				$filtered[$index] = $normalized;
			}
		}

		return $filtered;
	}

	private static function filterMetadataByTypeMap(array $metadata, array $typeMap) : array{
		$filtered = [];
		foreach($metadata as $index => $property){
			$index = (int) $index;
			if(!array_key_exists($index, $typeMap) or !self::isValidMetadataProperty($property)){
				continue;
			}

			$expectedType = $typeMap[$index];
			if($expectedType === null){
				$filtered[$index] = [(int) $property[0], $property[1]];
				continue;
			}

			$normalized = self::normalizeMetadataProperty($index, $property, $expectedType);
			if($normalized !== null){
				$filtered[$index] = $normalized;
			}
		}

		return $filtered;
	}

	public static function isValidMetadataProperty($property) : bool{
		if(!is_array($property) or !array_key_exists(0, $property) or !array_key_exists(1, $property)){
			return false;
		}

		if(!is_int($property[0]) and !is_float($property[0]) and !is_string($property[0])){
			return false;
		}

		$type = (int) $property[0];
		return $type >= self::DATA_TYPE_BYTE and $type <= self::DATA_TYPE_LONG;
	}

	private static function normalizeMetadataProperty(int $index, array $property, int $expectedType){
		if((int) $property[0] === $expectedType){
			return [$expectedType, $expectedType === self::DATA_TYPE_BYTE ? ((int) $property[1]) & 0xff : $property[1]];
		}

		if($index === 0 and self::isIntegerMetadataType((int) $property[0])){
			return [self::DATA_TYPE_BYTE, ((int) $property[1]) & 0xff];
		}

		return null;
	}

	private static function isIntegerMetadataType(int $type) : bool{
		return $type === self::DATA_TYPE_BYTE or
			$type === self::DATA_TYPE_SHORT or
			$type === self::DATA_TYPE_INT or
			$type === self::DATA_TYPE_LONG;
	}

	public static function metadataPropertyHasType($property, int $type) : bool{
		return self::isValidMetadataProperty($property) and (int) $property[0] === $type;
	}
}
