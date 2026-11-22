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
 * 生物移动的台阶判定。
 *
 * 回归背景：moveInDirection() 原来用 (groundY - floor(feetY)) 判断台阶，
 * 贴地时一点点浮点误差就会被当成 1 格台阶，导致所有生物边走边跳。
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

class MobMovementTest{

	public static function run() : array{
		$out = [];

		// 贴地时脚底 y 会比方块顶面低一点点，不能算台阶
		$out[] = ["no_jump_on_epsilon", !\pocketmine\entity\Mob::needsStepJump(63.999, 64, true, false), "feet 63.999 -> ground 64"];
		$out[] = ["no_jump_on_epsilon_2", !\pocketmine\entity\Mob::needsStepJump(63.99999, 64, true, false), "feet 63.99999 -> ground 64"];
		$out[] = ["no_jump_on_flat", !\pocketmine\entity\Mob::needsStepJump(64.0, 64, true, false), "feet 64 -> ground 64"];

		// stepHeight = 1.0（恢复原版）：1 格整台阶由 Entity::move() 自动抬升，不需要跳；
		// 显式传入更低的 stepHeight 时才保留 0.42 跳跃的兜底语义。
		$out[] = ["no_jump_on_one_block", !\pocketmine\entity\Mob::needsStepJump(64.0, 65, true, false), "默认 stepHeight 1.0 时 1 格台阶不跳"];
		$out[] = ["jump_when_step_height_too_low", \pocketmine\entity\Mob::needsStepJump(64.0, 65, true, false, 0.6), "显式 stepHeight 0.6 时兜底跳 1 格"];
		$out[] = ["no_jump_when_step_height_enough", !\pocketmine\entity\Mob::needsStepJump(64.0, 65, true, false, 1.0), "stepHeight 1.0 时不跳"];
		$out[] = ["no_jump_on_half_step", !\pocketmine\entity\Mob::needsStepJump(64.0, 64, true, false, 0.6), "y=64 平面上不需要跳"];

		// 2 格高是墙，不是台阶（moveInDirection 会直接返回 false）
		$out[] = ["no_jump_on_wall", !\pocketmine\entity\Mob::needsStepJump(64.0, 66, true, false), "feet 64 -> ground 66"];
		$out[] = ["no_jump_on_two_block_even_low_step", !\pocketmine\entity\Mob::needsStepJump(64.0, 66, true, false, 0.6), "2 格超出跳跃高度"];

		// 空中、水里不跳
		$out[] = ["no_jump_in_air", !\pocketmine\entity\Mob::needsStepJump(64.0, 65, false, false), "airborne"];
		$out[] = ["no_jump_in_water", !\pocketmine\entity\Mob::needsStepJump(64.0, 65, true, true), "in water"];

		// 与恢复后的原版行为一致：默认 stepHeight = 1.0，1 格台阶交给 Entity::move() 自动抬升。
		$stepHeight = (new \ReflectionClass(\pocketmine\entity\Mob::class))->getDefaultProperties()["stepHeight"];
		$out[] = ["mob_step_height_is_one", $stepHeight === 1.0, "Mob::\$stepHeight = " . var_export($stepHeight, true)];

		// 寻路层不再自己塞 0.42 的跳跃，台阶统一交给 moveInDirection
		$nav = file_get_contents(dirname(__DIR__) . "/src/pocketmine/entity/ai/navigation/PathNavigate.php");
		$out[] = ["navigator_has_no_own_jump", strpos($nav, "0.42") === false, "PathNavigate 不应再设置 0.42 的跳跃速度"];

		// ===== 头部朝向与身体朝向分离（原版 rotationYawHead / renderYawOffset）=====
		// 头最多拧到身体两侧 75 度
		$out[] = ["head_yaw_clamp_right", \pocketmine\entity\Entity::clampHeadYaw(0, 90) === 75.0, "body 0, head 90 -> 75"];
		$out[] = ["head_yaw_clamp_left", \pocketmine\entity\Entity::clampHeadYaw(0, -90) === -75.0, "body 0, head -90 -> -75"];
		$out[] = ["head_yaw_within_limit", \pocketmine\entity\Entity::clampHeadYaw(0, 45) === 45.0, "body 0, head 45 -> 45"];
		$out[] = ["head_yaw_crosses_180", \pocketmine\entity\Entity::clampHeadYaw(170, -170) === -170.0, "body 170, head -170（真实夹角 20 度）"];
		$out[] = ["head_yaw_crosses_180_clamped", \pocketmine\entity\Entity::clampHeadYaw(170, -100) === -115.0, "body 170, head -100 -> -115（+75）"];

		// 转身限速 10 度/tick
		$out[] = ["head_yaw_turn_step", \pocketmine\entity\Entity::turnTowardAngle(0, 90, 10) === 10.0, "0 -> 90 每 tick 10 度"];
		$out[] = ["head_yaw_turn_short_way", \pocketmine\entity\Entity::turnTowardAngle(175, -175, 10) === -175.0, "175 -> -175 走近路"];
		$out[] = ["head_yaw_turn_reaches", \pocketmine\entity\Entity::turnTowardAngle(0, 5, 10) === 5.0, "差距小于步长时直接到位"];

		// 广播使用 headYaw，而不是把头也写成身体
		$entitySrc = file_get_contents(dirname(__DIR__) . "/src/pocketmine/entity/Entity.php");
		$out[] = ["broadcast_uses_head_yaw", strpos($entitySrc, "\$this->z, \$this->yaw, \$this->pitch, \$this->headYaw);") !== false, "addEntityMovement 第 8 个参数应为 headYaw"];

		return $out;
	}
}
