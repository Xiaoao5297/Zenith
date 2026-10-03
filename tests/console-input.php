<?php

require __DIR__ . "/../src/pocketmine/utils/ConsoleLineEditor.php";

$cases = [
	["help\n", ["help"]],
	["help\r", ["help"]],
	["help\x1b\n", ["help"]],
	["\x1bhelp\n", ["help"]],
	["help\x1b[\n", ["help"]],
	["help\x1b[3\r", ["help"]],
	["help\x1bO\r", ["help"]],
	["hep\x1b[Dl\n", ["help"]],
	["helpp\x7f\n", ["help"]],
	["helxp\x1b[D\x1b[D\x1b[3~\n", ["help"]],
	["help\n\x1b[A\n", ["help", "help"]],
	["bad\x1b[\x15help\n", ["help"]],
	["\xe4\xb8\xad\xe6\x96\x87\x7f\n", ["\xe4\xb8\xad"]],
];
foreach($cases as [$input, $expected]){
	$editor = new \pocketmine\utils\ConsoleLineEditor();
	$actual = [];
	ob_start();
	foreach(str_split($input) as $byte){
		$line = $editor->processByte($byte);
		if($line !== null){
			$actual[] = $line;
		}
	}
	ob_end_clean();
	if($actual !== $expected){
		throw new \RuntimeException("Unexpected result for " . bin2hex($input));
	}
}

$editor = new \pocketmine\utils\ConsoleLineEditor();
$editor->processByte("\x1b");
$editor->processByte("[");
usleep(300000);
ob_start();
foreach(str_split("help") as $byte){
	$editor->processByte($byte);
}
$line = $editor->processByte("\n");
ob_end_clean();
if($line !== "help"){
	throw new \RuntimeException("Incomplete escape sequence did not expire");
}
echo "Console editor: 14 cases passed\n";
