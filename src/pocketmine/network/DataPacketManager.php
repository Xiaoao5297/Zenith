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

namespace pocketmine\network;

use pocketmine\network\compat\PacketCodecRegistry;
use pocketmine\network\compat\translators\PacketTranslator;
use pocketmine\network\compat\translators\V011Translator;
use pocketmine\network\compat\translators\V015Translator;
use pocketmine\network\protocol\DataPacket;
use pocketmine\network\protocol\v11\DataPacket as DataPacketV11;
use pocketmine\Player;

/**
 * 多协议数据包翻译的对外门面。
 *
 * 实现已拆分到 pocketmine\network\compat\ 下：
 *  - PacketCodecRegistry           : (协议, packetId) -> class 注册表
 *  - translators\V011Translator     : 0.11 翻译与批处理重映射
 *  - translators\V015Translator     : 0.15 翻译与批处理重映射
 *  - translators\PacketTranslator   : 按玩家协议路由的入口
 *
 * 本类仅做转发，保持既有调用方 (Server/Player/Network/RakLibInterface) 不变。
 */
class DataPacketManager{

	private function __construct(){
	}

	public static function getProtocol011PacketMap() : array{
		return PacketCodecRegistry::protocol011();
	}

	public static function getProtocol015PacketMap() : array{
		return PacketCodecRegistry::protocol015();
	}

	public static function parsePacket(Player $player, $packet){
		return PacketTranslator::parsePacket($player, $packet);
	}

	public static function toProtocol015Packet(DataPacket $packet, Player $player = null){
		return V015Translator::toProtocol015Packet($packet, $player);
	}

	public static function toCorePacket($packet){
		return PacketTranslator::toCorePacket($packet);
	}

	public static function toProtocol011Packet(DataPacket $packet, Player $player = null) : ?DataPacketV11{
		return V011Translator::toProtocol011Packet($packet, $player);
	}

	public static function toProtocol011Packets(DataPacket $packet, Player $player = null) : array{
		return V011Translator::toProtocol011Packets($packet, $player);
	}

	public static function remapBatchPayloadToProtocol011(string $payload, Player $player = null) : string{
		return V011Translator::remapBatchPayloadToProtocol011($payload, $player);
	}

	public static function remapPacketBufferToProtocol011(string $buffer, Player $player = null) : string{
		return V011Translator::remapPacketBufferToProtocol011($buffer, $player);
	}

	public static function toProtocol015Packets(DataPacket $packet, Player $player = null) : array{
		return V015Translator::toProtocol015Packets($packet, $player);
	}

	public static function remapBatchPayloadToProtocol015(string $payload, Player $player = null) : string{
		return V015Translator::remapBatchPayloadToProtocol015($payload, $player);
	}

	public static function remapPacketBufferToProtocol015(string $buffer, Player $player = null) : string{
		return V015Translator::remapPacketBufferToProtocol015($buffer, $player);
	}
}
