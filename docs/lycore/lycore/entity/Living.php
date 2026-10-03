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


use lycore\block\Block;
use lycore\event\entity\EntityDamageByChildEntityEvent;
use lycore\event\entity\EntityDamageByEntityEvent;
use lycore\event\entity\EntityDamageEvent;
use lycore\event\entity\EntityDeathEvent;
use lycore\event\entity\EntityRegainHealthEvent;
use lycore\event\Timings;
use lycore\item\enchantment\Enchantment;
use lycore\item\Item as ItemItem;
use lycore\item\Tool;
use lycore\math\Vector3;
use lycore\nbt\tag\ShortTag;
use lycore\network\Network;
use lycore\network\protocol\EntityEventPacket;

use lycore\Server;
use lycore\Player;
use lycore\utils\BlockIterator;

abstract class Living extends Entity implements Damageable{

	protected $gravity = 0.08;
	protected $drag = 0.02;

	protected const MELEE_HURT_COOLDOWN = 10;

	protected $attackTime = 0;
	
	protected $invisible = false;

	/** @var int */
	private $lastLootingLevel = 0;

	protected function initEntity(){
		parent::initEntity();

		if(isset($this->namedtag->HealF)){
			$this->namedtag->Health = new ShortTag("Health", (int) $this->namedtag["HealF"]);
			unset($this->namedtag->HealF);
		}

		if(!isset($this->namedtag->Health) or !($this->namedtag->Health instanceof ShortTag)){
			$this->namedtag->Health = new ShortTag("Health", $this->getMaxHealth());
		}
		
		if($this->namedtag["Health"] <= 0)
			$this->setHealth(20);
		else $this->setHealth($this->namedtag["Health"]);
	}

	public function setHealth($amount){
		$wasAlive = $this->isAlive();
		parent::setHealth($amount);
		if($this->isAlive() and !$wasAlive){
			$pk = new EntityEventPacket();
			$pk->eid = $this->getId();
			$pk->event = EntityEventPacket::RESPAWN;
			Server::broadcastPacket($this->hasSpawned, $pk);
		}
	}

	public function saveNBT(){
		parent::saveNBT();
		$this->namedtag->Health = new ShortTag("Health", $this->getHealth());
	}

	public abstract function getName();

	public function hasLineOfSight(Entity $entity){
		//TODO: head height
		return true;
		//return $this->getLevel()->rayTraceBlocks(Vector3::createVector($this->x, $this->y + $this->height, $this->z), Vector3::createVector($entity->x, $entity->y + $entity->height, $entity->z)) === null;
	}

	public function heal($amount, EntityRegainHealthEvent $source){
		parent::heal($amount, $source);
		if($source->isCancelled()){
			return;
		}

		$this->attackTime = 0;
	}

	public function attack($damage, EntityDamageEvent $source){
		if($source->getCause() === EntityDamageEvent::CAUSE_ENTITY_ATTACK){
			if($this->noDamageTicks > 0){
				$source->setCancelled();
			}
		}elseif($this->attackTime > 0 or $this->noDamageTicks > 0){
			$lastCause = $this->getLastDamageCause();
			if($lastCause !== null and $lastCause->getDamage() >= $damage){
				$source->setCancelled();
			}
		}
		if($source->isCancelled()){
			return false;
		}

		$this->captureLootingLevel($source);
		if(!$source->isCancelled() and !($this instanceof Player) and method_exists($this, "getArmorContents")){
			switch($source->getCause()){
				case EntityDamageEvent::CAUSE_CONTACT:
				case EntityDamageEvent::CAUSE_ENTITY_ATTACK:
				case EntityDamageEvent::CAUSE_PROJECTILE:
				case EntityDamageEvent::CAUSE_FIRE:
				case EntityDamageEvent::CAUSE_LAVA:
				case EntityDamageEvent::CAUSE_BLOCK_EXPLOSION:
				case EntityDamageEvent::CAUSE_ENTITY_EXPLOSION:
				case EntityDamageEvent::CAUSE_LIGHTNING:
					$source->setDamage(VanillaMobEquipment::applyArmorReduction($source->getDamage(), $this->getArmorContents(), $source->getCause()));
					$damage = $source->getFinalDamage();
					break;
			}
		}
        parent::attack($damage, $source);

        if($source->isCancelled()){
			$this->lastLootingLevel = 0;
            return false;
        }
		if($this->closed){
			return true;
		}

		if($source instanceof EntityDamageByEntityEvent){
			if(!($source->getDamager() instanceof Player)){
				Tool::applyWeaponHitEffects($source);
			}

			$e = $source->getDamager();
			if($source instanceof EntityDamageByChildEntityEvent){
				$e = $source->getChild();
			}

			if($e->isOnFire() > 0){
				$this->setOnFire(2 * $this->server->getDifficulty());
			}

			$deltaX = $this->x - $e->x;
			$deltaZ = $this->z - $e->z;
			$this->knockBack($e, $damage, $deltaX, $deltaZ, $source->getKnockBack());
			$this->applyThornsDamage($source, $source instanceof EntityDamageByChildEntityEvent ? $source->getDamager() : $e);
		}

		$pk = new EntityEventPacket();
		$pk->eid = $this->getId();
		$pk->event = $this->getHealth() <= 0 ? EntityEventPacket::DEATH_ANIMATION : EntityEventPacket::HURT_ANIMATION; //Ouch!
		Server::broadcastPacket($this->hasSpawned, $pk);

		$this->attackTime = self::MELEE_HURT_COOLDOWN; //0.5 seconds cooldown
		if($source->getCause() === EntityDamageEvent::CAUSE_ENTITY_ATTACK){
			$this->noDamageTicks = max($this->noDamageTicks, self::MELEE_HURT_COOLDOWN);
		}
		return true;
	}

