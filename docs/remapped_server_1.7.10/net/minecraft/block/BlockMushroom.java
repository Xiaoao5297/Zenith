package net.minecraft.block;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;

public class BlockMushroom extends BlockBush implements IGrowable {

    protected BlockMushroom() {
        float f = 0.2F;

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
        this.func_149675_a(true);
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (random.nextInt(25) == 0) {
            byte b0 = 4;
            int l = 5;

            int i1;
            int j1;
            int k1;

            for (i1 = i - b0; i1 <= i + b0; ++i1) {
                for (k1 = k - b0; k1 <= k + b0; ++k1) {
                    for (j1 = j - 1; j1 <= j + 1; ++j1) {
                        if (world.func_147439_a(i1, j1, k1) == this) {
                            --l;
                            if (l <= 0) {
                                return;
                            }
                        }
                    }
                }
            }

            i1 = i + random.nextInt(3) - 1;
            k1 = j + random.nextInt(2) - random.nextInt(2);
            j1 = k + random.nextInt(3) - 1;

            for (int l1 = 0; l1 < 4; ++l1) {
                if (world.func_147437_c(i1, k1, j1) && this.func_149718_j(world, i1, k1, j1)) {
                    i = i1;
                    j = k1;
                    k = j1;
                }

                i1 = i + random.nextInt(3) - 1;
                k1 = j + random.nextInt(2) - random.nextInt(2);
                j1 = k + random.nextInt(3) - 1;
            }

            if (world.func_147437_c(i1, k1, j1) && this.func_149718_j(world, i1, k1, j1)) {
                world.func_147465_d(i1, k1, j1, this, 0, 2);
            }
        }

    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return super.func_149742_c(world, i, j, k) && this.func_149718_j(world, i, j, k);
    }

    @Override
    protected boolean func_149854_a(Block block) {
        return block.func_149730_j();
    }

    @Override
    public boolean func_149718_j(World world, int i, int j, int k) {
        if (j >= 0 && j < 256) {
            Block block = world.func_147439_a(i, j - 1, k);

            return block == Blocks.field_150391_bh || block == Blocks.field_150346_d && world.func_72805_g(i, j - 1, k) == 2 || world.func_72883_k(i, j, k) < 13 && this.func_149854_a(block);
        } else {
            return false;
        }
    }

    public boolean func_149884_c(World world, int i, int j, int k, Random random) {
        int l = world.func_72805_g(i, j, k);

        world.func_147468_f(i, j, k);
        WorldGenBigMushroom worldgenbigmushroom = null;

        if (this == Blocks.field_150338_P) {
            worldgenbigmushroom = new WorldGenBigMushroom(0);
        } else if (this == Blocks.field_150337_Q) {
            worldgenbigmushroom = new WorldGenBigMushroom(1);
        }

        if (worldgenbigmushroom != null && worldgenbigmushroom.func_76484_a(world, random, i, j, k)) {
            return true;
        } else {
            world.func_147465_d(i, j, k, this, l, 3);
            return false;
        }
    }

    @Override
    public boolean func_149851_a(World world, int i, int j, int k, boolean flag) {
        return true;
    }

    @Override
    public boolean func_149852_a(World world, Random random, int i, int j, int k) {
        return (double) random.nextFloat() < 0.4D;
    }

    @Override
    public void func_149853_b(World world, Random random, int i, int j, int k) {
        this.func_149884_c(world, i, j, k, random);
    }
}
