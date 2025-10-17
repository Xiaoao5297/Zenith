<?php

/*
 *  ______   _____    ______  __   __  ______
 * /  ___/  /  ___|  / ___  \ \ \ / / |  ____|
 * | |___  | |      | |___| |  \ / /  | |____
 * \___  \ | |      |  ___  |   / /   |  ____|
 *  ___| | | |____  | |   | |  / / \  | |____
 * /_____/  \_____| |_|   |_| /_/ \_\ |______|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Sunch233#3226 QQ2125696621 And KKK
 * @link https://github.com/ScaxeTeam/Scaxe/
 *
*/

namespace pocketmine\command\defaults;

use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\event\TranslationContainer;

use pocketmine\level\format\LevelProviderManager;
use pocketmine\level\Level;
use pocketmine\level\generator\Generator;
use pocketmine\level\format\Chunk;
use pocketmine\level\format\FullChunk;

class ConvertCommand extends VanillaCommand{
	

	public function __construct($name){
		parent::__construct(
			$name,
			"Convert Level Format Command",
			"/convert <LevelName> <NewFormat>"
		);
		$this->setPermission("pocketmine.command.op.give");
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(count($args) != 2){
			$sender->sendMessage("用法: /convert <LevelName> <NewFormat>");
			return false;
		}
		
		if(!$this->testPermission($sender)){
			$sender->sendMessage(base64_decode("U0NBWEU="));
			return true;
		}
		if(!$this->autoLoad($sender, $args[0])){
			$sender->sendMessage("世界不存在!");
			return false;
		}
		$level = $sender->getServer()->getLevelByName($args[0]);
		$provider = $level->getProvider();
		if($provider->getProviderName() == "leveldb"){
			$sender->sendMessage("§c不支持从LevelDB地图转换!");
			return false;
		}
		echo($provider->getProviderName()." name\n");
		if(LevelProviderManager::getProviderByName($args[1]) === null){
			$sender->sendMessage("§c不支持的格式:". $args[1]);
			return false;
		}
		$generator = Generator::getGenerator($provider->getLevelData()->generatorName->getValue());

		$sender->getServer()->generateLevel($args[0]."_".$args[1], $provider->getSeed(), $generator, null, $args[1]);
		$NewLevel = $sender->getServer()->getLevelByName($args[0]."_".$args[1]);
		$NewProvider = $NewLevel->getProvider();
		
		$sender->sendMessage($args[0]."地图, 真实名称".$provider->getLevelData()->LevelName->getValue()." 格式: ".$provider->getProviderName());
		$sender->sendMessage("转换到: ".$NewProvider->getLevelData()->LevelName->getValue()." 格式: ".$NewProvider->getProviderName());
		$sender->sendMessage("路径: ".$provider->getPath()."region/");
		
		$sender->sendMessage("开始遍历文件...");
		$files = scandir($provider->getPath()."region/");
		
		$regionCount = count($files) - 2;
		$ChunkCount = (count($files) - 2) * 1024;
		$sender->sendMessage("遍历完成, 共有".(count($files) - 2)."个region, ".((count($files) - 2) * 1024)."个Chunk");
		sleep(2);
		$finishCount = 0;
		
		$i = 0;
		$blockId = 0;
		$meta = 0;
		foreach($files as $v) {
			if($v != '.' && $v != '..'){
				$i++;
				$v = str_replace("r.", "", $v);
				$v = str_replace("r.", "", $v);
				$v = str_replace(".mca", "", $v);
				$v = str_replace(".mcr", "", $v);
				$v2 = explode(".", $v);
				$sender->sendMessage("§c正在转换Region: ".$v2[0]."_".$v2[1]." | ".(round(($i / $regionCount), 4) * 100)."% | ".round((\memory_get_usage() / 1024 / 1024), 2)." MB");
				$rx = (int) $v2[0];
				$rz = (int) $v2[1];
				for($x = 0; $x < 32; $x++){
					for($z = 0; $z < 32; $z++){
						$xx = ($rx * 32) + $x;
						$zz = ($rz * 32) + $z;			
						$Chunk = $level->getChunk($xx, $zz, true);
						$sender->sendMessage("§6正在转换Chunk: ".$xx."_".$zz." | ".$finishCount."/".$ChunkCount." | ".(round(($finishCount / $ChunkCount), 4) * 100)."%");
						if($Chunk->isGenerated()){
							$NewChunk = $NewLevel->getChunk($xx, $zz, true);
							for($cx = 0; $cx < 16; $cx++){
								for($cz = 0; $cz < 16; $cz++){
									$NewChunk->setBiomeId($cx, $cz, $Chunk->getBiomeId($cx, $cz));
									$color = $Chunk->getBiomeColor($cx, $cz);
									$NewChunk->setBiomeColor($cx, $cz, $color[0], $color[1], $color[2]);
									
									for($cy = 0; $cy < 128; $cy++){
										$blockId = $Chunk->getBlockId($cx, $cy, $cz);
										$meta = $Chunk->getBlockData($cx, $cy, $cz);
										$NewChunk->setBlockId($cx, $cy, $cz, $blockId);
										$NewChunk->setBlockData($cx, $cy, $cz, $meta);
										
										$NewChunk->setBlockExtraData($cx, $cy, $cz, $Chunk->getBlockExtraData($cx, $cy, $cz));
										$NewChunk->setBlockLight($cx, $cy, $cz, $Chunk->getBlockLight($cx, $cy, $cz));
										$NewChunk->setBlockSkyLight($cx, $cy, $cz, $Chunk->getBlockSkyLight($cx, $cy, $cz));
									}
									foreach($Chunk->getTiles() as $tile){
										$NewChunk->addTile($tile);
									}
									foreach($Chunk->getEntities() as $entity){
										$NewChunk->addEntity($entity);
									}
								}
							}
							
							$NewChunk->recalculateHeightMap();
							$NewChunk->setGenerated(1);
							$NewChunk->setPopulated(1);
						}
						$finishCount++;
						$level->unloadChunk($xx, $zz, false, false);
						unset($Chunk);
					}
				}
				//$NewLevel->save();
				$NewLevel->unloadChunks(true);
				gc_collect_cycles();
			}
		}
		$sender->sendMessage("§b转换完成! 保存世界...");
		$NewLevel->save();
		$sender->sendMessage("§c完成!");
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