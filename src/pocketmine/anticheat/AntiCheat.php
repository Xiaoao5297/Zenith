<?php

/***
 *  _____                _  __   __
 * /__  /  ___   ____   (_)/ /_ / /_
 *   / /  / _ \ / __ \ / // __// __ \
 *  / /__/  __// / / // // /_ / / / /
 * /____/\___//_/ /_//_/ \__//_/ /_/
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/

namespace pocketmine\anticheat;

use pocketmine\Server;
use pocketmine\Player;
use pocketmine\block\Block;
use pocketmine\entity\Entity;
use pocketmine\level\Position;
use pocketmine\math\Vector3;
use pocketmine\scheduler\CallbackTask;
use pocketmine\utils\Config;

use pocketmine\anticheat\check\Check as BaseCheck;
use pocketmine\anticheat\check\MovementCheck;
use pocketmine\anticheat\check\SpeedCheck;
use pocketmine\anticheat\check\AttackCheck;
use pocketmine\anticheat\check\ReachCheck;
use pocketmine\anticheat\check\ItemCheck;
use pocketmine\anticheat\check\XRayCheck;
use pocketmine\anticheat\check\FlyCheck;
use pocketmine\anticheat\check\NoFallCheck;
use pocketmine\anticheat\check\AutoClickerCheck;
use pocketmine\anticheat\check\TimerCheck;
use pocketmine\anticheat\check\HitboxCheck;
use pocketmine\anticheat\check\TeleportCheck;
use pocketmine\anticheat\check\KnockbackCheck;

class AntiCheat{

	/** @var AntiCheat */
	private static $instance = null;

	/** @var Server */
	private $server;

	/** @var Config */
	private $config;

	/** @var Config */
	private $messages;

	/** @var Config */
	private $dailyWarnings;

	/** @var bool */
	private $enabled = true;

	/** @var int */
	private $maxDailyViolations = 5;

	/** @var int */
	private $punishThreshold = 3;

	/** @var array */
	private $dailyViolations = [];

	/** @var array */
	private $playerLastPosition = [];

	/** @var array */
	private $playerLastMoveTime = [];

	/** @var array */
	private $playerNoticeCooldown = [];

	/** @var BaseCheck[] */
	private $checks = [];

	/** @var PlayerData[] 键为小写玩家名 */
	private $playerData = [];

	/** @var ViolationManager|null */
	private $violationManager = null;

	/** @var bool */
	private $debug = false;

	public static function getInstance() : ?self{
		return self::$instance;
	}

	public function __construct(Server $server){
		self::$instance = $this;
		$this->server = $server;

		$this->initConfig();
		$this->initChecks();

		if($this->enabled){
			// 逐 tick 采样任务，驱动 Fly/NoFall 等需要每 tick 数据的检测器
			$this->server->getScheduler()->scheduleRepeatingTask(new CallbackTask([$this, "onTick"]), 1);
		}
	}

	private function initConfig(){
		$dataPath = $this->server->getDataPath();

		// 首次启动时复制带注释的默认配置
		if(!file_exists($dataPath . "anticheat.yml")){
			$resourcePath = $this->server->getFilePath() . "src/pocketmine/resources/anticheat.yml";
			if(file_exists($resourcePath)){
				copy($resourcePath, $dataPath . "anticheat.yml");
			}
		}

		// 加载配置
		$this->config = new Config($dataPath . "anticheat.yml", Config::YAML, $this->getDefaultConfig());

		// 加载消息配置
		if(!file_exists($dataPath . "anticheat_messages.yml")){
			$resourcePath = $this->server->getFilePath() . "src/pocketmine/resources/anticheat_messages.yml";
			if(file_exists($resourcePath)){
				copy($resourcePath, $dataPath . "anticheat_messages.yml");
			}
		}
		$this->messages = new Config($dataPath . "anticheat_messages.yml", Config::YAML, $this->getDefaultMessages());

		// 每日违规记录
		$this->dailyWarnings = new Config($dataPath . "anticheat_daily.yml", Config::YAML, []);
		$this->dailyWarnings->save();

		// 读取设置
		$settings = $this->config->get("settings", []);
		$this->enabled = (bool) ($settings["enabled"] ?? true);
		$this->debug = (bool) ($settings["debug"] ?? false);
		$this->maxDailyViolations = (int) ($settings["max-daily-violations"] ?? 5);

		$punishments = $this->config->get("punishments", []);
		$this->punishThreshold = (int) ($punishments["threshold"] ?? 3);

		$mode = (string) ($settings["mode"] ?? ViolationManager::MODE_ALERT);
		$this->violationManager = new ViolationManager($this, $mode, $this->maxDailyViolations, $this->punishThreshold, $this->dailyWarnings);
	}

	private function getDefaultConfig() : array{
		return [
			"settings" => [
				"enabled" => true,
				"mode" => ViolationManager::MODE_ALERT,
				"debug" => false,
				"broadcast-to-ops" => true,
				"log-to-console" => true,
				"max-daily-violations" => 5,
			],
			"punishments" => [
				"threshold" => 3,
			],
			"checks" => [
				"speed" => [
					"enabled" => true,
					"max-walk-speed" => 6.0,
					"max-sprint-speed" => 8.0,
					"max-fly-speed" => 20.0,
					"max-violations" => 8,
				],
				"attack" => [
					"enabled" => true,
					"max-attacks-per-second" => 10,
					"max-damage-multiplier" => 2.0,
					"max-rotation-per-tick" => 90.0,
					"max-violations" => 4,
				],
				"reach" => [
					"enabled" => true,
					"max-attack-reach" => 6.0,
					"max-block-reach" => 6.0,
					"max-interact-reach" => 6.0,
					"max-container-reach" => 6.0,
					"max-violations" => 6,
				],
				"hitbox" => [
					"enabled" => true,
					"max-attack-reach" => 6.0,
					"max-horizontal-range" => 0.5,
					"max-violations" => 6,
				],
				"teleport" => [
					"enabled" => true,
					"max-blocks-per-tick" => 10.0,
					"max-violations" => 6,
				],
				"knockback" => [
					"enabled" => true,
					"min-knockback-distance" => 0.3,
					"required-count" => 3,
					"max-violations" => 3,
				],
				"item" => [
					"enabled" => true,
					"check-stack" => true,
					"check-32k" => true,
					"check-enchantments" => true,
					"check-nbt" => true,
					"banned-items" => [],
					"max-violations" => 3,
				],
				"xray" => [
					"enabled" => true,
					"min-blocks-before-check" => 30,
					"thresholds" => [
						"common" => 15,
						"rare" => 5,
						"precious" => 2,
					],
					"check-exposed" => true,
					"reset-interval" => 180000,
					"max-violations" => 4,
				],
				"fly" => [
					"enabled" => true,
					"max-air-ticks" => 40,
					"max-hover-height" => 3.0,
					"max-violations" => 6,
				],
				"nofall" => [
					"enabled" => true,
					"min-fall-damage" => 4.0,
					"max-violations" => 5,
				],
				"autoclicker" => [
					"enabled" => true,
					"max-cps" => 18,
					"consistency-threshold" => 2.5,
					"consistency-min-samples" => 20,
					"max-violations" => 3,
				],
				"timer" => [
					"enabled" => true,
					"window-ticks" => 20,
					"max-samples" => 30,
					"max-violations" => 6,
				],
			],
		];
	}

	private function getDefaultMessages() : array{
		return [
			"cheat-detected" => "§c[AntiCheat] §e{player} §f疑似作弊 §7[{check}] §f{detail}",
			"kick-message" => "§c[AntiCheat] 你因作弊被踢出服务器\n§7原因: {check}",
			"ban-message" => "§c[AntiCheat] 你因作弊被封禁\n§7原因: {check}",
			"warning-message" => "§c[AntiCheat] 警告! 检测到异常行为: {check}",
			"item-removed" => "§c[AntiCheat] 非法物品已移除: {item}",
		];
	}

	private function initChecks(){
		$checksConfig = $this->config->get("checks", []);

		$this->checks = [
			new SpeedCheck($this, $checksConfig["speed"] ?? []),
			new AttackCheck($this, $checksConfig["attack"] ?? []),
			new ReachCheck($this, $checksConfig["reach"] ?? []),
			new ItemCheck($this, $checksConfig["item"] ?? []),
			new XRayCheck($this, $checksConfig["xray"] ?? []),
			new FlyCheck($this, $checksConfig["fly"] ?? []),
			new NoFallCheck($this, $checksConfig["nofall"] ?? []),
			new AutoClickerCheck($this, $checksConfig["autoclicker"] ?? []),
			new TimerCheck($this, $checksConfig["timer"] ?? []),
			new HitboxCheck($this, $checksConfig["hitbox"] ?? []),
			new TeleportCheck($this, $checksConfig["teleport"] ?? []),
			new KnockbackCheck($this, $checksConfig["knockback"] ?? []),
		];
	}

	// ============ 消息 ============

	public function getMessage(string $key, array $replace = []) : string{
		$msg = $this->messages->get($key, $key);
		foreach($replace as $k => $v){
			$msg = str_replace("{" . $k . "}", $v, $msg);
		}
		return $msg;
	}

	// ============ 日志 ============

	public function logCheat($playerName, string $check, string $detail = ""){
		$settings = $this->config->get("settings", []);
		if(!(bool) ($settings["log-to-console"] ?? true)) return;

		$msg = $this->getMessage("cheat-detected", [
			"player" => $playerName,
			"check" => $check,
			"detail" => $detail,
		]);

		$this->server->getLogger()->warning($msg);

		if((bool) ($settings["broadcast-to-ops"] ?? true)){
			foreach($this->server->getOnlinePlayers() as $p){
				if($p->hasPermission("fpacheat.notify")){
					$p->sendMessage($msg);
				}
			}
		}
	}

	// ============ 违规追踪 ============

	public function addViolation(string $playerName, string $checkName) : int{
		return $this->violationManager->addDailyViolation($playerName, $checkName);
	}

	public function getDailyViolations(string $playerName) : int{
		return $this->violationManager->getDailyViolations($playerName);
	}

	/**
	 * 获取（必要时创建）玩家运行时状态。
	 */
	public function getPlayerData(Player $player) : PlayerData{
		$key = strtolower($player->getName());
		if(!isset($this->playerData[$key])){
			$this->playerData[$key] = new PlayerData($player->getName(), $this->server->getTick());
		}
		return $this->playerData[$key];
	}

	public function getViolationManager() : ViolationManager{
		return $this->violationManager;
	}

	public function isPunishMode() : bool{
		return $this->violationManager !== null and $this->violationManager->isPunishMode();
	}

	public function isDebug() : bool{
		return $this->debug;
	}

	public function setDebug(bool $debug){
		$this->debug = $debug;
	}

	/**
	 * 按名字获取玩家运行时状态（不存在返回 null）。
	 */
	public function getPlayerDataByName(string $name) : ?PlayerData{
		return $this->playerData[strtolower($name)] ?? null;
	}

	// ============ 惩罚 ============

	public function punish(Player $player, string $checkName, int $violationCount = 1){
		// 仅告警模式不执行任何实际惩罚
		if(!$this->isPunishMode()){
			return;
		}

		$playerName = $player->getName();
		$dailyTotal = $this->addViolation($playerName, $checkName);

		$reason = $this->getMessage("kick-message", ["check" => $checkName]);

		if($dailyTotal >= $this->maxDailyViolations){
			// 每日上限：永久封禁
			$banMsg = $this->getMessage("ban-message", ["check" => $checkName]);
			$this->server->getNameBans()->addBan($playerName, $banMsg, null, "AntiCheat");
			$player->close("", $banMsg, true);
			$this->server->broadcastMessage("§5§l===========================");
			$this->server->broadcastMessage("§5    " . $playerName . " 被反作弊吃掉了!");
			$this->server->broadcastMessage("§5§l===========================");
			return;
		}

		if($violationCount >= $this->punishThreshold){
			// 达到阈值：封禁 + 踢出
			$this->server->getNameBans()->addBan($playerName, $reason, null, "AntiCheat");
			$player->close("", $reason, true);

			$this->server->broadcastMessage("§5§l============================================");
			$this->server->broadcastMessage("§5    " . $playerName . " §c因作弊被封禁 §7[" . $checkName . "]");
			$this->server->broadcastMessage("§5§l============================================");
		}else{
			// 警告
			$warning = $this->getMessage("warning-message", ["check" => $checkName]);
			$player->sendMessage($warning);
		}
	}

	// ============ 玩家位置缓存 ============

	public function setPlayerLastPosition(string $name, Position $pos){
		$this->playerLastPosition[$name] = $pos;
	}

	public function getPlayerLastPosition(string $name) : ?Position{
		return $this->playerLastPosition[$name] ?? null;
	}

	public function setPlayerLastMoveTime(string $name, float $time){
		$this->playerLastMoveTime[$name] = $time;
	}

	public function getPlayerLastMoveTime(string $name) : ?float{
		return $this->playerLastMoveTime[$name] ?? null;
	}

	// ============ 通知冷却 ============

	public function canSendNotice(string $playerName, string $key, int $cooldown = 3000) : bool{
		$now = microtime(true) * 1000;
		$k = $playerName . ":" . $key;
		if(isset($this->playerNoticeCooldown[$k]) && ($now - $this->playerNoticeCooldown[$k]) < $cooldown){
			return false;
		}
		$this->playerNoticeCooldown[$k] = $now;
		return true;
	}

	// ============ 检测调用入口 ============

	public function getServer() : Server{
		return $this->server;
	}

	public function getConfig() : Config{
		return $this->config;
	}

	public function isEnabled() : bool{
		return $this->enabled;
	}

	/**
	 * @return BaseCheck[]
	 */
	public function getChecks() : array{
		return $this->checks;
	}

	public function getCheck(string $class) : ?BaseCheck{
		foreach($this->checks as $check){
			if(get_class($check) === $class){
				return $check;
			}
		}
		return null;
	}

	// ============ 核心集成方法（从 Player/Entity 调用） ============

	/**
	 * 玩家移动检测（从 Player::processMovement 调用）
	 */
	public function onPlayerMove(Player $player, Vector3 $from, Vector3 $to){
		if(!$this->enabled) return;

		$tick = $this->server->getTick();
		$data = $this->getPlayerData($player);

		$last = $data->getLastSample();
		$tickDelta = $last !== null ? max(1, $tick - $last->getTick()) : 1;

		$exempt = $this->isMovementExempt($data, $tick);

		$snapshot = new MovementSnapshot(
			$tick,
			$tickDelta,
			$from,
			$to,
			$player->isOnGround(),
			$player->isInsideOfWater(),
			$this->isOnLadder($player),
			$this->isOnIce($player),
			$this->isOnSlime($player),
			$player->getLinkedEntity() !== null,
			$exempt
		);

		$data->setLastSample($snapshot);

		if($exempt){
			return;
		}

		foreach($this->checks as $check){
			if($check instanceof MovementCheck and !$check->sampledPerTick() and $check->isEnabled()){
				$check->checkMovement($player, $snapshot);
			}
		}

		/** @var KnockbackCheck $knockback */
		if(($knockback = $this->getCheck(KnockbackCheck::class)) !== null){
			$knockback->checkKnockbackMovement($player, $from, $to);
		}
	}

	/**
	 * 逐 tick 采样：驱动 Fly/NoFall 等需要每 tick 数据的检测器。
	 *
	 * 由调度器每 tick 调用一次；对每个在线玩家取服务器权威坐标构建快照，
	 * 静止悬停、缓慢位移同样会产生样本，不再依赖 PlayerMoveEvent。
	 *
	 * @param \pocketmine\scheduler\Task|null $task
	 */
	public function onTick($task = null){
		if(!$this->enabled) return;

		$tick = $this->server->getTick();

		foreach($this->server->getOnlinePlayers() as $player){
			if(!$player->isOnline() or !$player->spawned){
				continue;
			}

			$data = $this->getPlayerData($player);

			$prevPos = $data->getState("tick.prevPos");
			$prevTick = (int) $data->getState("tick.prevTick", $tick);

			$current = $player->getPosition();
			$from = $prevPos !== null ? $prevPos : $current;
			$tickDelta = max(1, $tick - $prevTick);

			$data->setState("tick.prevPos", clone $current);
			$data->setState("tick.prevTick", $tick);

			if($this->isMovementExempt($data, $tick)){
				continue;
			}

			$snapshot = new MovementSnapshot(
				$tick,
				$tickDelta,
				$from,
				$current,
				$player->isOnGround(),
				$player->isInsideOfWater(),
				$this->isOnLadder($player),
				$this->isOnIce($player),
				$this->isOnSlime($player),
				$player->getLinkedEntity() !== null,
				false
			);

			foreach($this->checks as $check){
				if($check instanceof MovementCheck and $check->sampledPerTick() and $check->isEnabled()){
					$check->checkMovement($player, $snapshot);
				}
			}
		}
	}

	/**
	 * 方块破坏（从 Level::useBreakOn 调用）。
	 */
	public function onBlockBreak(Player $player, Block $block){
		if(!$this->enabled) return;

		/** @var ReachCheck $reach */
		if(($reach = $this->getCheck(ReachCheck::class)) !== null){
			$reach->checkBlockBreak($player, $block);
		}

		/** @var XRayCheck $xray */
		if(($xray = $this->getCheck(XRayCheck::class)) !== null){
			$xray->check($player, $block);
		}
	}

	/**
	 * 方块放置（从 Level::useItemOn 调用）。
	 */
	public function onBlockPlace(Player $player, Block $block){
		if(!$this->enabled) return;

		/** @var ReachCheck $reach */
		if(($reach = $this->getCheck(ReachCheck::class)) !== null){
			$reach->checkBlockPlace($player, $block);
		}
	}

	/**
	 * 切换手持物品（从 PlayerInventory::setHeldItemSlot 调用）。
	 */
	public function onItemHeld(Player $player, \pocketmine\item\Item $item){
		if(!$this->enabled) return;

		/** @var ItemCheck $itemCheck */
		if(($itemCheck = $this->getCheck(ItemCheck::class)) !== null){
			$itemCheck->check($player, $item);
		}
	}

	/**
	 * 传送/切换世界/加入后的短暂豁免窗口（tick）。
	 */
	private function isMovementExempt(PlayerData $data, int $tick) : bool{
		$grace = 40;

		if($tick - $data->getJoinTick() < $grace){
			return true;
		}
		if($data->getLastTeleportTick() >= 0 and $tick - $data->getLastTeleportTick() < $grace){
			return true;
		}
		if($data->getLastWorldChangeTick() >= 0 and $tick - $data->getLastWorldChangeTick() < $grace){
			return true;
		}

		return false;
	}

	/**
	 * 传送发生时由核心调用，进入豁免窗口并清空移动采样。
	 */
	public function onTeleport(Player $player){
		$data = $this->getPlayerData($player);
		$data->markTeleport($this->server->getTick());
		$data->setLastSample(null);
	}

	/**
	 * 切换世界时由核心调用。
	 */
	public function onWorldChange(Player $player){
		$data = $this->getPlayerData($player);
		$data->markWorldChange($this->server->getTick());
		$data->setLastSample(null);
	}

	private function blockIdAt(Player $player, int $offsetY = 0) : int{
		$level = $player->getLevel();
		if($level === null){
			return 0;
		}
		return $level->getBlockIdAt((int) floor($player->x), (int) floor($player->y) + $offsetY, (int) floor($player->z));
	}

	private function isOnLadder(Player $player) : bool{
		$id = $this->blockIdAt($player);
		return $id === Block::LADDER or $id === Block::VINE;
	}

	private function isOnIce(Player $player) : bool{
		$id = $this->blockIdAt($player);
		if($id === Block::ICE or $id === Block::PACKED_ICE){
			return true;
		}
		$id = $this->blockIdAt($player, -1);
		return $id === Block::ICE or $id === Block::PACKED_ICE;
	}

	private function isOnSlime(Player $player) : bool{
		return $this->blockIdAt($player) === Block::SLIME_BLOCK or $this->blockIdAt($player, -1) === Block::SLIME_BLOCK;
	}

	/**
	 * 玩家攻击检测（从 INTERACT_PACKET handler 调用）
	 */
	public function onPlayerAttack(Player $player, Entity $target, \pocketmine\event\entity\EntityDamageByEntityEvent $event){
		/** @var AttackCheck $attack */
		if(($attack = $this->getCheck(AttackCheck::class)) !== null){
			$attack->checkAttack($player, $target, $event->getFinalDamage());
		}

		/** @var ReachCheck $reach */
		if(($reach = $this->getCheck(ReachCheck::class)) !== null){
			$reach->checkAttack($player, $target, $event);
		}

		/** @var AutoClickerCheck $autoclicker */
		if(($autoclicker = $this->getCheck(AutoClickerCheck::class)) !== null){
			$autoclicker->checkAttack($player);
		}

		/** @var HitboxCheck $hitbox */
		if(($hitbox = $this->getCheck(HitboxCheck::class)) !== null and !$event->isCancelled()){
			$hitbox->checkHitbox($player, $target);
		}
	}

	/**
	 * 实体受击检测（从 Entity::attack 调用，追踪击退/摔落）
	 */
	public function onEntityDamage(Player $player, \pocketmine\event\entity\EntityDamageEvent $source){
		$this->getPlayerData($player)->markDamage($this->server->getTick());

		if($source->getCause() === \pocketmine\event\entity\EntityDamageEvent::CAUSE_ENTITY_ATTACK){
			/** @var KnockbackCheck $knockback */
			if(($knockback = $this->getCheck(KnockbackCheck::class)) !== null){
				$knockback->checkKnockback($player);
			}
		}

		if($source->getCause() === \pocketmine\event\entity\EntityDamageEvent::CAUSE_FALL){
			/** @var NoFallCheck $nofall */
			if(($nofall = $this->getCheck(NoFallCheck::class)) !== null){
				$nofall->checkFallDamage($player, $source->getFinalDamage());
			}
		}
	}

	// ============ 玩家数据清理 ============

	public function clearPlayerData(string $playerName){
		$lower = strtolower($playerName);
		unset($this->playerLastPosition[$lower]);
		unset($this->playerLastMoveTime[$lower]);
		unset($this->playerData[$lower]);

		foreach($this->checks as $check){
			$check->clearPlayerData($playerName);
		}
	}
}
