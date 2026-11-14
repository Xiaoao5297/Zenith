package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandOp extends CommandBase {

    public CommandOp() {}

    @Override
    public String func_71517_b() {
        return "op";
    }

    @Override
    public int func_82362_a() {
        return 3;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.op.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length == 1 && astring[0].length() > 0) {
            MinecraftServer minecraftserver = MinecraftServer.func_71276_C();
            GameProfile gameprofile = minecraftserver.func_152358_ax().func_152655_a(astring[0]);

            if (gameprofile == null) {
                throw new CommandException("commands.op.failed", new Object[]{astring[0]});
            } else {
                minecraftserver.func_71203_ab().func_152605_a(gameprofile);
                func_152373_a(icommandsender, this, "commands.op.success", new Object[]{astring[0]});
            }
        } else {
            throw new WrongUsageException("commands.op.usage", new Object[0]);
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        if (astring.length == 1) {
            String s = astring[astring.length - 1];
            ArrayList arraylist = new ArrayList();
            GameProfile[] agameprofile = MinecraftServer.func_71276_C().func_152357_F();
            int i = agameprofile.length;

            for (int j = 0; j < i; ++j) {
                GameProfile gameprofile = agameprofile[j];

                if (!MinecraftServer.func_71276_C().func_71203_ab().func_152596_g(gameprofile) && func_71523_a(s, gameprofile.getName())) {
                    arraylist.add(gameprofile.getName());
                }
            }

            return arraylist;
        } else {
            return null;
        }
    }
}
