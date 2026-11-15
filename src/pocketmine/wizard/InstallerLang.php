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

namespace pocketmine\wizard;


class InstallerLang{
	public static $languages = [
		"eng" => "English",
		"chs" => "简体中文",
		"zho" => "繁體中文",
		"gen" => "文言",
		"jpn" => "日本語",
		"kor" => "한국어",
		"vie" => "Tiếng Việt",
		"fra" => "Français",
		"spa" => "Español",
		"deu" => "Deutsch",
		"rus" => "Русский",
		"por" => "Português",
		"ita" => "Italiano",
		"pol" => "Polski",
		"swe" => "Svenska",
		"nld" => "Nederlands"
	];
	private $texts = [];
	private $lang;
	private $langfile;

	public function __construct($lang = ""){
		$base = \pocketmine\PATH . "src/pocketmine/lang/";

		if(file_exists($base . $lang . "/installer.ini")){
			$this->lang = $lang;
			$this->langfile = $base . $lang . "/installer.ini";
		}else{
			$files = [];
			foreach(new \DirectoryIterator($base) as $dir){
				if($dir->isDot() or !$dir->isDir() or substr($dir->getFilename(), 0, 2) !== $lang){
					continue;
				}

				$file = $dir->getPathname() . "/installer.ini";
				if(is_file($file)){
					$files[$dir->getFilename()] = filesize($file);
				}
			}

			if(count($files) > 0){
				arsort($files);
				reset($files);
				$l = key($files);
				$this->lang = isset(self::$languages[$l]) ? $l : $lang;
				$this->langfile = $base . $l . "/installer.ini";
			}else{
				$this->lang = "en";
				$this->langfile = $base . "eng/installer.ini";
			}
		}

		$this->loadLang($base . "eng/installer.ini", "eng");
		if($this->lang !== "en"){
			$this->loadLang($this->langfile, $this->lang);
		}

	}

	public function getLang(){
		return ($this->lang);
	}

	public function loadLang($langfile, $lang = "en"){
		$this->texts[$lang] = [];
		$texts = explode("\n", str_replace(["\r", "\\/\\/"], ["", "//"], file_get_contents($langfile)));
		foreach($texts as $line){
			$line = trim($line);
			if($line === ""){
				continue;
			}
			$line = explode("=", $line);
			$this->texts[$lang][trim(array_shift($line))] = trim(str_replace(["\\n", "\\N",], "\n", implode("=", $line)));
		}
	}

	public function get($name, $search = [], $replace = []){
		if(!isset($this->texts[$this->lang][$name])){
			if($this->lang !== "en" and isset($this->texts["en"][$name])){
				return $this->texts["en"][$name];
			}else{
				return $name;
			}
		}elseif(count($search) > 0){
			return str_replace($search, $replace, $this->texts[$this->lang][$name]);
		}else{
			return $this->texts[$this->lang][$name];
		}
	}

	public function __get($name){
		return $this->get($name);
	}

}
