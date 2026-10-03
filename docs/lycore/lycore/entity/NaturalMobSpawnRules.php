<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝
 * ╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 *  ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: U core
 *
 * @Links:
 *  > LY Core
 *  > LY Core Project
*/

namespace lycore\entity;

use lycore\block\Block;
use lycore\level\generator\biome\Biome;
use lycore\level\Level;
use lycore\level\Position;

final class NaturalMobSpawnRules{
	const HOSTILE_SPAWN_MAX_LIGHT = 7;
	const PASSIVE_SPAWN_MIN_LIGHT = 9;
	const ZOMBIE_VILLAGER_VARIANT_CHANCE = 20;
	const DAYTIME_DESERT_HUSK_CHANCE = 64;
	const GHAST_LOCAL_DENSITY_LIMIT = 2;
	const GHAST_LOCAL_DENSITY_RADIUS = 128;
	const GHAST_SPAWN_VERTICAL_AIR = 4;
	const SPAWN_CATEGORY_MONSTERS = "monsters";
	const SPAWN_CATEGORY_ANIMALS = "animals";
	const SPAWN_CATEGORY_WATER_ANIMALS = "water-animals";
	const SPAWN_CATEGORY_AMBIENT = "ambient";

	private function __construct(){
	}

	public static function selectLandEntityId(Level $level, Position $pos, bool $inVillage = false, int $roll = null, int $zombieVariantRoll = null, int $daytimeDesertHuskRoll = null) : int{
		if($level->getDimension() === Level::DIMENSION_NETHER){
			if(!self::canSpawnLandEntityOn($level, $pos, PigZombie::NETWORK_ID, false)){
				return 0;
			}

			if(self::isNetherFortressPosition($level, $pos)){
				return self::selectFromPool(self::getNetherFortressLandPool(), $roll);
			}

			return self::selectFromPool(self::getNetherLandPool(), $roll);
		}

		if($level->getDimension() === Level::DIMENSION_END){
			if(!self::canSpawnHostileAt($level, $pos) || !self::canSpawnLandEntityOn($level, $pos, Enderman::NETWORK_ID, false)){
				return 0;
			}

			return self::selectFromPool(self::getHostilePool(Biome::END), $roll);
		}

		if($level->getDimension() !== Level::DIMENSION_NORMAL){
			return 0;
		}

		$biomeId = (int) $level->getBiomeId((int) floor($pos->x), (int) floor($pos->z));
		if($biomeId === Biome::VILLAGE){
			$inVillage = true;
		}

		if($inVillage && !self::isDarkOverworldPosition($level, $pos)){
			if(!self::canSpawnLandEntityOn($level, $pos, Villager::NETWORK_ID, true)){
				return 0;
			}

			return self::selectFromPool(self::getPassivePool($biomeId, true), $roll);
		}

		if(self::isDarkOverworldPosition($level, $pos)){
			if(self::canSpawnHostileAt($level, $pos)){
				$hostilePool = self::getHostilePoolForPosition($biomeId, $pos);
				$spawnablePool = [];
				foreach($hostilePool as $entityId){
					if(self::canSpawnLandEntityOn($level, $pos, $entityId, false)){
						$spawnablePool[] = $entityId;
					}
				}

				$entityId = self::selectFromPool($spawnablePool, $roll);
				return $entityId === Zombie::NETWORK_ID ? self::selectZombieVariantEntityId($zombieVariantRoll) : $entityId;
			}

			return 0;
		}

		if(self::shouldSelectDaytimeDesertHusk($level, $pos, $daytimeDesertHuskRoll)){
			return Husk::NETWORK_ID;
		}

		if(!self::canSpawnPassiveAt($level, $pos)){
			return 0;
		}

		$passivePool = self::getPassivePool($biomeId, false);
		$spawnablePool = [];
		foreach($passivePool as $entityId){
			if(self::canSpawnSelectedLandEntityAt($level, $pos, $entityId, false)){
				$spawnablePool[] = $entityId;
			}
		}

		return self::selectFromPool($spawnablePool, $roll);
	}

