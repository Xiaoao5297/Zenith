package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;

public class BiomeGenSavanna extends BiomeGenBase {

    private static final WorldGenSavannaTree field_150627_aC = new WorldGenSavannaTree(false);

    protected BiomeGenSavanna(int i) {
        super(i);
        this.field_76762_K.add(new BiomeGenBase$SpawnListEntry(EntityHorse.class, 1, 2, 6));
        this.field_76760_I.field_76832_z = 1;
        this.field_76760_I.field_76802_A = 4;
        this.field_76760_I.field_76803_B = 20;
    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return (WorldGenAbstractTree) (random.nextInt(5) > 0 ? BiomeGenSavanna.field_150627_aC : this.field_76757_N);
    }

    @Override
    protected BiomeGenBase func_150566_k() {
        BiomeGenSavanna$Mutated biomegensavanna$mutated = new BiomeGenSavanna$Mutated(this.field_76756_M + 128, this);

        biomegensavanna$mutated.field_76750_F = (this.field_76750_F + 1.0F) * 0.5F;
        biomegensavanna$mutated.field_76748_D = this.field_76748_D * 0.5F + 0.3F;
        biomegensavanna$mutated.field_76749_E = this.field_76749_E * 0.5F + 1.2F;
        return biomegensavanna$mutated;
    }

    @Override
    public void func_76728_a(World world, Random random, int i, int j) {
        BiomeGenSavanna.field_150610_ae.func_150548_a(2);

        for (int k = 0; k < 7; ++k) {
            int l = i + random.nextInt(16) + 8;
            int i1 = j + random.nextInt(16) + 8;
            int j1 = random.nextInt(world.func_72976_f(l, i1) + 32);

            BiomeGenSavanna.field_150610_ae.func_76484_a(world, random, l, j1, i1);
        }

        super.func_76728_a(world, random, i, j);
    }
}
