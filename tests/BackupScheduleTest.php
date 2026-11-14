<?php

/**
 * Regression tests for BackupManager 的启动调度对齐（BUG：启动后每 30 秒备份一次）。
 *
 * 旧实现把每个时段的计时初始化为 `now - interval`，启动瞬间所有时段同时到期，
 * 而 tick 每 30 秒只执行一个时段，表现为启动后每 30 秒一次全量备份。
 * 现在的约定：
 *   - 全新状态（无 .last）：从当前时间起算一个完整 interval；
 *   - 已过期状态：对齐到该时段自身的周期相位，不补跑；
 *   - 正常运行中的到期时段仍然立即执行。
 *
 * 通过 Reflection 绕过依赖 Server 的构造函数，并子类化覆盖 runBackup()，
 * 因此不需要 PocketMine 运行时即可验证调度逻辑。
 */

require_once __DIR__ . "/../src/pocketmine/utils/BackupManager.php";

class BackupScheduleTestStubLogger{
	public function info($m){}
	public function warning($m){}
	public function error($m){}
}

class BackupScheduleTestStubServer{
	public function getLogger(){ return new BackupScheduleTestStubLogger(); }
}

class BackupScheduleTestManager extends \pocketmine\utils\BackupManager{
	/** @var string[] */
	public $ran = [];

	public function runBackup($tier, $manual = false){
		$this->ran[] = $tier;
		return true;
	}
}

class BackupScheduleTest{

	private const TIERS = [
		"1min" => ["interval" => 60, "keep" => 3],
		"5min" => ["interval" => 300, "keep" => 3],
		"30min" => ["interval" => 1800, "keep" => 3],
		"1h" => ["interval" => 3600, "keep" => 4],
		"1day" => ["interval" => 86400, "keep" => 6],
		"1month" => ["interval" => 2592000, "keep" => 6]
	];

	public static function run() : array{
		$dir = sys_get_temp_dir() . DIRECTORY_SEPARATOR . "zenith-backup-test-" . getmypid();
		$results = [];

		$fresh = self::makeManager($dir);
		if($fresh === null){
			return [["temp_dir", false, "cannot create " . $dir]];
		}
		self::wipe($dir);

		// 1. 全新状态：首个 tick 只建立计时，不立即备份
		$m = self::newManager($dir);
		$m->tick();
		$results[] = ["fresh_no_immediate_backup", $m->ran === [], "ran=[" . implode(",", $m->ran) . "]"];
		$allCreated = true;
		$now = time();
		foreach(self::TIERS as $name => $t){
			$file = $dir . "/" . $name . ".last";
			if(!is_file($file) or abs((int) file_get_contents($file) - $now) > 3){
				$allCreated = false;
			}
		}
		$results[] = ["fresh_state_starts_full_interval", $allCreated, "all .last initialised to now"];

		// 2. 停机后过期：对齐相位，不补跑
		self::wipe($dir);
		$now = time();
		file_put_contents($dir . "/1min.last", $now - 90);       // 过期 90s，余 30s
		file_put_contents($dir . "/5min.last", $now - 700);      // 过期 700s，余 200s
		file_put_contents($dir . "/30min.last", $now - 1800);    // 恰好一个周期
		file_put_contents($dir . "/1h.last", $now - 100);        // 未过期
		file_put_contents($dir . "/1day.last", $now - 90000);    // 过期 25h，余 1h
		// 1month 缺失

		$m = self::newManager($dir);
		$m->tick();
		$results[] = ["stale_no_catchup_burst", $m->ran === [], "ran=[" . implode(",", $m->ran) . "]"];

		$results[] = ["align_1min_phase", abs((int) file_get_contents($dir . "/1min.last") - ($now - 30)) <= 2, "next due ~30s"];
		$results[] = ["align_5min_phase", abs((int) file_get_contents($dir . "/5min.last") - ($now - 100)) <= 2, "next due ~200s"];
		$results[] = ["align_30min_full_period", abs((int) file_get_contents($dir . "/30min.last") - $now) <= 2, "next due ~1800s"];
		$results[] = ["align_1h_untouched", abs((int) file_get_contents($dir . "/1h.last") - ($now - 100)) <= 2, "not overdue, unchanged"];
		$dueIn = 86400 - (time() - (int) file_get_contents($dir . "/1day.last"));
		$results[] = ["align_1day_keeps_phase", $dueIn > 80000 and $dueIn <= 86400, "next due ~82800s, got " . $dueIn];
		$results[] = ["align_1month_created", abs((int) file_get_contents($dir . "/1month.last") - $now) <= 2, "missing state initialised to now"];

		// 3. 正常运行中到期：仍然立即执行（且每 tick 只跑一个）
		file_put_contents($dir . "/1min.last", time() - 61);
		file_put_contents($dir . "/5min.last", time() - 301);
		$m->ran = [];
		$m->tick();
		$results[] = ["due_tier_runs_immediately", $m->ran === ["1min"], "one backup per tick, got [" . implode(",", $m->ran) . "]"];

		self::wipe($dir);
		@rmdir($dir);
		return $results;
	}

	private static function makeManager(string $dir){
		if(!is_dir($dir) and !@mkdir($dir, 0700, true)){
			return null;
		}
		return true;
	}

	private static function newManager(string $dir) : BackupScheduleTestManager{
		$class = new ReflectionClass(BackupScheduleTestManager::class);
		$parent = new ReflectionClass(\pocketmine\utils\BackupManager::class);
		/** @var BackupScheduleTestManager $m */
		$m = $class->newInstanceWithoutConstructor();
		$set = function($name, $value) use ($parent, $m){
			$p = $parent->getProperty($name);
			$p->setAccessible(true);
			$p->setValue($m, $value);
		};
		$set("server", new BackupScheduleTestStubServer());
		$set("enabled", true);
		$set("running", false);
		$set("stateDir", $dir . "/");
		$set("backupRoot", $dir . "/");
		$set("dataPath", $dir . "/");
		$set("tiers", self::TIERS);
		return $m;
	}

	private static function wipe(string $dir){
		foreach(glob($dir . "/*") as $f){
			@unlink($f);
		}
	}
}
