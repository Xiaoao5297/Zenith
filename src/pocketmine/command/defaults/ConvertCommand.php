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
			$sender->sendMessage("�÷�: /convert <LevelName> <NewFormat>");
			return false;
		}
		
		if(!$this->testPermission($sender)){
			$sender->sendMessage(base64_decode("U0NBWEU="));
			return true;
		}
		if(!$this->autoLoad($sender, $args[0])){
			$sender->sendMessage("���粻����!");
			return false;
		}
		$level = $sender->getServer()->getLevelByName($args[0]);
		$provider = $level->getProvider();
		if($provider->getProviderName() == "leveldb"){
			$sender->sendMessage("��c��֧�ִ�LevelDB��ͼת��!");
			return false;
		}
		echo($provider->getProviderName()." name\n");
		if(LevelProviderManager::getProviderByName($args[1]) === null){
			$sender->sendMessage("��c��֧�ֵĸ�ʽ:". $args[1]);
			return false;
		}
		$generator = Generator::getGenerator($provider->getLevelData()->generatorName->getValue());

		$sender->getServer()->generateLevel($args[0]."_".$args[1], $provider->getSeed(), $generator, null, $args[1]);
		$NewLevel = $sender->getServer()->getLevelByName($args[0]."_".$args[1]);
		$NewProvider = $NewLevel->getProvider();
		
		$sender->sendMessage($args[0]."��ͼ, ��ʵ����".$provider->getLevelData()->LevelName->getValue()." ��ʽ: ".$provider->getProviderName());
		$sender->sendMessage("ת����: ".$NewProvider->getLevelData()->LevelName->getValue()." ��ʽ: ".$NewProvider->getProviderName());
		$sender->sendMessage("·��: ".$provider->getPath()."region/");
		
		$sender->sendMessage("��ʼ�����ļ�...");
		$files = scandir($provider->getPath()."region/");
		
		$regionCount = count($files) - 2;
		$ChunkCount = (count($files) - 2) * 1024;
		$sender->sendMessage("�������, ����".(count($files) - 2)."��region, ".((count($files) - 2) * 1024)."��Chunk");
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
				$sender->sendMessage("��c����ת��Region: ".$v2[0]."_".$v2[1]." | ".(round(($i / $regionCount), 4) * 100)."% | ".round((\memory_get_usage() / 1024 / 1024), 2)." MB");
				$rx = (int) $v2[0];
				$rz = (int) $v2[1];
				for($x = 0; $x < 32; $x++){
					for($z = 0; $z < 32; $z++){
						$xx = ($rx * 32) + $x;
						$zz = ($rz * 32) + $z;			
						$Chunk = $level->getChunk($xx, $zz, true);
						$sender->sendMessage("��6����ת��Chunk: ".$xx."_".$zz." | ".$finishCount."/".$ChunkCount." | ".(round(($finishCount / $ChunkCount), 4) * 100)."%");
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
		$sender->sendMessage("��bת�����! ��������...");
		$NewLevel->save();
		$sender->sendMessage("��c���!");
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