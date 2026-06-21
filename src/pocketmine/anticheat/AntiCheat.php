<?php

namespace pocketmine\anticheat;

use pocketmine\Server;
use pocketmine\Player;
use pocketmine\entity\Entity;
use pocketmine\level\Position;
use pocketmine\math\Vector3;
use pocketmine\utils\Config;

use pocketmine\anticheat\check\Check as BaseCheck;
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

	public static function getInstance() : ?self{
		return self::$instance;
	}

	public function __construct(Server $server){
		self::$instance = $this;
		$this->server = $server;

		$this->initConfig();
		$this->initChecks();
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
		$this->maxDailyViolations = (int) ($settings["max-daily-violations"] ?? 5);

		$punishments = $this->config->get("punishments", []);
		$this->punishThreshold = (int) ($punishments["threshold"] ?? 3);
	}

	private function getDefaultConfig() : array{
		return [
			"settings" => [
				"enabled" => true,
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
					"max-walk-speed" => 7.0,
					"max-sprint-speed" => 9.0,
					"max-fly-speed" => 15.0,
					"rollback" => true,
					"max-violations" => 5,
				],
				"attack" => [
					"enabled" => true,
					"max-attacks-per-second" => 10,
					"max-damage-multiplier" => 1.5,
					"max-rotation-per-tick" => 90.0,
					"max-violations" => 1,
				],
				"reach" => [
					"enabled" => true,
					"max-attack-reach" => 4.5,
					"max-block-reach" => 5.5,
					"max-interact-reach" => 5.5,
					"max-container-reach" => 5.5,
					"cooldown" => 300,
					"max-violations" => 4,
				],
				"hitbox" => [
					"enabled" => true,
					"max-horizontal-range" => 0.3,
					"max-violations" => 4,
				],
				"teleport" => [
					"enabled" => true,
					"max-distance" => 10.0,
					"max-violations" => 4,
				],
				"knockback" => [
					"enabled" => true,
					"min-knockback-distance" => 0.3,
					"required-count" => 3,
					"max-violations" => 1,
				],
				"item" => [
					"enabled" => true,
					"check-stack" => true,
					"check-32k" => true,
					"check-enchantments" => true,
					"check-nbt" => true,
					"banned-items" => [],
					"max-violations" => 1,
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
					"max-violations" => 2,
				],
				"fly" => [
					"enabled" => true,
					"max-air-ticks" => 30,
					"max-hover-height" => 2.5,
					"max-violations" => 5,
				],
				"nofall" => [
					"enabled" => true,
					"max-violations" => 3,
				],
				"autoclicker" => [
					"enabled" => true,
					"max-cps" => 15,
					"consistency-threshold" => 8.0,
					"max-violations" => 1,
				],
				"timer" => [
					"enabled" => true,
					"max-violations" => 5,
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
		if(!$this->config->get("settings")["log-to-console"] ?? true) return;

		$msg = $this->getMessage("cheat-detected", [
			"player" => $playerName,
			"check" => $check,
			"detail" => $detail,
		]);

		$this->server->getLogger()->warning($msg);

		if($this->config->get("settings")["broadcast-to-ops"] ?? true){
			foreach($this->server->getOnlinePlayers() as $p){
				if($p->hasPermission("fpacheat.notify")){
					$p->sendMessage($msg);
				}
			}
		}
	}

	// ============ 违规追踪 ============

	public function addViolation(string $playerName, string $checkName) : int{
		$today = date("Y-m-d");
		$daily = $this->dailyWarnings->get($today, []);
		$playerKey = strtolower($playerName);
		$daily[$playerKey] = ($daily[$playerKey] ?? 0) + 1;
		$this->dailyWarnings->set($today, $daily);
		$this->dailyWarnings->save();

		return $daily[$playerKey];
	}

	public function getDailyViolations(string $playerName) : int{
		$today = date("Y-m-d");
		$daily = $this->dailyWarnings->get($today, []);
		return (int) ($daily[strtolower($playerName)] ?? 0);
	}

	// ============ 惩罚 ============

	public function punish(Player $player, string $checkName, int $violationCount = 1){
		$playerName = $player->getName();
		$dailyTotal = $this->addViolation($playerName, $checkName);

		$reason = $this->getMessage("kick-message", ["check" => $checkName]);

		if($dailyTotal >= $this->maxDailyViolations){
			// 每日上限：永久封禁
			$banMsg = $this->getMessage("ban-message", ["check" => $checkName]);
			$this->server->getNameBans()->addBan($playerName, $banMsg, null, "AntiCheat");
			$player->kick($banMsg, false);
			$this->server->broadcastMessage("§5§l===========================");
			$this->server->broadcastMessage("§5    " . $playerName . " 被反作弊吃掉了!");
			$this->server->broadcastMessage("§5§l===========================");
			return;
		}

		if($violationCount >= $this->punishThreshold){
			// 达到阈值：封禁 + 踢出
			$this->server->getNameBans()->addBan($playerName, $reason, null, "AntiCheat");
			$player->kick($reason, false);

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
		$elapsed = microtime(true) - ($this->getPlayerLastMoveTime($player->getName()) ?? microtime(true));
		$this->setPlayerLastMoveTime($player->getName(), microtime(true));

		/** @var SpeedCheck $speed */
		if(($speed = $this->getCheck(SpeedCheck::class)) !== null){
			$speed->check($player, $from, $to, $elapsed);
		}

		/** @var FlyCheck $fly */
		if(($fly = $this->getCheck(FlyCheck::class)) !== null){
			$fly->check($player, $from, $to);
		}

		/** @var NoFallCheck $nofall */
		if(($nofall = $this->getCheck(NoFallCheck::class)) !== null){
			$nofall->check($player, $from, $to);
		}

		/** @var TimerCheck $timer */
		if(($timer = $this->getCheck(TimerCheck::class)) !== null){
			$timer->check($player, $from, $to, $elapsed);
		}

		/** @var TeleportCheck $teleport */
		if(($teleport = $this->getCheck(TeleportCheck::class)) !== null){
			$teleport->check($player, $from, $to, $elapsed);
		}

		/** @var KnockbackCheck $knockback */
		if(($knockback = $this->getCheck(KnockbackCheck::class)) !== null){
			$knockback->checkKnockbackMovement($player, $from, $to);
		}
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

		foreach($this->checks as $check){
			$check->clearPlayerData($playerName);
		}
	}
}
