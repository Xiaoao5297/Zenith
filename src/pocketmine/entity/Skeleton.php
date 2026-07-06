<?php


namespace pocketmine\entity;

use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;
use pocketmine\network\protocol\MobEquipmentPacket;
use pocketmine\item\Item as ItemItem;
use pocketmine\entity\ai\behavior\{StrollBehavior, ShootPlayerBehavior, RandomLookAroundBehavior};

class Skeleton extends Monster implements ProjectileSource{
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
		
		$pk = new MobEquipmentPacket();
		$pk->eid = $this->getId();
		$pk->item = new ItemItem(ItemItem::BOW);
		$pk->slot = 0;
		$pk->selectedSlot = 0;

		$player->dataPacket($pk);
	}
	
	public function getDrops(){
		$drops = array(ItemItem::get(ItemItem::BONE, 0, mt_rand(1,2)));
		return $drops;
	}
}
