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
use lycore\block\Water;
use lycore\entity\behavior\attackEnemyBehavior;
use lycore\event\entity\EntityDamageByEntityEvent;
use lycore\event\entity\EntityDamageEvent;
use lycore\network\protocol\AddEntityPacket;
use lycore\nbt\tag\ShortTag;
use lycore\Player;
use lycore\item\Item as ItemItem;
use lycore\level\Level;
use lycore\level\sound\EndermanTeleportSound;
use lycore\math\Vector3;
class Enderman extends Monster{
	const NETWORK_ID = 38;

	public $width = 0.3;
	public $length = 0.9;
	public $height = 1.8;

	public $dropExp = [5, 5];

	private $hurt = 7;
	
	public function initEntity(){
		if(!isset($this->namedtag->carriedData)){
			$this->namedtag->carriedData = new ShortTag("carriedData", 0);
			$this->namedtag->carried = new ShortTag("carried", 0);
		}
		$this->addBehavior(new attackEnemyBehavior($this, [], false));
		parent::initEntity();
		$this->setTremble(false);
	}

	public function usesPm1eGroundAi() : bool{
		return true;
	}
	
	public function getName() : string{
		return "Enderman";
	}

	public function getHurt(){
		return $this->hurt;
	}
	
	public function setHurt($hurt){
		$this->hurt = $hurt;
	}
	
	public function setTremble(bool $setting){
		$this->setDataProperty(self::DATA_ENDERMAN_TREMBLE, self::DATA_TYPE_BYTE, $setting ? 1 : 0);
	}
	
	public function setBlockInHand(int $id, int $meta){
		$this->setDataProperty(self::DATA_ENDERMAN_HELD_ITEM_ID, self::DATA_TYPE_SHORT, $id);
		$this->setDataProperty(self::DATA_ENDERMAN_HELD_ITEM_DAMAGE, self::DATA_TYPE_SHORT, $meta);
		$this->namedtag->carriedData = new ShortTag("carriedData", $id);
		$this->namedtag->carried = new ShortTag("carried", $meta);
	}
	
	public function getBlockInHand(&$id, &$meta){
		$id = $this->namedtag["carriedData"];
		$meta = $this->namedtag["carried"];
	}

	public function entityBaseTick($tickDiff = 1){
		$hasUpdate = parent::entityBaseTick($tickDiff);

		if($this->isAlive() and !$this->closed and $this->isEndermanWet()){
			$hasUpdate = true;
			$ev = new EntityDamageEvent($this, EntityDamageEvent::CAUSE_CONTACT, 1);
			$this->attack($ev->getFinalDamage(), $ev);
			if($this->isAlive() and !$this->closed){
				$this->tryTeleportAwayFromHazard(true);
			}
		}

		return $hasUpdate;
	}

	public function attack($damage, EntityDamageEvent $source){
		$result = parent::attack($damage, $source);
		if(!$result or $source->isCancelled() or $this->closed or !$this->isAlive()){
			return $result;
		}

		if($source instanceof EntityDamageByEntityEvent){
			$damager = $source->getDamager();
			if($damager instanceof Player){
				$this->setPm1eRetaliationTarget($damager);
				$this->setTremble(true);
				$this->tryTeleportNearTarget($damager, new Vector3($this->x, $this->y, $this->z));
			}
		}

		return $result;
	}

	protected function isEndermanWet() : bool{
		if($this->isInsideOfWater()){
			return true;
		}

		foreach($this->getBlocksAround() as $block){
			if($this->isWaterBlock($block)){
				return true;
			}
		}

		$x = (int) floor($this->x);
		$y = (int) floor($this->y);
		$z = (int) floor($this->z);
		for($dy = 0; $dy <= (int) floor($this->height); ++$dy){
			if($this->isWaterBlockAt($x, $y + $dy, $z)){
				return true;
			}
		}

		return $this->isExposedToRain();
	}

	protected function isExposedToRain() : bool{
		$level = $this->getLevel();
		if(!($level instanceof Level) or $level->getDimension() !== Level::DIMENSION_NORMAL){
			return false;
		}

		return $this->isRainy($level) and $level->canBlockSeeSky(new Vector3(
			(int) floor($this->x),
			(int) floor($this->y + $this->height),
			(int) floor($this->z)
		));
	}

	protected function tryTeleportAwayFromHazard(bool $mustAvoidRain) : bool{
		$level = $this->getLevel();
		if(!($level instanceof Level)){
			return false;
		}

		for($i = 0; $i < 64; ++$i){
			$target = $this->getRandomTeleportTarget($mustAvoidRain);
			if(!($target instanceof Vector3)){
				continue;
			}

			$from = new Vector3($this->x, $this->y, $this->z);
			if($this->teleport($target)){
				$level->addSound(new EndermanTeleportSound($from));
				$level->addSound(new EndermanTeleportSound($this));
				return true;
			}
		}

		return false;
	}

