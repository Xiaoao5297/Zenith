package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneRepeater extends BlockRedstoneDiode {

    public static final double[] field_149973_b = new double[]{-0.0625D, 0.0625D, 0.1875D, 0.3125D};
    private static final int[] field_149974_M = new int[]{1, 2, 3, 4};

    protected BlockRedstoneRepeater(boolean flag) {
        super(flag);
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        int i1 = world.func_72805_g(i, j, k);
        int j1 = (i1 & 12) >> 2;

        j1 = j1 + 1 << 2 & 12;
        world.func_72921_c(i, j, k, j1 | i1 & 3, 3);
        return true;
    }

    @Override
    protected int func_149901_b(int i) {
        return BlockRedstoneRepeater.field_149974_M[(i & 12) >> 2] * 2;
    }

    @Override
    protected BlockRedstoneDiode func_149906_e() {
        return Blocks.field_150416_aS;
    }

    @Override
    protected BlockRedstoneDiode func_149898_i() {
        return Blocks.field_150413_aR;
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Items.field_151107_aW;
    }

    @Override
    public int func_149645_b() {
        return 15;
    }

    @Override
    public boolean func_149910_g(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return this.func_149902_h(iblockaccess, i, j, k, l) > 0;
    }

    @Override
    protected boolean func_149908_a(Block block) {
        return func_149909_d(block);
    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        super.func_149749_a(world, i, j, k, block, l);
        this.func_149911_e(world, i, j, k);
    }
}
