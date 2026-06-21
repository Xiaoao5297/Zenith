<?php

namespace pocketmine\anticheat\check;

use pocketmine\anticheat\AntiCheat;
use pocketmine\Player;
use pocketmine\block\Block;
use pocketmine\math\Vector3;
use pocketmine\entity\Effect;

class SpeedCheck extends Check{

	const BUFFER_SIZE = 3;

	/** @var array */
	private $playerData = [];

	public function __construct(AntiCheat $antiCheat, array $config){
		parent::__construct($antiCheat, $config);
	}

	public function clearPlayerData(string $playerName){
		$lower = strtolower($playerName);
		unset($this->playerData[$lower]);
	}

	public function check(Player $player, Vector3 $from, Vector3 $to, float $elapsed){
		if(!$this->enabled) return;

		if($player->hasPermission("fpacheat.bypass")) return;
		if($player->isCreative() or $player->isSpectator()) return;

		$name = $player->getName();
		$lower = strtolower($name);

		// 跨世界/跨维度移动跳过
		if($player->getLevel() === null) return;

		$dx = $to->x - $from->x;
		$dz = $to->z - $from->z;
		$dist = sqrt($dx * $dx + $dz * $dz);

		// 极小移动忽略
		if($dist < 0.001) return;

		// 时间异常过滤
		if($elapsed < 0.04 || $elapsed > 0.2) return;

		// 单次大移动视为传送
		if($dist > 2.0){
			$this->playerData[$lower] = [];
			return;
		}

		$speed = $dist / $elapsed;

		// 平滑缓冲区
		if(!isset($this->playerData[$lower])){
			$this->playerData[$lower] = [];
		}
		$this->playerData[$lower][] = $speed;
		if(count($this->playerData[$lower]) > self::BUFFER_SIZE){
			array_shift($this->playerData[$lower]);
		}

		$avgSpeed = array_sum($this->playerData[$lower]) / count($this->playerData[$lower]);

		$limit = $this->calcSpeedLimit($player);

		if($avgSpeed > $limit){
			if(!isset($this->playerData[$lower . "_violations"])){
				$this->playerData[$lower . "_violations"] = 0;
			}
			$this->playerData[$lower . "_violations"]++;

			$detail = number_format($avgSpeed, 1) . " (限制: " . number_format($limit, 1) . ")";
			$this->antiCheat->logCheat($name, "Speed", $detail);

			if($this->playerData[$lower . "_violations"] >= $this->maxViolations){
				$this->antiCheat->punish($player, "Speed", $this->playerData[$lower . "_violations"]);
				if($this->getConfig()["rollback"] ?? true){
					$player->teleport($from);
				}
				$this->playerData[$lower . "_violations"] = 0;
			}
		}
	}

	private function calcSpeedLimit(Player $player) : float{
		$base = 7.0; // walk
		if($player->isSprinting()) $base = (float) ($this->getConfig()["max-sprint-speed"] ?? 9.0);
		if($player->getAllowFlight()) $base = (float) ($this->getConfig()["max-fly-speed"] ?? 15.0);

		// 速度效果加成
		if($player->hasEffect(Effect::SPEED)){
			$eff = $player->getEffect(Effect::SPEED);
			$base *= 1.15 + ($eff->getAmplifier() * 0.15);
		}

		// 下落/跳跃时放宽
		if($player->motionY > 0.1 || $player->motionY < -0.1){
			$base *= 1.2;
		}

		// 梯子/藤蔓上放宽
		if($player->getLevel() !== null){
			$blockId = $player->getLevel()->getBlockIdAt((int) $player->x, (int) $player->y, (int) $player->z);
			if($blockId === Block::LADDER or $blockId === Block::VINE){
				$base *= 1.5;
			}
		}

		// 水中放宽
		if($player->isInsideOfWater()){
			$base *= 1.3;
		}

		return $base;
	}
}
