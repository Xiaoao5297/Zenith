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
		$motd = htmlspecialchars($server->getMotd(), ENT_QUOTES, 'UTF-8');
		$version = htmlspecialchars($server->getPocketMineVersion(), ENT_QUOTES, 'UTF-8');
		$apiVer = htmlspecialchars($server->getApiVersion(), ENT_QUOTES, 'UTF-8');
		$mcpeVer = htmlspecialchars($server->getVersion(), ENT_QUOTES, 'UTF-8');
		$phpVer = htmlspecialchars(PHP_VERSION, ENT_QUOTES, 'UTF-8');
		$os = htmlspecialchars(PHP_OS, ENT_QUOTES, 'UTF-8');
		$port = (int) $server->getPort();
		$gm = htmlspecialchars($server->getGamemode(), ENT_QUOTES, 'UTF-8');

		return <<<HTML
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>{$motd} - 面板</title>
<style>
*{margin:0;padding:0;box-sizing:border-box}
body{font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif;background:#111;color:#ccc;padding:20px;max-width:900px;margin:0 auto}
h1{font-size:20px;margin-bottom:16px;color:#e94560}
.grid{display:grid;grid-template-columns:repeat(3,1fr);gap:8px;margin-bottom:16px}
.card{background:#1a1a2e;padding:12px;border-radius:6px;text-align:center}
.card .v{font-size:22px;font-weight:bold;color:#e94560}
.card .l{font-size:11px;color:#666;margin-top:2px}
.good{color:#4ecca3}
.warn{color:#ffc107}
.bad{color:#e94560}
h2{font-size:14px;margin:12px 0 6px;color:#e94560}
table{width:100%;border-collapse:collapse;margin-bottom:12px;font-size:13px}
th,td{padding:4px 8px;text-align:left;border-bottom:1px solid #222}
th{background:#1a1a2e;color:#888;font-size:11px;text-transform:uppercase}
td{color:#ccc}
.footer{text-align:center;color:#444;font-size:11px;margin-top:20px}
</style>
</head>
<body>
<h1>⚡ {$motd}</h1>

<div class="grid" id="stats">
<div class="card"><div class="v" id="s-players">0/0</div><div class="l">玩家</div></div>
<div class="card"><div class="v" id="s-tps">0</div><div class="l">TPS</div></div>
<div class="card"><div class="v" id="s-load">0%</div><div class="l">负载</div></div>
<div class="card"><div class="v" id="s-mem">0 MB</div><div class="l">内存</div></div>
<div class="card"><div class="v" id="s-peak">0 MB</div><div class="l">峰值</div></div>
<div class="card"><div class="v" id="s-uptime">0</div><div class="l">运行</div></div>
</div>

<h2>👤 玩家 <span id="player-count" style="color:#666;font-size:12px">0</span></h2>
<table><thead><tr><th>名字</th><th>延迟</th><th>IP</th><th>世界</th></tr></thead><tbody id="player-list"></tbody></table>
<p id="no-players" style="color:#444;font-size:13px">暂无玩家</p>

<h2>🌍 世界</h2>
<table><thead><tr><th>世界</th><th>玩家</th><th>实体</th><th>区块</th></tr></thead><tbody id="level-list"></tbody></table>

<h2>📊 信息</h2>
<table>
<tr><td style="color:#666">服务端</td><td>{$version} (API {$apiVer})</td></tr>
<tr><td style="color:#666">MCPE</td><td>{$mcpeVer}</td></tr>
<tr><td style="color:#666">PHP</td><td>{$phpVer}</td></tr>
<tr><td style="color:#666">系统</td><td>{$os}</td></tr>
<tr><td style="color:#666">端口</td><td>{$port}</td></tr>
<tr><td style="color:#666">模式</td><td>{$gm}</td></tr>
</table>

<div class="footer">InCore Pro &middot; 实时更新</div>

<script>
function cls(v){return v>=18?'good':v>=10?'warn':'bad'}
function update(){
 fetch('/api').then(r=>r.json()).then(d=>{
  var s=d.server, sys=d.system;
  document.getElementById('s-players').textContent=sys.players+'/'+sys.maxPlayers;
  var tps=document.getElementById('s-tps');tps.textContent=s.tps;tps.className='v '+cls(s.tps);
  var ld=document.getElementById('s-load');ld.textContent=s.tpsUsage+'%';ld.className='v '+cls(100-s.tpsUsage);
  document.getElementById('s-mem').textContent=sys.memory+'MB';
  document.getElementById('s-peak').textContent=sys.memoryPeak+'MB';
  document.getElementById('s-uptime').textContent=s.uptime;

  var pl=document.getElementById('player-list');pl.innerHTML='';
  document.getElementById('player-count').textContent=sys.players;
  document.getElementById('no-players').style.display=d.players.length?'none':'block';
  d.players.forEach(function(p){pl.innerHTML+='<tr><td>'+esc(p.name)+'</td><td>'+p.ping+'ms</td><td>'+esc(p.ip)+'</td><td>'+esc(p.level)+'</td></tr>'});

  var ll=document.getElementById('level-list');ll.innerHTML='';
  d.levels.forEach(function(l){ll.innerHTML+='<tr><td>'+esc(l.name)+'</td><td>'+l.players+'</td><td>'+l.entities+'</td><td>'+l.chunks+'</td></tr>'});
 }).catch(function(){})
}
function esc(s){var d=document.createElement('div');d.appendChild(document.createTextNode(s));return d.innerHTML}
update();setInterval(update,1000);
</script>
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
