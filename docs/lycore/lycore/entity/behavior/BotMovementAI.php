<?php

namespace lycore\entity\behavior;

use lycore\block\Block;
use lycore\entity\Entity;
use lycore\entity\Bot;
use lycore\math\Vector3;

class BotMovementAI{
	const CHASE_SPEED_MULTIPLIER = 1.35;
	const RANGED_MOVE_MULTIPLIER = 0.45;
	const JUMP_RUN_MIN_DISTANCE = 2.0;
	const DIRECT_MOVE_COST = 10;
	const DIAGONAL_MOVE_COST = 14;
	const MAX_STEP_UP = 1;
	const MAX_DROP_DOWN = 3;

	/** @var Bot */
	private $bot;

	public function __construct(Bot $bot){
		$this->bot = $bot;
	}

	public function getChaseSpeedMultiplier() : float{
		return self::CHASE_SPEED_MULTIPLIER;
	}

	public function shouldJumpWhileChasing(float $distance, bool $inMeleeRange) : bool{
		return !$inMeleeRange and $distance >= self::JUMP_RUN_MIN_DISTANCE;
	}

	public function tick(Entity $target, bool $inMeleeRange, bool $rangedMode = false){
		$distance = $this->bot->distance($target);
		$this->bot->setPVPBotLookTarget($target);
		$isEating = $this->bot->isPVPBotEatingGoldenApple();
		$this->bot->setPVPBotJumpRunEnabled(!$isEating and !$rangedMode and $this->shouldJumpWhileChasing($distance, $inMeleeRange));

		if($inMeleeRange){
			if(!$isEating and $this->bot->tryMinePVPBotObstacleToward($target)){
				$this->bot->setPm1eFollowTarget(null);
				$this->bot->setPm1eMoveTarget(null);
				$this->bot->setPm1eMoveMultiplier(0.0);
				$this->bot->setPm1eStayTime(0);
				return;
			}
			$this->bot->setPm1eFollowTarget(null);
			$this->bot->setPm1eMoveTarget(null);
			$this->bot->setPm1eMoveMultiplier(0.0);
			return;
		}

		$waypoint = $this->bot->constrainBotWalkTarget($this->resolveChaseWaypoint($target));
		if(!$isEating and $this->bot->tryMinePVPBotObstacleToward($waypoint)){
			$this->bot->setPm1eFollowTarget(null);
			$this->bot->setPm1eMoveTarget(null);
			$this->bot->setPm1eMoveMultiplier(0.0);
			$this->bot->setPm1eStayTime(0);
			return;
		}
		$this->bot->tryPlacePVPBotBridgeBlockToward($waypoint);
		$this->bot->setPm1eFollowTarget(null);
		$this->bot->setPm1eMoveTarget($waypoint);
		$this->bot->setPm1eMoveMultiplier($isEating ? Bot::PVPBOT_ENCHANTED_GOLDEN_APPLE_EATING_MOVE_MULTIPLIER : ($rangedMode ? self::RANGED_MOVE_MULTIPLIER : self::CHASE_SPEED_MULTIPLIER));
		$this->bot->setPm1eStayTime(0);
	}

	public function clear(){
		$this->bot->setPVPBotLookTarget(null);
		$this->bot->setPVPBotJumpRunEnabled(false);
		$this->bot->setPm1eFollowTarget(null);
		$this->bot->setPm1eMoveTarget(null);
		$this->bot->setPm1eMoveMultiplier(1.0);
	}

	private function resolveChaseWaypoint(Entity $target) : Vector3{
		$level = $this->bot->getLevel();
		if($level === null){
			return new Vector3($target->x, $target->y, $target->z);
		}

		$route = self::findGridRoute(
			[$this->bot->x, $this->bot->y, $this->bot->z],
			[$target->x, $target->y, $target->z],
			function($x, $y, $z) use ($level){
				return self::resolveStandingY($level, $x, $y, $z);
			},
			260,
			24
		);

		if(!empty($route)){
			$node = $this->selectChaseRouteNode($route);
			return new Vector3($node[0] + 0.5, $node[1], $node[2] + 0.5);
		}

		return new Vector3($target->x, $target->y, $target->z);
	}

