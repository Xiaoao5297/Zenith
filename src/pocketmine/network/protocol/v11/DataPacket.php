<?php

/*
 *
 *  ____            _        _   __  __ _                  __  __ ____  
 * |  _ \ ___   ___| | _____| |_|  \/  (_)_ __   ___      |  \/  |  _ \ 
 * | |_) / _ \ / __| |/ / _ \ __| |\/| | | '_ \ / _ \_____| |\/| | |_) |
 * |  __/ (_) | (__|   <  __/ |_| |  | | | | | |  __/_____| |  | |  __/ 
 * |_|   \___/ \___|_|\_\___|\__|_|  |_|_|_| |_|\___|     |_|  |_|_| 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author LY Core Team
 * @link http://lycore.dev/
 * 
 *
*/

namespace pocketmine\network\protocol\v11;

use pocketmine\utils\Binary;











use pocketmine\item\Item;


abstract class DataPacket extends \stdClass{

	const NETWORK_ID = 0;

	public $offset = 0;
	public $buffer = "";
	public $isEncoded = \false;
	private $channel = 0;

	public function pid(){
		return $this::NETWORK_ID;
	}

	abstract public function encode();

	abstract public function decode();

	protected function reset(){
		$this->buffer = \chr($this::NETWORK_ID);
		$this->offset = 0;
	}

	public function setChannel($channel){
		$this->channel = (int) $channel;
		return $this;
	}

	public function getChannel(){
		return $this->channel;
	}

	public function setBuffer($buffer = \null, $offset = 0){
		$this->buffer = $buffer;
		$this->offset = (int) $offset;
	}

	public function getOffset(){
		return $this->offset;
	}

	public function getBuffer(){
		return $this->buffer;
	}

	protected function get($len){
		if($len < 0){
			$this->offset = \strlen($this->buffer) - 1;
			return "";
		}elseif($len === \true){
			return \substr($this->buffer, $this->offset);
		}

		return $len === 1 ? $this->buffer[$this->offset++] : \substr($this->buffer, ($this->offset += $len) - $len, $len);
	}

	protected function put($str){
		$this->buffer .= $str;
	}

	protected function getLong(){
		return Binary::readLong($this->get(8));
	}

	protected function putLong($v){
		$this->buffer .= Binary::writeLong($v);
	}

	protected function getInt(){
		return (\PHP_INT_SIZE === 8 ? \unpack("N", $this->get(4))[1] << 32 >> 32 : \unpack("N", $this->get(4))[1]);
	}

	protected function putInt($v){
		$this->buffer .= \pack("N", $v);
	}

	protected function getShort($signed = \true){
		return $signed ? (\PHP_INT_SIZE === 8 ? \unpack("n", $this->get(2))[1] << 48 >> 48 : \unpack("n", $this->get(2))[1] << 16 >> 16) : \unpack("n", $this->get(2))[1];
	}

	protected function putShort($v){
		$this->buffer .= \pack("n", $v);
	}

	protected function getFloat(){
		return (\ENDIANNESS === 0 ? \unpack("f", $this->get(4))[1] : \unpack("f", \strrev($this->get(4)))[1]);
	}

	protected function putFloat($v){
		$this->buffer .= (\ENDIANNESS === 0 ? \pack("f", $v) : \strrev(\pack("f", $v)));
	}

	protected function getTriad(){
		return \unpack("N", "\x00" . $this->get(3))[1];
	}

	protected function putTriad($v){
		$this->buffer .= \substr(\pack("N", $v), 1);
	}


	protected function getLTriad(){
		return \unpack("V", $this->get(3) . "\x00")[1];
	}

	protected function putLTriad($v){
		$this->buffer .= \substr(\pack("V", $v), 0, -1);
	}

	protected function getByte(){
		return \ord($this->buffer[$this->offset++]);
	}

	protected function putByte($v){
		$this->buffer .= \chr($v);
	}

	protected function getDataArray($len = 10){
		$data = [];
		for($i = 1; $i <= $len and !$this->feof(); ++$i){
			$data[] = $this->get(\unpack("N", "\x00" . $this->get(3))[1]);
		}

		return $data;
	}

	protected function putDataArray(array $data = []){
		foreach($data as $v){
			$this->buffer .= \substr(\pack("N", \strlen($v)), 1);
			$this->buffer .= $v;
		}
	}

	protected function getSlot(){
		$id = \unpack("n", $this->get(2))[1];
		$cnt = \ord($this->get(1));
		$meta = \unpack("n", $this->get(2))[1];
		return Item::get($id, $meta, $cnt);
	}

	protected function getLegacySlotItem(Item $item) : Item{
		$itemId = $item->getId();
		$itemMeta = $item->getDamage();
		$count = max(1, (int) $item->getCount());

		if($itemId === Item::ENCHANTED_BOOK){
			return Item::get(Item::BOOK, 0, $count);
		}

		if($itemId === Item::ENCHANTED_GOLDEN_APPLE or ($itemId === Item::GOLDEN_APPLE and $itemMeta > 0)){
			return Item::get(Item::GOLDEN_APPLE, 0, $count);
		}

		return $item;
	}

	protected function putSlot(Item $item){
		$item = $this->getLegacySlotItem($item);
		$this->buffer .= \pack("n", $item->getId());
		$this->buffer .= \chr($item->getCount());
		$this->buffer .= \pack("n", $item->getDamage() === null ? 0 : $item->getDamage());
	}

	protected function getString(){
		return $this->get(\unpack("n", $this->get(2))[1]);
	}

