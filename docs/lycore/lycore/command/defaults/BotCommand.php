<?php

namespace lycore\command\defaults;

use lycore\block\Block;
use lycore\command\Command;
use lycore\command\CommandSender;
use lycore\entity\Entity;
use lycore\entity\Bot;
use lycore\entity\BotTypeManager;
use lycore\level\Level;
use lycore\level\Position;
use lycore\math\Vector3;
use lycore\nbt\tag\CompoundTag;
use lycore\Player;
use lycore\utils\TextFormat;

class BotCommand extends VanillaCommand{
	/** @var BotTypeManager[] */
	private static $pvpBotTypeManagers = [];
	private static $walkAreaSelections = [];
	private static $respawnSelections = [];

	public function __construct($name){
		parent::__construct(
			$name,
			"生成和管理 AI 机器人",
			"§e§o§l/bot add pvpbot1-6 [x y z] [world]\n§e§o§l/bot add <type> [x y z] [world]\n§e§o§l/bot create <type>\§e§o§ln/bot del <type>\n§e§o§l/bot edit <type> [setting true|false|name <displayName>|bag <copy|clear|add>|walk <true|false|random|area>|respawn <true|false>]\n§e§o§l/bot list [world]\n§e§o§l/bot look <id>\n§e§o§l/bot wand\n§e§o§l/bot display\n/bot tp <id>\n§e§o§l/bot tphere <id>\n§e§o§l/bot remove <id|all|worldall>\n§e§o§l/bot help"
		);
		$this->setPermission("pocketmine.command.bot");
	}

	public static function parseBotAddArguments(array $args) : array{
		if(count($args) !== 2 and count($args) !== 5 and count($args) !== 6){
			throw new \InvalidArgumentException("用法：/bot add pvpbot1-6 [x y z] [world]");
		}
		if(strtolower((string) $args[0]) !== "add" or trim((string) $args[1]) === ""){
			throw new \InvalidArgumentException("用法：/bot add <type> <x> <y> <z> <world>");
		}

		$typeName = (string) $args[1];
		if(strtolower($typeName) === "pvpbot"){
			$difficulty = 1;
		}elseif(preg_match('/^pvpbot([1-6])$/i', $typeName, $matches) === 1){
			$typeName = "pvpbot";
			$difficulty = (int) $matches[1];
		}elseif(preg_match('/^pvpbot\\d+$/i', $typeName) === 1){
			throw new \InvalidArgumentException("PVP机器人难度必须为§o§4 1、2、3、4、5 或 6");
		}else{
			$difficulty = 1;
		}

		return [
			"type" => $typeName,
			"difficulty" => $difficulty,
			"hasPosition" => count($args) >= 5,
			"coords" => count($args) >= 5 ? [$args[2], $args[3], $args[4]] : null,
			"world" => count($args) === 6 ? (string) $args[5] : null,
		];
	}

	public static function resolvePVPBotSpawnPosition(Level $level, Vector3 $pos){
		$safe = $level->getSafeSpawn($pos);
		if(!($safe instanceof Vector3)){
			$safe = $pos;
		}

		$standing = self::findPVPBotStandingPosition($level, $pos);
		if($standing instanceof Position){
			return $standing;
		}

		$standing = self::findPVPBotStandingPosition($level, $safe);
		return $standing instanceof Position ? $standing : $safe;
	}

	private static function findPVPBotStandingPosition(Level $level, Vector3 $pos){
		$blockLookup = new \ReflectionMethod($level, "getBlockIdAt");
		if($level->getProvider() === null and $blockLookup->getDeclaringClass()->getName() === Level::class){
			return null;
		}

		$x = (int) floor($pos->x);
		$z = (int) floor($pos->z);
		$startY = max(1, min(Level::Y_MAX - 2, (int) floor($pos->y)));
		$groundPassable = self::isPVPBotPassableBlock($level, $x, $startY - 1, $z);
		$feetPassable = self::isPVPBotPassableBlock($level, $x, $startY, $z);
		$headPassable = self::isPVPBotPassableBlock($level, $x, $startY + 1, $z);

		if($groundPassable === false and $feetPassable === true and $headPassable === true){
			return new Position($pos->x, $startY, $pos->z, $level);
		}

		$searchUpFirst = $feetPassable !== true or $headPassable !== true;
		$standing = $searchUpFirst ? self::searchPVPBotStandingPositionUp($level, $pos, $x, $startY, $z) : self::searchPVPBotStandingPositionDown($level, $pos, $x, $startY, $z);
		if($standing instanceof Position){
			return $standing;
		}

		return $searchUpFirst ? self::searchPVPBotStandingPositionDown($level, $pos, $x, $startY, $z) : self::searchPVPBotStandingPositionUp($level, $pos, $x, $startY, $z);
	}

