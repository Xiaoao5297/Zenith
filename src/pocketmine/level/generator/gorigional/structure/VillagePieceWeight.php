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


namespace pocketmine\level\generator\gorigional\structure;

/**
 * 移植自 Java 1.7.10 StructureVillagePieces$PieceWeight。
 */
class VillagePieceWeight{
	/** @var string */
	public $className;
	/** @var int */
	public $weight;
	/** @var int */
	public $instances = 0;
	/** @var int */
	public $limit;

	public function __construct($className, $weight, $limit){
		$this->className = $className;
		$this->weight = $weight;
		$this->limit = $limit;
	}

	public function isValid($size){
		return $this->limit === 0 || $this->instances < $this->limit;
	}

	public function canContinue(){
		return $this->limit === 0 || $this->instances < $this->limit;
	}
}
