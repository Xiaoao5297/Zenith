package net.minecraft.command.server;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.IPBanEntry;
import net.minecraft.util.IChatComponent;

public class CommandBanIp extends CommandBase {

    public static final Pattern field_147211_a = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public CommandBanIp() {}

    @Override
    public String func_71517_b() {
        return "ban-ip";
    }

    @Override
    public int func_82362_a() {
        return 3;
    }

    @Override
    public boolean func_71519_b(ICommandSender icommandsender) {
        return MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152689_b() && super.func_71519_b(icommandsender);
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.banip.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length >= 1 && astring[0].length() > 1) {
            Matcher matcher = CommandBanIp.field_147211_a.matcher(astring[0]);
            IChatComponent ichatcomponent = null;

            if (astring.length >= 2) {
                ichatcomponent = func_147178_a(icommandsender, astring, 1);
            }

            if (matcher.matches()) {
                this.func_147210_a(icommandsender, astring[0], ichatcomponent == null ? null : ichatcomponent.func_150260_c());
            } else {
                EntityPlayerMP entityplayermp = MinecraftServer.func_71276_C().func_71203_ab().func_152612_a(astring[0]);

                if (entityplayermp == null) {
                    throw new PlayerNotFoundException("commands.banip.invalid", new Object[0]);
                }

                this.func_147210_a(icommandsender, entityplayermp.func_71114_r(), ichatcomponent == null ? null : ichatcomponent.func_150260_c());
            }

        } else {
            throw new WrongUsageException("commands.banip.usage", new Object[0]);
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 1 ? func_71530_a(astring, MinecraftServer.func_71276_C().func_71213_z()) : null;
    }

    protected void func_147210_a(ICommandSender icommandsender, String s, String s1) {
        IPBanEntry ipbanentry = new IPBanEntry(s, (Date) null, icommandsender.func_70005_c_(), (Date) null, s1);

        MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152687_a(ipbanentry);
        List list = MinecraftServer.func_71276_C().func_71203_ab().func_72382_j(s);
        String[] astring = new String[list.size()];
        int i = 0;

        EntityPlayerMP entityplayermp;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); astring[i++] = entityplayermp.func_70005_c_()) {
            entityplayermp = (EntityPlayerMP) iterator.next();
            entityplayermp.field_71135_a.func_147360_c("You have been IP banned.");
        }

        if (list.isEmpty()) {
            func_152373_a(icommandsender, this, "commands.banip.success", new Object[]{s});
        } else {
            func_152373_a(icommandsender, this, "commands.banip.success.players", new Object[]{s, func_71527_a(astring)});
        }

    }
}
