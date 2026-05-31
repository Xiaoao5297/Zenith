<?php

namespace pocketmine\entity;

use pocketmine\nbt\tag\ByteTag;

abstract class Animal extends Mob implements Ageable{
	

	public function initEntity(){
		parent::initEntity();
		if(!isset($this->namedtag->IsBaby)){
			$this->namedtag->IsBaby = new ByteTag("IsBaby", 1);
			$this->setBaby(false);
		}
	}

	public function isBaby(){
		return $this->namedtag["IsBaby"] == 0 ? false : true;
	}
	
	public function setBaby(bool $resting){
		$this->setDataProperty(self::DATA_IS_BABY, self::DATA_TYPE_BYTE, $resting ? 1 : 0);
		$this->namedtag->IsBaby = new ByteTag("IsBaby", $resting ? 1 : 0);
	}
	
	public function isInLove(){
		return $this->getDataProperty(self::DATA_IN_LOVE) === 1;
	}
	
	public function setInLove(bool $resting){
		$this->setDataProperty(self::DATA_IN_LOVE, self::DATA_TYPE_BYTE, $resting ? 1 : 0);
	}
}