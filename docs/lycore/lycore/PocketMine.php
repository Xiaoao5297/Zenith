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

namespace {
	function safe_var_dump(){
		static $cnt = 0;
		foreach(func_get_args() as $var){
			switch(true){
				case is_array($var):
					echo str_repeat("  ", $cnt) . "array(" . count($var) . ") {" . PHP_EOL;
					foreach($var as $key => $value){
						echo str_repeat("  ", $cnt + 1) . "[" . (is_integer($key) ? $key : '"' . $key . '"') . "]=>" . PHP_EOL;
						++$cnt;
						safe_var_dump($value);
						--$cnt;
					}
					echo str_repeat("  ", $cnt) . "}" . PHP_EOL;
					break;
				case is_int($var):
					echo str_repeat("  ", $cnt) . "int(" . $var . ")" . PHP_EOL;
					break;
				case is_float($var):
					echo str_repeat("  ", $cnt) . "float(" . $var . ")" . PHP_EOL;
					break;
				case is_bool($var):
					echo str_repeat("  ", $cnt) . "bool(" . ($var === true ? "true" : "false") . ")" . PHP_EOL;
					break;
				case is_string($var):
					echo str_repeat("  ", $cnt) . "string(" . strlen($var) . ") \"$var\"" . PHP_EOL;
					break;
				case is_resource($var):
					echo str_repeat("  ", $cnt) . "resource() of type (" . get_resource_type($var) . ")" . PHP_EOL;
					break;
				case is_object($var):
					echo str_repeat("  ", $cnt) . "object(" . get_class($var) . ")" . PHP_EOL;
					break;
				case is_null($var):
					echo str_repeat("  ", $cnt) . "NULL" . PHP_EOL;
					break;
			}
		}
	}

	function dummy(){

	}
}

namespace lycore {
	use lycore\utils\Binary;
	use lycore\utils\MainLogger;
	use lycore\utils\ServerKiller;
	use lycore\utils\Terminal;
	use lycore\utils\Utils;
	use lycore\wizard\Installer;

	const VERSION = "v1.2";
	const API_VERSION = "2.0.0";
	const CODENAME = "LY Core";
	const MINECRAFT_VERSION = "v0.11.x - v0.15.x";
	const MINECRAFT_VERSION_NETWORK = "0.11...0.15";
	const LY_CORE_API_VERSION = '1.7.3';
	const LYCORE_API_VERSION = LY_CORE_API_VERSION;

	/*
	 * Startup code. Do not look at it, it may harm you.
	 * Most of them are hacks to fix date-related bugs, or basic functions used after this
	 * This is the only non-class based file on this project.
	 * Enjoy it as much as I did writing it. I don't want to do it again.
	 */

	if(\Phar::running(true) !== ""){
		@define('lycore\PATH', \Phar::running(true) . "/");
	}else{
		@define('lycore\PATH', \getcwd() . DIRECTORY_SEPARATOR);
	}

	if(version_compare("8.0", PHP_VERSION) > 0){
		echo "[提示] 您必须使用 PHP >= 8.0" . PHP_EOL;
		echo "[提示] 请使用官方提供的安装程序" . PHP_EOL;
		exit(1);
	}

	if(!extension_loaded("pthreads")){
		echo "[提示] 无法找到 pthreads 扩展" . PHP_EOL;
		echo "[提示] 请使用官方提供的安装程序" . PHP_EOL;
		exit(1);
	}

	if(!class_exists("ClassLoader", false)){
		require_once(\lycore\PATH . "src/spl/ClassLoader.php");
		require_once(\lycore\PATH . "src/spl/BaseClassLoader.php");
		require_once(\lycore\PATH . "src/lycore/CompatibleClassLoader.php");
	}

	$autoloader = new CompatibleClassLoader();
	$autoloader->addPath(\lycore\PATH . "src");
	$autoloader->addPath(\lycore\PATH . "src" . DIRECTORY_SEPARATOR . "spl");
	$autoloader->register(true);


	set_time_limit(0); //Who set it to 30 seconds?!?!

	gc_enable();
	error_reporting(-1);
	ini_set("allow_url_fopen", 1);
	ini_set("display_errors", 1);
	ini_set("display_startup_errors", 1);
	ini_set("default_charset", "utf-8");

	ini_set("memory_limit", -1);
	define('lycore\START_TIME', microtime(true));

	$opts = getopt("", ["data:", "plugins:", "no-wizard", "enable-profiler"]);

