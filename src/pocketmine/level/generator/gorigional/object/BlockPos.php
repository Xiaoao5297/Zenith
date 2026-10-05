<?php

namespace pocketmine\level\generator\gorigional\object;

/**
 * 移植自 SCAXE-GO-CE world.BlockPos 的最小实现。
 */
class BlockPos{
	/** @var int */
	public $x;
	/** @var int */
	public $y;
	/** @var int */
	public $z;

	public function __construct($x, $y, $z){
		$this->x = $x;
		$this->y = $y;
		$this->z = $z;
	}

	public function getX(){
		return $this->x;
	}

	public function getY(){
		return $this->y;
	}

	public function getZ(){
		return $this->z;
	}

	public function add($x, $y, $z){
		return new BlockPos($this->x + $x, $this->y + $y, $this->z + $z);
	}

	public function north(){
		return new BlockPos($this->x, $this->y, $this->z - 1);
	}

	public function south(){
		return new BlockPos($this->x, $this->y, $this->z + 1);
	}

	public function west(){
		return new BlockPos($this->x - 1, $this->y, $this->z);
	}

	public function east(){
		return new BlockPos($this->x + 1, $this->y, $this->z);
	}
}
