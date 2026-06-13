<?php

/*
 *
 *    ____ _                   _
 *  / ___| | _____      _____| |_ ___  _ __   ___
 * | |  _| |/ _ \ \ /\ / / __| __/ _ \| '_ \ / _ \
 * | |_| | | (_) \ V  V /\__ \ || (_) | | | |  __/
 *  \____|_|\___/ \_/\_/ |___/\__\___/|_| |_|\___|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Glowstone (Lemdy)
 * @link vk.com/weany
 *
 */

namespace raklib\server;

use pocketmine\Thread;

class RakLibServer extends Thread{
	protected $port;
	protected $interface;
	protected $logger;
	protected $loader;

	public $loadPaths;

	protected $shutdown;

	/** @var \parallel\Channel */
	protected $externalQueue;
	/** @var \parallel\Channel */
	protected $internalQueue;
	/** @var string */
	protected $extChanName = "";
	/** @var string */
	protected $intChanName = "";
	/** @var resource|null */
	protected $dataSocket = null;

	protected $mainPath;

	public function __construct($port, $interface = "0.0.0.0"){
		$this->port = (int) $port;
		if($port < 1 or $port > 65536){
			throw new \Exception("Invalid port range");
		}

		$this->interface = $interface;
		$this->shutdown = false;
		$this->mainPath = \Phar::running(true) !== "" ? \Phar::running(true) : \getcwd() . DIRECTORY_SEPARATOR;

		$id = spl_object_id($this);
		$this->intChanName = "rak_int_{$id}";
		$this->internalQueue = \parallel\Channel::make($this->intChanName, \parallel\Channel::Infinite);

		// Debug: skip Runtime creation to isolate memory corruption
	}

	public function isShutdown(){
		return $this->shutdown === true;
	}

	public function shutdown(){
		$this->shutdown = true;
	}

	public function getPort(){
		return $this->port;
	}

	public function getInterface(){
		return $this->interface;
	}

	public function getExternalQueue(){
		return $this->externalQueue;
	}

	public function getInternalQueue(){
		return $this->internalQueue;
	}

	public function pushMainToThreadPacket($str){
		$this->internalQueue->send($str);
	}

	public function readMainToThreadPacket(){
		try{
			return $this->internalQueue->recv();
		}catch(\parallel\Channel\Error\Closed $e){
			return "";
		}
	}

	public function pushThreadToMainPacket($str){
		$this->externalQueue->send($str);
	}

	public function start(int $options = 0){
		// Debug: Runtime creation disabled to isolate memory corruption
		return false;
	}

	public function readThreadToMainPacket(){
		return "";
	}

	public function run(){
	}

	public function quit(){
		$this->shutdown = true;
		try{
			if($this->dataSocket){
				@fclose($this->dataSocket);
				$this->dataSocket = null;
			}
		}catch(\Throwable $e){}
		try{
			\parallel\Channel::destroy($this->intChanName);
		}catch(\Throwable $e){}
		try{
			$this->runtime?->close();
		}catch(\Throwable $e){}
	}

	public function getThreadName(){
		return "RakLib";
	}
}