	public static function selectLandEntityGroupIds(Level $level, Position $pos, bool $inVillage = false, int $roll = null, int $groupRoll = null, int $smallRoll = null, int $zombieVariantRoll = null, int $daytimeDesertHuskRoll = null) : array{
		if($level->getDimension() !== Level::DIMENSION_NORMAL){
			$entityId = self::selectLandEntityId($level, $pos, $inVillage, $roll, $zombieVariantRoll, $daytimeDesertHuskRoll);
			return $entityId > 0 ? [$entityId] : [];
		}

		$biomeId = (int) $level->getBiomeId((int) floor($pos->x), (int) floor($pos->z));
		if($biomeId === Biome::VILLAGE){
			$inVillage = true;
		}

		if($inVillage || self::isDarkOverworldPosition($level, $pos)){
			$entityId = self::selectLandEntityId($level, $pos, $inVillage, $roll, $zombieVariantRoll, $daytimeDesertHuskRoll);
			if($entityId <= 0){
				return [];
			}
			if(self::isHostileEntityId($entityId)){
				$range = self::getHostileGroupSizeRange($entityId, $biomeId);
				$groupSize = self::selectInRange($range[0], $range[1], $groupRoll);
				return array_fill(0, $groupSize, $entityId);
			}
			return [$entityId];
		}

		if(self::shouldSelectDaytimeDesertHusk($level, $pos, $daytimeDesertHuskRoll)){
			$range = self::getHostileGroupSizeRange(Husk::NETWORK_ID, (int) $level->getBiomeId((int) floor($pos->x), (int) floor($pos->z)));
			$groupSize = self::selectInRange($range[0], $range[1], $groupRoll);
			return array_fill(0, $groupSize, Husk::NETWORK_ID);
		}

		if(!self::canSpawnPassiveAt($level, $pos)){
			return [];
		}

		$passivePool = self::getPassiveHerdSelectionPool($biomeId);
		$spawnablePool = [];
		foreach($passivePool as $entityId){
			if(self::canSpawnSelectedLandEntityAt($level, $pos, $entityId, false)){
				$spawnablePool[] = $entityId;
			}
		}

		$entityId = self::selectFromPool($spawnablePool, $roll);
		if($entityId <= 0){
			return [];
		}

		$range = self::getPassiveGroupSizeRange($entityId, $biomeId);
		$groupSize = self::selectInRange($range[0], $range[1], $groupRoll);
		$group = array_fill(0, $groupSize, $entityId);

		if(self::canMixSmallPassiveWith($entityId)){
			$smallPool = [];
			foreach(self::getSmallPassiveMixPool($biomeId) as $smallEntityId){
				if($smallEntityId !== $entityId && self::canSpawnSelectedLandEntityAt($level, $pos, $smallEntityId, false)){
					$smallPool[] = $smallEntityId;
				}
			}

			$smallEntityId = self::selectFromPool($smallPool, $smallRoll);
			if($smallEntityId > 0){
				$group[] = $smallEntityId;
			}
		}

		return $group;
	}

	public static function selectAirEntityId(Level $level, Position $pos, int $roll = null) : int{
		if($level->getDimension() === Level::DIMENSION_NETHER){
			$spawnablePool = [];
			foreach(self::getNetherAirPool() as $entityId){
				if(self::canSpawnSelectedAirEntityAt($level, $pos, $entityId)){
					$spawnablePool[] = $entityId;
				}
			}

			return self::selectFromPool($spawnablePool, $roll);
		}

		return 0;
	}

	public static function selectCaveEntityId(Level $level, Position $pos, int $roll = null) : int{
		if($level->getDimension() !== Level::DIMENSION_NORMAL){
			return 0;
		}

		$biomeId = (int) $level->getBiomeId((int) floor($pos->x), (int) floor($pos->z));
		if(!self::canUseOverworldCaveAmbientPool($biomeId)){
			return 0;
		}

		if((int) floor($pos->y) >= 63){
			return 0;
		}

		if($level->getFullLightAt((int) floor($pos->x), (int) floor($pos->y), (int) floor($pos->z)) > 3){
			return 0;
		}

		return self::selectFromPool(self::getCaveAmbientPool(), $roll);
	}

	public static function selectWaterEntityId(int $biomeId, int $roll = null) : int{
		if(!self::isWaterBiome($biomeId)){
			return 0;
		}

		return self::selectFromPool([Squid::NETWORK_ID], $roll);
	}

	public static function selectWaterEntityIdForPosition(Level $level, Position $pos, int $roll = null) : int{
		if($level->getDimension() !== Level::DIMENSION_NORMAL){
			return 0;
		}

		$blockId = $level->getBlockIdAt((int) floor($pos->x), (int) floor($pos->y), (int) floor($pos->z));
		if($blockId !== Block::WATER && $blockId !== Block::STILL_WATER){
			return 0;
		}

		$biomeId = (int) $level->getBiomeId((int) floor($pos->x), (int) floor($pos->z));
		return self::selectWaterEntityId($biomeId, $roll);
	}

	public static function getNetherLandPool() : array{
		return [
			PigZombie::NETWORK_ID,
			PigZombie::NETWORK_ID,
			PigZombie::NETWORK_ID,
			LavaSlime::NETWORK_ID
		];
	}

	public static function getNetherFortressLandPool() : array{
		return [
			PigZombie::NETWORK_ID,
			PigZombie::NETWORK_ID,
			LavaSlime::NETWORK_ID,
			Blaze::NETWORK_ID
		];
	}

