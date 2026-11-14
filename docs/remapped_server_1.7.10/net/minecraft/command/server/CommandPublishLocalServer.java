package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSettings$GameType;

public class CommandPublishLocalServer extends CommandBase {

    public CommandPublishLocalServer() {}

    @Override
    public String func_71517_b() {
        return "publish";
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.publish.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        String s = MinecraftServer.func_71276_C().func_71206_a(WorldSettings$GameType.SURVIVAL, false);

        if (s != null) {
            func_152373_a(icommandsender, this, "commands.publish.started", new Object[]{s});
        } else {
            func_152373_a(icommandsender, this, "commands.publish.failed", new Object[0]);
        }

    }
}
