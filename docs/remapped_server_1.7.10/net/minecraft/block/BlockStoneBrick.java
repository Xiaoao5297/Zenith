package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockStoneBrick extends Block {

    public static final String[] field_150142_a = new String[]{"default", "mossy", "cracked", "chiseled"};
    public static final String[] field_150141_b = new String[]{null, "mossy", "cracked", "carved"};

    public BlockStoneBrick() {
        super(Material.field_151576_e);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public int func_149692_a(int i) {
        return i;
    }
}
