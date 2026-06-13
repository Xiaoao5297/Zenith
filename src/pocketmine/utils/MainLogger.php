<?php

/*
 * This file is part of the PocketMine-MP project.
 *  _____              _ __  __
 * /__  /  ___  ____  (_) /_/ /_
 *   / /  / _ \/ __ \/ / __/ __ \
 *  / /__/  __/ / / / / /_/ / / /
 * /____/\___/_/ /_/_/\__/_/ /_/
 *
 * This program is free software: you can redistribute/modify it
 * under the terms of the GNU LGPL, version 3 or later.
 *
 * @author Xiaoao
 * @link https://b23.tv/LQKxdts
 *
*/

namespace pocketmine\utils;

use LogLevel;

class MainLogger extends \AttachableThreadedLogger{
	protected $logFile;
	protected $shutdown;
	protected $logDebug;
	private $logResource;
	/** @var MainLogger */
	public static $logger = null;

	private $consoleCallback;

	/** Extra Settings */
	protected $write = true;

	public $shouldSendMsg = "";
	public $shouldRecordMsg = false;
	private $lastGet = 0;

	/** @var \parallel\Channel|null Logger channel (main -> background writer) */
	private $logChan = null;
	/** @var \parallel\Runtime|null Background file writer runtime */
	private $writerRuntime = null;
	/** @var \parallel\Future|null */
	private $writerFuture = null;

	public function setSendMsg($b){
		$this->shouldRecordMsg = $b;
		$this->lastGet = time();
	}

	public function getMessages(){
		$msg = $this->shouldSendMsg;
		$this->shouldSendMsg = "";
		$this->lastGet = time();
		return $msg;
	}

	/**
	 * @param string $logFile
	 * @param bool   $logDebug
	 *
	 * @throws \RuntimeException
	 */
	public function __construct($logFile, $logDebug = false){
		if(static::$logger instanceof MainLogger){
			throw new \RuntimeException("MainLogger has been already created");
		}
		static::$logger = $this;
		touch($logFile);
		$this->logFile = $logFile;
		$this->logDebug = (bool) $logDebug;

		$this->startWriter();
	}

	/**
	 * Start the background file writer using parallel.
	 */
	private function startWriter(){
		$chanName = "logger_" . spl_object_id($this);
		$this->logChan = \parallel\Channel::make($chanName, \parallel\Channel::Infinite);
		$this->writerRuntime = new \parallel\Runtime();

		$logFile = $this->logFile;

		$this->writerFuture = $this->writerRuntime->run(function($logFile, $chanName){
			$chan = \parallel\Channel::open($chanName);
			$shutdown = false;

			while(!$shutdown){
				try{
					$chunk = $chan->recv();
				}catch(\parallel\Channel\Error\Closed $e){
					break;
				}

				if($chunk === null){
					break;
				}

				if($chunk === "__SHUTDOWN__"){
					$shutdown = true;
					continue;
				}

				file_put_contents($logFile, $chunk, FILE_APPEND);
			}

			// Shutdown - exit the writer
		}, [$logFile, $chanName]);
	}

	/**
	 * @return MainLogger
	 */
	public static function getLogger(){
		return static::$logger;
	}

	public function emergency($message, $name = "EMERGENCY"){
		$this->send($message, \LogLevel::EMERGENCY, $name, TextFormat::RED);
	}

	public function alert($message, $name = "ALERT"){
		$this->send($message, \LogLevel::ALERT, $name, TextFormat::RED);
	}

	public function critical($message, $name = "CRITICAL"){
		$this->send($message, \LogLevel::CRITICAL, $name, TextFormat::RED);
	}

	public function error($message, $name = "ERROR"){
		$this->send($message, \LogLevel::ERROR, $name, TextFormat::DARK_RED);
	}

	public function warning($message, $name = "WARNING"){
		$this->send($message, \LogLevel::WARNING, $name, TextFormat::YELLOW);
	}

	public function notice($message, $name = "NOTICE"){
		$this->send($message, \LogLevel::NOTICE, $name, TextFormat::AQUA);
	}

	public function info($message, $name = "INFO"){
		$this->send($message, \LogLevel::INFO, $name, TextFormat::WHITE);
	}

	public function debug($message, $name = "DEBUG"){
		if($this->logDebug === false){
			return;
		}
		$this->send($message, \LogLevel::DEBUG, $name, TextFormat::GRAY);
	}

	/**
	 * @param bool $logDebug
	 */
	public function setLogDebug($logDebug){
		$this->logDebug = (bool) $logDebug;
	}

