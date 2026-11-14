<?php

/*
 * ██╗███╗   ██╗ ██████╗ ██████╗ ██████╗ ███████╗
 * ██║████╗  ██║██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║██╔██╗ ██║██║     ██║   ██║██████╔╝█████╗
 * ██║██║╚██╗██║██║     ██║   ██║██╔══██╗██╔══╝
 * ██║██║ ╚████║╚██████╗╚██████╔╝██║  ██║███████╗
 * ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: The_Bird, GoodCat456872
 *
 * @Links:
 *  > https://github.com/QwQ12222/InCore-II#incore-ii
 *  > https://gitcode.com/bad-cat-factory/InCore-II-InCore/tree/InCore-II
 *
 * Ported into Zenith from InCore-II and adapted to Zenith's Server/Level/Entity
 * API. Unsupported criteria (scoreboard scores, NBT tags, entity families)
 * throw a readable InvalidArgumentException instead of silently matching nothing.
 */

namespace pocketmine\command;

use pocketmine\entity\Entity;
use pocketmine\Player;

class TargetSelector{

	const ALL_PLAYERS = "a";
	const ALL_ENTITIES = "e";
	const NEAREST_PLAYER = "p";
	const RANDOM_PLAYER = "r";
	const SELF = "s";
	const INITIATOR = "initiator";

	/** @var bool[] 可识别的选择器参数（语法层面） */
	private static $knownArguments = [
		"x" => true,
		"y" => true,
		"z" => true,
		"dx" => true,
		"dy" => true,
		"dz" => true,
		"c" => true,
		"r" => true,
		"rm" => true,
		"name" => true,
		"tag" => true,
		"l" => true,
		"lm" => true,
		"m" => true,
		"type" => true,
		"family" => true,
		"rx" => true,
		"rxm" => true,
		"ry" => true,
		"rym" => true,
		"scores" => true
	];

	/**
	 * @var string[] 语法能解析、但 Zenith 数据模型没有对应实现的参数 => 原因
	 */
	private static $unsupportedArguments = [
		"scores" => "scoreboard objectives are not available in this server",
		"tag" => "entity tags are not available in this server",
		"family" => "entity families are not available in this server"
	];

