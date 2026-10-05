<?php

namespace pocketmine\level\generator\gorigional\layer;

/**
 * 移植自 SCAXE-GO-CE pkg/level/generator/gorigional/layer/initialize.go。
 */
final class LayerFactory{

	/**
	 * @return GenLayer[] [riverMix, voronoi, riverMix]
	 */
	public static function initializeAll($seed){
		$l = new GenLayerIsland(1);
		$l = new GenLayerZoom(2000, $l, true);
		$l = new GenLayerAddIsland(1, $l);
		$l = new GenLayerZoom(2001, $l);
		$l = new GenLayerAddIsland(2, $l);
		$l = new GenLayerAddIsland(50, $l);
		$l = new GenLayerAddIsland(70, $l);
		$l = new GenLayerRemoveTooMuchOcean(2, $l);
		$l = new GenLayerAddSnow(2, $l);
		$l = new GenLayerAddIsland(3, $l);
		$l = new GenLayerEdge(2, $l, GenLayerEdge::COOL_WARM);
		$l = new GenLayerEdge(2, $l, GenLayerEdge::HEAT_ICE);
		$l = new GenLayerEdge(3, $l, GenLayerEdge::SPECIAL);
		$l = new GenLayerZoom(2002, $l);
		$l = new GenLayerZoom(2003, $l);
		$l = new GenLayerAddIsland(4, $l);

		$l = new GenLayerAddMushroomIsland(5, $l);

		$l = new GenLayerDeepOcean(4, $l);

		$l4 = GenLayer::magnify(1000, $l, 0);

		$l7 = GenLayer::magnify(1000, $l4, 0);

		$riverInit = new GenLayerRiverInit(100, $l7);

		$l8 = new GenLayerBiome(200, $l4);

		$l6 = GenLayer::magnify(1000, $l8, 2);

		$biomeEdge = new GenLayerBiomeEdge(1000, $l6);

		$l9 = GenLayer::magnify(1000, $riverInit, 2);

		$hills = new GenLayerHills(1000, $biomeEdge, $l9);

		$l5 = GenLayer::magnify(1000, $riverInit, 2);

		$l5 = GenLayer::magnify(1000, $l5, 4);

		$river = new GenLayerRiver(1, $l5);
		$smoothRiver = new GenLayerSmooth(1000, $river);

		$hills = new GenLayerRareBiome(1001, $hills);

		for($k = 0; $k < 4; $k++){
			$hills = new GenLayerZoom(1000 + $k, $hills);

			if($k === 0){
				$hills = new GenLayerAddIsland(3, $hills);
			}

			if($k === 1){
				$hills = new GenLayerShore(1000, $hills);
			}
		}

		$smoothHills = new GenLayerSmooth(1000, $hills);

		$riverMix = new GenLayerRiverMix(100, $smoothHills, $smoothRiver);

		$voronoi = new GenLayerVoronoiZoom(10, $riverMix);

		$riverMix->initWorldGenSeed($seed);
		$voronoi->initWorldGenSeed($seed);

		return [$riverMix, $voronoi, $riverMix];
	}
}
