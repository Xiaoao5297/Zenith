package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class WorldGenDeadBush extends WorldGenerator {

    private Block field_150547_a;

    public WorldGenDeadBush(Block block) {
        this.field_150547_a = block;
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        Block block;

        while (((block = world.func_147439_a(i, j, k)).func_149688_o() == Material.field_151579_a || block.func_149688_o() == Material.field_151584_j) && j > 0) {
            --j;
        }

        for (int l = 0; l < 4; ++l) {
            int i1 = i + random.nextInt(8) - random.nextInt(8);
            int j1 = j + random.nextInt(4) - random.nextInt(4);
            int k1 = k + random.nextInt(8) - random.nextInt(8);

            if (world.func_147437_c(i1, j1, k1) && this.field_150547_a.func_149718_j(world, i1, j1, k1)) {
                world.func_147465_d(i1, j1, k1, this.field_150547_a, 0, 2);
            }
        }

        return true;
    }
}