	/** @var mixed[] 实体名 => 网络 ID（与 Zenith 的 NETWORK_ID 对照） */
	private static $entityTypeIds = [
		"minecraft:player" => "player",
		"player" => "player",
		"minecraft:chicken" => 10,
		"chicken" => 10,
		"minecraft:cow" => 11,
		"cow" => 11,
		"minecraft:pig" => 12,
		"pig" => 12,
		"minecraft:sheep" => 13,
		"sheep" => 13,
		"minecraft:wolf" => 14,
		"wolf" => 14,
		"minecraft:villager" => 15,
		"villager" => 15,
		"minecraft:mooshroom" => 16,
		"mooshroom" => 16,
		"minecraft:squid" => 17,
		"squid" => 17,
		"minecraft:rabbit" => 18,
		"rabbit" => 18,
		"minecraft:bat" => 19,
		"bat" => 19,
		"minecraft:iron_golem" => 20,
		"iron_golem" => 20,
		"irongolem" => 20,
		"minecraft:snow_golem" => 21,
		"snow_golem" => 21,
		"snowgolem" => 21,
		"minecraft:ocelot" => 22,
		"ocelot" => 22,
		"minecraft:horse" => 23,
		"horse" => 23,
		"minecraft:zombie" => 32,
		"zombie" => 32,
		"minecraft:creeper" => 33,
		"creeper" => 33,
		"minecraft:skeleton" => 34,
		"skeleton" => 34,
		"minecraft:spider" => 35,
		"spider" => 35,
		"minecraft:zombie_pigman" => 36,
		"zombie_pigman" => 36,
		"pig_zombie" => 36,
		"pigzombie" => 36,
		"minecraft:slime" => 37,
		"slime" => 37,
		"minecraft:enderman" => 38,
		"enderman" => 38,
		"minecraft:silverfish" => 39,
		"silverfish" => 39,
		"minecraft:cave_spider" => 40,
		"cave_spider" => 40,
		"cavespider" => 40,
		"minecraft:ghast" => 41,
		"ghast" => 41,
		"minecraft:magma_cube" => 42,
		"magma_cube" => 42,
		"lavaslime" => 42,
		"lava_slime" => 42,
		"minecraft:blaze" => 43,
		"blaze" => 43,
		"minecraft:zombie_villager" => 44,
		"zombie_villager" => 44,
		"zombievillager" => 44,
		"minecraft:witch" => 45,
		"witch" => 45,
		"minecraft:item" => 64,
		"item" => 64,
		"minecraft:tnt" => 65,
		"tnt" => 65,
		"primed_tnt" => 65,
		"minecraft:falling_block" => 66,
		"falling_block" => 66,
		"fallingsand" => 66,
		"minecraft:xp_orb" => 69,
		"xp_orb" => 69,
		"xporb" => 69,
		"minecraft:fishing_hook" => 77,
		"fishing_hook" => 77,
		"minecraft:arrow" => 80,
		"arrow" => 80,
		"minecraft:snowball" => 81,
		"snowball" => 81,
		"minecraft:egg" => 82,
		"egg" => 82,
		"minecraft:painting" => 83,
		"painting" => 83,
		"minecraft:minecart" => 84,
		"minecart" => 84,
		"minecraft:fireball" => 85,
		"fireball" => 85,
		"minecraft:thrown_potion" => 86,
		"thrown_potion" => 86,
		"potion" => 86,
		"minecraft:leash_knot" => 88,
		"leash_knot" => 88,
		"minecraft:boat" => 90,
		"boat" => 90,
		"minecraft:lightning_bolt" => 93,
		"lightning_bolt" => 93,
		"lightning" => 93,
		"minecraft:small_fireball" => 94,
		"small_fireball" => 94,
		"minecraft:hopper_minecart" => 96,
		"hopper_minecart" => 96,
		"minecart_hopper" => 96,
		"minecraft:tnt_minecart" => 97,
		"tnt_minecart" => 97,
		"minecart_tnt" => 97,
		"minecraft:chest_minecart" => 98,
		"chest_minecart" => 98,
		"minecart_chest" => 98
	];

	/** @var mixed Server 实例；测试环境可为 null */
	private $server;

	/**
	 * @param mixed $server
	 */
	public function __construct($server = null){
		$this->server = $server;
	}

	/**
	 * @param string $token
	 *
	 * @return bool
	 */
	public function isSelector($token){
		return self::isSelectorToken($token);
	}

	/**
	 * 语法层面判断是否是选择器 token。
	 *
	 * @param mixed $token
	 *
	 * @return bool
	 */
	public static function isSelectorToken($token){
		return is_string($token) && preg_match('/^@(?:[aeprs]|initiator)(?:\[.*\])?$/', $token) === 1;
	}

	/**
	 * 解析选择器 token（纯语法，不访问服务器）。
	 *
	 * @param string $token
	 *
	 * @throws \InvalidArgumentException
	 *
	 * @return array{type: string, arguments: array}
	 */
	public static function parseSelector($token){
		if(!is_string($token) || preg_match('/^@([aeprs]|initiator)(?:\[(.*)\])?$/', $token, $matches) !== 1){
			throw new \InvalidArgumentException("Malformed entity selector token");
		}

		$type = $matches[1];
		$arguments = self::parseArgumentMap(isset($matches[2]) ? $matches[2] : null);

		if(isset($arguments["family"]) && isset($arguments["type"])){
			throw new \InvalidArgumentException("Selector arguments 'type' and 'family' cannot be used together");
		}

		if(isset($arguments["scores"])){
			self::assertSingleArgument($arguments["scores"], "scores");
			self::parseScoreConditions($arguments["scores"][0]);
		}

		return ["type" => $type, "arguments" => $arguments];
	}

