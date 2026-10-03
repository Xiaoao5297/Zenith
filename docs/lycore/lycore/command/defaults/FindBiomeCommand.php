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

namespace lycore\command\defaults;

use lycore\command\Command;
use lycore\command\CommandSender;
use lycore\event\TranslationContainer;
use lycore\level\generator\Generator;
use lycore\level\generator\biome\BiomeLocator;
use lycore\level\generator\hell\Nether;
use lycore\level\generator\hell\populator\NetherFortressPopulator;
use lycore\level\generator\normal\Normal;
use lycore\level\generator\normal\populator\JungleTemple;
use lycore\level\generator\normal\populator\PillagerOutpost as PillagerOutpostPopulator;
use lycore\level\generator\normal\populator\RuinedPortal;
use lycore\level\generator\normal\populator\Stronghold as StrongholdPopulator;
use lycore\level\generator\normal\populator\Temple;
use lycore\level\generator\normal\populator\VillagePopulator;
use lycore\level\generator\normal\populator\WoodlandMansion as WoodlandMansionPopulator;
use lycore\level\Level;
use lycore\math\Vector3;
use lycore\Player;
use lycore\scheduler\CallbackTask;
use lycore\utils\Random;
use lycore\utils\TextFormat;

class FindBiomeCommand extends VanillaCommand{

	const DEFAULT_REGION_RADIUS = 24;
	const DEFAULT_BIOME_RADIUS = 4096;
	const TELEPORT_GENERATION_RETRY_INTERVAL = 20;
	const TELEPORT_GENERATION_MAX_ATTEMPTS = 300;
	const TELEPORT_PRELOAD_Y = 96;

	public function __construct($name){
		parent::__construct(
			$name,
			"Teleport to a biome or generated target",
			"/biome tp <biomeId|name|village|desertvillage|sandvillage|hell|stronghold|jungletemple|deserttemple|outpost|pillageroutpost|desertoutpost|portal|mansion|woodlandmansion> [radius]"
		);
		$this->setPermission("pocketmine.command.biome");
	}

	public static function isMansionTeleportTarget(string $type) : bool{
		return self::isTargetType($type, ["mansion", "woodlandmansion", "woodland_mansion", "府邸", "林地府邸", "林地宅邸"]);
	}

	private static function normalizeTargetType(string $type) : string{
		return strtolower(str_replace([" ", "-"], "_", trim($type)));
	}

	private static function isTargetType(string $type, array $aliases) : bool{
		return in_array(self::normalizeTargetType($type), $aliases, true);
	}

	private function isOverworldStructureLevel(Level $level) : bool{
		return $level->getDimension() === Level::DIMENSION_NORMAL && Generator::getGenerator($level->getProvider()->getGenerator()) === Normal::class;
	}

	private function isNetherStructureLevel(Level $level) : bool{
		return $level->getDimension() === Level::DIMENSION_NETHER;
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return true;
		}

