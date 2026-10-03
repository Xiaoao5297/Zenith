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


namespace lycore\item;

use lycore\block\Block;
use lycore\entity\CaveSpider;
use lycore\entity\Entity;
use lycore\entity\Human;
use lycore\entity\PigZombie;
use lycore\entity\Silverfish;
use lycore\entity\Skeleton;
use lycore\entity\Spider;
use lycore\entity\Zombie;
use lycore\event\entity\EntityDamageByEntityEvent;
use lycore\event\entity\EntityDamageEvent;
use lycore\nbt\tag\ByteTag;
use lycore\item\enchantment\Enchantment;
use lycore\Player;

abstract class Tool extends Item{
	const TIER_WOODEN = 1;
	const TIER_GOLD = 2;
	const TIER_STONE = 3;
	const TIER_IRON = 4;
	const TIER_DIAMOND = 5;

	const TYPE_NONE = 0;
	const TYPE_SWORD = 1;
	const TYPE_SHOVEL = 2;
	const TYPE_PICKAXE = 3;
	const TYPE_AXE = 4;
	const TYPE_SHEARS = 5;

	public function __construct($id, $meta = 0, $count = 1, $name = "Unknown"){
		parent::__construct($id, $meta, $count, $name);
	}

	public function getMaxStackSize() : int {
		return 1;
	}

	/**
	 * TODO: Move this to each item
	 *
	 * @param Entity|Block $object
	 * @param 1 for break|2 for Touch $type
	 *
	 * @return bool
	 */
	public function useOn($object, $type = 1)
	{
		if($this->isUnbreakable()){
			return true;
		}

		$unbreakingl = $this->getEnchantmentLevel(Enchantment::TYPE_MINING_DURABILITY);
		$unbreakingl = $unbreakingl > 3 ? 3 : $unbreakingl;
		if (mt_rand(1, $unbreakingl + 1) !== 1) {
			return true;
		}

		if ($type === 1) {
			if ($object instanceof Entity) {
				if ($this->isHoe() !== false or $this->isSword() !== false) {
					//Hoe and Sword
					$this->meta++;
					return true;
				} elseif ($this->isPickaxe() !== false or $this->isAxe() !== false or $this->isShovel() !== false) {
					//Pickaxe Axe and Shovel
					$this->meta += 2;
					return true;
				}
				return true;//Other tool do not lost durability white hitting
			} elseif ($object instanceof Block) {
				if ($this->isShears() !== false) {
					if ($object->getToolType() === Tool::TYPE_SHEARS) {//This should be checked in each block
						$this->meta++;
					}
					return true;
				} elseif ($object->getHardness() > 0) {//Sword Pickaxe Axe and Shovel
					if ($this->isSword() !== false) {
						$this->meta += 2;
						return true;
					} elseif ($this->isPickaxe() !== false or $this->isAxe() !== false or $this->isShovel() !== false) {
						$this->meta += 1;
						return true;
					}
				}
			}
		} elseif ($type === 2) {//For Touch. only trigger when OnActivate return true
			if ($this->isHoe() !== false or $this->id === self::FLINT_STEEL or $this->isShovel() !== false) {
				$this->meta++;
				return true;
			}
		}
		return true;
	}

	/**
	 * TODO: Move this to each item
	 *
	 * @return int|bool
	 */
	public function getMaxDurability(){

		$levels = [
			Tool::TIER_GOLD => 33,
			Tool::TIER_WOODEN => 60,
			Tool::TIER_STONE => 132,
			Tool::TIER_IRON => 251,
			Tool::TIER_DIAMOND => 1562,
			self::FLINT_STEEL => 65,
			self::SHEARS => 239,
			self::BOW => 385,
		];

		if(($type = $this->isPickaxe()) === false){
			if(($type = $this->isAxe()) === false){
				if(($type = $this->isSword()) === false){
					if(($type = $this->isShovel()) === false){
						if(($type = $this->isHoe()) === false){
							$type = $this->id;
						}
					}
				}
			}
		}

		return $levels[$type];
	}

	public function isUnbreakable(){
		$tag = $this->getNamedTagEntry("Unbreakable");
		return $tag !== null and $tag->getValue() > 0;
	}

