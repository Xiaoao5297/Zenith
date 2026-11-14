package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenSand extends WorldGenerator {

    private Block field_150517_a;
    private int field_76539_b;

    public WorldGenSand(Block block, int i) {
        this.field_150517_a = block;
        this.field_76539_b = i;
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        if (world.func_147439_a(i, j, k).func_149688_o() != Material.field_151586_h) {
            return false;
        } else {
            int l = random.nextInt(this.field_76539_b - 2) + 2;
            byte b0 = 2;

            for (int i1 = i - l; i1 <= i + l; ++i1) {
                for (int j1 = k - l; j1 <= k + l; ++j1) {
                    int k1 = i1 - i;
                    int l1 = j1 - k;

                    if (k1 * k1 + l1 * l1 <= l * l) {
                        for (int i2 = j - b0; i2 <= j + b0; ++i2) {
                            Block block = world.func_147439_a(i1, i2, j1);

                            if (block == Blocks.field_150346_d || block == Blocks.field_150349_c) {
                                world.func_147465_d(i1, i2, j1, this.field_150517_a, 0, 2);
                            }
                        }
                    }
                }
            }

            return true;
        }
    }
}
