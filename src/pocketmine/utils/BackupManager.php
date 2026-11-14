<?php

namespace pocketmine\utils;

use pocketmine\Server;
use pocketmine\scheduler\CallbackTask;
use pocketmine\scheduler\BackupTask;

class BackupManager{

	const DEFAULT_CONFIG = "---\n"
		. "# ============================================================\n"
		. "#  自动备份配置 (backup.yml)\n"
		. "#  修改后执行 /backup reload 生效（tick 修改需重启服务器）\n"
		. "# ============================================================\n"
		. "\n"
		. "# 是否启用自动备份\n"
		. "enabled: true\n"
		. "\n"
		. "# 调度检查间隔（秒）：每隔多少秒检查一次是否有到期的备份。\n"
		. "# 想要“几秒备份一次”时请把 tick 也调小（例如 tick: 5），否则最短延迟约一个 tick。\n"
		. "tick: 30\n"
		. "\n"
		. "# 磁盘剩余空间低于该值(MB)时跳过备份，避免撑满磁盘\n"
		. "min-free-mb: 2000\n"
		. "\n"
		. "# 【分时段备份】与 backup.sh 一致：1分钟 / 5分钟 / 30分钟 / 1小时 / 1天 / 1个月。\n"
		. "# interval 单位是【秒】，keep 是最多保留份数，超出后自动删除最旧的。\n"
		. "# 需要别的间隔可自行增删时段，例如要“每10秒”就加一个 \"10s\": {interval: 10, keep: 6}。\n"
		. "tiers:\n"
		. "  \"1min\":\n"
		. "    interval: 60\n"
		. "    keep: 3\n"
		. "  \"5min\":\n"
		. "    interval: 300\n"
		. "    keep: 3\n"
		. "  \"30min\":\n"
		. "    interval: 1800\n"
		. "    keep: 3\n"
		. "  \"1h\":\n"
		. "    interval: 3600\n"
		. "    keep: 4\n"
		. "  \"1day\":\n"
		. "    interval: 86400\n"
		. "    keep: 6\n"
		. "  \"1month\":\n"
		. "    interval: 2592000\n"
		. "    keep: 6\n"
		. "\n"
		. "# 需要备份的目录/文件（相对服务器根目录）\n"
		. "include:\n"
		. "  - config\n"
		. "  - players\n"
		. "  - player\n"
		. "  - maps\n"
		. "  - worlds\n"
		. "  - plugins\n"
		. "  - resource_packs\n"
		. "  - pocketmine.yml\n"
		. "  - genisys.yml\n"
		. "  - server.properties\n"
		. "  - permissions.yml\n"
		. "  - ops.txt\n"
		. "  - banned.txt\n"
		. "  - banned-ips.txt\n"
		. "  - whitelist.txt\n"
		. "  - white-list.txt\n"
		. "  - src\n"
		. "  - start.sh\n"
		. "  - php.ini\n"
		. "  - PocketMine-MP.phar\n";

	const DEFAULT_INCLUDE = [
		"config", "players", "player", "maps", "worlds", "plugins", "resource_packs",
		"pocketmine.yml", "genisys.yml", "server.properties", "permissions.yml",
		"ops.txt", "banned.txt", "banned-ips.txt", "whitelist.txt", "white-list.txt",
		"src", "start.sh", "php.ini", "PocketMine-MP.phar"
	];

	const DEFAULT_TIERS = [
		"1min" => ["interval" => 60, "keep" => 3],
		"5min" => ["interval" => 300, "keep" => 3],
		"30min" => ["interval" => 1800, "keep" => 3],
		"1h" => ["interval" => 3600, "keep" => 4],
		"1day" => ["interval" => 86400, "keep" => 6],
		"1month" => ["interval" => 2592000, "keep" => 6]
	];

