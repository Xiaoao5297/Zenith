<?php

/**
 * A3 回归测试：InCore-II 洞穴/峡谷雕刻逻辑（不启动服务端）。
 *
 * 覆盖 src/pocketmine/level/generator/populator/Cave.php 中可离线验证的纯逻辑：
 *  - mixSeed / nextLongSeed 的确定性与取值范围（世界生成的种子链不能被改坏）；
 *  - 隧道半径、峡谷宽度因子表的形状与确定性；
 *  - canReach 的裁剪半径（越界隧道必须被放弃，否则会白跑大量雕刻）；
 *  - 沙漠洞穴替换表（y >= 58 -> 砂岩，否则石头）；
 *  - carveEllipsoid 的区块裁剪、液体阻断与岩浆层；
 *  - carveCanyon / populate 的确定性，且写入永不越出目标区块。
 *
 * 依赖：Block、Biome、ChunkManager、Populator、Cave、Random。
 * 这里用 CaveTestChunkManager 冒充一个单区块的 ChunkManager，只保留世界坐标
 * 到区块局部坐标的映射，越界写入会被记到 outsideWrites 上。
 */

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\level\generator\biome\Biome;
use pocketmine\level\generator\populator\Cave;
use pocketmine\utils\Random;

$src = dirname(__DIR__) . "/src/pocketmine";
require_once $src . "/math/Vector3.php";
require_once $src . "/metadata/Metadatable.php";
require_once $src . "/level/Position.php";
require_once $src . "/block/Block.php";
require_once $src . "/level/generator/biome/Biome.php";
require_once $src . "/level/ChunkManager.php";
require_once $src . "/level/generator/populator/Populator.php";
require_once $src . "/level/generator/populator/Cave.php";
require_once $src . "/utils/Random.php";

class CaveTestChunk{
	/** @var int */
	private $biome;

	public function __construct(int $biome){
		$this->biome = $biome;
	}

	public function getBiomeId($x, $z){
		return $this->biome;
	}
}

class CaveTestChunkManager implements ChunkManager{
	const SIZE = 16;
	const HEIGHT = 128;

	/** @var int */
	public $seed;
	/** @var int[][][] */
	public $blocks = [];
	/** @var int */
	public $writes = 0;
	/** @var int */
	public $outsideWrites = 0;
	/** @var CaveTestChunk */
	private $chunk;
	/** @var int */
	private $chunkX;
	/** @var int */
	private $chunkZ;

	public function __construct(int $seed, int $chunkX, int $chunkZ, int $biome = Biome::PLAINS){
		$this->seed = $seed;
		$this->chunkX = $chunkX;
		$this->chunkZ = $chunkZ;
		$this->chunk = new CaveTestChunk($biome);
		$this->fill(Block::STONE);
	}

	public function fill(int $id){
		for($x = 0; $x < self::SIZE; ++$x){
			for($y = 0; $y < self::HEIGHT; ++$y){
				for($z = 0; $z < self::SIZE; ++$z){
					$this->blocks[$x][$y][$z] = $id;
				}
			}
		}
	}

	public function inside(int $x, int $y, int $z) : bool{
		return ($x >> 4) === $this->chunkX && ($z >> 4) === $this->chunkZ && $y >= 0 && $y < self::HEIGHT;
	}

	public function getBlockIdAt(int $x, int $y, int $z) : int{
		if(!$this->inside($x, $y, $z)){
			return Block::STONE;
		}

		return $this->blocks[$x & 0x0f][$y][$z & 0x0f];
	}

	public function setBlockIdAt(int $x, int $y, int $z, $id){
		if(!$this->inside($x, $y, $z)){
			++$this->outsideWrites;
			return;
		}
		$this->blocks[$x & 0x0f][$y][$z & 0x0f] = (int) $id;
		++$this->writes;
	}

	public function getBlockDataAt(int $x, int $y, int $z) : int{
		return 0;
	}

	public function setBlockDataAt(int $x, int $y, int $z, $data){
	}

	public function getBlockLightAt($x, $y, $z){
		return 0;
	}

	public function updateBlockLight($x, $y, $z){
	}

	public function setBlockLightAt($x, $y, $z, $level){
	}

	public function getChunk(int $chunkX, int $chunkZ){
		return $this->chunk;
	}

	public function setChunk(int $chunkX, int $chunkZ, \pocketmine\level\format\FullChunk $chunk = null){
	}

	public function getSeed(){
		return $this->seed;
	}
}

class CaveGeneratorTest{

	/** @var \ReflectionClass|null */
	private static $reflection = null;

	public static function run() : array{
		return array_merge(
			self::testSeedMixing(),
			self::testTunnelThickness(),
			self::testCanyonWidthFactors(),
			self::testCanReach(),
			self::testDesertReplacement(),
			self::testEllipsoidLiquidGuard(),
			self::testEllipsoidChunkClip(),
			self::testCarveCanyonDeterminism(),
			self::testPopulateDeterminism()
		);
	}

	private static function reflect() : \ReflectionClass{
		if(self::$reflection === null){
			self::$reflection = new \ReflectionClass(Cave::class);
		}

		return self::$reflection;
	}

