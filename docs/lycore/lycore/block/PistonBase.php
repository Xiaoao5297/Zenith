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

namespace lycore\block;

use lycore\event\block\BlockPistonEvent;
use lycore\item\Item;
use lycore\item\Tool;
use lycore\level\Level;
use lycore\level\sound\PistonInSound;
use lycore\level\sound\PistonOutSound;
use lycore\math\AxisAlignedBB;
use lycore\math\Vector3;
use lycore\nbt\NBT;
use lycore\nbt\tag\ByteTag;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\FloatTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\StringTag;
use lycore\Player;
use lycore\tile\Tile;

abstract class PistonBase extends Transparent{
	const META_FACING_MASK = 0x07;
	const META_EXTENDED = 0x08;
	const MOVE_LIMIT = 12;
	const MOVE_CANCELLED = -1;

	/** @var bool */
	protected $sticky = false;

	public function __construct($meta = 0){
		$this->meta = $meta & 0x0f;
	}

	public function getHardness(){
		return 1.5;
	}

	public function getResistance(){
		return 1.5;
	}

	public function getToolType(){
		return Tool::TYPE_PICKAXE;
	}

	public function isSolid(){
		return false;
	}

	public function canBePushedByPiston(){
		return !$this->isExtended();
	}

	public function canBePulledByPiston(){
		return !$this->isExtended();
	}

	public function isSticky(){
		return $this->sticky;
	}

	public function getFacing(){
		$facing = $this->meta & self::META_FACING_MASK;
		if($facing < Vector3::SIDE_DOWN or $facing > Vector3::SIDE_EAST){
			return Vector3::SIDE_NORTH;
		}
		return self::isHorizontalFacing($facing) ? Vector3::getOppositeSide($facing) : $facing;
	}

	public function setFacing($facing){
		$stored = self::logicFacingToMeta($facing);
		$this->meta = ($this->meta & self::META_EXTENDED) | ($stored & self::META_FACING_MASK);
	}

	public function isExtended(){
		if($this->isValid()){
			$face = $this->getFacing();
			$head = $this->getSide($face);
			if($head instanceof PistonHead){
				return $head->getFacing() === $face;
			}

			if($head->getId() === self::MOVING_BLOCK){
				return ($this->meta & self::META_EXTENDED) === self::META_EXTENDED;
			}

			return false;
		}

		return ($this->meta & self::META_EXTENDED) === self::META_EXTENDED;
	}

	public function setExtended($extended){
		if($extended){
			$this->meta |= self::META_EXTENDED;
		}else{
			$this->meta &= self::META_FACING_MASK;
		}
	}

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		if($player instanceof Player){
			$eyeY = $player->y + $player->getEyeHeight();
			if(abs($player->getFloorX() - $block->x) <= 1 and abs($player->getFloorZ() - $block->z) <= 1){
				if($eyeY - $block->y > 2){
					$this->setFacing(Vector3::SIDE_UP);
				}elseif($block->y - $eyeY > 0){
					$this->setFacing(Vector3::SIDE_DOWN);
				}else{
					$this->setFacing(self::playerDirectionToSide($player->getDirection()));
				}
			}else{
				$this->setFacing(self::playerDirectionToSide($player->getDirection()));
			}
		}

