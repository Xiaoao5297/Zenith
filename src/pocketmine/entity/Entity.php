<?php

/**
 * All the entity classes
 */
namespace pocketmine\entity;

use pocketmine\block\Block;
use pocketmine\block\Fire;
use pocketmine\block\Portal;
use pocketmine\block\PressurePlate;
use pocketmine\block\SlimeBlock;
use pocketmine\event\entity\EntityDamageEvent;
use pocketmine\event\entity\EntityDespawnEvent;
use pocketmine\event\entity\EntityLevelChangeEvent;
use pocketmine\event\entity\EntityMotionEvent;
use pocketmine\event\entity\EntityRegainHealthEvent;
use pocketmine\event\entity\EntitySpawnEvent;
use pocketmine\event\entity\EntityTeleportEvent;
use pocketmine\event\Timings;
use pocketmine\level\format\Chunk;
use pocketmine\level\format\FullChunk;
use pocketmine\level\Level;
use pocketmine\level\Location;
use pocketmine\level\particle\DestroyBlockParticle;
use pocketmine\level\Position;
use pocketmine\math\AxisAlignedBB;
use pocketmine\math\Math;
use pocketmine\math\Vector2;
use pocketmine\math\Vector3;
use pocketmine\metadata\Metadatable;
use pocketmine\metadata\MetadataValue;
use pocketmine\nbt\tag\ByteTag;
use pocketmine\nbt\tag\CompoundTag;
use pocketmine\nbt\tag\DoubleTag;
use pocketmine\nbt\tag\ListTag;
use pocketmine\nbt\tag\FloatTag;
use pocketmine\nbt\tag\IntTag;
use pocketmine\nbt\tag\LongTag;
use pocketmine\nbt\tag\ShortTag;
use pocketmine\nbt\tag\StringTag;
use pocketmine\network\protocol\MobEffectPacket;
use pocketmine\network\protocol\RemoveEntityPacket;
use pocketmine\network\protocol\SetEntityDataPacket;
use pocketmine\network\protocol\SetEntityLinkPacket;
use pocketmine\Player;
use pocketmine\plugin\Plugin;
use pocketmine\Server;
use pocketmine\utils\ChunkException;

abstract class Entity extends Location implements Metadatable{

    const NETWORK_ID = -1;

    const DATA_TYPE_BYTE = 0;
    const DATA_TYPE_SHORT = 1;
    const DATA_TYPE_INT = 2;
    const DATA_TYPE_FLOAT = 3;
    const DATA_TYPE_STRING = 4;
    const DATA_TYPE_SLOT = 5;
    const DATA_TYPE_POS = 6;
    const DATA_TYPE_ROTATION = 7;
    const DATA_TYPE_LONG = 8;

    /**
     * 判断“脚底已经贴住支撑面”的容差。
     * 浮点运算会让脚底与方块顶面差出 1e-7 量级，太小会判成悬空。
     */
    const THIN_BLOCK_GROUND_EPSILON = 0.001;

    /**
     * 部分方块（台阶、半砖、耕地）的支撑容差。
     * 走路时脚底可能略低于方块顶面（脚步插值），只差 0.126 以内都算踩住。
     */
    const PARTIAL_BLOCK_GROUND_EPSILON = 0.126;

    const DATA_FLAGS = 0; //存储DATA_FLAG
    const DATA_AIR = 1; //空气值(游泳) short
    const DATA_NAMETAG = 2; //string
    const DATA_SHOW_NAMETAG = 3;  //byte
    const DATA_SILENT = 4; //byte
    const DATA_POTION_COLOR = 7; //int
    const DATA_POTION_AMBIENT = 8; //byte
    /* 8-13 不知道 */
    const DATA_AGEABLE_FLAGS = 14; const DATA_IS_BABY = 14; //byte
    const DATA_NO_AI = 15; //byte
    
    //杂乱(生物种类)什么数据类型都有
    const DATA_PROFESSION_ID = 16; 
    const DATA_POTION_ID = 16; 
    const DATA_SLIME_SIZE = 16; 
    const DATA_COLOR_INFO = 16; 
    const DATA_CREPPER_SWELL_DIRECTION = 16; //苦力怕爆炸开始    
    const DATA_SHOOTER_ID = 17; //投掷物 long
    const DATA_CREPPER_SWELL = 17; //苦力怕爆炸动作 byte (似乎是控制闪光的)
    const DATA_CREPPER_SWELL_2 = 18; //苦力怕爆炸动作 byte (似乎是控制大小的)
    
    const DATA_RABBIT_TYPE = 18; const DATA_CAT_TYPE = 18; //生物种类 byte
    const DATA_JUMP_TYPE = 19; //unknown
    const DATA_BLOCK_INFO = 20; const DATA_WOOD_ID = 20; //重力方块，船的木头类型 int/byte
    const DATA_IN_LOVE = 21;//发情 byte
    /* 22-32 不知道 */
    const DATA_ONFIRE = 32;//byte | 和dataflag重复了，应该可以通用
    /* 33-43 不知道 */
    const DATA_ZOMBIE_IS_BABY= 44;//byte | 起码不能用通用的幼年生物data
    //46 ????
    const DATA_VILLAGER_IS_BABY= 46; //byte | Mojang你脑子是不是不好使村民可以用id14的data你非得再整一个
    //47 ????
    const DATA_ENDERMAN_HELD_ITEM_ID = 48; //short 
    const DATA_ENDERMAN_HELD_ITEM_DAMAGE = 49; //short
    const DATA_ENDERMAN_TREMBLE = 50; //byte 末影人被激怒以后的颤抖

    const DATA_FLAG_ONFIRE = 0;
    const DATA_FLAG_SNEAKING = 1;
    const DATA_FLAG_RIDING = 2;
    const DATA_FLAG_SPRINTING = 3;
    const DATA_FLAG_ACTION = 4;
    const DATA_FLAG_INVISIBLE = 5;
    
    const DATA_PLAYER_FLAG_SLEEP = 1;
    const DATA_PLAYER_FLAG_DEAD = 2;

    const DATA_PLAYER_FLAGS = 16;
    const DATA_PLAYER_BED_POSITION = 17;
    
    const DATA_LEAD_HOLDER = 23;
    const DATA_LEAD = 24;
    
    const SOUTH = 0;
    const WEST = 1;
    const NORTH = 2;
    const EAST = 3;

    public static $entityCount = 1;
    /** @var Entity[] */
    private static $knownEntities = [];
    private static $shortNames = [];

    /** @var bool[] 缓存有实体碰撞的方块 ID（避免创建 Block 对象）*/
    private static $collisionBlockIds = null;

    /**
     * 初始化碰撞方块 ID 表
     * 与 block/HasEntityCollision 接口保持同步
     */
    private static function initCollisionBlockIds(){
        if(self::$collisionBlockIds === null){
            self::$collisionBlockIds = [
                Block::WATER => true,
                Block::STILL_WATER => true,
                Block::LAVA => true,
                11 => true, // STILL_LAVA
                Block::COBWEB => true,
                Block::FIRE => true,
                Block::PORTAL => true,
                Block::VINE => true,
                Block::STONE_PRESSURE_PLATE => true,
                Block::WOODEN_PRESSURE_PLATE => true,
                Block::CACTUS => true,
                Block::LADDER => true,
                Block::SLIME_BLOCK => true,
            ];
        }
    }

    /**
     * @var Player[]
     */
    protected $hasSpawned = [];

    /** @var Effect[] */
    protected $effects = [];

    protected $id;

    protected $dataFlags = 0;
    protected $dataProperties = [
        self::DATA_FLAGS => [self::DATA_TYPE_BYTE, 0],
        self::DATA_AIR => [self::DATA_TYPE_SHORT, 300],
        self::DATA_NAMETAG => [self::DATA_TYPE_STRING, ""],
        self::DATA_SHOW_NAMETAG => [self::DATA_TYPE_BYTE, 1],
        self::DATA_SILENT => [self::DATA_TYPE_BYTE, 0],
        self::DATA_NO_AI => [self::DATA_TYPE_BYTE, 0],
        self::DATA_LEAD_HOLDER => [self::DATA_TYPE_LONG, -1],
        self::DATA_LEAD => [self::DATA_TYPE_BYTE, 0],
    ];

    public $passenger = null;
    public $vehicle = null;

    /** @var Chunk */
    public $chunk;

    protected $lastDamageCause = null;

    /** @var Block[] */
    private $blocksAround = [];

    public $lastX = null;
    public $lastY = null;
    public $lastZ = null;

    public $motionX;
    public $motionY;
    public $motionZ;
    /** @var Vector3 */
    public $temporalVector;
    public $lastMotionX;
    public $lastMotionY;
    public $lastMotionZ;

    public $lastYaw;
    public $lastPitch;
    /** @var float 头部朝向（身体用 $yaw），协议里是 MoveEntity 的 headYaw 字段 */
    public $headYaw;
    public $lastHeadYaw;

    /** @var AxisAlignedBB */
    public $boundingBox;
    public $onGround;
    public $inBlock = false;
    public $positionChanged;
    public $motionChanged;
    public $deadTicks = 0;
    protected $age = 0;

    public $height;

    public $eyeHeight = null;

    public $width;
    public $length;

    /** @var int */
    private $health = 20;
    private $maxHealth = 20;

    protected $ySize = 0;
    protected $stepHeight = 0;
    public $keepMovement = false;

    public $fallDistance = 0;
    public $ticksLived = 0;
    public $lastUpdate;
    public $maxFireTicks;
    public $fireTicks = 0;
    public $namedtag;
    public $canCollide = true;

    protected $isStatic = false;

    public $isCollided = false;
    public $isCollidedHorizontally = false;
    public $isCollidedVertically = false;

    public $noDamageTicks;
    protected $justCreated;
    protected $fireProof;
    private $invulnerable;

