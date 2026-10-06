<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/voronoi.go。
 */
class GenLayerVoronoiZoom extends GenLayer{

	public function __construct($baseSeed, GenLayer $parent){
		parent::__construct($baseSeed, $parent);
	}

	protected function getIntsInternal($x, $z, $width, $depth){
		$x -= 2;
		$z -= 2;

		$i = $x >> 2;
		$j = $z >> 2;
		$k = ($width >> 2) + 2;
		$m = ($depth >> 2) + 2;

		$parentInts = $this->parent->getInts($i, $j, $k, $m);

		$out = array_fill(0, $width * $depth, 0);

		for($k1 = 0; $k1 < $m - 1; $k1++){
			$l1 = 0;
			$i2 = $parentInts[$l1 + ($k1 + 0) * $k];

			for($l1 = 0; $l1 < $k - 1; $l1++){
				$this->initChunkSeed(($l1 + $i) << 2, ($k1 + $j) << 2);
				$d1 = ($this->nextInt(1024) / 1024.0 - 0.5) * 3.6;
				$d2 = ($this->nextInt(1024) / 1024.0 - 0.5) * 3.6;

				$this->initChunkSeed(($l1 + $i + 1) << 2, ($k1 + $j) << 2);
				$d3 = ($this->nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
				$d4 = ($this->nextInt(1024) / 1024.0 - 0.5) * 3.6;

				$this->initChunkSeed(($l1 + $i) << 2, ($k1 + $j + 1) << 2);
				$d5 = ($this->nextInt(1024) / 1024.0 - 0.5) * 3.6;
				$d6 = ($this->nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;

				$this->initChunkSeed(($l1 + $i + 1) << 2, ($k1 + $j + 1) << 2);
				$d7 = ($this->nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
				$d8 = ($this->nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;

				$k2 = $parentInts[$l1 + 1 + ($k1 + 0) * $k] & 255;
				$l2 = $parentInts[$l1 + 1 + ($k1 + 1) * $k] & 255;

				$j2 = $parentInts[$l1 + ($k1 + 1) * $k] & 255;

				$j2 = $parentInts[$l1 + ($k1 + 1) * $k] & 255;
				$i2 = $parentInts[$l1 + ($k1 + 0) * $k] & 255;

				for($i3 = 0; $i3 < 4; $i3++){
					$absZ = (($k1 + $j) << 2) + $i3;
					$outZ = $absZ - $z;

					for($k3 = 0; $k3 < 4; $k3++){
						$absX = (($l1 + $i) << 2) + $k3;
						$outX = $absX - $x;

						if($outX >= 0 && $outX < $width && $outZ >= 0 && $outZ < $depth){
							$d9 = ($i3 - $d2) * ($i3 - $d2) + ($k3 - $d1) * ($k3 - $d1);
							$d10 = ($i3 - $d4) * ($i3 - $d4) + ($k3 - $d3) * ($k3 - $d3);
							$d11 = ($i3 - $d6) * ($i3 - $d6) + ($k3 - $d5) * ($k3 - $d5);
							$d12 = ($i3 - $d8) * ($i3 - $d8) + ($k3 - $d7) * ($k3 - $d7);

							$idx = $outX + $outZ * $width;

							if($d9 < $d10 && $d9 < $d11 && $d9 < $d12){
								$out[$idx] = $i2;
							}elseif($d10 < $d9 && $d10 < $d11 && $d10 < $d12){
								$out[$idx] = $k2;
							}elseif($d11 < $d9 && $d11 < $d10 && $d11 < $d12){
								$out[$idx] = $j2;
							}else{
								$out[$idx] = $l2;
							}
						}
					}
				}
			}
		}

		return $out;
	}
}
