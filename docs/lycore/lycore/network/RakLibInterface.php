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

namespace lycore\network;

use lycore\event\player\PlayerCreationEvent;
use lycore\network\protocol\DataPacket;
use lycore\network\protocol\Info as ProtocolInfo;
use lycore\network\protocol\Info;
use lycore\network\protocol\ProtocolCompatibility;
use lycore\network\protocol\v11\BatchPacket as BatchPacketV11;
use lycore\network\protocol\v11\DataPacket as DataPacketV11;
use lycore\network\protocol\v11\Info as InfoV11;
use lycore\network\protocol\v84\DataPacketV84;
use lycore\network\protocol\v84\InfoV84;
use lycore\Player;
use lycore\Server;
use lycore\utils\MainLogger;
use raklib\protocol\EncapsulatedPacket;
use raklib\RakLib;
use raklib\server\RakLibServer;
use raklib\server\ServerHandler;
use raklib\server\ServerInstance;

class RakLibInterface implements ServerInstance, AdvancedSourceInterface{

	/** @var Server */
	private $server;

	/** @var Network */
	private $network;

	/** @var RakLibServer */
	private $rakLib;

	/** @var Player[] */
	private $players = [];

	/** @var string[] */
	private $identifiers;

	/** @var int[] */
	private $identifiersACK = [];

	/** @var ServerHandler */
	private $interface;

	public function __construct(Server $server){

		$this->server = $server;
		$this->identifiers = [];

		$this->rakLib = new RakLibServer($this->server->getLogger(), $this->server->getLoader(), $this->server->getPort(), $this->server->getIp() === "" ? "0.0.0.0" : $this->server->getIp());
		$this->interface = new ServerHandler($this->rakLib, $this);
	}

	public function setNetwork(Network $network){
		$this->network = $network;
	}

	public function process(){
		$work = false;
		if($this->interface->handlePacket()){
			$work = true;
			$lasttime = time();
			while($this->interface->handlePacket()){
				$diff = time() - $lasttime;
				if($diff >= 1) break;
			}
		}

		if($this->rakLib->isTerminated()){
			$this->network->unregisterInterface($this);

			throw new \Exception("RakLib Thread crashed");
		}

		return $work;
	}

	public function closeSession($identifier, $reason){
		if(isset($this->players[$identifier])){
			$player = $this->players[$identifier];
			unset($this->identifiers[spl_object_hash($player)]);
			unset($this->players[$identifier]);
			unset($this->identifiersACK[$identifier]);
			$player->close($player->getLeaveMessage(), $reason);
		}
	}

	public function close(Player $player, $reason = "unknown reason"){
		if(isset($this->identifiers[$h = spl_object_hash($player)])){
			unset($this->players[$this->identifiers[$h]]);
			unset($this->identifiersACK[$this->identifiers[$h]]);
			$this->interface->closeSession($this->identifiers[$h], $reason);
			unset($this->identifiers[$h]);
		}
	}
    
	public function handlePing($identifier, $ping)
    {
        if (isset($this->players[$identifier])) {
            $this->players[$identifier]->setPing($ping);
        }
    }
	
	public function shutdown(){
		$this->interface->shutdown();
	}

	public function emergencyShutdown(){
		$this->interface->emergencyShutdown();
	}

	public function openSession($identifier, $address, $port, $clientID){
		$ev = new PlayerCreationEvent($this, Player::class, Player::class, null, $address, $port);
		$this->server->getPluginManager()->callEvent($ev);
		$class = $ev->getPlayerClass();

		$player = new $class($this, $ev->getClientId(), $ev->getAddress(), $ev->getPort());
		$this->players[$identifier] = $player;
		$this->identifiersACK[$identifier] = 0;
		$this->identifiers[spl_object_hash($player)] = $identifier;
		$this->server->addPlayer($identifier, $player);
	}

	public function handleEncapsulated($identifier, EncapsulatedPacket $packet, $flags){
		if(isset($this->players[$identifier])){
			try{
				if($packet->buffer !== ""){
					$pk = $this->getPacket($packet->buffer, $this->players[$identifier]);
					if($pk !== null){
						$pk->protocol = (int) $this->players[$identifier]->getProtocol();
						$pk->decode();
						if($pk instanceof BatchPacketV11){
							$this->network->processBatch($pk, $this->players[$identifier]);
							return;
						}
						$pk = DataPacketManager::toCorePacket($pk);
						if($pk instanceof DataPacket){
							$this->players[$identifier]->handleDataPacket($pk);
						}
					}
				}
			}catch(\Throwable $e){
				$logger = $this->server->getLogger();
				if(\lycore\DEBUG > 1 and isset($pk)){
					$logger->debug("Exception in packet " . get_class($pk) . " 0x" . bin2hex($packet->buffer));
				}
				$logger->logException($e);
			}
		}
	}

	public function blockAddress($address, $timeout = 300){
		$this->interface->blockAddress($address, $timeout);
		if($this->server->netshBlock){
			foreach($this->server->getIPBans()->getEntries() as $entry){
				if($entry->getName() === $address){
					return;
				}
			}
			passthru('netsh advfirewall firewall add rule name="lycore_Block_'.$address.'" dir=in remoteip='.$address.' action=block');
			$this->server->getIPBans()->addBan($address, "执行Dos攻击", null, "CONSOLE");
			$this->server->getLogger()->notice("已成功调用Windows防火墙封禁此IP");
		}
	}

