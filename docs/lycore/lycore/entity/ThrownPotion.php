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

use lycore\level\format\FullChunk;
use lycore\level\particle\SpellParticle;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\ShortTag;
use lycore\network\protocol\AddEntityPacket;
use lycore\Player;
use lycore\item\Potion;

class ThrownPotion extends Projectile{
	const NETWORK_ID = 86;

	public $width = 0.25;
	public $length = 0.25;
	public $height = 0.25;

	protected $gravity = 0.1;
	protected $drag = 0.05;

	public function __construct(FullChunk $chunk, CompoundTag $nbt, Entity $shootingEntity = null){
		if(!isset($nbt->PotionId)){
			$nbt->PotionId = new ShortTag("PotionId", Potion::AWKWARD);
		}

		parent::__construct($chunk, $nbt, $shootingEntity);

		unset($this->dataProperties[self::DATA_SHOOTER_ID]);
		$this->setDataProperty(self::DATA_POTION_ID, self::DATA_TYPE_SHORT, $this->getPotionId());
	}
	
	public function getPotionId() : int{
		return (int) $this->namedtag["PotionId"];
	}
	
	public function kill(){
		$color = Potion::getColor($this->getPotionId());
		$this->getLevel()->addParticle(new SpellParticle($this, $color[0], $color[1], $color[2]));
		$affected = [];
		foreach($this->getViewers() as $player){
			if($player->distance($this) <= 6){
				$this->applyPotionEffect($player);
				$affected[$player->getId()] = true;
			}
		}

		foreach($this->getLevel()->getNearbyEntities($this->boundingBox->grow(6, 6, 6), $this) as $entity){
			if(isset($affected[$entity->getId()]) or !($entity instanceof Living) or $entity->distance($this) > 6){
				continue;
			}

			$this->applyPotionEffect($entity);
			$affected[$entity->getId()] = true;
		}
		
		parent::kill();
	}

