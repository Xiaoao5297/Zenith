package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenIceSpike extends WorldGenerator {

    public WorldGenIceSpike() {}

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        while (world.func_147437_c(i, j, k) && j > 2) {
            --j;
        }

        if (world.func_147439_a(i, j, k) != Blocks.field_150433_aE) {
            return false;
        } else {
            j += random.nextInt(4);
            int l = random.nextInt(4) + 7;
            int i1 = l / 4 + random.nextInt(2);

            if (i1 > 1 && random.nextInt(60) == 0) {
                j += 10 + random.nextInt(30);
            }

            int j1;
            int k1;
            int l1;

            for (j1 = 0; j1 < l; ++j1) {
                float f = (1.0F - (float) j1 / (float) l) * (float) i1;

                k1 = MathHelper.func_76123_f(f);

                for (l1 = -k1; l1 <= k1; ++l1) {
                    float f1 = (float) MathHelper.func_76130_a(l1) - 0.25F;

                    for (int i2 = -k1; i2 <= k1; ++i2) {
                        float f2 = (float) MathHelper.func_76130_a(i2) - 0.25F;

                        if ((l1 == 0 && i2 == 0 || f1 * f1 + f2 * f2 <= f * f) && (l1 != -k1 && l1 != k1 && i2 != -k1 && i2 != k1 || random.nextFloat() <= 0.75F)) {
                            Block block = world.func_147439_a(i + l1, j + j1, k + i2);

                            if (block.func_149688_o() == Material.field_151579_a || block == Blocks.field_150346_d || block == Blocks.field_150433_aE || block == Blocks.field_150432_aD) {
                                this.func_150515_a(world, i + l1, j + j1, k + i2, Blocks.field_150403_cj);
                            }

                            if (j1 != 0 && k1 > 1) {
                                block = world.func_147439_a(i + l1, j - j1, k + i2);
                                if (block.func_149688_o() == Material.field_151579_a || block == Blocks.field_150346_d || block == Blocks.field_150433_aE || block == Blocks.field_150432_aD) {
                                    this.func_150515_a(world, i + l1, j - j1, k + i2, Blocks.field_150403_cj);
                                }
                            }
                        }
                    }
                }
            }

            j1 = i1 - 1;
            if (j1 < 0) {
                j1 = 0;
            } else if (j1 > 1) {
                j1 = 1;
            }

            for (int j2 = -j1; j2 <= j1; ++j2) {
                k1 = -j1;

                while (k1 <= j1) {
                    l1 = j - 1;
                    int k2 = 50;

                    if (Math.abs(j2) == 1 && Math.abs(k1) == 1) {
                        k2 = random.nextInt(5);
                    }

                    while (true) {
                        if (l1 > 50) {
                            Block block1 = world.func_147439_a(i + j2, l1, k + k1);

                            if (block1.func_149688_o() == Material.field_151579_a || block1 == Blocks.field_150346_d || block1 == Blocks.field_150433_aE || block1 == Blocks.field_150432_aD || block1 == Blocks.field_150403_cj) {
                                this.func_150515_a(world, i + j2, l1, k + k1, Blocks.field_150403_cj);
                                --l1;
                                --k2;
                                if (k2 <= 0) {
                                    l1 -= random.nextInt(5) + 1;
                                    k2 = random.nextInt(5);
                                }
                                continue;
                            }
                        }

                        ++k1;
                        break;
                    }
                }
            }

            return true;
        }
    }
}
