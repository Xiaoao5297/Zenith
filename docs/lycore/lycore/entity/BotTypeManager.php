<?php

namespace lycore\entity;

use lycore\item\Item;
use lycore\nbt\NBT;
use lycore\nbt\tag\CompoundTag;
use lycore\utils\Config;

class BotTypeManager{
	const DEFAULT_SKIN = "default.png";
	const SETTINGS = [
		"nodrops",
		"playerfriendly",
		"mobfriendly",
		"animalfriendly",
		"nobreak",
		"noplace",
		"noconsume",
		"noeat",
		"noshoot",
		"noteleport",
		"nopickup",
		"nosplash"
	];

	/** @var string */
	private $directory;
	/** @var string */
	private $skinDirectory;
	/** @var string */
	private $configPath;
	/** @var Config */
	private $config;
	/** @var string */
	private $revision = "";

	public function __construct(string $dataPath){
		$this->directory = rtrim($dataPath, "/\\") . DIRECTORY_SEPARATOR . "bot" . DIRECTORY_SEPARATOR;
		$this->skinDirectory = $this->directory . "skindata" . DIRECTORY_SEPARATOR;
		if(!is_dir($this->skinDirectory)){
			@mkdir($this->skinDirectory, 0777, true);
		}
		$readmePath = $this->directory . "README.md";
		if(!is_file($readmePath)){
			@copy(dirname(__DIR__) . DIRECTORY_SEPARATOR . "resources" . DIRECTORY_SEPARATOR . "README.md", $readmePath);
		}

		$this->configPath = $this->directory . "config.yml";
		$this->config = new Config($this->configPath, Config::YAML, [
			"version" => 1,
			"types" => []
		]);
		$this->revision = $this->readConfigRevision();
	}

	public function getDirectory() : string{
		return $this->directory;
	}

	public function getSkinDirectory() : string{
		return $this->skinDirectory;
	}

	public function loadSkinData(string $skin) : ?array{
		if($skin === self::DEFAULT_SKIN or $skin === "" or basename($skin) !== $skin){
			return null;
		}

		$extension = strtolower(pathinfo($skin, PATHINFO_EXTENSION));
		if($extension !== "png" and $extension !== "jpg" and $extension !== "jpeg"){
			return null;
		}

		$path = $this->skinDirectory . $skin;
		if(!is_file($path) or !function_exists("imagecreatefrompng") or !function_exists("imagecreatefromjpeg")){
			return null;
		}

		$image = $extension === "png" ? @imagecreatefrompng($path) : @imagecreatefromjpeg($path);
		if($image === false){
			return null;
		}

		$width = imagesx($image);
		$height = imagesy($image);
		if($width !== 64 or ($height !== 32 and $height !== 64)){
			imagedestroy($image);
			return null;
		}

		$data = "";
		for($y = 0; $y < 32; ++$y){
			for($x = 0; $x < 64; ++$x){
				$color = imagecolorat($image, $x, $y);
				$alpha = ($color >> 24) & 0x7f;
				$data .= chr(($color >> 16) & 0xff) . chr(($color >> 8) & 0xff) . chr($color & 0xff) . chr(255 - (int) round($alpha * 255 / 127));
			}
		}
		imagedestroy($image);

		return ["name" => $skin, "data" => $data];
	}

	public function getType(string $name) : ?array{
		$types = $this->getTypes();
		return isset($types[$name]) && is_array($types[$name]) ? $this->normalizeTypeConfig($types[$name]) : null;
	}

	public function reloadIfChanged() : bool{
		$revision = $this->readConfigRevision();
		if($revision === $this->revision){
			return false;
		}
		$this->config->reload();
		$this->revision = $this->readConfigRevision();
		return true;
	}

	public function getRevision() : string{
		$this->reloadIfChanged();
		return $this->revision;
	}

	public function getTypes() : array{
		$this->reloadIfChanged();
		$types = $this->config->get("types", []);
		return is_array($types) ? $types : [];
	}

	public function createType(string $name) : bool{
		if(!$this->isValidTypeName($name) or $this->getType($name) !== null){
			return false;
		}

		$types = $this->getTypes();
		$types[$name] = $this->defaultTypeConfig();
		$this->setTypes($types);
		return true;
	}

	public function deleteType(string $name) : bool{
		$types = $this->getTypes();
		if(!isset($types[$name])){
			return false;
		}

		unset($types[$name]);
		$this->setTypes($types);
		return true;
	}

