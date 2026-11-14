package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenTaiga1 extends WorldGenAbstractTree {

    public WorldGenTaiga1() {
        super(false);
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        int l = random.nextInt(5) + 7;
        int i1 = l - random.nextInt(2) - 3;
        int j1 = l - i1;
        int k1 = 1 + random.nextInt(j1 + 1);
        boolean flag = true;

        if (j >= 1 && j + l + 1 <= 256) {
            int l1;
            int i2;
            int j2;

            for (int k2 = j; k2 <= j + 1 + l && flag; ++k2) {
                boolean flag1 = true;

                if (k2 - j < i1) {
                    l1 = 0;
                } else {
                    l1 = k1;
                }

                for (i2 = i - l1; i2 <= i + l1 && flag; ++i2) {
                    for (j2 = k - l1; j2 <= k + l1 && flag; ++j2) {
                        if (k2 >= 0 && k2 < 256) {
                            Block block = world.func_147439_a(i2, k2, j2);

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

                if ((block1 == Blocks.field_150349_c || block1 == Blocks.field_150346_d) && j < 256 - l - 1) {
                    this.func_150515_a(world, i, j - 1, k, Blocks.field_150346_d);
                    l1 = 0;

                    for (i2 = j + l; i2 >= j + i1; --i2) {
                        for (j2 = i - l1; j2 <= i + l1; ++j2) {
                            int l2 = j2 - i;

                            for (int i3 = k - l1; i3 <= k + l1; ++i3) {
                                int j3 = i3 - k;

                                if ((Math.abs(l2) != l1 || Math.abs(j3) != l1 || l1 <= 0) && !world.func_147439_a(j2, i2, i3).func_149730_j()) {
                                    this.func_150516_a(world, j2, i2, i3, Blocks.field_150362_t, 1);
                                }
                            }
                        }

                        if (l1 >= 1 && i2 == j + i1 + 1) {
                            --l1;
                        } else if (l1 < k1) {
                            ++l1;
                        }
                    }

                    for (i2 = 0; i2 < l - 1; ++i2) {
                        Block block2 = world.func_147439_a(i, j + i2, k);

                        if (block2.func_149688_o() == Material.field_151579_a || block2.func_149688_o() == Material.field_151584_j) {
                            this.func_150516_a(world, i, j + i2, k, Blocks.field_150364_r, 1);
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