	protected function applyPotionEffect(Entity $entity){
		switch($this->getPotionId()) {
			case Potion::NIGHT_VISION:
				$entity->addEffect(Effect::getEffect(Effect::NIGHT_VISION)->setAmplifier(0)->setDuration(3 * 60 * 20));
				break;
			case Potion::NIGHT_VISION_T:
				$entity->addEffect(Effect::getEffect(Effect::NIGHT_VISION)->setAmplifier(0)->setDuration(6 * 60 * 20));
				break;
			case Potion::INVISIBILITY:
				$entity->addEffect(Effect::getEffect(Effect::INVISIBILITY)->setAmplifier(0)->setDuration(3 * 60 * 20));
				break;
			case Potion::INVISIBILITY_T:
				$entity->addEffect(Effect::getEffect(Effect::INVISIBILITY)->setAmplifier(0)->setDuration(6 * 60 * 20));
				break;
			case Potion::LEAPING:
				$entity->addEffect(Effect::getEffect(Effect::JUMP)->setAmplifier(0)->setDuration(3 * 60 * 20));
				break;
			case Potion::LEAPING_T:
				$entity->addEffect(Effect::getEffect(Effect::JUMP)->setAmplifier(0)->setDuration(6 * 60 * 20));
				break;
			case Potion::LEAPING_TWO:
				$entity->addEffect(Effect::getEffect(Effect::JUMP)->setAmplifier(1)->setDuration(1.5 * 60 * 20));
				break;
			case Potion::FIRE_RESISTANCE:
				$entity->addEffect(Effect::getEffect(Effect::FIRE_RESISTANCE)->setAmplifier(0)->setDuration(3 * 60 * 20));
				break;
			case Potion::FIRE_RESISTANCE_T:
				$entity->addEffect(Effect::getEffect(Effect::FIRE_RESISTANCE)->setAmplifier(0)->setDuration(6 * 60 * 20));
				break;
			case Potion::SPEED:
				$entity->addEffect(Effect::getEffect(Effect::SPEED)->setAmplifier(0)->setDuration(3 * 60 * 20));
				break;
			case Potion::SPEED_T:
				$entity->addEffect(Effect::getEffect(Effect::SPEED)->setAmplifier(0)->setDuration(6 * 60 * 20));
				break;
			case Potion::SPEED_TWO:
				$entity->addEffect(Effect::getEffect(Effect::SPEED)->setAmplifier(1)->setDuration(1.5 * 60 * 20));
				break;
			case Potion::SLOWNESS:
				$entity->addEffect(Effect::getEffect(Effect::SLOWNESS)->setAmplifier(0)->setDuration(1 * 60 * 20));
				break;
			case Potion::SLOWNESS_T:
				$entity->addEffect(Effect::getEffect(Effect::SLOWNESS)->setAmplifier(0)->setDuration(4 * 60 * 20));
				break;
			case Potion::WATER_BREATHING:
				$entity->addEffect(Effect::getEffect(Effect::WATER_BREATHING)->setAmplifier(0)->setDuration(3 * 60 * 20));
				break;
			case Potion::WATER_BREATHING_T:
				$entity->addEffect(Effect::getEffect(Effect::WATER_BREATHING)->setAmplifier(0)->setDuration(6 * 60 * 20));
				break;
			case Potion::POISON:
				$entity->addEffect(Effect::getEffect(Effect::POISON)->setAmplifier(0)->setDuration(45 * 20));
				break;
			case Potion::POISON_T:
				$entity->addEffect(Effect::getEffect(Effect::POISON)->setAmplifier(0)->setDuration(2 * 60 * 20));
				break;
			case Potion::POISON_TWO:
				$entity->addEffect(Effect::getEffect(Effect::POISON)->setAmplifier(0)->setDuration(22 * 20));
				break;
			case Potion::REGENERATION:
				$entity->addEffect(Effect::getEffect(Effect::REGENERATION)->setAmplifier(0)->setDuration(45 * 20));
				break;
			case Potion::REGENERATION_T:
				$entity->addEffect(Effect::getEffect(Effect::REGENERATION)->setAmplifier(0)->setDuration(2 * 60 * 20));
				break;
			case Potion::REGENERATION_TWO:
				$entity->addEffect(Effect::getEffect(Effect::REGENERATION)->setAmplifier(1)->setDuration(22 * 20));
				break;
			case Potion::STRENGTH:
				$entity->addEffect(Effect::getEffect(Effect::STRENGTH)->setAmplifier(0)->setDuration(3 * 60 * 20));
				break;
			case Potion::STRENGTH_T:
				$entity->addEffect(Effect::getEffect(Effect::STRENGTH)->setAmplifier(0)->setDuration(6 * 60 * 20));
				break;
			case Potion::STRENGTH_TWO:
				$entity->addEffect(Effect::getEffect(Effect::STRENGTH)->setAmplifier(1)->setDuration(1.5 * 60 * 20));
				break;
			case Potion::WEAKNESS:
				$entity->addEffect(Effect::getEffect(Effect::WEAKNESS)->setAmplifier(0)->setDuration(1.5 * 60 * 20));
				break;
			case Potion::WEAKNESS_T:
				$entity->addEffect(Effect::getEffect(Effect::WEAKNESS)->setAmplifier(0)->setDuration(4 * 60 * 20));
				break;
			case Potion::HEALING:
				$entity->addEffect(Effect::getEffect(Effect::HEALING)->setAmplifier(0)->setDuration(1));
				break;
			case Potion::HEALING_TWO:
				$entity->addEffect(Effect::getEffect(Effect::HEALING)->setAmplifier(1)->setDuration(1));
				break;
			case Potion::HARMING:
				$entity->addEffect(Effect::getEffect(Effect::HARMING)->setAmplifier(0)->setDuration(1));
				break;
			case Potion::HARMING_TWO:
				$entity->addEffect(Effect::getEffect(Effect::HARMING)->setAmplifier(1)->setDuration(1));
				break;
		}
	}

	public function onUpdate($currentTick){
		if($this->closed){
			return false;
		}

		$this->timings->startTiming();

		$hasUpdate = parent::onUpdate($currentTick);

		$this->age++;

		if($this->age > 1200 or $this->isCollided){
			$this->kill();
			$this->close();
			$hasUpdate = true;
		}
		
		if($this->onGround) {
			$this->kill();
			$this->close();
		}

		$this->timings->stopTiming();

		return $hasUpdate;
	}

	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->type = ThrownPotion::NETWORK_ID;
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
