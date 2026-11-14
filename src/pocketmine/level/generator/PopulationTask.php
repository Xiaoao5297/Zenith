<?php
namespace pocketmine\level\generator;


use pocketmine\item\Item;
use pocketmine\level\format\FullChunk;
use pocketmine\level\Level;
use pocketmine\level\SimpleChunkManager;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\scheduler\AsyncTask;
use pocketmine\Server;
use pocketmine\tile\Chest;
use pocketmine\tile\MobSpawner;
use pocketmine\tile\Tile;


class PopulationTask extends AsyncTask{


	public $state;
	public $levelId;
	public $chunk;
	public $chunkClass;

	public $chunk0;
	public $chunk1;
	public $chunk2;
	public $chunk3;
	//center chunk
	public $chunk5;
	public $chunk6;
	public $chunk7;
	public $chunk8;

	/** @var bool[] whether each of the 9 positions was loaded when the task was created */
	public $chunkLoaded = [];
	/** @var int[] getMutationCounter() 快照，见 BaseFullChunk；比 hasChanged 布尔更可靠（不会被中途保存重置） */
	public $chunkMutationCounter = [];
	/**
	 * @var string[] spl_object_hash 快照，用于识别区块对象是否被卸载重载。
	 * 卸载重载会产生 counter 从 0 重新开始的新对象，单靠 counter 比对可能恰好相等；
	 * 对象身份不一致时放弃回写（宁可丢邻居回写，也不能用陈旧快照覆盖重载区块）。
	 * spl_object_hash 返回字符串，pthreads 属性必须是标量，字符串安全。
	 */
	public $chunkIdentity = [];

	/** @var int */
	public $chunkX;
	/** @var int */
	public $chunkZ;
	/** @var string|null */
	public $errorMessage = null;

	/** @var string JSON，避免 pthreads 把数组转成不可覆盖的 Threaded */
	public $deferredChests = "";
	/** @var string JSON */
	public $deferredSpawners = "";

	public function __construct(Level $level, FullChunk $chunk){
		$this->state = true;
		$this->levelId = $level->getId();
		$this->chunk = $chunk->toFastBinary();
		$this->chunkClass = get_class($chunk);
		$this->chunkX = $chunk->getX();
		$this->chunkZ = $chunk->getZ();

		for($i = 0; $i < 9; ++$i){
			if($i === 4){
				$this->chunkLoaded[4] = true;
				$this->chunkMutationCounter[4] = $chunk->getMutationCounter();
				$this->chunkIdentity[4] = spl_object_hash($chunk);
				continue;
			}
			$xx = -1 + $i % 3;
			$zz = -1 + (int) ($i / 3);
			$ck = $level->getChunk($chunk->getX() + $xx, $chunk->getZ() + $zz, false);
			$this->chunkLoaded[$i] = $ck !== null;
			$this->chunkMutationCounter[$i] = $ck !== null ? $ck->getMutationCounter() : 0;
			$this->chunkIdentity[$i] = $ck !== null ? spl_object_hash($ck) : "";
			$this->{"chunk$i"} = $ck !== null ? $ck->toFastBinary() : null;
		}
	}

	public function onRun(){
		try{
			$this->runPopulation();
		}catch(\Throwable $e){
			$this->state = false;
			$this->errorMessage = "PopulationTask failed for chunk ({$this->chunkX}, {$this->chunkZ}): " . $e->getMessage();
		}
	}

