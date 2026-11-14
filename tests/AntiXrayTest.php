<?php

/**
 * Zero-dependency tests for the Anti-Xray chunk obfuscator (A1).
 *
 * The obfuscator works on the raw FullChunkData payload layout:
 *   32768 bytes of block ids + 16384 bytes of nibble metadata.
 */

$zmRoot = dirname(__DIR__) . "/src";
if(!class_exists("pocketmine\\level\\AntiXrayObfuscator", false)){
	spl_autoload_register(function($class) use ($zmRoot){
		if(strpos($class, "pocketmine\\") === 0){
			$path = $zmRoot . "/" . str_replace("\\", "/", $class) . ".php";
			if(is_file($path)){
				require_once $path;
			}
		}
	});
}

class AntiXrayTest{

	private const PAYLOAD_SIZE = 49152;

	private static function index($x, $y, $z){
		return ($x << 11) | ($z << 7) | $y;
	}

	private static function payload(){
		return str_repeat(chr(\pocketmine\block\Block::STONE), 32768) . str_repeat("\x00", 16384);
	}

	private static function idAt($payload, $index){
		return ord($payload[$index]);
	}

	private static function setMeta(&$payload, $index, $meta){
		$dataIndex = 32768 + ($index >> 1);
		$byte = ord($payload[$dataIndex]);
		if(($index & 1) === 0){
			$payload[$dataIndex] = chr(($byte & 0xf0) | ($meta & 0x0f));
		}else{
			$payload[$dataIndex] = chr((($meta & 0x0f) << 4) | ($byte & 0x0f));
		}
	}

	private static function metaAt($payload, $index){
		$dataIndex = 32768 + ($index >> 1);
		$byte = ord($payload[$dataIndex]);
		return ($index & 1) === 0 ? ($byte & 0x0f) : (($byte >> 4) & 0x0f);
	}

	public static function run() : array{
		$results = [];
		$obf = \pocketmine\level\AntiXrayObfuscator::class;
		$oreIndex = self::index(8, 20, 8);
		$magic = [\pocketmine\block\Block::GOLD_ORE, \pocketmine\block\Block::IRON_ORE, \pocketmine\block\Block::COAL_ORE, \pocketmine\block\Block::LAPIS_ORE,
			\pocketmine\block\Block::DIAMOND_ORE, \pocketmine\block\Block::REDSTONE_ORE, \pocketmine\block\Block::EMERALD_ORE, \pocketmine\block\Block::NETHERRACK];

		$short = "short";
		$results[] = ["short payload returned unchanged", $obf::obfuscateChunkPayload($short) === $short, ""];

		$notString = [1, 2, 3];
		$results[] = ["non-string payload returned unchanged", $obf::obfuscateChunkPayload($notString) === $notString, ""];

		$payload = self::payload();
		$payload[$oreIndex] = chr(\pocketmine\block\Block::DIAMOND_ORE);
		$out = $obf::obfuscateChunkPayload($payload);
		$expected = $magic[$oreIndex & 0x07];
		$results[] = ["obfuscator mode replaces underground block",
			self::idAt($out, $oreIndex) === $expected,
			"index=$oreIndex got=" . self::idAt($out, $oreIndex) . " expected=$expected"];
		$results[] = ["obfuscator mode result is one of the magic blocks",
			in_array(self::idAt($out, $oreIndex), $magic, true), ""];

		$payload = self::payload();
		$payload[$oreIndex] = chr(\pocketmine\block\Block::DIAMOND_ORE);
		self::setMeta($payload, $oreIndex, 5);
		$out = $obf::obfuscateChunkPayload($payload, ["mode" => "hidden"]);
		$results[] = ["hidden mode replaces ore with fake block",
			self::idAt($out, $oreIndex) === \pocketmine\block\Block::STONE, "got=" . self::idAt($out, $oreIndex)];
		$results[] = ["hidden mode clears block metadata", self::metaAt($out, $oreIndex) === 0, ""];

		$payload = self::payload();
		$out = $obf::obfuscateChunkPayload($payload, ["mode" => "hidden"]);
		$results[] = ["hidden mode keeps non-ore blocks",
			self::idAt($out, $oreIndex) === \pocketmine\block\Block::STONE, "got=" . self::idAt($out, $oreIndex)];

		$payload = self::payload();
		$payload[$oreIndex] = chr(\pocketmine\block\Block::DIAMOND_ORE);
		$out = $obf::obfuscateChunkPayload($payload, ["mode" => "hidden", "fakeBlock" => \pocketmine\block\Block::GLASS]);
		$results[] = ["hidden mode honours custom fakeBlock",
			self::idAt($out, $oreIndex) === \pocketmine\block\Block::GLASS, "got=" . self::idAt($out, $oreIndex)];

		$payload = self::payload();
		$out = $obf::obfuscateChunkPayload($payload, ["mode" => "hidden", "ores" => [\pocketmine\block\Block::STONE]]);
		$results[] = ["hidden mode honours custom ore list",
			self::idAt($out, $oreIndex) === \pocketmine\block\Block::STONE, "stone should be treated as an ore here"];

		$deepIndex = self::index(8, 100, 8);
		$payload = self::payload();
		$payload[$deepIndex] = chr(\pocketmine\block\Block::DIAMOND_ORE);
		$out = $obf::obfuscateChunkPayload($payload);
		$results[] = ["blocks above scan height are untouched",
			self::idAt($out, $deepIndex) === \pocketmine\block\Block::DIAMOND_ORE, "y=100, maxY=79"];

		$payload = self::payload();
		$lowIndex = self::index(8, 40, 8);
		$payload[$lowIndex] = chr(\pocketmine\block\Block::DIAMOND_ORE);
		$out = $obf::obfuscateChunkPayload($payload, ["scanChunkHeightLimit" => 1]);
		$results[] = ["scanChunkHeightLimit limits the scanned volume",
			self::idAt($out, $lowIndex) === \pocketmine\block\Block::DIAMOND_ORE, "limit=1 -> maxY=31"];

		$edgeBottom = self::index(8, 16, 8);
		$edgeTop = self::index(8, 31, 8);
		$payload = self::payload();
		$payload[$edgeBottom] = chr(\pocketmine\block\Block::DIAMOND_ORE);
		$payload[$edgeTop] = chr(\pocketmine\block\Block::DIAMOND_ORE);
		$out = $obf::obfuscateChunkPayload($payload);
		$results[] = ["section boundary rows are skipped",
			self::idAt($out, $edgeBottom) === \pocketmine\block\Block::DIAMOND_ORE and self::idAt($out, $edgeTop) === \pocketmine\block\Block::DIAMOND_ORE,
			"y=16 and y=31 have section-local 0/15"];

		$payload = self::payload();
		$payload[$oreIndex] = chr(\pocketmine\block\Block::DIAMOND_ORE);
		$payload[$oreIndex + 1] = chr(\pocketmine\block\Block::AIR);
		$out = $obf::obfuscateChunkPayload($payload);
		$results[] = ["blocks adjacent to filtered blocks are skipped",
			self::idAt($out, $oreIndex) === \pocketmine\block\Block::DIAMOND_ORE, "air neighbour is a filter id"];

		$results[] = ["default ore set contains the nine overworld/nether ores",
			count($obf::getDefaultOreIds()) === 9, "count=" . count($obf::getDefaultOreIds())];

		$results[] = ["default filter set is non-empty",
			count($obf::getDefaultFilterIds()) > 100, "count=" . count($obf::getDefaultFilterIds())];

		return $results;
	}
}
