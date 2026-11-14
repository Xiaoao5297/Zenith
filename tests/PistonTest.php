<?php

/**
 * B1 回归测试：活塞体系（移植自 docs/incore-II/pocketmine）。
 *
 * 覆盖：
 *  - Block 常量与 $list 注册（普通活塞 33 / 粘性活塞 29 / 活塞头 34 / 移动方块 36）；
 *  - PistonBase / PistonHead 的朝向换算、META_EXTENDED 位运算；
 *  - PistonBase::canPush() 的不可推动方块/高度边界；
 *  - Block::breaksWhenMovedByPiston() / sticksToPiston() 查表；
 *  - PistonArm 的进度换算（preMove + onUpdate 推进到 finishMove）；
 *  - Tile / 音效常量与 Entity 活塞推动默认值。
 *
 * 依赖说明：活塞的真实伸缩需要 Level/Chunk/Server，离线只能测纯逻辑；
 * 方块在世界里的推动效果（含 tile 搬运、实体推移）需上服人工确认。
 */

use pocketmine\block\Block;
use pocketmine\block\MovingBlock;
use pocketmine\block\Piston;
use pocketmine\block\PistonBase;
use pocketmine\block\PistonHead;
use pocketmine\block\PistonMoveCalculator;
use pocketmine\block\StickyPiston;
use pocketmine\entity\Entity;
use pocketmine\item\Item;
use pocketmine\math\Vector3;
use pocketmine\network\protocol\LevelEventPacket;
use pocketmine\Player;
use pocketmine\tile\PistonArm;
use pocketmine\tile\Tile;

// 父类在类声明阶段就要解析，所以自动加载器要在文件作用域注册。
spl_autoload_register(function($class){
	$path = dirname(__DIR__) . "/src/" . str_replace("\\", "/", $class) . ".php";
	if(is_file($path)){
		require_once $path;
	}
});

/**
 * 暴露 protected createHead()，用于离线校验活塞头方向/粘性。
 */
class PistonTestPiston extends Piston{
	public function makeHead($face){
		return $this->createHead($face);
	}
}

class PistonTestStickyPiston extends StickyPiston{
	public function makeHead($face){
		return $this->createHead($face);
	}
}

/**
 * PistonArm 替身：跳过 NBT/发包/Level 队列，只测进度与状态机。
 */
class PistonTestArm extends PistonArm{
	public function saveNBT(){
	}

	public function spawnToAll(){
	}
}

/**
 * Entity 替身：空 __destruct 规避 Entity::close() 对 Server 的依赖。
 */
class PistonTestEntity extends Entity{
	public function __destruct(){
	}

	public function getSaveId(){
		return "PistonTestEntity";
	}
}

/**
 * 极简插件管理器替身：只吞掉 BlockPistonEvent。
 */
class PistonTestPluginManager{
	public function callEvent($event){
		return $event;
	}
}

class PistonTestServer{
	public $redstoneEnabled = true;

	/** @var PistonTestPluginManager */
	private $pluginManager;

	public function __construct(){
		$this->pluginManager = new PistonTestPluginManager();
	}

	public function getPluginManager(){
		return $this->pluginManager;
	}
}

/**
 * 内存 Level 替身：不碰 Chunk/方块缓存，只回答活塞需要的那几个方法，
 * 用来离线跑通 PistonBase::doMove() 的推动/收回路径。
 */
class PistonTestLevel extends \pocketmine\level\Level{
	/** @var Block[] */
	private $blocks = [];
	/** @var bool */
	public $powered = true;

	/** @var PistonTestServer */
	private $fakeServer;

	public function __construct(){
		$this->fakeServer = new PistonTestServer();
	}

	public function getServer(){
		return $this->fakeServer;
	}

	private function posKey($x, $y, $z){
		return ((int) $x) . ":" . ((int) $y) . ":" . ((int) $z);
	}

	public function put($x, $y, $z, $id, $meta = 0){
		return $this->setBlock(new Vector3($x, $y, $z), Block::get($id, $meta), true, false);
	}

	public function getBlock(Vector3 $pos, $cached = true){
		$pos = $pos->floor();
		$key = $this->posKey($pos->x, $pos->y, $pos->z);
		$block = isset($this->blocks[$key]) ? clone $this->blocks[$key] : Block::get(Block::AIR);

		$block->x = $pos->x;
		$block->y = $pos->y;
		$block->z = $pos->z;
		$block->level = $this;

		return $block;
	}

