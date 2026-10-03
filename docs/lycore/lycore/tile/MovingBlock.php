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

use lycore\block\Block;
use lycore\nbt\tag\ByteTag;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\StringTag;
use lycore\Player;

class MovingBlock extends Spawnable{
	/** @var Block */
	protected $movingBlock;
	/** @var CompoundTag|null */
	protected $movingBlockEntityCompound;
	/** @var CompoundTag|null */
	protected $movingBlockCompound;
	/** @var CompoundTag|null */
	protected $movingBlockExtraCompound;
	/** @var bool */
	protected $expanding = false;
	/** @var int */
	protected $pistonPosX = 0;
	/** @var int */
	protected $pistonPosY = 0;
	/** @var int */
	protected $pistonPosZ = 0;

	private function readMovingStateFromNbt(){
		$id = isset($this->namedtag->movingBlockId) ? (int) $this->namedtag["movingBlockId"] : Block::AIR;
		$meta = isset($this->namedtag->movingBlockData) ? (int) $this->namedtag["movingBlockData"] : 0;
		if(isset($this->namedtag->movingBlock) && $this->namedtag->movingBlock instanceof CompoundTag){
			list($id, $meta) = $this->readLegacyBlockState($this->namedtag->movingBlock, $id, $meta);
		}
		$this->movingBlockCompound = $this->createLegacyBlockCompound("movingBlock", $id, $meta);
		$this->movingBlockExtraCompound = null;
		$this->movingBlock = Block::get($id, $meta);
		$this->movingBlockEntityCompound = (isset($this->namedtag->movingEntity) && $this->namedtag->movingEntity instanceof CompoundTag) ? $this->cloneNamedCompound($this->namedtag->movingEntity, "movingEntity") : null;
		$this->expanding = isset($this->namedtag->expanding) && (bool) $this->namedtag["expanding"];
		$this->pistonPosX = isset($this->namedtag->pistonPosX) ? (int) $this->namedtag["pistonPosX"] : 0;
		$this->pistonPosY = isset($this->namedtag->pistonPosY) ? (int) $this->namedtag["pistonPosY"] : 0;
		$this->pistonPosZ = isset($this->namedtag->pistonPosZ) ? (int) $this->namedtag["pistonPosZ"] : 0;
		$this->movable = false;
	}

	public function spawnToAll(){
		$this->readMovingStateFromNbt();
		parent::spawnToAll();
	}

	public function getMovingBlock(){
		$this->readMovingStateFromNbt();
		return $this->movingBlock;
	}

	public function getMovingBlockEntityCompound(){
		$this->readMovingStateFromNbt();
		return $this->movingBlockEntityCompound;
	}

	public function getMovingBlockCompound(){
		$this->readMovingStateFromNbt();
		return $this->movingBlockCompound;
	}

	public function getMovingBlockExtraCompound(){
		$this->readMovingStateFromNbt();
		return $this->movingBlockExtraCompound;
	}

	public function isExpanding(){
		$this->readMovingStateFromNbt();
		return $this->expanding;
	}

	public function saveNBT(){
		parent::saveNBT();
		$this->namedtag->movingBlockId = new IntTag("movingBlockId", $this->movingBlock instanceof Block ? $this->movingBlock->getId() : Block::AIR);
		$this->namedtag->movingBlockData = new IntTag("movingBlockData", $this->movingBlock instanceof Block ? $this->movingBlock->getDamage() : 0);
		$this->namedtag->movingBlock = $this->movingBlockCompound instanceof CompoundTag ? $this->cloneNamedCompound($this->movingBlockCompound, "movingBlock") : $this->createLegacyBlockCompound("movingBlock", $this->movingBlock instanceof Block ? $this->movingBlock->getId() : Block::AIR, $this->movingBlock instanceof Block ? $this->movingBlock->getDamage() : 0);
		unset($this->namedtag->movingBlockExtra);
		$this->namedtag->expanding = new ByteTag("expanding", $this->expanding ? 1 : 0);
		$this->namedtag->pistonPosX = new IntTag("pistonPosX", $this->pistonPosX);
		$this->namedtag->pistonPosY = new IntTag("pistonPosY", $this->pistonPosY);
		$this->namedtag->pistonPosZ = new IntTag("pistonPosZ", $this->pistonPosZ);
		if($this->movingBlockEntityCompound instanceof CompoundTag){
			$this->namedtag->movingEntity = $this->cloneNamedCompound($this->movingBlockEntityCompound, "movingEntity");
		}
	}

	public function getSpawnCompound(){
		$this->readMovingStateFromNbt();
		$this->saveNBT();
		$tags = [
			new StringTag("id", $this->getSaveId()),
			new IntTag("x", $this->x),
			new IntTag("y", $this->y),
			new IntTag("z", $this->z),
			new ByteTag("isMovable", 0),
			new IntTag("movingBlockId", $this->movingBlock instanceof Block ? $this->movingBlock->getId() : Block::AIR),
			new IntTag("movingBlockData", $this->movingBlock instanceof Block ? $this->movingBlock->getDamage() : 0),
			$this->movingBlockCompound instanceof CompoundTag ? $this->cloneNamedCompound($this->movingBlockCompound, "movingBlock") : $this->createLegacyBlockCompound("movingBlock", $this->movingBlock instanceof Block ? $this->movingBlock->getId() : Block::AIR, $this->movingBlock instanceof Block ? $this->movingBlock->getDamage() : 0),
			new ByteTag("expanding", $this->expanding ? 1 : 0),
			new IntTag("pistonPosX", $this->pistonPosX),
			new IntTag("pistonPosY", $this->pistonPosY),
			new IntTag("pistonPosZ", $this->pistonPosZ),
		];
		if($this->movingBlockEntityCompound instanceof CompoundTag){
			$tags[] = $this->cloneNamedCompound($this->movingBlockEntityCompound, "movingEntity");
		}

		return new CompoundTag("", $tags);
	}

	private function cloneNamedCompound(CompoundTag $tag, $name){
		$copy = clone $tag;
		$copy->setName($name);
		return $copy;
	}

	private function createLegacyBlockCompound($name, $id, $data){
		return new CompoundTag($name, [
			new IntTag("id", (int) $id),
			new IntTag("data", (int) $data),
		]);
	}

	private function readLegacyBlockState(CompoundTag $tag, $id, $data){
		if(isset($tag->id) and $tag->id instanceof IntTag){
			$id = (int) $tag["id"];
		}
		if(isset($tag->data) and $tag->data instanceof IntTag){
			$data = (int) $tag["data"];
		}

		return [$id, $data];
	}

}
