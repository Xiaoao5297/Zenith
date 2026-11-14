package net.minecraft.block;

import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockNetherWart extends BlockBush {

    protected BlockNetherWart() {
        this.func_149675_a(true);
        float f = 0.5F;

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.func_149647_a((CreativeTabs) null);
    }

    @Override
    protected boolean func_149854_a(Block block) {
        return block == Blocks.field_150425_aM;
    }

    @Override
    public boolean func_149718_j(World world, int i, int j, int k) {
        return this.func_149854_a(world.func_147439_a(i, j - 1, k));
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        int l = world.func_72805_g(i, j, k);

        if (l < 3 && random.nextInt(10) == 0) {
            ++l;
            world.func_72921_c(i, j, k, l, 2);
        }

        super.func_149674_a(world, i, j, k, random);
    }

    @Override
    public int func_149645_b() {
        return 6;
    }

    @Override
    public void func_149690_a(World world, int i, int j, int k, int l, float f, int i1) {
        if (!world.field_72995_K) {
            int j1 = 1;

            if (l >= 3) {
                j1 = 2 + world.field_73012_v.nextInt(3);
                if (i1 > 0) {
                    j1 += world.field_73012_v.nextInt(i1 + 1);
                }
            }

            for (int k1 = 0; k1 < j1; ++k1) {
                this.func_149642_a(world, i, j, k, new ItemStack(Items.field_151075_bm));
            }

        }
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return null;
    }

    @Override
    public int func_149745_a(Random random) {
        return 0;
    }
}
