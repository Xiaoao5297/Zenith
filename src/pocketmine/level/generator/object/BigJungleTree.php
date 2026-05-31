<?php

namespace pocketmine\level\generator\object;

use pocketmine\block\Block;
use pocketmine\block\Wood;
use pocketmine\level\ChunkManager;
use pocketmine\utils\Random;
use pocketmine\math\Facing;

class BigJungleTree extends Tree{

	public function __construct(){
		$this->trunkBlock = Block::LOG;
		$this->leafBlock = Block::LEAVES;
		$this->type = Wood::JUNGLE;
	}
	public function canPlaceOn(Block $soil) : bool{
		$id = $soil->getId();
		return $id === Block::GRASS || $id === Block::DIRT;
	}

	public function canPlace(int $base_x, int $base_y, int $base_z, ChunkManager $world) : bool{
		for($y = $base_y; $y <= $base_y + 1 + $this->treeHeight; ++$y){
			// Space requirement
			$radius = 2; // default radius if above first block
			if($y === $base_y){
				$radius = 1; // radius at source block y is 1 (only trunk)
			}elseif($y >= $base_y + 1 + $this->treeHeight - 2){
				$radius = 2; // max radius starting at leaves bottom
			}
			// check for block collision on horizontal slices
			for($x = $base_x - $radius; $x <= $base_x + $radius; ++$x){
				for($z = $base_z - $radius; $z <= $base_z + $radius; ++$z){
					if($y >= 0 && $y < World::Y_MAX){
						// we can overlap some blocks around
						if(!array_key_exists($world->getBlockAt($x, $y, $z)->getId(), $this->overridables)){
							return false;
						}
					}else{ // height out of range
						return false;
					}
				}
			}
		}
		return true;
	}

	public function placeObject(ChunkManager $world, $source_x, $source_y, $source_z, Random $random){
		$this->treeHeight = $random->nextBoundedInt(20) + $random->nextBoundedInt(3) + 10;
		
		// generates the canopy leaves
		for($y = -2; $y <= 0; ++$y){
			$this->generateLeaves($source_x + 0, $source_y + $this->treeHeight + $y, $source_z, 3 - $y, false, $world);
		}

		// generates the branches
		$branch_height = $this->treeHeight - 2 - $random->nextBoundedInt(4);
		$height_half = intdiv($this->treeHeight, 2);
		while($branch_height > $height_half){ // branching start at least at middle height
			$x = 0;
			$z = 0;
			// generates a branch
			$d = $random->nextFloat() * M_PI * 2.0; // random direction
			for($i = 0; $i < 5; ++$i){
				// branches are always longer when facing south or east (positive X or positive Z)
				$x = (int) (cos($d) * $i + 1.5);
				$z = (int) (sin($d) * $i + 1.5);
				$world->setBlockIdAt($source_x + $x, $source_y + $branch_height - 3 + intdiv($i, 2), $source_z + $z, $this->trunkBlock);
				$world->setBlockDataAt($source_x + $x, $source_y + $branch_height - 3 + intdiv($i, 2), $source_z + $z, $this->type);
			}
			// generates leaves for this branch
			for($y = $branch_height - ($random->nextBoundedInt(2) + 1); $y <= $branch_height; ++$y){
				$this->generateLeaves($source_x + $x, $source_y + $y, $source_z + $z, 1 - ($y - $branch_height), true, $world);
			}
			$branch_height -= $random->nextBoundedInt(4) + 2;
		}

		// generates the trunk
		$this->generateTrunk($world, $source_x, $source_y, $source_z);

		// add some vines on the trunk
		$this->addVinesOnTrunk($world, $source_x, $source_y, $source_z, $random);

		// blocks below trunk are always dirt
		$this->generateDirtBelowTrunk($source_x, $source_y, $source_z, $world);
		return true;
	}

	protected function generateLeaves(int $source_x, int $source_y, int $source_z, int $radius, bool $odd, ChunkManager $world) : void{
		$n = 1;
		if($odd){
			$n = 0;
		}
		for($x = $source_x - $radius; $x <= $source_x + $radius + $n; ++$x){
			$radius_x = $x - $source_x;
			for($z = $source_z - $radius; $z <= $source_z + $radius + $n; ++$z){
				$radius_z = $z - $source_z;

				$sq_x = $radius_x * $radius_x;
				$sq_z = $radius_z * $radius_z;
				$sq_r = $radius * $radius;
				$sq_xb = ($radius_x - $n) * ($radius_x - $n);
				$sq_zb = ($radius_z - $n) * ($radius_z - $n);

				if($sq_x + $sq_z <= $sq_r || $sq_xb + $sq_zb <= $sq_r || $sq_x + $sq_zb <= $sq_r || $sq_xb + $sq_z <= $sq_r){
					$this->replaceIfAirOrLeaves($x, $source_y, $z, $this->leafBlock, $world);
				}
			}
		}
	}
	
