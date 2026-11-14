package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class CommandEmote extends CommandBase {

    public CommandEmote() {}

    @Override
    public String func_71517_b() {
        return "me";
    }

    @Override
    public int func_82362_a() {
        return 0;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.me.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length > 0) {
            IChatComponent ichatcomponent = func_147176_a(icommandsender, astring, 0, icommandsender.func_70003_b(1, "me"));

            MinecraftServer.func_71276_C().func_71203_ab().func_148539_a(new ChatComponentTranslation("chat.type.emote", new Object[]{icommandsender.func_145748_c_(), ichatcomponent}));
        } else {
            throw new WrongUsageException("commands.me.usage", new Object[0]);
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return func_71530_a(astring, MinecraftServer.func_71276_C().func_71213_z());
    }
}
