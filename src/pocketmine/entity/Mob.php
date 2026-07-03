<?php

namespace pocketmine\entity;

use pocketmine\entity\ai\behavior\Behavior;
use pocketmine\entity\ai\navigation\PathNavigate;
use pocketmine\utils\Random;
use pocketmine\math\Vector3;

use pocketmine\network\protocol\EntityEventPacket;

abstract class Mob extends Creature{

    protected $behaviors = [];
    /** @var Behavior | null */
    protected $currentBehavior = null;
    public $random;
    protected $behaviorsEnabled = false;

    /** @var PathNavigate|null */
    private $navigator = null;

    public function initEntity(){
        parent::initEntity();

        $this->random = new Random();
        $this->behaviorsEnabled = $this->level->getServer()->aiEnabled;
    }

    public function getNavigator(): PathNavigate{
        if($this->navigator === null){
            $this->navigator = new PathNavigate($this);
        }
        return $this->navigator;
    }

    public function getHorizDir(){
        $vec = new Vector3;

        $pitch = 0;
        $yaw = $this->yaw;
        $vec->x = -sin($yaw) * cos($pitch);
        $vec->y = -sin($pitch);
        $vec->z = sin($yaw) * cos($pitch);

        return $vec->normalize();
    }

    public function onUpdate($tick){
		$hasUpdate = parent::onUpdate($tick);
        if($this->closed or !$this->isAlive()) return false;

        if($this->behaviorsEnabled) {
            $this->currentBehavior = $this->checkBehavior();

            if ($this->currentBehavior != null) {
                $this->currentBehavior->onTick();
            }
        }

        // 寻路导航器更新（在 Behavior 之后、物理移动之前）
        if($this->navigator !== null){
            $this->navigator->update();
        }

        return $hasUpdate;
    }

    private function checkBehavior(){
        foreach($this->behaviors as $index => $behavior){
            if($behavior == $this->currentBehavior){
                if($behavior->canContinue()){
                    return $behavior;
                }

                $behavior->onEnd();
                $this->currentBehavior = null;
            }

            if($behavior->shouldStart()){
                if($this->currentBehavior == null or (array_search($this->currentBehavior, $this->behaviors)) > $index){
                    if($this->currentBehavior != null){
                        $this->currentBehavior->onEnd();
                    }
                    return $behavior;
                }
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