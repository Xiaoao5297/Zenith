<?php

/*
 * ██╗███╗   ██╗ ██████╗ ██████╗ ██████╗ ███████╗
 * ██║████╗  ██║██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║██╔██╗ ██║██║     ██║   ██║██████╔╝█████╗
 * ██║██║╚██╗██║██║     ██║   ██║██╔══██╗██╔══╝
 * ██║██║ ╚████║╚██████╗╚██████╔╝██║  ██║███████╗
 * ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ported to Zenith from InCore-II (B1 活塞体系移植)；上游 LGPLv3 头部原样保留。
 *
 * @Author: The_Bird, GoodCat456872
 *
 * @Links:
 *  > https://github.com/QwQ12222/InCore-II#incore-ii
 *  > https://gitcode.com/bad-cat-factory/InCore-II-InCore/tree/InCore-II
*/

namespace pocketmine\block;

class Piston extends PistonBase{
	protected $id = self::PISTON;

	public function getName() : string{
		return "Piston";
	}

	protected function createHead($face){
		$head = new PistonHead();
		$head->setFacing($face);
		return $head;
	}
}
