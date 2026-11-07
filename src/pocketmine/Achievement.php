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
			"name" => "achievement.mineWood.name",
			"requires" => [ //"openInventory",
			],
		],
		"buildWorkBench" => [
			"name" => "achievement.buildWorkBench.name",
			"requires" => [
				"mineWood",
			],
		],
		"buildPickaxe" => [
			"name" => "achievement.buildPickaxe.name",
			"requires" => [
				"buildWorkBench",
			],
		],
		"buildFurnace" => [
			"name" => "achievement.buildFurnace.name",
			"requires" => [
				"buildPickaxe",
			],
		],
		"acquireIron" => [
			"name" => "achievement.acquireIron.name",
			"requires" => [
				"buildFurnace",
			],
		],
		"buildHoe" => [
			"name" => "achievement.buildHoe.name",
			"requires" => [
				"buildWorkBench",
			],
		],
		"makeBread" => [
			"name" => "achievement.makeBread.name",
			"requires" => [
				"buildHoe",
			],
		],
		"bakeCake" => [
			"name" => "achievement.bakeCake.name",
			"requires" => [
				"buildHoe",
			],
		],
		"buildBetterPickaxe" => [
			"name" => "achievement.buildBetterPickaxe.name",
			"requires" => [
				"buildPickaxe",
			],
		],
		"buildSword" => [
			"name" => "achievement.buildSword.name",
			"requires" => [
				"buildWorkBench",
			],
		],
		"diamonds" => [
			"name" => "achievement.diamonds.name",
			"requires" => [
				"acquireIron",
			],
		],

	];


	public static function broadcast(Player $player, $achievementId){
		if(isset(Achievement::$list[$achievementId])){
			$translation = new TranslationContainer("chat.type.achievement", [$player->getDisplayName(), TextFormat::GREEN . Server::getInstance()->getLanguage()->translateString(Achievement::$list[$achievementId]["name"])]);
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