	public function setBlock(Vector3 $pos, Block $block, $direct = false, $update = true){
		$pos = $pos->floor();
		$block->x = $pos->x;
		$block->y = $pos->y;
		$block->z = $pos->z;
		$block->level = $this;
		$this->blocks[$this->posKey($pos->x, $pos->y, $pos->z)] = $block;

		return true;
	}

	public function scheduleUpdate(Vector3 $pos, $delay){
	}

	public function isBlockTickPending(Vector3 $pos, Block $block = null){
		return true;
	}

	public function getChunk(int $x, int $z, bool $create = false){
		return null;
	}

	public function getTile(Vector3 $pos){
		return null;
	}

	public function addSound(\pocketmine\level\sound\Sound $sound, array $players = null){
	}

	public function updateAround(Vector3 $pos){
	}

	public function updateAroundRedstone(Vector3 $pos, $ignoredFace = null){
	}

	public function useBreakOn(Vector3 $vector, Item &$item = null, Player $player = null, $createParticles = false){
		$this->setBlock($vector, Block::get(Block::AIR), true, false);

		return true;
	}

	public function getNearbyEntities(\pocketmine\math\AxisAlignedBB $bb, Entity $entity = null){
		return [];
	}

	public function isSidePowered(Vector3 $pos, $face) : bool{
		return $this->powered;
	}
}

class PistonTest{

	public static function run() : array{
		return array_merge(
			self::testBlockIds(),
			self::testBlockRegistry(),
			self::testFacingRoundtrip(),
			self::testExtendedBit(),
			self::testPistonHeadMeta(),
			self::testCreateHead(),
			self::testCanPush(),
			self::testBreakAndStickTables(),
			self::testMoveCalculator(),
			self::testPistonArmProgress(),
			self::testPistonArmAttachedState(),
			self::testTileAndSoundConstants(),
			self::testEntityDefaults(),
			self::testPistonMoveIntegration()
		);
	}

	private static function testBlockIds() : array{
		return [
			["block_id_sticky_piston", Block::STICKY_PISTON === 29, "STICKY_PISTON = " . Block::STICKY_PISTON],
			["block_id_piston", Block::PISTON === 33, "PISTON = " . Block::PISTON],
			["block_id_piston_head", Block::PISTON_HEAD === 34 and Block::PISTON_EXTENSION === 34, "PISTON_HEAD = " . Block::PISTON_HEAD],
			["block_id_moving_block", Block::MOVING_BLOCK === 36, "MOVING_BLOCK = " . Block::MOVING_BLOCK],
			["item_id_matches_block", Item::PISTON === Block::PISTON and Item::STICKY_PISTON === Block::STICKY_PISTON and Item::PISTON_HEAD === Block::PISTON_HEAD, "Item 29/33/34"],
		];
	}

	private static function testBlockRegistry() : array{
		Block::init();
		$list = Block::$list;

		$ok = $list[Block::PISTON] === Piston::class
			and $list[Block::STICKY_PISTON] === StickyPiston::class
			and $list[Block::PISTON_HEAD] === PistonHead::class
			and $list[Block::MOVING_BLOCK] === MovingBlock::class;

		return [
			["block_registry", $ok, "29/33/34/36 均注册到 Block::\$list"],
			["block_get_instance", Block::get(Block::PISTON) instanceof Piston and Block::get(Block::STICKY_PISTON) instanceof StickyPiston, "Block::get() 返回对应子类"],
			["block_full_list_meta", Block::$fullList[(Block::PISTON_HEAD << 4) | 0x09] instanceof PistonHead, "fullList 含活塞头 meta 9"],
			["block_names", (new Piston())->getName() === "Piston" and (new StickyPiston())->getName() === "Sticky Piston" and (new MovingBlock())->getName() === "Moving Block", "getName()"],
			["block_props", (new Piston())->getHardness() === 1.5 and (new Piston())->getResistance() === 1.5 and (new MovingBlock())->getHardness() === -1, "硬度/抗性"],
		];
	}

