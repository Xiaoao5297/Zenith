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

	/**
	 * 只用 sleep + kill, 不需要父进程的类加载器.
	 * 保存 Server 的 loader 会让 pthreads 在 start() 时序列化整个 loader 链,
	 * 在部分插件环境下抛 "Serialization of 'Closure' is not allowed",
	 * 导致关服最后一步异常退出.
	 */
	public function setClassLoader(\ClassLoader $loader = null){
	}

	public function run(){
		$start = time() + 1;
		$this->synchronized(function(){
			$this->wait($this->time * 1000000);
		});
		if(time() - $start >= $this->time){
			echo "\nTook too long to stop, server was killed forcefully!\n";
			@\pocketmine\kill(getmypid());
		}
	}

	public function getThreadName(){
		return "Server Killer";
	}
}
