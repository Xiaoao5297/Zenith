<?php

namespace pocketmine\network\rcon;

use pocketmine\Thread;
use pocketmine\utils\Binary;
use pocketmine\utils\MainLogger;

class RCONInstance extends Thread{
	public $stop;
	public $cmd;
	public $response;
	/** @var bool true once the main thread has filled $response for the pending command */
	public $responseReady;
	private $socket;
	private $password;
	private $maxClients;
	private $waiting;

	/** @var MainLogger */
	private $logger;

	public $serverStatus;

	public function isWaiting(){
		return $this->waiting === true;
	}


	public function __construct($logger, $socket, $password, $maxClients = 50){
		$this->logger = $logger;
		$this->stop = false;
		$this->cmd = "";
		$this->response = "";
		$this->responseReady = false;
		$this->socket = $socket;
		$this->password = $password;
		$this->maxClients = (int) $maxClients;
		for($n = 0; $n < $this->maxClients; ++$n){
			$this->{"client" . $n} = null;
			$this->{"status" . $n} = 0;
			$this->{"timeout" . $n} = 0;
		}

		$this->start();
	}

	private function writePacket($client, $requestID, $packetType, $payload){
		$pk = Binary::writeLInt((int) $requestID)
			. Binary::writeLInt((int) $packetType)
			. $payload
			. "\x00\x00"; //Terminate payload and packet
		return socket_write($client, Binary::writeLInt(strlen($pk)) . $pk);
	}

	private function readPacket($client, &$size, &$requestID, &$packetType, &$payload){
		socket_set_nonblock($client);
		$d = @socket_read($client, 4);
		if($this->stop === true){
			return false;
		}elseif($d === false){
			return null;
		}elseif($d === "" or strlen($d) < 4){
			return false;
		}
		socket_set_block($client);
		$size = Binary::readLInt($d);
		if($size < 0 or $size > 65535){
			return false;
		}
		$requestID = Binary::readLInt(socket_read($client, 4));
		$packetType = Binary::readLInt(socket_read($client, 4));
		$payload = rtrim(socket_read($client, $size + 2)); //Strip two null bytes
		return true;
	}

	public function close(){
		$this->stop = true;
	}

	public function run(){

		while($this->stop !== true){
			$this->synchronized(function(){
				$this->wait(2000);
			});
			$r = [$socket = $this->socket];
			$w = null;
			$e = null;
			if(socket_select($r, $w, $e, 0) === 1){
				if(($client = socket_accept($this->socket)) !== false){
					socket_set_block($client);
					socket_set_option($client, SOL_SOCKET, SO_KEEPALIVE, 1);
					$done = false;
					for($n = 0; $n < $this->maxClients; ++$n){
						if($this->{"client" . $n} === null){
							$this->{"client" . $n} = $client;
							$this->{"status" . $n} = 0;
							$this->{"timeout" . $n} = microtime(true) + 5;
							$done = true;
							break;
						}
					}
					if($done === false){
						@socket_close($client);
					}
				}
			}

			for($n = 0; $n < $this->maxClients; ++$n){
				$client = &$this->{"client" . $n};
				if($client !== null){
					if($this->{"status" . $n} !== -1 and $this->stop !== true){
						if($this->{"status" . $n} === 0 and $this->{"timeout" . $n} < microtime(true)){ //Timeout
							$this->{"status" . $n} = -1;
							continue;
						}
						$p = $this->readPacket($client, $size, $requestID, $packetType, $payload);
						if($p === false){
							$this->{"status" . $n} = -1;
							continue;
						}elseif($p === null){
							continue;
						}

						switch($packetType){
							case 9: //Protocol check
								if($this->{"status" . $n} !== 1){
									$this->{"status" . $n} = -1;
									continue;
								}
								$this->writePacket($client, $requestID, 0, RCON::PROTOCOL_VERSION);
								if($payload == RCON::PROTOCOL_VERSION) $this->logger->setSendMsg(true); //GeniRCON output
								break;
							case 4: //Logger
								if($this->{"status" . $n} !== 1){
									$this->{"status" . $n} = -1;
									continue;
								}
								$res = (array) [
									"serverStatus" => unserialize($this->serverStatus, ["allowed_classes" => false]),
									"logger" => str_replace("\n", "\r\n", trim($this->logger->getMessages()))
								];
								$this->writePacket($client, $requestID, 0, serialize($res));
								break;
							case 3: //Login
								if($this->{"status" . $n} !== 0){
									$this->{"status" . $n} = -1;
									continue;
								}
								if(hash_equals($this->password, $payload)){
									socket_getpeername($client, $addr, $port);
									$this->writePacket($client, $requestID, 2, "");
									$this->{"status" . $n} = 1;
								}else{
									$this->{"status" . $n} = -1;
									$this->writePacket($client, -1, 2, "");
									continue;
								}
								break;
							case 2: //Command
								if($this->{"status" . $n} !== 1){
									$this->{"status" . $n} = -1;
									continue;
								}
								if(strlen($payload) > 0){
									/*
									 * Whole handshake under the object monitor: only one command may be
									 * pending per worker at a time, and the response slot can only be
									 * consumed by the instance that submitted it, so responses can never
									 * be delivered to a different client (or contain a stale one).
									 */
									$this->synchronized(function() use (&$client, &$requestID, $payload){
										$this->cmd = ltrim($payload);
										$this->response = "";
										$this->responseReady = false;
										$this->waiting = true;
										//Bounded wait: a dead or wedged main thread must not block us forever.
										//wait()'s return value does not reliably distinguish notify from
										//timeout on all pthreads builds, so the responseReady sentinel
										//(set by the main thread under the lock in RCON's check handler)
										//is the authoritative predicate.
										$this->wait(5000);
										$this->waiting = false;
										$out = $this->responseReady ? $this->response : "Server busy, try again later";
										$this->writePacket($client, $requestID, 0, str_replace("\n", "\r\n", trim($out)));
										$this->cmd = "";
										$this->response = "";
										$this->responseReady = false;
									});
								}
								break;
						}

					}else{
						@socket_set_option($client, SOL_SOCKET, SO_LINGER, ["l_onoff" => 1, "l_linger" => 1]);
						@socket_shutdown($client, 2);
						@socket_set_block($client);
						@socket_read($client, 1);
						@socket_close($client);
						$this->{"status" . $n} = 0;
						$this->{"client" . $n} = null;
					}
				}
			}
		}
		unset($this->socket, $this->cmd, $this->response, $this->responseReady, $this->stop);
		exit(0);
	}

	public function getThreadName(){
		return "RCON";
	}
}