	private static function testFacingRoundtrip() : array{
		$sides = [Vector3::SIDE_DOWN, Vector3::SIDE_UP, Vector3::SIDE_NORTH, Vector3::SIDE_SOUTH, Vector3::SIDE_WEST, Vector3::SIDE_EAST];
		$results = [];

		foreach($sides as $side){
			$piston = new Piston(0);
			$piston->setFacing($side);
			$results[] = ["facing_" . $side, $piston->getFacing() === $side, "set/get 往返 side = $side"];
		}

		// 水平朝向在 meta 里存反方向（MC 的惯例）
		$piston = new Piston(0);
		$piston->setFacing(Vector3::SIDE_NORTH);
		$results[] = ["facing_meta_north", $piston->getDamage() === Vector3::SIDE_SOUTH, "NORTH 存成 meta " . $piston->getDamage()];

		$piston = new Piston(0);
		$piston->setFacing(Vector3::SIDE_UP);
		$results[] = ["facing_meta_up", $piston->getDamage() === Vector3::SIDE_UP, "UP 原样存 meta " . $piston->getDamage()];

		$piston = new Piston(0x0f);
		$results[] = ["facing_meta_clamped", $piston->getDamage() === 0x0f and $piston->getFacing() === Vector3::SIDE_NORTH, "meta 0x0f 的朝向位 7 越界，归一为 NORTH"];

		$piston = new Piston(0);
		$piston->setFacing(99);
		$results[] = ["facing_invalid_side", $piston->getFacing() === Vector3::SIDE_SOUTH, "非法 side 先归一成 NORTH 存储(2)，读回是它的反向 SOUTH"];

		return $results;
	}

	private static function testExtendedBit() : array{
		$piston = new Piston(0);
		$piston->setFacing(Vector3::SIDE_EAST);
		$facingMeta = $piston->getDamage();
		$piston->setExtended(true);
		$extendedMeta = $piston->getDamage();

		$piston->setFacing(Vector3::SIDE_WEST);
		$afterRefacing = $piston->getDamage();
		$piston->setExtended(false);

		return [
			["extended_bit_set", ($extendedMeta & PistonBase::META_EXTENDED) === PistonBase::META_EXTENDED and $piston->getFacing() === Vector3::SIDE_WEST, "META_EXTENDED = 0x08"],
			["extended_keeps_facing", ($facingMeta & PistonBase::META_FACING_MASK) === Vector3::SIDE_WEST, "setExtended 不清朝向"],
			["facing_keeps_extended", ($afterRefacing & PistonBase::META_EXTENDED) === PistonBase::META_EXTENDED, "setFacing 不清 extended 位"],
			["extended_bit_clear", $piston->getDamage() === Vector3::SIDE_EAST, "setExtended(false) 后只剩朝向"],
			["extended_without_level", (new Piston(PistonBase::META_EXTENDED))->isExtended() === true, "无 Level 时按 meta 判定"],
		];
	}

	private static function testPistonHeadMeta() : array{
		$head = new PistonHead(0);
		$head->setFacing(Vector3::SIDE_SOUTH);
		$facingOnly = $head->getDamage();
		$head->setSticky(true);
		$stickyMeta = $head->getDamage();
		$head->setFacing(Vector3::SIDE_UP);
		$refaced = $head->getDamage();
		$stickyName = $head->getName();
		$head->setSticky(false);

		return [
			["head_facing_meta", $facingOnly === Vector3::SIDE_NORTH, "SOUTH 存成 meta " . $facingOnly],
			["head_sticky_bit", ($stickyMeta & PistonHead::META_STICKY) === PistonHead::META_STICKY, "META_STICKY = 0x08"],
			["head_refacing_keeps_sticky", ($refaced & PistonHead::META_STICKY) === PistonHead::META_STICKY and $head->getFacing() === Vector3::SIDE_UP, "setFacing 保留粘性位"],
			["head_sticky_name", $stickyName === "Sticky Piston Head", $stickyName],
			["head_not_sticky", $head->isSticky() === false and $head->getName() === "Piston Head", "setSticky(false)"],
			["head_drops", $head->getDrops(Item::get(Item::AIR)) === [[Item::PISTON, 0, 1]], "普通头掉落活塞"],
			["head_no_push", $head->canBePushedByPiston() === false and $head->canBePulledByPiston() === false, "活塞头不可推/拉"],
		];
	}

	private static function testCreateHead() : array{
		$piston = new PistonTestPiston(0);
		$head = $piston->makeHead(Vector3::SIDE_NORTH);
		$sticky = (new PistonTestStickyPiston(0))->makeHead(Vector3::SIDE_DOWN);

		return [
			["create_head_normal", $head instanceof PistonHead and $head->getFacing() === Vector3::SIDE_NORTH and $head->isSticky() === false, "普通活塞生成非粘性头"],
			["create_head_sticky", $sticky instanceof PistonHead and $sticky->getFacing() === Vector3::SIDE_DOWN and $sticky->isSticky() === true, "粘性活塞生成粘性头"],
			["sticky_flag", (new StickyPiston(0))->isSticky() === true and (new Piston(0))->isSticky() === false, "PistonBase::\$sticky"],
		];
	}

