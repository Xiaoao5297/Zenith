<?php

namespace raklib\server;

/**
 * Proxy object used by RakLibServer's parallel Runtime to communicate via sockets and channels.
 */
class RakLibProxy{

	/** @var \parallel\Channel */
	public $internalQueue;
	/** @var resource */
	public $dataSocket;
	public $shutdown = false;
	private $mainPath;

	public function __construct($intChan, $dataSocket, $mainPath){
		$this->internalQueue = $intChan;
		$this->dataSocket = $dataSocket;
		$this->mainPath = $mainPath;
	}

	public function pushThreadToMainPacket($str){
		// Write packet data length + data to socket
		$data = pack("N", strlen($str)) . $str;
		@fwrite($this->dataSocket, $data);
	}

	public function readMainToThreadPacket(){
		try{
			return $this->internalQueue->recv();
		}catch(\parallel\Channel\Error\Closed $e){
			return "";
		}
	}

	public function pushMainToThreadPacket($str){
		$this->internalQueue->send($str);
	}

	public function isShutdown(){
		return $this->shutdown;
	}

	public function shutdown(){
		$this->shutdown = true;
	}

	public function getLogger(){
		return new \RakLibDummyLogger();
	}

	public function cleanPath($path){
		return rtrim(str_replace(["\\", ".php", "phar://", rtrim(str_replace(["\\", "phar://"], ["/", ""], $this->mainPath), "/")], ["/", "", "", ""], $path), "/");
	}

	public function getPort(){
		return 0;
	}

	public function getInterface(){
		return "0.0.0.0";
	}
}
