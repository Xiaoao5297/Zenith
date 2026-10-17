<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * MCPE 0.14.3 VillageStart：以水井为中心展开道路和房屋。
 */
class VillageStart extends StructureStart{

	public function __construct(World $world, JavaRandom $rnd, $chunkX, $chunkZ){
		parent::__construct($chunkX, $chunkZ);

		$size = 3;
		$weights = VillagePieces::getWeightedPieceList($rnd, $size);
		$well = new VillageWellStart($world->getBiomeManager(), 0, $rnd, ($chunkX << 4) + 8, ($chunkZ << 4) + 8, $weights, $size);

		$this->components[] = $well;
		$well->buildComponent($well, $this->components, $rnd);

		/*
		 * MCPE VillageStart 会不断从“待建房屋”或“待建道路”列表中
		 * 随机取出一个部件继续展开，直到两个列表都为空。
		 */
		$guard = 0;
		do{
			if(++$guard > 256){
				// 防御性上限：避免异常种子下道路互相派生导致无限展开。
				break;
			}
			if(count($well->villagePieces) === 0){
				if(count($well->paths) === 0){
					break;
				}
				$index = $rnd->nextBoundedInt(count($well->paths));
				$piece = $well->paths[$index];
				array_splice($well->paths, $index, 1);
			}else{
				$index = $rnd->nextBoundedInt(count($well->villagePieces));
				$piece = $well->villagePieces[$index];
				array_splice($well->villagePieces, $index, 1);
			}
			$piece->buildComponent($well, $this->components, $rnd);
		}while(true);

		$this->updateBoundingBox();
	}
}