	private static function testCanPush() : array{
		$air = Block::get(Block::AIR);
		$air->y = 5;
		$stone = Block::get(Block::STONE);
		$stone->y = 5;
		$obsidian = Block::get(Block::OBSIDIAN);
		$obsidian->y = 5;
		$bedrock = Block::get(Block::BEDROCK);
		$bedrock->y = 5;
		$lowStone = Block::get(Block::STONE);
		$lowStone->y = -1;
		$highStone = Block::get(Block::STONE);
		$highStone->y = 128;
		$bottomStone = Block::get(Block::STONE);
		$bottomStone->y = 0;
		$topStone = Block::get(Block::STONE);
		$topStone->y = 127;

		return [
			["canpush_air", PistonBase::canPush($air, Vector3::SIDE_NORTH, false, true) === true, "空气可推"],
			["canpush_stone", PistonBase::canPush($stone, Vector3::SIDE_NORTH, false, true) === true, "石头可推"],
			["canpush_obsidian", PistonBase::canPush($obsidian, Vector3::SIDE_NORTH, false, true) === false, "黑曜石不可推"],
			["canpush_bedrock", PistonBase::canPush($bedrock, Vector3::SIDE_NORTH, false, true) === false, "基岩不可推"],
			["canpush_y_min", PistonBase::canPush($lowStone, Vector3::SIDE_NORTH, false, true) === false, "y < 0 不可推"],
			["canpush_y_max", PistonBase::canPush($highStone, Vector3::SIDE_NORTH, false, true) === false, "y >= 128 不可推"],
			["canpush_floor", PistonBase::canPush($bottomStone, Vector3::SIDE_DOWN, false, true) === false, "y = 0 不能向下推"],
			["canpush_ceiling", PistonBase::canPush($topStone, Vector3::SIDE_UP, false, true) === false, "y = 127 不能向上推"],
			["canpush_breakable_no_destroy", PistonBase::canPush(Block::get(Block::CACTUS), Vector3::SIDE_NORTH, false, true) === false, "会被破坏且不许破坏时不推"],
			["canpush_breakable_destroy", PistonBase::canPush(Block::get(Block::CACTUS), Vector3::SIDE_NORTH, true, true) === true, "允许破坏时可推"],
		];
	}

	private static function testBreakAndStickTables() : array{
		return [
			["breaks_cactus", Block::get(Block::CACTUS)->breaksWhenMovedByPiston() === true, "仙人掌移动即破坏"],
			["breaks_stone", Block::get(Block::STONE)->breaksWhenMovedByPiston() === false, "石头不破坏"],
			["breaks_leaves", Block::get(Block::LEAVES)->breaksWhenMovedByPiston() === true, "树叶移动即破坏"],
			["breaks_ladder", Block::get(Block::LADDER)->breaksWhenMovedByPiston() === true, "梯子移动即破坏"],
			["sticks_stone", Block::get(Block::STONE)->canStickToPiston() === true, "石头可粘"],
			["sticks_ladder", Block::get(Block::LADDER)->canStickToPiston() === false, "梯子不可粘"],
			["stick_blocks_default", Block::get(Block::STONE)->canStickBlocks() === false, "默认不拉侧面方块"],
			["piston_base_push_rule", (new Piston(0))->canBePushedByPiston() === true and (new Piston(PistonBase::META_EXTENDED))->canBePushedByPiston() === false, "伸出中的活塞不可被推"],
		];
	}

	private static function testMoveCalculator() : array{
		$piston = new Piston(0);
		$piston->setFacing(Vector3::SIDE_UP);
		$calc = new PistonMoveCalculator($piston, true);
		$canMove = $calc->canMove();

		$retract = new PistonMoveCalculator(new StickyPiston(0), false);

		return [
			["calculator_empty_world", $canMove === true and count($calc->getBlocksToMove()) === 0 and count($calc->getBlocksToDestroy()) === 0, "无 Level 时视为空气"],
			["calculator_retract_ok", $retract->canMove() === true, "收回方向不阻塞"],
			["calculator_limits", PistonBase::MOVE_LIMIT === 12 and PistonBase::MOVE_CANCELLED === -1, "MOVE_LIMIT = 12"],
		];
	}

