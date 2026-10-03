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

use lycore\block\Water;
use lycore\event\entity\ProjectileHitEvent;
use lycore\event\player\PlayerFishEvent;
use lycore\item\enchantment\Enchantment;
use lycore\item\Item as ItemItem;
use lycore\level\format\FullChunk;
use lycore\level\particle\CriticalParticle;
use lycore\level\particle\WaterParticle;
use lycore\level\sound\SplashSound;
use lycore\math\Vector3;
use lycore\nbt\tag\CompoundTag;
use lycore\network\protocol\AddEntityPacket;
use lycore\network\protocol\EntityEventPacket;
use lycore\Player;

class FishingHook extends Projectile{
	const NETWORK_ID = 77;

	public $width = 0.25;
	public $length = 0.25;
	public $height = 0.25;

	protected $gravity = 0.04;
	protected $drag = 0.05;

	public $lead = -1;
	public $data = 0;
	public $attractTimer = 0;
	public $coughtTimer = 0;

	/** @var Player */
	private $hookedPlayer = null;
	private $fishApproachTicks = 0;

	private static $fishLoot = [
		[60, ItemItem::RAW_FISH, 0, 1],
		[25, ItemItem::RAW_SALMON, 0, 1],
		[7, ItemItem::CLOWN_FISH, 0, 1],
		[7, ItemItem::PUFFER_FISH, 0, 1],
	];

	private static $junkLoot = [
		[ItemItem::BOWL, 0, 1],
		[ItemItem::LEATHER, 0, 1],
		[ItemItem::LEATHER_BOOTS, 0, 1],
		[ItemItem::ROTTEN_FLESH, 0, 1],
		[ItemItem::STICK, 0, 1],
		[ItemItem::STRING, 0, 1],
		[ItemItem::GLASS_BOTTLE, 0, 1],
		[ItemItem::BONE, 0, 1],
		[ItemItem::DYE, 0, 1],
		[ItemItem::TRIPWIRE_HOOK, 0, 1],
	];

	private static $treasureLoot = [
		[ItemItem::BOW, 0, 1],
		[ItemItem::DIAMOND, 0, 1],
		[ItemItem::FISHING_ROD, 0, 1],
	];

	public function initEntity(){
		parent::initEntity();

		if(isset($this->namedtag->Data)){
			$this->data = $this->namedtag["Data"];
		}
	}

	public function __construct(FullChunk $chunk, CompoundTag $nbt, Entity $shootingEntity = null){
		parent::__construct($chunk, $nbt, $shootingEntity);
		if($shootingEntity instanceof Player){
			$this->setLeadHolderEid($shootingEntity->getId());
		}
		$this->resetFishingWait();
	}

	public function setData($id){
		$this->data = $id;
	}

	public function getData(){
		return $this->data;
	}

	public function setLeadHolderEid($eid){
		$this->lead = (int) $eid;
		$this->setDataProperty(self::DATA_LEAD_HOLDER, self::DATA_TYPE_LONG, (int) $eid);
		$this->setDataProperty(self::DATA_LEAD, self::DATA_TYPE_BYTE, 0);
	}

	public function getHookedPlayer(){
		if($this->hookedPlayer instanceof Player and !$this->hookedPlayer->closed and $this->hookedPlayer->isAlive() and $this->hookedPlayer->getLevel() === $this->getLevel()){
			return $this->hookedPlayer;
		}
		$this->hookedPlayer = null;
		return null;
	}

	public function hasHookedPlayer(){
		return $this->getHookedPlayer() instanceof Player;
	}

	public function canCatchFish(){
		return $this->coughtTimer > 0;
	}

