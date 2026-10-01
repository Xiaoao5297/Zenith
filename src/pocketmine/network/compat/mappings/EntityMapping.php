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

use pocketmine\network\compat\ProtocolCapabilities;

/**
 * 实体类型 / 名称 / 药水效果的协议兼容映射。
 */
final class EntityMapping{

	const ENTITY_TYPE_ARROW = 80;
	const ENTITY_TYPE_CREEPER = 33;
	const ENTITY_TYPE_ZOMBIE = 32;
	const ENTITY_TYPE_ZOMBIE_VILLAGER = 44;

	const LEGACY_013_ENTITY_TYPE_MAP = [
		45 => 15,
		96 => 84,
		97 => 84,
		98 => 84,
	];

	const LEGACY_011_ENTITY_TYPE_MAP = [
		self::ENTITY_TYPE_ZOMBIE_VILLAGER => self::ENTITY_TYPE_ZOMBIE,
	];

	const LEGACY_013_ENTITY_NAME_MAP = [
		45 => "女巫",
	];

	const LEGACY_PRE_015_ENTITY_TYPE_MAP = [
		46 => 34,
		47 => 32,
	];

	const LEGACY_PRE_015_ENTITY_NAME_MAP = [
		46 => "流浪者",
		47 => "尸壳",
	];

	private function __construct(){
	}

	public static function mapEntityTypeFor013(int $entityType) : int{
		return isset(self::LEGACY_013_ENTITY_TYPE_MAP[$entityType]) ? self::LEGACY_013_ENTITY_TYPE_MAP[$entityType] : $entityType;
	}

	public static function mapEntityTypeForProtocol(int $protocol, int $entityType) : int{
		if(ProtocolCapabilities::isProtocol011($protocol) and isset(self::LEGACY_011_ENTITY_TYPE_MAP[$entityType])){
			return self::LEGACY_011_ENTITY_TYPE_MAP[$entityType];
		}

		if(!ProtocolCapabilities::isProtocol015($protocol) && isset(self::LEGACY_PRE_015_ENTITY_TYPE_MAP[$entityType])){
			return self::LEGACY_PRE_015_ENTITY_TYPE_MAP[$entityType];
		}
		if(ProtocolCapabilities::usesLegacy013Mappings($protocol)){
			return self::mapEntityTypeFor013($entityType);
		}
		return $entityType;
	}

	public static function getLegacyMappedEntityNameForProtocol(int $protocol, int $entityType) : ?string{
		if(ProtocolCapabilities::usesLegacy013Mappings($protocol) and isset(self::LEGACY_013_ENTITY_NAME_MAP[$entityType])){
			return self::LEGACY_013_ENTITY_NAME_MAP[$entityType];
		}

		if(!ProtocolCapabilities::isProtocol015($protocol) and isset(self::LEGACY_PRE_015_ENTITY_NAME_MAP[$entityType])){
			return self::LEGACY_PRE_015_ENTITY_NAME_MAP[$entityType];
		}

		return null;
	}

	public static function applyLegacyMappedEntityNameForProtocol(int $protocol, int $entityType, array $metadata) : array{
		$mappedName = self::getLegacyMappedEntityNameForProtocol($protocol, $entityType);
		if($mappedName === null){
			return $metadata;
		}

		$nameProperty = isset($metadata[MetadataMapping::ENTITY_DATA_NAMETAG]) ? $metadata[MetadataMapping::ENTITY_DATA_NAMETAG] : null;
		$hasCustomName = MetadataMapping::metadataPropertyHasType($nameProperty, MetadataMapping::DATA_TYPE_STRING) && (string) $nameProperty[1] !== "";
		if(!$hasCustomName){
			$metadata[MetadataMapping::ENTITY_DATA_NAMETAG] = [MetadataMapping::DATA_TYPE_STRING, $mappedName];
		}
		$metadata[MetadataMapping::ENTITY_DATA_SHOW_NAMETAG] = [MetadataMapping::DATA_TYPE_BYTE, 1];

		return $metadata;
	}

	public static function canSendMobEffectIdForProtocol(int $protocol, int $effectId) : bool{
		if($effectId < 1){
			return false;
		}

		return ProtocolCapabilities::isProtocol015($protocol) ? $effectId <= 23 : $effectId <= 20;
	}
}
