<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;
use pocketmine\block\Air;
use pocketmine\Player;

use pocketmine\event\entity\EntityDamageEvent;
use pocketmine\event\entity\EntityDamageByEntityEvent;

class attackEnemyBehavior extends Behavior{

    public $speed;
    public $speedMultiplier;
	
	public $lookDistance = 16.0;
	public $NetworkID;
	public $enemy = null;
	public $timeLeft = 0;
	public $attackPlayer = false;

    public function __construct(Mob $entity, array $NetWorkID, bool $attackPlayer = false, float $speed = 0.25, float $speedMultiplier = 0.75){
        parent::__construct($entity);

        $this->speed = $speed;
        $this->speedMultiplier = $speedMultiplier;
		$this->NetworkID = $NetWorkID;
		$this->attackPlayer = $attackPlayer;
    }

    public function getName() : string{
        return "一般敌对实体攻击";
    }

    public function shouldStart() : bool{
        $entities = $this->entity->level->getEntities();

        $find = false;
		$MinDistance = 9999;
		foreach($entities as $entity){
			if(in_array($entity::NETWORK_ID, $this->NetworkID) and $this->entity->distance($entity) < $this->lookDistance){
				if($this->entity->distance($entity) < $MinDistance){
					$this->enemy = $entity;
					$MinDistance = $this->entity->distance($entity);
					$find = true;
				}
			}
        }
		if($this->attackPlayer){
			 $players = $this->entity->level->getPlayers();
			foreach($players as $p){
				if($this->entity->distance($p) < $this->lookDistance){
					if($this->entity->distance($p) < $MinDistance){
						if($p->isSurvival()){
							$this->enemy = $p;
							$MinDistance = $this->entity->distance($p);
							$find = true;
						}
					}
				}
			}
		}
		return $find;
		
    }

    public function canContinue() : bool{
		if($this->enemy->isAlive()){
			if(($this->enemy instanceof Player) and (!$this->enemy->isConnected())){
				return false;
			}
			return $this->entity->distance($this->enemy) < $this->lookDistance;
		}else{
			return false;
		}
        
    }

    public function onTick(){
		$distance = $this->entity->distance($this->enemy);
		$this->lookAt($this->enemy);
		$entity = $this->entity;
		if($distance >= 1.5){
			if($distance < 0.5){
				return;
			}
			$speed = $this->speed * $this->speedMultiplier;
			$this->entity->getNavigator()->moveTo($this->enemy, $speed);
		}elseif($this->timeLeft == 0){
			$damage = $entity->getHurt();
			$this->enemy->attack($damage, new EntityDamageByEntityEvent($entity, $this->enemy, EntityDamageEvent::CAUSE_ENTITY_ATTACK, $damage));
			$this->timeLeft = mt_rand(30, 40);
		}

		if($this->timeLeft > 0){
			--$this->timeLeft;
		}
		$this->swimming();
    }

    public function onEnd(){
        $this->entity->getNavigator()->clearPath();
        $this->entity->setMotion(new Vector3(0,0,0));
    }

}