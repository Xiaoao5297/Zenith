<?php

namespace pocketmine\command\defaults;

use pocketmine\command\CommandSender;
use pocketmine\utils\TextFormat;

class AntiCheatCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"查看/调试反作弊系统",
			"/ac <status|mode|debug|player|checks> [参数]"
		);
		$this->setPermission("pocketmine.command.anticheat");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return true;
		}

		$antiCheat = $sender->getServer()->getAntiCheat();
		if($antiCheat === null){
			$sender->sendMessage(TextFormat::RED . "[AntiCheat] 未初始化");
			return true;
		}

		$sub = strtolower(isset($args[0]) ? $args[0] : "status");

		switch($sub){
			case "status":
				$this->sendStatus($sender, $antiCheat);
				break;

			case "mode":
				if(!isset($args[1])){
					$sender->sendMessage(TextFormat::YELLOW . "[AntiCheat] 当前模式: " . $antiCheat->getViolationManager()->getMode());
					break;
				}
				$mode = strtolower($args[1]);
				if($mode !== "alert" and $mode !== "punish"){
					$sender->sendMessage(TextFormat::RED . "[AntiCheat] 模式只能是 alert 或 punish");
					break;
				}
				$antiCheat->getViolationManager()->setMode($mode);
				$sender->sendMessage(TextFormat::GREEN . "[AntiCheat] 已切换为 " . $mode . " 模式（仅本次运行，重启后以配置为准）");
				break;

			case "debug":
				if(!isset($args[1])){
					$sender->sendMessage(TextFormat::YELLOW . "[AntiCheat] debug 当前为 " . ($antiCheat->isDebug() ? "开" : "关"));
					break;
				}
				$on = in_array(strtolower($args[1]), ["on", "1", "true", "yes"], true);
				$antiCheat->setDebug($on);
				$sender->sendMessage(TextFormat::GREEN . "[AntiCheat] debug 已" . ($on ? "开启" : "关闭"));
				break;

			case "player":
			case "p":
				if(!isset($args[1])){
					$sender->sendMessage(TextFormat::RED . "[AntiCheat] 用法: /ac player <玩家名>");
					break;
				}
				$this->sendPlayer($sender, $antiCheat, $args[1]);
				break;

			case "checks":
				$this->sendChecks($sender, $antiCheat);
				break;

			default:
				$sender->sendMessage(TextFormat::YELLOW . "用法: /ac <status|mode|debug|player|checks>");
		}

		return true;
	}

	private function sendStatus(CommandSender $sender, $antiCheat){
		$manager = $antiCheat->getViolationManager();
		$sender->sendMessage(TextFormat::GOLD . "===== AntiCheat 状态 =====");
		$sender->sendMessage(TextFormat::GRAY . "启用: " . ($antiCheat->isEnabled() ? TextFormat::GREEN . "是" : TextFormat::RED . "否"));
		$sender->sendMessage(TextFormat::GRAY . "模式: " . TextFormat::YELLOW . $manager->getMode());
		$sender->sendMessage(TextFormat::GRAY . "调试: " . ($antiCheat->isDebug() ? TextFormat::GREEN . "开" : TextFormat::RED . "关"));
		$sender->sendMessage(TextFormat::GRAY . "每日违规上限: " . TextFormat::YELLOW . $manager->getMaxDailyViolations());
		$sender->sendMessage(TextFormat::GRAY . "检测器: " . count($antiCheat->getChecks()) . " 个（/ac checks 查看）");
	}

	private function sendChecks(CommandSender $sender, $antiCheat){
		$sender->sendMessage(TextFormat::GOLD . "===== 检测器 =====");
		foreach($antiCheat->getChecks() as $check){
			$color = $check->isEnabled() ? TextFormat::GREEN : TextFormat::RED;
			$sender->sendMessage($color . $check->getId() . TextFormat::GRAY . " (max-violations " . $check->getMaxViolations() . ")");
		}
	}

	private function sendPlayer(CommandSender $sender, $antiCheat, string $name){
		$data = $antiCheat->getPlayerDataByName($name);
		if($data === null){
			$sender->sendMessage(TextFormat::RED . "[AntiCheat] 未找到该玩家的运行时数据（需在线且已产生样本）");
			return;
		}

		$sender->sendMessage(TextFormat::GOLD . "===== " . $data->getName() . " =====");
		$sender->sendMessage(TextFormat::GRAY . "加入 tick: " . TextFormat::YELLOW . $data->getJoinTick());
		$sender->sendMessage(TextFormat::GRAY . "最近传送 tick: " . TextFormat::YELLOW . $data->getLastTeleportTick());
		$sender->sendMessage(TextFormat::GRAY . "最近受伤 tick: " . TextFormat::YELLOW . $data->getLastDamageTick());

		foreach($antiCheat->getChecks() as $check){
			$id = $check->getId();
			$buffer = $data->getBuffer($id);
			$violations = $data->getViolations($id);
			if($buffer <= 0 and $violations <= 0){
				continue;
			}
			$sender->sendMessage(TextFormat::GRAY . $id . ": " . TextFormat::YELLOW . "buffer " . round($buffer, 2) . TextFormat::GRAY . " / 违规 " . $violations);
		}
	}
}
