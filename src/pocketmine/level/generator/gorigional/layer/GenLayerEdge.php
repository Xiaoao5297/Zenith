<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/edge.go。
 */
class GenLayerEdge extends GenLayer{
	const COOL_WARM = 0;
	const HEAT_ICE = 1;
	const SPECIAL = 2;

	/** @var int */
	private $mode;

	public function __construct($seed, GenLayer $parent, $mode){
		parent::__construct($seed, $parent);
		$this->mode = $mode;
	}

	protected function getIntsInternal($areaX, $areaY, $width, $height){
		switch($this->mode){
			case self::HEAT_ICE:
				return $this->getIntsHeatIce($areaX, $areaY, $width, $height);
			case self::SPECIAL:
				return $this->getIntsSpecial($areaX, $areaY, $width, $height);
			case self::COOL_WARM:
			default:
				return $this->getIntsCoolWarm($areaX, $areaY, $width, $height);
		}
	}

	private function getIntsCoolWarm($areaX, $areaY, $width, $height){
		$parentX = $areaX - 1;
		$parentY = $areaY - 1;
		$parentWidth = $width + 2;
		$parentHeight = $height + 2;

		$parentInts = $this->parent->getInts($parentX, $parentY, $parentWidth, $parentHeight);
		$result = array_fill(0, $width * $height, 0);

		$k = $parentWidth;

		for($y = 0; $y < $height; $y++){
			for($x = 0; $x < $width; $x++){
				$this->initChunkSeed($x + $areaX, $y + $areaY);
				$k1 = $parentInts[$x + 1 + ($y + 1) * $k];

				if($k1 === 1){
					$l1 = $parentInts[$x + 1 + ($y + 0) * $k];
					$i2 = $parentInts[$x + 2 + ($y + 1) * $k];
					$j2 = $parentInts[$x + 0 + ($y + 1) * $k];
					$k2 = $parentInts[$x + 1 + ($y + 2) * $k];

					$flag = ($l1 === 3 || $i2 === 3 || $j2 === 3 || $k2 === 3);
					$flag1 = ($l1 === 4 || $i2 === 4 || $j2 === 4 || $k2 === 4);

					if($flag || $flag1){
						$k1 = 2;
					}
				}
				$result[$x + $y * $width] = $k1;
			}
		}
		return $result;
	}

	private function getIntsHeatIce($areaX, $areaY, $width, $height){
		$parentX = $areaX - 1;
		$parentY = $areaY - 1;
		$parentWidth = $width + 2;
		$parentHeight = $height + 2;

		$parentInts = $this->parent->getInts($parentX, $parentY, $parentWidth, $parentHeight);
		$result = array_fill(0, $width * $height, 0);

		$k = $parentWidth;

		for($y = 0; $y < $height; $y++){
			for($x = 0; $x < $width; $x++){
				$k1 = $parentInts[$x + 1 + ($y + 1) * $k];
				if($k1 === 4){
					$l1 = $parentInts[$x + 1 + ($y + 0) * $k];
					$i2 = $parentInts[$x + 2 + ($y + 1) * $k];
					$j2 = $parentInts[$x + 0 + ($y + 1) * $k];
					$k2 = $parentInts[$x + 1 + ($y + 2) * $k];

					$flag = ($l1 === 2 || $i2 === 2 || $j2 === 2 || $k2 === 2);
					$flag1 = ($l1 === 1 || $i2 === 1 || $j2 === 1 || $k2 === 1);

					if($flag || $flag1){
						$k1 = 3;
					}
				}
				$result[$x + $y * $width] = $k1;
			}
		}
		return $result;
	}

	private function getIntsSpecial($areaX, $areaY, $width, $height){
		$parentInts = $this->parent->getInts($areaX, $areaY, $width, $height);
		$result = array_fill(0, $width * $height, 0);

		for($i = 0; $i < count($parentInts); $i++){
			$this->initChunkSeed($i % $width + $areaX, intdiv($i, $width) + $areaY);
			$k = $parentInts[$i];

			if($k !== 0 && $this->nextInt(13) === 0){
				$k |= ((1 + $this->nextInt(15)) << 8) & 3840;
			}
			$result[$i] = $k;
		}
		return $result;
	}
}
