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

namespace pocketmine\command;

use pocketmine\Thread;

class CommandReader extends Thread{
	private $readline;
	/** @var resource */
	private $readSocket = null;
	/** @var resource */
	private $writeSocket = null;
	private $shutdown = false;
	private $stdin;
	/** @var \parallel\Runtime|null */
	private $readerRuntime = null;

	public function __construct(){
		$this->stdin = fopen("php://stdin", "r");
		$opts = getopt("", ["disable-readline"]);
		if(extension_loaded("readline") && !isset($opts["disable-readline"]) && (!function_exists("posix_isatty") || posix_isatty($this->stdin))){
			$this->readline = true;
		}else{
			$this->readline = false;
		}

		// Create socket pair for non-blocking line reading
		$sockets = @stream_socket_pair(STREAM_PF_UNIX, STREAM_SOCK_STREAM, STREAM_IPPROTO_IP);
		if($sockets !== false){
			$this->readSocket = $sockets[0];
			$this->writeSocket = $sockets[1];
			stream_set_blocking($this->readSocket, false);
			stream_set_blocking($this->writeSocket, false);
		}

		$this->start();
	}

	public function shutdown(){
		$this->shutdown = true;
	}

	/**
	 * Reads a line from console, if available. Returns null if not available
	 *
	 * @return string|null
	 */
	public function getLine(){
		if($this->readSocket === null or $this->readSocket === false){
			return null;
		}
		$r = [$this->readSocket];
		$w = null;
		$e = null;
		if(stream_select($r, $w, $e, 0, 0) > 0){
			$line = @fgets($this->readSocket);
			if($line !== false and $line !== ""){
				return trim($line);
			}
		}
		return null;
	}

	public function start(int $options = 0){
		$writeSocket = $this->writeSocket;
		$readlineMode = $this->readline;

		$this->readerRuntime = new \parallel\Runtime();

		$this->future = $this->readerRuntime->run(function($writeSocket, $readlineMode){
			$stdin = fopen("php://stdin", "r");

			if($readlineMode){
				$cb = function($line) use ($writeSocket){
					if($line !== ""){
						@fwrite($writeSocket, $line . "\n");
						readline_add_history($line);
					}
				};
				readline_callback_handler_install("Genisys> ", $cb);
			}

			$shutdown = false;
			while(!$shutdown){
				$r = [$stdin];
				$w = null;
				$e = null;
				if(stream_select($r, $w, $e, 0, 200000) > 0){
					if(feof($stdin)){
						break;
					}
					if(!$readlineMode){
						$line = trim(fgets($stdin));
						if($line !== ""){
							@fwrite($writeSocket, $line . "\n");
						}
					}else{
						readline_callback_read_char();
					}
				}
			}

			if($readlineMode){
				readline_callback_handler_remove();
			}
			fclose($stdin);
			@fclose($writeSocket);
		}, [$writeSocket, $readlineMode]);
	}

	public function quit(){
		$this->shutdown();
		try{
			if($this->readSocket){ @fclose($this->readSocket); $this->readSocket = null; }
		}catch(\Throwable $e){
		}
		try{
			if($this->writeSocket){ @fclose($this->writeSocket); $this->writeSocket = null; }
		}catch(\Throwable $e){
		}
		try{
			$this->readerRuntime?->close();
		}catch(\Throwable $e){
		}
	}

	public function run(){
	}
}
