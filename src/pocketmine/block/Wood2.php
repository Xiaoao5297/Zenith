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
 * @author PocketMine Team
 * @link http://www.pocketmine.net/
 * 
 *
*/

namespace pocketmine\block;

class Wood2 extends Wood{

    const ACACIA = 0;
    const DARK_OAK = 1;
    const RAINBOW = 2;

    protected $id = self::WOOD2;
    
    public function getName() : string{
        static $names = [
            self::ACACIA => "Acacia Wood",
            self::DARK_OAK => "Dark Oak Wood",
            self::RAINBOW => "彩虹橡木"
        ];
        
        $meta = $this->meta & 0x03; // 确保值在0-3范围内
        
        // 添加防御性检查，如果meta值不在定义范围内，使用默认值
        if(isset($names[$meta])){
            return $names[$meta];
        }
        
        // 如果meta值无效，返回默认的木块名称
        return "Unknown Wood";
    }
}
