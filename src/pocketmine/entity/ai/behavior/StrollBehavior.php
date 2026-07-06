<?php

namespace pocketmine\entity\ai\behavior;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;

class StrollBehavior extends Behavior{

    public $speed;
    public $timeout;
    public $timeLeft;

    /** @var Vector3|null */
    public $target = null;

    public function __construct(Mob $entity, float $speed = 0.7, int $timeout = 120){
        parent::__construct($entity);
        $this->speed = $speed;
        $this->timeout = $timeout;
        $this->timeLeft = $timeout;
    }

    public function getPriority(): int{
        return 7;
    }

    public function getName() : string{
        return "Stroll";
    }

    public function shouldStart() : bool{
        return mt_rand(0, 10) == 0;
    }

    public function canContinue() : bool{
        if($this->timeLeft-- <= 0){
            return false;
        }
        if($this->target !== null and $this->entity->distance($this->target) < 1.0){
            return false;
        }
        return true;
    }

    public function onStart(): void{
        $x = $this->entity->x + mt_rand(-1000, 1000) / 100;
        $z = $this->entity->z + mt_rand(-1000, 1000) / 100;
        $this->target = new Vector3($x, $this->entity->y, $z);
        $this->timeLeft = $this->timeout;
    }

    public function onTick(){
        if($this->target === null){
            return;
        }

        $dx = $this->target->x - $this->entity->x;
        $dz = $this->target->z - $this->entity->z;
        $dist = sqrt($dx * $dx + $dz * $dz);

        if($dist < 0.5){
            return;
        }

        $this->entity->yaw = -atan2($dx, $dz) * (180 / M_PI);

        $this->moveForward($this->speed);
        $this->swimming();
    }

    public function onEnd(){
        $this->target = null;
        $this->timeLeft = $this->timeout;
        $this->entity->setMotion(new Vector3(0, 0, 0));
    }
}
