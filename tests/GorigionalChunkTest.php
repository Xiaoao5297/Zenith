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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/

/**
 * gorigional 区块生成集成测试（不启动服务端）。
 *
 * 覆盖 Java 1.7.10 对齐后的几个关键点：
 *  - 区块随机种子公式（ChunkProviderGenerate.func_147424_a）；
 *  - populate 种子公式（func_73153_a），且确定性；
 *  - 同一个种子、同一个区块重复生成结果逐块一致；
 *  - 基岩层数落在 Java 的 1..5 范围内（nextInt(5)）；
 *  - 洞穴/峡谷确实在地表以下挖出了空气。
 *
 * 用一个最小 ChunkManager + 字节串区块充当运行环境，只跑 generateChunk()，
 * 不跑 populate（那部分依赖村庄/矿洞等结构，离线代价太高）。
 */

use pocketmine\level\ChunkManager;
use pocketmine\level\format\generic\BaseFullChunk;
use pocketmine\level\generator\gorigional\Gorigional;
use pocketmine\utils\Random;

$srcDir = dirname(__DIR__) . "/src/pocketmine";
spl_autoload_register(function($class) use ($srcDir){
	if(strpos($class, "pocketmine\\") !== 0){
		return;
	}
	$path = $srcDir . "/" . str_replace("\\", "/", substr($class, 10)) . ".php";
	if(is_file($path)){
		require_once $path;
	}
});

class GorigionalTestChunk extends BaseFullChunk{
	/** @var bool */
	private $populated = false;
	/** @var bool */
	private $generated = false;

	public function __construct($x, $z){
		parent::__construct(
			null, $x, $z,
			str_repeat("\x00", 32768),
			str_repeat("\x00", 16384),
			str_repeat("\xff", 32768),
			str_repeat("\x00", 32768)
		);
	}

	private function index($x, $y, $z){
		return ($x << 11) | ($z << 7) | $y;
	}

	public function getBlock($x, $y, $z, &$blockId, &$meta = null){
		$full = $this->getFullBlock($x, $y, $z);
		$blockId = $full & 0xff;
		$meta = $full >> 8;
	}

	public function getFullBlock($x, $y, $z){
		$i = $this->index($x, $y, $z);
		$j = $i >> 1;
		if(($y & 1) === 0){
			return ord($this->blocks[$i]) | ((ord($this->data[$j]) & 0x0f) << 8);
		}

		return ord($this->blocks[$i]) | (((ord($this->data[$j]) >> 4) & 0x0f) << 8);
	}

	public function setBlock($x, $y, $z, $blockId = null, $meta = null, $backwash = false){
		$i = $this->index($x, $y, $z);
		if($blockId !== null){
			$this->blocks[$i] = chr($blockId & 0xff);
		}
		if($meta !== null){
			$j = $i >> 1;
			$old = ord($this->data[$j]);
			if(($y & 1) === 0){
				$this->data[$j] = chr(($old & 0xf0) | ($meta & 0x0f));
			}else{
				$this->data[$j] = chr((($meta & 0x0f) << 4) | ($old & 0x0f));
			}
		}

		return true;
	}

	public function getBlockId($x, $y, $z){
		return ord($this->blocks[$this->index($x, $y, $z)]);
	}

	public function setBlockId($x, $y, $z, $id){
		$this->blocks[$this->index($x, $y, $z)] = chr($id & 0xff);
	}

	public function getBlockData($x, $y, $z){
		$j = $this->index($x, $y, $z) >> 1;
		$data = ord($this->data[$j]);
		if(($y & 1) === 0){
			return $data & 0x0f;
		}

		return ($data >> 4) & 0x0f;
	}

	public function setBlockData($x, $y, $z, $data){
		$j = $this->index($x, $y, $z) >> 1;
		$old = ord($this->data[$j]);
		if(($y & 1) === 0){
			$this->data[$j] = chr(($old & 0xf0) | ($data & 0x0f));
		}else{
			$this->data[$j] = chr((($data & 0x0f) << 4) | ($old & 0x0f));
		}
	}

