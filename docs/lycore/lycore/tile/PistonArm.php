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

use lycore\math\Vector3;
use lycore\nbt\NBT;
use lycore\nbt\tag\ByteTag;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\FloatTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\StringTag;
use lycore\Player;

class PistonArm extends Spawnable{
	const STATE_RETRACTED = 0;
	const STATE_EXTENDING = 1;
	const STATE_EXTENDED = 2;
	const STATE_RETRACTING = 3;
	const MOVE_STEP = 0.25;

	/** @var int */
	protected $state = self::STATE_RETRACTED;
	/** @var int */
	protected $newState = self::STATE_RETRACTED;
	/** @var float */
	protected $progress = 0.0;
	/** @var float */
	protected $lastProgress = 0.0;
	/** @var int */
	protected $facing = Vector3::SIDE_NORTH;
	/** @var bool */
	protected $sticky = false;
	/** @var bool */
	protected $extending = false;
	/** @var bool */
	protected $powered = false;
	/** @var Vector3[] */
	protected $attachedBlocks = [];

	private function readStateFromNbt(){
		$this->state = isset($this->namedtag->State) ? (int) $this->namedtag["State"] : self::STATE_RETRACTED;
		$this->newState = isset($this->namedtag->NewState) ? (int) $this->namedtag["NewState"] : $this->state;
		$this->progress = isset($this->namedtag->Progress) ? (float) $this->namedtag["Progress"] : 0.0;
		$this->lastProgress = isset($this->namedtag->LastProgress) ? (float) $this->namedtag["LastProgress"] : $this->progress;
		$this->facing = isset($this->namedtag->facing) ? (int) $this->namedtag["facing"] : Vector3::SIDE_NORTH;
		$this->sticky = isset($this->namedtag->Sticky) && (bool) $this->namedtag["Sticky"];
		$this->extending = isset($this->namedtag->Extending) && (bool) $this->namedtag["Extending"];
		$this->powered = isset($this->namedtag->powered) && (bool) $this->namedtag["powered"];
		$this->attachedBlocks = $this->readAttachedBlocks(isset($this->namedtag->AttachedBlocks) ? $this->namedtag->AttachedBlocks : null);
		$this->movable = !($this->state === self::STATE_EXTENDING or $this->state === self::STATE_RETRACTING);
	}

	public function spawnToAll(){
		$this->readStateFromNbt();
		if($this->level === null or $this->chunk === null){
			return;
		}
		parent::spawnToAll();
	}

	private function readAttachedBlocks($tag){
		$blocks = [];
		if(!$tag instanceof ListTag){
			return $blocks;
		}

		for($i = 0; $i + 2 < count($tag); $i += 3){
			$blocks[] = new Vector3((int) $tag[$i], (int) $tag[$i + 1], (int) $tag[$i + 2]);
		}

		return $blocks;
	}

	public function preMove($extending, array $attachedBlocks, $facing = null, $sticky = null, $powered = null){
		$this->extending = (bool) $extending;
		$this->state = $this->extending ? self::STATE_EXTENDING : self::STATE_RETRACTING;
		$this->newState = $this->extending ? self::STATE_EXTENDED : self::STATE_RETRACTED;
		$this->progress = $this->extending ? 0.0 : 1.0;
		$this->lastProgress = $this->progress;
		if($facing !== null){
			$this->facing = (int) $facing;
		}
		if($sticky !== null){
			$this->sticky = (bool) $sticky;
		}
		if($powered !== null){
			$this->powered = (bool) $powered;
		}
		$this->attachedBlocks = $attachedBlocks;
		$this->movable = false;
		$this->saveNBT();
		$this->spawnToAll();
	}

	public function finishMove(){
		$this->restoreMovingBlocks();
		$this->state = $this->newState;
		$this->progress = $this->state === self::STATE_EXTENDED ? 1.0 : 0.0;
		$this->lastProgress = $this->progress;
		$this->attachedBlocks = [];
		$this->movable = true;
		$this->saveNBT();
		$this->spawnToAll();
	}

	private function restoreMovingBlocks(){
		if($this->level === null){
			return;
		}

		$pushDirection = $this->extending ? $this->facing : Vector3::getOppositeSide($this->facing);
		foreach($this->attachedBlocks as $pos){
			if(!$pos instanceof Vector3){
				continue;
			}
			$target = $pos->getSide($pushDirection);
			if(!method_exists($this->level, "getTile") or !method_exists($this->level, "setBlock")){
				continue;
			}
			$movingTile = $this->level->getTile($target);
			if(!$movingTile instanceof MovingBlock){
				continue;
			}
			$moved = $movingTile->getMovingBlock();
			if(method_exists($movingTile, "close")){
				$movingTile->close();
			}
			if(method_exists($this->level, "removeTileObject")){
				$this->level->removeTileObject($movingTile);
			}
			$this->level->setBlock($target, $moved, true, true);
			$this->restoreMovingEntityTile($movingTile, $target);
			$moved->onUpdate(\lycore\level\Level::BLOCK_UPDATE_MOVED);
		}
	}

