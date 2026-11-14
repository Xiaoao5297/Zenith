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
*/

/*
 * 从 InCore-II 移植进 Zenith：物品 id 常量暂留在本类（Item::LEAD 尚未声明）。
 *
 * 已注册到 Item::$list (创造模式物品列表未添加, 0.14 客户端无此物品)。
 * （Item::init()/initCreativeItems() 不在本次改动范围）。
 * 玩家右键实体牵引的入口已在 Player::handleDataPacket (INTERACT_PACKET) 接线；
 * 栅栏挂结走 Level::useItemOn -> onActivate()。
 */

namespace pocketmine\item;

use pocketmine\block\Block;
use pocketmine\entity\Entity;
use pocketmine\entity\LeashKnot;
use pocketmine\level\Level;
use pocketmine\math\AxisAlignedBB;
use pocketmine\network\protocol\ProtocolCompatibility;
use pocketmine\Player;

class Lead extends Item{

	/** 拴绳物品 id（原版 0.15+）。Item::LEAD 声明前先放在这里。 */
	const LEAD = 420;

	public function __construct($meta = 0, $count = 1){
		parent::__construct(self::LEAD, $meta, $count, "Lead");
	}

	public function canBeActivated() : bool{
		return true;
	}

	/**
	 * 玩家用拴绳牵引实体。调用方负责校验距离/视线，这里只做状态机校验。
	 */
	public function leashEntity(Player $player, Entity $entity) : bool{
		if(!ProtocolCompatibility::isProtocol015((int) $player->getProtocol())){
			return false;
		}
		if($entity instanceof Player or $entity instanceof LeashKnot or !$entity->isAlive() or $entity->isLeashed()){
			return false;
		}
		if($this->getCount() <= 0){
			return false;
		}

		if(!$entity->setLeashedTo($player)){
			return false;
		}

		$this->consumeLead($player);
		return true;
	}

	public function onActivate(Level $level, Player $player, Block $block, Block $target, $face, $fx, $fy, $fz){
		if(!ProtocolCompatibility::isProtocol015((int) $player->getProtocol()) or !LeashKnot::isSupportedFence($target)){
			return false;
		}

		$knot = null;
		$attached = false;
		$range = 7;
		$bb = new AxisAlignedBB(
			$target->getX() - $range,
			$target->getY() - $range,
			$target->getZ() - $range,
			$target->getX() + $range + 1,
			$target->getY() + $range + 1,
			$target->getZ() + $range + 1
		);

		foreach($level->getNearbyEntities($bb, $player) as $entity){
			if($entity instanceof Entity and $entity->isLeashedTo($player)){
				if($knot === null){
					$knot = LeashKnot::getOrCreate($level, $target);
					if(!($knot instanceof LeashKnot)){
						return false;
					}
				}
				if($entity->setLeashedTo($knot)){
					$attached = true;
				}
			}
		}

		if(!$attached){
			//没有拴上任何实体时回收刚创建的绳结, 避免留下空结
			if($knot instanceof LeashKnot){
				$knot->close();
			}
			return false;
		}

		//挂到栅栏同样消耗一根拴绳(创造模式除外)
		$this->consumeLead($player);
		return true;
	}

	private function consumeLead(Player $player){
		if($player->isCreative()){
			return;
		}

		$item = $player->getInventory()->getItemInHand();
		if($item->getId() !== self::LEAD){
			$item = $this;
		}
		$item->setCount($item->getCount() - 1);
		$player->getInventory()->setItemInHand($item->getCount() > 0 ? $item : Item::get(Item::AIR, 0, 0));
	}
}