		$this->getLevel()->setBlock($block, $this, true, true);
		$this->syncPistonArmState($this->isExtended(), []);
		$this->checkState($this->isGettingPower());
		return true;
	}

	private static function playerDirectionToSide($direction){
		switch((int) $direction){
			case 0:
				return Vector3::SIDE_WEST;
			case 1:
				return Vector3::SIDE_NORTH;
			case 2:
				return Vector3::SIDE_EAST;
			case 3:
				return Vector3::SIDE_SOUTH;
			default:
				return Vector3::SIDE_NORTH;
		}
	}

	protected static function isHorizontalFacing($facing){
		return $facing === Vector3::SIDE_NORTH or $facing === Vector3::SIDE_SOUTH or $facing === Vector3::SIDE_WEST or $facing === Vector3::SIDE_EAST;
	}

	protected static function logicFacingToMeta($facing){
		$facing = (int) $facing;
		if($facing < Vector3::SIDE_DOWN or $facing > Vector3::SIDE_EAST){
			return Vector3::SIDE_NORTH;
		}
		return self::isHorizontalFacing($facing) ? Vector3::getOppositeSide($facing) : $facing;
	}

	public function onBreak(Item $item){
		$face = $this->getFacing();
		$this->getLevel()->setBlock($this, new Air(), true, true);

		$head = $this->getSide($face);
		if($head instanceof PistonHead and $head->getFacing() === $face){
			$this->getLevel()->setBlock($head, new Air(), true, true);
		}

		return true;
	}

	public function onUpdate($type){
		if($type === Level::BLOCK_UPDATE_NORMAL or $type === Level::BLOCK_UPDATE_REDSTONE or $type === Level::BLOCK_UPDATE_MOVED){
			if(!$this->canUseRedstone()){
				return false;
			}
			if(!$this->getLevel()->isBlockTickPending($this, $this)){
				$this->getLevel()->scheduleUpdate($this, 1);
			}
			return $type;
		}

		if($type === Level::BLOCK_UPDATE_SCHEDULED){
			if(!$this->canUseRedstone()){
				return false;
			}
			$powered = $this->isGettingPower();
			$this->updateAttachedRedstoneTorches($powered);
			if($powered !== $this->isExtended() and $this->getLevel()->checkAndHandleHighFrequencyRedstoneTransition($this, "piston:" . ($powered ? "extend" : "retract"))){
				return $type;
			}
			$moved = $this->checkState($powered);
			if($powered and !$this->isExtended() and $moved !== true and $moved !== self::MOVE_CANCELLED){
				$this->getLevel()->scheduleUpdate($this, 2);
			}
			return $type;
		}

		return false;
	}

	protected function canUseRedstone(){
		if(!$this->isValid()){
			return false;
		}

		$server = $this->getLevel()->getServer();
		return !isset($server->redstoneEnabled) or $server->redstoneEnabled;
	}

	public function isGettingPower(){
		if(!$this->isValid()){
			return false;
		}

		$face = $this->getFacing();
		foreach(Block::BLOCK_SIDES as $side){
			if($side === $face){
				continue;
			}
			$block = $this->getSide($side);
			if($block->getId() === self::REDSTONE_WIRE and $block->getDamage() > 0){
				return true;
			}
			if($this->getLevel()->isSidePowered($block, $side)){
				return true;
			}
		}

		return false;
	}

	protected function checkState($powered){
		$face = $this->getFacing();
		$head = $this->getSide($face);
		$hasHead = ($head instanceof PistonHead && $head->getFacing() === $face) || ($head->getId() === self::MOVING_BLOCK && $this->isExtended());

		if($powered and !$hasHead){
			return $this->doMove(true);
		}

		if(!$powered and $hasHead){
			return $this->doMove(false);
		}

		return false;
	}

	private function updateAttachedRedstoneTorches($powered){
		foreach(Block::BLOCK_SIDES as $side){
			$torch = $this->getSide($side);
			if(!$torch instanceof RedstoneTorch){
				continue;
			}

			$isLit = $torch->getId() === self::REDSTONE_TORCH;
			$isUnlit = $torch->getId() === self::UNLIT_REDSTONE_TORCH;
			if((!$powered or !$isLit) and ($powered or !$isUnlit)){
				continue;
			}

			if($torch->getSide(self::getRedstoneTorchSupportSide($torch->getDamage()))->equals($this)){
				$torch->onUpdate(Level::BLOCK_UPDATE_REDSTONE);
			}
		}
	}

	private static function getRedstoneTorchSupportSide($meta){
		switch((int) $meta){
			case 1:
				return Vector3::SIDE_WEST;
			case 2:
				return Vector3::SIDE_EAST;
			case 3:
				return Vector3::SIDE_NORTH;
			case 4:
				return Vector3::SIDE_SOUTH;
			case 5:
			case 6:
			case 0:
			default:
				return Vector3::SIDE_DOWN;
		}
	}

	protected function doMove($extending){
		$face = $this->getFacing();
		$calculator = new PistonMoveCalculator($this, $extending);
		$canMove = $calculator->canMove();

		if(!$canMove and $extending){
			return false;
		}

		$ev = new BlockPistonEvent($this, $face, $calculator->getBlocksToMove(), $calculator->getBlocksToDestroy(), $extending);
		$this->getLevel()->getServer()->getPluginManager()->callEvent($ev);
		if($ev->isCancelled()){
			return self::MOVE_CANCELLED;
		}

		$touched = [$this->floor()];
		$entityPushPositions = [$this->getSide($face)->floor()];

		if($extending or $this->sticky){
			foreach(array_reverse($calculator->getBlocksToDestroy()) as $block){
				$item = Item::get(Item::AIR, 0, 0);
				$this->getLevel()->useBreakOn($block, $item);
				$touched[] = $block->floor();
			}
		}

		$moveDirection = $extending ? $face : Vector3::getOppositeSide($face);
		$moved = [];
		$attachedBlocks = [];
		if($canMove and ($extending or $this->sticky)){
			foreach($calculator->getBlocksToMove() as $block){
				if($block->getId() === self::AIR){
					continue;
				}
				$oldPos = $block->floor();
				$newPos = $block->getSide($moveDirection)->floor();
				$tile = $this->captureMovableTile($block, $newPos);
				$attachedBlocks[] = $oldPos;
				$moved[] = [
					"old" => $oldPos,
					"new" => $newPos,
					"block" => Block::get($block->getId(), $block->getDamage()),
					"tile" => $tile,
					"movingBlockNbt" => $this->createMovingBlockNbt($block, $newPos, $extending, $tile),
				];
			}
		}

		if(!$extending){
			$head = $this->getSide($face);
			if(($head instanceof PistonHead && $head->getFacing() === $face) || $head->getId() === self::MOVING_BLOCK){
				$this->getLevel()->setBlock($head, new Air(), true, false);
				$touched[] = $head->floor();
			}
		}

		foreach($moved as $move){
			if($move["tile"] !== null and isset($move["tile"]["tile"]) and method_exists($move["tile"]["tile"], "close")){
				$move["tile"]["tile"]->close();
			}
			$this->getLevel()->setBlock($move["old"], new Air(), true, false);
			$touched[] = $move["old"];
		}

		$movedBlocks = [];
		foreach($moved as $move){
			$this->syncMovingBlockState($move["new"], $move["movingBlockNbt"]);
			$this->getLevel()->setBlock($move["new"], $move["block"], true, false);
			$this->removeMovingBlockTile($move["new"]);
			if($move["tile"] !== null and isset($move["tile"]["nbt"])){
				$this->restoreMovableTile($move["new"], $move["tile"]["nbt"]);
			}
			$movedBlocks[] = $move["block"];
			$touched[] = $move["new"];
			$entityPushPositions[] = $move["new"];
		}

		$this->setExtended($extending);
		$this->getLevel()->setBlock($this, $this, true, false);

		if($extending){
			$head = $this->createHead($face);
			$headPos = $this->getSide($face);
			$this->getLevel()->setBlock($headPos, $head, true, false);
			$touched[] = $headPos->floor();
		}

		foreach($movedBlocks as $block){
			$block->onUpdate(Level::BLOCK_UPDATE_MOVED);
		}

		$this->pushEntities($entityPushPositions, $moveDirection);
		$this->updateMovedBlocks($touched);
		$this->syncPistonArmState($extending, $attachedBlocks);
		$this->playMoveSound($extending);
		return true;
	}

	private function syncMovingBlockState(Vector3 $pos, CompoundTag $nbt){
		if(method_exists($this->getLevel(), "createMovingBlockFromPistonMove")){
			$this->getLevel()->createMovingBlockFromPistonMove($pos, $nbt);
		}
	}

	private function createMovingBlockNbt(Block $block, Vector3 $newPos, $extending, $tileData = null){
		$tags = [
			new StringTag("id", Tile::MOVING_BLOCK),
			new IntTag("x", (int) $newPos->x),
			new IntTag("y", (int) $newPos->y),
			new IntTag("z", (int) $newPos->z),
			new ByteTag("isMovable", 1),
			new ByteTag("expanding", $extending ? 1 : 0),
			new IntTag("pistonPosX", (int) $this->x),
			new IntTag("pistonPosY", (int) $this->y),
			new IntTag("pistonPosZ", (int) $this->z),
			new IntTag("movingBlockId", $block->getId()),
			new IntTag("movingBlockData", $block->getDamage()),
			$this->createLegacyMovingBlockCompound("movingBlock", $block->getId(), $block->getDamage()),
		];

		if(is_array($tileData) and isset($tileData["nbt"]) and $tileData["nbt"] instanceof CompoundTag){
			$movingEntity = $this->cloneNbtTag($tileData["nbt"]);
			$movingEntity->setName("movingEntity");
			$tags[] = $movingEntity;
		}

		return new CompoundTag("", $tags);
	}

	private function createLegacyMovingBlockCompound($name, $id, $data){
		return new CompoundTag($name, [
			new IntTag("id", (int) $id),
			new IntTag("data", (int) $data),
		]);
	}

	private function removeMovingBlockTile(Vector3 $pos){
		if(!method_exists($this->getLevel(), "getTile")){
			return;
		}

		$tile = $this->getLevel()->getTile($pos);
		if(!$tile instanceof \lycore\tile\MovingBlock){
			return;
		}

		if(method_exists($tile, "close")){
			$tile->close();
		}
		if(method_exists($this->getLevel(), "removeTileObject")){
			$this->getLevel()->removeTileObject($tile);
		}
	}

	private function syncPistonArmState($extending, array $attachedBlocks){
		$nbt = $this->createPistonArmNbt($extending, $attachedBlocks);
		$pos = $this->floor();

		if(method_exists($this->getLevel(), "createPistonArmFromPistonMove")){
			$this->getLevel()->createPistonArmFromPistonMove($pos, $nbt);
			return;
		}

		if(!method_exists($this->getLevel(), "getChunk")){
			return;
		}

		$chunk = $this->getLevel()->getChunk($pos->x >> 4, $pos->z >> 4, true);
		if($chunk !== null){
			Tile::createTile(Tile::PISTON_ARM, $chunk, $nbt);
		}
	}

	private function createPistonArmNbt($extending, array $attachedBlocks){
		$progress = $extending ? 1.0 : 0.0;
		$state = $extending ? 2 : 0;

		return new CompoundTag("", [
			new StringTag("id", Tile::PISTON_ARM),
			new IntTag("x", (int) $this->x),
			new IntTag("y", (int) $this->y),
			new IntTag("z", (int) $this->z),
			new ByteTag("isMovable", 1),
			new ByteTag("State", $state),
			new ByteTag("NewState", $state),
			new FloatTag("Progress", $progress),
			new FloatTag("LastProgress", $progress),
			new ByteTag("powered", $this->isGettingPower() ? 1 : 0),
			new ByteTag("facing", $this->getFacing()),
			new ByteTag("Sticky", $this->isSticky() ? 1 : 0),
			new ByteTag("Extending", $extending ? 1 : 0),
			$this->createAttachedBlocksTag($attachedBlocks),
			new ListTag("BreakBlocks", []),
		]);
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

	private function playMoveSound($extending){
		if(!method_exists($this->getLevel(), "addSound")){
			return;
		}

		$this->getLevel()->addSound($extending ? new PistonOutSound($this) : new PistonInSound($this));
	}

	private function captureMovableTile(Block $block, Vector3 $newPos){
		if(!$block->isValid() or !method_exists($this->getLevel(), "getTile")){
			return null;
		}

		$tile = $this->getLevel()->getTile($block);
		if($tile === null){
			return null;
		}

		if($tile instanceof \lycore\tile\MovingBlock and $block->getId() !== self::MOVING_BLOCK){
			if(method_exists($tile, "close")){
				$tile->close();
			}
			if(method_exists($this->getLevel(), "removeTileObject")){
				$this->getLevel()->removeTileObject($tile);
			}
			return null;
		}

		if(method_exists($tile, "isMovable") and !$tile->isMovable()){
			return null;
		}

		if(method_exists($tile, "saveNBT")){
			$tile->saveNBT();
		}

		if(!isset($tile->namedtag)){
			return null;
		}

		$nbt = $this->cloneNbtTag($tile->namedtag);
		$nbt->x = new IntTag("x", (int) $newPos->x);
		$nbt->y = new IntTag("y", (int) $newPos->y);
		$nbt->z = new IntTag("z", (int) $newPos->z);

		return [
			"tile" => $tile,
			"nbt" => $nbt,
		];
	}

	private function cloneNbtTag($tag){
		if(!is_object($tag)){
			return $tag;
		}

		$copy = clone $tag;
		foreach($copy as $name => $value){
			if(is_object($value)){
				$copy->{$name} = $this->cloneNbtTag($value);
			}
		}

		return $copy;
	}

	private function restoreMovableTile(Vector3 $pos, $nbt){
		if(method_exists($this->getLevel(), "createTileFromPistonMove")){
			$this->getLevel()->createTileFromPistonMove($pos, $nbt);
			return;
		}

		if(!isset($nbt->id) or !method_exists($this->getLevel(), "getChunk")){
			return;
		}

		$chunk = $this->getLevel()->getChunk($pos->x >> 4, $pos->z >> 4, true);
		if($chunk !== null){
			Tile::createTile($nbt["id"], $chunk, $nbt);
		}
	}

	private function pushEntities(array $positions, $moveDirection){
		if($moveDirection === Vector3::SIDE_DOWN or !method_exists($this->getLevel(), "getNearbyEntities")){
			return;
		}

		$dx = 0;
		$dy = 0;
		$dz = 0;
		switch($moveDirection){
			case Vector3::SIDE_UP:
				$dy = 2;
				break;
			case Vector3::SIDE_NORTH:
				$dz = -1;
				break;
			case Vector3::SIDE_SOUTH:
				$dz = 1;
				break;
			case Vector3::SIDE_WEST:
				$dx = -1;
				break;
			case Vector3::SIDE_EAST:
				$dx = 1;
				break;
			default:
				return;
		}

		$moved = [];
		foreach($positions as $pos){
			$bb = new AxisAlignedBB($pos->x, $pos->y, $pos->z, $pos->x + 1, $pos->y + 1, $pos->z + 1);
			foreach($this->getLevel()->getNearbyEntities($bb) as $entity){
				if(!is_object($entity) or $entity instanceof Player){
					continue;
				}
				$hash = spl_object_hash($entity);
				if(isset($moved[$hash])){
					continue;
				}
				$moved[$hash] = true;

				if(isset($entity->closed) and $entity->closed){
					continue;
				}
				if(method_exists($entity, "canBePushedByPiston") and !$entity->canBePushedByPiston()){
					continue;
				}
				if(method_exists($entity, "onPushByPiston")){
					$entity->onPushByPiston($this);
				}
				if(method_exists($entity, "move")){
					$entity->move($dx, $dy, $dz);
				}
			}
		}
	}

	private function updateMovedBlocks(array $positions){
		$seen = [];
		foreach($positions as $pos){
			$hash = Level::blockHash($pos->x, $pos->y, $pos->z);
			if(isset($seen[$hash])){
				continue;
			}
			$seen[$hash] = true;
			$this->getLevel()->updateAround($pos);
			$this->getLevel()->updateAroundRedstone($pos, null);
		}
	}

	abstract protected function createHead($face);

	public static function canPush(Block $block, $face, $destroyBlocks, $extending){
		if($block->getId() === self::AIR){
			return true;
		}

		if($block->y < 0 or $block->y >= 128){
			return false;
		}

		if(($face === Vector3::SIDE_DOWN and $block->y === 0) or ($face === Vector3::SIDE_UP and $block->y === 127)){
			return false;
		}

		if(($extending and !$block->canBePushedByPiston()) or (!$extending and !$block->canBePulledByPiston())){
			return false;
		}

		if($block->breaksWhenMovedByPiston()){
			return $destroyBlocks or $block->canStickToPiston();
		}

		return true;
	}

	public function getDrops(Item $item) : array{
		return [
			[$this->id, 0, 1],
		];
	}
}

