package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandListBans extends CommandBase {

    public CommandListBans() {}

    @Override
    public String func_71517_b() {
        return "banlist";
    }

    @Override
    public int func_82362_a() {
        return 3;
    }

    @Override
    public boolean func_71519_b(ICommandSender icommandsender) {
        return (MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152689_b() || MinecraftServer.func_71276_C().func_71203_ab().func_152608_h().func_152689_b()) && super.func_71519_b(icommandsender);
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.banlist.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length >= 1 && astring[0].equalsIgnoreCase("ips")) {
            icommandsender.func_145747_a(new ChatComponentTranslation("commands.banlist.ips", new Object[]{MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152685_a().length}));
            icommandsender.func_145747_a(new ChatComponentText(func_71527_a(MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152685_a())));
        } else {
            icommandsender.func_145747_a(new ChatComponentTranslation("commands.banlist.players", new Object[]{MinecraftServer.func_71276_C().func_71203_ab().func_152608_h().func_152685_a().length}));
            icommandsender.func_145747_a(new ChatComponentText(func_71527_a(MinecraftServer.func_71276_C().func_71203_ab().func_152608_h().func_152685_a())));
        }

    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length == 1 ? func_71530_a(astring, new String[]{"players", "ips"}) : null;
    }
}
