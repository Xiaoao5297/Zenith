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

	protected $mainPath;

	/**
	 * @param int             $port
	 * @param string          $interface
	 *
	 * @throws \Throwable
	 */
	public function __construct($port, $interface = "0.0.0.0"){
		$this->port = (int) $port;
		if($port < 1 or $port > 65536){
			throw new \Exception("Invalid port range");
		}

		$this->interface = $interface;
		$this->shutdown = false;
		$this->mainPath = \Phar::running(true) !== "" ? \Phar::running(true) : \getcwd() . DIRECTORY_SEPARATOR;

		$this->externalQueue = \parallel\Channel::make("rak_ext_" . spl_object_id($this), \parallel\Channel::Infinite);
		$this->internalQueue = \parallel\Channel::make("rak_int_" . spl_object_id($this), \parallel\Channel::Infinite);

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

	/**
	 * @return \parallel\Channel
	 */
	public function getExternalQueue(){
		return $this->externalQueue;
	}

	/**
	 * @return \parallel\Channel
	 */
	public function getInternalQueue(){
		return $this->internalQueue;
	}

	public function pushMainToThreadPacket($str){
		$this->internalQueue->send($str);
	}

	public function readMainToThreadPacket(){
		try{
			return $this->internalQueue->tryRecv();
		}catch(\parallel\Channel\Error\Existence $e){
			return "";
		}catch(\parallel\Channel\Error\Closed $e){
			return "";
		}
	}

	public function pushThreadToMainPacket($str){
		$this->externalQueue->send($str);
	}

	public function readThreadToMainPacket(){
		try{
			return $this->externalQueue->recv();
		}catch(\parallel\Channel\Error\Closed $e){
			return "";
		}
	}

	public function start(int $options = 0){
		$bootstrapPath = \pocketmine\PATH;
		$port = $this->port;
		$interface = $this->interface;
		$mainPath = $this->mainPath;
		$extChanName = $this->externalQueue->getName();
		$intChanName = $this->internalQueue->getName();

		$this->runtime = new \parallel\Runtime();
		$this->future = $this->runtime->run(function($bootstrapPath, $port, $interface, $mainPath, $extChanName, $intChanName){
			require_once $bootstrapPath . "src/spl/ClassLoader.php";
			require_once $bootstrapPath . "src/spl/BaseClassLoader.php";
			require_once $bootstrapPath . "src/pocketmine/CompatibleClassLoader.php";

			$loader = new \CompatibleClassLoader();
			$loader->addPath($bootstrapPath . "src");
			$loader->addPath($bootstrapPath . "src" . DIRECTORY_SEPARATOR . "spl");
			$loader->register(true);

			// Create channel-based logger for the RakLib thread
			$extChan = \parallel\Channel::open($extChanName);
			$intChan = \parallel\Channel::open($intChanName);

			// Create a simple proxy object to handle server communication via channels
			$serverProxy = new class($extChan, $intChan, $mainPath){
				public $externalQueue, $internalQueue;
				public $shutdown = false;
				private $mainPath;

				public function __construct($ext, $int, $mainPath){
					$this->externalQueue = $ext;
					$this->internalQueue = $int;
					$this->mainPath = $mainPath;
				}

				public function pushThreadToMainPacket($str){
					$this->externalQueue->send($str);
				}

				public function readMainToThreadPacket(){
					try{
						return $this->internalQueue->tryRecv();
					}catch(\parallel\Channel\Error\Existence $e){
						return "";
					}catch(\parallel\Channel\Error\Closed $e){
						return "";
					}
				}

				public function pushMainToThreadPacket($str){
					$this->internalQueue->send($str);
				}

				public function readThreadToMainPacket(){
					try{
						return $this->externalQueue->recv();
					}catch(\parallel\Channel\Error\Closed $e){
						return "";
					}
				}

				public function isShutdown(){
					return $this->shutdown;
				}

				public function shutdown(){
					$this->shutdown = true;
				}

				public function getLogger(){
					// Return a simple logger that sends to main thread
					return new class() extends \ThreadedLogger{
						public function log($level, $message){}
						public function emergency($message){}
						public function alert($message){}
						public function critical($message){}
						public function error($message){}
						public function warning($message){}
						public function notice($message){}
						public function info($message){}
						public function debug($message){}
						public function logException(\Throwable $e, $trace = null){}
						public function shutdown(){}
					};
				}

				public function cleanPath($path){
					return rtrim(str_replace(["\\", ".php", "phar://", rtrim(str_replace(["\\", "phar://"], ["/", ""], $this->mainPath), "/")], ["/", "", "", ""], $path), "/");
				}
			};

			$serverProxy->shutdown = false;
			gc_enable();
			error_reporting(-1);
			ini_set("display_errors", 1);
			ini_set("display_startup_errors", 1);

			set_error_handler(function($errno, $errstr, $errfile, $errline) use ($serverProxy){
				if(error_reporting() === 0) return false;
				$errorConversion = [
					E_ERROR => "E_ERROR", E_WARNING => "E_WARNING", E_PARSE => "E_PARSE",
					E_NOTICE => "E_NOTICE", E_CORE_ERROR => "E_CORE_ERROR", E_CORE_WARNING => "E_CORE_WARNING",
					E_COMPILE_ERROR => "E_COMPILE_ERROR", E_COMPILE_WARNING => "E_COMPILE_WARNING",
					E_USER_ERROR => "E_USER_ERROR", E_USER_WARNING => "E_USER_WARNING",
					E_USER_NOTICE => "E_USER_NOTICE", E_STRICT => "E_STRICT",
					E_RECOVERABLE_ERROR => "E_RECOVERABLE_ERROR", E_DEPRECATED => "E_DEPRECATED",
					E_USER_DEPRECATED => "E_USER_DEPRECATED",
				];
				$errnoStr = isset($errorConversion[$errno]) ? $errorConversion[$errno] : $errno;
				if(($pos = strpos($errstr, "\n")) !== false) $errstr = substr($errstr, 0, $pos);
				$errfile = $serverProxy->cleanPath($errfile);
				echo "[RakLib] $errnoStr: \"$errstr\" in \"$errfile\" at line $errline\n";
				return true;
			}, E_ALL);

			register_shutdown_function(function() use ($serverProxy){
				if(!$serverProxy->isShutdown()){
					echo "[RakLib] RakLib crashed!\n";
				}
			});

			$socket = new UDPServerSocket($serverProxy->getLogger(), $port, $interface);
			new SessionManager($serverProxy, $socket);
		}, [$bootstrapPath, $port, $interface, $mainPath, $extChanName, $intChanName]);
	}

	public function shutdownHandler(){
		if($this->shutdown !== true){
			// RakLib crashed
		}
	}

	public function errorHandler($errno, $errstr, $errfile, $errline, $context, $trace = null){
		return true;
	}

	public function getTrace($start = 1, $trace = null){
		return [];
	}

	public function cleanPath($path){
		return $path;
	}

	public function run(){
		// No-op, runs in parallel Runtime
	}

	public function quit(){
		$this->shutdown = true;
		try{
			\parallel\Channel::destroy($this->externalQueue->getName());
		}catch(\Throwable $e){}
		try{
			\parallel\Channel::destroy($this->internalQueue->getName());
		}catch(\Throwable $e){}
		try{
			$this->runtime?->close();
		}catch(\Throwable $e){}
	}

	public function getThreadName(){
		return "RakLib";
	}
}
