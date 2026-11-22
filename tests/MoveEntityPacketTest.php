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
 * MoveEntityPacket 线格式回归测试。
 *
 * MCPE 0.14 的 MoveEntityPacket 每条实体记录为：
 *   eid(8) + x/y/z(3 float) + yaw/pitch/headYaw(3 float)
 * 而 Level 内部按 [eid, x, y, z, yaw, headYaw, pitch] 存储，因此编码层
 * 必须把最后两段重排为 pitch、headYaw。核心协议与 v11 都要遵守。
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

require_once __DIR__ . "/MobStepTest.php";

if(!defined("ENDIANNESS")){
	define("ENDIANNESS", pack("d", 1) === "\77\360\0\0\0\0\0\0" ? \pocketmine\utils\Binary::BIG_ENDIAN : \pocketmine\utils\Binary::LITTLE_ENDIAN);
}

use pocketmine\entity\Entity;
use pocketmine\network\protocol\MoveEntityPacket;
use pocketmine\network\protocol\v11\MoveEntityPacket as MoveEntityPacketV11;
use pocketmine\utils\Binary;

class MoveEntityPacketTest{

	/**
	 * @return array{0:float,1:float,2:float}
	 */
	private static function readTail(string $buffer) : array{
		$offset = 1 + 4; // NETWORK_ID + count
		$offset += 8; // eid
		$offset += 12; // x/y/z
		return [
			Binary::readFloat(substr($buffer, $offset, 4)), // yaw
			Binary::readFloat(substr($buffer, $offset + 4, 4)), // pitch
			Binary::readFloat(substr($buffer, $offset + 8, 4)) // headYaw
		];
	}

	/**
	 * Level 存储顺序：[eid, x, y, z, yaw, headYaw, pitch]。
	 */
	private static function entity() : array{
		return [[7, 1.5, 64.25, 2.5, 10.0, 20.0, 30.0]];
	}

	public static function run() : array{
		$out = [];

		$core = new MoveEntityPacket();
		$core->entities = self::entity();
		$core->encode();
		list($yaw, $pitch, $headYaw) = self::readTail($core->getBuffer());
		$out[] = ["core_yaw", abs($yaw - 10.0) < 0.0001, "yaw=" . $yaw];
		$out[] = ["core_pitch", abs($pitch - 30.0) < 0.0001, "pitch=" . $pitch];
		$out[] = ["core_head_yaw", abs($headYaw - 20.0) < 0.0001, "headYaw=" . $headYaw];

		$v11 = new MoveEntityPacketV11();
		$v11->entities = self::entity();
		$v11->encode();
		list($yaw, $pitch, $headYaw) = self::readTail($v11->getBuffer());
		$out[] = ["v11_yaw", abs($yaw - 10.0) < 0.0001, "yaw=" . $yaw];
		$out[] = ["v11_pitch", abs($pitch - 30.0) < 0.0001, "pitch=" . $pitch];
		$out[] = ["v11_head_yaw", abs($headYaw - 20.0) < 0.0001, "headYaw=" . $headYaw];

		// 生物 MoveEntity 的 y 必须是脚底；玩家才加眼睛偏移。
		$mob = new MobStepTestMob();
		$mob->y = 64.25;
		$method = new ReflectionMethod(Entity::class, "getNetworkY");
		$method->setAccessible(true);
		$out[] = ["mob_network_y_is_feet", abs($method->invoke($mob) - 64.25) < 0.0001, "y=" . $method->invoke($mob)];

		$source = file_get_contents(dirname(__DIR__) . "/src/pocketmine/entity/Entity.php");
		$out[] = ["player_network_y_keeps_eye_offset",
			$source !== false and strpos($source, "instanceof \\pocketmine\\Player ? \$this->y + \$this->getEyeHeight()") !== false,
			"Player 应保留脚底 + 眼睛偏移"];
		$out[] = ["mob_network_y_has_no_eye_offset",
			$source !== false and strpos($source, "return \$this instanceof \\pocketmine\\Player ? \$this->y + \$this->getEyeHeight() : \$this->y;") !== false,
			"非 Player 应直接发送脚底 y"];

		// 支撑分支把 onGround 设为 true 时必须清掉残留的负 motionY，
		// 否则 Creature 的 motionY *= -0.5 会把它翻成向上速度。
		$out[] = ["ground_support_zeroes_motion_y",
			$source !== false and substr_count($source, "\$this->motionY = 0;") >= 2,
			"checkGroundState 的两个支撑分支都应清零 motionY"];

		return $out;
	}
}
