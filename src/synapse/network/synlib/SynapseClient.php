<?php
namespace synapse\network\synlib;

use pocketmine\Thread;

class SynapseClient extends Thread{
	const VERSION = "0.1.0";

	/** @var \parallel\Channel */
	private $internalQueue;
	/** @var string */
	private $intChanName = "";
	/** @var resource|null */
	private $dataSocket = null;
	/** @var resource|null */
	private $proxyDataSocket = null;
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
		$this->intChanName = "syn_int_{$id}";
		$this->internalQueue = \parallel\Channel::make($this->intChanName, \parallel\Channel::Infinite);

		// Socket pair for non-blocking data reading
		$sockets = @stream_socket_pair(STREAM_PF_UNIX, STREAM_SOCK_STREAM, STREAM_IPPROTO_IP);
		if($sockets !== false){
			$this->dataSocket = $sockets[0];
			$this->proxyDataSocket = $sockets[1];
			stream_set_blocking($this->dataSocket, false);
			stream_set_blocking($this->proxyDataSocket, false);
		}

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
			if($this->dataSocket){ @fclose($this->dataSocket); $this->dataSocket = null; }
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
		$intName = $this->intChanName;
		$proxySocket = $this->proxyDataSocket;

		$this->runtime = new \parallel\Runtime();
		$this->future = $this->runtime->run(function($bootstrapPath, $port, $interface, $mainPath, $intName, $proxySocket){
			require_once $bootstrapPath . "src/spl/ClassLoader.php";
			require_once $bootstrapPath . "src/spl/BaseClassLoader.php";
			require_once $bootstrapPath . "src/pocketmine/CompatibleClassLoader.php";
			require_once $bootstrapPath . "src/raklib/server/RakLibDummyLogger.php";
			require_once $bootstrapPath . "src/synapse/network/synlib/SynapseProxy.php";

			$loader = new \CompatibleClassLoader();
			$loader->addPath($bootstrapPath . "src");
			$loader->addPath($bootstrapPath . "src" . DIRECTORY_SEPARATOR . "spl");
			$loader->register(true);

			gc_enable();
			error_reporting(-1);
			ini_set("display_errors", 1);
			ini_set("display_startup_errors", 1);

			$intChan = \parallel\Channel::open($intName);

			$proxy = new SynapseProxy($intChan, $proxySocket, $mainPath, $port, $interface);
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
		}, [$bootstrapPath, $port, $interface, $mainPath, $intName, $proxySocket]);
	}

	public function getExternalQueue(){
		return null;
	}

	public function getInternalQueue(){
		return $this->internalQueue;
	}

	public function pushMainToThreadPacket($str){
		$this->internalQueue->send($str);
	}

	public function readMainToThreadPacket(){
		try{
			return $this->internalQueue->recv();
		}catch(\parallel\Channel\Error\Closed $e){
			return "";
		}
	}

	public function pushThreadToMainPacket($str){
		// Legacy - no-op from main thread side
	}

	public function readThreadToMainPacket(){
		if($this->dataSocket === null or $this->dataSocket === false){
			return "";
		}
		$r = [$this->dataSocket];
		$w = null;
		$e = null;
		if(stream_select($r, $w, $e, 0, 0) > 0){
			$header = @fread($this->dataSocket, 4);
			if($header === false or strlen($header) < 4){
				return "";
			}
			$len = unpack("N", $header)[1];
			$data = "";
			while(strlen($data) < $len){
				$chunk = @fread($this->dataSocket, $len - strlen($data));
				if($chunk === false or $chunk === "") break;
				$data .= $chunk;
			}
			return $data;
		}
		return "";
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
