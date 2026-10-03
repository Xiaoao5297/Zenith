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

namespace lycore\event\player;

use lycore\block\Block;
use lycore\event\Cancellable;
use lycore\item\Item;
use lycore\level\Position;
use lycore\math\Vector3;

/**
 * Called when a player interacts or touches a block (including air?)
 */
class PlayerInteractEvent extends PlayerEvent implements Cancellable{
	public static $handlerList = null;

	const LEFT_CLICK_BLOCK = 0;
	const RIGHT_CLICK_BLOCK = 1;
	const LEFT_CLICK_AIR = 2;
	const RIGHT_CLICK_AIR = 3;
	const PHYSICAL = 4;

	/**
	 * @var \lycore\block\Block;
	 */
	protected $blockTouched;

	protected $touchVector;

	/** @var int */
	protected $blockFace;

	/** @var \lycore\item\Item */
	protected $item;
	
	protected $action;

	public function __construct($player, Item $item, Vector3 $block, $face, $action = PlayerInteractEvent::RIGHT_CLICK_BLOCK){
		if($block instanceof Block){
			$this->blockTouched = $block;
			$this->touchVector = new Vector3(0, 0, 0);
		}else{
			$this->touchVector = $block;
			$this->blockTouched = Block::get(0, 0, new Position(0, 0, 0, $player->level));
		}
		$this->player = $player;
		$this->item = $item;
		$this->blockFace = (int) $face;
		$this->action = (int) $action;
	}

	/**
	 * @return int
	 */
	public function getAction(){
		return $this->action;
	}

	/**
	 * @return Item
	 */
	public function getItem(){
		return $this->item;
	}

	/**
	 * @return Block
	 */
	public function getBlock(){
		return $this->blockTouched;
	}

	/**
	 * @return Vector3
	 */
	public function getTouchVector(){
		return $this->touchVector;
	}

	/**
	 * @return int
	 */
	public function getFace(){
		return $this->blockFace;
	}
}
