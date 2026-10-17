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
 * MCPE 0.14.3 StrongholdStart。
 */
class StrongholdStart extends StructureStart{

	public function __construct(World $world, JavaRandom $rnd, $chunkX, $chunkZ){
		parent::__construct($chunkX, $chunkZ);
		StrongholdPieces::reset();

		$start = new StrongholdStairs2(0, $rnd, ($chunkX << 4) + 2, ($chunkZ << 4) + 2);
		$this->components[] = $start;
		StrongholdPieces::setStartBox($start->getBoundingBox());
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
