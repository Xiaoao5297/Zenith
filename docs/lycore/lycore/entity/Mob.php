<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝
 * ╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 *  ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: U core
 *
 * @Links:
 *  > LY Core
 *  > LY Core Project
*/

namespace lycore\entity;

use lycore\block\Fence;
use lycore\block\FenceGate;
use lycore\block\Lava;
use lycore\block\Liquid;
use lycore\block\Block;
use lycore\block\NetherBrickFence;
use lycore\block\SoulSand;
use lycore\block\Stair;
use lycore\block\StoneWall;
use lycore\block\Water;
use lycore\entity\behavior\ShootEnemyBehavior;
use lycore\entity\behavior\ShootPlayerBehavior;
use lycore\entity\behavior\attackEnemyBehavior;
use lycore\entity\behavior\Behavior;
use lycore\math\Vector3;
use lycore\network\protocol\EntityEventPacket;
use lycore\utils\Random;

abstract class Mob extends Creature{

    protected $behaviors = [];
    /** @var Behavior | null */
    protected $currentBehavior = null;
    public $random;
    protected $behaviorsEnabled = false;
    /** @var Vector3|null */
    protected $pm1eMoveTarget = null;
    /** @var bool */
    protected $pm1eGeneratedMoveTarget = false;
    /** @var Entity|null */
    protected $pm1eFollowTarget = null;
    /** @var Entity|null */
    protected $pm1eRetaliationTarget = null;
    /** @var int */
    protected $pm1eStayTime = 0;
    /** @var int */
    protected $pm1eMoveTime = 0;
    /** @var float */
    protected $pm1eMoveMultiplier = 1.0;
    /** @var int */
    protected $pm1eNoRotateTicks = 0;
    /** @var int */
    protected $pm1eKnockbackTicks = 0;

    public function initEntity(){
        parent::initEntity();

        $this->random = new Random();
        $this->behaviorsEnabled = $this->level->getServer()->aiEnabled;
        $this->stepHeight = 0.6;
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
        if($this->usesPm1eGroundAi()){
            return $this->onUpdatePm1e($tick);
        }

		$hasUpdate = parent::onUpdate($tick);
        if($this->closed or !$this->isAlive()) return false;
        
        if($this->behaviorsEnabled) {
            $this->currentBehavior = $this->checkBehavior();

            if ($this->currentBehavior != null) {
				//echo($this->currentBehavior->getName()." \n");
                $this->currentBehavior->onTick();
            }
        }

        return $hasUpdate;
    }

    public function usesPm1eGroundAi() : bool{
        return false;
    }

    public function knockBack(Entity $attacker, $damage, $x, $z, $base = 0.4){
        parent::knockBack($attacker, $damage, $x, $z, $base);

        if($this->usesPm1eGroundAi() && (abs($this->motionX) > 0.00001 || abs($this->motionY) > 0.00001 || abs($this->motionZ) > 0.00001)){
            $this->pm1eKnockbackTicks = max($this->pm1eKnockbackTicks, 6);
            $this->pm1eNoRotateTicks = max($this->pm1eNoRotateTicks, 6);
        }
    }

    protected function usesPm1eJumpingAi() : bool{
        return false;
    }

    protected function getPm1eJumpStrength() : float{
        return 0.42;
    }

    public function setPm1eFollowTarget($target = null){
        $this->pm1eFollowTarget = $target;
    }

    public function setPm1eRetaliationTarget(Entity $target = null){
        $this->pm1eRetaliationTarget = $target;
        $this->setPm1eFollowTarget($target);
        foreach($this->behaviors as $behavior){
            if($behavior instanceof attackEnemyBehavior || $behavior instanceof ShootEnemyBehavior){
                $behavior->enemy = $target;
            }
            if($behavior instanceof ShootPlayerBehavior){
                $behavior->player = $target;
            }
        }
    }

    public function getPm1eFollowTarget(){
        return $this->pm1eFollowTarget;
    }

    public function getPm1eRetaliationTarget(){
        return $this->pm1eRetaliationTarget;
    }

