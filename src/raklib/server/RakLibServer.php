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

	protected $mainPath;

	/** @var resource|null */
	private $process = null;
	/** @var resource|null */
	private $stdoutPipe = null;

	public function __construct($port, $interface = "0.0.0.0"){
		$this->port = (int) $port;
		if($port < 1 or $port > 65536){
			throw new \Exception("Invalid port range");
		}

		$this->interface = $interface;
		$this->shutdown = false;
		$this->mainPath = \Phar::running(true) !== "" ? \Phar::running(true) : \getcwd() . DIRECTORY_SEPARATOR;

		$id = spl_object_id($this);
		$this->extChanName = "rak_ext_{$id}";
		$this->intChanName = "rak_int_{$id}";
		$this->externalQueue = \parallel\Channel::make($this->extChanName, \parallel\Channel::Infinite);
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
		$phpBinary = PHP_BINARY;
		$bootstrapPath = \pocketmine\PATH;
		$bootstrapFile = $bootstrapPath . "src/raklib/server/raklib_bootstrap.php";

		$env = [
			"RAKLIB_BOOTSTRAP_PATH" => $bootstrapPath,
			"RAKLIB_PORT" => (string) $this->port,
			"RAKLIB_INTERFACE" => $this->interface,
			"RAKLIB_MAIN_PATH" => $this->mainPath,
			"RAKLIB_INT_CHAN" => $this->intChanName,
			"RAKLIB_EXT_CHAN" => $this->extChanName,
		];

		$descriptorspec = [
			0 => ["pipe", "r"],  // stdin
			1 => ["pipe", "w"],  // stdout - RakLib output
			2 => ["pipe", "w"],  // stderr
		];

		$this->process = @proc_open(
			$phpBinary . " " . escapeshellarg($bootstrapFile),
			$descriptorspec,
			$pipes,
			null,
			$env
		);

		if($this->process === false){
			echo "[RakLib] Failed to start RakLib process\n";
			return false;
		}

		// Close stdin (we use channel for communication)
		fclose($pipes[0]);
		// Store stdout pipe for reading RakLib output
		$this->stdoutPipe = $pipes[1];
		stream_set_blocking($this->stdoutPipe, false);
		// Close stderr
		fclose($pipes[2]);

		return true;
	}

	public function readThreadToMainPacket(){
		if($this->stdoutPipe === null){
			return "";
		}
		// Non-blocking read from stdout pipe
		$r = [$this->stdoutPipe];
		$w = null;
		$e = null;
		if(@stream_select($r, $w, $e, 0, 0) > 0){
			$header = @fread($this->stdoutPipe, 4);
			if($header === false or strlen($header) < 4){
				return "";
			}
			$len = unpack("N", $header)[1];
			$data = "";
			while(strlen($data) < $len){
				$chunk = @fread($this->stdoutPipe, $len - strlen($data));
				if($chunk === false or $chunk === "") break;
				$data .= $chunk;
			}
			return $data;
		}
		return "";
	}

	public function quit(){
		$this->shutdown = true;

		// Send shutdown signal via channel
		try{
			$this->internalQueue->send("\x7f"); // PACKET_EMERGENCY_SHUTDOWN
		}catch(\Throwable $e){}

		// Close channels
		try{
			\parallel\Channel::destroy($this->extChanName);
		}catch(\Throwable $e){}
		try{
			\parallel\Channel::destroy($this->intChanName);
		}catch(\Throwable $e){}

		// Close process pipes
		if($this->stdoutPipe){
			@fclose($this->stdoutPipe);
		}

		// Terminate the RakLib process
		if($this->process){
			$status = @proc_get_status($this->process);
			if($status !== false && $status["running"]){
				@proc_terminate($this->process, 15); // SIGTERM
				$timeout = 5;
				while($timeout > 0){
					$status = @proc_get_status($this->process);
					if($status === false || !$status["running"]) break;
					usleep(100000);
					$timeout -= 0.1;
				}
				if($timeout <= 0){
					@proc_terminate($this->process, 9); // SIGKILL
				}
			}
			@proc_close($this->process);
			$this->process = null;
		}
	}

	public function getThreadName(){
		return "RakLib";
	}
}
