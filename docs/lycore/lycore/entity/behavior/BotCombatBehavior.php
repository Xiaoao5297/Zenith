<?php

namespace lycore\entity\behavior;

use lycore\entity\Animal;
use lycore\entity\Entity;
use lycore\entity\Bot;
use lycore\Player;

class BotCombatBehavior extends Behavior{
	/** @var float */
	public $lookDistance = 24.0;
	/** @var bool */
	public $attackPlayer = true;
	/** @var Entity|null */
	public $enemy = null;

	public function __construct(Bot $entity, bool $attackPlayer = true){
		parent::__construct($entity);
		$this->attackPlayer = $attackPlayer;
	}

	public function getName() : string{
		return "Bot combat";
	}

	public function shouldStart() : bool{
		if($this->canUseCurrentTarget()){
			return true;
		}

		$level = $this->entity->getLevel();
		if($level === null){
			return false;
		}

		$closest = null;
		$bestDistance = $this->lookDistance * $this->lookDistance;
		if($this->attackPlayer and !$this->entity->hasPVPBotTypeSetting("playerfriendly")){
			foreach($level->getPlayers() as $player){
				if($this->shouldIgnorePlayer($player)){
					continue;
				}

				$distance = $this->entity->distanceSquared($player);
				if($distance <= $bestDistance){
					$bestDistance = $distance;
					$closest = $player;
				}
			}
		}

		if(method_exists($level, "getEntities")){
			foreach($level->getEntities() as $entity){
				if(!($entity instanceof Entity) or $entity === $this->entity){
					continue;
				}
				if($entity instanceof Animal){
					if($this->entity->hasPVPBotTypeSetting("animalfriendly")){
						continue;
					}
				}elseif(!Bot::isPVPBotHostileMobThreat($entity) or $this->entity->hasPVPBotTypeSetting("mobfriendly")){
					continue;
				}

				$distance = $this->entity->distanceSquared($entity);
				if($distance <= $bestDistance){
					$bestDistance = $distance;
					$closest = $entity;
				}
			}
		}

		$this->enemy = $closest;

		return $this->enemy instanceof Entity;
	}

	public function canContinue() : bool{
		return $this->canUseCurrentTarget();
	}

	public function onTick(){
		if(!($this->enemy instanceof Entity) or !$this->enemy->isAlive()){
			return;
		}

		/** @var Bot $bot */
		$bot = $this->entity;
		$bot->tickPVPBotGoldenAppleCombat($this->enemy);
		$bot->tryUsePVPBotHealingPotion();

		$distance = $bot->distance($this->enemy);
		$inMeleeRange = $distance <= Bot::PVPBOT_MELEE_RANGE;
		$inBowMode = !$inMeleeRange && $bot->canUsePVPBotBowRangedModeAgainst($this->enemy, $distance);
		if($inBowMode){
			$bot->enterPVPBotBowCombatStance();
		}else{
			$bot->leavePVPBotBowCombatStance();
		}
		$shotArrow = $inBowMode && $bot->tryPVPBotBowAttack($this->enemy);
		$bot->getBotMovementAI()->tick($this->enemy, $inMeleeRange, $inBowMode);
		if(!$shotArrow and $inMeleeRange){
			$bot->tryPVPBotMeleeAttack($this->enemy);
		}

		$this->swimming();
	}

	public function onEnd(){
		/** @var Bot $bot */
		$bot = $this->entity;
		$bot->leavePVPBotBowCombatStance();
		$bot->getBotMovementAI()->clear();
	}

	private function canUseCurrentTarget() : bool{
		if(!($this->enemy instanceof Entity)){
			return false;
		}
		if($this->enemy->closed or !$this->enemy->isAlive()){
			$this->enemy = null;
			return false;
		}
		if($this->enemy instanceof Player and ($this->entity->hasPVPBotTypeSetting("playerfriendly") or $this->shouldIgnorePlayer($this->enemy))){
			$this->enemy = null;
			return false;
		}
		if($this->enemy instanceof Animal and $this->entity->hasPVPBotTypeSetting("animalfriendly")){
			$this->enemy = null;
			return false;
		}
		if(Bot::isPVPBotHostileMobThreat($this->enemy) and $this->entity->hasPVPBotTypeSetting("mobfriendly")){
			$this->enemy = null;
			return false;
		}

		$loseDistance = max($this->lookDistance, Bot::PVPBOT_TARGET_LOSE_DISTANCE);
		return $this->entity->distanceSquared($this->enemy) <= ($loseDistance * $loseDistance);
	}

	private function shouldIgnorePlayer(Player $player) : bool{
		if(method_exists($player, "isConnected") and !$player->isConnected()){
			return true;
		}
		if(!$player->isAlive()){
			return true;
		}
		if(method_exists($player, "isSurvival") and method_exists($player, "isAdventure")){
			return !$player->isSurvival() and !$player->isAdventure();
		}
		if(method_exists($player, "isSurvival")){
			return !$player->isSurvival();
		}

		return false;
	}
}