	private static function searchPVPBotStandingPositionUp(Level $level, Vector3 $pos, int $x, int $startY, int $z){
		for($y = $startY; $y <= Level::Y_MAX - 2; ++$y){
			if(self::canPVPBotStandAt($level, $x, $y, $z)){
				return new Position($pos->x, $y, $pos->z, $level);
			}
		}

		return null;
	}

	private static function searchPVPBotStandingPositionDown(Level $level, Vector3 $pos, int $x, int $startY, int $z){
		for($y = $startY - 1; $y >= 1; --$y){
			if(self::canPVPBotStandAt($level, $x, $y, $z)){
				return new Position($pos->x, $y, $pos->z, $level);
			}
		}

		return null;
	}

	private static function canPVPBotStandAt(Level $level, int $x, int $y, int $z) : bool{
		$groundPassable = self::isPVPBotPassableBlock($level, $x, $y - 1, $z);
		$feetPassable = self::isPVPBotPassableBlock($level, $x, $y, $z);
		$headPassable = self::isPVPBotPassableBlock($level, $x, $y + 1, $z);

		return $groundPassable === false and $feetPassable === true and $headPassable === true;
	}

	private static function isPVPBotPassableBlock(Level $level, int $x, int $y, int $z){
		if($y < 0 or $y >= Level::Y_MAX){
			return false;
		}

		try{
			$id = $level->getBlockIdAt($x, $y, $z);
			$data = method_exists($level, "getBlockDataAt") ? $level->getBlockDataAt($x, $y, $z) : 0;
		}catch(\Throwable $e){
			return null;
		}

		return Block::get($id, $data)->canPassThrough();
	}

	public static function createPVPBotNBT(Vector3 $pos, int $difficulty) : CompoundTag{
		return Bot::createNBT($pos->x, $pos->y, $pos->z, $difficulty);
	}

	public static function spawnPVPBot(Level $level, Vector3 $pos, int $difficulty, BotTypeManager $typeManager = null, string $typeName = "pvpbot"){
		$chunk = $level->getChunk(((int) floor($pos->x)) >> 4, ((int) floor($pos->z)) >> 4, true);
		if($chunk === null){
			return null;
		}

		Entity::registerEntity(Bot::class, true);
		$entity = Entity::createEntity("Bot", $chunk, self::createPVPBotNBT($pos, $difficulty));
		if($entity instanceof Bot and $typeManager instanceof BotTypeManager and strtolower($typeName) !== "pvpbot"){
			$type = $typeManager->getType($typeName);
			if($type !== null){
				$entity->applyPVPBotType($typeName, $type, $typeManager->getEquipment($typeName), $typeManager->loadSkinData($type["skin"]));
			}
		}
		return $entity;
	}

