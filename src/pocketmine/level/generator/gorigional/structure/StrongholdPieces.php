<?php

/***
 *  _____                _  __   __
 * /__  /  ___   ____   (_)/ /_ / /_
 *   / /  / _ \ / __ \ / // __// __ \
 *  / /__/  __// / / // // /_ / / / /
 * /____/\___//_/ /_//_/ \__//_/ /_/
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/


namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 要塞部件工厂和权重表。
 *
 * 权重取自 Java 1.7.10 StructureStrongholdPieces；MCPE 0.14.3 的
 * StrongholdStart 使用同一组权重（名称略有不同）。
 */
class StrongholdPieces{
	/** @var array<string,array{class:string,weight:int,limit:int,count:int,minType?:int}> */
	private static $weights = [];
	/** @var BoundingBox|null */
	private static $startBox = null;
	/** @var StrongholdStairs2|null */
	private static $startPiece = null;
	/** @var string|null Java field_75203_d：下一次生成强制使用的部件类 */
	private static $forcedPiece = null;

	public static function reset(){
		self::$startBox = null;
		self::$startPiece = null;
		self::$forcedPiece = null;
		self::$weights = [
			StrongholdStraight::class => ['class' => StrongholdStraight::class, 'weight' => 40, 'limit' => 0, 'count' => 0],
			StrongholdPrison::class => ['class' => StrongholdPrison::class, 'weight' => 5, 'limit' => 5, 'count' => 0],
			StrongholdLeftTurn::class => ['class' => StrongholdLeftTurn::class, 'weight' => 20, 'limit' => 0, 'count' => 0],
			StrongholdRightTurn::class => ['class' => StrongholdRightTurn::class, 'weight' => 20, 'limit' => 0, 'count' => 0],
			StrongholdRoomCrossing::class => ['class' => StrongholdRoomCrossing::class, 'weight' => 10, 'limit' => 6, 'count' => 0],
			StrongholdStairsStraight::class => ['class' => StrongholdStairsStraight::class, 'weight' => 5, 'limit' => 5, 'count' => 0],
			StrongholdStairs::class => ['class' => StrongholdStairs::class, 'weight' => 5, 'limit' => 5, 'count' => 0],
			StrongholdCrossing::class => ['class' => StrongholdCrossing::class, 'weight' => 5, 'limit' => 4, 'count' => 0],
			StrongholdChestCorridor::class => ['class' => StrongholdChestCorridor::class, 'weight' => 5, 'limit' => 4, 'count' => 0],
			// Java StructureStrongholdPieces$1：func_75189_a 额外要求 type > 4。
			StrongholdLibrary::class => ['class' => StrongholdLibrary::class, 'weight' => 10, 'limit' => 2, 'count' => 0, 'minType' => 4],
			// Java StructureStrongholdPieces$2：func_75189_a 额外要求 type > 5。
			StrongholdPortalRoom::class => ['class' => StrongholdPortalRoom::class, 'weight' => 20, 'limit' => 1, 'count' => 0, 'minType' => 5],
		];
	}

	public static function setStartBox(BoundingBox $box, StrongholdStairs2 $startPiece = null){
		self::$startBox = $box;
		if($startPiece !== null){
			self::$startPiece = $startPiece;
		}
	}

	/**
	 * Java StructureStrongholdPieces.field_75203_d：起始楼梯强制下一段为 Crossing。
	 */
	public static function setForcedPiece($class){
		self::$forcedPiece = $class;
	}

	/**
	 * 对应 Java StructureStrongholdPieces.func_75196_c 的硬限制。
	 */
	public static function canCreateAt($type, $x, $z){
		// Java 在 func_75196_c 用父部件 type 判断 >50，再以 type+1 创建子部件。
		// 这里收到的是已 +1 的子部件 type，因此阈值是 51。
		if($type > 51 || self::$startBox === null){
			return false;
		}
		return abs($x - self::$startBox->minX) <= 112 && abs($z - self::$startBox->minZ) <= 112;
	}

	/**
	 * Java func_75202_c：只要还有未达到上限的有限部件就继续生成。
	 * 全部有限部件用尽后，即使还有无限权重部件也不再生成。
	 */
	private static function hasAvailableLimited(){
		foreach(self::$weights as $w){
			if($w['limit'] > 0 && $w['count'] < $w['limit']){
				return true;
			}
		}
		return false;
	}

	private static function isValidType(array $w, $type){
		return !isset($w['minType']) || $type > $w['minType'];
	}

	private static function totalWeight(){
		$total = 0;
		foreach(self::$weights as $w){
			$total += $w['weight'];
		}
		return $total;
	}

	public static function createRandom(array $components, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		if(!self::canCreateAt($type, $x, $z) || !self::hasAvailableLimited()){
			return null;
		}

		if(self::$forcedPiece !== null){
			$piece = self::createClass(self::$forcedPiece, $components, $rnd, $x, $y, $z, $facing, $type);
			self::$forcedPiece = null;
			if($piece !== null){
				return $piece;
			}
		}

		$total = self::totalWeight();
		if($total <= 0){
			return null;
		}

		for($attempt = 0; $attempt < 5; $attempt++){
			$pick = $rnd->nextBoundedInt($total);
			$chosen = null;
			foreach(self::$weights as $key => $w){
				$pick -= $w['weight'];
				if($pick < 0){
					// Java：类型不满足或与上一个部件相同则本次放弃，重新抽。
					if(!self::isValidType($w, $type) || (self::$startPiece !== null && self::$startPiece->lastPiece === $key)){
						break;
					}
					$chosen = $key;
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
				if(self::$startPiece !== null){
					self::$startPiece->lastPiece = $chosen;
				}
				if(self::$weights[$chosen]['limit'] > 0 && self::$weights[$chosen]['count'] >= self::$weights[$chosen]['limit']){
					unset(self::$weights[$chosen]);
				}
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
