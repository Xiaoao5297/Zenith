<?php



namespace pocketmine\entity;

use pocketmine\network\Network;
use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;
use pocketmine\event\entity\EntityDamageByEntityEvent;
use pocketmine\item\Item as ItemItem;
use pocketmine\entity\ai\behavior\{StrollBehavior, RandomLookaroundBehavior, attackEnemyBehavior};

class CaveSpider extends Monster{
	const NETWORK_ID = 40;

	public $width = 1;
	public $length = 1;
	//public $height = 1.8;//0.5 为了适配Default AI

	public $dropExp = [5, 5];
	private $hurt = 4;

	public function getName() : string{
		return "Cave Spider";
	}
	
	public function initEntity(){
		$this->setMaxHealth(12);
		
		$this->addBehavior(new attackEnemyBehavior($this, [20], true));
		$this->addBehavior(new StrollBehavior($this));
		$this->addBehavior(new RandomLookaroundBehavior($this));
		
		parent::initEntity();
	}
	
	public function getHurt(){
		return $this->hurt;
	}
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = CaveSpider::NETWORK_ID;
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