<?php

namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/object/tree_big_oak.go。
 */
class BigOakTree implements Feature{
	/** @var BlockPos */
	private $basePos;
	/** @var int */
	private $heightLimit;
	/** @var int */
	private $height;

	private $heightAttenuation = 0.618;
	private $branchSlope = 0.381;
	private $scaleWidth = 1.0;
	private $leafDensity = 1.0;

	private $trunkSize = 1;
	private $heightLimitLimit = 12;
	private $leafDistanceLimit = 4;

	/** @var array[] */
	private $foliageCoords = [];
	/** @var ObjectChunkManager */
	private $level;
	/** @var JavaRandom */
	private $rand;

	public function generate(ObjectChunkManager $level, JavaRandom $random, BlockPos $pos){
		$this->level = $level;
		$this->basePos = $pos;

		$seed = $random->nextInt();
		if($seed >= 0x80000000){
			$seed -= 0x100000000;
		}
		$this->rand = new JavaRandom($seed);

		$this->heightLimit = 5 + $this->rand->nextBoundedInt($this->heightLimitLimit);

		if(!$this->validTreeLocation()){
			return false;
		}

		$this->generateLeafNodeList();
		$this->generateLeaves();
		$this->generateTrunk();
		$this->generateLeafNodeBases();
		return true;
	}

	private function validTreeLocation(){
		$down = $this->basePos->down();
		$id = $this->level->getBlockId($down->x, $down->y, $down->z);

		if($id !== Block::DIRT && $id !== Block::GRASS && $id !== Block::FARMLAND){
			return false;
		}

		$limit = $this->checkBlockLine($this->basePos, $this->basePos->up($this->heightLimit - 1));
		if($limit === -1){
			return true;
		}elseif($limit < 6){
			return false;
		}else{
			$this->heightLimit = $limit;
			return true;
		}
	}

	private function generateLeafNodeList(){
		$this->height = (int) ($this->heightLimit * $this->heightAttenuation);
		if($this->height >= $this->heightLimit){
			$this->height = $this->heightLimit - 1;
		}

		$i = (int) (1.382 + pow($this->leafDensity * $this->heightLimit / 13.0, 2.0));
		if($i < 1){
			$i = 1;
		}

		$j = $this->basePos->y + $this->height;
		$k = $this->heightLimit - $this->leafDistanceLimit;

		$this->foliageCoords = [];
		$this->foliageCoords[] = ['pos' => $this->basePos->up($k), 'branchBase' => $j];

		for(; $k >= 0; $k--){
			$f = $this->layerSize($k);
			if($f >= 0.0){
				for($l = 0; $l < $i; $l++){
					$d0 = $this->scaleWidth * $f * ($this->rand->nextFloat() + 0.328);
					$d1 = $this->rand->nextFloat() * 2.0 * M_PI;
					$d2 = $d0 * sin($d1) + 0.5;
					$d3 = $d0 * cos($d1) + 0.5;

					$xVal = $this->basePos->x + $d2;
					$yVal = $this->basePos->y + ($k - 1);
					$zVal = $this->basePos->z + $d3;

					$blockpos = new BlockPos((int) floor($xVal), (int) floor($yVal), (int) floor($zVal));
					$blockpos1 = $blockpos->up($this->leafDistanceLimit);

					if($this->checkBlockLine($blockpos, $blockpos1) === -1){
						$i1 = $this->basePos->x - $blockpos->x;
						$j1 = $this->basePos->z - $blockpos->z;
						$d4 = $blockpos->y - sqrt($i1 * $i1 + $j1 * $j1) * $this->branchSlope;

						$k1 = (int) $d4;
						if($d4 > $j){
							$k1 = $j;
						}

						$blockpos2 = new BlockPos($this->basePos->x, $k1, $this->basePos->z);

						if($this->checkBlockLine($blockpos2, $blockpos) === -1){
							$this->foliageCoords[] = ['pos' => $blockpos, 'branchBase' => $blockpos2->y];
						}
					}
				}
			}
		}
	}

	private function layerSize($y){
		if($y < $this->heightLimit * 0.3){
			return -1.0;
		}
		$f = $this->heightLimit / 2.0;
		$f1 = $f - $y;
		$f2 = sqrt($f * $f - $f1 * $f1);
		if($f1 === 0.0){
			$f2 = $f;
		}elseif(abs($f1) >= $f){
			return 0.0;
		}
		return $f2 * 0.5;
	}

	private function generateLeaves(){
		foreach($this->foliageCoords as $coord){
			$this->generateLeafNode($coord['pos']);
		}
	}

	private function generateLeafNode(BlockPos $pos){
		for($i = 0; $i < $this->leafDistanceLimit; $i++){
			$this->crosSection($pos->up($i), $this->leafSize($i));
		}
	}

