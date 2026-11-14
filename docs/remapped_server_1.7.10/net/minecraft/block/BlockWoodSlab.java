package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockWoodSlab extends BlockSlab {

    public static final String[] field_150005_b = new String[]{"oak", "spruce", "birch", "jungle", "acacia", "big_oak"};

    public BlockWoodSlab(boolean flag) {
        super(flag, Material.field_151575_d);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Item.func_150898_a(Blocks.field_150376_bx);
    }

    @Override
    protected ItemStack func_149644_j(int i) {
        return new ItemStack(Item.func_150898_a(Blocks.field_150376_bx), 2, i & 7);
    }

    @Override
    public String func_150002_b(int i) {
        if (i < 0 || i >= BlockWoodSlab.field_150005_b.length) {
            i = 0;
        }

        return super.func_149739_a() + "." + BlockWoodSlab.field_150005_b[i];
    }
}
