<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Entity;
use pocketmine\entity\Mob;
use pocketmine\event\entity\EntityDamageByEntityEvent;
use pocketmine\math\Vector3;

class PanicBehavior extends StrollBehavior{

	/** @var Entity|null */
	private $attacker = null;

	/** @var int */
	private $retargetTicks = 0;

	public function __construct(Mob $entity, float $speed = 0.55, int $timeout = 100){
		parent::__construct($entity, $speed, $timeout);
	}

	public function getPriority() : int{
		return 1;
	}

	/**
	 * 逃跑同时占用“朝向”。
	 *
	 * 原来的实现只占用 MOVE，LookAtPlayerBehavior 仍然会在旁边把生物的头
	 * 转向玩家，于是生物面朝玩家、身体朝反方向移动，看起来就是倒退着跑。
	 */
	public function getControlFlags() : int{
		return self::CONTROL_MOVE | self::CONTROL_LOOK;
	}

	public function getName() : string{
		return "Panic";
	}

	public function shouldStart() : bool{
		return $this->entity->getLastDamageCause() !== null;
	}

	public function onStart() : void{
		$cause = $this->entity->getLastDamageCause();
		$this->attacker = ($cause instanceof EntityDamageByEntityEvent) ? $cause->getDamager() : null;
		$this->retargetTicks = 0;
		$this->target = $this->pickFleeTarget();
		if($this->target === null){
			$this->target = $this->pickTarget();
		}
		$this->timeLeft = $this->timeout;
	}

	public function onTick(){
		// 原版 panic 会隔一段时间重新挑一个逃跑点，而不是一路直线跑到死。
		if($this->retargetTicks-- <= 0){
			$this->retargetTicks = 20;
			$target = $this->pickFleeTarget();
			if($target !== null){
				$this->target = $target;
			}
		}

		parent::onTick();

		// 面朝逃跑方向；朝向由本行为接管，moveToward 不会再去抢 yaw。
		if($this->target !== null){
			$this->lookAt($this->target, false);
		}
	}

	public function canContinue() : bool{
		if($this->timeLeft-- <= 0){
			return false;
		}
		if($this->attacker !== null and (!$this->attacker->isAlive() or $this->entity->distance($this->attacker) > 24)){
			return false;
		}
		return true;
	}

	public function onEnd(){
		parent::onEnd();
		$this->attacker = null;
		$this->entity->resetLastDamageCause();
	}

	/**
	 * 在生物周围采样一批候选点，交给寻路走，能绕开障碍。
	 */
	protected function pickFleeTarget() : ?Vector3{
		$level = $this->entity->getLevel();
		if($level === null){
			return null;
		}

		$origin = $this->entity->getPosition();
		$baseY = (int) floor($this->entity->y);
		$candidates = [];

		for($i = 0; $i < 12; ++$i){
			$angle = mt_rand(0, 359) * (M_PI / 180);
			$radius = mt_rand(8, 14);
			$x = (int) floor($this->entity->x + cos($angle) * $radius);
			$z = (int) floor($this->entity->z + sin($angle) * $radius);
			$y = $baseY + mt_rand(-3, 3);

			$groundY = Mob::pickGroundY($level, $x, $y, $z);
			if($groundY === null){
				continue;
			}

			$headLayers = max(1, (int) ceil($this->entity->height));
			$blocked = false;
			for($layer = 0; $layer < $headLayers; ++$layer){
				if($level->getBlock(new Vector3($x, $groundY + $layer, $z))->isSolid()){
					$blocked = true;
					break;
				}
			}
			if($blocked){
				continue;
			}

			$groundId = $level->getBlockIdAt($x, $groundY - 1, $z);
			$bodyId = $level->getBlockIdAt($x, $groundY, $z);
			if(in_array($groundId, [8, 9, 10, 11, 51, 81], true) or in_array($bodyId, [8, 9, 10, 11, 51, 81], true)){
				continue;
			}

			$candidates[] = new Vector3($x + 0.5, $groundY, $z + 0.5);
		}

		if(count($candidates) === 0){
			return null;
		}
		if($this->attacker === null){
			return $candidates[mt_rand(0, count($candidates) - 1)];
		}

		return self::pickFleeCandidate($candidates, $origin, $this->attacker->getPosition());
	}

	/**
	 * 从候选点里挑一个“离攻击者最远、距离又不要偏太多”的目标。
	 *
	 * @param Vector3[] $candidates
	 * @param Vector3   $origin
	 * @param Vector3   $attacker
	 * @return Vector3|null
	 */
	public static function pickFleeCandidate(array $candidates, Vector3 $origin, Vector3 $attacker) : ?Vector3{
		$best = null;
		$bestScore = -INF;

		foreach($candidates as $candidate){
			$score = $candidate->distance($attacker) - 0.6 * abs($candidate->distance($origin) - 10.0);
			if($score > $bestScore){
				$bestScore = $score;
				$best = $candidate;
			}
		}

		return $best;
	}
}