	public function knockBack(Entity $attacker, $damage, $x, $z, $base = 0.4){
		$f = sqrt($x * $x + $z * $z);
		if($f <= 0){
			$yaw = $attacker->yaw;
			$x = sin(deg2rad($yaw));
			$z = -cos(deg2rad($yaw));
			$f = sqrt($x * $x + $z * $z);
			if($f <= 0){
				return;
			}
		}

		$f = 1 / $f;

		$motion = $this->getMotion();
		$motion->x = $motion->x * 0.5 + $x * $f * $base;
		$motion->z = $motion->z * 0.5 + $z * $f * $base;
		$motion->y = min(0.4, max(0.15, $base * 0.3));

		$this->setMotion($motion);
	}

	public function kill(){
		if(!$this->isAlive()){
			return;
		}
		parent::kill();
		$dropDisabled = $this->server->isWorldMobDeathDropsAndExperienceDisabled($this->getLevel());
		$drops = $dropDisabled ? [] : ($this->handlesLootingDrops() ? $this->getDrops() : $this->applyLootingDrops($this->getDrops()));
		$this->server->getPluginManager()->callEvent($ev = new EntityDeathEvent($this, $drops));
		if(!$dropDisabled){
			foreach($ev->getDrops() as $item){
				$this->getLevel()->dropItem($this, $item);
			}
		}
	}

	protected function getLastDamageLootingLevel() : int{
		return $this->lastLootingLevel;
	}

	protected function handlesLootingDrops() : bool{
		return false;
	}

	private function captureLootingLevel(EntityDamageEvent $source){
		$this->lastLootingLevel = 0;
		if($source instanceof EntityDamageByEntityEvent){
			$damager = $source->getDamager();
			if($source instanceof EntityDamageByChildEntityEvent){
				$damager = $source->getChild();
			}

			if($damager instanceof Player){
				$this->lastLootingLevel = min(3, $damager->getInventory()->getItemInHand()->getEnchantmentLevel(Enchantment::TYPE_WEAPON_LOOTING));
			}
		}
	}

	private function applyLootingDrops(array $drops){
		if($this->lastLootingLevel <= 0 || $this instanceof Player){
			return $drops;
		}

		foreach($drops as $item){
			if(!$item instanceof ItemItem || $item->getId() === ItemItem::AIR){
				continue;
			}

			$extra = mt_rand(0, $this->lastLootingLevel);
			if($extra <= 0){
				continue;
			}

			$bonus = clone $item;
			$bonus->setCount($extra);
			$drops[] = $bonus;
		}

		return $drops;
	}

	private function applyThornsDamage(EntityDamageEvent $source, Entity $attacker){
		if(!($this instanceof Player) || $source->getCause() === EntityDamageEvent::CAUSE_MAGIC || !$attacker->isAlive()){
			return;
		}

		$inventory = $this->getInventory();
		$armor = $inventory->getArmorContents();
		$changed = false;

		foreach($armor as $index => $item){
			$level = $item->isArmor() ? min(3, $item->getEnchantmentLevel(Enchantment::TYPE_ARMOR_THORNS)) : 0;
			if($level <= 0 || mt_rand(1, 100) > (15 * $level)){
				continue;
			}

			$damage = mt_rand(1, 4);
			$ev = new EntityDamageEvent($attacker, EntityDamageEvent::CAUSE_MAGIC, $damage);
			$attacker->attack($ev->getFinalDamage(), $ev);

			if(!$item->isUnbreakable()){
				$item->setDamage($item->getDamage() + 3);
				if($item->getDamage() >= $item->getMaxDurability()){
					$armor[$index] = ItemItem::get(ItemItem::AIR, 0, 0);
				}else{
					$armor[$index] = $item;
				}
				$changed = true;
			}

			break;
		}

		if($changed){
			$inventory->setArmorContents($armor);
		}
	}

