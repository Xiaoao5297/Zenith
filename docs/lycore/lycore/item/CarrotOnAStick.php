<?php

namespace lycore\item;

class CarrotOnAStick extends Item{
	public function __construct($meta = 0, $count = 1){
		parent::__construct(self::CARROT_ON_A_STICK, $meta, $count, "Carrot on a Stick");
	}

	public function getMaxStackSize() : int{
		return 1;
	}

	public function getMaxDurability(){
		return 25;
	}
}
