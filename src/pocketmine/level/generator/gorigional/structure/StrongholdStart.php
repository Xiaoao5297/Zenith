<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * MCPE 0.14.3 StrongholdStart。
 */
class StrongholdStart extends StructureStart{

	public function __construct(World $world, JavaRandom $rnd, $chunkX, $chunkZ){
		parent::__construct($chunkX, $chunkZ);
		StrongholdPieces::reset();

		$start = new StrongholdStairs2(0, $rnd, ($chunkX << 4) + 2, ($chunkZ << 4) + 2);
		$this->components[] = $start;
		$start->buildComponent($start, $this->components, $rnd);

		$guard = 0;
		while(!empty($start->pendingPieces)){
			if(++$guard > 120){
				break;
			}
			$index = $rnd->nextBoundedInt(count($start->pendingPieces));
			$piece = $start->pendingPieces[$index];
			array_splice($start->pendingPieces, $index, 1);
			$piece->buildComponent($start, $this->components, $rnd);
		}

		$this->updateBoundingBox();
		$this->placeAtHeight($world, $rnd, 5);
	}
}