	private static function invoke(string $method, array $args){
		$ref = self::reflect()->getMethod($method);
		$ref->setAccessible(true);

		return $ref->invokeArgs(new Cave(), $args);
	}

	private static function testSeedMixing() : array{
		$results = [];

		$first = self::invoke("mixSeed", [12345]);
		$second = self::invoke("mixSeed", [12345]);
		$other = self::invoke("mixSeed", [12346]);
		$negative = self::invoke("mixSeed", [-987654321]);
		$results[] = ["mix_seed_deterministic", $first === $second, "mixSeed(12345) = $first"];
		$results[] = ["mix_seed_discriminates", $first !== $other, "$first vs $other"];
		$results[] = ["mix_seed_31bit_range", $first >= 0 && $first <= 0x7fffffff, (string) $first];
		$results[] = ["mix_seed_handles_negative", $negative >= 0 && $negative <= 0x7fffffff, (string) $negative];

		$random = new Random(9876);
		$seeds = [];
		$inRange = true;
		for($i = 0; $i < 32; ++$i){
			$seed = self::invoke("nextLongSeed", [$random]);
			$seeds[] = $seed;
			if($seed < 0 || $seed > 0x7fffffff){
				$inRange = false;
			}
		}
		$results[] = ["next_long_seed_range", $inRange, "32 个种子"];
		$results[] = ["next_long_seed_varies", count(array_unique($seeds)) > 28, count(array_unique($seeds)) . "/32 个不同值"];

		return $results;
	}

	private static function testTunnelThickness() : array{
		$randomA = new Random(4242);
		$randomB = new Random(4242);
		$inRange = true;
		$same = true;
		$samples = [];
		for($i = 0; $i < 200; ++$i){
			$a = self::invoke("getTunnelThickness", [$randomA]);
			$b = self::invoke("getTunnelThickness", [$randomB]);
			$samples[] = $a;
			if($a !== $b){
				$same = false;
			}
			if($a <= 0.0 || $a > 12.0){
				$inRange = false;
			}
		}

		return [
			["tunnel_thickness_deterministic", $same, "同种子 200 次一致"],
			["tunnel_thickness_range", $inRange, "max = " . max($samples)]
		];
	}

	private static function testCanyonWidthFactors() : array{
		$table = self::invoke("initCanyonWidthFactors", [new Random(777)]);
		$expectedSize = Cave::WORLD_MAX_Y - Cave::WORLD_MIN_Y + 1;
		$allPositive = true;
		foreach($table as $value){
			if($value < 1.0){
				$allPositive = false;
				break;
			}
		}
		$again = self::invoke("initCanyonWidthFactors", [new Random(777)]);

		return [
			["canyon_width_factor_count", count($table) === $expectedSize, count($table) . " 层"],
			["canyon_width_factor_positive", $allPositive, "最小值 " . min($table)],
			["canyon_width_factor_deterministic", $table === $again, "同种子表一致"]
		];
	}

	private static function testCanReach() : array{
		$near = self::invoke("canReach", [8.0, 8.0, 9.0, 10.0, 0, 96, 2.0]);
		$far = self::invoke("canReach", [8.0, 8.0, 300.0, 300.0, 0, 96, 2.0]);
		$farLate = self::invoke("canReach", [8.0, 8.0, 300.0, 300.0, 95, 96, 2.0]);

		return [
			["can_reach_near", $near === true, "起点附近应可达"],
			["can_reach_far", $far === false, "远处隧道应被放弃"],
			["can_reach_far_late_step", $farLate === false, "末尾步远处仍应放弃"]
		];
	}

	private static function testDesertReplacement() : array{
		$desertLow = self::invoke("getDesertCaveReplacement", [57]);
		$desertHigh = self::invoke("getDesertCaveReplacement", [58]);
		$desertTop = self::invoke("getDesertCaveReplacement", [127]);

		$desertLevel = new CaveTestChunkManager(1, 0, 0, Biome::DESERT);
		$plainsLevel = new CaveTestChunkManager(1, 0, 0, Biome::PLAINS);
		$desertSurface = self::invoke("getBiomeSurfaceReplacement", [$desertLevel, 4, 70, 4]);
		$desertDeep = self::invoke("getBiomeSurfaceReplacement", [$desertLevel, 4, 30, 4]);
		$plainsSurface = self::invoke("getBiomeSurfaceReplacement", [$plainsLevel, 4, 70, 4]);

		return [
			["desert_replacement_below_58", $desertLow === Block::STONE, "y=57 -> {$desertLow}"],
			["desert_replacement_at_58", $desertHigh === Block::SANDSTONE, "y=58 -> {$desertHigh}"],
			["desert_replacement_top", $desertTop === Block::SANDSTONE, "y=127 -> {$desertTop}"],
			["biome_surface_desert_shallow", $desertSurface === Block::SANDSTONE, "y=70 -> {$desertSurface}"],
			["biome_surface_desert_deep", $desertDeep === Block::STONE, "y=30 -> {$desertDeep}"],
			["biome_surface_plains", $plainsSurface === Block::GRASS, "y=70 -> {$plainsSurface}"]
		];
	}

