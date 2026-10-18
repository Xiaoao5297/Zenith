<?php

namespace pocketmine\entity\ai\navigation;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;
use pocketmine\Server;

class PathNavigate{

	private const STATE_IDLE = 0;
	private const STATE_PATH_FOLLOWING = 1;
	private const STATE_FALLBACK = 2;

	/** @var Mob */
	private $entity;

	/** @var NodeEvaluator */
	private $evaluator;

	/** @var Path|null */
	private $path;

	/** @var Vector3|null */
	private $target;

	/** @var float */
	private $speed = 0.35;

	/** @var int */
	private $state = self::STATE_IDLE;

	/** @var int */
	private $retryCount = 0;

	/** @var int */
	private $blockedTicks = 0;

	/** @var int */
	private $recalcCooldown = 0;

	/** @var int */
	private $tickCounter = 0;

	/** @var Vector3|null 上一次进度检查的位置 */
	private $lastProgressPos;

	/** @var int */
	private $lastProgressTick = 0;

	/** @var int */
	private $horizontalRange;

	/** @var int */
	private $verticalRange;

	/** @var int */
	private $maxIterations;

	/** @var float */
	private $waterCost;

	public function __construct(Mob $entity){
		$this->entity = $entity;
		$this->evaluator = new NodeEvaluator($entity->getLevel());

		$config = Server::getInstance()->aiConfig;
		$this->horizontalRange = (int) ($config["pathfinding-range"] ?? 32);
		$this->verticalRange = (int) ($config["pathfinding-vertical-range"] ?? 8);
		$this->maxIterations = (int) ($config["pathfinding-max-iterations"] ?? 800);
		$this->waterCost = (float) ($config["pathfinding-water-cost"] ?? 2.0);
	}

	/**
	 * 设置寻路目标，由 Behavior 调用。
	 * 目标没有明显移动时不会每 tick 重算路径。
	 */
	public function moveTo(Vector3 $target, float $speed) : void{
		if(!$this->entity->isBehaviorsEnabled()){
			return;
		}

		$this->target = $target;
		$this->speed = $speed;

		if($this->state === self::STATE_FALLBACK){
			return;
		}

		if($this->state === self::STATE_PATH_FOLLOWING){
			if($this->path !== null and !$this->path->isDone()){
				$end = $this->path->getEndPoint();
				if($end !== null and $end->distance($target) <= 1.5){
					return;
				}
				if($this->recalcCooldown > 0){
					--$this->recalcCooldown;
					return;
				}
				$this->recalculatePath();
				$this->recalcCooldown = 10;
				return;
			}

			if($this->entity->distance($target) < 1.0){
				$this->clearPath();
				return;
			}
			if($this->recalcCooldown > 0){
				--$this->recalcCooldown;
				return;
			}
			$this->recalculatePath();
			$this->recalcCooldown = 10;
			return;
		}

		$this->state = self::STATE_PATH_FOLLOWING;
		$this->retryCount = 0;
		$this->recalculatePath();
	}

	/**
	 * 由 Mob::onUpdate() 每 tick 调用
	 */
	public function update() : void{
		if($this->state === self::STATE_IDLE){
			return;
		}

		++$this->tickCounter;

		if($this->state === self::STATE_PATH_FOLLOWING){
			$this->updatePathFollowing();
		}elseif($this->state === self::STATE_FALLBACK){
			$this->updateFallback();
		}
	}

	private function updatePathFollowing() : void{
		if($this->path === null or $this->path->isDone()){
			if($this->target !== null and $this->entity->distance($this->target) > 1.0){
				$this->recalculatePath();
			}else{
				$this->clearPath();
			}
			return;
		}

		$pos = $this->entity->getPosition();
		$this->checkStuck($pos);
		if($this->state !== self::STATE_PATH_FOLLOWING){
			return;
		}

		$waypoint = $this->path->getCurrentPoint();
		if($waypoint === null){
			$this->clearPath();
			return;
		}

		// 按实体宽度推进路点，而不是固定 0.8
		$advanceDistance = max(0.6, $this->entity->width);
		$advanced = false;
		while(($waypoint = $this->path->getCurrentPoint()) !== null){
			$horizontal = sqrt(($waypoint->x - $pos->x) ** 2 + ($waypoint->z - $pos->z) ** 2);
			$vertical = abs($waypoint->y - $pos->y);
			if($horizontal >= $advanceDistance or $vertical > 1.5){
				break;
			}
			$this->path->advance();
			$advanced = true;
		}

		if($this->path->isDone()){
			if($this->target !== null and $this->entity->distance($this->target) > 1.0){
				$this->recalculatePath();
			}else{
				$this->clearPath();
			}
			return;
		}

		if($advanced){
			$this->tryShortcut();
		}

		$waypoint = $this->path->getCurrentPoint();
		if($waypoint === null){
			$this->clearPath();
			return;
		}

		$this->moveToward($waypoint);
	}

