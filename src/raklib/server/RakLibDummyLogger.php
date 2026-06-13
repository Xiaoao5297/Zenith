<?php

/**
 * Dummy logger used inside parallel Runtimes where the real logger is not accessible.
 */
class RakLibDummyLogger extends \ThreadedLogger{
	public function log($level, $message){}
	public function emergency($message){}
	public function alert($message){}
	public function critical($message){}
	public function error($message){}
	public function warning($message){}
	public function notice($message){}
	public function info($message){}
	public function debug($message){}
	public function logException(\Throwable $e, $trace = null){}
	public function shutdown(){}
}