		return $this->executeBiomeTeleport($sender, $args);
	}

	public function executeBiomeTeleport(CommandSender $sender, array $args, $usageMessage = null){
		if(!($sender instanceof Player)){
			$sender->sendMessage(TextFormat::RED . "只能在游戏内使用此命令。");
			return false;
		}
		if(count($args) > 2 || count($args) < 1){
			$sender->sendMessage(new TranslationContainer("commands.generic.usage", [$usageMessage === null ? $this->usageMessage : $usageMessage]));
			return true;
		}

		$type = self::normalizeTargetType((string) $args[0]);
		if(self::isTargetType($type, ["village", "村庄"])){
			$maxRadius = isset($args[1]) ? max(1, min(128, (int) $args[1])) : self::DEFAULT_REGION_RADIUS;
			return $this->locateVillage($sender, $maxRadius, false);
		}
		if(self::isTargetType($type, ["desertvillage", "desert_village", "sandvillage", "沙漠村庄"])){
			$maxRadius = isset($args[1]) ? max(1, min(128, (int) $args[1])) : self::DEFAULT_REGION_RADIUS;
			return $this->locateVillage($sender, $maxRadius, true);
		}
		if(self::isTargetType($type, ["hell", "fortress", "netherfortress", "nether_fortress", "地狱堡垒", "下界堡垒"])){
			$maxRadius = isset($args[1]) ? max(1, min(128, (int) $args[1])) : self::DEFAULT_REGION_RADIUS;
			return $this->locateFortress($sender, $maxRadius);
		}
		if(self::isTargetType($type, ["stronghold", "strong_hold", "要塞"])){
			$maxRadius = isset($args[1]) ? max(1, min(128, (int) $args[1])) : self::DEFAULT_REGION_RADIUS;
			return $this->locateStronghold($sender, $maxRadius);
		}
		if(self::isTargetType($type, ["jungletemple", "jungle_temple", "丛林神庙", "丛林神殿"])){
			$maxRadius = isset($args[1]) ? max(1, min(128, (int) $args[1])) : self::DEFAULT_REGION_RADIUS;
			return $this->locateJungleTemple($sender, $maxRadius);
		}
		if(self::isTargetType($type, ["deserttemple", "desert_temple", "desertpyramid", "desert_pyramid", "沙漠神庙", "沙漠神殿", "沙漠金字塔"])){
			$maxRadius = isset($args[1]) ? max(1, min(128, (int) $args[1])) : self::DEFAULT_REGION_RADIUS;
			return $this->locateDesertTemple($sender, $maxRadius);
		}
		if(self::isTargetType($type, ["outpost", "pillageroutpost", "pillager_outpost", "desertoutpost", "desert_outpost", "前哨站", "掠夺者前哨站", "沙漠前哨站", "沙漠掠夺者前哨站"])){
			$maxRadius = isset($args[1]) ? max(1, min(128, (int) $args[1])) : self::DEFAULT_REGION_RADIUS;
			return $this->locatePillagerOutpost($sender, $maxRadius, self::isTargetType($type, ["desertoutpost", "desert_outpost", "沙漠前哨站", "沙漠掠夺者前哨站"]));
		}
		if(self::isTargetType($type, ["portal", "ruinedportal", "ruined_portal", "废弃传送门", "破损传送门", "废墟传送门"])){
			$maxRadius = isset($args[1]) ? max(1, min(128, (int) $args[1])) : self::DEFAULT_REGION_RADIUS;
			return $this->locateRuinedPortal($sender, $maxRadius);
		}
		if(self::isMansionTeleportTarget($type)){
			$maxRadius = isset($args[1]) ? max(1, min(128, (int) $args[1])) : self::DEFAULT_REGION_RADIUS;
			return $this->locateMansion($sender, $maxRadius);
		}

		$level = $sender->getLevel();
		$radius = isset($args[1]) ? max(16, min(30000, (int) $args[1])) : self::DEFAULT_BIOME_RADIUS;
		$locator = new BiomeLocator((int) $level->getSeed());
		$result = $locator->findNearestByName($args[0], (int) floor($sender->getX()), (int) floor($sender->getZ()), $radius, 16);
		if($result === null){
			$sender->sendMessage(TextFormat::RED . "没有在半径 {$radius} 方块内找到匹配的生物群系。");
			return true;
		}

		$x = (int) $result["x"];
		$z = (int) $result["z"];
		$y = 96;
		$placement = $this->prepareBiomeTeleport($level, $x, $z, $y);
		return $this->teleportWhenReady($sender, $level, $placement, function() use ($level, $x, $z, $y){
			return $this->prepareBiomeTeleport($level, $x, $z, $y);
		}, "biome " . $result["biome"], "生物群系 " . $result["biome"], 0, $this->targetPreviewPosition($x, $y, $z));
	}

	private function locateVillage(Player $sender, int $maxRadius, bool $desertOnly = false) : bool{
		$level = $sender->getLevel();
		if(!$this->isOverworldStructureLevel($level)){
			$sender->sendMessage(TextFormat::RED . "当前世界不是普通世界生成器，无法查找村庄。");
			return true;
		}

		$playerChunkX = ((int) $sender->x) >> 4;
		$playerChunkZ = ((int) $sender->z) >> 4;
		$originRegionX = VillagePopulator::regionCoord($playerChunkX, VillagePopulator::REGION_SIZE);
		$originRegionZ = VillagePopulator::regionCoord($playerChunkZ, VillagePopulator::REGION_SIZE);
		$found = null;

		for($radius = 0; $radius <= $maxRadius && $found === null; ++$radius){
			for($regionX = $originRegionX - $radius; $regionX <= $originRegionX + $radius && $found === null; ++$regionX){
				for($regionZ = $originRegionZ - $radius; $regionZ <= $originRegionZ + $radius; ++$regionZ){
					if($radius !== 0 && abs($regionX - $originRegionX) !== $radius && abs($regionZ - $originRegionZ) !== $radius){
						continue;
					}
					$candidate = VillagePopulator::findRegionVillageCandidate($level->getSeed(), $regionX, $regionZ);
					if($candidate !== null){
						if($desertOnly && (!isset($candidate["biomeId"]) || (int) $candidate["biomeId"] !== \lycore\level\generator\biome\Biome::DESERT)){
							continue;
						}
						$found = $candidate;
						break 2;
					}
				}
			}
		}

		if($found === null){
			$sender->sendMessage(TextFormat::RED . "没有在 {$maxRadius} 个区域半径内找到" . ($desertOnly ? "沙漠" : "") . "村庄。");
			return true;
		}

		$placement = $this->prepareLocatedVillage($level, (int) $found["chunkX"], (int) $found["chunkZ"]);
		return $this->teleportWhenReady($sender, $level, $placement, function() use ($level, $found){
			return $this->prepareLocatedVillage($level, (int) $found["chunkX"], (int) $found["chunkZ"]);
		}, ($desertOnly ? "desert village" : "village"), ($desertOnly ? "沙漠村庄" : "村庄"), 0, $this->chunkPreviewPosition((int) $found["chunkX"], (int) $found["chunkZ"]));
	}

	private function locateFortress(Player $sender, int $maxRadius) : bool{
		$level = $sender->getLevel();
		if(!$this->isNetherStructureLevel($level)){
			$sender->sendMessage(TextFormat::RED . "当前世界不是地狱生成器，无法查找地狱堡垒。");
			return true;
		}

		$playerChunkX = ((int) $sender->x) >> 4;
		$playerChunkZ = ((int) $sender->z) >> 4;
		$originRegionX = NetherFortressPopulator::floorDiv($playerChunkX, NetherFortressPopulator::REGION_SIZE);
		$originRegionZ = NetherFortressPopulator::floorDiv($playerChunkZ, NetherFortressPopulator::REGION_SIZE);
		$found = null;

		for($radius = 0; $radius <= $maxRadius && $found === null; ++$radius){
			for($regionX = $originRegionX - $radius; $regionX <= $originRegionX + $radius && $found === null; ++$regionX){
				for($regionZ = $originRegionZ - $radius; $regionZ <= $originRegionZ + $radius; ++$regionZ){
					if($radius !== 0 && abs($regionX - $originRegionX) !== $radius && abs($regionZ - $originRegionZ) !== $radius){
						continue;
					}
					$candidate = NetherFortressPopulator::getFortressCandidate($level->getSeed(), $regionX, $regionZ);
					if($candidate !== null){
						$found = $candidate;
						break 2;
					}
				}
			}
		}

		if($found === null){
			$sender->sendMessage(TextFormat::RED . "没有在 {$maxRadius} 个区域半径内找到地狱堡垒。");
			return true;
		}

		$placement = $this->prepareLocatedFortress($level, (int) $found["chunkX"], (int) $found["chunkZ"]);
		return $this->teleportWhenReady($sender, $level, $placement, function() use ($level, $found){
			return $this->prepareLocatedFortress($level, (int) $found["chunkX"], (int) $found["chunkZ"]);
		}, "hell fortress", "地狱堡垒", 0, $this->chunkPreviewPosition((int) $found["chunkX"], (int) $found["chunkZ"]));
	}

	private function locateStronghold(Player $sender, int $maxRadius) : bool{
		$level = $sender->getLevel();
		if(!$this->isOverworldStructureLevel($level)){
			$sender->sendMessage(TextFormat::RED . "当前世界不是普通世界生成器，无法查找要塞。");
			return true;
		}

		$playerChunkX = ((int) $sender->x) >> 4;
		$playerChunkZ = ((int) $sender->z) >> 4;
		$originRegionX = $this->floorDiv($playerChunkX, StrongholdPopulator::MAX_DISTANCE);
		$originRegionZ = $this->floorDiv($playerChunkZ, StrongholdPopulator::MAX_DISTANCE);
		$found = null;

		for($radius = 0; $radius <= $maxRadius && $found === null; ++$radius){
			for($regionX = $originRegionX - $radius; $regionX <= $originRegionX + $radius && $found === null; ++$regionX){
				for($regionZ = $originRegionZ - $radius; $regionZ <= $originRegionZ + $radius; ++$regionZ){
					if($radius !== 0 && abs($regionX - $originRegionX) !== $radius && abs($regionZ - $originRegionZ) !== $radius){
						continue;
					}
					$found = StrongholdPopulator::findCandidateInRegion($level->getSeed(), $regionX, $regionZ);
					break 2;
				}
			}
		}

		if($found === null){
			$sender->sendMessage(TextFormat::RED . "没有在 {$maxRadius} 个区域半径内找到要塞。");
			return true;
		}

		$placement = $this->prepareLocatedStronghold($level, (int) $found["chunkX"], (int) $found["chunkZ"]);
		return $this->teleportWhenReady($sender, $level, $placement, function() use ($level, $found){
			return $this->prepareLocatedStronghold($level, (int) $found["chunkX"], (int) $found["chunkZ"]);
		}, "stronghold", "要塞", 0, $this->chunkPreviewPosition((int) $found["chunkX"], (int) $found["chunkZ"]));
	}

	private function locateJungleTemple(Player $sender, int $maxRadius) : bool{
		$level = $sender->getLevel();
		if(!$this->isOverworldStructureLevel($level)){
			$sender->sendMessage(TextFormat::RED . "当前世界不是普通世界生成器，无法查找丛林神庙。");
			return true;
		}

		$locator = new BiomeLocator((int) $level->getSeed());
		$playerChunkX = ((int) $sender->x) >> 4;
		$playerChunkZ = ((int) $sender->z) >> 4;
		$originRegionX = JungleTemple::floorDiv($playerChunkX, JungleTemple::REGION_SIZE);
		$originRegionZ = JungleTemple::floorDiv($playerChunkZ, JungleTemple::REGION_SIZE);
		$found = null;

		for($radius = 0; $radius <= $maxRadius && $found === null; ++$radius){
			for($regionX = $originRegionX - $radius; $regionX <= $originRegionX + $radius && $found === null; ++$regionX){
				for($regionZ = $originRegionZ - $radius; $regionZ <= $originRegionZ + $radius; ++$regionZ){
					if($radius !== 0 && abs($regionX - $originRegionX) !== $radius && abs($regionZ - $originRegionZ) !== $radius){
						continue;
					}
					$candidate = JungleTemple::findRegionJungleTempleCandidate($level->getSeed(), $regionX, $regionZ);
					$sampleX = ($candidate["chunkX"] << 4) + 7;
					$sampleZ = ($candidate["chunkZ"] << 4) + 7;
					if(JungleTemple::isJungleTempleBiome($locator->getBiomeIdAt($sampleX, $sampleZ))){
						$found = $candidate;
						break 2;
					}
				}
			}
		}

		if($found === null){
			$sender->sendMessage(TextFormat::RED . "没有在 {$maxRadius} 个区域半径内找到丛林神庙。");
			return true;
		}

		$placement = $this->prepareLocatedJungleTemple($level, (int) $found["chunkX"], (int) $found["chunkZ"]);
		return $this->teleportWhenReady($sender, $level, $placement, function() use ($level, $found){
			return $this->prepareLocatedJungleTemple($level, (int) $found["chunkX"], (int) $found["chunkZ"]);
		}, "jungle temple", "丛林神庙", 0, $this->chunkPreviewPosition((int) $found["chunkX"], (int) $found["chunkZ"]));
	}

	private function locateDesertTemple(Player $sender, int $maxRadius) : bool{
		$level = $sender->getLevel();
		if(!$this->isOverworldStructureLevel($level)){
			$sender->sendMessage(TextFormat::RED . "当前世界不是普通世界生成器，无法查找沙漠神庙。");
			return true;
		}

		$locator = new BiomeLocator((int) $level->getSeed());
		$playerChunkX = ((int) $sender->x) >> 4;
		$playerChunkZ = ((int) $sender->z) >> 4;
		$originRegionX = Temple::floorDiv($playerChunkX, Temple::REGION_SIZE);
		$originRegionZ = Temple::floorDiv($playerChunkZ, Temple::REGION_SIZE);
		$found = null;

		for($radius = 0; $radius <= $maxRadius && $found === null; ++$radius){
			for($regionX = $originRegionX - $radius; $regionX <= $originRegionX + $radius && $found === null; ++$regionX){
				for($regionZ = $originRegionZ - $radius; $regionZ <= $originRegionZ + $radius; ++$regionZ){
					if($radius !== 0 && abs($regionX - $originRegionX) !== $radius && abs($regionZ - $originRegionZ) !== $radius){
						continue;
					}
					$candidate = Temple::findRegionTempleCandidate($level->getSeed(), $regionX, $regionZ);
					$origin = Temple::getTempleOrigin((int) $level->getSeed(), (int) $candidate["chunkX"], (int) $candidate["chunkZ"]);
					if(Temple::isDesertTempleBiome($locator->getBiomeIdAt((int) $origin["x"], (int) $origin["z"]))){
						$found = $candidate;
						break 2;
					}
				}
			}
		}

		if($found === null){
			$sender->sendMessage(TextFormat::RED . "没有在 {$maxRadius} 个区域半径内找到沙漠神庙。");
			return true;
		}

		$placement = $this->prepareLocatedDesertTemple($level, (int) $found["chunkX"], (int) $found["chunkZ"]);
		return $this->teleportWhenReady($sender, $level, $placement, function() use ($level, $found){
			return $this->prepareLocatedDesertTemple($level, (int) $found["chunkX"], (int) $found["chunkZ"]);
		}, "desert temple", "沙漠神庙", 0, $this->chunkPreviewPosition((int) $found["chunkX"], (int) $found["chunkZ"]));
	}

	private function locatePillagerOutpost(Player $sender, int $maxRadius, bool $desertOnly = false) : bool{
		$level = $sender->getLevel();
		if(!$this->isOverworldStructureLevel($level)){
			$sender->sendMessage(TextFormat::RED . "当前世界不是普通世界生成器，无法查找掠夺者前哨站。");
			return true;
		}

		$locator = new BiomeLocator((int) $level->getSeed());
		$playerChunkX = ((int) $sender->x) >> 4;
		$playerChunkZ = ((int) $sender->z) >> 4;
		$originRegionX = PillagerOutpostPopulator::regionCoord($playerChunkX, PillagerOutpostPopulator::REGION_SIZE);
		$originRegionZ = PillagerOutpostPopulator::regionCoord($playerChunkZ, PillagerOutpostPopulator::REGION_SIZE);
		$found = null;

		for($radius = 0; $radius <= $maxRadius && $found === null; ++$radius){
			for($regionX = $originRegionX - $radius; $regionX <= $originRegionX + $radius && $found === null; ++$regionX){
				for($regionZ = $originRegionZ - $radius; $regionZ <= $originRegionZ + $radius; ++$regionZ){
					if($radius !== 0 && abs($regionX - $originRegionX) !== $radius && abs($regionZ - $originRegionZ) !== $radius){
						continue;
					}
					$candidate = PillagerOutpostPopulator::findRegionOutpostCandidate($level->getSeed(), $regionX, $regionZ);
					$sampleX = ($candidate["chunkX"] << 4) + 7;
					$sampleZ = ($candidate["chunkZ"] << 4) + 7;
					$biomeId = $locator->getBiomeIdAt($sampleX, $sampleZ);
					if($desertOnly && (int) $biomeId !== \lycore\level\generator\biome\Biome::DESERT){
						continue;
					}
					if(PillagerOutpostPopulator::isValidOutpostBiome($biomeId)){
						$found = $candidate;
						break 2;
					}
				}
			}
		}

		if($found === null){
			$sender->sendMessage(TextFormat::RED . "没有在 {$maxRadius} 个区域半径内找到" . ($desertOnly ? "沙漠" : "") . "掠夺者前哨站。");
			return true;
		}

		$placement = $this->prepareLocatedPillagerOutpost($level, (int) $found["chunkX"], (int) $found["chunkZ"]);
		return $this->teleportWhenReady($sender, $level, $placement, function() use ($level, $found){
			return $this->prepareLocatedPillagerOutpost($level, (int) $found["chunkX"], (int) $found["chunkZ"]);
		}, ($desertOnly ? "desert pillager outpost" : "pillager outpost"), ($desertOnly ? "沙漠掠夺者前哨站" : "掠夺者前哨站"), 0, $this->chunkPreviewPosition((int) $found["chunkX"], (int) $found["chunkZ"]));
	}

	private function locateRuinedPortal(Player $sender, int $maxRadius) : bool{
		$level = $sender->getLevel();
		if(!$this->isOverworldStructureLevel($level)){
			$sender->sendMessage(TextFormat::RED . "当前世界不是普通世界生成器，无法查找废弃传送门。");
			return true;
		}

		$locator = new BiomeLocator((int) $level->getSeed());
		$playerChunkX = ((int) $sender->x) >> 4;
		$playerChunkZ = ((int) $sender->z) >> 4;
		$originRegionX = RuinedPortal::regionCoord($playerChunkX, RuinedPortal::REGION_SIZE);
		$originRegionZ = RuinedPortal::regionCoord($playerChunkZ, RuinedPortal::REGION_SIZE);
		$found = null;

		for($radius = 0; $radius <= $maxRadius && $found === null; ++$radius){
			for($regionX = $originRegionX - $radius; $regionX <= $originRegionX + $radius && $found === null; ++$regionX){
				for($regionZ = $originRegionZ - $radius; $regionZ <= $originRegionZ + $radius; ++$regionZ){
					if($radius !== 0 && abs($regionX - $originRegionX) !== $radius && abs($regionZ - $originRegionZ) !== $radius){
						continue;
					}
					$candidate = RuinedPortal::findRegionRuinedPortalCandidate($level->getSeed(), $regionX, $regionZ);
					$sampleX = ($candidate["chunkX"] << 4) + 7;
					$sampleZ = ($candidate["chunkZ"] << 4) + 7;
					if(RuinedPortal::isValidRuinedPortalBiome($locator->getBiomeIdAt($sampleX, $sampleZ))){
						$found = $candidate;
						break 2;
					}
				}
			}
		}

		if($found === null){
			$sender->sendMessage(TextFormat::RED . "没有在 {$maxRadius} 个区域半径内找到废弃传送门。");
			return true;
		}

		$placement = $this->prepareLocatedRuinedPortal($level, (int) $found["chunkX"], (int) $found["chunkZ"]);
		return $this->teleportWhenReady($sender, $level, $placement, function() use ($level, $found){
			return $this->prepareLocatedRuinedPortal($level, (int) $found["chunkX"], (int) $found["chunkZ"]);
		}, "ruined portal", "废弃传送门", 0, $this->chunkPreviewPosition((int) $found["chunkX"], (int) $found["chunkZ"]));
	}

	private function locateMansion(Player $sender, int $maxRadius) : bool{
		$level = $sender->getLevel();
		if(!$this->isOverworldStructureLevel($level)){
			$sender->sendMessage(TextFormat::RED . "当前世界不是普通世界生成器，无法查找林地府邸。");
			return true;
		}

		$locator = new BiomeLocator((int) $level->getSeed());
		$playerChunkX = ((int) $sender->x) >> 4;
		$playerChunkZ = ((int) $sender->z) >> 4;
		$originRegionX = WoodlandMansionPopulator::regionCoord($playerChunkX, WoodlandMansionPopulator::REGION_SIZE);
		$originRegionZ = WoodlandMansionPopulator::regionCoord($playerChunkZ, WoodlandMansionPopulator::REGION_SIZE);
		$found = null;

		for($radius = 0; $radius <= $maxRadius && $found === null; ++$radius){
			for($regionX = $originRegionX - $radius; $regionX <= $originRegionX + $radius && $found === null; ++$regionX){
				for($regionZ = $originRegionZ - $radius; $regionZ <= $originRegionZ + $radius; ++$regionZ){
					if($radius !== 0 && abs($regionX - $originRegionX) !== $radius && abs($regionZ - $originRegionZ) !== $radius){
						continue;
					}
					$candidate = WoodlandMansionPopulator::findRegionMansionCandidate($level->getSeed(), $regionX, $regionZ);
					$sampleX = ($candidate["chunkX"] << 4) + 7;
					$sampleZ = ($candidate["chunkZ"] << 4) + 7;
					if(WoodlandMansionPopulator::isValidMansionBiome($locator->getBiomeIdAt($sampleX, $sampleZ))){
						$found = $candidate;
						break 2;
					}
				}
			}
		}

		if($found === null){
			$sender->sendMessage(TextFormat::RED . "没有在 {$maxRadius} 个区域半径内找到林地府邸。");
			return true;
		}

		$placement = $this->prepareLocatedMansion($level, (int) $found["chunkX"], (int) $found["chunkZ"]);
		return $this->teleportWhenReady($sender, $level, $placement, function() use ($level, $found){
			return $this->prepareLocatedMansion($level, (int) $found["chunkX"], (int) $found["chunkZ"]);
		}, "woodland mansion", "林地府邸");
	}

	private function teleportWhenReady(Player $sender, Level $level, $placement, callable $preparePlacement, string $targetName, string $displayName = null, int $attempt = 0, Vector3 $previewPosition = null) : bool{
		if($placement !== null){
			return $this->teleportToPlacement($sender, $placement, $targetName);
		}

		$displayName = $displayName === null ? $targetName : $displayName;
		if($attempt >= self::TELEPORT_GENERATION_MAX_ATTEMPTS){
			$sender->sendMessage(TextFormat::RED . "{$displayName} chunk generation timed out, please try again later.");
			return true;
		}

		if($attempt === 0 && $previewPosition !== null){
			$this->forcePlayerIntoLoadingArea($sender, $previewPosition);
		}

		if($attempt === 0){
			$sender->sendMessage(TextFormat::YELLOW . "{$displayName}区块还在生成，生成完成后会自动传送。");
		}elseif($attempt % 30 === 0){
			$sender->sendMessage(TextFormat::YELLOW . "{$displayName}区块还在生成，正在等待自动传送。");
		}

		$sender->getServer()->getScheduler()->scheduleDelayedTask(new CallbackTask(function($task) use ($sender, $level, $preparePlacement, $targetName, $displayName, $attempt, $previewPosition){
			if(!$sender->isOnline() || $sender->getLevel() !== $level){
				return;
			}

			$this->teleportWhenReady($sender, $level, $preparePlacement(), $preparePlacement, $targetName, $displayName, $attempt + 1, $previewPosition);
		}), self::TELEPORT_GENERATION_RETRY_INTERVAL);

		return true;
	}

	private function teleportToPlacement(Player $sender, array $placement, string $targetName) : bool{
		$targetX = (int) $placement["targetX"];
		$targetY = (int) $placement["targetY"];
		$targetZ = (int) $placement["targetZ"];
		$sender->teleport(new Vector3($targetX + 0.5, $targetY, $targetZ + 0.5));
		Command::broadcastCommandMessage($sender, "Teleported to {$targetName} at {$targetX}, {$targetY}, {$targetZ}");
		return true;
	}

	private function forcePlayerIntoLoadingArea(Player $sender, Vector3 $position){
		$sender->teleport($position);
	}

	private function chunkPreviewPosition(int $chunkX, int $chunkZ) : Vector3{
		return new Vector3(($chunkX << 4) + 8.5, self::TELEPORT_PRELOAD_Y, ($chunkZ << 4) + 8.5);
	}

	private function targetPreviewPosition(int $x, int $y, int $z) : Vector3{
		return new Vector3($x + 0.5, $y, $z + 0.5);
	}

	private function prepareBiomeTeleport(Level $level, int $x, int $z, int $y){
		$chunkX = $x >> 4;
		$chunkZ = $z >> 4;
		if(!$this->prepareTargetChunk($level, $chunkX, $chunkZ)){
			return null;
		}

		return [
			"targetX" => $x,
			"targetY" => $y,
			"targetZ" => $z,
		];
	}

	private function prepareLocatedFortress(Level $level, int $chunkX, int $chunkZ){
		if(!$this->prepareTargetChunk($level, $chunkX, $chunkZ)){
			return null;
		}

		return [
			"targetX" => ($chunkX << 4) + 9,
			"targetY" => 64,
			"targetZ" => ($chunkZ << 4) + 9,
		];
	}

	private function prepareLocatedJungleTemple(Level $level, int $chunkX, int $chunkZ){
		$footprintChunks = JungleTemple::getJungleTempleFootprintChunks((int) $level->getSeed(), $chunkX, $chunkZ);
		if(!$this->prepareStructureFootprint($level, $footprintChunks, $chunkX, $chunkZ)){
			return null;
		}

		$populator = new JungleTemple();
		$random = new Random(0);
		$random->setSeed((int) $level->getSeed() ^ Level::chunkHash($chunkX, $chunkZ));
		return $populator->placeJungleTempleAtCandidate($level, $chunkX, $chunkZ, $random);
	}

	private function prepareLocatedVillage(Level $level, int $chunkX, int $chunkZ){
		$footprintChunks = VillagePopulator::getVillageFootprintChunks((int) $level->getSeed(), $chunkX, $chunkZ);
		if(!$this->prepareStructureFootprint($level, $footprintChunks, $chunkX, $chunkZ)){
			return null;
		}

		$populator = new VillagePopulator();
		$random = new Random(0);
		$random->setSeed((int) $level->getSeed() ^ Level::chunkHash($chunkX, $chunkZ));
		return $populator->placeVillageAtCandidate($level, $chunkX, $chunkZ, $random);
	}

	private function prepareLocatedDesertTemple(Level $level, int $chunkX, int $chunkZ){
		$footprintChunks = Temple::getTempleFootprintChunks((int) $level->getSeed(), $chunkX, $chunkZ);
		if(!$this->prepareStructureFootprint($level, $footprintChunks, $chunkX, $chunkZ)){
			return null;
		}

		$populator = new Temple();
		$random = new Random(0);
		$random->setSeed((int) $level->getSeed() ^ Level::chunkHash($chunkX, $chunkZ));
		return $populator->placeTempleAtCandidate($level, $chunkX, $chunkZ, $random);
	}

	private function prepareLocatedPillagerOutpost(Level $level, int $chunkX, int $chunkZ){
		$footprintChunks = PillagerOutpostPopulator::getOutpostFootprintChunks((int) $level->getSeed(), $chunkX, $chunkZ);
		if(!$this->prepareStructureFootprint($level, $footprintChunks, $chunkX, $chunkZ)){
			return null;
		}

		$populator = new PillagerOutpostPopulator();
		$random = new Random(0);
		$random->setSeed((int) $level->getSeed() ^ Level::chunkHash($chunkX, $chunkZ));
		return $populator->placeOutpostAtCandidate($level, $chunkX, $chunkZ, $random);
	}

	private function prepareLocatedRuinedPortal(Level $level, int $chunkX, int $chunkZ){
		$footprintChunks = RuinedPortal::getRuinedPortalFootprintChunks((int) $level->getSeed(), $chunkX, $chunkZ);
		if(!$this->prepareStructureFootprint($level, $footprintChunks, $chunkX, $chunkZ)){
			return null;
		}

		$populator = new RuinedPortal();
		$random = new Random(0);
		$random->setSeed((int) $level->getSeed() ^ Level::chunkHash($chunkX, $chunkZ));
		return $populator->placeRuinedPortalAtCandidate($level, $chunkX, $chunkZ, $random);
	}

	private function prepareLocatedMansion(Level $level, int $chunkX, int $chunkZ){
		$footprintChunks = WoodlandMansionPopulator::getMansionFootprintChunks((int) $level->getSeed(), $chunkX, $chunkZ);
		if(!$this->prepareMansionFootprint($level, $footprintChunks, $chunkX, $chunkZ)){
			return null;
		}

		$populator = new WoodlandMansionPopulator();
		$random = new Random(0);
		return $populator->placeMansionAtCandidate($level, $chunkX, $chunkZ, $random, true);
	}

	private function prepareMansionFootprint(Level $level, array $footprintChunks, int $chunkX, int $chunkZ) : bool{
		$allGenerated = true;
		foreach($footprintChunks as $chunkPos){
			$fx = (int) $chunkPos["chunkX"];
			$fz = (int) $chunkPos["chunkZ"];
			$level->loadChunk($fx, $fz, true);
			if(!$level->isChunkGenerated($fx, $fz)){
				if(method_exists($level, "generateChunkSynchronously")){
					if(!$level->generateChunkSynchronously($fx, $fz)){
						return false;
					}
				}else{
					$level->generateChunk($fx, $fz, true);
					$allGenerated = false;
				}
			}
		}

		if(!$allGenerated){
			return false;
		}

		$level->loadChunk($chunkX, $chunkZ, true);
		if(!$level->isChunkGenerated($chunkX, $chunkZ)){
			if(method_exists($level, "generateChunkSynchronously")){
				if(!$level->generateChunkSynchronously($chunkX, $chunkZ)){
					return false;
				}
			}else{
				$level->generateChunk($chunkX, $chunkZ, true);
				return false;
			}
		}

		if(!$level->isChunkPopulated($chunkX, $chunkZ)){
			if(method_exists($level, "populateChunkSynchronously")){
				return $level->populateChunkSynchronously($chunkX, $chunkZ);
			}
			$level->populateChunk($chunkX, $chunkZ, true);
			return false;
		}

		return true;
	}

	private function prepareLocatedStronghold(Level $level, int $chunkX, int $chunkZ){
		$footprintChunks = StrongholdPopulator::getStrongholdFootprintChunks((int) $level->getSeed(), $chunkX, $chunkZ);
		if(!$this->prepareStructureFootprint($level, $footprintChunks, $chunkX, $chunkZ)){
			return null;
		}

		$populator = new StrongholdPopulator();
		return $populator->placeStrongholdAtCandidate($level, $chunkX, $chunkZ);
	}

	private function prepareStructureFootprint(Level $level, array $footprintChunks, int $chunkX, int $chunkZ) : bool{
		$allGenerated = true;
		foreach($footprintChunks as $chunkPos){
			$fx = (int) $chunkPos["chunkX"];
			$fz = (int) $chunkPos["chunkZ"];
			$level->loadChunk($fx, $fz, true);
			if(!$level->isChunkGenerated($fx, $fz)){
				$level->generateChunk($fx, $fz, true);
				$allGenerated = false;
			}
		}

		$level->loadChunk($chunkX, $chunkZ, true);
		if(!$level->isChunkGenerated($chunkX, $chunkZ)){
			$level->generateChunk($chunkX, $chunkZ, true);
			$allGenerated = false;
		}

		if(!$allGenerated){
			return false;
		}

		return true;
	}

	private function prepareTargetChunk(Level $level, int $chunkX, int $chunkZ) : bool{
		$level->loadChunk($chunkX, $chunkZ, true);
		if(!$level->isChunkGenerated($chunkX, $chunkZ)){
			$level->generateChunk($chunkX, $chunkZ, true);
			return false;
		}

		if(!$level->isChunkPopulated($chunkX, $chunkZ)){
			$level->populateChunk($chunkX, $chunkZ, true);
			return false;
		}

		return $level->isChunkGenerated($chunkX, $chunkZ) && $level->isChunkPopulated($chunkX, $chunkZ);
	}

	private function floorDiv(int $value, int $divisor) : int{
		$result = intdiv($value, $divisor);
		if($value < 0 && ($value % $divisor) !== 0){
			--$result;
		}
		return $result;
	}
}
