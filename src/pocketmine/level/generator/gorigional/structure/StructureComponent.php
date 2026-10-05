<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 SCAXE-GO-CE structure.StructureComponent。
 */
interface StructureComponent{

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd);

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box);

	public function getBoundingBox();

	public function getComponentType();
}
