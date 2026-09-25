<?php
namespace pocketmine\level\generator;


use pocketmine\level\format\FullChunk;

use pocketmine\level\Level;
use pocketmine\level\SimpleChunkManager;
use pocketmine\scheduler\AsyncTask;
use pocketmine\Server;


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
	/** @var bool[] hasChanged() snapshot of each loaded position */
	public $chunkChanged = [];

	/** @var int */
	public $chunkX;
	/** @var int */
	public $chunkZ;
	/** @var string|null */
	public $errorMessage = null;

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
				$this->chunkChanged[4] = $chunk->hasChanged();
				continue;
			}
			$xx = -1 + $i % 3;
			$zz = -1 + (int) ($i / 3);
			$ck = $level->getChunk($chunk->getX() + $xx, $chunk->getZ() + $zz, false);
			$this->chunkLoaded[$i] = $ck !== null;
			$this->chunkChanged[$i] = $ck !== null ? $ck->hasChanged() : false;
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
				$chunks[$i] = $chunkC::getEmptyChunk($chunk->getX() + $xx, $chunk->getZ() + $zz);
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
				//main thread has loaded since. Only write back chunks that were loaded and
				//have not been modified on the main thread after the snapshot was taken.
				if(!$this->chunkLoaded[$i]){
					continue;
				}
				$cx = $chunk->getX() - 1 + $i % 3;
				$cz = $chunk->getZ() - 1 + (int) ($i / 3);
				$current = $level->getChunk($cx, $cz, false);
				if($current !== null and $current->hasChanged() !== $this->chunkChanged[$i]){
					continue;
				}
				$c = $chunkC::fromFastBinary($c, $level->getProvider());
				if($c !== null){
					$level->generateChunkCallback($c->getX(), $c->getZ(), $c, $this->chunkChanged[$i]);
				}
			}

			$level->generateChunkCallback($chunk->getX(), $chunk->getZ(), $chunk, $this->chunkChanged[4]);
		}
	}
}
