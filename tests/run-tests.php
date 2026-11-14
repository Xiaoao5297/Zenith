<?php

/**
 * Zero-dependency test runner for tests/ .
 *
 * Convention: every *Test.php file in this directory declares a class named
 * after the file with a single entry point:
 *
 *     public static function run() : array
 *
 * run() returns a list of [name, pass, detail] entries. This runner includes
 * each file, calls run(), prints PASS/FAIL per case and exits 0 when all
 * cases pass, 1 otherwise. No composer / phpunit / PocketMine runtime needed:
 * each test file is responsible for requiring only the classes it can load
 * standalone.
 *
 * Usage: php tests/run-tests.php
 */

$passed = 0;
$failures = 0;

$files = glob(__DIR__ . "/*Test.php");
sort($files);
if(count($files) === 0){
	fwrite(STDERR, "No *Test.php files found in " . __DIR__ . "\n");
	exit(1);
}

foreach($files as $file){
	$className = basename($file, ".php");
	require $file;
	if(!class_exists($className) or !method_exists($className, "run")){
		fwrite(STDERR, "SKIP $className: no class with static run() found\n");
		continue;
	}

	$results = $className::run();
	foreach($results as [$name, $pass, $detail]){
		$label = "$className::$name";
		if($pass){
			++$passed;
			echo "PASS $label" . ($detail !== "" ? " - $detail" : "") . "\n";
		}else{
			++$failures;
			echo "FAIL $label" . ($detail !== "" ? " - $detail" : "") . "\n";
		}
	}
}

$total = $passed + $failures;
echo "\n$passed/$total passed";
if($failures > 0){
	echo ", $failures failed";
}
echo "\n";

exit($failures > 0 ? 1 : 0);