	/** @var Server */
	private $server;
	/** @var string */
	private $dataPath;
	/** @var string */
	private $backupRoot;
	/** @var string */
	private $stateDir;
	/** @var Config */
	private $config;
	/** @var bool */
	private $enabled = true;
	/** @var array */
	private $tiers = [];
	/** @var array */
	private $include = [];
	/** @var int */
	private $minFreeMb = 2000;
	/** @var int */
	private $tick = 30;
	/** @var bool */
	private $running = false;
	/** @var string|null */
	private $runningTier = null;
	/** @var bool 本进程是否已完成启动时的调度对齐 */
	private $bootAligned = false;
	/** @var string */
	private $lastMessage = "";
	/** @var string */
	private $configPath;

	public function __construct(Server $server){
		$this->server = $server;
		$this->dataPath = $server->getDataPath();
		$this->backupRoot = $this->dataPath . "backups" . DIRECTORY_SEPARATOR;
		$this->stateDir = $this->backupRoot . ".state" . DIRECTORY_SEPARATOR;
		@mkdir($this->dataPath . "config" . DIRECTORY_SEPARATOR, 0777, true);
		$this->configPath = $this->dataPath . "config" . DIRECTORY_SEPARATOR . "backup.yml";
		$this->loadConfig();
		@mkdir($this->stateDir, 0700, true);
		if($this->enabled){
			$server->getScheduler()->scheduleRepeatingTask(new CallbackTask([$this, "tick"], []), max(5, $this->tick) * 20);
		}
	}

	private function loadConfig(){
		if(!is_file($this->configPath)){
			@file_put_contents($this->configPath, self::DEFAULT_CONFIG);
		}
		$this->config = new Config($this->configPath, Config::YAML);

		$this->enabled = (bool) $this->config->get("enabled", true);
		$this->tick = max(5, (int) $this->config->get("tick", 30));
		$this->minFreeMb = (int) $this->config->get("min-free-mb", 2000);

		$tiers = (array) $this->config->get("tiers", []);
		$this->tiers = [];
		foreach($tiers as $name => $t){
			$name = $this->sanitizeTier($name);
			if($name === ""){
				continue;
			}
			$this->tiers[$name] = [
				"interval" => (int) (isset($t["interval"]) ? $t["interval"] : 0),
				"keep" => (int) (isset($t["keep"]) ? $t["keep"] : 1)
			];
		}
		if(count($this->tiers) === 0){
			$this->tiers = self::DEFAULT_TIERS;
		}

		$include = (array) $this->config->get("include", []);
		$this->include = count($include) > 0 ? $include : self::DEFAULT_INCLUDE;
	}

	/**
	 * 重新载入 backup.yml（tick 修改需重启）
	 */
	public function reload(){
		$this->loadConfig();
		$this->lastMessage = "已重新载入 backup.yml";
		$this->server->getLogger()->info("[Backup] " . $this->lastMessage);
	}

	public function getConfigPath(){
		return $this->configPath;
	}

	public function isEnabled(){
		return $this->enabled;
	}

	public function isRunning(){
		return $this->running;
	}

	public function getRunningTier(){
		return $this->runningTier;
	}

	public function getLastMessage(){
		return $this->lastMessage;
	}

	public function getBackupRoot(){
		return $this->backupRoot;
	}

	public function getInclude(){
		return $this->include;
	}

	public function getTiers(){
		return $this->tiers;
	}

	public function getConfig(){
		return $this->config;
	}

	public function getTick(){
		return $this->tick;
	}

	/**
	 * Sanitizes a tier name so it can be safely used as a path component.
	 */
	public function sanitizeTier($tier){
		return strtolower(preg_replace('/[^A-Za-z0-9_\-]/', '', strval($tier)));
	}

