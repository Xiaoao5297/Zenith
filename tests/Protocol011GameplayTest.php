<?php

/**
 * 0.11~0.13 旧版玩法补丁回归测试（Player.php）。
 *
 * 只覆盖不依赖 Level/Server/Inventory 的纯逻辑与协议门控：
 *  - ProtocolCompatibility::isLegacyPre014Protocol() 的边界；
 *  - 0.13 配方克隆（保留 UUID、映射物品、非 0.13 原样返回）；
 *  - 0.11 合成格归一化（5 槽 -> 10 槽、超长截断、非法长度）；
 *  - 0.11 有序/无序配方匹配（含偏移、列优先、失败分支）；
 *  - 0.14 玩家调用旧版入口时一律 no-op。
 *
 * 依赖说明：Player 构造函数需要 Level/Server，测试用
 * newInstanceWithoutConstructor() 跳过构造，并用空 __destruct() 规避
 * Entity::close() 对 Server 的依赖。需要服务器状态的方法（合成包处理、
 * 铁砧/附魔台会话、进食）只验证 0.14 门控短路，不构造真实运行时。
 */

use pocketmine\block\Anvil;
use pocketmine\block\EnchantingTable;
use pocketmine\inventory\ShapedRecipeFromJson;
use pocketmine\inventory\ShapelessRecipe;
use pocketmine\item\Item;
use pocketmine\network\protocol\ProtocolCompatibility;
use pocketmine\Player;
use pocketmine\utils\UUID;

spl_autoload_register(function($class){
	$path = dirname(__DIR__) . "/src/" . str_replace("\\", "/", $class) . ".php";
	if(is_file($path)){
		require_once $path;
	}
});

/**
 * 轻量 Player 替身：跳过构造，避免析构时访问 Server。
 */
class Protocol011GameplayTestPlayer extends Player{

	public function __destruct(){
	}
}

class Protocol011GameplayTest{

	public static function run() : array{
		return array_merge(
			self::testProtocolGates(),
			self::testProtocol013RecipeClone(),
			self::testProtocol011SlotNormalization(),
			self::testProtocol011ShapedMatching(),
			self::testProtocol011ShapelessMatching(),
			self::testModernPlayerNoOps()
		);
	}

	private static function testProtocolGates() : array{
		return [
			["legacy_pre014_011", ProtocolCompatibility::isLegacyPre014Protocol(21) === true, "21"],
			["legacy_pre014_012", ProtocolCompatibility::isLegacyPre014Protocol(34) === true, "34"],
			["legacy_pre014_013", ProtocolCompatibility::isLegacyPre014Protocol(37) === true, "37"],
			["legacy_pre014_014", ProtocolCompatibility::isLegacyPre014Protocol(70) === false, "70"],
			["legacy_pre014_015", ProtocolCompatibility::isLegacyPre014Protocol(84) === false, "84"],
			["legacy_pre014_unknown", ProtocolCompatibility::isLegacyPre014Protocol(40) === false, "40"],
			["player_gate_011", self::invokePrivate(self::playerAt(21), "isProtocol011Player") === true, "Player::isProtocol011"],
			["player_gate_014", self::invokePrivate(self::playerAt(70), "isLegacyPre014Player") === false, "Player::isLegacyPre014"],
		];
	}

	private static function testProtocol013RecipeClone() : array{
		$recipe = self::carrotRecipe();
		$uuid = $recipe->getId();

		$modified013 = false;
		$clone = self::invokeClone(self::playerAt(37), $recipe, $modified013);

		$modified014 = false;
		$same014 = self::invokeClone(self::playerAt(70), $recipe, $modified014);

		$modified012 = false;
		$same012 = self::invokeClone(self::playerAt(34), $recipe, $modified012);

		return [
			["clone013_modified", $modified013 === true, "0.13 必须重建配方"],
			["clone013_new_instance", $clone !== $recipe and $clone instanceof ShapedRecipeFromJson, "返回新配方对象"],
			["clone013_keeps_uuid", $clone->getId() !== null and $clone->getId()->equals($uuid), "UUID 保留"],
			["clone013_maps_result", $clone->getResult()->getId() === Item::FURNACE, "胡萝卜钓竿 -> 熔炉"],
			["clone013_keeps_ingredient", $clone->getIngredient(0, 0)->getId() === Item::STICK, "材料 STICK 不变"],
			["clone013_original_untouched", $recipe->getResult()->getId() === Item::CARROT_ON_A_STICK and $recipe->getId()->equals($uuid), "原配方不被修改"],
			["clone014_untouched", $same014 === $recipe and $modified014 === false, "0.14 原样返回"],
			["clone012_untouched", $same012 === $recipe and $modified012 === false, "0.12 原样返回"],
		];
	}

