package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.world.World;

public class BiomeGenPlains extends BiomeGenBase {

    protected boolean field_150628_aC;

    protected BiomeGenPlains(int i) {
        super(i);
        this.func_76732_a(0.8F, 0.4F);
        this.func_150570_a(BiomeGenPlains.field_150593_e);
        this.field_76762_K.add(new BiomeGenBase$SpawnListEntry(EntityHorse.class, 5, 2, 6));
        this.field_76760_I.field_76832_z = -999;
        this.field_76760_I.field_76802_A = 4;
        this.field_76760_I.field_76803_B = 10;
    }

    @Override
    public String func_150572_a(Random random, int i, int j, int k) {
        double d0 = BiomeGenPlains.field_150606_ad.func_151601_a((double) i / 200.0D, (double) k / 200.0D);
        int l;

        if (d0 < -0.8D) {
            l = random.nextInt(4);
            return BlockFlower.field_149859_a[4 + l];
        } else if (random.nextInt(3) > 0) {
            l = random.nextInt(3);
            return l == 0 ? BlockFlower.field_149859_a[0] : (l == 1 ? BlockFlower.field_149859_a[3] : BlockFlower.field_149859_a[8]);
        } else {
            return BlockFlower.field_149858_b[0];
        }
    }

    @Override
    public void func_76728_a(World world, Random random, int i, int j) {
        double d0 = BiomeGenPlains.field_150606_ad.func_151601_a((double) (i + 8) / 200.0D, (double) (j + 8) / 200.0D);
        int k;
        int l;
        int i1;
        int j1;

        if (d0 < -0.8D) {
            this.field_76760_I.field_76802_A = 15;
            this.field_76760_I.field_76803_B = 5;
        } else {
            this.field_76760_I.field_76802_A = 4;
            this.field_76760_I.field_76803_B = 10;
            BiomeGenPlains.field_150610_ae.func_150548_a(2);

            for (j1 = 0; j1 < 7; ++j1) {
                k = i + random.nextInt(16) + 8;
                l = j + random.nextInt(16) + 8;
                i1 = random.nextInt(world.func_72976_f(k, l) + 32);
                BiomeGenPlains.field_150610_ae.func_76484_a(world, random, k, i1, l);
            }
        }

        if (this.field_150628_aC) {
            BiomeGenPlains.field_150610_ae.func_150548_a(0);

            for (j1 = 0; j1 < 10; ++j1) {
                k = i + random.nextInt(16) + 8;
                l = j + random.nextInt(16) + 8;
                i1 = random.nextInt(world.func_72976_f(k, l) + 32);
                BiomeGenPlains.field_150610_ae.func_76484_a(world, random, k, i1, l);
            }
        }

        super.func_76728_a(world, random, i, j);
    }

    @Override
    protected BiomeGenBase func_150566_k() {
        BiomeGenPlains biomegenplains = new BiomeGenPlains(this.field_76756_M + 128);

        biomegenplains.func_76735_a("Sunflower Plains");
        biomegenplains.field_150628_aC = true;
        biomegenplains.func_76739_b(9286496);
        biomegenplains.field_150609_ah = 14273354;
        return biomegenplains;
    }
}
