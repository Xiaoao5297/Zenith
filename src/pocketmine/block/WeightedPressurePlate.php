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

/*
 * 移植自 lycore\block\WeightedPressurePlate，命名空间改为 pocketmine\block。
 * 当前核心 PressurePlate 使用 meta 强度模型，故按其 activate/deactivate 机制改写。
 */

namespace pocketmine\block;

use pocketmine\entity\Entity;
use pocketmine\entity\Item as DroppedItem;

abstract class WeightedPressurePlate extends PressurePlate{
	protected $maxStrength = 15;
	protected $maxEntities = 15;

	protected function countTriggeredEntities(Entity $collidingEntity = null){
		$count = 0;
		$seen = [];

		if($collidingEntity !== null and $collidingEntity->canTriggerWalking()){
			$seen[spl_object_hash($collidingEntity)] = true;
			$count += $this->getEntityTriggerWeight($collidingEntity);
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
			$count += $this->getEntityTriggerWeight($entity);
		}

		return $count;
	}

	private function getEntityTriggerWeight(Entity $entity){
		if($entity instanceof DroppedItem){
			return max(1, $entity->getItem()->getCount());
		}

		return 1;
	}

	protected function computeRedstoneStrength(Entity $collidingEntity = null){
		$count = $this->countTriggeredEntities($collidingEntity);
		if($count <= 0){
			return 0;
		}

		$strength = (int) ceil(($count / $this->maxEntities) * $this->maxStrength);
		return max(0, min($this->maxStrength, $strength));
	}

	public function getStrength(){
		return max(0, min($this->maxStrength, $this->meta & 0x0f));
	}

	public function onEntityCollide(Entity $entity){
		if(!$this->getLevel()->getServer()->redstoneEnabled){
			return;
		}
		$this->updatePlate($entity);
	}

	public function checkActivation(){
		$this->updatePlate();
	}

	protected function updatePlate(Entity $entity = null){
		$strength = $this->computeRedstoneStrength($entity);
		if($strength > 0){
			if($this->meta !== $strength or !$this->isActivated()){
				$this->maxStrength = $strength;
				$this->meta = $strength;
				$this->getLevel()->setBlock($this, $this, true, false);
				$this->activate();
			}
		}elseif($this->meta !== 0){
			$this->meta = 0;
			$this->getLevel()->setBlock($this, $this, true, false);
			$this->deactivate();
		}
	}
}
