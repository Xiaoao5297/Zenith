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


namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 湖泊生成：Java 1.7.10 的椭球布局，加上 MCPE LakeFeature 的壳层检查和
 * 湖岸处理。MCPE 与 Java 的湖体几何基本同源，主要差异在随机流和壳层判定。
 */
class Lake implements Feature{
	/** @var int */
	private $blockID;

	public function __construct($blockID){
		$this->blockID = $blockID;
	}

	public function generate(ObjectChunkManager $w, JavaRandom $r, BlockPos $pos){
		$x = $pos->x - 8;
		$y = $pos->y;
		$z = $pos->z - 8;

		while($y > 5 && $w->getBlockId($x, $y, $z) === 0){
			$y--;
		}

		if($y <= 4){
			return false;
		}

		$y -= 4;

		$aboolean = array_fill(0, 2048, false);
		$i = ($r->nextInt() & 3) + 4;

		for($j = 0; $j < $i; $j++){
			$d0 = $r->nextFloat() * 6.0 + 3.0;
			$d1 = $r->nextFloat() * 4.0 + 2.0;
			$d2 = $r->nextFloat() * 6.0 + 3.0;
			$d3 = $r->nextFloat() * (16.0 - $d0 - 2.0) + 1.0 + $d0 / 2.0;
			$d4 = $r->nextFloat() * (8.0 - $d1 - 4.0) + 2.0 + $d1 / 2.0;
			$d5 = $r->nextFloat() * (16.0 - $d2 - 2.0) + 1.0 + $d2 / 2.0;

			for($l = 1; $l < 15; $l++){
				for($i1 = 1; $i1 < 15; $i1++){
					for($j1 = 1; $j1 < 7; $j1++){
						$d6 = ($l - $d3) / ($d0 / 2.0);
						$d7 = ($j1 - $d4) / ($d1 / 2.0);
						$d8 = ($i1 - $d5) / ($d2 / 2.0);
						$d9 = $d6 * $d6 + $d7 * $d7 + $d8 * $d8;

						if($d9 < 1.0){
							$aboolean[($l * 16 + $i1) * 8 + $j1] = true;
						}
					}
				}
			}
		}

		// MCPE LakeFeature 的 _check 检查的是湖体外壳：
		// 下半部外壳必须是实心或同种液体，上半部外壳不能是液体，
		// 避免湖水从洞穴、海面或陡坡泄漏。
		for($k1 = 0; $k1 < 16; $k1++){
			for($l2 = 0; $l2 < 16; $l2++){
				for($k = 0; $k < 8; $k++){
					$flag = !$aboolean[($k1 * 16 + $l2) * 8 + $k] && (($k1 < 15 && $aboolean[(($k1 + 1) * 16 + $l2) * 8 + $k]) ||
						($k1 > 0 && $aboolean[(($k1 - 1) * 16 + $l2) * 8 + $k]) ||
						($l2 < 15 && $aboolean[($k1 * 16 + $l2 + 1) * 8 + $k]) ||
						($l2 > 0 && $aboolean[($k1 * 16 + ($l2 - 1)) * 8 + $k]) ||
						($k < 7 && $aboolean[($k1 * 16 + $l2) * 8 + $k + 1]) ||
						($k > 0 && $aboolean[($k1 * 16 + $l2) * 8 + ($k - 1)]));

					if(!$flag){
						continue;
					}

					$mat = $w->getBlockId($x + $k1, $y + $k, $z + $l2);
					$isLiquid = $mat === Block::WATER || $mat === Block::STILL_WATER || $mat === Block::LAVA || $mat === Block::STILL_LAVA;

					if($k < 4){
						$isSolid = $mat !== 0 && !$isLiquid;
						if(!$isSolid && $mat !== $this->blockID){
							return false;
						}
					}elseif($isLiquid){
						return false;
					}
				}
			}
		}

		for($l1 = 0; $l1 < 16; $l1++){
			for($i3 = 0; $i3 < 16; $i3++){
				for($i4 = 0; $i4 < 8; $i4++){
					if($aboolean[($l1 * 16 + $i3) * 8 + $i4]){
						$bx = $x + $l1;
						$by = $y + $i4;
						$bz = $z + $i3;

						$w->setBlock($bx, $by, $bz, $i4 < 4 ? $this->blockID : 0, 0);
					}
				}
			}
		}

		// 湖面上方的泥土转草方块，贴近 MCPE 的湖岸处理。
		for($l1 = 0; $l1 < 16; $l1++){
			for($i3 = 0; $i3 < 16; $i3++){
				for($i4 = 4; $i4 < 8; $i4++){
					if($aboolean[($l1 * 16 + $i3) * 8 + $i4]){
						$bx = $x + $l1;
						$by = $y + $i4 - 1;
						$bz = $z + $i3;
						if($w->getBlockId($bx, $by, $bz) === Block::DIRT){
							$w->setBlock($bx, $by, $bz, Block::GRASS, 0);
						}
					}
				}
			}
		}

		return true;
	}
}
