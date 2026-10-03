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

namespace lycore\item\map;

use lycore\nbt\NBT;
use lycore\nbt\tag\CompoundTag;
use lycore\Server;
use lycore\utils\Config;
use lycore\utils\MapColor;

class MapData{
	
	public $MapData = array();
	private $path = '';
	private $wallBannerPath = '';
	private $server;
	
	public function __construct(Server $server, string $path) {
		$this->server = $server;
		$this->path = ($path."maps/");
		$this->wallBannerPath = ($path."plugins/WallBanner/maps/");
		if(!file_exists($this->path)){
			mkdir($this->path, 0777);
		}
	}
	
	public function getMapData($id){
		if(isset($this->MapData[$id])){
			return $this->MapData[$id];
		}else{
			return $this->loadMap($id);
		}
	}
	
	public function loadMap($id){
		if(file_exists($this->getNativeMapPath($id))){
			$img = imagecreatefrompng($this->getNativeMapPath($id));
			if($img === false){
				return null;
			}
			$array = [];
			for($y = 0; $y < 128; ++$y){
				for($x = 0; $x < 128; ++$x) {
					$rgb = ImageColorAt($img, $x, $y);
					$colors = imagecolorsforindex($img, $rgb);
					$array[$y][$x] = new MapColor($colors['red'], $colors['green'], $colors['blue']);
				}
			}
			$this->MapData[$id] = $array;
			imagedestroy($img);
			return $array;
		}

		$rawImage = $this->loadWallBannerMap($id);
		if($rawImage !== null){
			$this->MapData[$id] = $rawImage;
			return $rawImage;
		}

		return null;
	}

	private function loadWallBannerMap($id){
		$path = $this->getWallBannerMapPath($id);
		if(!file_exists($path)){
			return null;
		}

		$buffer = @file_get_contents($path);
		if(!is_string($buffer) or $buffer === ""){
			return null;
		}

		$decoded = @zlib_decode($buffer);
		if($decoded === false){
			return null;
		}

		try{
			$nbt = new NBT(NBT::BIG_ENDIAN);
			$nbt->read($decoded);
			$data = $nbt->getData();
			if($data instanceof CompoundTag and isset($data->rawImage)){
				$rawImage = $data->rawImage->getValue();
				if(is_string($rawImage) and strlen($rawImage) > 0){
					return $rawImage;
				}
			}
		}catch(\Throwable $e){
			return null;
		}

		return null;
	}

	private function getNativeMapPath($id){
		return $this->path."Map_".$id.".dat";
	}

	private function getWallBannerMapPath($id){
		return $this->wallBannerPath."map_".$id.".dat";
	}
	
	public function saveMapData($id, $data){
		$this->MapData[$id] = $data;
		$img = imagecreatetruecolor(128, 128); //GD2
		imagesavealpha($img, true);
		$background = imagecolorallocatealpha($img, 0x00, 0x00, 0x00, 0x00);
		imagefill($img, 0, 0, $background);
		for($y = 0; $y < 128; ++$y){
			for($x = 0; $x < 128; ++$x) {
				$color = $data[$y][$x];
				$rgb = imagecolorallocate($img, $color->getR(), $color->getG(), $color->getB());
				imagesetpixel($img, $x, $y, $rgb);
			}
		}
		imagepng($img, $this->path."Map_".$id.".dat");
		imagedestroy($img);
	}
	
	public function haveMap($id){
		return isset($this->MapData[$id]) or file_exists($this->getNativeMapPath($id)) or file_exists($this->getWallBannerMapPath($id));
	}
}