	public function entityBaseTick($tickDiff = 1){
		Timings::$timerLivingEntityBaseTick->startTiming();

		$hasUpdate = parent::entityBaseTick($tickDiff);

		if($this->isAlive()){
			if($this->isInsideOfSolid()){
				$hasUpdate = true;
				$ev = new EntityDamageEvent($this, EntityDamageEvent::CAUSE_SUFFOCATION, 1);
				$this->attack($ev->getFinalDamage(), $ev);
			}

			if(!$this->hasEffect(Effect::WATER_BREATHING) and $this->isInsideOfWater()){
				if($this instanceof WaterAnimal){
					$this->setDataProperty(self::DATA_AIR, self::DATA_TYPE_SHORT, 300);
				}else{
					$hasUpdate = true;
					$respiration = $this instanceof Player ? $this->getInventory()->getHelmet()->getEnchantmentLevel(Enchantment::TYPE_WATER_BREATHING) : 0;
					$airLoss = $tickDiff;
					if($respiration > 0){
						$airLoss = 0;
						for($i = 0; $i < $tickDiff; ++$i){
							if(mt_rand(0, $respiration) === 0){
								++$airLoss;
							}
						}
					}
					$airTicks = $this->getDataProperty(self::DATA_AIR) - $airLoss;
					if($airTicks <= -20){
						$airTicks = 0;

						$ev = new EntityDamageEvent($this, EntityDamageEvent::CAUSE_DROWNING, 2);
						$this->attack($ev->getFinalDamage(), $ev);
					}
					$this->setDataProperty(self::DATA_AIR, self::DATA_TYPE_SHORT, $airTicks);
				}
			}else{
				if($this instanceof WaterAnimal){
					$hasUpdate = true;
					$airTicks = $this->getDataProperty(self::DATA_AIR) - $tickDiff;
					if($airTicks <= -20){
						$airTicks = 0;

						$ev = new EntityDamageEvent($this, EntityDamageEvent::CAUSE_SUFFOCATION, 2);
						$this->attack($ev->getFinalDamage(), $ev);
					}
					$this->setDataProperty(self::DATA_AIR, self::DATA_TYPE_SHORT, $airTicks);
				}else{
					$this->setDataProperty(self::DATA_AIR, self::DATA_TYPE_SHORT, 300);
				}
			}
		}

		if($this->attackTime > 0){
			$this->attackTime -= $tickDiff;
		}

		Timings::$timerLivingEntityBaseTick->stopTiming();

		return $hasUpdate;
	}

	/**
	 * @return ItemItem[]
	 */
	public function getDrops(){
		return [];
	}

	/**
	 * @param int   $maxDistance
	 * @param int   $maxLength
	 * @param array $transparent
	 *
	 * @return Block[]
	 */
	public function getLineOfSight($maxDistance, $maxLength = 0, array $transparent = []){
		if($maxDistance > 120){
			$maxDistance = 120;
		}

		if(count($transparent) === 0){
			$transparent = null;
		}

		$blocks = [];
		$nextIndex = 0;

		$itr = new BlockIterator($this->level, $this->getPosition(), $this->getDirectionVector(), $this->getEyeHeight(), $maxDistance);

		while($itr->valid()){
			$itr->next();
			$block = $itr->current();
			$blocks[$nextIndex++] = $block;

			if($maxLength !== 0 and count($blocks) > $maxLength){
				array_shift($blocks);
				--$nextIndex;
			}

			$id = $block->getId();

			if($transparent === null){
				if($id !== 0){
					break;
				}
			}else{
				if(!isset($transparent[$id])){
					break;
				}
			}
		}

		return $blocks;
	}

	/**
	 * @param int   $maxDistance
	 * @param array $transparent
	 *
	 * @return Block
	 */
	public function getTargetBlock($maxDistance, array $transparent = []){
		try{
			$block = $this->getLineOfSight($maxDistance, 1, $transparent)[0];
			if($block instanceof Block){
				return $block;
			}
		}catch (\ArrayOutOfBoundsException $e){

		}

		return null;
	}
}
