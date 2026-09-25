<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝
 * ╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 *  ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: U core
 *
 * @Links:
 *  > LY Core
 *  > LY Core Project
*/

namespace pocketmine\level\generator\normal\object;

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\level\generator\object\PopulatorObject;
use pocketmine\utils\Random;

require_once __DIR__ . "/RuinedPortalLoot.php";

class RuinedPortal extends PopulatorObject{
	const WIDTH_X = 9;
	const WIDTH_Z = 9;
	const HEIGHT = 7;

	/** @var ChunkManager */
	private $level;
	/** @var Random */
	private $random;
	private $originX;
	private $originY;
	private $originZ;

	public function placeObject(ChunkManager $level, $x, $y, $z, Random $random){
		$this->level = $level;
		$this->random = $random;
		$this->originX = (int) $x;
		$this->originY = (int) $y;
		$this->originZ = (int) $z;

		$this->placeGroundPatch();
		$this->placeFrame();
		$this->placeDecorations();
	}

	private function placeGroundPatch(){
		for($x = 0; $x < self::WIDTH_X; ++$x){
			for($z = 0; $z < self::WIDTH_Z; ++$z){
				if($x === 4 && $z === 4){
					$this->place($x, -1, $z, Block::NETHERRACK);
					continue;
				}
				if($this->random->nextBoundedInt(4) !== 0){
					$block = $this->random->nextBoundedInt(5) === 0 ? Block::MOSS_STONE : Block::NETHERRACK;
					$this->place($x, -1, $z, $block);
				}
			}
		}

		$this->placeStoneRubble();
	}

	private function placeFrame(){
		foreach([
			[2, 1, 4, Block::OBSIDIAN],
			[2, 2, 4, Block::OBSIDIAN],
			[2, 3, 4, Block::OBSIDIAN],
			[2, 4, 4, Block::OBSIDIAN],
			[2, 5, 4, Block::OBSIDIAN],
			[3, 1, 4, Block::OBSIDIAN],
			[4, 1, 4, Block::OBSIDIAN],
			[5, 1, 4, Block::OBSIDIAN],
			[3, 5, 4, Block::OBSIDIAN],
			[4, 5, 4, Block::OBSIDIAN],
			[5, 5, 4, Block::OBSIDIAN],
			[6, 1, 4, Block::OBSIDIAN],
			[6, 2, 4, Block::OBSIDIAN],
			[6, 4, 4, Block::OBSIDIAN],
		] as $entry){
			$this->place($entry[0], $entry[1], $entry[2], $entry[3]);
		}
	}

	private function placeStoneRubble(){
		foreach([
			[0, 0, 1, Block::STONE_BRICKS, 2],
			[1, 0, 0, Block::STONE_BRICKS, 0],
			[1, 0, 1, Block::STONE_BRICKS, 1],
			[2, 0, 0, Block::COBBLESTONE_WALL, 0],
			[3, 0, 0, Block::STONE_BRICKS, 3],
			[3, 1, 0, Block::STONE_BRICK_STAIRS, 2],
			[5, 0, 0, Block::STONE_BRICKS, 0],
			[6, 0, 0, Block::COBBLESTONE_WALL, 0],
			[7, 0, 0, Block::SLAB, 5],
			[7, 0, 1, Block::STONE_BRICKS, 2],
			[8, 0, 2, Block::MOSS_STONE, 0],
			[8, 0, 3, Block::SLAB, 5],
			[8, 0, 5, Block::STONE_BRICK_STAIRS, 1],
			[8, 0, 6, Block::MOSS_STONE, 0],
			[7, 0, 7, Block::STONE_BRICKS, 1],
			[6, 0, 8, Block::COBBLESTONE_WALL, 0],
			[5, 0, 8, Block::SLAB, 5],
			[2, 0, 8, Block::STONE_BRICK_STAIRS, 3],
			[1, 0, 8, Block::STONE_BRICKS, 3],
			[0, 0, 7, Block::MOSS_STONE, 0],
			[0, 0, 6, Block::STONE_BRICKS, 2],
			[0, 0, 5, Block::SLAB, 5],
			[1, 0, 3, Block::STONE_BRICK_STAIRS, 0],
		] as $entry){
			$this->place($entry[0], $entry[1], $entry[2], $entry[3], $entry[4]);
		}

		foreach([
			[1, 1, 0], [6, 1, 0], [6, 1, 8]
		] as $entry){
			$this->place($entry[0], $entry[1], $entry[2], Block::COBBLESTONE_WALL);
		}
	}

	private function placeDecorations(){
		$this->place(7, 0, 2, Block::GOLD_BLOCK);
		$this->placeChest(1, 0, 6);
		$this->place(6, -1, 6, Block::NETHERRACK);
		$this->place(6, 0, 6, Block::LAVA);
		$this->place(4, -1, 6, Block::NETHERRACK);
		$this->place(4, 0, 6, Block::FIRE);
		$this->place(3, 0, 7, Block::GRAVEL);
		$this->place(5, 0, 7, Block::COBBLESTONE);
	}

	private function placeChest($x, $y, $z){
		$this->place($x, $y, $z, Block::CHEST, 4);
		$this->markExtra($x, $y, $z, RuinedPortalLoot::CHEST_MARKER_ID, RuinedPortalLoot::CHEST_MARKER_DATA, RuinedPortalLoot::CHEST_MARKER);
	}

	private function place($x, $y, $z, $id, $meta = 0){
		$this->level->setBlockIdAt($this->originX + $x, $this->originY + $y, $this->originZ + $z, $id);
		$this->level->setBlockDataAt($this->originX + $x, $this->originY + $y, $this->originZ + $z, $meta);
	}

	private function markExtra($x, $y, $z, $id, $data, $marker){
		$wx = $this->originX + $x;
		$wy = $this->originY + $y;
		$wz = $this->originZ + $z;
		if(method_exists($this->level, "setBlockExtraDataAt")){
			$this->level->setBlockExtraDataAt($wx, $wy, $wz, $id, $data);
			return;
		}

		$chunk = $this->level->getChunk($wx >> 4, $wz >> 4);
		if($chunk !== null && method_exists($chunk, "setBlockExtraData")){
			$chunk->setBlockExtraData($wx & 0x0f, $wy & 0x7f, $wz & 0x0f, $marker);
		}
	}
}
