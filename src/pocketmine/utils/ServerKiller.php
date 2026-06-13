<?php

/*
 *
 *  ____            _        _   __  __ _                  __  __ ____
 * |  _ \ ___   ___| | _____| |_|  \/  (_)_ __   ___      |  \/  |  _ \
 * | |_) / _ \ / __| |/ / _ \ __| |\/| | | '_ \ / _ \_____| |\/| | |_) |
 * |  __/ (_) | (__|   <  __/ |_| |  | | | | | |  __/_____| |  | |  __/
 * |_|   \___/ \___|_|\_\___|\__|_|  |_|_|_| |_|\___|     |_|  |_|_|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author PocketMine Team
 * @link http://www.pocketmine.net/
 *
 *
*/

namespace pocketmine\utils;

use pocketmine\Thread;

class ServerKiller extends Thread{

	public $time;

	public function __construct($time = 15){
		$this->time = $time;
	}

	public function start(int $options = 0){
		$time = $this->time;
		$this->future = \parallel\run(function() use ($time){
			$start = time() + 1;
			$sleepTime = $time * 1000000;
			usleep($sleepTime);
			if(time() - $start >= $time){
				echo "\nTook too long to stop, server was killed forcefully!\n";
				@\pocketmine\kill(getmypid());
			}
		});
	}

	public function run(){
		// No-op, executed via parallel\run in start()
	}

	public function quit(){
		// The future will complete on its own
	}

	public function getThreadName(){
		return "Server Killer";
	}
}