	private function updateFallback() : void{
		if($this->target === null){
			$this->clearPath();
			return;
		}

		$pos = $this->entity->getPosition();
		$dx = $this->target->x - $pos->x;
		$dz = $this->target->z - $pos->z;
		$len = sqrt($dx * $dx + $dz * $dz);

		if($len < 0.8){
			$this->clearPath();
			return;
		}

		$dx /= $len;
		$dz /= $len;

		if(!$this->entity->isLookControlled()){
			$this->turnToward(-atan2($dx, $dz) * (180 / M_PI));
		}

		$mult = $this->entity->isInsideOfWater() ? 0.30 : 0.60;
		$step = $this->speed * $mult;
		$dir = new Vector3($dx, 0, $dz);

		if(!$this->entity->moveInDirection($dir, $step)){
			++$this->blockedTicks;
			if($this->blockedTicks >= 8){
				$this->clearPath();
			}
		}else{
			$this->blockedTicks = 0;
		}
	}

	private function moveToward(Vector3 $waypoint) : void{
		$pos = $this->entity->getPosition();
		$dx = $waypoint->x - $pos->x;
		$dy = $waypoint->y - $pos->y;
		$dz = $waypoint->z - $pos->z;

		$horizontalDist = sqrt($dx * $dx + $dz * $dz);
		if($horizontalDist <= 0.001){
			return;
		}

		// 平滑转向，每 tick 最多 30 度；有 LOOK 行为时让 lookAt 控制朝向
		if(!$this->entity->isLookControlled()){
			$this->turnToward(-atan2($dx, $dz) * (180 / M_PI));
		}

		$dirX = $dx / $horizontalDist;
		$dirZ = $dz / $horizontalDist;

		$mult = $this->entity->isInsideOfWater() ? 0.30 : 0.60;
		$step = $this->speed * $mult;

		$dir = new Vector3($dirX, 0, $dirZ);
		if(!$this->entity->moveInDirection($dir, $step)){
			++$this->blockedTicks;
			if($this->blockedTicks >= 10){
				$this->blockedTicks = 0;
				$this->recalculatePath();
			}
		}else{
			$this->blockedTicks = 0;
		}
	}

	private function turnToward(float $targetYaw) : void{
		$delta = $targetYaw - $this->entity->yaw;
		while($delta > 180){
			$delta -= 360;
		}
		while($delta < -180){
			$delta += 360;
		}
		if($delta > 30){
			$delta = 30;
		}elseif($delta < -30){
			$delta = -30;
		}
		$this->entity->yaw = $this->normalizeAngle($this->entity->yaw + $delta);
	}

	private function normalizeAngle(float $angle) : float{
		while($angle > 180){
			$angle -= 360;
		}
		while($angle < -180){
			$angle += 360;
		}
		return $angle;
	}

	/**
	 * 每 100 tick 检查一次是否真的在前进。
	 * 比“5 tick 位移小于 0.1 就重算”稳定得多。
	 */
	private function checkStuck(Vector3 $pos) : void{
		if($this->lastProgressPos === null){
			$this->lastProgressPos = new Vector3($pos->x, $pos->y, $pos->z);
			$this->lastProgressTick = $this->tickCounter;
			return;
		}

		if($this->tickCounter - $this->lastProgressTick < 100){
			return;
		}

		if($pos->distance($this->lastProgressPos) < 1.5){
			$this->lastProgressPos = new Vector3($pos->x, $pos->y, $pos->z);
			$this->lastProgressTick = $this->tickCounter;
			$this->recalculatePath();
			return;
		}

		$this->lastProgressPos = new Vector3($pos->x, $pos->y, $pos->z);
		$this->lastProgressTick = $this->tickCounter;
	}

