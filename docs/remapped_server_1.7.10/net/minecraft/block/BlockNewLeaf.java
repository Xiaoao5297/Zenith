package net.minecraft.block;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockNewLeaf extends BlockLeaves {

    public static final String[][] field_150132_N = new String[][]{{"leaves_acacia", "leaves_big_oak"}, {"leaves_acacia_opaque", "leaves_big_oak_opaque"}};
    public static final String[] field_150133_O = new String[]{"acacia", "big_oak"};

    public BlockNewLeaf() {}

    @Override
    protected void func_150124_c(World world, int i, int j, int k, int l, int i1) {
        if ((l & 3) == 1 && world.field_73012_v.nextInt(i1) == 0) {
            this.func_149642_a(world, i, j, k, new ItemStack(Items.field_151034_e, 1, 0));
        }

    }

    @Override
    public int func_149692_a(int i) {
        return super.func_149692_a(i) + 4;
    }

    @Override
    public int func_149643_k(World world, int i, int j, int k) {
        return world.func_72805_g(i, j, k) & 3;
    }

    @Override
    public String[] func_150125_e() {
        return BlockNewLeaf.field_150133_O;
    }
}
