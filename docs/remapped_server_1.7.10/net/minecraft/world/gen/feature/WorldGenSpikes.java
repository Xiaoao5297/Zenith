package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenSpikes extends WorldGenerator {

    private Block field_150520_a;

    public WorldGenSpikes(Block block) {
        this.field_150520_a = block;
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        if (world.func_147437_c(i, j, k) && world.func_147439_a(i, j - 1, k) == this.field_150520_a) {
            int l = random.nextInt(32) + 6;
            int i1 = random.nextInt(4) + 1;

            int j1;
            int k1;
            int l1;
            int i2;

            for (k1 = i - i1; k1 <= i + i1; ++k1) {
                for (i2 = k - i1; i2 <= k + i1; ++i2) {
                    j1 = k1 - i;
                    l1 = i2 - k;
                    if (j1 * j1 + l1 * l1 <= i1 * i1 + 1 && world.func_147439_a(k1, j - 1, i2) != this.field_150520_a) {
                        return false;
                    }
                }
            }

            for (k1 = j; k1 < j + l && k1 < 256; ++k1) {
                for (i2 = i - i1; i2 <= i + i1; ++i2) {
                    for (j1 = k - i1; j1 <= k + i1; ++j1) {
                        l1 = i2 - i;
                        int j2 = j1 - k;

                        if (l1 * l1 + j2 * j2 <= i1 * i1 + 1) {
                            world.func_147465_d(i2, k1, j1, Blocks.field_150343_Z, 0, 2);
                        }
                    }
                }
            }

            EntityEnderCrystal entityendercrystal = new EntityEnderCrystal(world);

            entityendercrystal.func_70012_b((double) ((float) i + 0.5F), (double) (j + l), (double) ((float) k + 0.5F), random.nextFloat() * 360.0F, 0.0F);
            world.func_72838_d(entityendercrystal);
            world.func_147465_d(i, j + l, k, Blocks.field_150357_h, 0, 2);
            return true;
        } else {
            return false;
        }
    }
}
