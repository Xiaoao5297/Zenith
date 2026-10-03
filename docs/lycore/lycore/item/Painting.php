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

namespace lycore\item;

use lycore\block\Block;
use lycore\level\Level;
use lycore\math\Vector3;
use lycore\Player;
use lycore\entity\Painting as PaintingEntity;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\StringTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\FloatTag;

class Painting extends Item{
	public function __construct($meta = 0, $count = 1){
		parent::__construct(self::PAINTING, 0, $count, "Painting");
	}

	public function canBeActivated() : bool{
		return true;
	}

	private static function getMotives(){
		return [
			// Motive Width Height
			["Kebab", 1, 1],
			["Aztec", 1, 1],
			["Alban", 1, 1],
			["Aztec2", 1, 1],
			["Bomb", 1, 1],
			["Plant", 1, 1],
			["Wasteland", 1, 1],
			["Wanderer", 1, 2],
			["Graham", 1, 2],
			["Pool", 2, 1],
			["Courbet", 2, 1],
			["Sunset", 2, 1],
			["Sea", 2, 1],
			["Creebet", 2, 1],
			["Match", 2, 2],
			["Bust", 2, 2],
			["Stage", 2, 2],
			["Void", 2, 2],
			["SkullAndRoses", 2, 2],
			//array("Wither", 2, 2),
			["Fighters", 4, 2],
			["Skeleton", 4, 3],
			["DonkeyKong", 4, 3],
			["Pointer", 4, 4],
			["Pigscene", 4, 4],
			["Flaming Skull", 4, 4],
		];
	}

	private static function getRightSide($face){
		static $right = [
			Vector3::SIDE_NORTH => Vector3::SIDE_WEST,
			Vector3::SIDE_SOUTH => Vector3::SIDE_EAST,
			Vector3::SIDE_WEST => Vector3::SIDE_SOUTH,
			Vector3::SIDE_EAST => Vector3::SIDE_NORTH,
		];

		return isset($right[$face]) ? $right[$face] : null;
	}

	private static function getFaceFromPaintingDirection($direction){
		static $faces = [
			0 => Vector3::SIDE_SOUTH,
			1 => Vector3::SIDE_WEST,
			2 => Vector3::SIDE_NORTH,
			3 => Vector3::SIDE_EAST,
		];

		return isset($faces[$direction]) ? $faces[$direction] : null;
	}

	private static function getMotiveByName($name){
		foreach(self::getMotives() as $motive){
			if($motive[0] === $name){
				return $motive;
			}
		}

		return null;
	}

	private static function blockKey(Vector3 $block, $face){
		return $face . ":" . ((int) floor($block->x)) . ":" . ((int) floor($block->y)) . ":" . ((int) floor($block->z));
	}

	private static function getPaintingOccupiedBlocks(Vector3 $anchor, $face, array $motive){
		$right = self::getRightSide($face);
		if($right === null){
			return [];
		}

		$blocks = [];
		for($x = 0; $x < $motive[1]; ++$x){
			for($y = 0; $y < $motive[2]; ++$y){
				$pos = $anchor->getSide($right, $x)->getSide(Vector3::SIDE_UP, $y);
				$blocks[self::blockKey($pos, $face)] = true;
			}
		}

		return $blocks;
	}

	private static function getPaintingMotiveName(PaintingEntity $painting){
		if(isset($painting->namedtag)){
			if(is_array($painting->namedtag) and isset($painting->namedtag["Motive"])){
				return (string) $painting->namedtag["Motive"];
			}
			if(isset($painting->namedtag->Motive)){
				return (string) $painting->namedtag["Motive"];
			}
		}

		return null;
	}

	private static function paintingOverlapsExisting(Level $level, Vector3 $anchor, $face, array $motive){
		$newBlocks = self::getPaintingOccupiedBlocks($anchor, $face, $motive);
		if(count($newBlocks) === 0){
			return true;
		}

		foreach($level->getEntities() as $entity){
			if(!($entity instanceof PaintingEntity)){
				continue;
			}

			$existingMotiveName = self::getPaintingMotiveName($entity);
			$existingMotive = $existingMotiveName !== null ? self::getMotiveByName($existingMotiveName) : null;
			$existingFace = self::getFaceFromPaintingDirection($entity->getDirection());
			if($existingMotive === null or $existingFace === null){
				continue;
			}

			$existingAnchor = new Vector3($entity->x, $entity->y, $entity->z);
			foreach(self::getPaintingOccupiedBlocks($existingAnchor, $existingFace, $existingMotive) as $key => $_){
				if(isset($newBlocks[$key])){
					return true;
				}
			}
		}

		return false;
	}

	private static function canPlaceMotive(Level $level, Block $block, Block $target, $face, array $motive){
		$right = self::getRightSide($face);
		if($right === null){
			return false;
		}

		for($x = 0; $x < $motive[1]; ++$x){
			for($y = 0; $y < $motive[2]; ++$y){
				$wallBlock = $target->getSide($right, $x)->getSide(Vector3::SIDE_UP, $y);
				$frontBlock = $block->getSide($right, $x)->getSide(Vector3::SIDE_UP, $y);

				if($wallBlock->isTransparent() or $frontBlock->isSolid()){
					return false;
				}
			}
		}

		return !self::paintingOverlapsExisting($level, $target, $face, $motive);
	}

	public function onActivate(Level $level, Player $player, Block $block, Block $target, $face, $fx, $fy, $fz){
		if($target->isTransparent() === false and $face > 1 and $block->isSolid() === false){
			$faces = [
				2 => 1,
				3 => 3,
				4 => 0,
				5 => 2,
			];

			$validMotives = [];
			foreach(self::getMotives() as $motive){
				if(self::canPlaceMotive($level, $block, $target, $face, $motive)){
					$validMotives[] = $motive;
				}
			}

			if(count($validMotives) === 0){
				return false;
			}

			$motive = $validMotives[mt_rand(0, count($validMotives) - 1)];
			$data = [
				"x" => $target->x,
				"y" => $target->y,
				"z" => $target->z,
				"yaw" => $faces[$face] * 90,
				"Motive" => $motive[0],
			];

			$nbt = new CompoundTag("", [
				"Motive" => new StringTag("Motive", $data["Motive"]),
				"Pos" => new ListTag("Pos", [
					new DoubleTag("", $data["x"]),
					new DoubleTag("", $data["y"]),
					new DoubleTag("", $data["z"])
				]),
				"Motion" => new ListTag("Motion", [
					new DoubleTag("", 0),
					new DoubleTag("", 0),
					new DoubleTag("", 0)
				]),
				"Rotation" => new ListTag("Rotation", [
					new FloatTag("", $data["yaw"]),
					new FloatTag("", 0)
				]),
			]);

			$painting = new PaintingEntity($player->getLevel()->getChunk($block->getX() >> 4, $block->getZ() >> 4), $nbt);
			$painting->spawnToAll();

			if($player->isSurvival()){
				$item = $player->getInventory()->getItemInHand();
				$count = $item->getCount();
				if(--$count <= 0){
					$player->getInventory()->setItemInHand(Item::get(Item::AIR));
					return;
				}

				$item->setCount($count);
				$player->getInventory()->setItemInHand($item);
			}
			//TODO
			//$e = $server->api->entity->add($level, ENTITY_OBJECT, OBJECT_PAINTING, $data);
			//$e->spawnToAll();
			/*if(($player->gamemode & 0x01) === 0x00){
				$player->removeItem(Item::get($this->getId(), $this->getDamage(), 1));
			}*/

			return true;
		}

		return false;
	}

}
