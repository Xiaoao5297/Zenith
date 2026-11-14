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
 * @Author: The_Bird, GoodCat456872
 *
 * @Links:
 *  > https://github.com/QwQ12222/InCore-II#incore-ii
 *  > https://gitcode.com/bad-cat-factory/InCore-II-InCore/tree/InCore-II
 *
 * Ported into Zenith (module B6) from InCore-II; Level/Chunk/Block APIs adapted to Zenith.
*/

declare(strict_types=1);

namespace pocketmine\level\light;

/**
 * 天空光更新队列实现：读写区块的 skylight 半字节数组。
 */
class SkyLightUpdate extends LightUpdate{

	protected function getLight(int $x, int $y, int $z) : int{
		$chunk = $this->getChunkAt($x, $y, $z);
		return $chunk === null ? 0 : (int) $chunk->getBlockSkyLight($x & 0x0f, $y & 0x7f, $z & 0x0f);
	}

	protected function setLight(int $x, int $y, int $z, int $level) : void{
		$chunk = $this->getChunkAt($x, $y, $z);
		if($chunk === null){
			return;
		}

		$localX = $x & 0x0f;
		$localY = $y & 0x7f;
		$localZ = $z & 0x0f;
		$level &= 0x0f;
		if($chunk->getBlockSkyLight($localX, $localY, $localZ) === $level){
			return;
		}

		$chunk->setBlockSkyLight($localX, $localY, $localZ, $level);
		$this->setChunkLight($chunk, $x >> 4, $z >> 4);
	}
}
