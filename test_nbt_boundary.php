<?php
/**
 * NBT 边界测试工具 — 本地单元测试
 * 直接测试 NBT 解析器的长度限制，无需连接服务器
 *
 * 用法: php test_nbt_boundary.php
 */

// 自动寻找核心路径
$paths = [
    __DIR__ . "/src/pocketmine",
    __DIR__ . "/../src/pocketmine",
    getcwd() . "/src/pocketmine",
];
$found = false;
foreach($paths as $p){
    if(is_dir($p)){
        chdir(dirname(dirname($p)));
        $found = true;
        break;
    }
}
if(!$found){
    die("请在 Incore-Pro 或 mc 目录下运行本脚本\n");
}

// 简单自动加载（不依赖 pthreads/Threaded）
spl_autoload_register(function($class){
    $file = __DIR__ . "/src/" . str_replace("\\", "/", $class) . ".php";
    if(is_file($file)){
        require_once $file;
    }
});

use pocketmine\nbt\NBT;
use pocketmine\nbt\tag\ListTag;
use pocketmine\nbt\tag\ByteArrayTag;
use pocketmine\nbt\tag\IntArrayTag;
use pocketmine\nbt\tag\CompoundTag;

$passed = 0;
$failed = 0;

function test(string $name, callable $fn){
    global $passed, $failed;
    try{
        $fn();
        echo "  ✓ $name\n";
        $passed++;
    }catch(\Throwable $e){
        echo "  ✗ $name — " . $e->getMessage() . "\n";
        $failed++;
    }
}

function assert_eq($expected, $actual, string $msg){
    if($expected !== $actual){
        throw new \RuntimeException("$msg: 期望 $expected, 实际 " . var_export($actual, true));
    }
}

echo "=== NBT 边界测试 ===\n\n";

function makeNBT($buf){
    $nbt = new \pocketmine\nbt\NBT(\pocketmine\nbt\NBT::LITTLE_ENDIAN);
    $nbt->buffer = $buf;
    // offset 是私有属性，用反射设置
    $ref = new \ReflectionClass($nbt);
    $prop = $ref->getProperty("offset");
    $prop->setAccessible(true);
    $prop->setValue($nbt, 0);
    return $nbt;
}

// ---- 测试 1: ListTag 超大 size ----
echo "[ListTag]\n";

test("size=0 正常", function(){
    $nbt = makeNBT(chr(9) . pack("V", 0)); // TAG_Int x 0
    $tag = new ListTag("");
    $tag->read($nbt);
    assert_eq(0, count($tag->getValue()), "空 list 大小应为 0");
});

test("size=16384 边界值", function(){
    $nbt = makeNBT(chr(1) . pack("V", 16384) . str_repeat("\x00", 16384)); // TAG_Byte x 16384
    $tag = new ListTag("");
    $tag->read($nbt);
    assert_eq(16384, count($tag->getValue()), "16384 条应全部读取");
});

test("size=16385 超限被截断", function(){
    $nbt = makeNBT(chr(1) . pack("V", 16385)); // size 超限，无实际数据
    $tag = new ListTag("");
    $tag->read($nbt);
    assert_eq(0, count($tag->getValue()), "超限 size 应被截断为 0");
});

test("size=负数", function(){
    $nbt = makeNBT(chr(1) . pack("V", -1));
    $tag = new ListTag("");
    $tag->read($nbt);
    assert_eq(0, count($tag->getValue()), "负 size 应被截断为 0");
});

test("size=0x7FFFFFFF 超巨大", function(){
    $nbt = makeNBT(chr(1) . pack("V", 0x7FFFFFFF));
    $tag = new ListTag("");
    $tag->read($nbt);
    assert_eq(0, count($tag->getValue()), "超大 size 应被截断");
});

echo "\n[ByteArrayTag]\n";

test("len=0 正常", function(){
    $nbt = makeNBT(pack("V", 0));
    $tag = new ByteArrayTag("");
    $tag->read($nbt);
    assert_eq("", $tag->getValue(), "空数组应为空字符串");
});

test("len=2097152 边界值(2MB)", function(){
    $nbt = makeNBT(pack("V", 2097152) . str_repeat("\x00", 2097152));
    $tag = new ByteArrayTag("");
    $tag->read($nbt);
    assert_eq(2097152, strlen($tag->getValue()), "2MB 应正常读取");
});

test("len=2097153 超限被截断", function(){
    $nbt = makeNBT(pack("V", 2097153));
    $tag = new ByteArrayTag("");
    $tag->read($nbt);
    assert_eq("", $tag->getValue(), "超限 len 应被截断为 0");
});

test("len=负数", function(){
    $nbt = makeNBT(pack("V", -1));
    $tag = new ByteArrayTag("");
    $tag->read($nbt);
    assert_eq("", $tag->getValue(), "负 len 应被截断为 0");
});

echo "\n[IntArrayTag]\n";

test("size=0 正常", function(){
    $nbt = makeNBT(pack("V", 0));
    $tag = new IntArrayTag("");
    $tag->read($nbt);
    assert_eq(0, count($tag->getValue()), "空数组大小应为 0");
});

test("size=32768 边界值", function(){
    $nbt = makeNBT(pack("V", 32768) . str_repeat(pack("V", 1), 32768));
    $tag = new IntArrayTag("");
    $tag->read($nbt);
    assert_eq(32768, count($tag->getValue()), "32768 个 int 应全部读取");
});

test("size=32769 超限被截断", function(){
    $nbt = makeNBT(pack("V", 32769));
    $tag = new IntArrayTag("");
    $tag->read($nbt);
    assert_eq(0, count($tag->getValue()), "超限 size 应被截断为 0");
});

test("size=负数", function(){
    $nbt = makeNBT(pack("V", -1));
    $tag = new IntArrayTag("");
    $tag->read($nbt);
    assert_eq(0, count($tag->getValue()), "负 size 应被截断为 0");
});

// ---- 结论 ----
echo "\n=== 测试完成: $passed 通过, $failed 失败 ===\n";
exit($failed > 0 ? 1 : 0);
