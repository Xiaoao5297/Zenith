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

/**
 * On-demand PHP 8 compatibility fixer for legacy plugins.
 *
 * Plugins are loaded as-is. Only when PHP 8 raises a fatal compile error
 * (e.g. the removed `$s{0}` curly-offset syntax) does {@link Php8FatalRecovery}
 * invoke {@link fixPlugin()} to rewrite the offending plugin's source in place
 * and ask the operator to restart the server. No pre-scan, no cache, no
 * per-load traversal.
 */
final class PluginSourceCompatibility{

	private function __construct(){
	}

	/**
	 * Rewrite every PHP source file inside a plugin so it compiles under PHP 8.
	 *
	 * Auto-detects the plugin type from $pluginPath:
	 *  - *.phar   → rewrite files inside the Phar archive
	 *  - *.php    → single script plugin
	 *  - directory→ folder plugin (RecursiveDirectoryIterator)
	 *
	 * Returns true when at least one file was rewritten. Each rewrite is
	 * validated with `php -l`; rewrites that fail to lint are discarded so a
	 * broken rewrite can never corrupt the plugin.
	 *
	 * @param string     $pluginPath Absolute path to the plugin (phar/script/folder).
	 * @param string     $pluginName Display name used in log messages.
	 * @param mixed|null $logger     Logger with an info() method, or null.
	 *
	 * @return bool
	 */
	public static function fixPlugin(string $pluginPath, string $pluginName = "", $logger = null) : bool{
		$pluginPath = str_replace("\\", "/", $pluginPath);
		$pluginName = $pluginName !== "" ? $pluginName : basename($pluginPath);

		if(is_file($pluginPath) && strtolower(pathinfo($pluginPath, PATHINFO_EXTENSION)) === "phar"){
			$changed = self::fixPharPlugin($pluginPath);
		}elseif(is_file($pluginPath)){
			$changed = self::fixScriptPlugin($pluginPath);
		}elseif(is_dir($pluginPath)){
			$changed = self::fixFolderPlugin($pluginPath);
		}else{
			return false;
		}

		if($changed){
			self::logCompatibility($logger, $pluginName);
		}

		return $changed;
	}

	/**
	 * Scan the top level of the plugins directory and repair every plugin entry
	 * that contains PHP 8-incompatible source we know how to rewrite.
	 *
	 * @param string     $pluginDirectory Server plugins directory.
	 * @param mixed|null $logger          Logger with an info() method, or null.
	 *
	 * @return array{scanned:int,fixed:int,plugins:string[],errors:array<string,string>}
	 */
	public static function fixPluginDirectory(string $pluginDirectory, $logger = null) : array{
		$pluginDirectory = rtrim(str_replace("\\", "/", $pluginDirectory), "/");
		$result = [
			"scanned" => 0,
			"fixed" => 0,
			"plugins" => [],
			"errors" => [],
		];

		if($pluginDirectory === "" || !is_dir($pluginDirectory)){
			return $result;
		}

		self::warnIfPharReadonlyForDirectory($pluginDirectory, $logger);

		$iterator = new \DirectoryIterator($pluginDirectory);
		foreach($iterator as $entry){
			if($entry->isDot()){
				continue;
			}

			foreach(self::discoverPluginEntries(str_replace("\\", "/", $entry->getPathname())) as $pluginPath){
				++$result["scanned"];
				$pluginName = basename($pluginPath);
				try{
					if(self::fixPlugin($pluginPath, $pluginName, $logger)){
						++$result["fixed"];
						$result["plugins"][] = $pluginName;
					}
				}catch(\Throwable $e){
					$result["errors"][$pluginName] = $e->getMessage();
					if($logger !== null && method_exists($logger, "info")){
						$logger->info("PHP8 batch repair failed for " . $pluginName . ": " . $e->getMessage());
					}
				}
			}
		}

		return $result;
	}

	public static function isPharWritingEnabled() : bool{
		$value = ini_get("phar.readonly");
		if($value === false){
			return false;
		}

		$normalized = strtolower(trim((string) $value));
		return $normalized === "0" || $normalized === "" || $normalized === "off" || $normalized === "false" || $normalized === "no";
	}

