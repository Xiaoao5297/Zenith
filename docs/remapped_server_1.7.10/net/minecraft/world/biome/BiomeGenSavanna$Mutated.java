package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BiomeGenSavanna$Mutated extends BiomeGenMutated {

    public BiomeGenSavanna$Mutated(int i, BiomeGenBase biomegenbase) {
        super(i, biomegenbase);
        this.field_76760_I.field_76832_z = 2;
        this.field_76760_I.field_76802_A = 2;
        this.field_76760_I.field_76803_B = 5;
    }

    @Override
    public void func_150573_a(World world, Random random, Block[] ablock, byte[] abyte, int i, int j, double d0) {
        this.field_76752_A = Blocks.field_150349_c;
        this.field_150604_aj = 0;
        this.field_76753_B = Blocks.field_150346_d;
        if (d0 > 1.75D) {
            this.field_76752_A = Blocks.field_150348_b;
            this.field_76753_B = Blocks.field_150348_b;
        } else if (d0 > -0.5D) {
            this.field_76752_A = Blocks.field_150346_d;
            this.field_150604_aj = 1;
        }

        this.func_150560_b(world, random, ablock, abyte, i, j, d0);
    }

    @Override
    public void func_76728_a(World world, Random random, int i, int j) {
        this.field_76760_I.func_150512_a(world, random, this, i, j);
    }
}
