package net.minecraft.world.gen.layer;

import java.util.concurrent.Callable;
import net.minecraft.world.biome.BiomeGenBase;

final class GenLayer$2 implements Callable {

    GenLayer$2(int i) {
        this.field_151682_a = i;
    }

    public String call() {
        return String.valueOf(BiomeGenBase.func_150568_d(this.field_151682_a));
    }
}