	public function handleRaw($address, $port, $payload){
		$this->server->handlePacket($address, $port, $payload);
	}

	public function sendRawPacket($address, $port, $payload){
		$this->interface->sendRaw($address, $port, $payload);
	}

	public function notifyACK($identifier, $identifierACK){

	}

	public function setName($name){

		if($this->server->isDServerEnabled()){
			if($this->server->dserverConfig["motdMaxPlayers"] > 0) $pc = $this->server->dserverConfig["motdMaxPlayers"];
			elseif($this->server->dserverConfig["motdAllPlayers"]) $pc = $this->server->getDServerMaxPlayers();
			else $pc = $this->server->getMaxPlayers();

			if($this->server->dserverConfig["motdPlayers"]) $poc = $this->server->getDServerOnlinePlayers();
			else $poc = count($this->server->getOnlinePlayers());
		}else{
			$info = $this->server->getQueryInformation();
			$pc = $info->getMaxPlayerCount();
			$poc = $info->getPlayerCount();
		}

		$versionDisplay = Server::normalizeVersionDisplay($this->server->getConfigString("version-display", \lycore\MINECRAFT_VERSION_NETWORK));

		$this->interface->sendOption("name",
			"MCPE;" . addcslashes($name, ";") . "\n\n;" .
			ProtocolInfo::CURRENT_PROTOCOL . ";" .
			$versionDisplay . ";" .
			$poc . ";" .
			$pc
		);
	}

	public function setPortCheck($name){
		$this->interface->sendOption("portChecking", (bool) $name);
	}

	public function handleOption($name, $value){
		if($name === "bandwidth"){
			$v = unserialize($value);
			$this->network->addStatistics($v["up"], $v["down"], isset($v["cleaned"]) ? $v["cleaned"] : 0);
		}
	}

	public function putPacket(Player $player, $packet, $needACK = false, $immediate = false){
		if(isset($this->identifiers[$h = spl_object_hash($player)])){
			$identifier = $this->identifiers[$h];
			$protocol = (int) $player->getProtocol();
			$packetPrefix = ProtocolCompatibility::getRakLibPacketPrefix($protocol);
			$pk = null;
			if(!$packet->isEncoded){
				$packet->encode();
			}elseif(!$needACK){
				$cacheKey = $packet instanceof DataPacketV11 ? "__encapsulatedPacket011" : ($packet instanceof DataPacketV84 ? $packet->getEncapsulatedPacketCacheKey() : $packet->getEncapsulatedPacketCacheKey(ProtocolCompatibility::usesLegacySlotFormat($protocol)));
				if(!isset($packet->{$cacheKey})){
					$packet->{$cacheKey} = new CachedEncapsulatedPacket;
					$packet->{$cacheKey}->identifierACK = null;
					$packet->{$cacheKey}->buffer = $packetPrefix . $packet->buffer;
					$packet->{$cacheKey}->reliability = 3;
					$packet->{$cacheKey}->orderChannel = 0;
				}
				$pk = $packet->{$cacheKey};
			}

			if(!$immediate and !$needACK and $packet::NETWORK_ID !== ProtocolInfo::BATCH_PACKET
				and !($packet instanceof DataPacketV11 and $packet::NETWORK_ID === InfoV11::BATCH_PACKET)
				and !($packet instanceof DataPacketV84 and $packet::NETWORK_ID === InfoV84::BATCH_PACKET)
				and Network::$BATCH_THRESHOLD >= 0
				and strlen($packet->buffer) >= Network::$BATCH_THRESHOLD){
				$this->server->batchPackets([$player], [$packet], true);
				return null;
			}

			if($pk === null){
				$pk = new EncapsulatedPacket();
				$pk->buffer = $packetPrefix . $packet->buffer;
				$pk->reliability = 3;
				$pk->orderChannel = 0;

				if($needACK === true){
					$pk->identifierACK = $this->identifiersACK[$identifier]++;
				}
			}

			$this->interface->sendEncapsulated($identifier, $pk, ($needACK === true ? RakLib::FLAG_NEED_ACK : 0) | ($immediate === true ? RakLib::PRIORITY_IMMEDIATE : RakLib::PRIORITY_NORMAL));

			return $pk->identifierACK;
		}

		return null;
	}

	private function getPacket($buffer, Player $player = null){
		if($buffer === ""){
			return null;
		}

		$playerProtocol = $player !== null ? $player->getProtocol() : null;
		$protocol = $playerProtocol === null ? -1 : (int) $playerProtocol;
		$first = ord($buffer[0]);
		$lookupProtocol = $protocol;
		if($first === 0xfe){
			if(strlen($buffer) < 2){
				return null;
			}
			$pid = ord($buffer[1]);
			$packetOffset = 2;
		}elseif($first === 0x8e and !ProtocolCompatibility::isProtocol011($protocol)){
			if(strlen($buffer) < 2){
				return null;
			}
			$pid = ord($buffer[1]);
			$packetOffset = 2;
		}else{
			$pid = $first;
			$packetOffset = 1;
			if(ProtocolCompatibility::isProtocol011($protocol) or ($protocol < 0 and ($pid === InfoV11::LOGIN_PACKET or $pid === InfoV11::BATCH_PACKET))){
				$lookupProtocol = InfoV11::CURRENT_PROTOCOL;
			}
		}

		if(($data = $this->network->getPacket($pid, $lookupProtocol)) === null){
			return null;
		}
		$data->setBuffer($buffer, $packetOffset);

		return $data;
	}
}