	public static function getNetherAirPool() : array{
		return [Ghast::NETWORK_ID];
	}

	public static function getCaveAmbientPool() : array{
		return [Bat::NETWORK_ID];
	}

	public static function selectZombieVariantEntityId(int $roll = null) : int{
		if($roll === null){
			$roll = mt_rand(0, self::ZOMBIE_VILLAGER_VARIANT_CHANCE - 1);
		}

		return abs($roll) % self::ZOMBIE_VILLAGER_VARIANT_CHANCE === 0 ? ZombieVillager::NETWORK_ID : Zombie::NETWORK_ID;
	}

	public static function getNaturalSpawnEntityIds() : array{
		return [
			Bat::NETWORK_ID,
			Blaze::NETWORK_ID,
			Chicken::NETWORK_ID,
			Cow::NETWORK_ID,
			Creeper::NETWORK_ID,
			Enderman::NETWORK_ID,
			Ghast::NETWORK_ID,
			Horse::NETWORK_ID,
			Husk::NETWORK_ID,
			IronGolem::NETWORK_ID,
			LavaSlime::NETWORK_ID,
			Mooshroom::NETWORK_ID,
			Ocelot::NETWORK_ID,
			Pig::NETWORK_ID,
			PigZombie::NETWORK_ID,
			Rabbit::NETWORK_ID,
			Sheep::NETWORK_ID,
			Skeleton::NETWORK_ID,
			Slime::NETWORK_ID,
			Spider::NETWORK_ID,
			Stray::NETWORK_ID,
			Squid::NETWORK_ID,
			Villager::NETWORK_ID,
			Witch::NETWORK_ID,
			Wolf::NETWORK_ID,
			Zombie::NETWORK_ID,
			ZombieVillager::NETWORK_ID
		];
	}

	public static function getNonNaturalMobEntityReasons() : array{
		return [
			CaveSpider::NETWORK_ID => "spawner_only",
			Lightning::NETWORK_ID => "weather_event",
			Silverfish::NETWORK_ID => "block_or_spawner_only",
			SnowGolem::NETWORK_ID => "player_built_only"
		];
	}

	public static function getPassivePool(int $biomeId, bool $inVillage = false) : array{
		if($inVillage || $biomeId === Biome::VILLAGE){
			return [
				Villager::NETWORK_ID,
				Villager::NETWORK_ID,
				Villager::NETWORK_ID,
				Villager::NETWORK_ID,
				Villager::NETWORK_ID,
				Villager::NETWORK_ID,
				Villager::NETWORK_ID,
				Villager::NETWORK_ID,
				IronGolem::NETWORK_ID
			];
		}

		if(self::isMushroomBiome($biomeId)){
			return [Mooshroom::NETWORK_ID];
		}

		if(self::isWolfBiome($biomeId)){
			$pool = [
				Wolf::NETWORK_ID,
				Wolf::NETWORK_ID,
				Sheep::NETWORK_ID,
				Chicken::NETWORK_ID,
				Pig::NETWORK_ID,
				Cow::NETWORK_ID
			];
			if(self::isRabbitBiome($biomeId)){
				$pool[] = Rabbit::NETWORK_ID;
			}
			return $pool;
		}

		switch($biomeId){
			case Biome::END:
			case Biome::HELL:
			case Biome::SKY:
				return [];

			case Biome::DESERT:
			case Biome::DESERT_HILLS:
			case Biome::ICE_PLAINS:
			case Biome::ICE_MOUNTAINS:
				$pool = [Rabbit::NETWORK_ID];
				return $pool;

			case Biome::SAVANNA:
			case Biome::SAVANNA_PLATEAU:
				return [
					Rabbit::NETWORK_ID,
					Sheep::NETWORK_ID,
					Pig::NETWORK_ID,
					Chicken::NETWORK_ID,
					Cow::NETWORK_ID,
					Horse::NETWORK_ID
				];

			case Biome::PLAINS:
				return [
					Sheep::NETWORK_ID,
					Pig::NETWORK_ID,
					Chicken::NETWORK_ID,
					Cow::NETWORK_ID,
					Horse::NETWORK_ID
				];

			case Biome::JUNGLE:
			case Biome::JUNGLE_HILLS:
			case Biome::JUNGLE_EDGE:
				return [
					Ocelot::NETWORK_ID,
					Chicken::NETWORK_ID,
					Chicken::NETWORK_ID,
					Pig::NETWORK_ID,
					Cow::NETWORK_ID,
					Sheep::NETWORK_ID
				];

			case Biome::OCEAN:
			case Biome::DEEP_OCEAN:
			case Biome::FROZEN_OCEAN:
			case Biome::RIVER:
			case Biome::FROZEN_RIVER:
			case Biome::BEACH:
			case Biome::COLD_BEACH:
			case Biome::STONE_BEACH:
				return [];

			default:
				$pool = [
					Sheep::NETWORK_ID,
					Pig::NETWORK_ID,
					Chicken::NETWORK_ID,
					Cow::NETWORK_ID
				];
				if(self::isSnowyPassiveBiome($biomeId)){
					$pool[] = Rabbit::NETWORK_ID;
				}

				return $pool;
		}
	}

