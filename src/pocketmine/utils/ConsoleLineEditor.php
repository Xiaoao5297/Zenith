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

namespace pocketmine\utils;

/**
 * 终端行编辑器：在没有 readline 扩展时，提供类似 bash 的交互式命令行编辑。
 *
 * 支持：光标左右移动（含 Home/End）、上下方向键浏览历史命令、
 * Home/End/Ctrl-A/Ctrl-E/Ctrl-U/Ctrl-K、以及按 UTF-8 字符删除（中文删除一个完整字）。
 */
class ConsoleLineEditor{

	private $prompt;

	/** 当前行内容（按 UTF-8 字符拆分，便于增删） */
	private $chars = [];
	/** 光标位置（字符下标） */
	private $cursor = 0;

	/** 历史命令 */
	private $history = [];
	/** 0 = 正在编辑新行；1..n = 历史（1 为最近一条） */
	private $historyPos = 0;
	/** 进入历史浏览前保存的当前行 */
	private $savedChars = null;

	/** ANSI 转义序列解析状态：0=普通 1=收到ESC 2=收到ESC[ 3=收到ESC[3 4=收到ESCO */
	private $escState = 0;
	/** 尚未拼完的多字节 UTF-8 字符 */
	private $pendingUtf8 = "";
	private $pendingUtf8Len = 0;

	/** @var string|null 进入 raw 模式前保存的终端设置 */
	private $savedTty = null;

	/** @var \Threaded|null 跨线程共享的显示状态（供日志回调重绘） */
	private $displayState = null;

	public function __construct($prompt = "Genisys> "){
		$this->prompt = $prompt;
	}

	public function setDisplayState(\Threaded $state){
		$this->displayState = $state;
	}

	public function setPrompt($prompt){
		$this->prompt = $prompt;
	}

	public function enableRawMode(){
		if(Utils::getOS() === "win"){
			return;
		}
		$saved = @shell_exec("stty -g 2>/dev/null");
		if(is_string($saved) and trim($saved) !== ""){
			$this->savedTty = trim($saved);
		}
		@system("stty -icanon -echo -ixon 2>/dev/null");
	}

	public function disableRawMode(){
		if(Utils::getOS() === "win"){
			return;
		}
		if($this->savedTty !== null){
			@system("stty " . escapeshellarg($this->savedTty) . " 2>/dev/null");
		}else{
			@system("stty sane 2>/dev/null");
		}
	}

	/**
	 * 处理一个输入字节，返回已提交的命令行；未提交则返回 null。
	 */
	public function processByte($byte){
		$o = ord($byte);

		if($this->escState === 1){
			$this->escState = 0;
			if($byte === "["){
				$this->escState = 2;
			}elseif($byte === "O"){
				$this->escState = 4;
			}
			return null;
		}
		if($this->escState === 2){
			$this->escState = 0;
			switch($byte){
				case "A":
					$this->historyUp();
					break;
				case "B":
					$this->historyDown();
					break;
				case "C":
					$this->cursorRight();
					break;
				case "D":
					$this->cursorLeft();
					break;
				case "H":
					$this->cursorHome();
					break;
				case "F":
					$this->cursorEnd();
					break;
				case "3":
					$this->escState = 3;
					return null;
			}
			$this->redraw();
			return null;
		}
		if($this->escState === 3){
			$this->escState = 0;
			if($byte === "~"){
				$this->deleteAtCursor();
			}
			$this->redraw();
			return null;
		}
		if($this->escState === 4){
			$this->escState = 0;
			if($byte === "H"){
				$this->cursorHome();
			}elseif($byte === "F"){
				$this->cursorEnd();
			}
			$this->redraw();
			return null;
		}

		if($o === 0x1b){
			$this->escState = 1;
			return null;
		}
		if($o === 0x0a or $o === 0x0d){
			return $this->submit();
		}
		if($o === 0x7f or $o === 0x08){
			$this->backspace();
			$this->redraw();
			return null;
		}
		if($o === 0x01){ // Ctrl-A
			$this->cursorHome();
			$this->redraw();
			return null;
		}
		if($o === 0x05){ // Ctrl-E
			$this->cursorEnd();
			$this->redraw();
			return null;
		}
		if($o === 0x15){ // Ctrl-U：清空整行
			$this->clearLine();
			$this->redraw();
			return null;
		}
		if($o === 0x0b){ // Ctrl-K：删除光标后内容
			$this->killToEnd();
			$this->redraw();
			return null;
		}
		if($o === 0x04){ // Ctrl-D：忽略
			return null;
		}
		if($o < 0x20){
			return null;
		}

		$this->insertByte($byte);
		$this->redraw();
		return null;
	}

