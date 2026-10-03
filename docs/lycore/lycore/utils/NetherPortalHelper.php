<?php

namespace lycore\utils;

use lycore\block\Block;
use lycore\level\Level;
use lycore\level\Position;

final class NetherPortalHelper{
	const PORTAL_SEARCH_RADIUS = 128;
	const NETHER_PORTAL_SEARCH_RADIUS = 16;
	const DEFAULT_NETHER_SCALE = 8;
	const MAX_PORTAL_SIZE = 23;
	const LEGACY_MIN_HEIGHT = 0;
	const LEGACY_MAX_HEIGHT = 127;
	const NETHER_ROOF_SAFE_Y = 115;

	private function __construct(){
	}

	public static function createPortal(Level $level, Block $target) : bool{
		if($level->getDimension() === Level::DIMENSION_END){
			return false;
		}

		$x = (int) floor($target->x);
		$y = (int) floor($target->y);
		$z = (int) floor($target->z);

		if(self::getBlockId($level, $x, $y, $z) === Block::FIRE){
			return self::createPortalFromInterior($level, $x, $y, $z);
		}

		return self::createPortalAtBase($level, $x, $y, $z);
	}

	private static function createPortalFromInterior(Level $level, int $x, int $y, int $z) : bool{
		$minY = max(self::getMinHeight($level), $y - self::MAX_PORTAL_SIZE);
		for($baseY = $y - 1; $baseY >= $minY; $baseY--){
			if(self::getBlockId($level, $x, $baseY, $z) !== Block::OBSIDIAN){
				continue;
			}
			if(self::createPortalAtBase($level, $x, $baseY, $z)){
				return true;
			}
		}

		return false;
	}

	private static function createPortalAtBase(Level $level, int $x, int $y, int $z) : bool{
		for($i = 1; $i < 4; $i++){
			if(!self::isPortalInteriorReplaceableId(self::getBlockId($level, $x, $y + $i, $z))){
				return false;
			}
		}

		$sizePosX = self::countObsidian($level, $x, $y, $z, 1, 0);
		$sizeNegX = self::countObsidian($level, $x, $y, $z, -1, 0);
		$sizePosZ = self::countObsidian($level, $x, $y, $z, 0, 1);
		$sizeNegZ = self::countObsidian($level, $x, $y, $z, 0, -1);

		$sizeX = $sizePosX + $sizeNegX + 1;
		$sizeZ = $sizePosZ + $sizeNegZ + 1;

		if($sizeX >= 2 and $sizeX <= self::MAX_PORTAL_SIZE){
			return self::createPortalOnX($level, $x, $y, $z, $sizePosX);
		}

		if($sizeZ >= 2 and $sizeZ <= self::MAX_PORTAL_SIZE){
			return self::createPortalOnZ($level, $x, $y, $z, $sizePosZ);
		}

		return false;
	}

	public static function spawnPortal(Position $pos) : bool{
		if(!($pos->getLevel() instanceof Level)){
			return false;
		}

		$level = $pos->getLevel();
		$x = (int) floor($pos->x);
		$y = (int) floor($pos->y);
		$z = (int) floor($pos->z);

		if(!self::preparePortalFootprint($level, $x - 2, $z - 1, $x + 4, $z + 1)){
			return false;
		}

		for($xx = -2; $xx <= 4; $xx++){
			for($yy = -1; $yy <= 5; $yy++){
				for($zz = -1; $zz <= 1; $zz++){
					if(self::getBlockId($level, $x + $xx, $y + $yy, $z + $zz) !== Block::BEDROCK){
						self::setBlockId($level, $x + $xx, $y + $yy, $z + $zz, Block::AIR);
					}
				}
			}
		}

		$x -= 1;
		$z -= 1;

		self::setBlockId($level, $x + 1, $y, $z, Block::OBSIDIAN);
		self::setBlockId($level, $x + 2, $y, $z, Block::OBSIDIAN);
		$z++;
		self::setBlockId($level, $x, $y, $z, Block::OBSIDIAN);
		self::setBlockId($level, $x + 1, $y, $z, Block::OBSIDIAN);
		self::setBlockId($level, $x + 2, $y, $z, Block::OBSIDIAN);
		self::setBlockId($level, $x + 3, $y, $z, Block::OBSIDIAN);
		$z++;
		self::setBlockId($level, $x + 1, $y, $z, Block::OBSIDIAN);
		self::setBlockId($level, $x + 2, $y, $z, Block::OBSIDIAN);
		$z--;

		for($i = 0; $i < 3; $i++){
			$y++;
			self::setBlockId($level, $x, $y, $z, Block::OBSIDIAN);
			self::setBlockId($level, $x + 1, $y, $z, Block::PORTAL);
			self::setBlockId($level, $x + 2, $y, $z, Block::PORTAL);
			self::setBlockId($level, $x + 3, $y, $z, Block::OBSIDIAN);
		}

		$y++;
		self::setBlockId($level, $x, $y, $z, Block::OBSIDIAN);
		self::setBlockId($level, $x + 1, $y, $z, Block::OBSIDIAN);
		self::setBlockId($level, $x + 2, $y, $z, Block::OBSIDIAN);
		self::setBlockId($level, $x + 3, $y, $z, Block::OBSIDIAN);

		return true;
	}

