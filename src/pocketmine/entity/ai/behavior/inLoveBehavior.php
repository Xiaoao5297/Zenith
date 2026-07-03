<?php

/*
 *  ______   _____    ______  __   __  ______
 * /  ___/  /  ___|  / ___  \ \ \ / / |  ____|
 * | |___  | |      | |___| |  \ / /  | |____
 * \___  \ | |      |  ___  |   / /   |  ____|
 *  ___| | | |____  | |   | |  / / \  | |____
 * /_____/  \_____| |_|   |_| /_/ \_\ |______|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Sunch233#3226 QQ2125696621 And KKK
 * @link https://github.com/ScaxeTeam/Scaxe/
 *
*/

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;
use pocketmine\block\Air;
use pocketmine\Player;

use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\DoubleTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\nbt\tag\FloatTag;

class inLoveBehavior extends Behavior{

    public $speed;
    public $speedMultiplier;
	public $timeLeft;
	
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
		return $this->timeLeft-- > 0 and $this->inLoveEntity->isAlive();
        
    }

    public function onTick(){
		$this->lookAt($this->inLoveEntity);

		if($this->entity->distance($this->inLoveEntity) < 0.5){
			$this->inLovetime++;
			if($this->inLovetime >= 10){
				// ... 繁殖逻辑不变 ...
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
			return;
		}

		$speed = $this->speed * $this->speedMultiplier;
		$this->entity->getNavigator()->moveTo($this->inLoveEntity, $speed);
		$this->swimming();
    }

    public function onEnd(){
        $this->entity->getNavigator()->clearPath();
        $this->entity->setMotion(new Vector3(0,0,0));
    }
}