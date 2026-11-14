package net.minecraft.item;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public enum Item$ToolMaterial {

    WOOD(0, 59, 2.0F, 0.0F, 15), STONE(1, 131, 4.0F, 1.0F, 5), IRON(2, 250, 6.0F, 2.0F, 14), EMERALD(3, 1561, 8.0F, 3.0F, 10), GOLD(0, 32, 12.0F, 0.0F, 22);

    private final int field_78001_f;
    private final int field_78002_g;
    private final float field_78010_h;
    private final float field_78011_i;
    private final int field_78008_j;

    private Item$ToolMaterial(int i, int j, float f, float f1, int k) {
        this.field_78001_f = i;
        this.field_78002_g = j;
        this.field_78010_h = f;
        this.field_78011_i = f1;
        this.field_78008_j = k;
    }

    public int func_77997_a() {
        return this.field_78002_g;
    }

    public float func_77998_b() {
        return this.field_78010_h;
    }

    public float func_78000_c() {
        return this.field_78011_i;
    }

    public int func_77996_d() {
        return this.field_78001_f;
    }

    public int func_77995_e() {
        return this.field_78008_j;
    }

    public Item func_150995_f() {
        return this == Item$ToolMaterial.WOOD ? Item.func_150898_a(Blocks.field_150344_f) : (this == Item$ToolMaterial.STONE ? Item.func_150898_a(Blocks.field_150347_e) : (this == Item$ToolMaterial.GOLD ? Items.field_151043_k : (this == Item$ToolMaterial.IRON ? Items.field_151042_j : (this == Item$ToolMaterial.EMERALD ? Items.field_151045_i : null))));
    }
}
