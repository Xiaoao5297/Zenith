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
namespace lycore\event\inventory;

use lycore\event\Cancellable;
use lycore\event\Event;
use lycore\inventory\Recipe;
use lycore\item\Item;
use lycore\Player;

class CraftItemEvent extends Event implements Cancellable{
	public static $handlerList = null;
	/** @var Item[] */
	private $input = [];
	/** @var Recipe */
	private $recipe;
	/** @var \lycore\Player */
	private $player;

	/**
	 * @param \lycore\Player $player
	 * @param Item[]             $input
	 * @param Recipe             $recipe
	 */
	public function __construct(Player $player, array $input, Recipe $recipe){
		$this->player = $player;
		$this->input = $input;
		$this->recipe = $recipe;
	}

	/**
	 * @return Item[]
	 */
	public function getInput(){
		$items = [];
		foreach($items as $i => $item){
			$items[$i] = clone $item;
		}
		return $items;
	}

	/**
	 * @return Recipe
	 */
	public function getRecipe(){
		return $this->recipe;
	}

	/**
	 * @return \lycore\Player
	 */
	public function getPlayer(){
		return $this->player;
	}
}