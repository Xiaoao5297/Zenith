<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝
 * ╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 *  ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: U core
 *
 * @Links:
 *  > LY Core
 *  > LY Core Project
*/

namespace lycore\command\defaults;

use lycore\command\CommandSender;
use lycore\command\defaults\FindBiomeCommand;
use lycore\event\TranslationContainer;
use lycore\utils\TextFormat;
use lycore\Player;

class BiomeCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"%pocketmine.command.biome.description",
			"/biome <pos1|pos2|get|set|color|tp>"
		);
		$this->setPermission("pocketmine.command.biome");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return true;
		}

		if(count($args) === 0){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
			return false;
		}

		$subCommand = strtolower((string) $args[0]);
		if($subCommand === "tp"){
			$teleportCommand = new FindBiomeCommand("biome");
			return $teleportCommand->executeBiomeTeleport($sender, array_slice($args, 1));
		}

		if($sender instanceof Player){
			if($subCommand == "set"){
				$biome = isset($args[1]) ? $args[1] : 1;//默认改成草原
				if(isset($sender->selectedPos[0]) and isset($sender->selectedPos[1])){
					if(is_numeric($biome) === false){
						$sender->sendMessage(TextFormat::RED . "生物群系 ID 必须是数字。");
						return false;
					}
					$biome = (int) $biome;
					if($sender->selectedLev[0] !== $sender->selectedLev[1]){
						$sender->sendMessage(TextFormat::RED . "两个选区位置必须在同一个世界。");
						return false;
					}
					$x1 = min($sender->selectedPos[0][0], $sender->selectedPos[1][0]);
					$z1 = min($sender->selectedPos[0][1], $sender->selectedPos[1][1]);
					$x2 = max($sender->selectedPos[0][0], $sender->selectedPos[1][0]);
					$z2 = max($sender->selectedPos[0][1], $sender->selectedPos[1][1]);
					$level = $sender->selectedLev[0];
					for($x = $x1; $x <= $x2; $x++){
						for($z = $z1; $z <= $z2; $z++){
							$level->setBiomeId($x, $z, $biome);
						}
					}
					$sender->sendMessage("已将选区生物群系 ID 设置为 {$biome}。");
				}else{
					$sender->sendMessage("请先使用 /biome pos1 和 /biome pos2 选择区域。");
				}
			}elseif($subCommand == "color"){
				$color = isset($args[1]) ? $args[1] : "146,188,89";//1=草原("146,188,89"),2=沙漠(251,183,19)"130,180,147"
				$a = explode(",", $color);
				//var_dump($a);
				if(count($a) != 3){
					$sender->sendMessage(TextFormat::RED . "颜色格式错误，请使用 R,G,B，例如 146,188,89。");
					return false;
				}
				if(isset($sender->selectedPos[0]) and isset($sender->selectedPos[1])){
					if($sender->selectedLev[0] !== $sender->selectedLev[1]){
						$sender->sendMessage(TextFormat::RED . "两个选区位置必须在同一个世界。");
						return false;
					}
					$x1 = min($sender->selectedPos[0][0], $sender->selectedPos[1][0]);
					$z1 = min($sender->selectedPos[0][1], $sender->selectedPos[1][1]);
					$x2 = max($sender->selectedPos[0][0], $sender->selectedPos[1][0]);
					$z2 = max($sender->selectedPos[0][1], $sender->selectedPos[1][1]);
					for($x = $x1; $x <= $x2; $x++){
						for($z = $z1; $z <= $z2; $z++){
							$level = $sender->getLevel();
							$level->setBiomeColor($x, $z, $a[0], $a[1], $a[2]);
						}
					}
					//$sender->selectedPos = array();
					$sender->sendMessage("已将选区生物群系颜色设置为 {$a[0]}, {$a[1]}, {$a[2]}。");
				}else{
					$sender->sendMessage("请先使用 /biome pos1 和 /biome pos2 选择区域。");
				}
			}elseif($subCommand == "pos1"){
				$x = floor($sender->getX());
				$z = floor($sender->getZ());
				$sender->selectedLev[0] = $sender->getlevel();
				$sender->selectedPos[0][0] = $x;
				$sender->selectedPos[0][1] = $z;
				$sender->sendMessage("已设置位置 1：世界 {$sender->selectedLev[0]->getName()}，坐标 {$x}, {$z}。");
			}elseif($subCommand == "pos2"){
				$x = floor($sender->getX());
				$z = floor($sender->getZ());
				$sender->selectedLev[1] = $sender->getlevel();
				$sender->selectedPos[1][0] = $x;
				$sender->selectedPos[1][1] = $z;
				$sender->sendMessage("已设置位置 2：世界 {$sender->selectedLev[1]->getname()}，坐标 {$x}, {$z}。");
			}elseif($subCommand == "get"){
				$x = floor($sender->getX());
				$z = floor($sender->getZ());
				$biome = $sender->getLevel()->getBiomeId($x, $z);
				$color = $sender->getLevel()->getBiomeColor($x, $z);
				$sender->sendMessage("当前位置生物群系 ID：{$biome}，颜色：{$color[0]}, {$color[1]}, {$color[2]}。");
			}else{
				$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
				return true;
			}
		}else{
			$sender->sendMessage("只能在游戏内使用此命令。");
			return false;
		}
	}
}