	/**
	 * 从路径末端往回找最远的可视路点，直接跳过去，避免贴着 A* 折线走。
	 */
	private function tryShortcut() : void{
		if($this->path === null){
			return;
		}
		$points = $this->path->getPoints();
		$count = count($points);
		$index = $this->path->getCurrentIndex();
		if($count < 3 or $index >= $count - 1){
			return;
		}

		$from = $points[$index];
		for($i = $count - 1; $i > $index + 1; --$i){
			if($this->canMoveDirectly($from, $points[$i])){
				$this->path->setCurrentIndex($i);
				return;
			}
		}
	}

	private function canMoveDirectly(Vector3 $from, Vector3 $to) : bool{
		$dx = $to->x - $from->x;
		$dy = $to->y - $from->y;
		$dz = $to->z - $from->z;
		$horizontal = sqrt($dx * $dx + $dz * $dz);
		if($horizontal < 0.001){
			return true;
		}

		$steps = max(1, (int) ceil($horizontal / 0.5));
		$height = $this->entity->height;

		for($i = 1; $i <= $steps; ++$i){
			$t = $i / $steps;
			$x = (int) floor($from->x + $dx * $t);
			$y = (int) floor($from->y + $dy * $t);
			$z = (int) floor($from->z + $dz * $t);

			if(!$this->evaluator->isWalkable($x, $y, $z, $height) and !$this->evaluator->isWalkable($x, $y + 1, $z, $height)){
				return false;
			}
		}

		return true;
	}

	/**
	 * A* 寻路计算
	 */
	private function recalculatePath() : void{
		if($this->target === null){
			$this->fallbackToStraight();
			return;
		}

		++$this->retryCount;
		$this->evaluator->clearCache();

		$from = $this->entity->getPosition()->floor();
		$to = $this->target->floor();
		$entityHeight = $this->entity->height;

		if($this->posKey($from) === $this->posKey($to) or $this->entity->distance($this->target) < 1.0){
			$this->clearPath();
			return;
		}

		$result = $this->findPath($from, $to, $entityHeight);

		if($result !== null and $result->getPointCount() > 0){
			if($result->isPartial() and $this->retryCount >= 2){
				$this->fallbackToStraight();
				return;
			}

			$this->path = $result;
			$this->state = self::STATE_PATH_FOLLOWING;
			$this->blockedTicks = 0;
			if(!$result->isPartial()){
				$this->retryCount = 0;
			}
			$this->lastProgressPos = new Vector3($this->entity->x, $this->entity->y, $this->entity->z);
			$this->lastProgressTick = $this->tickCounter;
			$this->tryShortcut();
			return;
		}

		if($this->retryCount < 2){
			return;
		}

		$this->fallbackToStraight();
	}

	private function fallbackToStraight() : void{
		$this->path = null;
		$this->state = self::STATE_FALLBACK;
		$this->blockedTicks = 0;
		$this->retryCount = 0;
	}

	public function clearPath() : void{
		$this->path = null;
		$this->target = null;
		$this->state = self::STATE_IDLE;
		$this->blockedTicks = 0;
		$this->retryCount = 0;
		$this->recalcCooldown = 0;
		$this->lastProgressPos = null;
		$this->lastProgressTick = 0;
	}

	public function hasPath() : bool{
		return $this->path !== null and !$this->path->isDone();
	}

	public function isDone() : bool{
		if($this->state === self::STATE_IDLE){
			return true;
		}
		// FALLBACK 状态没有 Path，但仍在直线移动，不能算完成
		if($this->state === self::STATE_FALLBACK){
			return false;
		}
		return $this->path === null or $this->path->isDone();
	}

	public function getTarget() : ?Vector3{
		return $this->target;
	}

