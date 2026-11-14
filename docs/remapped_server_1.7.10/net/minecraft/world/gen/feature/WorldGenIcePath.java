package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenIcePath extends WorldGenerator {

    private Block field_150555_a;
    private int field_150554_b;

    public WorldGenIcePath(int i) {
        this.field_150555_a = Blocks.field_150403_cj;
        this.field_150554_b = i;
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        while (world.func_147437_c(i, j, k) && j > 2) {
            --j;
        }

        if (world.func_147439_a(i, j, k) != Blocks.field_150433_aE) {
            return false;
        } else {
            int l = random.nextInt(this.field_150554_b - 2) + 2;
            byte b0 = 1;

            for (int i1 = i - l; i1 <= i + l; ++i1) {
                for (int j1 = k - l; j1 <= k + l; ++j1) {
                    int k1 = i1 - i;
                    int l1 = j1 - k;

                    if (k1 * k1 + l1 * l1 <= l * l) {
                        for (int i2 = j - b0; i2 <= j + b0; ++i2) {
                            Block block = world.func_147439_a(i1, i2, j1);

                            if (block == Blocks.field_150346_d || block == Blocks.field_150433_aE || block == Blocks.field_150432_aD) {
                                world.func_147465_d(i1, i2, j1, this.field_150555_a, 0, 2);
                            }
                        }
                    }
                }
            }

            return true;
        }
    }
}
