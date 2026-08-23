<?php

namespace pocketmine\web;

use pocketmine\Server;
use pocketmine\utils\TextFormat;
use pocketmine\lang\BaseLang;

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
			// $this->server->getLogger()->error("WebPanel: 无法创建 socket");
			$this->server->getLogger()->error($this->server->getLanguage()->translateString("pocketmine.web.cannot.reateSocket", [$this->port]));
			return;
		}

		@socket_set_option($this->socket, SOL_SOCKET, SO_REUSEADDR, 1);
		@socket_set_option($this->socket, SOL_SOCKET, SO_REUSEPORT, 1);

		if(!@socket_bind($this->socket, "0.0.0.0", $this->port)){
			$this->server->getLogger()->error($this->server->getLanguage()->translateString("pocketmine.web.cannot.bindPort", [$this->port]));
			return;
		}

		if(!@socket_listen($this->socket)){
			// $this->server->getLogger()->error("WebPanel: 无法监听端口 {$this->port}");
			$this->server->getLogger()->error($this->server->getLanguage()->translateString("pocketmine.web.cannot.listenPort", [$this->port]));
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
<title>{$motd}</title>
<style>
*{margin:0;padding:0;box-sizing:border-box}
body{font-family:"SF Mono",Menlo,monospace;background:#0d1117;color:#8b949e;padding:16px;max-width:960px;margin:0 auto;font-size:13px}
h1{font-size:16px;color:#c9d1d9;margin-bottom:12px;font-weight:400}
.row{display:flex;gap:8px;margin-bottom:12px}
.row>*{flex:1}
.stat{background:#161b22;border:1px solid #21262d;padding:10px;border-radius:4px;text-align:center}
.stat .n{font-size:20px;color:#58a6ff;font-weight:600}
.stat .l{font-size:10px;color:#484f58;margin-top:2px;text-transform:uppercase}
.charts{display:flex;gap:8px;margin-bottom:12px}
.charts>div{background:#161b22;border:1px solid #21262d;padding:8px;border-radius:4px;flex:1}
.charts canvas{display:block;width:100%;height:80px}
.charts .l{font-size:10px;color:#484f58;text-align:center;margin-top:4px;text-transform:uppercase}
table{width:100%;border-collapse:collapse;margin-bottom:12px}
th,td{padding:3px 6px;text-align:left;border-bottom:1px solid #21262d}
th{color:#484f58;font-size:10px;font-weight:400;text-transform:uppercase}
td{color:#8b949e}
.info td:first-child{color:#484f58;width:80px}
.footer{text-align:center;color:#21262d;font-size:10px;margin-top:20px}
</style>
</head>
<body>
<h1>{$motd}</h1>

<div class="row" id="stats">
<div class="stat"><div class="n" id="s-players">0</div><div class="l">Players</div></div>
<div class="stat"><div class="n" id="s-tps">0</div><div class="l">TPS</div></div>
<div class="stat"><div class="n" id="s-load">0%</div><div class="l">Load</div></div>
<div class="stat"><div class="n" id="s-mem">0</div><div class="l">MB</div></div>
<div class="stat"><div class="n" id="s-uptime">0</div><div class="l">Uptime</div></div>
</div>

<div class="charts">
<div><canvas id="ch-tps"></canvas><div class="l">TPS</div></div>
<div><canvas id="ch-mem"></canvas><div class="l">Memory MB</div></div>
<div><canvas id="ch-load"></canvas><div class="l">Load %</div></div>
</div>

<table><thead><tr><th>Player</th><th>Ping</th><th>World</th></tr></thead><tbody id="player-list"></tbody></table>

<table class="info">
<tr><td>Version</td><td>{$version}</td></tr>
<tr><td>MCPE</td><td>{$mcpeVer}</td></tr>
<tr><td>PHP</td><td>{$phpVer}</td></tr>
<tr><td>OS</td><td>{$os}</td></tr>
<tr><td>Port</td><td>{$port}</td></tr>
<tr><td>Gamemode</td><td>{$gm}</td></tr>
</table>

<div class="footer">Zenith</div>

<script>
var buf={tps:[],mem:[],load:[]};
function draw(id,data,color){
 var c=document.getElementById(id),ctx=c.getContext('2d');
 var w=300,h=90,pad=30;
 c.width=w*2;c.height=h*2;c.style.width=w+'px';c.style.height=h+'px';
 ctx.scale(2,2);
 ctx.clearRect(0,0,w,h);
 var plotW=w-pad;
 if(data.length<2){ctx.fillStyle='#484f58';ctx.font='10px monospace';ctx.fillText('--',pad,50);return}
 var mx=Math.max.apply(null,data);
 mx=Math.ceil(mx*1.1);if(mx<1)mx=1;

 // grid + labels
 ctx.strokeStyle='#21262d';ctx.lineWidth=0.5;
 ctx.fillStyle='#484f58';ctx.font='9px monospace';ctx.textAlign='right';
 for(var i=0;i<3;i++){
  var v=i==0?mx:i==1?Math.round(mx/2):0;
  var y=h-10-(v/mx*(h-20));
  ctx.beginPath();ctx.moveTo(pad,y);ctx.lineTo(w,y);ctx.stroke();
  ctx.fillText(v,pad-4,y+3);
 }

 // line
 ctx.beginPath();ctx.strokeStyle=color;ctx.lineWidth=1.5;
 var step=plotW/(Math.min(data.length,60)-1),x=pad;
 data.slice(-60).forEach(function(v,i){
  var y=h-10-(v/mx*(h-20));
  if(i==0)ctx.moveTo(x,y);else ctx.lineTo(x,y);
  x+=step;
 });
 ctx.stroke();
}
function update(){
 fetch('/api').then(function(r){return r.json()}).then(function(d){
  var s=d.server,sys=d.system;
  document.getElementById('s-players').textContent=sys.players+'/'+sys.maxPlayers;
  document.getElementById('s-tps').textContent=s.tps;
  document.getElementById('s-load').textContent=s.tpsUsage+'%';
  document.getElementById('s-mem').textContent=sys.memory;
  document.getElementById('s-uptime').textContent=s.uptime;

  buf.tps.push(s.tps);if(buf.tps.length>60)buf.tps.shift();
  buf.mem.push(sys.memory);if(buf.mem.length>60)buf.mem.shift();
  buf.load.push(s.tpsUsage);if(buf.load.length>60)buf.load.shift();

  draw('ch-tps',buf.tps,'#58a6ff');
  draw('ch-mem',buf.mem,'#3fb950');
  draw('ch-load',buf.load,'#d29922');

  var pl=document.getElementById('player-list');pl.innerHTML='';
  d.players.forEach(function(p){pl.innerHTML+='<tr><td>'+esc(p.name)+'</td><td>'+p.ping+'ms</td><td>'+esc(p.level)+'</td></tr>'});
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