	define('lycore\DATA', isset($opts["data"]) ? $opts["data"] . DIRECTORY_SEPARATOR : \getcwd() . DIRECTORY_SEPARATOR);
	define('lycore\PLUGIN_PATH', isset($opts["plugins"]) ? $opts["plugins"] . DIRECTORY_SEPARATOR : \getcwd() . DIRECTORY_SEPARATOR . "plugins" . DIRECTORY_SEPARATOR);

	Terminal::init();

	define('lycore\ANSI', Terminal::hasFormattingCodes());

	if(!file_exists(\lycore\DATA)){
		mkdir(\lycore\DATA, 0777, true);
	}

	//Logger has a dependency on timezone, so we'll set it to UTC until we can get the actual timezone.
	date_default_timezone_set("UTC");

	$logger = new MainLogger(\lycore\DATA . "server.log", \lycore\ANSI);

	if(!ini_get("date.timezone")){
		if(($timezone = detect_system_timezone()) and date_default_timezone_set($timezone)){
			//Success! Timezone has already been set and validated in the if statement.
			//This here is just for redundancy just in case some program wants to read timezone data from the ini.
			ini_set("date.timezone", $timezone);
		}else{
			//If system timezone detection fails or timezone is an invalid value.
			if($response = Utils::getURL("http://ip-api.com/json")
				and $ip_geolocation_data = json_decode($response, true)
				and $ip_geolocation_data['status'] != 'fail'
				and date_default_timezone_set($ip_geolocation_data['timezone'])
			){
				//Again, for redundancy.
				ini_set("date.timezone", $ip_geolocation_data['timezone']);
			}else{
				ini_set("date.timezone", "UTC");
				date_default_timezone_set("UTC");
				$logger->warning("无法自动确定时区。时区设置不正确会导致控制台日志的时间戳错误。已默认设置为 \"UTC\" 。您可以在 php.ini 文件中更改它。");
			}
		}
	}else{
		/*
		 * This is here so that people don't come to us complaining and fill up the issue tracker when they put
		 * an incorrect timezone abbreviation in php.ini apparently.
		 */
		$timezone = ini_get("date.timezone");
		if(strpos($timezone, "/") === false){
			$default_timezone = timezone_name_from_abbr($timezone);
			ini_set("date.timezone", $default_timezone);
			date_default_timezone_set($default_timezone);
		} else {
			date_default_timezone_set($timezone);
		}
	}

	function detect_system_timezone(){
		switch(Utils::getOS()){
			case 'win':
				$keyPath = 'HKLM\\SYSTEM\\CurrentControlSet\\Control\\TimeZoneInformation';

				/*
				 * Get the timezone offset through the registry
				 *
				 * Sample Output var_dump
				 * array(13) {
				 *   [0]=>
				 *   string(0) ""
				 *   [1]=>
				 *   string(71) "HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\TimeZoneInformation"
				 *   [2]=>
				 *   string(35) "    Bias    REG_DWORD    0xfffffe20"
				 *   [3]=>
				 *   string(43) "    DaylightBias    REG_DWORD    0xffffffc4"
				 *   [4]=>
				 *   string(45) "    DaylightName    REG_SZ    @tzres.dll,-571"
				 *   [5]=>
				 *   string(67) "    DaylightStart    REG_BINARY    00000000000000000000000000000000"
				 *   [6]=>
				 *   string(36) "    StandardBias    REG_DWORD    0x0"
				 *   [7]=>
				 *   string(45) "    StandardName    REG_SZ    @tzres.dll,-572"
				 *   [8]=>
				 *   string(67) "    StandardStart    REG_BINARY    00000000000000000000000000000000"
				 *   [9]=>
				 *   string(52) "    TimeZoneKeyName    REG_SZ    China Standard Time"
				 *   [10]=>
				 *   string(51) "    DynamicDaylightTimeDisabled    REG_DWORD    0x0"
				 *   [11]=>
				 *   string(45) "    ActiveTimeBias    REG_DWORD    0xfffffe20"
				 *   [12]=>
				 *   string(0) ""
				 *	}
				 */
				exec("reg query " . escapeshellarg($keyPath), $output);

				foreach($output as $line){
					if(preg_match('/ActiveTimeBias\s+REG_DWORD\s+0x([0-9a-fA-F]+)/', $line, $matches) > 0){
						$offset_minutes = hexdec(trim($matches[1]));
						if($offset_minutes > 0x7fffffff){
							$offset_minutes -= 0x100000000;
						}

						if($offset_minutes === 0){
							return "UTC";
						}

						$sign = $offset_minutes <= 0 ? '+' : '-'; // Windows timezone + and - are opposite.
						$abs_minutes = abs($offset_minutes);
						$hours = floor($abs_minutes / 60);
						$minutes = $abs_minutes % 60;

						$offset = sprintf("%s%02d:%02d", $sign, $hours, $minutes);

						return parse_offset($offset);
					}
				}

				return false;
				break;
			case 'linux':
				// Ubuntu / Debian.
				if(file_exists('/etc/timezone')){
					$data = file_get_contents('/etc/timezone');
					if($data){
						return trim($data);
					}
				}

				// RHEL / CentOS
				if(file_exists('/etc/sysconfig/clock')){
					$data = parse_ini_file('/etc/sysconfig/clock');
					if(!empty($data['ZONE'])){
						return trim($data['ZONE']);
					}
				}

				//Portable method for incompatible linux distributions.

				$offset = trim(exec('date +%:z'));

				if($offset == "+00:00"){
					return "UTC";
				}

				return parse_offset($offset);
				break;
			case 'mac':
				if(is_link('/etc/localtime')){
					$filename = readlink('/etc/localtime');
					if(strpos($filename, '/usr/share/zoneinfo/') === 0){
						$timezone = substr($filename, 20);
						return trim($timezone);
					}
				}

				return false;
				break;
			default:
				return false;
				break;
		}
	}

