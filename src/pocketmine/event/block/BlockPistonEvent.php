<?php

/*
 * ██╗███╗   ██╗ ██████╗ ██████╗ ██████╗ ███████╗
 * ██║████╗  ██║██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║██╔██╗ ██║██║     ██║   ██║██████╔╝█████╗
 * ██║██║╚██╗██║██║     ██║   ██║██╔══██╗██╔══╝
 * ██║██║ ╚████║╚██████╗╚██████╔╝██║  ██║███████╗
 * ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ported to Zenith from InCore-II (B1 活塞体系移植)；上游 LGPLv3 头部原样保留。
 *
 * @Author: The_Bird, GoodCat456872
 *
 * @Links:
 *  > https://github.com/QwQ12222/InCore-II#incore-ii
 *  > https://gitcode.com/bad-cat-factory/InCore-II-InCore/tree/InCore-II
*/

namespace pocketmine\event\block;

use pocketmine\block\Block;
use pocketmine\block\PistonBase;
use pocketmine\event\Cancellable;

class BlockPistonEvent extends BlockEvent implements Cancellable{
	public static $handlerList = null;

	/** @var int */
	protected $direction;
	/** @var Block[] */
	protected $blocks;
	/** @var Block[] */
	protected $destroyedBlocks;
	/** @var bool */
	protected $extending;

	public function __construct(PistonBase $piston, $direction, array $blocks, array $destroyedBlocks, $extending){
		parent::__construct($piston);
		$this->direction = (int) $direction;
		$this->blocks = $blocks;
		$this->destroyedBlocks = $destroyedBlocks;
		$this->extending = (bool) $extending;
	}

	/**
	 * @return PistonBase
	 */
	public function getBlock(){
		return $this->block;
	}

	public function getDirection(){
		return $this->direction;
	}

	/**
	 * @return Block[]
	 */
	public function getBlocks(){
		return $this->blocks;
	}

	/**
	 * @return Block[]
	 */
	public function getDestroyedBlocks(){
		return $this->destroyedBlocks;
	}

	public function isExtending(){
		return $this->extending;
	}
}
