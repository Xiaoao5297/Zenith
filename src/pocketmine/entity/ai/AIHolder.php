<?php
namespace pocketmine\entity\ai;
use pocketmine\event\entity\EntityGenerateEvent;
use pocketmine\level\Position;
use pocketmine\level\Level;
use pocketmine\item\Item;
use pocketmine\Player;
use pocketmine\math\Vector3;
use pocketmine\entity\Entity;
use pocketmine\level\format\FullChunk;
use pocketmine\scheduler\CallbackTask;
use pocketmine\event\entity\EntityDeathEvent;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\DoubleTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\nbt\tag\FloatTag;
use pocketmine\event\entity\EntityDamageByEntityEvent;
use pocketmine\event\entity\EntityDamageEvent;
use pocketmine\scheduler\TaskHandler;
use pocketmine\Server;
use pocketmine\entity\Creeper;
use pocketmine\entity\Skeleton;
use pocketmine\entity\Cow;
use pocketmine\entity\Pig;
use pocketmine\entity\Sheep;
use pocketmine\entity\Spider;
use pocketmine\entity\Chicken;
use pocketmine\entity\Mooshroom;
use pocketmine\entity\Ocelot;
use pocketmine\entity\PigZombie;
use pocketmine\entity\Wolf;
use pocketmine\entity\Zombie;
class AIHolder {
	public $ChickenAI;
	public $CowAI;
	public $CreeperAI;
	public $PigAI;
	public $SheepAI;
	public $SkeletonAI;
	public $SpiderAI;
	public $ZombieAI;
	public $DefultAI;
	public $Zombie = [];
	public $Creeper = [];
	public $Skeleton = [];
	public $Cow = [];
	public $Pig = [];
	public $Sheep = [];
	public $Spider = [];
	public $Chicken = [];
	public $Defult = [];
	public $birth_r = 30;
	public $tasks = [];
	public $server;
	public function getServer() {
		return $this->server;
	}
	public function __construct(Server $server) {
		$this->server = $server;
		if($this->server->aiConfig["mobgenerate"]) {
			$this->tasks['ZombieGenerate'] = $this->getServer()->getScheduler()->scheduleRepeatingTask(new CallbackTask([
							$this,
							"MobGenerate"
						]), 20 * 60);
		}
	}