	public function execute(CommandSender $sender, $currentAlias, array $args){
		if(!$this->testPermission($sender)){
			return true;
		}
		$subCommand = isset($args[0]) ? strtolower((string) $args[0]) : "";
		if($subCommand === "list"){
			return $this->handlePVPBotListCommand($sender, array_slice($args, 1));
		}
		if($subCommand === "create"){
			return $this->handlePVPBotCreateCommand($sender, array_slice($args, 1));
		}
		if($subCommand === "del"){
			return $this->handlePVPBotDeleteTypeCommand($sender, array_slice($args, 1));
		}
		if($subCommand === "edit"){
			return $this->handlePVPBotEditCommand($sender, array_slice($args, 1));
		}
		if($subCommand === "look"){
			return $this->handlePVPBotLookCommand($sender, array_slice($args, 1));
		}
		if($subCommand === "wand"){
			return $this->handlePVPBotWandCommand($sender, array_slice($args, 1));
		}
		if($subCommand === "display"){
			return $this->handlePVPBotDisplayCommand($sender, array_slice($args, 1));
		}
		if($subCommand === "tp" or $subCommand === "tphere"){
			return $this->handlePVPBotTeleportCommand($sender, array_slice($args, 1), $subCommand === "tp");
		}
		if($subCommand === "remove"){
			return $this->handlePVPBotRemoveCommand($sender, array_slice($args, 1));
		}
		if($subCommand === "help"){
			$this->sendPVPBotHelp($sender);
			return true;
		}

		try{
			$parsed = self::parseBotAddArguments($args);
		}catch(\InvalidArgumentException $e){
			$sender->sendMessage(TextFormat::RED . $this->getUsage());
			return true;
		}

		$server = $sender->getServer();
		$level = $this->resolvePVPBotLevel($sender, $parsed["world"]);
		if(!($level instanceof Level)){
			$sender->sendMessage(TextFormat::RED . "机器人生成世界未加载");
			return true;
		}

		try{
			if($parsed["hasPosition"]){
				$base = $this->getCommandPositionBase($sender, $level->getSafeSpawn());
				$rawPosition = $this->getRelativeVector($base, $sender, $parsed["coords"]);
			}elseif($sender instanceof Player){
				$rawPosition = new Vector3($sender->x, $sender->y, $sender->z);
			}else{
				$rawPosition = $this->getCommandPositionBase($sender, $level->getSafeSpawn());
			}
		}catch(\InvalidArgumentException $e){
			$sender->sendMessage(TextFormat::RED . $this->getUsage());
			return true;
		}

		$typeManager = $this->getBotTypeManager($sender);
		$typeName = $parsed["type"];
		$type = strtolower($typeName) === "pvpbot" ? ["type" => "pvpbot"] : $typeManager->getType($typeName);
		if($type === null or strtolower($type["type"]) !== "pvpbot"){
			$sender->sendMessage(TextFormat::RED . "机器人类型 " . $typeName . " 不存在或不受支持");
			return true;
		}

		$position = self::resolvePVPBotSpawnPosition($level, $rawPosition);
		$entity = self::spawnPVPBot($level, $position, $parsed["difficulty"], $typeManager, $typeName);
		if($entity instanceof Entity){
			$entity->spawnToAll();
			Command::broadcastCommandMessage($sender, "已在 " . round($position->x, 2) . ", " . round($position->y, 2) . ", " . round($position->z, 2) . " 生成难度 " . $parsed["difficulty"] . " 的 PVP机器人");
			return true;
		}

		$sender->sendMessage(TextFormat::RED . "PVP机器人生成失败");
		return true;
	}

	private function handlePVPBotCreateCommand(CommandSender $sender, array $args) : bool{
		if(count($args) !== 1){
			$sender->sendMessage(TextFormat::RED . "用法：/bot create <type>");
			return true;
		}

		$name = (string) $args[0];
		if(!$this->getBotTypeManager($sender)->createType($name)){
			$sender->sendMessage(TextFormat::RED . "机器人类型已存在或名称无效");
			return true;
		}

		$sender->sendMessage("已创建机器人类型 " . $name);
		return true;
	}

	private function handlePVPBotDeleteTypeCommand(CommandSender $sender, array $args) : bool{
		if(count($args) !== 1){
			$sender->sendMessage(TextFormat::RED . "用法：/bot del <type>");
			return true;
		}

		$name = (string) $args[0];
		if(!$this->getBotTypeManager($sender)->deleteType($name)){
			$sender->sendMessage(TextFormat::RED . "机器人类型 " . $name . " 不存在");
			return true;
		}

		$sender->sendMessage("已删除机器人类型 " . $name);
		return true;
	}

