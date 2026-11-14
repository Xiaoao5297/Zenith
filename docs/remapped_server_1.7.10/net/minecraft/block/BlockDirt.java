package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockDirt extends Block {

    public static final String[] field_150009_a = new String[]{"default", "default", "podzol"};

    protected BlockDirt() {
        super(Material.field_151578_c);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public int func_149692_a(int i) {
        return 0;
    }

    @Override
    protected ItemStack func_149644_j(int i) {
        if (i == 1) {
            i = 0;
        }

        return super.func_149644_j(i);
    }

    @Override
    public int func_149643_k(World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k);

        if (l == 1) {
            l = 0;
        }

        return l;
    }
}
