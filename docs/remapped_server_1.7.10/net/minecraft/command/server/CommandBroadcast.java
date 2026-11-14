package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class CommandBroadcast extends CommandBase {

    public CommandBroadcast() {}

    @Override
    public String func_71517_b() {
        return "say";
    }

    @Override
    public int func_82362_a() {
        return 1;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.say.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length > 0 && astring[0].length() > 0) {
            IChatComponent ichatcomponent = func_147176_a(icommandsender, astring, 0, true);

            MinecraftServer.func_71276_C().func_71203_ab().func_148539_a(new ChatComponentTranslation("chat.type.announcement", new Object[]{icommandsender.func_70005_c_(), ichatcomponent}));
        } else {
            throw new WrongUsageException("commands.say.usage", new Object[0]);
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return astring.length >= 1 ? func_71530_a(astring, MinecraftServer.func_71276_C().func_71213_z()) : null;
    }
}
