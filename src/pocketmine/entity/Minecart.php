<?php


namespace pocketmine\entity;

use pocketmine\block\Block;
use pocketmine\block\Rail;
use pocketmine\math\Math;
use pocketmine\math\Vector3;
use pocketmine\event\entity\EntityDamageEvent;
use pocketmine\network\protocol\AddEntityPacket;
use pocketmine\item\Item as ItemItem;
use pocketmine\Player;

class Minecart extends Vehicle {
    const NETWORK_ID = 84;

    const TYPE_NORMAL = 1;
    const TYPE_CHEST = 2;
    const TYPE_HOPPER = 3;
    const TYPE_TNT = 4;

    const STATE_INITIAL = 0;
    const STATE_ON_RAIL = 1;
    const STATE_OFF_RAIL = 2;

    public $height = 0.7;
    public $width = 0.98;

    public $drag = 0.1;
    public $gravity = 0.5;

    public $isMoving = false;
    public $moveSpeed = 0.5;

    private $state = Minecart::STATE_INITIAL;
    private $direction = -1;
    private $moveVector = [];
	public $motionX = 0;
	public $motionY = 0;
	public $motionZ = 0;

    public function initEntity(){
        $this->setMaxHealth(1);
        $this->setHealth($this->getMaxHealth());
        $this->moveVector[Entity::NORTH] = new Vector3(-1, 0, 0);
        $this->moveVector[Entity::SOUTH] = new Vector3(1, 0, 0);
        $this->moveVector[Entity::EAST] = new Vector3(0, 0, -1);
        $this->moveVector[Entity::WEST] = new Vector3(0, 0, 1);
        parent::initEntity();
    }

    public function getName(): string{
        return "Minecart";
    }

    public function getType(): int{
        return self::TYPE_NORMAL;
    }

    public function onUpdate($currentTick){
        if ($this->closed !== false) {
            return false;
        }

        $tickDiff = $currentTick - $this->lastUpdate;
        if ($tickDiff <= 1) {
            return false;
        }

        $this->lastUpdate = $currentTick;

        $this->timings->startTiming();

        $hasUpdate = false;

        if ($this->isAlive()){
			$movingType = $this->getLevel()->getServer()->minecartMovingType;
			if($movingType == -1) return false;
			elseif($movingType == 1){
			$p = $this->getLinkedEntity();
			if ($p instanceof Player){
				if ($this->state === Minecart::STATE_INITIAL){
					$this->checkIfOnRail();
				}elseif($this->state === Minecart::STATE_ON_RAIL){
					$hasUpdate = $this->forwardOnRail($p);
					$this->updateMovement();
				}
			}
			}elseif($movingType == 0){
				$p = $this->getLinkedEntity();
				if($p instanceof Player){
					$this->motionX = -sin($p->getYaw() / 180 * M_PI);
					$this->motionZ = cos($p->getYaw() / 180 * M_PI);
				}
				$target = $this->getLevel()->getBlock($this->add($this->motionX, 0, $this->motionZ)->round());
				$target2 = $this->getLevel()->getBlock($this->add($this->motionX, 0, $this->motionZ)->floor());
				if($target->getId() != ItemItem::AIR or $target2->getId() != ItemItem::AIR) $this->motionY = $this->gravity * 3;
				else $this->motionY -= $this->gravity;

				if($this->checkObstruction($this->x, $this->y, $this->z)){
					$hasUpdate = true;
				}

				$this->move($this->motionX, $this->motionY, $this->motionZ);
				$this->updateMovement();

				$friction = 1 - $this->drag;

				if($this->onGround and (abs($this->motionX) > 0.00001 or abs($this->motionZ) > 0.00001)){
					$friction = $this->getLevel()->getBlock($this->temporalVector->setComponents((int) floor($this->x), (int) floor($this->y - 1), (int) floor($this->z) - 1))->getFrictionFactor() * $friction;
				}

				$this->motionX *= $friction;
				$this->motionY *= 1 - $this->drag;
				$this->motionZ *= $friction;

				if($this->onGround){
					$this->motionY *= -0.5;
				}
			}elseif($movingType == 2){
				$p = $this->getLinkedEntity();
				if($p instanceof Player){
				if ($this->state === Minecart::STATE_INITIAL){
					for ($y = -1; $y !== 2 and $this->state === Minecart::STATE_INITIAL; $y++) {
						$positionToCheck = $this->temporalVector->setComponents($this->x, $this->y + $y, $this->z);
						$block = $this->level->getBlock($positionToCheck);
						if ($this->isRail($block)) {
							$minecartPosition = $positionToCheck->floor()->add(0.5, 0, 0.5);
							$this->setPosition($minecartPosition);    // Move minecart to center of rail
							$this->state = Minecart::STATE_ON_RAIL;
						}
				}
				if ($this->state !== Minecart::STATE_ON_RAIL) {
					$this->state = Minecart::STATE_INITIAL;
				if($p instanceof Player){
					$this->motionX = -sin($p->getYaw() / 180 * M_PI);
					$this->motionZ = cos($p->getYaw() / 180 * M_PI);
				}
				$target = $this->getLevel()->getBlock($this->add($this->motionX, 0, $this->motionZ)->round());
				$target2 = $this->getLevel()->getBlock($this->add($this->motionX, 0, $this->motionZ)->floor());
				if($target->getId() != ItemItem::AIR or $target2->getId() != ItemItem::AIR) $this->motionY = $this->gravity * 3;
				else $this->motionY -= $this->gravity;

				if($this->checkObstruction($this->x, $this->y, $this->z)){
					$hasUpdate = true;
				}

				$this->move($this->motionX, $this->motionY, $this->motionZ);
				$this->updateMovement();

				$friction = 1 - $this->drag;

				if($this->onGround and (abs($this->motionX) > 0.00001 or abs($this->motionZ) > 0.00001)){
					$friction = $this->getLevel()->getBlock($this->temporalVector->setComponents((int) floor($this->x), (int) floor($this->y - 1), (int) floor($this->z) - 1))->getFrictionFactor() * $friction;
				}

				$this->motionX *= $friction;
				$this->motionY *= 1 - $this->drag;
				$this->motionZ *= $friction;

				if($this->onGround){
					$this->motionY *= -0.5;
				}
				}
				}elseif($this->state === Minecart::STATE_ON_RAIL){
					$hasUpdate = $this->forwardOnRail($p);
					$this->updateMovement();
				}
				}
			}
        }
		
        $this->timings->stopTiming();

        return $hasUpdate or !$this->onGround or abs($this->motionX) > 0.00001 or abs($this->motionY) > 0.00001 or abs($this->motionZ) > 0.00001;
    }

