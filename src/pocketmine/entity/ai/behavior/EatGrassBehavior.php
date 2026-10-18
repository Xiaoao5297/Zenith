<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\network\protocol\EntityEventPacket;

/**
 * 羊吃草：只有完整播放完吃草动画才恢复羊毛并消耗草方块。
 * 被更高优先级行为打断时不会产生“凭空长毛/方块变泥土”的副作用。
 */
class EatGrassBehavior extends Behavior{

	/** @var int */
	public $timeLeft = 0;

	/** @var bool */
	public $completed = false;

	/** @var bool */
	public $animationSent = false;

	public function getPriority() : int{
		return 4;
	}

	public function getControlFlags() : int{
		return self::CONTROL_MOVE;
	}

	public function getName() : string{
		return "EatGrass";
	}

	public function shouldStart() : bool{
		if($this->entity->hasWool()){
			return false;
		}

		$level = $this->entity->getLevel();
		if($level === null){
			return false;
		}

		$x = (int) floor($this->entity->x);
		$y = (int) floor($this->entity->y) - 1;
		$z = (int) floor($this->entity->z);

		return $level->getBlockIdAt($x, $y, $z) === 2 and mt_rand(0, 400) === 22;
	}

	public function onStart() : void{
		$this->timeLeft = 30;
		$this->completed = false;
		$this->animationSent = false;
		$this->entity->getNavigator()->clearPath();
	}

	public function canContinue() : bool{
		if(--$this->timeLeft > 0){
			return true;
		}

		$this->completed = true;
		return false;
	}

	public function onTick(){
		if($this->animationSent){
			return;
		}

		$this->animationSent = true;
		$pk = new EntityEventPacket();
		$pk->eid = $this->entity->getId();
		$pk->event = EntityEventPacket::EAT_GRASS_ANIMATION;
		foreach($this->entity->getViewers() as $player){
			$player->dataPacket($pk);
		}
	}

	public function onEnd(){
		if(!$this->completed){
			return;
		}

		$level = $this->entity->getLevel();
		if($level === null){
			return;
		}

		$x = (int) floor($this->entity->x);
		$y = (int) floor($this->entity->y) - 1;
		$z = (int) floor($this->entity->z);

		if($level->getBlockIdAt($x, $y, $z) !== 2){
			return;
		}

		$this->entity->sethasWool(true);
		$level->setBlockIdAt($x, $y, $z, 3);
	}
}
