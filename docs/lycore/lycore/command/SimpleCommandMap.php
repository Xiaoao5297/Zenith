<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝
 * ╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 *  ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: U core
 *
 * @Links:
 *  > LY Core
 *  > LY Core Project
*/

namespace lycore\command;

use lycore\command\defaults\BanCommand;
use lycore\command\defaults\BanIpCommand;
use lycore\command\defaults\BanListCommand;
use lycore\command\defaults\BiomeCommand;
use lycore\command\defaults\BotCommand;
use lycore\command\defaults\CaveCommand;
use lycore\command\defaults\ChunkInfoCommand;
use lycore\command\defaults\DefaultGamemodeCommand;
use lycore\command\defaults\DeopCommand;
use lycore\command\defaults\DifficultyCommand;
use lycore\command\defaults\DumpMemoryCommand;
use lycore\command\defaults\EffectCommand;
use lycore\command\defaults\EnchantCommand;
use lycore\command\defaults\GameruleCommand;
use lycore\command\defaults\GamemodeCommand;
use lycore\command\defaults\GarbageCollectorCommand;
use lycore\command\defaults\GiveCommand;
use lycore\command\defaults\HelpCommand;
use lycore\command\defaults\KickCommand;
use lycore\command\defaults\KillCommand;
use lycore\command\defaults\ListCommand;
use lycore\command\defaults\LoadPluginCommand;
use lycore\command\defaults\LvdatCommand;
use lycore\command\defaults\MeCommand;
use lycore\command\defaults\OpCommand;
use lycore\command\defaults\PardonCommand;
use lycore\command\defaults\PardonIpCommand;
use lycore\command\defaults\ParticleCommand;
use lycore\command\defaults\PluginsCommand;
use lycore\command\defaults\ReloadCommand;
use lycore\command\defaults\SaveCommand;
use lycore\command\defaults\SaveOffCommand;
use lycore\command\defaults\SaveOnCommand;
use lycore\command\defaults\SayCommand;
use lycore\command\defaults\SeedCommand;
use lycore\command\defaults\SetBlockCommand;
use lycore\command\defaults\SetWorldSpawnCommand;
use lycore\command\defaults\SpawnpointCommand;
use lycore\command\defaults\StatusCommand;
use lycore\command\defaults\StopCommand;
use lycore\command\defaults\SummonCommand;
use lycore\command\defaults\TeleportCommand;
use lycore\command\defaults\TellCommand;
use lycore\command\defaults\TimeCommand;
use lycore\command\defaults\PingCommand;
use lycore\command\defaults\SneakCommand;
use lycore\command\defaults\SprintCommand;
use lycore\command\defaults\TimingsCommand;
use lycore\command\defaults\VanillaCommand;
use lycore\command\defaults\VersionCommand;
use lycore\command\defaults\WhitelistCommand;
use lycore\command\defaults\XpCommand;
use lycore\command\defaults\FillCommand;
use lycore\command\defaults\EmojiCommand;
use lycore\event\TranslationContainer;
use lycore\Player;
use lycore\Server;
use lycore\utils\MainLogger;
use lycore\utils\TextFormat;

use lycore\command\defaults\MakeServerCommand;
use lycore\command\defaults\ExtractPluginCommand;
use lycore\command\defaults\ExtractPharCommand;
use lycore\command\defaults\MakePluginCommand;
use lycore\command\defaults\BancidbynameCommand;
use lycore\command\defaults\BanipbynameCommand;
use lycore\command\defaults\BanCidCommand;
use lycore\command\defaults\PardonCidCommand;
use lycore\command\defaults\WeatherCommand;
use lycore\command\defaults\OplistCommand;
use lycore\command\defaults\ClearbagCommand;
use lycore\command\defaults\NoticeCommand;

class SimpleCommandMap implements CommandMap{

	/**
	 * @var Command[]
	 */
	protected $knownCommands = [];

	/** @var Server */
	private $server;
	/** @var TargetSelector */
	private $targetSelector;

	public function __construct(Server $server){
		$this->server = $server;
		$this->targetSelector = new TargetSelector($server);
		$this->setDefaultCommands();
	}

