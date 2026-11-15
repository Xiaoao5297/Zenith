<?php

/**
 * ConfigGroup 多文件配置组测试。
 *
 * 依赖说明: ConfigGroup 只依赖同命名空间的 Config (Config 只在保存失败时才碰
 * Server / MainLogger)，所以这里直接 require Utils.php + Config.php + ConfigGroup.php，
 * 用临时目录里的 YAML 文件跑，不启动 PocketMine 运行时。
 *
 * 覆盖: 跨文件 get/getNested、首文件优先、默认值、exists、按归属写回、
 * 未注册键抛异常、getAll 深合并、save 后重新载入、reload 感知外部改动。
 */
class ConfigGroupTest{

	/** @var string|null */
	private static $dir = null;

	/** @var int */
	private static $seq = 0;

	public static function run() : array{
		require_once \dirname(__DIR__) . "/src/pocketmine/utils/Utils.php";
		require_once \dirname(__DIR__) . "/src/pocketmine/utils/Config.php";
		require_once \dirname(__DIR__) . "/src/pocketmine/utils/ConfigGroup.php";

		self::$dir = \sys_get_temp_dir() . "/zenith-config-group-" . \getmypid();
		if(!\is_dir(self::$dir)){
			@\mkdir(self::$dir, 0777, true);
		}

		$results = \array_merge(
			self::testCrossFileGet(),
			self::testCrossFileGetNested(),
			self::testMissingDefaults(),
			self::testFirstFilePriority(),
			self::testExists(),
			self::testSetWritesOwnerOnly(),
			self::testSetNestedWritesOwner(),
			self::testUnmappedKeyThrows(),
			self::testGetAllDeepMerge(),
			self::testSaveReloadRoundTrip(),
			self::testReloadPicksUpExternalChange(),
			self::testOwnerConfig(),
			self::testListValuesPreserved()
		);

		self::cleanup();

		return $results;
	}

	/**
	 * 建一组临时配置并返回 [ConfigGroup, [名字 => Config], [名字 => 路径]]。
	 */
	private static function makeGroup(array $serverData, array $worldData, array $gameplayData = []) : array{
		$paths = [
			"server.yml" => self::$dir . "/server-" . (++self::$seq) . ".yml",
			"world.yml" => self::$dir . "/world-" . self::$seq . ".yml",
			"gameplay.yml" => self::$dir . "/gameplay-" . self::$seq . ".yml",
		];
		\file_put_contents($paths["server.yml"], \yaml_emit($serverData, YAML_UTF8_ENCODING));
		\file_put_contents($paths["world.yml"], \yaml_emit($worldData, YAML_UTF8_ENCODING));
		if(\count($gameplayData) === 0){
			$gameplayData = ["player" => ["hunger" => true]];
		}
		\file_put_contents($paths["gameplay.yml"], \yaml_emit($gameplayData, YAML_UTF8_ENCODING));

		$configs = [
			"server.yml" => new \pocketmine\utils\Config($paths["server.yml"], \pocketmine\utils\Config::YAML, []),
			"world.yml" => new \pocketmine\utils\Config($paths["world.yml"], \pocketmine\utils\Config::YAML, []),
			"gameplay.yml" => new \pocketmine\utils\Config($paths["gameplay.yml"], \pocketmine\utils\Config::YAML, []),
		];

		$group = new \pocketmine\utils\ConfigGroup([
			["config" => $configs["server.yml"], "keys" => ["settings", "memory"]],
			["config" => $configs["world.yml"], "keys" => ["level-settings", "nether", "world"]],
			["config" => $configs["gameplay.yml"], "keys" => ["player", "server"]],
		]);

		return [$group, $configs, $paths];
	}

	private static function baseServer() : array{
		return [
			"settings" => ["language" => "chs", "async-workers" => "auto"],
			"memory" => ["main-hard-limit" => 1024],
		];
	}

	private static function baseWorld() : array{
		return [
			"level-settings" => ["default-format" => "leveldb"],
			"nether" => ["enabled" => false, "level-name" => "nether"],
			"world" => ["no-crop-growth" => []],
		];
	}