	private static function testPistonArmProgress() : array{
		$ref = new \ReflectionClass(PistonTestArm::class);
		$arm = $ref->newInstanceWithoutConstructor();

		$initial = $arm->getState() === PistonArm::STATE_RETRACTED and $arm->getProgress() === 0.0 and $arm->isExtending() === false;

		$arm->preMove(true, [], Vector3::SIDE_EAST, true, true);
		$preMoveOk = $arm->getState() === PistonArm::STATE_EXTENDING
			and $arm->getNewState() === PistonArm::STATE_EXTENDED
			and $arm->getProgress() === 0.0
			and $arm->getFacing() === Vector3::SIDE_EAST
			and $arm->isSticky() === true
			and $arm->isPowered() === true
			and $arm->movable === false;

		$steps = [];
		for($i = 0; $i < 3; ++$i){
			$steps[] = $arm->onUpdate();
		}
		$midProgress = $arm->getProgress();
		$midState = $arm->getState();
		$last = $arm->onUpdate();

		$extendedOk = $last === false
			and $arm->getState() === PistonArm::STATE_EXTENDED
			and $arm->getProgress() === 1.0
			and $arm->movable === true;

		$arm->preMove(false, []);
		for($i = 0; $i < 4; ++$i){
			$arm->onUpdate();
		}
		$retractedOk = $arm->getState() === PistonArm::STATE_RETRACTED and $arm->getProgress() === 0.0;

		return [
			["arm_defaults", $initial, "初始 RETRACTED / progress 0"],
			["arm_premove", $preMoveOk, "preMove 进 EXTENDING"],
			["arm_step", PistonArm::MOVE_STEP === 0.25 and $midProgress === 0.75 and $midState === PistonArm::STATE_EXTENDING, "3 tick 后 0.75"],
			["arm_finish_extend", $extendedOk, "第 4 tick 到 EXTENDED"],
			["arm_finish_retract", $retractedOk, "收回回到 RETRACTED"],
		];
	}

	private static function testPistonArmAttachedState() : array{
		$ref = new \ReflectionClass(PistonTestArm::class);
		$arm = $ref->newInstanceWithoutConstructor();
		$arm->preMove(true, [new Vector3(1, 2, 3), new Vector3(4, 5, 6)], Vector3::SIDE_NORTH, false, true);

		$attached = $arm->getAttachedBlockPositions();
		$ok = count($attached) === 2 and $attached[0]->equals(new Vector3(1, 2, 3));

		return [
			["arm_attached_blocks", $ok, "preMove 保存 2 个坐标"],
			["arm_states_distinct", PistonArm::STATE_RETRACTED === 0 and PistonArm::STATE_EXTENDING === 1 and PistonArm::STATE_EXTENDED === 2 and PistonArm::STATE_RETRACTING === 3, "0/1/2/3"],
			["arm_movable_api", method_exists($arm, "isMovable") and method_exists($arm, "setMovable"), "Zenith 补充 isMovable()/setMovable()"],
		];
	}

	private static function testTileAndSoundConstants() : array{
		$registered = Tile::registerTile(PistonArm::class);

		return [
			["tile_consts", Tile::PISTON_ARM === "PistonArm" and Tile::MOVING_BLOCK === "MovingBlock", "tile id 字符串"],
			["tile_register", $registered === true, "Tile::registerTile(PistonArm)"],
			["sound_consts", LevelEventPacket::EVENT_SOUND_PISTON_IN === 1083 and LevelEventPacket::EVENT_SOUND_PISTON_OUT === 1084, "1083/1084"],
			["sound_classes", is_subclass_of(\pocketmine\level\sound\PistonInSound::class, \pocketmine\level\sound\GenericSound::class) and is_subclass_of(\pocketmine\level\sound\PistonOutSound::class, \pocketmine\level\sound\GenericSound::class), "PistonIn/OutSound extends GenericSound"],
		];
	}

	private static function testEntityDefaults() : array{
		$ref = new \ReflectionClass(PistonTestEntity::class);
		$entity = $ref->newInstanceWithoutConstructor();

		return [
			["entity_can_be_pushed", $entity->canBePushedByPiston() === true, "默认允许被推"],
			["entity_on_push_noop", $entity->onPushByPiston(null) === null, "默认回调空实现"],
			["entity_methods_exist", method_exists(Entity::class, "canBePushedByPiston") and method_exists(Entity::class, "onPushByPiston"), "Entity 两个方法"],
		];
	}