	private function setDefaultCommands(){
		$this->register("lycore", new WeatherCommand("weather"));
		if($this->server->isBotEnabled()){
			$this->register("lycore", new BotCommand("bot"));
		}

		$this->register("lycore", new BanCidCommand("bancid"));
		$this->register("lycore", new PardonCidCommand("pardoncid"));
		$this->register("lycore", new BancidbynameCommand("bancidbyname"));
		$this->register("lycore", new BanipbynameCommand("banipbyname"));

		$this->register("lycore", new ExtractPharCommand("extractphar"));
		$this->register("lycore", new ExtractPluginCommand("extractplugin"));
		$this->register("lycore", new MakePluginCommand("makeplugin"));
		$this->register("lycore", new MakeServerCommand("ms"));
		$this->register("lycore", new MakeServerCommand("makeserver"));
		$this->register("lycore", new ExtractPluginCommand("ep"));
		$this->register("lycore", new MakePluginCommand("mp"));

		$this->register("lycore", new LoadPluginCommand("loadplugin"));

		$this->register("lycore", new LvdatCommand("lvdat"));
		$this->register("lycore", new BiomeCommand("biome"));
		$this->register("lycore", new CaveCommand("cave"));
		$this->register("lycore", new ChunkInfoCommand("chunkinfo"));

		$this->register("lycore", new VersionCommand("version"));
		$this->register("lycore", new FillCommand("fill"));
		$this->register("lycore", new PluginsCommand("plugins"));
		$this->register("lycore", new SeedCommand("seed"));
		$this->register("lycore", new HelpCommand("help"));
		$this->register("lycore", new StopCommand("stop"));
		$this->register("lycore", new TellCommand("tell"));
		$this->register("lycore", new DefaultGamemodeCommand("defaultgamemode"));
		$this->register("lycore", new BanCommand("ban"));
		$this->register("lycore", new BanIpCommand("ban-ip"));
		$this->register("lycore", new BanListCommand("banlist"));
		$this->register("lycore", new PardonCommand("pardon"));
		$this->register("lycore", new PardonIpCommand("pardon-ip"));
		$this->register("lycore", new SayCommand("say"));
		$this->register("lycore", new MeCommand("me"));
		$this->register("lycore", new ListCommand("list"));
		$this->register("lycore", new DifficultyCommand("difficulty"));
		$this->register("lycore", new KickCommand("kick"));
		$this->register("lycore", new OpCommand("op"));
		$this->register("lycore", new DeopCommand("deop"));
		$this->register("lycore", new WhitelistCommand("whitelist"));
		$this->register("lycore", new SaveOnCommand("save-on"));
		$this->register("lycore", new SaveOffCommand("save-off"));
		$this->register("lycore", new SaveCommand("save-all"));
		$this->register("lycore", new GiveCommand("give"));
		$this->register("lycore", new EffectCommand("effect"));
		$this->register("lycore", new EnchantCommand("enchant"));
		$this->register("lycore", new ParticleCommand("particle"));
		$this->register("lycore", new GameruleCommand("gamerule"));
		$this->register("lycore", new GamemodeCommand("gamemode"));
		$this->register("lycore", new KillCommand("kill"));
		$this->register("lycore", new SpawnpointCommand("spawnpoint"));
		$this->register("lycore", new SetWorldSpawnCommand("setworldspawn"));
		$this->register("lycore", new SummonCommand("summon"));
		$this->register("lycore", new TeleportCommand("tp"));
		$this->register("lycore", new TimeCommand("time"));
		$this->register("lycore", new TimingsCommand("timings"));
		$this->register("lycore", new ReloadCommand("reload"));
		$this->register("lycore", new XpCommand("xp"));
		$this->register("lycore", new SetBlockCommand("setblock"));
		$this->register("lycore", new OplistCommand("oplist"));
		$this->register("lycore", new ClearbagCommand("clearbag"));
		$this->register("lycore", new EmojiCommand("emoji"));
		$this->register("lycore", new PingCommand("ping"));
		$this->register("lycore", new SprintCommand("sprint"));
		$this->register("lycore", new SneakCommand("sneak"));
		$this->register("lycore", new NoticeCommand("notice"));

		if($this->server->getProperty("debug.commands", false)){
			$this->register("lycore", new StatusCommand("status"));
			$this->register("lycore", new GarbageCollectorCommand("gc"));
			$this->register("lycore", new DumpMemoryCommand("dumpmemory"));
		}
	}


