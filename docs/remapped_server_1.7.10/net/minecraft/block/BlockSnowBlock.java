package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class BlockSnowBlock extends Block {

    protected BlockSnowBlock() {
        super(Material.field_151596_z);
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Items.field_151126_ay;
    }

    @Override
    public int func_149745_a(Random random) {
        return 4;
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (world.func_72972_b(EnumSkyBlock.Block, i, j, k) > 11) {
            this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
            world.func_147468_f(i, j, k);
        }

    }
}
