<?php

/***
 *  _____                _  __   __
 * /__  /  ___   ____   (_)/ /_ / /_
 *   / /  / _ \ / __ \ / // __// __ \
 *  / /__/  __// / / // // /_ / / / /
 * /____/\___//_/ /_//_/ \__//_/ /_/
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/

namespace pocketmine\network\compat;

/**
 * 协议能力矩阵。
 *
 * 集中放置所有「某个协议版本是否具备某能力」的判断，取代散落各处的
 * ProtocolCompatibility::isProtocol0xx / usesLegacyXxx 方法。
 * 所有取值都来自 {@see ProtocolVersion}，不做具体映射。
 */
final class ProtocolCapabilities{

	private function __construct(){
	}

	public static function isProtocol011(int $protocol) : bool{
		return ProtocolVersion::is($protocol, ProtocolVersion::FAMILY_V011);
	}

	public static function isProtocol012(int $protocol) : bool{
		return ProtocolVersion::is($protocol, ProtocolVersion::FAMILY_V012);
	}

	public static function isProtocol013(int $protocol) : bool{
		return ProtocolVersion::is($protocol, ProtocolVersion::FAMILY_V013);
	}

	public static function isProtocol014(int $protocol) : bool{
		return ProtocolVersion::is($protocol, ProtocolVersion::FAMILY_V014);
	}

	public static function isProtocol015(int $protocol) : bool{
		return ProtocolVersion::is($protocol, ProtocolVersion::FAMILY_V015);
	}

	public static function isLegacy011Protocol(int $protocol) : bool{
		return self::isProtocol011($protocol);
	}

	/** 0.11 / 0.12 使用同一套旧方块/物品映射 */
	public static function usesLegacy012Mappings(int $protocol) : bool{
		return ProtocolVersion::isAtMost($protocol, ProtocolVersion::FAMILY_V012);
	}

	/** 0.11 / 0.12 / 0.13 使用旧方块状态映射 */
	public static function usesLegacy013Mappings(int $protocol) : bool{
		return ProtocolVersion::isAtMost($protocol, ProtocolVersion::FAMILY_V013);
	}

	/** 0.12 / 0.13 的 legacy slot (大端 NBT 长度) 格式 */
	public static function usesLegacySlotFormat(int $protocol) : bool{
		return self::isProtocol012($protocol) or self::isProtocol013($protocol);
	}

	/** 0.11 ~ 0.14 均需要 legacy 红石方块映射 */
	public static function requiresLegacyRedstoneMapping(int $protocol) : bool{
		return ProtocolVersion::isAtMost($protocol, ProtocolVersion::FAMILY_V014);
	}

	public static function canUseSlimeBlockPhysics(int $protocol) : bool{
		return !self::usesLegacy013Mappings($protocol);
	}

	/**
	 * RakLib 单包前缀：0.11~0.13 无前缀；0.15 用 0xfe；其余 (0.14) 用 0x8e。
	 */
	public static function getRakLibPacketPrefix(int $protocol) : string{
		if(ProtocolVersion::isAtMost($protocol, ProtocolVersion::FAMILY_V013)){
			return "";
		}

		return self::isProtocol015($protocol) ? chr(0xfe) : chr(0x8e);
	}
}