	private function handlePVPBotEditCommand(CommandSender $sender, array $args) : bool{
		if(count($args) < 1){
			$sender->sendMessage(TextFormat::RED . "用法：/bot edit <type> [setting true|false|name <displayName>|bag <copy|clear|add>|walk <true|false|random|area>|respawn <true|false>]");
			return true;
		}

		$manager = $this->getBotTypeManager($sender);
		$name = (string) $args[0];
		$type = $manager->getType($name);
		if($type === null){
			$sender->sendMessage(TextFormat::RED . "机器人类型 " . $name . " 不存在");
			return true;
		}

		if(count($args) === 1){
			$lines = ["机器人类型 " . $name . "（PVP机器人）"];
			$lines[] = " - 显示名称：" . $type["name"];
			foreach(BotTypeManager::SETTINGS as $setting){
				$lines[] = " - " . self::getPVPBotSettingDisplayName($setting) . "：" . ($type["settings"][$setting] ? "开启" : "关闭");
			}
			$lines[] = " - 背包操作：bag <copy|clear|add>";
			$lines[] = " - 移动：walk " . ($type["walk"]["enabled"] ? "开启" : "关闭") . "，模式：" . ($type["walk"]["mode"] === "area" ? "区域" : "自由");
			$lines[] = " - 重生：" . ($type["respawn"]["enabled"] ? "开启，等待 " . $type["respawn"]["delay"] . " 秒" : "关闭");
			$sender->sendMessage(implode("\n", $lines));
			return true;
		}

		$operation = strtolower((string) $args[1]);
		if($operation === "name"){
			$displayName = trim(implode(" ", array_slice($args, 2)));
			if($displayName === "" or strlen($displayName) > 64){
				$sender->sendMessage(TextFormat::RED . "用法：/bot edit <type> name <displayName>（最长 64 个字符）");
				return true;
			}
			if(!$manager->setName($name, $displayName)){
				$sender->sendMessage(TextFormat::RED . "机器人类型 " . $name . " 不存在");
				return true;
			}
			Bot::refreshPVPBotTypeInstances($sender->getServer(), $manager, $name);
			$sender->sendMessage("机器人类型 " . $name . " 的显示名称已设为 " . $displayName);
			return true;
		}

		if($operation === "walk"){
			if(count($args) !== 3 or !($sender instanceof Player) or !$sender->isOp()){
				$sender->sendMessage(TextFormat::RED . "用法：/bot edit <type> walk <true|false|random|area>");
				return true;
			}
			$value = strtolower((string) $args[2]);
			if($value === "true" or $value === "false"){
				if(!$manager->setWalkEnabled($name, $value === "true")){
					$sender->sendMessage(TextFormat::RED . "机器人类型 " . $name . " 不存在");
					return true;
				}
				Bot::refreshPVPBotTypeInstances($sender->getServer(), $manager, $name);
				$sender->sendMessage("机器人类型 " . $name . " 的移动已" . ($value === "true" ? "开启" : "关闭"));
				return true;
			}
			if($value === "random"){
				if(!$manager->setWalkMode($name, "random")){
					$sender->sendMessage(TextFormat::RED . "机器人类型 " . $name . " 不存在");
					return true;
				}
				Bot::refreshPVPBotTypeInstances($sender->getServer(), $manager, $name);
				$sender->sendMessage("机器人类型 " . $name . " 的移动模式已设为自由移动");
				return true;
			}
			if($value === "area"){
				unset(self::$respawnSelections[strtolower($sender->getName())]);
				self::$walkAreaSelections[strtolower($sender->getName())] = ["type" => $name, "step" => 1];
				$sender->sendMessage("请破坏区域第一个点的方块");
				return true;
			}
			$sender->sendMessage(TextFormat::RED . "用法：/bot edit <type> walk <true|false|random|area>");
			return true;
		}

		if($operation === "respawn"){
			if(count($args) !== 3 or !($sender instanceof Player) or !$sender->isOp() or ($args[2] !== "true" and $args[2] !== "false")){
				$sender->sendMessage(TextFormat::RED . "用法：/bot edit <type> respawn <true|false>");
				return true;
			}
			$key = strtolower($sender->getName());
			if($args[2] === "false"){
				unset(self::$respawnSelections[$key]);
				if(!$manager->setRespawnEnabled($name, false)){
					$sender->sendMessage(TextFormat::RED . "机器人类型 " . $name . " 的重生尚未配置");
					return true;
				}
				Bot::refreshPVPBotTypeInstances($sender->getServer(), $manager, $name);
				$sender->sendMessage("机器人类型 " . $name . " 的重生已关闭");
				return true;
			}
			unset(self::$walkAreaSelections[$key]);
			self::$respawnSelections[$key] = ["type" => $name, "step" => "point"];
			$sender->sendMessage("请破坏一个方块以标记机器人重生点");
			return true;
		}

		if($operation === "bag"){
			if(count($args) !== 3 or !($sender instanceof Player) or !$sender->isOp()){
				$sender->sendMessage(TextFormat::RED . "仅 OP 玩家可使用 /bot edit <type> bag <copy|clear|add>");
				return true;
			}

			$bagOperation = strtolower((string) $args[2]);
			$inventory = $sender->getInventory();
			if($bagOperation === "copy"){
				$contents = [];
				for($slot = 0; $slot < $inventory->getSize(); ++$slot){
					$contents[$slot] = $inventory->getItem($slot);
				}
				if(!$manager->setBagItems($name, $contents)){
					$sender->sendMessage(TextFormat::RED . "机器人类型 " . $name . " 不存在");
					return true;
				}
				Bot::refreshPVPBotTypeInstances($sender->getServer(), $manager, $name);
				$sender->sendMessage("已用当前背包完整覆盖机器人类型 " . $name . " 的背包");
				return true;
			}
			if($bagOperation === "clear"){
				if(!$manager->setBagItems($name, [])){
					$sender->sendMessage(TextFormat::RED . "机器人类型 " . $name . " 不存在");
					return true;
				}
				Bot::refreshPVPBotTypeInstances($sender->getServer(), $manager, $name);
				$sender->sendMessage("机器人类型 " . $name . " 的背包已清空");
				return true;
			}
			if($bagOperation === "add"){
				$item = $inventory->getItemInHand();
				if(!$manager->addBagItem($name, $item)){
					$sender->sendMessage(TextFormat::RED . "请手持有效物品，且机器人背包需要有空位");
					return true;
				}
				Bot::refreshPVPBotTypeInstances($sender->getServer(), $manager, $name);
				$sender->sendMessage("手持物品已加入机器人类型 " . $name . " 的背包");
				return true;
			}

			$sender->sendMessage(TextFormat::RED . "用法：/bot edit <type> bag <copy|clear|add>");
			return true;
		}

		if(count($args) !== 3 or ($args[2] !== "true" and $args[2] !== "false")){
			$sender->sendMessage(TextFormat::RED . "用法：/bot edit <type> <setting> true|false");
			return true;
		}
		if(!$manager->setSetting($name, $operation, $args[2] === "true")){
			$sender->sendMessage(TextFormat::RED . "未知的机器人类型设置：" . $operation);
			return true;
		}

		Bot::refreshPVPBotTypeInstances($sender->getServer(), $manager, $name);
		$sender->sendMessage("机器人类型 " . $name . " 的设置“" . self::getPVPBotSettingDisplayName($operation) . "”已设为：" . ($args[2] === "true" ? "开启" : "关闭"));
		return true;
	}