	public static function getPassiveGroupSizeRange(int $entityId, int $biomeId) : array{
		switch($entityId){
			case Cow::NETWORK_ID:
			case Sheep::NETWORK_ID:
				return [2, 3];

			case Pig::NETWORK_ID:
				return [1, 3];

			case Chicken::NETWORK_ID:
				return [2, 4];

			case Rabbit::NETWORK_ID:
				return [2, 3];

			case Mooshroom::NETWORK_ID:
				return [4, 8];

			case Wolf::NETWORK_ID:
				if($biomeId === Biome::TAIGA || $biomeId === Biome::TAIGA_HILLS || $biomeId === Biome::COLD_TAIGA || $biomeId === Biome::COLD_TAIGA_HILLS || $biomeId === Biome::MEGA_TAIGA || $biomeId === Biome::MEGA_TAIGA_HILLS){
					return [4, 4];
				}
				return [2, 4];

			case Horse::NETWORK_ID:
				return [2, 6];

			case Ocelot::NETWORK_ID:
				return [1, 2];

			default:
				return [1, 1];
		}
	}

	public static function getHostileGroupSizeRange(int $entityId, int $biomeId) : array{
		switch($entityId){
			case Husk::NETWORK_ID:
				return self::isDesertHuskBiome($biomeId) ? [1, 2] : [1, 1];
			case Stray::NETWORK_ID:
				return self::isFrozenStrayBiome($biomeId) ? [1, 2] : [1, 1];
			default:
				return [1, 1];
		}
	}

	public static function getSmallPassiveMixPool(int $biomeId) : array{
		$pool = [];
		foreach(self::getPassivePool($biomeId, false) as $entityId){
			if($entityId === Chicken::NETWORK_ID || $entityId === Rabbit::NETWORK_ID){
				if(!in_array($entityId, $pool, true)){
					$pool[] = $entityId;
				}
			}
		}

		return $pool;
	}

	public static function getHostilePool(int $biomeId) : array{
		if(self::isMushroomBiome($biomeId)){
			return [];
		}

		switch($biomeId){
			case Biome::END:
				return [Enderman::NETWORK_ID];

			case Biome::HELL:
			case Biome::SKY:
				return [];

			case Biome::DESERT:
			case Biome::DESERT_HILLS:
				return [
					Husk::NETWORK_ID,
					Skeleton::NETWORK_ID,
					Creeper::NETWORK_ID,
					Spider::NETWORK_ID,
					Enderman::NETWORK_ID,
					Witch::NETWORK_ID
				];

			case Biome::ICE_PLAINS:
			case Biome::ICE_MOUNTAINS:
			case Biome::FROZEN_OCEAN:
			case Biome::FROZEN_RIVER:
			case Biome::COLD_TAIGA:
			case Biome::COLD_TAIGA_HILLS:
				return [
					Zombie::NETWORK_ID,
					Zombie::NETWORK_ID,
					Zombie::NETWORK_ID,
					Stray::NETWORK_ID,
					Creeper::NETWORK_ID,
					Spider::NETWORK_ID,
					Enderman::NETWORK_ID,
					Witch::NETWORK_ID
				];

			case Biome::SWAMP:
				return [
					Zombie::NETWORK_ID,
					Zombie::NETWORK_ID,
					Zombie::NETWORK_ID,
					Skeleton::NETWORK_ID,
					Creeper::NETWORK_ID,
					Spider::NETWORK_ID,
					Enderman::NETWORK_ID,
					Witch::NETWORK_ID,
					Slime::NETWORK_ID
				];

			default:
				return [
					Zombie::NETWORK_ID,
					Zombie::NETWORK_ID,
					Zombie::NETWORK_ID,
					Skeleton::NETWORK_ID,
					Creeper::NETWORK_ID,
					Spider::NETWORK_ID,
					Enderman::NETWORK_ID,
					Witch::NETWORK_ID
				];
		}
	}

	public static function getDefaultSpawnLimits() : array{
		return [
			self::SPAWN_CATEGORY_MONSTERS => 70,
			self::SPAWN_CATEGORY_ANIMALS => 15,
			self::SPAWN_CATEGORY_WATER_ANIMALS => 5,
			self::SPAWN_CATEGORY_AMBIENT => 15
		];
	}