	/**
	 * 按顶层逗号切分参数，`{...}` 内部不切分。
	 *
	 * @param string $input
	 *
	 * @return string[]
	 */
	public static function splitArguments($input){
		$result = [];
		$depth = 0;
		$start = 0;
		$length = strlen($input);
		for($i = 0; $i < $length; ++$i){
			$char = $input[$i];
			if($char === "{"){
				++$depth;
			}elseif($char === "}" && $depth > 0){
				--$depth;
			}elseif($char === "," && $depth === 0){
				$part = substr($input, $start, $i - $start);
				if($part !== ""){
					$result[] = $part;
				}
				$start = $i + 1;
			}
		}

		if($start < $length){
			$part = substr($input, $start);
			if($part !== ""){
				$result[] = $part;
			}
		}

		return $result;
	}

	/**
	 * @param string|null $input
	 *
	 * @throws \InvalidArgumentException
	 *
	 * @return array<string, string[]>
	 */
	public static function parseArgumentMap($input){
		$args = [];
		if($input === null || $input === ""){
			return $args;
		}

		foreach(self::splitArguments($input) as $argument){
			$split = explode("=", $argument, 2);
			$name = $split[0];
			if(!isset(self::$knownArguments[$name])){
				throw new \InvalidArgumentException("Unknown selector argument: " . $name);
			}
			if(!isset($args[$name])){
				$args[$name] = [];
			}
			$args[$name][] = isset($split[1]) ? $split[1] : "";
		}

		return $args;
	}

	/**
	 * 返回在 Zenith 中无对应实现的参数（参数名 => 原因）。
	 *
	 * @param array $arguments
	 *
	 * @return string[]
	 */
	public static function unsupportedArguments(array $arguments){
		$result = [];
		foreach($arguments as $name => $values){
			if(isset(self::$unsupportedArguments[$name])){
				$result[$name] = self::$unsupportedArguments[$name];
			}
		}
		return $result;
	}

	/**
	 * @param string $token
	 * @param int    $default 无法判定默认值时的回退（通常是服务器默认模式）
	 *
	 * @throws \InvalidArgumentException
	 *
	 * @return int
	 */
	public static function parseGamemodeToken($token, $default = 0){
		switch(strtolower($token)){
			case "s":
			case "survival":
			case "0":
				return 0;
			case "c":
			case "creative":
			case "1":
				return 1;
			case "a":
			case "adventure":
			case "2":
				return 2;
			case "sp":
			case "spectator":
			case "3":
				return 3;
			case "d":
			case "default":
				return (int) $default;
		}
		throw new \InvalidArgumentException("Unknown gamemode token: " . $token);
	}

	/**
	 * 解析 scores={obj=1..5,obj2=!3} 的语法（Zenith 无计分板，匹配阶段会报不支持）。
	 *
	 * @param string $input
	 *
	 * @throws \InvalidArgumentException
	 *
	 * @return array<string, array{condition: string, negated: bool}>
	 */
	public static function parseScoreConditions($input){
		$input = trim($input);
		if(strlen($input) < 2 || $input[0] !== "{" || substr($input, -1) !== "}"){
			throw new \InvalidArgumentException("Malformed scores selector argument");
		}

		$body = substr($input, 1, -1);
		$result = [];
		if($body === ""){
			return $result;
		}

		foreach(explode(",", $body) as $entry){
			$split = explode("=", $entry, 2);
			if(count($split) !== 2 || $split[0] === "" || $split[1] === ""){
				throw new \InvalidArgumentException("Malformed scores selector argument");
			}
			$condition = $split[1];
			$negated = strlen($condition) > 0 && $condition[0] === "!";
			if($negated){
				$condition = substr($condition, 1);
			}
			if($condition === ""){
				throw new \InvalidArgumentException("Malformed scores selector argument");
			}
			$result[$split[0]] = ["condition" => $condition, "negated" => $negated];
		}

		return $result;
	}

