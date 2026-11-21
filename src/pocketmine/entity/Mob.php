<?php

namespace pocketmine\entity;

use pocketmine\entity\ai\behavior\Behavior;
use pocketmine\entity\ai\behavior\FloatGoal;
use pocketmine\entity\ai\control\MoveControl;
use pocketmine\entity\ai\navigation\PathNavigate;
use pocketmine\event\entity\EntityDamageEvent;
use pocketmine\level\Level;
use pocketmine\math\Vector3;
use pocketmine\Player;
use pocketmine\utils\Random;

abstract class Mob extends Creature{

	/**
	 * 生物与玩家中心之间必须保持的额外水平空隙（格）。
	 *
	 * 玩家的第一人称相机位于身体中心，客户端模型通常比服务端的
	 * width/height 更大（末影人客户端是 0.6 x 2.9，服务端是 0.3 x 1.8）。
	 * 距离过近时相机会落在模型内部，被近裁剪面切开的模型会渲染成
	 * 一直延伸到准心的黑色三角。0.4 是让相机退出模型并留出摆臂余量。
	 */
	const PLAYER_CLEARANCE = 0.4;

	/**
	 * 可跨越的台阶高度（与 InCore / 原版一致为 0.6）。
	 *
	 * 高度不足 1 格，所以 1 格整台阶仍然需要 0.42 的跳跃；
	 * 半砖、台阶方块这类低于 0.6 的落差会由 Entity::move() 直接平滑抬升。
	 */
	protected $stepHeight = 0.6;

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

	/** @var MoveControl|null 懒加载：行为/寻路只声明目标，motion 由它统一施加 */
	private $moveControl = null;

	/** @var int 上一次由 moveInDirection() 写入 motion 的 tick */
	private $lastMoveTick = -1;

	public function getNavigator() : PathNavigate{
		if($this->navigator === null){
			$this->navigator = new PathNavigate($this);
		}
		return $this->navigator;
	}

	public function getMoveControl() : MoveControl{
		if($this->moveControl === null){
			$this->moveControl = new MoveControl($this);
		}
		return $this->moveControl;
	}

	// ===== InCore 风格的地面移动（Ground AI）=====

	/**
	 * 是否使用 InCore 的地面移动：直接朝目标施加速度 + 前方方块探测，
	 * 不做 A* 寻路。原版生物就是这样移动的，观感比格子寻路自然得多。
	 *
	 * 飞行/水生生物覆写为 false，继续使用各自原有的物理。
	 */
	public function usesGroundAi() : bool{
		return true;
	}

	/** @var Vector3|null 行为指定的目标（追击/逃离/吸引），优先级高于随机漫游 */
	private $groundFollowTarget = null;

	/** @var float 行为给出的速度（沿用旧的 motion 单位，内部换算成 InCore 的 multiplier） */
	private $groundFollowSpeed = 0.35;

	/** @var Vector3|null 随机漫游目标 */
	private $groundMoveTarget = null;

	/** @var int 原地停留剩余 tick（对应 InCore 的 stayTime） */
	private $groundStayTime = 0;

	/** @var int 当前漫游目标剩余 tick（对应 InCore 的 moveTime） */
	private $groundMoveTime = 0;

	/** @var int 受击/被推时禁止转向的剩余 tick */
	private $groundNoRotateTicks = 0;

	/** @var int 击退期间不要覆盖水平 motion 的剩余 tick */
	private $groundKnockbackTicks = 0;

	/**
	 * 行为层声明“我要去这个位置”。
	 * 与 InCore 的 setPm1eFollowTarget() 对应，每 tick 调用也不会重置计时。
	 */
	public function setGroundFollowTarget(Vector3 $target, float $speed = 0.35) : void{
		$this->groundFollowTarget = new Vector3($target->x, $target->y, $target->z);
		$this->groundFollowSpeed = $speed;
	}

	public function clearGroundFollowTarget() : void{
		$this->groundFollowTarget = null;
	}

	public function getGroundFollowTarget() : ?Vector3{
		return $this->groundFollowTarget;
	}

