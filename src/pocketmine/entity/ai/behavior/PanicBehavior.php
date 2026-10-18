<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\event\entity\EntityDamageByEntityEvent;
use pocketmine\math\Vector3;

class PanicBehavior extends StrollBehavior{

	public function __construct(Mob $entity, float $speed = 0.55, int $timeout = 60){
		parent::__construct($entity, $speed, $timeout);
	}

	public function getPriority() : int{
		return 1;
	}

	public function getControlFlags() : int{
		return self::CONTROL_MOVE;
	}

	public function getName() : string{
		return "Panic";
	}

	public function shouldStart() : bool{
		return $this->entity->getLastDamageCause() !== null;
	}

	public function onStart() : void{
		$cause = $this->entity->getLastDamageCause();
		$this->target = null;

		if($cause instanceof EntityDamageByEntityEvent and $cause->getDamager() !== null){
			$attacker = $cause->getDamager();
			$dx = $this->entity->x - $attacker->x;
			$dz = $this->entity->z - $attacker->z;
			$len = sqrt($dx * $dx + $dz * $dz);
			if($len > 0.001){
				$dx = ($dx / $len) * 10;
				$dz = ($dz / $len) * 10;
				$x = (int) floor($this->entity->x + $dx);
				$z = (int) floor($this->entity->z + $dz);
				$groundY = Mob::pickGroundY($this->entity->getLevel(), $x, (int) floor($this->entity->y), $z);
				if($groundY !== null){
					$this->target = new Vector3($x + 0.5, $groundY, $z + 0.5);
				}
			}
		}

		if($this->target === null){
			$this->target = $this->pickTarget();
		}
		$this->timeLeft = $this->timeout;
	}

	public function onEnd(){
		parent::onEnd();
		$this->entity->resetLastDamageCause();
	}
}
