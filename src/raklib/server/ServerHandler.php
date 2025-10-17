<?php

/*
 * RakLib network library
 *
 *
 * This project is not affiliated with Jenkins Software LLC nor RakNet.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 */

namespace raklib\server;

use raklib\Binary;
use raklib\protocol\EncapsulatedPacket;
use raklib\RakLib;

class ServerHandler{

    /** @var RakLibServer */
    protected $server;
    /** @var ServerInstance */
    protected $instance;

    public function __construct(RakLibServer $server, ServerInstance $instance){
        $this->server = $server;
        $this->instance = $instance;
    }

    public function sendEncapsulated($identifier, EncapsulatedPacket $packet, $flags = RakLib::PRIORITY_NORMAL){
        $buffer = chr(RakLib::PACKET_ENCAPSULATED) . chr(strlen($identifier)) . $identifier . chr($flags) . $packet->toBinary(true);
        $this->server->pushMainToThreadPacket($buffer);
    }

    public function sendRaw($address, $port, $payload){
        $buffer = chr(RakLib::PACKET_RAW) . chr(strlen($address)) . $address . Binary::writeShort($port) . $payload;
        $this->server->pushMainToThreadPacket($buffer);
    }

    public function closeSession($identifier, $reason){
        $buffer = chr(RakLib::PACKET_CLOSE_SESSION) . chr(strlen($identifier)) . $identifier . chr(strlen($reason)) . $reason;
        $this->server->pushMainToThreadPacket($buffer);
    }

    public function sendOption($name, $value){
        $buffer = chr(RakLib::PACKET_SET_OPTION) . chr(strlen($name)) . $name . $value;
        $this->server->pushMainToThreadPacket($buffer);
    }

    public function blockAddress($address, $timeout){
        $buffer = chr(RakLib::PACKET_BLOCK_ADDRESS) . chr(strlen($address)) . $address . Binary::writeInt($timeout);
        $this->server->pushMainToThreadPacket($buffer);
    }

    public function shutdown() {
    // 1. 发送关闭指令到 RakLib 主线程
    $buffer = chr(RakLib::PACKET_SHUTDOWN);
    $this->server->pushMainToThreadPacket($buffer);

    // 2. 直接调用 RakLibServer 的 shutdown() 方法（无需额外等待）
    $this->server->shutdown();

    // 3. 如果当前类继承自 Swoole 进程，才需要 wait()
    // 如果 RakLibServer 是普通对象，移除以下代码
    if ($this->server instanceof \Swoole\Process) {
        $this->server->wait();
    }
}


    public function emergencyShutdown(){
	    $this->server->shutdown();
        $this->server->pushMainToThreadPacket("\x7f"); //RakLib::PACKET_EMERGENCY_SHUTDOWN
    }

    protected function invalidSession($identifier){
        $buffer = chr(RakLib::PACKET_INVALID_SESSION) . chr(strlen($identifier)) . $identifier;
        $this->server->pushMainToThreadPacket($buffer);
    }

    /**
     * @return bool
     */
    public function handlePacket(){
        if(strlen($packet = $this->server->readThreadToMainPacket()) > 0){
            $id = ord($packet{0});
            $offset = 1;
            if($id === RakLib::PACKET_ENCAPSULATED){
                $len = ord($packet{$offset++});
                $identifier = substr($packet, $offset, $len);
                $offset += $len;
                $flags = ord($packet{$offset++});
                $buffer = substr($packet, $offset);
                $this->instance->handleEncapsulated($identifier, EncapsulatedPacket::fromBinary($buffer, true), $flags);
            }elseif($id === RakLib::PACKET_RAW){
                $len = ord($packet{$offset++});
                $address = substr($packet, $offset, $len);
                $offset += $len;
                $port = Binary::readShort(substr($packet, $offset, 2));
                $offset += 2;
                $payload = substr($packet, $offset);
                $this->instance->handleRaw($address, $port, $payload);
            }elseif($id === RakLib::PACKET_SET_OPTION){
                $len = ord($packet{$offset++});
                $name = substr($packet, $offset, $len);
                $offset += $len;
                $value = substr($packet, $offset);
                $this->instance->handleOption($name, $value);
            }elseif($id === RakLib::PACKET_OPEN_SESSION){
                $len = ord($packet{$offset++});
                $identifier = substr($packet, $offset, $len);
                $offset += $len;
                $len = ord($packet{$offset++});
                $address = substr($packet, $offset, $len);
                $offset += $len;
                $port = Binary::readShort(substr($packet, $offset, 2));
                $offset += 2;
                $clientID = Binary::readLong(substr($packet, $offset, 8));
                $this->instance->openSession($identifier, $address, $port, $clientID);
            }elseif($id === RakLib::PACKET_CLOSE_SESSION){
                $len = ord($packet{$offset++});
                $identifier = substr($packet, $offset, $len);
                $offset += $len;
                $len = ord($packet{$offset++});
                $reason = substr($packet, $offset, $len);
                $this->instance->closeSession($identifier, $reason);
            }elseif($id === RakLib::PACKET_INVALID_SESSION){
                $len = ord($packet{$offset++});
                $identifier = substr($packet, $offset, $len);
                $this->instance->closeSession($identifier, "Invalid session");
            }elseif($id === RakLib::PACKET_ACK_NOTIFICATION){
                $len = ord($packet{$offset++});
                $identifier = substr($packet, $offset, $len);
                $offset += $len;
                $identifierACK = Binary::readInt(substr($packet, $offset, 4));
                $this->instance->notifyACK($identifier, $identifierACK);
            }

            return true;
        }

        return false;
    }
}