<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\Player;

/**
 * 被玩家手持食物吸引（原版 TemptGoal 的简化版）。
 * 只负责靠近和注视，不改变玩家的手持物品。
 */
class FindFoodBehavior extends Behavior{

	/** @var float */
	public $speed;

	/** @var float */
	public $lookDistance = 8.0;

	/** @var int */
	public $foodID;

	/** @var Player|null */
	public $player = null;

	public function __construct(Mob $entity, int $foodID, float $speed = 0.35){
		parent::__construct($entity);
		$this->speed = $speed;
		$this->foodID = $foodID;
	}

	public function getPriority() : int{
		return 5;
	}

	public function getControlFlags() : int{
		return self::CONTROL_MOVE | self::CONTROL_LOOK;
	}

	public function getName() : string{
		return "FindFood";
	}

	public function shouldStart() : bool{
		$best = null;
		$bestDistance = $this->lookDistance;

		foreach($this->entity->level->getPlayers() as $player){
			if(!$player->isConnected() or !$player->isAlive()){
				continue;
			}
			if($player->getItemInHand()->getID() !== $this->foodID){
				continue;
			}

			$distance = $this->entity->distance($player);
			if($distance < $bestDistance){
				$best = $player;
				$bestDistance = $distance;
			}
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
		if($this->player->getItemInHand()->getID() !== $this->foodID){
			return false;
		}
		return $this->entity->distance($this->player) <= $this->lookDistance + 2.0;
	}

	public function onTick(){
		if($this->player === null){
			return;
		}

		$this->lookAt($this->player);

		// 靠得太近就停下，但继续看着玩家，避免每 tick 结束又重新开始
		if($this->entity->distance($this->player) <= 1.5){
			$this->entity->getNavigator()->clearPath();
			return;
		}

		$this->entity->getNavigator()->moveTo($this->player, $this->speed);
		$this->swimming();
	}

	public function onEnd(){
		$this->player = null;
		$this->entity->getNavigator()->clearPath();
	}
}
