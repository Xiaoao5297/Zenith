<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 StructureMineshaftPieces$Room。
 */
class MineshaftRoom extends MineshaftPiece{
	/** @var BoundingBox[] */
	private $entrances = [];

	public function __construct($typeId, JavaRandom $rnd, $x, $z){
		$box = new BoundingBox($x, 50, $z, $x + 7 + $rnd->nextBoundedInt(6), 54 + $rnd->nextBoundedInt(6), $z + 7 + $rnd->nextBoundedInt(6));
		parent::__construct($typeId, $box, -1);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		$i = $this->componentType;
		$j = $this->boundingBox->getYSize() - 3 - 1;
		if($j <= 0){
			$j = 1;
		}

		$xSize = $this->boundingBox->getXSize();
		$zSize = $this->boundingBox->getZSize();

		for($k = 0; $k < $xSize; $k += 4){
			$k += $rnd->nextBoundedInt($xSize);
			if($k + 3 > $xSize){
				break;
			}

			$sc = self::getNextMineshaftComponent($component, $components, $rnd, $this->boundingBox->minX + $k, $this->boundingBox->minY + $rnd->nextBoundedInt($j) + 1, $this->boundingBox->minZ - 1, 2, $i);
			if($sc !== null){
				$bb = $sc->getBoundingBox();
				$this->entrances[] = new BoundingBox($bb->minX, $bb->minY, $this->boundingBox->minZ, $bb->maxX, $bb->maxY, $this->boundingBox->minZ + 1);
			}
		}

		for($k = 0; $k < $xSize; $k += 4){
			$k += $rnd->nextBoundedInt($xSize);
			if($k + 3 > $xSize){
				break;
			}

			$sc = self::getNextMineshaftComponent($component, $components, $rnd, $this->boundingBox->minX + $k, $this->boundingBox->minY + $rnd->nextBoundedInt($j) + 1, $this->boundingBox->maxZ + 1, 0, $i);
			if($sc !== null){
				$bb = $sc->getBoundingBox();
				$this->entrances[] = new BoundingBox($bb->minX, $bb->minY, $this->boundingBox->maxZ - 1, $bb->maxX, $bb->maxY, $this->boundingBox->maxZ);
			}
		}

		for($k = 0; $k < $zSize; $k += 4){
			$k += $rnd->nextBoundedInt($zSize);
			if($k + 3 > $zSize){
				break;
			}

			$sc = self::getNextMineshaftComponent($component, $components, $rnd, $this->boundingBox->minX - 1, $this->boundingBox->minY + $rnd->nextBoundedInt($j) + 1, $this->boundingBox->minZ + $k, 1, $i);
			if($sc !== null){
				$bb = $sc->getBoundingBox();
				$this->entrances[] = new BoundingBox($this->boundingBox->minX, $bb->minY, $bb->minZ, $this->boundingBox->minX + 1, $bb->maxY, $bb->maxZ);
			}
		}

		for($k = 0; $k < $zSize; $k += 4){
			$k += $rnd->nextBoundedInt($zSize);
			if($k + 3 > $zSize){
				break;
			}

			$sc = self::getNextMineshaftComponent($component, $components, $rnd, $this->boundingBox->maxX + 1, $this->boundingBox->minY + $rnd->nextBoundedInt($j) + 1, $this->boundingBox->minZ + $k, 3, $i);
			if($sc !== null){
				$bb = $sc->getBoundingBox();
				$this->entrances[] = new BoundingBox($this->boundingBox->maxX - 1, $bb->minY, $bb->minZ, $this->boundingBox->maxX, $bb->maxY, $bb->maxZ);
			}
		}
	}

	public function addComponentParts(WorldAccess $wld, JavaRandom $rnd, BoundingBox $box){
		if($this->isLiquidInStructureBoundingBox($wld, $box)){
			return false;
		}

		$this->fillWithBlocks($wld, $box, $this->boundingBox->minX, $this->boundingBox->minY, $this->boundingBox->minZ, $this->boundingBox->maxX, $this->boundingBox->minY, $this->boundingBox->maxZ, 3, 0, 0, 0, true);
		$this->fillWithBlocks($wld, $box, $this->boundingBox->minX, $this->boundingBox->minY + 1, $this->boundingBox->minZ, $this->boundingBox->maxX, min($this->boundingBox->maxY, $this->boundingBox->minY + 3), $this->boundingBox->maxZ, 0, 0, 0, 0, false);

		foreach($this->entrances as $e){
			$this->fillWithBlocks($wld, $box, $e->minX, $e->maxY - 2, $e->minZ, $e->maxX, $e->maxY, $e->maxZ, 0, 0, 0, 0, false);
		}

		$this->clearEllipsoid($wld, $box, $this->boundingBox->minX, $this->boundingBox->minY + 4, $this->boundingBox->minZ, $this->boundingBox->maxX, $this->boundingBox->maxY, $this->boundingBox->maxZ, 0, false);

		return true;
	}
}
