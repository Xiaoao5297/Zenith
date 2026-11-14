package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenWaterlily extends WorldGenerator {

    public WorldGenWaterlily() {}

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        for (int l = 0; l < 10; ++l) {
            int i1 = i + random.nextInt(8) - random.nextInt(8);
            int j1 = j + random.nextInt(4) - random.nextInt(4);
            int k1 = k + random.nextInt(8) - random.nextInt(8);

            if (world.func_147437_c(i1, j1, k1) && Blocks.field_150392_bi.func_149742_c(world, i1, j1, k1)) {
                world.func_147465_d(i1, j1, k1, Blocks.field_150392_bi, 0, 2);
            }
        }

        return true;
    }
}
