<?php


namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;
use pocketmine\block\Air;
use pocketmine\Player;

use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\ByteTag;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\DoubleTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\nbt\tag\FloatTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\LongTag;
use pocketmine\nbt\tag\ShortTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\entity\ThrownPotion;
use pocketmine\entity\Arrow;

class ShootPlayerBehavior extends Behavior{

    public $speed;
    public $speedMultiplier;
	
	public $lookDistance = 16.0;
	public $NetworkID;
	public $player = null;
	public $timeLeft = 0;

    public function __construct(Mob $entity, int $NetWorkID, float $speed = 0.25, float $speedMultiplier = 0.75){
        parent::__construct($entity);

        $this->speed = $speed;
        $this->speedMultiplier = $speedMultiplier;
		$this->NetworkID = $NetWorkID;
    }

    public function getName() : string{
        return "投掷类敌对实体攻击";
    }

    public function shouldStart() : bool{
        $players = $this->entity->level->getPlayers();

        $find = false;
		$MinDistance = 9999;
		foreach($players as $p){
			if($this->entity->distance($p) < $this->lookDistance){
				if($this->entity->distance($p) < $MinDistance){
					if($p->isSurvival()){
						$this->player = $p;
						$MinDistance = $this->entity->distance($p);
						$find = true;
					}
				}
			}
        }
		return $find;
		
    }

    public function canContinue() : bool{
		if($this->player->isConnected() and $this->player->isAlive()){
			return $this->entity->distance($this->player) < $this->lookDistance;
		}else{
			return false;
		}
        
    }

    public function onTick(){
		$distance = $this->entity->distance($this->player);
		$this->lookAt($this->player);
		$entity = $this->entity;
		if($this->timeLeft >= 5){
			if($distance < 4){
				// 近距离后退：手动控制 motion
				$direction = $this->entity->getDirectionVector();
				$direction->y = 0;
				$level = $this->entity->getLevel();
				$coordinates = $this->entity->getPosition();
				$retreatSpeed = -(float)($this->speed * $this->speedMultiplier * 0.7 * ($this->entity->isInsideOfWater() ? 0.3 : 0.4));
				$coord = $coordinates->add($direction->multiply($retreatSpeed))->add($direction->multiply(0.5));
				$block = $level->getBlock($coord);
				$blockUp = $level->getBlock($coord->add(0, 1, 0));
				if(!$block->isSolid() and !($entity->height >= 1 and $blockUp->isSolid())){
					$entity->setMotion($direction->multiply($retreatSpeed));
				}
			}else{
				$speed = $this->speed * $this->speedMultiplier;
				$this->entity->getNavigator()->moveTo($this->player, $speed);
			}
			if($this->timeLeft > 0){
				--$this->timeLeft;
			}
		}elseif($distance <= 10){
			$this->bowAimPitch($this->player, 0.04);
			$this->entity->level->addEntityMovement($this->entity->chunk->getX(), $this->entity->chunk->getZ(), $this->entity->getID(), $this->entity->x, $this->entity->y + $this->entity->getEyeHeight(), $this->entity->z, $this->entity->yaw, $this->entity->pitch, $this->entity->yaw);
			if($this->timeLeft <= 0){
				if($this->NetworkID == 86){ //药水Potion
					if($distance >= 8){
						$Damage = 17; //缓慢 1分7秒
					}elseif($this->player->getHealth() >= 8){
						$Damage = 25; //中毒 33秒
					}elseif($distance <= 3){
						$Damage = 34; //虚弱 1分7秒
					}else{
						$Damage = 23; //瞬间伤害
					}

					$pitch = $entity->pitch;

					$nbt = new CompoundTag("", [
						"Pos" => new ListTag("Pos", [
							new DoubleTag("", $entity->x),
							new DoubleTag("", $entity->y + 1.62),
							new DoubleTag("", $entity->z)
						]),
						"Motion" => new ListTag("Motion", [
							new DoubleTag("", -sin($entity->yaw / 180 * M_PI) * cos($pitch / 180 * M_PI)),
							new DoubleTag("", -sin(($pitch) / 180 * M_PI)),
							new DoubleTag("", cos($entity->yaw / 180 * M_PI) * cos($pitch / 180 * M_PI))
						]),
						"Rotation" => new ListTag("Rotation", [
							new FloatTag("", $entity->yaw),
							new FloatTag("", $pitch)
						]),
						"PotionId" => new ShortTag("PotionId", $Damage),
					]);

					$f = 1.1;
					$thrownPotion = new ThrownPotion($entity->chunk, $nbt, $entity);
					$thrownPotion->setMotion($thrownPotion->getMotion()->multiply($f));
					$thrownPotion->spawnToAll();
					$this->timeLeft = 40; //每种药水以2秒的间隔投掷。
				}elseif($this->NetworkID == 80){ //Arrow
					$pitch = $this->bowAimPitch($this->player, 0.04);
					$nbt = new CompoundTag("", [
						"Pos" => new ListTag("Pos", [
							new DoubleTag("", $entity->x),
							new DoubleTag("", $entity->y + 1.62),
							new DoubleTag("", $entity->z)
						]),
						"Motion" => new ListTag("Motion", [
							new DoubleTag("", -sin($entity->yaw / 180 * M_PI) * cos($pitch / 180 * M_PI)),
							new DoubleTag("", -sin(($pitch) / 180 * M_PI)),
							new DoubleTag("", cos($entity->yaw / 180 * M_PI) * cos($pitch / 180 * M_PI))
						]),
						"Rotation" => new ListTag("Rotation", [
							new FloatTag("", $entity->yaw),
							new FloatTag("", $pitch)
						]),
						"Fire" => new ShortTag("Fire", $entity->isOnFire() ? 45 * 60 : 0)
					]);

					$f = 1.1;
					$Arrow = new Arrow($entity->chunk, $nbt, $entity);
					$Arrow->setMotion($Arrow->getMotion()->multiply($f));
					$Arrow->spawnToAll();
					$this->timeLeft = 40; //在简单和普通难度中每2秒发射一次，在困难难度中每1秒发射一次。

					//骷髅会主动逃离狼；狼会主动尝试攻击骷髅。
				}else{
					$this->timeLeft = 40;
				}
			}else{
				--$this->timeLeft;
			}
		}
		$this->swimming();
    }

    public function onEnd(){
        $this->entity->getNavigator()->clearPath();
        $this->entity->setMotion(new Vector3(0,0,0));
    }

}