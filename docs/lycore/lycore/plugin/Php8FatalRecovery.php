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

namespace lycore\plugin;

use lycore\utils\MainLogger;

/**
 * Recovers from PHP 8 fatal compile errors caused by legacy plugin source.
 *
 * Plugins are loaded as-is; when PHP 8 hits removed syntax (e.g. the curly
 * offset access `$s{0}`) it raises an E_COMPILE_ERROR / E_PARSE. This handler,
 * registered before Server::crashDump(), locates the offending plugin from the
 * error's file path, rewrites the whole plugin in place via
 * {@link PluginSourceCompatibility::fixPlugin()}, and records that the legacy
 * shutdown recovery already fixed the trigger plugin. Server::crashDump() also
 * invokes {@link recoverBeforeCrashReport()} before sending crash reports so it
 * can batch-scan the plugins directory, notify players, and delay shutdown.
 */
final class Php8FatalRecovery{

	/** @var bool Guards against re-entering handle() within one process. */
	private static $handled = false;

	/** @var bool Guards against re-entering recoverBeforeCrashReport(). */
	private static $crashReportRecovered = false;

	/** @var bool True when the legacy shutdown handler repaired the offending plugin. */
	private static $legacyHandlerFixed = false;

	/** Marker file written next to the server data path so the next start can confirm the fix. */
	private const MARKER_FILENAME = ".php8fixed.json";
	private const FREEZE_MESSAGE = "PHP8插件兼容性致命错误正在自动修复，服务器已临时冻结。";
	private const FIXED_MESSAGE = "所有插件都已经被修复了，需要重启服务器生效。服务器将在5秒后关闭。";

	private function __construct(){
	}

	/**
	 * Register the legacy shutdown handler. Server::crashDump() still performs
	 * the player-facing batch recovery before crash report submission.
	 */
	public static function register() : void{
		register_shutdown_function([self::class, "handle"]);
	}

	public static function handle() : void{
		if(self::$handled){
			return;
		}
		self::$handled = true;

		$error = error_get_last();
		if(!is_array($error)){
			return;
		}

		// Only act on fatal compile/runtime errors. These are the categories
		// under which PHP 8 surfaces the removed curly-offset syntax and other
		// source-level incompatibilities.
		$fatalTypes = E_ERROR | E_PARSE | E_COMPILE_ERROR | E_CORE_ERROR;
		if((((int) ($error["type"] ?? 0)) & $fatalTypes) === 0){
			return;
		}

		$file = $error["file"] ?? "";
		if($file === ""){
			return;
		}

		$pluginRoot = self::locatePluginRoot($file);
		if($pluginRoot === null){
			return; // Not a plugin file — leave it to crashDump.
		}

		$pluginName = self::readPluginName($pluginRoot);

		$logger = self::safeLogger();
		$pluginPath = self::pluginPath();
		if($pluginPath !== null && self::recoverPluginDirectoryFromFatal($file, $pluginPath, $logger, $pluginName)){
			return;
		}

		try{
			$changed = PluginSourceCompatibility::fixPlugin($pluginRoot, $pluginName, $logger);
		}catch(\Throwable $e){
			if($logger !== null){
				$logger->info("PHP8自动修复<" . $pluginName . ">时出错: " . $e->getMessage());
			}
			return;
		}

		if(!$changed){
			// The fatal was not from a PHP8 syntax pattern we can rewrite.
			return;
		}

		self::$legacyHandlerFixed = true;
		self::writeMarker($pluginName);

		if($logger !== null){
			// "已自动修复 <插件名> 的PHP8不兼容语法，致命错误已处理，请重启服务器使其生效。"
			$logger->emergency("\xE5\xB7\xB2\xE8\x87\xAA\xE5\x8A\xA8\xE4\xBF\xAE\xE5\xA4\x8D " . $pluginName . " \xE7\x9A\x84PHP8\xE4\xB8\x8D\xE5\x85\xBC\xE5\xAE\xB9\xE8\xAF\xAD\xE6\xB3\x95\xEF\xBC\x8C\xE8\x87\xB4\xE5\x91\xBD\xE9\x94\x99\xE8\xAF\xAF\xE5\xB7\xB2\xE5\xA4\x84\xE7\x90\x86\xEF\xBC\x8C\xE8\xAF\xB7\xE9\x87\x8D\xE5\x90\xAF\xE6\x9C\x8D\xE5\x8A\xA1\xE5\x99\xA8\xE4\xBD\xBF\xE5\x85\xB6\xE7\x94\x9F\xE6\x95\x88\xE3\x80\x82");
		}
	}