	public static function getNearestValidPortal(Position $currentPos, int $searchRadius = self::PORTAL_SEARCH_RADIUS){
		if(!($currentPos->getLevel() instanceof Level)){
			return null;
		}

		$level = $currentPos->getLevel();
		$originX = (int) floor($currentPos->x);
		$originY = (int) floor($currentPos->y);
		$originZ = (int) floor($currentPos->z);
		$minY = self::getMinHeight($level);
		$maxY = self::getMaxHeight($level);
		$customBlockIdAt = self::hasCustomBlockIdAt($level);
		$best = null;
		$bestHorizontal = PHP_INT_MAX;
		$bestVertical = PHP_INT_MAX;

		$searchRadius = max(0, $searchRadius);

		for($ring = 0; $ring <= $searchRadius; $ring++){
			for($dx = -$ring; $dx <= $ring; $dx++){
				self::scanPortalColumn($level, $originX, $originY, $originZ, $originX + $dx, $originZ - $ring, $minY, $maxY, $customBlockIdAt, $best, $bestHorizontal, $bestVertical);
				if($ring !== 0){
					self::scanPortalColumn($level, $originX, $originY, $originZ, $originX + $dx, $originZ + $ring, $minY, $maxY, $customBlockIdAt, $best, $bestHorizontal, $bestVertical);
				}
			}
			for($dz = -$ring + 1; $dz <= $ring - 1; $dz++){
				self::scanPortalColumn($level, $originX, $originY, $originZ, $originX - $ring, $originZ + $dz, $minY, $maxY, $customBlockIdAt, $best, $bestHorizontal, $bestVertical);
				if($ring !== 0){
					self::scanPortalColumn($level, $originX, $originY, $originZ, $originX + $ring, $originZ + $dz, $minY, $maxY, $customBlockIdAt, $best, $bestHorizontal, $bestVertical);
				}
			}

			$nextRing = $ring + 1;
			if($best !== null and $bestHorizontal < $nextRing * $nextRing){
				break;
			}
		}

		return $best;
	}

	public static function convertPosBetweenNetherAndOverworld(Position $current, Level $targetLevel, int $scale = self::DEFAULT_NETHER_SCALE){
		if(!($current->getLevel() instanceof Level)){
			return null;
		}

		$currentLevel = $current->getLevel();
		if(method_exists($currentLevel, "getNetherScale")){
			$scale = (int) $currentLevel->getNetherScale();
		}
		$scale = max(1, $scale);

		if($currentLevel->getDimension() === Level::DIMENSION_NORMAL){
			$x = (int) floor($current->x / $scale);
			$z = (int) floor($current->z / $scale);
			if($scale === 1){
				$x += 2;
				$z += 2;
			}
			$toNether = true;
		}elseif($currentLevel->getDimension() === Level::DIMENSION_NETHER){
			$x = (int) floor($current->x) * $scale;
			$z = (int) floor($current->z) * $scale;
			$toNether = false;
		}else{
			return null;
		}

		if(!self::preparePortalChunkAtBlock($targetLevel, $x, $z)){
			return null;
		}

		$y = self::findSafePortalY($targetLevel, $x, $z, $toNether);
		$target = new Position($x, self::clamp($y, self::getMinHeight($targetLevel), self::getMaxHeight($targetLevel) - 1) + 1, $z, $targetLevel);
		$portalSearchRadius = self::getPortalSearchRadiusForTargetLevel($targetLevel);
		$portal = self::getNearestValidPortal($target, $portalSearchRadius);

		return $portal instanceof Position ? $portal : $target;
	}

	private static function getPortalSearchRadiusForTargetLevel(Level $targetLevel) : int{
		if($targetLevel->getDimension() === Level::DIMENSION_NETHER){
			return self::NETHER_PORTAL_SEARCH_RADIUS;
		}

		return self::PORTAL_SEARCH_RADIUS;
	}

