package net.minecraft.command;

import net.minecraft.server.MinecraftServer;

public class CommandSetPlayerTimeout extends CommandBase {

    public CommandSetPlayerTimeout() {}

    @Override
    public String func_71517_b() {
        return "setidletimeout";
    }

    @Override
    public int func_82362_a() {
        return 3;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.setidletimeout.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length == 1) {
            int i = func_71528_a(icommandsender, astring[0], 0);

            MinecraftServer.func_71276_C().func_143006_e(i);
            func_152373_a(icommandsender, this, "commands.setidletimeout.success", new Object[]{i});
        } else {
            throw new WrongUsageException("commands.setidletimeout.usage", new Object[0]);
        }
    }
}
