<?php

/*
 *   _  __   __  _   _  ___  _  _  _
 *  | |/ /  / / | | | ||_ _|| \| || |
 *  | ' /  / /  | |_| | | | | .` || |__
 *  |_|\_\ /_/   \___/ |___||_|\_||____|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 */

/**
 * 移动控制层（MoveControl）的离线测试。
 *
 * 不启动 PocketMine 运行时、不依赖 Level：
 * 用一个空的 Mob 子类替代真实实体，只保留 MoveControl 需要的
 * ticksLived / 坐标 / moveInDirection 接口。
 */
spl_autoload_register(function($class){
	$prefix = "pocketmine\\";
	if(strpos($class, $prefix) !== 0){
		return;
	}
	$path = dirname(__DIR__) . "/src/pocketmine/" . str_replace("\\", "/", substr($class, strlen($prefix))) . ".php";
	if(is_file($path)){
		require_once $path;
	}
});

/** 测试替身：绕过 Entity 构造函数，不访问 Level。 */
class MoveControlTestMob extends \pocketmine\entity\Mob{

	/** @var int */
	public $moveCalls = 0;

	/** @var float */
	public $lastSpeed = 0.0;

	/** @var bool */
	public $inWater = false;

	/** @var bool 让 moveInDirection 模拟被挡 */
	public $blocked = false;

	public function __construct(){
		// 不使用 Entity::__construct()，避免依赖 Level / Server。
		// closed = true 让 Entity::__destruct() 里的 close() 直接返回。
		$this->closed = true;
	}

	public function getName(){
		return "MoveControlTestMob";
	}

	/** 避免 Entity::__destruct() -> close() 访问 Server。 */
	public function close(){
		$this->closed = true;
	}

	public function isInsideOfWater(){
		return $this->inWater;
	}

	public function moveInDirection(\pocketmine\math\Vector3 $direction, float $speed) : bool{
		++$this->moveCalls;
		$this->lastSpeed = $speed;
		return !$this->blocked;
	}
}

class MoveControlTest{

	private static function near(float $a, float $b) : bool{
		return abs($a - $b) < 0.000001;
	}

	private static function newMob(int $tick = 1) : MoveControlTestMob{
		$mob = new MoveControlTestMob();
		$mob->ticksLived = $tick;
		$mob->x = 0.0;
		$mob->y = 64.0;
		$mob->z = 0.0;
		return $mob;
	}

	public static function run() : array{
		$out = [];

		// ===== normalizeSpeed：纯函数 =====
		$out[] = ["normalize_speed_land", self::near(\pocketmine\entity\ai\control\MoveControl::normalizeSpeed(0.35, false), 0.21), "0.35 陆上 -> 0.21"];
		$out[] = ["normalize_speed_water", self::near(\pocketmine\entity\ai\control\MoveControl::normalizeSpeed(0.35, true), 0.105), "0.35 水中 -> 0.105"];
		$out[] = ["normalize_speed_negative", \pocketmine\entity\ai\control\MoveControl::normalizeSpeed(-0.5, false) === 0.0, "负速度取 0"];
		$out[] = ["normalize_speed_zero", \pocketmine\entity\ai\control\MoveControl::normalizeSpeed(0.0, true) === 0.0, "零速度仍为 0"];
		$out[] = ["normalize_speed_water_slower", \pocketmine\entity\ai\control\MoveControl::normalizeSpeed(1.0, true) < \pocketmine\entity\ai\control\MoveControl::normalizeSpeed(1.0, false), "水中比陆上慢"];

		// ===== setWantedPosition 只记录，不移动 =====
		$mob = self::newMob(10);
		$control = $mob->getMoveControl();
		$out[] = ["move_control_is_lazy_singleton", $mob->getMoveControl() === $control, "getMoveControl() 应返回同一实例"];

		$control->setWantedPosition(new \pocketmine\math\Vector3(10, 64, 0), 0.35);
		$out[] = ["set_wanted_does_not_move", $mob->moveCalls === 0 and $control->hasWantedPosition(), "记录目标但本 tick 不施加 motion"];

		// ===== 同一 tick 最多执行一次 =====
		$control->tick();
		$out[] = ["first_tick_moves", $mob->moveCalls === 1, "第一次 tick 施加一次 motion"];
		$out[] = ["first_tick_uses_normalized_speed", self::near($mob->lastSpeed, 0.21), "陆上 0.35 -> 0.21"];

		$control->setWantedPosition(new \pocketmine\math\Vector3(10, 64, 0), 0.35);
		$control->tick();
		$out[] = ["same_tick_no_double_move", $mob->moveCalls === 1, "同一 tick 再次 tick 不重复施加 motion"];

		++$mob->ticksLived;
		$control->tick();
		$out[] = ["next_tick_moves_again", $mob->moveCalls === 2, "下一 tick 恢复移动"];

		// ===== 水中速度系数 =====
		$mob = self::newMob(3);
		$mob->inWater = true;
		$control = $mob->getMoveControl();
		$control->setWantedPosition(new \pocketmine\math\Vector3(10, 64, 0), 0.35);
		$control->tick();
		$out[] = ["tick_water_speed", self::near($mob->lastSpeed, 0.105), "水中 0.35 -> 0.105"];

		// ===== 到达判定：< 0.35 不再施加水平速度 =====
		$mob = self::newMob(5);
		$control = $mob->getMoveControl();
		$control->setWantedPosition(new \pocketmine\math\Vector3(0.2, 64, 0), 0.35);
		$control->tick();
		$out[] = ["arrival_no_move", $mob->moveCalls === 0 and !$control->hasWantedPosition(), "水平距离 0.2 < 0.35 视为到达"];

		// ===== 被挡计数与回调（导航层据此重算路径）=====
		$mob = self::newMob(1);
		$mob->blocked = true;
		$control = $mob->getMoveControl();
		$blockedCalls = 0;
		$control->setBlockedHandler(function($c) use (&$blockedCalls) : void{
			++$blockedCalls;
		});
		$control->setWantedPosition(new \pocketmine\math\Vector3(10, 64, 0), 0.35);

		for($i = 0; $i < 9; ++$i){
			$control->tick();
			++$mob->ticksLived;
		}
		$out[] = ["blocked_not_yet", $blockedCalls === 0, "9 tick 被挡还不触发重算"];

		$control->tick();
		++$mob->ticksLived;
		$out[] = ["blocked_handler_after_10", $blockedCalls === 1, "第 10 tick 触发一次回调"];

		$mob->blocked = false;
		$control->tick();
		$out[] = ["blocked_resets_on_success", $control->getBlockedTicks() === 0, "成功移动后 blockedTicks 归零"];

		// ===== Mob 每 tick 只接受一次 motion 写入 =====
		$mob = self::newMob(7);
		$out[] = ["can_apply_move_first", $mob->canApplyMove(), "本 tick 第一次写入允许"];
		$out[] = ["can_apply_move_second_denied", !$mob->canApplyMove(), "同一 tick 第二次写入被拒绝"];
		++$mob->ticksLived;
		$out[] = ["can_apply_move_next_tick", $mob->canApplyMove(), "下一 tick 恢复写入"];

		return $out;
	}
}
