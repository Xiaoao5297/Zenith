<?php
/**
 * NBT 边界测试工具 — 网络测试
 * 连接到真实服务器，发送超大 NBT 数据包
 * 确认服务端不会 OOM 崩溃
 *
 * 用法: php test_nbt_network.php [服务器地址] [端口]
 * 默认: 127.0.0.1 19132
 */

$host = $argv[1] ?? "127.0.0.1";
$port = (int)($argv[2] ?? 19132);
$timeout = 5;

echo "=== NBT 边界网络测试 ===\n";
echo "目标: $host:$port\n\n";

// ---- RakNet 协议常量 ----
define("RAKNET_UNCONNECTED_PING", 0x01);
define("RAKNET_UNCONNECTED_PONG", 0x1c);
define("RAKNET_OPEN_CONNECTION_REQUEST_1", 0x05);
define("RAKNET_OPEN_CONNECTION_REPLY_1", 0x06);
define("RAKNET_OPEN_CONNECTION_REQUEST_2", 0x07);
define("RAKNET_OPEN_CONNECTION_REPLY_2", 0x08);
define("RAKNET_GAME_PACKET", 0x80 | 0x60); // 0xfe 内部封装包

define("MAGIC", "\x00\xff\xff\x00\xfe\xfe\xfe\xfe\xfd\xfd\xfd\xfd\x12\x34\x56\x78");
define("SERVER_ID", 0); // 会被 pong 覆盖

// ---- 工具函数 ----
function udp_send_recv($sock, $buf, &$recvBuf, $maxRetry = 3): bool{
    for($i = 0; $i < $maxRetry; $i++){
        @socket_sendto($sock, $buf, strlen($buf), 0, $GLOBALS['host'], $GLOBALS['port']);
        $recvBuf = "";
        $peer = "";
        $port = 0;
        $ret = @socket_recvfrom($sock, $recvBuf, 2048, 0, $peer, $port);
        if($ret !== false && $ret > 0 && strlen($recvBuf) > 0){
            return true;
        }
        usleep(200000);
    }
    return false;
}

// ---- 1. 创建 UDP 套接字 ----
$sock = socket_create(AF_INET, SOCK_DGRAM, SOL_UDP);
socket_set_option($sock, SOL_SOCKET, SO_RCVTIMEO, ["sec" => 2, "usec" => 0]);
socket_set_option($sock, SOL_SOCKET, SO_SNDTIMEO, ["sec" => 2, "usec" => 0]);
socket_connect($sock, $host, $port);

echo "[1/4] Ping 服务器...\n";

// UNCONNECTED_PING
$ping = pack("C", RAKNET_UNCONNECTED_PING) . pack("NN", 0, microtime(true) * 1000) . MAGIC;
if(!udp_send_recv($sock, $ping, $pong)){
    die("服务器无响应\n");
}

if(ord($pong[0]) !== RAKNET_UNCONNECTED_PONG){
    die("Pong 响应异常: 0x" . dechex(ord($pong[0])) . "\n");
}

$serverGuid = Binary::readLong(substr($pong, 17, 8));
echo "  服务器 GUID: $serverGuid\n";

// ---- 2. OPEN_CONNECTION_REQUEST_1 ----
echo "[2/4] 发起连接 (MTU=1500)...\n";

$req1 = pack("C", RAKNET_OPEN_CONNECTION_REQUEST_1) . MAGIC . pack("CC", 0, 1500);
if(!udp_send_recv($sock, $req1, $rep1)){
    die("OPEN_CONNECTION_REQUEST_1 无响应\n");
}

if(ord($rep1[0]) !== RAKNET_OPEN_CONNECTION_REPLY_1){
    die("REPLY_1 异常: 0x" . dechex(ord($rep1[0])) . "\n");
}

$mtu = unpack("v", substr($rep1, 18, 2))[1];
$serverGuid = Binary::readLong(substr($rep1, 20, 8));
echo "  MTU: $mtu, GUID: $serverGuid\n";

// ---- 3. OPEN_CONNECTION_REQUEST_2 ----
echo "[3/4] 完成连接握手...\n";

$clientId = mt_rand(0, 0x7FFFFFFF);
$req2 = pack("C", RAKNET_OPEN_CONNECTION_REQUEST_2) . MAGIC . pack("N", $host) . pack("n", $port) . pack("v", $mtu) . pack("NN", 0, $clientId);
if(!udp_send_recv($sock, $req2, $rep2)){
    die("OPEN_CONNECTION_REQUEST_2 无响应\n");
}

if(ord($rep2[0]) !== RAKNET_OPEN_CONNECTION_REPLY_2){
    die("REPLY_2 异常\n");
}

echo "  连接已建立!\n";
usleep(100000);

// ---- 4. 发送超大 NBT 的 LoginPacket ----
echo "[4/4] 发送恶意 LoginPacket (List size=999999)...\n";

// 构造一个超大 NBT: CompoundTag 包含一个 ListTag, size=999999
// 但体积很小（只写 header，实际不写 999999 条数据）
$maliciousNbt = chr(10) . pack("v", 0) . chr(9) . pack("v", 0) . chr(1) . pack("V", 999999);

// LoginPacket: 协议版本 + 连接请求 (NBT) + 皮肤数据 (NBT)
// MCPE 0.14.x LoginPacket 格式简化版
$username = "NBT_Test_" . substr(md5(mt_rand()), 0, 4);

// 构建 LOCALE + 用户名 + 协议版本的标准 LoginPacket
// Protocol 0.14: 
$loginData = pack("c", -1) // protocol version (任意)
    . pack("V", strlen($username)) . $username
    . pack("V", 0) // 协议 1
    . pack("V", 0) // 协议 2
    . pack("V", 0) // 协议 3
    . pack("C", 0) // 是否有 NBT 数据
    . pack("V", strlen($maliciousNbt)) . $maliciousNbt // 客户端 NBT（含恶意的超大 List）
    . pack("C", 0); // 是否有皮肤 NBT

// 封装为 BatchPacket (0x04)
$batchPayload = chr(0x04) . pack("V", strlen($loginData)) . $loginData;

// zlib 压缩
$compressed = @gzencode($batchPayload, 0, FORCE_DEFLATE);
if($compressed === false){
    $compressed = zlib_encode($batchPayload, ZLIB_ENCODING_DEFLATE, 0);
}

$frame = chr(0xfe) . pack("V", strlen($compressed)) . $compressed;

// 封装为 RakNet 内部包 (0x80 | 0x60 = 0xFE)
// 加 0x8e 前缀
$internalPacket = chr(0x8e) . pack("V", strlen($frame)) . $frame;

// 通过 UDP 发送
socket_sendto($sock, $internalPacket, strlen($internalPacket), 0, $host, $port);
echo "  恶意包已发送\n";

// ---- 5. 等待 3 秒，发个 ping 确认服务器是否存活 ----
echo "  等待 3 秒验证服务器是否存活...\n";
sleep(3);

$ping2 = pack("C", RAKNET_UNCONNECTED_PING) . pack("NN", 0, microtime(true) * 1000) . MAGIC;
if(udp_send_recv($sock, $ping2, $pong2, 1)){
    echo "\n✓ 测试通过: 服务器存活，处理恶意 NBT 时未崩溃\n";
}else{
    echo "\n⚠ 服务器无响应！可能已崩溃\n";
}

socket_close($sock);

// ---- Binary 工具 ----
class Binary{
    static function readLong($buf){
        if(strlen($buf) < 8) return 0;
        $arr = unpack("N2", $buf);
        return ($arr[1] << 32) | ($arr[2] & 0xFFFFFFFF);
    }
}
