package net.minecraft.world;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;

public class WorldProviderEnd extends WorldProvider {

    public WorldProviderEnd() {}

    @Override
    public void func_76572_b() {
        this.field_76578_c = new WorldChunkManagerHell(BiomeGenBase.field_76779_k, 0.0F);
        this.field_76574_g = 1;
        this.field_76576_e = true;
    }

    @Override
    public IChunkProvider func_76555_c() {
        return new ChunkProviderEnd(this.field_76579_a, this.field_76579_a.func_72905_C());
    }

    @Override
    public float func_76563_a(long i, float f) {
        return 0.0F;
    }

    @Override
    public boolean func_76567_e() {
        return false;
    }

    @Override
    public boolean func_76569_d() {
        return false;
    }

    @Override
    public boolean func_76566_a(int i, int j) {
        return this.field_76579_a.func_147474_b(i, j).func_149688_o().func_76230_c();
    }

    @Override
    public ChunkCoordinates func_76554_h() {
        return new ChunkCoordinates(100, 50, 0);
    }

    @Override
    public int func_76557_i() {
        return 50;
    }

    @Override
    public String func_80007_l() {
        return "The End";
    }
}
