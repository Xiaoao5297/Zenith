<?php

/**
 * Zero-dependency tests for NaturalMobSpawnRules / AgeableSpawnHelper (B3).
 *
 * Every case calls only pure static helpers, so no Level, Server or world is
 * needed. Block::init() is required because rule evaluation resolves block
 * classes through Block::get().
 */

$zmRoot = dirname(__DIR__) . "/src";
if(!class_exists("pocketmine\\entity\\NaturalMobSpawnRules", false)){
	spl_autoload_register(function($class) use ($zmRoot){
		if(strpos($class, "pocketmine\\") === 0){
			$path = $zmRoot . "/" . str_replace("\\", "/", $class) . ".php";
			if(is_file($path)){
				require_once $path;
			}
		}
	});
}

class NaturalSpawnRulesTest{

	public static function run() : array{
		$rules = \pocketmine\entity\NaturalMobSpawnRules::class;
		$block = \pocketmine\block\Block::class;
		$biome = \pocketmine\level\generator\biome\Biome::class;
		$block::init();

		$results = [];

		$results[] = [
			"hostile light threshold 7/8",
			$rules::isHostileLightLevel(0) === true
				&& $rules::isHostileLightLevel(7) === true
				&& $rules::isHostileLightLevel(8) === false,
			"max=" . $rules::HOSTILE_SPAWN_MAX_LIGHT
		];

		$results[] = [
			"passive light threshold 8/9",
			$rules::isPassiveLightLevel(8) === false
				&& $rules::isPassiveLightLevel(9) === true
				&& $rules::isPassiveLightLevel(15) === true,
			"min=" . $rules::PASSIVE_SPAWN_MIN_LIGHT
		];

		$results[] = [
			"night window is 14000-22999",
			$rules::isNightTime(0) === false
				&& $rules::isNightTime(13999) === false
				&& $rules::isNightTime(14000) === true
				&& $rules::isNightTime(22999) === true
				&& $rules::isNightTime(23000) === false
				&& $rules::isNightTime(38000) === true,
			"TIME_FULL=24000 wraps"
		];

		$results[] = [
			"passive needs grass, hostile needs normal block",
			$rules::isValidSpawnGroundFor($block::GRASS, \pocketmine\entity\Cow::NETWORK_ID) === true
				&& $rules::isValidSpawnGroundFor($block::STONE, \pocketmine\entity\Cow::NETWORK_ID) === false
				&& $rules::isValidSpawnGroundFor($block::STONE, \pocketmine\entity\Zombie::NETWORK_ID) === true
				&& $rules::isValidSpawnGroundFor($block::AIR, \pocketmine\entity\Zombie::NETWORK_ID) === false
				&& $rules::isValidSpawnGroundFor($block::WATER, \pocketmine\entity\Zombie::NETWORK_ID) === false,
			"grass/stone/air/water"
		];

		$results[] = [
			"village ground allows planks but not sand",
			$rules::isValidSpawnGroundFor($block::PLANK, \pocketmine\entity\Villager::NETWORK_ID, true) === true
				&& $rules::isValidSpawnGroundFor($block::FARMLAND, \pocketmine\entity\Villager::NETWORK_ID, true) === true
				&& $rules::isValidSpawnGroundFor($block::SAND, \pocketmine\entity\Villager::NETWORK_ID, true) === false,
			"village surface set"
		];

		$results[] = [
			"desert rabbit requires sand",
			$rules::isValidSpawnGroundFor($block::SAND, \pocketmine\entity\Rabbit::NETWORK_ID, false, $biome::DESERT) === true
				&& $rules::isValidSpawnGroundFor($block::GRASS, \pocketmine\entity\Rabbit::NETWORK_ID, false, $biome::DESERT) === false,
			"desert-only sand rule"
		];

		$results[] = [
			"mooshroom requires mycelium",
			$rules::isValidSpawnGroundFor($block::MYCELIUM, \pocketmine\entity\Mooshroom::NETWORK_ID, false, $biome::MUSHROOM_ISLAND) === true
				&& $rules::isValidSpawnGroundFor($block::GRASS, \pocketmine\entity\Mooshroom::NETWORK_ID, false, $biome::MUSHROOM_ISLAND) === false,
			"mushroom island surface"
		];

		$desertPassive = $rules::getPassivePool($biome::DESERT);
		$junglePassive = $rules::getPassivePool($biome::JUNGLE);
		$results[] = [
			"passive pool per biome",
			$desertPassive === [\pocketmine\entity\Rabbit::NETWORK_ID]
				&& $rules::getPassivePool($biome::OCEAN) === []
				&& $rules::getPassivePool($biome::MUSHROOM_ISLAND) === [\pocketmine\entity\Mooshroom::NETWORK_ID]
				&& in_array(\pocketmine\entity\Ocelot::NETWORK_ID, $junglePassive, true),
			"desert/ocean/mushroom/jungle"
		];

		$swampHostile = $rules::getHostilePool($biome::SWAMP);
		$desertHostile = $rules::getHostilePool($biome::DESERT);
		$results[] = [
			"hostile pool per biome",
			$rules::getHostilePool($biome::MUSHROOM_ISLAND) === []
				&& in_array(\pocketmine\entity\Slime::NETWORK_ID, $swampHostile, true)
				&& in_array(\pocketmine\entity\Husk::NETWORK_ID, $desertHostile, true)
				&& !in_array(\pocketmine\entity\Zombie::NETWORK_ID, $desertHostile, true),
			"mushroom empty, swamp slime, desert husk/no-zombie"
		];

		$results[] = [
			"swamp slime height range 50-70",
			$rules::isSwampSlimeHeightValue(49) === false
				&& $rules::isSwampSlimeHeightValue(50) === true
				&& $rules::isSwampSlimeHeightValue(70) === true
				&& $rules::isSwampSlimeHeightValue(71) === false,
			"y=50..70"
		];

		$results[] = [
			"frozen stray biome and group size",
			$rules::isFrozenStrayBiome($biome::ICE_PLAINS) === true
				&& $rules::isFrozenStrayBiome($biome::DESERT) === false
				&& $rules::getHostileGroupSizeRange(\pocketmine\entity\Stray::NETWORK_ID, $biome::ICE_PLAINS) === [1, 2]
				&& $rules::getHostileGroupSizeRange(\pocketmine\entity\Stray::NETWORK_ID, $biome::PLAINS) === [1, 1]
				&& $rules::getHostileGroupSizeRange(\pocketmine\entity\Husk::NETWORK_ID, $biome::DESERT) === [1, 2],
			"ice plains vs desert"
		];

		$results[] = [
			"peaceful disables monsters only",
			$rules::isHostileSpawningAllowedForDifficulty(0) === false
				&& $rules::isHostileSpawningAllowedForDifficulty(1) === true
				&& $rules::getEntitySpawnLimit(\pocketmine\entity\Zombie::NETWORK_ID, false, 0, ["monsters" => 70]) === 0
				&& $rules::getEntitySpawnLimit(\pocketmine\entity\Zombie::NETWORK_ID, false, 1, ["monsters" => 70]) === 38
				&& $rules::getEntitySpawnLimit(\pocketmine\entity\Zombie::NETWORK_ID, false, 3, ["monsters" => 70]) === 70
				&& $rules::getEntitySpawnLimit(\pocketmine\entity\Cow::NETWORK_ID, false, 0, ["animals" => 15]) === 15,
			"monsters 0/38/70, animals unaffected"
		];

		$results[] = [
			"spawn limits honour configured values",
			$rules::getEntitySpawnLimit(\pocketmine\entity\Cow::NETWORK_ID, false, 1, ["animals" => 4]) === 4
				&& $rules::getEntitySpawnLimit(\pocketmine\entity\Cow::NETWORK_ID, false, 1, ["animals" => 0]) === 0
				&& $rules::getEntitySpawnLimit(\pocketmine\entity\Villager::NETWORK_ID, true, 1, ["animals" => 15]) === 12
				&& $rules::getEntitySpawnLimit(\pocketmine\entity\IronGolem::NETWORK_ID, true, 1, ["animals" => 15]) === 2,
			"animals=4/0, village villager=12, golem=2"
		];

		$results[] = [
			"spawn categories",
			$rules::getEntitySpawnCategory(\pocketmine\entity\Zombie::NETWORK_ID) === "monsters"
				&& $rules::getEntitySpawnCategory(\pocketmine\entity\Cow::NETWORK_ID) === "animals"
				&& $rules::getEntitySpawnCategory(\pocketmine\entity\Squid::NETWORK_ID) === "water-animals"
				&& $rules::getEntitySpawnCategory(\pocketmine\entity\Bat::NETWORK_ID) === "ambient"
				&& $rules::getEntitySpawnCategory(\pocketmine\entity\Lightning::NETWORK_ID) === "",
			"monsters/animals/water/ambient/none"
		];

		$results[] = [
			"zombie variant roll is deterministic",
			$rules::selectZombieVariantEntityId(0) === \pocketmine\entity\ZombieVillager::NETWORK_ID
				&& $rules::selectZombieVariantEntityId(20) === \pocketmine\entity\ZombieVillager::NETWORK_ID
				&& $rules::selectZombieVariantEntityId(1) === \pocketmine\entity\Zombie::NETWORK_ID,
			"roll%20==0 -> zombie villager"
		];

		$results[] = [
			"passive group size ranges",
			$rules::getPassiveGroupSizeRange(\pocketmine\entity\Cow::NETWORK_ID, $biome::PLAINS) === [2, 3]
				&& $rules::getPassiveGroupSizeRange(\pocketmine\entity\Horse::NETWORK_ID, $biome::PLAINS) === [2, 6]
				&& $rules::getPassiveGroupSizeRange(\pocketmine\entity\Wolf::NETWORK_ID, $biome::TAIGA) === [4, 4]
				&& $rules::getPassiveGroupSizeRange(\pocketmine\entity\Wolf::NETWORK_ID, $biome::PLAINS) === [2, 4],
			"cow/horse/wolf taiga vs plains"
		];

		$results[] = [
			"baby chance constants unchanged",
			\pocketmine\entity\AgeableSpawnHelper::PNX_NATURAL_BABY_CHANCE === 6
				&& \pocketmine\entity\AgeableSpawnHelper::PNX_SPAWN_EGG_BABY_CHANCE === 6,
			"natural/spawn-egg chance = 6"
		];

		return $results;
	}
}