	private static function testProtocol011SlotNormalization() : array{
		$player = self::playerAt(21);

		$five = [
			Item::get(Item::STONE, 0, 1),
			Item::get(Item::DIRT, 0, 1),
			Item::get(Item::COBBLESTONE, 0, 1),
			Item::get(Item::PLANKS, 0, 1),
			Item::get(Item::SAND, 0, 1),
		];
		$normalized = self::invokePrivate($player, "normalizeProtocol011CraftingSlots", [$five]);
		$ids = [];
		if(is_array($normalized)){
			foreach($normalized as $item){
				$ids[] = $item instanceof Item ? $item->getId() : -1;
			}
		}

		$long = [];
		for($i = 0; $i < 11; ++$i){
			$long[] = Item::get(Item::STONE, $i, 1);
		}
		$sliced = self::invokePrivate($player, "normalizeProtocol011CraftingSlots", [$long]);

		$six = array_fill(0, 6, Item::get(Item::AIR, 0, 0));

		return [
			["slots_five_shape", is_array($normalized) and count($normalized) === 10, "5 槽补齐为 10 槽"],
			["slots_five_layout", $ids === [Item::STONE, Item::DIRT, Item::AIR, Item::COBBLESTONE, Item::PLANKS, Item::AIR, Item::AIR, Item::AIR, Item::AIR, Item::SAND], implode(",", $ids)],
			["slots_long_truncated", is_array($sliced) and count($sliced) === 10 and $sliced[9]->getDamage() === 9, "11 槽只取前 10"],
			["slots_invalid_count", self::invokePrivate($player, "normalizeProtocol011CraftingSlots", [$six]) === null, "6 槽返回 null"],
		];
	}

	private static function testProtocol011ShapedMatching() : array{
		$player = self::playerAt(21);

		$recipe = new ShapedRecipeFromJson(Item::get(Item::STICK, 0, 4), 1, 2);
		$recipe->addIngredient(0, 0, Item::get(Item::PLANKS, 0, 1));
		$recipe->addIngredient(1, 0, Item::get(Item::PLANKS, 0, 1));

		$aligned = self::emptyGrid();
		$aligned[0] = Item::get(Item::PLANKS, 0, 1);
		$aligned[1] = Item::get(Item::PLANKS, 0, 1);

		$offset = self::emptyGrid();
		$offset[3] = Item::get(Item::PLANKS, 0, 1);
		$offset[4] = Item::get(Item::PLANKS, 0, 1);

		$wrong = self::emptyGrid();
		$wrong[0] = Item::get(Item::PLANKS, 0, 1);
		$wrong[1] = Item::get(Item::DIRT, 0, 1);

		$column = self::emptyGrid();
		$column[0] = Item::get(Item::PLANKS, 0, 1);
		$column[3] = Item::get(Item::PLANKS, 0, 1);

		$alignedIngredients = [];
		$alignedOk = self::invokeMatchShaped($player, $recipe, $aligned, $alignedIngredients);

		$offsetIngredients = [];
		$offsetOk = self::invokeMatchShaped($player, $recipe, $offset, $offsetIngredients);

		$wrongIngredients = [];
		$wrongOk = self::invokeMatchShaped($player, $recipe, $wrong, $wrongIngredients);

		$columnIngredients = [];
		$columnOk = self::invokeMatchShaped($player, $recipe, $column, $columnIngredients, true);

		return [
			["shaped_aligned", $alignedOk === true and count($alignedIngredients) === 2, "2 个材料"],
			["shaped_offset", $offsetOk === true, "自动裁剪空白行列"],
			["shaped_wrong_item", $wrongOk === false, "材料不匹配"],
			["shaped_column_major", $columnOk === true, "列优先布局"],
		];
	}

