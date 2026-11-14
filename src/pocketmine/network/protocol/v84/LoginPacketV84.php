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

namespace pocketmine\network\protocol\v84;

#include <rules/DataPacket.h>


class LoginPacketV84 extends DataPacketV84{
	const NETWORK_ID = InfoV84::LOGIN_PACKET;

	public $username;
	public $protocol;

	public $clientUUID;
	public $clientId;
	public $serverAddress;

	public $skinId = null;
	public $skin = null;

	public function decode(){
		$this->protocol = $this->getInt();
		if(!in_array($this->protocol, InfoV84::ACCEPTED_PROTOCOLS)){
			return; //Do not attempt to decode for non-accepted protocols
		}

		//SEC-004: 校验压缩负载, 畸形包直接抛异常由外层 catch 兜底, 不让 null/false 一路向下传
		$str = zlib_decode($this->get($this->getInt()), 1024 * 1024 * 64);
		if(!is_string($str) or $str === ""){
			throw new \UnexpectedValueException("LoginPacketV84 payload failed to decompress");
		}
		$this->setBuffer($str, 0);

		//SEC-003 方案A: 本服务器为离线模式, 不做也不宣称 JWT 签名验证.
		//这里仅解析 token payload 中的字段供 clientData 流程使用, 签名验证已移除.
		//SEC-004: chain JSON 结构校验, 畸形结构抛异常
		$chainJson = json_decode($this->get($this->getLInt()), true);
		if(!is_array($chainJson) or !isset($chainJson["chain"]) or !is_array($chainJson["chain"])){
			throw new \UnexpectedValueException("LoginPacketV84 chain data is malformed");
		}

		foreach($chainJson["chain"] as $chain){
			$webtoken = self::parseTokenPayload($chain);
			if($webtoken === null){
				continue;
			}
			if(isset($webtoken["extraData"]) and is_array($webtoken["extraData"])){
				if(isset($webtoken["extraData"]["displayName"])){
					$this->username = $webtoken["extraData"]["displayName"];
				}
				if(isset($webtoken["extraData"]["identity"])){
					$this->clientUUID = $webtoken["extraData"]["identity"];
				}
			}
		}

		$skinToken = self::parseTokenPayload($this->get($this->getLInt()));
		if($skinToken !== null){
			if(isset($skinToken["ClientRandomId"])){
				$this->clientId = $skinToken["ClientRandomId"];
			}
			if(isset($skinToken["ServerAddress"])){
				$this->serverAddress = $skinToken["ServerAddress"];
			}
			if(isset($skinToken["SkinData"])){
				$this->skin = base64_decode($skinToken["SkinData"]);
			}
			if(isset($skinToken["SkinId"])){
				$this->skinId = $skinToken["SkinId"];
			}
		}
	}

	public function encode(){

	}

	/**
	 * SEC-004: 解析 JWT payload. 段数不为 3、base64 解码失败或 json_decode
	 * 结果不是数组时返回 null (调用方已判空), 不再让 null 一路向下传递.
	 * 仅提取 payload, 不做签名验证 (离线模式, 见 SEC-003 方案A).
	 *
	 * @return array|null
	 */
	private static function parseTokenPayload($token){
		if(!is_string($token)){
			return null;
		}
		$tokens = explode(".", $token);
		if(count($tokens) !== 3){
			return null;
		}
		$payload = base64_decode(strtr($tokens[1], '-_', '+/'), true);
		if($payload === false or $payload === ""){
			return null;
		}
		$webtoken = json_decode($payload, true);
		return is_array($webtoken) ? $webtoken : null;
	}

}
