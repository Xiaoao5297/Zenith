<?php

namespace pocketmine\entity;

use pocketmine\nbt\tag\ByteTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\level\format\FullChunk;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\nbt\NBT;
use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;
use pocketmine\entity\ai\behavior\{StrollBehavior, RandomLookAroundBehavior, LookAtPlayerBehavior, PanicBehavior};
use pocketmine\entity\trade\VillagerTradeFactory;
use pocketmine\entity\trade\VillagerTradeOffer;
use pocketmine\inventory\VillagerTradeInventory;
use pocketmine\item\Item as ItemItem;

class Villager extends Mob implements NPC, Ageable{
	const PROFESSION_FARMER = 0;
	const PROFESSION_LIBRARIAN = 1;
	const PROFESSION_PRIEST = 2;
	const PROFESSION_BLACKSMITH = 3;
	const PROFESSION_BUTCHER = 4;
	//const PROFESSION_GENERIC = 5;

	const NETWORK_ID = 15;

	public $width = 0.6;
	public $length = 0.6;
	public $height = 0.8;
	/** @var VillagerTradeOffer[]|null */
	private $tradeOffers = null;

	public function getName() : string{
		return "Villager";
	}

	public static function getProfessionDisplayName(int $profession) : string{
		switch(min(4, max(0, $profession))){
			case self::PROFESSION_FARMER:
				return "农民";
			case self::PROFESSION_LIBRARIAN:
				return "图书管理员";
			case self::PROFESSION_PRIEST:
				return "牧师";
			case self::PROFESSION_BLACKSMITH:
				return "铁匠";
			case self::PROFESSION_BUTCHER:
				return "屠夫";
			default:
				return "村民";
		}
	}

	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		parent::__construct($chunk, $nbt);
		
		$this->addBehavior(new PanicBehavior($this, 0.25, 40));
		$this->addBehavior(new StrollBehavior($this));
		$this->addBehavior(new LookAtPlayerBehavior($this));
		$this->addBehavior(new RandomLookAroundBehavior($this));

		$this->setDataProperty(self::DATA_PROFESSION_ID, self::DATA_TYPE_BYTE, $this->getProfession());
	}

	public function initEntity(){
		parent::initEntity();
		if(!isset($this->namedtag->Profession)){
			$this->setProfession(mt_rand(0, 4)); //随机生成村民职业
		}
		self::ensureTradeData($this->namedtag);
		$this->tradeOffers = self::loadTradeOffersFromNBT($this->namedtag);
		if(!isset($this->namedtag->IsBaby)){
			$this->namedtag->IsBaby = new ByteTag("IsBaby", 1);
			$this->setBaby(false);
		}
	}

	public function saveNBT(){
		parent::saveNBT();
		$this->namedtag->Profession = new ByteTag("Profession", $this->getProfession());
		$this->saveTradeOffersToNBT($this->namedtag);
	}

	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = Villager::NETWORK_ID;
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

	/**
	 * Sets the villager profession
	 *
	 * @param int $profession
	 */
	public function setProfession(int $profession){
		$this->namedtag->Profession = new ByteTag("Profession", $profession);
	}

	public function getProfession() : int{
		$pro = (int) $this->namedtag["Profession"];
		return min(4, max(0, $pro));
	}

	/**
	 * @return VillagerTradeOffer[]
	 */
	public function getTradeOffers() : array{
		if($this->tradeOffers === null){
			self::ensureTradeData($this->namedtag);
			$this->tradeOffers = self::loadTradeOffersFromNBT($this->namedtag);
		}

		return $this->tradeOffers;
	}

	public function setTradeOffers(array $offers){
		$this->tradeOffers = array_values(array_filter($offers, function($offer){
			return $offer instanceof VillagerTradeOffer;
		}));
		$this->saveTradeOffersToNBT($this->namedtag);
	}

	public function saveTradeOffersToNBT(CompoundTag $nbt = null){
		$nbt = $nbt ?? $this->namedtag;
		$nbt->Offers = self::offersToNBT($this->getTradeOffers());
	}

	public static function ensureTradeData(CompoundTag $nbt, $seed = null){
		if(isset($nbt->Offers) and $nbt->Offers instanceof ListTag and $nbt->Offers->getCount() > 0){
			return;
		}

		$profession = isset($nbt->Profession) ? min(4, max(0, (int) $nbt["Profession"])) : self::PROFESSION_FARMER;
		$nbt->Offers = self::offersToNBT(VillagerTradeFactory::generateOffers($profession, $seed ?? mt_rand(1, PHP_INT_MAX)));
	}

	/**
	 * @return VillagerTradeOffer[]
	 */
	public static function loadTradeOffersFromNBT(CompoundTag $nbt) : array{
		if(!isset($nbt->Offers) or !($nbt->Offers instanceof ListTag)){
			return [];
		}

		$offers = [];
		foreach($nbt->Offers as $tag){
			if($tag instanceof CompoundTag){
				$offers[] = VillagerTradeOffer::fromNBT($tag);
			}
		}

		return $offers;
	}

	/**
	 * @param VillagerTradeOffer[] $offers
	 */
	public static function offersToNBT(array $offers) : ListTag{
		$list = new ListTag("Offers", []);
		$list->setTagType(NBT::TAG_Compound);
		foreach(array_values($offers) as $index => $offer){
			if($offer instanceof VillagerTradeOffer){
				$list->{$index} = $offer->toNBT($index);
			}
		}

		return $list;
	}

	public function openTradeWindow(Player $player, int $currentTradeIndex = 0) : int{
		if($this->isBaby()){
			return -1;
		}

		if(count($this->getTradeOffers()) === 0){
			self::ensureTradeData($this->namedtag);
			$this->tradeOffers = self::loadTradeOffersFromNBT($this->namedtag);
		}

		return $player->addWindow(VillagerTradeInventory::createForPlayer($this, $player, $currentTradeIndex));
	}

	public function isBaby(){
		return $this->namedtag["IsBaby"] == 0 ? false : true;
	}
	
	public function setBaby(bool $resting){
		$this->setDataProperty(self::DATA_VILLAGER_IS_BABY, self::DATA_TYPE_BYTE, $resting ? 1 : 0);
		$this->namedtag->IsBaby = new ByteTag("IsBaby", $resting ? 1 : 0);
	}
	
	public function getDrops(){
        return [
            ItemItem::get(ItemItem::EMERALD, 0, \mt_rand(0, 2))
        ];
    }
}