    /** @var AttributeMap */
    protected $attributeMap;

    protected $gravity;
    protected $drag;

    /** @var Server */
    protected $server;

    public $closed = false;

    /** @var \pocketmine\event\TimingsHandler */
    protected $timings;
    protected $isPlayer = false;

    /** @var Entity */
    protected $linkedEntity = null;
    /** 0 no linked 1 linked other 2 be linked */
    protected $linkedType = null;

    protected $riding = null;

    /** @var PressurePlate */
    protected $activatedPressurePlates = [];

    public $dropExp = [0, 0];

    public function __construct(FullChunk $chunk, CompoundTag $nbt){
        assert($chunk !== null and $chunk->getProvider() !== null);

        $this->timings = Timings::getEntityTimings($this);

        $this->isPlayer = $this instanceof Player;

        $this->temporalVector = new Vector3();

        if($this->eyeHeight === null){
            $this->eyeHeight = $this->height / 2 + 0.1;
        }

        $this->id = Entity::$entityCount++;
        $this->justCreated = true;
        $this->namedtag = $nbt;

        $this->chunk = $chunk;
        $this->setLevel($chunk->getProvider()->getLevel());
        $this->server = $chunk->getProvider()->getLevel()->getServer();

        $this->boundingBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
        $this->setPositionAndRotation(
            $this->temporalVector->setComponents(
                $this->namedtag["Pos"][0],
                $this->namedtag["Pos"][1],
                $this->namedtag["Pos"][2]
            ),
            $this->namedtag->Rotation[0],
            $this->namedtag->Rotation[1]
        );
        $this->setMotion($this->temporalVector->setComponents($this->namedtag["Motion"][0], $this->namedtag["Motion"][1], $this->namedtag["Motion"][2]));

        assert(!is_nan($this->x) and !is_infinite($this->x) and !is_nan($this->y) and !is_infinite($this->y) and !is_nan($this->z) and !is_infinite($this->z));

        if(!isset($this->namedtag->FallDistance)){
            $this->namedtag->FallDistance = new FloatTag("FallDistance", 0);
        }
        $this->fallDistance = $this->namedtag["FallDistance"];

        if(!isset($this->namedtag->Fire)){
            $this->namedtag->Fire = new ShortTag("Fire", 0);
        }
        $this->fireTicks = $this->namedtag["Fire"];

        if(!isset($this->namedtag->Air)){
            $this->namedtag->Air = new ShortTag("Air", 300);
        }
        $this->setDataProperty(self::DATA_AIR, self::DATA_TYPE_SHORT, $this->namedtag["Air"]);

        if(!isset($this->namedtag->OnGround)){
            $this->namedtag->OnGround = new ByteTag("OnGround", 0);
        }
        $this->onGround = $this->namedtag["OnGround"] > 0 ? true : false;

        if(!isset($this->namedtag->Invulnerable)){
            $this->namedtag->Invulnerable = new ByteTag("Invulnerable", 0);
        }
        $this->invulnerable = $this->namedtag["Invulnerable"] > 0 ? true : false;

        $this->attributeMap = new AttributeMap();

        $this->chunk->addEntity($this);
        $this->level->addEntity($this);
        $this->initEntity();
        if($this->closed){
            // initEntity() closed this entity (e.g. corrupt/missing NBT tag);
            // skip spawn event and scheduling for the already-closed entity
            return;
        }
        $this->lastUpdate = $this->server->getTick();
        $this->server->getPluginManager()->callEvent(new EntitySpawnEvent($this));

        $this->scheduleUpdate();
    }

    /**
     * @return int
     */
    public function getDropExpMin() : int{
        return $this->dropExp[0];
    }

    /**
     * @return int
     */
    public function getDropExpMax() : int{
        return $this->dropExp[1];
    }

    /**
     * @return string
     */
    public function getNameTag(){
        return $this->getDataProperty(self::DATA_NAMETAG);
    }

    /**
     * @return bool
     */
    public function isNameTagVisible(){
        return $this->getDataProperty(self::DATA_SHOW_NAMETAG) > 0;
    }

    /**
     * @param string $name
     */
    public function setNameTag($name){
        $this->setDataProperty(self::DATA_NAMETAG, self::DATA_TYPE_STRING, $name);
    }

    /**
     * @param bool $value
     */
    public function setNameTagVisible($value = true){
        $this->setDataProperty(self::DATA_SHOW_NAMETAG, self::DATA_TYPE_BYTE, $value ? 1 : 0);
    }

    public function isSneaking(){
        return $this->getDataFlag(self::DATA_FLAGS, self::DATA_FLAG_SNEAKING);
    }