	public function logException(\Throwable $e, $trace = null){
		if($trace === null){
			$trace = $e->getTrace();
		}
		$errstr = $e->getMessage();
		$errfile = $e->getFile();
		$errno = $e->getCode();
		$errline = $e->getLine();

		$errorConversion = [
			0 => "EXCEPTION",
			E_ERROR => "E_ERROR",
			E_WARNING => "E_WARNING",
			E_PARSE => "E_PARSE",
			E_NOTICE => "E_NOTICE",
			E_CORE_ERROR => "E_CORE_ERROR",
			E_CORE_WARNING => "E_CORE_WARNING",
			E_COMPILE_ERROR => "E_COMPILE_ERROR",
			E_COMPILE_WARNING => "E_COMPILE_WARNING",
			E_USER_ERROR => "E_USER_ERROR",
			E_USER_WARNING => "E_USER_WARNING",
			E_USER_NOTICE => "E_USER_NOTICE",
			E_STRICT => "E_STRICT",
			E_RECOVERABLE_ERROR => "E_RECOVERABLE_ERROR",
			E_DEPRECATED => "E_DEPRECATED",
			E_USER_DEPRECATED => "E_USER_DEPRECATED",
		];
		if($errno === 0){
			$type = LogLevel::CRITICAL;
		}else{
			$type = ($errno === E_ERROR or $errno === E_USER_ERROR) ? LogLevel::ERROR : (($errno === E_USER_WARNING or $errno === E_WARNING) ? LogLevel::WARNING : LogLevel::NOTICE);
		}
		$errno = isset($errorConversion[$errno]) ? $errorConversion[$errno] : $errno;
		if(($pos = strpos($errstr, "\n")) !== false){
			$errstr = substr($errstr, 0, $pos);
		}
		$errfile = \pocketmine\cleanPath($errfile);
		$this->log($type, get_class($e) . ": \"$errstr\" ($errno) in \"$errfile\" at line $errline");
		foreach(@\pocketmine\getTrace(1, $trace) as $i => $line){
			$this->debug($line);
		}
	}

	public function log($level, $message){
		switch($level){
			case LogLevel::EMERGENCY:
				$this->emergency($message);
				break;
			case LogLevel::ALERT:
				$this->alert($message);
				break;
			case LogLevel::CRITICAL:
				$this->critical($message);
				break;
			case LogLevel::ERROR:
				$this->error($message);
				break;
			case LogLevel::WARNING:
				$this->warning($message);
				break;
			case LogLevel::NOTICE:
				$this->notice($message);
				break;
			case LogLevel::INFO:
				$this->info($message);
				break;
			case LogLevel::DEBUG:
				$this->debug($message);
				break;
		}
	}

	public function shutdown(){
		$this->shutdown = true;
		// Signal the background writer to stop
		try{
			$this->logChan?->send("__SHUTDOWN__");
		}catch(\Throwable $e){
		}
	}

	protected function send($message, $level, $prefix, $color){
		$now = time();

		// Determine thread name (no longer using pthreads Thread::getCurrentThread)
		$threadName = "Server thread";

		if($this->shouldRecordMsg){
			if((time() - $this->lastGet) >= 10) $this->shouldRecordMsg = false;
			else{
				if(strlen($this->shouldSendMsg) >= 10000) $this->shouldSendMsg = "";
				$this->shouldSendMsg .= $color . "|" . $prefix . "|" . trim($message, "\r\n") . "\n";
			}
		}

		// 获取带毫秒的时间
        $microtime = microtime(true);
        $seconds = floor($microtime);
        $milliseconds = round(($microtime - $seconds) * 1000);

        // 使用 date() 函数获取当前时间（自动使用系统时区）
        $timeString = TextFormat::BOLD . date("H:i:s", $seconds) . TextFormat::DARK_GREEN . '.' . sprintf('%03d', $milliseconds) . TextFormat::GREEN;

		$message = TextFormat::toANSI(TextFormat::GREEN . $timeString . TextFormat::RESET. " " . $color  . $threadName . "/" . TextFormat::BOLD . $prefix . TextFormat::RESET . " §8> " . $color . $message . TextFormat::RESET);
		$cleanMessage = TextFormat::clean($message);

		if(!Terminal::hasFormattingCodes()){
			echo $cleanMessage . PHP_EOL;
		}else{
			echo $message . PHP_EOL;
		}

		if(isset($this->consoleCallback)){
			call_user_func($this->consoleCallback);
		}

		if($this->attachment instanceof \ThreadedLoggerAttachment){
			$this->attachment->call($level, $message);
		}

		// Send to background file writer
		try{
			$this->logChan?->send(date("Y-m-d", $now) . " " . $cleanMessage . "\n");
		}catch(\Throwable $e){
		}
	}

	public function run(){
		// No-op in parallel mode; file writing is handled by the background Runtime
	}

	public function setWrite($write){
		$this->write = $write;
	}

	public function setConsoleCallback($callback){
		$this->consoleCallback = $callback;
	}

    public function directSend($message){
        if(Terminal::hasFormattingCodes()){
            echo TextFormat::toANSI($message) . PHP_EOL;
        }else{
            echo TextFormat::clean($message) . PHP_EOL;
        }
    }
}
