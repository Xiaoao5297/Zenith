<?php

namespace pocketmine\level\generator;


use pocketmine\level\format\FullChunk;

use pocketmine\level\Level;
use pocketmine\level\SimpleChunkManager;
use pocketmine\scheduler\AsyncTask;
use pocketmine\Server;


class GenerationTask extends AsyncTask{

	public $state;
	public $levelId;
	public $chunk;
	public $chunkClass;

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
	}

	public function onRun(){
		try{
			$this->runGeneration();
		}catch(\Throwable $e){
			$this->state = false;
			$this->errorMessage = "GenerationTask failed for chunk ({$this->chunkX}, {$this->chunkZ}): " . $e->getMessage();
		}
	}

	private function runGeneration(){
		/** @var SimpleChunkManager $manager */
		$manager = $this->getFromThreadStore("generation.level{$this->levelId}.manager");
		/** @var Generator $generator */
		$generator = $this->getFromThreadStore("generation.level{$this->levelId}.generator");
		if($manager === null or $generator === null){
			$this->state = false;
			return;
		}

		/** @var FullChunk $chunk */
		$chunk = $this->chunkClass;
		$chunk = $chunk::fromFastBinary($this->chunk);
		if($chunk === null){
			//TODO error
			return;
		}

		$manager->setChunk($chunk->getX(), $chunk->getZ(), $chunk);

		$generator->generateChunk($chunk->getX(), $chunk->getZ());

		$chunk = $manager->getChunk($chunk->getX(), $chunk->getZ());
		$chunk->setGenerated();
		$this->chunk = $chunk->toFastBinary();

		$manager->setChunk($chunk->getX(), $chunk->getZ(), null);
	}

	public function onCompletion(Server $server){
		$level = $server->getLevel($this->levelId);
		if($level !== null){
			if($this->state === false){
				if($this->errorMessage !== null){
					$level->getServer()->getLogger()->error($this->errorMessage);
				}
				$level->cancelChunkGeneration($this->chunkX, $this->chunkZ);
				$level->registerGenerator();
				return;
			}
			/** @var FullChunk $chunk */
			$chunk = $this->chunkClass;
			$chunk = $chunk::fromFastBinary($this->chunk, $level->getProvider());
			if($chunk === null){
				$level->cancelChunkGeneration($this->chunkX, $this->chunkZ);
				return;
			}
			$level->generateChunkCallback($chunk->getX(), $chunk->getZ(), $chunk);
		}
	}
}
