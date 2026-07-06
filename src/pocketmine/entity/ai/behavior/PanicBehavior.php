<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;
use pocketmine\event\entity\EntityDamageByEntityEvent;

class PanicBehavior extends StrollBehavior{

    public function __construct(Mob $entity, float $speed = 1.0, int $timeout = 60){
        parent::__construct($entity, $speed, $timeout);
    }

    public function getPriority(): int{
        return 1;
    }

    public function getName() : string{
        return "Panic";
    }

    public function shouldStart() : bool{
        return $this->entity->getLastDamageCause() !== null;
    }

    public function onStart(): void{
        $cause = $this->entity->getLastDamageCause();
        if($cause instanceof EntityDamageByEntityEvent and $cause->getDamager() !== null){
            $attacker = $cause->getDamager();
            $dx = $this->entity->x - $attacker->x;
            $dz = $this->entity->z - $attacker->z;
            $len = sqrt($dx * $dx + $dz * $dz);
            if($len > 0){
                $dx = ($dx / $len) * 10;
                $dz = ($dz / $len) * 10;
            }
            $this->target = $this->entity->add($dx, 0, $dz);
        }else{
            parent::onStart();
        }
        $this->timeLeft = $this->timeout;
    }

    public function onEnd(){
        parent::onEnd();
        $this->entity->resetLastDamageCause();
    }

}