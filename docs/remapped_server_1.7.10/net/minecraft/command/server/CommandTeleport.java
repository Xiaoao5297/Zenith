package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandTeleport extends CommandBase {

    public CommandTeleport() {}

    @Override
    public String func_71517_b() {
        return "tp";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.tp.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length < 1) {
            throw new WrongUsageException("commands.tp.usage", new Object[0]);
        } else {
            EntityPlayerMP entityplayermp;

            if (astring.length != 2 && astring.length != 4) {
                entityplayermp = func_71521_c(icommandsender);
            } else {
                entityplayermp = func_82359_c(icommandsender, astring[0]);
                if (entityplayermp == null) {
                    throw new PlayerNotFoundException();
                }
            }

            if (astring.length != 3 && astring.length != 4) {
                if (astring.length == 1 || astring.length == 2) {
                    EntityPlayerMP entityplayermp1 = func_82359_c(icommandsender, astring[astring.length - 1]);

                    if (entityplayermp1 == null) {
                        throw new PlayerNotFoundException();
                    }

                    if (entityplayermp1.field_70170_p != entityplayermp.field_70170_p) {
                        func_152373_a(icommandsender, this, "commands.tp.notSameDimension", new Object[0]);
                        return;
                    }

                    entityplayermp.func_70078_a((Entity) null);
                    entityplayermp.field_71135_a.func_147364_a(entityplayermp1.field_70165_t, entityplayermp1.field_70163_u, entityplayermp1.field_70161_v, entityplayermp1.field_70177_z, entityplayermp1.field_70125_A);
                    func_152373_a(icommandsender, this, "commands.tp.success", new Object[]{entityplayermp.func_70005_c_(), entityplayermp1.func_70005_c_()});
                }
            } else if (entityplayermp.field_70170_p != null) {
                int i = astring.length - 3;
                double d0 = func_110666_a(icommandsender, entityplayermp.field_70165_t, astring[i++]);
                double d1 = func_110665_a(icommandsender, entityplayermp.field_70163_u, astring[i++], 0, 0);
                double d2 = func_110666_a(icommandsender, entityplayermp.field_70161_v, astring[i++]);

                entityplayermp.func_70078_a((Entity) null);
                entityplayermp.func_70634_a(d0, d1, d2);
                func_152373_a(icommandsender, this, "commands.tp.success.coordinates", new Object[]{entityplayermp.func_70005_c_(), d0, d1, d2});
            }

        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length != 1 && astring.length != 2 ? null : func_71530_a(astring, MinecraftServer.func_71276_C().func_71213_z());
    }

    @Override
    public boolean func_82358_a(String[] astring, int i) {
        return i == 0;
    }
}
