<?php
namespace synapse\network\synlib;

use pocketmine\Thread;

class SynapseClient extends Thread{
	const VERSION = "0.1.0";

	/** @var \parallel\Channel */
	private $externalQueue, $internalQueue;
	/** @var string */
	private $extChanName = "";
	/** @var string */
	private $intChanName = "";
	private $mainPath;
	private $needAuth = false;

	public function __construct($port, $interface = "127.0.0.1"){
		$this->interface = $interface;
		$this->port = (int) $port;
		if($port < 1 or $port > 65536){
			throw new \Exception("Invalid port range");
		}

		$this->shutdown = false;

		$id = spl_object_id($this);
		$this->extChanName = "syn_ext_{$id}";
		$this->intChanName = "syn_int_{$id}";
		$this->externalQueue = \parallel\Channel::make($this->extChanName, \parallel\Channel::Infinite);
		$this->internalQueue = \parallel\Channel::make($this->intChanName, \parallel\Channel::Infinite);

		if(\Phar::running(true) !== ""){
			$this->mainPath = \Phar::running(true);
		}else{
			$this->mainPath = \getcwd() . DIRECTORY_SEPARATOR;
		}

		$this->start();
	}

	/** @var string */
	private $interface;
	/** @var int */
	private $port;
	private $shutdown = true;

	public function isNeedAuth() : bool{
		return $this->needAuth;
	}

	public function setNeedAuth(bool $need){
		$this->needAuth = $need;
	}

	public function quit(){
		$this->shutdown = true;
		try{
			\parallel\Channel::destroy($this->extChanName);
		}catch(\Throwable $e){}
		try{
			\parallel\Channel::destroy($this->intChanName);
		}catch(\Throwable $e){}
	}

	public function start(int $options = 0){
		$bootstrapPath = \pocketmine\PATH;
		$port = $this->port;
		$interface = $this->interface;
		$mainPath = $this->mainPath;
		$extName = $this->extChanName;
		$intName = $this->intChanName;

		$this->runtime = new \parallel\Runtime();
		$this->future = $this->runtime->run(function($bootstrapPath, $port, $interface, $mainPath, $extName, $intName){
			require_once $bootstrapPath . "src/spl/ClassLoader.php";
			require_once $bootstrapPath . "src/spl/BaseClassLoader.php";
			require_once $bootstrapPath . "src/pocketmine/CompatibleClassLoader.php";

			$loader = new \CompatibleClassLoader();
			$loader->addPath($bootstrapPath . "src");
			$loader->addPath($bootstrapPath . "src" . DIRECTORY_SEPARATOR . "spl");
			$loader->register(true);

			$extChan = \parallel\Channel::open($extName);
			$intChan = \parallel\Channel::open($intName);

			gc_enable();
			error_reporting(-1);
			ini_set("display_errors", 1);
			ini_set("display_startup_errors", 1);

			// Create a proxy object that communicates via channels (like RakLibServer)
			$proxy = new class($extChan, $intChan, $mainPath){
				public $externalQueue, $internalQueue;
				public $shutdown = false;
				public $needAuth = false;
				private $mainPath;

				public function __construct($ext, $int, $mainPath){
					$this->externalQueue = $ext;
					$this->internalQueue = $int;
					$this->mainPath = $mainPath;
				}

				public function getExternalQueue(){ return $this->externalQueue; }
				public function getInternalQueue(){ return $this->internalQueue; }

				public function pushMainToThreadPacket($str){ $this->internalQueue->send($str); }
				public function readMainToThreadPacket(){
					try{ return $this->internalQueue->tryRecv(); }
					catch(\Throwable $e){ return ""; }
				}
				public function pushThreadToMainPacket($str){ $this->externalQueue->send($str); }
				public function readThreadToMainPacket(){
					try{ return $this->externalQueue->recv(); }
					catch(\Throwable $e){ return ""; }
				}
				public function isShutdown(){ return $this->shutdown; }
				public function shutdown(){ $this->shutdown = true; }
				public function isNeedAuth(){ return $this->needAuth; }
				public function setNeedAuth($v){ $this->needAuth = $v; }
				public function getPort(){ return $port; }
				public function getInterface(){ return $interface; }

				public function getLogger(){
					return new class() extends \ThreadedLogger{
						public function log($level, $message){}
						public function emergency($message){}
						public function alert($message){}
						public function critical($message){}
						public function error($message){}
						public function warning($message){}
						public function notice($message){}
						public function info($message){}
						public function debug($message){}
						public function logException(\Throwable $e, $trace = null){}
						public function shutdown(){}
					};
				}

				public function getTrace($start = 1, $trace = null){ return []; }
				public function cleanPath($path){
					return rtrim(str_replace(["\\", ".php", "phar://", rtrim(str_replace(["\\", "phar://"], ["/", ""], $this->mainPath), "/")], ["/", "", "", ""], $path), "/");
				}
			};

			$proxy->shutdown = false;

			set_error_handler(function($errno, $errstr, $errfile, $errline) use ($proxy){
				if(error_reporting() === 0) return false;
				$errfile = $proxy->cleanPath($errfile);
				echo "[SynLib] Error: \"$errstr\" in \"$errfile\" at line $errline\n";
				return true;
			}, E_ALL);

			register_shutdown_function(function() use ($proxy){
				if(!$proxy->isShutdown()){
					echo "[SynLib] SynLib crashed!\n";
				}
			});

			try{
				$socket = new SynapseSocket($proxy->getLogger(), $port, $interface);
				new ServerConnection($proxy, $socket);
			}catch(\Throwable $e){
				echo "[SynLib] " . $e->getMessage() . "\n";
			}
		}, [$bootstrapPath, $port, $interface, $mainPath, $extName, $intName]);
	}

	public function getExternalQueue(){
		return $this->externalQueue;
	}

	public function getInternalQueue(){
		return $this->internalQueue;
	}

	public function pushMainToThreadPacket($str){
		$this->internalQueue->send($str);
	}

	public function readMainToThreadPacket(){
		try{
			return $this->internalQueue->tryRecv();
		}catch(\parallel\Channel\Error\Existence $e){
			return "";
		}catch(\parallel\Channel\Error\Closed $e){
			return "";
		}
	}

	public function pushThreadToMainPacket($str){
		$this->externalQueue->send($str);
	}

	public function readThreadToMainPacket(){
		try{
			return $this->externalQueue->recv();
		}catch(\parallel\Channel\Error\Closed $e){
			return "";
		}
	}

	public function isShutdown(){
		return $this->shutdown === true;
	}

	public function shutdown(){
		$this->shutdown = true;
	}

	public function getPort(){
		return $this->port;
	}

	public function getInterface(){
		return $this->interface;
	}

	public function getLogger(){
		return null;
	}

	public function run(){
	}
}