	/**
	 * 用内存 Level 替身跑通 PistonBase::doMove()：验证推动方向偏移、
	 * 活塞头生成、粘性收回拉方块、普通活塞收回只删头。
	 */
	private static function testPistonMoveIntegration() : array{
		$results = [];

		// 1) 普通活塞向上推 2 格石头链
		$level = new PistonTestLevel();
		$piston = new Piston(0);
		$piston->setFacing(Vector3::SIDE_UP);
		$level->put(0, 4, 0, Block::PISTON, $piston->getDamage());
		$level->put(0, 5, 0, Block::STONE);
		$level->put(0, 6, 0, Block::STONE);
		$level->powered = true;

		$ret = $level->getBlock(new Vector3(0, 4, 0))->onUpdate(\pocketmine\level\Level::BLOCK_UPDATE_SCHEDULED);

		$results[] = ["move_extend_returns", $ret === \pocketmine\level\Level::BLOCK_UPDATE_SCHEDULED, "onUpdate 返回 SCHEDULED"];
		$results[] = ["move_extend_chain", $level->getBlock(new Vector3(0, 6, 0))->getId() === Block::STONE and $level->getBlock(new Vector3(0, 7, 0))->getId() === Block::STONE, "石头 5/6 -> 6/7，原 7 是空气"];
		$results[] = ["move_extend_head", $level->getBlock(new Vector3(0, 5, 0))->getId() === Block::PISTON_HEAD, "头部占住原第 1 格"];

		$extendedPiston = $level->getBlock(new Vector3(0, 4, 0));
		$results[] = ["move_extend_meta", $extendedPiston instanceof Piston and ($extendedPiston->getDamage() & PistonBase::META_EXTENDED) === PistonBase::META_EXTENDED, "活塞 meta 带 0x08"];

		// 2) 粘性活塞收回：把头上那格方块拉回来
		$level = new PistonTestLevel();
		$sticky = new StickyPiston(0);
		$sticky->setFacing(Vector3::SIDE_UP);
		$sticky->setExtended(true);
		$level->put(0, 4, 0, Block::STICKY_PISTON, $sticky->getDamage());
		$head = new PistonHead(0);
		$head->setFacing(Vector3::SIDE_UP);
		$head->setSticky(true);
		$level->put(0, 5, 0, Block::PISTON_HEAD, $head->getDamage());
		$level->put(0, 6, 0, Block::STONE);
		$level->powered = false;

		$level->getBlock(new Vector3(0, 4, 0))->onUpdate(\pocketmine\level\Level::BLOCK_UPDATE_SCHEDULED);

		$pulledPiston = $level->getBlock(new Vector3(0, 4, 0));
		$results[] = ["move_retract_sticky_pull", $level->getBlock(new Vector3(0, 5, 0))->getId() === Block::STONE and $level->getBlock(new Vector3(0, 6, 0))->getId() === Block::AIR, "石头 6 -> 5"];
		$results[] = ["move_retract_sticky_meta", ($pulledPiston->getDamage() & PistonBase::META_EXTENDED) === 0, "收回后清 0x08"];

		// 3) 普通活塞收回：只删头，不拉方块
		$level = new PistonTestLevel();
		$piston = new Piston(0);
		$piston->setFacing(Vector3::SIDE_EAST);
		$piston->setExtended(true);
		$level->put(2, 4, 0, Block::PISTON, $piston->getDamage());
		$head = new PistonHead(0);
		$head->setFacing(Vector3::SIDE_EAST);
		$level->put(3, 4, 0, Block::PISTON_HEAD, $head->getDamage());
		$level->put(4, 4, 0, Block::STONE);
		$level->powered = false;

		$level->getBlock(new Vector3(2, 4, 0))->onUpdate(\pocketmine\level\Level::BLOCK_UPDATE_SCHEDULED);

		$results[] = ["move_retract_normal_head", $level->getBlock(new Vector3(3, 4, 0))->getId() === Block::AIR, "头被移除"];
		$results[] = ["move_retract_normal_no_pull", $level->getBlock(new Vector3(4, 4, 0))->getId() === Block::STONE, "普通活塞不拉方块"];

		return $results;
	}
}
