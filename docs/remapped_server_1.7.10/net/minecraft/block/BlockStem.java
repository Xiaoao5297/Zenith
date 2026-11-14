package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStem extends BlockBush implements IGrowable {

    private final Block field_149877_a;

    protected BlockStem(Block block) {
        this.field_149877_a = block;
        this.func_149675_a(true);
        float f = 0.125F;

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.func_149647_a((CreativeTabs) null);
    }

    @Override
    protected boolean func_149854_a(Block block) {
        return block == Blocks.field_150458_ak;
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        super.func_149674_a(world, i, j, k, random);
        if (world.func_72957_l(i, j + 1, k) >= 9) {
            float f = this.func_149875_n(world, i, j, k);

            if (random.nextInt((int) (25.0F / f) + 1) == 0) {
                int l = world.func_72805_g(i, j, k);

                if (l < 7) {
                    ++l;
                    world.func_72921_c(i, j, k, l, 2);
                } else {
                    if (world.func_147439_a(i - 1, j, k) == this.field_149877_a) {
                        return;
                    }

                    if (world.func_147439_a(i + 1, j, k) == this.field_149877_a) {
                        return;
                    }

                    if (world.func_147439_a(i, j, k - 1) == this.field_149877_a) {
                        return;
                    }

                    if (world.func_147439_a(i, j, k + 1) == this.field_149877_a) {
                        return;
                    }

                    int i1 = random.nextInt(4);
                    int j1 = i;
                    int k1 = k;

                    if (i1 == 0) {
                        j1 = i - 1;
                    }

                    if (i1 == 1) {
                        ++j1;
                    }

                    if (i1 == 2) {
                        k1 = k - 1;
                    }

                    if (i1 == 3) {
                        ++k1;
                    }

                    Block block = world.func_147439_a(j1, j - 1, k1);

                    if (world.func_147439_a(j1, j, k1).field_149764_J == Material.field_151579_a && (block == Blocks.field_150458_ak || block == Blocks.field_150346_d || block == Blocks.field_150349_c)) {
                        world.func_147449_b(j1, j, k1, this.field_149877_a);
                    }
                }
            }
        }

    }

    public void func_149874_m(World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k) + MathHelper.func_76136_a(world.field_73012_v, 2, 5);

        if (l > 7) {
            l = 7;
        }

        world.func_72921_c(i, j, k, l, 2);
    }

    private float func_149875_n(World world, int i, int j, int k) {
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
                Block block8 = world.func_147439_a(l, j - 1, i1);
                float f1 = 0.0F;

                if (block8 == Blocks.field_150458_ak) {
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
    public void func_149683_g() {
        float f = 0.125F;

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        this.field_149756_F = (double) ((float) (iblockaccess.func_72805_g(i, j, k) * 2 + 2) / 16.0F);
        float f = 0.125F;

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, (float) this.field_149756_F, 0.5F + f);
    }

    @Override
    public int func_149645_b() {
        return 19;
    }

    @Override
    public void func_149690_a(World world, int i, int j, int k, int l, float f, int i1) {
        super.func_149690_a(world, i, j, k, l, f, i1);
        if (!world.field_72995_K) {
            Item item = null;

            if (this.field_149877_a == Blocks.field_150423_aK) {
                item = Items.field_151080_bb;
            }

            if (this.field_149877_a == Blocks.field_150440_ba) {
                item = Items.field_151081_bc;
            }

            for (int j1 = 0; j1 < 3; ++j1) {
                if (world.field_73012_v.nextInt(15) <= l) {
                    this.func_149642_a(world, i, j, k, new ItemStack(item));
                }
            }

        }
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return null;
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
        this.func_149874_m(world, i, j, k);
    }
}
