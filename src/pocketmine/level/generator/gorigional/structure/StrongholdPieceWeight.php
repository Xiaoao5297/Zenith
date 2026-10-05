<?php

namespace pocketmine\level\generator\gorigional\structure;

/**
 * 移植自 SCAXE-GO-CE structure.StrongholdPieceWeight。
 */
class StrongholdPieceWeight{
	/** @var string */
	public $type;
	/** @var int */
	public $weight;
	/** @var int */
	public $limit;
	/** @var int */
	public $instances = 0;

	public function __construct($type, $weight, $limit){
		$this->type = $type;
		$this->weight = $weight;
		$this->limit = $limit;
	}
}