	/**
	 * 解析坐标参数，支持 `~`、`~5`、`~-5` 和绝对坐标。
	 *
	 * @param string $token
	 * @param float  $base
	 *
	 * @return float
	 */
	public static function parseCoordinateValue($token, $base){
		$token = (string) $token;
		if(strlen($token) > 0 && $token[0] === "~"){
			return strlen($token) === 1 ? (double) $base : (double) $base + (double) substr($token, 1);
		}
		return (double) $token;
	}

	/**
	 * @param mixed $token
	 *
	 * @return bool
	 */
	public static function isNegatedValue($token){
		return is_string($token) && strlen($token) > 0 && $token[0] === "!";
	}

	/**
	 * 把选择器解析成实体列表；解析失败或无匹配都返回空数组，语法错误抛异常。
	 *
	 * @param mixed  $sender
	 * @param string $token
	 *
	 * @throws \InvalidArgumentException
	 *
	 * @return Entity[]
	 */
	public function matchEntities($sender, $token){
		$parsed = self::parseSelector($token);
		$type = $parsed["type"];
		$arguments = $parsed["arguments"];

		$unsupported = self::unsupportedArguments($arguments);
		if(count($unsupported) > 0){
			$names = array_keys($unsupported);
			throw new \InvalidArgumentException("Selector argument '" . $names[0] . "' is not supported: " . $unsupported[$names[0]]);
		}

		if($type === self::INITIATOR){
			throw new \InvalidArgumentException("Selector argument '@initiator' is not supported: commands have no initiator entity in this server");
		}

		$base = $this->getBasePosition($sender);
		$entities = $this->getInitialEntities($sender, $type, $base);
		if(count($entities) === 0){
			return [];
		}

		$this->applyCoordinateArguments($arguments, $base);
		$entities = $this->applyBoxArguments($entities, $arguments, $base);
		$entities = $this->applyRadiusArguments($entities, $arguments, $base);
		$entities = $this->applyRotationArguments($entities, $arguments);
		$entities = $this->applyNameArguments($entities, $arguments);
		$entities = $this->applyLevelArguments($entities, $arguments);
		$entities = $this->applyGamemodeArguments($entities, $arguments);
		$entities = $this->applyTypeArguments($entities, $arguments, $type);
		$entities = $this->applyCountArgument($entities, $arguments, $base);

		if($type === self::RANDOM_PLAYER && count($entities) > 1){
			return [$entities[array_rand($entities)]];
		}

		if($type === self::NEAREST_PLAYER && count($entities) > 1){
			return [$this->getNearestEntity($entities, $base)];
		}

		return array_values($entities);
	}

	/**
	 * @param mixed  $sender
	 * @param string $type
	 * @param object $base
	 *
	 * @return Entity[]
	 */
	private function getInitialEntities($sender, $type, $base){
		if($type === self::SELF){
			return $this->isEntityLike($sender) ? [$sender] : [];
		}

		if($type === self::ALL_PLAYERS || $type === self::NEAREST_PLAYER){
			if(is_object($base->level) && method_exists($base->level, "getPlayers")){
				return array_values($base->level->getPlayers());
			}
			return $this->getOnlinePlayers();
		}

		if(is_object($base->level) && method_exists($base->level, "getEntities")){
			return array_values($base->level->getEntities());
		}

		return $this->getServerEntities();
	}

	/**
	 * @return Player[]
	 */
	private function getOnlinePlayers(){
		if(is_object($this->server) && method_exists($this->server, "getOnlinePlayers")){
			return array_values($this->server->getOnlinePlayers());
		}
		return [];
	}

	/**
	 * @return Entity[]
	 */
	private function getServerEntities(){
		$entities = [];
		if(is_object($this->server) && method_exists($this->server, "getLevels")){
			foreach($this->server->getLevels() as $level){
				if(is_object($level) && method_exists($level, "getEntities")){
					foreach($level->getEntities() as $entity){
						$entities[] = $entity;
					}
				}
			}
		}
		return $entities;
	}

