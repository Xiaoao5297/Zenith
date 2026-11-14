package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenTaiga2 extends WorldGenAbstractTree {

    public WorldGenTaiga2(boolean flag) {
        super(flag);
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        int l = random.nextInt(4) + 6;
        int i1 = 1 + random.nextInt(2);
        int j1 = l - i1;
        int k1 = 2 + random.nextInt(2);
        boolean flag = true;

        if (j >= 1 && j + l + 1 <= 256) {
            int l1;
            int i2;

            for (int j2 = j; j2 <= j + 1 + l && flag; ++j2) {
                boolean flag1 = true;

                if (j2 - j < i1) {
                    l1 = 0;
                } else {
                    l1 = k1;
                }

                for (i2 = i - l1; i2 <= i + l1 && flag; ++i2) {
                    for (int k2 = k - l1; k2 <= k + l1 && flag; ++k2) {
                        if (j2 >= 0 && j2 < 256) {
                            Block block = world.func_147439_a(i2, j2, k2);

                            if (block.func_149688_o() != Material.field_151579_a && block.func_149688_o() != Material.field_151584_j) {
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
                    l1 = random.nextInt(2);
                    i2 = 1;
                    byte b0 = 0;

                    int l2;
                    int i3;

                    for (i3 = 0; i3 <= j1; ++i3) {
                        l2 = j + l - i3;

                        for (int j3 = i - l1; j3 <= i + l1; ++j3) {
                            int k3 = j3 - i;

                            for (int l3 = k - l1; l3 <= k + l1; ++l3) {
                                int i4 = l3 - k;

                                if ((Math.abs(k3) != l1 || Math.abs(i4) != l1 || l1 <= 0) && !world.func_147439_a(j3, l2, l3).func_149730_j()) {
                                    this.func_150516_a(world, j3, l2, l3, Blocks.field_150362_t, 1);
                                }
                            }
                        }

                        if (l1 >= i2) {
                            l1 = b0;
                            b0 = 1;
                            ++i2;
                            if (i2 > k1) {
                                i2 = k1;
                            }
                        } else {
                            ++l1;
                        }
                    }

                    i3 = random.nextInt(3);

                    for (l2 = 0; l2 < l - i3; ++l2) {
                        Block block2 = world.func_147439_a(i, j + l2, k);

                        if (block2.func_149688_o() == Material.field_151579_a || block2.func_149688_o() == Material.field_151584_j) {
                            this.func_150516_a(world, i, j + l2, k, Blocks.field_150364_r, 1);
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
