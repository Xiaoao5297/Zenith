package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

public class CommandSaveAll extends CommandBase {

    public CommandSaveAll() {}

    @Override
    public String func_71517_b() {
        return "save-all";
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.save.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        MinecraftServer minecraftserver = MinecraftServer.func_71276_C();

        icommandsender.func_145747_a(new ChatComponentTranslation("commands.save.start", new Object[0]));
        if (minecraftserver.func_71203_ab() != null) {
            minecraftserver.func_71203_ab().func_72389_g();
        }

        try {
            WorldServer worldserver;
            int i;
            boolean flag;

            for (i = 0; i < minecraftserver.field_71305_c.length; ++i) {
                if (minecraftserver.field_71305_c[i] != null) {
                    worldserver = minecraftserver.field_71305_c[i];
                    flag = worldserver.field_73058_d;
                    worldserver.field_73058_d = false;
                    worldserver.func_73044_a(true, (IProgressUpdate) null);
                    worldserver.field_73058_d = flag;
                }
            }

            if (astring.length > 0 && "flush".equals(astring[0])) {
                icommandsender.func_145747_a(new ChatComponentTranslation("commands.save.flushStart", new Object[0]));

                for (i = 0; i < minecraftserver.field_71305_c.length; ++i) {
                    if (minecraftserver.field_71305_c[i] != null) {
                        worldserver = minecraftserver.field_71305_c[i];
                        flag = worldserver.field_73058_d;
                        worldserver.field_73058_d = false;
                        worldserver.func_104140_m();
                        worldserver.field_73058_d = flag;
                    }
                }

                icommandsender.func_145747_a(new ChatComponentTranslation("commands.save.flushEnd", new Object[0]));
            }
        } catch (MinecraftException minecraftexception) {
            func_152373_a(icommandsender, this, "commands.save.failed", new Object[]{minecraftexception.getMessage()});
            return;
        }

        func_152373_a(icommandsender, this, "commands.save.success", new Object[0]);
    }
}
