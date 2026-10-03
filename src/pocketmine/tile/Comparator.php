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

/*
 * 移植自 lycore\tile\Comparator，命名空间改为 pocketmine\tile。
 * 当前核心 Tile 没有 isBlockEntityValid()，故移除该方法。
 */

namespace pocketmine\tile;

use pocketmine\level\format\FullChunk;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\IntTag;

class Comparator extends Tile{
	private $outputSignal = 0;

	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		if(!isset($nbt->OutputSignal)){
			$nbt->OutputSignal = new IntTag("OutputSignal", 0);
		}
		parent::__construct($chunk, $nbt);
		$this->outputSignal = (int) $this->namedtag["OutputSignal"];
	}

	public function getOutputSignal() : int{
		return $this->outputSignal;
	}

	public function setOutputSignal(int $outputSignal){
		$this->outputSignal = max(0, min(15, $outputSignal));
		$this->namedtag->OutputSignal = new IntTag("OutputSignal", $this->outputSignal);
	}

	public function saveNBT(){
		parent::saveNBT();
		$this->namedtag->OutputSignal = new IntTag("OutputSignal", $this->outputSignal);
	}
}
