package net.minecraft.command;

import com.google.common.primitives.Doubles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public abstract class CommandBase implements ICommand {

    private static IAdminCommand field_71533_a;

    public CommandBase() {}

    public int func_82362_a() {
        return 4;
    }

    @Override
    public List func_71514_a() {
        return null;
    }

    @Override
    public boolean func_71519_b(ICommandSender icommandsender) {
        return icommandsender.func_70003_b(this.func_82362_a(), this.func_71517_b());
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        return null;
    }

    public static int func_71526_a(ICommandSender icommandsender, String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException numberformatexception) {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[]{s});
        }
    }

    public static int func_71528_a(ICommandSender icommandsender, String s, int i) {
        return func_71532_a(icommandsender, s, i, Integer.MAX_VALUE);
    }

    public static int func_71532_a(ICommandSender icommandsender, String s, int i, int j) {
        int k = func_71526_a(icommandsender, s);

        if (k < i) {
            throw new NumberInvalidException("commands.generic.num.tooSmall", new Object[]{k, i});
        } else if (k > j) {
            throw new NumberInvalidException("commands.generic.num.tooBig", new Object[]{k, j});
        } else {
            return k;
        }
    }

    public static double func_82363_b(ICommandSender icommandsender, String s) {
        try {
            double d0 = Double.parseDouble(s);

            if (!Doubles.isFinite(d0)) {
                throw new NumberInvalidException("commands.generic.num.invalid", new Object[]{s});
            } else {
                return d0;
            }
        } catch (NumberFormatException numberformatexception) {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[]{s});
        }
    }

    public static double func_110664_a(ICommandSender icommandsender, String s, double d0) {
        return func_110661_a(icommandsender, s, d0, Double.MAX_VALUE);
    }

    public static double func_110661_a(ICommandSender icommandsender, String s, double d0, double d1) {
        double d2 = func_82363_b(icommandsender, s);

        if (d2 < d0) {
            throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[]{d2, d0});
        } else if (d2 > d1) {
            throw new NumberInvalidException("commands.generic.double.tooBig", new Object[]{d2, d1});
        } else {
            return d2;
        }
    }

    public static boolean func_110662_c(ICommandSender icommandsender, String s) {
        if (!s.equals("true") && !s.equals("1")) {
            if (!s.equals("false") && !s.equals("0")) {
                throw new CommandException("commands.generic.boolean.invalid", new Object[]{s});
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static EntityPlayerMP func_71521_c(ICommandSender icommandsender) {
        if (icommandsender instanceof EntityPlayerMP) {
            return (EntityPlayerMP) icommandsender;
        } else {
            throw new PlayerNotFoundException("You must specify which player you wish to perform this action on.", new Object[0]);
        }
    }

    public static EntityPlayerMP func_82359_c(ICommandSender icommandsender, String s) {
        EntityPlayerMP entityplayermp = PlayerSelector.func_82386_a(icommandsender, s);

        if (entityplayermp != null) {
            return entityplayermp;
        } else {
            entityplayermp = MinecraftServer.func_71276_C().func_71203_ab().func_152612_a(s);
            if (entityplayermp == null) {
                throw new PlayerNotFoundException();
            } else {
                return entityplayermp;
            }
        }
    }

    public static String func_96332_d(ICommandSender icommandsender, String s) {
        EntityPlayerMP entityplayermp = PlayerSelector.func_82386_a(icommandsender, s);

        if (entityplayermp != null) {
            return entityplayermp.func_70005_c_();
        } else if (PlayerSelector.func_82378_b(s)) {
            throw new PlayerNotFoundException();
        } else {
            return s;
        }
    }

    public static IChatComponent func_147178_a(ICommandSender icommandsender, String[] astring, int i) {
        return func_147176_a(icommandsender, astring, i, false);
    }

    public static IChatComponent func_147176_a(ICommandSender icommandsender, String[] astring, int i, boolean flag) {
        ChatComponentText chatcomponenttext = new ChatComponentText("");

        for (int j = i; j < astring.length; ++j) {
            if (j > i) {
                chatcomponenttext.func_150258_a(" ");
            }

            Object object = new ChatComponentText(astring[j]);

            if (flag) {
                IChatComponent ichatcomponent = PlayerSelector.func_150869_b(icommandsender, astring[j]);

                if (ichatcomponent != null) {
                    object = ichatcomponent;
                } else if (PlayerSelector.func_82378_b(astring[j])) {
                    throw new PlayerNotFoundException();
                }
            }

            chatcomponenttext.func_150257_a((IChatComponent) object);
        }

        return chatcomponenttext;
    }

    public static String func_82360_a(ICommandSender icommandsender, String[] astring, int i) {
        StringBuilder stringbuilder = new StringBuilder();

        for (int j = i; j < astring.length; ++j) {
            if (j > i) {
                stringbuilder.append(" ");
            }

            String s = astring[j];

            stringbuilder.append(s);
        }

        return stringbuilder.toString();
    }

    public static double func_110666_a(ICommandSender icommandsender, double d0, String s) {
        return func_110665_a(icommandsender, d0, s, -30000000, 30000000);
    }

    public static double func_110665_a(ICommandSender icommandsender, double d0, String s, int i, int j) {
        boolean flag = s.startsWith("~");

        if (flag && Double.isNaN(d0)) {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[]{d0});
        } else {
            double d1 = flag ? d0 : 0.0D;

            if (!flag || s.length() > 1) {
                boolean flag1 = s.contains(".");

                if (flag) {
                    s = s.substring(1);
                }

                d1 += func_82363_b(icommandsender, s);
                if (!flag1 && !flag) {
                    d1 += 0.5D;
                }
            }

            if (i != 0 || j != 0) {
                if (d1 < (double) i) {
                    throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[]{d1, i});
                }

                if (d1 > (double) j) {
                    throw new NumberInvalidException("commands.generic.double.tooBig", new Object[]{d1, j});
                }
            }

            return d1;
        }
    }

    public static Item func_147179_f(ICommandSender icommandsender, String s) {
        Item item = (Item) Item.field_150901_e.func_82594_a(s);

        if (item == null) {
            try {
                Item item1 = Item.func_150899_d(Integer.parseInt(s));

                if (item1 != null) {
                    ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.generic.deprecatedId", new Object[]{Item.field_150901_e.func_148750_c(item1)});

                    chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.GRAY);
                    icommandsender.func_145747_a(chatcomponenttranslation);
                }

                item = item1;
            } catch (NumberFormatException numberformatexception) {
                ;
            }
        }

        if (item == null) {
            throw new NumberInvalidException("commands.give.notFound", new Object[]{s});
        } else {
            return item;
        }
    }

    public static Block func_147180_g(ICommandSender icommandsender, String s) {
        if (Block.field_149771_c.func_148741_d(s)) {
            return (Block) Block.field_149771_c.func_82594_a(s);
        } else {
            try {
                int i = Integer.parseInt(s);

                if (Block.field_149771_c.func_148753_b(i)) {
                    Block block = Block.func_149729_e(i);
                    ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.generic.deprecatedId", new Object[]{Block.field_149771_c.func_148750_c(block)});

                    chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.GRAY);
                    icommandsender.func_145747_a(chatcomponenttranslation);
                    return block;
                }
            } catch (NumberFormatException numberformatexception) {
                ;
            }

            throw new NumberInvalidException("commands.give.notFound", new Object[]{s});
        }
    }

    public static String func_71527_a(Object[] aobject) {
        StringBuilder stringbuilder = new StringBuilder();

        for (int i = 0; i < aobject.length; ++i) {
            String s = aobject[i].toString();

            if (i > 0) {
                if (i == aobject.length - 1) {
                    stringbuilder.append(" and ");
                } else {
                    stringbuilder.append(", ");
                }
            }

            stringbuilder.append(s);
        }

        return stringbuilder.toString();
    }

    public static IChatComponent func_147177_a(IChatComponent[] aichatcomponent) {
        ChatComponentText chatcomponenttext = new ChatComponentText("");

        for (int i = 0; i < aichatcomponent.length; ++i) {
            if (i > 0) {
                if (i == aichatcomponent.length - 1) {
                    chatcomponenttext.func_150258_a(" and ");
                } else if (i > 0) {
                    chatcomponenttext.func_150258_a(", ");
                }
            }

            chatcomponenttext.func_150257_a(aichatcomponent[i]);
        }

        return chatcomponenttext;
    }

    public static String func_96333_a(Collection collection) {
        return func_71527_a(collection.toArray(new String[collection.size()]));
    }

    public static boolean func_71523_a(String s, String s1) {
        return s1.regionMatches(true, 0, s, 0, s.length());
    }

    public static List func_71530_a(String[] astring, String... astring1) {
        String s = astring[astring.length - 1];
        ArrayList arraylist = new ArrayList();
        String[] astring2 = astring1;
        int i = astring1.length;

        for (int j = 0; j < i; ++j) {
            String s1 = astring2[j];

            if (func_71523_a(s, s1)) {
                arraylist.add(s1);
            }
        }

        return arraylist;
    }

    public static List func_71531_a(String[] astring, Iterable iterable) {
        String s = astring[astring.length - 1];
        ArrayList arraylist = new ArrayList();
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            String s1 = (String) iterator.next();

            if (func_71523_a(s, s1)) {
                arraylist.add(s1);
            }
        }

        return arraylist;
    }

    @Override
    public boolean func_82358_a(String[] astring, int i) {
        return false;
    }

    public static void func_152373_a(ICommandSender icommandsender, ICommand icommand, String s, Object... aobject) {
        func_152374_a(icommandsender, icommand, 0, s, aobject);
    }

    public static void func_152374_a(ICommandSender icommandsender, ICommand icommand, int i, String s, Object... aobject) {
        if (CommandBase.field_71533_a != null) {
            CommandBase.field_71533_a.func_152372_a(icommandsender, icommand, i, s, aobject);
        }

    }

    public static void func_71529_a(IAdminCommand iadmincommand) {
        CommandBase.field_71533_a = iadmincommand;
    }

    public int compareTo(ICommand icommand) {
        return this.func_71517_b().compareTo(icommand.func_71517_b());
    }
}
