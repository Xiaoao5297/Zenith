<?php

namespace lycore\command\defaults;

use lycore\command\CommandSender;
use lycore\entity\Entity;
use lycore\event\TranslationContainer;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\FloatTag;
use lycore\nbt\tag\ListTag;
use lycore\Player;
use lycore\utils\TextFormat;

class SummonCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"%pocketmine.command.summon.description",
			"%commands.summon.usage"
		);
		$this->setPermission("pocketmine.command.summon");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return true;
		}

		if(count($args) != 1 and count($args) != 4 and count($args) != 5){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
			return true;
		}

		if($args[0] == "Human"){
			$sender->sendMessage(TextFormat::RED . "绂佹鐢熸垚姝ょ敓鐗╋紒");
			return false;
		}

		if(count($args) == 4 or count($args) == 5){
			$base = $this->getCommandPositionBase($sender);
			try{
				$pos = $this->getRelativeVector($base, $sender, array_slice($args, 1, 3));
			}catch(\InvalidArgumentException $e){
				$sender->sendMessage(TextFormat::RED . "Argument error");
				return false;
			}
		}elseif($sender instanceof Player){
			$pos = $this->getCommandPositionBase($sender);
		}else{
			$sender->sendMessage(TextFormat::RED . "You must specify a position where the entity is spawned to when using in console");
			return false;
		}

		$x = $pos->x;
		$y = $pos->y;
		$z = $pos->z;
		$type = $args[0];
		$level = ($sender instanceof Player) ? $sender->getLevel() : $sender->getServer()->getDefaultLevel();
		$chunk = $level->getChunk(round($x) >> 4, round($z) >> 4);
		$nbt = new CompoundTag("", [
			"Pos" => new ListTag("Pos", [
				new DoubleTag("", $x),
				new DoubleTag("", $y),
				new DoubleTag("", $z)
			]),
			"Motion" => new ListTag("Motion", [
				new DoubleTag("", 0),
				new DoubleTag("", 0),
				new DoubleTag("", 0)
			]),
			"Rotation" => new ListTag("Rotation", [
				new FloatTag("", lcg_value() * 360),
				new FloatTag("", 0)
			]),
		]);

		$entity = Entity::createEntity($type, $chunk, $nbt);
		if($entity instanceof Entity){
			$entity->spawnToAll();
			$sender->sendMessage("Successfully spawned entity $type at ($x, $y, $z)");
			return true;
		}

		$sender->sendMessage(TextFormat::RED . "An error occurred when spawning the entity $type");
		return false;
	}
}
