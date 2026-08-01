<?php


namespace pocketmine\entity;

use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;
use pocketmine\item\Item as ItemItem;
use pocketmine\entity\ai\behavior\{StrollBehavior, RandomLookAroundBehavior, LookAtPlayerBehavior, PanicBehavior, FindFoodBehavior, InLoveBehavior};

use pocketmine\nbt\tag\ByteTag;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\level\format\FullChunk;

class Pig extends Animal{
	const NETWORK_ID = 12;

	public $width = 0.3;
	public $length = 0.9;
	public $height = 1.1;

	public $dropExp = [1, 3];
	
	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		parent::__construct($chunk, $nbt);
	}
	
	public function getName() : string{
		return "Pig";
	}
	
	public function initEntity(){
		
		$this->addBehavior(new InLoveBehavior($this));
		$this->addBehavior(new PanicBehavior($this, 0.25, 40));
		$this->addBehavior(new FindFoodBehavior($this, 391));
		$this->addBehavior(new StrollBehavior($this));
		$this->addBehavior(new LookAtPlayerBehavior($this));
		$this->addBehavior(new RandomLookAroundBehavior($this));
		
		$this->setMaxHealth(10);
		parent::initEntity();
	}
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Pig::NETWORK_ID;
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
		if($this->isOnFire()){
			$drops = [ItemItem::get(ItemItem::COOKED_PORKCHOP, 0, mt_rand(1,2))];
		}else{
			$drops = [ItemItem::get(ItemItem::RAW_PORKCHOP, 0, mt_rand(1,2))];
		}
		return $drops;
	}
}