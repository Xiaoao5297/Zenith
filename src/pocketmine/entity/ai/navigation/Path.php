<?php

namespace pocketmine\entity\ai\navigation;

use pocketmine\math\Vector3;

class Path{

	/** @var Vector3[] */
	private $points = [];

	/** @var int */
	private $currentIndex = 0;

	/**
	 * @param Vector3[] $points
	 */
	public function __construct(array $points){
		$this->points = $points;
	}

	public function getCurrentPoint(): ?Vector3{
		return $this->points[$this->currentIndex] ?? null;
	}

	public function advance(): void{
		$this->currentIndex++;
	}

	public function isDone(): bool{
		return $this->currentIndex >= count($this->points);
	}

	public function getPointCount(): int{
		return count($this->points);
	}

	public function getEndPoint(): ?Vector3{
		$last = end($this->points);
		return $last !== false ? $last : null;
	}
}
