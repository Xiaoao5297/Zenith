package net.minecraft.block;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockOldLeaf extends BlockLeaves {

    public static final String[][] field_150130_N = new String[][]{{"leaves_oak", "leaves_spruce", "leaves_birch", "leaves_jungle"}, {"leaves_oak_opaque", "leaves_spruce_opaque", "leaves_birch_opaque", "leaves_jungle_opaque"}};
    public static final String[] field_150131_O = new String[]{"oak", "spruce", "birch", "jungle"};

    public BlockOldLeaf() {}

    @Override
    protected void func_150124_c(World world, int i, int j, int k, int l, int i1) {
        if ((l & 3) == 0 && world.field_73012_v.nextInt(i1) == 0) {
            this.func_149642_a(world, i, j, k, new ItemStack(Items.field_151034_e, 1, 0));
        }

    }

    @Override
    protected int func_150123_b(int i) {
        int j = super.func_150123_b(i);

        if ((i & 3) == 3) {
            j = 40;
        }

        return j;
    }

    @Override
    public String[] func_150125_e() {
        return BlockOldLeaf.field_150131_O;
    }
}
