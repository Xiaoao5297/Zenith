<?php

namespace pocketmine\entity\ai\navigation;

use pocketmine\entity\Mob;
use pocketmine\math\Vector3;
use pocketmine\Server;

class PathNavigate{

    private const STATE_IDLE = 0;
    private const STATE_PATH_FOLLOWING = 1;
    private const STATE_FALLBACK = 2;

    /** @var Mob */
    private $entity;

    /** @var NodeEvaluator */
    private $evaluator;

    /** @var Path|null */
    private $path;

    /** @var Vector3|null */
    private $target;

    /** @var float */
    private $speed = 0.35;

    /** @var int */
    private $state = self::STATE_IDLE;

    /** @var int */
    private $stuckTicks = 0;

    /** @var int */
    private $retryCount = 0;

    /** @var Vector3|null 上一次卡住的位置 */
    private $lastStuckPos;

    /** @var int */
    private $horizontalRange;

    /** @var int */
    private $verticalRange;

    /** @var int */
    private $maxIterations;

    /** @var float */
    private $waterCost;

    public function __construct(Mob $entity){
        $this->entity = $entity;
        $this->evaluator = new NodeEvaluator($entity->getLevel());

        $config = Server::getInstance()->aiConfig;
        $this->horizontalRange = (int) ($config["pathfinding-range"] ?? 32);
        $this->verticalRange = (int) ($config["pathfinding-vertical-range"] ?? 8);
        $this->maxIterations = (int) ($config["pathfinding-max-iterations"] ?? 800);
        $this->waterCost = (float) ($config["pathfinding-water-cost"] ?? 2.0);
    }

    /**
     * 设置寻路目标，由 Behavior 调用
     */
    public function moveTo(Vector3 $target, float $speed): void{
        if(!$this->entity->isBehaviorsEnabled()){
            return;
        }

        $this->target = $target;
        $this->speed = $speed;

        if($this->state === self::STATE_PATH_FOLLOWING){
            // 检查目标是否移动了
            $currentEnd = $this->path !== null ? $this->path->getEndPoint() : null;
            if($currentEnd !== null and $currentEnd->distance($target) > 1.5){
                $this->recalculatePath();
            }
        }else{
            $this->state = self::STATE_PATH_FOLLOWING;
            $this->retryCount = 0;
            $this->recalculatePath();
        }
    }

    /**
     * 每 tick 由 Mob::onUpdate() 调用
     */
    public function update(): void{
        if($this->state === self::STATE_IDLE){
            return;
        }

        if($this->state === self::STATE_PATH_FOLLOWING){
            $this->updatePathFollowing();
        }elseif($this->state === self::STATE_FALLBACK){
            $this->updateFallback();
        }
    }

    private function updatePathFollowing(): void{
        if($this->path === null or $this->path->isDone()){
            $this->clearPath();
            return;
        }

        $waypoint = $this->path->getCurrentPoint();
        if($waypoint === null){
            $this->clearPath();
            return;
        }

        // 检查是否卡住
        $pos = $this->entity->getPosition();
        if($this->lastStuckPos !== null and $pos->distance($this->lastStuckPos) < 0.1){
            $this->stuckTicks++;
        }else{
            $this->stuckTicks = 0;
            $this->lastStuckPos = new Vector3($pos->x, $pos->y, $pos->z);
        }

        if($this->stuckTicks >= 5){
            $this->stuckTicks = 0;
            $this->recalculatePath();
            return;
        }

        // 距离 waypoint 足够近，前进到下一个
        $dist = $pos->distance($waypoint);
        if($dist < 0.8){
            $this->path->advance();
            if($this->path->isDone()){
                $this->clearPath();
                return;
            }
            $waypoint = $this->path->getCurrentPoint();
            if($waypoint === null){
                $this->clearPath();
                return;
            }
        }

        // 朝向 waypoint 移动
        $this->moveToward($waypoint);
    }

