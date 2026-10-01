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

namespace pocketmine\network\compat\translators;

use pocketmine\network\protocol\DataPacket;
use pocketmine\network\protocol\ProtocolCompatibility;
use pocketmine\network\protocol\v11\DataPacket as DataPacketV11;
use pocketmine\network\protocol\v84\DataPacketV84;
use pocketmine\Player;

/**
 * 翻译器入口 (门面)：按玩家协议把入站/出站包路由到对应的 V011Translator / V015Translator。
 *
 * 0.12 / 0.13 / 0.14 是核心协议，保持原样 (核心 <-> 客户端同构)。
 */
final class PacketTranslator{

	private function __construct(){
	}

	/**
	 * 入站方向：把 v11/v84 包转成核心包，或把核心包按玩家协议转换成对应家族的包。
	 */
	public static function parsePacket(Player $player, $packet){
		if($packet instanceof DataPacketV11){
			return ProtocolCompatibility::isProtocol011((int) $player->getProtocol()) ? $packet : self::toCorePacket($packet);
		}

		if($packet instanceof DataPacketV84){
			return ProtocolCompatibility::isProtocol015((int) $player->getProtocol()) ? V015Translator::sanitizeProtocol015V84Packet($packet, $player) : $packet;
		}

		if(!$packet instanceof DataPacket){
			return $packet;
		}

		$protocol = (int) $player->getProtocol();
		if(ProtocolCompatibility::isProtocol011($protocol)){
			return V011Translator::toProtocol011Packet($packet, $player);
		}

		if(ProtocolCompatibility::isProtocol015($protocol)){
			return V015Translator::toProtocol015Packet($packet, $player);
		}

		return $packet;
	}

	/**
	 * 把厂商私有家族的包统一转成核心包。
	 */
	public static function toCorePacket($packet){
		if($packet instanceof DataPacketV11){
			return V011Translator::toCorePacketV11($packet);
		}

		if($packet instanceof DataPacketV84){
			return V015Translator::toCorePacketV84($packet);
		}

		return $packet;
	}
}
