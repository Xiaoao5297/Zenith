<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Entity;
use pocketmine\entity\Mob;
use pocketmine\event\entity\EntityDamageByEntityEvent;
use pocketmine\event\entity\EntityDamageEvent;
use pocketmine\Player;

class AttackEnemyBehavior extends Behavior{

	/** 反击时允许在常规索敌半径外额外锁定的距离 */
	const RETALIATION_RANGE_BONUS = 4;

	/** @var float */
	public $speed;

	/** @var float */
	public $lookDistance = 16.0;

	/** @var int[] */
	public $NetworkID;

	/** @var \pocketmine\entity\Entity|null */
	public $enemy = null;

	/** @var int */
	public $attackCooldown = 0;

	/** @var bool */
	public $attackPlayer = false;

	/** @var int 目标搜索节流，避免每 tick 全量扫描 */
	private $searchCooldown = 0;

	/** @var int */
	private $canSeeCooldown = 0;

	/** @var bool */
	private $canSeeTarget = true;

	/** @var int 丢失视线后的宽限 tick */
	private $lostTicks = 0;

	public function __construct(Mob $entity, array $NetWorkID, bool $attackPlayer = false, float $speed = 0.65, float $speedMultiplier = 0.50){
		parent::__construct($entity);
		$this->speed = $speed * $speedMultiplier;
		$this->NetworkID = $NetWorkID;
		$this->attackPlayer = $attackPlayer;
	}

	public function getPriority() : int{
		return 2;
	}

	public function getControlFlags() : int{
		return self::CONTROL_MOVE | self::CONTROL_LOOK;
	}

	public function getName() : string{
		return "AttackEnemy";
	}

	public function shouldStart() : bool{
		if($this->isValidTarget($this->enemy)){
			return true;
		}
		$this->enemy = null;

		// 反击（Java EntityAIHurtByTarget）：优先锁定最近伤害的来源，跳过搜索冷却。
		$cause = $this->entity->getLastDamageCause();
		if($cause instanceof EntityDamageByEntityEvent){
			$damager = $cause->getDamager();
			if($damager instanceof Entity){
				$isPlayer = $damager instanceof Player;
				$survival = !$isPlayer || $damager->isSurvival();
				$connected = !$isPlayer || $damager->isConnected();
				$networkId = $isPlayer ? 0 : $damager::NETWORK_ID;
				if(self::acceptsRetaliationTarget(
					$damager->isAlive(),
					$damager === $this->entity,
					$isPlayer,
					$survival,
					$connected,
					$networkId,
					$this->NetworkID,
					$this->attackPlayer,
					$this->entity->distance($damager),
					$this->lookDistance + self::RETALIATION_RANGE_BONUS
				)){
					$this->enemy = $damager;
					$this->searchCooldown = 0;
					return true;
				}
			}
		}

		if($this->searchCooldown > 0){
			--$this->searchCooldown;
			return false;
		}
		$this->searchCooldown = 10;
		return $this->findTarget();
	}

	/**
	 * 反击目标是否合法（纯静态，便于离线测试）。
	 *
	 * 规则与 findTarget() 的索敌保持一致：存活、非自己、距离不超过 maxDistance（**含边界**，
	 * 即 distance == maxDistance 仍允许）；玩家要求 attackPlayer && 生存模式 && 在线；
	 * 非玩家要求 NETWORK_ID 在列表内（严格比较）。
	 *
	 * @param int[] $networkIds
	 */
	public static function acceptsRetaliationTarget(bool $alive, bool $self, bool $isPlayer, bool $survival, bool $connected, int $networkId, array $networkIds, bool $attackPlayer, float $distance, float $maxDistance) : bool{
		if(!$alive or $self){
			return false;
		}
		if($distance > $maxDistance){
			return false;
		}
		if($isPlayer){
			return $attackPlayer && $survival && $connected;
		}
		return in_array($networkId, $networkIds, true);
	}

	private function findTarget() : bool{
		$best = null;
		$bestDistance = $this->lookDistance;
		$bb = $this->entity->boundingBox->grow($this->lookDistance, 4, $this->lookDistance);

		foreach($this->entity->level->getNearbyEntities($bb, $this->entity) as $entity){
			if(!$entity->isAlive() or $entity === $this->entity){
				continue;
			}
			$distance = $this->entity->distance($entity);
			if($distance > $bestDistance){
				continue;
			}

			if($entity instanceof Player){
				if(!$this->attackPlayer or !$entity->isSurvival() or !$entity->isConnected()){
					continue;
				}
			}elseif(!in_array($entity::NETWORK_ID, $this->NetworkID, true)){
				continue;
			}

			$best = $entity;
			$bestDistance = $distance;
		}

		$this->enemy = $best;
		return $best !== null;
	}

	private function isValidTarget($target) : bool{
		if($target === null or !$target->isAlive()){
			return false;
		}
		if($target instanceof Player and (!$target->isConnected() or !$target->isSurvival())){
			return false;
		}
		return $this->entity->distance($target) <= $this->lookDistance + 4;
	}

	public function canContinue() : bool{
		if(!$this->isValidTarget($this->enemy)){
			return false;
		}
		return $this->lostTicks < 60 or $this->entity->distance($this->enemy) <= 4.0;
	}

	public function onTick(){
		if($this->enemy === null){
			return;
		}

		$distance = $this->entity->distance($this->enemy);
		$this->lookAt($this->enemy);

		if($this->canSeeCooldown > 0){
			--$this->canSeeCooldown;
		}else{
			$this->canSeeCooldown = 5;
			$this->canSeeTarget = $this->canSee($this->enemy);
			if($this->canSeeTarget){
				$this->lostTicks = 0;
			}
		}
		if(!$this->canSeeTarget){
			++$this->lostTicks;
		}

		$attackReach = max(1.6, $this->entity->width + 1.2);
		if($distance > $attackReach){
			if($this->canSeeTarget or $distance <= 6.0){
				$this->entity->getNavigator()->moveTo($this->enemy, $this->speed);
			}else{
				$this->entity->getNavigator()->clearPath();
			}
		}else{
			$this->entity->getNavigator()->clearPath();
		}

		if($distance <= $attackReach + 0.4 and $this->attackCooldown <= 0 and $this->canSeeTarget){
			$damage = method_exists($this->entity, 'getHurt') ? $this->entity->getHurt() : 3;
			$ev = new EntityDamageByEntityEvent($this->entity, $this->enemy, EntityDamageEvent::CAUSE_ENTITY_ATTACK, $damage);
			$this->enemy->attack($damage, $ev);
			if(method_exists($this->entity, 'onSuccessfulMeleeAttack')){
				$this->entity->onSuccessfulMeleeAttack($this->enemy, $ev);
			}
			$this->attackCooldown = $distance <= 3.0 ? mt_rand(10, 15) : ($distance <= 6.0 ? mt_rand(15, 20) : mt_rand(20, 30));
		}

		if($this->attackCooldown > 0){
			--$this->attackCooldown;
		}
		$this->swimming();
	}

	public function onEnd(){
		$this->enemy = null;
		$this->attackCooldown = 0;
		$this->searchCooldown = 0;
		$this->lostTicks = 0;
		$this->entity->getNavigator()->clearPath();
	}
}
