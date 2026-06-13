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
	private $shutdown = false;
	private $stdin;
	private $buffer = [];

	public function __construct(){
		$this->stdin = fopen("php://stdin", "r");
		$opts = getopt("", ["disable-readline"]);
		if(extension_loaded("readline") && !isset($opts["disable-readline"]) && (!function_exists("posix_isatty") || posix_isatty($this->stdin))){
			$this->readline = true;
			readline_callback_handler_install("Genisys> ", function($line){
				if($line !== ""){
					$this->buffer[] = $line;
					readline_add_history($line);
				}
			});
		}else{
			$this->readline = false;
		}
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
		// Poll stdin for input (non-blocking)
		$r = [$this->stdin];
		$w = null;
		$e = null;
		if(stream_select($r, $w, $e, 0, 0) > 0){
			if(!$this->readline){
				$line = trim(fgets($this->stdin));
				if($line !== ""){
					$this->buffer[] = $line;
				}
			}else{
				readline_callback_read_char();
			}
		}

		if(!empty($this->buffer)){
			return array_shift($this->buffer);
		}

		return null;
	}

	public function start(int $options = 0){
	}

	public function quit(){
		$this->shutdown();
		if($this->readline){
			readline_callback_handler_remove();
		}
	}

	public function run(){
	}
}