	/**
	 * A* 搜索算法
	 * @return Path|null
	 */
	private function findPath(Vector3 $from, Vector3 $to, float $entityHeight) : ?Path{
		$startKey = $this->posKey($from);
		$goalKey = $this->posKey($to);

		if($startKey === $goalKey){
			return null;
		}

		if(!$this->evaluator->isWalkable((int) $to->x, (int) $to->y, (int) $to->z, $entityHeight)){
			return null;
		}

		$open = new \SplPriorityQueue();
		$open->setExtractFlags(\SplPriorityQueue::EXTR_BOTH);

		$gScore = [$startKey => 0.0];
		$cameFrom = [];
		$closed = [];

		$startH = $this->heuristic($from, $to);
		$open->insert($startKey, -$startH);

		$closestKey = $startKey;
		$closestH = $startH;
		$iterations = 0;

		while(!$open->isEmpty()){
			if(++$iterations > $this->maxIterations){
				break;
			}

			$item = $open->extract();
			$currentKey = $item["data"];
			if(isset($closed[$currentKey])){
				continue;
			}
			$closed[$currentKey] = true;

			[$cx, $cy, $cz] = $this->keyPos($currentKey);
			$currentH = $this->heuristic(new Vector3($cx, $cy, $cz), $to);
			if($currentH < $closestH){
				$closestH = $currentH;
				$closestKey = $currentKey;
			}

			if($currentKey === $goalKey){
				return $this->reconstructPath($cameFrom, $currentKey, false);
			}

			foreach($this->getNeighbors($cx, $cy, $cz, $entityHeight) as $neighbor){
				[$nx, $ny, $nz] = $neighbor;

				if(abs($nx - (int) $from->x) > $this->horizontalRange or
				   abs($nz - (int) $from->z) > $this->horizontalRange or
				   abs($ny - (int) $from->y) > $this->verticalRange){
					continue;
				}

				$neighborKey = $this->posKeyFromInt($nx, $ny, $nz);
				if(isset($closed[$neighborKey])){
					continue;
				}

				$moveCost = $ny > $cy ? 1.5 : ($ny < $cy ? 1.2 : 1.0);
				$blockCost = $this->evaluator->getCost($nx, $ny, $nz, $this->waterCost);
				$tentativeG = $gScore[$currentKey] + $moveCost * $blockCost;

				if(!isset($gScore[$neighborKey]) or $tentativeG < $gScore[$neighborKey]){
					$cameFrom[$neighborKey] = $currentKey;
					$gScore[$neighborKey] = $tentativeG;
					$f = $tentativeG + $this->heuristic(new Vector3($nx, $ny, $nz), $to);
					$open->insert($neighborKey, -$f);
				}
			}
		}

		// 找不到完整路径时，返回通往“离目标最近节点”的部分路径
		if($closestKey !== $startKey){
			return $this->reconstructPath($cameFrom, $closestKey, true);
		}

		return null;
	}

	/**
	 * 获取邻居节点（4 方向 + 上下一格）
	 */
	private function getNeighbors(int $x, int $y, int $z, float $entityHeight) : array{
		$neighbors = [];
		$dirs = [[1, 0], [-1, 0], [0, 1], [0, -1]];

		foreach($dirs as [$dx, $dz]){
			$nx = $x + $dx;
			$nz = $z + $dz;

			if($this->evaluator->isWalkable($nx, $y, $nz, $entityHeight)){
				$neighbors[] = [$nx, $y, $nz];
			}
			if($this->evaluator->isWalkable($nx, $y + 1, $nz, $entityHeight)){
				$neighbors[] = [$nx, $y + 1, $nz];
			}
			if($this->evaluator->isWalkable($nx, $y - 1, $nz, $entityHeight)){
				$neighbors[] = [$nx, $y - 1, $nz];
			}
		}

		return $neighbors;
	}

	/**
	 * 启发式距离（octile + 垂直）
	 */
	private function heuristic(Vector3 $a, Vector3 $b) : float{
		$dx = abs($a->x - $b->x);
		$dz = abs($a->z - $b->z);
		$dy = abs($a->y - $b->y);
		return max($dx, $dz) + 0.5 * min($dx, $dz) + $dy;
	}

	/**
	 * 从 cameFrom 重建路径
	 */
	private function reconstructPath(array $cameFrom, string $currentKey, bool $partial = false) : Path{
		$points = [];
		$key = $currentKey;

		while(isset($cameFrom[$key])){
			[$x, $y, $z] = $this->keyPos($key);
			array_unshift($points, new Vector3($x + 0.5, $y, $z + 0.5));
			$key = $cameFrom[$key];
		}

		// cameFrom 链在起点处结束，循环不会把起点加入 points，
		// 因此这里不需要再移除第一个路点。
		return new Path($points, $partial);
	}

	private function posKey(Vector3 $pos) : string{
		return ((int) floor($pos->x)) . ',' . ((int) floor($pos->y)) . ',' . ((int) floor($pos->z));
	}

	private function posKeyFromInt(int $x, int $y, int $z) : string{
		return $x . ',' . $y . ',' . $z;
	}

	private function keyPos(string $key) : array{
		$parts = explode(',', $key);
		return [(int) $parts[0], (int) $parts[1], (int) $parts[2]];
	}
}