	/**
	 * 选择器的基准坐标：发送者位置；控制台回退到默认世界出生点。
	 *
	 * @param mixed $sender
	 *
	 * @return object
	 */
	private function getBasePosition($sender){
		$base = new \stdClass();
		$base->x = $this->readNumber($sender, "x", 0);
		$base->y = $this->readNumber($sender, "y", 0);
		$base->z = $this->readNumber($sender, "z", 0);
		$base->yaw = $this->readNumber($sender, "yaw", 0);
		$base->pitch = $this->readNumber($sender, "pitch", 0);
		$base->level = $this->getEntityLevel($sender);

		if($base->level === null && is_object($this->server) && method_exists($this->server, "getDefaultLevel")){
			$base->level = $this->server->getDefaultLevel();
			if(is_object($base->level) && method_exists($base->level, "getSafeSpawn")){
				$spawn = $base->level->getSafeSpawn();
				$base->x = $this->readNumber($spawn, "x", $base->x);
				$base->y = $this->readNumber($spawn, "y", $base->y);
				$base->z = $this->readNumber($spawn, "z", $base->z);
			}
		}

		return $base;
	}

	/**
	 * @param array  $arguments
	 * @param object $base
	 */
	private function applyCoordinateArguments(array $arguments, $base){
		foreach(["x", "y", "z"] as $axis){
			if(isset($arguments[$axis])){
				$this->singleArgument($arguments[$axis], $axis);
				$this->cannotReverse($arguments[$axis][0]);
				$base->{$axis} = self::parseCoordinateValue($arguments[$axis][0], $base->{$axis});
			}
		}
	}

	/**
	 * @param array  $entities
	 * @param array  $arguments
	 * @param object $base
	 *
	 * @return Entity[]
	 */
	private function applyBoxArguments(array $entities, array $arguments, $base){
		if(!isset($arguments["dx"]) && !isset($arguments["dy"]) && !isset($arguments["dz"])){
			return $entities;
		}

		$dx = $this->readBoxArgument($arguments, "dx");
		$dy = $this->readBoxArgument($arguments, "dy");
		$dz = $this->readBoxArgument($arguments, "dz");

		return array_values(array_filter($entities, function($entity) use ($base, $dx, $dy, $dz){
			return $this->between($base->x, $base->x + $dx, $this->readNumber($entity, "x", 0))
				&& $this->between($base->y, $base->y + $dy, $this->readNumber($entity, "y", 0))
				&& $this->between($base->z, $base->z + $dz, $this->readNumber($entity, "z", 0));
		}));
	}

	/**
	 * @param array  $arguments
	 * @param string $key
	 *
	 * @return float
	 */
	private function readBoxArgument(array $arguments, $key){
		if(!isset($arguments[$key])){
			return 0.0;
		}
		$this->singleArgument($arguments[$key], $key);
		$this->cannotReverse($arguments[$key][0]);
		return (double) $arguments[$key][0];
	}

	/**
	 * @param array  $entities
	 * @param array  $arguments
	 * @param object $base
	 *
	 * @return Entity[]
	 */
	private function applyRadiusArguments(array $entities, array $arguments, $base){
		if(isset($arguments["r"])){
			$this->singleArgument($arguments["r"], "r");
			$this->cannotReverse($arguments["r"][0]);
			$r = (double) $arguments["r"][0];
			$entities = array_values(array_filter($entities, function($entity) use ($base, $r){
				return $this->distanceSquared($entity, $base) < ($r * $r);
			}));
		}

		if(isset($arguments["rm"])){
			$this->singleArgument($arguments["rm"], "rm");
			$this->cannotReverse($arguments["rm"][0]);
			$rm = (double) $arguments["rm"][0];
			$entities = array_values(array_filter($entities, function($entity) use ($base, $rm){
				return $this->distanceSquared($entity, $base) > ($rm * $rm);
			}));
		}

		return $entities;
	}