	public function getBlockSkyLight($x, $y, $z){
		$i = ($x << 10) | ($z << 6) | ($y >> 1);
		$sl = ord($this->skyLight[$i]);
		if(($y & 1) === 0){
			return $sl & 0x0f;
		}

		return ($sl >> 4) & 0x0f;
	}

	public function setBlockSkyLight($x, $y, $z, $level){
		$i = ($x << 10) | ($z << 6) | ($y >> 1);
		$old = ord($this->skyLight[$i]);
		if(($y & 1) === 0){
			$this->skyLight[$i] = chr(($old & 0xf0) | ($level & 0x0f));
		}else{
			$this->skyLight[$i] = chr((($level & 0x0f) << 4) | ($old & 0x0f));
		}
	}

	public function getBlockLight($x, $y, $z){
		$i = ($x << 10) | ($z << 6) | ($y >> 1);
		$l = ord($this->blockLight[$i]);
		if(($y & 1) === 0){
			return $l & 0x0f;
		}

		return ($l >> 4) & 0x0f;
	}

	public function setBlockLight($x, $y, $z, $level){
		$i = ($x << 10) | ($z << 6) | ($y >> 1);
		$old = ord($this->blockLight[$i]);
		if(($y & 1) === 0){
			$this->blockLight[$i] = chr(($old & 0xf0) | ($level & 0x0f));
		}else{
			$this->blockLight[$i] = chr((($level & 0x0f) << 4) | ($old & 0x0f));
		}
	}

	public function getBlockIdColumn($x, $z){
		return substr($this->blocks, ($x << 11) + ($z << 7), 128);
	}

	public function getBlockDataColumn($x, $z){
		return substr($this->data, ($x << 10) + ($z << 6), 64);
	}

	public function getBlockSkyLightColumn($x, $z){
		return substr($this->skyLight, ($x << 10) + ($z << 6), 64);
	}

	public function getBlockLightColumn($x, $z){
		return substr($this->blockLight, ($x << 10) + ($z << 6), 64);
	}

	public function isPopulated(){
		return $this->populated;
	}

	public function setPopulated($value = true){
		$this->populated = (bool) $value;
	}

	public function isGenerated(){
		return $this->generated;
	}

	public function setGenerated($value = true){
		$this->generated = (bool) $value;
	}

	public function toBinary(){
		return "";
	}

	public static function fromBinary($data, \pocketmine\level\format\LevelProvider $provider = null){
		return null;
	}

	public static function getEmptyChunk($chunkX, $chunkZ, \pocketmine\level\format\LevelProvider $provider = null){
		return new GorigionalTestChunk($chunkX, $chunkZ);
	}

	/** 导出整块方块 ID，便于比较两次生成结果 */
	public function dumpBlocks(){
		return $this->blocks;
	}
}

class GorigionalTestLevel implements ChunkManager{
	/** @var int */
	private $seed;
	/** @var GorigionalTestChunk[] */
	private $chunks = [];

	public function __construct($seed){
		$this->seed = $seed;
	}

	public function getSeed(){
		return $this->seed;
	}

	public function getChunk(int $chunkX, int $chunkZ){
		$key = $chunkX . ":" . $chunkZ;
		if(!isset($this->chunks[$key])){
			$this->chunks[$key] = new GorigionalTestChunk($chunkX, $chunkZ);
		}

		return $this->chunks[$key];
	}

	public function setChunk(int $chunkX, int $chunkZ, \pocketmine\level\format\FullChunk $chunk = null){
		$this->chunks[$chunkX . ":" . $chunkZ] = $chunk;
	}

	public function getBlockIdAt(int $x, int $y, int $z) : int{
		$chunk = $this->getChunk($x >> 4, $z >> 4);
		return $chunk->getBlockId($x & 15, $y, $z & 15);
	}

	public function setBlockIdAt(int $x, int $y, int $z, int $id){
		$chunk = $this->getChunk($x >> 4, $z >> 4);
		$chunk->setBlockId($x & 15, $y, $z & 15, $id);
	}

	public function getBlockDataAt(int $x, int $y, int $z) : int{
		$chunk = $this->getChunk($x >> 4, $z >> 4);
		return $chunk->getBlockData($x & 15, $y, $z & 15);
	}

