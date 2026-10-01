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
 * 从原始包缓冲区读取 (packetId, payloadOffset)。
 *
 * 兼容 0x8e / 0xfe 前缀（0.14 的 0x8e、0.15 的 0xfe）。
 */
final class PacketHeader{

	private function __construct(){
	}

	public static function read(string $buffer) : ?array{
		if($buffer === ""){
			return null;
		}

		$packetPrefix = ord($buffer[0]);
		if($packetPrefix === 0x8e or $packetPrefix === 0xfe){
			if(strlen($buffer) < 2){
				return null;
			}

			return [ord($buffer[1]), 2];
		}

		return [ord($buffer[0]), 1];
	}
}