	private static function testEllipsoidLiquidGuard() : array{
		$level = new CaveTestChunkManager(99, 0, 0);
		$level->fill(Block::STONE);
		$level->blocks[8][30][8] = Block::WATER;

		$cave = new Cave();
		$carved = $cave->carveEllipsoid($level, 0, 0, 8.5, 30.5, 8.5, 3.0, 2.0);
		$stoneLeft = $level->blocks[7][30][8] === Block::STONE;

		return [
			["ellipsoid_skips_liquid", $carved === false, "含水区域应整段放弃"],
			["ellipsoid_liquid_untouched", $stoneLeft && $level->writes === 0, "writes = {$level->writes}"]
		];
	}

	private static function testEllipsoidChunkClip() : array{
		$level = new CaveTestChunkManager(99, 0, 0);
		$level->fill(Block::STONE);

		$cave = new Cave();
		$outside = $cave->carveEllipsoid($level, 0, 0, 100.5, 30.5, 100.5, 3.0, 2.0);
		$outsideWrites = $level->outsideWrites;

		$center = $cave->carveEllipsoid($level, 0, 0, 0.5, 30.5, 0.5, 4.0, 3.0);
		$insideWrites = $level->writes;
		$lavaBelow = true;
		$airAbove = false;
		for($x = 0; $x < 16; ++$x){
			for($z = 0; $z < 16; ++$z){
				for($y = 0; $y < 128; ++$y){
					$id = $level->blocks[$x][$y][$z];
					if($id === Block::LAVA && $y > Cave::LAVA_LEVEL){
						$lavaBelow = false;
					}
					if($id === Block::AIR && $y > Cave::LAVA_LEVEL){
						$airAbove = true;
					}
				}
			}
		}

		return [
			["ellipsoid_outside_chunk_noop", $outside === false && $outsideWrites === 0, "outsideWrites = $outsideWrites"],
			["ellipsoid_center_carves", $center === true && $insideWrites > 0, "writes = $insideWrites"],
			["ellipsoid_lava_only_in_lava_layer", $lavaBelow, "LAVA_LEVEL = " . Cave::LAVA_LEVEL],
			["ellipsoid_air_above_lava", $airAbove, "y > " . Cave::LAVA_LEVEL]
		];
	}

	private static function testCarveCanyonDeterminism() : array{
		$runA = new CaveTestChunkManager(2024, 0, 0);
		$runB = new CaveTestChunkManager(2024, 0, 0);
		$caveA = new Cave();
		$caveB = new Cave();
		$caveA->carveCanyon($runA, new Random(31337), 0, 0, 8.0, 40.0, 8.0, 5.0, 0.75, 0.05, 24);
		$caveB->carveCanyon($runB, new Random(31337), 0, 0, 8.0, 40.0, 8.0, 5.0, 0.75, 0.05, 24);

		$air = 0;
		for($x = 0; $x < 16; ++$x){
			for($y = 0; $y < 128; ++$y){
				for($z = 0; $z < 16; ++$z){
					if($runA->blocks[$x][$y][$z] === Block::AIR){
						++$air;
					}
				}
			}
		}

		return [
			["canyon_deterministic", $runA->blocks === $runB->blocks, "同种子同输入"],
			["canyon_carves_something", $air > 0, "air = $air"],
			["canyon_stays_in_chunk", $runA->outsideWrites === 0, "outsideWrites = {$runA->outsideWrites}"]
		];
	}

	private static function testPopulateDeterminism() : array{
		$runA = new CaveTestChunkManager(12345, 0, 2);
		$runB = new CaveTestChunkManager(12345, 0, 2);
		$caveA = new Cave();
		$caveB = new Cave();
		$caveA->populate($runA, 0, 2, new Random(1));
		$caveB->populate($runB, 0, 2, new Random(2));

		$air = 0;
		for($x = 0; $x < 16; ++$x){
			for($y = 0; $y < 128; ++$y){
				for($z = 0; $z < 16; ++$z){
					if($runA->blocks[$x][$y][$z] === Block::AIR){
						++$air;
					}
				}
			}
		}

		return [
			["populate_ignores_incoming_random_state", $runA->blocks === $runB->blocks, "输入 Random 种子不同，结果相同"],
			["populate_carves_in_chunk", $air > 0, "air = $air"],
			["populate_stays_in_chunk", $runA->outsideWrites === 0, "outsideWrites = {$runA->outsideWrites}"],
			["populate_writes_blocks", $runA->writes === $air + self::countLava($runA->blocks), "writes = {$runA->writes}"]
		];
	}

	private static function countLava(array $blocks) : int{
		$lava = 0;
		for($x = 0; $x < 16; ++$x){
			for($y = 0; $y < 128; ++$y){
				for($z = 0; $z < 16; ++$z){
					if($blocks[$x][$y][$z] === Block::LAVA){
						++$lava;
					}
				}
			}
		}

		return $lava;
	}
}
