<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;
use pocketmine\event\entity\EntityDamageByEntityEvent;

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

    public function onTick(){
        $cause = $this->entity->getLastDamageCause();
        if($cause instanceof EntityDamageByEntityEvent){
            $attacker = $cause->getDamager();
            $dx = $this->entity->x - $attacker->x;
            $dz = $this->entity->z - $attacker->z;
            $len = sqrt($dx * $dx + $dz * $dz);
            if($len > 0){
                $dx = ($dx / $len) * 10;
                $dz = ($dz / $len) * 10;
            }
            $fleeTarget = $this->entity->add($dx, 0, $dz);
            $this->entity->getNavigator()->moveTo($fleeTarget, $this->speed);
        }else{
            parent::onTick();
        }
		$this->swimming();
    }

    public function onEnd(){
        $this->entity->getNavigator()->clearPath();
    	parent::onEnd();
    	$this->entity->resetLastDamageCause();
    }

}