	public function spawnZombie(Position $pos, $maxHealth = 20, $health = 20) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$zo = new Zombie($chunk, $nbt);
		$zo->setPosition($pos);
		$zo->setMaxHealth($maxHealth);
		$zo->setHealth($health);
		$zo->spawnToAll();
		//$this->getLogger()->info("生成了一只僵尸");
	}
	public function spawnCreeper(Position $pos, $maxHealth = 20, $health = 20) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$co = new Creeper($chunk, $nbt);
		$co->setPosition($pos);
		$co->setMaxHealth($maxHealth);
		$co->setHealth($health);
		$co->spawnToAll();
		//$this->getLogger()->info("生成了一只苦力怕");
	}
	public function spawnSkeleton(Position $pos, $maxHealth = 20, $health = 20) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$so = new Skeleton($chunk, $nbt);
		$so->setPosition($pos);
		$so->setMaxHealth($maxHealth);
		$so->setHealth($health);
		$so->spawnToAll();
		//$this->getLogger()->info("生成了一只骷髅");
	}
	public function spawnSpider(Position $pos, $maxHealth = 16, $health = 16) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$so = new Spider($chunk, $nbt);
		$so->setPosition($pos);
		$so->setMaxHealth($maxHealth);
		$so->setHealth($health);
		$so->spawnToAll();
		//$this->getLogger()->info("生成了一只蜘蛛");
	}
	public function spawnCow(Position $pos, $maxHealth = 8, $health = 8) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$coo = new Cow($chunk, $nbt);
		$coo->setPosition($pos);
		$coo->setMaxHealth($maxHealth);
		$coo->setHealth($health);
		$coo->spawnToAll();
		//$this->getLogger()->info("生成了一只牛");
	}
	public function spawnPig(Position $pos, $maxHealth = 10, $health = 10) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$po = new Pig($chunk, $nbt);
		$po->setPosition($pos);
		$po->setMaxHealth($maxHealth);
		$po->setHealth($health);
		$po->spawnToAll();
		//$this->getLogger()->info("生成了一只豬");
	}
	public function spawnSheep(Position $pos, $maxHealth = 8, $health = 8) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$sho = new Sheep($chunk, $nbt);
		$sho->setPosition($pos);
		$sho->setMaxHealth($maxHealth);
		$sho->setHealth($health);
		$sho->spawnToAll();
		//$this->getLogger()->info("生成了一只羊");
	}
	public function spawnChicken(Position $pos, $maxHealth = 4, $health = 4) {
		$chunk = $pos->level->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$nbt = $this->getNBT();
		$cho = new Chicken($chunk, $nbt);
		$cho->setPosition($pos);
		$cho->setMaxHealth($maxHealth);
		$cho->setHealth($health);
		$cho->spawnToAll();
		//$this->getLogger()->info("生成了一只鸡");
	}
	/**
	 * @param Player $player
	 * @param        $damage
	 * @return float
	 * 根据玩家的装备获取玩家应受到的伤害值
	 */
	public function getPlayerDamage(Player $player, $damage) {
		$armorValues = [
					Item::LEATHER_CAP => 1,
					Item::LEATHER_TUNIC => 3,
					Item::LEATHER_PANTS => 2,
					Item::LEATHER_BOOTS => 1,
					Item::CHAIN_HELMET => 1,
					Item::CHAIN_CHESTPLATE => 5,
					Item::CHAIN_LEGGINGS => 4,
					Item::CHAIN_BOOTS => 1,
					Item::GOLD_HELMET => 1,
					Item::GOLD_CHESTPLATE => 5,
					Item::GOLD_LEGGINGS => 3,
					Item::GOLD_BOOTS => 1,
					Item::IRON_HELMET => 2,
					Item::IRON_CHESTPLATE => 6,
					Item::IRON_LEGGINGS => 5,
					Item::IRON_BOOTS => 2,
					Item::DIAMOND_HELMET => 3,
					Item::DIAMOND_CHESTPLATE => 8,
					Item::DIAMOND_LEGGINGS => 6,
					Item::DIAMOND_BOOTS => 3,
				];
		$points = 0;
		foreach($player->getInventory()->getArmorContents() as $index => $i) {
			if(isset($armorValues[$i->getId()])) {
				$points += $armorValues[$i->getId()];
			}
		}
		$damage = floor($damage - $points * 0.04);
		if($damage < 0) {
			$damage = 0;
		}
		return $damage;
	}
	/**
	 * @return CompoundTag
	 * 返回一个空的实体通用NBT
	 */
	public function getNBT() : CompoundTag {
		$nbt = new CompoundTag("", [
					"Pos" => new ListTag("Pos", [
						new DoubleTag("", 0),
						new DoubleTag("", 0),
						new DoubleTag("", 0)
					]),
					"Motion" => new ListTag("Motion", [
						new DoubleTag("", 0),
						new DoubleTag("", 0),
						new DoubleTag("", 0)
					]),
					"Rotation" => new ListTag("Rotation", [
						new FloatTag("", 0),
						new FloatTag("", 0)
					]),
				]);
		return $nbt;
	}
	/**
	 * @param Position $pos
	 * @return int
	 * 获取某坐标(位置)的亮度
	 */
	public function getLight(Position $pos) {
		$chunk = $pos->getLevel()->getChunk($pos->x >> 4, $pos->z >> 4, false);
		$l = 0;
		if($chunk instanceof FullChunk) {
			$l = $chunk->getBlockSkyLight($pos->x & 0x0f, $pos->y & 0x7f, $pos->z & 0x0f);
			if($l < 15) {
				//$l = \max($chunk->getBlockLight($pos->x & 0x0f, $pos->y & 0x7f, $pos->z & 0x0f));
				$l = $chunk->getBlockLight($pos->x & 0x0f, $pos->y & 0x7f, $pos->z & 0x0f);
			}
		}
		return $l;
	}
	/******** API结束 以下为计时器 *****************************/
	/**
	 * @param Entity $entity
	 * @return bool
	 * 判断某生物周边32格内是否有玩家存在
	 * 控制僵尸是否移动（自由行走模式）
	 */
	public function willMove(Entity $entity) {
		foreach($entity->getViewers() as $viewer) {
			if($entity->distance($viewer->getLocation()) <= 32) return true;
		}
		return false;
	}
	/**
	 * @param $mx
	 * @param $mz
	 * @return float|int
	 * 获取yaw角度
	 */
	public function getyaw($mx, $mz) {
		//根据motion计算转向角度
		//转向计算
		if($mz == 0) {
			//斜率不存在
			if($mx < 0) {
				$yaw = -90;
			} else {
				$yaw = 90;
			}
		} else {
			//存在斜率
			if($mx >= 0 and $mz > 0) {
				//第一象限
				$atan = atan($mx / $mz);
				$yaw = rad2deg($atan);
			} elseif($mx >= 0 and $mz < 0) {
				//第二象限
				$atan = atan($mx / abs($mz));
				$yaw = 180 - rad2deg($atan);
			} elseif($mx < 0 and $mz < 0) {
				//第三象限
				$atan = atan($mx / $mz);
				$yaw = -(180 - rad2deg($atan));
			} elseif($mx < 0 and $mz > 0) {
				//第四象限
				$atan = atan(abs($mx) / $mz);
				$yaw = -(rad2deg($atan));
			} else {
				$yaw = 0;
			}
		}
		$yaw = -$yaw;
		return $yaw;
	}
	/**
	 * @param Vector3 $from
	 * @param Vector3 $to
	 * @return float|int
	 * 获取pitch角度
	 */
	public function getpitch(Vector3 $from, Vector3 $to) {
		$distance = $from->distance($to);
		$height = $to->y - $from->y;
		if($height > 0) {
			return -rad2deg(asin($height / $distance));
		} elseif($height < 0) {
			return rad2deg(asin(-$height / $distance));
		} else {
			return 0;
		}
	}
	/**
	 * @param Level $level
	 * @param Vector3 $v3
	 * @param bool $hate
	 * @param bool $reason
	 * @return bool|float|string
	 * 判断某坐标是否可以行走
	 * 并给出原因
     */
	public function ifjump(Level $level, Vector3 $v3, $hate = false, $reason = false) {
		//boybook Y轴算法核心函数
		$x = floor($v3->getX());
		$y = floor($v3->getY());
		$z = floor($v3->getZ());
		//echo ($y." ");
		if ($this->whatBlock($level,new Vector3($x,$y,$z)) == "air") {
			//echo "前方空气 ";
			if ($this->whatBlock($level,new Vector3($x,$y-1,$z)) == "block" or new Vector3($x,$y-1,$z) == "climb") {
				//方块
				//echo "考虑向前 ";
				if ($this->whatBlock($level,new Vector3($x,$y+1,$z)) == "block" or $this->whatBlock($level,new Vector3($x,$y+1,$z)) == "half" or $this->whatBlock($level,new Vector3($x,$y+1,$z)) == "high") {
					//上方一格被堵住了
					//echo "上方卡住 \n";
					if ($reason) return 'up!';
					return false;
					//上方卡住
				} else {
					//echo "GO向前走 \n";
					if ($reason) return 'GO';
					return $y;
					//向前走
				}
			} elseif ($this->whatBlock($level,new Vector3($x,$y-1,$z)) == "water") {
				//水
				//echo "下水游泳 \n";
				if ($reason) return 'swim';
				return $y-1;
				//降低一格向前走（下水游泳）
			} elseif ($this->whatBlock($level,new Vector3($x,$y-1,$z)) == "half") {
				//半砖
				//echo "下到半砖 \n";
				if ($reason) return 'half';
				return $y-0.5;
				//向下跳0.5格
			} elseif ($this->whatBlock($level,new Vector3($x,$y-1,$z)) == "lava") {
				//岩浆
				//echo "前方岩浆 \n";
				if ($reason) return 'lava';
				return false;
				//前方岩浆
			} elseif ($this->whatBlock($level,new Vector3($x,$y-1,$z)) == "air") {
				//空气
				//echo "考虑向下跳 ";
				if ($this->whatBlock($level,new Vector3($x,$y-2,$z)) == "block") {
					//echo "GO向下跳 \n";
					if ($reason) return 'down';
					return $y-1;
					//向下跳
				} else {
					//前方悬崖
					//echo "前方悬崖 \n";
					if ($reason) return 'fall';
					if ($hate === false) {
						return false;
					} else {
						return $y-1;
						//向下跳
					}
				}
			}
		} elseif ($this->whatBlock($level,new Vector3($x,$y,$z)) == "water") {
			//水
			//echo "正在水中";
			if ($this->whatBlock($level,new Vector3($x,$y+1,$z)) == "water") {
				//上面还是水
				//echo "向上游 \n";
				if ($reason) return 'inwater';
				return $y+1;
				//向上游，防溺水
			} elseif ($this->whatBlock($level,new Vector3($x,$y+1,$z)) == "block" or $this->whatBlock($level,new Vector3($x,$y+1,$z)) == "half") {
				//上方一格被堵住了
				if ($this->whatBlock($level,new Vector3($x,$y-1,$z)) == "block" or $this->whatBlock($level,new Vector3($x,$y-1,$z)) == "half") {
					//下方一格被也堵住了
					//echo "上下都被卡住 \n";
					if ($reason) return 'up!_down!';
					return false;
					//上下都被卡住
				} else {
					//echo "向下游 \n";
					if ($reason) return 'up!';
					return $y-1;
					//向下游，防卡住
				}
			} else {
				//echo "游泳ing... \n";
				return $y;
				//向前游
			}
		} elseif ($this->whatBlock($level,new Vector3($x,$y,$z)) == "half") {
			//半砖
			//echo "前方半砖 \n";
			if ($this->whatBlock($level,new Vector3($x,$y+1,$z)) == "block" or $this->whatBlock($level,new Vector3($x,$y+1,$z)) == "half" or $this->whatBlock($level,new Vector3($x,$y+1,$z)) == "high") {
				//上方一格被堵住了
				//return false;  //上方卡住
			} else {
				if ($reason) return 'halfGO';
				return $y+0.5;
			}
		} elseif ($this->whatBlock($level,new Vector3($x,$y,$z)) == "lava") {
			//岩浆
			//echo "前方岩浆 \n";
			if ($reason) return 'lava';
			return false;
		} elseif ($this->whatBlock($level,new Vector3($x,$y,$z)) == "high") {
			//1.5格高方块
			//echo "前方栅栏 \n";
			if ($reason) return 'high';
			return false;
		} elseif ($this->whatBlock($level,new Vector3($x,$y,$z)) == "climb") {
			//梯子
			//echo "前方梯子 \n";
			//return $y;
			if ($reason) return 'climb';
			if ($hate) {
				return $y + 0.7;
			} else {
				return $y + 0.5;
			}
		} else {
			//考虑向上
			//echo "考虑向上 ";
			if ($this->whatBlock($level,new Vector3($x,$y+1,$z)) != "air") {
				//前方是面墙
				//echo "前方是墙 \n";
				if ($reason) return 'wall';
				return false;
			} else {
				if ($this->whatBlock($level,new Vector3($x,$y+2,$z)) == "block" or $this->whatBlock($level,new Vector3($x,$y+2,$z)) == "half" or $this->whatBlock($level,new Vector3($x,$y+2,$z)) == "high") {
					//上方两格被堵住了
					//echo "2格处被堵 \n";
					if ($reason) return 'up2!';
					return false;
				} else {
					//echo "GO向上跳 \n";
					if ($reason) return 'upGO';
					return $y+1;
					//向上跳
				}
			}
		}
		return false;
	}
	public function whatBlock(Level $level, $v3) {
		//boybook的y轴判断法 核心 什么方块？
		$block = $level->getBlock($v3);
		$id = $block->getID();
		$damage = $block->getDamage();
		switch ($id) {
			case 0:
						case 6:
						case 27:
						case 30:
						case 31:
						case 37:
						case 38:
						case 39:
						case 40:
						case 50:
						case 51:
						case 63:
						case 66:
						case 68:
						case 111:
						case 141:
						case 142:
						case 171:
						case 175:
						case 244:
						case 323:
							//透明方块
			return "air";
			break;
			case 8:
						case 9:
							//水
			return "water";
			break;
			case 10:
						case 11:
							//岩浆
			return "lava";
			break;
			case 78:
			case 80:
				//雪片、雪块
				$block = $level->getBlock($v3);
				if($block instanceof SnowLayer){
					if(($block->getDamage() + 1) / 8 >= 0.75){
						//厚雪层视为可行走块
						return "block";
					}else{
						//薄雪层，若下方无方块则不视为地面
						$down = $level->getBlock($v3->add(0, -1, 0));
						return $down->isSolid() ? "block" : "air";
					}
				}
				return "block";
			break;
			case 44:
						case 158:
							//半砖
			if ($damage >= 8) {
				return "block";
			} else {
				return "half";
			}
			break;
			case 64:
							//门
			//var_dump($damage." ");
			//TODO 不知如何判断门是否开启，因为以下条件永远满足
			if (($damage & 0x08) === 0x08) {
				return "air";
			} else {
				return "block";
			}
			break;
			case 85:
						case 107:
						case 139:
							//1.5格高的无法跳跃物
			return "high";
			break;
			case 65:
						case 106:
							//可攀爬物
			return "climb";
			break;
			default:
							//普通方块
			return "block";
			break;
		}
	}
	public function MobDeath(EntityDeathEvent $event) {
		// 旧 AI 追踪数组已废弃，Behavior 系统不再使用
	}
	/**
	 * 刷僵尸计时器
	 */
	public function MobGenerate() {
		foreach($this->getServer()->getOnlinePlayers() as $p) {
			$level = $p->getLevel();
			$max = 5;
			$v3 = new Vector3($p->getX() + mt_rand(-$this->birth_r, $this->birth_r), $p->getY(), $p->getZ() + mt_rand(-$this->birth_r, $this->birth_r));
			for ($y0 = $p->getY() - 15; $y0 <= $p->getY() + 15; $y0++) {
				$v3->y = $y0;
				if($this->whatBlock($level, $v3) == "block") {
					$v3_1 = $v3;
					$v3_1->y = $y0 + 1;
					$v3_2 = $v3;
					$v3_2->y = $y0 + 2;
					$random = mt_rand(0, 7);
					if($level->getBlock($v3_1)->getID() == 0 and $level->getBlock($v3_2)->getID() == 0) {
						//找到地面
						$zoc = [];
						$skeletonc = [];
						$creeperc = [];
						$cowc = [];
						$sheepc = [];
						$spiderc = [];
						$pigc = [];
						$chickenc = [];
						foreach($level->getEntities() as $zo) {
							if($zo instanceof Zombie) $zoc[] = $zo;
							if($zo instanceof Skeleton) $skeletonc[] = $zo;
							if($zo instanceof Creeper) $creeperc[] = $zo;
							if($zo instanceof Spider) $spiderc[] = $zo;
							if($zo instanceof Cow) $cowc[] = $zo;
							if($zo instanceof Sheep) $sheepc[] = $zo;
							if($zo instanceof Pig) $pigc[] = $zo;
							if($zo instanceof Chicken) $chickenc[] = $zo;
						}
						if(count($zoc) > $max) {
							for ($i = 0; $i < (count($zoc) - $max); $i++) $zoc[$i]->kill();
						} elseif($random == 0 && $level->getTime() >= 13500) {
							$pos = new Position($v3->x, $v3->y, $v3->z, $level);
							$this->server->getPluginManager()->callEvent($ev = new EntityGenerateEvent($pos, Zombie::NETWORK_ID, EntityGenerateEvent::CAUSE_AI_HOLDER));
							if(!$ev->isCancelled()) {
								$this->spawnZombie($ev->getPosition());
							}
							//$this->server->getLogger()->info("生成1僵尸");
						}
						if(count($skeletonc) > $max) {
							for ($i = 0; $i < (count($skeletonc) - $max); $i++) $skeletonc[$i]->kill();
						} elseif($random == 1 && $level->getTime() >= 13500) {
							$pos = new Position($v3->x, $v3->y, $v3->z, $level);
							$this->server->getPluginManager()->callEvent($ev = new EntityGenerateEvent($pos, Skeleton::NETWORK_ID, EntityGenerateEvent::CAUSE_AI_HOLDER));
							if(!$ev->isCancelled()) {
								$this->spawnSkeleton($ev->getPosition());
							}
							//$this->server->getLogger()->info("生成1骷髅");
						}
						if(count($creeperc) > $max) {
							for ($i = 0; $i < (count($creeperc) - $max); $i++) $creeperc[$i]->kill();
						} elseif($random == 2 && $level->getTime() >= 13500) {
							$pos = new Position($v3->x, $v3->y, $v3->z, $level);
							$this->server->getPluginManager()->callEvent($ev = new EntityGenerateEvent($pos, Creeper::NETWORK_ID, EntityGenerateEvent::CAUSE_AI_HOLDER));
							if(!$ev->isCancelled()) {
								$this->spawnCreeper($ev->getPosition());
							}
							//$this->server->getLogger()->info("生成1苦力怕");
						}
						if(count($spiderc) > $max) {
							for ($i = 0; $i < (count($spiderc) - $max); $i++) $spiderc[$i]->kill();
						} elseif($random == 3 && $level->getTime() >= 13500) {
							$pos = new Position($v3->x, $v3->y, $v3->z, $level);
							$this->server->getPluginManager()->callEvent($ev = new EntityGenerateEvent($pos,Spider::NETWORK_ID, EntityGenerateEvent::CAUSE_AI_HOLDER));
							if(!$ev->isCancelled()) {
								$this->spawnSpider($ev->getPosition());
							}
							//$this->server->getLogger()->info("生成1蜘蛛");
						}
						if(count($chickenc) > $max) {
							for ($i = 0; $i < (count($chickenc) - $max); $i++) $chickenc[$i]->kill();
						} elseif($random == 4) {
							$pos = new Position($v3->x, $v3->y, $v3->z, $level);
							$this->server->getPluginManager()->callEvent($ev = new EntityGenerateEvent($pos, Chicken::NETWORK_ID, EntityGenerateEvent::CAUSE_AI_HOLDER));
							if(!$ev->isCancelled()) {
								$this->spawnChicken($ev->getPosition());
							}
							//$this->server->getLogger()->info("生成1小鸡");
						}
						if(count($sheepc) > $max) {
							for ($i = 0; $i < (count($sheepc) - $max); $i++) $sheepc[$i]->kill();
						} elseif($random == 5) {
							$pos = new Position($v3->x, $v3->y, $v3->z, $level);
							$this->server->getPluginManager()->callEvent($ev = new EntityGenerateEvent($pos, Sheep::NETWORK_ID, EntityGenerateEvent::CAUSE_AI_HOLDER));
							if(!$ev->isCancelled()) {
								$this->spawnSheep($ev->getPosition());
							}
							//$this->server->getLogger()->info("生成1绵羊");
						}
						if(count($pigc) > $max) {
							for ($i = 0; $i < (count($pigc) - $max); $i++) $pigc[$i]->kill();
						} elseif($random == 6) {
							$pos = new Position($v3->x, $v3->y, $v3->z, $level);
							$this->server->getPluginManager()->callEvent($ev = new EntityGenerateEvent($pos, Pig::NETWORK_ID, EntityGenerateEvent::CAUSE_AI_HOLDER));
							if(!$ev->isCancelled()) {
								$this->spawnPig($ev->getPosition());
							}
							//$this->server->getLogger()->info("生成1小猪");
						}
						if(count($cowc) > $max) {
							for ($i = 0; $i < (count($cowc) - $max); $i++) $cowc[$i]->kill();
						} elseif($random == 7) {
							$pos = new Position($v3->x, $v3->y, $v3->z, $level);
							$this->server->getPluginManager()->callEvent($ev = new EntityGenerateEvent($pos, Cow::NETWORK_ID, EntityGenerateEvent::CAUSE_AI_HOLDER));
							if(!$ev->isCancelled()) {
								$this->spawnCow($ev->getPosition());
							}
							//$this->server->getLogger()->info("生成1牛");
						}
						break;
					}
				}
			}
		}
	}
	public function EntityDamage(EntityDamageEvent $event) {
		// 旧 AI 击退系统已废弃，Behavior 系统使用 Living::knockBack()
	}
}