	private function handlePVPBotDisplayCommand(CommandSender $sender, array $args) : bool{
		if(count($args) !== 0){
			$sender->sendMessage(TextFormat::RED . "用法：/bot display");
			return true;
		}
		if(!($sender instanceof Player) or !$sender->isOp()){
			$sender->sendMessage(TextFormat::RED . "此指令仅限 OP 玩家使用");
			return true;
		}

		$enabled = Bot::togglePVPBotManagementIdDisplay();
		foreach($sender->getServer()->getLevels() as $level){
			if(!($level instanceof Level) or $level->isClosed()){
				continue;
			}
			foreach(self::getPVPBotsInLevel($level) as $bot){
				$bot->refreshPVPBotManagementIdDisplay();
			}
		}

		$sender->sendMessage($enabled ? "机器人ID显示已开启" : "机器人ID显示已关闭");
		return true;
	}

	private function handlePVPBotTeleportCommand(CommandSender $sender, array $args, bool $toBot) : bool{
		$usage = $toBot ? "/bot tp <id>" : "/bot tphere <id>";
		if(count($args) !== 1 or !ctype_digit((string) $args[0]) or (int) $args[0] > Bot::PVPBOT_MANAGEMENT_ID_MAX){
			$sender->sendMessage(TextFormat::RED . "用法：" . $usage);
			return true;
		}
		if(!($sender instanceof Player)){
			$sender->sendMessage(TextFormat::RED . "此指令仅限玩家使用");
			return true;
		}

		$targetId = (int) $args[0];
		foreach($sender->getServer()->getLevels() as $level){
			if(!($level instanceof Level) or $level->isClosed()){
				continue;
			}
			foreach(self::getPVPBotsInLevel($level) as $bot){
				if($bot->getPVPBotManagementId() !== $targetId){
					continue;
				}

				if($toBot){
					$sender->teleport($bot);
					$sender->sendMessage("已传送到机器人 " . $targetId);
				}else{
					$bot->teleport($sender);
					$sender->sendMessage("机器人 " . $targetId . " 已传送到你身边");
				}
				return true;
			}
		}

		$sender->sendMessage(TextFormat::RED . "机器人 " . $targetId . " 不存在");
		return true;
	}

