package net.minecraft.command;

import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings$GameType;

public class CommandDefaultGameMode extends CommandGameMode {

    public CommandDefaultGameMode() {}

    @Override
    public String func_71517_b() {
        return "defaultgamemode";
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.defaultgamemode.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length > 0) {
            WorldSettings$GameType worldsettings$gametype = this.func_71539_b(icommandsender, astring[0]);

            this.func_71541_a(worldsettings$gametype);
            func_152373_a(icommandsender, this, "commands.defaultgamemode.success", new Object[]{new ChatComponentTranslation("gameMode." + worldsettings$gametype.func_77149_b(), new Object[0])});
        } else {
            throw new WrongUsageException("commands.defaultgamemode.usage", new Object[0]);
        }
    }

    protected void func_71541_a(WorldSettings$GameType worldsettings$gametype) {
        MinecraftServer minecraftserver = MinecraftServer.func_71276_C();

        minecraftserver.func_71235_a(worldsettings$gametype);
        EntityPlayerMP entityplayermp;

        if (minecraftserver.func_104056_am()) {
            for (Iterator iterator = MinecraftServer.func_71276_C().func_71203_ab().field_72404_b.iterator(); iterator.hasNext(); entityplayermp.field_70143_R = 0.0F) {
                entityplayermp = (EntityPlayerMP) iterator.next();
                entityplayermp.func_71033_a(worldsettings$gametype);
            }
        }

    }
}
