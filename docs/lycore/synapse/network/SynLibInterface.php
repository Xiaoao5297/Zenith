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
 
namespace synapse\network;

use pocketmine\network\protocol\DataPacket;
use pocketmine\network\SourceInterface;
use pocketmine\Player;
use synapse\network\protocol\spp\RedirectPacket;
use synapse\Synapse;

class SynLibInterface implements SourceInterface{
	private $synapseInterface;
	private $synapse;

	public function __construct(Synapse $synapse, SynapseInterface $interface){
		$this->synapse = $synapse;
		$this->synapseInterface = $interface;
	}

	public function emergencyShutdown(){
	}

	public function setName($name){
	}

	public function process(){
	}

	public function close(Player $player, $reason = "unknown reason"){
	}

	public function putPacket(Player $player, DataPacket $packet, $needACK = false, $immediate = true){
		$packet->encode();
		$pk = new RedirectPacket();
		$pk->uuid = $player->getUniqueId();
		$pk->direct = $immediate;
		$pk->mcpeBuffer = $packet->buffer;
		$this->synapseInterface->putPacket($pk);
	}

	public function shutdown(){
	}
}