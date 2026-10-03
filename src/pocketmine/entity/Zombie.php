<?php


namespace pocketmine\entity;

use pocketmine\nbt\tag\ByteTag;
use pocketmine\item\Item as ItemItem;
use pocketmine\network\Network;
use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;
use pocketmine\event\entity\EntityDamageByEntityEvent;
use pocketmine\entity\ai\behavior\{StrollBehavior, RandomLookAroundBehavior, AttackEnemyBehavior};

class Zombie extends Monster implements Ageable{
	use MobEquipmentTrait;

	const NETWORK_ID = 32;

	public $width = 0.6;
	public $length = 0.6;
	public $height = 1.95;
	
	public $dropExp = [5, 5];
	
	private $hurt = 8;
	
	public function getName() : string{
		return "Zombie";
	}
	
	public function initEntity(){
		$this->setMaxHealth(20);
		
		if(!isset($this->namedtag->IsBaby)){
			$this->namedtag->IsBaby = new ByteTag("IsBaby", 1);
			$this->setBaby(false);
		}
		
		$this->addBehavior(new AttackEnemyBehavior($this, [20], true));
		$this->addBehavior(new StrollBehavior($this));
		$this->addBehavior(new RandomLookAroundBehavior($this));
		
		parent::initEntity();

		$equipment = VanillaMobEquipment::generateZombieEquipment($this->server->getDifficulty());
		$this->setMobEquipment($equipment["armor"], $equipment["weapon"]);
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
		$pk->type = Zombie::NETWORK_ID;
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

		$this->sendMobEquipment($player);
	}

	protected function handlesLootingDrops() : bool{
		return true;
	}

	public function getDrops(){
		$rareDrops = [];
		$looting = $this->getLastDamageLootingLevel();
		if(mt_rand(1, 1000) <= VanillaMobEquipment::rareDropChance($looting)){
			switch(mt_rand(0, 2)){
				case 0:
					$rareDrops[] = ItemItem::get(ItemItem::IRON_INGOT, 0, 1);
					break;
				case 1:
					$rareDrops[] = ItemItem::get(ItemItem::CARROT, 0, 1);
					break;
				case 2:
					$rareDrops[] = ItemItem::get(ItemItem::POTATO, 0, 1);
					break;
			}
		}
		$drops = [ItemItem::get(ItemItem::ROTTEN_FLESH, 0, mt_rand(0, 2))];
		$drops = VanillaMobEquipment::applyLootingToCommonDrops($drops, $looting);
		foreach($rareDrops as $drop){
			$drops[] = $drop;
		}
		foreach(VanillaMobEquipment::maybeDropEquipment($this->getArmorContents(), $this->getWeapon(), $looting) as $drop){
			$drops[] = $drop;
		}
		return $drops;
	}
	
	public function isBaby(){
		return $this->namedtag["IsBaby"] == 0 ? false : true;
	}
	
	public function setBaby(bool $resting){
		$this->setDataProperty(self::DATA_ZOMBIE_IS_BABY, self::DATA_TYPE_BYTE, $resting ? 1 : 0);
		$this->namedtag->IsBaby = new ByteTag("IsBaby", $resting ? 1 : 0);
	}
}
