---
name: pregenerate-chunks
description: 为 PocketMine-MP 服务端添加首次启动时预生成世界地形区块的功能，支持多世界、进度显示
source: auto-skill
extracted_at: '2026-06-14T03:30:04.649Z'
---

# 首次启动地形预生成（PocketMine-MP / Genisys / InCore）

## 目标
服务器首次启动时（世界目录尚不存在），在玩家加入前预生成多个世界的地形区块，并在控制台每 10% 显示一次进度。

## 适用场景
- PocketMine-MP / Genisys / InCore 等 PHP 基岩版服务端
- 需要首次开服时预加载主世界、地狱、末地、空岛等世界
- 已有世界时不重复加载

## 实现方案

### 1. 添加跟踪属性
在 `Server` 类（`src/pocketmine/Server.php`）属性区添加：

```php
/** @var string[] 新生成的需要预加载的世界列表 */
private $preGenerateQueue = [];

/** @var int 预加载区块半径 */
private $preGenerateRadius = 8;
```

### 2. 在构造函数中捕获新生成的世界
找到所有调用 `generateLevel()` 的地方，将返回值作为是否首次生成的标志：

```php
// 原代码
$this->generateLevel($name, ...);
// 改为
if($this->generateLevel($name, ...)){
    $this->preGenerateQueue[] = $name;
}
```

需要修改的几处：
- 默认主世界（`level-name` 配置）
- 地狱（`nether`，如果启用）
- 末地（`ender`，如果启用）
- 空岛（`skyworld`，如果启用）

### 3. 添加预生成方法

```php
private function preGenerateWorlds(){
    if(empty($this->preGenerateQueue)) return;

    // 按指定顺序排序
    $order = ["world", "nether", "ender", "skyworld"];
    usort($this->preGenerateQueue, function($a, $b) use ($order){
        $ia = array_search($a, $order);
        $ib = array_search($b, $order);
        if($ia === false && $ib === false) return 0;
        if($ia === false) return 1;
        if($ib === false) return -1;
        return $ia - $ib;
    });

    $this->logger->notice("检测到新世界，开始预加载地形...");

    foreach($this->preGenerateQueue as $worldName){
        $level = $this->getLevelByName($worldName);
        if(!($level instanceof Level)) continue;

        $this->logger->notice("正在预加载世界: $worldName");

        $spawn = $level->getSpawnLocation();
        $centerX = $spawn->getX() >> 4;
        $centerZ = $spawn->getZ() >> 4;

        $radius = $this->preGenerateRadius;
        $total = (2 * $radius + 1) * (2 * $radius + 1);
        $count = 0;
        $lastProgress = -1;

        // 按距出生点距离排序，中心优先
        $orderByDist = [];
        for($x = -$radius; $x <= $radius; ++$x){
            for($z = -$radius; $z <= $radius; ++$z){
                $dist = $x * $x + $z * $z;
                $orderByDist[$dist][] = [$centerX + $x, $centerZ + $z];
            }
        }
        ksort($orderByDist);

        foreach($orderByDist as $dist => $chunks){
            foreach($chunks as [$chunkX, $chunkZ]){
                $level->generateChunk($chunkX, $chunkZ, true);
                $level->populateChunk($chunkX, $chunkZ, true);
                ++$count;

                $progress = (int)($count / $total * 100);
                if($progress >= $lastProgress + 10){
                    $lastProgress = $progress;
                    $this->logger->info("  §7[$worldName] §a{$progress}% §7($count/$total)");
                }
            }
        }

        $this->logger->notice("世界 $worldName 预加载完成 (§b{$count}§f 个区块)");
    }

    $this->preGenerateQueue = [];
}
```

### 4. 在 `start()` 方法中调用
在服务器启动成功日志之后、主循环之前调用：

```php
$this->logger->info($this->getLanguage()->translateString("pocketmine.server.startFinished", [...]));
$this->preGenerateWorlds();  // <-- 新增
```

## 关键设计要点
- **仅首次生效**：通过 `generateLevel()` 返回 `false`（世界已存在）来跳过，后续重启不执行
- **异步友好**：`generateChunk()` 和 `populateChunk()` 内部使用异步任务，提交后立即返回，不阻塞主线程
- **按距中心排序**：优先生成出生点附近的区块，玩家一进入就能看到周围地形
- **进度显示**：每 10% 报告一次，避免刷屏
- **世界顺序**：通过 `usort` + 优先级数组控制加载顺序
