<?php

/* 
 *  _____              _ __  __  
 * /__  /  ___  ____  (_) /_/ /_ 
 *   / /  / _ \/ __ \/ / __/ __ \
 *  / /__/  __/ / / / / /_/ / / /
 * /____/\___/_/ /_/_/\__/_/ /_/ 
 *                               
 * This program is free software: you can redistribute/modify it 
 * under the terms of the GNU LGPL, version 3 or later.
 *
 * @author Xiaoao
 * @link https://b23.tv/LQKxdts
 *
*/

namespace pocketmine\command\defaults;

use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\event\TranslationContainer;

use pocketmine\level\format\generic\BaseLevelProvider;
use pocketmine\level\generator\Generator;
use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\nbt\tag\LongTag;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\math\Vector3;

class LvdatCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"%pocketmine.command.lvdat.description",
			"/lvdat <level-name> <opts|help>"
		);
		$this->setPermission("pocketmine.command.lvdat");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return false;
		}
		$levname = array_shift($args);
		if($levname == ""){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
			return false;
		}
		if(!$this->autoLoad($sender, $levname)){
			$sender->sendMessage(new TranslationContainer("pocketmine.command.lvdat.nofound", [$levname]));
			return false;
		}
		$level = $sender->getServer()->getLevelByName($levname);
		if(!$level){
			$sender->sendMessage(new TranslationContainer("pocketmine.command.lvdat.nofound", [$levname]));
			return false;
		}
		/** @var BaseLevelProvider $provider */
		$provider = $level->getProvider();
		$o = array_shift($args);
		$p = array_shift($args);
		switch($o){
			case "fixname":
				$provider->getLevelData()->LevelName = new StringTag("LevelName", $level->getFolderName());
				$sender->sendMessage(new TranslationContainer("pocketmine.command.lvdat.fixname", [$level->getFolderName()]));
				break;
			case "opts":
				$sender->sendMessage($levname."��ͼ, ��ʵ����".$provider->getLevelData()->LevelName->getValue()." ��ʽ: ".$provider->getProviderName());
				$sender->sendMessage("ʹ�� ".$provider->getLevelData()->generatorName->getValue()." ������, ����: ".$provider->getSeed());
				$sender->sendMessage("������ѡ��: ".$provider->getLevelData()->generatorOptions->getValue());
				$sender->sendMessage("����ʱ��: ".$provider->getTime());
				$v3 = $provider->getSpawn();
				$sender->sendMessage("������: ".$v3->x." ".$v3->y." ".$v3->z);
				break;
			case "help":
				$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
				$sender->sendMessage("/lvdat %commands.generic.level fixname");
				$sender->sendMessage("/lvdat %commands.generic.level seed %commands.generic.seed");
				$sender->sendMessage("/lvdat %commands.generic.level name %commands.generic.name");
				$sender->sendMessage("/lvdat %commands.generic.level generator %commands.generic.generator");
				$sender->sendMessage("/lvdat %commands.generic.level preset %pocketmine.command.lvdat.preset");
				break;
			case "seed":
				if($p == ""){
					$sender->sendMessage("%commands.generic.opt.missing");
					return false;
				}
				$provider->setSeed($p);
				$sender->sendMessage(new TranslationContainer("pocketmine.command.lvdat.changed", [$level->getFolderName(), $o]));
				break;
			case "name":
				if($p == ""){
					$sender->sendMessage("%commands.generic.opt.missing");
					return false;
				}
				$provider->getLevelData()->LevelName = new StringTag("LevelName", $p);
				$sender->sendMessage(new TranslationContainer("pocketmine.command.lvdat.changed", [$level->getFolderName(), $o]));
				break;
			case "generator":
				if($p == ""){
					$sender->sendMessage("%commands.generic.opt.missing");
					return false;
				}
				$provider->getLevelData()->generatorName = new StringTag("generatorName", $p);
				$sender->sendMessage(new TranslationContainer("pocketmine.command.lvdat.changed", [$level->getFolderName(), $o]));
				break;
			case "preset":
				if($p == ""){
					$sender->sendMessage("%commands.generic.opt.missing");
					return false;
				}
				$provider->getLevelData()->generatorOptions = new StringTag("generatorOptions", $p);
				$sender->sendMessage(new TranslationContainer("pocketmine.command.lvdat.changed", [$level->getFolderName(), $o]));
				break;
			default:
				$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
				return false;
		}
		$provider->saveLevelData();
		return true;
	}

	public function autoLoad(CommandSender $c, $world){
		if($c->getServer()->isLevelLoaded($world)) return true;
		if(!$c->getServer()->isLevelGenerated($world)){
			return false;
		}
		$c->getServer()->loadLevel($world);
		return $c->getServer()->isLevelLoaded($world);
	}
}
