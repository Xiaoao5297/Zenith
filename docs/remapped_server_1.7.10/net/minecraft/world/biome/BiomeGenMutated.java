package net.minecraft.world.biome;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenMutated extends BiomeGenBase {

    protected BiomeGenBase field_150611_aD;

    public BiomeGenMutated(int i, BiomeGenBase biomegenbase) {
        super(i);
        this.field_150611_aD = biomegenbase;
        this.func_150557_a(biomegenbase.field_76790_z, true);
        this.field_76791_y = biomegenbase.field_76791_y + " M";
        this.field_76752_A = biomegenbase.field_76752_A;
        this.field_76753_B = biomegenbase.field_76753_B;
        this.field_76754_C = biomegenbase.field_76754_C;
        this.field_76748_D = biomegenbase.field_76748_D;
        this.field_76749_E = biomegenbase.field_76749_E;
        this.field_76750_F = biomegenbase.field_76750_F;
        this.field_76751_G = biomegenbase.field_76751_G;
        this.field_76759_H = biomegenbase.field_76759_H;
        this.field_76766_R = biomegenbase.field_76766_R;
        this.field_76765_S = biomegenbase.field_76765_S;
        this.field_76762_K = new ArrayList(biomegenbase.field_76762_K);
        this.field_76761_J = new ArrayList(biomegenbase.field_76761_J);
        this.field_82914_M = new ArrayList(biomegenbase.field_82914_M);
        this.field_76755_L = new ArrayList(biomegenbase.field_76755_L);
        this.field_76750_F = biomegenbase.field_76750_F;
        this.field_76751_G = biomegenbase.field_76751_G;
        this.field_76748_D = biomegenbase.field_76748_D + 0.1F;
        this.field_76749_E = biomegenbase.field_76749_E + 0.2F;
    }

    @Override
    public void func_76728_a(World world, Random random, int i, int j) {
        this.field_150611_aD.field_76760_I.func_150512_a(world, random, this, i, j);
    }

    @Override
    public void func_150573_a(World world, Random random, Block[] ablock, byte[] abyte, int i, int j, double d0) {
        this.field_150611_aD.func_150573_a(world, random, ablock, abyte, i, j, d0);
    }

    @Override
    public float func_76741_f() {
        return this.field_150611_aD.func_76741_f();
    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return this.field_150611_aD.func_150567_a(random);
    }

    @Override
    public Class func_150562_l() {
        return this.field_150611_aD.func_150562_l();
    }

    @Override
    public boolean func_150569_a(BiomeGenBase biomegenbase) {
        return this.field_150611_aD.func_150569_a(biomegenbase);
    }

    @Override
    public BiomeGenBase$TempCategory func_150561_m() {
        return this.field_150611_aD.func_150561_m();
    }
}
