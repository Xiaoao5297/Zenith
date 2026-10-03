<?php

namespace lycore\command\defaults;

use lycore\command\CommandSender;
use lycore\event\TranslationContainer;
use lycore\level\generator\populator\Cave as CavePopulator;
use lycore\level\Level;
use lycore\level\Position;
use lycore\Player;
use lycore\utils\TextFormat;

class CaveCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"Generate a cave",
			"%commands.cave.usage"
		);
		$this->setPermission("pocketmine.command.cave");
	}

	public function execute(CommandSender $sender, $commandLabel, array $args){
		if(!$this->testPermission($sender)){
			return true;
		}

		if($sender instanceof Player && isset($args[0]) && $args[0] === "getmypos"){
			$sender->sendMessage("You position ({$sender->getX()}, {$sender->getY()}, {$sender->getZ()}, {$sender->getLevel()->getFolderName()})");
			return true;
		}

		if(count($args) !== 8){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
			return false;
		}

		$level = $sender->getServer()->getLevelByName($args[7]);
		if(!$level instanceof Level){
			$sender->sendMessage(TextFormat::RED . "Wrong LevelName");
			return false;
		}

		try{
			$vector = $this->getRelativeVector($this->getCommandPositionBase($sender), $sender, array_slice($args, 4, 3));
		}catch(\InvalidArgumentException $e){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
			return false;
		}

		$pos = new Position($vector->x, $vector->y, $vector->z, $level);
		$caves[0] = isset($args[0]) ? $args[0] : mt_rand(1, 360);
		$caves[1] = isset($args[1]) ? $args[1] : mt_rand(10, 300);
		$caves[2] = isset($args[2]) ? $args[2] : mt_rand(1, 6);
		$caves[4] = isset($args[3]) ? $args[3] : mt_rand(1, 10);
		$caves[3] = [false, true, true];

		$sender->sendMessage(new TranslationContainer("commands.cave.info", [$caves[0], $caves[1], $caves[2], $caves[4]]));
		$sender->sendMessage("[Caves] " . TextFormat::YELLOW . "%commands.cave.start");
		$sender->sendMessage($pos->x . " " . $pos->y . " " . $pos->z);
		$this->caves($pos, $caves);
		$sender->sendMessage("[Caves] " . TextFormat::GREEN . "%commands.cave.success");
		return true;
	}

	public function caves(Position $pos, $cave, $tt = false){
		$level = $pos->getLevel();
		$random = new \lycore\utils\Random(method_exists($level, "getSeed") ? $level->getSeed() : time());
		$generator = new CavePopulator();
		$generator->caves($random, $level, $pos, $cave, $tt);
	}
}