	public function redraw(){
		$line = implode("", $this->chars);
		$before = implode("", array_slice($this->chars, 0, $this->cursor));
		$cursorColumn = self::strWidth($this->prompt) + self::strWidth($before);
		self::redrawLine($this->prompt, $line, $cursorColumn);
		if($this->displayState !== null){
			$this->displayState["line"] = $line;
			$this->displayState["cursor"] = $cursorColumn;
		}
	}

	public static function redrawLine($prompt, $line, $cursorColumn){
		$out = "\r" . $prompt . $line . "\x1b[K";
		$totalWidth = self::strWidth($prompt) + self::strWidth($line);
		$moveBack = $totalWidth - $cursorColumn;
		if($moveBack > 0){
			$out .= "\x1b[" . $moveBack . "D";
		}
		echo $out;
	}

	private function insertByte($byte){
		if($this->pendingUtf8Len === 0){
			$this->pendingUtf8Len = self::utf8Len($byte);
			$this->pendingUtf8 = $byte;
		}else{
			$this->pendingUtf8 .= $byte;
		}
		if(strlen($this->pendingUtf8) >= $this->pendingUtf8Len){
			array_splice($this->chars, $this->cursor, 0, [$this->pendingUtf8]);
			$this->cursor++;
			$this->pendingUtf8 = "";
			$this->pendingUtf8Len = 0;
		}
	}

	private function backspace(){
		if($this->cursor > 0){
			array_splice($this->chars, $this->cursor - 1, 1);
			$this->cursor--;
		}
	}

	private function deleteAtCursor(){
		if($this->cursor < count($this->chars)){
			array_splice($this->chars, $this->cursor, 1);
		}
	}

	private function cursorLeft(){
		if($this->cursor > 0){
			$this->cursor--;
		}
	}

	private function cursorRight(){
		if($this->cursor < count($this->chars)){
			$this->cursor++;
		}
	}

	private function cursorHome(){
		$this->cursor = 0;
	}

	private function cursorEnd(){
		$this->cursor = count($this->chars);
	}

	private function clearLine(){
		$this->chars = [];
		$this->cursor = 0;
	}

	private function killToEnd(){
		$this->chars = array_slice($this->chars, 0, $this->cursor);
	}

	private function setLine(array $chars){
		$this->chars = $chars;
		$this->cursor = count($chars);
	}

	private function historyUp(){
		if(count($this->history) === 0){
			return;
		}
		if($this->historyPos === 0){
			$this->savedChars = $this->chars;
		}
		if($this->historyPos < count($this->history)){
			$this->historyPos++;
			$this->setLine(self::toChars($this->history[count($this->history) - $this->historyPos]));
		}
	}

	private function historyDown(){
		if($this->historyPos === 0){
			return;
		}
		$this->historyPos--;
		if($this->historyPos === 0){
			$this->setLine($this->savedChars !== null ? $this->savedChars : []);
			$this->savedChars = null;
		}else{
			$this->setLine(self::toChars($this->history[count($this->history) - $this->historyPos]));
		}
	}

	private function submit(){
		$line = implode("", $this->chars);
		$this->chars = [];
		$this->cursor = 0;
		$this->historyPos = 0;
		$this->savedChars = null;
		$this->pendingUtf8 = "";
		$this->pendingUtf8Len = 0;
		echo "\r\n";
		if($line !== ""){
			if(count($this->history) === 0 or $this->history[count($this->history) - 1] !== $line){
				$this->history[] = $line;
				if(count($this->history) > 500){
					array_shift($this->history);
				}
			}
		}
		$this->redraw();
		return $line;
	}

	private static function toChars($str){
		if(function_exists("preg_split")){
			$chars = @preg_split("//u", $str, -1, PREG_SPLIT_NO_EMPTY);
			if($chars !== false){
				return $chars;
			}
		}
		return str_split($str);
	}

	private static function strWidth($str){
		if(function_exists("mb_strwidth")){
			return (int) mb_strwidth($str, "UTF-8");
		}
		if(function_exists("preg_split")){
			$chars = @preg_split("//u", $str, -1, PREG_SPLIT_NO_EMPTY);
			if($chars !== false){
				$w = 0;
				foreach($chars as $c){
					$w += strlen($c) === 1 ? 1 : 2;
				}
				return $w;
			}
		}
		return strlen($str);
	}

	private static function utf8Len($byte){
		$o = ord($byte);
		if($o < 0x80){
			return 1;
		}
		if(($o & 0xE0) === 0xC0){
			return 2;
		}
		if(($o & 0xF0) === 0xE0){
			return 3;
		}
		if(($o & 0xF8) === 0xF0){
			return 4;
		}
		return 1;
	}

}