	private function selectChaseRouteNode(array $route) : array{
		$stopDistance = ($this->bot->width / 2 + 1);
		$stopDistanceSquared = $stopDistance * $stopDistance;
		foreach($route as $node){
			$dx = ($node[0] + 0.5) - $this->bot->x;
			$dz = ($node[2] + 0.5) - $this->bot->z;
			if(($dx * $dx + $dz * $dz) > $stopDistanceSquared){
				return $node;
			}
		}

		return $route[count($route) - 1];
	}

	public static function findGridRoute(array $start, array $target, callable $resolveY, int $maxNodes = 160, int $range = 16) : array{
		$startNode = [(int) floor($start[0]), (int) floor($start[1]), (int) floor($start[2])];
		$targetNode = [(int) floor($target[0]), (int) floor($target[1]), (int) floor($target[2])];
		$startKey = self::nodeKey($startNode);
		$targetKey = self::nodeKey($targetNode);
		if($startKey === $targetKey){
			return [];
		}

		$open = [
			$startKey => [
				"node" => $startNode,
				"g" => 0,
				"h" => self::estimateRouteCost($startNode, $targetNode),
				"f" => self::estimateRouteCost($startNode, $targetNode)
			]
		];
		$closed = [];
		$bestCost = [$startKey => 0];
		$previous = [];
		$nodes = [$startKey => $startNode];
		$directions = [
			[1, 0, self::DIRECT_MOVE_COST],
			[-1, 0, self::DIRECT_MOVE_COST],
			[0, 1, self::DIRECT_MOVE_COST],
			[0, -1, self::DIRECT_MOVE_COST],
			[1, 1, self::DIAGONAL_MOVE_COST],
			[1, -1, self::DIAGONAL_MOVE_COST],
			[-1, 1, self::DIAGONAL_MOVE_COST],
			[-1, -1, self::DIAGONAL_MOVE_COST],
		];
		$visited = 0;

		while(!empty($open) and $visited < $maxNodes){
			$currentKey = self::findLowestCostOpenNode($open);
			$currentData = $open[$currentKey];
			unset($open[$currentKey]);
			$closed[$currentKey] = true;
			++$visited;
			$current = $currentData["node"];

			if($currentKey === $targetKey){
				break;
			}

			foreach($directions as $direction){
				$x = $current[0] + $direction[0];
				$z = $current[2] + $direction[1];
				if(abs($x - $startNode[0]) > $range or abs($z - $startNode[2]) > $range){
					continue;
				}

				$node = self::resolveRouteNeighbor($resolveY, $current, $direction[0], $direction[1]);
				if($node === null){
					continue;
				}
				if($direction[0] !== 0 and $direction[1] !== 0 and !self::canMoveDiagonally($resolveY, $current, $direction[0], $direction[1], $node)){
					continue;
				}
				$key = self::nodeKey($node);
				if(isset($closed[$key])){
					continue;
				}

				$heightCost = abs($node[1] - $current[1]) * self::DIRECT_MOVE_COST;
				$cost = $currentData["g"] + $direction[2] + $heightCost;
				if(isset($bestCost[$key]) and $cost >= $bestCost[$key]){
					continue;
				}

				$bestCost[$key] = $cost;
				$previous[$key] = $currentKey;
				$nodes[$key] = $node;
				$h = self::estimateRouteCost($node, $targetNode);
				$open[$key] = [
					"node" => $node,
					"g" => $cost,
					"h" => $h,
					"f" => $cost + $h
				];
			}
		}

		$routeEndKey = isset($closed[$targetKey]) ? $targetKey : self::findNearestReachableNodeKey($closed, $nodes, $startKey, $targetNode, $previous);
		if($routeEndKey === null){
			return [];
		}

		return self::reconstructRoute($routeEndKey, $startKey, $previous, $nodes);
	}

	private static function resolveRouteNeighbor(callable $resolveY, array $current, int $dx, int $dz){
		$x = $current[0] + $dx;
		$z = $current[2] + $dz;
		$y = $resolveY($x, $current[1], $z);
		if($y === null or $y === false){
			return null;
		}

		$node = [$x, (int) floor($y), $z];
		return self::canStepBetween($current[1], $node[1]) ? $node : null;
	}

