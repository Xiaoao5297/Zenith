package net.minecraft.stats;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class Achievement extends StatBase {

    public final int field_75993_a;
    public final int field_75991_b;
    public final Achievement field_75992_c;
    private final String field_75996_k;
    public final ItemStack field_75990_d;
    private boolean field_75995_m;

    public Achievement(String s, String s1, int i, int j, Item item, Achievement achievement) {
        this(s, s1, i, j, new ItemStack(item), achievement);
    }

    public Achievement(String s, String s1, int i, int j, Block block, Achievement achievement) {
        this(s, s1, i, j, new ItemStack(block), achievement);
    }

    public Achievement(String s, String s1, int i, int j, ItemStack itemstack, Achievement achievement) {
        super(s, new ChatComponentTranslation("achievement." + s1, new Object[0]));
        this.field_75990_d = itemstack;
        this.field_75996_k = "achievement." + s1 + ".desc";
        this.field_75993_a = i;
        this.field_75991_b = j;
        if (i < AchievementList.field_76010_a) {
            AchievementList.field_76010_a = i;
        }

        if (j < AchievementList.field_76008_b) {
            AchievementList.field_76008_b = j;
        }

        if (i > AchievementList.field_76009_c) {
            AchievementList.field_76009_c = i;
        }

        if (j > AchievementList.field_76006_d) {
            AchievementList.field_76006_d = j;
        }

        this.field_75992_c = achievement;
    }

    @Override
    public Achievement func_75966_h() {
        this.field_75972_f = true;
        return this;
    }

    public Achievement func_75987_b() {
        this.field_75995_m = true;
        return this;
    }

    @Override
    public Achievement func_75971_g() {
        super.func_75971_g();
        AchievementList.field_76007_e.add(this);
        return this;
    }

    @Override
    public boolean func_75967_d() {
        return true;
    }

    @Override
    public IChatComponent func_150951_e() {
        IChatComponent ichatcomponent = super.func_150951_e();

        ichatcomponent.func_150256_b().func_150238_a(this.func_75984_f() ? EnumChatFormatting.DARK_PURPLE : EnumChatFormatting.GREEN);
        return ichatcomponent;
    }

    @Override
    public Achievement func_150953_b(Class oclass) {
        return (Achievement) super.func_150953_b(oclass);
    }

    public boolean func_75984_f() {
        return this.field_75995_m;
    }
}
