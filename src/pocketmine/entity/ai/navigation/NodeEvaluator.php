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
	public function isWalkable(int $x, int $y, int $z, float $entityHeight): bool{
		// 脚下必须是固体方块
		if(!$this->isSolid($x, $y - 1, $z)){
			return false;
		}

		// 身体位置必须是空气
		if($this->isSolid($x, $y, $z)){
			return false;
		}

		// 对于高度 >= 1 的实体，头部空间也必须是空气
		if($entityHeight >= 1.0 and $this->isSolid($x, $y + 1, $z)){
			return false;
		}

		return true;
	}

	/**
	 * 判断坐标处方块是否为固体
	 */
	public function isSolid(int $x, int $y, int $z): bool{
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
	 * 获取方块的移动代价（水中代价更高）
	 */
	public function getCost(int $x, int $y, int $z): float{
		$block = $this->level->getBlock(new Vector3($x, $y, $z));
		$id = $block->getId();
		if($id === 8 or $id === 9){ // 水
			return 2.0;
		}
		return 1.0;
	}

	public function clearCache(): void{
		$this->blockCache = [];
	}
}