	public static function recoverPluginDirectoryFromFatal(string $fatalFile, string $pluginPath, $logger = null, string $triggerPluginName = "") : bool{
		$pluginRoot = self::locatePluginRootInDirectory($fatalFile, $pluginPath);
		if($pluginRoot === null){
			return false;
		}

		if($triggerPluginName === ""){
			$triggerPluginName = self::readPluginName($pluginRoot);
		}

		try{
			$result = PluginSourceCompatibility::fixPluginDirectory($pluginPath, $logger);
		}catch(\Throwable $e){
			if($logger !== null && method_exists($logger, "info")){
				$logger->info("PHP8批量自动修复插件目录时出错: " . $e->getMessage());
			}
			return false;
		}

		$fixedCount = (int) ($result["fixed"] ?? 0);
		if(!empty($result["errors"]) || $fixedCount <= 0){
			return false;
		}

		self::$legacyHandlerFixed = true;
		$fixedPlugins = implode(", ", $result["plugins"] ?? []);
		self::writeMarker($fixedPlugins !== "" ? $fixedPlugins : $triggerPluginName);

		if($logger !== null){
			$message = "PHP8批量自动修复完成，已修复 " . $fixedCount . " 个插件，需要重启服务器生效。";
			if(method_exists($logger, "emergency")){
				$logger->emergency($message);
			}elseif(method_exists($logger, "info")){
				$logger->info($message);
			}
		}

		return true;
	}

	public static function recoverBeforeCrashReport($server, $dump, int $shutdownDelaySeconds = 5) : bool{
		if(self::$crashReportRecovered){
			return false;
		}

		if(!is_object($dump) || !method_exists($dump, "getData")){
			return false;
		}

		$data = $dump->getData();
		if(!is_array($data) || !isset($data["error"]) || !is_array($data["error"])){
			return false;
		}

		$error = $data["error"];
		if(!self::isFatalErrorType($error["type"] ?? null)){
			return false;
		}

		$pluginPath = self::pluginPathFromServer($server);
		if($pluginPath === null){
			return false;
		}

		$file = self::findPluginErrorFile($data, $pluginPath);
		if($file === null){
			return false;
		}

		$message = (string) ($error["message"] ?? "");
		if(!self::isPhp8CompatibilityFatal($message, $file)){
			return false;
		}

		self::$crashReportRecovered = true;

		$logger = self::loggerFromServer($server);
		if(method_exists($server, "shutdown")){
			$server->shutdown(false, self::FREEZE_MESSAGE);
		}

		if($logger !== null && method_exists($logger, "emergency")){
			$logger->emergency("检测到插件PHP8不兼容致命错误，已冻结服务器并开始批量扫描修复插件目录。");
		}

		try{
			$result = PluginSourceCompatibility::fixPluginDirectory($pluginPath, $logger);
		}catch(\Throwable $e){
			if($logger !== null && method_exists($logger, "info")){
				$logger->info("PHP8批量自动修复插件目录时出错: " . $e->getMessage());
			}
			return false;
		}

		$fixedCount = (int) ($result["fixed"] ?? 0);
		if(!empty($result["errors"])){
			if($logger !== null && method_exists($logger, "info")){
				$logger->info("PHP8批量自动修复有插件处理失败，继续按普通崩溃处理。");
			}
			return false;
		}

		if($fixedCount <= 0 && !self::$legacyHandlerFixed){
			if($logger !== null && method_exists($logger, "info")){
				$logger->info("PHP8批量自动修复未找到可改写的插件源码，继续按普通崩溃处理。");
			}
			return false;
		}

		$fixedPlugins = implode(", ", $result["plugins"] ?? []);
		self::writeMarker($fixedPlugins !== "" ? $fixedPlugins : "所有插件");

		if($logger !== null && method_exists($logger, "emergency")){
			if($fixedCount > 0){
				$logger->emergency("PHP8批量自动修复完成，已修复 " . $fixedCount . " 个插件，需要重启服务器生效。");
			}else{
				$logger->emergency("PHP8批量自动修复完成，触发崩溃的插件已在早期恢复流程中修复，需要重启服务器生效。");
			}
		}

		if(method_exists($server, "broadcastMessage")){
			$server->broadcastMessage(self::FIXED_MESSAGE);
		}

		if($shutdownDelaySeconds > 0){
			sleep($shutdownDelaySeconds);
		}

		return true;
	}

