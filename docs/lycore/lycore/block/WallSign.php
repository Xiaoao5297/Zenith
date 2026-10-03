<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║██║╚██╗ ██║██║   ██║██╔══██╗██╔══╝
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

namespace lycore\block;

use lycore\level\Level;

class WallSign extends SignPost{

	protected $id = self::WALL_SIGN;

	public function getName() : string{
		return "Wall Sign";
	}

	public function onUpdate($type){
		/*
		 * 墙上告示牌的元数据与依附方块方向对应关系：
		 *
		 * 告示牌方向 2，检查方向 3
		 * 告示牌方向 3，检查方向 2
		 * 告示牌方向 4，检查方向 5
		 * 告示牌方向 5，检查方向 4
		 */
		$faces = [
			2 => 3,
			3 => 2,
			4 => 5,
			5 => 4
		];

		if($type === Level::BLOCK_UPDATE_NORMAL){

			$meta = (int) $this->meta;

			if(isset($faces[$meta])){

				$attachedSide = $faces[$meta];

				if($this->getSide($attachedSide)->getId() === self::AIR){

					$this->getLevel()->useBreakOn($this);

					return Level::BLOCK_UPDATE_NORMAL;
				}
			}
		}

		return false;
	}
}