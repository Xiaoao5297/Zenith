<?php

/**
 * 0.15 拴绳元数据回归测试。
 *
 * 背景：Zenith 内部把 64 位长整型标为 8（7 被 DATA_TYPE_ROTATION 占用），
 * 而协议里的 LONG 是 7。0.15 的 DATA_LEAD_HOLDER（索引 23）是 long，
 * 类型不匹配时整条会被过滤掉，客户端就看不到拴绳。
 */
class MetadataLeadTest{

	public static function run() : array{
		$root = \dirname(__DIR__) . "/src";
		if(!\class_exists("\\pocketmine\\network\\compat\\mappings\\MetadataMapping", false)){
			\spl_autoload_register(function($class) use ($root){
				if(\strpos($class, "pocketmine\\") === 0){
					$path = $root . "/" . \str_replace("\\", "/", $class) . ".php";
					if(\is_file($path)){
						require_once $path;
					}
				}
			});
		}

		$mapping = "\\pocketmine\\network\\compat\\mappings\\MetadataMapping";
		$metadata = [
			0 => [0, 0],
			23 => [8, 42],
			24 => [0, 1],
		];

		$filtered015 = $mapping::filterMetadataForProtocol(84, $metadata);
		$filtered014 = $mapping::filterMetadataForProtocol(70, $metadata);

		return [
			["lead_holder_kept_015", isset($filtered015[23]), "0.15 保留 DATA_LEAD_HOLDER(索引 23)"],
			["lead_holder_type_015", isset($filtered015[23]) and $filtered015[23][0] === 7 and (int) $filtered015[23][1] === 42, "内部类型 8 被改写为协议 LONG(7) 且值不变"],
			["lead_kept_015", isset($filtered015[24]) and $filtered015[24][0] === 0, "0.15 保留 DATA_LEAD(索引 24)"],
			["lead_dropped_014", !isset($filtered014[23]) and !isset($filtered014[24]), "0.14 仍然移除拴绳元数据"],
			["base_byte_intact", isset($filtered015[0]) and $filtered015[0][0] === 0, "索引 0 的 byte 元数据不受影响"],
		];
	}
}
