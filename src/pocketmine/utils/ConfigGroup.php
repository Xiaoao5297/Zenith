<?php

/***
 *  _____                _  __   __
 * /__  /  ___   ____   (_)/ /_ / /_
 *   / /  / _ \ / __ \ / // __// __ \
 *  / /__/  __// / / // // /_ / / / /
 * /____/\___//_/ /_//_/ \__//_/ /_/
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/

namespace pocketmine\utils;

/**
 * 多文件配置组。
 *
 * 把按主题拆分到多个文件里的配置 (server.yml / world.yml / gameplay.yml /
 * debug.yml) 当成一个 Config 来读，调用方不需要知道某个键到底在哪个文件里。
 *
 * 使用方式:
 *   $group = new ConfigGroup([
 *       ["config" => $serverConfig, "keys" => ["settings", "memory"]],
 *       ["config" => $worldConfig,  "keys" => ["nether", "world"]],
 *   ]);
 *
 * 读取: 按构造顺序依次查每个 Config，第一个命中的值就是结果 (前面的文件优先)。
 * 写入: 用键的首段 (例如 "world.no-crop-growth" 的 "world") 定位所属文件；
 *       首段没有注册过时抛 InvalidArgumentException，避免把键悄悄写进错误的文件。
 *
 * 本类只依赖 Config，不引用 Server / Logger，可以脱离 PocketMine 运行时做单元测试。
 */
class ConfigGroup{

	/** @var array<int, array{config: Config, keys: string[]}> */
	private $groups = [];

	/** @var array<string, int> 首段键 => $groups 下标 */
	private $keyMap = [];

	/**
	 * @param array $configs 元素格式: ["config" => Config, "keys" => ["settings", ...]]
	 */
	public function __construct(array $configs){
		$index = 0;
		foreach($configs as $entry){
			if(!is_array($entry) or !isset($entry["config"]) or !($entry["config"] instanceof Config)){
				throw new \InvalidArgumentException("ConfigGroup 元素必须是 [\"config\" => Config, \"keys\" => array] 形式");
			}

			$keys = isset($entry["keys"]) ? (array) $entry["keys"] : [];
			$this->groups[$index] = [
				"config" => $entry["config"],
				"keys" => $keys,
			];
			foreach($keys as $key){
				$key = (string) $key;
				if($key === "" or isset($this->keyMap[$key])){
					// 同一个首段键只归属第一个声明它的文件，后面的忽略。
					continue;
				}
				$this->keyMap[$key] = $index;
			}
			++$index;
		}
	}

	/**
	 * 取一个顶层键。按文件顺序查找，第一个存在的值直接返回。
	 *
	 * @param string $k
	 * @param mixed  $d
	 *
	 * @return mixed
	 */
	public function get($k, $d = null){
		foreach($this->groups as $group){
			if($group["config"]->exists($k)){
				return $group["config"]->get($k);
			}
		}

		return $d;
	}

	/**
	 * 取嵌套键 (点号分隔)。按文件顺序查找，第一个命中的值直接返回。
	 *
	 * @param string $k
	 * @param mixed  $d
	 *
	 * @return mixed
	 */
	public function getNested($k, $d = null){
		$missing = self::missing();
		foreach($this->groups as $group){
			$value = $group["config"]->getNested($k, $missing);
			if($value !== $missing){
				return $value;
			}
		}

		return $d;
	}

	/**
	 * 判断键是否存在。含点号时按嵌套键判断。
	 *
	 * @param string $k
	 *
	 * @return bool
	 */
	public function exists($k) : bool{
		if(strpos($k, ".") === false){
			foreach($this->groups as $group){
				if($group["config"]->exists($k)){
					return true;
				}
			}

			return false;
		}

		$missing = self::missing();

		return $this->getNested($k, $missing) !== $missing;
	}

	/**
	 * 写顶层键，按首段键定位所属文件。
	 * 与 Config::set() 语义一致：$k 是完整的顶层键，不做点号拆分；嵌套路径请用 setNested()。
	 *
	 * @param string $k
	 * @param mixed  $v
	 */
	public function set($k, $v = true){
		$this->groupFor($k)["config"]->set($k, $v);
	}

	/**
	 * 写嵌套键，按首段键定位所属文件。文件里缺少中间层时由 Config::setNested 自动补空数组。
	 *
	 * @param string $k
	 * @param mixed  $v
	 */
	public function setNested($k, $v){
		$this->groupFor($k)["config"]->setNested($k, $v);
	}

	/**
	 * 返回某个键所属的 Config，找不到归属时返回 null (只读用途，例如保留注释的写回)。
	 *
	 * @param string $k
	 *
	 * @return Config|null
	 */
	public function getOwnerConfig($k){
		$base = explode(".", $k)[0];

		return isset($this->keyMap[$base]) ? $this->groups[$this->keyMap[$base]]["config"] : null;
	}

	/**
	 * 所有文件深合并后的数组。同一路径冲突时前面的文件优先，与 get() 的语义一致。
	 *
	 * @return array
	 */
	public function getAll() : array{
		$result = [];
		foreach($this->groups as $group){
			$this->mergeMissing($result, $group["config"]->getAll());
		}

		return $result;
	}

	/**
	 * 逐个保存。$async 透传给 Config::save()，默认异步 (与旧的 advancedConfig 行为一致)。
	 *
	 * @param bool $async
	 *
	 * @return bool 所有文件都保存成功时返回 true
	 */
	public function save($async = true) : bool{
		$ok = true;
		foreach($this->groups as $group){
			if($group["config"]->save($async) !== true){
				$ok = false;
			}
		}

		return $ok;
	}

	/**
	 * 逐个从磁盘重新载入。
	 */
	public function reload(){
		foreach($this->groups as $group){
			$group["config"]->reload();
		}
	}

	/**
	 * 定位键所属的组，找不到时抛异常，避免写进错误的文件。
	 *
	 * @param string $k
	 *
	 * @return array{config: Config, keys: string[]}
	 */
	private function groupFor($k) : array{
		$base = explode(".", $k)[0];
		if(isset($this->keyMap[$base])){
			return $this->groups[$this->keyMap[$base]];
		}

		throw new \InvalidArgumentException("配置键 \"" . (string) $k . "\" 没有归属文件: 首段键 \"" . $base . "\" 未在 ConfigGroup 中注册");
	}

	/**
	 * 把 $source 里 $target 还没有的键补进去 (递归)，实现"前面的文件优先"的深合并。
	 *
	 * @param array $target
	 * @param array $source
	 */
	private function mergeMissing(array &$target, array $source){
		foreach($source as $key => $value){
			if(!array_key_exists($key, $target)){
				$target[$key] = $value;
				continue;
			}
			if(is_array($target[$key]) and is_array($value)){
				$this->mergeMissing($target[$key], $value);
			}
		}
	}

	/**
	 * 用作 getNested() 的"没有这个键"哨兵，避免把合法的 null 值误判为缺失。
	 *
	 * @return \stdClass
	 */
	private static function missing(){
		static $missing = null;
		if($missing === null){
			$missing = new \stdClass();
		}

		return $missing;
	}

}