	public static function getEntitySpawnCategory(int $entityId) : string{
		if(self::isHostileEntityId($entityId)){
			return self::SPAWN_CATEGORY_MONSTERS;
		}

		switch($entityId){
			case Squid::NETWORK_ID:
				return self::SPAWN_CATEGORY_WATER_ANIMALS;
			case Bat::NETWORK_ID:
				return self::SPAWN_CATEGORY_AMBIENT;
			case Cow::NETWORK_ID:
			case Pig::NETWORK_ID:
			case Sheep::NETWORK_ID:
			case Chicken::NETWORK_ID:
			case Rabbit::NETWORK_ID:
			case Mooshroom::NETWORK_ID:
			case Ocelot::NETWORK_ID:
			case Wolf::NETWORK_ID:
			case Villager::NETWORK_ID:
			case IronGolem::NETWORK_ID:
			case Horse::NETWORK_ID:
				return self::SPAWN_CATEGORY_ANIMALS;
			default:
				return "";
		}
	}

	public static function getEntitySpawnLimit(int $entityId, bool $inVillage = false, int $difficulty = 1, $configuredLimits = null) : int{
		if($inVillage){
			switch($entityId){
				case Villager::NETWORK_ID:
					return 12;
				case IronGolem::NETWORK_ID:
					return 2;
			}
		}

		$category = self::getEntitySpawnCategory($entityId);
		if($category === ""){
			return 0;
		}

		$limit = self::getConfiguredSpawnLimit($category, $configuredLimits);
		if($limit <= 0){
			return 0;
		}

		return self::applyDifficultyToSpawnLimit($category, $limit, $difficulty);
	}

	private static function getConfiguredSpawnLimit(string $category, $configuredLimits = null) : int{
		$defaults = self::getDefaultSpawnLimits();
		$limit = $defaults[$category] ?? 0;
		if(is_array($configuredLimits) && isset($configuredLimits[$category])){
			$limit = (int) $configuredLimits[$category];
		}

		return max(0, (int) $limit);
	}

	private static function applyDifficultyToSpawnLimit(string $category, int $limit, int $difficulty) : int{
		$difficulty = max(0, min(3, $difficulty));
		if($category === self::SPAWN_CATEGORY_MONSTERS){
			switch($difficulty){
				case 0:
					return 0;
				case 1:
					return max(1, (int) floor($limit * 0.55));
				case 2:
					return max(1, (int) floor($limit * 0.8));
				case 3:
				default:
					return $limit;
			}
		}

		return max(1, $limit);
	}

	public static function canSpawnHostileAt(Level $level, Position $pos) : bool{
		if($level->getDimension() === Level::DIMENSION_END){
			return $level->getFullLightAt((int) floor($pos->x), (int) floor($pos->y), (int) floor($pos->z)) <= self::HOSTILE_SPAWN_MAX_LIGHT;
		}

		if($level->getDimension() !== Level::DIMENSION_NORMAL){
			return true;
		}

		$biomeId = (int) $level->getBiomeId((int) floor($pos->x), (int) floor($pos->z));
		if(self::isMushroomBiome($biomeId)){
			return false;
		}

		return self::isDarkOverworldPosition($level, $pos);
	}

	public static function canSpawnDaytimeDesertHuskAt(Level $level, Position $pos) : bool{
		if($level->getDimension() !== Level::DIMENSION_NORMAL || self::isNight($level)){
			return false;
		}

		$biomeId = (int) $level->getBiomeId((int) floor($pos->x), (int) floor($pos->z));
		if(!self::isDesertHuskBiome($biomeId) || self::isDarkOverworldPosition($level, $pos)){
			return false;
		}

		return self::canSpawnLandEntityOn($level, $pos, Husk::NETWORK_ID, false);
	}

	public static function canSpawnPassiveAt(Level $level, Position $pos) : bool{
		if($level->getDimension() !== Level::DIMENSION_NORMAL){
			return true;
		}

		if(self::isNight($level)){
			return false;
		}

		return $level->getFullLightAt((int) floor($pos->x), (int) floor($pos->y), (int) floor($pos->z)) >= self::PASSIVE_SPAWN_MIN_LIGHT;
	}

	public static function isNight(Level $level) : bool{
		$time = $level->getTime() % Level::TIME_FULL;
		return $time >= Level::TIME_NIGHT && $time < Level::TIME_SUNRISE;
	}

	public static function isSwampSlimeHeight(Position $pos) : bool{
		$y = (int) floor($pos->y);
		return $y >= 50 && $y <= 70;
	}

