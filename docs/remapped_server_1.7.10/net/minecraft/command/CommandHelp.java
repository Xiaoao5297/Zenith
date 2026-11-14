package net.minecraft.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent$Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class CommandHelp extends CommandBase {

    public CommandHelp() {}

    @Override
    public String func_71517_b() {
        return "help";
    }

    @Override
    public int func_82362_a() {
        return 0;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.help.usage";
    }

    @Override
    public List func_71514_a() {
        return Arrays.asList("?");
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        List list = this.func_71534_d(icommandsender);
        byte b0 = 7;
        int i = (list.size() - 1) / b0;
        boolean flag = false;

        int j;

        try {
            j = astring.length == 0 ? 0 : func_71532_a(icommandsender, astring[0], 1, i + 1) - 1;
        } catch (NumberInvalidException numberinvalidexception) {
            Map map = this.func_71535_c();
            ICommand icommand = (ICommand) map.get(astring[0]);

            if (icommand != null) {
                throw new WrongUsageException(icommand.func_71518_a(icommandsender), new Object[0]);
            }

            if (MathHelper.func_82715_a(astring[0], -1) != -1) {
                throw numberinvalidexception;
            }

            throw new CommandNotFoundException();
        }

        int k = Math.min((j + 1) * b0, list.size());
        ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.help.header", new Object[]{j + 1, i + 1});

        chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
        icommandsender.func_145747_a(chatcomponenttranslation);

        for (int l = j * b0; l < k; ++l) {
            ICommand icommand1 = (ICommand) list.get(l);
            ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation(icommand1.func_71518_a(icommandsender), new Object[0]);

            chatcomponenttranslation1.func_150256_b().func_150241_a(new ClickEvent(ClickEvent$Action.SUGGEST_COMMAND, "/" + icommand1.func_71517_b() + " "));
            icommandsender.func_145747_a(chatcomponenttranslation1);
        }

        if (j == 0 && icommandsender instanceof EntityPlayer) {
            ChatComponentTranslation chatcomponenttranslation2 = new ChatComponentTranslation("commands.help.footer", new Object[0]);

            chatcomponenttranslation2.func_150256_b().func_150238_a(EnumChatFormatting.GREEN);
            icommandsender.func_145747_a(chatcomponenttranslation2);
        }

    }

    protected List func_71534_d(ICommandSender icommandsender) {
        List list = MinecraftServer.func_71276_C().func_71187_D().func_71557_a(icommandsender);

        Collections.sort(list);
        return list;
    }

    protected Map func_71535_c() {
        return MinecraftServer.func_71276_C().func_71187_D().func_71555_a();
    }
}