class PistonMoveCalculator{
	/** @var PistonBase */
	private $piston;
	private $pistonPos;
	private $blockToMove;
	private $moveDirection;
	private $extending;
	private $toMove = [];
	private $toDestroy = [];
	private $armPos;

	public function __construct(PistonBase $piston, $extending){
		$this->piston = $piston;
		$this->pistonPos = $piston->floor();
		$this->extending = (bool) $extending;
		$face = $piston->getFacing();

		if($this->extending){
			$this->moveDirection = $face;
			$this->blockToMove = $piston->getSide($face);
		}else{
			$this->armPos = $piston->getSide($face)->floor();
			$this->moveDirection = Vector3::getOppositeSide($face);
			$this->blockToMove = $piston->isSticky() ? $piston->getSide($face, 2) : null;
		}
	}

	public function canMove(){
		if(!$this->piston->isSticky() and !$this->extending){
			return true;
		}

		$this->toMove = [];
		$this->toDestroy = [];

		if(!$this->blockToMove instanceof Block){
			return true;
		}

		if(!PistonBase::canPush($this->blockToMove, $this->moveDirection, true, $this->extending)){
			return false;
		}

		if($this->blockToMove->breaksWhenMovedByPiston()){
			if($this->extending or $this->blockToMove->canStickToPiston()){
				$this->toDestroy[] = $this->blockToMove;
			}
			return true;
		}

		if(!$this->addBlockLine($this->blockToMove, $this->blockToMove->getSide($this->moveDirection), true)){
			return false;
		}

		foreach($this->toMove as $block){
			if($block->canStickBlocks() and !$this->addBranchingBlocks($block)){
				return false;
			}
		}

		return true;
	}

