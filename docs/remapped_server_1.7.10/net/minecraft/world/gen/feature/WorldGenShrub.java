package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenShrub extends WorldGenTrees {

    private int field_150528_a;
    private int field_150527_b;

    public WorldGenShrub(int i, int j) {
        super(false);
        this.field_150527_b = i;
        this.field_150528_a = j;
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        Block block;

        while (((block = world.func_147439_a(i, j, k)).func_149688_o() == Material.field_151579_a || block.func_149688_o() == Material.field_151584_j) && j > 0) {
            --j;
        }

        Block block1 = world.func_147439_a(i, j, k);

        if (block1 == Blocks.field_150346_d || block1 == Blocks.field_150349_c) {
            ++j;
            this.func_150516_a(world, i, j, k, Blocks.field_150364_r, this.field_150527_b);

            for (int l = j; l <= j + 2; ++l) {
                int i1 = l - j;
                int j1 = 2 - i1;

                for (int k1 = i - j1; k1 <= i + j1; ++k1) {
                    int l1 = k1 - i;

                    for (int i2 = k - j1; i2 <= k + j1; ++i2) {
                        int j2 = i2 - k;

                        if ((Math.abs(l1) != j1 || Math.abs(j2) != j1 || random.nextInt(2) != 0) && !world.func_147439_a(k1, l, i2).func_149730_j()) {
                            this.func_150516_a(world, k1, l, i2, Blocks.field_150362_t, this.field_150528_a);
                        }
                    }
                }
            }
        }

        return true;
    }
}