	/**
	 * @param string $offset In the format of +09:00, +02:00, -04:00 etc.
	 *
	 * @return string
	 */
	function parse_offset($offset){
		//Make signed offsets unsigned for date_parse
		if(strpos($offset, '-') !== false){
			$negative_offset = true;
			$offset = str_replace('-', '', $offset);
		}else{
			if(strpos($offset, '+') !== false){
				$negative_offset = false;
				$offset = str_replace('+', '', $offset);
			}else{
				return false;
			}
		}

		$parsed = date_parse($offset);
		$offset = $parsed['hour'] * 3600 + $parsed['minute'] * 60 + $parsed['second'];

		//After date_parse is done, put the sign back
		if($negative_offset == true){
			$offset = -abs($offset);
		}

		//And then, look the offset up.
		//timezone_name_from_abbr is not used because it returns false on some(most) offsets because it's mapping function is weird.
		//That's been a bug in PHP since 2008!
		foreach(timezone_abbreviations_list() as $zones){
			foreach($zones as $timezone){
				if($timezone['offset'] == $offset){
					return $timezone['timezone_id'];
				}
			}
		}

		return false;
	}

	if(isset($opts["enable-profiler"])){
		if(function_exists("profiler_enable")){
			\profiler_enable();
			$logger->notice("正在分析执行情况");
		}else{
			$logger->notice("未找到分析器，请安装，地址： https://github.com/krakjoe/profiler");
		}
	}

	function kill($pid){
		switch(Utils::getOS()){
			case "win":
				exec("taskkill.exe /F /PID " . ((int) $pid) . " > NUL");
				break;
			case "mac":
			case "linux":
			default:
				if(function_exists("posix_kill")){
					posix_kill($pid, SIGKILL);
				}else{
					exec("kill -9 " . ((int)$pid) . " > /dev/null 2>&1");
				}
		}
	}

	/**
	 * @param object $value
	 * @param bool   $includeCurrent
	 *
	 * @return int
	 */
	function getReferenceCount($value, $includeCurrent = true){
		ob_start();
		debug_zval_dump($value);
		$ret = explode("\n", ob_get_contents());
		ob_end_clean();

		if(count($ret) >= 1 and preg_match('/^.* refcount\\(([0-9]+)\\)\\{$/', trim($ret[0]), $m) > 0){
			return ((int) $m[1]) - ($includeCurrent ? 3 : 4); //$value + zval call + extra call
		}
		return -1;
	}

	function getTrace($start = 1, $trace = null){
		if($trace === null){
			if(function_exists("xdebug_get_function_stack")){
				$trace = array_reverse(xdebug_get_function_stack());
			}else{
				$e = new \Exception();
				$trace = $e->getTrace();
			}
		}

		$messages = [];
		$j = 0;
		for($i = (int) $start; isset($trace[$i]); ++$i, ++$j){
			$params = "";
			if(isset($trace[$i]["args"]) or isset($trace[$i]["params"])){
				if(isset($trace[$i]["args"])){
					$args = $trace[$i]["args"];
				}else{
					$args = $trace[$i]["params"];
				}
				foreach($args as $name => $value){
					$params .= (is_object($value) ? get_class($value) . " " . (method_exists($value, "__toString") ? $value->__toString() : "object") : gettype($value) . " " . (is_array($value) ? "Array()" : Utils::printable(@strval($value)))) . ", ";
				}
			}
			$messages[] = "#$j " . (isset($trace[$i]["file"]) ? cleanPath($trace[$i]["file"]) : "") . "(" . (isset($trace[$i]["line"]) ? $trace[$i]["line"] : "") . "): " . (isset($trace[$i]["class"]) ? $trace[$i]["class"] . (($trace[$i]["type"] === "dynamic" or $trace[$i]["type"] === "->") ? "->" : "::") : "") . $trace[$i]["function"] . "(" . Utils::printable(substr($params, 0, -2)) . ")";
		}

		return $messages;
	}

