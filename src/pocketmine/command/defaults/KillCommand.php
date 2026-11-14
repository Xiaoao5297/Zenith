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
use pocketmine\entity\Entity;
use pocketmine\event\entity\EntityDamageEvent;
use pocketmine\event\TranslationContainer;
use pocketmine\Player;
use pocketmine\utils\TextFormat;

class KillCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"%pocketmine.command.kill.description",
			"%pocketmine.command.kill.usage",
			["suicide"]
		);
		$this->setPermission("pocketmine.command.kill.self;pocketmine.command.kill.other");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return true;
		}

		if(count($args) >= 2){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));

			return false;
		}

		if(count($args) === 1){
			if(!$sender->hasPermission("pocketmine.command.kill.other")){
				$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.generic.permission"));

				return true;
			}

			//参数可以是实体选择器（@a/@p/@r/@e/@s）；普通玩家名路径保持原有行为
			$selector = new TargetSelector($sender->getServer());
			if($selector->isSelector($args[0])){
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

				$killed = 0;
				foreach($targets as $target){
					if(!($target instanceof Entity) or !$target->isAlive()){
						continue;
					}
					if($this->killEntity($sender, $target)){
						++$killed;
						Command::broadcastCommandMessage($sender, new TranslationContainer("commands.kill.successful", [$this->getTargetName($target)]));
					}
				}

				if($killed === 0){
					$sender->sendMessage(TextFormat::RED . "No living targets matched selector " . $args[0]);
				}

				return true;
			}

			$player = $sender->getServer()->getPlayer($args[0]);

			if($player instanceof Player){
				if($this->killEntity($sender, $player)){
					Command::broadcastCommandMessage($sender, new TranslationContainer("commands.kill.successful", [$player->getName()]));
				}
			}else{
				$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.generic.player.notFound"));
			}

			return true;
		}

		if($sender instanceof Player){
			if(!$sender->hasPermission("pocketmine.command.kill.self")){
				$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.generic.permission"));

				return true;
			}

			if($this->killEntity($sender, $sender)){
				$sender->sendMessage(new TranslationContainer("commands.kill.successful", [$sender->getName()]));
			}
		}else{
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));

			return false;
		}

		return true;
	}

	/**
	 * 对单个实体触发自杀伤害事件并置零血量。
	 *
	 * @param CommandSender $sender
	 * @param Entity        $entity
	 *
	 * @return bool 事件是否未被取消
	 */
	private function killEntity(CommandSender $sender, Entity $entity){
		$sender->getServer()->getPluginManager()->callEvent($ev = new EntityDamageEvent($entity, EntityDamageEvent::CAUSE_SUICIDE, 1000));

		if($ev->isCancelled()){
			return false;
		}

		$entity->setLastDamageCause($ev);
		$entity->setHealth(0);

		return true;
	}

	/**
	 * Entity 没有 getName()，选择器可能匹配到非玩家实体。
	 *
	 * @param Entity $entity
	 *
	 * @return string
	 */
	private function getTargetName(Entity $entity){
		if($entity instanceof Player){
			return $entity->getName();
		}
		if($entity->getNameTag() !== ""){
			return $entity->getNameTag();
		}
		return "entity";
	}
}