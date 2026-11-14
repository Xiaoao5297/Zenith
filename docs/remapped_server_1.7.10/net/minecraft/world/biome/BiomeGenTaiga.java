package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenTaiga extends BiomeGenBase {

    private static final WorldGenTaiga1 field_150639_aC = new WorldGenTaiga1();
    private static final WorldGenTaiga2 field_150640_aD = new WorldGenTaiga2(false);
    private static final WorldGenMegaPineTree field_150641_aE = new WorldGenMegaPineTree(false, false);
    private static final WorldGenMegaPineTree field_150642_aF = new WorldGenMegaPineTree(false, true);
    private static final WorldGenBlockBlob field_150643_aG = new WorldGenBlockBlob(Blocks.field_150341_Y, 0);
    private int field_150644_aH;

    public BiomeGenTaiga(int i, int j) {
        super(i);
        this.field_150644_aH = j;
        this.field_76762_K.add(new BiomeGenBase$SpawnListEntry(EntityWolf.class, 8, 4, 4));
        this.field_76760_I.field_76832_z = 10;
        if (j != 1 && j != 2) {
            this.field_76760_I.field_76803_B = 1;
            this.field_76760_I.field_76798_D = 1;
        } else {
            this.field_76760_I.field_76803_B = 7;
            this.field_76760_I.field_76804_C = 1;
            this.field_76760_I.field_76798_D = 3;
        }

    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return (WorldGenAbstractTree) ((this.field_150644_aH == 1 || this.field_150644_aH == 2) && random.nextInt(3) == 0 ? (this.field_150644_aH != 2 && random.nextInt(13) != 0 ? BiomeGenTaiga.field_150641_aE : BiomeGenTaiga.field_150642_aF) : (random.nextInt(3) == 0 ? BiomeGenTaiga.field_150639_aC : BiomeGenTaiga.field_150640_aD));
    }

    @Override
    public WorldGenerator func_76730_b(Random random) {
        return random.nextInt(5) > 0 ? new WorldGenTallGrass(Blocks.field_150329_H, 2) : new WorldGenTallGrass(Blocks.field_150329_H, 1);
    }

    @Override
    public void func_76728_a(World world, Random random, int i, int j) {
        int k;
        int l;
        int i1;
        int j1;

        if (this.field_150644_aH == 1 || this.field_150644_aH == 2) {
            k = random.nextInt(3);

            for (j1 = 0; j1 < k; ++j1) {
                l = i + random.nextInt(16) + 8;
                i1 = j + random.nextInt(16) + 8;
                int k1 = world.func_72976_f(l, i1);

                BiomeGenTaiga.field_150643_aG.func_76484_a(world, random, l, k1, i1);
            }
        }

        BiomeGenTaiga.field_150610_ae.func_150548_a(3);

        for (k = 0; k < 7; ++k) {
            j1 = i + random.nextInt(16) + 8;
            l = j + random.nextInt(16) + 8;
            i1 = random.nextInt(world.func_72976_f(j1, l) + 32);
            BiomeGenTaiga.field_150610_ae.func_76484_a(world, random, j1, i1, l);
        }

        super.func_76728_a(world, random, i, j);
    }

    @Override
    public void func_150573_a(World world, Random random, Block[] ablock, byte[] abyte, int i, int j, double d0) {
        if (this.field_150644_aH == 1 || this.field_150644_aH == 2) {
            this.field_76752_A = Blocks.field_150349_c;
            this.field_150604_aj = 0;
            this.field_76753_B = Blocks.field_150346_d;
            if (d0 > 1.75D) {
                this.field_76752_A = Blocks.field_150346_d;
                this.field_150604_aj = 1;
            } else if (d0 > -0.95D) {
                this.field_76752_A = Blocks.field_150346_d;
                this.field_150604_aj = 2;
            }
        }

        this.func_150560_b(world, random, ablock, abyte, i, j, d0);
    }

    @Override
    protected BiomeGenBase func_150566_k() {
        return this.field_76756_M == BiomeGenBase.field_150578_U.field_76756_M ? (new BiomeGenTaiga(this.field_76756_M + 128, 2)).func_150557_a(5858897, true).func_76735_a("Mega Spruce Taiga").func_76733_a(5159473).func_76732_a(0.25F, 0.8F).func_150570_a(new BiomeGenBase$Height(this.field_76748_D, this.field_76749_E)) : super.func_150566_k();
    }
}
