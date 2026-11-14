package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public abstract class WorldGenHugeTrees extends WorldGenAbstractTree {

    protected final int field_76522_a;
    protected final int field_76520_b;
    protected final int field_76521_c;
    protected int field_150538_d;

    public WorldGenHugeTrees(boolean flag, int i, int j, int k, int l) {
        super(flag);
        this.field_76522_a = i;
        this.field_150538_d = j;
        this.field_76520_b = k;
        this.field_76521_c = l;
    }

    protected int func_150533_a(Random random) {
        int i = random.nextInt(3) + this.field_76522_a;

        if (this.field_150538_d > 1) {
            i += random.nextInt(this.field_150538_d);
        }

        return i;
    }

    private boolean func_150536_b(World world, Random random, int i, int j, int k, int l) {
        boolean flag = true;

        if (j >= 1 && j + l + 1 <= 256) {
            for (int i1 = j; i1 <= j + 1 + l; ++i1) {
                byte b0 = 2;

                if (i1 == j) {
                    b0 = 1;
                }

                if (i1 >= j + 1 + l - 2) {
                    b0 = 2;
                }

                for (int j1 = i - b0; j1 <= i + b0 && flag; ++j1) {
                    for (int k1 = k - b0; k1 <= k + b0 && flag; ++k1) {
                        if (i1 >= 0 && i1 < 256) {
                            Block block = world.func_147439_a(j1, i1, k1);

                            if (!this.func_150523_a(block)) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            return flag;
        } else {
            return false;
        }
    }

    private boolean func_150532_c(World world, Random random, int i, int j, int k) {
        Block block = world.func_147439_a(i, j - 1, k);

        if ((block == Blocks.field_150349_c || block == Blocks.field_150346_d) && j >= 2) {
            world.func_147465_d(i, j - 1, k, Blocks.field_150346_d, 0, 2);
            world.func_147465_d(i + 1, j - 1, k, Blocks.field_150346_d, 0, 2);
            world.func_147465_d(i, j - 1, k + 1, Blocks.field_150346_d, 0, 2);
            world.func_147465_d(i + 1, j - 1, k + 1, Blocks.field_150346_d, 0, 2);
            return true;
        } else {
            return false;
        }
    }

    protected boolean func_150537_a(World world, Random random, int i, int j, int k, int l) {
        return this.func_150536_b(world, random, i, j, k, l) && this.func_150532_c(world, random, i, j, k);
    }

    protected void func_150535_a(World world, int i, int j, int k, int l, Random random) {
        int i1 = l * l;

        for (int j1 = i - l; j1 <= i + l + 1; ++j1) {
            int k1 = j1 - i;

            for (int l1 = k - l; l1 <= k + l + 1; ++l1) {
                int i2 = l1 - k;
                int j2 = k1 - 1;
                int k2 = i2 - 1;

                if (k1 * k1 + i2 * i2 <= i1 || j2 * j2 + k2 * k2 <= i1 || k1 * k1 + k2 * k2 <= i1 || j2 * j2 + i2 * i2 <= i1) {
                    Block block = world.func_147439_a(j1, j, l1);

                    if (block.func_149688_o() == Material.field_151579_a || block.func_149688_o() == Material.field_151584_j) {
                        this.func_150516_a(world, j1, j, l1, Blocks.field_150362_t, this.field_76521_c);
                    }
                }
            }
        }

    }

    protected void func_150534_b(World world, int i, int j, int k, int l, Random random) {
        int i1 = l * l;

        for (int j1 = i - l; j1 <= i + l; ++j1) {
            int k1 = j1 - i;

            for (int l1 = k - l; l1 <= k + l; ++l1) {
                int i2 = l1 - k;

                if (k1 * k1 + i2 * i2 <= i1) {
                    Block block = world.func_147439_a(j1, j, l1);

                    if (block.func_149688_o() == Material.field_151579_a || block.func_149688_o() == Material.field_151584_j) {
                        this.func_150516_a(world, j1, j, l1, Blocks.field_150362_t, this.field_76521_c);
                    }
                }
            }
        }

    }
}
