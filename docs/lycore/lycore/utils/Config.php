<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝
 * ╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 *  ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: U core
 *
 * @Links:
 *  > LY Core
 *  > LY Core Project
*/

namespace lycore\utils;
use lycore\scheduler\FileWriteTask;
use lycore\Server;


/**
 * Class Config
 *
 * Config Class for simple config manipulation of multiple formats.
 */
class Config{
	const DETECT = -1; //Detect by file extension
	const PROPERTIES = 0; // .properties
	const CNF = Config::PROPERTIES; // .cnf
	const JSON = 1; // .js, .json
	const YAML = 2; // .yml, .yaml
	//const EXPORT = 3; // .export, .xport
	const SERIALIZED = 4; // .sl
	const ENUM = 5; // .txt, .list, .enum
	const ENUMERATION = Config::ENUM;

	/** @var array */
	private $config = [];

	private $nestedCache = [];

	/** @var string */
	private $file;
	/** @var boolean */
	private $correct = false;
	/** @var integer */
	private $type = Config::DETECT;
	/** @var bool */
	private $skipAutoSaveOnLoad = false;
	/** @var bool */
	private $forceAutoSaveOnLoad = false;

	public static $formats = [
		"properties" => Config::PROPERTIES,
		"cnf" => Config::CNF,
		"conf" => Config::CNF,
		"config" => Config::CNF,
		"json" => Config::JSON,
		"js" => Config::JSON,
		"yml" => Config::YAML,
		"yaml" => Config::YAML,
		//"export" => Config::EXPORT,
		//"xport" => Config::EXPORT,
		"sl" => Config::SERIALIZED,
		"serialize" => Config::SERIALIZED,
		"txt" => Config::ENUM,
		"list" => Config::ENUM,
		"enum" => Config::ENUM,
	];

	/**
	 * @param string $file     Path of the file to be loaded
	 * @param int    $type     Config type to load, -1 by default (detect)
	 * @param array  $default  Array with the default values that will be written to the file if it did not exist
	 * @param null   &$correct Sets correct to true if everything has been loaded correctly
	 */
	public function __construct($file, $type = Config::DETECT, $default = [], &$correct = null){
		$this->load($file, $type, $default);
		$correct = $this->correct;
	}

	/**
	 * Removes all the changes in memory and loads the file again
	 */
	public function reload(){
		$this->config = [];
		$this->nestedCache = [];
		$this->correct = false;
		$this->load($this->file);
		$this->load($this->file, $this->type);
	}

	/**
	 * @param $str
	 *
	 * @return mixed
	 */
	public static function fixYAMLIndexes($str){
		return preg_replace("#^([ ]*)([a-zA-Z_]{1}[ ]*)\\:$#m", "$1\"$2\":", $str);
	}

	/**
	 * @param       $file
	 * @param int   $type
	 * @param array $default
	 *
	 * @return bool
	 */
	public function load($file, $type = Config::DETECT, $default = []){
		$this->correct = true;
		$this->skipAutoSaveOnLoad = false;
		$this->forceAutoSaveOnLoad = false;
		$this->type = (int) $type;
		$this->file = $file;
		if(!is_array($default)){
			$default = [];
		}
		if(!file_exists($file)){
			$this->config = $default;
			$this->save();
		}else{
			if($this->type === Config::DETECT){
				$extension = explode(".", basename($this->file));
				$extension = strtolower(trim(array_pop($extension)));
				if(isset(Config::$formats[$extension])){
					$this->type = Config::$formats[$extension];
				}else{
					$this->correct = false;
				}
			}
			if($this->correct === true){
				$content = file_get_contents($this->file);
				switch($this->type){
					case Config::PROPERTIES:
					case Config::CNF:
						$rawContent = $content;
						$content = $this->normalizePropertiesContent($content);
						$this->config = $this->parseProperties($content);
						$this->skipAutoSaveOnLoad = $this->shouldSkipPropertiesAutoSave($content, $this->config);
						$this->forceAutoSaveOnLoad = !$this->skipAutoSaveOnLoad
							and is_string($rawContent)
							and is_string($content)
							and $rawContent !== $content
							and count($this->config) > 0;
						break;
					case Config::JSON:
						$this->config = json_decode($content, true);
						break;
					case Config::YAML:
						$content = self::fixYAMLIndexes($content);
						$this->config = yaml_parse($content);
						break;
					case Config::SERIALIZED:
						$this->config = unserialize($content);
						break;
					case Config::ENUM:
						$this->parseList($content);
						break;
					default:
						$this->correct = false;

						return false;
				}
				if(!is_array($this->config)){
					$this->config = $default;
				}
				$changed = $this->fillDefaults($default, $this->config);
				if(($changed > 0 or $this->forceAutoSaveOnLoad) and !$this->skipAutoSaveOnLoad){
					$this->save();
				}
			}else{
				return false;
			}
		}

		return true;
	}

