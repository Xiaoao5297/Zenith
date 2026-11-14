package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockReed extends Block {

    protected BlockReed() {
        super(Material.field_151585_k);
        float f = 0.375F;

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
        this.func_149675_a(true);
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (world.func_147439_a(i, j - 1, k) == Blocks.field_150436_aH || this.func_150170_e(world, i, j, k)) {
            if (world.func_147437_c(i, j + 1, k)) {
                int l;

                for (l = 1; world.func_147439_a(i, j - l, k) == this; ++l) {
                    ;
                }

                if (l < 3) {
                    int i1 = world.func_72805_g(i, j, k);

                    if (i1 == 15) {
                        world.func_147449_b(i, j + 1, k, this);
                        world.func_72921_c(i, j, k, 0, 4);
                    } else {
                        world.func_72921_c(i, j, k, i1 + 1, 4);
                    }
                }
            }

        }
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        Block block = world.func_147439_a(i, j - 1, k);

        return block == this ? true : (block != Blocks.field_150349_c && block != Blocks.field_150346_d && block != Blocks.field_150354_m ? false : (world.func_147439_a(i - 1, j - 1, k).func_149688_o() == Material.field_151586_h ? true : (world.func_147439_a(i + 1, j - 1, k).func_149688_o() == Material.field_151586_h ? true : (world.func_147439_a(i, j - 1, k - 1).func_149688_o() == Material.field_151586_h ? true : world.func_147439_a(i, j - 1, k + 1).func_149688_o() == Material.field_151586_h))));
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        this.func_150170_e(world, i, j, k);
    }

    protected final boolean func_150170_e(World world, int i, int j, int k) {
        if (!this.func_149718_j(world, i, j, k)) {
            this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
            world.func_147468_f(i, j, k);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean func_149718_j(World world, int i, int j, int k) {
        return this.func_149742_c(world, i, j, k);
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        return null;
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Items.field_151120_aE;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public int func_149645_b() {
        return 1;
    }
}
