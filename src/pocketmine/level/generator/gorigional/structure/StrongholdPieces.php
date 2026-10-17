<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 要塞部件工厂和权重表。
 *
 * 权重取自 Java 1.7.10 StructureStrongholdPieces；MCPE 0.14.3 的
 * StrongholdStart 使用同一组权重（名称略有不同）。
 */
class StrongholdPieces{
	/** @var array<int,array{class:string,weight:int,limit:int,count:int}> */
	private static $weights = [];

	public static function reset(){
		self::$weights = [
			['class' => StrongholdStraight::class, 'weight' => 40, 'limit' => 0, 'count' => 0],
			['class' => StrongholdPrison::class, 'weight' => 5, 'limit' => 5, 'count' => 0],
			['class' => StrongholdLeftTurn::class, 'weight' => 20, 'limit' => 0, 'count' => 0],
			['class' => StrongholdRightTurn::class, 'weight' => 20, 'limit' => 0, 'count' => 0],
			['class' => StrongholdRoomCrossing::class, 'weight' => 10, 'limit' => 6, 'count' => 0],
			['class' => StrongholdStairsStraight::class, 'weight' => 5, 'limit' => 5, 'count' => 0],
			['class' => StrongholdStairs::class, 'weight' => 5, 'limit' => 5, 'count' => 0],
			['class' => StrongholdCrossing::class, 'weight' => 5, 'limit' => 4, 'count' => 0],
			['class' => StrongholdChestCorridor::class, 'weight' => 5, 'limit' => 4, 'count' => 0],
			['class' => StrongholdLibrary::class, 'weight' => 10, 'limit' => 2, 'count' => 0],
			['class' => StrongholdPortalRoom::class, 'weight' => 20, 'limit' => 1, 'count' => 0],
		];
	}

	private static function available($type){
		$list = [];
		$total = 0;
		foreach(self::$weights as $i => $w){
			if($type > 50 && $w['class'] === StrongholdPortalRoom::class){
				continue;
			}
			if($w['limit'] > 0 && $w['count'] >= $w['limit']){
				continue;
			}
			$list[] = $i;
			$total += $w['weight'];
		}
		return [$list, $total];
	}

	public static function createRandom(array $components, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		if($type > 50){
			return StrongholdCorridor::create($components, $rnd, $x, $y, $z, $facing, $type);
		}

		for($attempt = 0; $attempt < 5; $attempt++){
			list($available, $total) = self::available($type);
			if($total <= 0){
				break;
			}
			$pick = $rnd->nextBoundedInt($total);
			$chosen = null;
			foreach($available as $index){
				$pick -= self::$weights[$index]['weight'];
				if($pick < 0){
					$chosen = $index;
					break;
				}
			}
			if($chosen === null){
				continue;
			}

			$class = self::$weights[$chosen]['class'];
			$piece = self::createClass($class, $components, $rnd, $x, $y, $z, $facing, $type);
			if($piece !== null){
				self::$weights[$chosen]['count']++;
				return $piece;
			}
		}

		// Java 的兜底：造一段短走廊，避免结构在这里断掉。
		return StrongholdCorridor::create($components, $rnd, $x, $y, $z, $facing, $type);
	}

	public static function createClass($class, array $components, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		switch($class){
			case StrongholdStraight::class:
				return StrongholdStraight::create($components, $rnd, $x, $y, $z, $facing, $type);
			case StrongholdPrison::class:
				return StrongholdPrison::create($components, $rnd, $x, $y, $z, $facing, $type);
			case StrongholdLeftTurn::class:
				return StrongholdLeftTurn::create($components, $rnd, $x, $y, $z, $facing, $type);
			case StrongholdRightTurn::class:
				return StrongholdRightTurn::create($components, $rnd, $x, $y, $z, $facing, $type);
			case StrongholdRoomCrossing::class:
				return StrongholdRoomCrossing::create($components, $rnd, $x, $y, $z, $facing, $type);
			case StrongholdStairsStraight::class:
				return StrongholdStairsStraight::create($components, $rnd, $x, $y, $z, $facing, $type);
			case StrongholdStairs::class:
				return StrongholdStairs::create($components, $rnd, $x, $y, $z, $facing, $type);
			case StrongholdCrossing::class:
				return StrongholdCrossing::create($components, $rnd, $x, $y, $z, $facing, $type);
			case StrongholdChestCorridor::class:
				return StrongholdChestCorridor::create($components, $rnd, $x, $y, $z, $facing, $type);
			case StrongholdLibrary::class:
				return StrongholdLibrary::create($components, $rnd, $x, $y, $z, $facing, $type);
			case StrongholdPortalRoom::class:
				return StrongholdPortalRoom::create($components, $rnd, $x, $y, $z, $facing, $type);
		}
		return null;
	}
}
