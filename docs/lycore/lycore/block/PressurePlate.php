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

use lycore\entity\Entity;
use lycore\item\Item;
use lycore\math\AxisAlignedBB;
use lycore\math\Vector3;
use lycore\level\Level;
use lycore\level\sound\GenericSound;
use lycore\Player;

class PressurePlate extends RedstoneSource{
	const RECHECK_DELAY = 20;

	protected $activateTime = 0;
	protected $canActivate = true;

	public function __construct($meta = 0){
		$this->meta = $meta;
	}

	public function hasEntityCollision(){
		return true;
	}

	public function getBoundingBox(){
		if($this->boundingBox === null){
			$this->boundingBox = new AxisAlignedBB($this->x + 0.125, $this->y, $this->z + 0.125, $this->x + 0.875, $this->y + 0.25, $this->z + 0.875);
		}
		return $this->boundingBox;
	}

	public function canPassThrough(){
		return true;
	}

	public function onEntityCollide(Entity $entity){
		if(!$this->getLevel()->getServer()->redstoneEnabled or !$this->canActivate or !$entity->canTriggerWalking()){
			return;
		}

		if(!$this->isActivated()){
			$this->updateState($entity);
		}
	}

	protected function computeRedstoneStrength(Entity $collidingEntity = null){
		return $this->countTriggeredEntities($collidingEntity) > 0 ? $this->maxStrength : 0;
	}

	protected function countTriggeredEntities(Entity $collidingEntity = null){
		$count = 0;
		$seen = [];

		if($collidingEntity !== null and $collidingEntity->canTriggerWalking()){
			$seen[spl_object_hash($collidingEntity)] = true;
			++$count;
		}

		foreach($this->getLevel()->getCollidingEntities($this->getBoundingBox()) as $entity){
			if(!($entity instanceof Entity) or !$entity->canTriggerWalking()){
				continue;
			}

			$hash = spl_object_hash($entity);
			if(isset($seen[$hash])){
				continue;
			}

			$seen[$hash] = true;
			++$count;
		}

		return $count;
	}

	protected function setRedstoneStrength($strength){
		$this->meta = $strength > 0 ? 1 : 0;
	}

	protected function updateState(Entity $collidingEntity = null){
		$oldStrength = $this->getStrength();
		$newStrength = $this->computeRedstoneStrength($collidingEntity);
		$wasActivated = $oldStrength > 0;
		$isActivated = $newStrength > 0;

		if($oldStrength !== $newStrength){
			$this->setRedstoneStrength($newStrength);
			$this->activateTime = $this->getLevel()->getServer()->getTick();
			$this->getLevel()->setBlock($this, $this, true, false);
			$this->getLevel()->addSound(new GenericSound($this, 1000));

			if($isActivated){
				$this->activate();
			}else{
				$this->deactivate();
			}

			$this->getLevel()->updateAroundRedstone($this);
			$this->getLevel()->updateAroundRedstone($this->getSide(Vector3::SIDE_DOWN));
		}elseif($wasActivated){
			$this->activateTime = $this->getLevel()->getServer()->getTick();
		}

		if($isActivated){
			$this->getLevel()->scheduleUpdate($this, self::RECHECK_DELAY);
		}
	}

	public function activate(array $ignore = []){
		parent::activate($ignore);

		$support = $this->getSide(Vector3::SIDE_DOWN);
		$blockAboveSupport = $support->getSide(Vector3::SIDE_UP);
		if(!$this->equals($blockAboveSupport)){
			$this->activateBlock($blockAboveSupport);
		}

		$this->activateBlock($this->getSide(Vector3::SIDE_DOWN, 2));
		$this->checkTorchOn($support, [Vector3::SIDE_UP]);
	}

	public function deactivate(array $ignore = []){
		parent::deactivate($ignore);

		$support = $this->getSide(Vector3::SIDE_DOWN);
		$blockAboveSupport = $support->getSide(Vector3::SIDE_UP);
		if(!$this->equals($blockAboveSupport)){
			$this->deactivateBlock($blockAboveSupport);
		}

		$this->deactivateBlock($this->getSide(Vector3::SIDE_DOWN, 2));
		$this->checkTorchOff($support, [Vector3::SIDE_UP]);
	}

	public function isActivated(Block $from = null){
		return ($this->meta == 0) ? false : true;
	}

	public function getStrongPower($side){
		return $side === Vector3::SIDE_UP ? $this->getWeakPower($side) : 0;
	}

	public function onUpdate($type){
		if($type === Level::BLOCK_UPDATE_NORMAL){
			$below = $this->getSide(Vector3::SIDE_DOWN);
			if($below instanceof Transparent){
				$this->getLevel()->useBreakOn($this);
				return Level::BLOCK_UPDATE_NORMAL;
			}
		}
		if($type == Level::BLOCK_UPDATE_SCHEDULED){
			if($this->isActivated()){
				$this->updateState();
			}
			return Level::BLOCK_UPDATE_SCHEDULED;
		}
		return true;
	}

	public function checkActivation(){
		if($this->isActivated()){
			$this->updateState();
		}
	}

	/*public function isCollided(){
		foreach($this->getLevel()->getEntities() as $p){
			$blocks = $p->getBlocksAround();
			if(isset($blocks[Level::blockHash($this->x, $this->y, $this->z)])) return true;
		}
		return false;
	}*/

	public function place(Item $item, Block $block, Block $target, $face, $fx, $fy, $fz, Player $player = null){
		$below = $this->getSide(Vector3::SIDE_DOWN);
		if($below instanceof Transparent) return;
		else $this->getLevel()->setBlock($block, $this, true, false);
	}

	public function onBreak(Item $item){
		if($this->isActivated()){
			$this->meta = 0;
			$this->deactivate();
		}
		$this->canActivate = false;
		$this->getLevel()->setBlock($this, new Air(), true);
	}

	public function getHardness() {
		return 0.5;
	}

	public function getResistance(){
		return 2.5;
	}
}