	/**
	 * Map a fatal-error file path back to the plugin root it belongs to.
	 * Returns the absolute plugin path (.phar / folder / script), or null when
	 * the file does not live under the plugins directory.
	 */
	private static function locatePluginRoot(string $file) : ?string{
		$pluginPath = self::pluginPath();
		if($pluginPath === null){
			return null;
		}

		return self::locatePluginRootInDirectory($file, $pluginPath);
	}

	private static function locatePluginRootInDirectory(string $file, string $pluginPath) : ?string{
		$file = str_replace("\\", "/", $file);
		$pluginPath = rtrim(str_replace("\\", "/", $pluginPath), "/");

		if(strpos($file, "phar://") === 0){
			$physical = substr($file, strlen("phar://"));
		}else{
			$physical = $file;
		}

		if(strpos($physical, $pluginPath . "/") !== 0){
			return null;
		}

		$relative = substr($physical, strlen($pluginPath) + 1);
		$firstSlash = strpos($relative, "/");
		$rootName = $firstSlash === false ? $relative : substr($relative, 0, $firstSlash);
		if($rootName === ""){
			return null;
		}

		$root = $pluginPath . "/" . $rootName;
		if(strtolower(pathinfo($rootName, PATHINFO_EXTENSION)) === "phar"){
			return is_file($root) ? $root : null;
		}

		if(is_dir($root) || is_file($root)){
			return $root;
		}

		return null;
	}

	private static function findPluginErrorFile(array $dumpData, string $pluginPath) : ?string{
		$candidates = [];
		if(isset($dumpData["lastError"]) && is_array($dumpData["lastError"]) && isset($dumpData["lastError"]["fullFile"])){
			$candidates[] = (string) $dumpData["lastError"]["fullFile"];
		}
		if(isset($dumpData["error"]) && is_array($dumpData["error"])){
			if(isset($dumpData["error"]["fullFile"])){
				$candidates[] = (string) $dumpData["error"]["fullFile"];
			}
			if(isset($dumpData["error"]["file"])){
				$candidates[] = (string) $dumpData["error"]["file"];
			}
		}

		$lastError = error_get_last();
		if(is_array($lastError) && isset($lastError["file"])){
			$candidates[] = (string) $lastError["file"];
		}

		foreach($candidates as $file){
			$physical = self::normalizeErrorFilePath($file, $pluginPath);
			if($physical !== null && self::locatePluginRootInDirectory($physical, $pluginPath) !== null){
				return $physical;
			}
		}

		return null;
	}

	private static function normalizeErrorFilePath(string $file, string $pluginPath) : ?string{
		if($file === ""){
			return null;
		}

		$file = str_replace("\\", "/", $file);
		$pluginPath = rtrim(str_replace("\\", "/", $pluginPath), "/");
		if(strpos($file, $pluginPath . "/") === 0 || strpos($file, "phar://" . $pluginPath . "/") === 0){
			return $file;
		}

		$pluginDirName = basename($pluginPath);
		$dataPath = substr($pluginPath, 0, -strlen("/" . $pluginDirName));
		$cleanFile = ltrim($file, "/");
		if(strpos($cleanFile, $pluginDirName . "/") === 0){
			return rtrim($dataPath, "/") . "/" . $cleanFile;
		}

		return null;
	}

