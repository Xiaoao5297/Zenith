<?php

namespace pocketmine\entity\ai\navigation;

use pocketmine\math\Vector3;

class Path{

	/** @var Vector3[] */
	private $points = [];

	/** @var int */
	private $currentIndex = 0;

	/** @var bool 是否是“只能走到离目标最近节点”的部分路径 */
	private $partial = false;

	/**
	 * @param Vector3[] $points
	 */
	public function __construct(array $points, bool $partial = false){
		$this->points = array_values($points);
		$this->partial = $partial;
	}

	public function getCurrentPoint() : ?Vector3{
		return $this->points[$this->currentIndex] ?? null;
	}

	public function getPoint(int $index) : ?Vector3{
		return $this->points[$index] ?? null;
	}

	/** @return Vector3[] */
	public function getPoints() : array{
		return $this->points;
	}

	public function getCurrentIndex() : int{
		return $this->currentIndex;
	}

	public function setCurrentIndex(int $index) : void{
		$this->currentIndex = max(0, min($index, count($this->points)));
	}

	public function advance() : void{
		++$this->currentIndex;
	}

	public function isDone() : bool{
		return $this->currentIndex >= count($this->points);
	}

	public function getPointCount() : int{
		return count($this->points);
	}

	public function isPartial() : bool{
		return $this->partial;
	}

	public function getEndPoint() : ?Vector3{
		if(count($this->points) === 0){
			return null;
		}
		return $this->points[count($this->points) - 1];
	}
}
