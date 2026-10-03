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

use lycore\item\Arrow as ItemArrow;
use lycore\item\Item;
use lycore\item\Potion;
use lycore\level\format\FullChunk;
use lycore\level\particle\CriticalParticle;
use lycore\level\particle\MobSpellParticle;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\ShortTag;
use lycore\network\protocol\AddEntityPacket;
use lycore\network\protocol\ProtocolCompatibility;
use lycore\Player;

class Arrow extends Projectile{
	const NETWORK_ID = 80;

	public $width = 0.5;
	public $length = 0.5;
	public $height = 0.5;

	protected $gravity = 0.05;
	protected $drag = 0.01;

	protected $damage = 2;

	protected $isCritical;
	protected $knockBack = 0.4;
	/** @var ItemArrow */
	protected $arrowItem;

	public function __construct(FullChunk $chunk, CompoundTag $nbt, Entity $shootingEntity = null, $critical = false){
		$this->isCritical = (bool) $critical;
		parent::__construct($chunk, $nbt, $shootingEntity);

		$arrowMeta = 0;
		if(isset($this->namedtag->Potion)){
			$arrowMeta = (int) $this->namedtag["Potion"];
		}elseif(isset($this->namedtag->PotionId)){
			$arrowMeta = ItemArrow::getArrowMetaFromPotionMeta((int) $this->namedtag["PotionId"]);
		}
		$this->setArrowItem(Item::get(Item::ARROW, $arrowMeta, 1));
	}

	public function setBaseDamage($damage){
		$this->damage = max(0, (float) $damage);
	}

	public function getBaseDamage(){
		return $this->damage;
	}

	public function setKnockBack($knockBack){
		$this->knockBack = max(0, (float) $knockBack);
	}

	protected function getProjectileKnockBack(){
		return $this->knockBack;
	}

	public function setArrowItem(Item $arrow){
		if(!$arrow instanceof ItemArrow){
			$arrow = Item::get(Item::ARROW, 0, 1);
		}else{
			$arrow = clone $arrow;
			$arrow->setCount(1);
		}

		$this->arrowItem = $arrow;
	}

	public function getArrowItem() : ItemArrow{
		return clone $this->arrowItem;
	}

	public function getPotionId() : int{
		return ($this->arrowItem instanceof ItemArrow and $this->arrowItem->isTipped()) ? (int) $this->arrowItem->getDamage() : 0;
	}

	public function canBePickedUpByPlayers() : bool{
		return !(isset($this->namedtag->PVPBotArrowNoPickup) and (int) $this->namedtag["PVPBotArrowNoPickup"] === 1);
	}

	public function applyTippedArrowEffects(Entity $entity){
		if(!$this->arrowItem instanceof ItemArrow or !$this->arrowItem->isTipped()){
			return;
		}

		foreach(Potion::getArrowEffectsByMeta((int) $this->arrowItem->getPotionMeta()) as $effect){
			$entity->addEffect($effect);
		}
	}

	public function saveNBT(){
		parent::saveNBT();
		$this->namedtag->Potion = new ShortTag("Potion", $this->getPotionId());
	}

	public function onUpdate($currentTick){
		if($this->closed){
			return false;
		}

		$this->timings->startTiming();

		$hasUpdate = parent::onUpdate($currentTick);

		if(!$this->hadCollision and $this->isCritical){
			$this->level->addParticle(new CriticalParticle($this->add(
				$this->width / 2 + mt_rand(-100, 100) / 500,
				$this->height / 2 + mt_rand(-100, 100) / 500,
				$this->width / 2 + mt_rand(-100, 100) / 500)));
		}elseif($this->onGround){
			$this->isCritical = false;
		}

		if($this->arrowItem instanceof ItemArrow and $this->arrowItem->isTipped()){
			if(!$this->onGround or ($this->onGround and ($currentTick % 4) === 0)){
				$color = Potion::getColor((int) $this->arrowItem->getPotionMeta());
				$this->level->addParticle(new MobSpellParticle($this->add(
					$this->width / 2 + mt_rand(-100, 100) / 500,
					$this->height / 2 + mt_rand(-100, 100) / 500,
					$this->width / 2 + mt_rand(-100, 100) / 500), $color[0], $color[1], $color[2]), $this->getViewers());
			}
			$hasUpdate = true;
		}

		if($this->age > 1200){
			$this->kill();
			$hasUpdate = true;
		}

		$this->timings->stopTiming();

		return $hasUpdate;
	}

	public function spawnTo(Player $player){
		if(ProtocolCompatibility::isProtocol012((int) $player->getProtocol())){
			return;
		}

		$pk = new AddEntityPacket();
		$pk->type = Arrow::NETWORK_ID;
		$pk->eid = $this->getId();
		$pk->x = $this->x;
		$pk->y = $this->y;
		$pk->z = $this->z;
		$pk->speedX = $this->motionX;
		$pk->speedY = $this->motionY;
		$pk->speedZ = $this->motionZ;
		$pk->metadata = $this->dataProperties;
		$player->dataPacket($pk);

		parent::spawnTo($player);
	}
}