	/**
	 * @param array $entities
	 * @param array $arguments
	 *
	 * @return Entity[]
	 */
	private function applyRotationArguments(array $entities, array $arguments){
		$checks = [
			"rx" => [-90, 90, "pitch", "<="],
			"rxm" => [-90, 90, "pitch", ">="],
			"ry" => [-180, 180, "yaw", "<="],
			"rym" => [-180, 180, "yaw", ">="]
		];

		foreach($checks as $key => $check){
			if(!isset($arguments[$key])){
				continue;
			}
			$this->singleArgument($arguments[$key], $key);
			$this->cannotReverse($arguments[$key][0]);
			$value = (double) $arguments[$key][0];
			if(!$this->between($check[0], $check[1], $value)){
				throw new \InvalidArgumentException($key . " out of bounds");
			}
			$axis = $check[2];
			$operator = $check[3];
			$entities = array_values(array_filter($entities, function($entity) use ($axis, $operator, $value){
				$current = $axis === "yaw" ? $this->normalizeYaw($this->readRotation($entity, $axis)) : $this->readRotation($entity, $axis);
				return $operator === "<=" ? $current <= $value : $current >= $value;
			}));
		}

		return $entities;
	}

	/**
	 * @param array $entities
	 * @param array $arguments
	 *
	 * @return Entity[]
	 */
	private function applyNameArguments(array $entities, array $arguments){
		if(!isset($arguments["name"])){
			return $entities;
		}

		$have = [];
		$dontHave = [];
		foreach($arguments["name"] as $name){
			if(self::isNegatedValue($name)){
				$dontHave[] = substr($name, 1);
			}else{
				$have[] = $name;
			}
		}

		return array_values(array_filter($entities, function($entity) use ($have, $dontHave){
			$name = $this->getEntityName($entity);
			foreach($have as $required){
				if($name !== $required){
					return false;
				}
			}
			foreach($dontHave as $blocked){
				if($name === $blocked){
					return false;
				}
			}
			return true;
		}));
	}

	/**
	 * @param array $entities
	 * @param array $arguments
	 *
	 * @return Entity[]
	 */
	private function applyLevelArguments(array $entities, array $arguments){
		if(isset($arguments["l"])){
			$this->singleArgument($arguments["l"], "l");
			$this->cannotReverse($arguments["l"][0]);
			$max = (int) $arguments["l"][0];
			$entities = array_values(array_filter($entities, function($entity) use ($max){
				return $this->isPlayer($entity) && $this->getExperienceLevel($entity) <= $max;
			}));
		}

		if(isset($arguments["lm"])){
			$this->singleArgument($arguments["lm"], "lm");
			$this->cannotReverse($arguments["lm"][0]);
			$min = (int) $arguments["lm"][0];
			$entities = array_values(array_filter($entities, function($entity) use ($min){
				return $this->isPlayer($entity) && $this->getExperienceLevel($entity) >= $min;
			}));
		}

		return $entities;
	}

	/**
	 * @param array $entities
	 * @param array $arguments
	 *
	 * @return Entity[]
	 */
	private function applyGamemodeArguments(array $entities, array $arguments){
		if(!isset($arguments["m"])){
			return $entities;
		}

		$this->singleArgument($arguments["m"], "m");
		$value = $arguments["m"][0];
		$reversed = self::isNegatedValue($value);
		if($reversed){
			$value = substr($value, 1);
		}
		$gamemode = $this->parseGamemode($value);

		return array_values(array_filter($entities, function($entity) use ($reversed, $gamemode){
			return $this->isPlayer($entity) && ($reversed !== ($this->getGamemode($entity) === $gamemode));
		}));
	}

	/**
	 * @param array  $entities
	 * @param array  $arguments
	 * @param string $selectorType
	 *
	 * @return Entity[]
	 */
	private function applyTypeArguments(array $entities, array $arguments, $selectorType){
		if(!isset($arguments["type"]) && $selectorType === self::RANDOM_PLAYER){
			$arguments["type"] = ["player"];
		}
		if(!isset($arguments["type"])){
			return $entities;
		}

		$have = [];
		$dontHave = [];
		foreach($arguments["type"] as $type){
			$reversed = self::isNegatedValue($type);
			$type = $this->normalizeTypeName($reversed ? substr($type, 1) : $type);
			if($reversed){
				$dontHave[] = $type;
			}else{
				$have[] = $type;
			}
		}

		return array_values(array_filter($entities, function($entity) use ($have, $dontHave){
			foreach($have as $type){
				if(!$this->entityIsType($entity, $type)){
					return false;
				}
			}
			foreach($dontHave as $type){
				if($this->entityIsType($entity, $type)){
					return false;
				}
			}
			return true;
		}));
	}