	public static function canSpawnLandEntityOn(Level $level, Position $pos, int $entityId, bool $inVillage = false) : bool{
		$groundId = $level->getBlockIdAt((int) floor($pos->x), (int) floor($pos->y) - 1, (int) floor($pos->z));

		if($level->getDimension() === Level::DIMENSION_END){
			return $entityId === Enderman::NETWORK_ID && $groundId === Block::END_STONE;
		}

		if($level->getDimension() === Level::DIMENSION_NETHER){
			switch($groundId){
				case Block::NETHERRACK:
				case Block::NETHER_BRICKS:
				case Block::NETHER_BRICK_FENCE:
				case Block::NETHER_BRICKS_STAIRS:
					return true;
				default:
					return false;
			}
		}

		if($inVillage){
			switch($groundId){
				case Block::GRASS:
				case Block::DIRT:
				case Block::GRAVEL:
				case Block::COBBLESTONE:
				case Block::PLANK:
				case Block::STONE:
				case Block::FARMLAND:
				case Block::GRASS_PATH:
					return true;
				default:
					return false;
			}
		}

		if($entityId === Mooshroom::NETWORK_ID){
			return $groundId === Block::MYCELIUM;
		}

		if($entityId === Rabbit::NETWORK_ID){
			$biomeId = (int) $level->getBiomeId((int) floor($pos->x), (int) floor($pos->z));
			if($biomeId === Biome::DESERT || $biomeId === Biome::DESERT_HILLS){
				return $groundId === Block::SAND;
			}
		}

		if(self::isHostileEntityId($entityId)){
			return self::isNaturalHostileSpawnGround($groundId);
		}

		return $groundId === Block::GRASS;
	}

	public static function canSpawnSelectedLandEntityAt(Level $level, Position $pos, int $entityId, bool $inVillage = false) : bool{
		if($entityId <= 0 || !self::canSpawnLandEntityOn($level, $pos, $entityId, $inVillage)){
			return false;
		}

		if($level->getDimension() === Level::DIMENSION_NETHER){
			if(self::isNetherFortressPosition($level, $pos)){
				return in_array($entityId, self::getNetherFortressLandPool(), true);
			}

			return in_array($entityId, self::getNetherLandPool(), true);
		}

		if($level->getDimension() === Level::DIMENSION_END){
			return self::canSpawnHostileAt($level, $pos) && in_array($entityId, self::getHostilePool(Biome::END), true);
		}

		if($level->getDimension() !== Level::DIMENSION_NORMAL){
			return false;
		}

		$biomeId = (int) $level->getBiomeId((int) floor($pos->x), (int) floor($pos->z));
		if($biomeId === Biome::VILLAGE){
			$inVillage = true;
		}

		if($inVillage && !self::isDarkOverworldPosition($level, $pos)){
			return in_array($entityId, self::getPassivePool($biomeId, true), true);
		}

		if(self::isHostileEntityId($entityId)){
			$hostilePoolEntityId = $entityId === ZombieVillager::NETWORK_ID ? Zombie::NETWORK_ID : $entityId;
			if($entityId === Husk::NETWORK_ID && self::canSpawnDaytimeDesertHuskAt($level, $pos)){
				return true;
			}
			return self::canSpawnHostileAt($level, $pos) && in_array($hostilePoolEntityId, self::getHostilePoolForPosition($biomeId, $pos), true);
		}

		return self::canSpawnPassiveAt($level, $pos) && in_array($entityId, self::getPassivePool($biomeId, false), true);
	}

	public static function canSpawnSelectedAirEntityAt(Level $level, Position $pos, int $entityId) : bool{
		if($level->getDimension() !== Level::DIMENSION_NETHER || !in_array($entityId, self::getNetherAirPool(), true)){
			return false;
		}

		if($entityId === Ghast::NETWORK_ID && !self::hasVerticalAirColumn($level, $pos, self::GHAST_SPAWN_VERTICAL_AIR)){
			return false;
		}

		return !self::isEntityDensityLimitReached($level, $pos, $entityId);
	}

	public static function isEntityDensityLimitReached(Level $level, Position $pos, int $entityId) : bool{
		$limit = self::getEntityDensityLimit($entityId);
		if($limit <= 0){
			return false;
		}

		$radius = self::getEntityDensityRadius($entityId);
		if($radius <= 0){
			return false;
		}

		return self::countNearbyEntities($level, $pos, $entityId, $radius) >= $limit;
	}

	public static function getEntityDensityLimit(int $entityId) : int{
		return $entityId === Ghast::NETWORK_ID ? self::GHAST_LOCAL_DENSITY_LIMIT : 0;
	}

	public static function getEntityDensityRadius(int $entityId) : int{
		return $entityId === Ghast::NETWORK_ID ? self::GHAST_LOCAL_DENSITY_RADIUS : 0;
	}

	private static function isNaturalHostileSpawnGround(int $groundId) : bool{
		$ground = Block::get($groundId);
		return $ground->isNormalBlock();
	}

