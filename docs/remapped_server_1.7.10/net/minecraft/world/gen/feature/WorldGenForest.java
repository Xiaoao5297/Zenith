package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenForest extends WorldGenAbstractTree {

    private boolean field_150531_a;

    public WorldGenForest(boolean flag, boolean flag1) {
        super(flag);
        this.field_150531_a = flag1;
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        int l = random.nextInt(3) + 5;

        if (this.field_150531_a) {
            l += random.nextInt(7);
        }

        boolean flag = true;

        if (j >= 1 && j + l + 1 <= 256) {
            int i1;
            int j1;
            int k1;

            for (int l1 = j; l1 <= j + 1 + l; ++l1) {
                i1 = 1;
                if (l1 == j) {
                    i1 = 0;
                }

                if (l1 >= j + 1 + l - 2) {
                    i1 = 2;
                }

                for (j1 = i - i1; j1 <= i + i1 && flag; ++j1) {
                    for (k1 = k - i1; k1 <= k + i1 && flag; ++k1) {
                        if (l1 >= 0 && l1 < 256) {
                            Block block = world.func_147439_a(j1, l1, k1);

                            if (!this.func_150523_a(block)) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                return false;
            } else {
                Block block1 = world.func_147439_a(i, j - 1, k);

                if ((block1 == Blocks.field_150349_c || block1 == Blocks.field_150346_d || block1 == Blocks.field_150458_ak) && j < 256 - l - 1) {
                    this.func_150515_a(world, i, j - 1, k, Blocks.field_150346_d);

                    for (i1 = j - 3 + l; i1 <= j + l; ++i1) {
                        j1 = i1 - (j + l);
                        k1 = 1 - j1 / 2;

                        for (int i2 = i - k1; i2 <= i + k1; ++i2) {
                            int j2 = i2 - i;

                            for (int k2 = k - k1; k2 <= k + k1; ++k2) {
                                int l2 = k2 - k;

                                if (Math.abs(j2) != k1 || Math.abs(l2) != k1 || random.nextInt(2) != 0 && j1 != 0) {
                                    Block block2 = world.func_147439_a(i2, i1, k2);

                                    if (block2.func_149688_o() == Material.field_151579_a || block2.func_149688_o() == Material.field_151584_j) {
                                        this.func_150516_a(world, i2, i1, k2, Blocks.field_150362_t, 2);
                                    }
                                }
                            }
                        }
                    }

                    for (i1 = 0; i1 < l; ++i1) {
                        Block block3 = world.func_147439_a(i, j + i1, k);

                        if (block3.func_149688_o() == Material.field_151579_a || block3.func_149688_o() == Material.field_151584_j) {
                            this.func_150516_a(world, i, j + i1, k, Blocks.field_150364_r, 2);
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}
