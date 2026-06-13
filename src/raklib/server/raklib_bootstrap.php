<?php
/**
 * Bootstrap for RakLib child process (started via proc_open).
 * Reads parameters from environment variables and opens communication channels.
 */

$bootstrapPath = getenv("RAKLIB_BOOTSTRAP_PATH");
$port = (int) getenv("RAKLIB_PORT");
$interface = getenv("RAKLIB_INTERFACE") ?: "0.0.0.0";
$mainPath = getenv("RAKLIB_MAIN_PATH") ?: "";
$intChanName = getenv("RAKLIB_INT_CHAN");
$extChanName = getenv("RAKLIB_EXT_CHAN");

require_once $bootstrapPath . "src/spl/ClassLoader.php";
require_once $bootstrapPath . "src/spl/BaseClassLoader.php";
require_once $bootstrapPath . "src/pocketmine/CompatibleClassLoader.php";

$loader = new CompatibleClassLoader();
$loader->addPath($bootstrapPath . "src");
$loader->addPath($bootstrapPath . "src" . DIRECTORY_SEPARATOR . "spl");
$loader->register(true);

require_once $bootstrapPath . "src/raklib/server/RakLibDummyLogger.php";

// Open channels for communication with main thread
$intChan = \parallel\Channel::open($intChanName);
$extChan = \parallel\Channel::open($extChanName);

// Create a simple proxy using channels
$proxy = new class($extChan, $intChan, $mainPath) {
    public $externalQueue, $internalQueue;
    public $shutdown = false;
    private $mainPath;

    public function __construct($ext, $int, $mainPath){
        $this->externalQueue = $ext;
        $this->internalQueue = $int;
        $this->mainPath = $mainPath;
    }

    public function pushThreadToMainPacket($str){
        // Write length-prefixed packet to stdout (main thread reads via pipe)
        $data = pack("N", strlen($str)) . $str;
        $written = @fwrite(STDOUT, $data);
        if($written === false){
            // Pipe broken, shutdown
            $this->shutdown = true;
        }
    }
    public function readMainToThreadPacket(){
        try{ return $this->internalQueue->recv(); }
        catch(\Throwable $e){ return ""; }
    }
    public function pushMainToThreadPacket($str){ $this->internalQueue->send($str); }
    public function readThreadToMainPacket(){
        try{ return $this->externalQueue->recv(); }
        catch(\Throwable $e){ return ""; }
    }
    public function isShutdown(){ return $this->shutdown; }
    public function shutdown(){ $this->shutdown = true; }
    public function getLogger(){ return new RakLibDummyLogger(); }
    public function cleanPath($path){
        return rtrim(str_replace(["\\", ".php", "phar://", rtrim(str_replace(["\\", "phar://"], ["/", ""], $this->mainPath), "/")], ["/", "", "", ""], $path), "/");
    }
    public function getPort(){ return 0; }
    public function getInterface(){ return "0.0.0.0"; }
};

$proxy->shutdown = false;
gc_enable();
error_reporting(-1);
ini_set("display_errors", 1);
ini_set("display_startup_errors", 1);

set_error_handler(function($errno, $errstr, $errfile, $errline) use ($proxy){
    if(error_reporting() === 0) return false;
    $errfile = $proxy->cleanPath($errfile);
    echo "[RakLib] Error: \"$errstr\" in \"$errfile\" at line $errline\n";
    return true;
}, E_ALL);

register_shutdown_function(function() use ($proxy){
    if(!$proxy->isShutdown()){
        echo "[RakLib] RakLib crashed!\n";
    }
});

$socket = new raklib\server\UDPServerSocket($proxy->getLogger(), $port, $interface);
new raklib\server\SessionManager($proxy, $socket);
