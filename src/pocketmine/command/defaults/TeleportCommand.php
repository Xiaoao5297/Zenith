<?php

/*
 *
 *  ____            _        _   __  __ _                  __  __ ____
 * |  _ \ ___   ___| | _____| |_|  \/  (_)_ __   ___      |  \/  |  _ \
 * | |_) / _ \ / __| |/ / _ \ __| |\/| | | '_ \ / _ \_____| |\/| | |_) |
 * |  __/ (_) | (__|   <  __/ |_| |  | | | | | |  __/_____| |  | |  __/
 * |_|   \___/ \___|_|\_\___|\__|_|  |_|_|_| |_|\___|     |_|  |_|_|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author PocketMine Team
 * @link http://www.pocketmine.net/
 *
 *
*/

namespace pocketmine\command\defaults;

use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\command\TargetSelector;
use pocketmine\event\TranslationContainer;
use pocketmine\math\Vector3;
use pocketmine\Player;
use pocketmine\utils\TextFormat;

class TeleportCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"%pocketmine.command.tp.description",
			"%commands.tp.usage"
		);
		$this->setPermission("pocketmine.command.teleport");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		$playerTpOpen = strtolower($sender->getServer()->getConfigString("player-tp", "false")) === "true";
		if(!$playerTpOpen && !$this->testPermission($sender)){
			return true;
		}
		if($playerTpOpen && !$this->testPermissionSilent($sender) && !(count($args) === 1 or count($args) === 3)){
			//开放模式下无权限的玩家只允许传送自己（/tp 玩家名 或 /tp x y z），不能操作其他玩家
			$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.generic.permission"));
			return true;
		}

		if(count($args) < 1 or count($args) > 6){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));

			return true;
		}

		//第一个参数允许是实体选择器（@a/@p/@r/@e/@s）；普通玩家名路径保持原有行为
		$selector = new TargetSelector($sender->getServer());
		$selectorUsed = $selector->isSelector($args[0]);
		$targets = [];

		if($selectorUsed){
			try{
				$targets = $selector->matchEntities($sender, $args[0]);
			}catch(\InvalidArgumentException $e){
				$sender->sendMessage(TextFormat::RED . "Invalid selector " . $args[0] . ": " . $e->getMessage());

				return true;
			}

			if(count($targets) === 0){
				$sender->sendMessage(TextFormat::RED . "No targets matched selector " . $args[0]);

				return true;
			}

			if($playerTpOpen && !$this->testPermissionSilent($sender)){
				foreach($targets as $target){
					if($target !== $sender){
						$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.generic.permission"));

						return true;
					}
				}
			}
		}elseif(count($args) === 3){
			// /tp x y z：把发送者自己传送到坐标（保持旧行为）
			if(!($sender instanceof Player)){
				$sender->sendMessage(TextFormat::RED . "Please provide a player!");

				return true;
			}
			$targets = [$sender];
		}else{
			$target = $sender->getServer()->getPlayer($args[0]);
			if($target === null){
				$sender->sendMessage(TextFormat::RED . "Can't find player " . $args[0]);

				return true;
			}
			$targets = [$target];
		}

		if(count($args) < 3){
			// /tp <目标> 或 /tp <目标> <目的地>：只能作用于单个目标
			if(count($targets) !== 1){
				$sender->sendMessage(TextFormat::RED . "Selector " . $args[0] . " matched " . count($targets) . " targets, but this form needs exactly one");

				return true;
			}

			$origin = $targets[0];
			$target = $origin;

			if(count($args) === 1){
				if(!($sender instanceof Player)){
					$sender->sendMessage(TextFormat::RED . "Please provide a player!");

					return true;
				}
				$origin = $sender;
			}else{
				$target = $sender->getServer()->getPlayer($args[1]);
				if($target === null){
					$sender->sendMessage(TextFormat::RED . "Can't find player " . $args[1]);

					return true;
				}
			}

			$origin->teleport($target);
			Command::broadcastCommandMessage($sender, new TranslationContainer("commands.tp.success", [$this->getTargetName($origin), $this->getTargetName($target)]));

			return true;
		}

		if($selectorUsed){
			// /tp @a x y z [yaw pitch]：坐标从第二个参数开始
			if(count($args) !== 4 and count($args) !== 6){
				$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));

				return true;
			}
			$pos = 1;
		}elseif(count($args) === 4 or count($args) === 6){
			$pos = 1;
		}else{
			$pos = 0;
		}

		$teleported = 0;
		foreach($targets as $target){
			if(!($target instanceof Vector3) or $this->getTargetLevel($target) === null){
				continue;
			}

			$offset = $pos;
			$x = $this->getRelativeDouble($target->x, $sender, $args[$offset++]);
			$y = $this->getRelativeDouble($target->y, $sender, $args[$offset++], 0, 128);
			$z = $this->getRelativeDouble($target->z, $sender, $args[$offset++]);
			$yaw = $target->getYaw();
			$pitch = $target->getPitch();

			if(count($args) === 6 or (count($args) === 5 and $offset === 3)){
				$yaw = $args[$offset++];
				$pitch = $args[$offset++];
			}

			$target->teleport(new Vector3($x, $y, $z), $yaw, $pitch);
			++$teleported;
			Command::broadcastCommandMessage($sender, new TranslationContainer("commands.tp.success.coordinates", [$this->getTargetName($target), round($x, 2), round($y, 2), round($z, 2)]));
		}

		if($teleported === 0){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
		}

		return true;
	}

	/**
	 * 选择器可能匹配到非玩家实体，Entity 没有 getName()。
	 *
	 * @param mixed $target
	 *
	 * @return string
	 */
	private function getTargetName($target){
		if($target instanceof Player){
			return $target->getName();
		}
		if(is_object($target) and method_exists($target, "getNameTag") and $target->getNameTag() !== ""){
			return $target->getNameTag();
		}
		return "entity";
	}

	/**
	 * @param mixed $target
	 *
	 * @return \pocketmine\level\Level|null
	 */
	private function getTargetLevel($target){
		if(is_object($target) and method_exists($target, "getLevel")){
			return $target->getLevel();
		}
		return null;
	}
}
