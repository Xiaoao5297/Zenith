<?php



namespace pocketmine\entity;

use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;
use pocketmine\entity\ai\behavior\{StrollBehavior, ShootPlayerBehavior, RandomLookaroundBehavior};

class Witch extends Monster {
	const NETWORK_ID = 45;

	/**
	 * @return string
	 */
	public function getName() : string{
		return "Witch";
	}

	public function initEntity(){
		$this->setMaxHealth(26);
		
		$this->addBehavior(new ShootPlayerBehavior($this, 86));//扔药水
		$this->addBehavior(new StrollBehavior($this));
		$this->addBehavior(new RandomLookaroundBehavior($this));
		
		parent::initEntity();
	}

	/**
	 * @return array
	 */
	public function getDrops(){
		if(mt_rand(1, 8) == 1){
			
		}
		//TODO
		return [];
	}

    public function getXpDropAmount(): int{
        return 5;
    }
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Witch::NETWORK_ID;
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