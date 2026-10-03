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

namespace lycore\event\block;

use lycore\block\Block;
use lycore\block\PistonBase;
use lycore\event\Cancellable;

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
