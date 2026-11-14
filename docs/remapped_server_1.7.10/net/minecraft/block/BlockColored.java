package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockColored extends Block {

    public BlockColored(Material material) {
        super(material);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public int func_149692_a(int i) {
        return i;
    }

    public static int func_150032_b(int i) {
        return func_150031_c(i);
    }

    public static int func_150031_c(int i) {
        return ~i & 15;
    }

    @Override
    public MapColor func_149728_f(int i) {
        return MapColor.func_151644_a(i);
    }
}
