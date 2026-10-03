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

namespace lycore\entity;

use lycore\network\protocol\AddEntityPacket;
use lycore\Player;
use lycore\entity\behavior\attackEnemyBehavior;
use lycore\math\Vector3;
use lycore\item\Item;

class IronGolem extends Animal{
	const NETWORK_ID = 20;
	const PM1E_LAND_SPEED = 0.25;
	const PM1E_STUCK_TICKS_BEFORE_UNSTUCK = 3;
	const PM1E_UNSTUCK_COOLDOWN_TICKS = 12;

	public $width = 1.4;
	public $length = 1.4;
	public $height = 2.9;
	/** @var int */
	protected $pm1eIronGolemStuckTicks = 0;
	/** @var int */
	protected $pm1eIronGolemUnstuckTicks = 0;
	/** @var Vector3|null */
	protected $pm1eIronGolemUnstuckTarget = null;
	
	public function initEntity(){
		$this->setMaxHealth(100);
		
		$this->addBehavior(new attackEnemyBehavior($this, [32, 33, 34, 35, 36, 40, 44], false));
		
		parent::initEntity();
	}

	public function usesPm1eGroundAi() : bool{
		return true;
	}

	protected function getPm1eBaseSpeed($liquidType = null) : float{
		if($liquidType !== null){
			return parent::getPm1eBaseSpeed($liquidType);
		}

		return self::PM1E_LAND_SPEED;
	}

	protected function tickPm1eGroundAi(int $tickDiff) : bool{
		$this->applyIronGolemUnstuckIntent($tickDiff);

		$targetBeforeMove = $this->pm1eFollowTarget instanceof Entity ? $this->pm1eFollowTarget : $this->pm1eMoveTarget;
		$xBefore = $this->x;
		$zBefore = $this->z;
		$updated = parent::tickPm1eGroundAi($tickDiff);
		$progressSquared = (($this->x - $xBefore) * ($this->x - $xBefore)) + (($this->z - $zBefore) * ($this->z - $zBefore));

		if($targetBeforeMove instanceof Vector3 and $this->isCollidedHorizontally and $progressSquared < 0.0004){
			++$this->pm1eIronGolemStuckTicks;
			if($this->pm1eIronGolemStuckTicks >= self::PM1E_STUCK_TICKS_BEFORE_UNSTUCK){
				$this->startIronGolemUnstuck($targetBeforeMove);
			}
		}else{
			$this->pm1eIronGolemStuckTicks = 0;
		}

		return $updated;
	}

	private function applyIronGolemUnstuckIntent(int $tickDiff){
		if($this->pm1eIronGolemUnstuckTicks <= 0){
			return;
		}

		$this->pm1eIronGolemUnstuckTicks = max(0, $this->pm1eIronGolemUnstuckTicks - max(1, $tickDiff));
		if(!($this->pm1eIronGolemUnstuckTarget instanceof Vector3)){
			return;
		}

		$this->pm1eFollowTarget = null;
		$this->pm1eMoveTarget = $this->pm1eIronGolemUnstuckTarget;
		$this->pm1eGeneratedMoveTarget = false;
		$this->pm1eMoveMultiplier = 0.8;
		$this->pm1eStayTime = 0;
	}

	private function startIronGolemUnstuck(Vector3 $blockedTarget){
		$this->pm1eIronGolemStuckTicks = 0;
		$this->pm1eIronGolemUnstuckTicks = self::PM1E_UNSTUCK_COOLDOWN_TICKS;
		$this->pm1eNoRotateTicks = max($this->pm1eNoRotateTicks, self::PM1E_UNSTUCK_COOLDOWN_TICKS);

		$dx = $blockedTarget->x - $this->x;
		$dz = $blockedTarget->z - $this->z;
		$length = sqrt(($dx * $dx) + ($dz * $dz));
		if($length <= 0.0001){
			$sideX = cos($this->yaw / 180 * M_PI);
			$sideZ = sin($this->yaw / 180 * M_PI);
		}else{
			$sideX = -$dz / $length;
			$sideZ = $dx / $length;
		}
		if(((int) floor($this->x + $this->z)) % 2 !== 0){
			$sideX = -$sideX;
			$sideZ = -$sideZ;
		}

		$backX = $length > 0.0001 ? -$dx / $length : 0.0;
		$backZ = $length > 0.0001 ? -$dz / $length : 0.0;
		$this->pm1eIronGolemUnstuckTarget = new Vector3(
			$this->x + ($sideX * 2.5) + ($backX * 0.75),
			$this->y,
			$this->z + ($sideZ * 2.5) + ($backZ * 0.75)
		);
		$this->pm1eFollowTarget = null;
		$this->pm1eMoveTarget = $this->pm1eIronGolemUnstuckTarget;
		$this->pm1eGeneratedMoveTarget = false;
	}
	
	public function getName() {
		return "Iron Golem";
	}
	
	public function getHurt(){
		return mt_rand(7, 21);
	}

	/**
	 * 死亡掉落：铁锭 3-5，虞美人 0-2（与原版 BE 一致，不受抢夺影响）
	 */
	public function getDrops(){
		$drops = [
			Item::get(Item::IRON_INGOT, 0, mt_rand(3, 5))
		];
		$poppyCount = mt_rand(0, 2);
		if($poppyCount > 0){
			// API3 老核心常量一般是 POPPY；若编译报常量不存在，改成 Item::RED_FLOWER
			$drops[] = Item::get(Item::POPPY, 0, $poppyCount);
		}
		return $drops;
	}
	
	public function spawnTo(Player $player) {
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = self::NETWORK_ID;
		$pk->x = $this->x;
		$pk->y = $this->y;
		$pk->z = $this->z;
		$pk->speedX = $this->motionX;
		$pk->speedY = $this->motionY;
		$pk->speedZ = $this->motionZ;
		$pk->yaw = $this->yaw;
		$pk->pitch = $this->pitch;
		$pk->metadata = $this->dataProperties;
		$player->dataPacket($pk);

		parent::spawnTo($player);
	}
}
