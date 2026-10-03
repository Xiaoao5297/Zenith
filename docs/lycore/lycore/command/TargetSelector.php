<?php

namespace lycore\command;

class TargetSelector{

	const ALL_PLAYERS = "a";
	const ALL_ENTITIES = "e";
	const NEAREST_PLAYER = "p";
	const RANDOM_PLAYER = "r";
	const SELF = "s";
	const INITIATOR = "initiator";

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

	private $server;

	public function __construct($server){
		$this->server = $server;
	}

	public function isSelector($token){
		return is_string($token) && preg_match('/^@(?:[aeprs]|initiator)(?:\[.*\])?$/', $token) === 1;
	}

	public function matchEntities($sender, $token){
		if(preg_match('/^@([aeprs]|initiator)(?:\[(.*)\])?$/', $token, $matches) !== 1){
			throw new \InvalidArgumentException("Malformed entity selector token");
		}

		$type = $matches[1];
		$arguments = $this->parseArgumentMap(isset($matches[2]) ? $matches[2] : null);
		if(isset($arguments["family"]) && count($arguments["family"]) > 0 && isset($arguments["type"]) && count($arguments["type"]) > 0){
			throw new \InvalidArgumentException("Selector arguments 'type' and 'family' cannot be used together");
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
		$entities = $this->applyTagArguments($entities, $arguments);
		$entities = $this->applyFamilyArguments($entities, $arguments);
		$entities = $this->applyLevelArguments($entities, $arguments);
		$entities = $this->applyGamemodeArguments($entities, $arguments);
		$entities = $this->applyTypeArguments($entities, $arguments, $type);
		$entities = $this->applyScoresArguments($entities, $arguments);
		$entities = $this->applyCountArgument($entities, $arguments, $base);

		if($type === self::RANDOM_PLAYER && count($entities) > 1){
			return [$entities[array_rand($entities)]];
		}

		if($type === self::NEAREST_PLAYER && count($entities) > 1){
			return [$this->getNearestEntity($entities, $base)];
		}

		return array_values($entities);
	}

	private function parseArgumentMap($input){
		$args = [];
		if($input === null || $input === ""){
			return $args;
		}

		foreach($this->separateArguments($input) as $argument){
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

	private function separateArguments($input){
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

	private function getInitialEntities($sender, $type, $base){
		if($type === self::SELF){
			return $this->isEntityLike($sender) ? [$sender] : [];
		}

		if($type === self::INITIATOR){
			if(is_object($sender) && method_exists($sender, "getInitiator")){
				$initiator = $sender->getInitiator();
				return $initiator !== null ? [$initiator] : [];
			}
			return [];
		}

		if($type === self::ALL_PLAYERS || $type === self::NEAREST_PLAYER){
			if($base->level !== null && is_object($base->level) && method_exists($base->level, "getPlayers")){
				return array_values($base->level->getPlayers());
			}
			return array_values($this->getOnlinePlayers());
		}

		if($base->level !== null && is_object($base->level) && method_exists($base->level, "getEntities")){
			return array_values($base->level->getEntities());
		}

		return $this->getServerEntities();
	}

	private function getOnlinePlayers(){
		if(is_object($this->server) && method_exists($this->server, "getOnlinePlayers")){
			return array_values($this->server->getOnlinePlayers());
		}
		return [];
	}

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
			if($base->level !== null && method_exists($base->level, "getSafeSpawn")){
				$spawn = $base->level->getSafeSpawn();
				$base->x = $this->readNumber($spawn, "x", $base->x);
				$base->y = $this->readNumber($spawn, "y", $base->y);
				$base->z = $this->readNumber($spawn, "z", $base->z);
			}
		}

		return $base;
	}

	private function applyCoordinateArguments(array $arguments, $base){
		foreach(["x", "y", "z"] as $axis){
			if(isset($arguments[$axis])){
				$this->singleArgument($arguments[$axis], $axis);
				$this->cannotReverse($arguments[$axis][0]);
				$base->{$axis} = $this->parseOffsetDouble($arguments[$axis][0], $base->{$axis});
			}
		}
	}

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

	private function readBoxArgument(array $arguments, $key){
		if(!isset($arguments[$key])){
			return 0.0;
		}
		$this->singleArgument($arguments[$key], $key);
		$this->cannotReverse($arguments[$key][0]);
		return (double) $arguments[$key][0];
	}

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

	private function applyNameArguments(array $entities, array $arguments){
		if(!isset($arguments["name"])){
			return $entities;
		}

		$have = [];
		$dontHave = [];
		foreach($arguments["name"] as $name){
			if(strlen($name) > 0 && $name[0] === "!"){
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

	private function applyTagArguments(array $entities, array $arguments){
		if(!isset($arguments["tag"])){
			return $entities;
		}

		return array_values(array_filter($entities, function($entity) use ($arguments){
			foreach($arguments["tag"] as $tag){
				$reversed = strlen($tag) > 0 && $tag[0] === "!";
				$name = $reversed ? substr($tag, 1) : $tag;
				$has = $this->entityHasTag($entity, $name);
				if($reversed ? $has : !$has){
					return false;
				}
			}
			return true;
		}));
	}

	private function applyFamilyArguments(array $entities, array $arguments){
		if(!isset($arguments["family"])){
			return $entities;
		}

		return array_values(array_filter($entities, function($entity) use ($arguments){
			foreach($arguments["family"] as $family){
				$reversed = strlen($family) > 0 && $family[0] === "!";
				$name = $reversed ? substr($family, 1) : $family;
				$has = $this->entityHasFamily($entity, $name);
				if($reversed ? $has : !$has){
					return false;
				}
			}
			return true;
		}));
	}

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

	private function applyGamemodeArguments(array $entities, array $arguments){
		if(!isset($arguments["m"])){
			return $entities;
		}

		$this->singleArgument($arguments["m"], "m");
		$value = $arguments["m"][0];
		$reversed = strlen($value) > 0 && $value[0] === "!";
		if($reversed){
			$value = substr($value, 1);
		}
		$gamemode = $this->parseGamemode($value);

		return array_values(array_filter($entities, function($entity) use ($reversed, $gamemode){
			return $this->isPlayer($entity) && ($reversed !== ($this->getGamemode($entity) === $gamemode));
		}));
	}

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
			$reversed = strlen($type) > 0 && $type[0] === "!";
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

	private function applyScoresArguments(array $entities, array $arguments){
		if(!isset($arguments["scores"])){
			return $entities;
		}

		$this->singleArgument($arguments["scores"], "scores");
		$scores = $this->parseScores($arguments["scores"][0]);
		if(count($scores) === 0){
			return $entities;
		}

		return array_values(array_filter($entities, function($entity) use ($scores){
			foreach($scores as $objective => $range){
				$score = $this->getEntityScore($entity, $objective);
				if($score === null || !$this->scoreInRange($score, $range)){
					return false;
				}
			}
			return true;
		}));
	}

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

	private function parseScores($input){
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
			if(count($split) !== 2){
				throw new \InvalidArgumentException("Malformed scores selector argument");
			}
			$condition = $split[1];
			$reversed = strlen($condition) > 0 && $condition[0] === "!";
			if($reversed){
				$condition = substr($condition, 1);
			}
			$result[$split[0]] = ["condition" => $condition, "reversed" => $reversed];
		}

		return $result;
	}

	private function scoreInRange($score, $range){
		$condition = is_array($range) ? $range["condition"] : $range;
		$reversed = is_array($range) && isset($range["reversed"]) ? $range["reversed"] : false;
		if(strpos($condition, "..") !== false){
			$parts = explode("..", $condition, 2);
			$min = $parts[0] === "" ? null : (int) $parts[0];
			$max = $parts[1] === "" ? null : (int) $parts[1];
			$matches = ($min === null || $score >= $min) && ($max === null || $score <= $max);
		}else{
			$matches = $score === (int) $condition;
		}
		return $reversed ? !$matches : $matches;
	}

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

	private function getEntityName($entity){
		if(is_object($entity) && method_exists($entity, "getName")){
			return $entity->getName();
		}
		return "";
	}

	private function getEntityLevel($entity){
		if(is_object($entity) && method_exists($entity, "getLevel")){
			return $entity->getLevel();
		}
		if(is_object($entity) && property_exists($entity, "level")){
			return $entity->level;
		}
		return null;
	}

	private function distanceSquared($entity, $base){
		if(is_object($entity) && method_exists($entity, "distanceSquared")){
			return $entity->distanceSquared($base);
		}
		$x = $this->readNumber($entity, "x", 0);
		$y = $this->readNumber($entity, "y", 0);
		$z = $this->readNumber($entity, "z", 0);
		return pow($x - $base->x, 2) + pow($y - $base->y, 2) + pow($z - $base->z, 2);
	}

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

	private function readRotation($entity, $axis){
		$getter = $axis === "yaw" ? "getYaw" : "getPitch";
		if(is_object($entity) && method_exists($entity, $getter)){
			return (double) $entity->{$getter}();
		}
		return $this->readNumber($entity, $axis, 0);
	}

	private function normalizeYaw($yaw){
		$value = fmod($yaw + 90, 360);
		if($value < 0){
			$value += 360;
		}
		return $value - 180;
	}

	private function isEntityLike($entity){
		return is_object($entity) && (property_exists($entity, "x") || method_exists($entity, "getLevel"));
	}

	private function isPlayer($entity){
		if(class_exists("lycore\\Player", false) && $entity instanceof \lycore\Player){
			return true;
		}
		if(is_object($entity) && method_exists($entity, "getGamemode") && method_exists($entity, "getServer")){
			return true;
		}
		return false;
	}

	private function getGamemode($entity){
		return is_object($entity) && method_exists($entity, "getGamemode") ? (int) $entity->getGamemode() : -1;
	}

	private function getExperienceLevel($entity){
		foreach(["getExpLevel", "getXpLevel", "getExperienceLevel"] as $method){
			if(is_object($entity) && method_exists($entity, $method)){
				return (int) $entity->{$method}();
			}
		}
		return 0;
	}

	private function parseGamemode($token){
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
			case "spectator":
			case "3":
				return 3;
			case "d":
			case "default":
				return is_object($this->server) && method_exists($this->server, "getDefaultGamemode") ? (int) $this->server->getDefaultGamemode() : 0;
		}
		throw new \InvalidArgumentException("Unknown gamemode token: " . $token);
	}

	private function normalizeTypeName($type){
		$type = strtolower($type);
		if(strpos($type, ":") === false && isset(self::$entityTypeIds["minecraft:" . $type])){
			return "minecraft:" . $type;
		}
		return $type;
	}

	private function entityIsType($entity, $type){
		if($type === "minecraft:player" || $type === "player"){
			return $this->isPlayer($entity);
		}
		if($this->isPlayer($entity)){
			return false;
		}

		$id = isset(self::$entityTypeIds[$type]) ? self::$entityTypeIds[$type] : null;
		if($id !== null && $this->getEntityNetworkId($entity) === $id){
			return true;
		}

		$saveId = "";
		if(is_object($entity) && method_exists($entity, "getSaveId")){
			$saveId = strtolower((string) $entity->getSaveId());
		}elseif(is_object($entity)){
			$class = get_class($entity);
			$saveId = strtolower(substr($class, strrpos($class, "\\") === false ? 0 : strrpos($class, "\\") + 1));
		}

		$plain = strpos($type, ":") !== false ? substr($type, strpos($type, ":") + 1) : $type;
		return $saveId === strtolower($plain);
	}

	private function getEntityNetworkId($entity){
		if(is_object($entity) && method_exists($entity, "getNetworkId")){
			return (int) $entity->getNetworkId();
		}
		if(is_object($entity)){
			$class = get_class($entity);
			if(defined($class . "::NETWORK_ID")){
				return (int) constant($class . "::NETWORK_ID");
			}
		}
		return null;
	}

	private function entityHasTag($entity, $tag){
		if(is_object($entity) && method_exists($entity, "hasTag")){
			return (bool) $entity->hasTag($tag);
		}
		if(is_object($entity) && method_exists($entity, "getTags")){
			return in_array($tag, $entity->getTags(), true);
		}
		if(is_object($entity) && property_exists($entity, "namedtag") && is_object($entity->namedtag) && isset($entity->namedtag->Tags)){
			foreach($entity->namedtag->Tags as $entry){
				if((string) $entry === $tag){
					return true;
				}
			}
		}
		return false;
	}

	private function entityHasFamily($entity, $family){
		if(is_object($entity) && method_exists($entity, "hasFamily")){
			return (bool) $entity->hasFamily($family);
		}
		if(is_object($entity) && method_exists($entity, "getFamilies")){
			return in_array($family, $entity->getFamilies(), true);
		}
		return false;
	}

	private function getEntityScore($entity, $objective){
		if(is_object($entity) && method_exists($entity, "getScore")){
			$score = $entity->getScore($objective);
			return $score === null ? null : (int) $score;
		}
		if(is_object($entity) && method_exists($entity, "getScoreTag")){
			$score = $entity->getScoreTag($objective);
			return $score === null ? null : (int) $score;
		}
		return null;
	}

	private function parseOffsetDouble($value, $base){
		if(strlen($value) > 0 && $value[0] === "~"){
			return strlen($value) === 1 ? $base : $base + (double) substr($value, 1);
		}
		return (double) $value;
	}

	private function singleArgument(array $arguments, $key){
		if(count($arguments) !== 1){
			throw new \InvalidArgumentException("Multiple arguments are not allowed for " . $key);
		}
	}

	private function cannotReverse($value){
		if(strlen($value) > 0 && $value[0] === "!"){
			throw new \InvalidArgumentException("Argument cannot be reversed");
		}
	}

	private function between($a, $b, $value){
		return $a < $b ? ($value >= $a && $value <= $b) : ($value >= $b && $value <= $a);
	}
}