	public static function isHostileEntityId(int $entityId) : bool{
		switch($entityId){
			case Zombie::NETWORK_ID:
			case ZombieVillager::NETWORK_ID:
			case Husk::NETWORK_ID:
			case Skeleton::NETWORK_ID:
			case Stray::NETWORK_ID:
			case Creeper::NETWORK_ID:
			case Spider::NETWORK_ID:
			case PigZombie::NETWORK_ID:
			case Slime::NETWORK_ID:
			case Enderman::NETWORK_ID:
			case Silverfish::NETWORK_ID:
			case CaveSpider::NETWORK_ID:
			case Ghast::NETWORK_ID:
			case LavaSlime::NETWORK_ID:
			case Blaze::NETWORK_ID:
			case Witch::NETWORK_ID:
				return true;
			default:
				return false;
		}
	}

	public static function isWolfBiome(int $biomeId) : bool{
		switch($biomeId){
			case Biome::FOREST:
			case Biome::FOREST_HILLS:
			case Biome::BIRCH_FOREST:
			case Biome::BIRCH_FOREST_HILLS:
			case Biome::PINK_FOREST:
			case Biome::GOLDEN_FOREST:
			case Biome::TAIGA:
			case Biome::TAIGA_HILLS:
			case Biome::COLD_TAIGA:
			case Biome::COLD_TAIGA_HILLS:
			case Biome::MEGA_TAIGA:
			case Biome::MEGA_TAIGA_HILLS:
				return true;
			default:
				return false;
		}
	}

	public static function isWaterBiome(int $biomeId) : bool{
		switch($biomeId){
			case Biome::OCEAN:
			case Biome::DEEP_OCEAN:
			case Biome::FROZEN_OCEAN:
			case Biome::RIVER:
			case Biome::FROZEN_RIVER:
				return true;
			default:
				return false;
		}
	}

	public static function isRabbitBiome(int $biomeId) : bool{
		switch($biomeId){
			case Biome::DESERT:
			case Biome::DESERT_HILLS:
			case Biome::ICE_PLAINS:
			case Biome::ICE_MOUNTAINS:
			case Biome::TAIGA:
			case Biome::TAIGA_HILLS:
			case Biome::COLD_TAIGA:
			case Biome::COLD_TAIGA_HILLS:
			case Biome::MEGA_TAIGA:
			case Biome::MEGA_TAIGA_HILLS:
			case Biome::SAVANNA:
			case Biome::SAVANNA_PLATEAU:
				return true;
			default:
				return false;
		}
	}

	private static function isSnowyPassiveBiome(int $biomeId) : bool{
		switch($biomeId){
			case Biome::ICE_PLAINS:
			case Biome::ICE_MOUNTAINS:
			case Biome::FROZEN_OCEAN:
			case Biome::FROZEN_RIVER:
			case Biome::COLD_TAIGA:
			case Biome::COLD_TAIGA_HILLS:
				return true;
			default:
				return false;
		}
	}

	public static function isDesertHuskBiome(int $biomeId) : bool{
		switch($biomeId){
			case Biome::DESERT:
			case Biome::DESERT_HILLS:
				return true;
			default:
				return false;
		}
	}

	public static function isFrozenStrayBiome(int $biomeId) : bool{
		switch($biomeId){
			case Biome::ICE_PLAINS:
			case Biome::ICE_MOUNTAINS:
			case Biome::COLD_TAIGA:
			case Biome::COLD_TAIGA_HILLS:
				return true;
			default:
				return false;
		}
	}

	private static function isMushroomBiome(int $biomeId) : bool{
		return $biomeId === Biome::MUSHROOM_ISLAND || $biomeId === Biome::MUSHROOM_ISLAND_SHORE;
	}

	private static function canUseOverworldCaveAmbientPool(int $biomeId) : bool{
		if($biomeId === Biome::END || $biomeId === Biome::HELL){
			return false;
		}

		return !self::isMushroomBiome($biomeId);
	}

	private static function shouldSelectDaytimeDesertHusk(Level $level, Position $pos, int $roll = null) : bool{
		if(!self::canSpawnDaytimeDesertHuskAt($level, $pos)){
			return false;
		}
		if($roll === null){
			$roll = mt_rand(0, self::DAYTIME_DESERT_HUSK_CHANCE - 1);
		}

		return abs($roll) % self::DAYTIME_DESERT_HUSK_CHANCE === 0;
	}

	private static function isNetherFortressPosition(Level $level, Position $pos) : bool{
		$belowId = $level->getBlockIdAt((int) floor($pos->x), (int) floor($pos->y) - 1, (int) floor($pos->z));
		switch($belowId){
			case Block::NETHER_BRICKS:
			case Block::NETHER_BRICK_FENCE:
			case Block::NETHER_BRICKS_STAIRS:
				return true;
			default:
				return false;
		}
	}

