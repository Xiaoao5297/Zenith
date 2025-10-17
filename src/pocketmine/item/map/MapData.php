<?php

namespace pocketmine\item\map;

use pocketmine\Server;
use pocketmine\utils\Config;
use pocketmine\utils\MapColor;

class MapData{
	
	public $MapData = array();
	private $path = '';
	private $server;
	
	public function __construct(Server $server, string $path) {
		$this->server = $server;
		$this->path = ($path."maps/");
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
		$img = imagecreatefrompng($this->path."Map_".$id.".dat");
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
		return file_exists($this->path."Map_".$id.".dat");
	}
}