<?php

namespace pocketmine\entity;

use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\network\protocol\MobEquipmentPacket;
use pocketmine\Player;
use pocketmine\item\Item as ItemItem;
use pocketmine\entity\ai\behavior\{StrollBehavior, RandomLookaroundBehavior, attackEnemyBehavior};

class PigZombie extends Monster{
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

		$this->addBehavior(new attackEnemyBehavior($this, [20], true));
		$this->addBehavior(new StrollBehavior($this));
		$this->addBehavior(new RandomLookaroundBehavior($this));

		parent::initEntity();
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
		
		$pk = new MobEquipmentPacket();
		$pk->eid = $this->getId();
		$pk->item = new ItemItem(283);
		$pk->slot = 0;
		$pk->selectedSlot = 0;

		$player->dataPacket($pk);
	}
}