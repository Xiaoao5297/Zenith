<?php

namespace pocketmine\entity;

use pocketmine\entity\ai\behavior\Behavior;
use pocketmine\utils\Random;
use pocketmine\math\Vector3;

abstract class Mob extends Creature{

    /** @var Behavior[] */
    protected $behaviors = [];
    /** @var Behavior|null */
    protected $currentBehavior = null;
    public $random;
    protected $behaviorsEnabled = false;

    public function initEntity(){
        parent::initEntity();

        $this->random = new Random();
        $this->behaviorsEnabled = $this->level->getServer()->aiEnabled;
    }

    public function getHorizDir(){
        $vec = new Vector3;
        $yaw = $this->yaw;
        $vec->x = -sin($yaw) * cos(0);
        $vec->y = 0;
        $vec->z = sin($yaw) * cos(0);
        return $vec->normalize();
    }

    public function onUpdate($tick){
        $hasUpdate = parent::onUpdate($tick);
        if($this->closed or !$this->isAlive()) return false;

        if($this->behaviorsEnabled){
            $prev = $this->currentBehavior;
            $this->currentBehavior = $this->checkBehavior();

            if($this->currentBehavior !== null and $this->currentBehavior !== $prev){
                $this->currentBehavior->onStart();
            }
            if($this->currentBehavior !== null){
                $this->currentBehavior->onTick();
            }
        }

        return $hasUpdate;
    }

    private function checkBehavior(){
        if($this->currentBehavior !== null){
            if($this->currentBehavior->canContinue()){
                return $this->currentBehavior;
            }
            $this->currentBehavior->onEnd();
            $this->currentBehavior = null;
        }

        foreach($this->behaviors as $behavior){
            if($behavior->shouldStart()){
                return $behavior;
            }
        }
        return null;
    }

    public function getCurrentBehavior(){
        return $this->currentBehavior;
    }

    public function addBehavior(Behavior $behavior){
        $this->behaviors[] = $behavior;
    }

    public function setBehavior(int $index, Behavior $b){
        $this->behaviors[$index] = $b;
    }

    public function removeBehavior(int $key){
        unset($this->behaviors[$key]);
    }

    public function isBehaviorsEnabled() : bool{
        return $this->behaviorsEnabled;
    }

    public function setBehaviorsEnabled(bool $value = true){
        $this->behaviorsEnabled = $value;
    }
}