	private function addBlockLine(Block $origin, Block $from, $mainBlockLine){
		if($origin->getId() === Block::AIR){
			return true;
		}

		if(!$mainBlockLine and $origin->canStickBlocks() and $from->canStickBlocks() and $origin->getId() !== $from->getId()){
			return true;
		}

		if(!PistonBase::canPush($origin, $this->moveDirection, false, $this->extending)){
			return true;
		}

		if($origin->equals($this->pistonPos) or $this->contains($this->toMove, $origin)){
			return true;
		}

		if(count($this->toMove) >= PistonBase::MOVE_LIMIT){
			return false;
		}

		$this->toMove[] = $origin;
		$count = 1;
		$beStuck = [];
		$block = $origin;

		while($block->canStickBlocks()){
			$oldBlock = $block;
			$block = $origin->getSide(Vector3::getOppositeSide($this->moveDirection), $count);
			if((!$this->extending or !$mainBlockLine) and $block->canStickBlocks() and $oldBlock->canStickBlocks() and $block->getId() !== $oldBlock->getId()){
				break;
			}

			if($block->getId() === Block::AIR or !PistonBase::canPush($block, $this->moveDirection, false, $this->extending) or $block->equals($this->pistonPos)){
				break;
			}

			if($block->breaksWhenMovedByPiston() and $block->canStickToPiston()){
				$this->toDestroy[] = $block;
				break;
			}

			if($count + count($this->toMove) > PistonBase::MOVE_LIMIT){
				return false;
			}

			++$count;
			$beStuck[] = $block;
		}

		$beStuckCount = count($beStuck);
		if($beStuckCount > 0){
			$this->toMove = array_merge($this->toMove, array_reverse($beStuck));
		}

		$step = 1;
		while(true){
			$nextBlock = $origin->getSide($this->moveDirection, $step);
			$index = $this->indexOf($this->toMove, $nextBlock);
			if($index > -1){
				$this->reorderListAtCollision($beStuckCount, $index);
				for($i = 0; $i <= $index + $beStuckCount; ++$i){
					if(isset($this->toMove[$i]) and $this->toMove[$i]->canStickBlocks() and !$this->addBranchingBlocks($this->toMove[$i])){
						return false;
					}
				}
				return true;
			}

			if($nextBlock->getId() === Block::AIR or ($this->armPos instanceof Vector3 and $nextBlock->equals($this->armPos))){
				return true;
			}

			if(!PistonBase::canPush($nextBlock, $this->moveDirection, true, $this->extending) or $nextBlock->equals($this->pistonPos)){
				return false;
			}

			if($nextBlock->breaksWhenMovedByPiston()){
				$this->toDestroy[] = $nextBlock;
				return true;
			}

			if(count($this->toMove) >= PistonBase::MOVE_LIMIT){
				return false;
			}

			$this->toMove[] = $nextBlock;
			++$beStuckCount;
			++$step;
		}
	}

