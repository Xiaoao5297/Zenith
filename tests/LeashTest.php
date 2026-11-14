<?php

/**
 * B2 回归测试：拴绳（Lead）+ 马具物品 + Entity 拴绳状态机。
 *
 * 覆盖 docs/incore-II 移植进来的三块内容：
 *  - entity/LeashKnot：网络 id、尺寸、栅栏判定、NBT 构造、坐标判定；
 *  - entity/Entity：DATA_LEAD / DATA_LEAD_HOLDER 默认值、setLeashedTo/unleash/
 *    isLeashedTo 状态机、saveNBT 持久化与 restoreLeashFromNBT 恢复；
 *  - item/{Lead,NameTag,Saddle,CarrotOnAStick,*HorseArmor}：id、名称、堆叠上限。
 *
 * 依赖说明：Entity 的构造函数需要 FullChunk/Level/Server，无法离线构造，
 * 因此用 newInstanceWithoutConstructor() 跳过构造，只测不依赖世界的逻辑；
 * 测试替身用 __destruct() 空实现规避 Entity::close() 对 Server 的依赖。
 *
 * 覆盖限制：Leash 分支依赖真实 Level::getBlock() 与 LeashKnot::getOrCreate()，
 * 只验证「无 Level 时不崩溃、不产生拴绳」，栅栏坐标重建需上服人工确认。
 */

use pocketmine\block\Fence;
use pocketmine\block\FenceGate;
use pocketmine\block\NetherBrickFence;
use pocketmine\block\Stone;
use pocketmine\entity\Entity;
use pocketmine\entity\LeashKnot;
use pocketmine\item\CarrotOnAStick;
use pocketmine\item\DiamondHorseArmor;
use pocketmine\item\GoldenHorseArmor;
use pocketmine\item\IronHorseArmor;
use pocketmine\item\Item;
use pocketmine\item\Lead;
use pocketmine\item\LeatherHorseArmor;
use pocketmine\item\NameTag;
use pocketmine\item\Saddle;
use pocketmine\nbt\tag\ByteTag;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\DoubleTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\nbt\tag\LongTag;

// 测试文件在类声明阶段就要解析 Entity/LeashKnot 的父类，所以自动加载器必须在
// 文件作用域注册（而不是在 run() 里）。
spl_autoload_register(function($class){
	$path = dirname(__DIR__) . "/src/" . str_replace("\\", "/", $class) . ".php";
	if(is_file($path)){
		require_once $path;
	}
});

/**
 * 轻量 Entity 替身：跳过构造、吞掉发包、暴露拴绳内部状态。
 */
class LeashTestEntity extends Entity{

	public function __destruct(){
	}

	public function sendData($player, array $data = null){
	}

	public function getSaveId(){
		return "LeashTestEntity";
	}

	public function assignId($id){
		$this->id = $id;
	}

	public function assignNbt(CompoundTag $tag){
		$this->namedtag = $tag;
	}

	public function doRestoreLeash(){
		$this->restoreLeashFromNBT();
	}
}

/**
 * LeashKnot 替身：只为了在测试结束时绕过 Entity::close() 对 Server 的依赖。
 */
class LeashTestKnot extends LeashKnot{

	public function __destruct(){
	}
}

class LeashTest{

	public static function run() : array{
		return array_merge(
			self::testKnotDefaults(),
			self::testSupportedFence(),
			self::testKnotNbt(),
			self::testKnotPositionChecks(),
			self::testEntityLeashDefaults(),
			self::testLeashTransitions(),
			self::testLeashNbtRoundtrip(),
			self::testItems()
		);
	}

	private static function testKnotDefaults() : array{
		$ref = new \ReflectionClass(LeashKnot::class);
		$defaults = $ref->getDefaultProperties();

		return [
			["knot_network_id", LeashKnot::NETWORK_ID === 88, "NETWORK_ID = " . LeashKnot::NETWORK_ID],
			["knot_size", $defaults["width"] === 0.375 and $defaults["length"] === 0.375 and $defaults["height"] === 0.5, "0.375 x 0.375 x 0.5"],
			["knot_no_collide", $defaults["canCollide"] === false, "canCollide = false"],
			["knot_save_id", self::newKnot(0.5, 64.0, 0.5)->getSaveId() === "LeashKnot", "未注册实体也不写坏 id"],
		];
	}