	/**
	 * @return boolean
	 */
	public function check(){
		return $this->correct === true;
	}

	/**
	 * @param bool $async
	 *
	 * @return boolean
	 */
	public function save($async = false){
		if($this->correct === true){
			try{
				$content = null;
				switch($this->type){
					case Config::PROPERTIES:
					case Config::CNF:
						$content = $this->writeProperties();
						break;
					case Config::JSON:
						$content = json_encode($this->config, JSON_PRETTY_PRINT | JSON_BIGINT_AS_STRING);
						break;
					case Config::YAML:
						$content = yaml_emit($this->config, YAML_UTF8_ENCODING);
						break;
					case Config::SERIALIZED:
						$content = serialize($this->config);
						break;
					case Config::ENUM:
						$content = implode("\r\n", array_keys($this->config));
						break;
				}

				if($async){
					Server::getInstance()->getScheduler()->scheduleAsyncTask(new FileWriteTask($this->file, $content));
				}else{
					file_put_contents($this->file, $content);
				}
			}catch(\Throwable $e){
				$logger = Server::getInstance()->getLogger();
				$logger->critical("Could not save Config " . $this->file . ": " . $e->getMessage());
				if(\lycore\DEBUG > 1 and $logger instanceof MainLogger){
					$logger->logException($e);
				}
			}

			return true;
		}else{
			return false;
		}
	}

	/**
	 * @param $k
	 *
	 * @return boolean|mixed
	 */
	public function __get($k){
		return $this->get($k);
	}

	/**
	 * @param $k
	 * @param $v
	 */
	public function __set($k, $v){
		$this->set($k, $v);
	}

	/**
	 * @param $k
	 *
	 * @return boolean
	 */
	public function __isset($k){
		return $this->exists($k);
	}

	/**
	 * @param $k
	 */
	public function __unset($k){
		$this->remove($k);
	}

	/**
	 * @param $key
	 * @param $value
	 */
	public function setNested($key, $value){
		$vars = explode(".", $key);
		$base = array_shift($vars);

		if(!isset($this->config[$base])){
			$this->config[$base] = [];
		}

		$base =& $this->config[$base];

		while(count($vars) > 0){
			$baseKey = array_shift($vars);
			if(!isset($base[$baseKey])){
				$base[$baseKey] = [];
			}
			$base =& $base[$baseKey];
		}

		$base = $value;
		$this->nestedCache[$key] = $value;
	}

	/**
	 * @param       $key
	 * @param mixed $default
	 *
	 * @return mixed
	 */
	public function getNested($key, $default = null){
		if(isset($this->nestedCache[$key])){
			return $this->nestedCache[$key];
		}

		$vars = explode(".", $key);
		$base = array_shift($vars);
		if(isset($this->config[$base])){
			$base = $this->config[$base];
		}else{
			return $default;
		}

		while(count($vars) > 0){
			$baseKey = array_shift($vars);
			if(is_array($base) and isset($base[$baseKey])){
				$base = $base[$baseKey];
			}else{
				return $default;
			}
		}

		return $this->nestedCache[$key] = $base;
	}

	/**
	 * @param       $k
	 * @param mixed $default
	 *
	 * @return boolean|mixed
	 */
	public function get($k, $default = false){
		return ($this->correct and isset($this->config[$k])) ? $this->config[$k] : $default;
	}

	/**
	 * @param string $path
	 *
	 * @deprecated
	 *
	 * @return mixed
	 */
	public function getPath($path){
		$currPath =& $this->config;
		foreach(explode(".", $path) as $component){
			if(isset($currPath[$component])){
				$currPath =& $currPath[$component];
			}else{
				$currPath = null;
			}
		}

		return $currPath;
	}

	/**
	 *
	 * @deprecated
	 *
	 * @param string $path
	 * @param mixed  $value
	 */
	public function setPath($path, $value){
		$currPath =& $this->config;
		$components = explode(".", $path);
		$final = array_pop($components);
		foreach($components as $component){
			if(!isset($currPath[$component])){
				$currPath[$component] = [];
			}
			$currPath =& $currPath[$component];
		}
		$currPath[$final] = $value;
	}

