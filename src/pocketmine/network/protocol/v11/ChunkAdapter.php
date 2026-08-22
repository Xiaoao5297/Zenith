<?php

namespace pocketmine\network\protocol\v11;

use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\nbt\tag\Tag;
use pocketmine\network\protocol\FullChunkDataPacket as CurrentFullChunkDataPacket;

final class ChunkAdapter{

	private const BLOCK_IDS_LENGTH = 32768;
	private const HALF_ARRAY_LENGTH = 16384;
	private const HEIGHT_MAP_LENGTH = 256;
	private const BIOME_COLORS_LENGTH = 1024;
	private const LIGHT_END_OFFSET = self::BLOCK_IDS_LENGTH + (self::HALF_ARRAY_LENGTH * 3);
	private const TILE_DATA_OFFSET = self::LIGHT_END_OFFSET + self::HEIGHT_MAP_LENGTH + self::BIOME_COLORS_LENGTH;
	private const LEGACY_TILE_CHEST = "Chest";
	private const LEGACY_TILE_SIGN = "Sign";

	private function __construct(){
	}

	public static function adaptPayload(string $payload, int $ordering = CurrentFullChunkDataPacket::ORDER_COLUMNS, ?int $chunkX = null, ?int $chunkZ = null) : string{
		if(strlen($payload) < self::TILE_DATA_OFFSET){
			return $payload;
		}

		if($ordering === CurrentFullChunkDataPacket::ORDER_LAYERED){
			$payload = self::columnizeLayeredPayload($payload);
		}

		return self::stripBlockExtraData($payload, $chunkX, $chunkZ);
	}

	public static function stripBlockExtraData(string $payload, ?int $chunkX = null, ?int $chunkZ = null) : string{
		// 0.11 clients expect tile-entity NBT immediately after biome colors; block extra data was added later.
		$payloadLength = strlen($payload);
		if($payloadLength <= self::TILE_DATA_OFFSET){
			return $payload;
		}

		$basePayload = substr($payload, 0, self::TILE_DATA_OFFSET);
		$tail = substr($payload, self::TILE_DATA_OFFSET);
		if($tail === ""){
			return $basePayload;
		}

		if(ord($tail[0]) === NBT::TAG_Compound){
			$tags = self::readTileEntityTags($tail);
			if($tags !== null){
				return $basePayload . self::sanitizeTileEntityTags($tags, $chunkX, $chunkZ);
			}
			// If this is malformed old-format tile data, do not send it to 0.11.
			if(strlen($tail) < 4){
				return $basePayload;
			}
		}
		if(strlen($tail) < 4){
			return $basePayload;
		}

		$countData = unpack("Vcount", substr($tail, 0, 4));
		if($countData === false){
			return $basePayload;
		}

		$count = (int) $countData["count"];
		if($count > 0x7fffffff){
			return $basePayload;
		}

		$extraDataLength = 4 + ($count * 6);
		if($extraDataLength > strlen($tail)){
			return $basePayload;
		}

		return $basePayload . self::sanitizeTileEntityData(substr($tail, $extraDataLength), $chunkX, $chunkZ);
	}

	public static function adaptTileEntityNamedTag(string $tileData, ?int $chunkX = null, ?int $chunkZ = null) : ?string{
		$tags = self::readTileEntityTags($tileData);
		if($tags === null or count($tags) !== 1){
			return null;
		}

		$tag = self::toLegacyTileEntityTag($tags[0], $chunkX, $chunkZ);
		if($tag === null){
			return null;
		}

		$nbt = new NBT(NBT::LITTLE_ENDIAN);
		$nbt->setData($tag);
		$data = $nbt->write();

		return $data === false ? null : $data;
	}

	private static function sanitizeTileEntityData(string $tileData, ?int $chunkX = null, ?int $chunkZ = null) : string{
		if($tileData === ""){
			return "";
		}

		$tags = self::readTileEntityTags($tileData);
		if($tags === null){
			return "";
		}

		return self::sanitizeTileEntityTags($tags, $chunkX, $chunkZ);
	}

	private static function readTileEntityTags(string $tileData) : ?array{
		try{
			set_error_handler(static function($severity, $message, $file, $line){
				throw new \ErrorException($message, 0, $severity, $file, $line);
			});
			$nbt = new NBT(NBT::LITTLE_ENDIAN);
			$nbt->read($tileData, true);
			$data = $nbt->getData();
			if($data instanceof CompoundTag){
				return [$data];
			}
			if(!is_array($data)){
				return null;
			}
			$tags = [];
			foreach($data as $tag){
				if(!($tag instanceof CompoundTag)){
					return null;
				}
				$tags[] = $tag;
			}

			return $tags;
		}catch(\Throwable $e){
			return null;
		}finally{
			restore_error_handler();
		}
	}

	private static function sanitizeTileEntityTags(array $tags, ?int $chunkX = null, ?int $chunkZ = null) : string{
		$filtered = [];
		foreach($tags as $tag){
			if($tag instanceof CompoundTag){
				$legacyTag = self::toLegacyTileEntityTag($tag, $chunkX, $chunkZ);
				if($legacyTag !== null){
					$filtered[] = $legacyTag;
				}
			}
		}

		if(count($filtered) === 0){
			return "";
		}

		$nbt = new NBT(NBT::LITTLE_ENDIAN);
		$nbt->setData($filtered);
		$data = $nbt->write();

		return $data === false ? "" : $data;
	}

