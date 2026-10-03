<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝
 * ╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 *  ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: U core
 *
 * @Links:
 *  > LY Core
 *  > LY Core Project
*/

namespace lycore\level\generator;


use lycore\level\format\FullChunk;

use lycore\level\Level;
use lycore\level\SimpleChunkManager;
use lycore\math\Vector3;
use lycore\scheduler\AsyncTask;
use lycore\Server;


class PopulationTask extends AsyncTask{

	const DEFAULT_POPULATION_RADIUS = 1;

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

	public $chunks = [];
	public $populationRadius;
	public $scheduledBlockUpdates = "";

	public function __construct(Level $level, FullChunk $chunk, int $populationRadius = self::DEFAULT_POPULATION_RADIUS){
		$this->state = true;
		$this->levelId = $level->getId();
		$this->chunk = $chunk->toFastBinary();
		$this->chunkClass = get_class($chunk);
		$this->populationRadius = max(self::DEFAULT_POPULATION_RADIUS, $populationRadius);

		if($this->populationRadius === self::DEFAULT_POPULATION_RADIUS){
			for($i = 0; $i < 9; ++$i){
				if($i === 4){
					continue;
				}
				$xx = -1 + $i % 3;
				$zz = -1 + (int) ($i / 3);
				$ck = $level->getChunk($chunk->getX() + $xx, $chunk->getZ() + $zz, false);
				$this->{"chunk$i"} = $ck !== null ? $ck->toFastBinary() : null;
			}
		}else{
			for($xx = -$this->populationRadius; $xx <= $this->populationRadius; ++$xx){
				for($zz = -$this->populationRadius; $zz <= $this->populationRadius; ++$zz){
					if($xx === 0 && $zz === 0){
						continue;
					}
					$key = $xx . ":" . $zz;
					$ck = $level->getChunk($chunk->getX() + $xx, $chunk->getZ() + $zz, false);
					$this->chunks[$key] = $ck !== null ? $ck->toFastBinary() : null;
				}
			}
		}
	}

	private function readChunkOffset(string $key) : array{
		$parts = explode(":", $key, 2);
		return [(int) $parts[0], (int) ($parts[1] ?? 0)];
	}

	public function onRun(){
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

		if($chunk === null){
			//TODO error
			return;
		}

		if($this->populationRadius === self::DEFAULT_POPULATION_RADIUS){
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
		}else{
			foreach($this->chunks as $key => $serializedChunk){
				list($xx, $zz) = $this->readChunkOffset($key);
				$chunks[$key] = $serializedChunk !== null ?
					$chunkC::fromFastBinary($serializedChunk) :
					$chunkC::getEmptyChunk($chunk->getX() + $xx, $chunk->getZ() + $zz);
			}
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
		$this->scheduledBlockUpdates = serialize(method_exists($manager, "getScheduledBlockUpdates") ? $manager->getScheduledBlockUpdates() : []);
		$structureFootprintChunks = method_exists($manager, "getStructureFootprintChunks") ? $manager->getStructureFootprintChunks() : [];
		$structureFootprintIndex = [];
		foreach($structureFootprintChunks as $chunkPos){
			if(!is_array($chunkPos) || !isset($chunkPos["chunkX"], $chunkPos["chunkZ"])){
				continue;
			}
			$footprintChunkX = (int) $chunkPos["chunkX"];
			$footprintChunkZ = (int) $chunkPos["chunkZ"];
			$structureFootprintIndex[Level::chunkHash($footprintChunkX, $footprintChunkZ)] = true;

			$footprintChunk = $manager->getChunk($footprintChunkX, $footprintChunkZ);
			if($footprintChunk !== null){
				$footprintChunk->recalculateHeightMap();
				$footprintChunk->populateSkyLight();
				$footprintChunk->setLightPopulated();
				$footprintChunk->setGenerated();
				$footprintChunk->setPopulated();
				$footprintChunk->setChanged();
			}
		}

		$chunk = $manager->getChunk($chunk->getX(), $chunk->getZ());
		$chunk->recalculateHeightMap();
		$chunk->populateSkyLight();
		$chunk->setLightPopulated();
		$chunk->setPopulated();
		$manager->populateBlockLight();
		$this->chunk = $chunk->toFastBinary();

		$manager->setChunk($chunk->getX(), $chunk->getZ(), null);

		foreach($chunks as $key => $c){
			if($c !== null){
				$updatedChunk = $manager->getChunk($c->getX(), $c->getZ());
				$chunks[$key] = ($updatedChunk !== null && ($updatedChunk->hasChanged() || isset($structureFootprintIndex[Level::chunkHash($c->getX(), $c->getZ())]))) ? $updatedChunk : null;
			}else{
				//This way non-changed chunks are not set
				$chunks[$key] = null;
			}
		}

		$manager->cleanChunks();

		if($this->populationRadius === self::DEFAULT_POPULATION_RADIUS){
			for($i = 0; $i < 9; ++$i){
				if($i === 4){
					continue;
				}

				$this->{"chunk$i"} = $chunks[$i] !== null ? $chunks[$i]->toFastBinary() : null;
			}
		}else{
			foreach($chunks as $key => $c){
				$this->chunks[$key] = $c !== null ? $c->toFastBinary() : null;
			}
		}
	}

	public function onCompletion(Server $server){
		$level = $server->getLevel($this->levelId);
		if($level !== null){
			if($this->state === false){
				$level->registerGenerator();
				return;
			}

			/** @var FullChunk $chunkC */
			$chunkC = $this->chunkClass;

			$chunk = $chunkC::fromFastBinary($this->chunk, $level->getProvider());

			if($chunk === null){
				//TODO error
				return;
			}

			if($this->populationRadius === self::DEFAULT_POPULATION_RADIUS){
				for($i = 0; $i < 9; ++$i){
					if($i === 4){
						continue;
					}
					$c = $this->{"chunk$i"};
					if($c !== null){
						$c = $chunkC::fromFastBinary($c, $level->getProvider());
						$level->generateChunkCallback($c->getX(), $c->getZ(), $c, true);
					}
				}
			}else{
				foreach($this->chunks as $c){
					if($c !== null){
						$c = $chunkC::fromFastBinary($c, $level->getProvider());
						$level->generateChunkCallback($c->getX(), $c->getZ(), $c, true);
					}
				}
			}

			$level->generateChunkCallback($chunk->getX(), $chunk->getZ(), $chunk, true);

			$scheduledBlockUpdates = unserialize($this->scheduledBlockUpdates);
			if(!is_array($scheduledBlockUpdates)){
				$scheduledBlockUpdates = [];
			}
			foreach($scheduledBlockUpdates as $update){
				if(count($update) < 4){
					continue;
				}
				$level->scheduleUpdate(new Vector3((int) $update[0], (int) $update[1], (int) $update[2]), (int) $update[3]);
			}
		}
	}
}
