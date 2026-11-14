package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings$GameType;

public class CommandGameMode extends CommandBase {

    public CommandGameMode() {}

    @Override
    public String func_71517_b() {
        return "gamemode";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.gamemode.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length > 0) {
            WorldSettings$GameType worldsettings$gametype = this.func_71539_b(icommandsender, astring[0]);
            EntityPlayerMP entityplayermp = astring.length >= 2 ? func_82359_c(icommandsender, astring[1]) : func_71521_c(icommandsender);

            entityplayermp.func_71033_a(worldsettings$gametype);
            entityplayermp.field_70143_R = 0.0F;
            ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("gameMode." + worldsettings$gametype.func_77149_b(), new Object[0]);

            if (entityplayermp != icommandsender) {
                func_152374_a(icommandsender, this, 1, "commands.gamemode.success.other", new Object[]{entityplayermp.func_70005_c_(), chatcomponenttranslation});
            } else {
                func_152374_a(icommandsender, this, 1, "commands.gamemode.success.self", new Object[]{chatcomponenttranslation});
            }

        } else {
            throw new WrongUsageException("commands.gamemode.usage", new Object[0]);
        }
    }

    protected WorldSettings$GameType func_71539_b(ICommandSender icommandsender, String s) {
        return !s.equalsIgnoreCase(WorldSettings$GameType.SURVIVAL.func_77149_b()) && !s.equalsIgnoreCase("s") ? (!s.equalsIgnoreCase(WorldSettings$GameType.CREATIVE.func_77149_b()) && !s.equalsIgnoreCase("c") ? (!s.equalsIgnoreCase(WorldSettings$GameType.ADVENTURE.func_77149_b()) && !s.equalsIgnoreCase("a") ? WorldSettings.func_77161_a(func_71532_a(icommandsender, s, 0, WorldSettings$GameType.values().length - 2)) : WorldSettings$GameType.ADVENTURE) : WorldSettings$GameType.CREATIVE) : WorldSettings$GameType.SURVIVAL;
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 1 ? func_71530_a(astring, new String[]{"survival", "creative", "adventure"}) : (astring.length == 2 ? func_71530_a(astring, this.func_71538_c()) : null);
    }

    protected String[] func_71538_c() {
        return MinecraftServer.func_71276_C().func_71213_z();
    }

    @Override
    public boolean func_82358_a(String[] astring, int i) {
        return i == 1;
    }
}