	private static function testProtocol011ShapelessMatching() : array{
		$player = self::playerAt(21);

		$recipe = new ShapelessRecipe(Item::get(Item::COOKIE, 0, 8));
		$recipe->addIngredient(Item::get(Item::WHEAT, 0, 1));
		$recipe->addIngredient(Item::get(Item::WHEAT, 0, 1));

		$grid = self::emptyGrid();
		$grid[2] = Item::get(Item::WHEAT, 0, 1);
		$grid[7] = Item::get(Item::WHEAT, 0, 1);

		$tooMany = self::emptyGrid();
		$tooMany[0] = Item::get(Item::WHEAT, 0, 1);
		$tooMany[1] = Item::get(Item::WHEAT, 0, 1);
		$tooMany[2] = Item::get(Item::WHEAT, 0, 1);

		$ingredients = [];
		$ok = self::invokeMatchShapeless($player, $recipe, $grid, $ingredients);

		$wrongIngredients = [];
		$wrong = self::invokeMatchShapeless($player, $recipe, $tooMany, $wrongIngredients);

		return [
			["shapeless_any_order", $ok === true and count($ingredients) === 2, "任意顺序 2 个小麦"],
			["shapeless_extra_item", $wrong === false, "多出材料不匹配"],
		];
	}

	private static function testModernPlayerNoOps() : array{
		$player = self::playerAt(70);
		$hand = Item::get(Item::STONE, 0, 1);

		return [
			["noop_anvil", self::invokePrivate($player, "handleProtocol011AnvilInteraction", [new Anvil(), $hand]) === false, "非 0.11 返回 false"],
			["noop_enchant", self::invokePrivate($player, "handleProtocol011EnchantingTableInteraction", [new EnchantingTable(), $hand]) === false, "非 0.11 返回 false"],
			["noop_direct_food", self::invokePrivate($player, "shouldLegacy011UseDirectFoodConsume", [Item::get(Item::APPLE, 0, 1)]) === false, "0.14 不直接进食"],
			["noop_consume_food", self::invokePrivate($player, "consumeProtocol011FoodInHand", [Item::get(Item::APPLE, 0, 1)]) === false, "0.14 不拦截 UseItem"],
		];
	}

	private static function carrotRecipe() : ShapedRecipeFromJson{
		$recipe = new ShapedRecipeFromJson(Item::get(Item::CARROT_ON_A_STICK, 0, 1), 1, 1);
		$recipe->addIngredient(0, 0, Item::get(Item::STICK, 0, 1));
		$recipe->setId(UUID::fromRandom());

		return $recipe;
	}

	private static function emptyGrid() : array{
		$grid = [];
		for($i = 0; $i < 9; ++$i){
			$grid[$i] = Item::get(Item::AIR, 0, 0);
		}

		return $grid;
	}

	private static function playerAt(int $protocol) : Protocol011GameplayTestPlayer{
		$ref = new \ReflectionClass(Protocol011GameplayTestPlayer::class);
		/** @var Protocol011GameplayTestPlayer $player */
		$player = $ref->newInstanceWithoutConstructor();

		$prop = new \ReflectionProperty(Player::class, "protocol");
		$prop->setAccessible(true);
		$prop->setValue($player, $protocol);

		return $player;
	}

	private static function invokePrivate(Player $player, string $method, array $args = []){
		$ref = new \ReflectionMethod(Player::class, $method);
		$ref->setAccessible(true);

		return $ref->invokeArgs($player, $args);
	}

	private static function invokeClone(Player $player, $recipe, bool &$modified){
		$ref = new \ReflectionMethod(Player::class, "cloneProtocol013RecipeWithId");
		$ref->setAccessible(true);

		$args = [$recipe, &$modified];

		return $ref->invokeArgs($player, $args);
	}

	private static function invokeMatchShaped(Player $player, ShapedRecipeFromJson $recipe, array $grid, array &$ingredients, bool $columnMajor = false) : bool{
		$ref = new \ReflectionMethod(Player::class, "matchProtocol011ShapedCraftingRecipe");
		$ref->setAccessible(true);

		$args = [$recipe, $grid, &$ingredients, $columnMajor];

		return (bool) $ref->invokeArgs($player, $args);
	}

	private static function invokeMatchShapeless(Player $player, ShapelessRecipe $recipe, array $grid, array &$ingredients) : bool{
		$ref = new \ReflectionMethod(Player::class, "matchProtocol011ShapelessCraftingRecipe");
		$ref->setAccessible(true);

		$args = [$recipe, $grid, &$ingredients];

		return (bool) $ref->invokeArgs($player, $args);
	}
}