    protected function findPm1eMobTarget(float $maxDistanceSquared = 256.0){
        if($this->pm1eRetaliationTarget instanceof Entity){
            if($this->pm1eRetaliationTarget->closed || !$this->pm1eRetaliationTarget->isAlive() || $this->distanceSquared($this->pm1eRetaliationTarget) > $maxDistanceSquared){
                $this->setPm1eRetaliationTarget(null);
            }else{
                return $this->pm1eRetaliationTarget;
            }
        }

        $level = $this->getLevel();
        if($level === null){
            return null;
        }

        $closest = null;
        $bestDistance = $maxDistanceSquared;

        foreach($level->getPlayers() as $player){
            if(method_exists($player, "isSurvival") && method_exists($player, "isAdventure")){
                if(!$player->isSurvival() && !$player->isAdventure()){
                    continue;
                }
            }
            if(property_exists($player, "closed") && $player->closed){
                continue;
            }
            if(method_exists($player, "isAlive") && !$player->isAlive()){
                continue;
            }

            $distance = $this->distanceSquared($player);
            if($distance <= $bestDistance){
                $bestDistance = $distance;
                $closest = $player;
            }
        }

        if(method_exists($level, "getEntities")){
            foreach($level->getEntities() as $entity){
                if(!($entity instanceof Bot) || $entity === $this || $entity->closed || !$entity->isAlive()){
                    continue;
                }

                $distance = $this->distanceSquared($entity);
                if($distance <= $bestDistance){
                    $bestDistance = $distance;
                    $closest = $entity;
                }
            }
        }

        return $closest;
    }

    public function setPm1eMoveTarget(Vector3 $target = null){
        $this->pm1eMoveTarget = $target;
        $this->pm1eGeneratedMoveTarget = false;
    }

    public function getPm1eMoveTarget(){
        return $this->pm1eMoveTarget;
    }

    public function setPm1eMoveMultiplier(float $multiplier = 1.0){
        $this->pm1eMoveMultiplier = $multiplier;
    }

    public function setPm1eStayTime(int $ticks){
        $this->pm1eStayTime = max(0, $ticks);
    }

    public function setPm1eNoRotateTicks(int $ticks){
        $this->pm1eNoRotateTicks = max(0, $ticks);
    }

    public function clearPm1eIntent(){
        $this->pm1eFollowTarget = null;
        $this->pm1eRetaliationTarget = null;
        $this->pm1eMoveTarget = null;
        $this->pm1eGeneratedMoveTarget = false;
        $this->pm1eMoveMultiplier = 1.0;
        $this->pm1eNoRotateTicks = 0;
        $this->pm1eKnockbackTicks = 0;
    }

    protected function onUpdatePm1e($currentTick){
        if($this->closed){
            return false;
        }

        if($this->attackingTick > 0){
            --$this->attackingTick;
        }

        if(!$this->isAlive() and $this->hasSpawned){
            ++$this->deadTicks;
            if($this->deadTicks >= 20){
                $this->despawnFromAll();
            }
            return true;
        }

        $tickDiff = $currentTick - $this->lastUpdate;
        if($tickDiff <= 0){
            return false;
        }

        $this->lastUpdate = $currentTick;
        $this->timings->startTiming();

        $hasUpdate = $this->entityBaseTick($tickDiff);
        if($this->closed){
            $this->timings->stopTiming();
            return $hasUpdate;
        }

        if($this->isAlive()){
            if($this->behaviorsEnabled){
                $this->currentBehavior = $this->checkBehavior();

                if($this->currentBehavior !== null){
                    $this->currentBehavior->onTick();
                }
            }

            $hasUpdate = $this->tickPm1eGroundAi($tickDiff) || $hasUpdate;
            $this->updateMovement();
        }

        $this->timings->stopTiming();

        return $hasUpdate or !$this->onGround or abs($this->motionX) > 0.00001 or abs($this->motionY) > 0.00001 or abs($this->motionZ) > 0.00001;
    }

