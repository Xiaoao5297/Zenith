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

namespace lycore\tile;

use lycore\item\Skull as SkullItem;
use lycore\level\format\FullChunk;
use lycore\nbt\tag\ByteTag;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\NamedTag;
use lycore\nbt\tag\StringTag;

class Skull extends Spawnable{
	private const SAFE_TAGS = [
		"id" => true,
		"x" => true,
		"y" => true,
		"z" => true,
		"isMovable" => true,
		"SkullType" => true,
		"Rot" => true
	];

	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		$sanitized = self::sanitizeCompoundTag($nbt);
		parent::__construct($chunk, $nbt);
		if($sanitized){
			$this->chunk->setChanged();
		}
	}

	private static function sanitizeScalarTag(CompoundTag $nbt, string $name, string $tagClass, int $value) : bool{
		if(isset($nbt->{$name}) and $nbt->{$name} instanceof $tagClass and (int) $nbt->{$name}->getValue() === $value){
			return false;
		}

		$nbt->{$name} = new $tagClass($name, $value);
		return true;
	}

	private static function getScalarValue(CompoundTag $nbt, string $name){
		if(!isset($nbt->{$name}) or !($nbt->{$name} instanceof NamedTag)){
			return null;
		}

		$value = $nbt->{$name}->getValue();
		return is_array($value) || is_object($value) ? null : $value;
	}

	public static function sanitizeCompoundTag(CompoundTag $nbt) : bool{
		$changed = false;

		foreach($nbt as $key => $tag){
			if($tag instanceof NamedTag and !isset(self::SAFE_TAGS[$key])){
				unset($nbt->{$key});
				$changed = true;
			}
		}

		$changed = self::sanitizeScalarTag(
			$nbt,
			"SkullType",
			ByteTag::class,
			SkullItem::sanitizeSkullTypeValue(self::getScalarValue($nbt, "SkullType"))
		) || $changed;
		$changed = self::sanitizeScalarTag(
			$nbt,
			"Rot",
			ByteTag::class,
			self::sanitizeRotationValue(self::getScalarValue($nbt, "Rot"))
		) || $changed;

		return $changed;
	}

	private static function sanitizeRotationValue($value) : int{
		$value = is_numeric($value) ? (int) $value : 0;
		if($value < 0 or $value > 0x0f){
			return 0;
		}

		return $value;
	}

	public function saveNBT(){
		parent::saveNBT();
		self::sanitizeCompoundTag($this->namedtag);
	}

	public function getSpawnCompound(){
		self::sanitizeCompoundTag($this->namedtag);
		return new CompoundTag("", [
			new StringTag("id", Tile::SKULL),
			new ByteTag("SkullType", $this->getSkullType()),
			new IntTag("x", (int)$this->x),
			new IntTag("y", (int)$this->y),
			new IntTag("z", (int)$this->z),
			new ByteTag("Rot", self::sanitizeRotationValue(self::getScalarValue($this->namedtag, "Rot")))
		]);
	}

	public function getSkullType(){
		self::sanitizeCompoundTag($this->namedtag);
		return (int) $this->namedtag["SkullType"];
	}
}
