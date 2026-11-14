package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

class BiomeGenForest$1 extends BiomeGenMutated {

    BiomeGenForest$1(BiomeGenForest biomegenforest, int i, BiomeGenBase biomegenbase) {
        super(i, biomegenbase);
        this.field_150612_aC = biomegenforest;
    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return random.nextBoolean() ? BiomeGenForest.field_150629_aC : BiomeGenForest.field_150630_aD;
    }
}