	private static function canStepBetween(int $fromY, int $toY) : bool{
		$dy = $toY - $fromY;

		return $dy <= self::MAX_STEP_UP and $dy >= -self::MAX_DROP_DOWN;
	}

	private static function findNearestReachableNodeKey(array $closed, array $nodes, string $startKey, array $targetNode, array $previous){
		$bestKey = null;
		$bestDistance = null;
		$bestCost = null;
		foreach($closed as $key => $_){
			if($key === $startKey or !isset($nodes[$key]) or !isset($previous[$key])){
				continue;
			}

			$node = $nodes[$key];
			$distance = self::estimateRouteDistanceSquared($node, $targetNode);
			$cost = self::estimateRouteCost($node, $targetNode);
			if($bestKey === null or $distance < $bestDistance or ($distance === $bestDistance and $cost < $bestCost)){
				$bestKey = $key;
				$bestDistance = $distance;
				$bestCost = $cost;
			}
		}

		return $bestKey;
	}

	private static function estimateRouteDistanceSquared(array $from, array $to) : int{
		$dx = $to[0] - $from[0];
		$dy = $to[1] - $from[1];
		$dz = $to[2] - $from[2];

		return ($dx * $dx) + ($dy * $dy) + ($dz * $dz);
	}

	private static function reconstructRoute(string $endKey, string $startKey, array $previous, array $nodes) : array{
		$route = [];
		for($key = $endKey; $key !== $startKey; ){
			if(!isset($nodes[$key]) or !isset($previous[$key])){
				return [];
			}

			array_unshift($route, $nodes[$key]);
			$key = $previous[$key];
		}

		return $route;
	}

	private static function canMoveDiagonally(callable $resolveY, array $current, int $dx, int $dz, array $diagonalNode) : bool{
		if($diagonalNode[1] !== $current[1]){
			return false;
		}

		$sideA = self::resolveRouteNeighbor($resolveY, $current, $dx, 0);
		$sideB = self::resolveRouteNeighbor($resolveY, $current, 0, $dz);

		return $sideA !== null and $sideB !== null;
	}

	private static function findLowestCostOpenNode(array $open) : string{
		$bestKey = null;
		$bestF = null;
		$bestH = null;
		foreach($open as $key => $data){
			if($bestKey === null or $data["f"] < $bestF or ($data["f"] === $bestF and $data["h"] < $bestH)){
				$bestKey = $key;
				$bestF = $data["f"];
				$bestH = $data["h"];
			}
		}

		return $bestKey;
	}

	private static function estimateRouteCost(array $from, array $to) : int{
		$dx = abs($to[0] - $from[0]);
		$dz = abs($to[2] - $from[2]);
		$diagonal = min($dx, $dz);
		$straight = max($dx, $dz) - $diagonal;

		return (int) ($diagonal * self::DIAGONAL_MOVE_COST + $straight * self::DIRECT_MOVE_COST + abs($to[1] - $from[1]) * self::DIRECT_MOVE_COST);
	}

	private static function nodeKey(array $node) : string{
		return $node[0] . ":" . $node[1] . ":" . $node[2];
	}

	private static function resolveStandingY($level, int $x, int $startY, int $z){
		for($y = $startY + self::MAX_STEP_UP; $y >= $startY - self::MAX_DROP_DOWN; --$y){
			if(self::canStandAt($level, $x, $y, $z)){
				return $y;
			}
		}

		return null;
	}

	private static function canStandAt($level, int $x, int $y, int $z) : bool{
		return self::isPassableAt($level, $x, $y - 1, $z) === false and
			self::isPassableAt($level, $x, $y, $z) === true and
			self::isPassableAt($level, $x, $y + 1, $z) === true;
	}

	private static function isPassableAt($level, int $x, int $y, int $z){
		if($y < 0 or $y >= 128){
			return false;
		}

		try{
			if(method_exists($level, "getBlockIdAt")){
				$id = $level->getBlockIdAt($x, $y, $z);
				$data = method_exists($level, "getBlockDataAt") ? $level->getBlockDataAt($x, $y, $z) : 0;
				return Block::get($id, $data)->canPassThrough();
			}
			if(method_exists($level, "getBlock")){
				return $level->getBlock(new Vector3($x, $y, $z))->canPassThrough();
			}
		}catch(\Throwable $e){
			return null;
		}

		return null;
	}
}