	private function handlePVPBotWandCommand(CommandSender $sender, array $args) : bool{
		if(count($args) !== 0){
			$sender->sendMessage(TextFormat::RED . "用法：/bot wand");
			return true;
		}
		if(!($sender instanceof Player)){
			$sender->sendMessage(TextFormat::RED . "此指令仅限玩家使用");
			return true;
		}

		$sender->getInventory()->addItem(Bot::createPVPBotInspectorWand());
		$sender->sendMessage("已获得机器人查阅器");
		return true;
	}

	private function handlePVPBotLookCommand(CommandSender $sender, array $args) : bool{
		if(count($args) !== 1 or !ctype_digit((string) $args[0]) or (int) $args[0] > Bot::PVPBOT_MANAGEMENT_ID_MAX){
			$sender->sendMessage(TextFormat::RED . "用法：/bot look <id>");
			return true;
		}

		$targetId = (int) $args[0];
		foreach($sender->getServer()->getLevels() as $level){
			if(!($level instanceof Level) or $level->isClosed()){
				continue;
			}
			foreach(self::getPVPBotsInLevel($level) as $bot){
				if($bot->getPVPBotManagementId() !== $targetId){
					continue;
				}

				$sender->sendMessage($bot->getPVPBotManagementInfo());
				return true;
			}
		}

		$sender->sendMessage(TextFormat::RED . "机器人 " . $targetId . " 不存在");
		return true;
	}

	private function handlePVPBotListCommand(CommandSender $sender, array $args) : bool{
		if(count($args) > 1){
			$sender->sendMessage(TextFormat::RED . "用法：/bot list [world]");
			return true;
		}

		$server = $sender->getServer();
		if(count($args) === 1){
			$level = $this->resolvePVPBotLevel($sender, (string) $args[0]);
			if(!($level instanceof Level)){
				$sender->sendMessage(TextFormat::RED . "世界 " . $args[0] . " 未加载");
				return true;
			}

			$lines = ["世界 " . $level->getName() . " 的机器人列表："];
			foreach(self::getPVPBotsInLevel($level) as $bot){
				$lines[] = " - " . TextFormat::clean($bot->getName()) . "（机器人ID：" . $bot->getPVPBotManagementId() . "）";
			}
			$sender->sendMessage(implode("\n", $lines));
			return true;
		}

		$lines = [];
		foreach($server->getLevels() as $level){
			if($level instanceof Level and !$level->isClosed()){
				$lines[] = TextFormat::GOLD . $level->getName() . TextFormat::YELLOW . "：" . TextFormat::DARK_GREEN . count(self::getPVPBotsInLevel($level));
			}
		}
		$lines[] = TextFormat::GRAY . "请输入 /bot list <world> 查看指定世界的机器人详情";
		$sender->sendMessage(implode("\n", $lines));
		return true;
	}