    protected function tickPm1eGroundAi(int $tickDiff) : bool{
        $level = $this->getLevel();
        if($level === null){
            return false;
        }

        $hasUpdate = false;
        $liquidType = $this->getPm1eLiquidType();
        $inLiquid = $liquidType !== null;

        if($this->pm1eNoRotateTicks > 0){
            $this->pm1eNoRotateTicks = max(0, $this->pm1eNoRotateTicks - $tickDiff);
        }
        $usingKnockbackMotion = $this->pm1eKnockbackTicks > 0 && (abs($this->motionX) > 0.00001 || abs($this->motionY) > 0.00001 || abs($this->motionZ) > 0.00001);
        if($this->pm1eKnockbackTicks > 0){
            $this->pm1eKnockbackTicks = max(0, $this->pm1eKnockbackTicks - $tickDiff);
        }
        if($this->pm1eStayTime > 0){
            $this->pm1eStayTime = max(0, $this->pm1eStayTime - $tickDiff);
        }
        if($this->pm1eMoveTime > 0){
            $this->pm1eMoveTime = max(0, $this->pm1eMoveTime - $tickDiff);
        }

        if(!($this->pm1eFollowTarget instanceof Entity) || $this->pm1eFollowTarget->closed || !$this->pm1eFollowTarget->isAlive()){
            $this->pm1eFollowTarget = null;
            if($this->pm1eRetaliationTarget !== null && (!($this->pm1eRetaliationTarget instanceof Entity) || $this->pm1eRetaliationTarget->closed || !$this->pm1eRetaliationTarget->isAlive())){
                $this->pm1eRetaliationTarget = null;
            }
        }

        if($this->pm1eFollowTarget === null){
            $this->refreshPm1eWanderTarget();
        }

        $target = $this->pm1eFollowTarget !== null ? $this->pm1eFollowTarget : $this->pm1eMoveTarget;
        $movementMultiplier = abs($this->pm1eMoveMultiplier);
        if($movementMultiplier < 0.001){
            $movementMultiplier = 0.001;
        }
        $directionSign = $this->pm1eMoveMultiplier < 0 ? -1 : 1;

        if($usingKnockbackMotion){
            $hasUpdate = true;
        }elseif($target instanceof Vector3){
            $dx = $target->x - $this->x;
            $dz = $target->z - $this->z;
            $diff = abs($dx) + abs($dz);
            $distanceSquared = ($dx * $dx) + ($dz * $dz);
            $stopDistance = ($this->width / 2 + 1);
            $stopDistanceSquared = $stopDistance * $stopDistance;

            if($this->pm1eNoRotateTicks <= 0 && $diff > 0.001){
                $this->yaw = -atan2($dx / $diff, $dz / $diff) * (180 / M_PI);
            }

            if($diff <= 0.001 || ($this->pm1eFollowTarget === null && $this->pm1eStayTime > 0) || ($directionSign > 0 && $distanceSquared <= $stopDistanceSquared)){
                $this->motionX = 0;
                $this->motionZ = 0;
            }else{
                $baseSpeed = $this->getPm1eBaseSpeed($liquidType) * $movementMultiplier;
                $this->motionX = $baseSpeed * $directionSign * ($dx / $diff);
                $this->motionZ = $baseSpeed * $directionSign * ($dz / $diff);
            }
        }else{
            $this->motionX = 0;
            $this->motionZ = 0;
        }

        $this->normalizePm1eGroundCollisionBox();
        if(!$inLiquid && $this->recoverPartialBlockStandingHeight()){
            $hasUpdate = true;
        }

        if($inLiquid){
            $jumped = false;
            if(!$usingKnockbackMotion || $this->motionY <= 0){
                $this->motionY = max($this->motionY, $this->getPm1eLiquidBuoyancy());
            }
        }else{
            $jumped = !$usingKnockbackMotion && ($this->usesPm1eJumpingAi() ? $this->checkPm1eJumpingJump() : $this->checkPm1eJump());

            if(!$jumped){
                if($this->onGround){
                    if(!$usingKnockbackMotion || $this->motionY <= 0){
                        $this->motionY = 0;
                    }
                }else{
                    $this->motionY -= $this->gravity;
                }
            }
        }

        $this->move($this->motionX, $this->motionY, $this->motionZ);
        if(!$inLiquid && $this->recoverPartialBlockStandingHeight()){
            $hasUpdate = true;
        }
        $hasUpdate = true;

        $friction = 1 - $this->drag;
        if($this->onGround and (abs($this->motionX) > 0.00001 or abs($this->motionZ) > 0.00001)){
            $groundY = $this->boundingBox instanceof \lycore\math\AxisAlignedBB ? $this->boundingBox->minY - 0.01 : $this->y - 0.01;
            $friction = $this->getLevel()->getBlock($this->temporalVector->setComponents((int) floor($this->x), (int) floor($groundY), (int) floor($this->z)))->getFrictionFactor() * $friction;
        }

        $this->motionX *= $friction;
        $this->motionZ *= $friction;
        $this->motionY *= $inLiquid ? 0.8 : 1 - $this->drag;

        if($this->onGround && !$inLiquid){
            $this->motionY *= -0.5;
        }

        return $hasUpdate;
    }