	public static function isPortalPosition(Position $pos) : bool{
		$level = $pos->getLevel();
		return $level instanceof Level and self::getBlockId($level, (int) floor($pos->x), (int) floor($pos->y), (int) floor($pos->z)) === Block::PORTAL;
	}

	private static function createPortalOnX(Level $level, int $targX, int $targY, int $targZ, int $sizePosX) : bool{
		$scanX = $targX;
		$scanY = $targY + 1;
		$scanZ = $targZ;

		for($i = 0; $i < $sizePosX + 1; $i++){
			if(!self::isPortalInteriorReplaceableId(self::getBlockId($level, $scanX + $i, $scanY, $scanZ))){
				return false;
			}
			if(self::getBlockId($level, $scanX + $i + 1, $scanY, $scanZ) === Block::OBSIDIAN){
				$scanX += $i;
				break;
			}
		}

		if(self::getBlockId($level, $scanX + 1, $scanY, $scanZ) !== Block::OBSIDIAN){
			return false;
		}

		$innerWidth = self::measureInnerX($level, $scanX, $scanY, $scanZ);
		$innerHeight = self::measureInnerY($level, $scanX, $scanY, $scanZ);
		if($innerWidth === false or $innerHeight === false or !self::isValidInnerSize($innerWidth, $innerHeight)){
			return false;
		}

		for($height = 0; $height < $innerHeight + 1; $height++){
			if($height === $innerHeight){
				for($width = 0; $width < $innerWidth; $width++){
					if(self::getBlockId($level, $scanX - $width, $scanY + $height, $scanZ) !== Block::OBSIDIAN){
						return false;
					}
				}
			}else{
				if(self::getBlockId($level, $scanX + 1, $scanY + $height, $scanZ) !== Block::OBSIDIAN or self::getBlockId($level, $scanX - $innerWidth, $scanY + $height, $scanZ) !== Block::OBSIDIAN){
					return false;
				}
				for($width = 0; $width < $innerWidth; $width++){
					if(!self::isPortalInteriorReplaceableId(self::getBlockId($level, $scanX - $width, $scanY + $height, $scanZ))){
						return false;
					}
				}
			}
		}

		for($height = 0; $height < $innerHeight; $height++){
			for($width = 0; $width < $innerWidth; $width++){
				self::setBlockId($level, $scanX - $width, $scanY + $height, $scanZ, Block::PORTAL);
			}
		}

		return true;
	}

	private static function createPortalOnZ(Level $level, int $targX, int $targY, int $targZ, int $sizePosZ) : bool{
		$scanX = $targX;
		$scanY = $targY + 1;
		$scanZ = $targZ;

		for($i = 0; $i < $sizePosZ + 1; $i++){
			if(!self::isPortalInteriorReplaceableId(self::getBlockId($level, $scanX, $scanY, $scanZ + $i))){
				return false;
			}
			if(self::getBlockId($level, $scanX, $scanY, $scanZ + $i + 1) === Block::OBSIDIAN){
				$scanZ += $i;
				break;
			}
		}

		if(self::getBlockId($level, $scanX, $scanY, $scanZ + 1) !== Block::OBSIDIAN){
			return false;
		}

		$innerWidth = self::measureInnerZ($level, $scanX, $scanY, $scanZ);
		$innerHeight = self::measureInnerY($level, $scanX, $scanY, $scanZ);
		if($innerWidth === false or $innerHeight === false or !self::isValidInnerSize($innerWidth, $innerHeight)){
			return false;
		}

		for($height = 0; $height < $innerHeight + 1; $height++){
			if($height === $innerHeight){
				for($width = 0; $width < $innerWidth; $width++){
					if(self::getBlockId($level, $scanX, $scanY + $height, $scanZ - $width) !== Block::OBSIDIAN){
						return false;
					}
				}
			}else{
				if(self::getBlockId($level, $scanX, $scanY + $height, $scanZ + 1) !== Block::OBSIDIAN or self::getBlockId($level, $scanX, $scanY + $height, $scanZ - $innerWidth) !== Block::OBSIDIAN){
					return false;
				}
				for($width = 0; $width < $innerWidth; $width++){
					if(!self::isPortalInteriorReplaceableId(self::getBlockId($level, $scanX, $scanY + $height, $scanZ - $width))){
						return false;
					}
				}
			}
		}

		for($height = 0; $height < $innerHeight; $height++){
			for($width = 0; $width < $innerWidth; $width++){
				self::setBlockId($level, $scanX, $scanY + $height, $scanZ - $width, Block::PORTAL);
			}
		}

		return true;
	}

