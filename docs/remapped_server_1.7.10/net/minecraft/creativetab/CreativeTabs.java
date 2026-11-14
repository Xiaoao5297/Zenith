package net.minecraft.creativetab;

import net.minecraft.enchantment.EnumEnchantmentType;

public abstract class CreativeTabs {

    public static final CreativeTabs[] field_78032_a = new CreativeTabs[12];
    public static final CreativeTabs field_78030_b = new CreativeTabs$1(0, "buildingBlocks");
    public static final CreativeTabs field_78031_c = new CreativeTabs$2(1, "decorations");
    public static final CreativeTabs field_78028_d = new CreativeTabs$3(2, "redstone");
    public static final CreativeTabs field_78029_e = new CreativeTabs$4(3, "transportation");
    public static final CreativeTabs field_78026_f = (new CreativeTabs$5(4, "misc")).func_111229_a(new EnumEnchantmentType[]{EnumEnchantmentType.all});
    public static final CreativeTabs field_78027_g = (new CreativeTabs$6(5, "search")).func_78025_a("item_search.png");
    public static final CreativeTabs field_78039_h = new CreativeTabs$7(6, "food");
    public static final CreativeTabs field_78040_i = (new CreativeTabs$8(7, "tools")).func_111229_a(new EnumEnchantmentType[]{EnumEnchantmentType.digger, EnumEnchantmentType.fishing_rod, EnumEnchantmentType.breakable});
    public static final CreativeTabs field_78037_j = (new CreativeTabs$9(8, "combat")).func_111229_a(new EnumEnchantmentType[]{EnumEnchantmentType.armor, EnumEnchantmentType.armor_feet, EnumEnchantmentType.armor_head, EnumEnchantmentType.armor_legs, EnumEnchantmentType.armor_torso, EnumEnchantmentType.bow, EnumEnchantmentType.weapon});
    public static final CreativeTabs field_78038_k = new CreativeTabs$10(9, "brewing");
    public static final CreativeTabs field_78035_l = new CreativeTabs$11(10, "materials");
    public static final CreativeTabs field_78036_m = (new CreativeTabs$12(11, "inventory")).func_78025_a("inventory.png").func_78022_j().func_78014_h();
    private final int field_78033_n;
    private final String field_78034_o;
    private String field_78043_p = "items.png";
    private boolean field_78042_q = true;
    private boolean field_78041_r = true;
    private EnumEnchantmentType[] field_111230_s;

    public CreativeTabs(int i, String s) {
        this.field_78033_n = i;
        this.field_78034_o = s;
        CreativeTabs.field_78032_a[i] = this;
    }

    public CreativeTabs func_78025_a(String s) {
        this.field_78043_p = s;
        return this;
    }

    public CreativeTabs func_78014_h() {
        this.field_78041_r = false;
        return this;
    }

    public CreativeTabs func_78022_j() {
        this.field_78042_q = false;
        return this;
    }

    public CreativeTabs func_111229_a(EnumEnchantmentType... aenumenchantmenttype) {
        this.field_111230_s = aenumenchantmenttype;
        return this;
    }
}