	/**
	 * @param array  $entities
	 * @param array  $arguments
	 * @param object $base
	 *
	 * @return Entity[]
	 */
	private function applyCountArgument(array $entities, array $arguments, $base){
		if(!isset($arguments["c"])){
			return array_values($entities);
		}

		$this->singleArgument($arguments["c"], "c");
		$this->cannotReverse($arguments["c"][0]);
		$count = (int) $arguments["c"][0];
		if($count === 0){
			throw new \InvalidArgumentException("Selector argument c cannot be zero");
		}

		usort($entities, function($a, $b) use ($base){
			$distanceA = $this->distanceSquared($a, $base);
			$distanceB = $this->distanceSquared($b, $base);
			if($distanceA == $distanceB){
				return 0;
			}
			return $distanceA < $distanceB ? -1 : 1;
		});

		if($count < 0){
			$entities = array_reverse($entities);
		}

		return array_slice($entities, 0, abs($count));
	}

	/**
	 * @param Entity[] $entities
	 * @param object   $base
	 *
	 * @return Entity|null
	 */
	private function getNearestEntity(array $entities, $base){
		$nearest = null;
		$min = PHP_FLOAT_MAX;
		foreach($entities as $entity){
			$distance = $this->distanceSquared($entity, $base);
			if($distance < $min){
				$min = $distance;
				$nearest = $entity;
			}
		}
		return $nearest;
	}

	/**
	 * @param mixed $entity
	 *
	 * @return string
	 */
	private function getEntityName($entity){
		if($entity instanceof Player){
			return $entity->getName();
		}
		if(is_object($entity) && method_exists($entity, "getNameTag")){
			return (string) $entity->getNameTag();
		}
		if(is_object($entity) && method_exists($entity, "getName")){
			return (string) $entity->getName();
		}
		return "";
	}

	/**
	 * @param mixed $entity
	 *
	 * @return \pocketmine\level\Level|null
	 */
	private function getEntityLevel($entity){
		if(is_object($entity) && method_exists($entity, "getLevel")){
			return $entity->getLevel();
		}
		return null;
	}

	/**
	 * 距离平方：基准点是 stdClass，不能借用 Vector3::distanceSquared()。
	 *
	 * @param mixed  $entity
	 * @param object $base
	 *
	 * @return float
	 */
	private function distanceSquared($entity, $base){
		$x = $this->readNumber($entity, "x", 0);
		$y = $this->readNumber($entity, "y", 0);
		$z = $this->readNumber($entity, "z", 0);
		return pow($x - $base->x, 2) + pow($y - $base->y, 2) + pow($z - $base->z, 2);
	}

	/**
	 * @param mixed  $object
	 * @param string $property
	 * @param mixed  $default
	 *
	 * @return float
	 */
	private function readNumber($object, $property, $default){
		if(is_object($object)){
			$getter = "get" . ucfirst($property);
			if(method_exists($object, $getter)){
				return (double) $object->{$getter}();
			}
			if(property_exists($object, $property)){
				return (double) $object->{$property};
			}
		}
		return (double) $default;
	}

	/**
	 * @param mixed  $entity
	 * @param string $axis
	 *
	 * @return float
	 */
	private function readRotation($entity, $axis){
		$getter = $axis === "yaw" ? "getYaw" : "getPitch";
		if(is_object($entity) && method_exists($entity, $getter)){
			return (double) $entity->{$getter}();
		}
		return $this->readNumber($entity, $axis, 0);
	}

