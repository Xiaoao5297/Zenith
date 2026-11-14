package net.minecraft.world.biome;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LongHashMap;

public class BiomeCache {

    private final WorldChunkManager field_76844_a;
    private long field_76842_b;
    private LongHashMap field_76843_c = new LongHashMap();
    private List field_76841_d = new ArrayList();

    public BiomeCache(WorldChunkManager worldchunkmanager) {
        this.field_76844_a = worldchunkmanager;
    }

    public BiomeCache$Block func_76840_a(int i, int j) {
        i >>= 4;
        j >>= 4;
        long k = (long) i & 4294967295L | ((long) j & 4294967295L) << 32;
        BiomeCache$Block biomecache$block = (BiomeCache$Block) this.field_76843_c.func_76164_a(k);

        if (biomecache$block == null) {
            biomecache$block = new BiomeCache$Block(this, i, j);
            this.field_76843_c.func_76163_a(k, biomecache$block);
            this.field_76841_d.add(biomecache$block);
        }

        biomecache$block.field_76886_f = MinecraftServer.func_130071_aq();
        return biomecache$block;
    }

    public BiomeGenBase func_76837_b(int i, int j) {
        return this.func_76840_a(i, j).func_76885_a(i, j);
    }

    public void func_76838_a() {
        long i = MinecraftServer.func_130071_aq();
        long j = i - this.field_76842_b;

        if (j > 7500L || j < 0L) {
            this.field_76842_b = i;

            for (int k = 0; k < this.field_76841_d.size(); ++k) {
                BiomeCache$Block biomecache$block = (BiomeCache$Block) this.field_76841_d.get(k);
                long l = i - biomecache$block.field_76886_f;

                if (l > 30000L || l < 0L) {
                    this.field_76841_d.remove(k--);
                    long i1 = (long) biomecache$block.field_76888_d & 4294967295L | ((long) biomecache$block.field_76889_e & 4294967295L) << 32;

                    this.field_76843_c.func_76159_d(i1);
                }
            }
        }

    }

    public BiomeGenBase[] func_76839_e(int i, int j) {
        return this.func_76840_a(i, j).field_76891_c;
    }
}
