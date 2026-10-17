<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 要塞部件的公共基类。
 *
 * 结构布局参考 Java 1.7.10 StructureStrongholdPieces，随机数和要塞
 * 选址使用 MCPE 0.14.3 的 StrongholdFeature。
 */
abstract class StrongholdPiece extends StructureComponentBase{
	const AIR = 0;
	const STONE = 1;
	const COBBLESTONE = 4;
	const PLANKS = 5;
	const WATER = 8;
	const LAVA = 10;
	const IRON_BARS = 101;
	const LADDER = 65;
	const TORCH = 50;
	const CHEST = 54;
	const MONSTER_SPAWNER = 52;
	const STONE_BRICKS = 98;
	const MOSSY_STONE_BRICKS = 98; // meta 1
	const CRACKED_STONE_BRICKS = 98; // meta 2
	const STONE_BRICK_STAIRS = 109;
	const BOOKSHELF = 47;
	const FENCE = 85;
	const END_PORTAL_FRAME = 120;
	const END_PORTAL = 119;
	const ICE = 79;

	/** @var int */
	protected $entryDoor = 0; // 0=opening, 1=wood, 2=iron, 3=grates
	/** @var int */
	protected $pieceType = 0;

	public function __construct($type){
		$this->componentType = $type;
		$this->pieceType = $type;
	}

	/**
	 * 生成随机石砖：大部分是普通石砖，少量苔石/裂石。
	 */
	protected function stoneBrick(JavaRandom $rnd){
		$r = $rnd->nextBoundedInt(10);
		if($r === 0){
			return [self::STONE_BRICKS, 1];
		}
		if($r === 1){
			return [self::STONE_BRICKS, 2];
		}
		return [self::STONE_BRICKS, 0];
	}

	protected function fillRandomBricks(WorldAccess $w, BoundingBox $box, $minX, $minY, $minZ, $maxX, $maxY, $maxZ, JavaRandom $rnd, $keepOld = false){
		$this->fillWithRandomizedBlocks($w, $box, $minX, $minY, $minZ, $maxX, $maxY, $maxZ, $keepOld, $rnd, function($r, $x, $y, $z, $wall){
			return $this->stoneBrick($r);
		});
	}

	/**
	 * 先砌外壳，再挖空内部；对应 Java 的 generateBox(..., true, ...)。
	 */
	protected function generateShell(WorldAccess $w, JavaRandom $rnd, BoundingBox $box, $maxX, $maxY, $maxZ){
		$this->fillRandomBricks($w, $box, 0, 0, 0, $maxX, $maxY, $maxZ, $rnd, false);
		$this->fillWithAir($w, $box, 1, 1, 1, $maxX - 1, $maxY - 1, $maxZ - 1);
	}

	/**
	 * 在入口处挖一个 3x3 的门洞，并可选放门。
	 */
	protected function generateDoor(WorldAccess $w, BoundingBox $box, $x, $y, $z, $doorType){
		for($dx = 0; $dx < 3; $dx++){
			for($dy = 0; $dy < 3; $dy++){
				$this->placeBlock($w, self::AIR, 0, $x + $dx, $y + $dy, $z, $box);
			}
		}

		if($doorType === 1){
			for($dy = 0; $dy < 2; $dy++){
				$this->placeBlock($w, 64, 0, $x + 1, $y + $dy, $z, $box);
			}
		}elseif($doorType === 2){
			for($dy = 0; $dy < 2; $dy++){
				$this->placeBlock($w, 71, 0, $x + 1, $y + $dy, $z, $box);
			}
		}elseif($doorType === 3){
			for($dx = 0; $dx < 3; $dx++){
				for($dy = 0; $dy < 2; $dy++){
					$this->placeBlock($w, self::IRON_BARS, 0, $x + $dx, $y + $dy, $z, $box);
				}
			}
			$this->placeBlock($w, self::AIR, 0, $x + 1, $y, $z, $box);
			$this->placeBlock($w, self::AIR, 0, $x + 1, $y + 1, $z, $box);
		}
	}

	protected function placeSpawner(WorldAccess $w, BoundingBox $box, $x, $y, $z, $entityId){
		$worldX = $this->getXWithOffset($x, $z);
		$worldY = $this->getYWithOffset($y);
		$worldZ = $this->getZWithOffset($x, $z);
		if($box->resultIsInside($worldX, $worldY, $worldZ)){
			$w->setBlock($worldX, $worldY, $worldZ, self::MONSTER_SPAWNER, 0);
			$w->setSpawnerEntity($worldX, $worldY, $worldZ, $entityId);
		}
	}

