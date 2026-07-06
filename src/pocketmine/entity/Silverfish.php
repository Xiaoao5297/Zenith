<?php


namespace pocketmine\entity;

use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;
use pocketmine\entity\ai\behavior\{StrollBehavior, RandomLookAroundBehavior, AttackEnemyBehavior};

class Silverfish extends Monster{
	const NETWORK_ID = 39;

	public $dropExp = [5, 5];

	private $hurt = 1;

	public function getName() : string{
		return "Silverfish";
	}

	public function initEntity(){
		$this->setMaxHealth(8);

		$this->addBehavior(new AttackEnemyBehavior($this, [20], true));
		$this->addBehavior(new StrollBehavior($this));
		$this->addBehavior(new RandomLookAroundBehavior($this));

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
		$pk->type = Silverfish::NETWORK_ID;
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