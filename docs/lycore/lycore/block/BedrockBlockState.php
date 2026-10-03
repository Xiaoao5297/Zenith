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

namespace lycore\block;

use lycore\nbt\tag\ByteTag;
use lycore\nbt\tag\CompoundTag;
use lycore\nbt\tag\IntTag;
use lycore\nbt\tag\StringTag;

final class BedrockBlockState{
	const CURRENT_VERSION = 18100737;

	private function __construct(){
	}

	public static function createCompound($name, $id, $meta = 0){
		$state = self::getState((int) $id, (int) $meta);
		return new CompoundTag($name, [
			new StringTag("name", $state["name"]),
			self::createStatesCompound($state["states"]),
			new IntTag("version", self::CURRENT_VERSION),
		]);
	}

	private static function createStatesCompound(array $states){
		$tags = [];
		foreach($states as $name => $value){
			if(is_bool($value)){
				$tags[] = new ByteTag($name, $value ? 1 : 0);
			}elseif(is_int($value)){
				$tags[] = new IntTag($name, $value);
			}else{
				$tags[] = new StringTag($name, (string) $value);
			}
		}

		return new CompoundTag("states", $tags);
	}

	private static function getState($id, $meta){
		$meta &= 0x0f;
		switch($id){
			case Block::WATER:
				return ["name" => "minecraft:flowing_water", "states" => ["liquid_depth" => min($meta, 7)]];
			case Block::STILL_WATER:
				return ["name" => "minecraft:water", "states" => ["liquid_depth" => min($meta, 7)]];
			case Block::LAVA:
				return ["name" => "minecraft:flowing_lava", "states" => ["liquid_depth" => min($meta, 7)]];
			case Block::STILL_LAVA:
				return ["name" => "minecraft:lava", "states" => ["liquid_depth" => min($meta, 7)]];
			case Block::STICKY_PISTON:
				return ["name" => "minecraft:sticky_piston", "states" => ["facing_direction" => min($meta & 0x07, 5)]];
			case Block::PISTON:
				return ["name" => "minecraft:piston", "states" => ["facing_direction" => min($meta & 0x07, 5)]];
			case Block::PISTON_HEAD:
				return ["name" => "minecraft:piston_arm_collision", "states" => ["facing_direction" => min($meta & 0x07, 5)]];
			case Block::WOOL:
				return ["name" => "minecraft:" . self::colorName($meta) . "_wool", "states" => []];
			case Block::CHEST:
				return ["name" => "minecraft:chest", "states" => ["facing_direction" => min($meta, 5)]];
			case Block::FURNACE:
				return ["name" => "minecraft:furnace", "states" => ["minecraft:cardinal_direction" => self::cardinalFromFacing($meta)]];
			case Block::BURNING_FURNACE:
				return ["name" => "minecraft:lit_furnace", "states" => ["minecraft:cardinal_direction" => self::cardinalFromFacing($meta)]];
			case Block::STAINED_CLAY:
				return ["name" => "minecraft:" . self::colorName($meta) . "_terracotta", "states" => []];
			case Block::HAY_BALE:
				return ["name" => "minecraft:hay_block", "states" => ["pillar_axis" => self::axisFromDirection(($meta >> 2) & 0x03)]];
			case Block::OBSERVER:
				return ["name" => "minecraft:observer", "states" => [
					"minecraft:facing_direction" => self::blockFaceFromFacing($meta & 0x07),
					"powered_bit" => ($meta & 0x08) !== 0,
				]];
		}

		static $states = [
			Block::AIR => ["minecraft:air", []],
			Block::STONE => [
				0 => ["minecraft:stone", []],
				1 => ["minecraft:granite", []],
				2 => ["minecraft:polished_granite", []],
				3 => ["minecraft:diorite", []],
				4 => ["minecraft:polished_diorite", []],
				5 => ["minecraft:andesite", []],
				6 => ["minecraft:polished_andesite", []],
			],
			Block::GRASS => ["minecraft:grass", []],
			Block::DIRT => [
				0 => ["minecraft:dirt", []],
				1 => ["minecraft:coarse_dirt", []],
			],
			Block::COBBLESTONE => ["minecraft:cobblestone", []],
			Block::PLANKS => [
				0 => ["minecraft:oak_planks", []],
				1 => ["minecraft:spruce_planks", []],
				2 => ["minecraft:birch_planks", []],
				3 => ["minecraft:jungle_planks", []],
				4 => ["minecraft:acacia_planks", []],
				5 => ["minecraft:dark_oak_planks", []],
			],
			Block::BEDROCK => ["minecraft:bedrock", []],
			Block::SAND => [
				0 => ["minecraft:sand", []],
				1 => ["minecraft:red_sand", []],
			],
			Block::GRAVEL => ["minecraft:gravel", []],
			Block::GOLD_ORE => ["minecraft:gold_ore", []],
			Block::IRON_ORE => ["minecraft:iron_ore", []],
			Block::COAL_ORE => ["minecraft:coal_ore", []],
			Block::WOOD => [
				0 => ["minecraft:oak_log", ["pillar_axis" => "y"]],
				1 => ["minecraft:spruce_log", ["pillar_axis" => "y"]],
				2 => ["minecraft:birch_log", ["pillar_axis" => "y"]],
				3 => ["minecraft:jungle_log", ["pillar_axis" => "y"]],
				4 => ["minecraft:oak_log", ["pillar_axis" => "x"]],
				5 => ["minecraft:spruce_log", ["pillar_axis" => "x"]],
				6 => ["minecraft:birch_log", ["pillar_axis" => "x"]],
				7 => ["minecraft:jungle_log", ["pillar_axis" => "x"]],
				8 => ["minecraft:oak_log", ["pillar_axis" => "z"]],
				9 => ["minecraft:spruce_log", ["pillar_axis" => "z"]],
				10 => ["minecraft:birch_log", ["pillar_axis" => "z"]],
				11 => ["minecraft:jungle_log", ["pillar_axis" => "z"]],
			],
			Block::GLASS => ["minecraft:glass", []],
			Block::LAPIS_ORE => ["minecraft:lapis_ore", []],
			Block::LAPIS_BLOCK => ["minecraft:lapis_block", []],
			Block::MOVING_BLOCK => ["minecraft:moving_block", []],
			Block::GOLD_BLOCK => ["minecraft:gold_block", []],
			Block::IRON_BLOCK => ["minecraft:iron_block", []],
			Block::BRICKS_BLOCK => ["minecraft:brick_block", []],
			Block::TNT => ["minecraft:tnt", []],
			Block::BOOKSHELF => ["minecraft:bookshelf", []],
			Block::MOSS_STONE => ["minecraft:mossy_cobblestone", []],
			Block::OBSIDIAN => ["minecraft:obsidian", []],
			Block::DIAMOND_ORE => ["minecraft:diamond_ore", []],
			Block::DIAMOND_BLOCK => ["minecraft:diamond_block", []],
			Block::WORKBENCH => ["minecraft:crafting_table", []],
			Block::REDSTONE_ORE => ["minecraft:redstone_ore", []],
			Block::GLOWING_REDSTONE_ORE => ["minecraft:lit_redstone_ore", []],
			Block::ICE => ["minecraft:ice", []],
			Block::SNOW_BLOCK => ["minecraft:snow", []],
			Block::CLAY_BLOCK => ["minecraft:clay", []],
			Block::NETHERRACK => ["minecraft:netherrack", []],
			Block::SOUL_SAND => ["minecraft:soul_sand", []],
			Block::GLOWSTONE_BLOCK => ["minecraft:glowstone", []],
			Block::STONE_BRICKS => [
				0 => ["minecraft:stonebrick", ["stone_brick_type" => "default"]],
				1 => ["minecraft:stonebrick", ["stone_brick_type" => "mossy"]],
				2 => ["minecraft:stonebrick", ["stone_brick_type" => "cracked"]],
				3 => ["minecraft:stonebrick", ["stone_brick_type" => "chiseled"]],
			],
			Block::IRON_BAR => ["minecraft:iron_bars", []],
			Block::GLASS_PANE => ["minecraft:glass_pane", []],
			Block::MELON_BLOCK => ["minecraft:melon_block", []],
			Block::EMERALD_BLOCK => ["minecraft:emerald_block", []],
			Block::REDSTONE_BLOCK => ["minecraft:redstone_block", []],
			Block::NETHER_QUARTZ_ORE => ["minecraft:quartz_ore", []],
			Block::QUARTZ_BLOCK => [
				0 => ["minecraft:quartz_block", []],
				1 => ["minecraft:chiseled_quartz_block", []],
				2 => ["minecraft:quartz_pillar", ["pillar_axis" => "y"]],
				3 => ["minecraft:quartz_pillar", ["pillar_axis" => "x"]],
				4 => ["minecraft:quartz_pillar", ["pillar_axis" => "z"]],
			],
			Block::WOOD2 => [
				0 => ["minecraft:acacia_log", ["pillar_axis" => "y"]],
				1 => ["minecraft:dark_oak_log", ["pillar_axis" => "y"]],
				4 => ["minecraft:acacia_log", ["pillar_axis" => "x"]],
				5 => ["minecraft:dark_oak_log", ["pillar_axis" => "x"]],
				8 => ["minecraft:acacia_log", ["pillar_axis" => "z"]],
				9 => ["minecraft:dark_oak_log", ["pillar_axis" => "z"]],
			],
			Block::SLIME_BLOCK => ["minecraft:slime", []],
			Block::HARDENED_CLAY => ["minecraft:hardened_clay", []],
			Block::COAL_BLOCK => ["minecraft:coal_block", []],
			Block::PACKED_ICE => ["minecraft:packed_ice", []],
			Block::RED_SANDSTONE => "minecraft:red_sandstone",
			Block::PODZOL => ["minecraft:podzol", []],
		];

		if(isset($states[$id])){
			$state = $states[$id];
			if(is_string($state)){
				return ["name" => $state, "states" => []];
			}
			if(isset($state[0]) and is_string($state[0])){
				return ["name" => $state[0], "states" => isset($state[1]) ? $state[1] : []];
			}
			$metaState = isset($state[$meta]) ? $state[$meta] : $state[0];
			if(is_string($metaState)){
				return ["name" => $metaState, "states" => []];
			}

			return ["name" => $metaState[0], "states" => isset($metaState[1]) ? $metaState[1] : []];
		}

		return ["name" => "minecraft:air", "states" => []];
	}

	private static function cardinalFromFacing($facing){
		switch((int) $facing){
			case 2:
				return "north";
			case 3:
				return "south";
			case 4:
				return "west";
			case 5:
				return "east";
			default:
				return "north";
		}
	}

	private static function blockFaceFromFacing($facing){
		static $faces = ["down", "up", "north", "south", "west", "east"];
		return isset($faces[$facing]) ? $faces[$facing] : "north";
	}

	private static function axisFromDirection($direction){
		static $axes = ["y", "x", "z", "y"];
		return isset($axes[$direction]) ? $axes[$direction] : "y";
	}

	private static function colorName($meta){
		static $colors = [
			"white",
			"orange",
			"magenta",
			"light_blue",
			"yellow",
			"lime",
			"pink",
			"gray",
			"light_gray",
			"cyan",
			"purple",
			"blue",
			"brown",
			"green",
			"red",
			"black",
		];
		return $colors[$meta & 0x0f];
	}
}
