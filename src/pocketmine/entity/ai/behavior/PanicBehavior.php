<?php


namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;

class PanicBehavior extends StrollBehavior{
	
    public function __construct(Mob $entity, $speed = 1.2, $speedMultiplier = 0.75){
        parent::__construct($entity, 60, $speed, $speedMultiplier);
    }
	
    public function getName() : string{
        return "受伤之后行走";
    }

    public function shouldStart() : bool{
        return $this->entity->getLastDamageCause() != null;
    }
    
    public function onEnd(){
    	parent::onEnd();
    	$this->entity->resetLastDamageCause();
    }

}