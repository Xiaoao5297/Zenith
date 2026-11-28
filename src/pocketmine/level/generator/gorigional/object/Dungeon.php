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


namespace pocketmine\level\generator\gorigional\object;

use pocketmine\block\Block;
use pocketmine\entity\Skeleton;
use pocketmine\entity\Spider;
use pocketmine\entity\Zombie;
use pocketmine\item\Item;
use pocketmine\level\generator\gorigional\JavaRandom;

/**
 * 移植自 Java 1.7.10 net.minecraft.world.gen.feature.WorldGenDungeons。
 */
class Dungeon implements Feature{

	/** @var array[] [id, meta, min, max, weight] */
	private static $loot = [
		[Item::SADDLE, 0, 1, 1, 10],
		[Item::IRON_INGOT, 0, 1, 4, 10],
		[Item::BREAD, 0, 1, 1, 10],
		[Item::WHEAT, 0, 1, 4, 10],
		[Item::GUNPOWDER, 0, 1, 4, 10],
		[Item::STRING, 0, 1, 4, 10],
		[Item::BUCKET, 0, 1, 1, 10],
		[Item::GOLDEN_APPLE, 0, 1, 1, 1],
		[Item::REDSTONE, 0, 1, 4, 10],
		[500, 0, 1, 1, 10],
		[501, 0, 1, 1, 10],
		[421, 0, 1, 1, 10],
		[Item::GOLDEN_HORSE_ARMOR, 0, 1, 1, 2],
		[Item::IRON_HORSE_ARMOR, 0, 1, 1, 5],
		[Item::DIAMOND_HORSE_ARMOR, 0, 1, 1, 1],
		[Item::ENCHANTED_BOOK, 0, 1, 1, 1]
	];

	public function generate(ObjectChunkManager $w, JavaRandom $r, BlockPos $pos){
		$x = $pos->x;
		$y = $pos->y;
		$z = $pos->z;

		// Java: byte b0 = 3; l/i1 = nextInt(2) + 2.
		$b0 = 3;
		$l = $r->nextBoundedInt(2) + 2;
		$i1 = $r->nextBoundedInt(2) + 2;
		$j1 = 0;

		for($k1 = $x - $l - 1; $k1 <= $x + $l + 1; $k1++){
			for($l1 = $y - 1; $l1 <= $y + $b0 + 1; $l1++){
				for($i2 = $z - $i1 - 1; $i2 <= $z + $i1 + 1; $i2++){
					$solid = $this->isSolid($w->getBlockId($k1, $l1, $i2));

					if(($l1 === $y - 1 || $l1 === $y + $b0 + 1) && !$solid){
						return false;
					}

					if(($k1 === $x - $l - 1 || $k1 === $x + $l + 1 || $i2 === $z - $i1 - 1 || $i2 === $z + $i1 + 1)
						&& $l1 === $y
						&& $w->getBlockId($k1, $l1, $i2) === Block::AIR
						&& $w->getBlockId($k1, $l1 + 1, $i2) === Block::AIR){
						$j1++;
					}
				}
			}
		}

		if($j1 < 1 || $j1 > 5){
			return false;
		}

		for($k1 = $x - $l - 1; $k1 <= $x + $l + 1; $k1++){
			for($l1 = $y + $b0; $l1 >= $y - 1; $l1--){
				for($i2 = $z - $i1 - 1; $i2 <= $z + $i1 + 1; $i2++){
					$target = $w->getBlockId($k1, $l1, $i2);

					if($k1 !== $x - $l - 1 && $l1 !== $y - 1 && $i2 !== $z - $i1 - 1
						&& $k1 !== $x + $l + 1 && $l1 !== $y + $b0 + 1 && $i2 !== $z + $i1 + 1){
						$w->setBlock($k1, $l1, $i2, Block::AIR, 0);
					}elseif($l1 >= 0 && !$this->isSolid($w->getBlockId($k1, $l1 - 1, $i2))){
						$w->setBlock($k1, $l1, $i2, Block::AIR, 0);
					}elseif($this->isSolid($target)){
						if($l1 === $y - 1 && $r->nextBoundedInt(4) !== 0){
							$w->setBlock($k1, $l1, $i2, Block::MOSS_STONE, 0);
						}else{
							$w->setBlock($k1, $l1, $i2, Block::COBBLESTONE, 0);
						}
					}
				}
			}
		}

		// Java: 两个箱子，每箱最多尝试 3 次；四邻恰好 1 个实心方块。
		for($chest = 0; $chest < 2; $chest++){
			for($attempt = 0; $attempt < 3; $attempt++){
				$cx = $x + $r->nextBoundedInt($l * 2 + 1) - $l;
				$cz = $z + $r->nextBoundedInt($i1 * 2 + 1) - $i1;

				if($w->getBlockId($cx, $y, $cz) !== Block::AIR){
					continue;
				}

				$solidNeighbours = 0;
				if($this->isSolid($w->getBlockId($cx - 1, $y, $cz))){
					$solidNeighbours++;
				}
				if($this->isSolid($w->getBlockId($cx + 1, $y, $cz))){
					$solidNeighbours++;
				}
				if($this->isSolid($w->getBlockId($cx, $y, $cz - 1))){
					$solidNeighbours++;
				}
				if($this->isSolid($w->getBlockId($cx, $y, $cz + 1))){
					$solidNeighbours++;
				}

				if($solidNeighbours === 1){
					$w->setBlock($cx, $y, $cz, Block::CHEST, 0);
					$this->fillChest($w, $r, $cx, $y, $cz);
					break;
				}
			}
		}

		$w->setBlock($x, $y, $z, Block::MONSTER_SPAWNER, 0);
		if(method_exists($w, "setSpawnerEntity")){
			$entity = $r->nextBoundedInt(4);
			$w->setSpawnerEntity($x, $y, $z, $entity === 0 ? Skeleton::NETWORK_ID : ($entity === 3 ? Spider::NETWORK_ID : Zombie::NETWORK_ID));
		}

		return true;
	}

	/**
	 * Java WeightedRandomChestContent.func_76293_a(random, loot, chest, 8)。
	 */
	private function fillChest(ObjectChunkManager $w, JavaRandom $r, $x, $y, $z){
		if(!method_exists($w, "setChestContents")){
			return;
		}

		$items = [];
		for($i = 0; $i < 8; $i++){
			$entry = $this->pickLoot($r);
			$count = $entry[2] + $r->nextBoundedInt($entry[3] - $entry[2] + 1);
			$items[] = [$r->nextBoundedInt(27), $entry[0], $entry[1], $count];
		}

		$w->setChestContents($x, $y, $z, $items);
	}

	private function pickLoot(JavaRandom $r){
		$total = 0;
		foreach(self::$loot as $entry){
			$total += $entry[4];
		}

		$weight = $r->nextBoundedInt($total);
		foreach(self::$loot as $entry){
			$weight -= $entry[4];
			if($weight < 0){
				return $entry;
			}
		}

		return self::$loot[0];
	}

	private function isSolid($id){
		return $id !== Block::AIR && Block::get($id)->isSolid();
	}
}
