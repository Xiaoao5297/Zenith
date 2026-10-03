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
 
namespace synapse\event\player;

use synapse\Player;

class PlayerConnectEvent extends PlayerEvent{
	public static $handlerList = null;

	/** @var bool */
	private $firstTime;

	public function __construct(Player $player, bool $firstTime = true){
		$this->player = $player;
		$this->firstTime = $firstTime;
	}

	/**
	 * Gets if the player is first time login
	 *
	 * @return bool
	 */
	public function isFirstTime() : bool{
		return $this->firstTime;
	}
}