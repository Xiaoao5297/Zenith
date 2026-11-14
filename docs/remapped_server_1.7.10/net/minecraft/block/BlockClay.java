package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockClay extends Block {

    public BlockClay() {
        super(Material.field_151571_B);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Items.field_151119_aD;
    }

    @Override
    public int func_149745_a(Random random) {
        return 4;
    }
}
