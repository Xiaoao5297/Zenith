<?php

/*
 *
 *  ____            _        _   __  __ _                  __  __ ____  
 * |  _ \ ___   ___| | _____| |_|  \/  (_)_ __   ___      |  \/  |  _ \ 
 * | |_) / _ \ / __| |/ / _ \ __| |\/| | | '_ \ / _ \_____| |\/| | |_) |
 * |  __/ (_) | (__|   <  __/ |_| |  | | | | | |  __/_____| |  | |  __/ 
 * |_|   \___/ \___|_|\_\___|\__|_|  |_|_|_| |_|\___|     |_|  |_|_| 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author PocketMine Team
 * @link http://www.pocketmine.net/
 * 
 *
*/

/**
 * Set-up wizard used on the first run
 * Can be disabled with --no-wizard
 */
namespace pocketmine\wizard;

use pocketmine\utils\Config;
use pocketmine\utils\Utils;

class Installer{
	const DEFAULT_NAME = "Minecraft PE 0.14 Server";
	const DEFAULT_PORT = 19132;
	const DEFAULT_MEMORY = 512;
	const DEFAULT_PLAYERS = 50;
	const DEFAULT_GAMEMODE = 0;
	const DEFAULT_LEVEL_NAME = "world";
	const DEFAULT_LEVEL_TYPE = "gorigional";
	const DEFAULT_FLAT_PRESET = "2;7,2x3,2;1;";

	/**
	 * 向导里可选的世界生成器: 输入 (编号或名称) => level-type 值。
	 * 只接受这张白名单，因为向导运行时 Server 还没有注册 Generator，
	 * 无法用 Generator::getGenerator() 校验。
	 */
	private static $generatorChoices = [
		"1" => "gorigional",
		"gorigional" => "gorigional",
		"original" => "gorigional",
		"2" => "normal",
		"normal" => "normal",
		"default" => "normal",
		"3" => "flat",
		"flat" => "flat",
		"4" => "void",
		"void" => "void"
	];

	private $defaultLang;

	public function __construct(){
		@mkdir(\pocketmine\DATA . "config", 0777, true);
		echo "[*] Zenith 配置程序\n";
		echo "[*] 请选择语言:\n";
		foreach(InstallerLang::$languages as $short => $native){
			echo " $native => $short\n";
		}
		do{
			echo "[?] 语言 (默认中文简体[chs]): ";
			$lang = strtolower($this->getInput("chs"));
			if(!isset(InstallerLang::$languages[$lang])){
				echo "[!] 没有该语言\n";
				$lang = false;
			}
			$this->defaultLang = $lang;
		}while($lang == false);
		$this->lang = new InstallerLang($lang);


		echo "[*] " . $this->lang->language_has_been_selected . "\n";

		if(!$this->showLicense()){
			@\pocketmine\kill(getmypid());
			exit(-1);
		}

		echo "[?] " . $this->lang->skip_installer . " (y/N): ";
		if(strtolower($this->getInput()) === "y"){
			return;
		}
		echo "\n";
		$this->welcome();
		$this->generateBaseConfig();
		$this->generateUserFiles();

		$this->networkFunctions();

		$this->endWizard();
	}

	public function getDefaultLang(){
		return $this->defaultLang;
	}

	private function showLicense(){
		echo $this->lang->welcome_to_pocketmine . "\n";
		echo <<<LICENSE
  Chinese License:
  本程序是自由软件：你可以根据自由软件基金会发布的 GNU 通用公共许可证（GPL）条款，
  重新分发和/或修改它，许可证可以是第3版，或（你选择的）任何更新版本。

  本程序的发布是希望它有用，但不提供任何担保，甚至不提供适销性或特定用途适用性的默示担保。
  详见 GNU 通用公共许可证。

  你应该已经随本程序收到了 GNU 通用公共许可证的副本。如果没有，请见 <https://www.gnu.org/licenses/>。

  English License:
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.

  Bug reports / 问题反馈: QQ 2574459918 / 2574459918@qq.com

LICENSE;
		echo "\n[?] " . $this->lang->accept_license . " (y/N): ";
		if(strtolower($this->getInput("n")) != "y"){
			echo "[!] " . $this->lang->you_have_to_accept_the_license . "\n";
			sleep(5);

			return false;
		}

		return true;
	}