    public function setSneaking($value = true){
        $this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_SNEAKING, (bool)$value);
    }

    public function isSprinting(){
        return $this->getDataFlag(self::DATA_FLAGS, self::DATA_FLAG_SPRINTING);
    }

    public function setSprinting($value = true){
        if($value !== $this->isSprinting()){
            $this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_SPRINTING, (bool) $value);
            $attr = $this->attributeMap->getAttribute(Attribute::MOVEMENT_SPEED);
            $attr->setValue($value ? ($attr->getValue() * 1.3) : ($attr->getValue() / 1.3));
        }
    }

    /**
     * @return Effect[]
     */
    public function getEffects(){
        return $this->effects;
    }

    public function removeAllEffects(){
        foreach($this->effects as $effect){
            $this->removeEffect($effect->getId());
        }
    }

    public function removeEffect($effectId){
        if(isset($this->effects[$effectId])){
            $effect = $this->effects[$effectId];
            unset($this->effects[$effectId]);
            $effect->remove($this);

            $this->recalculateEffectColor();
        }
    }

    public function getEffect($effectId){
        return isset($this->effects[$effectId]) ? $this->effects[$effectId] : null;
    }

    public function hasEffect($effectId){
        return isset($this->effects[$effectId]);
    }

    public function addEffect(Effect $effect){
        if(isset($this->effects[$effect->getId()])){
            $oldEffect = $this->effects[$effect->getId()];
            if(
                abs($effect->getAmplifier()) <= ($oldEffect->getAmplifier())
                or (abs($effect->getAmplifier()) === abs($oldEffect->getAmplifier())
                    and $effect->getDuration() < $oldEffect->getDuration())
            ){
                return;
            }
            $effect->add($this, true);
        }else{
            $effect->add($this, false);
        }

        $this->effects[$effect->getId()] = $effect;

        $this->recalculateEffectColor();

        if($effect->getId() === Effect::HEALTH_BOOST){
            $this->setHealth($this->getHealth() + 4 * ($effect->getAmplifier() + 1));
        }
    }

    protected function recalculateEffectColor(){
        $color = [0, 0, 0]; //RGB
        $count = 0;
        $ambient = true;
        foreach($this->effects as $effect){
            if($effect->isVisible()){
                $c = $effect->getColor();
                $color[0] += $c[0] * ($effect->getAmplifier() + 1);
                $color[1] += $c[1] * ($effect->getAmplifier() + 1);
                $color[2] += $c[2] * ($effect->getAmplifier() + 1);
                $count += $effect->getAmplifier() + 1;
                if(!$effect->isAmbient()){
                    $ambient = false;
                }
            }
        }

        if($count > 0){
            $r = ($color[0] / $count) & 0xff;
            $g = ($color[1] / $count) & 0xff;
            $b = ($color[2] / $count) & 0xff;

            $this->setDataProperty(Entity::DATA_POTION_COLOR, Entity::DATA_TYPE_INT, ($r << 16) + ($g << 8) + $b);
            $this->setDataProperty(Entity::DATA_POTION_AMBIENT, Entity::DATA_TYPE_BYTE, $ambient ? 1 : 0);
        }else{
            $this->setDataProperty(Entity::DATA_POTION_COLOR, Entity::DATA_TYPE_INT, 0);
            $this->setDataProperty(Entity::DATA_POTION_AMBIENT, Entity::DATA_TYPE_BYTE, 0);
        }
    }

    /**
     * @param int|string $type
     * @param FullChunk  $chunk
     * @param CompoundTag   $nbt
     * @param            $args
     *
     * @return Entity|Projectile
     */
    public static function createEntity($type, FullChunk $chunk, CompoundTag $nbt, ...$args){
        if(isset(self::$knownEntities[$type])){
            $class = self::$knownEntities[$type];
            return new $class($chunk, $nbt, ...$args);
        }

        return null;
    }

    public static function registerEntity($className, $force = false){
        $class = new \ReflectionClass($className);
        if(is_a($className, Entity::class, true) and !$class->isAbstract()){
            if($className::NETWORK_ID !== -1){
                self::$knownEntities[$className::NETWORK_ID] = $className;
            }elseif(!$force){
                return false;
            }

            self::$knownEntities[$class->getShortName()] = $className;
            self::$shortNames[$className] = $class->getShortName();
            return true;
        }

        return false;
    }

    /**
     * Returns the short save name
     *
     * @return string
     */
    /**
     * 是否随区块保存到磁盘。绳结(LeashKnot)由被拴实体的 NBT 恢复, 不落盘,
     * 否则每次区块重载都会按动物 NBT 新建一个、再加载旧的, 造成无限增长。
     *
     * @return bool
     */
    public function canBeSaved() : bool{
        return true;
    }

    public function getSaveId(){
        return self::$shortNames[static::class];
    }

    public function saveNBT(){
        if(!($this instanceof Player)){
            $this->namedtag->id = new StringTag("id", $this->getSaveId());
            if($this->getNameTag() !== ""){
                $this->namedtag->CustomName = new StringTag("CustomName", $this->getNameTag());
                $this->namedtag->CustomNameVisible = new StringTag("CustomNameVisible", $this->isNameTagVisible());
            }else{
                unset($this->namedtag->CustomName);
                unset($this->namedtag->CustomNameVisible);
            }
        }

        $this->namedtag->Pos = new ListTag("Pos", [
            new DoubleTag(0, $this->x),
            new DoubleTag(1, $this->y),
            new DoubleTag(2, $this->z)
        ]);

        $this->namedtag->Motion = new ListTag("Motion", [
            new DoubleTag(0, $this->motionX),
            new DoubleTag(1, $this->motionY),
            new DoubleTag(2, $this->motionZ)
        ]);

        $this->namedtag->Rotation = new ListTag("Rotation", [
            new FloatTag(0, $this->yaw),
            new FloatTag(1, $this->pitch)
        ]);

        $this->namedtag->FallDistance = new FloatTag("FallDistance", $this->fallDistance);
        $this->namedtag->Fire = new ShortTag("Fire", $this->fireTicks);
        $this->namedtag->Air = new ShortTag("Air", $this->getDataProperty(self::DATA_AIR));
        $this->namedtag->OnGround = new ByteTag("OnGround", $this->onGround == true ? 1 : 0);
        $this->namedtag->Invulnerable = new ByteTag("Invulnerable", $this->invulnerable == true ? 1 : 0);

        // 拴绳状态持久化：挂在栅栏上时只存栅栏坐标，由 LeashKnot 重建；
        // 被其它实体牵引时存持有者实体 id。
        if($this->isLeashed()){
            $this->namedtag->Leashed = new ByteTag("Leashed", 1);
            $holder = $this->getLeashHolder();
            if($holder instanceof LeashKnot){
                $this->namedtag->Leash = new CompoundTag("Leash", [
                    new IntTag("X", (int) floor($holder->x)),
                    new IntTag("Y", (int) floor($holder->y)),
                    new IntTag("Z", (int) floor($holder->z)),
                ]);
                unset($this->namedtag->LeashHolder);
            }else{
                $this->namedtag->LeashHolder = new LongTag("LeashHolder", $this->getLeashHolderEid());
                unset($this->namedtag->Leash);
            }
        }else{
            unset($this->namedtag->Leashed);
            unset($this->namedtag->Leash);
            unset($this->namedtag->LeashHolder);
        }

        if(count($this->effects) > 0){
            $effects = [];
            foreach($this->effects as $effect){
                $effects[$effect->getId()] = new CompoundTag($effect->getId(), [
                    "Id" => new ByteTag("Id", $effect->getId()),
                    "Amplifier" => new ByteTag("Amplifier", $effect->getAmplifier()),
                    "Duration" => new IntTag("Duration", $effect->getDuration()),
                    "Ambient" => new ByteTag("Ambient", 0),
                    "ShowParticles" => new ByteTag("ShowParticles", $effect->isVisible() ? 1 : 0)
                ]);
            }

            $this->namedtag->ActiveEffects = new ListTag("ActiveEffects", $effects);
        }else{
            unset($this->namedtag->ActiveEffects);
        }
    }

    protected function initEntity(){
        assert($this->namedtag instanceof CompoundTag);

        if(isset($this->namedtag->CustomName)){
            $this->setNameTag($this->namedtag["CustomName"]);
            if(isset($this->namedtag->CustomNameVisible)){
                $this->setNameTagVisible($this->namedtag["CustomNameVisible"] > 0);
            }
        }

        $this->scheduleUpdate();

        $this->addAttributes();

        if(isset($this->namedtag->ActiveEffects)){
            foreach($this->namedtag->ActiveEffects->getValue() as $e){
                $effect = Effect::getEffect($e["Id"]);
                if($effect === null){
                    continue;
                }

                $effect->setAmplifier($e["Amplifier"])->setDuration($e["Duration"])->setVisible($e["ShowParticles"] > 0);

                $this->addEffect($effect);
            }
        }

        $this->restoreLeashFromNBT();
    }

    protected function addAttributes(){
    }

    /**
     * @return Player[]
     */
    public function getViewers(){
        return $this->hasSpawned;
    }

    /**
     * @param Player $player
     */
    public function spawnTo(Player $player){
        if(!isset($this->hasSpawned[$player->getLoaderId()]) and isset($player->usedChunks[Level::chunkHash($this->chunk->getX(), $this->chunk->getZ())])){
            $this->hasSpawned[$player->getLoaderId()] = $player;
        }
    }

    /**
     * 只在玩家还没有收到过这个实体时发送 AddEntityPacket。
     *
     * 实体子类的 spawnTo() 会无条件发包，区块重发/玩家重生时会对同一个 eid
     * 重复 AddEntity，客户端会出现「孤儿实体」病灶。所有批量生成入口都用这个方法。
     */
    public function trySpawnTo(Player $player){
        if(!isset($this->hasSpawned[$player->getLoaderId()])){
            $this->spawnTo($player);
        }
    }

    public function sendPotionEffects(Player $player){
        foreach($this->effects as $effect){
            $pk = new MobEffectPacket();
            $pk->eid = 0;
            $pk->effectId = $effect->getId();
            $pk->amplifier = $effect->getAmplifier();
            $pk->particles = $effect->isVisible();
            $pk->duration = $effect->getDuration();
            $pk->eventId = MobEffectPacket::EVENT_ADD;

            $player->dataPacket($pk);
        }
    }

    /**
     * @deprecated
     */
    public function sendMetadata($player){
        $this->sendData($player);
    }

    /**
     * @param Player[]|Player $player
     * @param array           $data Properly formatted entity data, defaults to everything
     */
    public function sendData($player, array $data = null){
        $pk = new SetEntityDataPacket();
        $pk->eid = ($player === $this ? 0 : $this->getId());
        $pk->metadata = $data === null ? $this->dataProperties : $data;

        if(!is_array($player)){
            $player->dataPacket($pk);
        }else{
            Server::broadcastPacket($player, $pk);
        }
    }

    /**
     * @param Player $player
     */
    public function despawnFrom(Player $player){
        if(isset($this->hasSpawned[$player->getLoaderId()])){
            $pk = new RemoveEntityPacket();
            $pk->eid = $this->getId();
            $player->dataPacket($pk);
            unset($this->hasSpawned[$player->getLoaderId()]);
        }
    }

    /**
     * @param float             $damage
     * @param EntityDamageEvent $source
     *
     * @return bool
     */
    public function attack($damage, EntityDamageEvent $source){
        if($this->hasEffect(Effect::FIRE_RESISTANCE)
            and ($source->getCause() === EntityDamageEvent::CAUSE_FIRE
                or $source->getCause() === EntityDamageEvent::CAUSE_FIRE_TICK
                or $source->getCause() === EntityDamageEvent::CAUSE_LAVA)
        ){
            $source->setCancelled();
        }

        $this->server->getPluginManager()->callEvent($source);
        if($source->isCancelled()){
            return false;
        }
        $this->setLastDamageCause($source);

        // 反作弊：受击追踪（击退检测等）
        if($this instanceof \pocketmine\Player and \pocketmine\anticheat\AntiCheat::getInstance() !== null){
            \pocketmine\anticheat\AntiCheat::getInstance()->onEntityDamage($this, $source);
        }

        $this->setHealth($this->getHealth() - $source->getFinalDamage());
        return true;
    }

    /**
     * @param float                   $amount
     * @param EntityRegainHealthEvent $source
     *
     */
    public function heal($amount, EntityRegainHealthEvent $source){
        $this->server->getPluginManager()->callEvent($source);
        if($source->isCancelled()){
            return;
        }

        $this->setHealth($this->getHealth() + $source->getAmount());
    }

    /**
     * @return int
     */
    public function getHealth(){
        return $this->health;
    }

    public function isAlive(){
        return $this->health > 0;
    }

    /**
     * Sets the health of the Entity. This won't send any update to the players
     *
     * @param int $amount
     */
    public function setHealth($amount){
        if(!is_finite($amount)){
            // NaN/Inf are invalid input; ignore instead of coercing (NaN casts to 0 on ARM64 and kills the entity)
            return;
        }
        $amount = (int)$amount;
        if($amount === $this->health){
            return;
        }

        if($amount <= 0){
            if($this->isAlive()){
                $this->kill();
            }
        }elseif($amount <= $this->getMaxHealth()){
            $this->health = (int)$amount;
        }else{
            $this->health = $this->getMaxHealth();
        }
    }

    /**
     * @param EntityDamageEvent $type
     */
    public function setLastDamageCause(EntityDamageEvent $type){
        $this->lastDamageCause = $type;
    }

    /**
     * @return EntityDamageEvent|null
     */
    public function getLastDamageCause(){
        return $this->lastDamageCause;
    }
    
    public function resetLastDamageCause(){
        $this->lastDamageCause = null;
    }

    public function getAttributeMap(){
        return $this->attributeMap;
    }

    /**
     * @return int
     */
    public function getMaxHealth(){
        return $this->maxHealth + ($this->hasEffect(Effect::HEALTH_BOOST) ? 4 * ($this->getEffect(Effect::HEALTH_BOOST)->getAmplifier() + 1) : 0);
    }

    /**
     * @param int $amount
     */
    public function setMaxHealth($amount){
        $this->maxHealth = (int)$amount;
    }

    public function canCollideWith(Entity $entity){
        return !$this->justCreated and $entity !== $this;
    }

    protected function checkObstruction($x, $y, $z){
        $i = Math::floorFloat($x);
        $j = Math::floorFloat($y);
        $k = Math::floorFloat($z);

        $diffX = $x - $i;
        $diffY = $y - $j;
        $diffZ = $z - $k;

        if(Block::$solid[$this->level->getBlockIdAt($i, $j, $k)]){
            $flag = !Block::$solid[$this->level->getBlockIdAt($i - 1, $j, $k)];
            $flag1 = !Block::$solid[$this->level->getBlockIdAt($i + 1, $j, $k)];
            $flag2 = !Block::$solid[$this->level->getBlockIdAt($i, $j - 1, $k)];
            $flag3 = !Block::$solid[$this->level->getBlockIdAt($i, $j + 1, $k)];
            $flag4 = !Block::$solid[$this->level->getBlockIdAt($i, $j, $k - 1)];
            $flag5 = !Block::$solid[$this->level->getBlockIdAt($i, $j, $k + 1)];

            $direction = -1;
            $limit = 9999;

            if($flag){
                $limit = $diffX;
                $direction = 0;
            }

            if($flag1 and 1 - $diffX < $limit){
                $limit = 1 - $diffX;
                $direction = 1;
            }

            if($flag2 and $diffY < $limit){
                $limit = $diffY;
                $direction = 2;
            }

            if($flag3 and 1 - $diffY < $limit){
                $limit = 1 - $diffY;
                $direction = 3;
            }

            if($flag4 and $diffZ < $limit){
                $limit = $diffZ;
                $direction = 4;
            }

            if($flag5 and 1 - $diffZ < $limit){
                $direction = 5;
            }

            $force = lcg_value() * 0.2 + 0.1;

            if($direction === 0){
                $this->motionX = -$force;

                return true;
            }

            if($direction === 1){
                $this->motionX = $force;

                return true;
            }

            if($direction === 2){
                $this->motionY = -$force;

                return true;
            }

            if($direction === 3){
                $this->motionY = $force;

                return true;
            }

            if($direction === 4){
                $this->motionZ = -$force;

                return true;
            }

            if($direction === 5){
                $this->motionZ = $force;

                return true;
            }
        }

        return false;
    }

    public function entityBaseTick($tickDiff = 1){
        Timings::$timerEntityBaseTick->startTiming();
        //TODO: check vehicles

        $this->justCreated = false;

        if(!$this->isAlive()){
            $this->removeAllEffects();
            $this->despawnFromAll();
            if(!$this->isPlayer){
                $this->close();
            }

            Timings::$timerEntityBaseTick->stopTiming();
            return false;
        }

        if(count($this->effects) > 0){
            foreach($this->effects as $effect){
                if($effect->canTick()){
                    $effect->applyEffect($this);
                }
                $effect->setDuration($effect->getDuration() - $tickDiff);
                if($effect->getDuration() <= 0){
                    $this->removeEffect($effect->getId());
                }
            }
        }

        $hasUpdate = false;

        $this->checkBlockCollision();

        if($this->y <= -16 and $this->isAlive()){
            $ev = new EntityDamageEvent($this, EntityDamageEvent::CAUSE_VOID, 10);
            $this->attack($ev->getFinalDamage(), $ev);
            $hasUpdate = true;
        }

        if($this->fireTicks > 0){
            if($this->fireProof){
                $this->fireTicks -= 4 * $tickDiff;
                if($this->fireTicks < 0){
                    $this->fireTicks = 0;
                }
            }else{
                if(!$this->hasEffect(Effect::FIRE_RESISTANCE) and (($this->fireTicks % 20) === 0 or $tickDiff > 20)){
                    $ev = new EntityDamageEvent($this, EntityDamageEvent::CAUSE_FIRE_TICK, 1);
                    $this->attack($ev->getFinalDamage(), $ev);
                }
                $this->fireTicks -= $tickDiff;
            }

            if($this->fireTicks <= 0 && $this->fireTicks > -10){
                $this->extinguish();
            }else{
                $this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ONFIRE, true);
                $hasUpdate = true;
            }
        }

        if($this->noDamageTicks > 0){
            $this->noDamageTicks -= $tickDiff;
            if($this->noDamageTicks < 0){
                $this->noDamageTicks = 0;
            }
        }

        $this->age += $tickDiff;
        $this->ticksLived += $tickDiff;

        Timings::$timerEntityBaseTick->stopTiming();

        return $hasUpdate;
    }

    protected function updateMovement(){
        $diffPosition = ($this->x - $this->lastX) ** 2 + ($this->y - $this->lastY) ** 2 + ($this->z - $this->lastZ) ** 2;
        $diffRotation = ($this->yaw - $this->lastYaw) ** 2 + ($this->pitch - $this->lastPitch) ** 2 + ($this->headYaw - $this->lastHeadYaw) ** 2;

        $diffMotion = ($this->motionX - $this->lastMotionX) ** 2 + ($this->motionY - $this->lastMotionY) ** 2 + ($this->motionZ - $this->lastMotionZ) ** 2;

        if($diffPosition > 0.0004 or $diffRotation > 2.25 and ($diffMotion > 0.0001 and $this->getMotion()->lengthSquared() <= 0.00001)){ //0.2 ** 2, 1.5 ** 2
            $this->lastX = $this->x;
            $this->lastY = $this->y;
            $this->lastZ = $this->z;

            $this->lastYaw = $this->yaw;
            $this->lastPitch = $this->pitch;
            $this->lastHeadYaw = $this->headYaw;

            $this->level->addEntityMovement($this->chunk->getX(), $this->chunk->getZ(), $this->id, $this->x, $this->getNetworkY(), $this->z, $this->yaw, $this->pitch, $this->headYaw);
        }

        if($diffMotion > 0.0025 or ($diffMotion > 0.0001 and $this->getMotion()->lengthSquared() <= 0.0001)){ //0.05 ** 2
            $this->lastMotionX = $this->motionX;
            $this->lastMotionY = $this->motionY;
            $this->lastMotionZ = $this->motionZ;

            $this->level->addEntityMotion($this->chunk->getX(), $this->chunk->getZ(), $this->id, $this->motionX, $this->motionY, $this->motionZ);
        }
    }

    /**
     * MoveEntityPacket 的 y 坐标。
     *
     * 0.14 客户端对生物使用脚底 y；玩家由于 ridingHeight（1.62），
     * 仍按脚底 + 眼睛高度发送。AddEntity 也是同样的约定。
     */
    protected function getNetworkY() : float{
        return $this instanceof \pocketmine\Player ? $this->y + $this->getEyeHeight() : $this->y;
    }

    /**
     * @return Vector3
     */
    public function getDirectionVector(){
        $y = -sin(deg2rad($this->pitch));
        $xz = cos(deg2rad($this->pitch));
        $x = -$xz * sin(deg2rad($this->yaw));
        $z = $xz * cos(deg2rad($this->yaw));

        return $this->temporalVector->setComponents($x, $y, $z)->normalize();
    }

    public function getDirectionPlane(){
        return (new Vector2(-cos(deg2rad($this->yaw) - M_PI_2), -sin(deg2rad($this->yaw) - M_PI_2)))->normalize();
    }

    public function onUpdate($currentTick){
        if($this->closed){
            return false;
        }

        if(!$this->isAlive()){
            ++$this->deadTicks;
            if($this->deadTicks >= 10){
                $this->despawnFromAll();
                if(!$this->isPlayer){
                    $this->close();
                }
            }
            return $this->deadTicks < 10;
        }

        $tickDiff = $currentTick - $this->lastUpdate;
        if($tickDiff <= 0){
            return false;
        }

        $this->lastUpdate = $currentTick;

        $this->timings->startTiming();

        $hasUpdate = $this->entityBaseTick($tickDiff);

        $this->updateMovement();

        $this->timings->stopTiming();

        //if($this->isStatic())
        return $hasUpdate;
        //return !($this instanceof Player);
    }

    public final function scheduleUpdate(){
        $this->level->updateEntities[$this->id] = $this;
    }

    public function isOnFire(){
        return $this->fireTicks > 0;
    }

    public function setOnFire($seconds){
        $ticks = $seconds * 20;
        if($ticks > $this->fireTicks){
            $this->fireTicks = $ticks;
        }
    }

    public function getDirection(){
        $rotation = ($this->yaw - 90) % 360;
        if($rotation < 0){
            $rotation += 360.0;
        }
        if((0 <= $rotation and $rotation < 45) or (315 <= $rotation and $rotation < 360)){
            return 2; //North
        }elseif(45 <= $rotation and $rotation < 135){
            return 3; //East
        }elseif(135 <= $rotation and $rotation < 225){
            return 0; //South
        }elseif(225 <= $rotation and $rotation < 315){
            return 1; //West
        }else{
            return null;
        }
    }

    public function extinguish(){
        $this->fireTicks = 0;
        $this->setDataFlag(self::DATA_FLAGS, self::DATA_FLAG_ONFIRE, false);
    }

    public function canTriggerWalking(){
        return true;
    }

    public function resetFallDistance(){
        $this->fallDistance = 0;
    }

    protected function updateFallState($distanceThisTick, $onGround){
        if($onGround === true){
            if($this->fallDistance > 0){
                if($this instanceof Living){
                    $this->fall($this->fallDistance);
                }
                $this->resetFallDistance();
            }
        }elseif($distanceThisTick < 0){
            $this->fallDistance -= $distanceThisTick;
        }
    }

    public function getBoundingBox(){
        return $this->boundingBox;
    }

    public function fall($fallDistance){
        if($this instanceof Player and $this->isSpectator()){
            return;
        }
        if($this->getLevel()->getServer()->destroyBlockParticle and $fallDistance > 3){
            $this->getLevel()->addParticle(new DestroyBlockParticle($this, $this->getLevel()->getBlock($this->floor()->subtract(0, 1, 0))));
        }
        if($this->isInsideOfWater()){
            return;
        }
        $damage = floor($fallDistance - 3 - ($this->hasEffect(Effect::JUMP) ? $this->getEffect(Effect::JUMP)->getAmplifier() + 1 : 0));
        
        //Get the block directly beneath the player's feet, check if it is a slime block
        if($this->getLevel()->getBlock($this->floor()->subtract(0, 1, 0)) instanceof SlimeBlock){
            $damage = 0;
        }
        if($damage > 0){
            $ev = new EntityDamageEvent($this, EntityDamageEvent::CAUSE_FALL, $damage);
            $this->attack($ev->getFinalDamage(), $ev);
        }
    }

    public function handleLavaMovement(){ //TODO
    }

    public function getEyeHeight(){
        return $this->eyeHeight;
    }

    public function moveFlying(){ //TODO
    }

    public function onCollideWithPlayer(Human $entityPlayer){
    }

    protected function switchLevel(Level $targetLevel){
        if($this->closed){
            return false;
        }

        if($this->isValid()){
            $this->server->getPluginManager()->callEvent($ev = new EntityLevelChangeEvent($this, $this->level, $targetLevel));
            if($ev->isCancelled()){
                return false;
            }

            $this->level->removeEntity($this);
            if($this->chunk !== null){
                $this->chunk->removeEntity($this);
            }
            $this->despawnFromAll();
        }

        $this->setLevel($targetLevel);
        $this->level->addEntity($this);
        $this->chunk = null;

        return true;
    }

    public function getPosition(){
        return new Position($this->x, $this->y, $this->z, $this->level);
    }

    public function getLocation(){
        return new Location($this->x, $this->y, $this->z, $this->yaw, $this->pitch, $this->level);
    }

    public function isInsideOfPortal(){
        $blocks = $this->getBlocksAround();

        foreach($blocks as $block){
            if($block instanceof Portal){
                return true;
            }
        }

        return false;
    }

    public function isInsideOfWater(){
        $x = Math::floorFloat($this->x);
        $eyeY = $this->y + $this->getEyeHeight();
        $floorY = Math::floorFloat($eyeY);
        $z = Math::floorFloat($this->z);

        $id = $this->level->getBlockIdAt($x, $floorY, $z);

        if($id === Block::WATER or $id === Block::STILL_WATER){
            $data = $this->level->getBlockDataAt($x, $floorY, $z);
            $d = $data >= 8 ? 0 : $data;
            $f = ($floorY + 1) - ((($d + 1) / 9) - 0.1111111);
            return $eyeY < $f;
        }

        return false;
    }

    public function isInsideOfSolid(){
        $x = Math::floorFloat($this->x);
        $eyeY = Math::floorFloat($this->y + $this->getEyeHeight());
        $z = Math::floorFloat($this->z);

        $id = $this->level->getBlockIdAt($x, $eyeY, $z);

        // 快速路径：非实体方块直接返回，避免创建 Block 对象
        if($id === 0 or !Block::$solid[$id]){
            return false;
        }

        $block = $this->level->getBlock($this->temporalVector->setComponents($x, $eyeY, $z));
        $bb = $block->getBoundingBox();

        if($bb !== null and !$block->isTransparent() and $bb->intersectsWith($this->getBoundingBox())){
            return true;
        }
        return false;
    }

    public function isInsideOfFire(){
        foreach($this->getBlocksAround() as $block){
            if($block instanceof Fire){
                return true;
            }
        }
        return false;
    }

    public function fastMove($dx, $dy, $dz){
        if($dx == 0 and $dz == 0 and $dy == 0){
            return true;
        }

        Timings::$entityMoveTimer->startTiming();

        /*$newBB = $this->boundingBox->getOffsetBoundingBox($dx, $dy, $dz);

        $list = $this->level->getCollisionCubes($this, $newBB, false);

        if(count($list) === 0){
            $this->boundingBox = $newBB;
        }*/

        $this->x = ($this->boundingBox->minX + $this->boundingBox->maxX) / 2;
        $this->y = $this->boundingBox->minY - $this->ySize;
        $this->z = ($this->boundingBox->minZ + $this->boundingBox->maxZ) / 2;

        $this->blocksAround = null;
        $this->checkChunks();

        if(!$this->onGround or $dy != 0){
            $bb = clone $this->boundingBox;
            $bb->minY -= 0.75;
            $this->onGround = false;
            if(!$this->level->getBlock(new Vector3($this->x, $this->y - 1, $this->z))->isTransparent())
                $this->onGround = true;
            /*
                        if(count($this->level->getCollisionBlocks($bb)) > 0){
                            $this->onGround = true;
                        }*/
        }
        $this->isCollided = $this->onGround;
        $this->updateFallState($dy, $this->onGround);

        Timings::$entityMoveTimer->stopTiming();

        return true;
    }

    public function move($dx, $dy, $dz){
        // 玩家睡觉时禁止移动，避免在睡眠界面里穿墙。
        if($this->isPlayer and $this->getDataFlag(self::DATA_PLAYER_FLAGS, self::DATA_PLAYER_FLAG_SLEEP)){
            return true;
        }

        if($dx == 0 and $dz == 0 and $dy == 0){
            // 站着不动时也要把脚底从台阶/半砖的“嵌入”状态拉出来，
            // 否则生物会以极慢的速度下沉，看上去一直在抖动。
            if(!$this->isPlayer){
                $this->recoverPartialBlockStandingHeight();
            }
            return true;
        }

        // 下落/贴地时先做一次部分方块支撑恢复：脚底已经陷进台阶时，
        // 直接解算碰撞会把实体往下压，形成“走下台阶就弹一下”。
        if(!$this->isPlayer and $dy <= 0){
            $this->recoverPartialBlockStandingHeight();
        }

        if($this->keepMovement){
            $this->boundingBox->offset($dx, $dy, $dz);
            $this->setPosition($this->temporalVector->setComponents(($this->boundingBox->minX + $this->boundingBox->maxX) / 2, $this->boundingBox->minY, ($this->boundingBox->minZ + $this->boundingBox->maxZ) / 2));
            $this->onGround = $this->isPlayer ? true : false;
            return true;
        }else{
            Timings::$entityMoveTimer->startTiming();

            $this->ySize *= 0.4;

            /*
            if($this->isColliding){ //With cobweb?
                $this->isColliding = false;
                $dx *= 0.25;
                $dy *= 0.05;
                $dz *= 0.25;
                $this->motionX = 0;
                $this->motionY = 0;
                $this->motionZ = 0;
            }
            */

            $movX = $dx;
            $movY = $dy;
            $movZ = $dz;

            $axisalignedbb = clone $this->boundingBox;

            /*$sneakFlag = $this->onGround and $this instanceof Player;

            if($sneakFlag){
                for($mov = 0.05; $dx != 0.0 and count($this->level->getCollisionCubes($this, $this->boundingBox->getOffsetBoundingBox($dx, -1, 0))) === 0; $movX = $dx){
                    if($dx < $mov and $dx >= -$mov){
                        $dx = 0;
                    }elseif($dx > 0){
                        $dx -= $mov;
                    }else{
                        $dx += $mov;
                    }
                }

                for(; $dz != 0.0 and count($this->level->getCollisionCubes($this, $this->boundingBox->getOffsetBoundingBox(0, -1, $dz))) === 0; $movZ = $dz){
                    if($dz < $mov and $dz >= -$mov){
                        $dz = 0;
                    }elseif($dz > 0){
                        $dz -= $mov;
                    }else{
                        $dz += $mov;
                    }
                }

                //TODO: big messy loop
            }*/

            $list = $this->level->getCollisionCubes($this, $this->level->getTickRate() > 1 ? $this->boundingBox->getOffsetBoundingBox($dx, $dy, $dz) : $this->boundingBox->addCoord($dx, $dy, $dz), false);

            foreach($list as $bb){
                $dy = $bb->calculateYOffset($this->boundingBox, $dy);
            }

            $this->boundingBox->offset(0, $dy, 0);

            $fallingFlag = ($this->onGround or ($dy != $movY and $movY < 0));

            foreach($list as $bb){
                $dx = $bb->calculateXOffset($this->boundingBox, $dx);
            }

            $this->boundingBox->offset($dx, 0, 0);

            foreach($list as $bb){
                $dz = $bb->calculateZOffset($this->boundingBox, $dz);
            }

            $this->boundingBox->offset(0, 0, $dz);

            if($this->stepHeight > 0 and $fallingFlag and $this->ySize < 0.05 and ($movX != $dx or $movZ != $dz)){
                $cx = $dx;
                $cy = $dy;
                $cz = $dz;
                $dx = $movX;
                $dy = $this->stepHeight;
                $dz = $movZ;

                $axisalignedbb1 = clone $this->boundingBox;

                $this->boundingBox->setBB($axisalignedbb);

                $list = $this->level->getCollisionCubes($this, $this->boundingBox->addCoord($dx, $dy, $dz), false);

                foreach($list as $bb){
                    $dy = $bb->calculateYOffset($this->boundingBox, $dy);
                }

                $this->boundingBox->offset(0, $dy, 0);

                foreach($list as $bb){
                    $dx = $bb->calculateXOffset($this->boundingBox, $dx);
                }

                $this->boundingBox->offset($dx, 0, 0);

                foreach($list as $bb){
                    $dz = $bb->calculateZOffset($this->boundingBox, $dz);
                }

                $this->boundingBox->offset(0, 0, $dz);

                if(($cx ** 2 + $cz ** 2) >= ($dx ** 2 + $dz ** 2)){
                    $dx = $cx;
                    $dy = $cy;
                    $dz = $cz;
                    $this->boundingBox->setBB($axisalignedbb1);
                }else{
                    $this->ySize += 0.5;
                }
            }

            $this->x = ($this->boundingBox->minX + $this->boundingBox->maxX) / 2;
            $this->y = $this->boundingBox->minY - $this->ySize;
            $this->z = ($this->boundingBox->minZ + $this->boundingBox->maxZ) / 2;

            $this->blocksAround = null;
            $this->checkChunks();

            $this->checkGroundState($movX, $movY, $movZ, $dx, $dy, $dz);
            $this->updateFallState($dy, $this->onGround);

            if($movX != $dx){
                $this->motionX = 0;
            }

            if($movY != $dy){
                $this->motionY = 0;
            }

            if($movZ != $dz){
                $this->motionZ = 0;
            }

            //TODO: vehicle collision events (first we need to spawn them!)

            Timings::$entityMoveTimer->stopTiming();

            return true;
        }
    }

    protected function checkGroundState($movX, $movY, $movZ, $dx, $dy, $dz){
        $this->isCollidedVertically = $movY != $dy;
        $this->isCollidedHorizontally = ($movX != $dx or $movZ != $dz);
        $this->isCollided = ($this->isCollidedHorizontally or $this->isCollidedVertically);
        $this->onGround = ($movY != $dy and $movY < 0);

        // 碰撞解算只认完整方块；站在台阶/半砖边缘时 movY 可能等于 dy，
        // 于是被误判成悬空，下一 tick 下落、再被顶回来，形成 1 格高度的抖动。
        // 这里补一次“部分方块支撑”判定。
        if(!$this->onGround and $movY <= 0 and $dy <= 0){
            if($this->hasGroundSupport(0.01, false)){
                $this->onGround = true;
                // 没有发生垂直碰撞（movY == dy）时，上面的通用清零不会执行；
                // 若保留负的 motionY，Creature 末尾乘 -0.5 会把它变成向上弹跳。
                $this->motionY = 0;
                return;
            }

            $supportY = $this->findPartialBlockGroundSupportY();
            if($supportY !== null){
                $this->snapToPartialBlockGroundSupport($supportY);
                $this->onGround = true;
                $this->motionY = 0;
                return;
            }

            if($this->recoverPartialBlockStandingHeight()){
                return;
            }
        }
    }

    /**
     * 脚底下方 epsilon 范围内是否有支撑（完整方块或部分方块）。
     *
     * @param float $epsilon
     * @param bool  $includePartialBlockGround
     *
     * @return bool
     */
    protected function hasGroundSupport($epsilon = 0.01, $includePartialBlockGround = true){
        if(!($this->boundingBox instanceof AxisAlignedBB) or $this->level === null){
            return false;
        }

        $bb = new AxisAlignedBB(
            $this->boundingBox->minX,
            $this->boundingBox->minY - $epsilon,
            $this->boundingBox->minZ,
            $this->boundingBox->maxX,
            $this->boundingBox->minY,
            $this->boundingBox->maxZ
        );

        if(count($this->level->getCollisionCubes($this, $bb, false)) > 0){
            return true;
        }

        return $includePartialBlockGround and $this->findPartialBlockGroundSupportY(max($epsilon, self::PARTIAL_BLOCK_GROUND_EPSILON)) !== null;
    }

    /**
     * 找出脚底附近可以踩住的方块顶面（台阶/半砖等非完整方块）。
     *
     * @param float $epsilon
     *
     * @return float|null
     */
    protected function findPartialBlockGroundSupportY($epsilon = self::PARTIAL_BLOCK_GROUND_EPSILON){
        if(!($this->boundingBox instanceof AxisAlignedBB) or $this->level === null){
            return null;
        }

        $footY = $this->boundingBox->minY;
        $probe = new AxisAlignedBB(
            $this->boundingBox->minX,
            $footY - $epsilon,
            $this->boundingBox->minZ,
            $this->boundingBox->maxX,
            $footY + $epsilon,
            $this->boundingBox->maxZ
        );

        $minX = Math::floorFloat($probe->minX);
        $maxX = Math::ceilFloat($probe->maxX);
        $minY = Math::floorFloat($probe->minY);
        $maxY = Math::ceilFloat($probe->maxY);
        $minZ = Math::floorFloat($probe->minZ);
        $maxZ = Math::ceilFloat($probe->maxZ);
        $highestTop = null;

        for($x = $minX; $x <= $maxX; ++$x){
            for($y = $minY; $y <= $maxY; ++$y){
                for($z = $minZ; $z <= $maxZ; ++$z){
                    $block = $this->level->getBlock(new Vector3($x, $y, $z));
                    if($block->getId() === Block::AIR){
                        continue;
                    }

                    foreach($block->getCollisionBoxes($probe) as $bb){
                        if(!$this->entityFootprintIntersectsGroundSupport($bb)){
                            continue;
                        }
                        if($this->shouldSnapEntityFootToSupportTop($footY, $bb, $epsilon)){
                            $highestTop = $highestTop === null ? $bb->maxY : max($highestTop, $bb->maxY);
                        }
                    }
                }
            }
        }

        return $highestTop;
    }

    /**
     * 脚底与支撑面是否近到应该直接吸附。
     *
     * @param float         $footY
     * @param AxisAlignedBB $bb
     * @param float         $epsilon
     *
     * @return bool
     */
    protected function shouldSnapEntityFootToSupportTop($footY, AxisAlignedBB $bb, $epsilon) : bool{
        $topDelta = $footY - $bb->maxY;
        if(abs($topDelta) <= self::THIN_BLOCK_GROUND_EPSILON){
            return true;
        }

        // 脚底略低于顶面：走路插值造成，允许在 epsilon 内吸附。
        if($topDelta < 0 and -$topDelta <= $epsilon and $footY >= $bb->minY - self::THIN_BLOCK_GROUND_EPSILON){
            return true;
        }

        // 脚底略高于顶面：只有完整方块的顶面才吸附，避免把台阶顶面下沿当支撑。
        if($topDelta > 0 and $topDelta <= $epsilon){
            $fullBlockTop = ceil($bb->maxY - self::THIN_BLOCK_GROUND_EPSILON);
            return abs($footY - $fullBlockTop) <= self::THIN_BLOCK_GROUND_EPSILON;
        }

        return false;
    }

    /**
     * 实体水平投影是否与支撑面相交（只比较 X/Z，不比较 Y）。
     *
     * @param AxisAlignedBB $bb
     *
     * @return bool
     */
    protected function entityFootprintIntersectsGroundSupport(AxisAlignedBB $bb) : bool{
        return $this->boundingBox->maxX > $bb->minX and
            $this->boundingBox->minX < $bb->maxX and
            $this->boundingBox->maxZ > $bb->minZ and
            $this->boundingBox->minZ < $bb->maxZ;
    }

    /**
     * 把脚底吸附到支撑面高度。
     *
     * @param float $supportY
     */
    protected function snapToPartialBlockGroundSupport($supportY){
        if(!($this->boundingBox instanceof AxisAlignedBB)){
            return;
        }

        $offsetY = $supportY - $this->boundingBox->minY;
        if(abs($offsetY) <= self::THIN_BLOCK_GROUND_EPSILON){
            return;
        }

        $this->boundingBox->offset(0, $offsetY, 0);
        $this->y = $this->boundingBox->minY - $this->ySize;
    }

    /**
     * 生物被卡在台阶/半砖内部时，把脚底抬回方块顶面。
     *
     * @param float $maxRecoverDistance
     *
     * @return bool 是否发生了修正
     */
    protected function recoverPartialBlockStandingHeight($maxRecoverDistance = 1.0) : bool{
        $supportY = $this->findEmbeddedPartialBlockGroundSupportY($maxRecoverDistance);
        if($supportY === null){
            return false;
        }

        $changed = abs($supportY - $this->boundingBox->minY) > self::THIN_BLOCK_GROUND_EPSILON or !$this->onGround or abs($this->motionY) > self::THIN_BLOCK_GROUND_EPSILON;
        $this->snapToPartialBlockGroundSupport($supportY);
        $this->motionY = 0.0;
        $this->onGround = true;
        $this->isCollided = true;
        $this->isCollidedVertically = true;
        $this->blocksAround = null;

        return $changed;
    }

    /**
     * 向上寻找“脚底嵌在里面”的部分方块（台阶/半砖/耕地）。
     *
     * @param float $maxRecoverDistance
     *
     * @return float|null
     */
    protected function findEmbeddedPartialBlockGroundSupportY($maxRecoverDistance = 1.0){
        if(!($this->boundingBox instanceof AxisAlignedBB) or $this->level === null){
            return null;
        }

        $footY = $this->boundingBox->minY;
        $maxRecoverDistance = max(self::PARTIAL_BLOCK_GROUND_EPSILON, (float) $maxRecoverDistance + self::PARTIAL_BLOCK_GROUND_EPSILON);
        $probe = new AxisAlignedBB(
            $this->boundingBox->minX,
            $footY - self::THIN_BLOCK_GROUND_EPSILON,
            $this->boundingBox->minZ,
            $this->boundingBox->maxX,
            $footY + $maxRecoverDistance + self::THIN_BLOCK_GROUND_EPSILON,
            $this->boundingBox->maxZ
        );

        $minX = Math::floorFloat($probe->minX);
        $maxX = Math::ceilFloat($probe->maxX);
        $minY = Math::floorFloat($probe->minY);
        $maxY = Math::ceilFloat($probe->maxY);
        $minZ = Math::floorFloat($probe->minZ);
        $maxZ = Math::ceilFloat($probe->maxZ);
        $highestTop = null;

        for($x = $minX; $x <= $maxX; ++$x){
            for($y = $minY; $y <= $maxY; ++$y){
                for($z = $minZ; $z <= $maxZ; ++$z){
                    $block = $this->level->getBlock(new Vector3($x, $y, $z));
                    if($block->getId() === Block::AIR or $block->canPassThrough()){
                        continue;
                    }

                    foreach($block->getCollisionBoxes($probe) as $bb){
                        if(!$this->entityFootprintIntersectsGroundSupport($bb)){
                            continue;
                        }

                        $height = $bb->maxY - $bb->minY;
                        if($height >= 1.0 - self::THIN_BLOCK_GROUND_EPSILON){
                            // 完整方块不是“嵌入”，交给碰撞解算。
                            continue;
                        }

                        $rise = $bb->maxY - $footY;
                        if($rise <= self::THIN_BLOCK_GROUND_EPSILON or $rise > $maxRecoverDistance + self::THIN_BLOCK_GROUND_EPSILON){
                            continue;
                        }

                        if($footY < $bb->minY - self::PARTIAL_BLOCK_GROUND_EPSILON){
                            continue;
                        }

                        if(!$this->canOccupyPartialBlockStandingY($bb->maxY)){
                            continue;
                        }

                        $highestTop = $highestTop === null ? $bb->maxY : max($highestTop, $bb->maxY);
                    }
                }
            }
        }

        return $highestTop;
    }

    /**
     * 抬到该高度后是否会卡进上方方块。
     *
     * @param float $standingY
     *
     * @return bool
     */
    protected function canOccupyPartialBlockStandingY($standingY) : bool{
        if(!($this->boundingBox instanceof AxisAlignedBB) or $this->level === null){
            return false;
        }

        $bb = clone $this->boundingBox;
        $bb->offset(0, $standingY - $bb->minY, 0);

        return count($this->level->getCollisionCubes($this, $bb, false)) === 0;
    }

    public function getBlocksAround(){
        if($this->blocksAround === null){
            $minX = Math::floorFloat($this->boundingBox->minX);
            $minY = Math::floorFloat($this->boundingBox->minY);
            $minZ = Math::floorFloat($this->boundingBox->minZ);
            $maxX = Math::ceilFloat($this->boundingBox->maxX);
            $maxY = Math::ceilFloat($this->boundingBox->maxY);
            $maxZ = Math::ceilFloat($this->boundingBox->maxZ);

            $this->blocksAround = [];

            self::initCollisionBlockIds();

            for($z = $minZ; $z <= $maxZ; ++$z){
                for($x = $minX; $x <= $maxX; ++$x){
                    for($y = $minY; $y <= $maxY; ++$y){
                        $id = $this->level->getBlockIdAt($x, $y, $z);
                        // 快速路径：只有已知有实体碰撞的方块才创建 Block 对象
                        if(isset(self::$collisionBlockIds[$id])){
                            $block = $this->level->getBlock($this->temporalVector->setComponents($x, $y, $z));
                            $this->blocksAround[Level::blockHash($block->x, $block->y, $block->z)] = $block;
                        }
                    }
                }
            }
        }

        return $this->blocksAround;
    }

    protected function checkBlockCollision(){
        $vector = new Vector3(0, 0, 0);

        foreach($blocksaround = $this->getBlocksAround() as $block){
            $block->onEntityCollide($this);
            if($this->getLevel()->getServer()->redstoneEnabled and !$this->isPlayer){
                if($block instanceof PressurePlate){
                    $this->activatedPressurePlates[Level::blockHash($block->x, $block->y, $block->z)] = $block;
                }
            }
            $block->addVelocityToEntity($this, $vector);
        }

        if($this->getLevel()->getServer()->redstoneEnabled and !$this->isPlayer){
            /** @var \pocketmine\block\PressurePlate $block **/
            foreach($this->activatedPressurePlates as $key => $block){
                if(!isset($blocksaround[$key])) $block->checkActivation();
            }
        }

        if($vector->lengthSquared() > 0){
            $vector = $vector->normalize();
            $d = 0.014;
            $this->motionX += $vector->x * $d;
            $this->motionY += $vector->y * $d;
            $this->motionZ += $vector->z * $d;
        }
    }

    public function setPositionAndRotation(Vector3 $pos, $yaw, $pitch){
        if($this->setPosition($pos) === true){
            $this->setRotation($yaw, $pitch);

            return true;
        }

        return false;
    }

    public function setRotation($yaw, $pitch){
        $this->yaw = $yaw;
        $this->pitch = $pitch;
        // 传送/生成时头跟着身体朝向，之后由 look 行为或漂移单独控制
        $this->headYaw = $yaw;
        $this->lastHeadYaw = $yaw;
        $this->scheduleUpdate();
    }

    /**
     * 把头部朝向夹到身体朝向两侧 ±$max 度内（原版头不能拧过肩）。
     */
    public static function clampHeadYaw(float $bodyYaw, float $headYaw, float $max = 75.0) : float{
        $delta = $headYaw - $bodyYaw;
        while($delta > 180){
            $delta -= 360;
        }
        while($delta < -180){
            $delta += 360;
        }
        if($delta > $max){
            $delta = $max;
        }elseif($delta < -$max){
            $delta = -$max;
        }

        $result = $bodyYaw + $delta;
        while($result > 180){
            $result -= 360;
        }
        while($result < -180){
            $result += 360;
        }

        return $result;
    }

    /**
     * 把角度 $current 朝 $target 转动，单次不超过 $maxStep 度。
     */
    public static function turnTowardAngle(float $current, float $target, float $maxStep) : float{
        $delta = $target - $current;
        while($delta > 180){
            $delta -= 360;
        }
        while($delta < -180){
            $delta += 360;
        }
        if($delta > $maxStep){
            $delta = $maxStep;
        }elseif($delta < -$maxStep){
            $delta = -$maxStep;
        }

        $result = $current + $delta;
        while($result > 180){
            $result -= 360;
        }
        while($result < -180){
            $result += 360;
        }

        return $result;
    }

    protected function checkChunks(){
        if($this->chunk === null or ($this->chunk->getX() !== ($this->x >> 4) or $this->chunk->getZ() !== ($this->z >> 4))){
            if($this->chunk !== null){
                $this->chunk->removeEntity($this);
            }
            $this->chunk = $this->level->getChunk($this->x >> 4, $this->z >> 4, true);

            if(!$this->justCreated){
                $newChunk = $this->level->getChunkPlayers($this->x >> 4, $this->z >> 4);
                foreach($this->hasSpawned as $player){
                    if(!isset($newChunk[$player->getLoaderId()])){
                        $this->despawnFrom($player);
                    }else{
                        unset($newChunk[$player->getLoaderId()]);
                    }
                }
                foreach($newChunk as $player){
                    $this->spawnTo($player);
                }
            }

            if($this->chunk === null){
                return;
            }

            $this->chunk->addEntity($this);
        }
    }

    public function setLocation(Location $pos){
        if($this->closed){
            return false;
        }

        $this->setPositionAndRotation($pos, $pos->yaw, $pos->pitch);

        return true;
    }

    public function setPosition(Vector3 $pos){
        if($this->closed){
            return false;
        }

        if($pos instanceof Position and $pos->level !== null and $pos->level !== $this->level){
            if($this->switchLevel($pos->getLevel()) === false){
                return false;
            }
        }

        $this->x = $pos->x;
        $this->y = $pos->y;
        $this->z = $pos->z;

        //$this->boundingBox->setBounds($pos->x - $radius, $pos->y, $pos->z - $radius, $pos->x + $radius, $pos->y + $this->height, $pos->z + $radius);
        $this->recalculateBoundingBox();

        $this->blocksAround = null;
        $this->checkChunks();

        return true;
    }
    
    protected function recalculateBoundingBox(){
        $halfWidth = $this->width / 2;

        $this->boundingBox->setBounds(
            $this->x - $halfWidth,
            $this->y,
            $this->z - $halfWidth,
            $this->x + $halfWidth,
            $this->y + $this->height,
            $this->z + $halfWidth
        );
    }

    public function getMotion(){
        return new Vector3($this->motionX, $this->motionY, $this->motionZ);
    }

    public function setMotion(Vector3 $motion){
        if(!$this->justCreated){
            $this->server->getPluginManager()->callEvent($ev = new EntityMotionEvent($this, $motion));
            if($ev->isCancelled()){
                return false;
            }
        }

        $this->motionX = $motion->x;
        $this->motionY = $motion->y;
        $this->motionZ = $motion->z;

        if(!$this->justCreated){
            $this->updateMovement();
        }

        return true;
    }

    public function isOnGround(){
        return $this->onGround === true;
    }

    /**
     * 活塞推动判定（移植自 InCore-II entity/Entity）。
     *
     * @return bool
     */
    public function canBePushedByPiston(){
        return true;
    }

    /**
     * 活塞推动回调（移植自 InCore-II entity/Entity），默认不做事。
     *
     * @param Block|null $piston
     */
    public function onPushByPiston($piston = null){
    }

    public function kill(){
        $this->health = 0;
        $this->removeAllEffects();
        $this->scheduleUpdate();

        if($this->getLevel()->getServer()->expEnabled and !$this->getLevel()->getServer()->isWorldMobDeathDropsAndExperienceDisabled($this->getLevel())) {
            $exp = mt_rand($this->getDropExpMin(), $this->getDropExpMax());
            if($exp > 0) $this->getLevel()->spawnXPOrb($this, $exp);
        }
    }

    /**
     * @param Vector3|Position|Location $pos
     * @param float                     $yaw
     * @param float                     $pitch
     *
     * @return bool
     */
    public function teleport(Vector3 $pos, $yaw = null, $pitch = null){
        if($pos instanceof Location){
            $yaw = $yaw === null ? $pos->yaw : $yaw;
            $pitch = $pitch === null ? $pos->pitch : $pitch;
        }
        $from = Position::fromObject($this, $this->level);
        $to = Position::fromObject($pos, $pos instanceof Position ? $pos->getLevel() : $this->level);
        // 限制传送坐标在世界范围内
        if($to->y < 0 or $to->y >= 128 or $to->x > 30000000 or $to->x < -30000000 or $to->z > 30000000 or $to->z < -30000000){
            return false;
        }
        $this->server->getPluginManager()->callEvent($ev = new EntityTeleportEvent($this, $from, $to));
        if($ev->isCancelled()){
            return false;
        }
        $this->ySize = 0;
        $pos = $ev->getTo();

        $this->setMotion($this->temporalVector->setComponents(0, 0, 0));
        if($this->setPositionAndRotation($pos, $yaw === null ? $this->yaw : $yaw, $pitch === null ? $this->pitch : $pitch) !== false){
            $this->resetFallDistance();
            $this->onGround = true;

            $this->lastX = $this->x;
            $this->lastY = $this->y;
            $this->lastZ = $this->z;

            $this->lastYaw = $this->yaw;
            $this->lastPitch = $this->pitch;

            $this->updateMovement();

            return true;
        }

        return false;
    }

    public function getId(){
        return $this->id;
    }

    public function respawnToAll(){
        foreach($this->hasSpawned as $key => $player){
            unset($this->hasSpawned[$key]);
            $this->spawnTo($player);
        }
    }

    public function spawnToAll(){
        if($this->chunk === null or $this->closed){
            return;
        }
        foreach($this->level->getChunkPlayers($this->chunk->getX(), $this->chunk->getZ()) as $player){
            if($player->isOnline()){
                $this->spawnTo($player);
            }
        }
    }

    public function despawnFromAll(){
        foreach($this->hasSpawned as $player){
            $this->despawnFrom($player);
        }
    }

    public function close(){
        if(!$this->closed){
            $this->server->getPluginManager()->callEvent(new EntityDespawnEvent($this));
            $this->closed = true;
            $this->removeEffect(Effect::HEALTH_BOOST);
            $this->despawnFromAll();
            // 实体消失时松开所有指向它的拴绳，避免悬挂的 leash 指向无效实体
            if($this->level !== null){
                foreach($this->level->getEntities() as $entity){
                    if($entity !== $this and $entity instanceof Entity and $entity->isLeashedTo($this)){
                        $entity->unleash();
                    }
                }
            }
            if($this->linkedType != 0 and $this->linkedEntity !== null){
                $this->linkedEntity->setLinked(0, $this);
            }
            if($this->chunk !== null){
                $this->chunk->removeEntity($this);
            }
            if($this->level !== null){
                $this->level->removeEntity($this);
            }
        }

        if($this->getLevel()->getServer()->redstoneEnabled){
            /** @var \pocketmine\block\PressurePlate $block **/
            foreach($this->activatedPressurePlates as $key => $block){
                $block->checkActivation();
            }
        }

        $this->activatedPressurePlates = [];

        if($this->attributeMap != null) {
            $this->attributeMap = null;
        }
    }

    /**
     * @param int   $id
     * @param int   $type
     * @param mixed $value
     *
     * @return bool
     */
    public function setDataProperty($id, $type, $value){
        if($this->getDataProperty($id) !== $value){
            $this->dataProperties[$id] = [$type, $value];

            $this->sendData($this->hasSpawned, [$id => $this->dataProperties[$id]]);

            return true;
        }

        return false;
    }

    /**
     * 拴绳 API（移植自 InCore-II entity/Entity.php）。
     * 状态只存在 metadata 里：DATA_LEAD_HOLDER 存持有者实体 id，
     * DATA_LEAD 存是否被牵引，真正的绳子渲染交给客户端。
     */
    public function isLeashed() : bool{
        return (int) $this->getDataProperty(self::DATA_LEAD) === 1 and $this->getLeashHolderEid() > 0;
    }

    public function getLeashHolderEid() : int{
        return (int) $this->getDataProperty(self::DATA_LEAD_HOLDER);
    }

    /**
     * @return Entity|null
     */
    public function getLeashHolder(){
        if($this->level === null or $this->getLeashHolderEid() <= 0){
            return null;
        }

        return $this->level->getEntity($this->getLeashHolderEid());
    }

    public function isLeashedTo(Entity $holder) : bool{
        return $this->isLeashed() and $this->getLeashHolderEid() === $holder->getId();
    }

    public function setLeashedTo(Entity $holder) : bool{
        if($holder === $this or !$holder->isAlive()){
            return false;
        }

        $this->setDataProperty(self::DATA_LEAD_HOLDER, self::DATA_TYPE_LONG, $holder->getId());
        $this->setDataProperty(self::DATA_LEAD, self::DATA_TYPE_BYTE, 1);
        return true;
    }

    public function unleash() : bool{
        if(!$this->isLeashed()){
            return false;
        }

        $this->setDataProperty(self::DATA_LEAD_HOLDER, self::DATA_TYPE_LONG, -1);
        $this->setDataProperty(self::DATA_LEAD, self::DATA_TYPE_BYTE, 0);
        return true;
    }

    /**
     * 从 NBT 恢复拴绳状态：Leash 记录栅栏坐标（对应 LeashKnot），
     * LeashHolder 记录持有者实体 id。
     */
    protected function restoreLeashFromNBT(){
        if($this instanceof LeashKnot or !isset($this->namedtag->Leashed) or $this->namedtag["Leashed"] <= 0){
            return;
        }

        if(isset($this->namedtag->Leash) and $this->namedtag->Leash instanceof CompoundTag){
            $tag = $this->namedtag->Leash;
            if($this->level !== null and isset($tag->X) and isset($tag->Y) and isset($tag->Z)){
                $block = $this->level->getBlock(new Vector3((int) $tag["X"], (int) $tag["Y"], (int) $tag["Z"]));
                if(LeashKnot::isSupportedFence($block)){
                    $knot = LeashKnot::getOrCreate($this->level, $block);
                    if($knot instanceof LeashKnot){
                        $this->setLeashedTo($knot);
                        return;
                    }
                }
            }
        }

        if(isset($this->namedtag->LeashHolder)){
            $this->setDataProperty(self::DATA_LEAD_HOLDER, self::DATA_TYPE_LONG, (int) $this->namedtag["LeashHolder"]);
            $this->setDataProperty(self::DATA_LEAD, self::DATA_TYPE_BYTE, 1);
        }
    }

    public function linkEntity(Entity $entity){
        return $this->setLinked(1, $entity);
    }

    public function sendLinkedData(){
        if($this->linkedEntity instanceof Entity){
            $this->setLinked($this->linkedType, $this->linkedEntity);
        }
    }

    public function setLinked($type, Entity $entity){
        if($type != 0 and $entity === null){
            return false;
        }
        if($entity === $this){
            return false;
        }
        switch($type){
            case 0:
                if($this->linkedType == 0){
                    return true;
                }
                $this->linkedType = 0;
                $pk = new SetEntityLinkPacket();
                $pk->from = $entity->getId();
                $pk->to = $this->getId();
                $pk->type = 3;
                $this->server->broadcastPacket($this->level->getPlayers(), $pk);
                if($this instanceof Player){
                    $pk = new SetEntityLinkPacket();
                    $pk->from = $entity->getId();
                    $pk->to = 0;
                    $pk->type = 3;
                    $this->dataPacket($pk);
                }
                if($this->linkedEntity->getLinkedType()){
                    $this->linkedEntity->setLinked(0, $this);
                }
                $this->linkedEntity = null;
                return true;
            case 1:
                if(!$entity->isAlive()){
                    return false;
                }
                $this->linkedEntity = $entity;
                $this->linkedType = 1;
                $entity->linkedEntity = $this;
                $entity->linkedType = 1;
                $pk = new SetEntityLinkPacket();
                $pk->from = $entity->getId();
                $pk->to = $this->getId();
                $pk->type = 2;
                $this->server->broadcastPacket($this->level->getPlayers(), $pk);
                if($this instanceof Player){
                    $pk = new SetEntityLinkPacket();
                    $pk->from = $entity->getId();
                    $pk->to = 0;
                    $pk->type = 2;
                    $this->dataPacket($pk);
                }
                return true;
            case 2:
                if(!$entity->isAlive()){
                    return false;
                }
                if($entity->getLinkedEntity() !== $this){
                    return $entity->linkEntity($this);
                }
                $this->linkedEntity = $entity;
                $this->linkedType = 2;
                return true;
            default:
                return false;
        }
    }

    public function getLinkedEntity(){
        return $this->linkedEntity;
    }

    public function getLinkedType(){
        return $this->linkedType;
    }

    /**
     * @param int $id
     *
     * @return mixed
     */
    public function getDataProperty($id){
        return isset($this->dataProperties[$id]) ? $this->dataProperties[$id][1] : null;
    }

    /**
     * @param int $id
     *
     * @return int
     */
    public function getDataPropertyType($id){
        return isset($this->dataProperties[$id]) ? $this->dataProperties[$id][0] : null;
    }

    /**
     * @param      $propertyId
     * @param      $id
     * @param bool $value
     * @param int  $type
     */
    public function setDataFlag($propertyId, $id, $value = true, $type = self::DATA_TYPE_BYTE){
        if($this->getDataFlag($propertyId, $id) !== $value){
            $flags = (int)$this->getDataProperty($propertyId);
            $flags ^= 1 << $id;
            $this->setDataProperty($propertyId, $type, $flags);
        }
    }

    /**
     * @param int $propertyId
     * @param int $id
     *
     * @return bool
     */
    public function getDataFlag($propertyId, $id){
        return (((int)$this->getDataProperty($propertyId)) & (1 << $id)) > 0;
    }

    public function __destruct(){
        $this->close();
    }

    public function setMetadata($metadataKey, MetadataValue $metadataValue){
        $this->server->getEntityMetadata()->setMetadata($this, $metadataKey, $metadataValue);
    }

    public function getMetadata($metadataKey){
        return $this->server->getEntityMetadata()->getMetadata($this, $metadataKey);
    }

    public function hasMetadata($metadataKey){
        return $this->server->getEntityMetadata()->hasMetadata($this, $metadataKey);
    }

    public function removeMetadata($metadataKey, Plugin $plugin){
        $this->server->getEntityMetadata()->removeMetadata($this, $metadataKey, $plugin);
    }

    public function __toString(){
        return (new \ReflectionClass($this))->getShortName() . "(" . $this->getId() . ")";
    }
}
