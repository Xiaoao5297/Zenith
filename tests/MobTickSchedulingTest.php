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
 * 生物每 tick 更新回归测试。
 *
 * 背景：Entity::onUpdate() 正常返回 false，Level::doTick() 会把实体从
 * updateEntities 移除；只有随机区块 tick 才重新 scheduleUpdate()。
 * 移动中的生物因此只在区块被选中时更新一次，客户端看到固定间隔的
 * “弹一下/跳一下”。InCore 在 Mob 移动后直接把 $hasUpdate 置 true。
 *
 * 这里用桩 Level 跑真实的 Mob::onUpdate()，断言活着的生物持续返回 true，
 * 并模拟 Level 的移除逻辑，确认它不会在第二 tick 被踢出更新队列。
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

class MobTickSchedulingTestMob extends MobStepTestMob{

	/** Entity::entityBaseTick() 会检查方块碰撞；测试里不需要。 */
	protected function checkBlockCollision(){
	}
}

class MobTickSchedulingTest{

	private static function newMob() : MobTickSchedulingTestMob{
		$level = new MobStepTestLevel(function(int $x, int $z) : int{
			return 64;
		});
		$mob = new MobTickSchedulingTestMob();
		$mob->setLevelForTest($level);
		$mob->closed = false;
		$mob->setPosition(new \pocketmine\level\Position(0.5, 64.0, 0.5, $level));
		$mob->onGround = true;
		$mob->ticksLived = 0;

		$timings = new class{
			public function startTiming($id = null) : void{}
			public function stopTiming($id = null) : void{}
		};
		$prop = (new \ReflectionClass(\pocketmine\entity\Entity::class))->getProperty("timings");
		$prop->setAccessible(true);
		$prop->setValue($mob, $timings);

		$chunk = new class{
			public function getX() : int{ return 0; }
			public function getZ() : int{ return 0; }
		};
		$chunkProp = (new \ReflectionClass(\pocketmine\entity\Entity::class))->getProperty("chunk");
		$chunkProp->setAccessible(true);
		$chunkProp->setValue($mob, $chunk);

		return $mob;
	}

	public static function run() : array{
		$out = [];

		if(\pocketmine\block\Block::$fullList === null){
			\pocketmine\block\Block::init();
		}

		$timings = new class{
			public function startTiming($id = null) : void{}
			public function stopTiming($id = null) : void{}
		};
		foreach([
			"timerEntityBaseTick",
			"timerLivingEntityBaseTick",
			"timerEntityAI",
			"timerEntityAICollision",
			"timerEntityAIMove",
			"timerEntityTickRest",
			"entityMoveTimer"
		] as $name){
			\pocketmine\event\Timings::$$name = $timings;
		}

		$mob = self::newMob();
		$out[] = ["alive_mob_returns_true", $mob->onUpdate(1) === true, "活着的生物第 1 tick 应返回 true"];

		$out[] = ["alive_mob_returns_true_next_tick", $mob->onUpdate(2) === true, "活着的生物第 2 tick 仍应返回 true"];

		// 模拟 Level::doTick()：返回 false 会从 updateEntities 移除。
		$mob = self::newMob();
		$entities = [$mob->getId() => $mob];
		for($t = 1; $t <= 5; ++$t){
			foreach($entities as $id => $entity){
				if($entity->closed or !$entity->onUpdate($t)){
					unset($entities[$id]);
				}
			}
		}
		$out[] = ["mob_stays_in_update_queue", count($entities) === 1, "5 tick 后 updateEntities 仍有生物，实际 = " . count($entities)];

		return $out;
	}
}
