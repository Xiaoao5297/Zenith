<?php

/*
 * ██╗███╗   ██╗ ██████╗ ██████╗ ██████╗ ███████╗
 * ██║████╗  ██║██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║██╔██╗ ██║██║     ██║   ██║██████╔╝█████╗
 * ██║██║╚██╗██║██║     ██║   ██║██╔══██╗██╔══╝
 * ██║██║ ╚████║╚██████╗╚██████╔╝██║  ██║███████╗
 * ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: The_Bird, GoodCat456872
 *
 * @Links:
 *  > https://github.com/QwQ12222/InCore-II#incore-ii
 *  > https://gitcode.com/bad-cat-factory/InCore-II-InCore/tree/InCore-II
 *
 * Ported into Zenith (module B6) from InCore-II; Level/Chunk/Block APIs adapted to Zenith.
*/

declare(strict_types=1);

namespace pocketmine\level\light;

use pocketmine\block\Block;
use pocketmine\level\ChunkManager;
use pocketmine\level\Level;

/**
 * 光照更新队列基类。
 *
 * 所有变更先写入 $updateNodes（同一坐标按 blockHash 去重），由 execute() 统一排空：
 * 先做移除传播（removalQueue），再做扩散传播（spreadQueue）。这样一次 tick 内的
 * 多次 setBlock 只会产生一轮传播，而不是每次 setBlock 都立刻逐格递归。
 */
abstract class LightUpdate{
	private const WORLD_HEIGHT = 128;

	/** @var ChunkManager */
	protected $level;
	/** @var int[][] */
	protected $updateNodes = [];
	/** @var \SplQueue */
	protected $spreadQueue;
	/** @var bool[] */
	protected $spreadVisited = [];
	/** @var \SplQueue */
	protected $removalQueue;
	/** @var bool[] */
	protected $removalVisited = [];

	public function __construct(ChunkManager $level){
		$this->level = $level;
		$this->spreadQueue = new \SplQueue();
		$this->removalQueue = new \SplQueue();
	}

	abstract protected function getLight(int $x, int $y, int $z) : int;

	abstract protected function setLight(int $x, int $y, int $z, int $level) : void;

	public function setAndUpdateLight(int $x, int $y, int $z, int $newLevel) : void{
		if($y < 0 or $y >= self::WORLD_HEIGHT){
			return;
		}

		$this->updateNodes[Level::blockHash($x, $y, $z)] = [$x, $y, $z, max(0, min(15, $newLevel))];
	}

	public function execute() : void{
		$nodes = $this->updateNodes;
		$this->updateNodes = [];

		foreach($nodes as $blockHash => $node){
			$x = $node[0];
			$y = $node[1];
			$z = $node[2];
			$newLevel = $node[3];
			if($this->getChunkAt($x, $y, $z) === null){
				continue;
			}

			$oldLevel = $this->getLight($x, $y, $z);
			if($oldLevel === $newLevel){
				continue;
			}

			$this->setLight($x, $y, $z, $newLevel);
			if($oldLevel < $newLevel){
				$this->spreadVisited[$blockHash] = true;
				$this->spreadQueue->enqueue([$x, $y, $z]);
			}else{
				$this->removalVisited[$blockHash] = true;
				$this->removalQueue->enqueue([$x, $y, $z, $oldLevel]);
			}
		}

		while(!$this->removalQueue->isEmpty()){
			$node = $this->removalQueue->dequeue();
			$x = $node[0];
			$y = $node[1];
			$z = $node[2];
			$oldLevel = $node[3];

			$this->computeRemoveLight($x - 1, $y, $z, $oldLevel);
			$this->computeRemoveLight($x + 1, $y, $z, $oldLevel);
			$this->computeRemoveLight($x, $y - 1, $z, $oldLevel);
			$this->computeRemoveLight($x, $y + 1, $z, $oldLevel);
			$this->computeRemoveLight($x, $y, $z - 1, $oldLevel);
			$this->computeRemoveLight($x, $y, $z + 1, $oldLevel);
		}

		while(!$this->spreadQueue->isEmpty()){
			$node = $this->spreadQueue->dequeue();
			$x = $node[0];
			$y = $node[1];
			$z = $node[2];
			unset($this->spreadVisited[Level::blockHash($x, $y, $z)]);

			if($this->getChunkAt($x, $y, $z) === null){
				continue;
			}

			$level = $this->getLight($x, $y, $z);
			if($level <= 0){
				continue;
			}

			$this->computeSpreadLight($x - 1, $y, $z, $level);
			$this->computeSpreadLight($x + 1, $y, $z, $level);
			$this->computeSpreadLight($x, $y - 1, $z, $level);
			$this->computeSpreadLight($x, $y + 1, $z, $level);
			$this->computeSpreadLight($x, $y, $z - 1, $level);
			$this->computeSpreadLight($x, $y, $z + 1, $level);
		}

		$this->spreadVisited = [];
		$this->removalVisited = [];
	}

	protected function getChunkAt(int $x, int $y, int $z){
		if($y < 0 or $y >= self::WORLD_HEIGHT){
			return null;
		}

		return $this->level->getChunk($x >> 4, $z >> 4);
	}

	protected function getBlockIdAt(int $x, int $y, int $z) : int{
		$chunk = $this->getChunkAt($x, $y, $z);
		return $chunk === null ? Block::AIR : (int) $chunk->getBlockId($x & 0x0f, $y & 0x7f, $z & 0x0f);
	}

	protected function setChunkLight($chunk, int $chunkX, int $chunkZ) : void{
		if(method_exists($chunk, "setChanged")){
			$chunk->setChanged();
		}
		if(method_exists($this->level, "markLightChunkDirty")){
			$this->level->markLightChunkDirty($chunkX, $chunkZ);
		}
	}

	private function computeRemoveLight(int $x, int $y, int $z, int $oldAdjacentLevel) : void{
		if($this->getChunkAt($x, $y, $z) === null){
			return;
		}

		$current = $this->getLight($x, $y, $z);
		if($current !== 0 and $current < $oldAdjacentLevel){
			$this->setLight($x, $y, $z, 0);
			$index = Level::blockHash($x, $y, $z);
			if(!isset($this->removalVisited[$index])){
				$this->removalVisited[$index] = true;
				if($current > 1){
					$this->removalQueue->enqueue([$x, $y, $z, $current]);
				}
			}
		}elseif($current >= $oldAdjacentLevel){
			$index = Level::blockHash($x, $y, $z);
			if(!isset($this->spreadVisited[$index])){
				$this->spreadVisited[$index] = true;
				$this->spreadQueue->enqueue([$x, $y, $z]);
			}
		}
	}

	private function computeSpreadLight(int $x, int $y, int $z, int $sourceLevel) : void{
		if($this->getChunkAt($x, $y, $z) === null){
			return;
		}

		$potentialLevel = $sourceLevel - $this->getLightFilter($this->getBlockIdAt($x, $y, $z));
		if($potentialLevel <= 0){
			return;
		}

		$current = $this->getLight($x, $y, $z);
		if($current >= $potentialLevel){
			return;
		}

		$this->setLight($x, $y, $z, $potentialLevel);
		$index = Level::blockHash($x, $y, $z);
		if($potentialLevel > 1 and !isset($this->spreadVisited[$index])){
			$this->spreadVisited[$index] = true;
			$this->spreadQueue->enqueue([$x, $y, $z]);
		}
	}

	private function getLightFilter(int $blockId) : int{
		if(!isset(Block::$lightFilter[$blockId])){
			return 15;
		}

		return max(1, min(15, (int) Block::$lightFilter[$blockId]));
	}
}