	private function restoreMovingEntityTile(MovingBlock $movingTile, Vector3 $target){
		$nbt = $movingTile->getMovingBlockEntityCompound();
		if(!$nbt instanceof CompoundTag){
			return;
		}

		$nbt->setName("");
		$nbt->x = new IntTag("x", (int) $target->x);
		$nbt->y = new IntTag("y", (int) $target->y);
		$nbt->z = new IntTag("z", (int) $target->z);

		if(method_exists($this->level, "createTileFromPistonMove")){
			$this->level->createTileFromPistonMove($target, $nbt);
			return;
		}

		if(!isset($nbt->id) or !method_exists($this->level, "getChunk")){
			return;
		}

		$chunk = $this->level->getChunk($target->x >> 4, $target->z >> 4, true);
		if($chunk !== null){
			Tile::createTile($nbt["id"], $chunk, $nbt);
		}
	}

	public function onUpdate(){
		if($this->closed or ($this->state !== self::STATE_EXTENDING and $this->state !== self::STATE_RETRACTING)){
			return false;
		}

		if($this->extending){
			$this->progress = min(1.0, $this->progress + self::MOVE_STEP);
			$this->lastProgress = min(1.0, $this->lastProgress + self::MOVE_STEP);
		}else{
			$this->progress = max(0.0, $this->progress - self::MOVE_STEP);
			$this->lastProgress = max(0.0, $this->lastProgress - self::MOVE_STEP);
		}

		if($this->progress === $this->lastProgress and ($this->progress === 0.0 or $this->progress === 1.0)){
			$this->finishMove();
			return false;
		}

		$this->saveNBT();
		$this->spawnToAll();
		return true;
	}

	public function getState(){
		return $this->state;
	}

	public function getNewState(){
		return $this->newState;
	}

	public function getProgress(){
		return $this->progress;
	}

	public function getLastProgress(){
		return $this->lastProgress;
	}

	public function getFacing(){
		return $this->facing;
	}

	public function isSticky(){
		return $this->sticky;
	}

	public function isExtending(){
		return $this->extending;
	}

	public function isPowered(){
		return $this->powered;
	}

	public function getAttachedBlockPositions(){
		return $this->attachedBlocks;
	}

	public function saveNBT(){
		parent::saveNBT();
		$this->namedtag->State = new ByteTag("State", $this->state);
		$this->namedtag->NewState = new ByteTag("NewState", $this->newState);
		$this->namedtag->Progress = new FloatTag("Progress", $this->progress);
		$this->namedtag->LastProgress = new FloatTag("LastProgress", $this->lastProgress);
		$this->namedtag->powered = new ByteTag("powered", $this->powered ? 1 : 0);
		$this->namedtag->facing = new ByteTag("facing", $this->facing);
		$this->namedtag->Sticky = new ByteTag("Sticky", $this->sticky ? 1 : 0);
		$this->namedtag->Extending = new ByteTag("Extending", $this->extending ? 1 : 0);
		$this->namedtag->AttachedBlocks = $this->createAttachedBlocksTag($this->attachedBlocks);
	}

	public function getSpawnCompound(){
		$this->readStateFromNbt();
		$this->saveNBT();
		$nbt = new CompoundTag("", [
			new StringTag("id", $this->getSaveId()),
			new IntTag("x", $this->x),
			new IntTag("y", $this->y),
			new IntTag("z", $this->z),
			new ByteTag("isMovable", $this->movable ? 1 : 0),
			new ByteTag("State", $this->state),
			new ByteTag("NewState", $this->newState),
			new FloatTag("Progress", $this->progress),
			new FloatTag("LastProgress", $this->lastProgress),
			new ByteTag("powered", $this->powered ? 1 : 0),
			new ByteTag("facing", $this->facing),
			new ByteTag("Sticky", $this->sticky ? 1 : 0),
			new ByteTag("Extending", $this->extending ? 1 : 0),
			$this->createAttachedBlocksTag($this->attachedBlocks),
			new ListTag("BreakBlocks", []),
		]);
		$nbt->AttachedBlocks->setTagType(NBT::TAG_Int);

		return $nbt;
	}

	private function createAttachedBlocksTag(array $blocks){
		$tags = [];
		foreach($blocks as $block){
			if(!$block instanceof Vector3){
				continue;
			}
			$tags[] = new IntTag("", (int) $block->x);
			$tags[] = new IntTag("", (int) $block->y);
			$tags[] = new IntTag("", (int) $block->z);
		}

		$list = new ListTag("AttachedBlocks", $tags);
		$list->setTagType(NBT::TAG_Int);
		return $list;
	}
}