	function cleanPath($path){
		return rtrim(str_replace(["\\", ".php", "phar://", rtrim(str_replace(["\\", "phar://"], ["/", ""], \lycore\PATH), "/"), rtrim(str_replace(["\\", "phar://"], ["/", ""], \lycore\PLUGIN_PATH), "/")], ["/", "", "", "", ""], $path), "/");
	}

	$errors = 0;

	if(php_sapi_name() !== "cli"){
		$logger->critical("您必须使用命令行界面运行 LY Core");
		++$errors;
	}

	if(!extension_loaded("sockets")){
		$logger->critical("无法找到 Socket 扩展");
		++$errors;
	}

	$pthreads_version = phpversion("pthreads");
	if(substr_count($pthreads_version, ".") < 2){
		$pthreads_version = "0.$pthreads_version";
	}
	if(version_compare($pthreads_version, "3.1.5") < 0){
		$logger->critical("需要 pthreads >= 3.1.5，但你当前的版本是 $pthreads_version");
		++$errors;
	}

	if(!extension_loaded("uopz")){
		//$logger->notice("Couldn't find the uopz extension. Some functions may be limited");
	}

	if(extension_loaded("pocketmine")){
		if(version_compare(phpversion("pocketmine"), "0.0.1") < 0){
			$logger->critical("你拥有原生的 PocketMine 扩展，但你的版本低于 0.0.1");
			++$errors;
		}elseif(version_compare(phpversion("pocketmine"), "0.0.4") > 0){
			$logger->critical("你拥有原生 PocketMine 扩展，但你的版本高于 0.0.4");
			++$errors;
		}
	}

	if(!extension_loaded("curl")){
		$logger->critical("无法找到 cURL 扩展");
		++$errors;
	}

	if(!extension_loaded("yaml")){
		$logger->critical("无法找到 YAML 扩展");
		++$errors;
	}

	if(!extension_loaded("sqlite3")){
		$logger->critical("无法找到 SQLite3 扩展");
		++$errors;
	}

	if(!extension_loaded("zlib")){
		$logger->critical("无法找到 zlib 扩展");
		++$errors;
	}

	if($errors > 0){
		$logger->critical("请使用官方提供的安装程序，或重新编译 PHP。");
		$logger->shutdown();
		$logger->join();
		exit(1); //Exit with error
	}

	if(file_exists(\lycore\PATH . ".git/refs/heads/master")){ //Found Git information!
		define('lycore\GIT_COMMIT', strtolower(trim(file_get_contents(\lycore\PATH . ".git/refs/heads/master"))));
	}else{ //Unknown :(
		define('lycore\GIT_COMMIT', str_repeat("00", 20));
	}

	@define("ENDIANNESS", (pack("d", 1) === "\77\360\0\0\0\0\0\0" ? Binary::BIG_ENDIAN : Binary::LITTLE_ENDIAN));
	@define("INT32_MASK", is_int(0xffffffff) ? 0xffffffff : -1);
	@ini_set("opcache.mmap_base", bin2hex(Utils::getRandomBytes(8, false))); //Fix OPCache address errors

	$lang = "unknown";
	if(!file_exists(\lycore\DATA . "server.properties") and !isset($opts["no-wizard"])){
		$inst = new Installer();
		$lang = $inst->getDefaultLang();
	}

	/*if(\Phar::running(true) === ""){
		$logger->warning("Non-packaged LY Core installation detected, do not use on production.");
	}*/

	ThreadManager::init();
	$server = new Server($autoloader, $logger, \lycore\PATH, \lycore\DATA, \lycore\PLUGIN_PATH, $lang);

	$logger->info("正在结束其他进程");

	foreach(ThreadManager::getInstance()->getAll() as $id => $thread){
		$logger->debug("正在结束 " . (new \ReflectionClass($thread))->getShortName() . " 进程");
		$thread->quit();
	}

	$killer = new ServerKiller(8);
	$killer->start();

	$logger->shutdown();
	$logger->join();

	echo "服务器已关闭" . Terminal::$FORMAT_RESET . "\n";

	exit(0);

}
