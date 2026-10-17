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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author Xiaoao
 * @link https://github.com/Xiaoao5297/Zenith
 *
 *
*/


namespace pocketmine\level\generator\gorigional;

/**
 * 随机数源接口。
 *
 * 地形噪声需要同时兼容 Java 1.7 的 LCG（生物群系层）和 MCPE 0.14.3 的
 * MT19937（地形噪声、装饰、结构）。接口只暴露噪声/装饰真正用到的方法。
 */
interface RandomSource{

	public function nextBoundedInt($n);

	public function nextDouble();

	public function nextFloat();
}