	/**
	 * @param string $k key to be set
	 * @param mixed  $v value to set key
	 */
	public function set($k, $v = true){
		$this->config[$k] = $v;
		foreach($this->nestedCache as $nestedKey => $nvalue){
			if(substr($nestedKey, 0, strlen($k) + 1) === ($k . ".")){
				unset($this->nestedCache[$nestedKey]);
  			}
		}
	}

	/**
	 * @param array $v
	 */
	public function setAll($v){
		$this->config = $v;
	}

	/**
	 * @param      $k
	 * @param bool $lowercase If set, searches Config in single-case / lowercase.
	 *
	 * @return boolean
	 */
	public function exists($k, $lowercase = false){
		if($lowercase === true){
			$k = strtolower($k); //Convert requested  key to lower
			$array = array_change_key_case($this->config, CASE_LOWER); //Change all keys in array to lower
			return isset($array[$k]); //Find $k in modified array
		}else{
			return isset($this->config[$k]);
		}
	}

	/**
	 * @param $k
	 */
	public function remove($k){
		unset($this->config[$k]);
	}

	/**
	 * @param bool $keys
	 *
	 * @return array
	 */
	public function getAll($keys = false){
		return ($keys === true ? array_keys($this->config) : $this->config);
	}

	/**
	 * @param array $defaults
	 */
	public function setDefaults(array $defaults){
		$this->fillDefaults($defaults, $this->config);
	}

	/**
	 * @param $default
	 * @param $data
	 *
	 * @return integer
	 */
	private function fillDefaults($default, &$data){
		$changed = 0;
		foreach($default as $k => $v){
			if(is_array($v)){
				if(!isset($data[$k]) or !is_array($data[$k])){
					$data[$k] = [];
				}
				$changed += $this->fillDefaults($v, $data[$k]);
			}elseif(!isset($data[$k])){
				$data[$k] = $v;
				++$changed;
			}
		}

		return $changed;
	}

	/**
	 * @param $content
	 */
	private function parseList($content){
		foreach(explode("\n", trim(str_replace("\r\n", "\n", $content))) as $v){
			$v = trim($v);
			if($v == ""){
				continue;
			}
			$this->config[$v] = true;
		}
	}

	/**
	 * @return string
	 */
	private function writeProperties(){
		$content = "#Properties Config file\r\n#" . date("D M j H:i:s T Y") . "\r\n";
		foreach($this->config as $k => $v){
			if(is_bool($v) === true){
				$v = $v === true ? "on" : "off";
			}elseif(is_array($v)){
				$v = implode(";", $v);
			}
			$content .= $this->escapePropertiesString((string) $k, true) . "=" . $this->escapePropertiesString((string) $v, false) . "\r\n";
		}

		return $content;
	}

	private function normalizePropertiesContent($content){
		if(!is_string($content) or $content === ""){
			return (string) $content;
		}

		$convertedFromBom = $this->convertPropertiesBomEncodedContent($content);
		if(is_string($convertedFromBom)){
			$content = $convertedFromBom;
		}

		$content = $this->stripUtf8Bom($content);
		if($this->isValidUtf8($content)){
			$repaired = $this->repairUtf8MojibakePropertiesContent($content);
			if(is_string($repaired)){
				return $repaired;
			}
			return $content;
		}

		foreach($this->getPropertiesEncodingGuesses($content) as $encoding){
			$converted = $this->convertEncodedPropertiesContent($content, $encoding);
			if(is_string($converted) and $converted !== ""){
				$converted = $this->stripUtf8Bom($converted);
				if($this->isValidUtf8($converted)){
					return $converted;
				}
			}
		}

		return $content;
	}

	private function convertPropertiesBomEncodedContent($content){
		$boms = [
			"\x00\x00\xFE\xFF" => "UTF-32BE",
			"\xFF\xFE\x00\x00" => "UTF-32LE",
			"\xFE\xFF" => "UTF-16BE",
			"\xFF\xFE" => "UTF-16LE",
		];

		foreach($boms as $bom => $encoding){
			if(strpos($content, $bom) === 0){
				return $this->convertEncodedPropertiesContent(substr($content, strlen($bom)), $encoding);
			}
		}

		return null;
	}

