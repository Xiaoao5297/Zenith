package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandXP extends CommandBase {

    public CommandXP() {}

    @Override
    public String func_71517_b() {
        return "xp";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.xp.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length <= 0) {
            throw new WrongUsageException("commands.xp.usage", new Object[0]);
        } else {
            String s = astring[0];
            boolean flag = s.endsWith("l") || s.endsWith("L");

            if (flag && s.length() > 1) {
                s = s.substring(0, s.length() - 1);
            }

            int i = func_71526_a(icommandsender, s);
            boolean flag1 = i < 0;

            if (flag1) {
                i *= -1;
            }

            EntityPlayerMP entityplayermp;

            if (astring.length > 1) {
                entityplayermp = func_82359_c(icommandsender, astring[1]);
            } else {
                entityplayermp = func_71521_c(icommandsender);
            }

            if (flag) {
                if (flag1) {
                    entityplayermp.func_82242_a(-i);
                    func_152373_a(icommandsender, this, "commands.xp.success.negative.levels", new Object[]{i, entityplayermp.func_70005_c_()});
                } else {
                    entityplayermp.func_82242_a(i);
                    func_152373_a(icommandsender, this, "commands.xp.success.levels", new Object[]{i, entityplayermp.func_70005_c_()});
                }
            } else {
                if (flag1) {
                    throw new WrongUsageException("commands.xp.failure.widthdrawXp", new Object[0]);
                }

                entityplayermp.func_71023_q(i);
                func_152373_a(icommandsender, this, "commands.xp.success", new Object[]{i, entityplayermp.func_70005_c_()});
            }

        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 2 ? func_71530_a(astring, this.func_71542_c()) : null;
    }

    protected String[] func_71542_c() {
        return MinecraftServer.func_71276_C().func_71213_z();
    }

    @Override
    public boolean func_82358_a(String[] astring, int i) {
        return i == 1;
    }
}
