<?php

namespace pocketmine\network\rcon;

use pocketmine\Thread;
use pocketmine\utils\Binary;
use pocketmine\utils\MainLogger;

class RCONInstance extends Thread{
	public $stop;
	public $cmd;
	public $response;
	private $socket;
	private $password;
	private $maxClients;
	private $waiting;

	/** @var MainLogger */
	private $logger;

	public $serverStatus;

	/** @var \parallel\Channel|null */
	private $cmdChan = null;
	/** @var \parallel\Channel|null */
	private $respChan = null;
	/** @var string */
	private $cmdChanName = "";
	/** @var string */
	private $respChanName = "";
	/** @var \parallel\Future|null */
	private $pendingCmdFuture = null;

	public function isWaiting(){
		return $this->waiting === true;
	}

	public function __construct($logger, $socket, $password, $maxClients = 50){
		$this->logger = $logger;
		$this->stop = false;
		$this->cmd = "";
		$this->response = "";
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

	public function start(int $options = 0){
		$id = spl_object_id($this);
		$cmdChanName = "rcon_cmd_{$id}";
		$respChanName = "rcon_resp_{$id}";
		$this->cmdChan = \parallel\Channel::make($cmdChanName, \parallel\Channel::Infinite);
		$this->respChan = \parallel\Channel::make($respChanName, \parallel\Channel::Infinite);
		$this->cmdChanName = $cmdChanName;
		$this->respChanName = $respChanName;
		$password = $this->password;
		$maxClients = $this->maxClients;
		$socket = $this->socket;

		$this->runtime = new \parallel\Runtime();
		$this->future = $this->runtime->run(function($cmdChanName, $respChanName, $password, $maxClients, $socket){
			require_once \pocketmine\PATH . "src/spl/ClassLoader.php";
			require_once \pocketmine\PATH . "src/spl/BaseClassLoader.php";
			require_once \pocketmine\PATH . "src/pocketmine/CompatibleClassLoader.php";

			$loader = new CompatibleClassLoader();
			$loader->addPath(\pocketmine\PATH . "src");
			$loader->addPath(\pocketmine\PATH . "src" . DIRECTORY_SEPARATOR . "spl");
			$loader->register(true);

			$cmdChan = \parallel\Channel::open($cmdChanName);
			$respChan = \parallel\Channel::open($respChanName);

			$stop = false;
			$waiting = false;
			$clients = [];
			$statuses = [];
			$timeouts = [];

			while(!$stop){
				usleep(2000000);

				$r = [$socket];
				$w = null;
				$e = null;
				if(socket_select($r, $w, $e, 0) === 1){
					if(($client = socket_accept($socket)) !== false){
						socket_set_block($client);
						socket_set_option($client, SOL_SOCKET, SO_KEEPALIVE, 1);
						$done = false;
						for($n = 0; $n < $maxClients; ++$n){
							if(!isset($clients[$n])){
								$clients[$n] = $client;
								$statuses[$n] = 0;
								$timeouts[$n] = microtime(true) + 5;
								$done = true;
								break;
							}
						}
						if(!$done){
							@socket_close($client);
						}
					}
				}

				for($n = 0; $n < $maxClients; ++$n){
					if(!isset($clients[$n])) continue;
					$client = $clients[$n];
					if($statuses[$n] !== -1 and !$stop){
						if($statuses[$n] === 0 and $timeouts[$n] < microtime(true)){
							$statuses[$n] = -1;
							continue;
						}
						socket_set_nonblock($client);
						$d = @socket_read($client, 4);
						if($d === false or $d === "" or strlen($d) < 4){
							continue;
						}
						socket_set_block($client);
						$size = Binary::readLInt($d);
						if($size < 0 or $size > 65535){ $statuses[$n] = -1; continue; }
						$requestID = Binary::readLInt(socket_read($client, 4));
						$packetType = Binary::readLInt(socket_read($client, 4));
						$payload = rtrim(socket_read($client, $size + 2));

						switch($packetType){
							case 3: // Login
								if($statuses[$n] !== 0){ $statuses[$n] = -1; continue 2; }
								if($payload === $password){
									$pk = Binary::writeLInt($requestID) . Binary::writeLInt(2) . "\x00\x00";
									socket_write($client, Binary::writeLInt(strlen($pk)) . $pk);
									$statuses[$n] = 1;
								}else{
									$statuses[$n] = -1;
									$pk = Binary::writeLInt(-1) . Binary::writeLInt(2) . "\x00\x00";
									socket_write($client, Binary::writeLInt(strlen($pk)) . $pk);
								}
								break;
							case 2: // Command
								if($statuses[$n] !== 1){ $statuses[$n] = -1; continue 2; }
								if(strlen($payload) > 0){
									$cmdChan->send(ltrim($payload));
									$waiting = true;
									$response = $respChan->recv();
									$waiting = false;
									$pk = Binary::writeLInt($requestID) . Binary::writeLInt(0) . str_replace("\n", "\r\n", trim($response)) . "\x00\x00";
									socket_write($client, Binary::writeLInt(strlen($pk)) . $pk);
								}
								break;
							default:
								if($statuses[$n] !== 1){ $statuses[$n] = -1; continue 2; }
								$pk = Binary::writeLInt($requestID) . Binary::writeLInt(0) . "\x00\x00";
								socket_write($client, Binary::writeLInt(strlen($pk)) . $pk);
								break;
						}
					}else{
						@socket_set_option($client, SOL_SOCKET, SO_LINGER, ["l_onoff" => 1, "l_linger" => 1]);
						@socket_shutdown($client, 2);
						@socket_set_block($client);
						@socket_read($client, 1);
						@socket_close($client);
						unset($clients[$n], $statuses[$n], $timeouts[$n]);
					}
				}
			}
		}, [$cmdChanName, $respChanName, $password, $maxClients, $socket]);
	}

	public function getCmd(){
		// Check if a pending cmd future has completed
		if($this->pendingCmdFuture !== null){
			try{
				if($this->pendingCmdFuture->done()){
					$cmd = $this->pendingCmdFuture->value();
					$this->pendingCmdFuture = null;
					return $cmd;
				}
			}catch(\Throwable $e){
				$this->pendingCmdFuture = null;
			}
			return null;
		}

		// Start a new future to read one command from the channel (blocks until available)
		if($this->cmdChanName === ""){
			return null;
		}
		$chanName = $this->cmdChanName;
		$this->pendingCmdFuture = \parallel\run(function($chanName){
			$chan = \parallel\Channel::open($chanName);
			try{
				return $chan->recv();
			}catch(\parallel\Channel\Error\Closed $e){
				return null;
			}
		}, [$chanName]);

		return null;
	}

	public function setResponse($response){
		try{
			$this->respChan?->send($response);
		}catch(\Throwable $e){
		}
	}

	public function close(){
		$this->stop = true;
	}

	public function run(){
	}

	public function quit(){
		$this->stop = true;
		try{
			if($this->cmdChanName !== "") \parallel\Channel::destroy($this->cmdChanName);
		}catch(\Throwable $e){}
		try{
			if($this->respChanName !== "") \parallel\Channel::destroy($this->respChanName);
		}catch(\Throwable $e){}
		try{
			$this->runtime?->close();
		}catch(\Throwable $e){}
	}

	public function getThreadName(){
		return "RCON";
	}
}