	private function getPropertiesEncodingGuesses($content){
		$guesses = [];
		if(strpos($content, "\x00") !== false){
			$guesses[] = "UTF-16LE";
			$guesses[] = "UTF-16BE";
			$guesses[] = "UTF-32LE";
			$guesses[] = "UTF-32BE";
		}
		$guesses[] = "GB18030";
		$guesses[] = "GBK";
		$guesses[] = "BIG5";

		return array_values(array_unique($guesses));
	}

	private function convertEncodedPropertiesContent($content, $encoding){
		if(function_exists("iconv")){
			$converted = @iconv($encoding, "UTF-8//IGNORE", $content);
			if(is_string($converted) and $converted !== ""){
				return $converted;
			}
		}

		if(function_exists("mb_convert_encoding")){
			$converted = @mb_convert_encoding($content, "UTF-8", $encoding);
			if(is_string($converted) and $converted !== ""){
				return $converted;
			}
		}

		return false;
	}

	private function repairUtf8MojibakePropertiesContent($content){
		foreach(["GB18030", "GBK"] as $encoding){
			$repaired = $this->reencodePropertiesContent($content, $encoding);
			if(is_string($repaired) and $repaired !== "" and $repaired !== $content and $this->isValidUtf8($repaired)){
				return $repaired;
			}
		}

		return null;
	}

	private function reencodePropertiesContent($content, $encoding){
		if(function_exists("iconv")){
			$converted = @iconv("UTF-8", $encoding . "//IGNORE", $content);
			if(is_string($converted) and $converted !== ""){
				return $converted;
			}
		}

		if(function_exists("mb_convert_encoding")){
			$converted = @mb_convert_encoding($content, $encoding, "UTF-8");
			if(is_string($converted) and $converted !== ""){
				return $converted;
			}
		}

		return false;
	}

	private function stripUtf8Bom($content){
		return substr($content, 0, 3) === "\xEF\xBB\xBF" ? substr($content, 3) : $content;
	}

	private function isValidUtf8($content){
		return is_string($content) and preg_match('//u', $content) === 1;
	}

	/**
	 * @param $content
	 */
	private function parseProperties($content){
		$result = [];
		$content = str_replace(["\r\n", "\r"], "\n", (string) $content);
		foreach(explode("\n", $content) as $line){
			$line = $this->stripUtf8Bom($line);
			$trimmed = trim($line);
			if($trimmed === ""){
				continue;
			}

			$prefix = $trimmed[0];
			if($prefix === "#" or $prefix === ";" or $prefix === "!"){
				continue;
			}

			$separatorPos = strpos($line, "=");
			$colonPos = strpos($line, ":");
			if($separatorPos === false or ($colonPos !== false and $colonPos < $separatorPos)){
				$separatorPos = $colonPos;
			}
			if($separatorPos === false){
				continue;
			}

			$k = trim(substr($line, 0, $separatorPos));
			if($k === ""){
				continue;
			}

			$k = $this->decodePropertiesString($k);
			$v = $this->decodePropertiesString(trim(substr($line, $separatorPos + 1)));
			switch(strtolower($v)){
				case "on":
				case "true":
				case "yes":
					$v = true;
					break;
				case "off":
				case "false":
				case "no":
					$v = false;
					break;
			}

			if(isset($result[$k])){
				$logger = class_exists(__NAMESPACE__ . "\\MainLogger", false) ? MainLogger::getLogger() : null;
				if($logger !== null){
					$logger->debug("[Config] Repeated property " . $k . " on file " . $this->file);
				}
			}
			$result[$k] = $v;
		}

		return $result;
	}

	private function decodePropertiesString($value){
		$length = strlen($value);
		$result = "";
		for($i = 0; $i < $length; ++$i){
			$char = $value[$i];
			if($char !== "\\"){
				$result .= $char;
				continue;
			}

			if(++$i >= $length){
				$result .= "\\";
				break;
			}

			$escape = $value[$i];
			switch($escape){
				case "t":
					$result .= "\t";
					break;
				case "n":
					$result .= "\n";
					break;
				case "r":
					$result .= "\r";
					break;
				case "f":
					$result .= "\f";
					break;
				case "u":
					$decoded = $this->decodeUnicodeEscapeSequence($value, $i);
					if($decoded === null){
						$result .= "\\u";
					}else{
						$result .= $decoded["char"];
						$i = $decoded["offset"];
					}
					break;
				default:
					$result .= $escape;
					break;
			}
		}

		return $result;
	}

