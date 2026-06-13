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
	/** @var \parallel\Channel */
	protected $buffer;
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
		$this->buffer = \parallel\Channel::make("cmd_" . spl_object_id($this), \parallel\Channel::Infinite);
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
		try{
			return $this->buffer->tryRecv();
		}catch(\parallel\Channel\Error\Existence $e){
			return null;
		}catch(\parallel\Channel\Error\Closed $e){
			return null;
		}
	}

	public function start(int $options = 0){
		$bufferName = $this->buffer->getName();
		$readlineMode = $this->readline;

		$this->readerRuntime = new \parallel\Runtime();

		$this->future = $this->readerRuntime->run(function($bufferName, $readlineMode){
			$buffer = \parallel\Channel::open($bufferName);
			$stdin = fopen("php://stdin", "r");

			if($readlineMode){
				$cb = function($line) use ($buffer){
					if($line !== ""){
						$buffer->send($line);
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
							$buffer->send($line);
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
		}, [$bufferName, $readlineMode]);
	}

	public function quit(){
		$this->shutdown();
		try{
			\parallel\Channel::destroy($this->buffer->getName());
		}catch(\Throwable $e){
		}
		try{
			$this->readerRuntime?->close();
		}catch(\Throwable $e){
		}
	}

	public function run(){
		// No-op
	}

	public function getThreadName(){
		return "Console";
	}
}
