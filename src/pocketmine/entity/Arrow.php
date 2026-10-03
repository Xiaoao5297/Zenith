<?php

namespace pocketmine\entity;

use pocketmine\item\Arrow as ItemArrow;
use pocketmine\item\Item;
use pocketmine\item\Potion;
use pocketmine\level\format\FullChunk;
use pocketmine\level\particle\CriticalParticle;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\ShortTag;
use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;

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

	public function applyTippedArrowEffects(Entity $entity){
		if(!$this->arrowItem instanceof ItemArrow or !$this->arrowItem->isTipped()){
			return;
		}

		foreach(Potion::getArrowEffectsByMeta((int) $this->arrowItem->getPotionMeta()) as $effect){
			$entity->addEffect($effect);
		}
	}

	protected function onHitEntity(Entity $entityHit){
		$this->applyTippedArrowEffects($entityHit);
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

		if($this->age > 1200){
			$this->kill();
			$hasUpdate = true;
		}

		$this->timings->stopTiming();

		return $hasUpdate;
	}

	public function spawnTo(Player $player){
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