	private function leafSize($y){
		if($y >= 0 && $y < $this->leafDistanceLimit){
			if($y !== 0 && $y !== $this->leafDistanceLimit - 1){
				return 3.0;
			}
			return 2.0;
		}
		return -1.0;
	}

	private function crosSection(BlockPos $pos, $radius){
		$i = (int) ($radius + 0.618);

		for($j = -$i; $j <= $i; $j++){
			for($k = -$i; $k <= $i; $k++){
				if(pow(abs($j) + 0.5, 2.0) + pow(abs($k) + 0.5, 2.0) <= $radius * $radius){
					$blockpos = $pos->add($j, 0, $k);
					$mat = $this->level->getBlockId($blockpos->x, $blockpos->y, $blockpos->z);
					if($mat === 0 || $mat === Block::LEAVES){
						$this->level->setBlock($blockpos->x, $blockpos->y, $blockpos->z, Block::LEAVES, 0);
					}
				}
			}
		}
	}

	private function generateTrunk(){
		$blockpos = $this->basePos;
		$blockpos1 = $this->basePos->up($this->height);

		$this->limb($blockpos, $blockpos1);

		if($this->trunkSize === 2){
			$this->limb($blockpos->east(), $blockpos1->east());
			$this->limb($blockpos->east()->south(), $blockpos1->east()->south());
			$this->limb($blockpos->south(), $blockpos1->south());
		}
	}

	private function generateLeafNodeBases(){
		foreach($this->foliageCoords as $coord){
			$i = $coord['branchBase'];
			$blockpos = new BlockPos($this->basePos->x, $i, $this->basePos->z);

			if(!($blockpos->x === $coord['pos']->x && $blockpos->y === $coord['pos']->y && $blockpos->z === $coord['pos']->z) && $this->leafNodeNeedsBase($i - $this->basePos->y)){
				$this->limb($blockpos, $coord['pos']);
			}
		}
	}

	private function leafNodeNeedsBase($p){
		return $p >= $this->heightLimit * 0.2;
	}

	private function limb(BlockPos $start, BlockPos $end){
		$blockpos = $end->add(-$start->x, -$start->y, -$start->z);
		$i = $this->getGreatestDistance($blockpos);

		if($i === 0){
			return;
		}

		$f = $blockpos->x / $i;
		$f1 = $blockpos->y / $i;
		$f2 = $blockpos->z / $i;

		for($j = 0; $j <= $i; $j++){
			$xVal = $start->x + 0.5 + $j * $f;
			$yVal = $start->y + 0.5 + $j * $f1;
			$zVal = $start->z + 0.5 + $j * $f2;

			$blockpos1 = new BlockPos((int) floor($xVal), (int) floor($yVal), (int) floor($zVal));

			$axis = $this->getLogAxis($start, $blockpos1);

			$meta = 0;
			switch($axis){
				case 1: $meta |= 4; break;
				case 2: $meta |= 8; break;
			}

			$this->level->setBlock($blockpos1->x, $blockpos1->y, $blockpos1->z, Block::LOG, $meta);
		}
	}

	private function getLogAxis(BlockPos $start, BlockPos $end){
		$axis = 0;
		$i = abs($end->x - $start->x);
		$j = abs($end->z - $start->z);
		$k = max($i, $j);

		if($k > 0){
			if($i === $k){
				$axis = 1;
			}elseif($j === $k){
				$axis = 2;
			}
		}
		return $axis;
	}

	private function getGreatestDistance(BlockPos $pos){
		$i = abs($pos->x);
		$j = abs($pos->y);
		$k = abs($pos->z);

		if($k > $i && $k > $j){
			return $k;
		}
		if($j > $i){
			return $j;
		}
		return $i;
	}

	private function checkBlockLine(BlockPos $start, BlockPos $end){
		$blockpos = $end->add(-$start->x, -$start->y, -$start->z);
		$i = $this->getGreatestDistance($blockpos);

		if($i === 0){
			return -1;
		}

		$f = $blockpos->x / $i;
		$f1 = $blockpos->y / $i;
		$f2 = $blockpos->z / $i;

		for($j = 0; $j <= $i; $j++){
			$xVal = $start->x + 0.5 + $j * $f;
			$yVal = $start->y + 0.5 + $j * $f1;
			$zVal = $start->z + 0.5 + $j * $f2;

			$blockpos1 = new BlockPos((int) floor($xVal), (int) floor($yVal), (int) floor($zVal));

			$id = $this->level->getBlockId($blockpos1->x, $blockpos1->y, $blockpos1->z);

			if($id !== 0 && $id !== Block::LEAVES && $id !== Block::SAPLING && $id !== Block::VINE && $id !== Block::LOG){
				return $j;
			}
		}
		return -1;
	}
}