	protected static function makeBox($x, $y, $z, $minX, $minY, $minZ, $maxX, $maxY, $maxZ, $facing){
		return BoundingBox::getComponentToAddBoundingBox($x, $y, $z, $minX, $minY, $minZ, $maxX, $maxY, $maxZ, $facing);
	}

	protected static function validBox(BoundingBox $box = null){
		return $box !== null && $box->minY > 10;
	}

	protected function createForward(StructureComponent $start, array &$components, JavaRandom $rnd, $i, $j){
		if($this->coordBaseMode === 0){
			$child = StrongholdPieces::createRandom($components, $rnd, $this->boundingBox->minX + $i, $this->boundingBox->minY + $j, $this->boundingBox->maxZ + 1, 0, $this->pieceType + 1);
		}elseif($this->coordBaseMode === 1){
			$child = StrongholdPieces::createRandom($components, $rnd, $this->boundingBox->minX - 1, $this->boundingBox->minY + $j, $this->boundingBox->minZ + $i, 1, $this->pieceType + 1);
		}elseif($this->coordBaseMode === 2){
			$child = StrongholdPieces::createRandom($components, $rnd, $this->boundingBox->minX + $i, $this->boundingBox->minY + $j, $this->boundingBox->minZ - 1, 2, $this->pieceType + 1);
		}else{
			$child = StrongholdPieces::createRandom($components, $rnd, $this->boundingBox->maxX + 1, $this->boundingBox->minY + $j, $this->boundingBox->minZ + $i, 3, $this->pieceType + 1);
		}
		$this->registerChild($start, $components, $child);
	}

	protected function createLeft(StructureComponent $start, array &$components, JavaRandom $rnd, $i, $j){
		if($this->coordBaseMode === 0){
			$child = StrongholdPieces::createRandom($components, $rnd, $this->boundingBox->minX - 1, $this->boundingBox->minY + $i, $this->boundingBox->minZ + $j, 1, $this->pieceType + 1);
		}elseif($this->coordBaseMode === 1){
			$child = StrongholdPieces::createRandom($components, $rnd, $this->boundingBox->minX + $j, $this->boundingBox->minY + $i, $this->boundingBox->minZ - 1, 2, $this->pieceType + 1);
		}elseif($this->coordBaseMode === 2){
			$child = StrongholdPieces::createRandom($components, $rnd, $this->boundingBox->minX - 1, $this->boundingBox->minY + $i, $this->boundingBox->minZ + $j, 1, $this->pieceType + 1);
		}else{
			$child = StrongholdPieces::createRandom($components, $rnd, $this->boundingBox->minX + $j, $this->boundingBox->minY + $i, $this->boundingBox->minZ - 1, 2, $this->pieceType + 1);
		}
		$this->registerChild($start, $components, $child);
	}

	protected function createRight(StructureComponent $start, array &$components, JavaRandom $rnd, $i, $j){
		if($this->coordBaseMode === 0){
			$child = StrongholdPieces::createRandom($components, $rnd, $this->boundingBox->maxX + 1, $this->boundingBox->minY + $i, $this->boundingBox->minZ + $j, 3, $this->pieceType + 1);
		}elseif($this->coordBaseMode === 1){
			$child = StrongholdPieces::createRandom($components, $rnd, $this->boundingBox->minX + $j, $this->boundingBox->minY + $i, $this->boundingBox->maxZ + 1, 0, $this->pieceType + 1);
		}elseif($this->coordBaseMode === 2){
			$child = StrongholdPieces::createRandom($components, $rnd, $this->boundingBox->maxX + 1, $this->boundingBox->minY + $i, $this->boundingBox->minZ + $j, 3, $this->pieceType + 1);
		}else{
			$child = StrongholdPieces::createRandom($components, $rnd, $this->boundingBox->minX + $j, $this->boundingBox->minY + $i, $this->boundingBox->maxZ + 1, 0, $this->pieceType + 1);
		}
		$this->registerChild($start, $components, $child);
	}

	protected function registerChild(StructureComponent $start, array &$components, $child){
		if($child !== null){
			$components[] = $child;
			if($start instanceof StrongholdStairs2){
				$start->pendingPieces[] = $child;
			}
		}
	}

	abstract public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd);
}
