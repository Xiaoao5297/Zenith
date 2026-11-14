package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

public class WorldGenVines extends WorldGenerator {

    public WorldGenVines() {}

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        int l = i;

        for (int i1 = k; j < 128; ++j) {
            if (world.func_147437_c(i, j, k)) {
                for (int j1 = 2; j1 <= 5; ++j1) {
                    if (Blocks.field_150395_bd.func_149707_d(world, i, j, k, j1)) {
                        world.func_147465_d(i, j, k, Blocks.field_150395_bd, 1 << Direction.field_71579_d[Facing.field_71588_a[j1]], 2);
                        break;
                    }
                }
            } else {
                i = l + random.nextInt(4) - random.nextInt(4);
                k = i1 + random.nextInt(4) - random.nextInt(4);
            }
        }

        return true;
    }
}
