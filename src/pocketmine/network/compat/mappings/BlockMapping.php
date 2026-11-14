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

use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\network\compat\ProtocolCapabilities;
use pocketmine\network\protocol\Info;
use pocketmine\utils\Binary;
use pocketmine\network\compat\PacketHeader;

/**
 * 方块 ID / 状态 / 区块负载的协议兼容映射。
 */
final class BlockMapping{

	const CHUNK_BLOCK_IDS_LENGTH = 32768;
	const CHUNK_BLOCK_DATA_LENGTH = 16384;
	const CHUNK_EXTRA_DATA_OFFSET = 83200;
	const LEVEL_EVENT_PARTICLE_DESTROY = 2001;

	const LEGACY_011_BLOCK_ID_MAP = [
		27 => [66, 0],
		28 => [66, 0],
		88 => [12, 0],
		113 => [85, 0],
		115 => [0, 0],
		116 => [247, 0],
		117 => [120, 0],
		126 => [66, 0],
		140 => [0, 0],
		144 => [0, 0],
		145 => [42, 0],
		146 => [54, 0],
		153 => [87, 0],
		174 => [79, 0],
	];

	const LEGACY_012_BLOCK_ID_MAP = [
		25 => [5, 0],
		28 => [66, 0],
		69 => [63, 0],
		75 => [50, 0],
		76 => [50, 0],
		77 => [63, 0],
		123 => [89, 0],
		124 => [1, 0],
		126 => [66, 0],
		143 => [63, 0],
		146 => [54, 0],
		151 => [44, 0],
		167 => [96, 0],
		178 => [44, 0],
	];

	const LEGACY_012_PRESSURE_PLATES = [70, 72, 147, 148];
	const LEGACY_012_DOORS = [193, 194, 195, 196, 197];

	const LEGACY_011_MAPPED_INTERACTIVE_BLOCK_IDS = [
		116 => true,
		145 => true,
	];

	const LEGACY_013_BLOCK_ID_MAP = [
		23 => [4, 0],
		36 => [0, 0],
		93 => [44, 0],
		94 => [44, 0],
		95 => [7, 0],
		97 => [1, 0],
		125 => [4, 0],
		131 => [0, 0],
		132 => [0, 0],
		149 => [44, 0],
		150 => [44, 0],
		154 => [1, 0],
		165 => [26, 8],
		199 => [0, 0],
		439 => [0, 0],
	];

	const LEGACY_REDSTONE_BLOCK_ID_MAP = [
		29 => [4, 0],
		33 => [4, 0],
		34 => [85, 0],
		36 => [0, 0],
		251 => [247, 0],
	];

	const LEGACY_013_BLOCK_STATE_MAP_IDS = [
		12 => true,
		179 => true,
		180 => true,
		181 => true,
		182 => true,
	];

	const LEGACY_013_HIDDEN_TILE_IDS = [
		"ItemFrame" => true,
		"Hopper" => true,
		"Dropper" => true,
		"Dispenser" => true,
	];

	/** @var string[] protocol => 受限方块 id 字节串缓存 */
	private static $restrictedChunkBlockBytes = [];

	private function __construct(){
	}

	public static function isRestrictedBlockIdFor013(int $blockId) : bool{
		return self::isRestrictedBlockIdForProtocol(37, $blockId);
	}

	private static function getRestrictedChunkBlockBytesForProtocol(int $protocol) : string{
		if(isset(self::$restrictedChunkBlockBytes[$protocol])){
			return self::$restrictedChunkBlockBytes[$protocol];
		}

		$bytes = "";
		for($blockId = 0; $blockId <= 255; ++$blockId){
			if(self::isRestrictedBlockIdForProtocol($protocol, $blockId)){
				$bytes .= chr($blockId);
			}
		}

		return self::$restrictedChunkBlockBytes[$protocol] = $bytes;
	}

