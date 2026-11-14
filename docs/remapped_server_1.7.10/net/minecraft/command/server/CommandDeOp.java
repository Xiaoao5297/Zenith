package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandDeOp extends CommandBase {

    public CommandDeOp() {}

    @Override
    public String func_71517_b() {
        return "deop";
    }

    @Override
    public int func_82362_a() {
        return 3;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.deop.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length == 1 && astring[0].length() > 0) {
            MinecraftServer minecraftserver = MinecraftServer.func_71276_C();
            GameProfile gameprofile = minecraftserver.func_71203_ab().func_152603_m().func_152700_a(astring[0]);

            if (gameprofile == null) {
                throw new CommandException("commands.deop.failed", new Object[]{astring[0]});
            } else {
                minecraftserver.func_71203_ab().func_152610_b(gameprofile);
                func_152373_a(icommandsender, this, "commands.deop.success", new Object[]{astring[0]});
            }
        } else {
            throw new WrongUsageException("commands.deop.usage", new Object[0]);
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 1 ? func_71530_a(astring, MinecraftServer.func_71276_C().func_71203_ab().func_152606_n()) : null;
    }
}
