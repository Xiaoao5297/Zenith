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

namespace lycore\entity\behavior;

use lycore\entity\Mob;

use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\FloatTag;

class inLoveBehavior extends Behavior{

    public $speed = null;
    public $speedMultiplier = null;
	public $timeLeft = null;
	
	public $inLoveEntity = null;
	public $inLovetime = 0;

    public function __construct(Mob $entity, float $speed = 0.25, float $speedMultiplier = 0.75){
        parent::__construct($entity);

        $this->speed = $speed;
        $this->speedMultiplier = $speedMultiplier;
    }

    public function getName() : string{
        return "繁殖";
    }

    public function shouldStart() : bool{
		if(!$this->entity->isInLove()){
			return false;
		}
		$find = false;
		foreach($this->entity->level->getNearbyEntities($this->entity->boundingBox->grow(10, 3, 10), $this->entity) as $entity){
			if(get_class($entity) == get_class($this->entity) and $entity->isInLove()){
				$find = true;
				$this->timeLeft = 200;
				$this->inLoveEntity = $entity;
			}
		}
		return $find;
		
    }

    public function canContinue() : bool{
		return $this->timeLeft-- > 0 or !$this->inLoveEntity->isAlive();
        
    }

    public function onTick(){
		if($this->entity->distance($this->inLoveEntity) < 0.5){
			$this->inLovetime++;
			if($this->inLovetime >= 10){
				$nbt = new CompoundTag("", [
							"Pos" => new ListTag("Pos", [
								new DoubleTag("", 0),
								new DoubleTag("", 0),
								new DoubleTag("", 0)
							]),
							"Motion" => new ListTag("Motion", [
								new DoubleTag("", 0),
								new DoubleTag("", 0),
								new DoubleTag("", 0)
							]),
							"Rotation" => new ListTag("Rotation", [
								new FloatTag("", 0),
								new FloatTag("", 0)
							]),
						]);
				$class = get_class($this->entity);
				$entity = new $class($this->entity->chunk, $nbt);
				$entity->setBaby(true);
				$entity->setPosition($this->entity->getPosition());
				$entity->setHealth($this->entity->getMaxHealth());
				$entity->spawnToAll();
				$this->entity->setInLove(false);
				$this->inLoveEntity->setInLove(false);
				$this->inLovetime = 0;
				$this->timeLeft = 0;
			}
			$this->entity->setPm1eFollowTarget(null);
			$this->entity->setPm1eStayTime(10);
			return;
		}

		$this->entity->setPm1eFollowTarget($this->inLoveEntity);
		$this->entity->setPm1eMoveMultiplier($this->speedMultiplier);
		$this->entity->setPm1eStayTime(0);
		$this->swimming();
    }
	
    public function onEnd(){
        $this->entity->setPm1eFollowTarget(null);
        $this->entity->setPm1eMoveMultiplier(1.0);
    }
}