	private static function countObsidian(Level $level, int $x, int $y, int $z, int $dx, int $dz) : int{
		$count = 0;
		for($i = 1; $i < self::MAX_PORTAL_SIZE; $i++){
			if(self::getBlockId($level, $x + ($dx * $i), $y, $z + ($dz * $i)) !== Block::OBSIDIAN){
				break;
			}
			$count++;
		}
		return $count;
	}

	private static function measureInnerX(Level $level, int $x, int $y, int $z){
		$innerWidth = 0;
		for($i = 0; $i < self::MAX_PORTAL_SIZE - 2; $i++){
			$id = self::getBlockId($level, $x - $i, $y, $z);
			if(self::isPortalInteriorReplaceableId($id)){
				$innerWidth++;
			}elseif($id === Block::OBSIDIAN){
				break;
			}else{
				return false;
			}
		}
		return $innerWidth;
	}

	private static function measureInnerZ(Level $level, int $x, int $y, int $z){
		$innerWidth = 0;
		for($i = 0; $i < self::MAX_PORTAL_SIZE - 2; $i++){
			$id = self::getBlockId($level, $x, $y, $z - $i);
			if(self::isPortalInteriorReplaceableId($id)){
				$innerWidth++;
			}elseif($id === Block::OBSIDIAN){
				break;
			}else{
				return false;
			}
		}
		return $innerWidth;
	}

	private static function measureInnerY(Level $level, int $x, int $y, int $z){
		$innerHeight = 0;
		for($i = 0; $i < self::MAX_PORTAL_SIZE - 2; $i++){
			$id = self::getBlockId($level, $x, $y + $i, $z);
			if(self::isPortalInteriorReplaceableId($id)){
				$innerHeight++;
			}elseif($id === Block::OBSIDIAN){
				break;
			}else{
				return false;
			}
		}
		return $innerHeight;
	}

	private static function isValidInnerSize(int $innerWidth, int $innerHeight) : bool{
		return $innerWidth >= 2 and $innerWidth <= self::MAX_PORTAL_SIZE - 2 and $innerHeight >= 3 and $innerHeight <= self::MAX_PORTAL_SIZE - 2;
	}

	private static function isPortalInteriorReplaceableId(int $id) : bool{
		return $id === Block::AIR or $id === Block::FIRE;
	}

	private static function preparePortalFootprint(Level $level, int $minX, int $minZ, int $maxX, int $maxZ) : bool{
		$minChunkX = $minX >> 4;
		$maxChunkX = $maxX >> 4;
		$minChunkZ = $minZ >> 4;
		$maxChunkZ = $maxZ >> 4;

		for($chunkX = $minChunkX; $chunkX <= $maxChunkX; ++$chunkX){
			for($chunkZ = $minChunkZ; $chunkZ <= $maxChunkZ; ++$chunkZ){
				if(!self::preparePortalChunk($level, $chunkX, $chunkZ)){
					return false;
				}
			}
		}

		return true;
	}

	private static function preparePortalChunkAtBlock(Level $level, int $x, int $z) : bool{
		return self::preparePortalChunk($level, $x >> 4, $z >> 4);
	}

	private static function preparePortalChunk(Level $level, int $chunkX, int $chunkZ) : bool{
		try{
			if(self::canUseLevelMethod($level, "populateChunkSynchronously")){
				return $level->populateChunkSynchronously($chunkX, $chunkZ);
			}

			if(!$level->isChunkLoaded($chunkX, $chunkZ)){
				return $level->loadChunk($chunkX, $chunkZ);
			}
		}catch(\Throwable $e){
			return false;
		}

		return true;
	}

	private static function canUseLevelMethod(Level $level, string $method) : bool{
		if(!method_exists($level, $method)){
			return false;
		}

		try{
			$reflection = new \ReflectionMethod(get_class($level), $method);
		}catch(\ReflectionException $e){
			return false;
		}

		return get_class($level) === Level::class or $reflection->getDeclaringClass()->getName() !== Level::class;
	}

	private static function findSafePortalY(Level $level, int $x, int $z, bool $toNether) : int{
		$minY = self::getMinHeight($level);
		$maxY = self::getMaxHeight($level);
		$highest = (int) $level->getHighestBlockAt($x, $z);
		if($toNether){
			$highest = min($highest, self::NETHER_ROOF_SAFE_Y);
		}
		$highest = self::clamp($highest, $minY + 2, max($minY + 2, $maxY - 5));

		for($i = $highest; $i > $minY + 2; $i--){
			$ground = self::getBlockId($level, $x, $i - 1, $z);
			if(self::hasFiveBlockAirColumn($level, $x, $i, $z) and self::isSolidBlockId($ground) and !self::isUnsafeGroundId($ground, $toNether)){
				return $i;
			}
		}

		return $highest;
	}

