<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;

class LookAtPlayerBehavior extends Behavior{

    public $player;
    public $lookDistance = 0;

    public function getName() : string{
        return "看玩家";
    }

    public function __construct(Mob $entity, float $lookDistance = 6.0){
        parent::__construct($entity);

        $this->lookDistance = $lookDistance;
    }

    public function shouldStart() : bool{
        if(rand(0,10) != 0) return false;

        $players = $this->entity->level->getPlayers();

        foreach($players as $p){
            if($this->entity->distance($p) < $this->lookDistance){
                $this->player = $p;
                break;
            }
        }

        if($this->player == null) return false;

        $this->duration = 40 + mt_rand(0,40);

        return true;
    }

    public function canContinue() : bool{
		return $this->duration-- > 0;
    }

    public function onTick(){
		$this->lookAt($this->player);
		$this->swimming();
		$this->entity->level->addEntityMovement($this->entity->chunk->getX(), $this->entity->chunk->getZ(), $this->entity->getID(), $this->entity->x, $this->entity->y + $this->entity->getEyeHeight(), $this->entity->z, $this->entity->yaw, $this->entity->pitch, $this->entity->yaw);
    }
	
    public function onEnd(){
        $this->player = null;
        $this->entity->pitch = 0;
    }

    public function toDegree($angle){
        return $angle * (180 / pi());
    }
}