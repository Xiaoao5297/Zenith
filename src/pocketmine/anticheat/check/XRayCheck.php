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


namespace pocketmine\anticheat\check;

use pocketmine\Player;
use pocketmine\block\Block;
use pocketmine\math\Vector3;

/**
 * 透视（X-Ray）检测：按挖掘方块统计矿石发现率。
 *
 * 修复了旧版配置键名/单位不匹配（thresholds 为百分比，代码按比例读取）的问题；
 * 命中进入统一缓冲。
 */
class XRayCheck extends Check{

	/** @var int[] */
	private static $NORMAL_ORES = [
		Block::COAL_ORE,
		Block::IRON_ORE,
	];

	/** @var int[] */
	private static $RARE_ORES = [
		Block::GOLD_ORE,
		Block::LAPIS_ORE,
		Block::REDSTONE_ORE,
		Block::GLOWING_REDSTONE_ORE,
	];

	/** @var int[] */
	private static $PRECIOUS_ORES = [
		Block::DIAMOND_ORE,
		Block::EMERALD_ORE,
	];

	public function getDisplayName() : string{
		return "XRay";
	}

	public function clearPlayerData(string $playerName){
	}

	public function check(Player $player, Block $block){
		if(!$this->enabled) return;

		$blockId = $block->getId();
		if(!in_array($blockId, array_merge(self::$NORMAL_ORES, self::$RARE_ORES, self::$PRECIOUS_ORES), true)){
			return;
		}

		$data = $this->getPlayerData($player);

		$currentTime = round(microtime(true) * 1000);
		$resetInterval = (int) ($this->config["reset-interval"] ?? 180000);
		$lastReset = (int) $data->getState("xray.lastReset", $currentTime);

		if($currentTime - $lastReset > $resetInterval){
			$data->setState("xray.ores", []);
			$data->setState("xray.total", 0);
			$data->setState("xray.lastReset", $currentTime);
		}

		$ores = $data->getState("xray.ores", []);
		$ores[$blockId] = ($ores[$blockId] ?? 0) + 1;
		$data->setState("xray.ores", $ores);

		$total = (int) $data->getState("xray.total", 0) + 1;
		$data->setState("xray.total", $total);

		$minBlocks = (int) ($this->config["min-blocks-before-check"] ?? 30);
		if($total < $minBlocks){
			return;
		}

		$thresholds = $this->config["thresholds"] ?? [];
		$normalThreshold = ((float) ($thresholds["common"] ?? 15)) / 100.0;
		$rareThreshold = ((float) ($thresholds["rare"] ?? 5)) / 100.0;
		$preciousThreshold = ((float) ($thresholds["precious"] ?? 2)) / 100.0;

		$suspicious = false;
		$oreType = "";
		$ratio = 0.0;

		$normalCount = $this->countOres($ores, self::$NORMAL_ORES);
		$rareCount = $this->countOres($ores, self::$RARE_ORES);
		$preciousCount = $this->countOres($ores, self::$PRECIOUS_ORES);

		if($normalCount > 0 and ($ratio = $normalCount / $total) > $normalThreshold){
			$suspicious = true;
			$oreType = "普通矿石";
		}elseif($rareCount > 0 and ($ratio = $rareCount / $total) > $rareThreshold){
			$suspicious = true;
			$oreType = "稀有矿石";
		}elseif($preciousCount > 0 and ($ratio = $preciousCount / $total) > $preciousThreshold){
			$suspicious = true;
			$oreType = "珍贵矿石";
		}

		if(!$suspicious){
			$this->decay($player, 0.5);
			return;
		}

		if((bool) ($this->config["check-exposed"] ?? true) and $this->isOreExposed($block)){
			return;
		}

		$detail = sprintf("%s发现率 %.2f%% (总挖掘 %d)", $oreType, $ratio * 100, $total);
		$this->flag($player, $detail, 1.0);
	}

	private function countOres(array $ores, array $oreTypes) : int{
		$count = 0;
		foreach($oreTypes as $oreId){
			$count += $ores[$oreId] ?? 0;
		}
		return $count;
	}

	private function isOreExposed(Block $block) : bool{
		$level = $block->getLevel();
		if($level === null){
			return false;
		}

		$x = $block->getFloorX();
		$y = $block->getFloorY();
		$z = $block->getFloorZ();

		$directions = [
			[1, 0, 0], [-1, 0, 0],
			[0, 1, 0], [0, -1, 0],
			[0, 0, 1], [0, 0, -1],
		];

		foreach($directions as $dir){
			$neighbor = $level->getBlock(new Vector3($x + $dir[0], $y + $dir[1], $z + $dir[2]));
			if($neighbor->getId() === Block::AIR or $neighbor->getId() === Block::WATER or $neighbor->getId() === Block::STILL_WATER){
				return true;
			}
		}

		return false;
	}
}