	private static function testCrossFileGet() : array{
		list($group) = self::makeGroup(self::baseServer(), self::baseWorld());

		return [
			["cross_file_get_server", $group->get("settings") === ["language" => "chs", "async-workers" => "auto"], "settings 从 server.yml 读出"],
			["cross_file_get_world", $group->get("level-settings") === ["default-format" => "leveldb"], "level-settings 从 world.yml 读出"],
			["cross_file_get_gameplay", $group->get("player") === ["hunger" => true], "player 从 gameplay.yml 读出"],
		];
	}

	private static function testCrossFileGetNested() : array{
		list($group) = self::makeGroup(self::baseServer(), self::baseWorld());

		return [
			["nested_server", $group->getNested("settings.language") === "chs", "settings.language 跨文件解析"],
			["nested_world", $group->getNested("nether.level-name") === "nether", "nether.level-name 跨文件解析"],
			["nested_deep_default_missing", $group->getNested("world.do-mob-spawning") === null, "不存在的子键返回 null"],
		];
	}

	private static function testMissingDefaults() : array{
		list($group) = self::makeGroup(self::baseServer(), self::baseWorld());

		return [
			["missing_get_default", $group->get("nope", "fallback") === "fallback", "get() 支持默认值"],
			["missing_get_nested_default", $group->getNested("nope.deep", 42) === 42, "getNested() 支持默认值"],
			["missing_root_default", $group->getNested("settings.nope", false) === false, "首段存在但子键缺失时返回默认值"],
		];
	}

	private static function testFirstFilePriority() : array{
		$group = new \pocketmine\utils\ConfigGroup([
			["config" => new \pocketmine\utils\Config(self::$dir . "/prio-a-" . (++self::$seq) . ".yml", \pocketmine\utils\Config::YAML, ["shared" => ["value" => "first"]]), "keys" => ["shared"]],
			["config" => new \pocketmine\utils\Config(self::$dir . "/prio-b-" . self::$seq . ".yml", \pocketmine\utils\Config::YAML, ["shared" => ["value" => "second"]]), "keys" => ["other"]],
		]);

		return [
			["first_file_priority_get", $group->get("shared") === ["value" => "first"], "同名键取第一个文件"],
			["first_file_priority_nested", $group->getNested("shared.value") === "first", "嵌套值也取第一个文件"],
		];
	}

	private static function testExists() : array{
		list($group) = self::makeGroup(self::baseServer(), self::baseWorld());

		return [
			["exists_top", $group->exists("settings") === true, "顶层键存在"],
			["exists_nested", $group->exists("nether.enabled") === true, "嵌套键存在"],
			["exists_missing", $group->exists("aliases") === false, "未注册的键不存在"],
			["exists_missing_nested", $group->exists("nether.nope") === false, "缺失的嵌套键不存在"],
		];
	}

	private static function testSetWritesOwnerOnly() : array{
		list($group, $configs, $paths) = self::makeGroup(self::baseServer(), self::baseWorld());
		$worldBefore = \file_get_contents($paths["world.yml"]);
		// set() 与 Config::set() 一致，只写顶层键；嵌套路径请用 setNested()。
		$group->set("memory", ["main-hard-limit" => 2048]);
		$group->save(false);

		$serverReloaded = new \pocketmine\utils\Config($paths["server.yml"], \pocketmine\utils\Config::YAML, []);
		$worldReloaded = new \pocketmine\utils\Config($paths["world.yml"], \pocketmine\utils\Config::YAML, []);

		return [
			["set_owner_written", $serverReloaded->getNested("memory.main-hard-limit") === 2048, "memory 写入 server.yml"],
			["set_owner_settings_kept", $serverReloaded->getNested("settings.language") === "chs", "同文件其它键未受影响"],
			["set_non_owner_untouched", \file_get_contents($paths["world.yml"]) === $worldBefore and $worldReloaded->getNested("nether.level-name") === "nether", "非归属文件保持原样"],
		];
	}

	private static function testSetNestedWritesOwner() : array{
		list($group, $configs, $paths) = self::makeGroup(self::baseServer(), self::baseWorld());
		$group->setNested("nether.allow-nether", true);
		$group->setNested("world.gamerules.world.doMobSpawning", false);
		$group->save(false);

		$worldReloaded = new \pocketmine\utils\Config($paths["world.yml"], \pocketmine\utils\Config::YAML, []);

		return [
			["set_nested_written", $group->getNested("nether.allow-nether") === true, "内存中已写入"],
			["set_nested_persisted", $worldReloaded->getNested("nether.allow-nether") === true, "nether 写入 world.yml 并落盘"],
			["set_nested_deep_persisted", $worldReloaded->getNested("world.gamerules.world.doMobSpawning") === false, "多级路径写入 world.yml 并落盘"],
			["set_nested_other_file_untouched", $configs["server.yml"]->getNested("nether.allow-nether") === null, "server.yml 没有被写入"],
		];
	}