	private function addBranchingBlocks(Block $block){
		foreach(Block::BLOCK_SIDES as $face){
			if($this->getAxis($face) !== $this->getAxis($this->moveDirection) and !$this->addBlockLine($block->getSide($face), $block, false)){
				return false;
			}
		}

		return true;
	}

	private function getAxis($side){
		if($side === Vector3::SIDE_DOWN or $side === Vector3::SIDE_UP){
			return 1;
		}
		if($side === Vector3::SIDE_WEST or $side === Vector3::SIDE_EAST){
			return 2;
		}
		return 3;
	}

	private function reorderListAtCollision($count, $index){
		$list = array_slice($this->toMove, 0, $index);
		$list1 = $count > 0 ? array_slice($this->toMove, count($this->toMove) - $count) : [];
		$list2 = array_slice($this->toMove, $index, count($this->toMove) - $index - $count);
		$this->toMove = array_merge($list, $list1, $list2);
	}

	private function indexOf(array $blocks, Block $needle){
		foreach($blocks as $i => $block){
			if($block->equals($needle)){
				return $i;
			}
		}

		return -1;
	}

	private function contains(array $blocks, Block $needle){
		return $this->indexOf($blocks, $needle) > -1;
	}

	public function getBlocksToMove(){
		return $this->toMove;
	}

	public function getBlocksToDestroy(){
		return $this->toDestroy;
	}
}