	public function setSetting(string $name, string $setting, bool $value) : bool{
		if(!in_array($setting, self::SETTINGS, true)){
			return false;
		}

		$types = $this->getTypes();
		if(!isset($types[$name]) or !is_array($types[$name])){
			return false;
		}

		$types[$name] = $this->normalizeTypeConfig($types[$name]);
		$types[$name]["settings"][$setting] = $value;
		$this->setTypes($types);
		return true;
	}

	public function setName(string $name, string $displayName) : bool{
		$types = $this->getTypes();
		if(!isset($types[$name]) or !is_array($types[$name])){
			return false;
		}

		$types[$name] = $this->normalizeTypeConfig($types[$name]);
		$types[$name]["name"] = $displayName;
		$this->setTypes($types);
		return true;
	}

	public function setWalkEnabled(string $name, bool $enabled) : bool{
		return $this->updateWalkConfig($name, function(array $walk) use ($enabled){
			$walk["enabled"] = $enabled;
			return $walk;
		});
	}

	public function setWalkMode(string $name, string $mode) : bool{
		if($mode !== "random" and $mode !== "area"){
			return false;
		}
		return $this->updateWalkConfig($name, function(array $walk) use ($mode){
			$walk["mode"] = $mode;
			return $walk;
		});
	}

	public function setWalkArea(string $name, array $first, array $second, array $returnPoint) : bool{
		foreach([$first, $second, $returnPoint] as $point){
			if(!isset($point["world"], $point["x"], $point["y"], $point["z"]) or !is_string($point["world"]) or $point["world"] === ""){
				return false;
			}
		}
		if($first["world"] !== $second["world"] or $first["world"] !== $returnPoint["world"]){
			return false;
		}

		return $this->updateWalkConfig($name, function(array $walk) use ($first, $second, $returnPoint){
			$walk["mode"] = "area";
			$walk["area"] = [
				"world" => $first["world"],
				"min" => ["x" => min((int) $first["x"], (int) $second["x"]), "z" => min((int) $first["z"], (int) $second["z"])],
				"max" => ["x" => max((int) $first["x"], (int) $second["x"]), "z" => max((int) $first["z"], (int) $second["z"])],
				"return" => ["x" => (int) $returnPoint["x"], "y" => (int) $returnPoint["y"], "z" => (int) $returnPoint["z"]]
			];
			return $walk;
		});
	}

	public function setRespawn(string $name, array $point, int $delay) : bool{
		if($delay < 0 or $delay > 86400 or !isset($point["world"], $point["x"], $point["y"], $point["z"]) or !is_string($point["world"]) or $point["world"] === ""){
			return false;
		}

		$types = $this->getTypes();
		if(!isset($types[$name]) or !is_array($types[$name])){
			return false;
		}
		$types[$name] = $this->normalizeTypeConfig($types[$name]);
		$types[$name]["respawn"] = [
			"enabled" => true,
			"delay" => $delay,
			"point" => [
				"world" => $point["world"],
				"x" => (int) $point["x"],
				"y" => (int) $point["y"],
				"z" => (int) $point["z"]
			]
		];
		$this->setTypes($types);
		return true;
	}

	public function setRespawnEnabled(string $name, bool $enabled) : bool{
		$types = $this->getTypes();
		if(!isset($types[$name]) or !is_array($types[$name])){
			return false;
		}
		$types[$name] = $this->normalizeTypeConfig($types[$name]);
		if($enabled and $types[$name]["respawn"]["point"] === null){
			return false;
		}
		$types[$name]["respawn"]["enabled"] = $enabled;
		$this->setTypes($types);
		return true;
	}

	public function copyInventory(string $name, array $armor, array $inventory) : bool{
		$types = $this->getTypes();
		if(!isset($types[$name]) or !is_array($types[$name])){
			return false;
		}

		$types[$name] = $this->normalizeTypeConfig($types[$name]);
		$types[$name]["equipment"] = [
			"copied" => true,
			"armor_copied" => true,
			"armor" => $this->serializeItems($armor),
			"inventory" => $this->serializeItems($inventory)
		];
		$this->setTypes($types);
		return true;
	}

	public function setBagItems(string $name, array $inventory) : bool{
		$types = $this->getTypes();
		if(!isset($types[$name]) or !is_array($types[$name])){
			return false;
		}

		$types[$name] = $this->normalizeTypeConfig($types[$name]);
		$types[$name]["equipment"]["copied"] = true;
		$types[$name]["equipment"]["inventory"] = $this->serializeItems($inventory);
		$this->setTypes($types);
		return true;
	}