	public function onUpdate($currentTick){
		if($this->closed){
			return false;
		}

		$this->timings->startTiming();

		if($this->hasHookedPlayer()){
			$tickDiff = $currentTick - $this->lastUpdate;
			if($tickDiff <= 0 and !$this->justCreated){
				$this->timings->stopTiming();
				return true;
			}
			$this->lastUpdate = $currentTick;
			$hasUpdate = $this->entityBaseTick($tickDiff);
			if($this->isAlive()){
				$hasUpdate = $this->syncHookedPlayer() or $hasUpdate;
			}
			$this->timings->stopTiming();
			return $hasUpdate;
		}

		$oldGravity = $this->gravity;
		if($this->getWaterSurfaceY() !== null){
			$this->gravity = 0;
			$this->motionY = max(-0.05, min(0.05, $this->motionY));
		}

		$hasUpdate = parent::onUpdate($currentTick);
		$this->gravity = $oldGravity;

		if($this->hasHookedPlayer()){
			$this->timings->stopTiming();
			return true;
		}

		$this->addTrailParticle();

		if($this->applyWaterBuoyancy()){
			$hasUpdate = true;
			$this->tickFishingLogic();
		}elseif($this->isCollided and $this->keepMovement === true){
			$this->motionX = 0;
			$this->motionY = 0;
			$this->motionZ = 0;
			$this->motionChanged = true;
			$this->keepMovement = false;
			$hasUpdate = true;
		}

		$this->timings->stopTiming();
		return $hasUpdate;
	}

	protected function onHitEntity(Entity $entityHit){
		if($entityHit === $this->shootingEntity){
			return false;
		}

		if($entityHit instanceof Player){
			$this->server->getPluginManager()->callEvent(new ProjectileHitEvent($this));
			return $this->attachToPlayer($entityHit);
		}

		return false;
	}

	private function resetFishingWait(){
		$lure = $this->getRodEnchantmentLevel(Enchantment::TYPE_FISHING_LURE);
		$this->attractTimer = max(100, mt_rand(100, 900) - ($lure * 100));
		$this->fishApproachTicks = 0;
		$this->coughtTimer = 0;
	}

	private function tickFishingLogic(){
		if(!($this->shootingEntity instanceof Player)){
			$this->close();
			return;
		}

		if($this->coughtTimer > 0){
			--$this->coughtTimer;
			if($this->coughtTimer <= 0){
				$this->resetFishingWait();
			}
			return;
		}

		if($this->fishApproachTicks > 0){
			--$this->fishApproachTicks;
			$this->spawnApproachParticles();
			if($this->fishApproachTicks <= 0){
				$this->coughtTimer = mt_rand(20, 40);
				$this->splashBite();
			}
			return;
		}

		if($this->attractTimer > 0){
			--$this->attractTimer;
			if($this->attractTimer <= 0){
				$this->fishApproachTicks = mt_rand(20, 80);
			}
		}
	}

	private function attachToPlayer(Player $player){
		if($player === $this->shootingEntity){
			return false;
		}

		$this->hookedPlayer = $player;
		$this->keepMovement = false;
		$this->isCollided = false;
		$this->hadCollision = true;
		$this->motionX = 0;
		$this->motionY = 0;
		$this->motionZ = 0;
		$this->setPosition($this->getHookedPlayerPosition($player));
		$this->updateMovement();

		$pk = new EntityEventPacket();
		$pk->eid = $this->getId();
		$pk->event = EntityEventPacket::FISH_HOOK_HOOK;
		$this->server->broadcastPacket($this->level->getPlayers(), $pk);

		return true;
	}

	private function syncHookedPlayer(){
		$hookedPlayer = $this->getHookedPlayer();
		if(!($hookedPlayer instanceof Player)){
			if($this->shootingEntity instanceof Player and $this->shootingEntity->getFishingHook() === $this){
				$this->shootingEntity->unlinkHookFromPlayer();
			}else{
				$this->close();
			}
			return false;
		}

		$this->motionX = 0;
		$this->motionY = 0;
		$this->motionZ = 0;
		$this->setPosition($this->getHookedPlayerPosition($hookedPlayer));
		$this->updateMovement();
		return true;
	}

	private function getHookedPlayerPosition(Player $player){
		return new Vector3($player->x, $player->y + ($player->height * 0.5), $player->z);
	}