    private function updateFallback(): void{
        if($this->target === null){
            $this->clearPath();
            return;
        }

        $pos = $this->entity->getPosition();
        $dx = $this->target->x - $pos->x;
        $dz = $this->target->z - $pos->z;
        $len = sqrt($dx * $dx + $dz * $dz);

        if($len < 0.5){
            $this->clearPath();
            return;
        }

        $dx /= $len;
        $dz /= $len;

        $this->entity->yaw = -atan2($dx, $dz) * (180 / M_PI);

        $dir = new Vector3($dx, 0, $dz);
        $step = $this->speed * 0.55 * ($this->entity->isInsideOfWater() ? 0.04 : 0.06);

        if(!$this->entity->moveInDirection($dir, $step)){
            $this->stuckTicks++;
            if($this->stuckTicks >= 8){
                $this->clearPath();
            }
        }else{
            $this->stuckTicks = 0;
        }
    }

    private function moveToward(Vector3 $waypoint): void{
        $pos = $this->entity->getPosition();
        $dx = $waypoint->x - $pos->x;
        $dy = $waypoint->y - $pos->y;
        $dz = $waypoint->z - $pos->z;

        $yaw = -atan2($dx, $dz) * (180 / M_PI);
        $this->entity->yaw = $yaw;

        $dir = $this->entity->getDirectionVector();
        $dir->y = 0;

        $step = $this->speed * 0.55 * ($this->entity->isInsideOfWater() ? 0.04 : 0.06);

        if(!$this->entity->moveInDirection($dir, $step)){
            $this->stuckTicks++;
            if($this->stuckTicks >= 5){
                $this->stuckTicks = 0;
                $this->recalculatePath();
            }
        }else{
            $this->stuckTicks = 0;
        }
    }

    /**
     * A* 寻路计算
     */
    private function recalculatePath(): void{
        if($this->target === null){
            $this->fallbackToStraight();
            return;
        }

        $this->retryCount++;
        $this->evaluator->clearCache();

        $from = $this->entity->getPosition()->floor();
        $to = $this->target->floor();
        $entityHeight = $this->entity->height;

        $result = $this->findPath($from, $to, $entityHeight);

        if($result !== null){
            $this->path = $result;
            $this->stuckTicks = 0;
            $this->lastStuckPos = null;
            return;
        }

        // 第一次失败，重试
        if($this->retryCount < 2){
            return;
        }

        $this->fallbackToStraight();
    }

    private function fallbackToStraight(): void{
        $this->path = null;
        $this->state = self::STATE_FALLBACK;
        $this->stuckTicks = 0;
        $this->retryCount = 0;
    }

    public function clearPath(): void{
        $this->path = null;
        $this->target = null;
        $this->state = self::STATE_IDLE;
        $this->stuckTicks = 0;
        $this->retryCount = 0;
        $this->lastStuckPos = null;
    }

    public function hasPath(): bool{
        return $this->path !== null and !$this->path->isDone();
    }

    public function isDone(): bool{
        return $this->state === self::STATE_IDLE;
    }

