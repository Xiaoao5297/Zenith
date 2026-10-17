<?php

/***
 *  _____                _  __   __
 * /__  /  ___   ____   (_)/ /_ / /_
 *   / /  / _ \ / __ \ / // __// __ \
 *  / /__/  __// / / // // /_ / / / /
 * /____/\___//_/ /_//_/ \__//_/ /_/
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/


namespace pocketmine\level\generator\gorigional\structure;

/**
 * 移植自 SCAXE-GO-CE structure 的 WorldAccess 接口。
 */
interface WorldAccess{

	public function getBlockId($x, $y, $z);

	public function setBlock($x, $y, $z, $id, $meta);

	/**
	 * @param array $items [[slot, id, meta, count], ...]
	 */
	public function setChestContents($x, $y, $z, array $items);

	public function setSpawnerEntity($x, $y, $z, $entityId);

	public function getHeight($x, $z);
}