	private function runPopulation(){
		/** @var SimpleChunkManager $manager */
		$manager = $this->getFromThreadStore("generation.level{$this->levelId}.manager");
		/** @var Generator $generator */
		$generator = $this->getFromThreadStore("generation.level{$this->levelId}.generator");
		if($manager === null or $generator === null){
			$this->state = false;
			return;
		}

		/** @var FullChunk[] $chunks */
		$chunks = [];
		/** @var FullChunk $chunkC */
		$chunkC = $this->chunkClass;

		$chunk = $chunkC::fromFastBinary($this->chunk);

		for($i = 0; $i < 9; ++$i){
			if($i === 4){
				continue;
			}
			$xx = -1 + $i % 3;
			$zz = -1 + (int) ($i / 3);
			$ck = $this->{"chunk$i"};
			if($ck === null){
				// 未加载的邻居区块稍后也不会写回 Level，生成它纯属浪费。
				$chunks[$i] = null;
			}else{
				$chunks[$i] = $chunkC::fromFastBinary($ck);
			}
		}

		if($chunk === null){
			//TODO error
			return;
		}

		$manager->setChunk($chunk->getX(), $chunk->getZ(), $chunk);
		if(!$chunk->isGenerated()){
			$generator->generateChunk($chunk->getX(), $chunk->getZ());
			$chunk->setGenerated();
		}

		foreach($chunks as $c){
			if($c !== null){
				$manager->setChunk($c->getX(), $c->getZ(), $c);
				if(!$c->isGenerated()){
					$generator->generateChunk($c->getX(), $c->getZ());
					$c = $manager->getChunk($c->getX(), $c->getZ());
					$c->setGenerated();
				}
			}
		}

		$generator->populateChunk($chunk->getX(), $chunk->getZ());

		// 异步 populate 阶段区块没有 LevelProvider，箱子/刷怪笼先记录下来，
		// 回到主线程、区块重新绑定 provider 后再创建 tile。
		if(method_exists($generator, 'takeDeferredTiles')){
			$deferred = $generator->takeDeferredTiles();
			$this->deferredChests = json_encode($deferred[0]);
			$this->deferredSpawners = json_encode($deferred[1]);
		}

		$chunk = $manager->getChunk($chunk->getX(), $chunk->getZ());
		$chunk->recalculateHeightMap();
		$chunk->populateSkyLight();
		$chunk->setLightPopulated();
		$chunk->setPopulated();
		$this->chunk = $chunk->toFastBinary();

		$manager->setChunk($chunk->getX(), $chunk->getZ(), null);

		foreach($chunks as $i => $c){
			if($c !== null){
				$c = $chunks[$i] = $manager->getChunk($c->getX(), $c->getZ());
				if(!$c->hasChanged()){
					$chunks[$i] = null;
				}
			}else{
				//This way non-changed chunks are not set
				$chunks[$i] = null;
			}
		}

		$manager->cleanChunks();

		for($i = 0; $i < 9; ++$i){
			if($i === 4){
				continue;
			}

			$this->{"chunk$i"} = $chunks[$i] !== null ? $chunks[$i]->toFastBinary() : null;
		}
	}

