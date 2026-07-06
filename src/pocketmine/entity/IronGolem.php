<?php


namespace pocketmine\entity;

use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;
use pocketmine\entity\ai\behavior\{StrollBehavior, RandomLookAroundBehavior, AttackEnemyBehavior};

class IronGolem extends Animal{
	const NETWORK_ID = 20;

	public $width = 0.3;
	public $length = 0.9;
	public $height = 2.8;
	
	public function initEntity(){
		$this->setMaxHealth(100);
		
		$this->addBehavior(new AttackEnemyBehavior($this, [32, 33, 34, 35, 36, 40, 44], false));
		$this->addBehavior(new StrollBehavior($this));
		$this->addBehavior(new RandomLookAroundBehavior($this));
		
		parent::initEntity();
	}
	
	public function getName() {
		return "Iron Golem";
	}
	
	public function getHurt(){
		return mt_rand(7, 21);
	}
	
	public function spawnTo(Player $player) {
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
		$pk->metadata = $this->dataProperties;
		$player->dataPacket($pk);

		parent::spawnTo($player);
	}
}