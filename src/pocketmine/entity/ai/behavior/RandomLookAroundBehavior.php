<?php

namespace pocketmine\entity\ai\behavior;

class RandomLookAroundBehavior extends Behavior{

	/** @var int */
	private $duration = 0;

	/** @var float */
	private $targetYaw = 0;

	/** @var float */
	private $targetPitch = 0;

	public function getPriority() : int{
		return 8;
	}

	public function getControlFlags() : int{
		return self::CONTROL_LOOK;
	}

	public function getName() : string{
		return "RandomLookAround";
	}

	public function shouldStart() : bool{
		if(mt_rand(0, 30) !== 0){
			return false;
		}

		$this->duration = 20 + mt_rand(0, 20);
		$this->targetYaw = $this->normalizeAngle($this->entity->yaw + mt_rand(-120, 120));
		$this->targetPitch = mt_rand(-20, 20);
		return true;
	}

	public function canContinue() : bool{
		return $this->duration-- > 0;
	}

	public function onTick(){
		$this->entity->yaw = $this->turnToward($this->entity->yaw, $this->targetYaw, 15.0);
		$this->entity->pitch = $this->turnToward($this->entity->pitch, $this->targetPitch, 10.0);
		$this->swimming();
	}

	public function onEnd(){
		$this->entity->pitch = 0;
	}
}
