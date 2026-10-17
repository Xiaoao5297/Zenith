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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/


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

	public function up($n = 1){
		return new BlockPos($this->x, $this->y + $n, $this->z);
	}

	public function down($n = 1){
		return new BlockPos($this->x, $this->y - $n, $this->z);
	}
}
