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

namespace raklib\protocol;

use raklib\Binary;
use raklib\RakLib;


class OPEN_CONNECTION_REPLY_1 extends Packet
{
    public static $ID = 0x06;

    public $serverID;
    public $mtuSize;

    public function encode()
    {
        parent::encode();
        $this->buffer .= RakLib::MAGIC;
        $this->buffer .= Binary::writeLong($this->serverID);
        $this->buffer .= \chr(0); //Server security
        $this->buffer .= \pack("n", $this->mtuSize);
    }

    public function decode()
    {
        parent::decode();
        $this->offset += 16; //Magic
        $this->serverID = Binary::readLong($this->get(8));
        \ord($this->get(1)); //security
        $this->mtuSize = \unpack("n", $this->get(2))[1];
    }
}
