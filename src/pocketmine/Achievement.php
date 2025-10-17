<?php

/* 
 * This file is part of the PocketMine-MP project.
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

namespace pocketmine;

use pocketmine\event\TranslationContainer;
use pocketmine\utils\TextFormat;

/**
 * Handles the achievement list and a bit more
 */
abstract class Achievement{
	/**
	 * @var array[]
	 */
	public static $list = [
		/*"openInventory" => array(
			"name" => "Taking Inventory",
			"requires" => [],
		),*/
		"mineWood" => [
			"name" => "获得木头",
			"requires" => [ //"openInventory",
			],
		],
		"buildWorkBench" => [
			"name" => "这是，工作台？",
			"requires" => [
				"mineWood",
			],
		],
		"buildPickaxe" => [
			"name" => "下矿时间到!",
			"requires" => [
				"buildWorkBench",
			],
		],
		"buildFurnace" => [
			"name" => "“热”门话题",
			"requires" => [
				"buildPickaxe",
			],
		],
		"acquireIron" => [
			"name" => "来硬的",
			"requires" => [
				"buildFurnace",
			],
		],
		"buildHoe" => [
			"name" => "耕种时间到！",
			"requires" => [
				"buildWorkBench",
			],
		],
		"makeBread" => [
			"name" => "烤面包",
			"requires" => [
				"buildHoe",
			],
		],
		"bakeCake" => [
			"name" => "蛋糕是个谎言",
			"requires" => [
				"buildHoe",
			],
		],
		"buildBetterPickaxe" => [
			"name" => "获得升级",
			"requires" => [
				"buildPickaxe",
			],
		],
		"buildSword" => [
			"name" => "出击时间到!",
			"requires" => [
				"buildWorkBench",
			],
		],
		"diamonds" => [
			"name" => "钻石!",
			"requires" => [
				"acquireIron",
			],
		],

	];


	public static function broadcast(Player $player, $achievementId){
		if(isset(Achievement::$list[$achievementId])){
			$translation = new TranslationContainer("chat.type.achievement", [$player->getDisplayName(), TextFormat::GREEN . Achievement::$list[$achievementId]["name"]]);
			if(Server::getInstance()->getConfigString("announce-player-achievements", true) === true){
				Server::getInstance()->broadcastMessage($translation);
			}else{
				$player->sendMessage($translation);
			}

			return true;
		}

		return false;
	}

	public static function add($achievementId, $achievementName, array $requires = []){
		if(!isset(Achievement::$list[$achievementId])){
			Achievement::$list[$achievementId] = [
				"name" => $achievementName,
				"requires" => $requires,
			];

			return true;
		}

		return false;
	}


}