	public static function isRestrictedBlockIdForProtocol(int $protocol, int $blockId) : bool{
		if(ProtocolCapabilities::isProtocol011($protocol) and isset(self::LEGACY_011_BLOCK_ID_MAP[$blockId])){
			return true;
		}

		if(ProtocolCapabilities::usesLegacy012Mappings($protocol) and self::needsLegacy012BlockMapping($blockId)){
			return true;
		}

		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_REDSTONE_BLOCK_ID_MAP[$blockId])){
			return true;
		}

		if(ProtocolCapabilities::usesLegacy013Mappings($protocol)){
			return isset(self::LEGACY_013_BLOCK_ID_MAP[$blockId]) or isset(self::LEGACY_013_BLOCK_STATE_MAP_IDS[$blockId]);
		}

		return false;
	}

	public static function isRestrictedBlockStateFor013(int $blockId, int $blockMeta = 0) : bool{
		return self::isRestrictedBlockStateForProtocol(37, $blockId, $blockMeta);
	}

	public static function isRestrictedBlockStateForProtocol(int $protocol, int $blockId, int $blockMeta = 0) : bool{
		[$mappedId, $mappedMeta] = self::mapBlockForProtocol($protocol, $blockId, $blockMeta);
		return $mappedId !== $blockId or $mappedMeta !== $blockMeta;
	}

	public static function isHiddenBlockIdFor013(int $blockId, int $blockMeta = 0) : bool{
		return self::isHiddenBlockIdForProtocol(37, $blockId, $blockMeta);
	}

	public static function isHiddenBlockIdForProtocol(int $protocol, int $blockId, int $blockMeta = 0) : bool{
		[$mappedId, $mappedMeta] = self::mapBlockForProtocol($protocol, $blockId, $blockMeta);
		return ($mappedId !== $blockId or $mappedMeta !== $blockMeta) and $mappedId === 0;
	}

	public static function shouldBlockBreakFor013(int $blockId, int $blockMeta = 0) : bool{
		return self::shouldBlockBreakForProtocol(37, $blockId, $blockMeta);
	}

	public static function shouldBlockBreakForProtocol(int $protocol, int $blockId, int $blockMeta = 0) : bool{
		return self::isRestrictedBlockStateForProtocol($protocol, $blockId, $blockMeta);
	}

	public static function mapBlockFor013(int $blockId, int $blockMeta) : array{
		return self::mapBlockForProtocol(37, $blockId, $blockMeta);
	}

	public static function mapBlockForProtocol(int $protocol, int $blockId, int $blockMeta) : array{
		if(ProtocolCapabilities::isProtocol011($protocol) and isset(self::LEGACY_011_BLOCK_ID_MAP[$blockId])){
			return self::LEGACY_011_BLOCK_ID_MAP[$blockId];
		}

		if(ProtocolCapabilities::usesLegacy012Mappings($protocol) and self::needsLegacy012BlockMapping($blockId)){
			return self::mapLegacy012Block($blockId, $blockMeta);
		}

		if(ProtocolCapabilities::requiresLegacyRedstoneMapping($protocol) and isset(self::LEGACY_REDSTONE_BLOCK_ID_MAP[$blockId])){
			return self::LEGACY_REDSTONE_BLOCK_ID_MAP[$blockId];
		}

		if(!ProtocolCapabilities::usesLegacy013Mappings($protocol)){
			return [$blockId, $blockMeta];
		}

		if(isset(self::LEGACY_013_BLOCK_ID_MAP[$blockId])){
			return self::LEGACY_013_BLOCK_ID_MAP[$blockId];
		}

		switch($blockId){
			case 12:
				return ($blockMeta & 0x0f) === 1 ? [12, 0] : [$blockId, $blockMeta];
			case 179:
				return [24, $blockMeta & 0x03];
			case 180:
				return [128, $blockMeta & 0x07];
			case 181:
				return [43, 1];
			case 182:
				return [44, ($blockMeta & 0x08) | 1];
		}

		return [$blockId, $blockMeta];
	}

	public static function needsLegacy012BlockMapping(int $blockId) : bool{
		return isset(self::LEGACY_012_BLOCK_ID_MAP[$blockId]) or
			in_array($blockId, self::LEGACY_012_PRESSURE_PLATES, true) or
			in_array($blockId, self::LEGACY_012_DOORS, true);
	}

	public static function shouldUseLegacyMappedWoodenDoorAnimation(int $protocol, int $blockId) : bool{
		return (ProtocolCapabilities::isProtocol011($protocol) or ProtocolCapabilities::isProtocol012($protocol)) and in_array($blockId, self::LEGACY_012_DOORS, true);
	}

	public static function shouldAllowLegacyMappedBlockActivation(int $protocol, int $blockId) : bool{
		if(self::shouldUseLegacyMappedWoodenDoorAnimation($protocol, $blockId)){
			return true;
		}

		return ProtocolCapabilities::isProtocol011($protocol) and isset(self::LEGACY_011_MAPPED_INTERACTIVE_BLOCK_IDS[$blockId]);
	}

	public static function mapLegacy012Block(int $blockId, int $blockMeta) : array{
		if(in_array($blockId, self::LEGACY_012_DOORS, true)){
			return [64, self::normalizeLegacy012DoorMeta($blockMeta)];
		}

		if(in_array($blockId, self::LEGACY_012_PRESSURE_PLATES, true)){
			return [171, 0];
		}

		if(isset(self::LEGACY_012_BLOCK_ID_MAP[$blockId])){
			return self::LEGACY_012_BLOCK_ID_MAP[$blockId];
		}

		return [$blockId, $blockMeta];
	}

	private static function normalizeLegacy012DoorMeta(int $blockMeta) : int{
		return ($blockMeta & 0x08) === 0 ? ($blockMeta & 0x0f) : (($blockMeta & 0x07) | 0x08);
	}

	public static function mapLevelEventDataForProtocol(int $protocol, int $eventId, int $data) : int{
		if($eventId !== self::LEVEL_EVENT_PARTICLE_DESTROY){
			return $data;
		}

		$blockId = $data & 0xfff;
		$blockMeta = ($data >> 12) & 0x0f;
		[$mappedId, $mappedMeta] = self::mapBlockForProtocol($protocol, $blockId, $blockMeta);
		if($mappedId === $blockId and $mappedMeta === $blockMeta){
			return $data;
		}

		return ($data & ~0xffff) | ($mappedId & 0xfff) | (($mappedMeta & 0x0f) << 12);
	}

	public static function isHiddenTileIdFor013(string $tileId) : bool{
		return isset(self::LEGACY_013_HIDDEN_TILE_IDS[$tileId]);
	}

	public static function isHiddenTileIdForProtocol(int $protocol, string $tileId) : bool{
		return ProtocolCapabilities::usesLegacy013Mappings($protocol) and self::isHiddenTileIdFor013($tileId);
	}

	public static function remapChunkPayloadFor013(string $payload) : string{
		return self::remapChunkPayloadForProtocol(37, $payload);
	}

	public static function remapChunkPayloadForProtocol(int $protocol, string $payload) : string{
		$minimumLength = self::CHUNK_BLOCK_IDS_LENGTH + self::CHUNK_BLOCK_DATA_LENGTH;
		if(strlen($payload) < $minimumLength){
			return $payload;
		}

		$blockIds = substr($payload, 0, self::CHUNK_BLOCK_IDS_LENGTH);
		$blockData = substr($payload, self::CHUNK_BLOCK_IDS_LENGTH, self::CHUNK_BLOCK_DATA_LENGTH);
		$restrictedChunkBlockBytes = self::getRestrictedChunkBlockBytesForProtocol($protocol);
		if($restrictedChunkBlockBytes === "" or strpbrk($blockIds, $restrictedChunkBlockBytes) === false){
			return ProtocolCapabilities::usesLegacy013Mappings($protocol) ? self::stripHiddenTileEntitiesFor013($payload) : $payload;
		}

		$modified = false;

		for($index = 0; $index < self::CHUNK_BLOCK_IDS_LENGTH; ++$index){
			$blockId = ord($blockIds[$index]);
			if(!self::isRestrictedBlockIdForProtocol($protocol, $blockId)){
				continue;
			}

			$dataIndex = $index >> 1;
			$dataByte = ord($blockData[$dataIndex]);
			$blockMeta = ($index & 1) === 0 ? ($dataByte & 0x0f) : ($dataByte >> 4);
			[$mappedId, $mappedMeta] = self::mapBlockForProtocol($protocol, $blockId, $blockMeta);

			if($mappedId !== $blockId){
				$blockIds[$index] = chr($mappedId);
				$modified = true;
			}

			if($mappedMeta !== $blockMeta){
				if(($index & 1) === 0){
					$blockData[$dataIndex] = chr(($dataByte & 0xf0) | ($mappedMeta & 0x0f));
				}else{
					$blockData[$dataIndex] = chr((($mappedMeta & 0x0f) << 4) | ($dataByte & 0x0f));
				}
				$modified = true;
			}
		}

		if($modified){
			$payload = $blockIds . $blockData . substr($payload, $minimumLength);
		}

		return ProtocolCapabilities::usesLegacy013Mappings($protocol) ? self::stripHiddenTileEntitiesFor013($payload) : $payload;
	}

	public static function stripHiddenTileEntitiesFor013(string $payload) : string{
		if(strlen($payload) <= self::CHUNK_EXTRA_DATA_OFFSET + 4){
			return $payload;
		}

		$extraData = substr($payload, self::CHUNK_EXTRA_DATA_OFFSET);
		$extraCount = Binary::readLInt(substr($extraData, 0, 4));
		$tileDataOffset = 4 + ($extraCount * 6);
		if($extraCount < 0 or strlen($extraData) < $tileDataOffset){
			return $payload;
		}

		$tileData = substr($extraData, $tileDataOffset);
		if($tileData === ""){
			return $payload;
		}

		try{
			$nbt = new NBT(NBT::LITTLE_ENDIAN);
			$nbt->read($tileData, true);
			$tags = $nbt->getData();
			if(!is_array($tags)){
				return $payload;
			}

			$filtered = [];
			$modified = false;
			foreach($tags as $tag){
				if($tag instanceof CompoundTag and isset($tag->id) and $tag->id instanceof StringTag and self::isHiddenTileIdFor013($tag->id->getValue())){
					$modified = true;
					continue;
				}
				$filtered[] = $tag;
			}

			if(!$modified){
				return $payload;
			}

			$nbt->setData($filtered);
			$newTileData = $nbt->write();
			if($newTileData === false){
				return $payload;
			}

			return substr($payload, 0, self::CHUNK_EXTRA_DATA_OFFSET + $tileDataOffset) . $newTileData;
		}catch(\Throwable $e){
			return $payload;
		}
	}

	public static function remapPacketBufferFor013(string $buffer) : string{
		return self::remapPacketBufferForProtocol(37, $buffer);
	}

	public static function remapPacketBufferForProtocol(int $protocol, string $buffer) : string{
		$header = PacketHeader::read($buffer);
		if($header === null){
			return $buffer;
		}

		[$pid, $packetOffset] = $header;
		if($pid === Info::LEVEL_EVENT_PACKET){
			$eventOffset = $packetOffset;
			$dataOffset = $eventOffset + 14;
			if(strlen($buffer) < $dataOffset + 4){
				return $buffer;
			}

			$eventId = Binary::readShort(substr($buffer, $eventOffset, 2));
			$data = Binary::readInt(substr($buffer, $dataOffset, 4));
			$mappedData = self::mapLevelEventDataForProtocol($protocol, $eventId, $data);
			if($mappedData === $data){
				return $buffer;
			}

			return substr($buffer, 0, $dataOffset) . Binary::writeInt($mappedData) . substr($buffer, $dataOffset + 4);
		}

		if($pid !== Info::FULL_CHUNK_DATA_PACKET){
			return $buffer;
		}

		$dataLengthOffset = $packetOffset + 9;
		if(strlen($buffer) < $dataLengthOffset + 4){
			return $buffer;
		}

		$dataLength = Binary::readInt(substr($buffer, $dataLengthOffset, 4));
		$dataOffset = $dataLengthOffset + 4;
		if($dataLength < 0 or strlen($buffer) < $dataOffset + $dataLength){
			return $buffer;
		}

		$data = substr($buffer, $dataOffset, $dataLength);
		$mappedData = self::remapChunkPayloadForProtocol($protocol, $data);
		if($mappedData === $data){
			return $buffer;
		}

		return substr($buffer, 0, $dataLengthOffset) .
			Binary::writeInt(strlen($mappedData)) .
			$mappedData .
			substr($buffer, $dataOffset + $dataLength);
	}

	public static function remapBatchRawPayloadFor013(string $rawPayload) : string{
		return self::remapBatchRawPayloadForProtocol(37, $rawPayload);
	}

	public static function remapBatchRawPayloadForProtocol(int $protocol, string $rawPayload) : string{
		$length = strlen($rawPayload);
		$offset = 0;
		$mappedPayload = "";
		$modified = false;

		while($offset < $length){
			if($length - $offset < 4){
				return $rawPayload;
			}

			$packetLength = Binary::readInt(substr($rawPayload, $offset, 4));
			$offset += 4;
			if($packetLength < 0 or $length - $offset < $packetLength){
				return $rawPayload;
			}

			$packetBuffer = substr($rawPayload, $offset, $packetLength);
			$offset += $packetLength;

			$mappedPacketBuffer = self::remapPacketBufferForProtocol($protocol, $packetBuffer);
			if($mappedPacketBuffer !== $packetBuffer){
				$modified = true;
			}

			$mappedPayload .= Binary::writeInt(strlen($mappedPacketBuffer)) . $mappedPacketBuffer;
		}

		return $modified ? $mappedPayload : $rawPayload;
	}

	public static function remapBatchPayloadFor013(string $payload, int $compressionLevel = -1) : string{
		return self::remapBatchPayloadForProtocol(37, $payload, $compressionLevel);
	}

	public static function remapBatchPayloadForProtocol(int $protocol, string $payload, int $compressionLevel = -1) : string{
		$rawPayload = @zlib_decode($payload, 1024 * 1024 * 64);
		if(!is_string($rawPayload)){
			return $payload;
		}

		$mappedPayload = self::remapBatchRawPayloadForProtocol($protocol, $rawPayload);
		if($mappedPayload === $rawPayload){
			return $payload;
		}

		$compressedPayload = zlib_encode($mappedPayload, ZLIB_ENCODING_DEFLATE, $compressionLevel);
		return is_string($compressedPayload) ? $compressedPayload : $payload;
	}
}