	protected function tryTeleportNearTarget(Entity $target, Vector3 $origin) : bool{
		$level = $this->getLevel();
		if(!($level instanceof Level) or $target->getLevel() !== $level){
			return false;
		}

		$mustAvoidRain = $this->isRainy($level);
		for($i = 0; $i < 64; ++$i){
			$pos = $this->getRandomTeleportTargetNear($target, $origin, $mustAvoidRain);
			if(!($pos instanceof Vector3)){
				continue;
			}

			$from = new Vector3($this->x, $this->y, $this->z);
			if($this->teleport($pos)){
				$level->addSound(new EndermanTeleportSound($from));
				$level->addSound(new EndermanTeleportSound($this));
				return true;
			}
		}

		return false;
	}

	protected function getRandomTeleportTarget(bool $mustAvoidRain){
		$level = $this->getLevel();
		if(!($level instanceof Level)){
			return null;
		}

		$x = (int) floor($this->x) + mt_rand(-16, 16);
		$z = (int) floor($this->z) + mt_rand(-16, 16);
		return $this->getSurfaceTeleportTargetAt($x, $z, $mustAvoidRain);
	}

	protected function getRandomTeleportTargetNear(Entity $target, Vector3 $origin, bool $mustAvoidRain){
		$level = $this->getLevel();
		if(!($level instanceof Level)){
			return null;
		}

		$targetX = (int) floor($target->x);
		$targetZ = (int) floor($target->z);
		$distance = mt_rand(3, 8);
		$angle = mt_rand(0, 359) * M_PI / 180;
		$x = $targetX + (int) round(cos($angle) * $distance);
		$z = $targetZ + (int) round(sin($angle) * $distance);
		$pos = $this->getSurfaceTeleportTargetAt($x, $z, $mustAvoidRain);

		if(!($pos instanceof Vector3)){
			return null;
		}

		if($origin->distanceSquared($pos) > 256){
			return null;
		}

		return $pos;
	}

	protected function getSurfaceTeleportTargetAt(int $x, int $z, bool $mustAvoidRain){
		$level = $this->getLevel();
		if(!($level instanceof Level)){
			return null;
		}

		$surfaceY = $level->getHighestBlockAt($x, $z) + 1;
		$pos = new Vector3($x + 0.5, $surfaceY, $z + 0.5);
		if($this->canEndermanTeleportTo($pos, $mustAvoidRain)){
			return $pos;
		}

		return null;
	}

	protected function canEndermanTeleportTo(Vector3 $pos, bool $mustAvoidRain = true) : bool{
		$level = $this->getLevel();
		if(!($level instanceof Level)){
			return false;
		}

		$x = (int) floor($pos->x);
		$y = (int) floor($pos->y);
		$z = (int) floor($pos->z);
		if($y < 1 or $y + 2 >= Level::Y_MAX){
			return false;
		}

		$floor = $level->getBlock(new Vector3($x, $y - 1, $z));
		if(!$floor->isTopFacingSurfaceSolid()){
			return false;
		}

		for($dy = 0; $dy <= (int) floor($this->height); ++$dy){
			$block = $level->getBlock(new Vector3($x, $y + $dy, $z));
			if($this->isWaterBlock($block) or !$block->canPassThrough()){
				return false;
			}
		}

		if($mustAvoidRain and $this->isRainy($level) and $level->canBlockSeeSky(new Vector3($x, (int) floor($y + $this->height), $z))){
			return false;
		}

		return true;
	}

	protected function isRainy(Level $level) : bool{
		$weather = $level->getWeather();
		return $weather !== null and ($weather->isRainy() or $weather->isRainyThunder());
	}

	protected function isWaterBlockAt(int $x, int $y, int $z) : bool{
		$level = $this->getLevel();
		if(!($level instanceof Level) or $y < 0 or $y >= Level::Y_MAX){
			return false;
		}

		return $this->isWaterBlock($level->getBlock(new Vector3($x, $y, $z)));
	}

	protected function isWaterBlock($block) : bool{
		if($block instanceof Water){
			return true;
		}

		$id = method_exists($block, "getId") ? $block->getId() : null;
		return $id === Block::WATER or $id === Block::STILL_WATER;
	}
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Enderman::NETWORK_ID;
		$pk->x = $this->x;
		$pk->y = $this->y;
		$pk->z = $this->z;
		$pk->speedX = $this->motionX;
		$pk->speedY = $this->motionY;
		$pk->speedZ = $this->motionZ;
		$pk->yaw = $this->yaw;
		$pk->pitch = $this->pitch;
		$pk->metadata = $this->dataProperties;
		$player->dataPacket($pk);

		parent::spawnTo($player);
	}
	
	public function getDrops(){
        return [
            ItemItem::get(ItemItem::SNOWBALL, 1, 1)
        ];
    }
}
