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

use lycore\entity\Entity;
use lycore\Event;
use lycore\event\Cancellable;
use lycore\math\Vector3;

/**
 * @deprecated
 */
class EntityMoveEvent extends EntityEvent implements Cancellable{
	public static $handlerList = null;

	/** @var \lycore\math\Vector3 */
	private $pos;

	public function __construct(Entity $entity, Vector3 $pos){
		$this->entity = $entity;
		$this->pos = $pos;
	}

	public function getVector(){
		return $this->pos;
	}


}