	protected function replaceIfAirOrLeaves(int $x, int $y, int $z,  $new_material, ChunkManager $world) : void{
		$old_material = $world->getBlockIdAt($x, $y, $z);
		if($old_material === Block::AIR || $old_material === Block::LEAVES){
			$world->setBlockIdAt($x, $y, $z, $new_material);
			$world->setBlockDataAt($x, $y, $z, $this->type);
		}
	}

	protected function generateTrunk(ChunkManager $world, int $block_x, int $block_y, int $block_z) : void{
		// SELF, SOUTH, EAST, SOUTH EAST
		for($y = 0; $y < $this->treeHeight + -1; ++$y){
			$type = $world->getBlockIdAt($block_x + 0, $block_y + $y, $block_z + 0);
			if($type === Block::AIR || $type === Block::LEAVES){
				$world->setBlockIdAt($block_x + 0, $block_y + $y, $block_z, $this->trunkBlock);
				$world->setBlockDataAt($block_x + 0, $block_y + $y, $block_z, $this->type);
			}
			$type = $world->getBlockIdAt($block_x + 0, $block_y + $y, $block_z + 1);
			if($type === Block::AIR || $type === Block::LEAVES){
				$world->setBlockIdAt($block_x + 0, $block_y + $y, $block_z + 1, $this->trunkBlock);
				$world->setBlockDataAt($block_x + 0, $block_y + $y, $block_z + 1, $this->type);
			}
			$type = $world->getBlockIdAt($block_x + 1, $block_y + $y, $block_z + 0);
			if($type === Block::AIR || $type === Block::LEAVES){
				$world->setBlockIdAt($block_x + 1, $block_y + $y, $block_z, $this->trunkBlock);
				$world->setBlockDataAt($block_x + 1, $block_y + $y, $block_z, $this->type);
			}
			$type = $world->getBlockIdAt($block_x + 1, $block_y + $y, $block_z + 1);
			if($type === Block::AIR || $type === Block::LEAVES){
				$world->setBlockIdAt($block_x + 1, $block_y + $y, $block_z + 1, $this->trunkBlock);
				$world->setBlockDataAt($block_x + 1, $block_y + $y, $block_z + 1, $this->type);
			}
		}
	}

	protected function generateDirtBelowTrunk(int $block_x, int $block_y, int $block_z, ChunkManager $world) : void{
		// SELF, SOUTH, EAST, SOUTH EAST
		$dirt = Block::DIRT;
		$world->setBlockIdAt($block_x + 0, $block_y + -1, $block_z, $dirt);
		$world->setBlockIdAt($block_x + 0, $block_y + -1, $block_z + 1, $dirt);
		$world->setBlockIdAt($block_x + 1, $block_y + -1, $block_z, $dirt);
		$world->setBlockIdAt($block_x + 1, $block_y + -1, $block_z + 1, $dirt);
	}

	private function addVinesOnTrunk(ChunkManager $world, int $block_x, int $block_y, int $block_z, Random $random) : void{
		for($y = 1; $y < $this->treeHeight; ++$y){
			$this->maybePlaceVine($world, $block_x + -1, $block_y + $y, $block_z + 0, Facing::WEST, $random);
			$this->maybePlaceVine($world, $block_x + 0, $block_y + $y, $block_z + -1, Facing::NORTH, $random);
			$this->maybePlaceVine($world, $block_x + 2, $block_y + $y, $block_z + 0, Facing::EAST, $random);
			$this->maybePlaceVine($world, $block_x + 1, $block_y + $y, $block_z + -1, Facing::NORTH, $random);
			$this->maybePlaceVine($world, $block_x + 2, $block_y + $y, $block_z + 1, Facing::EAST, $random);
			$this->maybePlaceVine($world, $block_x + 1, $block_y + $y, $block_z + 2, Facing::SOUTH, $random);
			$this->maybePlaceVine($world, $block_x + -1, $block_y + $y, $block_z + 1, Facing::WEST, $random);
			$this->maybePlaceVine($world, $block_x + 0, $block_y + $y, $block_z + 2, Facing::SOUTH, $random);
		}
	}

	private function maybePlaceVine(ChunkManager $world, int $absolute_x, int $absolute_y, int $absolute_z, int $face_direction, Random $random) : void{
		
			$faces = [
				0 => 0,
				1 => 0,
				2 => 1,
				3 => 4,
				4 => 8,
				5 => 2,
			];
			
		if(
			$random->nextBoundedInt(3) !== 0 &&
			$world->getBlockIdAt($absolute_x, $absolute_y, $absolute_z) === Block::AIR
		){
			$world->setBlockIdAt($absolute_x, $absolute_y, $absolute_z, Block::VINE);
			$world->setBlockDataAt($absolute_x, $absolute_y, $absolute_z, $faces[$face_direction]);
		}
	}

}