    private function checkIfOnRail(){
        for ($y = -1; $y !== 2 and $this->state === Minecart::STATE_INITIAL; $y++) {
            $positionToCheck = $this->temporalVector->setComponents($this->x, $this->y + $y, $this->z);
            $block = $this->level->getBlock($positionToCheck);
            if ($this->isRail($block)) {
                $minecartPosition = $positionToCheck->floor()->add(0.5, 0, 0.5);
                $this->setPosition($minecartPosition);    // Move minecart to center of rail
                $this->state = Minecart::STATE_ON_RAIL;
            }
        }
        if ($this->state !== Minecart::STATE_ON_RAIL) {
            $this->state = Minecart::STATE_OFF_RAIL;
        }
    }

    private function isRail(Block $rail){
        return ($rail !== null and in_array($rail->getId(), [Block::RAIL, Block::ACTIVATOR_RAIL, Block::DETECTOR_RAIL, Block::POWERED_RAIL]));
    }

    private function getCurrentRail(){
        $block = $this->getLevel()->getBlock($this);
        if ($this->isRail($block)) {
            return $block;
        }
        // Rail could be one block below descending down
        $down = $this->temporalVector->setComponents($this->x, $this->y - 1, $this->z);
        $block = $this->getLevel()->getBlock($down);
        if ($this->isRail($block)) {
            return $block;
        }
        return null;
    }

    private function forwardOnRail(Player $player){
        if ($this->direction === -1) {
            $candidateDirection = $player->getDirection();
        } else {
            $candidateDirection = $this->direction;
        }
        $rail = $this->getCurrentRail();
        if ($rail !== null) {
            $railType = $rail->getDamage();
            $nextDirection = $this->getDirectionToMove($railType, $candidateDirection);
            if ($nextDirection !== -1) {
                $this->direction = $nextDirection;
                $moved = $this->checkForVertical($railType, $nextDirection);
                if (!$moved) {
                    return $this->moveIfRail();
                } else {
                    return true;
                }
            } else {
                $this->direction = -1;  // Was not able to determine direction to move, so wait for player to look in valid direction
            }
        } else {
            // Not able to find rail
            $this->state = Minecart::STATE_INITIAL;
        }

        return false;
    }

