package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandServerKick extends CommandBase {

    public CommandServerKick() {}

    @Override
    public String func_71517_b() {
        return "kick";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.kick.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length > 0 && astring[0].length() > 1) {
            EntityPlayerMP entityplayermp = MinecraftServer.func_71276_C().func_71203_ab().func_152612_a(astring[0]);
            String s = "Kicked by an operator.";
            boolean flag = false;

            if (entityplayermp == null) {
                throw new PlayerNotFoundException();
            } else {
                if (astring.length >= 2) {
                    s = func_147178_a(icommandsender, astring, 1).func_150260_c();
                    flag = true;
                }

                entityplayermp.field_71135_a.func_147360_c(s);
                if (flag) {
                    func_152373_a(icommandsender, this, "commands.kick.success.reason", new Object[]{entityplayermp.func_70005_c_(), s});
                } else {
                    func_152373_a(icommandsender, this, "commands.kick.success", new Object[]{entityplayermp.func_70005_c_()});
                }

            }
        } else {
            throw new WrongUsageException("commands.kick.usage", new Object[0]);
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length >= 1 ? func_71530_a(astring, MinecraftServer.func_71276_C().func_71213_z()) : null;
    }
}
