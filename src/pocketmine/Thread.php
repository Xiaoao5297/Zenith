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
 * Base class for all custom threading classes.
 * Uses parallel\Runtime and parallel\Channel instead of pthreads.
 */
abstract class Thread{

	/** @var \ClassLoader */
	protected $classLoader;
	protected $isKilled = false;

	/** @var \parallel\Runtime|null */
	private $runtime = null;
	/** @var \parallel\Future|null */
	protected $future = null;

	/** @var \parallel\Channel|null  Communication: main -> thread */
	private $inChan = null;
	/** @var \parallel\Channel|null  Communication: thread -> main */
	private $outChan = null;

	public function getClassLoader(){
		return $this->classLoader;
	}

	public function setClassLoader(\ClassLoader $loader = null){
		if($loader === null){
			$loader = Server::getInstance()->getLoader();
		}
		$this->classLoader = $loader;
	}

	public function registerClassLoader(){
		if(!interface_exists("ClassLoader", false)){
			require(\pocketmine\PATH . "src/spl/ClassLoader.php");
			require(\pocketmine\PATH . "src/spl/BaseClassLoader.php");
			require(\pocketmine\PATH . "src/pocketmine/CompatibleClassLoader.php");
		}
		if($this->classLoader !== null){
			$this->classLoader->register(true);
		}
	}

	/**
	 * Start the thread in a parallel Runtime.
	 * Subclasses should call parent::start() or implement their own parallel bootstrap.
	 */
	public function start(){
		ThreadManager::getInstance()->add($this);

		if($this->isRunning()){
			return false;
		}

		$id = spl_object_id($this);
		$inName = "thr_in_{$id}";
		$outName = "thr_out_{$id}";

		$this->inChan = \parallel\Channel::make($inName, \parallel\Channel::Infinite);
		$this->outChan = \parallel\Channel::make($outName, \parallel\Channel::Infinite);

		if($this->getClassLoader() === null){
			$this->setClassLoader();
		}

		$className = get_class($this);
		$threadData = serialize($this->getThreadData());
		$bootstrapPath = \pocketmine\PATH;

		$this->runtime = new \parallel\Runtime();
		$this->future = $this->runtime->run(function($className, $threadData, $bootstrapPath, $inName, $outName){
			require_once $bootstrapPath . "src/spl/ClassLoader.php";
			require_once $bootstrapPath . "src/spl/BaseClassLoader.php";
			require_once $bootstrapPath . "src/pocketmine/CompatibleClassLoader.php";

			$loader = new CompatibleClassLoader();
			$loader->addPath($bootstrapPath . "src");
			$loader->addPath($bootstrapPath . "src" . DIRECTORY_SEPARATOR . "spl");
			$loader->register(true);

			$data = unserialize($threadData);
			$inChan = \parallel\Channel::open($inName);
			$outChan = \parallel\Channel::open($outName);

			$ref = new \ReflectionClass($className);
			$instance = $ref->newInstanceWithoutConstructor();

			// Inject channel references via reflection
			$setChan = function($chan, $name) use ($ref, $instance){
				$prop = $ref->getProperty($name);
				$prop->setAccessible(true);
				$prop->setValue($instance, $chan);
			};
			$setChan($inChan, "inChan");
			$setChan($outChan, "outChan");

			// Restore serialized properties
			foreach($data as $prop => $value){
				if($ref->hasProperty($prop) and $prop !== "runtime" and $prop !== "future" and $prop !== "inChan" and $prop !== "outChan"){
					$p = $ref->getProperty($prop);
					$p->setAccessible(true);
					$p->setValue($instance, $value);
				}
			}

			// Run any custom initialization
			if(method_exists($instance, "internalInit")){
				$instance->internalInit($data);
			}

			$instance->run();
		}, [$className, $threadData, $bootstrapPath, $inName, $outName]);

		return true;
	}

	/**
	 * Returns data to be serialized and passed to the thread.
	 * Override in subclasses to include constructor arguments.
	 */
	protected function getThreadData() : array{
		return [];
	}

	/**
	 * Called inside the thread after the object is reconstructed.
	 * Override to perform custom initialization from serialized data.
	 */
	protected function internalInit(array $data) : void{
	}

	/**
	 * Send data FROM the main thread TO this thread.
	 */
	public function pushMainToThreadPacket($str){
		$this->inChan?->send($str);
	}

	/**
	 * Read data sent FROM this thread back to the main thread (blocking).
	 */
	public function readThreadToMainPacket(){
		try{
			return $this->outChan?->recv();
		}catch(\parallel\Channel\Error\Closed $e){
			return null;
		}
	}

	/**
	 * Called by the thread to read data FROM the main thread (blocking).
	 */
	protected function readMainToThreadPacket(){
		try{
			return $this->inChan?->recv();
		}catch(\parallel\Channel\Error\Closed $e){
			return null;
		}
	}

	/**
	 * Called by the thread to send data TO the main thread.
	 */
	protected function pushThreadToMainPacket($str){
		$this->outChan?->send($str);
	}

	public function isRunning() : bool{
		return $this->future !== null and !$this->future->done();
	}

	public function isJoined() : bool{
		return $this->future === null || $this->future->done();
	}

	public function isTerminated() : bool{
		return $this->runtime === null || $this->future === null;
	}

	/**
	 * Stop the thread.
	 */
	public function quit(){
		$this->isKilled = true;

		// Close channels to unblock recv()
		try{
			if($this->inChan !== null){
				$name = $this->inChan->getName();
				\parallel\Channel::destroy($name);
				$this->inChan = null;
			}
		}catch(\Throwable $e){
		}

		try{
			if($this->outChan !== null){
				$name = $this->outChan->getName();
				\parallel\Channel::destroy($name);
				$this->outChan = null;
			}
		}catch(\Throwable $e){
		}

		// Close runtime
		try{
			$this->runtime?->close();
		}catch(\Throwable $e){
		}

		$this->runtime = null;
		$this->future = null;

		ThreadManager::getInstance()->remove($this);
	}

	public function getThreadName(){
		return (new \ReflectionClass($this))->getShortName();
	}
}
