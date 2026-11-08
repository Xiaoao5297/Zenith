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

	// 单包解压输出上限 4MB：raklib 单包 MTU 上限 1432 字节、单包最多 128 分片
	// （Session::MAX_SPLIT_SIZE），理论批包上限约 180KB，正常批包远小于此，4MB 已非常宽松
	const MAX_PAYLOAD = 4194304; // 4MB

	private function __construct(){
	}

	/**
	 * @deprecated 已无调用方，请改用 {@see BatchCodec::streamingDecompress()}（流式解压，带压缩炸弹防护）。
	 * 方法保留作为公共兼容 API。
	 */
	public static function decompress(string $payload){
		return zlib_decode($payload, self::MAX_PAYLOAD);
	}

	/**
	 * 流式解压 zlib/deflate/gzip 负载，成功结果与 zlib_decode($payload, $max) 一致，
	 * 但输出超过 $max 时立即中止，避免一次性分配超限内存（压缩炸弹防护）。
	 *
	 * @param string $payload
	 * @param int    $max 解压输出上限（字节）
	 * @return string|bool 成功返回解压结果，失败或超限返回 false
	 */
	public static function streamingDecompress(string $payload, int $max){
		if($payload === ""){
			return false;
		}
		$first = ord($payload[0]);
		if(($first & 0x0f) === 0x08){
			$encoding = ZLIB_ENCODING_DEFLATE;
		}elseif($first === 0x1f){
			$encoding = ZLIB_ENCODING_GZIP;
		}else{
			$encoding = ZLIB_ENCODING_RAW;
		}
		$context = @inflate_init($encoding);
		if($context === false){
			return false;
		}
		$out = "";
		$length = strlen($payload);
		for($offset = 0; $offset < $length; $offset += 65536){
			$result = @inflate_add($context, substr($payload, $offset, 65536));
			if($result === false){
				return false;
			}
			$out .= $result;
			if(strlen($out) > $max){
				return false;
			}
		}
		$tail = @inflate_add($context, "", ZLIB_FINISH);
		if($tail === false){
			return false;
		}
		$out .= $tail;
		if(strlen($out) > $max){
			return false;
		}
		return $out;
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
