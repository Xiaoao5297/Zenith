<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\Player;

class CreeperBehavior extends Behavior{

	/** @var float */
	public $lookDistance = 16.0;

	/** @var float */
	public $explodeRadius = 2.5;

	/** @var float */
	public $speed = 0.30;

	/** @var Player|null */
	public $enemy = null;

	/** @var int */
	private $searchCooldown = 0;

	/** @var int */
	private $canSeeCooldown = 0;

	/** @var bool */
	private $canSeeTarget = false;

	public function getPriority() : int{
		return 1;
	}

	public function getControlFlags() : int{
		return self::CONTROL_MOVE | self::CONTROL_LOOK;
	}

	public function getName() : string{
		return "CreeperExplode";
	}

	public function shouldStart() : bool{
		if($this->isValidTarget($this->enemy)){
			return true;
		}
		$this->enemy = null;

		if($this->searchCooldown > 0){
			--$this->searchCooldown;
			return false;
		}
		$this->searchCooldown = 10;

		$best = null;
		$bestDistance = $this->lookDistance;
		foreach($this->entity->level->getPlayers() as $player){
			if(!$player->isAlive() or !$player->isConnected() or !$player->isSurvival()){
				continue;
			}
			$distance = $this->entity->distance($player);
			if($distance < $bestDistance){
				$best = $player;
				$bestDistance = $distance;
			}
		}

		$this->enemy = $best;
		return $best !== null;
	}

	private function isValidTarget($target) : bool{
		if(!$target instanceof Player or !$target->isAlive() or !$target->isConnected() or !$target->isSurvival()){
			return false;
		}
		return $this->entity->distance($target) <= $this->lookDistance + 4;
	}

	public function canContinue() : bool{
		return $this->isValidTarget($this->enemy);
	}

	public function onTick(){
		if($this->enemy === null){
			return;
		}

		$distance = $this->entity->distance($this->enemy);
		$this->lookAt($this->enemy);

		if($this->canSeeCooldown > 0){
			--$this->canSeeCooldown;
		}else{
			$this->canSeeCooldown = 5;
			$this->canSeeTarget = $this->canSee($this->enemy);
		}

		if($distance > 1.8){
			$this->entity->getNavigator()->moveTo($this->enemy, $this->speed);
		}else{
			$this->entity->getNavigator()->clearPath();
		}

		// 只有贴近且有视线时才膨胀，玩家跑远或隔墙时回缩
		$shouldSwell = $distance <= $this->explodeRadius and $this->canSeeTarget;
		if($shouldSwell !== $this->entity->isSwelled()){
			$this->entity->setSwelled($shouldSwell);
		}

		$this->swimming();
	}

	public function onEnd(){
		$this->enemy = null;
		$this->searchCooldown = 0;
		$this->canSeeTarget = false;
		$this->entity->setSwelled(false);
		$this->entity->getNavigator()->clearPath();
	}
}
