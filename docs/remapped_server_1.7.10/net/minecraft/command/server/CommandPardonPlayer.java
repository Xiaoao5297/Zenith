package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandPardonPlayer extends CommandBase {

    public CommandPardonPlayer() {}

    @Override
    public String func_71517_b() {
        return "pardon";
    }

    @Override
    public int func_82362_a() {
        return 3;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.unban.usage";
    }

    @Override
    public boolean func_71519_b(ICommandSender icommandsender) {
        return MinecraftServer.func_71276_C().func_71203_ab().func_152608_h().func_152689_b() && super.func_71519_b(icommandsender);
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length == 1 && astring[0].length() > 0) {
            MinecraftServer minecraftserver = MinecraftServer.func_71276_C();
            GameProfile gameprofile = minecraftserver.func_71203_ab().func_152608_h().func_152703_a(astring[0]);

            if (gameprofile == null) {
                throw new CommandException("commands.unban.failed", new Object[]{astring[0]});
            } else {
                minecraftserver.func_71203_ab().func_152608_h().func_152684_c(gameprofile);
                func_152373_a(icommandsender, this, "commands.unban.success", new Object[]{astring[0]});
            }
        } else {
            throw new WrongUsageException("commands.unban.usage", new Object[0]);
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 1 ? func_71530_a(astring, MinecraftServer.func_71276_C().func_71203_ab().func_152608_h().func_152685_a()) : null;
    }
}