    protected function normalizePm1eGroundCollisionBox(){
        $changed = false;

        if($this->width <= 0){
            $this->width = 0.6;
            $changed = true;
        }

        if($this->height <= 0){
            $this->height = 1.8;
            $changed = true;
        }

        if($changed){
            $halfWidth = $this->width / 2;
            $this->boundingBox->setBounds(
                $this->x - $halfWidth,
                $this->y,
                $this->z - $halfWidth,
                $this->x + $halfWidth,
                $this->y + $this->height,
                $this->z + $halfWidth
            );
        }
    }

    protected function refreshPm1eWanderTarget(){
        if($this->pm1eMoveTarget instanceof Vector3 && !$this->pm1eGeneratedMoveTarget){
            return;
        }

        if($this->pm1eStayTime > 0){
            $this->pm1eMoveTarget = null;
            $this->pm1eGeneratedMoveTarget = false;
            return;
        }

        if($this->pm1eMoveTarget instanceof Vector3){
            $dx = $this->pm1eMoveTarget->x - $this->x;
            $dz = $this->pm1eMoveTarget->z - $this->z;
            if((($dx * $dx) + ($dz * $dz)) <= 4 && $this->pm1eMoveTime > 0){
                return;
            }
        }

        if($this->pm1eMoveTime > 0 && $this->pm1eMoveTarget instanceof Vector3){
            return;
        }

        if(mt_rand(1, 100) === 1){
            $this->pm1eStayTime = mt_rand(80, 200);
            $this->pm1eMoveTarget = null;
            $this->pm1eGeneratedMoveTarget = false;
            return;
        }

        $this->pm1eMoveTime = mt_rand(80, 200);
        $this->pm1eMoveTarget = new Vector3(
            $this->x + mt_rand(-30, 30),
            $this->y,
            $this->z + mt_rand(-30, 30)
        );
        $this->pm1eGeneratedMoveTarget = true;
    }

    protected function getPm1eBaseSpeed($liquidType = null) : float{
        if($liquidType === "lava"){
            return 0.02;
        }
        if($liquidType === "water"){
            return 0.05;
        }

        return 0.15;
    }

    protected function getPm1eLiquidType(){
        if($this->isInsideOfWater()){
            return "water";
        }

        $block = $this->getLevel()->getBlock(new Vector3((int) floor($this->x), (int) floor($this->y), (int) floor($this->z)));
        $blockId = method_exists($block, "getId") ? $block->getId() : null;
        if($block instanceof Lava || $blockId === Block::LAVA || $blockId === Block::STILL_LAVA){
            return "lava";
        }
        if($block instanceof Water || $blockId === Block::WATER || $blockId === Block::STILL_WATER){
            return "water";
        }
        if($block instanceof Liquid){
            return "water";
        }

        return null;
    }

    protected function getPm1eLiquidBuoyancy() : float{
        return $this->gravity * 0.3;
    }