	/**
	 * @param float $yaw
	 *
	 * @return float
	 */
	private function normalizeYaw($yaw){
		$value = fmod($yaw + 90, 360);
		if($value < 0){
			$value += 360;
		}
		return $value - 180;
	}

	/**
	 * @param mixed $entity
	 *
	 * @return bool
	 */
	private function isEntityLike($entity){
		return is_object($entity) && (property_exists($entity, "x") || method_exists($entity, "getLevel"));
	}

	/**
	 * @param mixed $entity
	 *
	 * @return bool
	 */
	private function isPlayer($entity){
		return $entity instanceof Player;
	}

	/**
	 * @param mixed $entity
	 *
	 * @return int
	 */
	private function getGamemode($entity){
		return is_object($entity) && method_exists($entity, "getGamemode") ? (int) $entity->getGamemode() : -1;
	}

	/**
	 * @param mixed $entity
	 *
	 * @return int
	 */
	private function getExperienceLevel($entity){
		if(is_object($entity) && method_exists($entity, "getExpLevel")){
			return (int) $entity->getExpLevel();
		}
		return 0;
	}

	/**
	 * @param string $token
	 *
	 * @return int
	 */
	private function parseGamemode($token){
		$default = is_object($this->server) && method_exists($this->server, "getDefaultGamemode") ? (int) $this->server->getDefaultGamemode() : 0;
		return self::parseGamemodeToken($token, $default);
	}

	/**
	 * @param string $type
	 *
	 * @return string
	 */
	private function normalizeTypeName($type){
		$type = strtolower($type);
		if(strpos($type, ":") === false && isset(self::$entityTypeIds["minecraft:" . $type])){
			return "minecraft:" . $type;
		}
		return $type;
	}

	/**
	 * @param mixed  $entity
	 * @param string $type
	 *
	 * @return bool
	 */
	private function entityIsType($entity, $type){
		if($type === "minecraft:player" || $type === "player"){
			return $this->isPlayer($entity);
		}
		if($this->isPlayer($entity)){
			return false;
		}

		$plain = strpos($type, ":") !== false ? substr($type, strpos($type, ":") + 1) : $type;

		if(isset(self::$entityTypeIds[$type]) && is_int(self::$entityTypeIds[$type])){
			$networkId = self::getEntityNetworkId($entity);
			if($networkId !== null && $networkId === self::$entityTypeIds[$type]){
				return true;
			}
		}

		$class = is_object($entity) ? get_class($entity) : "";
		$position = strrpos($class, "\\");
		$shortName = strtolower($position === false ? $class : substr($class, $position + 1));

		return $shortName !== "" && str_replace("_", "", $shortName) === str_replace("_", "", strtolower($plain));
	}

	/**
	 * @param mixed $entity
	 *
	 * @return int|null
	 */
	private static function getEntityNetworkId($entity){
		if(!is_object($entity)){
			return null;
		}
		$class = get_class($entity);
		if(defined($class . "::NETWORK_ID")){
			$id = (int) constant($class . "::NETWORK_ID");
			return $id >= 0 ? $id : null;
		}
		return null;
	}

	/**
	 * @param array  $values
	 * @param string $key
	 */
	private function singleArgument(array $values, $key){
		self::assertSingleArgument($values, $key);
	}

	/**
	 * @param array  $values
	 * @param string $key
	 */
	private static function assertSingleArgument(array $values, $key){
		if(count($values) !== 1){
			throw new \InvalidArgumentException("Multiple arguments are not allowed for " . $key);
		}
	}

	/**
	 * @param string $value
	 */
	private function cannotReverse($value){
		if(self::isNegatedValue($value)){
			throw new \InvalidArgumentException("Argument cannot be reversed");
		}
	}

	/**
	 * @param float $a
	 * @param float $b
	 * @param float $value
	 *
	 * @return bool
	 */
	private function between($a, $b, $value){
		return $a < $b ? ($value >= $a && $value <= $b) : ($value >= $b && $value <= $a);
	}
}
