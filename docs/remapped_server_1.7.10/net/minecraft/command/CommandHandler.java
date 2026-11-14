package net.minecraft.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandHandler implements ICommandManager {

    private static final Logger field_147175_a = LogManager.getLogger();
    private final Map field_71562_a = new HashMap();
    private final Set field_71561_b = new HashSet();

    public CommandHandler() {}

    @Override
    public int func_71556_a(ICommandSender icommandsender, String s) {
        s = s.trim();
        if (s.startsWith("/")) {
            s = s.substring(1);
        }

        String[] astring = s.split(" ");
        String s1 = astring[0];

        astring = func_71559_a(astring);
        ICommand icommand = (ICommand) this.field_71562_a.get(s1);
        int i = this.func_82370_a(icommand, astring);
        int j = 0;

        ChatComponentTranslation chatcomponenttranslation;

        try {
            if (icommand == null) {
                throw new CommandNotFoundException();
            }

            if (icommand.func_71519_b(icommandsender)) {
                if (i > -1) {
                    EntityPlayerMP[] aentityplayermp = PlayerSelector.func_82380_c(icommandsender, astring[i]);
                    String s2 = astring[i];
                    EntityPlayerMP[] aentityplayermp1 = aentityplayermp;
                    int k = aentityplayermp.length;

                    for (int l = 0; l < k; ++l) {
                        EntityPlayerMP entityplayermp = aentityplayermp1[l];

                        astring[i] = entityplayermp.func_70005_c_();

                        try {
                            icommand.func_71515_b(icommandsender, astring);
                            ++j;
                        } catch (CommandException commandexception) {
                            ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation(commandexception.getMessage(), commandexception.func_74844_a());

                            chatcomponenttranslation1.func_150256_b().func_150238_a(EnumChatFormatting.RED);
                            icommandsender.func_145747_a(chatcomponenttranslation1);
                        }
                    }

                    astring[i] = s2;
                } else {
                    try {
                        icommand.func_71515_b(icommandsender, astring);
                        ++j;
                    } catch (CommandException commandexception1) {
                        chatcomponenttranslation = new ChatComponentTranslation(commandexception1.getMessage(), commandexception1.func_74844_a());
                        chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.RED);
                        icommandsender.func_145747_a(chatcomponenttranslation);
                    }
                }
            } else {
                ChatComponentTranslation chatcomponenttranslation2 = new ChatComponentTranslation("commands.generic.permission", new Object[0]);

                chatcomponenttranslation2.func_150256_b().func_150238_a(EnumChatFormatting.RED);
                icommandsender.func_145747_a(chatcomponenttranslation2);
            }
        } catch (WrongUsageException wrongusageexception) {
            chatcomponenttranslation = new ChatComponentTranslation("commands.generic.usage", new Object[]{new ChatComponentTranslation(wrongusageexception.getMessage(), wrongusageexception.func_74844_a())});
            chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.RED);
            icommandsender.func_145747_a(chatcomponenttranslation);
        } catch (CommandException commandexception2) {
            chatcomponenttranslation = new ChatComponentTranslation(commandexception2.getMessage(), commandexception2.func_74844_a());
            chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.RED);
            icommandsender.func_145747_a(chatcomponenttranslation);
        } catch (Throwable throwable) {
            chatcomponenttranslation = new ChatComponentTranslation("commands.generic.exception", new Object[0]);
            chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.RED);
            icommandsender.func_145747_a(chatcomponenttranslation);
            CommandHandler.field_147175_a.error("Couldn't process command: '" + s + "'", throwable);
        }

        return j;
    }

    public ICommand func_71560_a(ICommand icommand) {
        List list = icommand.func_71514_a();

        this.field_71562_a.put(icommand.func_71517_b(), icommand);
        this.field_71561_b.add(icommand);
        if (list != null) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                ICommand icommand1 = (ICommand) this.field_71562_a.get(s);

                if (icommand1 == null || !icommand1.func_71517_b().equals(s)) {
                    this.field_71562_a.put(s, icommand);
                }
            }
        }

        return icommand;
    }

    private static String[] func_71559_a(String[] astring) {
        String[] astring1 = new String[astring.length - 1];

        for (int i = 1; i < astring.length; ++i) {
            astring1[i - 1] = astring[i];
        }

        return astring1;
    }

    @Override
    public List func_71558_b(ICommandSender icommandsender, String s) {
        String[] astring = s.split(" ", -1);
        String s1 = astring[0];

        if (astring.length == 1) {
            ArrayList arraylist = new ArrayList();
            Iterator iterator = this.field_71562_a.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                if (CommandBase.func_71523_a(s1, (String) entry.getKey()) && ((ICommand) entry.getValue()).func_71519_b(icommandsender)) {
                    arraylist.add(entry.getKey());
                }
            }

            return arraylist;
        } else {
            if (astring.length > 1) {
                ICommand icommand = (ICommand) this.field_71562_a.get(s1);

                if (icommand != null) {
                    return icommand.func_71516_a(icommandsender, func_71559_a(astring));
                }
            }

            return null;
        }
    }

    @Override
    public List func_71557_a(ICommandSender icommandsender) {
        ArrayList arraylist = new ArrayList();
        Iterator iterator = this.field_71561_b.iterator();

        while (iterator.hasNext()) {
            ICommand icommand = (ICommand) iterator.next();

            if (icommand.func_71519_b(icommandsender)) {
                arraylist.add(icommand);
            }
        }

        return arraylist;
    }

    @Override
    public Map func_71555_a() {
        return this.field_71562_a;
    }

    private int func_82370_a(ICommand icommand, String[] astring) {
        if (icommand == null) {
            return -1;
        } else {
            for (int i = 0; i < astring.length; ++i) {
                if (icommand.func_82358_a(astring, i) && PlayerSelector.func_82377_a(astring[i])) {
                    return i;
                }
            }

            return -1;
        }
    }
}
