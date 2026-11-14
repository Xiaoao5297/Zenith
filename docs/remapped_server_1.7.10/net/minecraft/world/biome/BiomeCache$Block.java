package net.minecraft.world.biome;

public class BiomeCache$Block {

    public float[] field_76890_b;
    public BiomeGenBase[] field_76891_c;
    public int field_76888_d;
    public int field_76889_e;
    public long field_76886_f;

    public BiomeCache$Block(BiomeCache biomecache, int i, int j) {
        this.field_76887_g = biomecache;
        this.field_76890_b = new float[256];
        this.field_76891_c = new BiomeGenBase[256];
        this.field_76888_d = i;
        this.field_76889_e = j;
        BiomeCache.access$000(biomecache).func_76936_a(this.field_76890_b, i << 4, j << 4, 16, 16);
        BiomeCache.access$000(biomecache).func_76931_a(this.field_76891_c, i << 4, j << 4, 16, 16, false);
    }

    public BiomeGenBase func_76885_a(int i, int j) {
        return this.field_76891_c[i & 15 | (j & 15) << 4];
    }
}
