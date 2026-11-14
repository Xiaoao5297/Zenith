package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockStoneSlab extends BlockSlab {

    public static final String[] field_150006_b = new String[]{"stone", "sand", "wood", "cobble", "brick", "smoothStoneBrick", "netherBrick", "quartz"};

    public BlockStoneSlab(boolean flag) {
        super(flag, Material.field_151576_e);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Item.func_150898_a(Blocks.field_150333_U);
    }

    @Override
    protected ItemStack func_149644_j(int i) {
        return new ItemStack(Item.func_150898_a(Blocks.field_150333_U), 2, i & 7);
    }

    @Override
    public String func_150002_b(int i) {
        if (i < 0 || i >= BlockStoneSlab.field_150006_b.length) {
            i = 0;
        }

        return super.func_149739_a() + "." + BlockStoneSlab.field_150006_b[i];
    }
}
