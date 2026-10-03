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

namespace lycore\level\generator\biome;

use lycore\level\generator\noise\Simplex;
use lycore\utils\Random;

class BiomeSelector{

	/** @var Biome */
	private $fallback;

	/** @var Simplex */
	private $temperature;
	/** @var Simplex */
	private $rainfall;
	
	private $river;
	
	private $ocean;
	
	private $hills;

	/** @var Biome[] */
	private $biomes = [];

	private $map = [];

	private $lookup;
	private $postProcessor;

	public function __construct(Random $random, callable $lookup, Biome $fallback, callable $postProcessor = null){
		$this->fallback = $fallback;
		$this->lookup = $lookup;
		$this->postProcessor = $postProcessor;
		$this->temperature = new Simplex($random, 2, 1 / 8, 1 / 2048);
		$this->rainfall = new Simplex($random, 2, 1 / 8, 1 / 2048);
		$this->river = new Simplex($random, 6, 1 / 2, 1 / 1024);
		$this->ocean = new Simplex($random, 6, 1 / 2, 1 / 2048);
		$this->hills = new Simplex($random, 2, 1 / 2, 1 / 2048);
	}

	public function recalculate(){
		/*$this->map = new \SplFixedArray(64 * 64);

		for($i = 0; $i < 64; ++$i){
			for($j = 0; $j < 64; ++$j){
				$this->map[$i + ($j << 6)] = call_user_func($this->lookup, $i / 63, $j / 63);
			}
		}*/
	}
	
	

	public function addBiome(Biome $biome){
		$this->biomes[$biome->getId()] = $biome;
	}

	public function getTemperature($x, $z){
		return $this->temperature->noise2D($x, $z, true);
	}

	public function getRainfall($x, $z){
		return $this->rainfall->noise2D($x, $z, true);
	}
	
	public function getRiver($x, $z){
		return $this->river->noise2D($x, $z, true);
	}
	
	public function getOcean($x, $z){
		return $this->ocean->noise2D($x, $z, true);
	}
	
	public function getHills($x, $z){
		return $this->hills->noise2D($x, $z, true);
	}

	public function pickRawBiomeId($x, $z){
		return call_user_func(
			$this->lookup,
			$this->getTemperature($x, $z),
			$this->getRainfall($x, $z),
			$this->getRiver($x, $z),
			$this->getOcean($x, $z),
			$this->getHills($x, $z)
		);
	}

	/**
	 * @param $x
	 * @param $z
	 *
	 * @return Biome
	 */
	public function pickBiome($x, $z){
		$temperature = $this->getTemperature($x, $z);
		$rainfall = $this->getRainfall($x, $z);
		$River = $this->getRiver($x, $z);
		$ocean = $this->getOcean($x, $z);
		$hills = $this->getHills($x, $z);

		//$biomeId = $this->map[$temperature + ($rainfall << 6)];
		$biomeId = call_user_func($this->lookup, $temperature, $rainfall, $River, $ocean, $hills);
		if($this->postProcessor !== null){
			$biomeId = call_user_func($this->postProcessor, $biomeId, $temperature, $rainfall, $River, $ocean, $hills, $x, $z, $this);
		}
		return isset($this->biomes[$biomeId]) ? $this->biomes[$biomeId] : $this->fallback;
	}
}