	private static function toLegacyTileEntityTag(CompoundTag $tag, ?int $chunkX = null, ?int $chunkZ = null) : ?CompoundTag{
		$id = self::stringValue($tag, "id");
		if($id !== self::LEGACY_TILE_CHEST and $id !== self::LEGACY_TILE_SIGN){
			return null;
		}

		$x = self::intValue($tag, "x");
		$y = self::intValue($tag, "y");
		$z = self::intValue($tag, "z");
		if($x === null or $y === null or $z === null){
			return null;
		}
		if($y < 0 or $y >= 128){
			return null;
		}
		if($chunkX !== null and ($x >> 4) !== $chunkX){
			return null;
		}
		if($chunkZ !== null and ($z >> 4) !== $chunkZ){
			return null;
		}

		if($id === self::LEGACY_TILE_CHEST){
			$tags = [
				new StringTag("id", self::LEGACY_TILE_CHEST),
				new IntTag("x", $x),
				new IntTag("y", $y),
				new IntTag("z", $z)
			];
			$pairX = self::intValue($tag, "pairx");
			$pairZ = self::intValue($tag, "pairz");
			if($pairX !== null and $pairZ !== null){
				$tags[] = new IntTag("pairx", $pairX);
				$tags[] = new IntTag("pairz", $pairZ);
			}

			return new CompoundTag("", $tags);
		}

		return new CompoundTag("", [
			new StringTag("id", self::LEGACY_TILE_SIGN),
			new StringTag("Text1", self::stringValue($tag, "Text1")),
			new StringTag("Text2", self::stringValue($tag, "Text2")),
			new StringTag("Text3", self::stringValue($tag, "Text3")),
			new StringTag("Text4", self::stringValue($tag, "Text4")),
			new IntTag("x", $x),
			new IntTag("y", $y),
			new IntTag("z", $z)
		]);
	}

	private static function intValue(CompoundTag $tag, string $name) : ?int{
		if(!isset($tag->{$name}) or !($tag->{$name} instanceof Tag)){
			return null;
		}

		$value = $tag->{$name}->getValue();
		if(is_array($value) or is_object($value)){
			return null;
		}

		return (int) $value;
	}

	private static function stringValue(CompoundTag $tag, string $name) : string{
		if(!isset($tag->{$name}) or !($tag->{$name} instanceof Tag)){
			return "";
		}

		$value = $tag->{$name}->getValue();
		if(is_array($value) or is_object($value)){
			return "";
		}

		return (string) $value;
	}

	private static function columnizeLayeredPayload(string $payload) : string{
		$blockIds = substr($payload, 0, self::BLOCK_IDS_LENGTH);
		$blockData = substr($payload, self::BLOCK_IDS_LENGTH, self::HALF_ARRAY_LENGTH);
		$skyLight = substr($payload, self::BLOCK_IDS_LENGTH + self::HALF_ARRAY_LENGTH, self::HALF_ARRAY_LENGTH);
		$blockLight = substr($payload, self::BLOCK_IDS_LENGTH + (self::HALF_ARRAY_LENGTH * 2), self::HALF_ARRAY_LENGTH);

		$orderedIds = "";
		$orderedData = "";
		$orderedSkyLight = "";
		$orderedLight = "";

		for($x = 0; $x < 16; ++$x){
			for($z = 0; $z < 16; ++$z){
				$orderedIds .= self::getLayeredColumn($blockIds, $x, $z);
				$orderedData .= self::getLayeredHalfColumn($blockData, $x, $z);
				$orderedSkyLight .= self::getLayeredHalfColumn($skyLight, $x, $z);
				$orderedLight .= self::getLayeredHalfColumn($blockLight, $x, $z);
			}
		}

		return $orderedIds . $orderedData . $orderedSkyLight . $orderedLight . substr($payload, self::LIGHT_END_OFFSET);
	}

	private static function getLayeredColumn(string $data, int $x, int $z) : string{
		$column = "";
		$i = ($z << 4) + $x;
		for($y = 0; $y < 128; ++$y){
			$column .= $data[($y << 8) + $i];
		}

		return $column;
	}

	private static function getLayeredHalfColumn(string $data, int $x, int $z) : string{
		$column = "";
		$i = ($z << 3) + ($x >> 1);
		if(($x & 1) === 0){
			for($y = 0; $y < 128; $y += 2){
				$low = ord($data[($y << 7) + $i]) & 0x0f;
				$high = (ord($data[(($y + 1) << 7) + $i]) & 0x0f) << 4;
				$column .= chr($low | $high);
			}
		}else{
			for($y = 0; $y < 128; $y += 2){
				$low = (ord($data[($y << 7) + $i]) & 0xf0) >> 4;
				$high = ord($data[(($y + 1) << 7) + $i]) & 0xf0;
				$column .= chr($low | $high);
			}
		}

		return $column;
	}
}
