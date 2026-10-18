<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;

class StrollBehavior extends Behavior{

	/** @var float */
	public $speed;

	/** @var int */
	public $timeout;

	/** @var int */
	public $timeLeft;

	/** @var Vector3|null */
	public $target = null;

	public function __construct(Mob $entity, float $speed = 0.40, int $timeout = 120){
		parent::__construct($entity);
		$this->speed = $speed;
		$this->timeout = $timeout;
		$this->timeLeft = $timeout;
	}

	public function getPriority() : int{
		return 7;
	}

	public function getControlFlags() : int{
		return self::CONTROL_MOVE;
	}

	public function getName() : string{
		return "Stroll";
	}

	public function shouldStart() : bool{
		return mt_rand(0, 120) === 0;
	}

	public function onStart() : void{
		$this->stuckTicks = 0;
		$this->target = $this->pickTarget();
		$this->timeLeft = $this->timeout;
	}

	/**
	 * 在附近采样多个点并打分，而不是完全随机选一个点。
	 * 这样可以明显减少“往水里、悬崖下、方块里走”的情况。
	 */
	protected function pickTarget() : ?Vector3{
		$level = $this->entity->getLevel();
		if($level === null){
			return null;
		}

		$baseY = (int) floor($this->entity->y);
		$best = null;
		$bestScore = -INF;

		for($i = 0; $i < 10; ++$i){
			$x = (int) floor($this->entity->x) + mt_rand(-10, 10);
			$z = (int) floor($this->entity->z) + mt_rand(-10, 10);
			$y = $baseY + mt_rand(-3, 3);

			$groundY = Mob::pickGroundY($level, $x, $y, $z);
			if($groundY === null){
				continue;
			}

			// 身体和头部空间
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

			$distance = sqrt(($x - $this->entity->x) ** 2 + ($z - $this->entity->z) ** 2);
			if($distance < 2.0){
				continue;
			}

			// 偏好 4~8 格远的目标
			$score = -abs($distance - 6.0) + mt_rand(0, 20) / 10.0;
			if($score > $bestScore){
				$bestScore = $score;
				$best = new Vector3($x + 0.5, $groundY, $z + 0.5);
			}
		}

		return $best;
	}

	public function onTick(){
		if($this->target !== null){
			$this->entity->getNavigator()->moveTo($this->target, $this->speed);
		}
		$this->swimming();
	}

	public function canContinue() : bool{
		if($this->target === null){
			return false;
		}
		if($this->timeLeft-- <= 0){
			return false;
		}
		if($this->entity->distance($this->target) < 1.0){
			return false;
		}
		return !$this->entity->getNavigator()->isDone();
	}

	public function onEnd(){
		$this->target = null;
		$this->timeLeft = $this->timeout;
		$this->entity->getNavigator()->clearPath();
	}
}
