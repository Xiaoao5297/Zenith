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

namespace lycore\entity\behavior;
use lycore\entity\Arrow;
use lycore\entity\AgeableSpawnHelper;
use lycore\entity\Blaze;
use lycore\entity\Bat;
use lycore\entity\CaveSpider;
use lycore\entity\Enderman;
use lycore\entity\Ghast;
use lycore\entity\Horse;
use lycore\entity\Husk;
use lycore\entity\IronGolem;
use lycore\entity\LavaSlime;
use lycore\entity\NaturalMobSpawnRules;
use lycore\entity\Ocelot;
use lycore\entity\Rabbit;
use lycore\entity\Silverfish;
use lycore\entity\Slime;
use lycore\entity\Squid;
use lycore\entity\Stray;
use lycore\entity\Villager;
use lycore\entity\Witch;
use lycore\entity\Wolf;
use lycore\entity\ZombieVillager;
use lycore\event\entity\EntityGenerateEvent;
use lycore\level\generator\normal\populator\VillagePopulator;
use lycore\level\Position;
use lycore\level\Level;
use lycore\item\Item;
use lycore\Player;
use lycore\math\AxisAlignedBB;
use lycore\math\Vector3;
use lycore\entity\Entity;
use lycore\entity\FlyingAnimal;
use lycore\entity\Monster;
use lycore\level\format\FullChunk;
use lycore\scheduler\CallbackTask;
use lycore\event\entity\EntityDeathEvent;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\DoubleTag;
use lycore\nbt\tag\ListTag;
use lycore\nbt\tag\FloatTag;
use lycore\event\entity\EntityDamageByChildEntityEvent;
use lycore\event\entity\EntityDamageByEntityEvent;
use lycore\event\entity\EntityDamageEvent;
use lycore\scheduler\TaskHandler;
use lycore\Server;
use lycore\entity\Creeper;
use lycore\entity\Skeleton;
use lycore\entity\Cow;
use lycore\entity\Pig;
use lycore\entity\Sheep;
use lycore\entity\Spider;
use lycore\entity\Chicken;
use lycore\entity\Mooshroom;
use lycore\entity\PigZombie;
use lycore\entity\Zombie;
class AIHolder {
	const HOSTILE_SPAWN_MAX_LIGHT = 7;
	const NATURAL_SPAWN_TASK = "MobGenerate";
	const NATURAL_SPAWN_DEBUG_PREFIX = "[MobSpawn]";
	const NATURAL_LAND_SPAWN_ANY = 0;
	const NATURAL_LAND_SPAWN_HOSTILE = 1;
	const NATURAL_LAND_SPAWN_FRIENDLY = 2;

	public $ChickenAI;
	public $CowAI;
	public $CreeperAI;
	public $PigAI;
	public $SheepAI;
	public $SkeletonAI;
	public $SpiderAI;
	public $ZombieAI;
	public $DefultAI;
	public $Zombie = [];
    public $ZombieVillager = [];
    public $PigZombie = [];
	public $Creeper = [];
	public $Skeleton = [];
	public $Cow = [];
    public $Squid = [];
	public $Pig = [];
	public $Sheep = [];
	public $Spider = [];
    public $CaveSpider = [];
    public $LavaSlime = [];
    public $Slime = [];
    public $Enderman = [];
    public $Witch = [];
    public $Ghast = [];
    public $Bat = [];
    public $Silverfish = [];
	public $Chicken = [];
    public $Ocelot = [];
    public $Mooshroom = [];
    public $Blaze = [];
    public $Rabbit = [];
	public $Wolf = [];
	public $Villager = [];
	public $IronGolem = [];
	public $Defult = [];
	public $birth_r = 30;
	public $tasks = [];
	public $server;
	private $lastNaturalMobSpawnDebugLogTick = 0;
	private $lastNaturalMobSpawnTaskTick = 0;
	private $naturalHostileMobSpawnElapsedTicks = 0;
	private $naturalFriendlyMobSpawnElapsedTicks = 0;
	public function getServer() {
		return $this->server;
	}
	public function __construct(Server $server) {
		$this->server = $server;
		if($this->server->aiConfig["mobgenerate"]) {
			$this->RestartSpawnTimer();
		}
		$this->getServer()->getScheduler()->scheduleRepeatingTask(new CallbackTask([
					$this,
					"TimeFix"
				]), 20);
		$this->getServer()->getScheduler()->scheduleRepeatingTask(new CallbackTask ([$this, "RotationTimer"]), 2);
		/*$this->ZombieAI = new ZombieAI($this);
		$this->CowAI = new CowAI($this);
		$this->PigAI = new PigAI($this);
		$this->SheepAI = new SheepAI($this);
		$this->ChickenAI = new ChickenAI($this);
		$this->SpiderAI = new SpiderAI($this);*/
		//$this->SkeletonAI = new SkeletonAI($this);
	}

	public static function getNaturalSpawnTaskPeriod($difficulty) : int{
		switch(max(0, min(3, (int) $difficulty))){
			case 0:
				return 20 * 60;
			case 1:
				return 20 * 30;
			case 2:
				return 20 * 18;
			case 3:
				return 20 * 12;
			default:
				return 20 * 30;
		}
	}

	public static function getNaturalSpawnAttempts($difficulty) : int{
		switch(max(0, min(3, (int) $difficulty))){
			case 0:
				return 1;
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 3;
			default:
				return 1;
		}
	}

	public static function getNaturalHostileSpawnAttempts($difficulty) : int{
		return self::getNaturalSpawnAttempts($difficulty);
	}

	public static function getNaturalFriendlySpawnAttempts($difficulty) : int{
		return self::getNaturalSpawnAttempts(2);
	}

	public static function getNaturalHostileSpawnTaskPeriod($difficulty) : int{
		return self::getNaturalSpawnTaskPeriod($difficulty);
	}

	public static function getNaturalFriendlySpawnTaskPeriod($difficulty) : int{
		return self::getNaturalSpawnTaskPeriod(2);
	}

	private function getConfiguredNaturalSpawnTaskPeriod() : int{
		return min($this->getConfiguredNaturalHostileSpawnTaskPeriod(), $this->getConfiguredNaturalFriendlySpawnTaskPeriod());
	}

	private function getConfiguredNaturalHostileSpawnTaskPeriod() : int{
		$defaultPeriod = self::getNaturalHostileSpawnTaskPeriod($this->server->getDifficulty());
		$ticks = (int) $this->server->getProperty("ticks-per.monster-spawns", $defaultPeriod);
		return $ticks > 0 ? $this->normalizeConfiguredNaturalSpawnTaskPeriod($ticks, $defaultPeriod) : $defaultPeriod;
	}

	private function getConfiguredNaturalFriendlySpawnTaskPeriod() : int{
		$defaultPeriod = self::getNaturalFriendlySpawnTaskPeriod($this->server->getDifficulty());
		$ticks = (int) $this->server->getProperty("ticks-per.animal-spawns", $defaultPeriod);
		return $ticks > 0 ? $this->normalizeConfiguredNaturalSpawnTaskPeriod($ticks, $defaultPeriod) : $defaultPeriod;
	}

	private function normalizeConfiguredNaturalSpawnTaskPeriod(int $ticks, int $defaultPeriod) : int{
		return max($defaultPeriod, $ticks);
	}

	private function getNaturalMobSpawnTaskElapsedTicks() : int{
		$period = max(1, $this->getConfiguredNaturalSpawnTaskPeriod());
		if(!method_exists($this->server, "getTick")){
			return $period;
		}

		$tick = (int) $this->server->getTick();
		if($this->lastNaturalMobSpawnTaskTick > 0 && $tick >= $this->lastNaturalMobSpawnTaskTick){
			$elapsedTicks = max(1, $tick - $this->lastNaturalMobSpawnTaskTick);
		}else{
			$elapsedTicks = $period;
		}
		$this->lastNaturalMobSpawnTaskTick = $tick;

		return $elapsedTicks;
	}

	private function shouldRunNaturalHostileMobSpawn(int $elapsedTicks) : bool{
		return $this->shouldRunNaturalSpawnBucket("naturalHostileMobSpawnElapsedTicks", $this->getConfiguredNaturalHostileSpawnTaskPeriod(), $elapsedTicks);
	}

	private function shouldRunNaturalFriendlyMobSpawn(int $elapsedTicks) : bool{
		return $this->shouldRunNaturalSpawnBucket("naturalFriendlyMobSpawnElapsedTicks", $this->getConfiguredNaturalFriendlySpawnTaskPeriod(), $elapsedTicks);
	}

	private function shouldRunNaturalSpawnBucket(string $elapsedProperty, int $period, int $elapsedTicks) : bool{
		$period = max(1, $period);
		$this->{$elapsedProperty} += max(1, $elapsedTicks);
		if($this->{$elapsedProperty} < $period){
			return false;
		}

		$this->{$elapsedProperty} -= $period;
		if($this->{$elapsedProperty} >= $period){
			$this->{$elapsedProperty} %= $period;
		}

		return true;
	}

	private function isNaturalMobSpawnDebugEnabled() : bool{
		return (bool) $this->server->getProperty("debug.mob-spawns", false);
	}

	private function getNaturalMobSpawnDebugInterval() : int{
		return max(1, (int) $this->server->getProperty("debug.mob-spawns-interval", 20));
	}

	private function logNaturalMobSpawnDebug(string $message) : void{
		if(!$this->isNaturalMobSpawnDebugEnabled()){
			return;
		}

		$this->server->getLogger()->info(self::NATURAL_SPAWN_DEBUG_PREFIX . " " . $message);
	}

	private function createNaturalMobSpawnDebugStats() : array{
		return [
			"players" => 0,
			"attempts" => 0,
			"waterAttempts" => 0,
			"caveAttempts" => 0,
			"airAttempts" => 0,
			"landAttempts" => 0,
			"spawned" => 0,
			"reasons" => [],
			"landSamples" => []
		];
	}

	private function recordNaturalMobSpawnDebugCount(&$stats, string $key, int $amount = 1) : void{
		if(!is_array($stats)){
			return;
		}
		if(!isset($stats[$key])){
			$stats[$key] = 0;
		}
		$stats[$key] += $amount;
	}

	private function recordNaturalMobSpawnDebugReason(&$stats, string $reason) : void{
		if(!is_array($stats)){
			return;
		}
		if(!isset($stats["reasons"][$reason])){
			$stats["reasons"][$reason] = 0;
		}
		++$stats["reasons"][$reason];
	}

	private function addNaturalMobSpawnDebugSample(&$stats, string $key, string $sample, int $limit = 3) : void{
		if(!is_array($stats)){
			return;
		}
		if(!isset($stats[$key]) || !is_array($stats[$key])){
			$stats[$key] = [];
		}
		if(count($stats[$key]) >= $limit){
			return;
		}

		$stats[$key][] = $sample;
	}