	private static function testSupportedFence() : array{
		return [
			["fence_supported", LeashKnot::isSupportedFence(self::fenceAt(1, 64, 1)) === true, "Fence"],
			["nether_fence_supported", LeashKnot::isSupportedFence(new NetherBrickFence()) === true, "NetherBrickFence"],
			["stone_not_supported", LeashKnot::isSupportedFence(new Stone()) === false, "Stone"],
			["fence_gate_not_supported", LeashKnot::isSupportedFence(new FenceGate()) === false, "FenceGate"],
		];
	}

	private static function testKnotNbt() : array{
		$fence = self::fenceAt(10, 70, -3);
		$nbt = LeashKnot::createNBT($fence);

		return [
			["knot_nbt_pos", $nbt["Pos"][0] === 10.5 and $nbt["Pos"][1] === 70.5 and $nbt["Pos"][2] === -2.5, "Pos = 栅栏中心"],
			["knot_nbt_motion", (float) $nbt["Motion"][0] === 0.0 and (float) $nbt["Motion"][1] === 0.0 and (float) $nbt["Motion"][2] === 0.0, "Motion = 0"],
			["knot_nbt_rotation", (float) $nbt["Rotation"][0] === 0.0 and (float) $nbt["Rotation"][1] === 0.0, "Rotation = 0"],
		];
	}

	private static function testKnotPositionChecks() : array{
		$knot = self::newKnot(5.5, 64.0, 5.5);

		return [
			["knot_on_fence", $knot->isOnFence(self::fenceAt(5, 64, 5)) === true, "floor(5.5) = 5"],
			["knot_off_fence", $knot->isOnFence(self::fenceAt(6, 64, 5)) === false, "相邻格子不算"],
			["knot_off_fence_y", $knot->isOnFence(self::fenceAt(5, 65, 5)) === false, "Y 轴必须一致"],
		];
	}

	private static function testEntityLeashDefaults() : array{
		$entity = self::newEntity(1);

		return [
			["entity_lead_default_off", (int) $entity->getDataProperty(Entity::DATA_LEAD) === 0, "DATA_LEAD = 0"],
			["entity_lead_holder_default", $entity->getLeashHolderEid() === -1, "DATA_LEAD_HOLDER = -1"],
			["entity_not_leashed", $entity->isLeashed() === false, "初始未牵引"],
			["entity_no_holder", $entity->getLeashHolder() === null, "无 Level 时返回 null"],
		];
	}

	private static function testLeashTransitions() : array{
		$holder = self::newEntity(7);
		$mob = self::newEntity(8);

		$leashed = $mob->setLeashedTo($holder);
		$stateAfterLeash = [$mob->isLeashed(), $mob->getLeashHolderEid(), $mob->isLeashedTo($holder)];

		$unleashed = $mob->unleash();
		$stateAfterUnleash = [$mob->isLeashed(), $mob->getLeashHolderEid()];
		$secondUnleash = $mob->unleash();

		$selfLeash = $mob->setLeashedTo($mob);

		$dead = self::newEntity(9);
		self::setHealth($dead, 0);
		$deadLeash = $mob->setLeashedTo($dead);

		return [
			["leash_set_returns_true", $leashed === true, "setLeashedTo(holder)"],
			["leash_state", $stateAfterLeash === [true, 7, true], "isLeashed / holderEid / isLeashedTo"],
			["unleash", $unleashed === true and $stateAfterUnleash === [false, -1], "holder 复位为 -1"],
			["unleash_twice", $secondUnleash === false, "未牵引时返回 false"],
			["leash_self_rejected", $selfLeash === false, "不能拴自己"],
			["leash_dead_holder_rejected", $deadLeash === false, "已死亡持有者"],
		];
	}