	private function handlePVPBotRemoveCommand(CommandSender $sender, array $args) : bool{
		if(count($args) !== 1){
			$sender->sendMessage(TextFormat::RED . "用法：/bot remove <id|all|worldall>");
			return true;
		}

		$target = (string) $args[0];
		$server = $sender->getServer();
		if(strtolower($target) === "all"){
			$level = $this->resolvePVPBotLevel($sender);
			if(!($level instanceof Level)){
				$sender->sendMessage(TextFormat::RED . "当前世界未加载");
				return true;
			}
			$count = self::removePVPBotsInLevel($level);
			$sender->sendMessage("当前世界已删除 " . $count . " 个机器人！");
			return true;
		}

		if(strtolower($target) === "worldall"){
			$count = 0;
			foreach($server->getLevels() as $level){
				if($level instanceof Level and !$level->isClosed()){
					$count += self::removePVPBotsInLevel($level);
				}
			}
			$sender->sendMessage("所有世界已删除 " . $count . " 个机器人！");
			return true;
		}

		if(!ctype_digit($target) or (int) $target > Bot::PVPBOT_MANAGEMENT_ID_MAX){
			$sender->sendMessage(TextFormat::RED . "机器人ID必须在 0 到 " . Bot::PVPBOT_MANAGEMENT_ID_MAX . " 之间");
			return true;
		}

		foreach($server->getLevels() as $level){
			if(!($level instanceof Level) or $level->isClosed()){
				continue;
			}
			foreach(self::getPVPBotsInLevel($level) as $bot){
				if($bot->getPVPBotManagementId() === (int) $target){
					$bot->close();
					$sender->sendMessage("机器人 " . $target . " 已删除！");
					return true;
				}
			}
		}

		$sender->sendMessage(TextFormat::RED . "机器人 " . $target . " 不存在");
		return true;
	}

	private function sendPVPBotHelp(CommandSender $sender){
		$sender->sendMessage(implode("\n", [
			"/bot add pvpbot1-6 [x y z] [world] - 生成 PVP机器人",
			"/bot add <type> [x y z] [world] - 生成自定义机器人类型",
			"/bot create <type> - 创建自定义机器人类型",
			"/bot del <type> - 删除自定义机器人类型",
			"/bot edit <type> [setting true|false|name <displayName>|bag <copy|clear|add>|walk <true|false|random|area>] - 配置机器人类型",
			"/bot list [world] - 查看所有世界或指定世界的机器人",
			"/bot look <id> - 查看指定机器人的信息",
			"/bot wand - 获取机器人查阅器",
			"/bot display - 开关 OP 可见的机器人ID标识",
			"/bot tp <id> - 传送到指定机器人",
			"/bot tphere <id> - 将指定机器人传送到自己身边",
			"/bot remove <id|all|worldall> - 删除指定机器人、当前世界机器人或所有世界机器人",
			"/bot help - 查看此帮助"
		]));
	}

	private static function getPVPBotSettingDisplayName(string $setting) : string{
		$names = [
			"nodrops" => "禁止掉落",
			"playerfriendly" => "对玩家友好",
			"mobfriendly" => "对怪物友好",
			"animalfriendly" => "对动物友好",
			"nobreak" => "禁止破坏",
			"noplace" => "禁止放置",
			"noconsume" => "禁止消耗",
			"noeat" => "禁止进食",
			"noshoot" => "禁止射击",
			"noteleport" => "禁止传送",
			"nopickup" => "禁止拾取",
			"nosplash" => "禁止喷溅药水"
		];
		return isset($names[$setting]) ? $names[$setting] : $setting;
	}

	public static function handleWalkAreaBlockBreak(Player $player, Vector3 $point) : bool{
		$key = strtolower($player->getName());
		if(!isset(self::$walkAreaSelections[$key])){
			return false;
		}
		if(self::$walkAreaSelections[$key]["step"] === 3){
			return self::handleWalkAreaClick($player, $point);
		}
		if(self::$walkAreaSelections[$key]["step"] > 2){
			return false;
		}
		$selection = self::$walkAreaSelections[$key];
		$selection["point" . $selection["step"]] = self::walkAreaPoint($player, $point);
		++$selection["step"];
		self::$walkAreaSelections[$key] = $selection;
		$player->sendMessage($selection["step"] === 2 ? "第一个点已选择，请破坏第二个点的方块" : "第二个点已选择，请破坏第三个回传点的方块");
		return true;
	}

	public static function handleRespawnPointBlockBreak(Player $player, Vector3 $point) : bool{
		$key = strtolower($player->getName());
		if(!isset(self::$respawnSelections[$key]) or self::$respawnSelections[$key]["step"] !== "point" or !$player->isOp()){
			return false;
		}
		self::$respawnSelections[$key]["point"] = self::walkAreaPoint($player, $point);
		self::$respawnSelections[$key]["step"] = "delay";
		$player->sendMessage("请在聊天栏输入重生等待时间（秒，可为 0）");
		return true;
	}

