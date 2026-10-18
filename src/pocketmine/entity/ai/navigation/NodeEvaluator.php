<?php

namespace pocketmine\entity\ai\navigation;

use pocketmine\level\Level;
use pocketmine\math\Vector3;

class NodeEvaluator{

	/** @var Level */
	private $level;

	/** @var array<int, int> 缓存已查询的方块 ID */
	private $blockCache = [];

	public function __construct(Level $level){
		$this->level = $level;
	}

	/**
	 * 判断坐标是否可站立（脚下有方块，身体空间为空）
	 */
	public function isWalkable(int $x, int $y, int $z, float $entityHeight) : bool{
		// 脚下必须是固体方块
		if(!$this->isSolid($x, $y - 1, $z)){
			return false;
		}

		// 身体位置必须是空气
		if($this->isSolid($x, $y, $z)){
			return false;
		}

		// 不能走进岩浆、火、仙人掌等危险方块
		if($this->isDangerous($x, $y, $z) or $this->isDangerous($x, $y - 1, $z)){
			return false;
		}

		// 身体占用的层数按高度计算；1.8 格高的生物只需要 y 和 y+1 两格，
		// y+2 是头顶上方，即使有天花板也不影响通过 2 格高的隧道。
		$bodyLayers = max(1, (int) ceil($entityHeight));
		for($i = 1; $i < $bodyLayers; ++$i){
			if($this->isSolid($x, $y + $i, $z)){
				return false;
			}
		}

		return true;
	}

	/**
	 * 判断坐标处方块是否为固体
	 */
	public function isSolid(int $x, int $y, int $z) : bool{
		$key = ($x & 0xFFFFF) | (($y & 0xFF) << 20) | (($z & 0xFFFFF) << 28);
		if(isset($this->blockCache[$key])){
			return $this->blockCache[$key] === 1;
		}

		$block = $this->level->getBlock(new Vector3($x, $y, $z));
		$solid = $block->isSolid();
		$this->blockCache[$key] = $solid ? 1 : 0;

		// 限制缓存大小
		if(count($this->blockCache) > 4096){
			$this->blockCache = [];
		}

		return $solid;
	}

	/**
	 * 危险方块：岩浆、火、仙人掌
	 */
	public function isDangerous(int $x, int $y, int $z) : bool{
		$id = $this->getBlockId($x, $y, $z);
		return $id === 10 or $id === 11 or $id === 51 or $id === 81;
	}

	/**
	 * 获取方块的移动代价（水中代价更高）
	 */
	public function getCost(int $x, int $y, int $z, float $waterCost = 2.0) : float{
		$id = $this->getBlockId($x, $y, $z);
		if($id === 8 or $id === 9){ // 水
			return $waterCost;
		}
		return 1.0;
	}

	public function getBlockId(int $x, int $y, int $z) : int{
		return $this->level->getBlock(new Vector3($x, $y, $z))->getId();
	}

	public function clearCache() : void{
		$this->blockCache = [];
	}
}
