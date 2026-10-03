<?php

/*
 * LY Core MCPE Server Project
 * Do not release the source.
 * @Author: U core
 *
 * Project Website:
 *  > LY Core
 *  > LY Core Project
*/

namespace lycore\command\defaults;

use lycore\command\CommandSender;
use lycore\event\TranslationContainer;
use lycore\item\Item;
use lycore\item\ItemBlock;
use lycore\level\Level;
use lycore\math\Vector3;
use lycore\Player;
use lycore\utils\TextFormat;

class FillCommand extends VanillaCommand{

	public function __construct($name){
		parent::__construct(
			$name,
			"%pocketmine.command.fill.description",
			"/fill <x1> <y1> <z1> <x2> <y2> <z2> <Block> [方块损害值]"
		);
		$this->setPermission("pocketmine.command.fill");
	}

	public function execute(CommandSender $sender, $label, array $args){
		if(!$this->testPermission($sender)){
			return true;
		}

		if(count($args) < 7){
			$sender->sendMessage(TextFormat::RED . new TranslationContainer("Invalid arguments", []));
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
			return false;
		}

		try{
			$base = $this->getCommandPositionBase($sender);
			$from = $this->getRelativeVector($base, $sender, array_slice($args, 0, 3), true);
			$to = $this->getRelativeVector($base, $sender, array_slice($args, 3, 3), true);
		}catch(\InvalidArgumentException $e){
			$sender->sendMessage(TextFormat::RED . new TranslationContainer($e->getMessage(), []));
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$this->usageMessage]));
			return false;
		}

		$item = Item::fromString($args[6]);
		if(!($item instanceof ItemBlock)){
			$sender->sendMessage(TextFormat::RED . new TranslationContainer($args[6] . " is not a valid block.", []));
			return false;
		}

		$xmin = min($from->x, $to->x);
		$xmax = max($from->x, $to->x);
		$ymin = min($from->y, $to->y);
		$ymax = max($from->y, $to->y);
		$zmin = min($from->z, $to->z);
		$zmax = max($from->z, $to->z);
		$level = ($sender instanceof Player) ? $sender->getLevel() : $sender->getServer()->getDefaultLevel();
		$n = 0;
		$nmax = ($xmax - $xmin + 1) * ($ymax - $ymin + 1) * ($zmax - $zmin + 1);

		for($x = $xmin; $x <= $xmax; $x++){
			for($y = $ymin; $y <= $ymax; $y++){
				for($z = $zmin; $z <= $zmax; $z++){
					if($this->setBlock(new Vector3($x, $y, $z), $level, $item, isset($args[7]) ? (int) $args[7] : 0)){
						$n++;
						if(is_int($n / 10000)){
							$sender->sendMessage(new TranslationContainer("$n out of $nmax blocks filled, now at $x $y $z", []));
						}
					}else{
						$sender->sendMessage(TextFormat::RED . new TranslationContainer("Error after filling $n out of $nmax blocks.", []));
						return false;
					}
				}
			}
		}

		$sender->sendMessage(new TranslationContainer("Total of $n blocks filled.", []));
		return true;
	}

	private function setBlock(Vector3 $p, Level $lvl, ItemBlock $b, int $meta = 0) : bool{
		$block = $b->getBlock();
		$block->setDamage($meta);
		$lvl->setBlock($p, $block);
		return true;
	}
}
