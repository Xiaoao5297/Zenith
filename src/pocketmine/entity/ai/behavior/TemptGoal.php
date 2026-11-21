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

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\Player;

/**
 * 原版 EntityAITempt：玩家手持食物时把动物吸引过来。
 *
 * 与旧版 FindFoodBehavior 的区别：
 *  - 支持多种食物（鸡对种子、猪对胡萝卜/马铃薯）；
 *  - 可选 scaredByPlayerMovement：玩家边靠近边移动时动物会被吓退；
 *  - 距离判定与原版一致（10 格内可见，2 格内停下）。
 */
class TemptGoal extends Behavior{

	/** 原版 EntityAITempt 的吸引半径 */
	public const TEMPT_DISTANCE = 10.0;

	/** 原版走到该距离内就停止寻路 */
	public const STOP_DISTANCE = 2.0;

	/** @var array 食物 ID 集合，形如 [296 => true] */
	public $items;

	/** @var float */
	public $speed;

	/** @var bool */
	public $scaredByPlayerMovement;

	/** @var Player|null */
	public $player = null;

	/**
	 * @param array $items 食物 ID 列表（如 [296]），也接受 [296 => true]
	 */
	public function __construct(Mob $entity, array $items, float $speed = 1.0, bool $scaredByPlayerMovement = false){
		parent::__construct($entity);
		$this->items = self::normalizeItems($items);
		$this->speed = $speed;
		$this->scaredByPlayerMovement = $scaredByPlayerMovement;
	}

	public function getPriority() : int{
		return 4;
	}

	public function getControlFlags() : int{
		return self::CONTROL_MOVE | self::CONTROL_LOOK;
	}

	public function getName() : string{
		return "Tempt";
	}

	public function shouldStart() : bool{
		$level = $this->entity->getLevel();
		if($level === null){
			return false;
		}

		$best = null;
		$bestDistance = self::TEMPT_DISTANCE;

		foreach($level->getPlayers() as $player){
			if(!$player->isConnected() or !$player->isAlive() or $player->isSpectator()){
				continue;
			}

			$distance = $this->entity->distance($player);
			if($distance >= $bestDistance){
				continue;
			}

			$itemId = $player->getInventory()->getItemInHand()->getId();
			if(!self::isTemptedBy($itemId, $this->items, $distance, $player->isMoving(), $this->scaredByPlayerMovement)){
				continue;
			}

			$best = $player;
			$bestDistance = $distance;
		}

		if($best === null){
			return false;
		}

		$this->player = $best;
		return true;
	}

	public function canContinue() : bool{
		if($this->player === null or !$this->player->isConnected() or !$this->player->isAlive()){
			return false;
		}
		if(!self::containsItem($this->items, $this->player->getInventory()->getItemInHand()->getId())){
			return false;
		}
		return $this->entity->distance($this->player) < self::TEMPT_DISTANCE;
	}

	public function onTick(){
		if($this->player === null){
			return;
		}

		$this->lookAt($this->player);
		$this->entity->getNavigator()->moveTo($this->player, $this->speed);

		// 原版：距离平方 < 4.0（即 2 格）时清空路径，避免动物顶着玩家推挤
		if($this->entity->distance($this->player) < self::STOP_DISTANCE){
			$this->entity->getNavigator()->clearPath();
		}
	}

	public function onEnd(){
		$this->player = null;
		$this->entity->getNavigator()->clearPath();
	}

	/**
	 * 吸引判定，抽成纯函数便于离线测试。
	 *
	 * @param int   $itemId              玩家手持物品 ID
	 * @param array $items               食物 ID 列表
	 * @param float $distance            玩家与动物的距离
	 * @param bool  $playerMoving        玩家本 tick 是否在移动
	 * @param bool  $scaredByPlayerMovement 玩家移动时是否放弃吸引
	 */
	public static function isTemptedBy(int $itemId, array $items, float $distance, bool $playerMoving, bool $scaredByPlayerMovement) : bool{
		if($distance >= self::TEMPT_DISTANCE){
			return false;
		}
		if($scaredByPlayerMovement and $playerMoving){
			return false;
		}
		return self::containsItem($items, $itemId);
	}

	/**
	 * 同时兼容 [296, 391] 与 [296 => true] 两种写法。
	 */
	public static function containsItem(array $items, int $itemId) : bool{
		foreach($items as $key => $value){
			if(is_int($value) and $value === $itemId){
				return true;
			}
			if($value === true and $key === $itemId){
				return true;
			}
		}
		return false;
	}

	public static function normalizeItems(array $items) : array{
		$map = [];
		foreach($items as $key => $value){
			if(is_int($value)){
				$map[$value] = true;
			}elseif($value === true){
				$map[(int) $key] = true;
			}
		}
		return $map;
	}
}
