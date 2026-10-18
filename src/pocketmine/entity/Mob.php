<?php

namespace pocketmine\entity;

use pocketmine\entity\ai\behavior\Behavior;
use pocketmine\entity\ai\navigation\PathNavigate;
use pocketmine\level\Level;
use pocketmine\math\Vector3;
use pocketmine\utils\Random;

abstract class Mob extends Creature{

	/** @var Behavior[] 按优先级排序 */
	protected $behaviors = [];

	/** @var Behavior[] 当前正在运行的行为 */
	protected $runningBehaviors = [];

	/** @var Behavior|null 当前优先级最高的行为（兼容旧接口） */
	protected $currentBehavior = null;

	/** @var Random */
	public $random;

	/** @var bool */
	protected $behaviorsEnabled = false;

	/** @var bool 当前是否有行为在控制视线（避免寻路覆盖 lookAt） */
	private $lookControlled = false;

	/** @var PathNavigate|null */
	private $navigator = null;

	public function getNavigator() : PathNavigate{
		if($this->navigator === null){
			$this->navigator = new PathNavigate($this);
		}
		return $this->navigator;
	}

	public function initEntity(){
		parent::initEntity();
		$this->random = new Random();
		$this->behaviorsEnabled = $this->level->getServer()->aiEnabled;
	}

	public function onUpdate($currentTick){
		if($this->closed){
			return false;
		}

		// 死亡后仍然要让 Creature/Entity 处理 deadTicks、despawn 和 close，
		// 否则尸体会永远留在 updateEntities 里。
		if(!$this->isAlive()){
			$this->stopAllBehaviors();
			if($this->navigator !== null){
				$this->navigator->clearPath();
			}
			return parent::onUpdate($currentTick);
		}

		if($this->behaviorsEnabled){
			$this->tickBehaviors();
		}

		// 导航更新（在 Behavior 更新之后、父类更新之前应用 motion）
		if($this->navigator !== null){
			$this->navigator->update();
		}

		return parent::onUpdate($currentTick);
	}

	/**
	 * 按“优先级 + 控制位”选择行为。
	 * 控制位不重叠的行为可以同时运行，例如边走边看。
	 */
	private function tickBehaviors() : void{
		$running = $this->runningBehaviors;

		// 1. 停止已经失效的行为
		foreach($running as $index => $behavior){
			if(!$behavior->canContinue()){
				$behavior->onEnd();
				unset($running[$index]);
			}
		}
		$running = array_values($running);

		// 2. 按优先级尝试启动新行为
		foreach($this->behaviors as $behavior){
			if(in_array($behavior, $running, true)){
				continue;
			}
			if(!$this->canStartBehavior($behavior, $running)){
				continue;
			}
			if(!$behavior->shouldStart()){
				continue;
			}

			// 高优先级行为可以打断低优先级、且允许打断的行为
			foreach($running as $index => $other){
				if(($other->getControlFlags() & $behavior->getControlFlags()) !== 0 and $other->getPriority() > $behavior->getPriority()){
					$other->onEnd();
					unset($running[$index]);
				}
			}
			$running = array_values($running);

			$behavior->onStart();
			$running[] = $behavior;
		}

		// 3. 更新所有正在运行的行为
		foreach($running as $behavior){
			$behavior->onTick();
		}

		$this->runningBehaviors = $running;
		$this->currentBehavior = null;
		$this->lookControlled = false;
		foreach($running as $behavior){
			if($this->currentBehavior === null or $behavior->getPriority() < $this->currentBehavior->getPriority()){
				$this->currentBehavior = $behavior;
			}
			if(($behavior->getControlFlags() & Behavior::CONTROL_LOOK) !== 0){
				$this->lookControlled = true;
			}
		}
	}

	/**
	 * 判断候选行为能否与当前行为共存。
	 * 同优先级或更高优先级的行为必须控制位不冲突；
	 * 低优先级行为只有在 canInterrupt() 为 true 时才会被抢占。
	 */
	private function canStartBehavior(Behavior $candidate, array $running) : bool{
		foreach($running as $other){
			if(($other->getControlFlags() & $candidate->getControlFlags()) === 0){
				continue;
			}
			if($other->getPriority() <= $candidate->getPriority()){
				return false;
			}
			if(!$other->canInterrupt()){
				return false;
			}
		}
		return true;
	}

