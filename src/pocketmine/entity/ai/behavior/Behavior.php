<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;

abstract class Behavior{

    /** @var Mob */
    public $entity;
	public $swimmingTick;

    public function __construct(Mob $entity){
        $this->entity = $entity;
    }

    public abstract function getName() : string;

    public abstract function shouldStart() : bool;

    public abstract function onTick();

    public abstract function onEnd();

    public abstract function canContinue() : bool;
	
	public function swimming(){
		if($this->entity->isInsideOfWater()){ //实体游泳
			$airTicks = $this->entity->getDataProperty(1); //DATA_AIR
			if($this->swimmingTick <= 0){
				if($airTicks <= 175){
					$this->entity->motionY = 0.3;
					$this->swimmingTick = 0;
				}else{
					$this->entity->motionY = 0.8;
					$this->swimmingTick = 10;
				}
			}else{
				--$this->swimmingTick;
			}
		}
	}
}