	public static function getPharReadonlyHelpMessage() : string{
		$platform = defined("PHP_OS_FAMILY") ? PHP_OS_FAMILY : (DIRECTORY_SEPARATOR === "\\" ? "Windows" : "Linux/Unix");
		return "\n==============\n当前 " . $platform . " PHP运行环境没有将 phar.readonly 设置为0，PHP8自动修复无法重新打包phar插件。请打开当前PHP正在使用的php.ini，添加一行\n \"phar.readonly=0\"，\n保存后重启服务器再运行，以确保PHP8自动修复系统生效。\n==============\n当前 " . $platform . " PHP运行环境没有将 phar.readonly 设置为0，PHP8自动修复无法重新打包phar插件。请打开当前PHP正在使用的php.ini，添加一行\n \"phar.readonly=0\"，\n保存后重启服务器再运行，以确保PHP8自动修复系统生效。\n==============\n当前 " . $platform . " PHP运行环境没有将 phar.readonly 设置为0，PHP8自动修复无法重新打包phar插件。请打开当前PHP正在使用的php.ini，添加一行\n \"phar.readonly=0\"，\n保存后重启服务器再运行，以确保PHP8自动修复系统生效。\n==============\n当前 " . $platform . " PHP运行环境没有将 phar.readonly 设置为0，PHP8自动修复无法重新打包phar插件。请打开当前PHP正在使用的php.ini，添加一行\n \"phar.readonly=0\"，\n保存后重启服务器再运行，以确保PHP8自动修复系统生效。\n==============\n当前 " . $platform . " PHP运行环境没有将 phar.readonly 设置为0，PHP8自动修复无法重新打包phar插件。请打开当前PHP正在使用的php.ini，添加一行\n \"phar.readonly=0\"，\n保存后重启服务器再运行，以确保PHP8自动修复系统生效。\n==============\n当前 " . $platform . " PHP运行环境没有将 phar.readonly 设置为0，PHP8自动修复无法重新打包phar插件。请打开当前PHP正在使用的php.ini，添加一行\n \"phar.readonly=0\"，\n保存后重启服务器再运行，以确保PHP8自动修复系统生效。\n==============\n当前 " . $platform . " PHP运行环境没有将 phar.readonly 设置为0，PHP8自动修复无法重新打包phar插件。请打开当前PHP正在使用的php.ini，添加一行\n \"phar.readonly=0\"，\n保存后重启服务器再运行，以确保PHP8自动修复系统生效。\n==============\n当前 " . $platform . " PHP运行环境没有将 phar.readonly 设置为0，PHP8自动修复无法重新打包phar插件。请打开当前PHP正在使用的php.ini，添加一行\n \"phar.readonly=0\"，\n保存后重启服务器再运行，以确保PHP8自动修复系统生效。\n==============";
	}

	public static function warnIfPharReadonlyForDirectory(string $pluginDirectory, $logger = null) : bool{
		if(self::isPharWritingEnabled()){
			return false;
		}

		if(!self::hasPharPluginEntries($pluginDirectory)){
			return false;
		}

		self::logPharReadonlyHelp($logger);
		return true;
	}

	private static function fixPharPlugin(string $pharPath) : bool{
		$pharRoot = self::pharRoot($pharPath);
		$prefix = $pharRoot . "/";
		$phar = new \Phar($pharPath);
		$stub = $phar->getStub();
		$metadata = $phar->hasMetadata() ? $phar->getMetadata() : null;
		$files = [];
		$changed = false;

		foreach(new \RecursiveIteratorIterator($phar) as $fileInfo){
			if(!$fileInfo->isFile()){
				continue;
			}

			$pathName = str_replace("\\", "/", $fileInfo->getPathName());
			$localPath = strpos($pathName, $prefix) === 0 ? substr($pathName, strlen($prefix)) : $fileInfo->getFilename();
			$content = $fileInfo->getContent();
			$fileMetadata = $fileInfo->hasMetadata() ? $fileInfo->getMetadata() : null;
			if(!self::isPhpSourcePath($localPath)){
				$files[$localPath] = [$content, $fileMetadata];
				continue;
			}

			$rewritten = self::rewriteUnsupportedPhp8Syntax($content, $localPath);
			if($rewritten === null){
				$files[$localPath] = [$content, $fileMetadata];
				continue;
			}

			$files[$localPath] = [$rewritten, $fileMetadata];
			$changed = true;
		}
		unset($phar);

		if($changed){
			self::replacePhar($pharPath, $files, $stub, $metadata);
		}

		return $changed;
	}

