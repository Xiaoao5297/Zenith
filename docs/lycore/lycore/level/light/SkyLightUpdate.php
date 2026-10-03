<?php

declare(strict_types=1);

namespace lycore\level\light;

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
