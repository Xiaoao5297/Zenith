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

		$this->start();
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
		$bootstrapPath = \pocketmine\PATH;
		$port = $this->port;
		$interface = $this->interface;
		$mainPath = $this->mainPath;
		$intChanName = $this->intChanName;

		// Create socket pair for non-blocking data channel (RakLib -> main)
		$sockets = @stream_socket_pair(STREAM_PF_UNIX, STREAM_SOCK_STREAM, STREAM_IPPROTO_IP);
		if($sockets === false){
			echo "[RakLib] Failed to create socket pair\n";
			return false;
		}
		$dataSocket = $sockets[0];  // main thread side
		$proxySocket = $sockets[1]; // proxy side (sent to Runtime)
		stream_set_blocking($dataSocket, false);
		stream_set_blocking($proxySocket, false);

		$this->runtime = new \parallel\Runtime();
		$this->future = $this->runtime->run(function($bootstrapPath, $port, $interface, $mainPath, $intChanName, $proxySocket){
			require_once $bootstrapPath . "src/spl/ClassLoader.php";
			require_once $bootstrapPath . "src/spl/BaseClassLoader.php";
			require_once $bootstrapPath . "src/pocketmine/CompatibleClassLoader.php";
			require_once $bootstrapPath . "src/raklib/server/RakLibDummyLogger.php";
			require_once $bootstrapPath . "src/raklib/server/RakLibProxy.php";

			$loader = new \CompatibleClassLoader();
			$loader->addPath($bootstrapPath . "src");
			$loader->addPath($bootstrapPath . "src" . DIRECTORY_SEPARATOR . "spl");
			$loader->register(true);

			$intChan = \parallel\Channel::open($intChanName);

			$proxy = new RakLibProxy($intChan, $proxySocket, $mainPath);
			$proxy->shutdown = false;

			gc_enable();
			error_reporting(-1);
			ini_set("display_errors", 1);
			ini_set("display_startup_errors", 1);

			set_error_handler(function($errno, $errstr, $errfile, $errline) use ($proxy){
				if(error_reporting() === 0) return false;
				$errfile = $proxy->cleanPath($errfile);
				echo "[RakLib] Error: \"$errstr\" in \"$errfile\" at line $errline\n";
				return true;
			}, E_ALL);

			register_shutdown_function(function() use ($proxy){
				if(!$proxy->isShutdown()){
					echo "[RakLib] RakLib crashed!\n";
				}
			});

			$socket = new UDPServerSocket($proxy->getLogger(), $port, $interface);
			new SessionManager($proxy, $socket);
		}, [$bootstrapPath, $port, $interface, $mainPath, $intChanName, $proxySocket]);

		$this->dataSocket = $dataSocket;

		return true;
	}

	public function readThreadToMainPacket(){
		if(!isset($this->dataSocket) or $this->dataSocket === null){
			return "";
		}
		// Non-blocking read from data socket
		$r = [$this->dataSocket];
		$w = null;
		$e = null;
		if(stream_select($r, $w, $e, 0, 0) > 0){
			$header = @fread($this->dataSocket, 4);
			if($header === false or strlen($header) < 4){
				return "";
			}
			$len = unpack("N", $header)[1];
			$data = "";
			while(strlen($data) < $len){
				$chunk = @fread($this->dataSocket, $len - strlen($data));
				if($chunk === false or $chunk === "") break;
				$data .= $chunk;
			}
			return $data;
		}
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
