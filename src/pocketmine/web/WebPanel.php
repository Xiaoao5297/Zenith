<?php

namespace pocketmine\web;

use pocketmine\Server;
use pocketmine\utils\TextFormat;

class WebPanel{

	/** @var Server */
	private $server;

	/** @var resource */
	private $socket;

	/** @var int */
	private $port;

	/** @var bool */
	private $running = false;

	public function __construct(Server $server, int $port = 8080){
		$this->server = $server;
		$this->port = $port;
	}

	public function isRunning() : bool{
		return $this->running;
	}

	public function start(){
		$this->socket = @socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
		if($this->socket === false){
			$this->server->getLogger()->error("WebPanel: 无法创建 socket");
			return;
		}

		if(!@socket_bind($this->socket, "0.0.0.0", $this->port)){
			$this->server->getLogger()->error("WebPanel: 无法绑定端口 {$this->port}");
			return;
		}

		if(!@socket_listen($this->socket)){
			$this->server->getLogger()->error("WebPanel: 无法监听端口 {$this->port}");
			return;
		}

		@socket_set_nonblock($this->socket);
		$this->running = true;
		$this->server->getLogger()->info("WebPanel: http://0.0.0.0:{$this->port}");
	}

	public function tick(){
		if(!$this->running or $this->socket === false){
			return;
		}

		$client = @socket_accept($this->socket);
		if($client === false){
			return;
		}

		$request = @socket_read($client, 8192);
		if($request === false or $request === ""){
			@socket_close($client);
			return;
		}

		// 解析请求路径
		$path = "/";
		if(preg_match("#^GET\s+(/[^\s]*)#i", $request, $m)){
			$path = $m[1];
		}

		// 路由
		if($path === "/api"){
			$body = $this->getApiData();
			$mime = "application/json";
		}else{
			$body = $this->renderDashboard();
			$mime = "text/html; charset=utf-8";
		}

		$response = "HTTP/1.1 200 OK\r\n"
			. "Content-Type: {$mime}\r\n"
			. "Content-Length: " . strlen($body) . "\r\n"
			. "Connection: close\r\n"
			. "\r\n"
			. $body;

		@socket_write($client, $response);
		@socket_close($client);
	}

	public function shutdown(){
		$this->running = false;
		if($this->socket !== false){
			@socket_close($this->socket);
		}
	}

	private function getApiData() : string{
		$server = $this->server;
		$players = [];
		foreach($server->getOnlinePlayers() as $p){
			$players[] = [
				"name" => $p->getName(),
				"ping" => $p->getPing(),
				"ip" => $p->getAddress(),
				"port" => $p->getPort(),
				"level" => $p->getLevel()->getName(),
				"x" => round($p->x, 1),
				"y" => round($p->y, 1),
				"z" => round($p->z, 1),
			];
		}

		$levels = [];
		foreach($server->getLevels() as $level){
			$levels[] = [
				"name" => $level->getName(),
				"players" => count($level->getPlayers()),
				"entities" => count($level->getEntities()),
				"chunks" => count($level->getChunks()),
			];
		}

		return json_encode([
			"server" => [
				"name" => $server->getMotd(),
				"version" => $server->getPocketMineVersion(),
				"api" => $server->getApiVersion(),
				"mcpe" => $server->getVersion(),
				"gameType" => $server->getGamemode(),
				"port" => $server->getPort(),
				"uptime" => $this->formatUptime(microtime(true) - \pocketmine\START_TIME),
				"tps" => $server->getTicksPerSecond(),
				"tpsUsage" => $server->getTickUsage(),
			],
			"system" => [
				"memory" => round(memory_get_usage(true) / 1024 / 1024, 1),
				"memoryPeak" => round(memory_get_peak_usage(true) / 1024 / 1024, 1),
				"os" => PHP_OS,
				"php" => PHP_VERSION,
				"players" => count($players),
				"maxPlayers" => $server->getMaxPlayers(),
			],
			"players" => $players,
			"levels" => $levels,
		]);
	}

