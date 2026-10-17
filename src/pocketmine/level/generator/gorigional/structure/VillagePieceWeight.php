<?php

namespace pocketmine\level\generator\gorigional\structure;

/**
 * 移植自 Java 1.7.10 StructureVillagePieces$PieceWeight。
 */
class VillagePieceWeight{
	/** @var string */
	public $className;
	/** @var int */
	public $weight;
	/** @var int */
	public $instances = 0;
	/** @var int */
	public $limit;

	public function __construct($className, $weight, $limit){
		$this->className = $className;
		$this->weight = $weight;
		$this->limit = $limit;
	}

	public function isValid($size){
		return $this->limit === 0 || $this->instances < $this->limit;
	}

	public function canContinue(){
		return $this->limit === 0 || $this->instances < $this->limit;
	}
}