	private function stopAllBehaviors() : void{
		foreach($this->runningBehaviors as $behavior){
			$behavior->onEnd();
		}
		$this->runningBehaviors = [];
		$this->currentBehavior = null;
		$this->lookControlled = false;
	}

	public function isLookControlled() : bool{
		return $this->lookControlled;
	}

	public function getCurrentBehavior(){
		return $this->currentBehavior;
	}

	/** @return Behavior[] */
	public function getRunningBehaviors() : array{
		return $this->runningBehaviors;
	}

	public function addBehavior(Behavior $behavior){
		$this->behaviors[] = $behavior;
		$this->sortBehaviors();
	}

	public function setBehavior(int $index, Behavior $behavior){
		$this->behaviors[$index] = $behavior;
		$this->sortBehaviors();
	}

	public function removeBehavior(int $index){
		unset($this->behaviors[$index]);
		$this->behaviors = array_values($this->behaviors);
	}

	private function sortBehaviors() : void{
		usort($this->behaviors, function(Behavior $a, Behavior $b) : int{
			return $a->getPriority() <=> $b->getPriority();
		});
	}

	public function isBehaviorsEnabled() : bool{
		return $this->behaviorsEnabled;
	}

	public function setBehaviorsEnabled(bool $enabled = true){
		$this->behaviorsEnabled = $enabled;
		if(!$enabled){
			$this->stopAllBehaviors();
			if($this->navigator !== null){
				$this->navigator->clearPath();
			}
		}
	}

	// ===== 地形感知移动（供 Behavior 与 PathNavigate 使用）=====

	/**
	 * 智能水平移动（自动应对地形、台阶、落差检测）
	 * @param Vector3 $direction 水平方向单位向量（仅 x/z 有效）
	 * @param float   $speed     移动速度（motion 大小）
	 * @return bool true=移动成功，false=被阻挡
	 */
	public function moveInDirection(Vector3 $direction, float $speed) : bool{
		$level = $this->getLevel();
		if($level === null){
			return false;
		}

		if($this->isInsideOfWater()){
			// 只补一点上浮速度，避免和 swimming() 一起把生物弹飞
			if($this->motionY < 0.05){
				$this->motionY = 0.12;
			}
		}

		$dx = (int) floor($this->x + $direction->x * ($speed + 0.5));
		$dy = (int) floor($this->y);
		$dz = (int) floor($this->z + $direction->z * ($speed + 0.5));

		$groundY = self::pickGroundY($level, $dx, $dy, $dz);
		if($groundY === null){
			return false;
		}

		$diff = $groundY - $dy;
		if($diff > 1){
			return false;
		}

		// 检查目标位置的身体和头部空间（以目标地面为基准）
		$bodyLayers = max(1, (int) ceil($this->height));
		for($i = 0; $i < $bodyLayers; ++$i){
			$block = $level->getBlock(new Vector3($dx, $groundY + $i, $dz));
			if($block->isSolid()){
				return false;
			}
			$id = $block->getId();
			if($id === 10 or $id === 11 or $id === 51 or $id === 81){
				return false;
			}
		}

		$this->motionX = $direction->x * $speed;
		$this->motionZ = $direction->z * $speed;

		if($diff > 0 and $this->onGround and !$this->isInsideOfWater()){
			$this->motionY = 0.42;
		}elseif($diff < -1 and $this->onGround){
			$this->motionY = -0.15;
		}

		return true;
	}

	/**
	 * 静态地形检测：获取目标位置的地面 Y
	 * @return int|null 可站立的地面 Y 点，null=无法通行
	 */
	public static function pickGroundY(Level $level, int $x, int $dy, int $z) : ?int{
		$block = $level->getBlock(new Vector3($x, $dy, $z));

		if($block->isSolid()){
			$below = $level->getBlock(new Vector3($x, $dy - 1, $z))->isSolid();
			if(!$below){
				return null;
			}
			$above1 = $level->getBlock(new Vector3($x, $dy + 1, $z));
			$above2 = $level->getBlock(new Vector3($x, $dy + 2, $z));
			if(!$above1->isSolid() and !$above2->isSolid()){
				return $dy + 1;
			}
			return null;
		}

		for($y = 0; $y >= -2; $y--){
			if($level->getBlock(new Vector3($x, $dy + $y - 1, $z))->isSolid()){
				return $dy + $y;
			}
		}
		return null;
	}
}
