package net.minecraft.command;

import java.util.List;
import java.util.Random;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;

public class CommandWeather extends CommandBase {

    public CommandWeather() {}

    @Override
    public String func_71517_b() {
        return "weather";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.weather.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length >= 1 && astring.length <= 2) {
            int i = (300 + (new Random()).nextInt(600)) * 20;

            if (astring.length >= 2) {
                i = func_71532_a(icommandsender, astring[1], 1, 1000000) * 20;
            }

            WorldServer worldserver = MinecraftServer.func_71276_C().field_71305_c[0];
            WorldInfo worldinfo = worldserver.func_72912_H();

            if ("clear".equalsIgnoreCase(astring[0])) {
                worldinfo.func_76080_g(0);
                worldinfo.func_76090_f(0);
                worldinfo.func_76084_b(false);
                worldinfo.func_76069_a(false);
                func_152373_a(icommandsender, this, "commands.weather.clear", new Object[0]);
            } else if ("rain".equalsIgnoreCase(astring[0])) {
                worldinfo.func_76080_g(i);
                worldinfo.func_76084_b(true);
                worldinfo.func_76069_a(false);
                func_152373_a(icommandsender, this, "commands.weather.rain", new Object[0]);
            } else {
                if (!"thunder".equalsIgnoreCase(astring[0])) {
                    throw new WrongUsageException("commands.weather.usage", new Object[0]);
                }

                worldinfo.func_76080_g(i);
                worldinfo.func_76090_f(i);
                worldinfo.func_76084_b(true);
                worldinfo.func_76069_a(true);
                func_152373_a(icommandsender, this, "commands.weather.thunder", new Object[0]);
            }

        } else {
            throw new WrongUsageException("commands.weather.usage", new Object[0]);
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 1 ? func_71530_a(astring, new String[]{"clear", "rain", "thunder"}) : null;
    }
}