	private static function isDarkOverworldPosition(Level $level, Position $pos) : bool{
		if($level->getDimension() !== Level::DIMENSION_NORMAL){
			return false;
		}

		$x = (int) floor($pos->x);
		$y = (int) floor($pos->y);
		$z = (int) floor($pos->z);
		if(self::isNight($level)){
			return $level->getBlockLightAt($x, $y, $z) <= self::HOSTILE_SPAWN_MAX_LIGHT;
		}

		return $level->getFullLightAt($x, $y, $z) <= self::HOSTILE_SPAWN_MAX_LIGHT;
	}

	private static function getHostilePoolForPosition(int $biomeId, Position $pos) : array{
		$pool = self::getHostilePool($biomeId);
		if(self::isFrozenStrayBiome($biomeId)){
			if(!self::canUseFrozenStraySpawnHeight($biomeId, $pos)){
				$pool = self::removeEntityIdFromPool($pool, Stray::NETWORK_ID);
			}
			return $pool;
		}
		if(self::isDesertHuskBiome($biomeId)){
			return $pool;
		}
		if($biomeId !== Biome::SWAMP || self::isSwampSlimeHeight($pos)){
			return $pool;
		}

		$filteredPool = [];
		foreach($pool as $entityId){
			if($entityId !== Slime::NETWORK_ID){
				$filteredPool[] = $entityId;
			}
		}

		return $filteredPool;
	}

	private static function canUseFrozenStraySpawnHeight(int $biomeId, Position $pos) : bool{
		if($biomeId !== Biome::FROZEN_OCEAN){
			return true;
		}

		$y = (int) floor($pos->y);
		return $y >= 60 && $y <= 66;
	}

	private static function removeEntityIdFromPool(array $pool, int $removeEntityId) : array{
		$filteredPool = [];
		foreach($pool as $entityId){
			if($entityId !== $removeEntityId){
				$filteredPool[] = $entityId;
			}
		}

		return $filteredPool;
	}

	private static function getPassiveHerdSelectionPool(int $biomeId) : array{
		$pool = [];
		foreach(self::getPassivePool($biomeId, false) as $entityId){
			if(self::isPassiveHerdEntityId($entityId)){
				$pool[] = $entityId;
			}
		}

		return $pool;
	}

	private static function isPassiveHerdEntityId(int $entityId) : bool{
		switch($entityId){
			case Cow::NETWORK_ID:
			case Pig::NETWORK_ID:
			case Sheep::NETWORK_ID:
			case Chicken::NETWORK_ID:
			case Rabbit::NETWORK_ID:
			case Mooshroom::NETWORK_ID:
			case Wolf::NETWORK_ID:
			case Ocelot::NETWORK_ID:
			case Horse::NETWORK_ID:
				return true;
			default:
				return false;
		}
	}

	private static function canMixSmallPassiveWith(int $entityId) : bool{
		switch($entityId){
			case Cow::NETWORK_ID:
			case Pig::NETWORK_ID:
			case Sheep::NETWORK_ID:
				return true;
			default:
				return false;
		}
	}

	private static function hasVerticalAirColumn(Level $level, Position $pos, int $height) : bool{
		$x = (int) floor($pos->x);
		$y = (int) floor($pos->y);
		$z = (int) floor($pos->z);
		for($dy = 0; $dy < $height; ++$dy){
			if($level->getBlockIdAt($x, $y + $dy, $z) !== Block::AIR){
				return false;
			}
		}

		return true;
	}

	private static function countNearbyEntities(Level $level, Position $pos, int $entityId, int $radius) : int{
		$radiusSquared = $radius * $radius;
		$count = 0;
		foreach($level->getEntities() as $entity){
			if(!($entity instanceof Entity) || (int) $entity::NETWORK_ID !== $entityId){
				continue;
			}

			$dx = $entity->x - $pos->x;
			$dy = $entity->y - $pos->y;
			$dz = $entity->z - $pos->z;
			if(($dx * $dx) + ($dy * $dy) + ($dz * $dz) < $radiusSquared){
				++$count;
			}
		}

		return $count;
	}

	private static function selectInRange(int $min, int $max, int $roll = null) : int{
		if($max <= $min){
			return $min;
		}

		if($roll === null){
			$roll = mt_rand(0, $max - $min);
		}

		return $min + abs($roll) % ($max - $min + 1);
	}

	private static function selectFromPool(array $pool, int $roll = null) : int{
		$count = count($pool);
		if($count === 0){
			return 0;
		}

		if($roll === null){
			$roll = mt_rand(0, $count - 1);
		}

		return (int) $pool[abs($roll) % $count];
	}
}