	private static function testUnmappedKeyThrows() : array{
		list($group) = self::makeGroup(self::baseServer(), self::baseWorld());
		$caught = false;
		try{
			$group->set("unknown.key", 1);
		}catch(\InvalidArgumentException $e){
			$caught = true;
		}

		$caughtNested = false;
		try{
			$group->setNested("unknown.deep.key", 1);
		}catch(\InvalidArgumentException $e){
			$caughtNested = true;
		}

		return [
			["unmapped_set_throws", $caught, "set() 对未注册首段键抛 InvalidArgumentException"],
			["unmapped_set_nested_throws", $caughtNested, "setNested() 对未注册首段键抛 InvalidArgumentException"],
		];
	}

	private static function testGetAllDeepMerge() : array{
		list($group) = self::makeGroup(self::baseServer(), self::baseWorld());

		$all = $group->getAll();

		return [
			["get_all_has_server", isset($all["settings"]["language"]) and $all["settings"]["language"] === "chs", "getAll 含 server.yml 的键"],
			["get_all_has_world", isset($all["level-settings"]["default-format"]) and $all["level-settings"]["default-format"] === "leveldb", "getAll 含 world.yml 的键"],
			["get_all_has_gameplay", isset($all["player"]["hunger"]) and $all["player"]["hunger"] === true, "getAll 含 gameplay.yml 的键"],
			["get_all_nested_merge", isset($all["nether"]["enabled"]) and $all["nether"]["enabled"] === false, "嵌套数组被深合并而不是整段覆盖"],
		];
	}

	private static function testSaveReloadRoundTrip() : array{
		list($group) = self::makeGroup(self::baseServer(), self::baseWorld());
		$group->setNested("settings.shutdown-message", "重启中");
		$group->setNested("world.no-tnt-block-damage", ["pk"]);
		$group->save(false);
		$group->reload();

		return [
			["roundtrip_server", $group->getNested("settings.shutdown-message") === "重启中", "save 后 reload 仍读到新值"],
			["roundtrip_world", $group->getNested("world.no-tnt-block-damage") === ["pk"], "嵌套列表 save 后 reload 一致"],
			["roundtrip_original_kept", $group->getNested("settings.language") === "chs", "未修改的键保持原值"],
		];
	}

	private static function testReloadPicksUpExternalChange() : array{
		list($group, $configs, $paths) = self::makeGroup(self::baseServer(), self::baseWorld());
		$changed = self::baseWorld();
		$changed["nether"]["allow-nether"] = true;
		\file_put_contents($paths["world.yml"], \yaml_emit($changed, YAML_UTF8_ENCODING));

		$before = $group->getNested("nether.allow-nether");
		$group->reload();
		$after = $group->getNested("nether.allow-nether");

		return [
			["external_change_before", $before === null, "reload 前读不到新增的键"],
			["external_change_after", $after === true, "reload 后读到外部改动"],
		];
	}

	private static function testOwnerConfig() : array{
		list($group, $configs) = self::makeGroup(self::baseServer(), self::baseWorld());

		return [
			["owner_config_world", $group->getOwnerConfig("world.gamerules") === $configs["world.yml"], "world.* 归属 world.yml"],
			["owner_config_server", $group->getOwnerConfig("settings.language") === $configs["server.yml"], "settings.* 归属 server.yml"],
			["owner_config_missing", $group->getOwnerConfig("unknown.key") === null, "未注册键返回 null"],
		];
	}

	private static function testListValuesPreserved() : array{
		list($group) = self::makeGroup(self::baseServer(), self::baseWorld());

		return [
			["empty_list_preserved", $group->getNested("world.no-crop-growth") === [], "空列表保持为数组"],
			["list_get_returns_array", \is_array($group->getNested("world.no-crop-growth")), "列表读出后类型是数组"],
		];
	}

	private static function cleanup(){
		if(self::$dir === null or !\is_dir(self::$dir)){
			return;
		}
		foreach((array) \glob(self::$dir . "/*") as $file){
			@\unlink($file);
		}
		@\rmdir(self::$dir);
	}

}