	private function getNaturalMobSpawnDebugReasons(array $stats) : string{
		if(empty($stats["reasons"])){
			return "none";
		}

		$reasons = $stats["reasons"];
		arsort($reasons);
		$parts = [];
		foreach(array_slice($reasons, 0, 8, true) as $reason => $count){
			$parts[] = $reason . "=" . $count;
		}

		return implode(", ", $parts);
	}

	private function getNaturalMobSpawnDebugSamples(array $stats, string $key) : string{
		if(empty($stats[$key]) || !is_array($stats[$key])){
			return "";
		}

		return implode(" | ", array_slice($stats[$key], 0, 3));
	}

	private function flushNaturalMobSpawnDebugStats(array $stats) : void{
		if(!$this->isNaturalMobSpawnDebugEnabled()){
			return;
		}

		$tick = method_exists($this->server, "getTick") ? (int) $this->server->getTick() : 0;
		$interval = $this->getNaturalMobSpawnDebugInterval();
		if($this->lastNaturalMobSpawnDebugLogTick > 0 && ($tick - $this->lastNaturalMobSpawnDebugLogTick) < $interval){
			return;
		}
		$this->lastNaturalMobSpawnDebugLogTick = $tick;

		$this->logNaturalMobSpawnDebug(
			"summary tick=" . $tick .
			" players=" . (int) $stats["players"] .
			" attempts=" . (int) $stats["attempts"] .
			" water=" . (int) $stats["waterAttempts"] .
			" cave=" . (int) $stats["caveAttempts"] .
			" air=" . (int) $stats["airAttempts"] .
			" land=" . (int) $stats["landAttempts"] .
			" spawned=" . (int) $stats["spawned"] .
			" reasons=[" . $this->getNaturalMobSpawnDebugReasons($stats) . "]"
		);
		$landSamples = $this->getNaturalMobSpawnDebugSamples($stats, "landSamples");
		if($landSamples !== ""){
			$this->logNaturalMobSpawnDebug("land_samples=[" . $landSamples . "]");
		}
	}

	private function getPositionDebugText(Position $pos) : string{
		$level = $pos->getLevel();
		$levelName = $level instanceof Level ? $level->getName() : "null";
		return "level=" . $levelName . " x=" . round($pos->x, 2) . " y=" . round($pos->y, 2) . " z=" . round($pos->z, 2);
	}

	private function createNaturalLandSpawnDebugSample(Player $player, Level $level, int $x, int $z, array $candidates) : string{
		$chunk = $level->getChunk($x >> 4, $z >> 4, false);
		$chunkText = $chunk instanceof FullChunk
			? (($chunk->isGenerated() ? "gen=1" : "gen=0") . "," . ($chunk->isPopulated() ? "pop=1" : "pop=0"))
			: "missing";
		$highest = "n/a";
		if($chunk instanceof FullChunk && method_exists($level, "getHighestBlockAt")){
			$highest = (string) (int) $level->getHighestBlockAt($x, $z);
		}

		$typeCounts = [];
		$candidateMin = null;
		$candidateMax = null;
		$standY = "none";
		foreach($candidates as $y){
			$y = (int) $y;
			$candidateMin = $candidateMin === null ? $y : min($candidateMin, $y);
			$candidateMax = $candidateMax === null ? $y : max($candidateMax, $y);
			$type = $this->whatBlock($level, new Vector3($x, $y, $z));
			if(!isset($typeCounts[$type])){
				$typeCounts[$type] = 0;
			}
			++$typeCounts[$type];
			if($standY === "none" && $type === "block" && $this->whatBlock($level, new Vector3($x, $y + 1, $z)) === "air" && $this->whatBlock($level, new Vector3($x, $y + 2, $z)) === "air"){
				$standY = (string) $this->getAiSupportStandingY($level, $x, $y, $z, $y + 1);
			}
		}
		ksort($typeCounts);
		$typeParts = [];
		foreach($typeCounts as $type => $count){
			$typeParts[] = $type . ":" . $count;
		}
		$candidateText = $candidateMin === null ? "empty" : ($candidateMin . ".." . $candidateMax . "/" . count($candidates));

		return "x=" . $x .
			" z=" . $z .
			" chunk=" . ($x >> 4) . "," . ($z >> 4) . ":" . $chunkText .
			" playerY=" . (int) floor($player->getY()) .
			" highest=" . $highest .
			" candidateY=" . $candidateText .
			" standY=" . $standY .
			" types=" . (empty($typeParts) ? "none" : implode(",", $typeParts));
	}
	/*
	 ************ API 閮ㄥ垎 ************************************
	 */
	/**
	 * @param $r
	 * 璁剧疆鍍靛案浠囨仺鍗婂緞
	 */
	public function setZombieHatred_r($r) {
		$this->ZombieAI->hatred_r = $r;
	}
	/**
	 * @param $r
	 * 璁剧疆澶滄櫄鍒峰兊灏歌寖鍥达紙浠ユ瘡涓帺瀹朵负涓績锛?	 */
	public function setZombieBirth_r($r) {
		$this->birth_r = $r;
	}
	/**
	 * @param $v
	 * 璁剧疆鍍靛案浠囨仺妯″紡涓嬬殑璧拌矾閫熷害
	 */
	public function setZombieHate_v($v) {
		$this->ZombieAI->zo_hate_v = $v;
	}
	/**
	 * @param $tick
	 * @return bool
	 * 閲嶆柊鍚姩鍒锋€鏃跺櫒
	 * 锛堝彲鐢ㄤ簬鏇存敼鍒锋€椂闂撮棿闅旓級
	 */
	public function RestartSpawnTimer($tick = null) {
		if($tick === null){
			$tick = $this->getConfiguredNaturalSpawnTaskPeriod();
		}
		$this->lastNaturalMobSpawnTaskTick = 0;
		$this->naturalHostileMobSpawnElapsedTicks = 0;
		$this->naturalFriendlyMobSpawnElapsedTicks = 0;

		if(isset($this->tasks[self::NATURAL_SPAWN_TASK]) and $this->tasks[self::NATURAL_SPAWN_TASK] instanceof TaskHandler) {
			$this->tasks[self::NATURAL_SPAWN_TASK]->cancel();
			unset($this->tasks[self::NATURAL_SPAWN_TASK]);
		}

		if(!$this->server->aiConfig["mobgenerate"]) {
			$this->logNaturalMobSpawnDebug("task not scheduled: ai.mobgenerate=false");
			return false;
		}

		$this->tasks[self::NATURAL_SPAWN_TASK] = $this->getServer()->getScheduler()->scheduleRepeatingTask(new CallbackTask([
			$this,
			"MobGenerate"
		]), max(1, (int) $tick));

		$scheduled = $this->tasks[self::NATURAL_SPAWN_TASK] instanceof TaskHandler;
		if($scheduled){
			$limits = $this->getConfiguredNaturalSpawnLimits();
			$hostilePeriod = $this->getConfiguredNaturalHostileSpawnTaskPeriod();
			$friendlyPeriod = $this->getConfiguredNaturalFriendlySpawnTaskPeriod();
			$this->logNaturalMobSpawnDebug(
				"task scheduled period=" . max(1, (int) $tick) .
				" difficulty=" . (int) $this->server->getDifficulty() .
				" hostile_period=" . $hostilePeriod .
				" friendly_period=" . $friendlyPeriod .
				" attempts=" . self::getNaturalHostileSpawnAttempts($this->server->getDifficulty()) .
				" friendly_attempts=" . self::getNaturalFriendlySpawnAttempts($this->server->getDifficulty()) .
				" limits=monsters:" . $limits[NaturalMobSpawnRules::SPAWN_CATEGORY_MONSTERS] .
				",animals:" . $limits[NaturalMobSpawnRules::SPAWN_CATEGORY_ANIMALS] .
				",water:" . $limits[NaturalMobSpawnRules::SPAWN_CATEGORY_WATER_ANIMALS] .
				",ambient:" . $limits[NaturalMobSpawnRules::SPAWN_CATEGORY_AMBIENT]
			);
		}

		return $scheduled;
	}
	public function TimeFix() {
		$mode = $this->mode();
		foreach($this->getServer()->getLevels() as $level) {
			foreach($level->getEntities() as $entity) {
				if($entity instanceof Zombie) {
					$this->ahurt($entity,$mode);
				}
				if($entity instanceof Spider or $entity instanceof CaveSpider) {
					$this->bhurt($entity,$mode);
				}
			}
			if($level->getTime() > 24000) {
				$level->setTime(0);
			}
		}
	}
	public function mode() {
		$mode = 1;
		switch($this->getServer()->getDifficulty()) {
			case 0:
						case 1:
						$mode = 1;
			break;
			case 2:
						$mode = 2;
			break;
			case 3:
						$mode = 3;
			break;
		}
		return $mode;
	}
	public function ahurt($e,$mode) {
		switch($mode) {
			case 1:
						$hurt = 5;
			break;
			case 2:
						$hurt = 6;
			break;
			case 3:
						$hurt = 8;
			break;
		}
		return $e->setHurt($hurt);
	}
	public function bhurt($e,$mode) {
		switch($mode) {
			case 1:
						$hurt = 5;
			break;
			case 2:
						$hurt = 6;
			break;
			case 3:
						$hurt = 6;
			break;
		}
		return $e->setHurt($hurt);
	}

	private function finalizeNaturalMob(Entity $entity){
		AgeableSpawnHelper::maybeSetBaby($entity, AgeableSpawnHelper::PNX_NATURAL_BABY_CHANCE);
		$entity->spawnToAll();
	}

