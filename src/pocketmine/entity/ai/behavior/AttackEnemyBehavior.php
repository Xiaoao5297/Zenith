<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;
use pocketmine\Player;

use pocketmine\event\entity\EntityDamageEvent;
use pocketmine\event\entity\EntityDamageByEntityEvent;

class AttackEnemyBehavior extends Behavior{

    public $speed;
    public $lookDistance = 16.0;
    public $NetworkID;
    public $enemy = null;
    public $attackCooldown = 0;
    public $attackPlayer = false;

    public function __construct(Mob $entity, array $NetWorkID, bool $attackPlayer = false, float $speed = 0.7, float $speedMultiplier = 0.75){
        parent::__construct($entity);
        $this->speed = $speed * $speedMultiplier;
        $this->NetworkID = $NetWorkID;
        $this->attackPlayer = $attackPlayer;
    }

    public function getPriority(): int{
        return 2;
    }

    public function getName() : string{
        return "AttackEnemy";
    }

    public function shouldStart() : bool{
        $entities = $this->entity->level->getEntities();
        $find = false;
        $minDist = $this->lookDistance;

        foreach($entities as $entity){
            if(!$entity->isAlive()) continue;
            $dist = $this->entity->distance($entity);
            if($dist < $minDist and in_array($entity::NETWORK_ID, $this->NetworkID, true)){
                $this->enemy = $entity;
                $minDist = $dist;
                $find = true;
            }
        }

        if($this->attackPlayer){
            foreach($this->entity->level->getPlayers() as $p){
                $dist = $this->entity->distance($p);
                if($dist < $minDist and $p->isSurvival()){
                    $this->enemy = $p;
                    $minDist = $dist;
                    $find = true;
                }
            }
        }
        return $find;
    }

    public function canContinue() : bool{
        if($this->enemy === null or !$this->enemy->isAlive()){
            return false;
        }
        if($this->enemy instanceof Player and !$this->enemy->isConnected()){
            return false;
        }
        return $this->entity->distance($this->enemy) < $this->lookDistance;
    }

    public function onTick(){
        $distance = $this->entity->distance($this->enemy);
        $this->lookAt($this->enemy);

        if($distance >= 1.5){
            $this->moveForward($this->speed);
        }elseif($this->attackCooldown <= 0){
            $damage = method_exists($this->entity, 'getHurt') ? $this->entity->getHurt() : 3;
            $this->enemy->attack($damage, new EntityDamageByEntityEvent($this->entity, $this->enemy, EntityDamageEvent::CAUSE_ENTITY_ATTACK, $damage));
            $this->attackCooldown = mt_rand(30, 40);
        }

        if($this->attackCooldown > 0){
            --$this->attackCooldown;
        }
        $this->swimming();
    }

    public function onEnd(){
        $this->enemy = null;
        $this->attackCooldown = 0;
        $this->entity->setMotion(new Vector3(0, 0, 0));
    }
}