    private function getDirectionToMove($railType, $candidateDirection){
        switch ($railType) {
            case Rail::STRAIGHT_NORTH_SOUTH:
            case Rail::SLOPED_ASCENDING_NORTH:
            case Rail::SLOPED_ASCENDING_SOUTH:
                switch ($candidateDirection) {
                    case Entity::NORTH:
                    case Entity::SOUTH:
                        return $candidateDirection;
                }
                break;
            case Rail::STRAIGHT_EAST_WEST:
            case Rail::SLOPED_ASCENDING_EAST:
            case Rail::SLOPED_ASCENDING_WEST:
                switch ($candidateDirection) {
                    case Entity::WEST:
                    case Entity::EAST:
                        return $candidateDirection;
                }
                break;
            case Rail::CURVED_SOUTH_EAST:
                switch ($candidateDirection) {
                    case Entity::SOUTH:
                    case Entity::EAST:
                        return $candidateDirection;
                    case Entity::NORTH:
                        return $this->checkForTurn($candidateDirection, Entity::EAST);
                    case Entity::WEST:
                        return $this->checkForTurn($candidateDirection, Entity::SOUTH);
                }
                break;
            case Rail::CURVED_SOUTH_WEST:
                switch ($candidateDirection) {
                    case Entity::SOUTH:
                    case Entity::WEST:
                        return $candidateDirection;
                    case Entity::NORTH:
                        return $this->checkForTurn($candidateDirection, Entity::WEST);
                    case Entity::EAST:
                        return $this->checkForTurn($candidateDirection, Entity::SOUTH);
                }
                break;
            case Rail::CURVED_NORTH_WEST:
                switch ($candidateDirection) {
                    case Entity::NORTH:
                    case Entity::WEST:
                        return $candidateDirection;
                    case Entity::SOUTH:
                        return $this->checkForTurn($candidateDirection, Entity::WEST);
                    case Entity::EAST:
                        return $this->checkForTurn($candidateDirection, Entity::NORTH);

                }
                break;
            case Rail::CURVED_NORTH_EAST:
                switch ($candidateDirection) {
                    case Entity::NORTH:
                    case Entity::EAST:
                        return $candidateDirection;
                    case Entity::SOUTH:
                        return $this->checkForTurn($candidateDirection, Entity::EAST);
                    case Entity::WEST:
                        return $this->checkForTurn($candidateDirection, Entity::NORTH);
                }
                break;
        }
        return -1;
    }

    private function checkForTurn($currentDirection, $newDirection){
        switch ($currentDirection) {
            case Entity::NORTH:
                $diff = $this->x - $this->getFloorX();
                if ($diff !== 0 and $diff <= .5) {
                    $dx = ($this->getFloorX() + .5) - $this->x;
                    $this->move($dx, 0, 0);
                    return $newDirection;
                }
                break;
            case Entity::SOUTH:
                $diff = $this->x - $this->getFloorX();
                if ($diff !== 0 and $diff >= .5) {
                    $dx = ($this->getFloorX() + .5) - $this->x;
                    $this->move($dx, 0, 0);
                    return $newDirection;
                }
                break;
            case Entity::EAST:
                $diff = $this->z - $this->getFloorZ();
                if ($diff !== 0 and $diff <= .5) {
                    $dz = ($this->getFloorZ() + .5) - $this->z;
                    $this->move(0, 0, $dz);
                    return $newDirection;
                }
                break;
            case Entity::WEST:
                $diff = $this->z - $this->getFloorZ();
                if ($diff !== 0 and $diff >= .5) {
                    $dz = $dz = ($this->getFloorZ() + .5) - $this->z;
                    $this->move(0, 0, $dz);
                    return $newDirection;
                }
                break;
        }
		

        return $currentDirection;
    }

    private function checkForVertical($railType, $currentDirection){
        switch ($railType) {
            case Rail::SLOPED_ASCENDING_NORTH:
                switch ($currentDirection) {
                    case Entity::NORTH:
                        $diff = $this->x - $this->getFloorX();
                        if ($diff !== 0 and $diff <= .5) {
                            $dx = ($this->getFloorX() - .1) - $this->x;
                            $this->move($dx, -1, 0);
                            return true;
                        }
                        break;
                    case Entity::SOUTH:
                        $diff = $this->x - $this->getFloorX();
                        if ($diff !== 0 and $diff >= .5) {
                            $dx = ($this->getFloorX() + 1) - $this->x;
                            $this->move($dx, 1, 0);
                            return true;
                        }
                        break;
                }
                break;
            case Rail::SLOPED_ASCENDING_SOUTH:
                switch ($currentDirection) {
                    case Entity::SOUTH:
                        $diff = $this->x - $this->getFloorX();
                        if ($diff !== 0 and $diff >= .5) {
                            $dx = ($this->getFloorX() + 1) - $this->x;
                            $this->move($dx, -1, 0);
                            return true;
                        }
                        break;
                    case Entity::NORTH:
                        $diff = $this->x - $this->getFloorX();
                        if ($diff !== 0 and $diff <= .5) {
                            $dx = ($this->getFloorX() - .1) - $this->x;
                            $this->move($dx, 1, 0);
                            return true;
                        }
                        break;
                }
                break;
            case Rail::SLOPED_ASCENDING_EAST:
                switch ($currentDirection) {
                    case Entity::EAST:
                        $diff = $this->z - $this->getFloorZ();
                        if ($diff !== 0 and $diff <= .5) {
                            $dz = ($this->getFloorZ() - .1) - $this->z;
                            $this->move(0, 1, $dz);
                            return true;
                        }
                        break;
                    case Entity::WEST:
                        $diff = $this->z - $this->getFloorZ();
                        if ($diff !== 0 and $diff >= .5) {
                            $dz = ($this->getFloorZ() + 1) - $this->z;
                            $this->move(0, -1, $dz);
                            return true;
                        }
                        break;
                }
                break;
            case Rail::SLOPED_ASCENDING_WEST:
                switch ($currentDirection) {
                    case Entity::WEST:
						
                        $diff = $this->z - $this->getFloorZ();
                        if ($diff !== 0 and $diff >= .5) {
                            $dz = ($this->getFloorZ() + 1) - $this->z;
                            $this->move(0, 1, $dz);
                            return true;
                        }
                        break;
                    case Entity::EAST:
                        $diff = $this->z - $this->getFloorZ();
                        if ($diff !== 0 and $diff <= .5) {
                            $dz = ($this->getFloorZ() - .1) - $this->z;
                            $this->move(0, -1, $dz);
                            return true;
                        }
                        break;
                }
                break;
        }

        return false;
    }

