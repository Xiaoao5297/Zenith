<?php

namespace pocketmine\entity;

use pocketmine\level\format\FullChunk;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;
use pocketmine\entity\ai\behavior\{StrollBehavior, RandomLookAroundBehavior, LookAtPlayerBehavior, PanicBehavior, InLoveBehavior, AttackEnemyBehavior};

class Wolf extends Animal{
	const NETWORK_ID = 14;

	public $width = 0.3;
	public $length = 0.9;
	public $height = 0.85;

	public $dropExp = [1, 3];

	public function getName() : string{
		return "Wolf";
	}

	public function initEntity(){
		$this->setMaxHealth(8);

$this->addBehavior(new AttackEnemyBehavior($this, [13], false));
		$this->addBehavior(new InLoveBehavior($this));
		$this->addBehavior(new StrollBehavior($this));
		$this->addBehavior(new LookAtPlayerBehavior($this));
		$this->addBehavior(new RandomLookAroundBehavior($this));

		parent::initEntity();
	}

	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Wolf::NETWORK_ID;
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