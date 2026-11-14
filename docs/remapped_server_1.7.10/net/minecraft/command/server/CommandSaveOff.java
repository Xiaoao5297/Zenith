package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandSaveOff extends CommandBase {

    public CommandSaveOff() {}

    @Override
    public String func_71517_b() {
        return "save-off";
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.save-off.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        MinecraftServer minecraftserver = MinecraftServer.func_71276_C();
        boolean flag = false;

        for (int i = 0; i < minecraftserver.field_71305_c.length; ++i) {
            if (minecraftserver.field_71305_c[i] != null) {
                WorldServer worldserver = minecraftserver.field_71305_c[i];

                if (!worldserver.field_73058_d) {
                    worldserver.field_73058_d = true;
                    flag = true;
                }
            }
        }

        if (flag) {
            func_152373_a(icommandsender, this, "commands.save.disabled", new Object[0]);
        } else {
            throw new CommandException("commands.save-off.alreadyOff", new Object[0]);
        }
    }
}
