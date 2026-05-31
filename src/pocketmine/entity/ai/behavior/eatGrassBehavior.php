<?php
namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;
use pocketmine\block\Air;
use pocketmine\block\Grass;
use pocketmine\Player;
use pocketmine\network\protocol\EntityEventPacket;

class eatGrassBehavior extends Behavior{
	
	public $lookDistance = 6.0;
	public $foodID;
	public $player = null;
	public $timeLeft;

    public function getName() : string{
        return "吃草";
    }

    public function shouldStart() : bool{
		if(!$this->entity->hasWool() and $this->entity->getLevel()->getBlockIdAt($this->entity->x, round($this->entity->y) - 1, $this->entity->z) == 2){
			if(mt_rand(0, 400) == 22){
				$this->timeLeft = 30;
				return true;
			}
		}
		return false;
		
    }

    public function canContinue() : bool{
		--$this->timeLeft;
		if($this->timeLeft <= 0){
			return false;
		}
		return true;
        
    }

    public function onTick(){
		if($this->timeLeft == 30){
			$viewers = $this->entity->getViewers();
			foreach($viewers as $p){
				$pk = new EntityEventPacket();
				$pk->eid = $this->entity->getId();
				$pk->event = EntityEventPacket::EAT_GRASS_ANIMATION;
				$p->dataPacket($pk);
			}
		}
    }

    public function onEnd(){
		$this->entity->sethasWool(true);
		$this->entity->getLevel()->setBlockIdAt($this->entity->x, round($this->entity->y) - 1, $this->entity->z, 3);
    }
}