	public function onCompletion(Server $server){
		$level = $server->getLevel($this->levelId);
		if($level !== null){
			if($this->state === false){
				if($this->errorMessage !== null){
					$level->getServer()->getLogger()->error($this->errorMessage);
				}
				$level->cancelChunkPopulation($this->chunkX, $this->chunkZ);
				$level->registerGenerator();
				return;
			}

			/** @var FullChunk $chunkC */
			$chunkC = $this->chunkClass;

			$chunk = $chunkC::fromFastBinary($this->chunk, $level->getProvider());

			if($chunk === null){
				$level->cancelChunkPopulation($this->chunkX, $this->chunkZ);
				return;
			}

			for($i = 0; $i < 9; ++$i){
				if($i === 4){
					continue;
				}
				$c = $this->{"chunk$i"};
				if($c === null){
					continue;
				}
				//The async side filled this position with an empty chunk (it was not loaded
				//when the task was created). Writing it back could swallow the real chunk the
				//main thread has loaded since. Only write back chunks whose mutation counter
				//has not advanced since the snapshot was taken (any change increments it,
				//and it is never reset, not even by an intermediate save).
				if(!$this->chunkLoaded[$i]){
					continue;
				}
				$cx = $chunk->getX() - 1 + $i % 3;
				$cz = $chunk->getZ() - 1 + (int) ($i / 3);
				$current = $level->getChunk($cx, $cz, false);
				//Identity check first: if the chunk was unloaded and reloaded while the task was
				//running, the new object's counter restarts at 0 and may coincide with the
				//snapshot. Only the counter check would then let a stale snapshot overwrite the
				//reloaded chunk (BUG-028). A different object means the write-back is abandoned.
				if($current === null or !isset($this->chunkIdentity[$i]) or !is_string($this->chunkIdentity[$i]) or $this->chunkIdentity[$i] === "" or spl_object_hash($current) !== $this->chunkIdentity[$i]){
					continue;
				}
				if($current->getMutationCounter() !== $this->chunkMutationCounter[$i]){
					continue;
				}
				$c = $chunkC::fromFastBinary($c, $level->getProvider());
				if($c !== null){
					$level->generateChunkCallback($c->getX(), $c->getZ(), $c, $current === null ? null : $this->chunkMutationCounter[$i]);
				}
			}

			$currentCenter = $level->getChunk($chunk->getX(), $chunk->getZ(), false);
			//Center chunk follows the same identity rule as the neighbors (BUG-028): a reload
			//during the task window creates a fresh object whose counter may coincide with the
			//snapshot. Skip the write-back and cancel the population instead of overwriting the
			//reloaded chunk with stale data.
			if($currentCenter !== null and isset($this->chunkIdentity[4]) and is_string($this->chunkIdentity[4]) and $this->chunkIdentity[4] !== "" and spl_object_hash($currentCenter) === $this->chunkIdentity[4] and $currentCenter->getMutationCounter() === $this->chunkMutationCounter[4]){
				$level->generateChunkCallback($chunk->getX(), $chunk->getZ(), $chunk, $this->chunkMutationCounter[4]);
			}else{
				$level->cancelChunkPopulation($chunk->getX(), $chunk->getZ());
			}
			$this->applyDeferredTiles($level);
		}
	}

	private function applyDeferredTiles(Level $level){
		// 队列以 JSON 字符串跨线程传递；pthreads 不能安全覆盖 Threaded 数组属性。
		$chests = json_decode($this->deferredChests, true);
		if(!is_array($chests)){
			$chests = [];
		}
		foreach($chests as $entry){
			list($x, $y, $z, $items) = $entry;
			$chunk = $level->getChunk($x >> 4, $z >> 4, false);
			if($chunk === null || $chunk->getTile($x & 15, $y, $z & 15) !== null){
				continue;
			}
			$nbt = new CompoundTag("", [
				"id" => new StringTag("id", Tile::CHEST),
				"x" => new IntTag("x", $x),
				"y" => new IntTag("y", $y),
				"z" => new IntTag("z", $z)
			]);
			$tile = Tile::createTile(Tile::CHEST, $chunk, $nbt);
			if($tile instanceof Chest){
				foreach($items as $item){
					$tile->setItem($item[0], Item::get($item[1], $item[2], $item[3]));
				}
			}
		}

		$spawners = json_decode($this->deferredSpawners, true);
		if(!is_array($spawners)){
			$spawners = [];
		}
		foreach($spawners as $entry){
			list($x, $y, $z, $entityId) = $entry;
			$chunk = $level->getChunk($x >> 4, $z >> 4, false);
			if($chunk === null || $chunk->getTile($x & 15, $y, $z & 15) !== null){
				continue;
			}
			$nbt = new CompoundTag("", [
				"id" => new StringTag("id", Tile::MOB_SPAWNER),
				"x" => new IntTag("x", $x),
				"y" => new IntTag("y", $y),
				"z" => new IntTag("z", $z)
			]);
			$tile = Tile::createTile(Tile::MOB_SPAWNER, $chunk, $nbt);
			if($tile instanceof MobSpawner){
				$tile->setEntityId($entityId);
			}
		}
	}
}
