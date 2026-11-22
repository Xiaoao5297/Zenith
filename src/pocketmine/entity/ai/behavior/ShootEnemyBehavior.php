<?php

/*
 *   _  __   __  _   _  ___  _  _  _
 *  | |/ /  / / | | | ||_ _|| \| || |
 *  | ' /  / /  | |_| | | | | .` || |__
 *  |_|\_\ /_/   \___/ |___||_|\_||____|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 */

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Entity;
use pocketmine\entity\Mob;
use pocketmine\entity\Projectile;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\DoubleTag;
use pocketmine\nbt\tag\FloatTag;
use pocketmine\nbt\tag\ListTag;

/**
 * 雪傀儡等投掷型生物的攻击行为（参考 InCore-II ShootEnemyBehavior）。
 *
 * 与近战 AttackEnemyBehavior 的区别：
 *  - 只锁定 NETWORK_ID 在白名单里的实体，不会攻击玩家；
 *  - 保持 4~6 格的中距离：太远靠近、太近后退、射程内站定；
 *  - 冷却结束后从眼睛高度抛出配置的投掷物（默认雪球）。
 */
class ShootEnemyBehavior extends Behavior{

	/** 两次开火之间的冷却 tick，与 InCore 的 40 tick 一致 */
	const SHOOT_COOLDOWN = 40;

	/** @var float */
	public $speed;

	/** @var float InCore 的移动倍率，保留用于后续调整后退速度 */
	public $speedMultiplier;

	/** @var float 索敌半径 */
	public $lookDistance = 16.0;

	/** @var float 开火距离 */
	public $shootDistance = 10.0;

	/** @var int[] 允许攻击的 NETWORK_ID 列表 */
	public $networkIds;

	/** @var string 投掷物实体短名或类名，交给 Entity::createEntity 解析 */
	public $projectileClass;

	/** @var \pocketmine\entity\Entity|null */
	public $enemy = null;

	/** @var int */
	public $shootCooldown = 0;

	/** @var int 目标搜索节流，避免每 tick 全量扫描 */
	private $searchCooldown = 0;

	/** @var int */
	private $canSeeCooldown = 0;

	/** @var bool */
	private $canSeeTarget = true;

	public function __construct(Mob $entity, array $networkIds, string $projectileClass = "Snowball", float $speed = 0.25, float $speedMultiplier = 0.75){
		parent::__construct($entity);
		$this->networkIds = $networkIds;
		$this->projectileClass = $projectileClass;
		$this->speed = $speed;
		$this->speedMultiplier = $speedMultiplier;
	}

	public function getPriority() : int{
		return 2;
	}

	public function getControlFlags() : int{
		return self::CONTROL_MOVE | self::CONTROL_LOOK;
	}

	public function getName() : string{
		return "ShootEnemy";
	}

	/**
	 * 目标合法性判定（纯函数，供离线测试）。
	 *
	 * 距离边界取 <=：距离恰好等于 lookDistance 仍算在攻击范围内，
	 * 与 InCore 的配置语义保持一致。
	 */
	public static function acceptsTarget(bool $alive, bool $self, int $networkId, array $networkIds, float $distance, float $lookDistance) : bool{
		if(!$alive or $self){
			return false;
		}
		if(!in_array($networkId, $networkIds, true)){
			return false;
		}
		return $distance <= $lookDistance;
	}

	/**
	 * 是否满足开火条件（纯函数，供离线测试）。
	 *
	 * 三个条件同时成立：在射程内、有视线、冷却结束。
	 */
	public static function canShoot(bool $canSee, float $distance, float $shootDistance, int $cooldown) : bool{
		return $canSee && $distance <= $shootDistance && $cooldown <= 0;
	}

	public function shouldStart() : bool{
		if($this->isValidTarget($this->enemy)){
			return true;
		}
		$this->enemy = null;

		if($this->searchCooldown > 0){
			--$this->searchCooldown;
			return false;
		}
		$this->searchCooldown = 10;
		return $this->findTarget();
	}

