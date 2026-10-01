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

namespace pocketmine\network\compat;

/**
 * 协议版本的唯一事实来源 (single source of truth)。
 *
 * 这里只保存「版本号 -> 协议家族」的静态数据，不做任何包/方块/物品层面的判断。
 * 具体的能力判断见 {@see ProtocolCapabilities}。
 */
final class ProtocolVersion{

	/** 核心当前协议版本 (0.14) */
	const CORE = 70;

	/** 各客户端版本的协议号分组 */
	const V011 = [21, 22, 23, 24, 25, 26, 27];
	const V012 = [28, 29, 30, 31, 32, 33, 34];
	const V013 = [37, 38, 39];
	const V014 = [41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70];
	const V015 = [];

	/** 服务器接受的协议号 */
	const ACCEPTED = [21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70];

	/** 协议家族标识 */
	const FAMILY_V011 = "v011";
	const FAMILY_V012 = "v012";
	const FAMILY_V013 = "v013";
	const FAMILY_V014 = "v014";
	const FAMILY_V015 = "v015";
	const FAMILY_UNKNOWN = "unknown";

	/** 家族版本次序，越大越新；用于 isAtMost() 的区间判断 */
	const RANK_V011 = 0;
	const RANK_V012 = 1;
	const RANK_V013 = 2;
	const RANK_V014 = 3;
	const RANK_V015 = 4;

	/** @var string[]|null protocol => family */
	private static $familyByProtocol = null;

	private function __construct(){
	}

	private static function boot() : void{
		if(self::$familyByProtocol !== null){
			return;
		}

		$map = [];
		foreach([
			self::FAMILY_V011 => self::V011,
			self::FAMILY_V012 => self::V012,
			self::FAMILY_V013 => self::V013,
			self::FAMILY_V014 => self::V014,
			self::FAMILY_V015 => self::V015,
		] as $family => $protocols){
			foreach($protocols as $protocol){
				$map[$protocol] = $family;
			}
		}

		self::$familyByProtocol = $map;
	}

	public static function familyOf(int $protocol) : string{
		self::boot();
		return self::$familyByProtocol[$protocol] ?? self::FAMILY_UNKNOWN;
	}

	public static function rankOf(int $protocol) : ?int{
		switch(self::familyOf($protocol)){
			case self::FAMILY_V011:
				return self::RANK_V011;
			case self::FAMILY_V012:
				return self::RANK_V012;
			case self::FAMILY_V013:
				return self::RANK_V013;
			case self::FAMILY_V014:
				return self::RANK_V014;
			case self::FAMILY_V015:
				return self::RANK_V015;
		}

		return null;
	}

	public static function is(int $protocol, string $family) : bool{
		return self::familyOf($protocol) === $family;
	}

	/**
	 * 协议号是否不属于任何已知家族。
	 */
	public static function isUnknown(int $protocol) : bool{
		return self::familyOf($protocol) === self::FAMILY_UNKNOWN;
	}

	/**
	 * 协议号是否 <= 给定家族（已知协议范围内）。未知协议恒为 false。
	 */
	public static function isAtMost(int $protocol, string $family) : bool{
		$rank = self::rankOf($protocol);
		if($rank === null){
			return false;
		}

		return $rank <= self::rankLimit($family);
	}

	public static function rankLimit(string $family) : int{
		switch($family){
			case self::FAMILY_V011:
				return self::RANK_V011;
			case self::FAMILY_V012:
				return self::RANK_V012;
			case self::FAMILY_V013:
				return self::RANK_V013;
			case self::FAMILY_V014:
				return self::RANK_V014;
			case self::FAMILY_V015:
				return self::RANK_V015;
		}

		return -1;
	}

	public static function protocolsOf(string $family) : array{
		switch($family){
			case self::FAMILY_V011:
				return self::V011;
			case self::FAMILY_V012:
				return self::V012;
			case self::FAMILY_V013:
				return self::V013;
			case self::FAMILY_V014:
				return self::V014;
			case self::FAMILY_V015:
				return self::V015;
		}

		return [];
	}

	public static function accepted() : array{
		return self::ACCEPTED;
	}
}