	private static function replacePhar(string $pharPath, array $files, string $stub, $metadata) : void{
		self::assertPharWritingEnabled();

		$tmpPath = $pharPath . ".php8compat." . getmypid() . "." . mt_rand() . ".phar";
		$backupPath = $pharPath . ".php8compat.bak";
		if(is_file($tmpPath)){
			unlink($tmpPath);
		}
		if(is_file($backupPath)){
			unlink($backupPath);
		}

		$tmpPhar = new \Phar($tmpPath);
		$tmpPhar->startBuffering();
		if($metadata !== null){
			$tmpPhar->setMetadata($metadata);
		}
		foreach($files as $localPath => $entry){
			$tmpPhar[$localPath] = $entry[0];
			if($entry[1] !== null){
				$tmpPhar[$localPath]->setMetadata($entry[1]);
			}
		}
		$tmpPhar->setStub($stub);
		$tmpPhar->stopBuffering();
		unset($tmpPhar);

		if(!@rename($pharPath, $backupPath)){
			@unlink($tmpPath);
			throw new \RuntimeException("Unable to back up phar plugin " . $pharPath);
		}

		if(!@rename($tmpPath, $pharPath)){
			@rename($backupPath, $pharPath);
			@unlink($tmpPath);
			throw new \RuntimeException("Unable to replace phar plugin " . $pharPath);
		}

		@unlink($backupPath);
	}

	private static function assertPharWritingEnabled() : void{
		if(!self::isPharWritingEnabled()){
			throw new \RuntimeException(self::getPharReadonlyHelpMessage());
		}
	}

	private static function fixFolderPlugin(string $pluginPath) : bool{
		$pluginPath = rtrim($pluginPath, "/");
		$changed = false;

		$iterator = new \RecursiveIteratorIterator(new \RecursiveDirectoryIterator($pluginPath, \FilesystemIterator::SKIP_DOTS));
		foreach($iterator as $fileInfo){
			if(!$fileInfo->isFile()){
				continue;
			}

			$sourcePath = $fileInfo->getPathname();
			$localPath = str_replace("\\", "/", substr($sourcePath, strlen($pluginPath) + 1));
			if(!self::isPhpSourcePath($localPath)){
				continue;
			}

			$contents = file_get_contents($sourcePath);
			if(!is_string($contents)){
				continue;
			}

			$rewritten = self::rewriteUnsupportedPhp8Syntax($contents, $localPath);
			if($rewritten === null){
				continue;
			}

			if(file_put_contents($sourcePath, $rewritten) === false){
				throw new \RuntimeException("Unable to write rewritten plugin source " . $sourcePath);
			}
			$changed = true;
		}

		return $changed;
	}

	private static function fixScriptPlugin(string $scriptPath) : bool{
		if(!self::isPhpSourcePath(basename($scriptPath))){
			return false;
		}

		$contents = file_get_contents($scriptPath);
		if(!is_string($contents)){
			return false;
		}

		$rewritten = self::rewriteUnsupportedPhp8Syntax($contents, basename($scriptPath));
		if($rewritten === null){
			return false;
		}

		if(file_put_contents($scriptPath, $rewritten) === false){
			throw new \RuntimeException("Unable to write rewritten plugin script " . $scriptPath);
		}

		return true;
	}

	public static function isPhpSourcePath(string $localPath) : bool{
		$localPath = str_replace("\\", "/", $localPath);
		$extension = strtolower(pathinfo($localPath, PATHINFO_EXTENSION));
		if(in_array($extension, ["php", "php5", "phtml", "inc"], true)){
			return true;
		}

		return $extension === "" && strpos($localPath, "src/") === 0;
	}

	private static function discoverPluginEntries(string $path) : array{
		if(is_file($path)){
			$extension = strtolower(pathinfo($path, PATHINFO_EXTENSION));
			return ($extension === "phar" || self::isPhpSourcePath(basename($path))) ? [$path] : [];
		}

		if(!is_dir($path)){
			return [];
		}

		if(is_file(rtrim($path, "/") . "/plugin.yml") && is_dir(rtrim($path, "/") . "/src")){
			return [$path];
		}

		$plugins = [];
		foreach(new \DirectoryIterator($path) as $entry){
			if($entry->isDot()){
				continue;
			}

			foreach(self::discoverPluginEntries(str_replace("\\", "/", $entry->getPathname())) as $pluginPath){
				$plugins[] = $pluginPath;
			}
		}

		return $plugins;
	}

	private static function hasPharPluginEntries(string $pluginDirectory) : bool{
		$pluginDirectory = rtrim(str_replace("\\", "/", $pluginDirectory), "/");
		if($pluginDirectory === "" || !is_dir($pluginDirectory)){
			return false;
		}

		foreach(new \DirectoryIterator($pluginDirectory) as $entry){
			if($entry->isDot()){
				continue;
			}

			foreach(self::discoverPluginEntries(str_replace("\\", "/", $entry->getPathname())) as $pluginPath){
				if(is_file($pluginPath) && strtolower(pathinfo($pluginPath, PATHINFO_EXTENSION)) === "phar"){
					return true;
				}
			}
		}

		return false;
	}

