package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaPineTree extends WorldGenHugeTrees {

    private boolean field_150542_e;

    public WorldGenMegaPineTree(boolean flag, boolean flag1) {
        super(flag, 13, 15, 1, 1);
        this.field_150542_e = flag1;
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        int l = this.func_150533_a(random);

        if (!this.func_150537_a(world, random, i, j, k, l)) {
            return false;
        } else {
            this.func_150541_c(world, i, k, j + l, 0, random);

            for (int i1 = 0; i1 < l; ++i1) {
                Block block = world.func_147439_a(i, j + i1, k);

                if (block.func_149688_o() == Material.field_151579_a || block.func_149688_o() == Material.field_151584_j) {
                    this.func_150516_a(world, i, j + i1, k, Blocks.field_150364_r, this.field_76520_b);
                }

                if (i1 < l - 1) {
                    block = world.func_147439_a(i + 1, j + i1, k);
                    if (block.func_149688_o() == Material.field_151579_a || block.func_149688_o() == Material.field_151584_j) {
                        this.func_150516_a(world, i + 1, j + i1, k, Blocks.field_150364_r, this.field_76520_b);
                    }

                    block = world.func_147439_a(i + 1, j + i1, k + 1);
                    if (block.func_149688_o() == Material.field_151579_a || block.func_149688_o() == Material.field_151584_j) {
                        this.func_150516_a(world, i + 1, j + i1, k + 1, Blocks.field_150364_r, this.field_76520_b);
                    }

                    block = world.func_147439_a(i, j + i1, k + 1);
                    if (block.func_149688_o() == Material.field_151579_a || block.func_149688_o() == Material.field_151584_j) {
                        this.func_150516_a(world, i, j + i1, k + 1, Blocks.field_150364_r, this.field_76520_b);
                    }
                }
            }

            return true;
        }
    }

    private void func_150541_c(World world, int i, int j, int k, int l, Random random) {
        int i1 = random.nextInt(5);

        if (this.field_150542_e) {
            i1 += this.field_76522_a;
        } else {
            i1 += 3;
        }

        int j1 = 0;

        for (int k1 = k - i1; k1 <= k; ++k1) {
            int l1 = k - k1;
            int i2 = l + MathHelper.func_76141_d((float) l1 / (float) i1 * 3.5F);

            this.func_150535_a(world, i, k1, j, i2 + (l1 > 0 && i2 == j1 && (k1 & 1) == 0 ? 1 : 0), random);
            j1 = i2;
        }

    }

    @Override
    public void func_150524_b(World world, Random random, int i, int j, int k) {
        this.func_150539_c(world, random, i - 1, j, k - 1);
        this.func_150539_c(world, random, i + 2, j, k - 1);
        this.func_150539_c(world, random, i - 1, j, k + 2);
        this.func_150539_c(world, random, i + 2, j, k + 2);

        for (int l = 0; l < 5; ++l) {
            int i1 = random.nextInt(64);
            int j1 = i1 % 8;
            int k1 = i1 / 8;

            if (j1 == 0 || j1 == 7 || k1 == 0 || k1 == 7) {
                this.func_150539_c(world, random, i - 3 + j1, j, k - 3 + k1);
            }
        }

    }

    private void func_150539_c(World world, Random random, int i, int j, int k) {
        for (int l = -2; l <= 2; ++l) {
            for (int i1 = -2; i1 <= 2; ++i1) {
                if (Math.abs(l) != 2 || Math.abs(i1) != 2) {
                    this.func_150540_a(world, i + l, j, k + i1);
                }
            }
        }

    }

    private void func_150540_a(World world, int i, int j, int k) {
        for (int l = j + 2; l >= j - 3; --l) {
            Block block = world.func_147439_a(i, l, k);

            if (block == Blocks.field_150349_c || block == Blocks.field_150346_d) {
                this.func_150516_a(world, i, l, k, Blocks.field_150346_d, 2);
                break;
            }

            if (block.func_149688_o() != Material.field_151579_a && l < j) {
                break;
            }
        }

    }
}