	public static function handleRespawnTimeChat(Player $player, string $message) : bool{
		$key = strtolower($player->getName());
		if(!isset(self::$respawnSelections[$key]) or self::$respawnSelections[$key]["step"] !== "delay" or !$player->isOp()){
			return false;
		}

		$delayText = trim($message);
		if(!ctype_digit($delayText) or (int) $delayText > 86400){
			$player->sendMessage(TextFormat::RED . "请输入 0 到 86400 的整数秒数");
			return true;
		}

		$selection = self::$respawnSelections[$key];
		$dataPath = $player->getServer()->getDataPath();
		$manager = isset(self::$pvpBotTypeManagers[$dataPath]) ? self::$pvpBotTypeManagers[$dataPath] : new BotTypeManager($dataPath);
		self::$pvpBotTypeManagers[$dataPath] = $manager;
		if(!$manager->setRespawn($selection["type"], $selection["point"], (int) $delayText)){
			$player->sendMessage(TextFormat::RED . "重生配置保存失败");
			return true;
		}

		unset(self::$respawnSelections[$key]);
		Bot::refreshPVPBotTypeInstances($player->getServer(), $manager, $selection["type"]);
		$player->sendMessage("机器人类型 " . $selection["type"] . " 的重生已开启，等待 " . (int) $delayText . " 秒");
		return true;
	}

	public static function handleWalkAreaClick(Player $player, Vector3 $point) : bool{
		$key = strtolower($player->getName());
		if(!isset(self::$walkAreaSelections[$key]) or self::$walkAreaSelections[$key]["step"] !== 3){
			return false;
		}
		$selection = self::$walkAreaSelections[$key];
		$dataPath = $player->getServer()->getDataPath();
		$manager = isset(self::$pvpBotTypeManagers[$dataPath]) ? self::$pvpBotTypeManagers[$dataPath] : new BotTypeManager($dataPath);
		self::$pvpBotTypeManagers[$dataPath] = $manager;
		unset(self::$walkAreaSelections[$key]);
		if($manager->setWalkArea($selection["type"], $selection["point1"], $selection["point2"], self::walkAreaPoint($player, $point))){
			Bot::refreshPVPBotTypeInstances($player->getServer(), $manager, $selection["type"]);
			$player->sendMessage("机器人移动区域已保存，越界时将安全传送回第三个点附近");
		}else{
			$player->sendMessage(TextFormat::RED . "移动区域保存失败，三个点必须在同一世界");
		}
		return true;
	}

	private static function walkAreaPoint(Player $player, Vector3 $point) : array{
		return ["world" => $player->getLevel()->getName(), "x" => (int) floor($point->x), "y" => (int) floor($point->y), "z" => (int) floor($point->z)];
	}

	private function getBotTypeManager(CommandSender $sender) : BotTypeManager{
		$server = $sender->getServer();
		$dataPath = method_exists($server, "getDataPath") ? $server->getDataPath() : getcwd() . DIRECTORY_SEPARATOR;
		if(!isset(self::$pvpBotTypeManagers[$dataPath])){
			self::$pvpBotTypeManagers[$dataPath] = new BotTypeManager($dataPath);
		}

		return self::$pvpBotTypeManagers[$dataPath];
	}

	private static function getPVPBotsInLevel(Level $level) : array{
		$bots = [];
		foreach($level->getEntities() as $entity){
			if($entity instanceof Bot and !$entity->closed){
				$bots[] = $entity;
			}
		}
		usort($bots, function(Bot $left, Bot $right){
			return $left->getPVPBotManagementId() <=> $right->getPVPBotManagementId();
		});
		return $bots;
	}

	private static function removePVPBotsInLevel(Level $level) : int{
		$bots = self::getPVPBotsInLevel($level);
		foreach($bots as $bot){
			$bot->close();
		}
		return count($bots);
	}

	private function resolvePVPBotLevel(CommandSender $sender, $world = null){
		$server = $sender->getServer();
		if($world !== null and $world !== ""){
			$level = $server->getLevelByName($world);
			if(!($level instanceof Level) and method_exists($server, "loadLevel")){
				$server->loadLevel($world);
				$level = $server->getLevelByName($world);
			}

			return $level;
		}

		if($sender instanceof Player){
			return $sender->getLevel();
		}

		return $server->getDefaultLevel();
	}
}