	private function pullHookedPlayer(Player $player, Player $hookedPlayer){
		$dx = $player->x - $hookedPlayer->x;
		$dy = ($player->y + ($player->height * 0.5)) - ($hookedPlayer->y + ($hookedPlayer->height * 0.5));
		$dz = $player->z - $hookedPlayer->z;
		$distance = sqrt(($dx * $dx) + ($dz * $dz));
		if($distance <= 0.0001){
			return;
		}

		$motion = $hookedPlayer->getMotion();
		$horizontalPull = min(0.58, 0.22 + $distance * 0.035);
		$verticalPull = max(-0.025, min(0.035, $dy * 0.012));
		if($hookedPlayer->isOnGround()){
			$verticalPull = max(0.018, $verticalPull);
		}

		$motion->x = $motion->x * 0.15 + ($dx / $distance) * $horizontalPull;
		$motion->y = $motion->y * 0.1 + $verticalPull;
		$motion->z = $motion->z * 0.15 + ($dz / $distance) * $horizontalPull;
		$motion->x = max(-0.58, min(0.58, $motion->x));
		$motion->y = max(-0.045, min(0.045, $motion->y));
		$motion->z = max(-0.58, min(0.58, $motion->z));
		$hookedPlayer->setMotion($motion);
	}

	private function getWaterSurfaceY(){
		$block = $this->level->getBlock($this->temporalVector->setComponents((int) floor($this->x), (int) floor($this->y), (int) floor($this->z)));
		if(!$block instanceof Water){
			return null;
		}

		return ($block->y + 1) - ($block->getFluidHeightPercent() - 0.1111111);
	}

	private function applyWaterBuoyancy(){
		$surfaceY = $this->getWaterSurfaceY();
		if($surfaceY === null){
			return false;
		}

		$targetY = $surfaceY - 0.08;
		$offset = $targetY - $this->y;
		$this->motionX *= 0.7;
		$this->motionZ *= 0.7;

		if(abs($this->motionX) < 0.001){
			$this->motionX = 0;
		}
		if(abs($this->motionZ) < 0.001){
			$this->motionZ = 0;
		}

		$this->motionY = abs($offset) < 0.015 ? 0 : max(-0.025, min(0.035, $offset * 0.18));
		$this->motionChanged = true;
		return true;
	}

	private function addTrailParticle(){
		if(($this->ticksLived % 2) !== 0 or $this->hadCollision or $this->isCollided or $this->getWaterSurfaceY() !== null or $this->hasHookedPlayer()){
			return;
		}

		$this->level->addParticle(new CriticalParticle($this->add(
			mt_rand(-10, 10) / 100,
			($this->height / 2) + (mt_rand(-10, 10) / 100),
			mt_rand(-10, 10) / 100
		)));
	}

	private function spawnApproachParticles(){
		if(($this->fishApproachTicks % 5) !== 0){
			return;
		}

		$angle = mt_rand(0, 628) / 100;
		$distance = max(0.4, $this->fishApproachTicks / 20);
		$this->level->addParticle(new WaterParticle(new Vector3(
			$this->x + cos($angle) * $distance,
			$this->y + 0.05,
			$this->z + sin($angle) * $distance
		)));
	}

	private function splashBite(){
		for($i = 0; $i < 8; ++$i){
			$this->level->addParticle(new WaterParticle(new Vector3(
				$this->x + mt_rand(-20, 20) / 100,
				$this->y + 0.1,
				$this->z + mt_rand(-20, 20) / 100
			)));
		}
		$this->level->addSound(new SplashSound(new Vector3($this->x, $this->y, $this->z)));

		$pk = new EntityEventPacket();
		$pk->eid = $this->getId();
		$pk->event = EntityEventPacket::FISH_HOOK_BUBBLE;
		$this->server->broadcastPacket($this->level->getPlayers(), $pk);
	}

	private function getRodEnchantmentLevel($enchantmentId){
		if(!($this->shootingEntity instanceof Player)){
			return 0;
		}

		$rod = $this->shootingEntity->getInventory()->getItemInHand();
		if($rod->getId() !== ItemItem::FISHING_ROD){
			return 0;
		}

		return $rod->getEnchantmentLevel($enchantmentId);
	}

