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

namespace lycore\entity\behavior;

use lycore\entity\Entity;
use lycore\entity\Mob;
use lycore\entity\Bot;
use lycore\Player;

use lycore\nbt\NBT;
use lycore\nbt\tag\ByteTag;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\FloatTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\LongTag;
use lycore\nbt\tag\ShortTag;
use lycore\nbt\tag\StringTag;
use lycore\entity\ThrownPotion;
use lycore\entity\Arrow;
use lycore\item\enchantment\Enchantment;

class ShootPlayerBehavior extends Behavior{

    public $speed;
    public $speedMultiplier;
	
	public $lookDistance = 16.0;
	public $NetworkID;
	public $player = null;
	public $timeLeft = 0;

    public function __construct(Mob $entity, int $NetWorkID, float $speed = 0.25, float $speedMultiplier = 0.75){
        parent::__construct($entity);

        $this->speed = $speed;
        $this->speedMultiplier = $speedMultiplier;
		$this->NetworkID = $NetWorkID;
    }

    public function getName() : string{
        return "投掷类敌对实体攻击";
    }

    public function shouldStart() : bool{
		if($this->canUseCurrentTarget()){
			return true;
		}

        $players = $this->entity->level->getPlayers();

        $find = false;
		$MinDistance = 9999;
		foreach($players as $p){
			$this->trySelectPlayerTarget($p, $MinDistance, $find);
        }
		if(method_exists($this->entity->level, "getEntities")){
			foreach($this->entity->level->getEntities() as $entity){
				if($entity instanceof Bot){
					$this->trySelectPlayerTarget($entity, $MinDistance, $find);
				}
			}
		}
		return $find;
		
    }

	private function trySelectPlayerTarget(Entity $target, &$MinDistance, &$find){
		if($target === $this->entity or $target->closed or !$target->isAlive()){
			return;
		}
		if($target instanceof Player){
			if(!$target->isConnected()){
				return;
			}
			if(method_exists($target, "isSurvival") and !$target->isSurvival()){
				return;
			}
		}

		$distance = $this->entity->distance($target);
		if($distance < $this->lookDistance and $distance < $MinDistance){
			$this->player = $target;
			$MinDistance = $distance;
			$find = true;
		}
	}

	private function canUseCurrentTarget() : bool{
		if(!($this->player instanceof Entity) or !$this->player->isAlive()){
			return false;
		}
		if(($this->player instanceof Player) and !$this->player->isConnected()){
			return false;
		}
		if($this->player->closed){
			return false;
		}
		return $this->entity->distance($this->player) < $this->lookDistance;
	}

    public function canContinue() : bool{
		return $this->canUseCurrentTarget();
        
    }