	private function welcome(){
		echo "[*] " . $this->lang->setting_up_server_now . "\n";
		echo "[*] " . $this->lang->default_values_info . "\n";
		echo "[*] " . $this->lang->server_properties . "\n";

	}

	private function generateBaseConfig(){
		$config = new Config(\pocketmine\DATA . "config" . DIRECTORY_SEPARATOR . "server.properties", Config::PROPERTIES);
		echo "[?] " . $this->lang->name_your_server . " (" . self::DEFAULT_NAME . "): ";
		$config->set("server-name", $this->getInput(self::DEFAULT_NAME));
		echo "[?] " . $this->lang->name_your_motd . " (" . self::DEFAULT_NAME . "): ";
		$config->set("motd", $this->getInput(self::DEFAULT_NAME));
		echo "[*] " . $this->lang->port_warning . "\n";
		do{
			echo "[?] " . $this->lang->server_port . " (" . self::DEFAULT_PORT . "): ";
			$port = (int) $this->getInput(self::DEFAULT_PORT);
			if($port <= 0 or $port > 65535){
				echo "[!] " . $this->lang->invalid_port . "\n";
			}
		}while($port <= 0 or $port > 65535);
		$config->set("server-port", $port);
		
		echo "[?] " . $this->lang->level_name . " (" . self::DEFAULT_LEVEL_NAME . "): ";
		$levelName = $this->getInput(self::DEFAULT_LEVEL_NAME);
		$config->set("level-name", $levelName);
		if(Utils::isValidLevelName($levelName) and is_dir(\pocketmine\DATA . "worlds" . DIRECTORY_SEPARATOR . $levelName)){
			echo "[!] " . $this->lang->generator_world_exists . "\n";
		}

		echo "[*] " . $this->lang->generator_info . "\n";
		echo "    1) gorigional - " . $this->lang->generator_gorigional . "\n";
		echo "    2) normal     - " . $this->lang->generator_normal . "\n";
		echo "    3) flat       - " . $this->lang->generator_flat . "\n";
		echo "    4) void       - " . $this->lang->generator_void . "\n";
		do{
			echo "[?] " . $this->lang->level_type . " (1): ";
			$type = self::normalizeGenerator($this->getInput("1"));
			if($type === null){
				echo "[!] " . $this->lang->invalid_level_type . "\n";
			}
		}while($type === null);
		$config->set("level-type", $type);

		if($type === "flat"){
			do{
				echo "[?] " . $this->lang->flat_preset . " (" . self::DEFAULT_FLAT_PRESET . "): ";
				$preset = trim($this->getInput(self::DEFAULT_FLAT_PRESET));
				if(!self::isValidFlatPreset($preset)){
					echo "[!] " . $this->lang->invalid_flat_preset . "\n";
				}
			}while(!self::isValidFlatPreset($preset));
			$config->set("generator-settings", $preset);
		}

		echo "[?] " . $this->lang->level_seed . ": ";
		$seed = trim($this->getInput(""));
		if($seed !== ""){
			$config->set("level-seed", $seed);
		}

		/*echo "[*] " . $this->lang->ram_warning . "\n";
		echo "[?] " . $this->lang->server_ram . " (" . self::DEFAULT_MEMORY . "): ";
		$config->set("memory-limit", ((int) $this->getInput(self::DEFAULT_MEMORY)) . "M");*/
		echo "[*] " . $this->lang->gamemode_info . "\n";
		do{
			echo "[?] " . $this->lang->default_gamemode . ": (" . self::DEFAULT_GAMEMODE . "): ";
			$gamemode = (int) $this->getInput(self::DEFAULT_GAMEMODE);
		}while($gamemode < 0 or $gamemode > 3);
		$config->set("gamemode", $gamemode);
		echo "[?] " . $this->lang->max_players . " (" . self::DEFAULT_PLAYERS . "): ";
		$config->set("max-players", (int) $this->getInput(self::DEFAULT_PLAYERS));
		echo "[*] " . $this->lang->spawn_protection_info . "\n";
		echo "[?] " . $this->lang->spawn_protection . " (Y/n): ";
		if(strtolower($this->getInput("y")) == "n"){
			$config->set("spawn-protection", -1);
		}else{
			$config->set("spawn-protection", 16);
		}
		
		echo "[?] " . $this->lang->announce_player_achievements . " (y/N): ";
		if(strtolower($this->getInput("n")) === "y"){
			$config->set("announce-player-achievements", "on");
		}else{
			$config->set("announce-player-achievements", "off");
		}
		$config->save();
	}

