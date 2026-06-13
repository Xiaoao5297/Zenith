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

namespace pocketmine;

/**
 * Base class for custom Worker classes.
 * Uses parallel\Runtime and parallel\Channel instead of pthreads Worker.
 */
abstract class Worker extends Thread{

	/**
	 * Stacks a task onto the worker's queue.
	 * Override in subclasses for actual parallel task execution.
	 */
	public function stack(&$task){
		$this->pushMainToThreadPacket($task);
	}

	/**
	 * Removes a task from the worker's queue.
	 */
	public function unstack(){
	}

	/**
	 * Collects finished tasks.
	 */
	public function collector($task){
	}

	/**
	 * Shuts down the worker.
	 */
	public function shutdown(){
		$this->quit();
	}

	public function getThreadName(){
		return "Worker #" . spl_object_id($this);
	}
}
