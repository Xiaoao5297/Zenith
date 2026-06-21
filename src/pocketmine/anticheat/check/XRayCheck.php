<?php

namespace pocketmine\anticheat\check;

use pocketmine\anticheat\AntiCheat;
use pocketmine\Player;
use pocketmine\block\Block;
use pocketmine\math\Vector3;

class XRayCheck extends Check{

	/** @var array */
	private $violations = [];

	/** @var array */
	private $oreDiscoveryCount = [];

	/** @var array */
	private $totalBlocksMined = [];

	/** @var array */
	private $lastResetTime = [];

	/** @var array */
	private static $NORMAL_ORES = [
		Block::COAL_ORE,
		Block::IRON_ORE
	];

	/** @var array */
	private static $RARE_ORES = [
		Block::GOLD_ORE,
		Block::LAPIS_ORE,
		Block::REDSTONE_ORE,
		Block::GLOWING_REDSTONE_ORE
	];

	/** @var array */
	private static $PRECIOUS_ORES = [
		Block::DIAMOND_ORE,
		Block::EMERALD_ORE
	];

	/** @var array|null */
	private static $ALL_ORES = null;

	public function __construct(AntiCheat $antiCheat, array $config){
		parent::__construct($antiCheat, $config);
		self::$ALL_ORES = array_merge(self::$NORMAL_ORES, self::$RARE_ORES, self::$PRECIOUS_ORES);
	}

	public function clearPlayerData(string $playerName){
		unset($this->violations[$playerName]);
		unset($this->oreDiscoveryCount[$playerName]);
		unset($this->totalBlocksMined[$playerName]);
		unset($this->lastResetTime[$playerName]);
	}

	public function check(Player $player, Block $block){
		$name = $player->getName();

		if(!$this->enabled) return;

		if(!in_array($block->getId(), self::$ALL_ORES)){
			return;
		}

		$currentTime = round(microtime(true) * 1000);
		$lastReset = isset($this->lastResetTime[$name]) ? $this->lastResetTime[$name] : $currentTime;
		$resetInterval = (int) ($this->getConfig()["reset-interval"] ?? 300000);

		if($currentTime - $lastReset > $resetInterval){
			unset($this->oreDiscoveryCount[$name]);
			unset($this->totalBlocksMined[$name]);
			$this->lastResetTime[$name] = $currentTime;
		}

		if(!isset($this->oreDiscoveryCount[$name])){
			$this->oreDiscoveryCount[$name] = [];
		}
		$playerOres = &$this->oreDiscoveryCount[$name];

		$blockId = $block->getId();
		$playerOres[$blockId] = isset($playerOres[$blockId]) ? $playerOres[$blockId] + 1 : 1;

		$total = isset($this->totalBlocksMined[$name]) ? $this->totalBlocksMined[$name] + 1 : 1;
		$this->totalBlocksMined[$name] = $total;

		$minBlocks = (int) ($this->getConfig()["min-blocks-before-check"] ?? 50);
		if($total < $minBlocks){
			return;
		}

		$normalThreshold = (float) ($this->getConfig()["normal-ore-threshold"] ?? 0.15);
		$rareThreshold = (float) ($this->getConfig()["rare-ore-threshold"] ?? 0.05);
		$preciousThreshold = (float) ($this->getConfig()["precious-ore-threshold"] ?? 0.02);

		$suspicious = false;
		$oreType = "";
		$ratio = 0;

		$normalCount = $this->countOres($playerOres, self::$NORMAL_ORES);
		$rareCount = $this->countOres($playerOres, self::$RARE_ORES);
		$preciousCount = $this->countOres($playerOres, self::$PRECIOUS_ORES);

		if($normalCount > 0){
			$ratio = (double) $normalCount / $total;
			if($ratio > $normalThreshold){
				$suspicious = true;
				$oreType = "普通矿石";
			}
		}

		if(!$suspicious && $rareCount > 0){
			$ratio = (double) $rareCount / $total;
			if($ratio > $rareThreshold){
				$suspicious = true;
				$oreType = "稀有矿石";
			}
		}

		if(!$suspicious && $preciousCount > 0){
			$ratio = (double) $preciousCount / $total;
			if($ratio > $preciousThreshold){
				$suspicious = true;
				$oreType = "珍贵矿石";
			}
		}

		if($suspicious){
			if($this->getConfig()["check-exposed"] ?? true){
				if($this->isOreExposed($block)){
					return;
				}
			}

			$violation = isset($this->violations[$name]) ? $this->violations[$name] + 1 : 1;
			$this->violations[$name] = $violation;

			$detail = sprintf("%s发现率异常: %.2f%% (总挖掘: %d)", $oreType, $ratio * 100, $total);
			$this->antiCheat->logCheat($player->getName(), "XRayCheck", $detail);

			$maxViolations = (int) ($this->getConfig()["max-violations"] ?? 5);
			if($violation >= $maxViolations){
				$this->antiCheat->punish($player, "XRayCheck", $violation);
			}
		}
	}

	private function countOres($playerOres, $oreTypes){
		$count = 0;
		foreach($oreTypes as $oreId){
			$count += isset($playerOres[$oreId]) ? $playerOres[$oreId] : 0;
		}
		return $count;
	}

	private function isOreExposed(Block $block){
		$level = $block->getLevel();
		$x = $block->getFloorX();
		$y = $block->getFloorY();
		$z = $block->getFloorZ();

		$directions = [
			[1, 0, 0], [-1, 0, 0],
			[0, 1, 0], [0, -1, 0],
			[0, 0, 1], [0, 0, -1]
		];

		foreach($directions as $dir){
			$neighbor = $level->getBlock(new Vector3($x + $dir[0], $y + $dir[1], $z + $dir[2]));
			if($neighbor->getId() === Block::AIR || $neighbor->getId() === Block::WATER || $neighbor->getId() === Block::STILL_WATER){
				return true;
			}
		}

		return false;
	}

	public function getStats(Player $player){
		$name = $player->getName();
		$stats = [];

		$ores = isset($this->oreDiscoveryCount[$name]) ? $this->oreDiscoveryCount[$name] : [];

		$normalCount = $this->countOres($ores, self::$NORMAL_ORES);
		$rareCount = $this->countOres($ores, self::$RARE_ORES);
		$preciousCount = $this->countOres($ores, self::$PRECIOUS_ORES);
		$total = isset($this->totalBlocksMined[$name]) ? $this->totalBlocksMined[$name] : 0;

		$stats["normal_ores"] = $normalCount;
		$stats["rare_ores"] = $rareCount;
		$stats["precious_ores"] = $preciousCount;
		$stats["total_blocks"] = $total;
		$stats["violations"] = isset($this->violations[$name]) ? $this->violations[$name] : 0;

		if($total > 0){
			$stats["normal_ratio"] = (double) $normalCount / $total;
			$stats["rare_ratio"] = (double) $rareCount / $total;
			$stats["precious_ratio"] = (double) $preciousCount / $total;
		}

		return $stats;
	}
}