	private function renderDashboard() : string{
		$server = $this->server;
		$online = count($server->getOnlinePlayers());
		$max = $server->getMaxPlayers();
		$tps = $server->getTicksPerSecond();
		$tickUsage = $server->getTickUsage();
		$mem = round(memory_get_usage(true) / 1024 / 1024, 1);
		$peak = round(memory_get_peak_usage(true) / 1024 / 1024, 1);
		$uptime = $this->formatUptime(microtime(true) - \pocketmine\START_TIME);
		$levels = $server->getLevels();
		$tpsClass = $tps >= 18 ? "good" : ($tps >= 10 ? "warn" : "bad");
		$loadClass = $tickUsage < 50 ? "good" : ($tickUsage < 80 ? "warn" : "bad");
		$noPlayers = $online === 0 ? "<p style='color:#666'>暂无玩家在线</p>" : "";

		$playerRows = "";
		foreach($server->getOnlinePlayers() as $p){
			$playerRows .= "<tr><td>{$p->getName()}</td><td>{$p->getPing()}ms</td><td>{$p->getAddress()}</td><td>{$p->getLevel()->getName()}</td></tr>";
		}

		$levelRows = "";
		foreach($levels as $level){
			$levelRows .= "<tr><td>{$level->getName()}</td><td>" . count($level->getPlayers()) . "</td><td>" . count($level->getEntities()) . "</td><td>" . count($level->getChunks()) . "</td></tr>";
		}

		return <<<HTML
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>{$server->getMotd()} - 控制面板</title>
<style>
*{margin:0;padding:0;box-sizing:border-box}
body{font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif;background:#1a1a2e;color:#eee;padding:20px}
h1{font-size:24px;margin-bottom:20px;color:#e94560}
h2{font-size:18px;margin:20px 0 10px;color:#0f3460;background:#e94560;display:inline-block;padding:4px 12px;border-radius:4px;color:#fff}
.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:12px;margin-bottom:20px}
.card{background:#16213e;padding:15px;border-radius:8px;text-align:center}
.card .value{font-size:28px;font-weight:bold;color:#e94560}
.card .label{font-size:12px;color:#888;margin-top:4px}
.card .good{color:#4ecca3}
.card .warn{color:#ffc107}
.card .bad{color:#e94560}
table{width:100%;border-collapse:collapse;margin-bottom:20px}
th,td{padding:8px 12px;text-align:left;border-bottom:1px solid #333}
th{background:#0f3460;color:#eee;font-size:13px}
td{font-size:14px}
tr:hover{background:#1a1a3e}
.footer{margin-top:30px;text-align:center;color:#555;font-size:12px}
a{color:#4ecca3;text-decoration:none}
</style>
</head>
<body>
<h1>⚡ {$server->getMotd()}</h1>

<div class="grid">
<div class="card"><div class="value">{$online}/{$max}</div><div class="label">在线玩家</div></div>
<div class="card"><div class="value {$tpsClass}">{$tps}</div><div class="label">TPS</div></div>
<div class="card"><div class="value {$loadClass}">{$tickUsage}%</div><div class="label">Tick 负载</div></div>
<div class="card"><div class="value">{$mem}MB</div><div class="label">内存使用</div></div>
<div class="card"><div class="value">{$peak}MB</div><div class="label">内存峰值</div></div>
<div class="card"><div class="value">{$uptime}</div><div class="label">运行时间</div></div>
</div>

<h2>👤 在线玩家</h2>
<table>
<tr><th>玩家</th><th>延迟</th><th>IP</th><th>所在世界</th></tr>
{$playerRows}
</table>
{$noPlayers}

<h2>🌍 世界列表</h2>
<table>
<tr><th>世界</th><th>玩家</th><th>实体</th><th>区块</th></tr>
{$levelRows}
</table>

<h2>📊 服务器信息</h2>
<table>
<tr><td>版本</td><td>{$server->getPocketMineVersion()} (API {$server->getApiVersion()})</td></tr>
<tr><td>MCPE 版本</td><td>{$server->getVersion()}</td></tr>
<tr><td>PHP 版本</td><td>" . PHP_VERSION . "</td></tr>
<tr><td>操作系统</td><td>" . PHP_OS . "</td></tr>
<tr><td>端口</td><td>{$server->getPort()}</td></tr>
<tr><td>游戏模式</td><td>{$server->getGamemode()}</td></tr>
</table>

<div class="footer">
<a href="/">🔄 刷新</a> &middot; <a href="/api">📡 API</a> &middot; InCore Pro WebPanel
</div>
</body>
</html>
HTML;
	}

	private function formatUptime(float $seconds) : string{
		$days = floor($seconds / 86400);
		$hours = floor(($seconds % 86400) / 3600);
		$mins = floor(($seconds % 3600) / 60);
		$secs = floor($seconds % 60);
		$parts = [];
		if($days > 0) $parts[] = "{$days}天";
		if($hours > 0) $parts[] = "{$hours}小时";
		if($mins > 0) $parts[] = "{$mins}分";
		$parts[] = "{$secs}秒";
		return implode("", $parts);
	}
}
