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

namespace lycore\event\entity;

use lycore\entity\Effect;
use lycore\entity\Entity;
use lycore\event\Cancellable;
use lycore\item\FoodSource;

class EntityEatEvent extends EntityEvent implements Cancellable{
	public static $handlerList = null;

	/** @var FoodSource */
	private $foodSource;
	/** @var int */
	private $foodRestore;
	/** @var float */
	private $saturationRestore;
	private $residue;
	/** @var Effect[] */
	private $additionalEffects;

	public function __construct(Entity $entity, FoodSource $foodSource){
		$this->entity = $entity;
		$this->foodSource = $foodSource;
		$this->foodRestore = $foodSource->getFoodRestore();
		$this->saturationRestore = $foodSource->getSaturationRestore();
		$this->residue = $foodSource->getResidue();
		$this->additionalEffects = $foodSource->getAdditionalEffects();
	}

	public function getFoodSource(){
		return $this->foodSource;
	}

	public function getFoodRestore() : int{
		return $this->foodRestore;
	}

	public function setFoodRestore(int $foodRestore){
		$this->foodRestore = $foodRestore;
	}

	public function getSaturationRestore() : float{
		return $this->saturationRestore;
	}

	public function setSaturationRestore(float $saturationRestore){
		$this->saturationRestore = $saturationRestore;
	}

	public function getResidue(){
		return $this->residue;
	}

	public function setResidue($residue){
		$this->residue = $residue;
	}

	/**
	 * @return Effect[]
	 */
	public function getAdditionalEffects(){
		return $this->additionalEffects;
	}

	/**
	 * @param Effect[] $additionalEffects
	 *
	 * @throws \TypeError
	 */
	public function setAdditionalEffects(array $additionalEffects){
		foreach($additionalEffects as $effect){
			if(!($effect instanceof Effect)){
				throw new \TypeError("Argument 1 passed to EntityEatEvent::setAdditionalEffects() must be an Effect array");
			}
		}
		$this->additionalEffects = $additionalEffects;
	}
}
