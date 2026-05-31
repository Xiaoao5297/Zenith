<?php

namespace pocketmine\entity;

use pocketmine\network\Network;
use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;
use pocketmine\event\entity\EntityDamageByEntityEvent;
use pocketmine\entity\ai\behavior\{StrollBehavior, RandomLookaroundBehavior, attackEnemyBehavior};
use pocketmine\item\Item as ItemItem;

class Spider extends Monster{
	const NETWORK_ID = 35;
	public $width = 0.3;
	public $length = 0.9;
	public $height = 1.9;

	public $dropExp = [5, 5];
	
	private $hurt = 6;
	
	public function getName() : string{
		return "Spider";
	}
	
	public function initEntity(){
		$this->setMaxHealth(16);
		
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
		$pk->type = Spider::NETWORK_ID;
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
	
	public function getDrops(){
		$drops = [];
		if(mt_rand(0, 2) < 1){
			$drops[] = ItemItem::get(ItemItem::SPIDER_EYE, 0, 1);
		}else{
			$drops[] = ItemItem::get(ItemItem::STRING, 0, 1);
		}
		return $drops;
	}
}