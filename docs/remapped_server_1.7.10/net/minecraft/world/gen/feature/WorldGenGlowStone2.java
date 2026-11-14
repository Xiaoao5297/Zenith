package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenGlowStone2 extends WorldGenerator {

    public WorldGenGlowStone2() {}

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        if (!world.func_147437_c(i, j, k)) {
            return false;
        } else if (world.func_147439_a(i, j + 1, k) != Blocks.field_150424_aL) {
            return false;
        } else {
            world.func_147465_d(i, j, k, Blocks.field_150426_aN, 0, 2);

            for (int l = 0; l < 1500; ++l) {
                int i1 = i + random.nextInt(8) - random.nextInt(8);
                int j1 = j - random.nextInt(12);
                int k1 = k + random.nextInt(8) - random.nextInt(8);

                if (world.func_147439_a(i1, j1, k1).func_149688_o() == Material.field_151579_a) {
                    int l1 = 0;

                    for (int i2 = 0; i2 < 6; ++i2) {
                        Block block = null;

                        if (i2 == 0) {
                            block = world.func_147439_a(i1 - 1, j1, k1);
                        }

                        if (i2 == 1) {
                            block = world.func_147439_a(i1 + 1, j1, k1);
                        }

                        if (i2 == 2) {
                            block = world.func_147439_a(i1, j1 - 1, k1);
                        }

                        if (i2 == 3) {
                            block = world.func_147439_a(i1, j1 + 1, k1);
                        }

                        if (i2 == 4) {
                            block = world.func_147439_a(i1, j1, k1 - 1);
                        }

                        if (i2 == 5) {
                            block = world.func_147439_a(i1, j1, k1 + 1);
                        }

                        if (block == Blocks.field_150426_aN) {
                            ++l1;
                        }
                    }

                    if (l1 == 1) {
                        world.func_147465_d(i1, j1, k1, Blocks.field_150426_aN, 0, 2);
                    }
                }
            }

            return true;
        }
    }
}