    private function moveIfRail(){
        $nextMoveVector = $this->moveVector[$this->direction];
        $nextMoveVector = $nextMoveVector->multiply($this->moveSpeed);
        $newVector = $this->add($nextMoveVector->x, $nextMoveVector->y, $nextMoveVector->z);
        $possibleRail = $this->getCurrentRail();
        if (in_array($possibleRail->getId(), [Block::RAIL, Block::ACTIVATOR_RAIL, Block::DETECTOR_RAIL, Block::POWERED_RAIL])) {
            $this->moveUsingVector($newVector);
            return true;
        }

        return false;
    }

    private function moveUsingVector(Vector3 $desiredPosition){
        $dx = $desiredPosition->x - $this->x;
        $dy = $desiredPosition->y - $this->y;
        $dz = $desiredPosition->z - $this->z;
        $this->move($dx, $dy, $dz);
    }

    public function getNearestRail(){
        $minX = Math::floorFloat($this->boundingBox->minX);
        $minY = Math::floorFloat($this->boundingBox->minY);
        $minZ = Math::floorFloat($this->boundingBox->minZ);
        $maxX = Math::ceilFloat($this->boundingBox->maxX);
        $maxY = Math::ceilFloat($this->boundingBox->maxY);
        $maxZ = Math::ceilFloat($this->boundingBox->maxZ);

        $rails = [];

        for ($z = $minZ; $z <= $maxZ; ++$z) {
            for ($x = $minX; $x <= $maxX; ++$x) {
                for ($y = $minY; $y <= $maxY; ++$y) {
                    $block = $this->level->getBlock($this->temporalVector->setComponents($x, $y, $z));
                    if (in_array($block->getId(), [Block::RAIL, Block::ACTIVATOR_RAIL, Block::DETECTOR_RAIL, Block::POWERED_RAIL])) $rails[] = $block;
                }
            }
        }

        $minDistance = PHP_INT_MAX;
        $nearestRail = null;
        foreach ($rails as $rail) {
            $dis = $this->distance($rail);
            if ($dis < $minDistance) {
                $nearestRail = $rail;
                $minDistance = $dis;
            }
        }

        return $nearestRail;
    }

    public function spawnTo(Player $player){
        $pk = new AddEntityPacket();
        $pk->eid = $this->getId();
        $pk->type = Minecart::NETWORK_ID;
        $pk->x = $this->x;
        $pk->y = $this->y + $this->getEyeHeight() - 1.25;
        $pk->z = $this->z;
        $pk->speedX = 0;
        $pk->speedY = 0;
        $pk->speedZ = 0;
        $pk->yaw = 0;
        $pk->pitch = 0;
        $pk->metadata = $this->dataProperties;
        $player->dataPacket($pk);

        parent::spawnTo($player);
    }

    public function attack($damage, EntityDamageEvent $source){
        parent::attack($damage, $source);

        if(!$source->isCancelled()){
            $pk = new EntityEventPacket();
            $pk->eid = $this->id;
            $pk->event = EntityEventPacket::HURT_ANIMATION;
            foreach($this->getLevel()->getPlayers() as $player){
                $player->dataPacket($pk);
            }
        }
    }
	
	public function getDrops(){
		return [ItemItem::get(ItemItem::MINECART, 0, 1)];
	}

    public function getSaveId(){
        $class = new \ReflectionClass(static::class);
        return $class->getShortName();
    }
	
	public function close(){
		if(!$this->closed){
			if(!$this->getLevel()->getServer()->isWorldNonLivingEntityDropsDisabled($this->getLevel())){
				foreach($this->getDrops() as $item){
					$this->getLevel()->dropItem($this, $item);
				}
			}
		}
		parent::close();
	}
}
