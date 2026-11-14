package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenDesertWells extends WorldGenerator {

    public WorldGenDesertWells() {}

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        while (world.func_147437_c(i, j, k) && j > 2) {
            --j;
        }

        if (world.func_147439_a(i, j, k) != Blocks.field_150354_m) {
            return false;
        } else {
            int l;
            int i1;

            for (i1 = -2; i1 <= 2; ++i1) {
                for (l = -2; l <= 2; ++l) {
                    if (world.func_147437_c(i + i1, j - 1, k + l) && world.func_147437_c(i + i1, j - 2, k + l)) {
                        return false;
                    }
                }
            }

            for (i1 = -1; i1 <= 0; ++i1) {
                for (l = -2; l <= 2; ++l) {
                    for (int j1 = -2; j1 <= 2; ++j1) {
                        world.func_147465_d(i + l, j + i1, k + j1, Blocks.field_150322_A, 0, 2);
                    }
                }
            }

            world.func_147465_d(i, j, k, Blocks.field_150358_i, 0, 2);
            world.func_147465_d(i - 1, j, k, Blocks.field_150358_i, 0, 2);
            world.func_147465_d(i + 1, j, k, Blocks.field_150358_i, 0, 2);
            world.func_147465_d(i, j, k - 1, Blocks.field_150358_i, 0, 2);
            world.func_147465_d(i, j, k + 1, Blocks.field_150358_i, 0, 2);

            for (i1 = -2; i1 <= 2; ++i1) {
                for (l = -2; l <= 2; ++l) {
                    if (i1 == -2 || i1 == 2 || l == -2 || l == 2) {
                        world.func_147465_d(i + i1, j + 1, k + l, Blocks.field_150322_A, 0, 2);
                    }
                }
            }

            world.func_147465_d(i + 2, j + 1, k, Blocks.field_150333_U, 1, 2);
            world.func_147465_d(i - 2, j + 1, k, Blocks.field_150333_U, 1, 2);
            world.func_147465_d(i, j + 1, k + 2, Blocks.field_150333_U, 1, 2);
            world.func_147465_d(i, j + 1, k - 2, Blocks.field_150333_U, 1, 2);

            for (i1 = -1; i1 <= 1; ++i1) {
                for (l = -1; l <= 1; ++l) {
                    if (i1 == 0 && l == 0) {
                        world.func_147465_d(i + i1, j + 4, k + l, Blocks.field_150322_A, 0, 2);
                    } else {
                        world.func_147465_d(i + i1, j + 4, k + l, Blocks.field_150333_U, 1, 2);
                    }
                }
            }

            for (i1 = 1; i1 <= 3; ++i1) {
                world.func_147465_d(i - 1, j + i1, k - 1, Blocks.field_150322_A, 0, 2);
                world.func_147465_d(i - 1, j + i1, k + 1, Blocks.field_150322_A, 0, 2);
                world.func_147465_d(i + 1, j + i1, k - 1, Blocks.field_150322_A, 0, 2);
                world.func_147465_d(i + 1, j + i1, k + 1, Blocks.field_150322_A, 0, 2);
            }

            return true;
        }
    }
}