	private static function testLeashNbtRoundtrip() : array{
		$holder = self::newEntity(11);
		$mob = self::newEntity(12);
		$mob->setLeashedTo($holder);
		$mob->saveNBT();

		$saved = [
			isset($mob->namedtag->Leashed) and (int) $mob->namedtag["Leashed"] === 1,
			isset($mob->namedtag->LeashHolder) and (int) $mob->namedtag["LeashHolder"] === 11,
			!isset($mob->namedtag->Leash),
		];

		$mob->unleash();
		$mob->saveNBT();
		$cleared = !isset($mob->namedtag->Leashed) and !isset($mob->namedtag->Leash) and !isset($mob->namedtag->LeashHolder);

		$restored = self::newEntity(13);
		$restored->namedtag->Leashed = new ByteTag("Leashed", 1);
		$restored->namedtag->LeashHolder = new LongTag("LeashHolder", 42);
		$restored->doRestoreLeash();

		$notLeashed = self::newEntity(14);
		$notLeashed->namedtag->Leashed = new ByteTag("Leashed", 0);
		$notLeashed->namedtag->LeashHolder = new LongTag("LeashHolder", 42);
		$notLeashed->doRestoreLeash();

		$noLevel = self::newEntity(15);
		$noLevel->namedtag->Leashed = new ByteTag("Leashed", 1);
		$noLevel->namedtag->Leash = new CompoundTag("Leash", [
			new IntTag("X", 3),
			new IntTag("Y", 64),
			new IntTag("Z", 3),
		]);
		$noLevel->doRestoreLeash();

		return [
			["nbt_saves_leashed_holder", $saved === [true, true, true], "Leashed=1 + LeashHolder=11"],
			["nbt_clears_when_unleashed", $cleared === true, "unleash 后三个标签都移除"],
			["nbt_restore_holder", $restored->isLeashed() === true and $restored->getLeashHolderEid() === 42, "LeashHolder 直接恢复"],
			["nbt_restore_ignores_unleashed", $notLeashed->isLeashed() === false, "Leashed=0 不恢复"],
			["nbt_restore_fence_without_level", $noLevel->isLeashed() === false, "无 Level 时不崩溃、不恢复"],
		];
	}

	private static function testItems() : array{
		$lead = new Lead();
		$nameTag = new NameTag();
		$saddle = new Saddle();
		$carrot = new CarrotOnAStick();
		$leather = new LeatherHorseArmor();
		$iron = new IronHorseArmor();
		$gold = new GoldenHorseArmor();
		$diamond = new DiamondHorseArmor();

		$armorIds = [$leather->getId(), $iron->getId(), $gold->getId(), $diamond->getId()];

		return [
			["lead_id", $lead->getId() === 420 and Lead::LEAD === 420, "id = 420"],
			["lead_meta", $lead->getName() === "Lead" and $lead->getMaxStackSize() === 64, "name / stack 64"],
			["nametag_id", $nameTag->getId() === 421 and NameTag::NAME_TAG === 421, "id = 421"],
			["nametag_stack", $nameTag->getName() === "Name Tag" and $nameTag->getMaxStackSize() === 64, "name / stack 64"],
			["saddle_id", $saddle->getId() === Item::SADDLE and Item::SADDLE === 329, "id = 329"],
			["saddle_single", $saddle->getMaxStackSize() === 1 and (new Saddle(0, 5))->getCount() === 1, "构造时钳到 1"],
			["carrot_on_a_stick", $carrot->getId() === Item::CARROT_ON_A_STICK and $carrot->getMaxStackSize() === 1 and $carrot->getMaxDurability() === 25, "id 398 / durability 25"],
			["horse_armor_ids", $armorIds === [416, 417, 418, 419], implode(",", $armorIds)],
			["horse_armor_single", $leather->getMaxStackSize() === 1 and $iron->getMaxStackSize() === 1 and $gold->getMaxStackSize() === 1 and $diamond->getMaxStackSize() === 1, "全部 stack 1"],
		];
	}

	/**
	 * @return LeashTestEntity
	 */
	private static function newEntity(int $id){
		$ref = new \ReflectionClass(LeashTestEntity::class);
		/** @var LeashTestEntity $entity */
		$entity = $ref->newInstanceWithoutConstructor();
		$entity->assignId($id);
		$entity->assignNbt(new CompoundTag("", [
			"Pos" => new ListTag("Pos", [
				new DoubleTag(0, 0.0),
				new DoubleTag(1, 64.0),
				new DoubleTag(2, 0.0),
			]),
		]));
		return $entity;
	}

	/**
	 * @return LeashTestKnot
	 */
	private static function newKnot(float $x, float $y, float $z){
		$ref = new \ReflectionClass(LeashTestKnot::class);
		/** @var LeashTestKnot $knot */
		$knot = $ref->newInstanceWithoutConstructor();
		$knot->x = $x;
		$knot->y = $y;
		$knot->z = $z;
		return $knot;
	}

	private static function fenceAt(int $x, int $y, int $z) : Fence{
		$fence = new Fence();
		$fence->x = $x;
		$fence->y = $y;
		$fence->z = $z;
		return $fence;
	}

	private static function setHealth(Entity $entity, int $health){
		$prop = new \ReflectionProperty(Entity::class, "health");
		$prop->setAccessible(true);
		$prop->setValue($entity, $health);
	}
}
