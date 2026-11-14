package net.minecraft.world;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderHell;

public class WorldProviderHell extends WorldProvider {

    public WorldProviderHell() {}

    @Override
    public void func_76572_b() {
        this.field_76578_c = new WorldChunkManagerHell(BiomeGenBase.field_76778_j, 0.0F);
        this.field_76575_d = true;
        this.field_76576_e = true;
        this.field_76574_g = -1;
    }

    @Override
    protected void func_76556_a() {
        float f = 0.1F;

        for (int i = 0; i <= 15; ++i) {
            float f1 = 1.0F - (float) i / 15.0F;

            this.field_76573_f[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
        }

    }

    @Override
    public IChunkProvider func_76555_c() {
        return new ChunkProviderHell(this.field_76579_a, this.field_76579_a.func_72905_C());
    }

    @Override
    public boolean func_76569_d() {
        return false;
    }

    @Override
    public boolean func_76566_a(int i, int j) {
        return false;
    }

    @Override
    public float func_76563_a(long i, float f) {
        return 0.5F;
    }

    @Override
    public boolean func_76567_e() {
        return false;
    }

    @Override
    public String func_80007_l() {
        return "Nether";
    }
}
