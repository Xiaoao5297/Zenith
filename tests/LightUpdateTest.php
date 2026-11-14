<?php

/**
 * Zero-dependency tests for the B6 light update queue
 * (src/pocketmine/level/light/LightUpdate.php + SkyLightUpdate.php).
 *
 * 不启动 PocketMine 运行时：这里用一个假的 ChunkManager / 区块对象喂真实的
 * LightUpdate::execute()，只验证队列语义（入队、去重、边界、排空顺序、传播/移除）。
 */

$zlRoot = dirname(__DIR__) . "/src";
if(!class_exists("pocketmine\\level\\light\\LightUpdate", false)){
	spl_autoload_register(function($class) use ($zlRoot){
		if(strpos($class, "pocketmine\\") === 0){
			$path = $zlRoot . "/" . str_replace("\\", "/", $class) . ".php";
			if(is_file($path)){
				require_once $path;
			}
		}
	});
}

/**
 * 区块替身：只实现 LightUpdate 用到的四个方法。
 * skylight / 方块 id 都用 [x][y][z] 稀疏数组保存，缺省为 0（AIR / 无光）。
 */
class LightUpdateTestFakeChunk{

	public $skyLight = [];
	public $blocks = [];
	public $changed = 0;

	public function getBlockId($x, $y, $z){
		return isset($this->blocks[$x][$y][$z]) ? $this->blocks[$x][$y][$z] : 0;
	}

	public function setBlockId($x, $y, $z, $id){
		$this->blocks[$x][$y][$z] = $id & 0xff;
	}

	public function getBlockSkyLight($x, $y, $z){
		return isset($this->skyLight[$x][$y][$z]) ? $this->skyLight[$x][$y][$z] : 0;
	}

	public function setBlockSkyLight($x, $y, $z, $level){
		$this->skyLight[$x][$y][$z] = $level & 0x0f;
	}

	public function setChanged($changed = true){
		$this->changed++;
	}
}

class LightUpdateTestFakeLevel implements \pocketmine\level\ChunkManager{

	/** @var LightUpdateTestFakeChunk[] */
	public $chunks = [];

	/** @return LightUpdateTestFakeChunk|null */
	public function getChunk(int $chunkX, int $chunkZ){
		$key = $chunkX . ":" . $chunkZ;
		return isset($this->chunks[$key]) ? $this->chunks[$key] : null;
	}

	public function setChunk(int $chunkX, int $chunkZ, \pocketmine\level\format\FullChunk $chunk = null){
		$this->chunks[$chunkX . ":" . $chunkZ] = $chunk;
	}

	public function addChunk(int $chunkX, int $chunkZ) : LightUpdateTestFakeChunk{
		$chunk = new LightUpdateTestFakeChunk();
		$this->chunks[$chunkX . ":" . $chunkZ] = $chunk;
		return $chunk;
	}

	public function getBlockIdAt(int $x, int $y, int $z) : int{
		$chunk = $this->getChunk($x >> 4, $z >> 4);
		return $chunk === null ? 0 : (int) $chunk->getBlockId($x & 0x0f, $y & 0x7f, $z & 0x0f);
	}

	public function setBlockIdAt(int $x, int $y, int $z, int $id){
		$chunk = $this->getChunk($x >> 4, $z >> 4);
		if($chunk !== null){
			$chunk->setBlockId($x & 0x0f, $y & 0x7f, $z & 0x0f, $id);
		}
	}

	public function getBlockDataAt(int $x, int $y, int $z) : int{
		return 0;
	}

	public function setBlockDataAt(int $x, int $y, int $z, int $data){
	}

	public function getBlockLightAt($x, $y, $z){
		return 0;
	}

	public function updateBlockLight($x, $y, $z){
	}

	public function setBlockLightAt($x, $y, $z, $level){
	}

	public function getSeed(){
		return 0;
	}
}

/** 暴露受保护的 $updateNodes，供断言“入队但未执行”的状态。 */
class LightUpdateTestProbe extends \pocketmine\level\light\SkyLightUpdate{

	/** @return int[][] */
	public function pending() : array{
		return $this->updateNodes;
	}

	public function pendingCount() : int{
		return count($this->updateNodes);
	}
}

class LightUpdateTest{

	/**
	 * 建一个 1x1 区块的世界，区块内所有方块为空气、天空光默认 0。
	 *
	 * @return array{0: LightUpdateTestFakeLevel, 1: LightUpdateTestProbe}
	 */
	private static function newWorld(bool $withChunk = true) : array{
		$level = new LightUpdateTestFakeLevel();
		if($withChunk){
			$level->addChunk(0, 0);
		}
		return [$level, new LightUpdateTestProbe($level)];
	}

	/** 取区块内的天空光（全局坐标）。 */
	private static function light(LightUpdateTestFakeLevel $level, int $x, int $y, int $z){
		$chunk = $level->getChunk($x >> 4, $z >> 4);
		if($chunk === null){
			return null;
		}
		return $chunk->getBlockSkyLight($x & 0x0f, $y & 0x7f, $z & 0x0f);
	}