    protected function checkPm1eJump() : bool{
        if(!$this->onGround || $this->pm1eStayTime > 0){
            return false;
        }

        $motionLengthSquared = ($this->motionX * $this->motionX) + ($this->motionZ * $this->motionZ);
        if($motionLengthSquared <= 0.0000001){
            return false;
        }

        $forwardX = $this->x + ($this->motionX >= 0 ? $this->width / 2 : -$this->width / 2) + $this->motionX;
        $forwardZ = $this->z + ($this->motionZ >= 0 ? $this->width / 2 : -$this->width / 2) + $this->motionZ;
        $nextX = (int) floor($forwardX);
        $nextY = (int) floor($this->y);
        $nextZ = (int) floor($forwardZ);

        $that = $this->getLevel()->getBlock(new Vector3($nextX, $nextY, $nextZ));
        $horizontalFacing = $this->resolvePm1eHorizontalFacing();
        $blockX = $nextX;
        $blockY = $nextY;
        $blockZ = $nextZ;

        if($that->canPassThrough()){
            switch($horizontalFacing){
                case Vector3::SIDE_EAST:
                    ++$blockX;
                    break;
                case Vector3::SIDE_WEST:
                    --$blockX;
                    break;
                case Vector3::SIDE_SOUTH:
                    ++$blockZ;
                    break;
                case Vector3::SIDE_NORTH:
                    --$blockZ;
                    break;
            }
            $block = $this->getLevel()->getBlock(new Vector3($blockX, $blockY, $blockZ));
        }else{
            $block = $that;
        }

        $down = $this->getLevel()->getBlock(new Vector3($blockX, $blockY - 1, $blockZ));

        if($this->shouldAvoidPm1eCliff($block, $down)){
            $downTwo = $this->getLevel()->getBlock(new Vector3($blockX, $blockY - 2, $blockZ));
            if($downTwo->canPassThrough()){
                $this->pm1eStayTime = max($this->pm1eStayTime, 10);
                $this->motionX = 0.0;
                $this->motionZ = 0.0;
                return false;
            }
        }

        $blockUp = $this->getLevel()->getBlock(new Vector3($blockX, $blockY + 1, $blockZ));
        $blockUpUp = $this->getLevel()->getBlock(new Vector3($blockX, $blockY + 2, $blockZ));

        if($this->isPm1eUnjumpableBarrier($block)){
            $this->pm1eStayTime = max($this->pm1eStayTime, 10);
            $this->motionX = 0.0;
            $this->motionY = 0.0;
            $this->motionZ = 0.0;
            return false;
        }

        if(
            !$block->canPassThrough() &&
            $block->getBoundingBox() !== null &&
            !$this->canPm1eStepOnto($block) &&
            !($block instanceof SoulSand) &&
            $blockUp->canPassThrough() &&
            $blockUpUp->canPassThrough()
        ){
            if($this->motionY < $this->getPm1eJumpStrength()){
                $this->motionY = $this->getPm1eJumpStrength();
            }else{
                $this->motionY += $this->gravity * 0.25;
            }
            return true;
        }

        return false;
    }

    protected function canPm1eStepOnto(Block $block) : bool{
        $bb = $block->getBoundingBox();
        if($bb === null){
            return true;
        }

        return $bb->maxY <= $this->y + $this->stepHeight + 0.001;
    }

    protected function isPm1eUnjumpableBarrier(Block $block) : bool{
        return $block->getBoundingBox() !== null && (
            $block instanceof Fence ||
            $block instanceof FenceGate ||
            $block instanceof StoneWall ||
            $block instanceof NetherBrickFence
        );
    }

    protected function shouldAvoidPm1eCliff($block, $down) : bool{
        return $this->pm1eFollowTarget === null &&
            !($this->pm1eMoveTarget instanceof Entity) &&
            $down->canPassThrough() &&
            $block->canPassThrough();
    }

    protected function resolvePm1eHorizontalFacing() : int{
        if(abs($this->motionX) > abs($this->motionZ)){
            return $this->motionX > 0 ? Vector3::SIDE_EAST : Vector3::SIDE_WEST;
        }

        return $this->motionZ > 0 ? Vector3::SIDE_SOUTH : Vector3::SIDE_NORTH;
    }

    protected function checkPm1eJumpingJump() : bool{
        if($this->isInsideOfWater()){
            $this->motionY = $this->gravity * 2;
            return true;
        }

        if(!$this->onGround){
            return false;
        }

        if(abs($this->motionX) > 0.00001 or abs($this->motionZ) > 0.00001){
            $this->motionY = $this->getPm1eJumpStrength();
            return true;
        }

        return false;
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
