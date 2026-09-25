<?php

namespace pocketmine\command\defaults;

use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\utils\BackupManager;
use pocketmine\utils\TextFormat;

class BackupCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"查看/管理服务器自动备份",
			"/backup <status|list|run|clean|reload> [时段]"
		);
		$this->setPermission("pocketmine.command.backup");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return true;
		}
		$manager = $sender->getServer()->getBackupManager();
		if(!($manager instanceof BackupManager)){
			$sender->sendMessage(TextFormat::RED . "备份管理器未启用");
			return true;
		}
		$sub = strtolower(isset($args[0]) ? $args[0] : "status");
		$tier = isset($args[1]) ? strtolower($args[1]) : null;

		switch($sub){
			case "status":
			case "info":
				$this->sendStatus($sender, $manager);
				break;
			case "list":
				$this->sendList($sender, $manager, $tier);
				break;
			case "run":
			case "now":
				$runTier = $tier === null ? "manual" : $tier;
				if($manager->runBackup($runTier, true)){
					$sender->sendMessage(TextFormat::GREEN . "[Backup] 已开始备份 [" . $runTier . "]，完成后会在控制台输出结果");
				}else{
					$sender->sendMessage(TextFormat::RED . "[Backup] 无法开始备份：" . $manager->getLastMessage());
				}
				break;
			case "clean":
			case "prune":
				if($tier !== null){
					if(isset($manager->getTiers()[$tier])){
						$n = $manager->prune($tier);
						$sender->sendMessage(TextFormat::GREEN . "[Backup] 已按保留策略清理 [" . $tier . "] 旧快照 " . $n . " 份");
					}else{
						$n = $manager->removeAllSnapshots($tier);
						$sender->sendMessage(TextFormat::GREEN . "[Backup] 已清空 [" . $tier . "] 快照共 " . $n . " 份");
					}
				}else{
					$n = $manager->cleanAll();
					$sender->sendMessage(TextFormat::GREEN . "[Backup] 已按保留策略清理旧快照共 " . $n . " 份");
				}
				break;
			case "reload":
				$manager->reload();
				$sender->sendMessage(TextFormat::GREEN . "[Backup] 已重新载入 " . $manager->getConfigPath());
				break;
			case "config":
			case "path":
				$sender->sendMessage(TextFormat::GOLD . "[Backup] 配置文件: " . TextFormat::WHITE . $manager->getConfigPath());
				break;
			default:
				$sender->sendMessage(TextFormat::YELLOW . "用法: /backup <status|list|run|clean|reload> [时段]");
				$sender->sendMessage(TextFormat::GRAY . "  status - 查看状态；list [时段] - 列出快照；run [时段] - 立即备份");
				$sender->sendMessage(TextFormat::GRAY . "  clean [时段] - 清理旧快照；reload - 重新载入 backup.yml");
				break;
		}
		return true;
	}

	private function sendStatus(CommandSender $sender, BackupManager $manager){
		$sender->sendMessage(TextFormat::GOLD . "===== 自动备份状态 =====");
		$sender->sendMessage(TextFormat::GRAY . "启用: " . ($manager->isEnabled() ? TextFormat::GREEN . "是" : TextFormat::RED . "否")
			. TextFormat::GRAY . "  运行中: " . ($manager->isRunning() ? TextFormat::YELLOW . (string) $manager->getRunningTier() : "否"));
		$sender->sendMessage(TextFormat::GRAY . "目录: " . $manager->getBackupRoot());
		$sender->sendMessage(TextFormat::GRAY . "配置: " . $manager->getConfigPath() . "  (检查间隔 " . $manager->getTick() . " 秒)");
		if($manager->getLastMessage() !== ""){
			$sender->sendMessage(TextFormat::GRAY . "最近: " . $manager->getLastMessage());
		}
		$stateDir = $manager->getBackupRoot() . ".state" . DIRECTORY_SEPARATOR;
		$now = time();
		$sender->sendMessage(TextFormat::WHITE . sprintf("%-8s %-6s %-10s %-18s %s", "时段", "份数", "占用", "最新快照", "下次"));
		foreach($manager->getTiers() as $name => $t){
			$snaps = $manager->listSnapshots($name);
			$count = count($snaps);
			$latest = $count > 0 ? $snaps[$count - 1] : "-";
			$size = $manager->humanSize($manager->tierSize($name));
			$next = "-";
			$state = $stateDir . $name . ".last";
			if(is_file($state) and $t["interval"] > 0){
				$left = (((int) @file_get_contents($state)) + $t["interval"]) - $now;
				if($left < 0){
					$left = 0;
				}
				$next = $this->formatDuration($left) . "后";
			}elseif($t["interval"] > 0){
				$next = "待启动";
			}
			$sender->sendMessage(TextFormat::WHITE . sprintf("%-8s %-6d %-10s %-18s %s", $name, $count, $size, $latest, $next));
		}
	}

	private function sendList(CommandSender $sender, BackupManager $manager, $tier){
		$tiers = $tier === null ? array_keys($manager->getTiers()) : [$tier];
		$sender->sendMessage(TextFormat::GOLD . "===== 备份快照列表 =====");
		foreach($tiers as $t){
			$snaps = $manager->listSnapshots($t);
			$sender->sendMessage(TextFormat::AQUA . "[" . $t . "] " . TextFormat::WHITE . count($snaps) . " 份");
			foreach($snaps as $s){
				$sender->sendMessage(TextFormat::GRAY . "  - " . $s);
			}
		}
	}

	private function formatDuration($s){
		$s = (int) $s;
		if($s >= 86400){
			return floor($s / 86400) . "天" . floor(($s % 86400) / 3600) . "时";
		}elseif($s >= 3600){
			return floor($s / 3600) . "时" . floor(($s % 3600) / 60) . "分";
		}elseif($s >= 60){
			return floor($s / 60) . "分" . ($s % 60) . "秒";
		}
		return $s . "秒";
	}
}
