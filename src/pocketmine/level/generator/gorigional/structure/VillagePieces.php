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
 * it under the terms of the GNU Lesser General Public License as published by
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
 * 移植自 Java 1.7.10 StructureVillagePieces。
 */
final class VillagePieces{
	const ALLOWED_BIOMES = [1, 2, 35];

	/**
	 * @return VillagePieceWeight[]
	 */
	public static function getWeightedPieceList(JavaRandom $rnd, $size){
		$list = [];
		$list[] = new VillagePieceWeight('house4garden', 4, $rnd->nextRange(2 + $size, 4 + $size * 2));
		$list[] = new VillagePieceWeight('church', 20, $rnd->nextRange(0 + $size, 1 + $size));
		$list[] = new VillagePieceWeight('house1', 20, $rnd->nextRange(0 + $size, 2 + $size));
		$list[] = new VillagePieceWeight('woodhut', 3, $rnd->nextRange(2 + $size, 5 + $size * 3));
		$list[] = new VillagePieceWeight('hall', 15, $rnd->nextRange(0 + $size, 2 + $size));
		$list[] = new VillagePieceWeight('field1', 3, $rnd->nextRange(1 + $size, 4 + $size));
		$list[] = new VillagePieceWeight('field2', 3, $rnd->nextRange(2 + $size, 4 + $size * 2));
		$list[] = new VillagePieceWeight('house2', 15, $rnd->nextRange(0, 1 + $size));
		$list[] = new VillagePieceWeight('house3', 8, $rnd->nextRange(0 + $size, 3 + $size * 2));

		foreach($list as $k => $w){
			if($w->limit === 0){
				unset($list[$k]);
			}
		}

		return array_values($list);
	}

	private static function getTotalWeight(array $list){
		$has = false;
		$total = 0;

		foreach($list as $w){
			if($w->limit > 0 && $w->instances < $w->limit){
				$has = true;
			}
			$total += $w->weight;
		}

		return $has ? $total : -1;
	}

	private static function createVillagePiece(VillageWellStart $start, VillagePieceWeight $weight, array &$list, JavaRandom $rnd, $i, $j, $k, $l, $i1){
		switch($weight->className){
			case 'house4garden':
				return VillageHouse4Garden::create($start, $list, $rnd, $i, $j, $k, $l, $i1);
			case 'church':
				return VillageChurch::create($start, $list, $rnd, $i, $j, $k, $l, $i1);
			case 'house1':
				return VillageHouse1::create($start, $list, $rnd, $i, $j, $k, $l, $i1);
			case 'woodhut':
				return VillageWoodHut::create($start, $list, $rnd, $i, $j, $k, $l, $i1);
			case 'hall':
				return VillageHall::create($start, $list, $rnd, $i, $j, $k, $l, $i1);
			case 'field1':
				return VillageField1::create($start, $list, $rnd, $i, $j, $k, $l, $i1);
			case 'field2':
				return VillageField2::create($start, $list, $rnd, $i, $j, $k, $l, $i1);
			case 'house2':
				return VillageHouse2::create($start, $list, $rnd, $i, $j, $k, $l, $i1);
			case 'house3':
				return VillageHouse3::create($start, $list, $rnd, $i, $j, $k, $l, $i1);
		}
		return null;
	}

	private static function rollVillagePiece(VillageWellStart $start, array &$list, JavaRandom $rnd, $i, $j, $k, $l, $i1){
		$total = self::getTotalWeight($start->weights);
		if($total <= 0){
			return null;
		}

		for($k1 = 0; $k1 < 5; $k1++){
			$pick = $rnd->nextBoundedInt($total);

			foreach($start->weights as $w){
				$pick -= $w->weight;
				if($pick < 0){
					if(!$w->isValid($i1) || ($w === $start->lastPiece && count($start->weights) > 1)){
						break;
					}

					$piece = self::createVillagePiece($start, $w, $list, $rnd, $i, $j, $k, $l, $i1);
					if($piece !== null){
						$w->instances++;
						$start->lastPiece = $w;
						if(!$w->canContinue()){
							$start->removeWeight($w);
						}
						return $piece;
					}
				}
			}
		}

		$box = VillageTorch::createBox($start, $list, $rnd, $i, $j, $k, $l);
		if($box !== null){
			return new VillageTorch($start, $i1, $rnd, $box, $l);
		}
		return null;
	}

	/**
	 * 对应 func_75077_d：派生一个村庄建筑部件。
	 */
	public static function addVillagePiece(VillageWellStart $start, array &$list, JavaRandom $rnd, $i, $j, $k, $l, $i1){
		if($i1 > 50){
			return null;
		}

		$bb = $start->getBoundingBox();
		if(abs($i - $bb->minX) > 112 || abs($k - $bb->minZ) > 112){
			return null;
		}

		$piece = self::rollVillagePiece($start, $list, $rnd, $i, $j, $k, $l, $i1 + 1);
		if($piece !== null){
			$pb = $piece->getBoundingBox();
			$j1 = intdiv($pb->minX + $pb->maxX, 2);
			$k1 = intdiv($pb->minZ + $pb->maxZ, 2);
			$l1 = $pb->maxX - $pb->minX;
			$i2 = $pb->maxZ - $pb->minZ;
			$j2 = max($l1, $i2);

			if($start->biomeManager->isBiomeAllowed($j1, $k1, intdiv($j2, 2) + 4, self::ALLOWED_BIOMES)){
				$list[] = $piece;
				$start->villagePieces[] = $piece;
				return $piece;
			}
		}

		return null;
	}

	/**
	 * 对应 func_75080_e：派生一个道路/路径部件。
	 */
	public static function addPath(VillageWellStart $start, array &$list, JavaRandom $rnd, $i, $j, $k, $l, $i1){
		if($i1 > 3 + $start->size){
			return null;
		}

		$bb = $start->getBoundingBox();
		if(abs($i - $bb->minX) > 112 || abs($k - $bb->minZ) > 112){
			return null;
		}

		$box = VillagePath::createBox($start, $list, $rnd, $i, $j, $k, $l);
		if($box !== null && $box->minY > 10){
			$path = new VillagePath($start, $i1, $rnd, $box, $l);
			$pb = $path->getBoundingBox();
			$j1 = intdiv($pb->minX + $pb->maxX, 2);
			$k1 = intdiv($pb->minZ + $pb->maxZ, 2);
			$l1 = $pb->maxX - $pb->minX;
			$i2 = $pb->maxZ - $pb->minZ;
			$j2 = max($l1, $i2);

			if($start->biomeManager->isBiomeAllowed($j1, $k1, intdiv($j2, 2) + 4, self::ALLOWED_BIOMES)){
				$list[] = $path;
				$start->paths[] = $path;
				return $path;
			}
		}

		return null;
	}
}
