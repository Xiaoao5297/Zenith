<?php

namespace pocketmine\entity;

use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\network\protocol\MobEquipmentPacket;
use pocketmine\Player;
use pocketmine\item\Item as ItemItem;
use pocketmine\entity\ai\behavior\{StrollBehavior, RandomLookAroundBehavior, AttackEnemyBehavior};

class PigZombie extends Monster{
	use MobEquipmentTrait;

	const NETWORK_ID = 36;

	public $width = 0.6;
	public $length = 0.6;
	public $height = 1.8;

	public $drag = 0.2;
	public $gravity = 0.3;

	public $dropExp = [5, 5];

	private $hurt = 10;

	public function getName() : string{
		return "PigZombie";
	}

	public function initEntity(){
		$this->setMaxHealth(20);

		$this->addBehavior(new AttackEnemyBehavior($this, [], true));
		$this->addBehavior(new StrollBehavior($this));
		$this->addBehavior(new RandomLookAroundBehavior($this));

		parent::initEntity();

		$equipment = VanillaMobEquipment::generatePigZombieEquipment($this->server->getDifficulty());
		$this->setMobEquipment($equipment["armor"], $equipment["weapon"]);
	}
	
	public function getHurt(){
		return $this->hurt;
	}
	
	public function setHurt($hurt){
		$this->hurt = $hurt;
	}
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = PigZombie::NETWORK_ID;
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
			ItemItem::get(ItemItem::ROTTEN_FLESH, 0, mt_rand(0, 1)),
			ItemItem::get(ItemItem::GOLD_NUGGET, 0, mt_rand(0, 1))
		];
		$drops = VanillaMobEquipment::applyLootingToCommonDrops($drops, $looting);
		if(mt_rand(1, 1000) <= VanillaMobEquipment::rareDropChance($looting)){
			$drops[] = ItemItem::get(ItemItem::GOLD_INGOT, 0, 1);
		}
		foreach(VanillaMobEquipment::maybeDropEquipment([], $this->getWeapon(), $looting) as $drop){
			$drops[] = $drop;
		}
		return $drops;
	}
}