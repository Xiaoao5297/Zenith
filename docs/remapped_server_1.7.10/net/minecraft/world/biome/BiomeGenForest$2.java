package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.world.World;

class BiomeGenForest$2 extends BiomeGenMutated {

    BiomeGenForest$2(BiomeGenForest biomegenforest, int i, BiomeGenBase biomegenbase) {
        super(i, biomegenbase);
        this.field_150613_aC = biomegenforest;
    }

    @Override
    public void func_76728_a(World world, Random random, int i, int j) {
        this.field_150611_aD.func_76728_a(world, random, i, j);
    }
}