	/**
	 * 把行为层的速度（约 0.2–0.5 格/tick）换算成 InCore 的 multiplier。
	 * 陆上基础速度是 0.15，除以 0.25 可以让原来的漫游速度保持不变。
	 */
	public static function groundSpeedMultiplier(float $speed) : float{
		return max(0.4, min(2.0, $speed / 0.25));
	}

	public function initEntity(){
		parent::initEntity();
		$this->random = new Random();
		$this->behaviorsEnabled = $this->level->getServer()->aiEnabled;

		// 原版所有 Mob 都带 float goal（优先级 0，不占控制位），这里统一注册，
		// 避免每个动物各写一遍；水中且不在地面时才生效。
		$this->addBehavior(new FloatGoal($this));
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
			// 没有 look 行为接管时，头以 10°/tick 慢慢回到身体朝向
			// （对应原版 EntityLookHelper：rotationYawHead 向 renderYawOffset 靠拢）。
			if($this->headYaw === null){
				$this->headYaw = $this->yaw;
			}
			$this->headYaw = self::turnTowardAngle($this->headYaw, $this->yaw, 10.0);

			$this->tickBehaviors();
		}

		if($this->usesGroundAi()){
			$tickDiff = $currentTick - $this->lastUpdate;
			if($tickDiff < 1){
				$tickDiff = 1;
			}

			// InCore 风格的地面移动：不做 A*，直接朝目标施加速度，
			// 并用前方方块探测决定起跳/停下。行为层仍然通过
			// navigator->moveTo() 声明目标（PathNavigate 会转发到这里）。
			$this->tickGroundAi($tickDiff);

			$return = parent::onUpdate($currentTick);
			if(!$this->closed and $this->isAlive()){
				$this->pushAwayFromPlayers();
			}
			return $return;
		}

		// 导航更新（在 Behavior 更新之后、父类更新之前应用 motion）
		if($this->navigator !== null){
			$this->navigator->update();
		}

		// 统一移动层：本 tick 只在这里（或首次 moveInDirection）写一次 motion。
		if($this->moveControl !== null){
			$this->moveControl->tick();
		}

		$return = parent::onUpdate($currentTick);
		// 生物和玩家之间没有实体碰撞（Entity::move 只解算方块），
		// 生物可以走进玩家身体里，第一人称相机就会落在模型内部，
		// 客户端会把被近裁剪面切开的黑色模型渲染成「糊在准心上的黑色三角」。
		// 这里在移动之后把生物从玩家身上水平推开，保证相机不会进模型。
		if(!$this->closed and $this->isAlive()){
			$this->pushAwayFromPlayers();
		}

