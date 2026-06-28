<?php

namespace pocketmine\anticheat\check;

use pocketmine\anticheat\AntiCheat;
use pocketmine\entity\Entity;
use pocketmine\entity\Effect;
use pocketmine\item\Item;
use pocketmine\item\TieredTool;
use pocketmine\item\Weapon;
use pocketmine\Player;

class AttackCheck extends Check{

	/** @var array */
	private $playerData = [];

	public function __construct(AntiCheat $antiCheat, array $config){
		parent::__construct($antiCheat, $config);
	}

	public function clearPlayerData(string $playerName){
		$lower = strtolower($playerName);
		unset($this->playerData[$lower]);
		unset($this->playerData[$lower . "_hit_times"]);
		unset($this->playerData[$lower . "_targets"]);
		unset($this->playerData[$lower . "_yaw_pitch"]);
	}

	/**
	 * @param Entity $damager
	 * @param Entity $target
	 * @param float  $damage
	 */
	public function checkAttack($damager, $target, $damage){
		if(!$this->enabled) return;
		if(!($damager instanceof Player)) return;
		if($damager->hasPermission("fpacheat.bypass")) return;
		if($damager->isCreative() or $damager->isSpectator()) return;

		$name = $damager->getName();
		$lower = strtolower($name);
		$now = microtime(true);

		$this->checkAttackFrequency($damager, $lower, $now);
		$this->checkDamageAnomaly($damager, $target, $damage, $lower);
		$this->checkKillAura($damager, $target, $lower, $now);
		$this->checkMultiAura($damager, $target, $lower, $now);
	}

	private function checkAttackFrequency(Player $player, string $lower, float $now){
		if(!isset($this->playerData[$lower . "_hit_times"])){
			$this->playerData[$lower . "_hit_times"] = [];
		}

		$this->playerData[$lower . "_hit_times"][] = $now;
		// 只保留最近 2 秒
		$this->playerData[$lower . "_hit_times"] = array_filter($this->playerData[$lower . "_hit_times"], function($t) use ($now){
			return ($now - $t) <= 2.0;
		});

		$maxAPS = (int) ($this->getConfig()["max-attacks-per-second"] ?? 10);
		$recentHits = count($this->playerData[$lower . "_hit_times"]);

		if($recentHits > $maxAPS * 2){
			$this->antiCheat->logCheat($player->getName(), "Attack", "攻击频率异常: " . $recentHits . "次/2秒");
			$this->violate($player, $lower);
		}
	}

	private function checkDamageAnomaly(Player $player, Entity $target, float $damage, string $lower){
		$item = $player->getInventory()->getItemInHand();
		$expectedMax = $this->getExpectedMaxDamage($item);

		// 力量效果加成
		if($player->hasEffect(Effect::STRENGTH)){
			$eff = $player->getEffect(Effect::STRENGTH);
			$expectedMax *= 1.3 * ($eff->getAmplifier() + 1);
		}

		$maxMultiplier = (float) ($this->getConfig()["max-damage-multiplier"] ?? 1.5);

		if($damage > $expectedMax * $maxMultiplier && $damage > 8){
			$this->antiCheat->logCheat($player->getName(), "Attack", "伤害异常: " . number_format($damage, 1) . " (预期上限: " . number_format($expectedMax * $maxMultiplier, 1) . ")");
			$this->violate($player, $lower);
		}
	}

	private function getExpectedMaxDamage(Item $item) : float{
		$damages = [
			Item::DIAMOND_SWORD => 7.0,
			Item::IRON_SWORD => 6.0,
			Item::STONE_SWORD => 5.0,
			Item::WOODEN_SWORD => 4.0,
			Item::GOLD_SWORD => 4.0,
			Item::DIAMOND_AXE => 6.0,
			Item::IRON_AXE => 5.0,
			Item::STONE_AXE => 4.0,
		];
		return $damages[$item->getId()] ?? 2.0;
	}

	private function checkKillAura(Player $player, Entity $target, string $lower, float $now){
		if(!isset($this->playerData[$lower . "_targets"])){
			$this->playerData[$lower . "_targets"] = [];
			$this->playerData[$lower . "_yaw_pitch"] = [];
		}

		$pYaw = $player->yaw;
		$pPitch = $player->pitch;
		$dX = $target->x - $player->x;
		$dZ = $target->z - $player->z;
		$targetYaw = atan2($dZ, $dX) * 180 / M_PI - 90;
		if($targetYaw < 0) $targetYaw += 360;

		$maxRotation = (float) ($this->getConfig()["max-rotation-per-tick"] ?? 90.0);
		$lastYaw = $this->playerData[$lower . "_yaw_pitch"][0] ?? $pYaw;
		$lastPitch = $this->playerData[$lower . "_yaw_pitch"][1] ?? $pPitch;

		$yawDiff = abs($pYaw - $lastYaw);
		$pitchDiff = abs($pPitch - $lastPitch);

		if(($yawDiff > $maxRotation || $pitchDiff > $maxRotation) && ($now - ($this->playerData[$lower . "_lastCheckTime"] ?? 0)) < 0.5){
			$this->antiCheat->logCheat($player->getName(), "Attack", "KillAura 旋转异常: yaw=" . round($yawDiff, 1) . " pitch=" . round($pitchDiff, 1));
			$this->violate($player, $lower);
		}

		$this->playerData[$lower . "_yaw_pitch"] = [$pYaw, $pPitch];
		$this->playerData[$lower . "_lastCheckTime"] = $now;
	}

	private function checkMultiAura(Player $player, Entity $target, string $lower, float $now){
		if(!isset($this->playerData[$lower . "_targets"])){
			$this->playerData[$lower . "_targets"] = [];
			$this->playerData[$lower . "_target_switch_time"] = 0;
			$this->playerData[$lower . "_switch_count"] = 0;
		}

		$targetId = $target->getId();
		$recentTargets =& $this->playerData[$lower . "_targets"];

		if(!empty($recentTargets) && end($recentTargets) !== $targetId){
			if(!isset($this->playerData[$lower . "_target_switch_time"])){
				$this->playerData[$lower . "_target_switch_time"] = 0;
			}
			if(!isset($this->playerData[$lower . "_switch_count"])){
				$this->playerData[$lower . "_switch_count"] = 0;
			}
			if((microtime(true) - $this->playerData[$lower . "_target_switch_time"]) < 1.0){
				$this->playerData[$lower . "_switch_count"]++;
			}else{
				$this->playerData[$lower . "_switch_count"] = 1;
			}
			$this->playerData[$lower . "_target_switch_time"] = microtime(true);
		}

		$recentTargets[] = $targetId;
		if(count($recentTargets) > 10) array_shift($recentTargets);

		if($this->playerData[$lower . "_switch_count"] >= 5){
			$this->antiCheat->logCheat($player->getName(), "Attack", "MultiAura 目标切换频繁");
			$this->violate($player, $lower);
			$this->playerData[$lower . "_switch_count"] = 0;
		}
	}

	private function violate(Player $player, string $lower){
		if(!isset($this->playerData[$lower . "_violations"])){
			$this->playerData[$lower . "_violations"] = 0;
		}
		$this->playerData[$lower . "_violations"]++;
		if($this->playerData[$lower . "_violations"] >= $this->maxViolations){
			$this->antiCheat->punish($player, "Attack", $this->playerData[$lower . "_violations"]);
			$this->playerData[$lower . "_violations"] = 0;
		}
	}
}