	public function spawnZombie(Position $pos, $maxHealth = 20, $health = 20) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$zo = new Zombie($chunk, $nbt);
		$zo->setPosition($pos);
		$zo->setMaxHealth($maxHealth);
		$zo->setHealth($health);
		$this->finalizeNaturalMob($zo);
		//$this->getLogger()->info("鐢熸垚浜嗕竴鍙兊灏?);
	}
	public function spawnHusk(Position $pos, $maxHealth = 20, $health = 20) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$husk = new Husk($chunk, $nbt);
		$husk->setPosition($pos);
		$husk->setMaxHealth($maxHealth);
		$husk->setHealth($health);
		$this->finalizeNaturalMob($husk);
	}
    public function spawnZombieVillager(Position $pos, $maxHealth = 20, $health = 20) {
        $chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
        $nbt = $this->getNBT();
        $zov = new ZombieVillager($chunk, $nbt);
        $zov->setPosition($pos);
        $zov->setMaxHealth($maxHealth);
        $zov->setHealth($health);
        $this->finalizeNaturalMob($zov);
        //$this->getLogger()->info("鐢熸垚浜嗕竴鍙潙姘戝兊灏?);
    }
    public function spawnPigZombie(Position $pos, $maxHealth = 20, $health = 20) {
        $chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
        $nbt = $this->getNBT();
        $zo = new PigZombie($chunk, $nbt);
        $zo->setPosition($pos);
        $zo->setMaxHealth($maxHealth);
        $zo->setHealth($health);
        $this->finalizeNaturalMob($zo);
        //$this->getLogger()->info("鐢熸垚浜嗕竴鍙兊灏哥尓浜?);
    }
	public function spawnCreeper(Position $pos, $maxHealth = 20, $health = 20) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$co = new Creeper($chunk, $nbt);
		$co->setPosition($pos);
		$co->setMaxHealth($maxHealth);
		$co->setHealth($health);
		$this->finalizeNaturalMob($co);
		//$this->getLogger()->info("鐢熸垚浜嗕竴鍙嫤鍔涙€?);
	}
    public function spawnSquid(Position $pos, $maxHealth = 20, $health = 20) {
        $chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
        $nbt = $this->getNBT();
        $squ = new Squid($chunk, $nbt);
        $squ->setPosition($pos);
        $squ->setMaxHealth($maxHealth);
        $squ->setHealth($health);
        $this->finalizeNaturalMob($squ);
        //$this->getLogger()->info("鐢熸垚浜嗕竴鍙笨楸?);
    }
	public function spawnSkeleton(Position $pos, $maxHealth = 20, $health = 20) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$so = new Skeleton($chunk, $nbt);
		$so->setPosition($pos);
		$so->setMaxHealth($maxHealth);
		$so->setHealth($health);
		$this->finalizeNaturalMob($so);
		//$this->getLogger()->info("鐢熸垚浜嗕竴鍙楂?);
	}
	public function spawnStray(Position $pos, $maxHealth = 20, $health = 20) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$stray = new Stray($chunk, $nbt);
		$stray->setPosition($pos);
		$stray->setMaxHealth($maxHealth);
		$stray->setHealth($health);
		$this->finalizeNaturalMob($stray);
	}
	public function spawnSpider(Position $pos, $maxHealth = 16, $health = 16) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$so = new Spider($chunk, $nbt);
		$so->setPosition($pos);
		$so->setMaxHealth($maxHealth);
		$so->setHealth($health);
		$this->finalizeNaturalMob($so);
		//$this->getLogger()->info("鐢熸垚浜嗕竴鍙湗铔?);
	}
	public function spawnCow(Position $pos, $maxHealth = 8, $health = 8) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$coo = new Cow($chunk, $nbt);
		$coo->setPosition($pos);
		$coo->setMaxHealth($maxHealth);
		$coo->setHealth($health);
		$this->finalizeNaturalMob($coo);
		//$this->getLogger()->info("鐢熸垚浜嗕竴鍙墰");
	}
	public function spawnPig(Position $pos, $maxHealth = 10, $health = 10) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$po = new Pig($chunk, $nbt);
		$po->setPosition($pos);
		$po->setMaxHealth($maxHealth);
		$po->setHealth($health);
		$this->finalizeNaturalMob($po);
		//$this->getLogger()->info("鐢熸垚浜嗕竴鍙爆");
	}
	public function spawnSheep(Position $pos, $maxHealth = 8, $health = 8) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$sho = new Sheep($chunk, $nbt);
		$sho->setPosition($pos);
		$sho->setMaxHealth($maxHealth);
		$sho->setHealth($health);
		$this->finalizeNaturalMob($sho);
		//$this->getLogger()->info("鐢熸垚浜嗕竴鍙緤");
	}
	public function spawnChicken(Position $pos, $maxHealth = 4, $health = 4) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$cho = new Chicken($chunk, $nbt);
		$cho->setPosition($pos);
		$cho->setMaxHealth($maxHealth);
		$cho->setHealth($health);
		$this->finalizeNaturalMob($cho);
		//$this->getLogger()->info("鐢熸垚浜嗕竴鍙浮");
	}
    public function spawnRabbit(Position $pos, $maxHealth = 3, $health = 3) {
        $chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
        $nbt = $this->getNBT();
        $rab = new Rabbit($chunk, $nbt);
        $rab->setPosition($pos);
        $rab->setMaxHealth($maxHealth);
        $rab->setHealth($health);
        $this->finalizeNaturalMob($rab);
        //$this->getLogger()->info("鐢熸垚浜嗕竴鍙厰");
    }
	public function spawnHorse(Position $pos, $maxHealth = 30, $health = 30) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$horse = new Horse($chunk, $nbt);
		$horse->setPosition($pos);
		$horse->setMaxHealth($maxHealth);
		$horse->setHealth($health);
		$this->finalizeNaturalMob($horse);
	}
    public function spawnMooshroom(Position $pos, $maxHealth = 10, $health = 10) {
        $chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
        $nbt = $this->getNBT();
        $moo = new Mooshroom($chunk, $nbt);
        $moo->setPosition($pos);
        $moo->setMaxHealth($maxHealth);
        $moo->setHealth($health);
        $this->finalizeNaturalMob($moo);
        //$this->getLogger()->info("鐢熸垚浜嗕竴鍙槕鑿囩墰");
    }
	public function spawnWolf(Position $pos, $maxHealth = 8, $health = 8) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$wolf = new Wolf($chunk, $nbt);
		$wolf->setPosition($pos);
		$wolf->setMaxHealth($maxHealth);
		$wolf->setHealth($health);
		$this->finalizeNaturalMob($wolf);
	}
	public function spawnOcelot(Position $pos, $maxHealth = 10, $health = 10) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$ocelot = new Ocelot($chunk, $nbt);
		$ocelot->setPosition($pos);
		$ocelot->setMaxHealth($maxHealth);
		$ocelot->setHealth($health);
		$this->finalizeNaturalMob($ocelot);
	}
	public function spawnGhast(Position $pos, $maxHealth = 10, $health = 10) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$ghast = new Ghast($chunk, $nbt);
		$ghast->setPosition($pos);
		$ghast->setMaxHealth($maxHealth);
		$ghast->setHealth($health);
		$this->finalizeNaturalMob($ghast);
	}
	public function spawnBat(Position $pos, $maxHealth = 6, $health = 6) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$bat = new Bat($chunk, $nbt);
		$bat->setPosition($pos);
		$bat->setMaxHealth($maxHealth);
		$bat->setHealth($health);
		$this->finalizeNaturalMob($bat);
	}
	public function spawnVillager(Position $pos, $maxHealth = 20, $health = 20) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$villager = new Villager($chunk, $nbt);
		$villager->setPosition($pos);
		$villager->setMaxHealth($maxHealth);
		$villager->setHealth($health);
		$this->finalizeNaturalMob($villager);
	}
	public function spawnIronGolem(Position $pos, $maxHealth = 100, $health = 100) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$golem = new IronGolem($chunk, $nbt);
		$golem->setPosition($pos);
		$golem->setMaxHealth($maxHealth);
		$golem->setHealth($health);
		$this->finalizeNaturalMob($golem);
	}
    public function spawnCaveSpider(Position $pos, $maxHealth = 12, $health = 12) {
        $chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
        $nbt = $this->getNBT();
        $cspi = new CaveSpider($chunk, $nbt);
        $cspi->setPosition($pos);
        $cspi->setMaxHealth($maxHealth);
        $cspi->setHealth($health);
        $this->finalizeNaturalMob($cspi);
        //$this->getLogger()->info("鐢熸垚浜嗕竴鍙礊绌磋湗铔?);
    }
    public function spawnBlaze(Position $pos, $maxHealth = 20, $health = 20) {
        $chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
        $nbt = $this->getNBT();
        $bla = new Blaze($chunk, $nbt);
        $bla->setPosition($pos);
        $bla->setMaxHealth($maxHealth);
        $bla->setHealth($health);
        $this->finalizeNaturalMob($bla);
        //$this->getLogger()->info("鐢熸垚浜嗕竴鍙儓鐒颁汉");
    }
    public function spawnLavaSlime(Position $pos, $maxHealth = 10, $health = 10) {
        $chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
        $nbt = $this->getNBT();
        $lav = new LavaSlime($chunk, $nbt);
        $lav->setPosition($pos);
        $lav->setMaxHealth($maxHealth);
        $lav->setHealth($health);
        $this->finalizeNaturalMob($lav);
        //$this->getLogger()->info("鐢熸垚浜嗕竴鍙博娴嗗彶鑾卞");
    }
    public function spawnSlime(Position $pos, $maxHealth = 16, $health = 16) {
        $chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
        $nbt = $this->getNBT();
        $slime = new Slime($chunk, $nbt);
        $slime->setPosition($pos);
        $slime->setMaxHealth($maxHealth);
        $slime->setHealth($health);
        $this->finalizeNaturalMob($slime);
    }
    public function spawnEnderman(Position $pos, $maxHealth = 40, $health = 40) {
        $chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
        $nbt = $this->getNBT();
        $enderman = new Enderman($chunk, $nbt);
        $enderman->setPosition($pos);
        $enderman->setMaxHealth($maxHealth);
        $enderman->setHealth($health);
        $this->finalizeNaturalMob($enderman);
    }
    public function spawnWitch(Position $pos, $maxHealth = 26, $health = 26) {
        $chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
        $nbt = $this->getNBT();
        $witch = new Witch($chunk, $nbt);
        $witch->setPosition($pos);
        $witch->setMaxHealth($maxHealth);
        $witch->setHealth($health);
        $this->finalizeNaturalMob($witch);
    }
    public function spawnSilverfish(Position $pos, $maxHealth = 8, $health = 8) {
        $chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
        $nbt = $this->getNBT();
        $silverfish = new Silverfish($chunk, $nbt);
        $silverfish->setPosition($pos);
        $silverfish->setMaxHealth($maxHealth);
        $silverfish->setHealth($health);
        $this->finalizeNaturalMob($silverfish);
    }
	/**
	 * @param Player $player
	 * @param        $damage
	 * @return float
	 * 鏍规嵁鐜╁鐨勮澶囪幏鍙栫帺瀹跺簲鍙楀埌鐨勪激瀹冲€?	 */
	public function getPlayerDamage(Player $player, $damage) {
		$armorValues = [
					Item::LEATHER_CAP => 1,
					Item::LEATHER_TUNIC => 3,
					Item::LEATHER_PANTS => 2,
					Item::LEATHER_BOOTS => 1,
					Item::CHAIN_HELMET => 1,
					Item::CHAIN_CHESTPLATE => 5,
					Item::CHAIN_LEGGINGS => 4,
					Item::CHAIN_BOOTS => 1,
					Item::GOLD_HELMET => 1,
					Item::GOLD_CHESTPLATE => 5,
					Item::GOLD_LEGGINGS => 3,
					Item::GOLD_BOOTS => 1,
					Item::IRON_HELMET => 2,
					Item::IRON_CHESTPLATE => 6,
					Item::IRON_LEGGINGS => 5,
					Item::IRON_BOOTS => 2,
					Item::DIAMOND_HELMET => 3,
					Item::DIAMOND_CHESTPLATE => 8,
					Item::DIAMOND_LEGGINGS => 6,
					Item::DIAMOND_BOOTS => 3,
				];
		$points = 0;
		foreach($player->getInventory()->getArmorContents() as $index => $i) {
			if(isset($armorValues[$i->getId()])) {
				$points += $armorValues[$i->getId()];
			}
		}
		$damage = floor($damage - $points * 0.04);
		if($damage < 0) {
			$damage = 0;
		}
		return $damage;
	}
	/**
	 * @return CompoundTag
	 * 杩斿洖涓€涓┖鐨勫疄浣撻€氱敤NBT
	 */
	public function getNBT() : CompoundTag {
		$nbt = new CompoundTag("", [
					"Pos" => new ListTag("Pos", [
						new DoubleTag("", 0),
						new DoubleTag("", 0),
						new DoubleTag("", 0)
					]),
					"Motion" => new ListTag("Motion", [
						new DoubleTag("", 0),
						new DoubleTag("", 0),
						new DoubleTag("", 0)
					]),
					"Rotation" => new ListTag("Rotation", [
						new FloatTag("", 0),
						new FloatTag("", 0)
					]),
				]);
		return $nbt;
	}
	/**
	 * @param Position $pos
	 * @return int
	 * 鑾峰彇鏌愬潗鏍?浣嶇疆)鐨勪寒搴?	 */
	public function getLight(Position $pos) {
		$chunk = $pos->getLevel()->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$l = 0;
		if($chunk instanceof FullChunk) {
			$l = $chunk->getBlockSkyLight($pos->x & 0x0f, $pos->y & 0x7f, $pos->z & 0x0f);
			if($l < 15) {
				//$l = \max($chunk->getBlockLight($pos->x & 0x0f, $pos->y & 0x7f, $pos->z & 0x0f));
				$l = $chunk->getBlockLight($pos->x & 0x0f, $pos->y & 0x7f, $pos->z & 0x0f);
			}
		}
		return $l;
	}

	public function canSpawnHostileAt(Position $pos){
		$level = $pos->getLevel();
		if(!($level instanceof Level)){
			return false;
		}

		return NaturalMobSpawnRules::canSpawnHostileAt($level, $pos);
	}

	private function isNaturalSpawnLevel(Level $level) : bool{
		return $level->getDimension() === Level::DIMENSION_NORMAL || $level->getDimension() === Level::DIMENSION_NETHER || $level->getDimension() === Level::DIMENSION_END;
	}

	private function isNaturalSpawnChunkReady(Level $level, int $x, int $z) : bool{
		$chunk = $level->getChunk($x >> 4, $z >> 4, false);
		return $chunk instanceof FullChunk && $chunk->isGenerated() && $chunk->isPopulated();
	}

	private function isInGeneratedVillage(Level $level, Position $pos) : bool{
		return VillagePopulator::findGeneratedVillageCenter(
			$level,
			(int) floor($pos->x),
			(int) floor($pos->z),
			96
		) !== null;
	}

	private function countNaturalEntities(Level $level, int $entityId) : int{
		$count = 0;
		foreach($level->getEntities() as $entity){
			if($entity instanceof Entity && $entity::NETWORK_ID === $entityId){
				++$count;
			}
		}

		return $count;
	}

	private function getConfiguredNaturalSpawnLimits() : array{
		return [
			NaturalMobSpawnRules::SPAWN_CATEGORY_MONSTERS => (int) $this->server->getProperty("spawn-limits.monsters", 70),
			NaturalMobSpawnRules::SPAWN_CATEGORY_ANIMALS => (int) $this->server->getProperty("spawn-limits.animals", 15),
			NaturalMobSpawnRules::SPAWN_CATEGORY_WATER_ANIMALS => (int) $this->server->getProperty("spawn-limits.water-animals", 5),
			NaturalMobSpawnRules::SPAWN_CATEGORY_AMBIENT => (int) $this->server->getProperty("spawn-limits.ambient", 15)
		];
	}

	private function countNaturalEntitiesByCategory(Level $level, string $category) : int{
		$count = 0;
		foreach($level->getEntities() as $entity){
			if($entity instanceof Entity && NaturalMobSpawnRules::getEntitySpawnCategory((int) $entity::NETWORK_ID) === $category){
				++$count;
			}
		}

		return $count;
	}

	private function getNaturalEntityGenerateBlockReason(Level $level, int $entityId, bool $inVillage = false) : string{
		if($entityId <= 0){
			return "invalid_entity";
		}

		$limit = NaturalMobSpawnRules::getEntitySpawnLimit(
			$entityId,
			$inVillage,
			$this->server->getDifficulty(),
			$this->getConfiguredNaturalSpawnLimits()
		);
		if($limit <= 0){
			return "limit_zero_entity_" . $entityId;
		}

		if($inVillage && ($entityId === Villager::NETWORK_ID || $entityId === IronGolem::NETWORK_ID)){
			$count = $this->countNaturalEntities($level, $entityId);
			return $count < $limit ? "" : "village_cap_entity_" . $entityId . "_" . $count . "_of_" . $limit;
		}

		$category = NaturalMobSpawnRules::getEntitySpawnCategory($entityId);
		if($category === ""){
			return "no_spawn_category_entity_" . $entityId;
		}

		$count = $this->countNaturalEntitiesByCategory($level, $category);
		return $count < $limit ? "" : $category . "_cap_" . $count . "_of_" . $limit;
	}

	private function canGenerateNaturalEntity(Level $level, int $entityId, bool $inVillage = false) : bool{
		return $this->getNaturalEntityGenerateBlockReason($level, $entityId, $inVillage) === "";
	}

	private function generateNaturalEntity(int $entityId, Position $pos, bool $inVillage = false, &$debugStats = null) : bool{
		$blockReason = $this->getNaturalEntityGenerateBlockReason($pos->getLevel(), $entityId, $inVillage);
		if($blockReason !== ""){
			$this->recordNaturalMobSpawnDebugReason($debugStats, $blockReason);
			return false;
		}

		$ev = new EntityGenerateEvent($pos, $entityId, EntityGenerateEvent::CAUSE_AI_HOLDER);
		$this->server->getPluginManager()->callEvent($ev);
		if($ev->isCancelled()){
			$this->recordNaturalMobSpawnDebugReason($debugStats, "event_cancelled_entity_" . $entityId);
			return false;
		}

		if(!$this->spawnNaturalEntity($entityId, $ev->getPosition())){
			$this->recordNaturalMobSpawnDebugReason($debugStats, "spawn_method_failed_entity_" . $entityId);
			return false;
		}

		$this->recordNaturalMobSpawnDebugCount($debugStats, "spawned");
		$this->logNaturalMobSpawnDebug("spawned entity=" . $entityId . " " . $this->getPositionDebugText($ev->getPosition()));
		return true;
	}

	private function generateNaturalEntityGroup(array $entityIds, Position $center, bool $inVillage = false, &$debugStats = null) : bool{
		$spawned = false;
		$index = 0;
		foreach($entityIds as $entityId){
			$pos = $index === 0 ? $center : $this->findNaturalGroupSpawnPosition($center, $entityId, $index, $inVillage);
			++$index;
			if(!($pos instanceof Position)){
				$this->recordNaturalMobSpawnDebugReason($debugStats, "group_position_blocked_entity_" . $entityId);
				continue;
			}
			if($this->generateNaturalEntity((int) $entityId, $pos, $inVillage, $debugStats)){
				$spawned = true;
			}
		}

		return $spawned;
	}

	private function isNaturalEntityGroupHostile(array $entityIds) : bool{
		foreach($entityIds as $entityId){
			if(NaturalMobSpawnRules::isHostileEntityId((int) $entityId)){
				return true;
			}
		}

		return false;
	}

	private function isNaturalLandEntityGroupAllowedForMode(array $entityIds, int $spawnMode) : bool{
		if($spawnMode === self::NATURAL_LAND_SPAWN_ANY){
			return true;
		}

		$isHostile = $this->isNaturalEntityGroupHostile($entityIds);
		if($spawnMode === self::NATURAL_LAND_SPAWN_HOSTILE){
			return $isHostile;
		}
		if($spawnMode === self::NATURAL_LAND_SPAWN_FRIENDLY){
			return !$isHostile;
		}

		return true;
	}

	private function findNaturalGroupSpawnPosition(Position $center, int $entityId, int $index, bool $inVillage = false){
		$level = $center->getLevel();
		$baseX = (int) floor($center->x);
		$baseY = (int) floor($center->y);
		$baseZ = (int) floor($center->z);
		$offsets = [
			[1, 0],
			[-1, 0],
			[0, 1],
			[0, -1],
			[2, 1],
			[-2, -1],
			[1, -2],
			[-1, 2],
			[2, -2],
			[-2, 2]
		];
		$count = count($offsets);

		for($i = 0; $i < $count; ++$i){
			$offset = $offsets[($index + $i - 1) % $count];
			$x = $baseX + $offset[0];
			$z = $baseZ + $offset[1];
			for($y = $baseY - 2; $y <= $baseY + 2; ++$y){
				if($this->whatBlock($level, new Vector3($x, $y - 1, $z)) !== "block"){
					continue;
				}
				if($this->whatBlock($level, new Vector3($x, $y, $z)) !== "air"){
					continue;
				}
				if($this->whatBlock($level, new Vector3($x, $y + 1, $z)) !== "air"){
					continue;
				}

				$standingY = $this->getAiSupportStandingY($level, $x, $y - 1, $z, $y);
				$pos = new Position($x + 0.5, $standingY, $z + 0.5, $level);
				if(NaturalMobSpawnRules::canSpawnSelectedLandEntityAt($level, $pos, $entityId, $inVillage)){
					return $pos;
				}
			}
		}

		return null;
	}

	private function spawnNaturalEntity(int $entityId, Position $pos) : bool{
		switch($entityId){
			case Zombie::NETWORK_ID:
				$this->spawnZombie($pos);
				return true;
			case Husk::NETWORK_ID:
				$this->spawnHusk($pos);
				return true;
			case ZombieVillager::NETWORK_ID:
				$this->spawnZombieVillager($pos);
				return true;
			case Skeleton::NETWORK_ID:
				$this->spawnSkeleton($pos);
				return true;
			case Stray::NETWORK_ID:
				$this->spawnStray($pos);
				return true;
			case Creeper::NETWORK_ID:
				$this->spawnCreeper($pos);
				return true;
			case Spider::NETWORK_ID:
				$this->spawnSpider($pos);
				return true;
			case CaveSpider::NETWORK_ID:
				$this->spawnCaveSpider($pos);
				return true;
			case Slime::NETWORK_ID:
				$this->spawnSlime($pos);
				return true;
			case Enderman::NETWORK_ID:
				$this->spawnEnderman($pos);
				return true;
			case Witch::NETWORK_ID:
				$this->spawnWitch($pos);
				return true;
			case PigZombie::NETWORK_ID:
				$this->spawnPigZombie($pos);
				return true;
			case LavaSlime::NETWORK_ID:
				$this->spawnLavaSlime($pos);
				return true;
			case Blaze::NETWORK_ID:
				$this->spawnBlaze($pos);
				return true;
			case Ghast::NETWORK_ID:
				$this->spawnGhast($pos);
				return true;
			case Bat::NETWORK_ID:
				$this->spawnBat($pos);
				return true;
			case Silverfish::NETWORK_ID:
				$this->spawnSilverfish($pos);
				return true;
			case Cow::NETWORK_ID:
				$this->spawnCow($pos);
				return true;
			case Pig::NETWORK_ID:
				$this->spawnPig($pos);
				return true;
			case Sheep::NETWORK_ID:
				$this->spawnSheep($pos);
				return true;
			case Chicken::NETWORK_ID:
				$this->spawnChicken($pos);
				return true;
			case Rabbit::NETWORK_ID:
				$this->spawnRabbit($pos);
				return true;
			case Horse::NETWORK_ID:
				$this->spawnHorse($pos);
				return true;
			case Mooshroom::NETWORK_ID:
				$this->spawnMooshroom($pos);
				return true;
			case Ocelot::NETWORK_ID:
				$this->spawnOcelot($pos);
				return true;
			case Squid::NETWORK_ID:
				$this->spawnSquid($pos);
				return true;
			case Wolf::NETWORK_ID:
				$this->spawnWolf($pos);
				return true;
			case Villager::NETWORK_ID:
				$this->spawnVillager($pos);
				return true;
			case IronGolem::NETWORK_ID:
				$this->spawnIronGolem($pos);
				return true;
			default:
				return false;
		}
	}

	private function tryGenerateWaterMobNear(Player $player, Level $level, &$debugStats = null) : bool{
		$this->recordNaturalMobSpawnDebugCount($debugStats, "waterAttempts");
		$x = (int) floor($player->x + mt_rand(-$this->birth_r, $this->birth_r));
		$z = (int) floor($player->z + mt_rand(-$this->birth_r, $this->birth_r));
		if(!$this->isNaturalSpawnChunkReady($level, $x, $z)){
			$this->recordNaturalMobSpawnDebugReason($debugStats, "water_chunk_not_ready");
			return false;
		}

		for($y = 62; $y >= 46; --$y){
			$v = new Vector3($x, $y, $z);
			if($this->whatBlock($level, $v) === "water"){
				$pos = new Position($x, $y, $z, $level);
				$entityId = NaturalMobSpawnRules::selectWaterEntityIdForPosition($level, $pos);
				if($entityId <= 0){
					$this->recordNaturalMobSpawnDebugReason($debugStats, "water_no_entity_selected");
					return false;
				}
				return $this->generateNaturalEntity($entityId, $pos, false, $debugStats);
			}
		}

		$this->recordNaturalMobSpawnDebugReason($debugStats, "water_no_column");
		return false;
	}
	/******** API缁撴潫 浠ヤ笅涓鸿鏃跺櫒 *****************************/
	/**
	 * @param Entity $entity
	 * @return bool
	 * 鍒ゆ柇鏌愮敓鐗╁懆杈?2鏍煎唴鏄惁鏈夌帺瀹跺瓨鍦?	 * 鎺у埗鍍靛案鏄惁绉诲姩锛堣嚜鐢辫璧版ā寮忥級
	 */
	private function tryGenerateAirMobNear(Player $player, Level $level, &$debugStats = null) : bool{
		$this->recordNaturalMobSpawnDebugCount($debugStats, "airAttempts");
		$x = (int) floor($player->getX() + mt_rand(-$this->birth_r, $this->birth_r));
		$z = (int) floor($player->getZ() + mt_rand(-$this->birth_r, $this->birth_r));
		$baseY = (int) floor($player->getY());
		if(!$this->isNaturalSpawnChunkReady($level, $x, $z)){
			$this->recordNaturalMobSpawnDebugReason($debugStats, "air_chunk_not_ready");
			return false;
		}

		for($y = $baseY - 8; $y <= $baseY + 16; ++$y){
			if($this->whatBlock($level, new Vector3($x, $y, $z)) !== "air"){
				$this->recordNaturalMobSpawnDebugReason($debugStats, "air_space_blocked");
				continue;
			}
			if($this->whatBlock($level, new Vector3($x, $y + 1, $z)) !== "air"){
				$this->recordNaturalMobSpawnDebugReason($debugStats, "air_head_blocked");
				continue;
			}

			$pos = new Position($x + 0.5, $y, $z + 0.5, $level);
			$entityId = NaturalMobSpawnRules::selectAirEntityId($level, $pos);
			if($entityId <= 0){
				$this->recordNaturalMobSpawnDebugReason($debugStats, "air_no_entity_selected");
				continue;
			}

			return $this->generateNaturalEntity($entityId, $pos, false, $debugStats);
		}

		$this->recordNaturalMobSpawnDebugReason($debugStats, "air_no_space");
		return false;
	}

	private function tryGenerateCaveMobNear(Player $player, Level $level, &$debugStats = null) : bool{
		$this->recordNaturalMobSpawnDebugCount($debugStats, "caveAttempts");
		$x = (int) floor($player->getX() + mt_rand(-$this->birth_r, $this->birth_r));
		$z = (int) floor($player->getZ() + mt_rand(-$this->birth_r, $this->birth_r));
		$baseY = min((int) floor($player->getY()), 62);
		if(!$this->isNaturalSpawnChunkReady($level, $x, $z)){
			$this->recordNaturalMobSpawnDebugReason($debugStats, "cave_chunk_not_ready");
			return false;
		}

		for($y = max(1, $baseY - 20); $y <= min(62, $baseY + 10); ++$y){
			if($this->whatBlock($level, new Vector3($x, $y, $z)) !== "air"){
				$this->recordNaturalMobSpawnDebugReason($debugStats, "cave_space_blocked");
				continue;
			}

			$pos = new Position($x + 0.5, $y, $z + 0.5, $level);
			$entityId = NaturalMobSpawnRules::selectCaveEntityId($level, $pos);
			if($entityId <= 0){
				$this->recordNaturalMobSpawnDebugReason($debugStats, "cave_no_entity_selected");
				return false;
			}

			return $this->generateNaturalEntity($entityId, $pos, false, $debugStats);
		}

		$this->recordNaturalMobSpawnDebugReason($debugStats, "cave_no_space");
		return false;
	}

	private function tryGenerateLandMobNear(Player $player, Level $level, &$debugStats = null, int $spawnMode = self::NATURAL_LAND_SPAWN_ANY) : bool{
		$this->recordNaturalMobSpawnDebugCount($debugStats, "landAttempts");
		$x = (int) floor($player->getX() + mt_rand(-$this->birth_r, $this->birth_r));
		$z = (int) floor($player->getZ() + mt_rand(-$this->birth_r, $this->birth_r));
		if(!$this->isNaturalSpawnChunkReady($level, $x, $z)){
			$this->recordNaturalMobSpawnDebugReason($debugStats, "land_chunk_not_ready");
			return false;
		}
		$candidates = $this->getNaturalLandSpawnGroundCandidates($player, $level, $x, $z);
		if($this->isNaturalMobSpawnDebugEnabled()){
			$this->addNaturalMobSpawnDebugSample($debugStats, "landSamples", $this->createNaturalLandSpawnDebugSample($player, $level, $x, $z, $candidates));
		}
		foreach($candidates as $y){
			if($y < 1 || $y > 125){
				$this->recordNaturalMobSpawnDebugReason($debugStats, "land_y_out_of_range");
				continue;
			}
			if($this->whatBlock($level, new Vector3($x, $y, $z)) !== "block"){
				$this->recordNaturalMobSpawnDebugReason($debugStats, "land_ground_not_block");
				continue;
			}
			if($this->whatBlock($level, new Vector3($x, $y + 1, $z)) !== "air"){
				$this->recordNaturalMobSpawnDebugReason($debugStats, "land_feet_blocked");
				continue;
			}
			if($this->whatBlock($level, new Vector3($x, $y + 2, $z)) !== "air"){
				$this->recordNaturalMobSpawnDebugReason($debugStats, "land_head_blocked");
				continue;
			}

			$standingY = $this->getAiSupportStandingY($level, $x, $y, $z, $y + 1);
			$pos = new Position($x + 0.5, $standingY, $z + 0.5, $level);
			$inVillage = $this->isInGeneratedVillage($level, $pos);
			if($spawnMode === self::NATURAL_LAND_SPAWN_HOSTILE && !NaturalMobSpawnRules::canSpawnHostileAt($level, $pos) && !NaturalMobSpawnRules::canSpawnDaytimeDesertHuskAt($level, $pos)){
				$this->recordNaturalMobSpawnDebugReason($debugStats, "land_hostile_conditions_not_met");
				continue;
			}
			if($spawnMode === self::NATURAL_LAND_SPAWN_FRIENDLY && !NaturalMobSpawnRules::canSpawnPassiveAt($level, $pos)){
				$this->recordNaturalMobSpawnDebugReason($debugStats, "land_friendly_conditions_not_met");
				continue;
			}
			$entityIds = NaturalMobSpawnRules::selectLandEntityGroupIds($level, $pos, $inVillage);
			if(count($entityIds) <= 0){
				$this->recordNaturalMobSpawnDebugReason($debugStats, "land_no_entity_selected");
				continue;
			}
			if(!$this->isNaturalLandEntityGroupAllowedForMode($entityIds, $spawnMode)){
				$this->recordNaturalMobSpawnDebugReason($debugStats, $this->isNaturalEntityGroupHostile($entityIds) ? "land_hostile_skipped" : "land_friendly_skipped");
				continue;
			}
			if($this->generateNaturalEntityGroup($entityIds, $pos, $inVillage, $debugStats)){
				return true;
			}
			$this->recordNaturalMobSpawnDebugReason($debugStats, "land_group_failed");
		}

		$this->recordNaturalMobSpawnDebugReason($debugStats, "land_no_valid_position");
		return false;
	}

	private function getNaturalLandSpawnGroundCandidates(Player $player, Level $level, int $x, int $z) : array{
		$candidates = [];
		$seen = [];
		$add = function(int $y) use (&$candidates, &$seen){
			if($y < 1 || $y > 125 || isset($seen[$y])){
				return;
			}
			$seen[$y] = true;
			$candidates[] = $y;
		};

		if(!$this->isNaturalSpawnChunkReady($level, $x, $z)){
			return [];
		}

		if(method_exists($level, "getHighestBlockAt")){
			$highest = (int) $level->getHighestBlockAt($x, $z);
			for($y = min(125, $highest); $y >= max(1, $highest - 16); --$y){
				$add($y);
			}
		}

		$baseY = (int) floor($player->getY());
		for($y = max(1, $baseY - 20); $y <= min(125, $baseY + 20); ++$y){
			$add($y);
		}

		return $candidates;
	}

	public function willMove(Entity $entity) {
		foreach($entity->getViewers() as $viewer) {
			if($entity->distance($viewer->getLocation()) <= 32) return true;
		}
		return false;
	}

	private function getTrackedEntityArrayName(Entity $entity) : string{
		if($entity instanceof ZombieVillager){
			return "ZombieVillager";
		}
		if($entity instanceof Zombie){
			return "Zombie";
		}
		if($entity instanceof PigZombie){
			return "PigZombie";
		}
		if($entity instanceof Creeper){
			return "Creeper";
		}
		if($entity instanceof Skeleton){
			return "Skeleton";
		}
		if($entity instanceof Cow){
			return "Cow";
		}
		if($entity instanceof Squid){
			return "Squid";
		}
		if($entity instanceof Pig){
			return "Pig";
		}
		if($entity instanceof Sheep){
			return "Sheep";
		}
		if($entity instanceof Chicken){
			return "Chicken";
		}
		if($entity instanceof CaveSpider){
			return "CaveSpider";
		}
		if($entity instanceof Spider){
			return "Spider";
		}
		if($entity instanceof Slime){
			return "Slime";
		}
		if($entity instanceof Enderman){
			return "Enderman";
		}
		if($entity instanceof Witch){
			return "Witch";
		}
		if($entity instanceof Ghast){
			return "Ghast";
		}
		if($entity instanceof Bat){
			return "Bat";
		}
		if($entity instanceof Silverfish){
			return "Silverfish";
		}
		if($entity instanceof Ocelot){
			return "Ocelot";
		}
		if($entity instanceof Mooshroom){
			return "Mooshroom";
		}
		if($entity instanceof Rabbit){
			return "Rabbit";
		}
		if($entity instanceof Wolf){
			return "Wolf";
		}
		if($entity instanceof Villager){
			return "Villager";
		}
		if($entity instanceof IronGolem){
			return "IronGolem";
		}
		if($entity instanceof Blaze){
			return "Blaze";
		}
		if($entity instanceof LavaSlime){
			return "LavaSlime";
		}

		return "";
	}

	private function getTrackedEntityArrayNames() : array{
		return [
			"ZombieVillager",
			"PigZombie",
			"CaveSpider",
			"LavaSlime",
			"Zombie",
			"Creeper",
			"Skeleton",
			"Cow",
			"Squid",
			"Pig",
			"Sheep",
			"Chicken",
			"Spider",
			"Slime",
			"Enderman",
			"Witch",
			"Ghast",
			"Bat",
			"Silverfish",
			"Ocelot",
			"Mooshroom",
			"Blaze",
			"Rabbit",
			"Wolf",
			"Villager",
			"IronGolem"
		];
	}

	public function RotationTimer() {
		foreach($this->getServer()->getLevels() as $level) {
			foreach($level->getEntities() as $entity) {
				if($entity instanceof \lycore\entity\Mob and $entity->usesPm1eGroundAi()) {
					continue;
				}
				$arrayName = $this->getTrackedEntityArrayName($entity);
				if($arrayName !== "") {
					if(count($entity->getViewers()) != 0) {
						$array = &$this->{$arrayName};
						if(isset($array[$entity->getId()])) {
							$yaw0 = $entity->yaw;
							//瀹為檯yaw
							$yaw = $array[$entity->getId()]['yaw'];
							//鐩爣yaw
							//$this->getLogger()->info($yaw0.' '.$yaw);
							if(abs($yaw0 - $yaw) <= 180) {
								//-180鍒?180姝ｆ柟鍚?
								if($yaw0 <= $yaw) {
									//瀹為檯鍦ㄧ洰鏍囧乏杈?
									if($yaw - $yaw0 <= 15) {
										$yaw0 = $yaw;
									} else {
										$yaw0 += 15;
									}
								} else {
									////瀹為檯鍦ㄧ洰鏍囧彸杈?
									if($yaw0 - $yaw <= 15) {
										$yaw0 = $yaw;
									} else {
										$yaw0 -= 15;
									}
								}
							} else {
								////+180鍒?180鏂瑰悜
								if($yaw0 >= $yaw) {
									//瀹為檯鍦ㄧ洰鏍囧乏杈?
									if((180 - $yaw0) + ($yaw + 180) <= 15) {
										$yaw0 = $yaw;
									} else {
										$yaw0 += 15;
										if($yaw0 >= 180) $yaw0 = $yaw0 - 360;
									}
								} else {
									////瀹為檯鍦ㄧ洰鏍囧彸杈?
									if((180 - $yaw) - ($yaw0 + 180) <= 15) {
										$yaw0 = $yaw;
									} else {
										$yaw0 -= 15;
										if($yaw0 <= 180) $yaw0 = $yaw0 + 360;
									}
								}
							}
							$pitch0 = $entity->pitch;
							//瀹為檯pitch
							$pitch = $array[$entity->getId()]['pitch'];
							//鐩爣pitch
							if(abs($pitch0 - $pitch) <= 15) {
								$pitch0 = $pitch;
							} elseif($pitch > $pitch0) {
								$pitch0 += 10;
							} elseif($pitch < $pitch0) {
								$pitch0 -= 10;
							}
							$entity->setRotation($yaw0, $pitch0);
							//$this->RotateHead($entity,$yaw);
						}
					}
				}
			}
		}
	}
	/**
	 * @param $mx
	 * @param $mz
	 * @return float|int
	 * 鑾峰彇yaw瑙掑害
	 */
	public function getyaw($mx, $mz) {
		//鏍规嵁motion璁＄畻杞悜瑙掑害
		//杞悜璁＄畻
		if($mz == 0) {
			//鏂滅巼涓嶅瓨鍦?
			if($mx < 0) {
				$yaw = -90;
			} else {
				$yaw = 90;
			}
		} else {
			//瀛樺湪鏂滅巼
			if($mx >= 0 and $mz > 0) {
				//绗竴璞￠檺
				$atan = atan($mx / $mz);
				$yaw = rad2deg($atan);
			} elseif($mx >= 0 and $mz < 0) {
				//绗簩璞￠檺
				$atan = atan($mx / abs($mz));
				$yaw = 180 - rad2deg($atan);
			} elseif($mx < 0 and $mz < 0) {
				//绗笁璞￠檺
				$atan = atan($mx / $mz);
				$yaw = -(180 - rad2deg($atan));
			} elseif($mx < 0 and $mz > 0) {
				//绗洓璞￠檺
				$atan = atan(abs($mx) / $mz);
				$yaw = -(rad2deg($atan));
			} else {
				$yaw = 0;
			}
		}
		$yaw = -$yaw;
		return $yaw;
	}
	/**
	 * @param Vector3 $from
	 * @param Vector3 $to
	 * @return float|int
	 * 鑾峰彇pitch瑙掑害
	 */
	public function getpitch(Vector3 $from, Vector3 $to) {
		$distance = $from->distance($to);
		$height = $to->y - $from->y;
		if($height > 0) {
			return -rad2deg(asin($height / $distance));
		} elseif($height < 0) {
			return rad2deg(asin(-$height / $distance));
		} else {
			return 0;
		}
	}

	protected function getAiBlockStandingY(Level $level, int $x, int $y, int $z){
		$block = $level->getBlock(new Vector3($x, $y, $z));
		if($block->canPassThrough()){
			return null;
		}

		$bb = $block->getBoundingBox();
		return $bb !== null ? (float) $bb->maxY : null;
	}

	protected function getAiSupportStandingY(Level $level, int $x, int $y, int $z, $fallback){
		$standingY = $this->getAiBlockStandingY($level, $x, $y, $z);
		return $standingY !== null ? $standingY : $fallback;
	}

	public function getEntityPositionOnGroundSupport(Entity $entity, Vector3 $position) : Vector3{
		$level = $entity->getLevel();
		if(!($level instanceof Level)){
			return $position;
		}

		$supportY = $this->findEntityGroundSupportYForPosition($entity, $level, $position);
		if($supportY === null){
			return $position;
		}

		return new Vector3($position->x, $supportY, $position->z);
	}

	public function setEntityPositionOnGroundSupport(Entity $entity, Vector3 $position) : Vector3{
		$position = $this->getEntityPositionOnGroundSupport($entity, $position);
		$entity->setPosition($position);
		return $position;
	}

	protected function findEntityGroundSupportYForPosition(Entity $entity, Level $level, Vector3 $position){
		$halfWidth = max(0.001, $entity->width / 2);
		$minX = (int) floor($position->x - $halfWidth);
		$maxX = (int) floor($position->x + $halfWidth);
		$minZ = (int) floor($position->z - $halfWidth);
		$maxZ = (int) floor($position->z + $halfWidth);
		$baseY = (int) floor($position->y);
		$maxRecoverDistance = 1.0 + Entity::PARTIAL_BLOCK_GROUND_EPSILON;
		$highestTop = null;

		for($x = $minX; $x <= $maxX; ++$x){
			for($z = $minZ; $z <= $maxZ; ++$z){
				for($y = $baseY - 1; $y <= $baseY + 1; ++$y){
					$block = $level->getBlock(new Vector3($x, $y, $z));
					if($block->canPassThrough()){
						continue;
					}

					$bb = $block->getBoundingBox();
					if($bb === null){
						continue;
					}

					if($position->x + $halfWidth <= $bb->minX or $position->x - $halfWidth >= $bb->maxX or $position->z + $halfWidth <= $bb->minZ or $position->z - $halfWidth >= $bb->maxZ){
						continue;
					}

					$rise = $bb->maxY - $position->y;
					if($position->y < $bb->minY - Entity::PARTIAL_BLOCK_GROUND_EPSILON or $rise > $maxRecoverDistance + Entity::THIN_BLOCK_GROUND_EPSILON){
						continue;
					}

					if($rise > Entity::PARTIAL_BLOCK_GROUND_EPSILON and ($bb->maxY - $bb->minY) >= 1.0 - Entity::THIN_BLOCK_GROUND_EPSILON){
						continue;
					}

					if($rise < -Entity::PARTIAL_BLOCK_GROUND_EPSILON){
						continue;
					}

					if(!$this->canEntityOccupyGroundSupport($entity, $level, $position, $bb->maxY)){
						continue;
					}

					$highestTop = $highestTop === null ? $bb->maxY : max($highestTop, $bb->maxY);
				}
			}
		}

		return $highestTop;
	}

	protected function canEntityOccupyGroundSupport(Entity $entity, Level $level, Vector3 $position, $standingY) : bool{
		$halfWidth = max(0.001, $entity->width / 2);
		$height = max(0.001, $entity->height);
		$bb = new AxisAlignedBB(
			$position->x - $halfWidth,
			$standingY,
			$position->z - $halfWidth,
			$position->x + $halfWidth,
			$standingY + $height,
			$position->z + $halfWidth
		);

		return count($level->getCollisionCubes($entity, $bb, false)) === 0;
	}

	protected function getAiMovementYForSupport(Level $level, int $x, int $y, int $z, $fallback){
		$standingY = $this->getAiBlockStandingY($level, $x, $y, $z);
		return $standingY !== null ? $standingY + 1.0 : $fallback;
	}

	protected function resolveAiStandYForCurrentBlock(Level $level, int $x, int $y, int $z){
		$standingY = $this->getAiBlockStandingY($level, $x, $y, $z);
		if($standingY === null or $standingY <= $y + 0.0001 or $standingY >= $y + 0.9999){
			return null;
		}

		if($this->whatBlock($level, new Vector3($x, $y + 1, $z)) !== "air"){
			return null;
		}

		if($this->whatBlock($level, new Vector3($x, $y + 2, $z)) !== "air"){
			return null;
		}

		return $standingY + 1.0;
	}

	/**
	 * @param Level $level
	 * @param Vector3 $v3
	 * @param bool $hate
	 * @param bool $reason
	 * @return bool|float|string
	 * 鍒ゆ柇鏌愬潗鏍囨槸鍚﹀彲浠ヨ璧?	 * 骞剁粰鍑哄師鍥?     */
	public function ifjump(Level $level, Vector3 $v3, $hate = false, $reason = false) {
		//boybook Y杞寸畻娉曟牳蹇冨嚱鏁?
		$x = floor($v3->getX());
		$y = floor($v3->getY());
		$z = floor($v3->getZ());

		$currentStandingY = $this->resolveAiStandYForCurrentBlock($level, (int) $x, (int) $y, (int) $z);
		if($currentStandingY !== null){
			if($reason) return 'GO';
			return $currentStandingY;
		}

		//echo ($y." ");
		if ($this->whatBlock($level,new Vector3($x,$y,$z)) == "air") {
			//echo "鍓嶆柟绌烘皵 ";
			if ($this->whatBlock($level,new Vector3($x,$y-1,$z)) == "block" or new Vector3($x,$y-1,$z) == "climb") {
				//鏂瑰潡
				//echo "鑰冭檻鍚戝墠 ";
				if ($this->whatBlock($level,new Vector3($x,$y+1,$z)) == "block" or $this->whatBlock($level,new Vector3($x,$y+1,$z)) == "half" or $this->whatBlock($level,new Vector3($x,$y+1,$z)) == "high") {
					//涓婃柟涓€鏍艰鍫典綇浜?					//echo "涓婃柟鍗′綇 \n";
					if ($reason) return 'up!';
					return false;
					//涓婃柟鍗′綇
				} else {
					//echo "GO鍚戝墠璧?\n";
					if ($reason) return 'GO';
					return $this->getAiMovementYForSupport($level, (int) $x, (int) $y - 1, (int) $z, $y);
					//鍚戝墠璧?
				}
			} elseif ($this->whatBlock($level,new Vector3($x,$y-1,$z)) == "water") {
				//姘?				//echo "涓嬫按娓告吵 \n";
				if ($reason) return 'swim';
				return $y-1;
				//闄嶄綆涓€鏍煎悜鍓嶈蛋锛堜笅姘存父娉筹級
			} elseif ($this->whatBlock($level,new Vector3($x,$y-1,$z)) == "half") {
				//鍗婄爾
				//echo "涓嬪埌鍗婄爾 \n";
				if ($reason) return 'half';
				return $this->getAiMovementYForSupport($level, (int) $x, (int) $y - 1, (int) $z, $y + 0.5);
				//鍚戜笅璺?.5鏍?
			} elseif ($this->whatBlock($level,new Vector3($x,$y-1,$z)) == "lava") {
				//宀╂祮
				//echo "鍓嶆柟宀╂祮 \n";
				if ($reason) return 'lava';
				return false;
				//鍓嶆柟宀╂祮
			} elseif ($this->whatBlock($level,new Vector3($x,$y-1,$z)) == "air") {
				//绌烘皵
				//echo "鑰冭檻鍚戜笅璺?";
				if ($this->whatBlock($level,new Vector3($x,$y-2,$z)) == "block") {
					//echo "GO鍚戜笅璺?\n";
					if ($reason) return 'down';
					return $this->getAiMovementYForSupport($level, (int) $x, (int) $y - 2, (int) $z, $y);
					//鍚戜笅璺?
				} else {
					//鍓嶆柟鎮礀
					//echo "鍓嶆柟鎮礀 \n";
					if ($reason) return 'fall';
					if ($hate === false) {
						return false;
					} else {
						return $y-1;
						//鍚戜笅璺?
					}
				}
			}
		} elseif ($this->whatBlock($level,new Vector3($x,$y,$z)) == "water") {
			//姘?			//echo "姝ｅ湪姘翠腑";
			if ($this->whatBlock($level,new Vector3($x,$y+1,$z)) == "water") {
				//涓婇潰杩樻槸姘?				//echo "鍚戜笂娓?\n";
				if ($reason) return 'inwater';
				return $y+1;
				//鍚戜笂娓革紝闃叉汉姘?
			} elseif ($this->whatBlock($level,new Vector3($x,$y+1,$z)) == "block" or $this->whatBlock($level,new Vector3($x,$y+1,$z)) == "half") {
				//涓婃柟涓€鏍艰鍫典綇浜?
				if ($this->whatBlock($level,new Vector3($x,$y-1,$z)) == "block" or $this->whatBlock($level,new Vector3($x,$y-1,$z)) == "half") {
					//涓嬫柟涓€鏍艰涔熷牭浣忎簡
					//echo "涓婁笅閮借鍗′綇 \n";
					if ($reason) return 'up!_down!';
					return false;
					//涓婁笅閮借鍗′綇
				} else {
					//echo "鍚戜笅娓?\n";
					if ($reason) return 'up!';
					return $y-1;
					//鍚戜笅娓革紝闃插崱浣?
				}
			} else {
				//echo "娓告吵ing... \n";
				return $y;
				//鍚戝墠娓?
			}
		} elseif ($this->whatBlock($level,new Vector3($x,$y,$z)) == "half") {
			//鍗婄爾
			//echo "鍓嶆柟鍗婄爾 \n";
			if ($this->whatBlock($level,new Vector3($x,$y+1,$z)) == "block" or $this->whatBlock($level,new Vector3($x,$y+1,$z)) == "half" or $this->whatBlock($level,new Vector3($x,$y+1,$z)) == "high") {
				//涓婃柟涓€鏍艰鍫典綇浜?				//return false;  //涓婃柟鍗′綇
			} else {
				if ($reason) return 'halfGO';
				return $this->getAiMovementYForSupport($level, (int) $x, (int) $y, (int) $z, $y + 1.5);
			}
		} elseif ($this->whatBlock($level,new Vector3($x,$y,$z)) == "lava") {
			//宀╂祮
			//echo "鍓嶆柟宀╂祮 \n";
			if ($reason) return 'lava';
			return false;
		} elseif ($this->whatBlock($level,new Vector3($x,$y,$z)) == "high") {
			//1.5鏍奸珮鏂瑰潡
			//echo "鍓嶆柟鏍呮爮 \n";
			if ($reason) return 'high';
			return false;
		} elseif ($this->whatBlock($level,new Vector3($x,$y,$z)) == "climb") {
			//姊瓙
			//echo "鍓嶆柟姊瓙 \n";
			//return $y;
			if ($reason) return 'climb';
			if ($hate) {
				return $y + 0.7;
			} else {
				return $y + 0.5;
			}
		} else {
			//鑰冭檻鍚戜笂
			//echo "鑰冭檻鍚戜笂 ";
			if ($this->whatBlock($level,new Vector3($x,$y+1,$z)) != "air") {
				//鍓嶆柟鏄潰澧?				//echo "鍓嶆柟鏄 \n";
				if ($reason) return 'wall';
				return false;
			} else {
				if ($this->whatBlock($level,new Vector3($x,$y+2,$z)) == "block" or $this->whatBlock($level,new Vector3($x,$y+2,$z)) == "half" or $this->whatBlock($level,new Vector3($x,$y+2,$z)) == "high") {
					//涓婃柟涓ゆ牸琚牭浣忎簡
					//echo "2鏍煎琚牭 \n";
					if ($reason) return 'up2!';
					return false;
				} else {
					//echo "GO鍚戜笂璺?\n";
					if ($reason) return 'upGO';
					return $this->getAiMovementYForSupport($level, (int) $x, (int) $y, (int) $z, $y + 2);
					//鍚戜笂璺?
				}
			}
		}
		return false;
	}
	public function whatBlock(Level $level, $v3) {
		//boybook鐨剏杞村垽鏂硶 鏍稿績 浠€涔堟柟鍧楋紵
		$block = $level->getBlock($v3);
		$id = $block->getID();
		$damage = $block->getDamage();
		switch ($id) {
			case 0:
						case 6:
						case 27:
						case 30:
						case 31:
						case 37:
						case 38:
						case 39:
						case 40:
						case 50:
						case 51:
						case 63:
						case 66:
						case 68:
						case 78:
						case 111:
						case 141:
						case 142:
						case 171:
						case 175:
						case 244:
						case 323:
							//閫忔槑鏂瑰潡
			return "air";
			break;
			case 8:
						case 9:
							//姘?			return "water";
			break;
			case 10:
						case 11:
							//宀╂祮
			return "lava";
			break;
			case 44:
						case 158:
							//鍗婄爾
			if ($damage >= 8) {
				return "block";
			} else {
				return "half";
			}
			break;
			case 64:
							//闂?			//var_dump($damage." ");
			//TODO 涓嶇煡濡備綍鍒ゆ柇闂ㄦ槸鍚﹀紑鍚紝鍥犱负浠ヤ笅鏉′欢姘歌繙婊¤冻
			if (($damage & 0x08) === 0x08) {
				return "air";
			} else {
				return "block";
			}
			break;
			case 85:
						case 107:
						case 113:
						case 139:
						case 183:
						case 184:
						case 185:
						case 186:
						case 187:
							//1.5鏍奸珮鐨勬棤娉曡烦璺冪墿
			return "high";
			break;
			case 65:
						case 106:
							//鍙攢鐖墿
			return "climb";
			break;
			default:
			return "block";
							//鏅€氭柟鍧?			return "block";
			break;
		}
	}
	public function MobDeath(EntityDeathEvent $event) {
		//var_dump("death");
		$entity = $event->getEntity();
		$arrayName = $this->getTrackedEntityArrayName($entity);
		if($arrayName === ""){
			return;
		}

		$eid = $entity->getID();
		if($entity instanceof Creeper) {
			$eid = $entity->getID();
			if(isset($this->Creeper[$eid])) {
				if($this->Creeper[$eid]['boomed'] == true){
					$event->setDrops([]);
				}
				
				unset($this->Creeper[$eid]);
			}
			return;
		}

		if(isset($this->{$arrayName}[$eid])){
			unset($this->{$arrayName}[$eid]);
		}
	}


	/**
	 * 鍒峰兊灏歌鏃跺櫒
	 */
	public function MobGenerate()
    {
		$debugStats = $this->createNaturalMobSpawnDebugStats();
		$elapsedTicks = $this->getNaturalMobSpawnTaskElapsedTicks();
		$runHostileSpawn = $this->shouldRunNaturalHostileMobSpawn($elapsedTicks);
		$runFriendlySpawn = $this->shouldRunNaturalFriendlyMobSpawn($elapsedTicks);
        foreach ($this->getServer()->getOnlinePlayers() as $p) {
			$this->recordNaturalMobSpawnDebugCount($debugStats, "players");
			$level = $p->getLevel();
			if(!$this->isNaturalSpawnLevel($level)){
				$this->recordNaturalMobSpawnDebugReason($debugStats, "player_level_not_natural");
				continue;
			}
			if($this->getServer()->isWorldNaturalMobSpawnDisabled($level)){
				$this->recordNaturalMobSpawnDebugReason($debugStats, "world_mob_spawning_disabled");
				continue;
			}

			if($runHostileSpawn){
				$attempts = self::getNaturalHostileSpawnAttempts($this->getServer()->getDifficulty());
				for($i = 0; $i < $attempts; ++$i){
					$this->recordNaturalMobSpawnDebugCount($debugStats, "attempts");
					if($level->getDimension() === \lycore\level\Level::DIMENSION_NETHER){
						$this->tryGenerateAirMobNear($p, $level, $debugStats);
					}
					$this->tryGenerateLandMobNear($p, $level, $debugStats, self::NATURAL_LAND_SPAWN_HOSTILE);
				}
			}

			if($runFriendlySpawn && $level->getDimension() === \lycore\level\Level::DIMENSION_NORMAL){
				$attempts = self::getNaturalFriendlySpawnAttempts($this->getServer()->getDifficulty());
				for($i = 0; $i < $attempts; ++$i){
					$this->recordNaturalMobSpawnDebugCount($debugStats, "attempts");
					$this->tryGenerateWaterMobNear($p, $level, $debugStats);
					$this->tryGenerateCaveMobNear($p, $level, $debugStats);
					$this->tryGenerateLandMobNear($p, $level, $debugStats, self::NATURAL_LAND_SPAWN_FRIENDLY);
				}
			}
        }
		$this->flushNaturalMobSpawnDebugStats($debugStats);
    }

	public function EntityDamage(EntityDamageEvent $event) {
		$this->handleSkeletonArrowRetaliation($event);
		$this->handleSkeletonHostileRetaliation($event);
		$this->handlePlayerGuardianRetaliation($event);
		$this->handleTamedWolfOwnerCombat($event);

		//鍑婚€€淇
		if($event instanceof EntityDamageByEntityEvent) {
			$p = $event->getDamager();
			$entity = $event->getEntity();
			$arrayName = $this->getTrackedEntityArrayName($entity);
			if($arrayName !== "") {
				$array = &$this->{$arrayName};
			}else {
				$array = [];
			}
			if(isset($array[$entity->getId()])) {
				if($p instanceof Player and ($array[$entity->getId()]['canAttack'] == 0)) {
					$weapon = $p->getInventory()->getItemInHand()->getID();
					//寰楀埌鐜╁鎵嬩腑鐨勬鍣?					$high = 0;
					if($weapon == 258 or $weapon == 271 or $weapon == 275) {
						//鍑婚€€x5
						$back = 1.5;
					} elseif($weapon == 267 or $weapon == 272 or $weapon == 279 or $weapon == 283 or $weapon == 286) {
						//鍑婚€€x1
						$back = 3;
					} elseif($weapon == 276) {
						//鍑婚€€x2
						$back = 4;
					} elseif($weapon == 292) {
						//鍑婚€€x10
						$back = 8;
						$high = 3;
					} else {
						$back = 1;
					}
					//var_dump("鐜╁".$p->getName()."鏀诲嚮浜咺D涓?.$zo->getId()."鐨勫疄浣?);
					$array[$entity->getId()]['x'] = $array[$entity->getId()]['x'] - $array[$entity->getId()]['xxx'] * $back;
					$array[$entity->getId()]['y'] = $entity->getY() + $high;
					$array[$entity->getId()]['z'] = $array[$entity->getId()]['z'] - $array[$entity->getId()]['zzz'] * $back;
					$pos = new Vector3 ($array[$entity->getId()]['x'], $array[$entity->getId()]['y'], $array[$entity->getId()]['z']);
					//鐩爣鍧愭爣
					//$entity->setPosition($pos);
					$entity->knockBack($entity, 0, $array[$entity->getId()]['xxx'] * $back, $array[$entity->getId()]['zzz'] * $back);
					if(isset($array[$entity->getId()])) {
						$zom = &$array[$entity->getId()];
						$zom['IsChasing'] = $p->getName();
						//var_dump( $zom['IsChasing']);
					}
				}
			}
		}
	}

	private function handlePlayerGuardianRetaliation(EntityDamageEvent $event){
		if(!($event instanceof EntityDamageByEntityEvent) || $event instanceof EntityDamageByChildEntityEvent){
			return;
		}
		if($event->getCause() !== EntityDamageEvent::CAUSE_ENTITY_ATTACK){
			return;
		}

		$player = $event->getDamager();
		if(!($player instanceof Player)){
			return;
		}

		$target = $event->getEntity();
		if($target instanceof IronGolem){
			if(!$target->closed && $target->isAlive() && !$player->closed && $player->isAlive()){
				$target->setPm1eRetaliationTarget($player);
			}
			return;
		}

		if($target instanceof Wolf){
			$this->retaliateForAttackedWolf($target, $player);
			return;
		}

		if($target instanceof Villager){
			$this->retaliateForAttackedVillager($target, $player);
		}
	}

	private function handleTamedWolfOwnerCombat(EntityDamageEvent $event){
		if($event->isCancelled()){
			return;
		}
		if(!($event instanceof EntityDamageByEntityEvent)){
			return;
		}
		$isDirectAttack = !($event instanceof EntityDamageByChildEntityEvent) && $event->getCause() === EntityDamageEvent::CAUSE_ENTITY_ATTACK;
		$isProjectile = $event instanceof EntityDamageByChildEntityEvent && $event->getCause() === EntityDamageEvent::CAUSE_PROJECTILE;
		if(!$isDirectAttack && !$isProjectile){
			return;
		}

		$attacker = $event->getDamager();
		$target = $event->getEntity();
		if($attacker instanceof Player && $target instanceof Entity){
			$this->commandTamedWolves($attacker, $target);
		}

		$victim = $target;
		if($victim instanceof Player && $attacker instanceof Entity){
			$this->commandTamedWolves($victim, $attacker);
		}
	}

	private function commandTamedWolves(Player $owner, Entity $target){
		if($owner->closed || !$owner->isAlive() || $target === $owner || $target->closed || !$target->isAlive()){
			return;
		}

		$level = $owner->getLevel();
		if($level === null){
			return;
		}

		foreach($level->getEntities() as $entity){
			if(!($entity instanceof Wolf) || $entity->closed || !$entity->isAlive()){
				continue;
			}
			if(!$entity->isTamed() || !$entity->isOwner($owner) || $entity->isSitting()){
				continue;
			}
			if($entity === $target){
				continue;
			}
			if($target instanceof Wolf && $target->isOwner($owner)){
				continue;
			}

			$entity->setPm1eRetaliationTarget($target);
			$entity->setAngry(true);
		}
	}

	private function retaliateForAttackedWolf(Wolf $wolf, Player $player){
		if($wolf->closed || !$wolf->isAlive() || $player->closed || !$player->isAlive()){
			return;
		}

		if($wolf->isTamed()){
			if(!$wolf->isOwner($player) && !$wolf->isSitting()){
				$wolf->setPm1eRetaliationTarget($player);
				$wolf->setAngry(true);
			}
			return;
		}

		$level = $wolf->getLevel();
		if($level === null){
			return;
		}

		foreach($level->getEntities() as $entity){
			if(!($entity instanceof Wolf) || $entity->closed || !$entity->isAlive()){
				continue;
			}
			if($entity->distanceSquared($wolf) > 1024){
				continue;
			}
			if($entity->isTamed()){
				continue;
			}
			$entity->setPm1eRetaliationTarget($player);
		}
	}

	private function retaliateForAttackedVillager(Villager $villager, Player $player){
		if($villager->closed || !$villager->isAlive() || $player->closed || !$player->isAlive()){
			return;
		}

		$level = $villager->getLevel();
		if($level === null){
			return;
		}

		foreach($level->getEntities() as $entity){
			if(!($entity instanceof IronGolem) || $entity->closed || !$entity->isAlive()){
				continue;
			}
			if($entity->distanceSquared($villager) > 1024){
				continue;
			}
			$entity->setPm1eRetaliationTarget($player);
		}
	}

	private function handleSkeletonHostileRetaliation(EntityDamageEvent $event){
		if(!($event instanceof EntityDamageByEntityEvent) || $event instanceof EntityDamageByChildEntityEvent){
			return;
		}
		if($event->getCause() !== EntityDamageEvent::CAUSE_ENTITY_ATTACK){
			return;
		}

		$target = $event->getEntity();
		$attacker = $event->getDamager();
		if(!($target instanceof Skeleton) || $target === $attacker){
			return;
		}
		if(!$this->isSkeletonArrowRetaliationTarget($attacker)){
			return;
		}
		if($target->closed || !$target->isAlive() || $attacker->closed || !$attacker->isAlive()){
			return;
		}

		$target->setPm1eRetaliationTarget($attacker);
	}

	private function handleSkeletonArrowRetaliation(EntityDamageEvent $event){
		if(!($event instanceof EntityDamageByChildEntityEvent)){
			return;
		}
		if($event->getCause() !== EntityDamageEvent::CAUSE_PROJECTILE){
			return;
		}

		$shooter = $event->getDamager();
		$projectile = $event->getChild();
		$target = $event->getEntity();
		if(!($shooter instanceof Skeleton) || !($projectile instanceof Arrow) || $target === $shooter){
			return;
		}
		if(!$this->isSkeletonArrowRetaliationTarget($target)){
			return;
		}
		if($shooter->closed || !$shooter->isAlive() || $target->closed || !$target->isAlive()){
			return;
		}

		if($target instanceof FlyingAnimal){
			$target->setPm1eFlyFollowTarget($shooter);
		}else{
			$target->setPm1eRetaliationTarget($shooter);
		}
	}

	private function isSkeletonArrowRetaliationTarget(Entity $target) : bool{
		return $target instanceof Monster || $target instanceof Slime || $target instanceof LavaSlime || $target instanceof Blaze || $target instanceof Ghast;
	}

	public function knockBackover(Entity $entity, Vector3 $v3) {
		if($entity instanceof Entity) {
			foreach($this->getTrackedEntityArrayNames() as $arrayName) {
				if(isset($this->{$arrayName}[$entity->getId()])) {
					$this->setEntityPositionOnGroundSupport($entity, $v3);
					$this->{$arrayName}[$entity->getId()]['knockBack'] = false;
				}
			}
			if(isset($this->Defult[$entity->getId()])) {
				$this->setEntityPositionOnGroundSupport($entity, $v3);
				$this->Defult[$entity->getId()]['knockBack'] = false;
			}
		}
	}
}