    /**
     * A* 搜索算法
     * @return Path|null
     */
    private function findPath(Vector3 $from, Vector3 $to, float $entityHeight): ?Path{
        $startKey = $this->posKey($from);
        $goalKey = $this->posKey($to);

        if($startKey === $goalKey){
            return null;
        }

        // 目标不可达
        if(!$this->evaluator->isWalkable((int)$to->x, (int)$to->y, (int)$to->z, $entityHeight)){
            return null;
        }

        $openSet = [];
        $cameFrom = [];

        $gScore = [$startKey => 0.0];
        $fScore = [$startKey => $this->heuristic($from, $to)];

        $openSet[$startKey] = $fScore[$startKey];

        $iterations = 0;

        while(!empty($openSet)){
            if(++$iterations > $this->maxIterations){
                break;
            }

            // 取出 f 值最小的节点
            $currentKey = array_search(min($openSet), $openSet, true);
            unset($openSet[$currentKey]);

            if($currentKey === $goalKey){
                return $this->reconstructPath($cameFrom, $currentKey);
            }

            [$cx, $cy, $cz] = $this->keyPos($currentKey);
            $neighbors = $this->getNeighbors($cx, $cy, $cz, $entityHeight);

            foreach($neighbors as $neighbor){
                [$nx, $ny, $nz] = $neighbor;

                // 超出搜索范围
                if(abs($nx - (int)$from->x) > $this->horizontalRange or
                   abs($nz - (int)$from->z) > $this->horizontalRange or
                   abs($ny - (int)$from->y) > $this->verticalRange){
                    continue;
                }

                $neighborKey = $this->posKeyFromInt($nx, $ny, $nz);

                $moveCost = ($ny > $cy) ? 1.5 : 1.0; // 跳跃代价
                $blockCost = $this->evaluator->getCost($nx, $ny, $nz) * $this->waterCost;
                $tentativeG = $gScore[$currentKey] + $moveCost * $blockCost;

                if(!isset($gScore[$neighborKey]) or $tentativeG < $gScore[$neighborKey]){
                    $cameFrom[$neighborKey] = $currentKey;
                    $gScore[$neighborKey] = $tentativeG;
                    $neighborPos = new Vector3($nx, $ny, $nz);
                    $fScore[$neighborKey] = $tentativeG + $this->heuristic($neighborPos, $to);
                    $openSet[$neighborKey] = $fScore[$neighborKey];
                }
            }
        }

        return null;
    }

    /**
     * 获取邻居节点（4 方向）
     */
    private function getNeighbors(int $x, int $y, int $z, float $entityHeight): array{
        $neighbors = [];

        // 水平方向
        $dirs = [[1,0], [-1,0], [0,1], [0,-1]];

        foreach($dirs as [$dx, $dz]){
            $nx = $x + $dx;
            $nz = $z + $dz;

            // 同层
            if($this->evaluator->isWalkable($nx, $y, $nz, $entityHeight)){
                $neighbors[] = [$nx, $y, $nz];
            }

            // 上一格（跳跃）
            if($this->evaluator->isWalkable($nx, $y + 1, $nz, $entityHeight)){
                $neighbors[] = [$nx, $y + 1, $nz];
            }

            // 下一格（下落）
            if($this->evaluator->isWalkable($nx, $y - 1, $nz, $entityHeight)){
                $neighbors[] = [$nx, $y - 1, $nz];
            }
        }

        return $neighbors;
    }

    /**
     * 启发式距离（octile + 垂直）
     */
    private function heuristic(Vector3 $a, Vector3 $b): float{
        $dx = abs($a->x - $b->x);
        $dz = abs($a->z - $b->z);
        $dy = abs($a->y - $b->y);
        return max($dx, $dz) + 0.5 * min($dx, $dz) + $dy;
    }

    /**
     * 从 cameFrom 重建路径
     */
    private function reconstructPath(array $cameFrom, string $currentKey): Path{
        $points = [];
        $key = $currentKey;

        while(isset($cameFrom[$key])){
            [$x, $y, $z] = $this->keyPos($key);
            array_unshift($points, new Vector3($x + 0.5, $y, $z + 0.5));
            $key = $cameFrom[$key];
        }

        // 移除起点
        if(!empty($points)){
            array_shift($points);
        }

        return new Path($points);
    }

    private function posKey(Vector3 $pos): string{
        return ((int)$pos->x) . ',' . ((int)$pos->y) . ',' . ((int)$pos->z);
    }

    private function posKeyFromInt(int $x, int $y, int $z): string{
        return $x . ',' . $y . ',' . $z;
    }

    private function keyPos(string $key): array{
        $parts = explode(',', $key);
        return [(int)$parts[0], (int)$parts[1], (int)$parts[2]];
    }
}
