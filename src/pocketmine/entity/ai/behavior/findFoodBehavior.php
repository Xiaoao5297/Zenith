<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;
use pocketmine\block\Air;
use pocketmine\Player;

class findFoodBehavior extends Behavior{

    public $speed;
    public $speedMultiplier;
	
	public $lookDistance = 6.0;
	public $foodID;
	public $player = null;
	public $timeLeft;

    public function __construct(Mob $entity, int $foodID, float $speed = 0.25, float $speedMultiplier = 0.75){
        parent::__construct($entity);

        $this->speed = $speed;
        $this->speedMultiplier = $speedMultiplier;
		$this->foodID = $foodID;
    }

    public function getName() : string{
        return "觅食";
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

		$speed = $this->speed * $this->speedMultiplier;
		$this->entity->getNavigator()->moveTo($this->player, $speed);
		$this->swimming();
    }

    public function onEnd(){
        $this->entity->getNavigator()->clearPath();
        $this->entity->setMotion(new Vector3(0,0,0));
    }
}
