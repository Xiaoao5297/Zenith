package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenBlockBlob extends WorldGenerator {

    private Block field_150545_a;
    private int field_150544_b;

    public WorldGenBlockBlob(Block block, int i) {
        super(false);
        this.field_150545_a = block;
        this.field_150544_b = i;
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        while (true) {
            if (j > 3) {
                label63:
                {
                    if (!world.func_147437_c(i, j - 1, k)) {
                        Block block = world.func_147439_a(i, j - 1, k);

                        if (block == Blocks.field_150349_c || block == Blocks.field_150346_d || block == Blocks.field_150348_b) {
                            break label63;
                        }
                    }

                    --j;
                    continue;
                }
            }

            if (j <= 3) {
                return false;
            }

            int l = this.field_150544_b;

            for (int i1 = 0; l >= 0 && i1 < 3; ++i1) {
                int j1 = l + random.nextInt(2);
                int k1 = l + random.nextInt(2);
                int l1 = l + random.nextInt(2);
                float f = (float) (j1 + k1 + l1) * 0.333F + 0.5F;

                for (int i2 = i - j1; i2 <= i + j1; ++i2) {
                    for (int j2 = k - l1; j2 <= k + l1; ++j2) {
                        for (int k2 = j - k1; k2 <= j + k1; ++k2) {
                            float f1 = (float) (i2 - i);
                            float f2 = (float) (j2 - k);
                            float f3 = (float) (k2 - j);

                            if (f1 * f1 + f2 * f2 + f3 * f3 <= f * f) {
                                world.func_147465_d(i2, k2, j2, this.field_150545_a, 0, 4);
                            }
                        }
                    }
                }

                i += -(l + 1) + random.nextInt(2 + l * 2);
                k += -(l + 1) + random.nextInt(2 + l * 2);
                j += 0 - random.nextInt(2);
            }

            return true;
        }
    }
}