	private static function readPluginName(string $pluginRoot) : string{
		// Phar plugin: read plugin.yml from the archive.
		if(is_file($pluginRoot) && strtolower(pathinfo($pluginRoot, PATHINFO_EXTENSION)) === "phar"){
			try{
				$phar = new \Phar($pluginRoot);
				if(isset($phar["plugin.yml"])){
					$yaml = $phar["plugin.yml"]->getContent();
					$name = self::parsePluginNameFromYaml($yaml);
					if($name !== ""){
						return $name;
					}
				}
			}catch(\Throwable $e){
				// fall through to basename
			}

			return basename($pluginRoot);
		}

		// Folder plugin: read plugin.yml from the directory.
		if(is_dir($pluginRoot)){
			$yml = $pluginRoot . "/plugin.yml";
			if(is_file($yml)){
				$contents = @file_get_contents($yml);
				if(is_string($contents)){
					$name = self::parsePluginNameFromYaml($contents);
					if($name !== ""){
						return $name;
					}
				}
			}

			return basename($pluginRoot);
		}

		// Script plugin: no plugin.yml, use the file name.
		return basename($pluginRoot);
	}

	private static function parsePluginNameFromYaml(string $yaml) : string{
		if(preg_match('/^[ \t]*name[ \t]*:[ \t]*(.+?)\s*$/m', $yaml, $matches)){
			$name = trim($matches[1]);
			$name = trim($name, "\"'");

			return $name;
		}

		return "";
	}

	private static function safeLogger(){
		if(!class_exists(MainLogger::class, false)){
			return null;
		}

		try{
			$logger = MainLogger::$logger;
			if($logger !== null){
				return $logger;
			}

			return MainLogger::getLogger();
		}catch(\Throwable $e){
			return null;
		}
	}

	private static function pluginPath() : ?string{
		if(defined("lycore\\PLUGIN_PATH")){
			return constant("lycore\\PLUGIN_PATH");
		}

		return null;
	}

	private static function loggerFromServer($server){
		if(is_object($server) && method_exists($server, "getLogger")){
			try{
				return $server->getLogger();
			}catch(\Throwable $e){
				return null;
			}
		}

		return self::safeLogger();
	}

	private static function pluginPathFromServer($server) : ?string{
		if(is_object($server) && method_exists($server, "getPluginPath")){
			try{
				$path = $server->getPluginPath();
				if(is_string($path) && $path !== ""){
					return $path;
				}
			}catch(\Throwable $e){
				return null;
			}
		}

		return self::pluginPath();
	}

	private static function isFatalErrorType($type) : bool{
		if(is_int($type)){
			$fatalTypes = E_ERROR | E_PARSE | E_COMPILE_ERROR | E_CORE_ERROR;
			return ($type & $fatalTypes) !== 0;
		}

		if(!is_string($type)){
			return false;
		}

		return in_array($type, ["E_ERROR", "E_PARSE", "E_COMPILE_ERROR", "E_CORE_ERROR"], true);
	}

	private static function isPhp8CompatibilityFatal(string $message, string $file) : bool{
		$lower = strtolower($message);
		if((strpos($lower, "curly") !== false && strpos($lower, "no longer supported") !== false) || strpos($lower, "array and string offset access") !== false){
			return true;
		}

		$source = @file_get_contents($file);
		if(!is_string($source)){
			return false;
		}

		return PluginSourceCompatibility::rewriteSource($source) !== $source;
	}

	private static function dataPath() : ?string{
		if(defined("lycore\\DATA")){
			return constant("lycore\\DATA");
		}

		return null;
	}

	private static function writeMarker(string $pluginName) : void{
		$dataPath = self::dataPath();
		if($dataPath === null){
			return;
		}

		$marker = [
			"plugin" => $pluginName,
			"time" => time(),
		];

		$encoded = json_encode($marker, JSON_PRETTY_PRINT | JSON_UNESCAPED_SLASHES);
		if(is_string($encoded)){
			@file_put_contents($dataPath . self::MARKER_FILENAME, $encoded . "\n");
		}
	}

	/**
	 * Read and delete the marker file left by a previous auto-fix.
	 * Returns the plugin name that was fixed, or null if no marker exists.
	 */
	public static function consumeMarker() : ?string{
		$dataPath = self::dataPath();
		if($dataPath === null){
			return null;
		}

		$path = $dataPath . self::MARKER_FILENAME;
		if(!is_file($path)){
			return null;
		}

		$contents = @file_get_contents($path);
		@unlink($path);
		if(!is_string($contents)){
			return null;
		}

		$data = json_decode($contents, true);
		if(is_array($data) && isset($data["plugin"]) && is_string($data["plugin"])){
			return $data["plugin"];
		}

		return null;
	}

}