	public function registerAll($fallbackPrefix, array $commands){
		foreach($commands as $command){
			$this->register($fallbackPrefix, $command);
		}
	}

	public function register($fallbackPrefix, Command $command, $label = null){
		if($label === null){
			$label = $command->getName();
		}
		$label = strtolower(trim($label));
		$fallbackPrefix = strtolower(trim($fallbackPrefix));

		$registered = $this->registerAlias($command, false, $fallbackPrefix, $label);

		$aliases = $command->getAliases();
		foreach($aliases as $index => $alias){
			if(!$this->registerAlias($command, true, $fallbackPrefix, $alias)){
				unset($aliases[$index]);
			}
		}
		$command->setAliases($aliases);

		if(!$registered){
			$command->setLabel($fallbackPrefix . ":" . $label);
		}

		$command->register($this);

		return $registered;
	}

	private function registerAlias(Command $command, $isAlias, $fallbackPrefix, $label){
		$this->knownCommands[$fallbackPrefix . ":" . $label] = $command;
		if(($command instanceof VanillaCommand or $isAlias) and isset($this->knownCommands[$label])){
			return false;
		}

		if(isset($this->knownCommands[$label]) and $this->knownCommands[$label]->getLabel() !== null and $this->knownCommands[$label]->getLabel() === $label){
			return false;
		}

		if(!$isAlias){
			$command->setLabel($label);
		}

		$this->knownCommands[$label] = $command;

		return true;
	}

	private function dispatchAdvanced(CommandSender $sender, Command $command, $label, array $args, $offset = 0){
		if(isset($args[$offset])){
			$argsTemp = $args;
			if($this->targetSelector->isSelector($args[$offset])){
				try{
					$targets = $this->targetSelector->matchEntities($sender, $args[$offset]);
				}catch(\InvalidArgumentException $e){
					$sender->sendMessage(TextFormat::RED . $e->getMessage());
					return;
				}
				if(count($targets) <= 0){
					$sender->sendMessage(TextFormat::RED . "No targets matched selector " . $args[$offset]);
					return;
				}
				foreach($targets as $target){
					$argsTemp[$offset] = $this->commandAcceptsSelectorEntities($command) ? $target : $this->getSelectorTargetArgument($target);
					$this->dispatchAdvanced($sender, $command, $label, $argsTemp, $offset + 1);
				}
			}else{
				$this->dispatchAdvanced($sender, $command, $label, $argsTemp, $offset + 1);
			}
		}else $command->execute($sender, $label, $args);
	}

	private function commandAcceptsSelectorEntities(Command $command){
		return method_exists($command, "acceptsEntitySelectorTargets") && $command->acceptsEntitySelectorTargets();
	}

	private function getSelectorTargetArgument($target){
		if(is_object($target) && method_exists($target, "getName")){
			return $target->getName();
		}

		$name = "entity";
		if(is_object($target) && method_exists($target, "getSaveId")){
			$saveId = $target->getSaveId();
			if($saveId !== null && $saveId !== ""){
				$name = strtolower((string) $saveId);
			}
		}elseif(is_object($target)){
			$class = get_class($target);
			$pos = strrpos($class, "\\");
			$name = strtolower($pos === false ? $class : substr($class, $pos + 1));
		}

		if(is_object($target) && method_exists($target, "getId")){
			return $name . "#" . $target->getId();
		}

		return $name;
	}