		return $return;
	}

	/**
	 * 受击时进入击退状态：10 tick 内不覆盖水平 motion、不让 AI 重新转向，
	 * 让 Living::knockBack() 写入的速度真正生效（对应 InCore 的 knockbackTicks）。
	 */
	public function attack($damage, EntityDamageEvent $source){
		parent::attack($damage, $source);
		if(!$source->isCancelled() and $this->usesGroundAi()){
			$this->groundKnockbackTicks = 10;
			$this->groundNoRotateTicks = 10;
		}
	}

	/**
	 * 把生物从与其重叠的玩家身上水平推开。
	 *
	 * 只移动生物、不移动玩家：直接改玩家坐标会造成客户端回拉/橡皮筋。
	 */
	protected function pushAwayFromPlayers() : void{
		if($this->level === null or $this->justCreated){
			return;
		}

		foreach($this->level->getPlayers() as $player){
			if(!($player instanceof Player) or $player->closed or !$player->isAlive() or $player->isSpectator()){
				continue;
			}

			// 先做廉价的距离筛选。
			if(abs($this->x - $player->x) > 3.0 or abs($this->z - $player->z) > 3.0){
				continue;
			}

			// 不能只判断服务端包围盒相交：玩家的相机在 eyeHeight（1.62），
			// 而客户端模型（例如末影人是 0.6 x 2.9）比服务端 width/height 更大，
			// 两个包围盒还没相交时相机就已经在模型内部了。
			$minDistance = ($this->width + $player->width) / 2 + self::PLAYER_CLEARANCE;
			$dx = $this->x - $player->x;
			$dz = $this->z - $player->z;
			if(abs($dx) > $minDistance or abs($dz) > $minDistance){
				continue;
			}
			if($this->y >= $player->y + $player->height or $this->y + $this->height <= $player->y){
				continue;
			}

			$push = self::separationPush($dx, $dz, $minDistance);
			if($push === null){
				continue;
			}

			list($normalX, $normalZ, $distance) = $push;

			// 用实体自身的移动解算推开，避免把生物直接塞进墙里。
			$beforeX = $this->x;
			$beforeZ = $this->z;
			$this->move($normalX * $distance, 0, $normalZ * $distance);
			if(abs($this->x - $beforeX) < 1.0E-4 and abs($this->z - $beforeZ) < 1.0E-4){
				// 完全被墙挡住，推不动；保持原样，交给行为/寻路处理。
				continue;
			}

			// 去掉朝玩家内部的速度分量，否则下一 tick 又会贴回去。
			$inward = $this->motionX * $normalX + $this->motionZ * $normalZ;
			if($inward < 0){
				$this->motionX -= $inward * $normalX;
				$this->motionZ -= $inward * $normalZ;
			}
		}
	}

	/**
	 * 计算两个重叠实体的水平分离量（纯函数，方便离线测试）。
	 *
	 * @param float $dx          生物中心相对玩家中心的 X 偏移
	 * @param float $dz          生物中心相对玩家中心的 Z 偏移
	 * @param float $minDistance 两个中心允许的最小水平距离
	 *
	 * @return float[]|null [单位法线X, 单位法线Z, 需要推开的距离]；已经分开则返回 null
	 */
	public static function separationPush(float $dx, float $dz, float $minDistance) : ?array{
		if($minDistance <= 0){
			return null;
		}

		$distanceSquared = $dx * $dx + $dz * $dz;
		if($distanceSquared < 1.0E-8){
			// 两者中心完全重合：固定沿 +X 推开，避免除零产生 NaN/NAN 坐标。
			return [1.0, 0.0, $minDistance];
		}

		$distance = sqrt($distanceSquared);
		if($distance >= $minDistance){
			return null;
		}

		return [$dx / $distance, $dz / $distance, $minDistance - $distance];
	}

	/**
	 * InCore 风格的地面移动主体。
	 *
	 * 与 InCore 的 Mob::tickPm1eGroundAi() 一一对应：
	 *   1. 处理停留/漫游计时与随机漫游目标；
	 *   2. 朝目标设置 yaw 与水平 motion（曼哈顿归一化，速度按液体类型区分）；
	 *   3. 贴地时清零 motionY、空中施加重力、液体里给浮力；
	 *   4. 前方探测：台阶起跳、高墙/栅栏停下、悬崖停下；
	 *   5. move() + 摩擦。
	 *
	 * 注意：这里不做寻路，因此不会出现“路径点抖动/反复重算”。
	 */
	private function tickGroundAi(int $tickDiff) : void{
		$level = $this->getLevel();
		if($level === null){
			return;
		}

		if($this->groundStayTime > 0){
			$this->groundStayTime -= $tickDiff;
		}
		if($this->groundMoveTime > 0){
			$this->groundMoveTime -= $tickDiff;
		}
		if($this->groundNoRotateTicks > 0){
			$this->groundNoRotateTicks -= $tickDiff;
		}
		if($this->groundKnockbackTicks > 0){
			$this->groundKnockbackTicks -= $tickDiff;
		}

		$liquid = $this->getGroundLiquidType($level);

		$this->refreshGroundWanderTarget();
		$followTarget = $this->groundFollowTarget;
		$target = $followTarget !== null ? $followTarget : $this->groundMoveTarget;

		// 行为给的速度只用于 followTarget；漫游使用 1.0 的基准倍率。
		$multiplier = $followTarget !== null ? self::groundSpeedMultiplier($this->groundFollowSpeed) : 1.0;
		$directionSign = 1.0;
		if($multiplier < 0){
			$directionSign = -1.0;
			$multiplier = -$multiplier;
		}
		$multiplier = max(0.001, $multiplier);

		if($target !== null){
			$dx = $target->x - $this->x;
			$dz = $target->z - $this->z;
			$diff = abs($dx) + abs($dz);

			if($this->groundNoRotateTicks <= 0 and $diff > 0.001){
				// 与 InCore 相同的朝向公式：-atan2(dx/diff, dz/diff)。
				$this->yaw = -atan2($dx / $diff, $dz / $diff) * (180 / M_PI);
			}

			$stopDistance = $this->width / 2 + 1.0;
			$distSq = $dx * $dx + $dz * $dz;
			$knockingBack = $this->groundKnockbackTicks > 0 and (abs($this->motionX) > 0.001 or abs($this->motionZ) > 0.001);

			if(!$knockingBack){
				if($diff <= 0.001 or ($followTarget === null and $this->groundStayTime > 0) or ($directionSign > 0 and $distSq <= $stopDistance * $stopDistance)){
					$this->motionX = 0;
					$this->motionZ = 0;
				}else{
					$baseSpeed = self::getGroundBaseSpeed($liquid) * $multiplier;
					$this->motionX = $baseSpeed * $directionSign * ($dx / $diff);
					$this->motionZ = $baseSpeed * $directionSign * ($dz / $diff);
				}
			}
		}else{
			$this->motionX = 0;
			$this->motionZ = 0;
		}

		if($liquid !== null){
			$this->motionY = max($this->motionY, $this->gravity * 0.3);
		}else{
			$jumped = $this->checkGroundJump($level);
			if(!$jumped){
				if($this->onGround){
					$this->motionY = 0;
				}else{
					$this->motionY -= $this->gravity;
				}
			}
		}

		$this->move($this->motionX, $this->motionY, $this->motionZ);
		$this->recoverPartialBlockStandingHeight();

		// 与 InCore 相同的摩擦处理：贴地时乘以脚下方块的摩擦系数。
		$friction = 1 - $this->drag;
		if($this->onGround and (abs($this->motionX) > 0.00001 or abs($this->motionZ) > 0.00001)){
			$friction = $level->getBlock(new Vector3(
				(int) floor($this->x),
				(int) floor($this->boundingBox->minY - 0.01),
				(int) floor($this->z)
			))->getFrictionFactor() * $friction;
		}

		$this->motionX *= $friction;
		$this->motionZ *= $friction;
		$this->motionY *= $liquid !== null ? 0.8 : (1 - $this->drag);
		if($this->onGround and $liquid === null){
			$this->motionY *= -0.5;
		}
	}

	/**
	 * 维护随机漫游目标（InCore 的 moveTarget/stayTime/moveTime）。
	 * 有行为指定的 followTarget 时不动漫游状态。
	 */
	private function refreshGroundWanderTarget() : void{
		if($this->groundFollowTarget !== null){
			return;
		}

		if($this->groundStayTime > 0){
			$this->groundMoveTarget = null;
			return;
		}

		if($this->groundMoveTarget !== null){
			if($this->groundMoveTime > 0){
				// 目标还没到期：继续走（到达后由 tickGroundAi 清零 motion）。
				return;
			}
			$this->groundMoveTarget = null;
		}

		// 1% 概率原地停留 4–10 秒，避免所有生物一直在走。
		if(mt_rand(1, 100) === 1){
			$this->groundStayTime = mt_rand(80, 200);
			return;
		}

		$this->groundMoveTarget = new Vector3(
			$this->x + mt_rand(-30, 30),
			$this->y,
			$this->z + mt_rand(-30, 30)
		);
		$this->groundMoveTime = mt_rand(80, 200);
	}

	/**
	 * 前方方块探测，决定是否起跳、是否停下。
	 *
	 * @return bool true = 本 tick 已经给了跳跃速度
	 */
	private function checkGroundJump(Level $level) : bool{
		if(!$this->onGround or $this->groundStayTime > 0){
			return false;
		}

		$horizontalLen = sqrt($this->motionX * $this->motionX + $this->motionZ * $this->motionZ);
		if($horizontalLen < 0.001){
			return false;
		}

		$nx = $this->motionX / $horizontalLen;
		$nz = $this->motionZ / $horizontalLen;
		$probe = $this->width / 2 + 0.5;

		$fx = (int) floor($this->x + $nx * $probe);
		$fz = (int) floor($this->z + $nz * $probe);
		$feetY = (int) floor($this->y + 0.001);

		$feetBlock = $level->getBlock(new Vector3($fx, $feetY, $fz));
		$above1 = $level->getBlock(new Vector3($fx, $feetY + 1, $fz));
		$above2 = $level->getBlock(new Vector3($fx, $feetY + 2, $fz));

		if(!$feetBlock->isSolid()){
			// 前方没有方块：下方两格都空说明是 2 格以上的悬崖，停下换目标。
			if(!$level->getBlock(new Vector3($fx, $feetY - 1, $fz))->isSolid()
				and !$level->getBlock(new Vector3($fx, $feetY - 2, $fz))->isSolid()){
				$this->stopGroundMovement(10);
			}
			return false;
		}

		$bb = $feetBlock->getBoundingBox();
		if($bb === null){
			return false;
		}

		$riserHeight = $bb->maxY - $this->y;
		if($riserHeight <= $this->stepHeight + 0.001){
			// 低于 stepHeight 的台阶由 Entity::move() 直接抬升。
			return false;
		}

		if($riserHeight > 1.05 or $above1->isSolid()){
			// 高墙、栅栏等跳不上去的障碍：停一下，让行为重新选目标。
			$this->stopGroundMovement(10);
			return false;
		}

		if(!$above1->isSolid() and !$above2->isSolid()){
			$this->motionY = max($this->motionY, 0.42);
			return true;
		}

		return false;
	}

	/**
	 * 停止地面移动一段时间（对应 InCore 的 stayTime）。
	 */
	private function stopGroundMovement(int $ticks) : void{
		$this->motionX = 0;
		$this->motionZ = 0;
		if($this->groundStayTime < $ticks){
			$this->groundStayTime = $ticks;
		}
	}

	/**
	 * 脚下方块决定生物是在水里、岩浆里还是陆地上。
	 *
	 * @return string|null "water" / "lava" / null
	 */
	private function getGroundLiquidType(Level $level) : ?string{
		$id = $level->getBlockIdAt(
			(int) floor($this->x),
			(int) floor($this->boundingBox->minY + 0.1),
			(int) floor($this->z)
		);

		if($id === 8 or $id === 9){
			return "water";
		}
		if($id === 10 or $id === 11){
			return "lava";
		}
		return null;
	}

	/**
	 * 陆地/水/岩浆的基础速度，与 InCore 的 0.15 / 0.05 / 0.02 一致。
	 */
	public static function getGroundBaseSpeed(?string $liquid) : float{
		if($liquid === "lava"){
			return 0.02;
		}
		if($liquid === "water"){
			return 0.05;
		}
		return 0.15;
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

	/**
	 * 是否有正在运行、且声明了 CONTROL_MOVE 的行为。
	 * FloatGoal / RestrictSunGoal 这类不占控制位的 Goal 不算，
	 * 否则 FlyingAnimal 会把它们误判成“移动已被行为接管”。
	 */
	public function isMovementControlled() : bool{
		foreach($this->runningBehaviors as $behavior){
			if(($behavior->getControlFlags() & Behavior::CONTROL_MOVE) !== 0){
				return true;
			}
		}
		return false;
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
	 * 本 tick 是否还可以写入一次 motion。
	 * 同一 tick 内只允许第一次调用生效，后续调用视为“已被其他来源占用”，
	 * 防止行为层与寻路层在同一 tick 互相覆盖。
	 */
	public function canApplyMove() : bool{
		if($this->lastMoveTick === $this->ticksLived){
			return false;
		}
		$this->lastMoveTick = $this->ticksLived;
		return true;
	}

	/**
	 * 智能水平移动（自动应对地形、台阶、落差检测）
	 * @param Vector3 $direction 水平方向单位向量（仅 x/z 有效）
	 * @param float   $speed     移动速度（motion 大小）
	 * @return bool true=移动成功（或本 tick 已有其他来源写过 motion），false=被阻挡
	 */
	public function moveInDirection(Vector3 $direction, float $speed) : bool{
		$level = $this->getLevel();
		if($level === null){
			return false;
		}

		if(!$this->canApplyMove()){
			// 本 tick 的 motion 已经由更早的调用写入，保留第一份结果。
			return true;
		}

		if($this->isInsideOfWater()){
			// 只补一点上浮速度，避免和 swimming() 一起把生物弹飞
			if($this->motionY < 0.05){
				$this->motionY = 0.12;
			}
		}

		// 探测前方约半格，用于发现墙、断崖和危险方块。
		// 探测到墙时也要继续施加水平 motion，让 Entity::move() 的碰撞逻辑
		// 把移动削成贴墙/沿墙滑行；否则生物会停在离墙半格处，然后每 10 tick
		// 重算一次路径，看起来像原地回弹、走不到位。
		$probe = $speed + 0.5;
		$dx = (int) floor($this->x + $direction->x * $probe);
		$dy = (int) floor($this->y);
		$dz = (int) floor($this->z + $direction->z * $probe);

		$groundY = self::pickGroundY($level, $dx, $dy, $dz);
		$blocked = false;
		if($groundY === null){
			// null 有两种情况：前方是实心墙，或前方悬空（落差超过 3 格）。
			// 墙 -> 继续推，交给碰撞滑行并记 blocked；悬空 -> 直接拒绝前进。
			if(!$level->getBlock(new Vector3($dx, $dy, $dz))->isSolid()){
				return false;
			}
			$blocked = true;
			$groundY = $dy;
		}elseif($groundY - $dy > 1){
			// 2 格以上是墙，不是台阶；照样推一次，让碰撞去削。
			$blocked = true;
			$groundY = $dy;
		}else{
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
		}

		$this->motionX = $direction->x * $speed;
		$this->motionZ = $direction->z * $speed;

		// 台阶判断要用脚底的实际高度差：贴地时 y 与 floor(y) 可能差一点点，
		// 只比较 (groundY - floor(y)) 会让生物每 tick 都触发一次跳跃。
		// Mob 的 stepHeight 已经是 1.0，1 格台阶由 Entity::move() 的台阶逻辑
		// 平滑抬高，不应再叠加一次 0.42 的强制起跳（那会造成反复跳跃）。
		if(!$blocked and self::needsStepJump($this->y, $groundY, $this->onGround, $this->isInsideOfWater(), $this->stepHeight)){
			$this->motionY = 0.42;
		}elseif($groundY - $this->y < -1.1 and $this->onGround){
			$this->motionY = -0.15;
		}

		// 返回 false 让 MoveControl 累计 blockedTicks，到阈值后重算路径。
		return !$blocked;
	}

	/**
	 * 是否需要为台阶起跳（兜底）。
	 *
	 * 生物默认 stepHeight = 1.0，1 格以内的抬升由 Entity::move() 直接处理，
	 * 这里只在“脚底到目标地面”的高度差超过当前 stepHeight、但仍在跳跃
	 * 可达范围（约 1.1 格）时才返回 true。
	 *
	 * 用脚底实际高度差而不是 (ground - floor(y))，否则贴地时的一点点浮点误差
	 * 会被当成 1 格台阶，导致生物边走边跳。
	 *
	 * @param float $feetY      实体脚底 y
	 * @param int   $groundY    目标列可站立的地面 y
	 * @param bool  $onGround
	 * @param bool  $inWater
	 * @param float $stepHeight 实体自身可跨越的高度
	 * @return bool
	 */
	public static function needsStepJump(float $feetY, int $groundY, bool $onGround, bool $inWater, float $stepHeight = 1.0) : bool{
		if(!$onGround or $inWater){
			return false;
		}

		$stepUp = $groundY - $feetY;

		return $stepUp > $stepHeight + 0.001 and $stepUp <= 1.2;
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
