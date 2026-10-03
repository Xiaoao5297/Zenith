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

use lycore\event\Cancellable;
use lycore\Player;

class PlayerExperienceChangeEvent extends PlayerEvent implements Cancellable{
	const ADD_EXPERIENCE = 0;
	const SET_EXPERIENCE = 1;
	
	public static $handlerList = null;
	
	public $exp;
	public $expLevel;
	public $action;

	public function __construct(Player $player, $exp, $expLevel, $action = PlayerExperienceChangeEvent::SET_EXPERIENCE){
		$this->exp = $exp;
		$this->expLevel = $expLevel;
		$this->action = $action;
	}
	
	public function getAction(){
		return $this->action;
	}
	
	public function getExp(){
		return $this->exp;
	}
	
	public function getExpLevel(){
		return $this->expLevel;
	}
	
	public function setExp($exp){
		$this->exp = $exp;
	}
	
	public function setExpLevel($level){
		$this->expLevel = $level;
	}

}
