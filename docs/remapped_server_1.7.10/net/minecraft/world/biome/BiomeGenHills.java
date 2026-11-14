package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenHills extends BiomeGenBase {

    private WorldGenerator field_82915_S;
    private WorldGenTaiga2 field_150634_aD;
    private int field_150635_aE;
    private int field_150636_aF;
    private int field_150637_aG;
    private int field_150638_aH;

    protected BiomeGenHills(int i, boolean flag) {
        super(i);
        this.field_82915_S = new WorldGenMinable(Blocks.field_150418_aU, 8);
        this.field_150634_aD = new WorldGenTaiga2(false);
        this.field_150635_aE = 0;
        this.field_150636_aF = 1;
        this.field_150637_aG = 2;
        this.field_150638_aH = this.field_150635_aE;
        if (flag) {
            this.field_76760_I.field_76832_z = 3;
            this.field_150638_aH = this.field_150636_aF;
        }

    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return (WorldGenAbstractTree) (random.nextInt(3) > 0 ? this.field_150634_aD : super.func_150567_a(random));
    }

    @Override
    public void func_76728_a(World world, Random random, int i, int j) {
        super.func_76728_a(world, random, i, j);
        int k = 3 + random.nextInt(6);

        int l;
        int i1;
        int j1;

        for (j1 = 0; j1 < k; ++j1) {
            l = i + random.nextInt(16);
            i1 = random.nextInt(28) + 4;
            int k1 = j + random.nextInt(16);

            if (world.func_147439_a(l, i1, k1) == Blocks.field_150348_b) {
                world.func_147465_d(l, i1, k1, Blocks.field_150412_bA, 0, 2);
            }
        }

        for (k = 0; k < 7; ++k) {
            j1 = i + random.nextInt(16);
            l = random.nextInt(64);
            i1 = j + random.nextInt(16);
            this.field_82915_S.func_76484_a(world, random, j1, l, i1);
        }

    }

    @Override
    public void func_150573_a(World world, Random random, Block[] ablock, byte[] abyte, int i, int j, double d0) {
        this.field_76752_A = Blocks.field_150349_c;
        this.field_150604_aj = 0;
        this.field_76753_B = Blocks.field_150346_d;
        if ((d0 < -1.0D || d0 > 2.0D) && this.field_150638_aH == this.field_150637_aG) {
            this.field_76752_A = Blocks.field_150351_n;
            this.field_76753_B = Blocks.field_150351_n;
        } else if (d0 > 1.0D && this.field_150638_aH != this.field_150636_aF) {
            this.field_76752_A = Blocks.field_150348_b;
            this.field_76753_B = Blocks.field_150348_b;
        }

        this.func_150560_b(world, random, ablock, abyte, i, j, d0);
    }

    private BiomeGenHills func_150633_b(BiomeGenBase biomegenbase) {
        this.field_150638_aH = this.field_150637_aG;
        this.func_150557_a(biomegenbase.field_76790_z, true);
        this.func_76735_a(biomegenbase.field_76791_y + " M");
        this.func_150570_a(new BiomeGenBase$Height(biomegenbase.field_76748_D, biomegenbase.field_76749_E));
        this.func_76732_a(biomegenbase.field_76750_F, biomegenbase.field_76751_G);
        return this;
    }

    @Override
    protected BiomeGenBase func_150566_k() {
        return (new BiomeGenHills(this.field_76756_M + 128, false)).func_150633_b(this);
    }
}
