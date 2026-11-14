package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandTime extends CommandBase {

    public CommandTime() {}

    @Override
    public String func_71517_b() {
        return "time";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.time.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length > 1) {
            int i;

            if (astring[0].equals("set")) {
                if (astring[1].equals("day")) {
                    i = 1000;
                } else if (astring[1].equals("night")) {
                    i = 13000;
                } else {
                    i = func_71528_a(icommandsender, astring[1], 0);
                }

                this.func_71552_a(icommandsender, i);
                func_152373_a(icommandsender, this, "commands.time.set", new Object[]{i});
                return;
            }

            if (astring[0].equals("add")) {
                i = func_71528_a(icommandsender, astring[1], 0);
                this.func_71553_b(icommandsender, i);
                func_152373_a(icommandsender, this, "commands.time.added", new Object[]{i});
                return;
            }
        }

        throw new WrongUsageException("commands.time.usage", new Object[0]);
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 1 ? func_71530_a(astring, new String[]{"set", "add"}) : (astring.length == 2 && astring[0].equals("set") ? func_71530_a(astring, new String[]{"day", "night"}) : null);
    }

    protected void func_71552_a(ICommandSender icommandsender, int i) {
        for (int j = 0; j < MinecraftServer.func_71276_C().field_71305_c.length; ++j) {
            MinecraftServer.func_71276_C().field_71305_c[j].func_72877_b((long) i);
        }

    }

    protected void func_71553_b(ICommandSender icommandsender, int i) {
        for (int j = 0; j < MinecraftServer.func_71276_C().field_71305_c.length; ++j) {
            WorldServer worldserver = MinecraftServer.func_71276_C().field_71305_c[j];

            worldserver.func_72877_b(worldserver.func_72820_D() + (long) i);
        }

    }
}
