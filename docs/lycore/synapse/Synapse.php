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

namespace synapse;

use pocketmine\Server;
use pocketmine\utils\MainLogger;
use pocketmine\utils\Utils;
use synapse\network\protocol\spp\ConnectPacket;
use synapse\network\protocol\spp\DataPacket;
use synapse\network\protocol\spp\DisconnectPacket;
use synapse\network\protocol\spp\HeartbeatPacket;
use synapse\network\protocol\spp\Info;
use synapse\network\protocol\spp\InformationPacket;
use synapse\network\protocol\spp\PlayerLoginPacket;
use synapse\network\protocol\spp\PlayerLogoutPacket;
use synapse\network\protocol\spp\RedirectPacket;
use synapse\network\SynapseInterface;
use synapse\network\SynLibInterface;

class Synapse{
	private static $obj = null;
	/** @var Server */
	private $server;
	/** @var MainLogger */
	private $logger;
	private $serverIp;
	private $port;
	private $isMainServer;
	private $password;
	private $interface;
	private $verified = false;
	private $lastUpdate;
	/** @var Player[] */
	private $players = [];
	/** @var SynLibInterface */
	private $synLibInterface;
	private $clientData = [];
	private $description;

	public function __construct(Server $server, array $config){
		self::$obj = $this;
		$this->server = $server;
		$this->serverIp = $config["server-ip"];
		$this->port = $config["server-port"];
		$this->isMainServer = $config["isMainServer"];
		$this->password = $config["password"];
		$this->description = $config["description"];
		$this->logger = $server->getLogger();
		$this->interface = new SynapseInterface($this, $this->serverIp, $this->port);
		$this->synLibInterface = new SynLibInterface($this, $this->interface);
		$this->lastUpdate = microtime(true);
		$this->connect();
	}

	public function getClientData(){
		return $this->clientData;
	}

	public function getLYCoreServer(){
		return $this->server;
	}

	public function getInterface(){
		return $this->interface;
	}

	public static function getInstance(){
		return self::$obj;
	}

	public function shutdown(){
		if($this->verified){
			$pk = new DisconnectPacket();
			$pk->type = DisconnectPacket::TYPE_GENERIC;
			$pk->message = "服务器已关闭";
			$this->sendDataPacket($pk);
			$this->getLogger()->debug("Synapse 客户端已断开与 Synapse 服务器的连接");
		}
	}

	public function getDescription() : string{
		return $this->description;
	}

	public function setDescription(string $description){
		$this->description = $description;
	}

	public function sendDataPacket(DataPacket $pk){
		$this->interface->putPacket($pk);
	}

	public function connect(){
		$this->verified = false;
		$pk = new ConnectPacket();
		$pk->encodedPassword = base64_encode(Utils::aes_encode($this->password, $this->password));
		$pk->isMainServer = $this->isMainServer();
		$pk->description = $this->description;
		$pk->maxPlayers = $this->server->getMaxPlayers();
		$pk->protocol = Info::CURRENT_PROTOCOL;
		$this->sendDataPacket($pk);
	}

	public function tick(){
		$this->interface->process();
		if((($time = microtime(true)) - $this->lastUpdate) >= 5){//Heartbeat!
			$this->lastUpdate = $time;
			$pk = new HeartbeatPacket();
			$pk->tps = $this->server->getTicksPerSecondAverage();
			$pk->load = $this->server->getTickUsageAverage();
			$pk->upTime = microtime(true) - \lycore\START_TIME;
			$this->sendDataPacket($pk);
		}
	}

	public function getServerIp() : string{
		return $this->serverIp;
	}

	public function getPort() : int{
		return $this->port;
	}

	public function isMainServer() : bool{
		return $this->isMainServer;
	}

	public function getLogger(){
		return $this->logger;
	}

	public function getHash() : string{
		return $this->serverIp . ":" . $this->port;
	}

	public function getPacket($buffer){
		$pid = ord($buffer[1]);

		if(($data = $this->server->getNetwork()->getPacket($pid)) === null){
			return null;
		}
		$data->setBuffer($buffer, 2);

		return $data;
	}

	public function removePlayer(Player $player){
		if(isset($this->players[$uuid = $player->getUniqueId()->toBinary()])){
			unset($this->players[$uuid]);
		}
	}

	public function handleDataPacket(DataPacket $pk){
		$this->logger->debug("收到数据包 " . $pk::NETWORK_ID . " 来自 {$this->serverIp}:{$this->port}");
		switch($pk::NETWORK_ID){
			case Info::INFORMATION_PACKET:
				/** @var InformationPacket $pk */
				switch($pk->type){
					case InformationPacket::TYPE_LOGIN:
						if($pk->message == InformationPacket::INFO_LOGIN_SUCCESS){
							$this->logger->info("登录成功于 {$this->serverIp}:{$this->port}");
							$this->verified = true;
						}elseif($pk->message == InformationPacket::INFO_LOGIN_FAILED){
							$this->logger->info("登录失败于 {$this->serverIp}:{$this->port}");
						}
					break;
					case InformationPacket::TYPE_CLIENT_DATA:
						$this->clientData = json_decode($pk->message, true);
						break;
				}

				break;
			case Info::PLAYER_LOGIN_PACKET:
				/** @var PlayerLoginPacket $pk */
				$player = new Player($this->synLibInterface, mt_rand(0, PHP_INT_MAX), $pk->address, $pk->port);
				$player->setUniqueId($pk->uuid);
				$this->server->addPlayer(spl_object_hash($player), $player);
				$this->players[$pk->uuid->toBinary()] = $player;
				$player->handleLoginPacket($pk);
				break;
			case Info::REDIRECT_PACKET:
				/** @var RedirectPacket $pk */
				if(isset($this->players[$uuid = $pk->uuid->toBinary()])){
					$pk = $this->getPacket($pk->mcpeBuffer);
					$pk->decode();
					$this->players[$uuid]->handleDataPacket($pk);
				}
				break;
			case Info::PLAYER_LOGOUT_PACKET:
				/** @var PlayerLogoutPacket $pk */
				if(isset($this->players[$uuid = $pk->uuid->toBinary()])){
					$this->players[$uuid]->close("", $pk->reason);
					$this->removePlayer($this->players[$uuid]);
				}
				break;
		}
	}
}