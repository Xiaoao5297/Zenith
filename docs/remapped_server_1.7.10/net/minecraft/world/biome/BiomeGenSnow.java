package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenIcePath;
import net.minecraft.world.gen.feature.WorldGenIceSpike;
import net.minecraft.world.gen.feature.WorldGenTaiga2;

public class BiomeGenSnow extends BiomeGenBase {

    private boolean field_150615_aC;
    private WorldGenIceSpike field_150616_aD = new WorldGenIceSpike();
    private WorldGenIcePath field_150617_aE = new WorldGenIcePath(4);

    public BiomeGenSnow(int i, boolean flag) {
        super(i);
        this.field_150615_aC = flag;
        if (flag) {
            this.field_76752_A = Blocks.field_150433_aE;
        }

        this.field_76762_K.clear();
    }

    @Override
    public void func_76728_a(World world, Random random, int i, int j) {
        if (this.field_150615_aC) {
            int k;
            int l;
            int i1;

            for (i1 = 0; i1 < 3; ++i1) {
                k = i + random.nextInt(16) + 8;
                l = j + random.nextInt(16) + 8;
                this.field_150616_aD.func_76484_a(world, random, k, world.func_72976_f(k, l), l);
            }

            for (i1 = 0; i1 < 2; ++i1) {
                k = i + random.nextInt(16) + 8;
                l = j + random.nextInt(16) + 8;
                this.field_150617_aE.func_76484_a(world, random, k, world.func_72976_f(k, l), l);
            }
        }

        super.func_76728_a(world, random, i, j);
    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return new WorldGenTaiga2(false);
    }

    @Override
    protected BiomeGenBase func_150566_k() {
        BiomeGenBase biomegenbase = (new BiomeGenSnow(this.field_76756_M + 128, true)).func_150557_a(13828095, true).func_76735_a(this.field_76791_y + " Spikes").func_76742_b().func_76732_a(0.0F, 0.5F).func_150570_a(new BiomeGenBase$Height(this.field_76748_D + 0.1F, this.field_76749_E + 0.1F));

        biomegenbase.field_76748_D = this.field_76748_D + 0.3F;
        biomegenbase.field_76749_E = this.field_76749_E + 0.4F;
        return biomegenbase;
    }
}
