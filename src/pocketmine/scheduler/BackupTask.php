<?php

namespace pocketmine\scheduler;

use pocketmine\Server;

class BackupTask extends AsyncTask{

	/** @var string */
	private $tier;
	/** @var string */
	private $srcRoot;
	/** @var string */
	private $destDir;
	/** @var string */
	private $prevDir;
	/** @var array */
	private $include;

	public function __construct($tier, $srcRoot, $destDir, $prevDir, array $include){
		$this->tier = (string) $tier;
		$this->srcRoot = (string) $srcRoot;
		$this->destDir = (string) $destDir;
		$this->prevDir = (string) $prevDir;
		$this->include = $include;
	}

	public function onRun(){
		$result = ["success" => false, "message" => "", "files" => 0, "linked" => 0, "copied" => 0];
		try{
			if(!is_dir($this->destDir)){
				@mkdir($this->destDir, 0777, true);
			}
			$srcRoot = rtrim($this->srcRoot, "/\\") . DIRECTORY_SEPARATOR;
			$destRoot = rtrim($this->destDir, "/\\") . DIRECTORY_SEPARATOR;
			$prevRoot = $this->prevDir === "" ? "" : rtrim($this->prevDir, "/\\") . DIRECTORY_SEPARATOR;
			foreach($this->include as $item){
				$src = $srcRoot . $item;
				if(!file_exists($src)){
					continue;
				}
				$this->copyTree($src, $destRoot . $item, $prevRoot === "" ? "" : $prevRoot . $item, $result);
			}
			$result["success"] = true;
		}catch(\Throwable $e){
			$result["message"] = $e->getMessage();
		}
		$this->setResult($result);
	}

	private function copyTree($src, $dest, $prev, array &$result){
		if(is_dir($src)){
			if(!is_dir($dest)){
				@mkdir($dest, 0777, true);
			}
			$dh = @opendir($src);
			if($dh === false){
				return;
			}
			while(($f = readdir($dh)) !== false){
				if($f === "." or $f === ".." or $f === "session.lock"){
					continue;
				}
				$this->copyTree($src . DIRECTORY_SEPARATOR . $f, $dest . DIRECTORY_SEPARATOR . $f, $prev === "" ? "" : $prev . DIRECTORY_SEPARATOR . $f, $result);
			}
			closedir($dh);
			return;
		}
		if(!is_file($src)){
			return;
		}
		$parent = dirname($dest);
		if(!is_dir($parent)){
			@mkdir($parent, 0777, true);
		}
		$linked = false;
		if($prev !== "" and is_file($prev)){
			clearstatcache(true, $src);
			clearstatcache(true, $prev);
			if(@filesize($src) === @filesize($prev) and @filemtime($src) === @filemtime($prev)){
				if(@link($prev, $dest)){
					$linked = true;
					$result["linked"]++;
				}
			}
		}
		if(!$linked){
			if(@copy($src, $dest)){
				@touch($dest, @filemtime($src));
				$result["copied"]++;
			}
		}
		$result["files"]++;
	}

	public function onCompletion(Server $server){
		$manager = $server->getBackupManager();
		if($manager !== null){
			$manager->onTaskComplete($this->tier, (array) $this->getResult());
		}
	}
}
