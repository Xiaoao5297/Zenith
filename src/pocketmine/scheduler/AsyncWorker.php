<?php

/*
 *
 *  ____            _        _   __  __ _                  __  __ ____
 *  |  _ \ ___   ___| | _____| |_|  \/  (_)_ __   ___      |  \/  |  _ \
 *  | |_) / _ \ / __| |/ / _ \ __| |\/| | | '_ \ / _ \_____| |\/| | |_) |
 *  |  __/ (_) | (__|   <  __/ |_| |  | | | | | |  __/_____| |  | |  __/
 *  |_|   \___/ \___|_|\_\___|\__|_|  |_|_|_| |_|\___|     |_|  |_|_|
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

namespace pocketmine\scheduler;

use pocketmine\Worker;

class AsyncWorker extends Worker{

	private $logger;
	private $id;

	public function __construct(\ThreadedLogger $logger, $id){
		$this->logger = $logger;
		$this->id = $id;
	}

	public function start(int $options = 0){
	}

	public function stack(&$task){
		// Debug: execute synchronously
		$task->run();
		$task->setGarbage();
		$task->cleanObject();
	}

	public function collectResults() : array{
		return [];
	}

	public function handleException(\Throwable $e){
	}

	public function run(){
	}

	public function quit(){
	}

	public function isRunning() : bool{
		return false;
	}

	public function getThreadName(){
		return "Asynchronous Worker #" . $this->id;
	}
}
