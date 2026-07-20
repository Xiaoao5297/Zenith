<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;
use pocketmine\block\Air;
use pocketmine\Player;

class FindFoodBehavior extends Behavior{

    public $speed;
	
	public $lookDistance = 6.0;
	public $foodID;
	public $player = null;

    public function __construct(Mob $entity, int $foodID, float $speed = 0.35){
        parent::__construct($entity);
        $this->speed = $speed;
		$this->foodID = $foodID;
    }

    public function getPriority(): int{
        return 5;
    }

    public function getName() : string{
        return "FindFood";
    }

    public function shouldStart() : bool{
        $players = $this->entity->level->getPlayers();

        $find = false;
		foreach($players as $p){
			if($p->isConnected() and $p->isAlive()){
				if($p->getItemInHand()->getID() == $this->foodID){
					if($this->entity->distance($p) < $this->lookDistance){
						$this->player = $p;
						$find = true;
						break;
					}
				}
			}
        }
		return $find;
		
    }

    public function canContinue() : bool{
		if($this->player->isConnected() and $this->player->isAlive()){
			return $this->player->getItemInHand()->getID() == $this->foodID;
		}else{
			return false;
		}
        
    }

    public function onTick(){
		$this->lookAt($this->player);

		if($this->entity->distance($this->player) < 0.5){
			return;
		}

		$this->entity->getNavigator()->moveTo($this->player, $this->speed);
		$this->swimming();
    }

    public function onEnd(){
        $this->entity->getNavigator()->clearPath();
    }
}

