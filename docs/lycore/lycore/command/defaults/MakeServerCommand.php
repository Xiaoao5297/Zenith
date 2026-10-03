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

namespace lycore\command\defaults;

use lycore\command\CommandSender;
use lycore\plugin\Plugin;
use lycore\Server;
use lycore\utils\TextFormat;
use lycore\network\protocol\Info;

class MakeServerCommand extends VanillaCommand{

	public static function shouldPackagePath($path){
		$path = str_replace("\\", "/", $path);
		if($path === "" or $path[0] === "." or strpos($path, "/.") !== false or substr($path, 0, 4) !== "src/"){
			return false;
		}

		$lowerPath = strtolower($path);
		return substr($lowerPath, -4) !== ".zip" and substr($lowerPath, -3) !== ".7z";
	}

	public static function isSourceGuardianProtectedFile($path){
		$contents = @file_get_contents($path);
		if($contents === false){
			return false;
		}

		return strpos($contents, "sg_load(") !== false and (
			strpos($contents, "function_exists('sg_load')") !== false or
			strpos($contents, 'function_exists("sg_load")') !== false or
			strpos($contents, "SourceGuardian") !== false
		);
	}

	public static function buildPharPath($directory, $serverName, $serverVersion, $sourceGuardianEnabled){
		$suffix = $sourceGuardianEnabled ? " - sg.enabled" : " - no.sg";
		return rtrim($directory, "\\/") . DIRECTORY_SEPARATOR . $serverName . "_" . $serverVersion . $suffix . ".phar";
	}

	public function __construct($name){
		parent::__construct(
			$name,
			"创建一个 PocketMine Phar",
			"/makeserver (nogz)"
		);
		$this->setPermission("pocketmine.command.makeserver");
	}

	public function execute(CommandSender $sender, $commandLabel, array $args){
		if(!$this->testPermission($sender)){
			return false;
		}

		$server = $sender->getServer();
		$filePath = substr(\lycore\PATH, 0, 7) === "phar://" ? \lycore\PATH : realpath(\lycore\PATH) . "/";
		$filePath = rtrim(str_replace("\\", "/", $filePath), "/") . "/";
		$sourceGuardianFileCount = 0;
		foreach(new \RecursiveIteratorIterator(new \RecursiveDirectoryIterator($filePath . "src")) as $file){
			$fileName = $file->getPathname();
			$path = ltrim(str_replace(["\\", $filePath], ["/", ""], $fileName), "/");
			if(!self::shouldPackagePath($path)){
				continue;
			}
			if(self::isSourceGuardianProtectedFile($fileName)){
				++$sourceGuardianFileCount;
			}
		}

		$sourceGuardianEnabled = $sourceGuardianFileCount > 0;
		$pharPath = self::buildPharPath(Server::getInstance()->getPluginPath() . DIRECTORY_SEPARATOR . "LY Core", $server->getName(), $server->getPocketMineVersion(), $sourceGuardianEnabled);
		if(file_exists($pharPath)){
			$sender->sendMessage("Phar file already exists, overwriting...");
			@unlink($pharPath);
		}
		$phar = new \Phar($pharPath);
		$phar->setMetadata([
			"name" => $server->getName(),
			"version" => $server->getPocketMineVersion(),
			"api" => $server->getApiVersion(),
			"geniapi" => $server->getGeniApiVersion(),
			"minecraft" => $server->getVersion(),
			"protocol" => Info::CURRENT_PROTOCOL,
			"creator" => "LY Core MakeServerCommand",
			"creationDate" => time()
		]);
		$phar->setStub('<?php define("lycore\\\\PATH", "phar://". __FILE__ ."/"); require_once("phar://". __FILE__ ."/src/lycore/PocketMine.php");  __HALT_COMPILER();');
		$phar->setSignatureAlgorithm(\Phar::SHA1);
		$phar->startBuffering();

		foreach(new \RecursiveIteratorIterator(new \RecursiveDirectoryIterator($filePath . "src")) as $file){
			$path = ltrim(str_replace(["\\", $filePath], ["/", ""], $file), "/");
			if(!self::shouldPackagePath($path)){
				continue;
			}
			$phar->addFile($file, $path);
			$sender->sendMessage("[核心打包程序] 正在写入 $path");
		}
		foreach($phar as $file => $finfo){
			/** @var \PharFileInfo $finfo */
			if($finfo->getSize() > (1024 * 512)){
				$finfo->compress(\Phar::GZ);
			}
		}
		if(!isset($args[0]) or (isset($args[0]) and $args[0] != "nogz")){
			$phar->compressFiles(\Phar::GZ);
		}
		$phar->stopBuffering();

		$sender->sendMessage($server->getName() . " " . $server->getPocketMineVersion() . " Phar file has been created on " . $pharPath);
		$sender->sendMessage("[MakeServer] Phar SG status: " . ($sourceGuardianEnabled ? "sg.enabled, detected " . $sourceGuardianFileCount . " SourceGuardian protected file(s)" : "no.sg, no SourceGuardian protected files detected"));

		return true;
	}
}
