package net.minecraft.item;

import net.minecraft.util.EnumChatFormatting;

public enum EnumRarity {

    common(EnumChatFormatting.WHITE, "Common"), uncommon(EnumChatFormatting.YELLOW, "Uncommon"), rare(EnumChatFormatting.AQUA, "Rare"), epic(EnumChatFormatting.LIGHT_PURPLE, "Epic");

    public final EnumChatFormatting field_77937_e;
    public final String field_77934_f;

    private EnumRarity(EnumChatFormatting enumchatformatting, String s) {
        this.field_77937_e = enumchatformatting;
        this.field_77934_f = s;
    }
}
