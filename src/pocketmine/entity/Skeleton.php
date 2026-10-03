<?php


namespace pocketmine\entity;

use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;
use pocketmine\network\protocol\MobEquipmentPacket;
use pocketmine\item\Item as ItemItem;
use pocketmine\entity\ai\behavior\{StrollBehavior, ShootPlayerBehavior, RandomLookAroundBehavior};

class Skeleton extends Monster implements ProjectileSource{
	use MobEquipmentTrait;

	const NETWORK_ID = 34;
	
	public $width = 0.6;
	public $length = 0.6;
	public $height = 1.8;
	
	public $dropExp = [5, 5];
	
	public function getName() : string{
		return "Skeleton";
	}
	
	public function initEntity(){
		$this->setMaxHealth(20);
		
		$this->addBehavior(new ShootPlayerBehavior($this, 80));
		$this->addBehavior(new StrollBehavior($this));
		$this->addBehavior(new RandomLookAroundBehavior($this));
		
		parent::initEntity();

		$equipment = VanillaMobEquipment::generateSkeletonEquipment($this->server->getDifficulty());
		$this->setMobEquipment($equipment["armor"], $equipment["weapon"]);
	}

	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Skeleton::NETWORK_ID;
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

		$this->sendMobEquipment($player);
	}

	protected function handlesLootingDrops() : bool{
		return true;
	}

	public function getDrops(){
		$looting = $this->getLastDamageLootingLevel();
		$drops = [
			ItemItem::get(ItemItem::BONE, 0, mt_rand(0, 2)),
			ItemItem::get(ItemItem::ARROW, 0, mt_rand(0, 2))
		];
		$drops = VanillaMobEquipment::applyLootingToCommonDrops($drops, $looting);
		if(mt_rand(1, 1000) <= VanillaMobEquipment::rareDropChance($looting)){
			$drops[] = ItemItem::get(ItemItem::BOW, mt_rand(250, 384), 1);
		}
		foreach(VanillaMobEquipment::maybeDropEquipment($this->getArmorContents(), $this->getWeapon(), $looting) as $drop){
			$drops[] = $drop;
		}
		return $drops;
	}
}