	public function tick($task = null){
		if(!$this->enabled or $this->running){
			return;
		}
		$now = time();

		// 启动对齐：每个进程只在首个 tick 执行一次，避免停机后补跑造成启动风暴
		if(!$this->bootAligned){
			$this->bootAligned = true;
			$this->alignSchedules($now);
			return;
		}

		foreach($this->tiers as $name => $t){
			if($t["interval"] <= 0){
				continue;
			}
			$state = $this->stateDir . $name . ".last";
			if(!is_file($state)){
				// 运行时状态文件丢失：从当前时间起算一个完整 interval，不立即补跑
				@file_put_contents($state, $now);
				continue;
			}
			$last = (int) @file_get_contents($state);
			if(($now - $last) >= $t["interval"]){
				$this->runBackup($name);
				return;
			}
		}
	}

	/**
	 * 启动时对齐各时段的计时，不补跑停机期间错过的备份。
	 *
	 * 旧实现对每个时段用 `now - interval`（或最近快照时间）初始化，等于启动瞬间
	 * 所有时段同时到期；而 tick 每 30 秒只执行一个时段，于是表现为
	 * “每 30 秒备份一次”的瀑布式全量备份。
	 *
	 * 现在：
	 *  - 状态文件缺失（全新安装/被清理）：从当前时间起算一个完整 interval；
	 *  - 状态文件已过期：把计时推进到该时段自身周期的相位点，等到下一个周期点再备份。
	 * 相位按“累计运行时间”推进，因此频繁重启也不会让长周期时段永远轮不到。
	 */
	private function alignSchedules($now){
		$aligned = [];
		foreach($this->tiers as $name => $t){
			if($t["interval"] <= 0){
				continue;
			}
			$state = $this->stateDir . $name . ".last";
			if(!is_file($state)){
				@file_put_contents($state, $now);
				continue;
			}
			$last = (int) @file_get_contents($state);
			$elapsed = $now - $last;
			if($elapsed < $t["interval"]){
				continue;
			}
			// 只抹掉“多出来的周期”，保留已累计运行时间的相位
			@file_put_contents($state, $now - ($elapsed % $t["interval"]));
			$aligned[] = $name;
		}
		if(count($aligned) > 0){
			$this->server->getLogger()->info("[Backup] 启动对齐：已错过的时段 [" . implode(", ", $aligned) . "] 不补跑，按原周期继续");
		}
	}

	public function runBackup($tier, $manual = false){
		$tier = $this->sanitizeTier($tier);
		if($tier === ""){
			return false;
		}
		if($this->running){
			$this->lastMessage = "已有备份任务在执行";
			return false;
		}
		if(!is_dir($this->backupRoot)){
			@mkdir($this->backupRoot, 0700, true);
		}
		$free = @disk_free_space($this->backupRoot);
		if($free !== false and $free < ($this->minFreeMb * 1048576.0)){
			$this->lastMessage = "磁盘剩余低于 " . $this->minFreeMb . "MB，跳过备份";
			$this->server->getLogger()->warning("[Backup] " . $this->lastMessage);
			return false;
		}
		$dir = $this->backupRoot . $tier;
		if(!is_dir($dir)){
			@mkdir($dir, 0700, true);
		}
		$dest = $dir . DIRECTORY_SEPARATOR . date("Ymd-His");
		$snaps = $this->listSnapshots($tier);
		$prev = count($snaps) > 0 ? $dir . DIRECTORY_SEPARATOR . $snaps[count($snaps) - 1] : "";
		$this->running = true;
		$this->runningTier = $tier;
		@file_put_contents($this->stateDir . $tier . ".running", time());
		$this->server->getLogger()->info("[Backup] 开始备份 [" . $tier . "] -> " . $dest);
		$this->server->getScheduler()->scheduleAsyncTask(new BackupTask($tier, $this->dataPath, $dest, $prev, $this->include));
		return true;
	}

