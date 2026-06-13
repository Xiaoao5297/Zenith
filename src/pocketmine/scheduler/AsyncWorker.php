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

	/** @var array<int, array{future: \parallel\Future, runtime: \parallel\Runtime}> */
	private $pending = [];

	public function __construct(\ThreadedLogger $logger, $id){
		$this->logger = $logger;
		$this->id = $id;
	}

	/**
	 * Start the worker (no-op in Future-based mode).
	 */
	public function start(int $options = 0){
	}

	/**
	 * Stack a task for execution. Each task gets its own parallel Runtime.
	 */
	public function stack(&$task){
		$bootstrapPath = \pocketmine\PATH;
		$serialized = serialize($task);

		$runtime = new \parallel\Runtime();
		$future = $runtime->run(function($bootstrapPath, $serialized){
			require_once $bootstrapPath . "src/spl/ClassLoader.php";
			require_once $bootstrapPath . "src/spl/BaseClassLoader.php";
			require_once $bootstrapPath . "src/pocketmine/CompatibleClassLoader.php";

			$loader = new \CompatibleClassLoader();
			$loader->addPath($bootstrapPath . "src");
			$loader->addPath($bootstrapPath . "src" . DIRECTORY_SEPARATOR . "spl");
			$loader->register(true);

			gc_enable();
			ini_set("memory_limit", -1);

			global $store;
			$store = [];

			$task = unserialize($serialized);
			if($task instanceof AsyncTask){
				$task->run();
			}
			return serialize($task);
		}, [$bootstrapPath, $serialized]);

		$this->pending[$task->getTaskId()] = [
			'future' => $future,
			'runtime' => $runtime,
		];
	}

	/**
	 * Collect completed results (non-blocking, uses Future::done()).
	 * @return AsyncTask[]
	 */
	public function collectResults() : array{
		$results = [];
		foreach($this->pending as $id => $item){
			try{
				if($item['future']->done()){
					$result = unserialize($item['future']->value());
					$item['runtime']->close();
					if($result instanceof AsyncTask){
						$results[] = $result;
					}
					unset($this->pending[$id]);
				}
			}catch(\Throwable $e){
				unset($this->pending[$id]);
			}
		}
		return $results;
	}

	public function handleException(\Throwable $e){
	}

	public function run(){
	}

	public function quit(){
		foreach($this->pending as $item){
			try{ $item['runtime']?->close(); }catch(\Throwable $e){}
		}
		$this->pending = [];
		parent::quit();
	}

	public function isRunning() : bool{
		return count($this->pending) > 0;
	}

	public function getThreadName(){
		return "Asynchronous Worker #" . $this->id;
	}
}
