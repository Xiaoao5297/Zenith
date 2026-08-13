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

    public function onUpdate($currentTick){
        if($this->closed or !$this->isAlive()) return false;

        // 先执行行为 + 然后执行导航（不干扰 motion）
        if($this->behaviorsEnabled){
            $old = $this->currentBehavior;
            $this->currentBehavior = $this->checkBehavior();

            if($this->currentBehavior !== null and $this->currentBehavior !== $old){
                $this->currentBehavior->onStart();
            }
            if($this->currentBehavior !== null){
                $this->currentBehavior->onTick();
            }
        }

        // 导航更新（在 Behavior 更新之后、父类更新之前应用 motion）
        if($this->navigator !== null){
            $this->navigator->update();
        }

        // 最后执行 Creature::onUpdate -> move()
        return parent::onUpdate($currentTick);
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

    public function setBehavior(int $index, Behavior $behavior){
        $this->behaviors[$index] = $behavior;
    }

    public function removeBehavior(int $index){
        unset($this->behaviors[$index]);
    }

    public function isBehaviorsEnabled() : bool{
        return $this->behaviorsEnabled;
    }

    public function setBehaviorsEnabled(bool $enabled = true){
        $this->behaviorsEnabled = $enabled;
    }

    // ===== 地形感知移动（供 Behavior 与 PathNavigate 使用）=====

    /**
     * 智能水平移动（自动应对地形、台阶、落差检测）
     * @param Vector3 $direction 水平方向单位向量（仅 x/z 有效）
     * @param float   $speed     移动速度（motion 大小）
     * @return bool true=移动成功, false=被阻挡
     */
    public function moveInDirection(Vector3 $direction, float $speed): bool{
        $level = $this->getLevel();
        if($this->isInsideOfWater()){
            $this->motionY = 0.8;
        }

        $dx = (int) floor($this->x + $direction->x * ($speed + 0.5));
        $dy = (int) floor($this->y);
        $dz = (int) floor($this->z + $direction->z * ($speed + 0.5));

        $groundY = self::pickGroundY($level, $dx, $dy, $dz);
        if($groundY === null){
            return false;
        }

        if($this->height >= 1.0 and $level->getBlock(new Vector3($dx, $dy + 1, $dz))->isSolid()){
            return false;
        }

        $this->motionX = $direction->x * $speed;
        $this->motionZ = $direction->z * $speed;

        $diff = $groundY - $dy;
        // 当 ground 高于 Y 足够时，触发跳跃/上升，避免卡住
        // 0.42 为标准跳跃初速（可跳 1 格高）
        if($diff > 0 and $this->onGround){
            $this->motionY = 0.42;
        }elseif($diff < -1 and $this->onGround){
            $this->motionY = -0.15;
        }
        // diff 在 -1~0 时，自然下落即可处理不平整地形的 Y 调整

        return true;
    }

    /**
     * 静态地形检测：获取目标位置的地面 Y
     * @return int|null 可站立的地面 Y 点，null=无法通行
     */
    public static function pickGroundY(Level $level, int $x, int $dy, int $z): ?int{
        $block = $level->getBlock(new Vector3($x, $dy, $z));

        if($block->isSolid()){
            $below = $level->getBlock(new Vector3($x, $dy - 1, $z))->isSolid();
            if(!$below){
                return null;
            }
            $above1 = $level->getBlock(new Vector3($x, $dy + 1, $z));
            $above2 = $level->getBlock(new Vector3($x, $dy + 2, $z));
            if(!$above1->isSolid() and !$above2->isSolid()){
                return $dy + 1;
            }
            return null;
        }

        for($y = 0; $y >= -2; $y--){
            if($level->getBlock(new Vector3($x, $dy + $y - 1, $z))->isSolid()){
                return $dy + $y;
            }
        }
        return null;
    }
}