    public function onTick(){
		$distance = $this->entity->distance($this->player);
		$entity = $this->entity;
		if($this->timeLeft >= 5){
			$entity->setPm1eFollowTarget($this->player);
			$entity->setPm1eMoveMultiplier($distance < 4 ? -$this->speedMultiplier : $this->speedMultiplier);
			$entity->setPm1eStayTime(0);
			if($this->timeLeft > 0){
				--$this->timeLeft;
			}
		}elseif($distance <= 10){
			$this->bowAimPitch($this->player, $this->entity);
			$this->entity->level->addEntityMovement($this->entity->chunk->getX(), $this->entity->chunk->getZ(), $this->entity->getID(), $this->entity->x, $this->entity->y + $this->entity->getEyeHeight(), $this->entity->z, $this->entity->yaw, $this->entity->pitch, $this->entity->yaw);
			if($this->timeLeft <= 0){
				if($this->NetworkID == 86){ //药水Potion
					if($distance >= 8){
						$Damage = 17; //缓慢 1分7秒
					}elseif($this->player->getHealth() >= 8){
						$Damage = 25; //中毒 33秒
					}elseif($distance <= 3){
						$Damage = 34; //虚弱 1分7秒
					}else{
						$Damage = 23; //瞬间伤害
					}
					
					//$pitch = $this->getmypitch(($this->player->getY() - $entity->getY()),  $entity->distance($pos)); //弓箭瞄准算法(?)
					//$entity->pitch = $pitch;
					$pitch = $entity->pitch;
					
					$nbt = new CompoundTag("", [
						"Pos" => new ListTag("Pos", [
							new DoubleTag("", $entity->x),
							new DoubleTag("", $entity->y + 1.62),
							new DoubleTag("", $entity->z)
						]),
						"Motion" => new ListTag("Motion", [
							new DoubleTag("", -sin($entity->yaw / 180 * M_PI) * cos($pitch / 180 * M_PI)),
							new DoubleTag("", -sin(($pitch) / 180 * M_PI)),
							new DoubleTag("", cos($entity->yaw / 180 * M_PI) * cos($pitch / 180 * M_PI))
						]),
						"Rotation" => new ListTag("Rotation", [
							new FloatTag("", $entity->yaw),
							new FloatTag("", $pitch)
						]),
						"PotionId" => new ShortTag("PotionId", $Damage),
					]);

					$f = 1.1;
					$thrownPotion = new ThrownPotion($entity->chunk, $nbt, $entity);
					$thrownPotion->setMotion($thrownPotion->getMotion()->multiply($f));
					$thrownPotion->spawnToAll();
					$this->timeLeft = 40; //每种药水以2秒的间隔投掷。
				}elseif($this->NetworkID == 80){ //Arrow
					$pitch = $this->bowAimPitch($this->player, $this->entity, 0.04);
					$nbt = new CompoundTag("", [
						"Pos" => new ListTag("Pos", [
							new DoubleTag("", $entity->x),
							new DoubleTag("", $entity->y + 1.62),
							new DoubleTag("", $entity->z)
						]),
						"Motion" => new ListTag("Motion", [
							new DoubleTag("", -sin($entity->yaw / 180 * M_PI) * cos($pitch / 180 * M_PI)),
							new DoubleTag("", -sin(($pitch) / 180 * M_PI)),
							new DoubleTag("", cos($entity->yaw / 180 * M_PI) * cos($pitch / 180 * M_PI))
						]),
						"Rotation" => new ListTag("Rotation", [
							new FloatTag("", $entity->yaw),
							new FloatTag("", $pitch)
						]),
						"Fire" => new ShortTag("Fire", $entity->isOnFire() ? 45 * 60 : 0)
					]);

					$f = 1.1;
					$Arrow = new Arrow($entity->chunk, $nbt, $entity);
					if(method_exists($entity, "getProjectileArrowItem")){
						$Arrow->setArrowItem($entity->getProjectileArrowItem());
					}
					if(method_exists($entity, "getWeapon")){
						$bow = $entity->getWeapon();
						$power = $bow->getEnchantmentLevel(Enchantment::TYPE_BOW_POWER);
						if($power > 0){
							$Arrow->setBaseDamage($Arrow->getBaseDamage() + 0.5 * $power + 0.5);
						}
						$punch = $bow->getEnchantmentLevel(Enchantment::TYPE_BOW_KNOCKBACK);
						if($punch > 0){
							$Arrow->setKnockBack(0.4 + 0.5 * $punch);
						}
						if($bow->getEnchantmentLevel(Enchantment::TYPE_BOW_FLAME) > 0){
							$Arrow->setOnFire(100);
						}
					}
					$Arrow->setMotion($Arrow->getMotion()->multiply($f));
					$Arrow->spawnToAll();
					$this->timeLeft = 40; //在简单和普通难度中每2秒发射一次，在困难难度中每1秒发射一次。
					
					//骷髅会主动逃离狼；狼会主动尝试攻击骷髅。
				}else{
					$this->timeLeft = 40;
				}
			}else{
				--$this->timeLeft;
			}
		}
		$this->swimming();
    }
	
	public function AimPlayer($palyer, $entity){
		$x = $palyer->x - $entity->x;
		$y = $palyer->y - $entity->y;
		$z = $palyer->z - $entity->z;
		
		$a = $palyer->x + 0.5;
		$b = $palyer->y;
		$c = $palyer->z + 0.5;
		$len = sqrt($x * $x + $y * $y + $z * $z);
		$y = $y / $len;
		$pitch = asin($y);
		$pitch = $pitch * 180 / M_PI;
		$pitch = -$pitch;
		$entity->pitch = $pitch;
		
	}

    public function onEnd(){
        $this->entity->setPm1eFollowTarget(null);
        $this->entity->setPm1eMoveMultiplier(1.0);
    }
	
	public function bowAimPitch($palyer, $entity, $distance = 0.07){
		
		$_0x2bf6x17f = 1;
		
		$x = $palyer->x - $entity->x;
		$y = $palyer->y - $entity->y;
		$z = $palyer->z - $entity->z;
		
		$_0x2bf6x183 = sqrt($x * $x + $z * $z);
		$_0x2bf6x184 = $distance;
		$_0x2bf6x185 = ($_0x2bf6x17f * $_0x2bf6x17f * $_0x2bf6x17f * $_0x2bf6x17f - $_0x2bf6x184 * ($_0x2bf6x184 * ($_0x2bf6x183 * $_0x2bf6x183) + 2 * $y * ($_0x2bf6x17f * $_0x2bf6x17f)));
		$pitch = -(180 / M_PI) * (atan(($_0x2bf6x17f * $_0x2bf6x17f - sqrt($_0x2bf6x185)) / ($_0x2bf6x184 * $_0x2bf6x183)));
		if(is_nan($pitch)){
			$pitch = 0;
		}
		$entity->pitch = $pitch;
		
		return $pitch;
	}
	
	
}
