<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Arrow;
use pocketmine\entity\Fireball;
use pocketmine\entity\Mob;
use pocketmine\entity\Projectile;
use pocketmine\entity\SmallFireball;
use pocketmine\entity\ThrownPotion;
use pocketmine\math\Vector3;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\DoubleTag;
use pocketmine\nbt\tag\FloatTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\nbt\tag\ShortTag;
use pocketmine\Player;

class ShootPlayerBehavior extends Behavior{

	/** @var float */
	public $speed;

	/** @var float */
	public $lookDistance = 16.0;

	/** @var int */
	public $NetworkID;

	/** @var Player|null */
	public $player = null;

	/** @var int */
	public $shootCooldown = 0;

	/** @var int */
	private $searchCooldown = 0;

	/** @var int */
	private $canSeeCooldown = 0;

	/** @var bool */
	private $canSeeTarget = true;

	/** @var int */
	private $lostTicks = 0;

	public function __construct(Mob $entity, int $NetWorkID, float $speed = 0.35){
		parent::__construct($entity);
		$this->speed = $speed;
		$this->NetworkID = $NetWorkID;
	}

	public function getPriority() : int{
		return 2;
	}

	public function getControlFlags() : int{
		return self::CONTROL_MOVE | self::CONTROL_LOOK;
	}

	public function getName() : string{
		return "ShootPlayer";
	}

	public function shouldStart() : bool{
		if($this->isValidTarget($this->player)){
			return true;
		}
		$this->player = null;

		if($this->searchCooldown > 0){
			--$this->searchCooldown;
			return false;
		}
		$this->searchCooldown = 10;

		$best = null;
		$bestDistance = $this->lookDistance;
		foreach($this->entity->level->getPlayers() as $player){
			if(!$player->isAlive() or !$player->isConnected() or !$player->isSurvival()){
				continue;
			}
			$distance = $this->entity->distance($player);
			if($distance < $bestDistance){
				$best = $player;
				$bestDistance = $distance;
			}
		}

		$this->player = $best;
		return $best !== null;
	}

	private function isValidTarget($target) : bool{
		if(!$target instanceof Player or !$target->isAlive() or !$target->isConnected() or !$target->isSurvival()){
			return false;
		}
		return $this->entity->distance($target) <= $this->lookDistance + 4;
	}

	public function canContinue() : bool{
		if(!$this->isValidTarget($this->player)){
			return false;
		}
		return $this->lostTicks < 60 or $this->entity->distance($this->player) <= 4.0;
	}

	public function onTick(){
		if($this->player === null){
			return;
		}

		$distance = $this->entity->distance($this->player);
		$this->lookAt($this->player, false);

		if($this->canSeeCooldown > 0){
			--$this->canSeeCooldown;
		}else{
			$this->canSeeCooldown = 5;
			$this->canSeeTarget = $this->canSee($this->player);
			if($this->canSeeTarget){
				$this->lostTicks = 0;
			}
		}
		if(!$this->canSeeTarget){
			++$this->lostTicks;
		}

		// 太近就后退，太远就靠近，射程内停下
		if($distance < 4.0 and $distance >= 1.5){
			$retreat = $this->entity->add(
				($this->entity->x - $this->player->x) * 2,
				0,
				($this->entity->z - $this->player->z) * 2
			);
			$this->entity->getNavigator()->moveTo($retreat, $this->speed * 0.7);
		}elseif($distance >= 6.0 and ($this->canSeeTarget or $this->lostTicks < 20)){
			$this->entity->getNavigator()->moveTo($this->player, $this->speed);
		}else{
			$this->entity->getNavigator()->clearPath();
		}

		if($this->shootCooldown > 0){
			--$this->shootCooldown;
		}

		if($distance <= 12.0 and $this->canSeeTarget and $this->shootCooldown <= 0){
			$this->shoot($this->player);
			$ratio = min(1.0, $distance / 12.0);
			$this->shootCooldown = 30 + mt_rand(0, (int) (30 * $ratio));
		}

		$this->swimming();
	}

	private function shoot(Player $target) : void{
		$entity = $this->entity;
		$pitch = $this->bowAimPitch($target, 0.04);

		$tags = [
			"Pos" => new ListTag("Pos", [
				new DoubleTag("", $entity->x),
				new DoubleTag("", $entity->y + $entity->getEyeHeight()),
				new DoubleTag("", $entity->z)
			]),
			"Motion" => new ListTag("Motion", [
				new DoubleTag("", -sin($entity->yaw / 180 * M_PI) * cos($pitch / 180 * M_PI)),
				new DoubleTag("", -sin($pitch / 180 * M_PI)),
				new DoubleTag("", cos($entity->yaw / 180 * M_PI) * cos($pitch / 180 * M_PI))
			]),
			"Rotation" => new ListTag("Rotation", [
				new FloatTag("", $entity->yaw),
				new FloatTag("", $pitch)
			])
		];

		if($this->NetworkID === 86){
			$tags["PotionId"] = new ShortTag("PotionId", 23);
			$nbt = new CompoundTag("", $tags);
			$projectile = new ThrownPotion($entity->chunk, $nbt, $entity);
		}elseif($this->NetworkID === 80){
			$tags["Fire"] = new ShortTag("Fire", $entity->isOnFire() ? 45 * 60 : 0);
			$nbt = new CompoundTag("", $tags);
			$projectile = new Arrow($entity->chunk, $nbt, $entity);
			if(method_exists($entity, "getProjectileArrowItem")){
				$projectile->setArrowItem($entity->getProjectileArrowItem());
			}
		}elseif($this->NetworkID === 85){
			$nbt = new CompoundTag("", $tags);
			$projectile = new Fireball($entity->chunk, $nbt, $entity);
		}elseif($this->NetworkID === 94){
			$nbt = new CompoundTag("", $tags);
			$projectile = new SmallFireball($entity->chunk, $nbt, $entity);
		}else{
			return;
		}

		/** @var Projectile $projectile */
		$projectile->setMotion($projectile->getMotion()->multiply(1.1));
		$projectile->spawnToAll();
	}

	public function onEnd(){
		$this->player = null;
		$this->shootCooldown = 0;
		$this->searchCooldown = 0;
		$this->lostTicks = 0;
		$this->entity->getNavigator()->clearPath();
	}
}
