<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 右转弯，参考 Java StructureStrongholdPieces$RightTurn。
 */
class StrongholdRightTurn extends StrongholdLeftTurn{

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		if($this->coordBaseMode !== 2 && $this->coordBaseMode !== 3){
			$this->createLeft($component, $components, $rnd, 1, 1);
		}else{
			$this->createRight($component, $components, $rnd, 1, 1);
		}
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		$this->generateShell($w, $rnd, $box, 4, 4, 4);
		$this->generateDoor($w, $box, 1, 1, 0, $this->entryDoor);

		if($this->coordBaseMode !== 2 && $this->coordBaseMode !== 3){
			$this->fillWithAir($w, $box, 0, 1, 1, 0, 3, 3);
		}else{
			$this->fillWithAir($w, $box, 4, 1, 1, 4, 3, 3);
		}
		return true;
	}
}
