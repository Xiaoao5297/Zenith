package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDesertWells;

public class BiomeGenDesert extends BiomeGenBase {

    public BiomeGenDesert(int i) {
        super(i);
        this.field_76762_K.clear();
        this.field_76752_A = Blocks.field_150354_m;
        this.field_76753_B = Blocks.field_150354_m;
        this.field_76760_I.field_76832_z = -999;
        this.field_76760_I.field_76804_C = 2;
        this.field_76760_I.field_76799_E = 50;
        this.field_76760_I.field_76800_F = 10;
        this.field_76762_K.clear();
    }

    @Override
    public void func_76728_a(World world, Random random, int i, int j) {
        super.func_76728_a(world, random, i, j);
        if (random.nextInt(1000) == 0) {
            int k = i + random.nextInt(16) + 8;
            int l = j + random.nextInt(16) + 8;
            WorldGenDesertWells worldgendesertwells = new WorldGenDesertWells();

            worldgendesertwells.func_76484_a(world, random, k, world.func_72976_f(k, l) + 1, l);
        }

    }
}
