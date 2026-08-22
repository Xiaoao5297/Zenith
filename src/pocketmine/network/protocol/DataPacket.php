<?php

namespace pocketmine\network\protocol;

#include <rules/DataPacket.h>

#ifndef COMPILE

#endif

use pocketmine\utils\BinaryStream;
use pocketmine\utils\Utils;

abstract class DataPacket extends BinaryStream{
    const NETWORK_ID = 0;
    
    public $isEncoded = false;
    private $channel = 0;
    public $protocol = 0;  // 添加这行
    public $encodedProtocol = 0; // encode() 时使用的协议版本, 用于判断缓存包是否需要按新协议重新编码
    
    public function pid(){
        return $this::NETWORK_ID;
    }
    
    public function setProtocol($protocol){
        // error_log("设置DataPacket协议: " . $protocol);
        $this->protocol = $protocol;
        return $this;
    }
    
    abstract public function encode();
    abstract public function decode();
    
    public function reset(){
        $this->buffer = chr($this::NETWORK_ID);
        $this->offset = 0;
        $this->encodedProtocol = $this->protocol;
    }
    
    public function setChannel($channel){
        $this->channel = (int) $channel;
        return $this;
    }
    
    public function getChannel(){
        return $this->channel;
    }
    
    public function clean(){
        $this->buffer = null;
        $this->isEncoded = false;
        $this->offset = 0;
        $this->encodedProtocol = 0;
        return $this;
    }
    
    // 添加针对不同协议版本的putSlot方法
    public function putSlot($item, bool $legacy013 = false){
        if($item === null || $item->getId() === 0){
            $this->putShort(0);
            return;
        }

        $this->putShort($item->getId());
        $this->putByte($item->getCount());
        $this->putShort($item->getDamage() === null ? -1 : $item->getDamage());
        $nbt = $item->getCompoundTag();
        
        // 0.12/0.13 使用大端short NBT长度, 0.14+ 使用小端short
        if($legacy013 or ProtocolCompatibility::usesLegacySlotFormat((int) ($this->protocol ?? 0))){
            $this->putShort(strlen($nbt));
        } else {
            $this->putLShort(strlen($nbt));
        }
        
        $this->put($nbt);
    }
    
    // 添加针对不同协议版本的getSlot方法
    public function getSlot(bool $legacy013 = false){
        $id = $this->getSignedShort();

        if($id <= 0){
            return \pocketmine\item\Item::get(0, 0, 0);
        }

        $cnt = $this->getByte();
        $data = $this->getShort();
        
        // 0.12/0.13 使用大端short NBT长度, 0.14+ 使用小端short
        if($legacy013 or ProtocolCompatibility::usesLegacySlotFormat((int) ($this->protocol ?? 0))){
            $nbtLen = $this->getShort();
        } else {
            $nbtLen = $this->getLShort();
        }

        $nbt = "";

        if($nbtLen > 0){
            $nbt = $this->get($nbtLen);
        }
        
        return \pocketmine\item\Item::get($id, $data, $cnt, $nbt);
    }
    
    public function __debugInfo(){
        $data = [];
        foreach($this as $k => $v){
            if($k === "buffer"){
                $data[$k] = bin2hex($v);
            }elseif(is_string($v) or (is_object($v) and method_exists($v, "__toString"))){
                $data[$k] = Utils::printable((string) $v);
            }else{
                $data[$k] = $v;
            }
        }
        return $data;
    }
}