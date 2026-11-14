package net.minecraft.world;

import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.FlatGeneratorInfo;

public abstract class WorldProvider {

    public static final float[] field_111203_a = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
    public World field_76579_a;
    public WorldType field_76577_b;
    public String field_82913_c;
    public WorldChunkManager field_76578_c;
    public boolean field_76575_d;
    public boolean field_76576_e;
    public float[] field_76573_f = new float[16];
    public int field_76574_g;
    private float[] field_76580_h = new float[4];

    public WorldProvider() {}

    public final void func_76558_a(World world) {
        this.field_76579_a = world;
        this.field_76577_b = world.func_72912_H().func_76067_t();
        this.field_82913_c = world.func_72912_H().func_82571_y();
        this.func_76572_b();
        this.func_76556_a();
    }

    protected void func_76556_a() {
        float f = 0.0F;

        for (int i = 0; i <= 15; ++i) {
            float f1 = 1.0F - (float) i / 15.0F;

            this.field_76573_f[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
        }

    }

    protected void func_76572_b() {
        if (this.field_76579_a.func_72912_H().func_76067_t() == WorldType.field_77138_c) {
            FlatGeneratorInfo flatgeneratorinfo = FlatGeneratorInfo.func_82651_a(this.field_76579_a.func_72912_H().func_82571_y());

            this.field_76578_c = new WorldChunkManagerHell(BiomeGenBase.func_150568_d(flatgeneratorinfo.func_82648_a()), 0.5F);
        } else {
            this.field_76578_c = new WorldChunkManager(this.field_76579_a);
        }

    }

    public IChunkProvider func_76555_c() {
        return (IChunkProvider) (this.field_76577_b == WorldType.field_77138_c ? new ChunkProviderFlat(this.field_76579_a, this.field_76579_a.func_72905_C(), this.field_76579_a.func_72912_H().func_76089_r(), this.field_82913_c) : new ChunkProviderGenerate(this.field_76579_a, this.field_76579_a.func_72905_C(), this.field_76579_a.func_72912_H().func_76089_r()));
    }

    public boolean func_76566_a(int i, int j) {
        return this.field_76579_a.func_147474_b(i, j) == Blocks.field_150349_c;
    }

    public float func_76563_a(long i, float f) {
        int j = (int) (i % 24000L);
        float f1 = ((float) j + f) / 24000.0F - 0.25F;

        if (f1 < 0.0F) {
            ++f1;
        }

        if (f1 > 1.0F) {
            --f1;
        }

        float f2 = f1;

        f1 = 1.0F - (float) ((Math.cos((double) f1 * Math.PI) + 1.0D) / 2.0D);
        f1 = f2 + (f1 - f2) / 3.0F;
        return f1;
    }

    public int func_76559_b(long i) {
        return (int) (i / 24000L % 8L + 8L) % 8;
    }

    public boolean func_76569_d() {
        return true;
    }

    public boolean func_76567_e() {
        return true;
    }

    public static WorldProvider func_76570_a(int i) {
        return (WorldProvider) (i == -1 ? new WorldProviderHell() : (i == 0 ? new WorldProviderSurface() : (i == 1 ? new WorldProviderEnd() : null)));
    }

    public ChunkCoordinates func_76554_h() {
        return null;
    }

    public int func_76557_i() {
        return this.field_76577_b == WorldType.field_77138_c ? 4 : 64;
    }

    public abstract String func_80007_l();
}