	public function addBagItem(string $name, Item $item) : bool{
		if($item->getId() === Item::AIR or $item->getCount() <= 0){
			return false;
		}

		$equipment = $this->getEquipment($name);
		if($equipment === null){
			return false;
		}
		$inventory = $equipment["inventory"];
		for($slot = 0; $slot < 36; ++$slot){
			if(!isset($inventory[$slot])){
				$inventory[$slot] = clone $item;
				return $this->setBagItems($name, $inventory);
			}
		}

		return false;
	}

	public function getEquipment(string $name) : array{
		$type = $this->getType($name);
		if($type === null){
			return ["copied" => false, "armor" => [], "inventory" => []];
		}

		$equipment = isset($type["equipment"]) && is_array($type["equipment"]) ? $type["equipment"] : [];
		return [
			"copied" => !empty($equipment["copied"]),
			"armor_copied" => !empty($equipment["armor_copied"]),
			"armor" => $this->deserializeItems(isset($equipment["armor"]) && is_array($equipment["armor"]) ? $equipment["armor"] : []),
			"inventory" => $this->deserializeItems(isset($equipment["inventory"]) && is_array($equipment["inventory"]) ? $equipment["inventory"] : [])
		];
	}

	public function defaultTypeConfig() : array{
		$settings = [];
		foreach(self::SETTINGS as $setting){
			$settings[$setting] = false;
		}

		return [
			"type" => "pvpbot",
			"name" => "",
			"skin" => self::DEFAULT_SKIN,
			"walk" => ["enabled" => true, "mode" => "random", "area" => null],
			"respawn" => ["enabled" => false, "delay" => 0, "point" => null],
			"settings" => $settings,
			"equipment" => [
				"copied" => false,
				"armor_copied" => false,
				"armor" => [],
				"inventory" => []
			]
		];
	}

	public function serializeItem(Item $item, int $slot) : array{
		$nbt = new NBT(NBT::LITTLE_ENDIAN);
		$nbt->setData(NBT::putItemHelper($item, $slot));
		$enchantments = [];
		foreach($item->getEnchantments() as $enchantment){
			$enchantments[] = [
				"id" => $enchantment->getId(),
				"level" => $enchantment->getLevel()
			];
		}
		return [
			"slot" => $slot,
			"id" => $item->getId(),
			"meta" => $item->getDamage(),
			"count" => $item->getCount(),
			"name" => $item->getName(),
			"custom_name" => $item->hasCustomName() ? $item->getCustomName() : "",
			"enchantments" => $enchantments,
			"nbt" => base64_encode($nbt->write())
		];
	}

	private function serializeItems(array $items) : array{
		$serialized = [];
		foreach($items as $slot => $item){
			if($item instanceof Item and $item->getId() !== Item::AIR and $item->getCount() > 0){
				$serialized[] = $this->serializeItem($item, (int) $slot);
			}
		}
		return $serialized;
	}

	private function deserializeItems(array $serialized) : array{
		$items = [];
		foreach($serialized as $entry){
			if(!is_array($entry) or !isset($entry["slot"])){
				continue;
			}
			$item = null;
			$raw = isset($entry["nbt"]) && is_string($entry["nbt"]) ? base64_decode($entry["nbt"], true) : false;
			try{
				if($raw !== false){
					$nbt = new NBT(NBT::LITTLE_ENDIAN);
					$nbt->read($raw);
					$tag = $nbt->getData();
					if($tag instanceof CompoundTag){
						$item = NBT::getItemHelper($tag);
					}
				}
				if(!($item instanceof Item) and isset($entry["id"])){
					$item = Item::get((int) $entry["id"], isset($entry["meta"]) ? (int) $entry["meta"] : 0, isset($entry["count"]) ? (int) $entry["count"] : 1);
				}
				if(!($item instanceof Item)){
					continue;
				}

				$meta = isset($entry["meta"]) ? (int) $entry["meta"] : $item->getDamage();
				$count = isset($entry["count"]) ? max(1, (int) $entry["count"]) : $item->getCount();
				if(isset($entry["id"]) and (int) $entry["id"] !== $item->getId()){
					$tag = $item->getNamedTag();
					$item = Item::get((int) $entry["id"], $meta, $count);
					if($tag instanceof CompoundTag){
						$item->setNamedTag($tag);
					}
				}else{
					$item->setDamage($meta);
					$item->setCount($count);
				}
				$items[(int) $entry["slot"]] = $item;
			}catch(\Throwable $e){
				continue;
			}
		}
		return $items;
	}