	private function decodeUnicodeEscapeSequence($value, $uOffset){
		if($uOffset + 4 >= strlen($value)){
			return null;
		}

		$hex = substr($value, $uOffset + 1, 4);
		if(!ctype_xdigit($hex)){
			return null;
		}

		$codeUnit = hexdec($hex);
		$offset = $uOffset + 4;
		if($codeUnit >= 0xD800 and $codeUnit <= 0xDBFF){
			if($offset + 6 < strlen($value) and $value[$offset + 1] === "\\" and $value[$offset + 2] === "u"){
				$lowHex = substr($value, $offset + 3, 4);
				if(ctype_xdigit($lowHex)){
					$lowUnit = hexdec($lowHex);
					if($lowUnit >= 0xDC00 and $lowUnit <= 0xDFFF){
						$codeUnit = 0x10000 + (($codeUnit - 0xD800) << 10) + ($lowUnit - 0xDC00);
						$offset += 6;
					}
				}
			}
		}

		return [
			"char" => $this->encodeUnicodeCodepoint($codeUnit),
			"offset" => $offset,
		];
	}

	private function escapePropertiesString($value, bool $isKey){
		if($value === ""){
			return $value;
		}

		$chars = preg_split('//u', $value, -1, PREG_SPLIT_NO_EMPTY);
		if($chars === false){
			return $value;
		}

		$result = "";
		foreach($chars as $index => $char){
			$ord = $this->unicodeOrd($char);
			switch($char){
				case "\\":
					$result .= "\\\\";
					continue 2;
				case "\t":
					$result .= "\\t";
					continue 2;
				case "\n":
					$result .= "\\n";
					continue 2;
				case "\r":
					$result .= "\\r";
					continue 2;
				case "\f":
					$result .= "\\f";
					continue 2;
				case " ":
					if($index === 0){
						$result .= "\\ ";
						continue 2;
					}
					break;
				case "=":
				case ":":
				case "#":
				case "!":
					if($isKey){
						$result .= "\\" . $char;
						continue 2;
					}
					break;
			}

			if($ord < 0x20 || $ord > 0x7E){
				$result .= $this->escapeUnicodeCodepoint($ord);
			}else{
				$result .= $char;
			}
		}

		return $result;
	}

	private function escapeUnicodeCodepoint(int $codepoint){
		if($codepoint <= 0xFFFF){
			return sprintf("\\u%04X", $codepoint);
		}

		$codepoint -= 0x10000;
		$high = 0xD800 + (($codepoint >> 10) & 0x3FF);
		$low = 0xDC00 + ($codepoint & 0x3FF);
		return sprintf("\\u%04X\\u%04X", $high, $low);
	}

	private function unicodeOrd($char){
		if(function_exists("mb_convert_encoding")){
			$data = @mb_convert_encoding($char, "UCS-4BE", "UTF-8");
			if(is_string($data) and strlen($data) === 4){
				$unpacked = unpack("Ncodepoint", $data);
				if(is_array($unpacked) and isset($unpacked["codepoint"])){
					return (int) $unpacked["codepoint"];
				}
			}
		}

		if(function_exists("iconv")){
			$data = @iconv("UTF-8", "UCS-4BE//IGNORE", $char);
			if(is_string($data) and strlen($data) === 4){
				$unpacked = unpack("Ncodepoint", $data);
				if(is_array($unpacked) and isset($unpacked["codepoint"])){
					return (int) $unpacked["codepoint"];
				}
			}
		}

		return ord($char);
	}

	private function encodeUnicodeCodepoint(int $codepoint){
		if(function_exists("mb_convert_encoding")){
			return mb_convert_encoding(pack("N", $codepoint), "UTF-8", "UCS-4BE");
		}

		if(function_exists("iconv")){
			$converted = @iconv("UCS-4BE", "UTF-8//IGNORE", pack("N", $codepoint));
			if(is_string($converted) and $converted !== ""){
				return $converted;
			}
		}

		return "";
	}

	private function shouldSkipPropertiesAutoSave($content, array $parsed){
		if(count($parsed) > 0){
			return false;
		}

		$content = str_replace("\x00", "", str_replace(["\r\n", "\r"], "\n", (string) $content));
		foreach(explode("\n", $content) as $line){
			$line = $this->stripUtf8Bom($line);
			$trimmed = trim($line);
			if($trimmed === ""){
				continue;
			}

			$prefix = $trimmed[0];
			if($prefix === "#" or $prefix === ";" or $prefix === "!"){
				continue;
			}

			if(strpos($trimmed, "=") !== false or strpos($trimmed, ":") !== false){
				return true;
			}
		}

		return false;
	}

}