	public function onTaskComplete($tier, array $result){
		$tier = $this->sanitizeTier($tier);
		$this->running = false;
		$this->runningTier = null;
		@file_put_contents($this->stateDir . $tier . ".last", time());
		@unlink($this->stateDir . $tier . ".running");
		if(!empty($result["success"])){
			$removed = $this->prune($tier);
			$this->lastMessage = sprintf("备份完成 [%s] 文件:%d 链接:%d 复制:%d 清理:%d",
				$tier,
				isset($result["files"]) ? $result["files"] : 0,
				isset($result["linked"]) ? $result["linked"] : 0,
				isset($result["copied"]) ? $result["copied"] : 0,
				$removed);
			$this->server->getLogger()->info("[Backup] " . $this->lastMessage);
		}else{
			$dest = isset($result["dest"]) ? (string) $result["dest"] : "";
			if($dest !== "" and is_dir($dest)){
				$this->removeDir($dest);
			}
			$this->lastMessage = "备份失败 [" . $tier . "]: " . (isset($result["message"]) ? $result["message"] : "unknown");
			$this->server->getLogger()->error("[Backup] " . $this->lastMessage);
		}
	}

	public function listSnapshots($tier){
		$tier = $this->sanitizeTier($tier);
		$d = $this->backupRoot . $tier;
		$out = [];
		if(!is_dir($d)){
			return $out;
		}
		foreach(scandir($d) as $f){
			if($f === "." or $f === ".."){
				continue;
			}
			if(is_dir($d . DIRECTORY_SEPARATOR . $f) and preg_match('/^[0-9]{8}-[0-9]{6}$/', $f)){
				$out[] = $f;
			}
		}
		sort($out);
		return $out;
	}

	public function tierSize($tier){
		$tier = $this->sanitizeTier($tier);
		$dir = $this->backupRoot . $tier;
		if(!is_dir($dir)){
			return 0;
		}
		$total = 0;
		$inodes = [];
		try{
			$it = new \RecursiveIteratorIterator(new \RecursiveDirectoryIterator($dir, \FilesystemIterator::SKIP_DOTS), \RecursiveIteratorIterator::LEAVES_ONLY);
			foreach($it as $f){
				if(!$f->isFile()){
					continue;
				}
				$ino = @$f->getInode();
				if($ino !== false and isset($inodes[$ino])){
					continue;
				}
				if($ino !== false){
					$inodes[$ino] = true;
				}
				$total += $f->getSize();
			}
		}catch(\Throwable $e){
			return $total;
		}
		return $total;
	}

	public function prune($tier){
		$tier = $this->sanitizeTier($tier);
		if(!isset($this->tiers[$tier])){
			return 0;
		}
		$keep = max(0, (int) $this->tiers[$tier]["keep"]);
		$snaps = $this->listSnapshots($tier);
		$removed = 0;
		while(count($snaps) > $keep){
			$old = array_shift($snaps);
			$this->removeDir($this->backupRoot . $tier . DIRECTORY_SEPARATOR . $old);
			$removed++;
		}
		return $removed;
	}

	public function cleanAll(){
		$n = 0;
		foreach($this->tiers as $tier => $t){
			$n += $this->prune($tier);
		}
		return $n;
	}

	public function removeAllSnapshots($tier){
		$tier = $this->sanitizeTier($tier);
		$n = 0;
		foreach($this->listSnapshots($tier) as $s){
			$this->removeDir($this->backupRoot . $tier . DIRECTORY_SEPARATOR . $s);
			$n++;
		}
		return $n;
	}

	public function removeDir($dir){
		if(!is_dir($dir)){
			return;
		}
		$it = new \RecursiveIteratorIterator(new \RecursiveDirectoryIterator($dir, \FilesystemIterator::SKIP_DOTS), \RecursiveIteratorIterator::CHILD_FIRST);
		foreach($it as $f){
			if($f->isDir()){
				@rmdir($f->getPathname());
			}else{
				@unlink($f->getPathname());
			}
		}
		@rmdir($dir);
	}

	public function humanSize($bytes){
		$bytes = (float) $bytes;
		$units = ["B", "KB", "MB", "GB", "TB"];
		$i = 0;
		while($bytes >= 1024 and $i < count($units) - 1){
			$bytes /= 1024;
			$i++;
		}
		return round($bytes, 2) . $units[$i];
	}
}
