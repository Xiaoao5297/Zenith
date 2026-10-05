<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/island.go。
 */
class GenLayerIsland extends GenLayer{

	public function __construct($seed){
		parent::__construct($seed, null);
	}

	public function getInts($x, $z, $width, $depth){
		$result = array_fill(0, $width * $depth, 0);

		for($i = 0; $i < $depth; $i++){
			for($j = 0; $j < $width; $j++){
				$this->initChunkSeed($x + $j, $z + $i);

				$val = 0;
				if($this->nextInt(10) === 0){
					$val = 1;
				}
				$result[$j + $i * $width] = $val;
			}
		}

		if($x > -$width && $x <= 0 && $z > -$depth && $z <= 0){
			$result[(-$x) + (-$z) * $width] = 1;
		}

		return $result;
	}
}
