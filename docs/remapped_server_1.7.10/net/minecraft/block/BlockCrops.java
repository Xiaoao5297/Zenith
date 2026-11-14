package net.minecraft.block;

import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCrops extends BlockBush implements IGrowable {

    protected BlockCrops() {
        this.func_149675_a(true);
        float f = 0.5F;

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.func_149647_a((CreativeTabs) null);
        this.func_149711_c(0.0F);
        this.func_149672_a(BlockCrops.field_149779_h);
        this.func_149649_H();
    }

    @Override
    protected boolean func_149854_a(Block block) {
        return block == Blocks.field_150458_ak;
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        super.func_149674_a(world, i, j, k, random);
        if (world.func_72957_l(i, j + 1, k) >= 9) {
            int l = world.func_72805_g(i, j, k);

            if (l < 7) {
                float f = this.func_149864_n(world, i, j, k);

                if (random.nextInt((int) (25.0F / f) + 1) == 0) {
                    ++l;
                    world.func_72921_c(i, j, k, l, 2);
                }
            }
        }

    }

    public void func_149863_m(World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k) + MathHelper.func_76136_a(world.field_73012_v, 2, 5);

        if (l > 7) {
            l = 7;
        }

        world.func_72921_c(i, j, k, l, 2);
    }

    private float func_149864_n(World world, int i, int j, int k) {
        float f = 1.0F;
        Block block = world.func_147439_a(i, j, k - 1);
        Block block1 = world.func_147439_a(i, j, k + 1);
        Block block2 = world.func_147439_a(i - 1, j, k);
        Block block3 = world.func_147439_a(i + 1, j, k);
        Block block4 = world.func_147439_a(i - 1, j, k - 1);
        Block block5 = world.func_147439_a(i + 1, j, k - 1);
        Block block6 = world.func_147439_a(i + 1, j, k + 1);
        Block block7 = world.func_147439_a(i - 1, j, k + 1);
        boolean flag = block2 == this || block3 == this;
        boolean flag1 = block == this || block1 == this;
        boolean flag2 = block4 == this || block5 == this || block6 == this || block7 == this;

        for (int l = i - 1; l <= i + 1; ++l) {
            for (int i1 = k - 1; i1 <= k + 1; ++i1) {
                float f1 = 0.0F;

                if (world.func_147439_a(l, j - 1, i1) == Blocks.field_150458_ak) {
                    f1 = 1.0F;
                    if (world.func_72805_g(l, j - 1, i1) > 0) {
                        f1 = 3.0F;
                    }
                }

                if (l != i || i1 != k) {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        if (flag2 || flag && flag1) {
            f /= 2.0F;
        }

        return f;
    }

    @Override
    public int func_149645_b() {
        return 6;
    }

    protected Item func_149866_i() {
        return Items.field_151014_N;
    }

    protected Item func_149865_P() {
        return Items.field_151015_O;
    }

    @Override
    public void func_149690_a(World world, int i, int j, int k, int l, float f, int i1) {
        super.func_149690_a(world, i, j, k, l, f, 0);
        if (!world.field_72995_K) {
            if (l >= 7) {
                int j1 = 3 + i1;

                for (int k1 = 0; k1 < j1; ++k1) {
                    if (world.field_73012_v.nextInt(15) <= l) {
                        this.func_149642_a(world, i, j, k, new ItemStack(this.func_149866_i(), 1, 0));
                    }
                }
            }

        }
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return i == 7 ? this.func_149865_P() : this.func_149866_i();
    }

    @Override
    public int func_149745_a(Random random) {
        return 1;
    }

    @Override
    public boolean func_149851_a(World world, int i, int j, int k, boolean flag) {
        return world.func_72805_g(i, j, k) != 7;
    }

    @Override
    public boolean func_149852_a(World world, Random random, int i, int j, int k) {
        return true;
    }

    @Override
    public void func_149853_b(World world, Random random, int i, int j, int k) {
        this.func_149863_m(world, i, j, k);
    }
}
