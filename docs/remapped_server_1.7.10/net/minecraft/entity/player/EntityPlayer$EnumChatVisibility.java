package net.minecraft.entity.player;

public enum EntityPlayer$EnumChatVisibility {

    FULL(0, "options.chat.visibility.full"), SYSTEM(1, "options.chat.visibility.system"), HIDDEN(2, "options.chat.visibility.hidden");

    private static final EntityPlayer$EnumChatVisibility[] field_151432_d = new EntityPlayer$EnumChatVisibility[values().length];
    private final int field_151433_e;
    private final String field_151430_f;

    private EntityPlayer$EnumChatVisibility(int i, String s) {
        this.field_151433_e = i;
        this.field_151430_f = s;
    }

    public int func_151428_a() {
        return this.field_151433_e;
    }

    public static EntityPlayer$EnumChatVisibility func_151426_a(int i) {
        return EntityPlayer$EnumChatVisibility.field_151432_d[i % EntityPlayer$EnumChatVisibility.field_151432_d.length];
    }

    static {
        EntityPlayer$EnumChatVisibility[] aentityplayer$enumchatvisibility = values();
        int i = aentityplayer$enumchatvisibility.length;

        for (int j = 0; j < i; ++j) {
            EntityPlayer$EnumChatVisibility entityplayer$enumchatvisibility = aentityplayer$enumchatvisibility[j];

            EntityPlayer$EnumChatVisibility.field_151432_d[entityplayer$enumchatvisibility.field_151433_e] = entityplayer$enumchatvisibility;
        }

    }
}
