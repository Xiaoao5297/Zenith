package net.minecraft.item;

import net.minecraft.init.Items;

public enum ItemArmor$ArmorMaterial {

    CLOTH(5, new int[]{1, 3, 2, 1}, 15), CHAIN(15, new int[]{2, 5, 4, 1}, 12), IRON(15, new int[]{2, 6, 5, 2}, 9), GOLD(7, new int[]{2, 5, 3, 1}, 25), DIAMOND(33, new int[]{3, 8, 6, 3}, 10);

    private int field_78048_f;
    private int[] field_78049_g;
    private int field_78055_h;

    private ItemArmor$ArmorMaterial(int i, int[] aint, int j) {
        this.field_78048_f = i;
        this.field_78049_g = aint;
        this.field_78055_h = j;
    }

    public int func_78046_a(int i) {
        return ItemArmor.access$000()[i] * this.field_78048_f;
    }

    public int func_78044_b(int i) {
        return this.field_78049_g[i];
    }

    public int func_78045_a() {
        return this.field_78055_h;
    }

    public Item func_151685_b() {
        return this == ItemArmor$ArmorMaterial.CLOTH ? Items.field_151116_aA : (this == ItemArmor$ArmorMaterial.CHAIN ? Items.field_151042_j : (this == ItemArmor$ArmorMaterial.GOLD ? Items.field_151043_k : (this == ItemArmor$ArmorMaterial.IRON ? Items.field_151042_j : (this == ItemArmor$ArmorMaterial.DIAMOND ? Items.field_151045_i : null))));
    }
}
