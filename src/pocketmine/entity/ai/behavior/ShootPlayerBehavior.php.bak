<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;
use pocketmine\Player;

use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\DoubleTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\nbt\tag\FloatTag;
use pocketmine\nbt\tag\ShortTag;
use pocketmine\entity\ThrownPotion;
use pocketmine\entity\Arrow;

class ShootPlayerBehavior extends Behavior{

    public $speed;
	
	public $lookDistance = 16.0;
	public $NetworkID;
	public $player = null;
	public $shootCooldown = 0;

    public function __construct(Mob $entity, int $NetWorkID, float $speed = 0.35){
        parent::__construct($entity);
        $this->speed = $speed;
		$this->NetworkID = $NetWorkID;
    }

    public function getPriority(): int{
        return 2;
    }

    public function getName() : string{
        return "ShootPlayer";
    }

    public function shouldStart() : bool{
        $players = $this->entity->level->getPlayers();
        $find = false;
		$minDist = $this->lookDistance;
		foreach($players as $p){
			$dist = $this->entity->distance($p);
			if($dist < $minDist and $p->isSurvival()){
				$this->player = $p;
				$minDist = $dist;
				$find = true;
			}
        }
		return $find;
    }

    public function canContinue() : bool{
		if($this->player === null) return false;
		if($this->player->isConnected() and $this->player->isAlive()){
			return $this->entity->distance($this->player) < $this->lookDistance;
		}
		return false;
    }

    public function onTick(){
		$distance = $this->entity->distance($this->player);
		$this->lookAt($this->player);
		$entity = $this->entity;

		if($this->shootCooldown > 0){
			$this->shootCooldown--;
		}

		if($distance < 4 and $distance >= 1.5){
			$retreat = $this->entity->add(
				($this->entity->x - $this->player->x) * 2,
				0,
				($this->entity->z - $this->player->z) * 2
			);
			$this->entity->getNavigator()->moveTo($retreat, $this->speed * 0.7);
		}elseif($distance >= 4){
			$this->entity->getNavigator()->moveTo($this->player, $this->speed);
		}

		if($distance <= 10 and $this->shootCooldown <= 0){
			$this->bowAimPitch($this->player, 0.04);
			$entity->level->addEntityMovement($entity->chunk->getX(), $entity->chunk->getZ(), $entity->getID(), $entity->x, $entity->y + $entity->getEyeHeight(), $entity->z, $entity->yaw, $entity->pitch, $entity->yaw);

			if($this->NetworkID == 86){
				$Damage = 23;
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
$this->shootCooldown = 40;
		}elseif($this->NetworkID == 80){
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
				$this->shootCooldown = 40;
			}else{
				$this->shootCooldown = 40;
			}
		}
		$this->swimming();
    }

    public function onEnd(){
        $this->entity->getNavigator()->clearPath();
    }

}

