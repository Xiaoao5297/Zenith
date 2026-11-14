package net.minecraft.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public enum EnumChatFormatting {

    BLACK('0'), DARK_BLUE('1'), DARK_GREEN('2'), DARK_AQUA('3'), DARK_RED('4'), DARK_PURPLE('5'), GOLD('6'), GRAY('7'), DARK_GRAY('8'), BLUE('9'), GREEN('a'), AQUA('b'), RED('c'), LIGHT_PURPLE('d'), YELLOW('e'), WHITE('f'), OBFUSCATED('k', true), BOLD('l', true), STRIKETHROUGH('m', true), UNDERLINE('n', true), ITALIC('o', true), RESET('r');

    private static final Map field_96321_w = new HashMap();
    private static final Map field_96331_x = new HashMap();
    private static final Pattern field_96330_y = Pattern.compile("(?i)" + String.valueOf('\u00a7') + "[0-9A-FK-OR]");
    private final char field_96329_z;
    private final boolean field_96303_A;
    private final String field_96304_B;

    private EnumChatFormatting(char c0) {
        this(c0, false);
    }

    private EnumChatFormatting(char c0, boolean flag) {
        this.field_96329_z = c0;
        this.field_96303_A = flag;
        this.field_96304_B = "\u00a7" + c0;
    }

    public char func_96298_a() {
        return this.field_96329_z;
    }

    public boolean func_96301_b() {
        return this.field_96303_A;
    }

    public boolean func_96302_c() {
        return !this.field_96303_A && this != EnumChatFormatting.RESET;
    }

    public String func_96297_d() {
        return this.name().toLowerCase();
    }

    public String toString() {
        return this.field_96304_B;
    }

    public static EnumChatFormatting func_96300_b(String s) {
        return s == null ? null : (EnumChatFormatting) EnumChatFormatting.field_96331_x.get(s.toLowerCase());
    }

    public static Collection func_96296_a(boolean flag, boolean flag1) {
        ArrayList arraylist = new ArrayList();
        EnumChatFormatting[] aenumchatformatting = values();
        int i = aenumchatformatting.length;

        for (int j = 0; j < i; ++j) {
            EnumChatFormatting enumchatformatting = aenumchatformatting[j];

            if ((!enumchatformatting.func_96302_c() || flag) && (!enumchatformatting.func_96301_b() || flag1)) {
                arraylist.add(enumchatformatting.func_96297_d());
            }
        }

        return arraylist;
    }

    static {
        EnumChatFormatting[] aenumchatformatting = values();
        int i = aenumchatformatting.length;

        for (int j = 0; j < i; ++j) {
            EnumChatFormatting enumchatformatting = aenumchatformatting[j];

            EnumChatFormatting.field_96321_w.put(enumchatformatting.func_96298_a(), enumchatformatting);
            EnumChatFormatting.field_96331_x.put(enumchatformatting.func_96297_d(), enumchatformatting);
        }

    }
}
