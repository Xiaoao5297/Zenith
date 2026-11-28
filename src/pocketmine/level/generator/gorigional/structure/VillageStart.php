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
 * MCPE 0.14.3 VillageStart：以水井为中心展开道路和房屋。
 */
class VillageStart extends StructureStart{

	public function __construct(World $world, JavaRandom $rnd, $chunkX, $chunkZ, $size = 0){
		parent::__construct($chunkX, $chunkZ);

		$weights = VillagePieces::getWeightedPieceList($rnd, $size);
		$well = new VillageWellStart($world->getBiomeManager(), 0, $rnd, ($chunkX << 4) + 2, ($chunkZ << 4) + 2, $weights, $size);

		$this->components[] = $well;
		$well->buildComponent($well, $this->components, $rnd);

		/*
		 * Java MapGenVillage$Start：优先从道路列表取，再取房屋列表，
		 * 直到两个列表都为空。
		 */
		$guard = 0;
		do{
			if(++$guard > 256){
				// 防御性上限：避免异常种子下道路互相派生导致无限展开。
				break;
			}
			if(count($well->paths) === 0){
				if(count($well->villagePieces) === 0){
					break;
				}
				$index = $rnd->nextBoundedInt(count($well->villagePieces));
				$piece = $well->villagePieces[$index];
				array_splice($well->villagePieces, $index, 1);
			}else{
				$index = $rnd->nextBoundedInt(count($well->paths));
				$piece = $well->paths[$index];
				array_splice($well->paths, $index, 1);
			}
			$piece->buildComponent($well, $this->components, $rnd);
		}while(true);

		$this->updateBoundingBox();
	}

	/**
	 * Java MapGenVillage$Start.func_75069_d：非道路部件超过 2 个才算有效村庄。
	 */
	public function isValid(){
		$count = 0;
		foreach($this->components as $component){
			if(!($component instanceof VillageRoad)){
				$count++;
			}
		}
		return $count > 2;
	}
}