	private function generateUserFiles(){
		echo "[*] " . $this->lang->op_info . "\n";
		echo "[?] " . $this->lang->op_who . ": ";
		$op = strtolower($this->getInput(""));
		if($op === ""){
			echo "[!] " . $this->lang->op_warning . "\n";
		}else{
			$ops = new Config(\pocketmine\DATA . "config" . DIRECTORY_SEPARATOR . "ops.txt", Config::ENUM);
			$ops->set($op, true);
			$ops->save();
		}
		echo "[*] " . $this->lang->whitelist_info . "\n";
		echo "[?] " . $this->lang->whitelist_enable . " (y/N): ";
		$config = new Config(\pocketmine\DATA . "config" . DIRECTORY_SEPARATOR . "server.properties", Config::PROPERTIES);
		if(strtolower($this->getInput("n")) === "y"){
			echo "[!] " . $this->lang->whitelist_warning . "\n";
			$config->set("white-list", true);
		}else{
			$config->set("white-list", false);
		}
		$config->save();
	}

	private function networkFunctions(){
		$config = new Config(\pocketmine\DATA . "config" . DIRECTORY_SEPARATOR . "server.properties", Config::PROPERTIES);
		echo "[!] " . $this->lang->query_warning1 . "\n";
		echo "[!] " . $this->lang->query_warning2 . "\n";
		echo "[?] " . $this->lang->query_disable . " (y/N): ";
		if(strtolower($this->getInput("n")) === "y"){
			$config->set("enable-query", false);
		}else{
			$config->set("enable-query", true);
		}

		echo "[*] " . $this->lang->rcon_info . "\n";
		echo "[?] " . $this->lang->rcon_enable . " (y/N): ";
		if(strtolower($this->getInput("n")) === "y"){
			$config->set("enable-rcon", true);
			$password = substr(base64_encode(@Utils::getRandomBytes(20, false)), 3, 10);
			$config->set("rcon.password", $password);
			echo "[*] " . $this->lang->rcon_password . ": " . $password . "\n";
		}else{
			$config->set("enable-rcon", false);
		}

		/*echo "[*] " . $this->lang->usage_info . "\n";
		echo "[?] " . $this->lang->usage_disable . " (y/N): ";
		if(strtolower($this->getInput("n")) === "y"){
			$config->set("send-usage", false);
		}else{
			$config->set("send-usage", true);
		}*/
		$config->save();


		echo "[*] " . $this->lang->ip_get . "\n";

		$externalIP = Utils::getIP();
		$internalIP = gethostbyname(trim(`hostname`));

		echo "[!] " . $this->lang->get("ip_warning", ["{{EXTERNAL_IP}}", "{{INTERNAL_IP}}"], [$externalIP, $internalIP]) . "\n";
		echo "[!] " . $this->lang->ip_confirm;
		$this->getInput();
	}

	private function endWizard(){
		echo "[*] " . $this->lang->you_have_finished . "\n";
		echo "[*] " . $this->lang->pocketmine_plugins . "\n";
		echo "[*] " . $this->lang->pocketmine_will_start . "\n\n\n";
		sleep(4);
	}

	private function getInput($default = ""){
		$input = trim(fgets(STDIN));

		return $input === "" ? $default : $input;
	}

	/**
	 * 把向导里输入的生成器编号/名称归一化为 level-type 值。
	 * 空输入按默认 (normal) 处理。
	 *
	 * @param mixed $input
	 *
	 * @return string|null 无法识别时返回 null
	 */
	public static function normalizeGenerator($input){
		if(!is_string($input)){
			return null;
		}
		$key = strtolower(trim($input));
		if($key === ""){
			$key = "1";
		}

		return isset(self::$generatorChoices[$key]) ? self::$generatorChoices[$key] : null;
	}

	/**
	 * 校验超平坦预设，格式为 "版本;层;生物群系;装饰选项"，例如 2;7,2x3,2;1;
	 * 第四段之后的内容 (decoration(...) 等) 不做校验，交给 Flat 生成器解析。
	 *
	 * @param mixed $preset
	 *
	 * @return bool
	 */
	public static function isValidFlatPreset($preset) : bool{
		if(!is_string($preset)){
			return false;
		}

		return preg_match('/^[0-9]+;[0-9a-z_,:x]*;[0-9a-z_,:x]*;/i', trim($preset)) === 1;
	}


}