	public function setBlockDataAt(int $x, int $y, int $z, int $data){
		$chunk = $this->getChunk($x >> 4, $z >> 4);
		$chunk->setBlockData($x & 15, $y, $z & 15, $data);
	}

	public function getBlockLightAt($x, $y, $z){
		return 0;
	}

	public function setBlockLightAt($x, $y, $z, $level){
	}

	public function updateBlockLight($x, $y, $z){
	}
}

class GorigionalChunkTest{
	public static function run() : array{
		$out = [];

		// Java: new Random(x * 341873128712L + z * 132897987541L)
		$out[] = ["chunk_seed_origin", Gorigional::chunkSeed(0, 0) === 0, Gorigional::chunkSeed(0, 0)];
		$out[] = ["chunk_seed_x", Gorigional::chunkSeed(1, 0) === 341873128712, Gorigional::chunkSeed(1, 0)];
		$out[] = ["chunk_seed_z", Gorigional::chunkSeed(0, 1) === 132897987541, Gorigional::chunkSeed(0, 1)];
		$out[] = ["chunk_seed_both", Gorigional::chunkSeed(1, 1) === 474771116253, Gorigional::chunkSeed(1, 1)];
		$out[] = ["chunk_seed_negative", Gorigional::chunkSeed(-1, 0) === -341873128712, Gorigional::chunkSeed(-1, 0)];

		$p1 = Gorigional::populateSeed(3, -7, 1234);
		$p2 = Gorigional::populateSeed(3, -7, 1234);
		$out[] = ["populate_seed_deterministic", $p1 === $p2, $p1];
		$out[] = ["populate_seed_differs_by_chunk", Gorigional::populateSeed(4, -7, 1234) !== $p1, "=!="];
		$out[] = ["populate_seed_differs_by_world", Gorigional::populateSeed(3, -7, 1235) !== $p1, "=!="];

		// 真实生成两个区块
		$level = new GorigionalTestLevel(1234);
		$generator = new Gorigional([]);
		$generator->init($level, new Random(1234));
		$generator->generateChunk(0, 0);
		$generator->generateChunk(1, 0);

		$chunk = $level->getChunk(0, 0);

		$airBelow50 = 0;
		$stone = 0;
		$bedrockTop = [];
		$heights = [];
		for($x = 0; $x < 16; $x++){
			for($z = 0; $z < 16; $z++){
				$bed = -1;
				$top = -1;
				for($y = 0; $y < 128; $y++){
					$id = $chunk->getBlockId($x, $y, $z);
					if($id === 7){
						$bed = $y;
					}
					if($id !== 0){
						$top = $y;
					}
					if($y < 50 and $id === 0){
						$airBelow50++;
					}
					if($id === 1){
						$stone++;
					}
				}
				$bedrockTop[] = $bed;
				$heights[] = $top;
			}
		}

		$allBedrock = true;
		foreach($bedrockTop as $top){
			if($top < 0 or $top > 4){
				$allBedrock = false;
				break;
			}
		}
		$out[] = ["bedrock_1_to_5_layers", $allBedrock, "顶层范围 " . min($bedrockTop) . ".." . max($bedrockTop)];
		$out[] = ["bedrock_always_at_y0", $chunk->getBlockId(0, 0, 0) === 7, $chunk->getBlockId(0, 0, 0)];
		$out[] = ["stone_present", $stone > 1000, $stone];
		$out[] = ["caves_carved_below_50", $airBelow50 > 100, $airBelow50];
		$out[] = ["surface_variation", max($heights) - min($heights) >= 2, min($heights) . ".." . max($heights)];

		// 确定性：同一区块再生成一次，方块数据必须完全一致
		$before = $chunk->dumpBlocks();
		$level2 = new GorigionalTestLevel(1234);
		$generator2 = new Gorigional([]);
		$generator2->init($level2, new Random(1234));
		$generator2->generateChunk(0, 0);
		$out[] = ["deterministic_chunk", $level2->getChunk(0, 0)->dumpBlocks() === $before, strlen($before) . " bytes"];

		return $out;
	}
}
