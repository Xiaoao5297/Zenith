<?php

namespace pocketmine\entity;

use pocketmine\entity\ai\behavior\Behavior;
use pocketmine\entity\ai\navigation\PathNavigate;
use pocketmine\utils\Random;
use pocketmine\math\Vector3;

abstract class Mob extends Creature{

    /** @var Behavior[] */
    protected $behaviors = [];
    /** @var Behavior|null */
    protected $currentBehavior = null;
    public $random;
    protected $behaviorsEnabled = false;

    /** @var PathNavigate|null */
    private $navigator = null;

    public function getNavigator(): PathNavigate{
        if($this->navigator === null){
            $this->navigator = new PathNavigate($this);
        }
        return $this->navigator;
    }

    public function initEntity(){
        parent::initEntity();
        $this->random = new Random();
        $this->behaviorsEnabled = $this->level->getServer()->aiEnabled;
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

        // 导航器更新（在 Behavior 决策之后、下一个 tick 物理之前应用 motion）
        if($this->navigator !== null){
            $this->navigator->update();
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

    // ===== 地形感知移动（供 Behavior 和 PathNavigate 共用） =====

    /**
     * 沿给定方向水平移动。
     * 先设 motion（让 Entity::move() 能处理碰撞+踏步），再返回碰撞状态。
     * 头部高度检测避免把地面当障碍。
     */
    public function moveInDirection(Vector3 $direction, float $step): bool{
        $level = $this->getLevel();
        if($this->isInsideOfWater()){
            $this->motionY = 0.8;
        }

        $this->motionX = $direction->x * $step;
        $this->motionZ = $direction->z * $step;

        // 头部高度碰撞预检（给 Behavior/Navigator 提供卡住信号）
        $tx = (int) floor($this->x + $direction->x * ($step + 0.5));
        $tz = (int) floor($this->z + $direction->z * ($step + 0.5));
        $headY = (int) floor($this->y + $this->height - 0.01);

        return !$level->getBlock(new Vector3($tx, $headY, $tz))->isSolid();
    }
}