	private function dispatchAdvancedLegacy(CommandSender $sender, Command $command, $label, array $args, $offset = 0){
		if(isset($args[$offset])){
			$argsTemp = $args;
			switch($args[$offset]){
				case "@a":
					$p = $this->server->getOnlinePlayers();
					if(count($p) <= 0){
						$sender->sendMessage(TextFormat::RED . "没有玩家在线"); //TODO: add language
					}else{
						foreach($p as $player){
							$argsTemp[$offset] = $player->getName();
							$this->dispatchAdvanced($sender, $command, $label, $argsTemp, $offset + 1);
						}
					}
					break;
				case "@r":
					$players = $this->server->getOnlinePlayers();
					if(count($players) > 0){
						$argsTemp[$offset] = $players[array_rand($players)]->getName();
						$this->dispatchAdvanced($sender, $command, $label, $argsTemp, $offset + 1);
					}
					break;
				case "@s":
					if($sender instanceof Player){
						$argsTemp[$offset] = $sender->getName();
						$this->dispatchAdvanced($sender, $command, $label, $argsTemp, $offset + 1);
					}else $sender->sendMessage(TextFormat::RED . "你必须是玩家！");
					break;
				case "@p":
					if($sender instanceof Player){
						$distance = 5;
						$nearestPlayer = $sender;
						foreach($sender->getLevel()->getPlayers() as $p){
							if($p != $sender and (($dis = $p->distance($sender)) < $distance)){
								$distance = $dis;
								$nearestPlayer = $p;
							}
						}
						if($distance != 5){
							$argsTemp[$offset] = $nearestPlayer->getName();
							$this->dispatchAdvanced($sender, $command, $label, $argsTemp, $offset + 1);
						}else $sender->sendMessage(TextFormat::RED . "没有玩家在你附近");
					}else $sender->sendMessage(TextFormat::RED . "你必须是玩家！"); //TODO: add language
					break;
				default:
					$this->dispatchAdvanced($sender, $command, $label, $argsTemp, $offset + 1);
			}
		}else $command->execute($sender, $label, $args);
	}

	public function dispatch(CommandSender $sender, $commandLine){
		$args = explode(" ", $commandLine);

		if(count($args) === 0){
			return false;
		}

		$sentCommandLabel = strtolower(array_shift($args));
		$target = $this->getCommand($sentCommandLabel);

		if($target === null){
			return false;
		}

		$target->timings->startTiming();
		try{
			if($this->server->advancedCommandSelector){
				$this->dispatchAdvanced($sender, $target, $sentCommandLabel, $args);
			}else{
				$target->execute($sender, $sentCommandLabel, $args);
			}
		}catch(\Throwable $e){
			$sender->sendMessage(new TranslationContainer(TextFormat::RED . "%commands.generic.exception"));
			$this->server->getLogger()->critical($this->server->getLanguage()->translateString("pocketmine.command.exception", [$commandLine, (string) $target, $e->getMessage()]));
			$logger = $sender->getServer()->getLogger();
			if($logger instanceof MainLogger){
				$logger->logException($e);
			}
		}
		$target->timings->stopTiming();

		return true;
	}

	public function clearCommands(){
		foreach($this->knownCommands as $command){
			$command->unregister($this);
		}
		$this->knownCommands = [];
		$this->setDefaultCommands();
	}

	public function getCommand($name){
		if(isset($this->knownCommands[$name])){
			return $this->knownCommands[$name];
		}

		return null;
	}

	/**
	 * @return Command[]
	 */
	public function getCommands(){
		return $this->knownCommands;
	}


	/**
	 * @return void
	 */
	public function registerServerAliases(){
		$values = $this->server->getCommandAliases();

		foreach($values as $alias => $commandStrings){
			if(strpos($alias, ":") !== false or strpos($alias, " ") !== false){
				$this->server->getLogger()->warning($this->server->getLanguage()->translateString("pocketmine.command.alias.illegal", [$alias]));
				continue;
			}

			$targets = [];

			$bad = "";
			foreach($commandStrings as $commandString){
				$args = explode(" ", $commandString);
				$command = $this->getCommand($args[0]);

				if($command === null){
					if(strlen($bad) > 0){
						$bad .= ", ";
					}
					$bad .= $commandString;
				}else{
					$targets[] = $commandString;
				}
			}

			if(strlen($bad) > 0){
				$this->server->getLogger()->warning($this->server->getLanguage()->translateString("pocketmine.command.alias.notFound", [$alias, $bad]));
				continue;
			}

			//These registered commands have absolute priority
			if(count($targets) > 0){
				$this->knownCommands[strtolower($alias)] = new FormattedCommandAlias(strtolower($alias), $targets);
			}else{
				unset($this->knownCommands[strtolower($alias)]);
			}

		}
	}


}
