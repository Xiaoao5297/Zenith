package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class BiomeGenOcean extends BiomeGenBase {

    public BiomeGenOcean(int i) {
        super(i);
        this.field_76762_K.clear();
    }

    @Override
    public BiomeGenBase$TempCategory func_150561_m() {
        return BiomeGenBase$TempCategory.OCEAN;
    }

    @Override
    public void func_150573_a(World world, Random random, Block[] ablock, byte[] abyte, int i, int j, double d0) {
        super.func_150573_a(world, random, ablock, abyte, i, j, d0);
    }
}
