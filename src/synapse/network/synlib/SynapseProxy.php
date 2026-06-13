<?php
namespace synapse\network\synlib;

class SynapseProxy{

	/** @var \parallel\Channel */
	public $internalQueue;
	/** @var resource */
	public $dataSocket;
	public $shutdown = false;
	public $needAuth = false;
	private $mainPath;
	private $port;
	private $interface;

	public function __construct($intChan, $dataSocket, $mainPath, $port, $interface){
		$this->internalQueue = $intChan;
		$this->dataSocket = $dataSocket;
		$this->mainPath = $mainPath;
		$this->port = $port;
		$this->interface = $interface;
	}

	public function pushMainToThreadPacket($str){ $this->internalQueue->send($str); }
	public function readMainToThreadPacket(){
		try{ return $this->internalQueue->recv(); }
		catch(\Throwable $e){ return ""; }
	}
	public function pushThreadToMainPacket($str){
		$data = pack("N", strlen($str)) . $str;
		@fwrite($this->dataSocket, $data);
	}
	public function isShutdown(){ return $this->shutdown; }
	public function shutdown(){ $this->shutdown = true; }
	public function isNeedAuth(){ return $this->needAuth; }
	public function setNeedAuth($v){ $this->needAuth = $v; }
	public function getPort(){ return $this->port; }
	public function getInterface(){ return $this->interface; }

	public function getLogger(){
		return new \RakLibDummyLogger();
	}

	public function getTrace($start = 1, $trace = null){ return []; }
	public function cleanPath($path){
		return rtrim(str_replace(["\\", ".php", "phar://", rtrim(str_replace(["\\", "phar://"], ["/", ""], $this->mainPath), "/")], ["/", "", "", ""], $path), "/");
	}
}