	private function rollLoot(){
		$luck = $this->getRodEnchantmentLevel(Enchantment::TYPE_FISHING_FORTUNE);
		$fish = 70;
		$treasure = max(1, 3 + $luck * 2);
		$junk = max(1, 27 - $luck * 2);
		$roll = mt_rand(1, $fish + $treasure + $junk);

		if($roll <= $treasure){
			return $this->itemFromEntry(self::$treasureLoot[array_rand(self::$treasureLoot)]);
		}
		if($roll <= $treasure + $junk){
			return $this->itemFromEntry(self::$junkLoot[array_rand(self::$junkLoot)]);
		}

		return $this->rollFish();
	}

	private function rollFish(){
		$total = 0;
		foreach(self::$fishLoot as $entry){
			$total += $entry[0];
		}

		$roll = mt_rand(1, $total);
		foreach(self::$fishLoot as $entry){
			$roll -= $entry[0];
			if($roll <= 0){
				return ItemItem::get($entry[1], $entry[2], $entry[3]);
			}
		}

		return ItemItem::get(ItemItem::RAW_FISH, 0, 1);
	}

	private function itemFromEntry(array $entry){
		return ItemItem::get($entry[0], $entry[1], $entry[2]);
	}

	private function consumeDurability(Player $player, $caughtSomething = false){
		$itemInHand = $player->getInventory()->getItemInHand();
		if($itemInHand->getId() !== ItemItem::FISHING_ROD){
			return;
		}

		$damage = $caughtSomething ? 1 : 0;
		if($damage <= 0){
			return;
		}

		$unbreakingLevel = min(3, $itemInHand->getEnchantmentLevel(Enchantment::TYPE_MINING_DURABILITY));
		if($unbreakingLevel > 0 and mt_rand(1, $unbreakingLevel + 1) !== 1){
			return;
		}

		$itemInHand->setDamage($itemInHand->getDamage() + $damage);
		$maxDurability = $itemInHand->getMaxDurability();
		if($maxDurability !== false and $itemInHand->getDamage() >= $maxDurability){
			$player->getInventory()->setItemInHand(ItemItem::get(ItemItem::AIR, 0, 0));
		}else{
			$player->getInventory()->setItemInHand($itemInHand);
		}
	}

	public function reelIn(){
		if(!($this->shootingEntity instanceof Player)){
			$this->close();
			return;
		}

		$player = $this->shootingEntity;
		$hookedPlayer = $this->getHookedPlayer();
		if($hookedPlayer instanceof Player){
			$this->pullHookedPlayer($player, $hookedPlayer);
			$this->consumeDurability($player, true);
			$player->unlinkHookFromPlayer();
			$this->close();
			return;
		}

		if(!$this->canCatchFish()){
			$player->unlinkHookFromPlayer();
			$this->close();
			return;
		}

		$item = $this->rollLoot();
		$ev = new PlayerFishEvent($player, $item, $this);
		$this->server->getPluginManager()->callEvent($ev);
		if(!$ev->isCancelled()){
			$dropPos = $player->getPosition()->add(0, 1.5, 0)->add($player->getDirectionVector()->multiply(0.5));
			$this->level->dropItem($dropPos, $ev->getItem(), new Vector3(0, 0.15, 0));
			$player->addExperience(mt_rand(2, 12));
			$this->level->addSound(new SplashSound($player));
			$this->consumeDurability($player, true);
		}

		$player->unlinkHookFromPlayer();
		$this->close();
	}

	public function reeline(){
		$this->reelIn();
	}

	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = self::NETWORK_ID;
		$pk->x = $this->x;
		$pk->y = $this->y;
		$pk->z = $this->z;
		$pk->speedX = $this->motionX;
		$pk->speedY = $this->motionY;
		$pk->speedZ = $this->motionZ;
		$pk->yaw = $this->yaw;
		$pk->pitch = $this->pitch;
		$pk->modifiers = 0;
		$pk->metadata = $this->dataProperties;
		if($this->lead > 0){
			$pk->metadata[self::DATA_LEAD_HOLDER] = [self::DATA_TYPE_LONG, $this->lead];
			$pk->metadata[self::DATA_LEAD] = [self::DATA_TYPE_BYTE, 0];
		}
		$player->dataPacket($pk);
		parent::spawnTo($player);
	}
}
