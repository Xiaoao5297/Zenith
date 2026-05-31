<?php

/*
 *来自AWSD核心
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
			"name" => "获取木材 -获取原木",
			"requires" => [ //"openInventory",
			],
		],
		"buildWorkBench" => [
			"name" => "制作工作台 -用四块木板合成工作台",
			"requires" => [
				"mineWood",
			],
		],
		"buildPickaxe" => [
			"name" => "采矿时间到! -制作木稿",
			"requires" => [
				"buildWorkBench",
			],
		],
		"buildFurnace" => [
			"name" => "热门话题 -制作熔炉",
			"requires" => [
				"buildPickaxe",
			],
		],
		"acquireIron" => [
			"name" => "来硬的 -炼出铁锭",
			"requires" => [
				"buildFurnace",
			],
		],
		"buildHoe" => [
			"name" => "耕种时间到! -制作木锄",
			"requires" => [
				"buildWorkBench",
			],
		],
		"makeBread" => [
			"name" => "烤面包 -制作一个面包",
			"requires" => [
				"buildHoe",
			],
		],
		"bakeCake" => [
			"name" => "蛋糕是个谎言 -制作一个蛋糕",
			"requires" => [
				"buildHoe",
			],
		],
		"buildBetterPickaxe" => [
			"name" => "获得升级 -制作石稿",
			"requires" => [
				"buildPickaxe",
			],
		],
		"buildSword" => [
			"name" => "出击时间到! -做出木剑",
			"requires" => [
				"buildWorkBench",
			],
		],
		"diamonds" => [
			"name" => "是钻石! -获取钻石",
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
