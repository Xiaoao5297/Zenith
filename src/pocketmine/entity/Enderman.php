<?php


namespace pocketmine\entity;

use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\nbt\tag\ShortTag;
use pocketmine\Player;
use pocketmine\item\Item as ItemItem;
use pocketmine\entity\ai\behavior\{StrollBehavior, RandomLookaroundBehavior, LookAtPlayerBehavior, PanicBehavior};

class Enderman extends Monster{
	const NETWORK_ID = 38;

	public $width = 0.3;
	public $length = 0.9;
	public $height = 1.8;

	public $dropExp = [5, 5];
	
	public function initEntity(){
		if(!isset($this->namedtag->carriedData)){
			$this->namedtag->carriedData = new ShortTag("carriedData", 0);
			$this->namedtag->carried = new ShortTag("carried", 0);
		}
		$this->addBehavior(new PanicBehavior($this, 0.25, 2.0));
		$this->addBehavior(new StrollBehavior($this));
		$this->addBehavior(new LookAtPlayerBehavior($this));
		$this->addBehavior(new RandomLookaroundBehavior($this));
		parent::initEntity();
	}
	
	public function getName() : string{
		return "Enderman";
	}
	
	public function setTremble(bool $setting){
		$this->setDataProperty(self::DATA_ENDERMAN_TREMBLE, self::DATA_TYPE_BYTE, $setting ? 1 : 0);
	}
	
	public function setBlockInHand(int $id, int $meta){
		$this->setDataProperty(self::DATA_ENDERMAN_HELD_ITEM_ID, self::DATA_TYPE_SHORT, $id);
		$this->setDataProperty(self::DATA_ENDERMAN_HELD_ITEM_DAMAGE, self::DATA_TYPE_SHORT, $meta);
		$this->namedtag->carriedData = new ShortTag("carriedData", $id);
		$this->namedtag->carried = new ShortTag("carried", $meta);
	}
	
	public function getBlockInHand(&$id, &$meta){
		$id = $this->namedtag["carriedData"];
		$meta = $this->namedtag["carried"];
	}
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Enderman::NETWORK_ID;
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
        return [
            ItemItem::get(ItemItem::END_STONE, 0, \mt_rand(0, 2))
        ];
    }
}