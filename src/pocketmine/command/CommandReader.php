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
use pocketmine\utils\ConsoleLineEditor;
use pocketmine\utils\MainLogger;
use pocketmine\utils\Utils;

class CommandReader extends Thread{
	private $readline;
	/** @var bool */
	private $useEditor = false;
	/** @var \Threaded|null 跨线程共享的显示状态，供日志回调重绘 */
	private $displayState = null;
	/** @var \Threaded */
	protected $buffer;
	private $shutdown = false;
	private $stdin;
	/** @var MainLogger */
	private $logger;
	private $prompt = "Genisys> ";

	public function __construct($logger){
		$this->stdin = fopen("php://stdin", "r");
		$opts = getopt("", ["disable-readline"]);
		$isTty = (!function_exists("posix_isatty") or posix_isatty($this->stdin));
		if(extension_loaded("readline") && !isset($opts["disable-readline"]) && $isTty){
			$this->readline = true;
		}elseif($isTty && Utils::getOS() !== "win" && !isset($opts["disable-readline"])){
			$this->readline = false;
			$this->useEditor = true;
			// 子线程不会继承 spl_autoload_register，须在 start() 前预加载，否则 run() 里 new 不到
			class_exists("pocketmine\\utils\\ConsoleLineEditor");
			stream_set_blocking($this->stdin, false);
			$this->displayState = new \Threaded;
			$this->displayState["line"] = "";
			$this->displayState["cursor"] = 0;
		}else{
			$this->readline = false;
		}
		$this->logger = $logger;
		$this->buffer = new \Threaded;
		$this->start();
	}

	public function shutdown(){
		$this->shutdown = true;
	}

	private function readline_callback($line){
		if($line !== ""){
			$this->buffer[] = $line;
			readline_add_history($line);
		}
	}

	private function readLine($editor){
		if($this->readline){
			readline_callback_read_char();
		}elseif($editor !== null){
			$data = fread($this->stdin, 8192);
			if($data === false or $data === ""){
				return;
			}
			$len = strlen($data);
			for($i = 0; $i < $len; $i++){
				$line = $editor->processByte($data{$i});
				if($line !== null and $line !== ""){
					$this->buffer[] = $line;
				}
			}
		}else{
			$line = trim(fgets($this->stdin));
			if($line !== ""){
				$this->buffer[] = $line;
			}
		}
	}

	/**
	 * Reads a line from console, if available. Returns null if not available
	 *
	 * @return string|null
	 */
	public function getLine(){
		if($this->buffer->count() !== 0){
			return $this->buffer->shift();
		}

		return null;
	}

	public function quit(){
		$this->shutdown();
		// Windows sucks
		if(Utils::getOS() != "win"){
			parent::quit();
		}
	}

	public function run(){
		$editor = null;
		if($this->readline){
			readline_callback_handler_install($this->prompt, [$this, "readline_callback"]);
			$this->logger->setConsoleCallback("readline_redisplay");
		}elseif($this->useEditor){
			$editor = new ConsoleLineEditor($this->prompt);
			$editor->setDisplayState($this->displayState);
			$editor->enableRawMode();
			$editor->redraw();
			$state = $this->displayState;
			$prompt = $this->prompt;
			$this->logger->setConsoleEditorActive(true);
			$this->logger->setConsoleCallback(function() use ($state, $prompt){
				ConsoleLineEditor::redrawLine($prompt, $state["line"], $state["cursor"]);
			});
		}

		while(!$this->shutdown){
			$r = [$this->stdin];
			$w = null;
			$e = null;
			if(stream_select($r, $w, $e, 0, 200000) > 0){
				// PHP on Windows sucks
				if(feof($this->stdin)){
					if(Utils::getOS() == "win"){
						$this->stdin = fopen("php://stdin", "r");
						if(!is_resource($this->stdin)){
							break;
						}
					}else{
						break;
					}
				}
				$this->readLine($editor);
			}
		}

		if($this->readline){
			$this->logger->setConsoleCallback(null);
			readline_callback_handler_remove();
		}elseif($editor !== null){
			$this->logger->setConsoleCallback(null);
			$this->logger->setConsoleEditorActive(false);
			$editor->disableRawMode();
		}
	}

	public function getThreadName(){
		return "Console";
	}
}