	public function isPickaxe(){
		return false;
	}

	public function isAxe(){
		return false;
	}

	public function isSword(){
		return false;
	}

	public function isShovel(){
		return false;
	}

	public function isHoe(){
		return false;
	}

	public function isShears(){
		return ($this->id === self::SHEARS);
	}

	public function isTool(){
		return ($this->id === self::FLINT_STEEL or $this->id === self::SHEARS or $this->id === self::BOW or $this->isPickaxe() !== false or $this->isAxe() !== false or $this->isShovel() !== false or $this->isSword() !== false or $this->isHoe() !== false);
	}

	public static function getWeaponEnchantmentDamageBonus($damagerOrWeapon, Entity $target){
		$weapon = $damagerOrWeapon instanceof Item ? $damagerOrWeapon : null;
		if($weapon === null and $damagerOrWeapon instanceof Entity){
			$weapon = self::getWeaponFromDamager($damagerOrWeapon);
		}
		if(!($weapon instanceof Item) or !$weapon->hasEnchantments()){
			return 0;
		}

		$bonus = 0;
		$sharpness = $weapon->getEnchantmentLevel(Enchantment::TYPE_WEAPON_SHARPNESS);
		if($sharpness > 0){
			$bonus = max($bonus, 1 + max(0, $sharpness - 1) * 0.5);
		}

		$smite = $weapon->getEnchantmentLevel(Enchantment::TYPE_WEAPON_SMITE);
		if($smite > 0 and self::isUndead($target)){
			$bonus = max($bonus, 2.5 * $smite);
		}

		$arthropods = $weapon->getEnchantmentLevel(Enchantment::TYPE_WEAPON_ARTHROPODS);
		if($arthropods > 0 and self::isArthropod($target)){
			$bonus = max($bonus, 2.5 * $arthropods);
		}

		return $bonus;
	}

	public static function getWeaponKnockBackBonus($damagerOrWeapon){
		return self::getWeaponKnockBackStrength(0.4, $damagerOrWeapon) - 0.4;
	}

	public static function getWeaponKnockBackStrength($baseKnockBack, $damagerOrWeapon){
		$weapon = $damagerOrWeapon instanceof Item ? $damagerOrWeapon : null;
		if($weapon === null and $damagerOrWeapon instanceof Entity){
			$weapon = self::getWeaponFromDamager($damagerOrWeapon);
		}
		if(!($weapon instanceof Item) or !$weapon->hasEnchantments()){
			return $baseKnockBack;
		}
		$level = $weapon->getEnchantmentLevel(Enchantment::TYPE_WEAPON_KNOCKBACK);
		if($level <= 0){
			return $baseKnockBack;
		}

		return $baseKnockBack * (1.0 + 0.105 * $level);
	}

	public static function applyWeaponHitEffects(EntityDamageByEntityEvent $event){
		if($event->getCause() !== EntityDamageEvent::CAUSE_ENTITY_ATTACK or $event->isCancelled()){
			return;
		}

		$weapon = self::getWeaponFromDamager($event->getDamager());
		if(!($weapon instanceof Item) or !$weapon->hasEnchantments()){
			return;
		}

		$fire = $weapon->getEnchantmentLevel(Enchantment::TYPE_WEAPON_FIRE_ASPECT);
		if($fire > 0){
			$event->getEntity()->setOnFire(5 * $fire);
		}
	}

	private static function getWeaponFromDamager(Entity $damager){
		if($damager instanceof Player or $damager instanceof Human){
			return $damager->getInventory()->getItemInHand();
		}
		if(method_exists($damager, "getWeapon")){
			$weapon = $damager->getWeapon();
			if($weapon instanceof Item){
				return $weapon;
			}
		}
		return null;
	}

	private static function isUndead(Entity $entity){
		return $entity instanceof Zombie or $entity instanceof PigZombie or $entity instanceof Skeleton;
	}

	private static function isArthropod(Entity $entity){
		return $entity instanceof Spider or $entity instanceof CaveSpider or $entity instanceof Silverfish;
	}
}