	protected function putString($v){
		$this->buffer .= \pack("n", \strlen($v));
		$this->buffer .= $v;
	}

	protected function feof(){
		return !isset($this->buffer[$this->offset]);
	}

	public function toCurrentPacket(){
		switch($this::NETWORK_ID){
			case Info::LOGIN_PACKET:
				$pk = new \pocketmine\network\protocol\LoginPacket();
				$pk->username = $this->username ?? "";
				$pk->protocol1 = $this->protocol1 ?? Info::CURRENT_PROTOCOL;
				$pk->protocol2 = $this->protocol2 ?? $pk->protocol1;
				$pk->clientId = $this->clientId ?? 0;
				$pk->clientUUID = \pocketmine\utils\UUID::fromData("v11", strtolower($pk->username), (string) $pk->clientId);
				$pk->serverAddress = "";
				$pk->clientSecret = "";
				$pk->skinName = ($this->slim ?? false) ? "Standard_Alex" : "Standard_Steve";
				$pk->skin = (isset($this->skin) and is_string($this->skin) and strlen($this->skin) > 0) ? $this->skin : str_repeat("\x00", 64 * 32 * 4);
				return $pk;
			case Info::TEXT_PACKET:
				$pk = new \pocketmine\network\protocol\TextPacket();
				$pk->type = $this->type;
				$pk->source = $this->source ?? "";
				$pk->message = $this->message ?? "";
				$pk->parameters = $this->parameters ?? [];
				return $pk;
			case Info::MOVE_PLAYER_PACKET:
				$pk = new \pocketmine\network\protocol\MovePlayerPacket();
				$pk->eid = $this->eid;
				$pk->x = $this->x;
				$pk->y = $this->y;
				$pk->z = $this->z;
				$pk->yaw = $this->yaw;
				$pk->bodyYaw = $this->bodyYaw;
				$pk->pitch = $this->pitch;
				$pk->mode = $this->mode;
				$pk->onGround = $this->onGround;
				return $pk;
			case Info::REMOVE_BLOCK_PACKET:
				$pk = new \pocketmine\network\protocol\RemoveBlockPacket();
				$pk->eid = $this->eid;
				$pk->x = $this->x;
				$pk->y = $this->y;
				$pk->z = $this->z;
				return $pk;
			case Info::PLAYER_EQUIPMENT_PACKET:
				$pk = new \pocketmine\network\protocol\MobEquipmentPacket();
				$pk->eid = $this->eid;
				$pk->item = $this->item instanceof Item ? $this->item : Item::get($this->item, $this->meta, 1);
				$pk->slot = $this->slot;
				$pk->selectedSlot = $this->selectedSlot;
				return $pk;
			case Info::INTERACT_PACKET:
				$pk = new \pocketmine\network\protocol\InteractPacket();
				$pk->action = $this->action;
				$pk->eid = $this->eid ?? 0;
				$pk->target = $this->target;
				return $pk;
			case Info::USE_ITEM_PACKET:
				$pk = new \pocketmine\network\protocol\UseItemPacket();
				$pk->x = $this->x;
				$pk->y = $this->y;
				$pk->z = $this->z;
				$pk->face = $this->face;
				$pk->item = $this->item instanceof Item ? $this->item : Item::get($this->item, $this->meta, 1);
				$pk->fx = $this->fx;
				$pk->fy = $this->fy;
				$pk->fz = $this->fz;
				$pk->posX = $this->posX;
				$pk->posY = $this->posY;
				$pk->posZ = $this->posZ;
				return $pk;
			case Info::PLAYER_ACTION_PACKET:
				$pk = new \pocketmine\network\protocol\PlayerActionPacket();
				$pk->eid = $this->eid;
				$pk->action = $this->action;
				$pk->x = $this->x;
				$pk->y = $this->y;
				$pk->z = $this->z;
				$pk->face = $this->face;
				return $pk;
			case Info::ENTITY_EVENT_PACKET:
				$pk = new \pocketmine\network\protocol\EntityEventPacket();
				$pk->eid = $this->eid;
				$pk->event = $this->event;
				$pk->data = $this->data ?? 0;
				return $pk;
			case Info::DROP_ITEM_PACKET:
				$pk = new \pocketmine\network\protocol\DropItemPacket();
				$pk->type = $this->unknown ?? 0;
				$pk->item = $this->item;
				return $pk;
			case Info::CONTAINER_CLOSE_PACKET:
				$pk = new \pocketmine\network\protocol\ContainerClosePacket();
				$pk->windowid = $this->windowid;
				return $pk;
			case Info::CONTAINER_SET_SLOT_PACKET:
				$pk = new \pocketmine\network\protocol\ContainerSetSlotPacket();
				$pk->windowid = $this->windowid;
				$pk->slot = $this->slot;
				$pk->item = $this->item;
				return $pk;
			case Info::CONTAINER_SET_CONTENT_PACKET:
				$pk = new \pocketmine\network\protocol\ContainerSetContentPacket();
				$pk->windowid = $this->windowid;
				$pk->slots = $this->slots;
				$pk->hotbar = $this->hotbar;
				return $pk;
			case Info::TILE_ENTITY_DATA_PACKET:
				$pk = new \pocketmine\network\protocol\BlockEntityDataPacket();
				$pk->x = $this->x;
				$pk->y = $this->y;
				$pk->z = $this->z;
				$pk->namedtag = $this->namedtag;
				return $pk;
		}

		return null;
	}

	public function clean(){
		$this->buffer = \null;
		$this->isEncoded = \false;
		$this->offset = 0;
		return $this;
	}
}