	private function findTarget() : bool{
		$level = $this->entity->getLevel();
		if($level === null){
			return false;
		}

		$best = null;
		$bestDistance = $this->lookDistance;
		$bb = $this->entity->boundingBox->grow($this->lookDistance, 4, $this->lookDistance);

		foreach($level->getNearbyEntities($bb, $this->entity) as $entity){
			$distance = $this->entity->distance($entity);
			if(!self::acceptsTarget(
				$entity->isAlive(),
				$entity === $this->entity,
				(int) $entity::NETWORK_ID,
				$this->networkIds,
				$distance,
				$this->lookDistance
			)){
				continue;
			}
			if($distance > $bestDistance){
				continue;
			}

			$best = $entity;
			$bestDistance = $distance;
		}

		$this->enemy = $best;
		return $best !== null;
	}

	private function isValidTarget($target) : bool{
		if($target === null){
			return false;
		}

		return self::acceptsTarget(
			$target->isAlive(),
			$target === $this->entity,
			(int) $target::NETWORK_ID,
			$this->networkIds,
			$this->entity->distance($target),
			$this->lookDistance
		);
	}

	public function canContinue() : bool{
		return $this->isValidTarget($this->enemy);
	}

	public function onTick(){
		$target = $this->enemy;
		if(!$target instanceof Entity or !$target->isAlive()){
			return;
		}

		$distance = $this->entity->distance($target);
		$this->lookAt($target);

		// 视线检测节流：每 5 tick 才做一次方块采样
		if($this->canSeeCooldown > 0){
			--$this->canSeeCooldown;
		}else{
			$this->canSeeCooldown = 5;
			$this->canSeeTarget = $this->canSee($target);
		}

		// 太远就靠近，太近就后退，射程内站定
		if($distance > 6.0){
			$this->entity->getNavigator()->moveTo($target, $this->speed);
		}elseif($distance < 4.0){
			$retreat = $this->entity->add(
				($this->entity->x - $target->x) * 2,
				0,
				($this->entity->z - $target->z) * 2
			);
			$this->entity->getNavigator()->moveTo($retreat, $this->speed * 0.7);
		}else{
			$this->entity->getNavigator()->clearPath();
		}

		if($this->shootCooldown > 0){
			--$this->shootCooldown;
		}

		if(self::canShoot($this->canSeeTarget, $distance, $this->shootDistance, $this->shootCooldown)){
			$this->shoot($target);
			$this->shootCooldown = self::SHOOT_COOLDOWN;
		}

		$this->swimming();
	}

	/**
	 * 从眼睛高度朝目标眼睛抛出一个带速度的投掷物。
	 * 与 ShootPlayerBehavior 使用同一套 yaw/pitch 构造，保证 NBT Motion 与 Rotation 一致。
	 */
	private function shoot(Entity $target) : void{
		$entity = $this->entity;
		$pitch = $this->bowAimPitch($target, 0.04);
		// 用头部朝向瞄准：身体朝向由寻路决定，头才是对着目标的那个
		$yaw = $entity->headYaw === null ? $entity->yaw : $entity->headYaw;

		$nbt = new CompoundTag("", [
			"Pos" => new ListTag("Pos", [
				new DoubleTag("", $entity->x),
				new DoubleTag("", $entity->y + $entity->getEyeHeight()),
				new DoubleTag("", $entity->z)
			]),
			"Motion" => new ListTag("Motion", [
				new DoubleTag("", -sin($yaw / 180 * M_PI) * cos($pitch / 180 * M_PI)),
				new DoubleTag("", -sin($pitch / 180 * M_PI)),
				new DoubleTag("", cos($yaw / 180 * M_PI) * cos($pitch / 180 * M_PI))
			]),
			"Rotation" => new ListTag("Rotation", [
				new FloatTag("", $yaw),
				new FloatTag("", $pitch)
			])
		]);

		$projectile = Entity::createEntity($this->projectileClass, $entity->chunk, $nbt, $entity);
		if(!$projectile instanceof Projectile){
			return;
		}

		$projectile->setMotion($projectile->getMotion()->multiply(1.5));
		$projectile->spawnToAll();
	}

	public function onEnd(){
		$this->enemy = null;
		$this->shootCooldown = 0;
		$this->searchCooldown = 0;
		$this->canSeeCooldown = 0;
		$this->canSeeTarget = true;
		$this->entity->getNavigator()->clearPath();
	}
}