	public static function run() : array{
		\pocketmine\block\Block::init();

		$results = [];

		// 1. 同一坐标多次入队按 blockHash 去重，后写覆盖先写
		list($level, $update) = self::newWorld();
		$update->setAndUpdateLight(5, 64, 5, 10);
		$update->setAndUpdateLight(5, 64, 5, 12);
		$update->setAndUpdateLight(5, 64, 5, 12);
		$nodes = $update->pending();
		$results[] = [
			"same coordinate is deduped",
			$update->pendingCount() === 1
				&& $nodes[\pocketmine\level\Level::blockHash(5, 64, 5)][3] === 12,
			"pending=" . $update->pendingCount()
		];

		// 2. 入队不立即落地，execute() 才写入区块
		$results[] = [
			"enqueue does not write light",
			self::light($level, 5, 64, 5) === 0,
			"light=" . self::light($level, 5, 64, 5)
		];
		$update->execute();
		$results[] = [
			"execute writes queued light",
			self::light($level, 5, 64, 5) === 12 && $update->pendingCount() === 0,
			"light=" . self::light($level, 5, 64, 5)
		];

		// 3. 世界高度边界：y<0 与 y>=128 丢弃，y=0 / y=127 接受
		list(, $update) = self::newWorld();
		$update->setAndUpdateLight(0, -1, 0, 15);
		$update->setAndUpdateLight(0, 128, 0, 15);
		$update->setAndUpdateLight(0, 255, 0, 15);
		$rejected = $update->pendingCount();
		$update->setAndUpdateLight(0, 0, 0, 15);
		$update->setAndUpdateLight(0, 127, 0, 15);
		$results[] = [
			"y outside 0..127 is rejected",
			$rejected === 0 && $update->pendingCount() === 2,
			"rejected=$rejected accepted=" . $update->pendingCount()
		];

		// 4. 亮度钳制到 0..15
		list(, $update) = self::newWorld();
		$update->setAndUpdateLight(1, 64, 1, 99);
		$update->setAndUpdateLight(2, 64, 1, -7);
		$nodes = $update->pending();
		$results[] = [
			"light level is clamped to 0..15",
			$nodes[\pocketmine\level\Level::blockHash(1, 64, 1)][3] === 15
				&& $nodes[\pocketmine\level\Level::blockHash(2, 64, 1)][3] === 0,
			"high=" . $nodes[\pocketmine\level\Level::blockHash(1, 64, 1)][3]
				. " low=" . $nodes[\pocketmine\level\Level::blockHash(2, 64, 1)][3]
		];

		// 5. 空队列 execute() 是安全的空操作
		list($level, $update) = self::newWorld();
		$update->execute();
		$update->execute();
		$results[] = [
			"empty queue execute is a no-op",
			$update->pendingCount() === 0 && self::light($level, 0, 64, 0) === 0,
			"no exception"
		];

		// 6. 目标区块不存在时不崩溃，节点照样被清空
		list($level, $update) = self::newWorld(false);
		$update->setAndUpdateLight(5, 64, 5, 15);
		$update->execute();
		$results[] = [
			"missing chunk is skipped",
			$update->pendingCount() === 0,
			"pending=0"
		];

		// 7. 空中扩散：15 -> 14 -> 13 ...（空气衰减 1）
		list($level, $update) = self::newWorld();
		$update->setAndUpdateLight(5, 64, 5, 15);
		$update->execute();
		$results[] = [
			"sky light spreads through air",
			self::light($level, 5, 64, 5) === 15
				&& self::light($level, 6, 64, 5) === 14
				&& self::light($level, 5, 65, 5) === 14
				&& self::light($level, 9, 64, 5) === 11
				&& self::light($level, 6, 64, 6) === 13,
			"15/14/14/11/13"
		];

		// 8. 重复 execute() 不会继续改光（队列已排空）
		$snapshot = [$level->chunks["0:0"]->skyLight];
		$update->execute();
		$results[] = [
			"second execute is idempotent",
			$level->chunks["0:0"]->skyLight === $snapshot[0],
			"unchanged"
		];

		// 9. 移除：把光源改成 0 后整片旧光被清掉
		$update->setAndUpdateLight(5, 64, 5, 0);
		$update->execute();
		$results[] = [
			"removal clears propagated light",
			self::light($level, 5, 64, 5) === 0
				&& self::light($level, 6, 64, 5) === 0
				&& self::light($level, 9, 64, 5) === 0
				&& self::light($level, 5, 65, 5) === 0,
			"all zero"
		];

		// 10. 不透明方块阻断扩散：石头的 lightFilter 是 15
		list($level, $update) = self::newWorld();
		$level->setBlockIdAt(6, 64, 5, \pocketmine\block\Block::STONE);
		$update->setAndUpdateLight(5, 64, 5, 15);
		$update->execute();
		$results[] = [
			"opaque block blocks spread",
			self::light($level, 6, 64, 5) === 0
				&& self::light($level, 6, 65, 5) === 13
				&& self::light($level, 7, 64, 5) < 15,
			"stone cell=0, above=13"
		];

		// 11. 负坐标（区块 -1）不会越界或串区块
		list($level, $update) = self::newWorld();
		$level->addChunk(-1, 0);
		$update->setAndUpdateLight(-1, 64, 5, 15);
		$update->execute();
		$results[] = [
			"negative coordinates stay in their chunk",
			self::light($level, -1, 64, 5) === 15
				&& self::light($level, 0, 64, 5) === 14
				&& self::light($level, 5, 64, 5) === 9,
			"-1:64:5=15 | 0:64:5=14 | 5:64:5=9"
		];

		// 12. 同一批次混合“移除 + 扩散”：先排空移除队列，再排空扩散队列
		list($level, $update) = self::newWorld();
		$update->setAndUpdateLight(1, 64, 1, 15);
		$update->execute();

		$update->setAndUpdateLight(1, 64, 1, 0);
		$update->setAndUpdateLight(10, 64, 10, 10);
		$update->execute();
		$first = $level->chunks["0:0"]->skyLight;
		$update->execute();
		$results[] = [
			"mixed batch is stable",
			$level->chunks["0:0"]->skyLight === $first
				&& self::light($level, 1, 64, 1) === 0
				&& self::light($level, 2, 64, 1) === 0
				&& self::light($level, 10, 64, 10) === 10
				&& self::light($level, 11, 64, 10) === 9,
			"removed=0 new source=10 neighbor=9"
		];

		return $results;
	}
}
