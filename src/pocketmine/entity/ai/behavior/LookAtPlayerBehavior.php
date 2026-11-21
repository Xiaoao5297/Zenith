<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\Player;

class LookAtPlayerBehavior extends Behavior{

	/** @var Player|null */
	public $player = null;

	/** @var float */
	public $lookDistance = 6.0;

	/** @var int */
	private $duration = 0;

	public function __construct(Mob $entity, float $lookDistance = 6.0){
		parent::__construct($entity);
		$this->lookDistance = $lookDistance;
	}

	public function getPriority() : int{
		return 6;
	}

	public function getControlFlags() : int{
		return self::CONTROL_LOOK;
	}

	public function getName() : string{
		return "LookAtPlayer";
	}

	public function shouldStart() : bool{
		if(mt_rand(0, 20) !== 0){
			return false;
		}

		$best = null;
		$bestDistance = $this->lookDistance;
		foreach($this->entity->level->getPlayers() as $player){
			if(!$player->isAlive() or !$player->isConnected()){
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
		$this->duration = 40 + mt_rand(0, 40);
		return true;
	}

	public function canContinue() : bool{
		if($this->player === null or !$this->player->isAlive() or !$this->player->isConnected()){
			return false;
		}
		if($this->entity->distance($this->player) > $this->lookDistance + 2){
			return false;
		}
		return $this->duration-- > 0;
	}

	public function onTick(){
		if($this->player !== null){
			$this->lookAt($this->player);
		}
		$this->swimming();
	}

	public function onEnd(){
		$this->player = null;
		$this->entity->pitch = 0;
	}
}
