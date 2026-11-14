package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenDoublePlant extends WorldGenerator {

    private int field_150549_a;

    public WorldGenDoublePlant() {}

    public void func_150548_a(int i) {
        this.field_150549_a = i;
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        boolean flag = false;

        for (int l = 0; l < 64; ++l) {
            int i1 = i + random.nextInt(8) - random.nextInt(8);
            int j1 = j + random.nextInt(4) - random.nextInt(4);
            int k1 = k + random.nextInt(8) - random.nextInt(8);

            if (world.func_147437_c(i1, j1, k1) && (!world.field_73011_w.field_76576_e || j1 < 254) && Blocks.field_150398_cm.func_149742_c(world, i1, j1, k1)) {
                Blocks.field_150398_cm.func_149889_c(world, i1, j1, k1, this.field_150549_a, 2);
                flag = true;
            }
        }

        return flag;
    }
}
