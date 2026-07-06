<?php


namespace pocketmine\entity;

use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;
use pocketmine\entity\ai\behavior\{RandomLookAroundBehavior, AttackEnemyBehavior};

class Ghast extends FlyingAnimal{
	const NETWORK_ID = 41;

	public $width = 6;
	public $length = 6;
	public $height = 6;

	public function getName() : string{
		return "Ghast";
	}

	public function initEntity(){
		$this->setMaxHealth(10);

		$this->addBehavior(new AttackEnemyBehavior($this, [20], true));
		$this->addBehavior(new RandomLookAroundBehavior($this));

		parent::initEntity();
	}
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Ghast::NETWORK_ID;
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