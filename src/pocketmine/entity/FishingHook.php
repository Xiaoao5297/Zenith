<?php


namespace pocketmine\entity;

use pocketmine\event\player\PlayerFishEvent;
use pocketmine\level\format\FullChunk;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\Player;
use pocketmine\item\Item as ItemItem;
use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\Server;
use pocketmine\math\Vector3;
use pocketmine\level\particle\Particle;
use pocketmine\level\particle\WaterParticle;
use pocketmine\level\sound\sound;
use pocketmine\level\sound\SplashSound;

class FishingHook extends Projectile{
	const NETWORK_ID = 77;

	public $width = 0.25;
	public $length = 0.25;
	public $height = 0.25;

	protected $gravity = 0.1;
	protected $drag = 0.05;

	public $data = 0;
	public $attractTimer = 100;
	public $coughtTimer = 0;

	public function initEntity(){
		parent::initEntity();

		if(isset($this->namedtag->Data)){
			$this->data = $this->namedtag["Data"];
		}

		// $this->setDataProperty(FallingSand::DATA_BLOCK_INFO, self::DATA_TYPE_INT, $this->getData());
	}

	public function __construct(FullChunk $chunk, CompoundTag $nbt, Entity $shootingEntity = null){
		parent::__construct($chunk, $nbt, $shootingEntity);
	}

	public function setData($id){
		$this->data = $id;
	}

	public function getData(){
		return $this->data;
	}

	public function onUpdate($currentTick){
		if($this->closed){
			return false;
		}

		$this->timings->startTiming();

		$hasUpdate = parent::onUpdate($currentTick);

		if($this->isCollidedVertically && $this->isInsideOfWater()){
			$this->motionX = 0;
			$this->motionY += 0.01;
			$this->motionZ = 0;
			$this->motionChanged = true;
			$hasUpdate = true;
			if($this->attractTimer === 0 && mt_rand(0, 200) <= 20){ // chance, that a fish bites
				$this->coughtTimer = mt_rand(5, 10) * 20; // random delay to catch fish
				$this->attractTimer = mt_rand(30, 100) * 20; // reset timer
				$this->reeline();
			}elseif($this->attractTimer > 0){
				$this->attractTimer--;
			}
			if($this->coughtTimer > 0){
				$this->coughtTimer--;
			}
		}elseif($this->isCollided && $this->keepMovement === true){
			$this->motionX = 0;
			$this->motionY = 0;
			$this->motionZ = 0;
			$this->motionChanged = true;
			$this->keepMovement = false;
			$hasUpdate = true;
		}
		
		$this->timings->stopTiming();

		return $hasUpdate;
	}
	
	public function reeline(){
		if($this->shootingEntity instanceof Player){
			$pos = new Vector3($this->x + 0.2, $this->y + 1, $this->z);
			$this->shootingEntity->getLevel()->addParticle(new WaterParticle($pos)); //水花粒子
			$pos = new Vector3($this->x - 0.2, $this->y + 1, $this->z);
			$this->shootingEntity->getLevel()->addParticle(new WaterParticle($pos)); //水花粒子
			$pos = new Vector3($this->x, $this->y + 1, $this->z + 0.2);
			$this->shootingEntity->getLevel()->addParticle(new WaterParticle($pos)); //水花粒子
			$pos = new Vector3($this->x, $this->y + 1, $this->z - 0.2);
			$this->shootingEntity->getLevel()->addParticle(new WaterParticle($pos)); //水花粒子
			$pos = new Vector3($this->x + 0.2, $this->y + 1, $this->z + 0.2);
			$this->shootingEntity->getLevel()->addParticle(new WaterParticle($pos)); //水花粒子
			$pos = new Vector3($this->x - 0.2, $this->y + 1, $this->z - 0.2);
			$this->shootingEntity->getLevel()->addParticle(new WaterParticle($pos)); //水花粒子
			$pos = new Vector3($this->x - 0.2, $this->y + 1, $this->z + 0.2);
			$this->shootingEntity->getLevel()->addParticle(new WaterParticle($pos)); //水花粒子
			$pos = new Vector3($this->x + 0.2, $this->y + 1, $this->z - 0.2);
			$this->shootingEntity->getLevel()->addParticle(new WaterParticle($pos)); //水花粒子
				
			$pos = new Vector3($this->x, $this->y, $this->z);
			$this->shootingEntity->getLevel()->addSound(new SplashSound($pos, 5)); //水声音
		}

		$fishes = [ItemItem::RAW_FISH, ItemItem::RAW_SALMON, ItemItem::CLOWN_FISH, ItemItem::PUFFER_FISH];
		$fish = array_rand($fishes, 1);
		$item = ItemItem::get($fishes[$fish]);
		$this->getLevel()->getServer()->getPluginManager()->callEvent($ev = new PlayerFishEvent($this->shootingEntity, $item, $this));
		$this->shootingEntity->getInventory()->addItem($item);
     	$this->shootingEntity->addExperience(mt_rand(2, 12));
		
		if($this->shootingEntity instanceof Player){
			$this->shootingEntity->unlinkHookFromPlayer();
		}
		
		if(!$this->closed){
			$this->kill();
			$this->close();
		}
	}

	public function spawnTo(Player $player){		
		$pk = new AddEntityPacket();
		$pk->eid = $this->getId();
		$pk->type = FishingHook::NETWORK_ID;
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