	private static function hasFiveBlockAirColumn(Level $level, int $x, int $y, int $z) : bool{
		for($i = 0; $i < 5; $i++){
			if(self::getBlockId($level, $x, $y + $i, $z) !== Block::AIR){
				return false;
			}
		}
		return true;
	}

	private static function isSolidBlockId(int $id) : bool{
		if($id === Block::AIR){
			return false;
		}
		if(Block::$solid instanceof \SplFixedArray and isset(Block::$solid[$id])){
			return (bool) Block::$solid[$id];
		}
		return Block::get($id)->isSolid();
	}

	private static function isUnsafeGroundId(int $id, bool $toNether) : bool{
		if($id === Block::LAVA or $id === Block::STILL_LAVA){
			return true;
		}
		if($toNether){
			return $id === Block::BEDROCK;
		}
		return $id === Block::WATER or $id === Block::STILL_WATER;
	}

	private static function scanPortalColumn(Level $level, int $originX, int $originY, int $originZ, int $x, int $z, int $minY, int $maxY, bool $customBlockIdAt, &$best, int &$bestHorizontal, int &$bestVertical){
		$dx = $x - $originX;
		$dz = $z - $originZ;
		$horizontal = $dx * $dx + $dz * $dz;
		if($horizontal > $bestHorizontal){
			return;
		}

		$chunk = null;
		if(!$customBlockIdAt){
			$chunk = $level->getChunk($x >> 4, $z >> 4, false);
			if($chunk === null){
				return;
			}
		}

		for($y = $minY; $y <= $maxY; $y++){
			if(self::getColumnBlockId($level, $chunk, $customBlockIdAt, $x, $y, $z) !== Block::PORTAL){
				continue;
			}
			if($y > $minY and self::getColumnBlockId($level, $chunk, $customBlockIdAt, $x, $y - 1, $z) === Block::PORTAL){
				continue;
			}

			$vertical = ($originY - $y) * ($originY - $y);
			if($best === null or $horizontal < $bestHorizontal or ($horizontal === $bestHorizontal and $vertical < $bestVertical)){
				$best = new Position($x, $y, $z, $level);
				$bestHorizontal = $horizontal;
				$bestVertical = $vertical;
			}
		}
	}

	private static function getColumnBlockId(Level $level, $chunk, bool $customBlockIdAt, int $x, int $y, int $z) : int{
		if($customBlockIdAt){
			return $level->getBlockIdAt($x, $y, $z);
		}
		return $chunk->getBlockId($x & 0x0f, $y & 0x7f, $z & 0x0f);
	}

	private static function getBlockId(Level $level, int $x, int $y, int $z) : int{
		return self::getBlockIdFast($level, $x, $y, $z, self::hasCustomBlockIdAt($level));
	}

	private static function getBlockIdFast(Level $level, int $x, int $y, int $z, bool $customBlockIdAt) : int{
		if($y < self::getMinHeight($level) or $y > self::getMaxHeight($level)){
			return Block::AIR;
		}
		if($customBlockIdAt){
			return $level->getBlockIdAt($x, $y, $z);
		}

		$chunk = $level->getChunk($x >> 4, $z >> 4, false);
		if($chunk === null){
			return Block::AIR;
		}
		return $chunk->getBlockId($x & 0x0f, $y & 0x7f, $z & 0x0f);
	}

	private static function setBlockId(Level $level, int $x, int $y, int $z, int $id){
		if($y < self::getMinHeight($level) or $y > self::getMaxHeight($level)){
			return false;
		}
		return $level->setBlock(new Position($x, $y, $z, $level), Block::get($id), false, true);
	}

	private static function getMinHeight(Level $level) : int{
		return method_exists($level, "getMinHeight") ? (int) $level->getMinHeight() : self::LEGACY_MIN_HEIGHT;
	}

	private static function getMaxHeight(Level $level) : int{
		return method_exists($level, "getMaxHeight") ? (int) $level->getMaxHeight() : self::LEGACY_MAX_HEIGHT;
	}

	private static function hasCustomBlockIdAt(Level $level) : bool{
		static $cache = [];
		$class = get_class($level);
		if(!isset($cache[$class])){
			$method = new \ReflectionMethod($class, "getBlockIdAt");
			$cache[$class] = $method->getDeclaringClass()->getName() !== Level::class;
		}
		return $cache[$class];
	}

	private static function clamp(int $value, int $min, int $max) : int{
		if($value < $min){
			return $min;
		}
		if($value > $max){
			return $max;
		}
		return $value;
	}
}
