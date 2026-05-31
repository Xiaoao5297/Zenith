<?php


namespace pocketmine\entity;

use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Player;
use pocketmine\event\entity\EntityDamageByEntityEvent;
use pocketmine\item\Item as ItemItem;

use pocketmine\entity\ai\behavior\SlimeBehavior;

class LavaSlime extends Mob{
	const NETWORK_ID = 42;

	public $width = 0.3;
	public $length = 0.9;
	public $height = 2.04;

	public $dropExp = [1, 4];
	
	public function getName() : string{
		return "LavaSlime";
	}
	
	public function initEntity(){
		$this->setDataProperty(self::DATA_SLIME_SIZE, self::DATA_TYPE_BYTE, mt_rand(1,4));
		
		$this->addBehavior(new SlimeBehavior($this));
		
		parent::initEntity();
	}
	
	public function spawnTo(Player $player){
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = LavaSlime::NETWORK_ID;
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
		$drops = array(ItemItem::get(ItemItem::SLIMEBALL, 0, 1));
		if ($this->lastDamageCause instanceof EntityDamageByEntityEvent and $this->lastDamageCause->getEntity() instanceof Player) {
			if (\mt_rand(0, 199) < 5) {
				switch (\mt_rand(0, 2)) {
					case 0:
						$drops[] = ItemItem::get(ItemItem::IRON_INGOT, 0, 1);
						break;
					case 1:
						$drops[] = ItemItem::get(ItemItem::CARROT, 0, 1);
						break;
					case 2:
						$drops[] = ItemItem::get(ItemItem::POTATO, 0, 1);
						break;
				}
			}
		}
		return $drops;
	}
	
	public function getSize(){
		return $this->getDataProperty(self::DATA_SLIME_SIZE);
	}
	
	public function setSize(int $resting){
		$this->setDataProperty(self::DATA_SLIME_SIZE, self::DATA_TYPE_BYTE, $resting);
	}
}