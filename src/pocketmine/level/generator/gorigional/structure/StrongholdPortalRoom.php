<?php

namespace pocketmine\level\generator\gorigional\structure;

use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 末地传送门房间，参考 Java StructureStrongholdPieces$PortalRoom。
 */
class StrongholdPortalRoom extends StrongholdPiece{

	public function __construct($type, JavaRandom $rnd, BoundingBox $box, $facing){
		parent::__construct($type);
		$this->coordBaseMode = $facing;
		$this->entryDoor = 0;
		$this->boundingBox = $box;
	}

	public static function create(array $list, JavaRandom $rnd, $x, $y, $z, $facing, $type){
		$box = self::makeBox($x, $y, $z, -4, -1, 0, 11, 8, 16, $facing);
		if(!self::validBox($box) || self::findIntersecting($list, $box) !== null){
			return null;
		}
		return new self($type, $rnd, $box, $facing);
	}

	public function buildComponent(StructureComponent $component = null, array &$components, JavaRandom $rnd){
		// 传送门房间是终点。
	}

	public function addComponentParts(WorldAccess $w, JavaRandom $rnd, BoundingBox $box){
		$this->generateShell($w, $rnd, $box, 10, 7, 15);
		$this->generateDoor($w, $box, 4, 1, 0, 0);

		// 入口台阶。
		$this->fillWithBlocks($w, $box, 4, 1, 5, 6, 1, 7, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->fillWithBlocks($w, $box, 4, 2, 6, 6, 2, 7, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->fillWithBlocks($w, $box, 4, 3, 7, 6, 3, 7, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);

		// 末地传送门：水平 3x3 传送门方块，外围 5x5 框架。
		// 放在房间中部，避免旋转后落到包围盒外。
		$centerZ = 7;
		for($x = 4; $x <= 6; $x++){
			for($z = $centerZ - 1; $z <= $centerZ + 1; $z++){
				$this->placeBlock($w, self::END_PORTAL, 0, $x, 3, $z, $box);
			}
		}
		for($x = 3; $x <= 7; $x++){
			$this->placeBlock($w, self::END_PORTAL_FRAME, 0, $x, 3, $centerZ - 2, $box);
			$this->placeBlock($w, self::END_PORTAL_FRAME, 0, $x, 3, $centerZ + 2, $box);
		}
		for($z = $centerZ - 1; $z <= $centerZ + 1; $z++){
			$this->placeBlock($w, self::END_PORTAL_FRAME, 0, 3, 3, $z, $box);
			$this->placeBlock($w, self::END_PORTAL_FRAME, 0, 7, 3, $z, $box);
		}

		// 传送门前方的岩浆池。
		$this->fillWithBlocks($w, $box, 3, 1, 10, 7, 1, 13, self::STONE_BRICKS, 0, self::STONE_BRICKS, 0, false);
		$this->fillWithBlocks($w, $box, 4, 1, 11, 6, 1, 12, self::LAVA, 0, self::LAVA, 0, false);

		// 刷怪笼放在传送门一侧。
		$this->placeBlock($w, self::MONSTER_SPAWNER, 0, 2, 1, 12, $box);
		return true;
	}
}
