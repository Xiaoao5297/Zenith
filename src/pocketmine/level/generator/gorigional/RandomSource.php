<?php

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