	private function normalizeTypeConfig(array $type) : array{
		$defaults = $this->defaultTypeConfig();
		$type["type"] = isset($type["type"]) && is_string($type["type"]) ? strtolower($type["type"]) : $defaults["type"];
		$type["name"] = isset($type["name"]) && is_string($type["name"]) ? $type["name"] : $defaults["name"];
		$type["skin"] = isset($type["skin"]) && is_string($type["skin"]) && trim($type["skin"]) !== "" ? $type["skin"] : $defaults["skin"];
		$type["walk"] = isset($type["walk"]) && is_array($type["walk"]) ? $type["walk"] : $defaults["walk"];
		$type["walk"]["enabled"] = isset($type["walk"]["enabled"]) ? (bool) $type["walk"]["enabled"] : true;
		$type["walk"]["mode"] = isset($type["walk"]["mode"]) && ($type["walk"]["mode"] === "area" or $type["walk"]["mode"] === "random") ? $type["walk"]["mode"] : "random";
		$type["walk"]["area"] = isset($type["walk"]["area"]) && is_array($type["walk"]["area"]) ? $type["walk"]["area"] : null;
		$type["respawn"] = isset($type["respawn"]) && is_array($type["respawn"]) ? $type["respawn"] : $defaults["respawn"];
		$respawnPoint = isset($type["respawn"]["point"]) && is_array($type["respawn"]["point"]) ? $type["respawn"]["point"] : null;
		if($respawnPoint === null or !isset($respawnPoint["world"], $respawnPoint["x"], $respawnPoint["y"], $respawnPoint["z"]) or !is_string($respawnPoint["world"]) or $respawnPoint["world"] === ""){
			$respawnPoint = null;
		}else{
			$respawnPoint = ["world" => $respawnPoint["world"], "x" => (int) $respawnPoint["x"], "y" => (int) $respawnPoint["y"], "z" => (int) $respawnPoint["z"]];
		}
		$type["respawn"]["point"] = $respawnPoint;
		$type["respawn"]["delay"] = max(0, min(86400, isset($type["respawn"]["delay"]) ? (int) $type["respawn"]["delay"] : 0));
		$type["respawn"]["enabled"] = !empty($type["respawn"]["enabled"]) && $respawnPoint !== null;
		$type["settings"] = isset($type["settings"]) && is_array($type["settings"]) ? $type["settings"] : [];
		foreach(self::SETTINGS as $setting){
			$type["settings"][$setting] = isset($type["settings"][$setting]) ? (bool) $type["settings"][$setting] : false;
		}
		$type["equipment"] = isset($type["equipment"]) && is_array($type["equipment"]) ? $type["equipment"] : $defaults["equipment"];
		$type["equipment"]["copied"] = !empty($type["equipment"]["copied"]);
		$type["equipment"]["armor_copied"] = isset($type["equipment"]["armor_copied"]) ? !empty($type["equipment"]["armor_copied"]) : $type["equipment"]["copied"];
		$type["equipment"]["armor"] = isset($type["equipment"]["armor"]) && is_array($type["equipment"]["armor"]) ? $type["equipment"]["armor"] : [];
		$type["equipment"]["inventory"] = isset($type["equipment"]["inventory"]) && is_array($type["equipment"]["inventory"]) ? $type["equipment"]["inventory"] : [];
		return $type;
	}

	private function setTypes(array $types){
		$this->config->set("types", $types);
		$this->config->save();
		$this->revision = $this->readConfigRevision();
	}

	private function readConfigRevision() : string{
		clearstatcache(true, $this->configPath);
		$hash = @sha1_file($this->configPath);
		return is_string($hash) ? $hash : "";
	}

	private function updateWalkConfig(string $name, callable $update) : bool{
		$types = $this->getTypes();
		if(!isset($types[$name]) or !is_array($types[$name])){
			return false;
		}
		$types[$name] = $this->normalizeTypeConfig($types[$name]);
		$types[$name]["walk"] = $update($types[$name]["walk"]);
		$this->setTypes($types);
		return true;
	}

	private function isValidTypeName(string $name) : bool{
		return preg_match('/^[\p{L}\p{N}_-]{1,32}$/u', $name) === 1;
	}
}
