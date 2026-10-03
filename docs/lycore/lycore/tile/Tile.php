<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝
 * ╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 *  ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: U core
 *
 * @Links:
 *  > LY Core
 *  > LY Core Project
*/
namespace lycore\tile;

use lycore\event\Timings;
use lycore\level\format\Chunk;
use lycore\level\format\FullChunk;
use lycore\level\Level;
use lycore\level\Position;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\ByteTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\StringTag;
use lycore\utils\ChunkException;

abstract class Tile extends Position{
	const SIGN = "Sign";
	const CHEST = "Chest";
	const FURNACE = "Furnace";
	const FLOWER_POT = "FlowerPot";
	const MOB_SPAWNER = "MobSpawner";
	const SKULL = "Skull";
	const BREWING_STAND = "BrewingStand";
	const ENCHANT_TABLE = "EnchantTable";
	const ITEM_FRAME = "ItemFrame";
	const DISPENSER = "Dispenser";
	const DROPPER = "Dropper";
	const DAY_LIGHT_DETECTOR = "DLDetector";
	const CAULDRON = "Cauldron";
	const HOPPER = "Hopper";
	const MINECART_CHEST = "MinecartChest";
	const COMPARATOR = "Comparator";
	const PISTON_ARM = "PistonArm";
	const MOVING_BLOCK = "MovingBlock";

	public static $tileCount = 1;

	private static $knownTiles = [];
	private static $shortNames = [];

	/** @var Chunk */
	public $chunk;
	public $name;
	public $id;
	public $x;
	public $y;
	public $z;
	public $movable = true;
	public $attach;
	public $metadata;
	public $closed = false;
	public $namedtag;
	protected $lastUpdate;
	protected $server;
	protected $timings;

	/** @var \lycore\event\TimingsHandler */
	public $tickTimer;

	/**
	 * @param string    $type
	 * @param FullChunk $chunk
	 * @param CompoundTag  $nbt
	 * @param           $args
	 *
	 * @return Tile
	 */
	public static function createTile($type, FullChunk $chunk, CompoundTag $nbt, ...$args){
		if(isset(self::$knownTiles[$type])){
			$class = self::$knownTiles[$type];
			return new $class($chunk, $nbt, ...$args);
		}

		return null;
	}

	/**
	 * @param $className
	 *
	 * @return bool
	 */
	public static function registerTile($className){
		$class = new \ReflectionClass($className);
		if(is_a($className, Tile::class, true) and !$class->isAbstract()){
			self::$knownTiles[$class->getShortName()] = $className;
			self::$shortNames[$className] = $class->getShortName();
			return true;
		}

		return false;
	}

	/**
	 * Returns the short save name
	 *
	 * @return string
	 */
	public function getSaveId(){
		return self::$shortNames[static::class];
	}

	public function __construct(FullChunk $chunk, CompoundTag $nbt){
		if($chunk === null or $chunk->getProvider() === null){
			throw new ChunkException("Invalid garbage Chunk given to Tile");
		}

		$this->timings = Timings::getTileEntityTimings($this);

		$this->server = $chunk->getProvider()->getLevel()->getServer();
		$this->chunk = $chunk;
		$this->setLevel($chunk->getProvider()->getLevel());
		$this->namedtag = $nbt;
		$this->name = "";
		$this->lastUpdate = microtime(true);
		$this->id = Tile::$tileCount++;
		$this->x = (int) $this->namedtag["x"];
		$this->y = (int) $this->namedtag["y"];
		$this->z = (int) $this->namedtag["z"];
		if(isset($this->namedtag->isMovable)){
			$this->movable = (bool) $this->namedtag["isMovable"];
		}

		$this->chunk->addTile($this);
		$this->getLevel()->addTile($this);
		$this->tickTimer = Timings::getTileEntityTimings($this);
	}

	public function getId(){
		return $this->id;
	}

	public function saveNBT(){
		$this->namedtag->id = new StringTag("id", $this->getSaveId());
		$this->namedtag->x = new IntTag("x", $this->x);
		$this->namedtag->y = new IntTag("y", $this->y);
		$this->namedtag->z = new IntTag("z", $this->z);
		$this->namedtag->isMovable = new ByteTag("isMovable", $this->movable ? 1 : 0);
	}

	public function isMovable(){
		return $this->movable;
	}

	public function setMovable($movable){
		$this->movable = (bool) $movable;
	}

	/**
	 * @return \lycore\block\Block
	 */
	public function getBlock(){
		return $this->level->getBlock($this);
	}

	public function onUpdate(){
		return false;
	}

	public final function scheduleUpdate(){
		$this->level->updateTiles[$this->id] = $this;
	}

	public function __destruct(){
		$this->close();
	}

	public function close(){
		if(!$this->closed){
			$this->closed = true;
			unset($this->level->updateTiles[$this->id]);
			if($this->chunk instanceof FullChunk){
				$this->chunk->removeTile($this);
			}
			if(($level = $this->getLevel()) instanceof Level){
				$level->removeTile($this);
			}
			$this->level = null;
		}
	}

	public function getName() : string{
		return $this->name;
	}

}