	public static function rewriteSource(string $source) : string{
		if(strpos($source, "{") === false){
			return $source;
		}

		$tokens = token_get_all($source);
		$result = "";
		$braceStack = [];
		foreach($tokens as $index => $token){
			$text = is_array($token) ? $token[1] : $token;
			if($text === "{"){
				if(self::isOffsetOpening($tokens, $index)){
					$result .= "[";
					$braceStack[] = "offset";
				}else{
					$result .= "{";
					$braceStack[] = "normal";
				}
				continue;
			}

			if($text === "}"){
				$type = array_pop($braceStack);
				$result .= $type === "offset" ? "]" : "}";
				continue;
			}

			$result .= $text;
		}

		return $result;
	}

	private static function rewriteUnsupportedPhp8Syntax(string $source, string $displayPath) : ?string{
		$rewritten = self::rewriteSource($source);
		if($rewritten === $source){
			return null;
		}

		$rewrittenLint = self::lintSource($rewritten, $displayPath);
		if(!$rewrittenLint["ok"]){
			return null;
		}

		return $rewritten;
	}

	private static function lintSource(string $source, string $displayPath) : array{
		$tmpFile = tempnam(sys_get_temp_dir(), "lycore-plugin-lint-");
		if($tmpFile === false){
			throw new \RuntimeException("Unable to create temporary lint file for " . $displayPath);
		}

		$tmpPhpFile = $tmpFile . ".php";
		if(file_put_contents($tmpPhpFile, $source) === false){
			@unlink($tmpFile);
			throw new \RuntimeException("Unable to write temporary lint file for " . $displayPath);
		}

		$output = [];
		$status = 0;
		exec(escapeshellarg(PHP_BINARY) . " -l " . escapeshellarg($tmpPhpFile) . " 2>&1", $output, $status);
		@unlink($tmpPhpFile);
		@unlink($tmpFile);

		return [
			"ok" => $status === 0,
			"output" => implode("\n", $output),
		];
	}

	private static function logCompatibility($logger, string $pluginName) : void{
		if($logger !== null && method_exists($logger, "info")){
			// "已自动修复 <插件名> 的PHP8不兼容语法，请重启服务器使其生效"
			$logger->info("\xE5\xB7\xB2\xE8\x87\xAA\xE5\x8A\xA8\xE4\xBF\xAE\xE5\xA4\x8D " . $pluginName . " \xE7\x9A\x84PHP8\xE4\xB8\x8D\xE5\x85\xBC\xE5\xAE\xB9\xE8\xAF\xAD\xE6\xB3\x95\xEF\xBC\x8C\xE8\xAF\xB7\xE9\x87\x8D\xE5\x90\xAF\xE6\x9C\x8D\xE5\x8A\xA1\xE5\x99\xA8\xE4\xBD\xBF\xE5\x85\xB6\xE7\x94\x9F\xE6\x95\x88");
		}
	}

	private static function logPharReadonlyHelp($logger) : void{
		if($logger === null){
			return;
		}

		$message = self::getPharReadonlyHelpMessage();
		foreach(["warning", "emergency", "info"] as $method){
			if(method_exists($logger, $method)){
				$logger->{$method}($message);
				return;
			}
		}
	}

	private static function pharRoot(string $pharPath) : string{
		return "phar://" . str_replace("\\", "/", $pharPath);
	}

	private static function isOffsetOpening(array $tokens, int $index) : bool{
		$previousIndex = self::previousSignificantTokenIndex($tokens, $index - 1);
		if($previousIndex === null){
			return false;
		}

		$previous = $tokens[$previousIndex];
		if(is_array($previous)){
			if($previous[0] === T_VARIABLE){
				return true;
			}

			if($previous[0] === T_STRING){
				$beforePropertyIndex = self::previousSignificantTokenIndex($tokens, $previousIndex - 1);
				if($beforePropertyIndex !== null){
					$beforeProperty = $tokens[$beforePropertyIndex];
					if(is_array($beforeProperty) && ($beforeProperty[0] === T_OBJECT_OPERATOR || $beforeProperty[0] === T_DOUBLE_COLON)){
						return true;
					}
				}
			}

			return false;
		}

		return $previous === "]";
	}

	private static function previousSignificantTokenIndex(array $tokens, int $index) : ?int{
		for($i = $index; $i >= 0; --$i){
			$token = $tokens[$i];
			if(is_array($token) && in_array($token[0], [T_WHITESPACE, T_COMMENT, T_DOC_COMMENT], true)){
				continue;
			}

			return $i;
		}

		return null;
	}

}
