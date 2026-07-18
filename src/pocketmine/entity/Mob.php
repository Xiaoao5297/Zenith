<?php

namespace pocketmine\entity;

use pocketmine\entity\ai\behavior\Behavior;
use pocketmine\entity\ai\navigation\PathNavigate;
use pocketmine\level\Level;
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
     * 沿给定方向移动，自动适应地形（坡、台阶、悬崖检测）。
     * @param Vector3 $direction 水平方向向量（单位向量）
     * @param float   $step      移动步长（motion 大小）
     * @return bool true=移动成功, false=被阻挡
     */
    public function moveInDirection(Vector3 $direction, float $step): bool{
        $level = $this->getLevel();
        if($this->isInsideOfWater()){
            $this->motionY = 0.8;
        }

        $tx = (int) floor($this->x + $direction->x * ($step + 0.5));
        $ty = (int) floor($this->y);
        $tz = (int) floor($this->z + $direction->z * ($step + 0.5));

        $targetY = self::pickGroundY($level, $tx, $ty, $tz);
        if($targetY === null){
            return false;
        }

        if($this->height >= 1.0 and $level->getBlock(new Vector3($tx, $targetY + 1, $tz))->isSolid()){
            return false;
        }

        $this->motionX = $direction->x * $step;
        $this->motionZ = $direction->z * $step;

        $diff = $targetY - $ty;
        if($diff > 0){
            $this->motionY = 0.42;
        }elseif($diff < 0){
            $this->motionY = -0.2;
        }

        return true;
    }

    /**
     * 静态地形检测：找目标位置的地面 Y
     * @return int|null 可行走的 Y 层，null=不可通行
     */
    public static function pickGroundY(Level $level, int $tx, int $ty, int $tz): ?int{
        $footBlock = $level->getBlock(new Vector3($tx, $ty, $tz));

        if($footBlock->isSolid()){
            $belowSolid = $level->getBlock(new Vector3($tx, $ty - 1, $tz))->isSolid();
            if(!$belowSolid){
                return null;
            }
            $above  = $level->getBlock(new Vector3($tx, $ty + 1, $tz));
            $above2 = $level->getBlock(new Vector3($tx, $ty + 2, $tz));
            if(!$above->isSolid() and !$above2->isSolid()){
                return $ty + 1;
            }
            return null;
        }

        for($dy = 0; $dy >= -2; $dy--){
            if($level->getBlock(new Vector3($tx, $ty + $dy - 1, $tz))->isSolid()){
                return $ty + $dy;
            }
        }
        return null;
    }
}
