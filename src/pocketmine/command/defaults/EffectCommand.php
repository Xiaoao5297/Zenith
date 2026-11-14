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


use pocketmine\command\CommandSender;
use pocketmine\command\TargetSelector;
use pocketmine\entity\Effect;
use pocketmine\entity\Entity;
use pocketmine\entity\InstantEffect;
use pocketmine\event\TranslationContainer;
use pocketmine\Player;
use pocketmine\utils\TextFormat;

class EffectCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"%pocketmine.command.effect.description",
			"%commands.effect.usage"
		);
		$this->setPermission("pocketmine.command.effect;pocketmine.command.effect.other");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return true;
		}

		if(count($args) < 2){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
			return true;
		}

		//第一个参数可以是实体选择器（@a/@p/@r/@e/@s）；普通玩家名路径保持原有行为
		$selector = new TargetSelector($sender->getServer());
		$targets = [];

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
		}else{
			$player = $sender->getServer()->getPlayer($args[0]);

			if($player === null){
				$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.generic.player.notFound"));
				return true;
			}

			$targets = [$player];
		}

		if(!$sender->hasPermission("pocketmine.command.effect.other")){
			foreach($targets as $target){
				if($target !== $sender){
					$sender->sendMessage("You don't have permission to give effect to other player .");
					return true;
				}
			}
		}

		if(strtolower($args[1]) === "clear"){
			foreach($targets as $target){
				if(!($target instanceof Entity)){
					continue;
				}
				foreach($target->getEffects() as $effect){
					$target->removeEffect($effect->getId());
				}

				$sender->sendMessage(new TranslationContainer("commands.effect.success.removed.all", [$this->getTargetName($target)]));
			}
			return true;
		}

		$effect = Effect::getEffectByName($args[1]);

		if($effect === null){
			$effect = Effect::getEffect((int) $args[1]);
		}

		if($effect === null){
			$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.effect.notFound", [(string) $args[1]]));
			return true;
		}

		$duration = 300;
		$amplification = 0;

		if(count($args) >= 3){
			$duration = (int) $args[2];
			if(!($effect instanceof InstantEffect)){
				$duration *= 20;
			}
		}elseif($effect instanceof InstantEffect){
			$duration = 1;
		}

		if(count($args) >= 4){
			$amplification = (int) $args[3];
		}

		$visible = true;
		if(count($args) >= 5){
			$v = strtolower($args[4]);
			if($v === "on" or $v === "true" or $v === "t" or $v === "1"){
				$visible = false;
			}
		}

		foreach($targets as $target){
			if(!($target instanceof Entity)){
				continue;
			}

			//每个目标用独立副本，避免多个玩家共享同一个 Effect 实例
			$targetEffect = clone $effect;
			if(!$visible){
				$targetEffect->setVisible(false);
			}
			$displayName = $this->getTargetName($target);

			if($duration === 0){
				if(!$target->hasEffect($targetEffect->getId())){
					if(count($target->getEffects()) === 0){
						$sender->sendMessage(new TranslationContainer("commands.effect.failure.notActive.all", [$displayName]));
					}else{
						$sender->sendMessage(new TranslationContainer("commands.effect.failure.notActive", [$targetEffect->getName(), $displayName]));
					}
					continue;
				}

				$target->removeEffect($targetEffect->getId());
				$sender->sendMessage(new TranslationContainer("commands.effect.success.removed", [$targetEffect->getName(), $displayName]));
			}else{
				$targetEffect->setDuration($duration)->setAmplifier($amplification);

				$target->addEffect($targetEffect);
				self::broadcastCommandMessage($sender, new TranslationContainer("%commands.effect.success", [$targetEffect->getName(), $targetEffect->getId(), $targetEffect->getAmplifier(), $displayName, $targetEffect->getDuration() / 20]));
			}
		}

		return true;
	}

	/**
	 * Entity 没有 getDisplayName()，选择器可能匹配到非玩家实体。
	 *
	 * @param Entity $entity
	 *
	 * @return string
	 */
	private function getTargetName(Entity $entity){
		if($entity instanceof Player){
			return $entity->getDisplayName();
		}
		if($entity->getNameTag() !== ""){
			return $entity->getNameTag();
		}
		return "entity";
	}
}
