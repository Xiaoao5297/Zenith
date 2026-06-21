<?php

namespace pocketmine\anticheat\check;

use pocketmine\anticheat\AntiCheat;
use pocketmine\Player;
use pocketmine\entity\Entity;
use pocketmine\level\Level;
use pocketmine\math\Vector3;
use pocketmine\block\Block;

class HitboxCheck extends Check{

	/** @var array */
	private $violations = [];

	/** @var array */
	private $lastAttackTime = [];

	/** @var array */
	private $lastDistance = [];

	public function __construct(AntiCheat $antiCheat, array $config){
		parent::__construct($antiCheat, $config);
	}

	public function clearPlayerData(string $playerName){
		unset($this->violations[$playerName]);
		unset($this->lastAttackTime[$playerName]);
		unset($this->lastDistance[$playerName]);
	}

	public function checkHitbox(Player $player, Entity $target){
		if(!$this->enabled) return;

		if($player->hasPermission("fpacheat.bypass")) return;
		if($player->isCreative() || $player->isSpectator()) return;

		$name = $player->getName();

		$eyeX = $player->x;
		$eyeY = $player->y + 1.62;
		$eyeZ = $player->z;

		$minX = $target->x - 0.3;
		$maxX = $target->x + 0.3;
		$minY = $target->y;
		$targetHeight = method_exists($target, 'getHeight') ? $target->getHeight() : 0.6;
		$maxY = $target->y + $targetHeight;
		$minZ = $target->z - 0.3;
		$maxZ = $target->z + 0.3;

		$closestX = $this->clamp($eyeX, $minX, $maxX);
		$closestY = $this->clamp($eyeY, $minY, $maxY);
		$closestZ = $this->clamp($eyeZ, $minZ, $maxZ);

		$dx = $closestX - $eyeX;
		$dy = $closestY - $eyeY;
		$dz = $closestZ - $eyeZ;

		$distance = sqrt($dx * $dx + $dy * $dy + $dz * $dz);

		$maxReach = 3.5;
		$threshold = 0.1;

		if($distance > $maxReach + $threshold){
			$violation = isset($this->violations[$name]) ? $this->violations[$name] + 1 : 1;
			$this->violations[$name] = $violation;

			$detail = sprintf("攻击距离: %.2f, 限制: %.2f", $distance, $maxReach);
			$this->antiCheat->logCheat($name, "HitboxCheck-Reach", $detail);

			if($violation >= $this->maxViolations){
				$this->antiCheat->punish($player, "HitboxCheck", $violation);
			}
			return;
		}

		$angleDiff = $this->getAngleDifference($player, $target);

		if($angleDiff > 120 && $distance > 2.0){
			$violation = isset($this->violations[$name]) ? $this->violations[$name] + 1 : 1;
			$this->violations[$name] = $violation;

			$detail = sprintf("角度异常: %.1f度, 距离: %.2f", $angleDiff, $distance);
			$this->antiCheat->logCheat($name, "HitboxCheck-Angle", $detail);

			if($violation >= $this->maxViolations){
				$this->antiCheat->punish($player, "HitboxCheck", $violation);
			}
			return;
		}

		if($distance > 3.0 && !$this->hasLineOfSight($player, $target)){
			$violation = isset($this->violations[$name]) ? $this->violations[$name] + 1 : 1;
			$this->violations[$name] = $violation;

			$detail = "穿墙攻击";
			$this->antiCheat->logCheat($name, "HitboxCheck-Wall", $detail);

			if($violation >= $this->maxViolations){
				$this->antiCheat->punish($player, "HitboxCheck", $violation);
			}
			return;
		}

		$current = isset($this->violations[$name]) ? $this->violations[$name] : 0;
		if($current > 0){
			$this->violations[$name] = $current - 1;
		}
	}

	private function clamp($value, $min, $max){
		return max($min, min($max, $value));
	}

	private function getAngleDifference(Player $player, Entity $target){
		$dx = $target->x - $player->x;
		$dz = $target->z - $player->z;

		$yawToTarget = rad2deg(atan2(-$dx, $dz));
		$playerYaw = $player->getYaw();

		$diff = abs($yawToTarget - $playerYaw);
		if($diff > 180){
			$diff = 360 - $diff;
		}

		return $diff;
	}

	private function hasLineOfSight(Player $player, Entity $target){
		$level = $player->getLevel();

		$eyeX = $player->x;
		$eyeY = $player->y + 1.62;
		$eyeZ = $player->z;

		$targetX = $target->x;
		$targetY = $target->y + (method_exists($target, 'getHeight') ? $target->getHeight() / 2 : 0.3);
		$targetZ = $target->z;

		$distance = sqrt(
			pow($targetX - $eyeX, 2) +
			pow($targetY - $eyeY, 2) +
			pow($targetZ - $eyeZ, 2)
		);

		if($distance < 1.5){
			return true;
		}

		$steps = (int)ceil($distance);
		$dx = ($targetX - $eyeX) / $steps;
		$dy = ($targetY - $eyeY) / $steps;
		$dz = ($targetZ - $eyeZ) / $steps;

		$solidCount = 0;

		for($i = 1; $i < $steps; $i++){
			$x = $eyeX + $dx * $i;
			$y = $eyeY + $dy * $i;
			$z = $eyeZ + $dz * $i;

			$block = $level->getBlock(new Vector3($x, $y, $z));

			if($block !== null && $block->isSolid()){
				$solidCount++;
				if($solidCount >= 2){
					return false;
				}
			}
		}

		return true;
	}
}
