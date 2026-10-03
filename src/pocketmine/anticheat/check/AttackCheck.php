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
use pocketmine\entity\Entity;
use pocketmine\entity\Effect;
use pocketmine\item\Item;
use pocketmine\item\enchantment\Enchantment;

/**
 * 攻击检测：频率、伤害异常、KillAura 旋转、MultiAura。
 *
 * 频率/旋转/多目标窗口全部改为服务器 tick；伤害期望值纳入锋利附魔与力量/虚弱效果，
 * 避免附魔剑、药水导致的误判。
 */
class AttackCheck extends Check{

	const WINDOW_TICKS = 20;

	public function getDisplayName() : string{
		return "Attack";
	}

	public function clearPlayerData(string $playerName){
	}

	public function checkAttack($damager, $target, $damage){
		if(!$this->enabled) return;
		if(!($damager instanceof Player)) return;
		if($damager->hasPermission("fpacheat.bypass")) return;
		if($damager->isCreative() or $damager->isSpectator()) return;

		$this->checkAttackFrequency($damager);
		$this->checkDamageAnomaly($damager, $damage);
		$this->checkRotation($damager);
		$this->checkMultiTarget($damager, $target);
	}

	private function checkAttackFrequency(Player $player){
		$data = $this->getPlayerData($player);
		$tick = $this->antiCheat->getServer()->getTick();

		$hits = $data->getState("attack.hits", []);
		$hits[] = $tick;
		$cutoff = $tick - self::WINDOW_TICKS;
		$hits = array_values(array_filter($hits, function($t) use ($cutoff){
			return $t > $cutoff;
		}));
		$data->setState("attack.hits", $hits);

		$maxAPS = (int) ($this->config["max-attacks-per-second"] ?? 10);
		$allowed = (int) ceil($maxAPS * 1.5);

		if(count($hits) > $allowed){
			$detail = sprintf("%d tick 内 %d 次攻击 (限制 %d)", self::WINDOW_TICKS, count($hits), $allowed);
			$this->flag($player, $detail, 1.0);
			$data->setState("attack.hits", []);
		}
	}

	private function checkDamageAnomaly(Player $player, float $damage){
		$item = $player->getInventory()->getItemInHand();
		$expectedMax = $this->getExpectedMaxDamage($item);

		if($player->hasEffect(Effect::STRENGTH)){
			$amplifier = $player->getEffect(Effect::STRENGTH)->getAmplifier();
			$expectedMax *= 1.3 * ($amplifier + 1);
		}
		if($player->hasEffect(Effect::WEAKNESS)){
			$expectedMax *= 0.8;
		}

		$maxMultiplier = (float) ($this->config["max-damage-multiplier"] ?? 2.0);

		if($damage > $expectedMax * $maxMultiplier and $damage > 10){
			$detail = sprintf("伤害 %.1f 超过预期 %.1f", $damage, $expectedMax * $maxMultiplier);
			$this->flag($player, $detail, 1.0);
		}else{
			$this->decay($player, 0.5);
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
			Item::GOLD_AXE => 4.0,
			Item::WOODEN_AXE => 3.0,
			Item::DIAMOND_PICKAXE => 5.0,
			Item::IRON_PICKAXE => 4.0,
			Item::STONE_PICKAXE => 3.0,
		];

		$base = $damages[$item->getId()] ?? 2.0;

		$sharpness = $item->getEnchantmentLevel(Enchantment::TYPE_WEAPON_SHARPNESS);
		if($sharpness > 0){
			$base += 1.25 * $sharpness;
		}

		return $base;
	}

	private function checkRotation(Player $player){
		$data = $this->getPlayerData($player);
		$tick = $this->antiCheat->getServer()->getTick();

		$last = $data->getState("attack.rot");
		$lastTick = $data->getState("attack.rotTick");

		$maxRotation = (float) ($this->config["max-rotation-per-tick"] ?? 90.0);

		if($last !== null and $lastTick !== null and ($tick - $lastTick) <= 10){
			$yawDiff = abs($player->yaw - $last[0]);
			$pitchDiff = abs($player->pitch - $last[1]);
			if($yawDiff > 180){
				$yawDiff = 360 - $yawDiff;
			}

			if($yawDiff > $maxRotation or $pitchDiff > $maxRotation){
				$detail = sprintf("旋转异常 yaw %.1f pitch %.1f (限制 %.1f)", $yawDiff, $pitchDiff, $maxRotation);
				$this->flag($player, $detail, 1.0);
			}
		}

		$data->setState("attack.rot", [$player->yaw, $player->pitch]);
		$data->setState("attack.rotTick", $tick);
	}

	private function checkMultiTarget(Player $player, Entity $target){
		$data = $this->getPlayerData($player);
		$tick = $this->antiCheat->getServer()->getTick();

		$recent = $data->getState("attack.targets", []);
		$recent[] = [$tick, $target->getId()];
		$cutoff = $tick - self::WINDOW_TICKS;
		$recent = array_values(array_filter($recent, function($entry) use ($cutoff){
			return $entry[0] > $cutoff;
		}));
		$data->setState("attack.targets", $recent);

		$ids = [];
		foreach($recent as $entry){
			$ids[$entry[1]] = true;
		}

		if(count($ids) >= 5){
			$detail = sprintf("%d tick 内攻击 %d 个不同目标", self::WINDOW_TICKS, count($ids));
			$this->flag($player, $detail, 1.0);
			$data->setState("attack.targets", []);
		}
	}
}
