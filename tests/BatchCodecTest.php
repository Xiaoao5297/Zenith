<?php

/**
 * SEC-001 回归测试：批包解压上限与负载切分。
 *
 * 覆盖 docs/audit-2026-11-01.md 中 SEC-001（批包解压内存炸弹）与
 * BatchCodec 的公共 API 行为：
 *  - MAX_PAYLOAD 必须保持 4MB，防止退回旧的 64MB 上限；
 *  - streamingDecompress 必须拒绝超过上限的输出（压缩炸弹）；
 *  - zlib / gzip / raw 三种编码都能正确往返；
 *  - forEachFramed 对截断/超长/零长度前缀必须安全停止；
 *  - forEachV11 的非法返回值必须终止遍历而不是死循环。
 *
 * 依赖：pocketmine\network\compat\BatchCodec + pocketmine\utils\Binary。
 * Binary 只在未被调用的方法里引用 Entity 常量，可独立加载。
 */

use pocketmine\network\compat\BatchCodec;
use pocketmine\utils\Binary;

require_once dirname(__DIR__) . "/src/pocketmine/utils/Binary.php";
require_once dirname(__DIR__) . "/src/pocketmine/network/compat/BatchCodec.php";

class BatchCodecTest{

	public static function run() : array{
		return array_merge(
			self::testPayloadLimit(),
			self::testCompressRoundtrip(),
			self::testStreamingEncodings(),
			self::testStreamingLimits(),
			self::testFramed(),
			self::testV11()
		);
	}

	private static function testPayloadLimit() : array{
		return [
			["max_payload_constant", BatchCodec::MAX_PAYLOAD === 4194304, "MAX_PAYLOAD = " . BatchCodec::MAX_PAYLOAD]
		];
	}

	private static function testCompressRoundtrip() : array{
		$raw = str_repeat("batch packet payload|", 40);
		$compressed = BatchCodec::compress($raw);
		$out = [];
		$out[] = ["compress_nonempty", is_string($compressed) and strlen($compressed) > 0, "len=" . strlen($compressed)];
		$out[] = ["decompress_roundtrip", BatchCodec::decompress($compressed) === $raw, "legacy zlib_decode path"];
		$out[] = ["streaming_roundtrip", BatchCodec::streamingDecompress($compressed, BatchCodec::MAX_PAYLOAD) === $raw, "zlib wrapper"];
		return $out;
	}

	private static function testStreamingEncodings() : array{
		$raw = str_repeat("The quick brown fox jumps over the lazy dog. ", 20);
		$gzip = gzencode($raw);
		$deflate = gzdeflate($raw);

		$out = [];
		$out[] = ["gzip_roundtrip", BatchCodec::streamingDecompress($gzip, BatchCodec::MAX_PAYLOAD) === $raw, "first byte 0x" . dechex(ord($gzip[0]))];
		$out[] = ["raw_deflate_roundtrip", BatchCodec::streamingDecompress($deflate, BatchCodec::MAX_PAYLOAD) === $raw, "first byte 0x" . dechex(ord($deflate[0]))];
		$out[] = ["empty_payload_rejected", BatchCodec::streamingDecompress("", BatchCodec::MAX_PAYLOAD) === false, "empty string"];
		return $out;
	}

	private static function testStreamingLimits() : array{
		$out = [];

		// 压缩炸弹：8MB 零字节压缩后仅数 KB，解压必须被上限拦截
		$bomb = BatchCodec::compress(str_repeat("\0", 8 * 1024 * 1024));
		$out[] = ["bomb_rejected", BatchCodec::streamingDecompress($bomb, BatchCodec::MAX_PAYLOAD) === false, "compressed=" . strlen($bomb) . "B, limit=4MB"];

		// 上限略小于真实输出：必须拒绝而不是截断返回
		$small = BatchCodec::compress(str_repeat("x", 1024));
		$out[] = ["over_max_rejected", BatchCodec::streamingDecompress($small, 512) === false, "output=1024B, max=512B"];

		// 截断的 zlib 流必须失败
		$broken = substr(BatchCodec::compress(str_repeat("y", 4096)), 0, 8);
		$out[] = ["truncated_rejected", BatchCodec::streamingDecompress($broken, BatchCodec::MAX_PAYLOAD) === false, "truncated stream"];

		return $out;
	}

	private static function testFramed() : array{
		$out = [];

		$raw = pack("N", 3) . "abc" . pack("N", 2) . "de";
		$seen = [];
		BatchCodec::forEachFramed($raw, function($buffer) use (&$seen){
			$seen[] = $buffer;
		});
		$out[] = ["framed_walks_all", $seen === ["abc", "de"], implode(",", $seen)];

		// 不足 4 字节的长度前缀：安全停止
		$seen = [];
		BatchCodec::forEachFramed("\x00\x00\x00", function($buffer) use (&$seen){
			$seen[] = $buffer;
		});
		$out[] = ["framed_truncated_prefix", $seen === [], "3-byte prefix"];

		// 零长度 / 超长声明：安全停止
		$seen = [];
		BatchCodec::forEachFramed(pack("N", 0) . "abc", function($buffer) use (&$seen){
			$seen[] = $buffer;
		});
		$out[] = ["framed_zero_length", $seen === [], "declared 0"];

		$seen = [];
		BatchCodec::forEachFramed(pack("N", 10) . "abc", function($buffer) use (&$seen){
			$seen[] = $buffer;
		});
		$out[] = ["framed_oversized_length", $seen === [], "declared 10 > remaining 3"];

		// 回调返回 false：提前结束
		$seen = [];
		BatchCodec::forEachFramed(pack("N", 3) . "abc" . pack("N", 2) . "de", function($buffer) use (&$seen){
			$seen[] = $buffer;
			return false;
		});
		$out[] = ["framed_callback_stop", $seen === ["abc"], "stopped after first frame"];

		return $out;
	}

	private static function testV11() : array{
		$out = [];

		// 正常推进：id + 3 字节包体，回调返回下一个包的绝对偏移
		$raw = chr(0x01) . "abc" . chr(0x02) . "de";
		$seen = [];
		BatchCodec::forEachV11($raw, function($id, $offset) use (&$seen){
			$seen[] = [$id, $offset];
			return $offset + 3;
		});
		$out[] = ["v11_walks_all", $seen === [[1, 1], [2, 5]], json_encode($seen)];

		// null：跳过该包继续
		$seen = [];
		BatchCodec::forEachV11(chr(0x07), function($id, $offset) use (&$seen){
			$seen[] = $id;
			return null;
		});
		$out[] = ["v11_null_continues", $seen === [7], "consumed payload"];

		// 非 int：必须终止，不能死循环
		$seen = [];
		BatchCodec::forEachV11(chr(0x01) . "abc" . chr(0x02), function($id, $offset) use (&$seen){
			$seen[] = $id;
			return "bogus";
		});
		$out[] = ["v11_non_int_stops", $seen === [1], "returned string"];

		// 偏移不前进：必须终止
		$seen = [];
		BatchCodec::forEachV11(chr(0x01) . "abc" . chr(0x02), function($id, $offset) use (&$seen){
			$seen[] = $id;
			return $offset;
		});
		$out[] = ["v11_no_advance_stops", $seen === [1], "returned same offset"];

		return $out;
	}
}
