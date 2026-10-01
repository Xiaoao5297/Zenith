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

use pocketmine\utils\Binary;

/**
 * 批处理包 (BatchPacket) 的解压/压缩与负载切分。
 *
 * 支持两种负载格式：
 *  - 核心/0.12+：4 字节大端长度前缀 + 包体（见 {@see forEachFramed}）
 *  - 0.11：裸 packetId + 变长包体，包长由 decode 决定（见 {@see forEachV11}）
 */
final class BatchCodec{

	const MAX_PAYLOAD = 67108864; // 64MB

	private function __construct(){
	}

	public static function decompress(string $payload){
		return zlib_decode($payload, self::MAX_PAYLOAD);
	}

	public static function compress(string $raw) : string{
		return zlib_encode($raw, ZLIB_ENCODING_DEFLATE, 7);
	}

	/**
	 * 遍历「4 字节长度前缀」格式的批包负载。
	 *
	 * 回调返回 false 可提前结束遍历；返回 null 继续。
	 */
	public static function forEachFramed(string $raw, callable $callback) : void{
		$len = strlen($raw);
		$offset = 0;
		while($offset < $len){
			if($offset + 4 > $len){
				break;
			}

			$packetLength = Binary::readInt(substr($raw, $offset, 4));
			$offset += 4;
			if($packetLength <= 0 or $packetLength > ($len - $offset)){
				break;
			}

			$buffer = substr($raw, $offset, $packetLength);
			$offset += $packetLength;

			if($callback($buffer) === false){
				return;
			}
		}
	}

	/**
	 * 遍历「裸 packetId + 变长包体」格式的 0.11 批包负载。
	 *
	 * 回调签名 ($packetId, $packetOffset)：返回下一个包的绝对偏移；返回 null 表示跳过该包；
	 * 返回非 int 或 <= $packetOffset 表示结束遍历。
	 */
	public static function forEachV11(string $raw, callable $callback) : void{
		$len = strlen($raw);
		$offset = 0;
		while($offset < $len){
			$packetId = ord($raw[$offset++]);
			$next = $callback($packetId, $offset);
			if($next === null){
				continue;
			}
			if(!is_int($next) or $next <= $offset){
				return;
			}
			$offset = $next;
		}
	}
}
