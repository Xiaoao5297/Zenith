<?php

namespace pocketmine\entity;

use pocketmine\entity\ai\behavior\Behavior;
use pocketmine\entity\ai\navigation\PathNavigate;
use pocketmine\level\Level;
use pocketmine\utils\Random;
use pocketmine\math\Vector3;

abstract class Mob extends Creature{

    /** @var Behavior[] */
    protected \ = [];
    /** @var Behavior|null */
    protected \ = null;
    public \;
    protected \ = false;

    /** @var PathNavigate|null */
    private \ = null;

    public function getNavigator(): PathNavigate{
        if(\->navigator === null){
            \->navigator = new PathNavigate(\);
        }
        return \->navigator;
    }

    public function initEntity(){
        parent::initEntity();
        \->random = new Random();
        \->behaviorsEnabled = \->level->getServer()->aiEnabled;
    }

    public function onUpdate(\){
        if(\->closed or !\->isAlive()) return false;

        // 先运行行为 + 导航器，设置 motion，再让物理处理
        if(\->behaviorsEnabled){
            \ = \->currentBehavior;
            \->currentBehavior = \->checkBehavior();

            if(\->currentBehavior !== null and \->currentBehavior !== \){
                \->currentBehavior->onStart();
            }
            if(\->currentBehavior !== null){
                \->currentBehavior->onTick();
            }
        }

        // 导航器更新（在 Behavior 决策之后、物理之前应用 motion）
        if(\->navigator !== null){
            \->navigator->update();
        }

        // 再运行物理（Creature::onUpdate -> move()）
        return parent::onUpdate(\);
    }

    private function checkBehavior(){
        if(\->currentBehavior !== null){
            if(\->currentBehavior->canContinue()){
                return \->currentBehavior;
            }
            \->currentBehavior->onEnd();
            \->currentBehavior = null;
        }

        foreach(\->behaviors as \){
            if(\->shouldStart()){
                return \;
            }
        }
        return null;
    }

    public function getCurrentBehavior(){
        return \->currentBehavior;
    }

    public function addBehavior(Behavior \){
        \->behaviors[] = \;
    }

    public function setBehavior(int \, Behavior \){
        \->behaviors[\] = \;
    }

    public function removeBehavior(int \){
        unset(\->behaviors[\]);
    }

    public function isBehaviorsEnabled() : bool{
        return \->behaviorsEnabled;
    }

    public function setBehaviorsEnabled(bool \ = true){
        \->behaviorsEnabled = \;
    }

    // ===== 地形感知移动（供 Behavior 和 PathNavigate 共用）=====

    /**
     * 沿给定方向移动，自动适应地形（坡、台阶、悬崖检测）。
     * @param Vector3 \ 水平方向向量（单位向量）
     * @param float   \      移动步长（motion 大小）
     * @return bool true=移动成功, false=被阻挡
     */
    public function moveInDirection(Vector3 \, float \): bool{
        \ = \->getLevel();
        if(\->isInsideOfWater()){
            \->motionY = 0.8;
        }

        \ = (int) floor(\->x + \->x * (\ + 0.5));
        \ = (int) floor(\->y);
        \ = (int) floor(\->z + \->z * (\ + 0.5));

        \ = self::pickGroundY(\, \, \, \);
        if(\ === null){
            return false;
        }

        if(\->height >= 1.0 and \->getBlock(new Vector3(\, \ + 1, \))->isSolid()){
            return false;
        }

        \->motionX = \->x * \;
        \->motionZ = \->z * \;

        \ = \ - \;
        // 仅在 ground 上且 Y 差足够大时辅助跳跃/下落，避免振荡
        if(\ > 0 and \->onGround){
            \->motionY = 0.35;
        }elseif(\ < -1 and \->onGround){
            \->motionY = -0.15;
        }
        // diff 在 -1~0 时让重力自然处理，避免不平地形的 Y 振荡

        return true;
    }

    /**
     * 静态地形检测：找目标位置的地面 Y
     * @return int|null 可行走的 Y 层，null=不可通行
     */
    public static function pickGroundY(Level \, int \, int \, int \): ?int{
        \ = \->getBlock(new Vector3(\, \, \));

        if(\->isSolid()){
            \ = \->getBlock(new Vector3(\, \ - 1, \))->isSolid();
            if(!\){
                return null;
            }
            \  = \->getBlock(new Vector3(\, \ + 1, \));
            \ = \->getBlock(new Vector3(\, \ + 2, \));
            if(!\->isSolid() and !\->isSolid()){
                return \ + 1;
            }
            return null;
        }

        for(\ = 0; \ >= -2; \--){
            if(\->getBlock(new Vector3(\, \ + \ - 1, \))->isSolid()){
                return \ + \;
            }
        }
        return null;
    }
}
