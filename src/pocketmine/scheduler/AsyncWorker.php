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

namespace pocketmine\scheduler;

use pocketmine\Worker;

class AsyncWorker extends Worker{

	private $logger;
	private $id;

	/** @var \parallel\Runtime|null */
	private $workerRuntime = null;
	/** @var \parallel\Channel|null */
	private $taskChan = null;
	/** @var \parallel\Channel|null */
	private $resultChan = null;

	public function __construct(\ThreadedLogger $logger, $id){
		$this->logger = $logger;
		$this->id = $id;
	}

	/**
	 * Start the parallel worker runtime and the worker loop.
	 */
	public function start(int $options = 0){
		\pocketmine\ThreadManager::getInstance()->add($this);

		$id = spl_object_id($this);
		$taskChanName = "aw_task_{$id}";
		$resultChanName = "aw_result_{$id}";

		$this->taskChan = \parallel\Channel::make($taskChanName, \parallel\Channel::Infinite);
		$this->resultChan = \parallel\Channel::make($resultChanName, \parallel\Channel::Infinite);

		$this->workerRuntime = new \parallel\Runtime();

		$bootstrapPath = \pocketmine\PATH;

		$this->future = $this->workerRuntime->run(function($bootstrapPath, $taskChanName, $resultChanName){
			require_once $bootstrapPath . "src/spl/ClassLoader.php";
			require_once $bootstrapPath . "src/spl/BaseClassLoader.php";
			require_once $bootstrapPath . "src/pocketmine/CompatibleClassLoader.php";

			$loader = new CompatibleClassLoader();
			$loader->addPath($bootstrapPath . "src");
			$loader->addPath($bootstrapPath . "src" . DIRECTORY_SEPARATOR . "spl");
			$loader->register(true);

			gc_enable();
			ini_set("memory_limit", -1);

			global $store;
			$store = [];

			$taskChan = \parallel\Channel::open($taskChanName);
			$resultChan = \parallel\Channel::open($resultChanName);

			while(true){
				try{
					$task = $taskChan->recv();
				}catch(\parallel\Channel\Error\Closed $e){
					break;
				}

				if($task === null){
					break;
				}

				$taskObj = null;
				if(is_string($task)){
					$taskObj = unserialize($task);
				}

				if($taskObj instanceof AsyncTask){
					try{
						$taskObj->run();
					}catch(\Throwable $e){
						// Exception handled by the task itself
					}
					$resultChan->send(serialize($taskObj));
				}
			}
		}, [$bootstrapPath, $taskChanName, $resultChanName]);
	}

	/**
	 * Stack a task onto the worker queue for execution.
	 */
	public function stack(&$task){
		$this->taskChan?->send(serialize($task));
	}

	/**
	 * Collect completed tasks from the result channel (non-blocking).
	 * @return AsyncTask[] Completed task objects
	 */
	public function collectResults() : array{
		$results = [];
		if($this->resultChan === null){
			return $results;
		}
		while(true){
			try{
				$result = $this->resultChan->tryRecv();
			}catch(\parallel\Channel\Error\Existence $e){
				break; // No data available, non-blocking
			}catch(\parallel\Channel\Error\Closed $e){
				break;
			}
			if($result === null){
				break;
			}
			$taskObj = unserialize($result);
			if($taskObj instanceof AsyncTask){
				$results[] = $taskObj;
			}
		}
		return $results;
	}

	public function handleException(\Throwable $e){
	}

	public function run(){
		// No-op
	}

	public function quit(){
		try{
			$this->taskChan?->send(null);
			unset($this->taskChan);
		}catch(\Throwable $e){
		}
		try{
			unset($this->resultChan);
		}catch(\Throwable $e){
		}
		try{
			$this->workerRuntime?->close();
		}catch(\Throwable $e){
		}
		parent::quit();
	}

	public function isRunning() : bool{
		return $this->future !== null and !$this->future->done();
	}

	public function getThreadName(){
		return "Asynchronous Worker #" . $this->id;
	}
}
