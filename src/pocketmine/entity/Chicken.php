<?php


namespace pocketmine\entity;

use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;
use pocketmine\event\entity\EntityDamageByEntityEvent;
use pocketmine\item\Item as ItemItem;
use pocketmine\entity\ai\behavior\{StrollBehavior, RandomLookaroundBehavior, LookAtPlayerBehavior, PanicBehavior, findFoodBehavior, inLoveBehavior};

class Chicken extends Animal{
	const NETWORK_ID = 10;

	public $width = 0.6;
	public $length = 0.6;
	public $height = 0;

	public $dropExp = [1, 3];
	
	public function getName() : string{
		return "Chicken";
	}
	
	public function initEntity(){
		$this->setMaxHealth(4);
		
		$this->addBehavior(new inLoveBehavior($this));
		$this->addBehavior(new PanicBehavior($this, 0.25, 2.0));
		$this->addBehavior(new findFoodBehavior($this, 296));
		$this->addBehavior(new StrollBehavior($this));
		$this->addBehavior(new LookAtPlayerBehavior($this));
		$this->addBehavior(new RandomLookaroundBehavior($this));
		
		parent::initEntity();
	}
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Chicken::NETWORK_ID;
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
		switch (\mt_rand(0, 1)) {
			case 0:
				if($this->isOnFire()){
					$drops[] = ItemItem::get(ItemItem::COOKED_CHICKEN, 0, mt_rand(1,2));
				}else{
					$drops[] = ItemItem::get(ItemItem::RAW_CHICKEN, 0, mt_rand(1,2));
				}
				break;
			case 1:
				$drops[] = ItemItem::get(ItemItem::FEATHER, 0, mt_rand(1,2));
				break;
		}
		return $drops;
	}
}