<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║██╔██╗ ██║██║     ██║   ██║█████╔╝█████╗
 * ██║██║╚██╗██║██║     ██║   ██║██╔══╝
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

use lycore\item\Item;
use lycore\item\Tool;
use lycore\level\Level;
use lycore\Player;
use lycore\math\Vector3;

class SignPost extends Transparent{

	protected $id = self::SIGN_POST;

	public function __construct($meta = 0){
		$this->meta = (int) $meta;
	}

	public function getHardness(){
		return 1;
	}

	public function isSolid(){
		return false;
	}

	public function getName() : string{
		return "Sign Post";
	}

	public function getBoundingBox(){
		return null;
	}

	public function place(
		Item $item,
		Block $block,
		Block $target,
		$face,
		$fx,
		$fy,
		$fz,
		Player $player = null
	){
		$face = (int) $face;

		/*
		 * SIDE_DOWN = 0，不能在方块底部放置告示牌。
		 */
		if($face === Vector3::SIDE_DOWN){
			return false;
		}

		/*
		 * 2 = NORTH
		 * 3 = SOUTH
		 * 4 = WEST
		 * 5 = EAST
		 *
		 * 墙上告示牌的元数据使用放置面方向。
		 */
		$wallFaces = [
			2 => 2,
			3 => 3,
			4 => 4,
			5 => 5
		];

		if(isset($wallFaces[$face])){

			$this->meta = $wallFaces[$face];

			/*
			 * 使用 Block 常量，不使用 Item::WALL_SIGN，
			 * 避免部分 API 2.0.0 核心中不存在 Item::WALL_SIGN。
			 */
			$this->getLevel()->setBlock(
				$block,
				Block::get(self::WALL_SIGN, $this->meta),
				true
			);

			return true;
		}

		/*
		 * 放在方块顶部时生成普通告示牌。
		 *
		 * 没有玩家对象时使用默认方向，避免 PHP 8 下
		 * 对 null 调用 yaw 导致服务器报错。
		 */
		$yaw = 0;

		if($player !== null){
			$yaw = (float) $player->yaw;
		}

		$this->meta = ((int) floor((($yaw + 180) * 16 / 360) + 0.5)) & 0x0F;

		$this->getLevel()->setBlock(
			$block,
			Block::get(self::SIGN_POST, $this->meta),
			true
		);

		return true;
	}

	public function onUpdate($type){
		if($type === Level::BLOCK_UPDATE_NORMAL){

			/*
			 * 普通告示牌必须依附在下方方块上。
			 */
			if($this->getSide(Vector3::SIDE_DOWN)->getId() === self::AIR){

				$this->getLevel()->useBreakOn($this);

				return Level::BLOCK_UPDATE_NORMAL;
			}
		}

		return false;
	}

	public function getDrops(Item $item) : array{
		return [
			[
				Item::SIGN,
				0,
				1
			]
		];
	}

	public function getToolType(){
		return Tool::TYPE_AXE;
	}
}