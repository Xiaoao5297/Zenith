<?php

namespace pocketmine\entity;

use pocketmine\block\Wool;
use pocketmine\nbt\tag\ByteTag;
use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;
use pocketmine\item\Item as ItemItem;
use pocketmine\level\format\FullChunk;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\entity\ai\behavior\{StrollBehavior, RandomLookaroundBehavior, LookAtPlayerBehavior, PanicBehavior, findFoodBehavior, inLoveBehavior, eatGrassBehavior};

class Sheep extends Animal implements Colorable{
	const NETWORK_ID = 13;


	public $width = 0.625;
	public $length = 1.4375;
	public $height = 1.1;
	
	public function getName() : string{
		return "Sheep";
	}
	
	public function initEntity(){
		$this->setMaxHealth(8);
		
		$this->addBehavior(new inLoveBehavior($this));
		$this->addBehavior(new PanicBehavior($this, 0.25, 2.0));
		$this->addBehavior(new eatGrassBehavior($this));
		$this->addBehavior(new findFoodBehavior($this, 296));
		$this->addBehavior(new StrollBehavior($this));
		$this->addBehavior(new LookAtPlayerBehavior($this));
		$this->addBehavior(new RandomLookaroundBehavior($this));
		
		parent::initEntity();
	}

	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		if(!isset($nbt->Color)){
			$nbt->Color = new ByteTag("Color", self::getRandomColor());
		}
		parent::__construct($chunk, $nbt);

		$this->sethasWool(true);
	}

	public static function getRandomColor() : int{
		$rand = "";
		$rand .= str_repeat(Wool::WHITE . " ", 20);
		$rand .= str_repeat(Wool::ORANGE . " ", 5);
		$rand .= str_repeat(Wool::MAGENTA . " ", 5);
		$rand .= str_repeat(Wool::LIME . " ", 5);
		$rand .= str_repeat(Wool::LIGHT_BLUE . " ", 5);
		$rand .= str_repeat(Wool::YELLOW . " ", 5);
		$rand .= str_repeat(Wool::PINK . " ", 5);
		$rand .= str_repeat(Wool::GRAY . " ", 10);
		$rand .= str_repeat(Wool::LIGHT_GRAY . " ", 5);
		$rand .= str_repeat(Wool::CYAN . " ", 5);
		$rand .= str_repeat(Wool::PURPLE . " ", 5);
		$rand .= str_repeat(Wool::BLUE . " ", 5);
		$rand .= str_repeat(Wool::BROWN . " ", 5);
		$rand .= str_repeat(Wool::GREEN . " ", 5);
		$rand .= str_repeat(Wool::RED . " ", 5);
		$rand .= str_repeat(Wool::BLACK . " ", 5);
		$arr = explode(" ", $rand);
		return $arr[mt_rand(0, count($arr) - 16)];
	}

	public function getColor() : int{
		return (int) $this->namedtag["Color"];
	}

	public function setColor(int $color){
		$this->namedtag->Color = new ByteTag("Color", $color);
	}
	
	public function sethasWool(bool $resting){
		if($resting){
			$this->setDataProperty(self::DATA_COLOR_INFO, self::DATA_TYPE_BYTE, $this->getColor());
		}else{
			$this->setDataProperty(self::DATA_COLOR_INFO, self::DATA_TYPE_BYTE, 16);
		}
	}
	
	public function hasWool(){
		return $this->getDataProperty(self::DATA_COLOR_INFO) !== 16;
	}
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Sheep::NETWORK_ID;
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
		$drops = [
			ItemItem::get(ItemItem::WOOL, $this->getColor(), 1)
		];
		return $drops;
	}
}