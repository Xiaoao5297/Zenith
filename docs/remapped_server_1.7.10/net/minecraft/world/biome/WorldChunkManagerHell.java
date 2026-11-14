package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.world.ChunkPosition;

public class WorldChunkManagerHell extends WorldChunkManager {

    private BiomeGenBase field_76947_d;
    private float field_76946_f;

    public WorldChunkManagerHell(BiomeGenBase biomegenbase, float f) {
        this.field_76947_d = biomegenbase;
        this.field_76946_f = f;
    }

    @Override
    public BiomeGenBase func_76935_a(int i, int j) {
        return this.field_76947_d;
    }

    @Override
    public BiomeGenBase[] func_76937_a(BiomeGenBase[] abiomegenbase, int i, int j, int k, int l) {
        if (abiomegenbase == null || abiomegenbase.length < k * l) {
            abiomegenbase = new BiomeGenBase[k * l];
        }

        Arrays.fill(abiomegenbase, 0, k * l, this.field_76947_d);
        return abiomegenbase;
    }

    @Override
    public float[] func_76936_a(float[] afloat, int i, int j, int k, int l) {
        if (afloat == null || afloat.length < k * l) {
            afloat = new float[k * l];
        }

        Arrays.fill(afloat, 0, k * l, this.field_76946_f);
        return afloat;
    }

    @Override
    public BiomeGenBase[] func_76933_b(BiomeGenBase[] abiomegenbase, int i, int j, int k, int l) {
        if (abiomegenbase == null || abiomegenbase.length < k * l) {
            abiomegenbase = new BiomeGenBase[k * l];
        }

        Arrays.fill(abiomegenbase, 0, k * l, this.field_76947_d);
        return abiomegenbase;
    }

    @Override
    public BiomeGenBase[] func_76931_a(BiomeGenBase[] abiomegenbase, int i, int j, int k, int l, boolean flag) {
        return this.func_76933_b(abiomegenbase, i, j, k, l);
    }

    @Override
    public ChunkPosition func_150795_a(int i, int j, int k, List list, Random random) {
        return list.contains(this.field_76947_d) ? new ChunkPosition(i - k + random.nextInt(k * 2 + 1), 0, j - k + random.nextInt(k * 2 + 1)) : null;
    }

    @Override
    public boolean func_76940_a(int i, int j, int k, List list) {
        return list.contains(this.field_76947_d);
    }
}
