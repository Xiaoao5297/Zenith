package net.minecraft.command.server;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandMessage extends CommandBase {

    public CommandMessage() {}

    @Override
    public List func_71514_a() {
        return Arrays.asList("w", "msg");
    }

    @Override
    public String func_71517_b() {
        return "tell";
    }

    @Override
    public int func_82362_a() {
        return 0;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.message.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length < 2) {
            throw new WrongUsageException("commands.message.usage", new Object[0]);
        } else {
            EntityPlayerMP entityplayermp = func_82359_c(icommandsender, astring[0]);

            if (entityplayermp == null) {
                throw new PlayerNotFoundException();
            } else if (entityplayermp == icommandsender) {
                throw new PlayerNotFoundException("commands.message.sameTarget", new Object[0]);
            } else {
                IChatComponent ichatcomponent = func_147176_a(icommandsender, astring, 1, !(icommandsender instanceof EntityPlayer));
                ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.message.display.incoming", new Object[]{icommandsender.func_145748_c_(), ichatcomponent.func_150259_f()});
                ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation("commands.message.display.outgoing", new Object[]{entityplayermp.func_145748_c_(), ichatcomponent.func_150259_f()});

                chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.GRAY).func_150217_b(true);
                chatcomponenttranslation1.func_150256_b().func_150238_a(EnumChatFormatting.GRAY).func_150217_b(true);
                entityplayermp.func_145747_a(chatcomponenttranslation);
                icommandsender.func_145747_a(chatcomponenttranslation1);
            }
        }
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return func_71530_a(astring, MinecraftServer.func_71276_C().func_71213_z());
    }

    @Override
    public boolean func_82358_a(String[] astring, int i) {
        return i == 0;
    }
}
