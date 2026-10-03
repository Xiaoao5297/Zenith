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

namespace lycore\item;

use lycore\block\Block;
use lycore\nbt\tag\CompoundTag;

class Skull extends Item{
	const SKELETON = 0;
	const WITHER_SKELETON = 1;
	const ZOMBIE = 2;
	const STEVE = 3;
	const CREEPER = 4;
	const MIN_TYPE = self::SKELETON;
	const MAX_TYPE = self::CREEPER;

	public function __construct($meta = 0, $count = 1){
		$this->block = Block::get(Block::SKULL_BLOCK);
		parent::__construct(self::SKULL, self::sanitizeSkullTypeValue($meta), $count, "Skull");
	}

	public function getMaxStackSize() : int {
		return 64;
	}

	public static function sanitizeSkullTypeValue($value) : int{
		$value = is_numeric($value) ? (int) $value : self::SKELETON;
		if($value < self::MIN_TYPE or $value > self::MAX_TYPE){
			return self::SKELETON;
		}

		return $value;
	}

	private function sanitizeNamedTagCompound(?CompoundTag $tag){
		if(!($tag instanceof CompoundTag)){
			return null;
		}

		if(isset($tag->BlockEntityTag) and $tag->BlockEntityTag instanceof CompoundTag){
			unset($tag->BlockEntityTag);
		}

		return $tag->getCount() > 0 ? $tag : null;
	}

	private function sanitizeStoredNamedTag(){
		if(!$this->hasCompoundTag()){
			return $this;
		}

		$tag = parent::getNamedTag();
		$tag = $this->sanitizeNamedTagCompound($tag);
		if($tag instanceof CompoundTag){
			parent::setNamedTag($tag);
		}else{
			parent::clearNamedTag();
		}

		return $this;
	}

	public function setDamage($meta){
		parent::setDamage(self::sanitizeSkullTypeValue($meta));
	}

	public function setCompoundTag($tags){
		parent::setCompoundTag($tags);
		return $this->sanitizeStoredNamedTag();
	}

	public function setNamedTag(CompoundTag $tag){
		$tag = $this->sanitizeNamedTagCompound(clone $tag);
		if($tag instanceof CompoundTag){
			parent::setNamedTag($tag);
		}else{
			parent::clearNamedTag();
		}

		return $this;
	}

	public function clearCustomBlockData(){
		if(!$this->hasCompoundTag()){
			return $this;
		}

		$tag = parent::getNamedTag();
		if($tag instanceof CompoundTag and isset($tag->BlockEntityTag) and $tag->BlockEntityTag instanceof CompoundTag){
			unset($tag->BlockEntityTag);
			if($tag->getCount() > 0){
				parent::setNamedTag($tag);
			}else{
				parent::clearNamedTag();
			}
		}

		return $this;
	}

	public function setCustomBlockData(CompoundTag $compound){
		return $this->clearCustomBlockData();
	}

	public function getCustomBlockData(){
		$this->sanitizeStoredNamedTag();
		return null;
	}

	public function getNamedTag(){
		$this->sanitizeStoredNamedTag();
		return parent::getNamedTag();
	}

	public function getCompoundTag(){
		$this->sanitizeStoredNamedTag();
		return parent::getCompoundTag();
	}

}
