package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class WorldGenFlowers extends WorldGenerator {

    private Block field_150552_a;
    private int field_150551_b;

    public WorldGenFlowers(Block block) {
        this.field_150552_a = block;
    }

    public void func_150550_a(Block block, int i) {
        this.field_150552_a = block;
        this.field_150551_b = i;
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        for (int l = 0; l < 64; ++l) {
            int i1 = i + random.nextInt(8) - random.nextInt(8);
            int j1 = j + random.nextInt(4) - random.nextInt(4);
            int k1 = k + random.nextInt(8) - random.nextInt(8);

            if (world.func_147437_c(i1, j1, k1) && (!world.field_73011_w.field_76576_e || j1 < 255) && this.field_150552_a.func_149718_j(world, i1, j1, k1)) {
                world.func_147465_d(i1, j1, k1, this.field_150552_a, this.field_150551_b, 2);
            }
        }

        return true;
    }
}
