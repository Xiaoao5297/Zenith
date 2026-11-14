package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;

public class BiomeGenForest extends BiomeGenBase {

    private int field_150632_aF;
    protected static final WorldGenForest field_150629_aC = new WorldGenForest(false, true);
    protected static final WorldGenForest field_150630_aD = new WorldGenForest(false, false);
    protected static final WorldGenCanopyTree field_150631_aE = new WorldGenCanopyTree(false);

    public BiomeGenForest(int i, int j) {
        super(i);
        this.field_150632_aF = j;
        this.field_76760_I.field_76832_z = 10;
        this.field_76760_I.field_76803_B = 2;
        if (this.field_150632_aF == 1) {
            this.field_76760_I.field_76832_z = 6;
            this.field_76760_I.field_76802_A = 100;
            this.field_76760_I.field_76803_B = 1;
        }

        this.func_76733_a(5159473);
        this.func_76732_a(0.7F, 0.8F);
        if (this.field_150632_aF == 2) {
            this.field_150609_ah = 353825;
            this.field_76790_z = 3175492;
            this.func_76732_a(0.6F, 0.6F);
        }

        if (this.field_150632_aF == 0) {
            this.field_76762_K.add(new BiomeGenBase$SpawnListEntry(EntityWolf.class, 5, 4, 4));
        }

        if (this.field_150632_aF == 3) {
            this.field_76760_I.field_76832_z = -999;
        }

    }

    @Override
    protected BiomeGenBase func_150557_a(int i, boolean flag) {
        if (this.field_150632_aF == 2) {
            this.field_150609_ah = 353825;
            this.field_76790_z = i;
            if (flag) {
                this.field_150609_ah = (this.field_150609_ah & 16711422) >> 1;
            }

            return this;
        } else {
            return super.func_150557_a(i, flag);
        }
    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return (WorldGenAbstractTree) (this.field_150632_aF == 3 && random.nextInt(3) > 0 ? BiomeGenForest.field_150631_aE : (this.field_150632_aF != 2 && random.nextInt(5) != 0 ? this.field_76757_N : BiomeGenForest.field_150630_aD));
    }

    @Override
    public String func_150572_a(Random random, int i, int j, int k) {
        if (this.field_150632_aF == 1) {
            double d0 = MathHelper.func_151237_a((1.0D + BiomeGenForest.field_150606_ad.func_151601_a((double) i / 48.0D, (double) k / 48.0D)) / 2.0D, 0.0D, 0.9999D);
            int l = (int) (d0 * (double) BlockFlower.field_149859_a.length);

            if (l == 1) {
                l = 0;
            }

            return BlockFlower.field_149859_a[l];
        } else {
            return super.func_150572_a(random, i, j, k);
        }
    }

    @Override
    public void func_76728_a(World world, Random random, int i, int j) {
        int k;
        int l;
        int i1;
        int j1;
        int k1;

        if (this.field_150632_aF == 3) {
            for (l = 0; l < 4; ++l) {
                for (j1 = 0; j1 < 4; ++j1) {
                    k = i + l * 4 + 1 + 8 + random.nextInt(3);
                    i1 = j + j1 * 4 + 1 + 8 + random.nextInt(3);
                    k1 = world.func_72976_f(k, i1);
                    if (random.nextInt(20) == 0) {
                        WorldGenBigMushroom worldgenbigmushroom = new WorldGenBigMushroom();

                        worldgenbigmushroom.func_76484_a(world, random, k, k1, i1);
                    } else {
                        WorldGenAbstractTree worldgenabstracttree = this.func_150567_a(random);

                        worldgenabstracttree.func_76487_a(1.0D, 1.0D, 1.0D);
                        if (worldgenabstracttree.func_76484_a(world, random, k, k1, i1)) {
                            worldgenabstracttree.func_150524_b(world, random, k, k1, i1);
                        }
                    }
                }
            }
        }

        l = random.nextInt(5) - 3;
        if (this.field_150632_aF == 1) {
            l += 2;
        }

        j1 = 0;

        while (j1 < l) {
            k = random.nextInt(3);
            if (k == 0) {
                BiomeGenForest.field_150610_ae.func_150548_a(1);
            } else if (k == 1) {
                BiomeGenForest.field_150610_ae.func_150548_a(4);
            } else if (k == 2) {
                BiomeGenForest.field_150610_ae.func_150548_a(5);
            }

            i1 = 0;

            while (true) {
                if (i1 < 5) {
                    k1 = i + random.nextInt(16) + 8;
                    int l1 = j + random.nextInt(16) + 8;
                    int i2 = random.nextInt(world.func_72976_f(k1, l1) + 32);

                    if (!BiomeGenForest.field_150610_ae.func_76484_a(world, random, k1, i2, l1)) {
                        ++i1;
                        continue;
                    }
                }

                ++j1;
                break;
            }
        }

        super.func_76728_a(world, random, i, j);
    }

    @Override
    protected BiomeGenBase func_150566_k() {
        if (this.field_76756_M == BiomeGenBase.field_76767_f.field_76756_M) {
            BiomeGenForest biomegenforest = new BiomeGenForest(this.field_76756_M + 128, 1);

            biomegenforest.func_150570_a(new BiomeGenBase$Height(this.field_76748_D, this.field_76749_E + 0.2F));
            biomegenforest.func_76735_a("Flower Forest");
            biomegenforest.func_150557_a(6976549, true);
            biomegenforest.func_76733_a(8233509);
            return biomegenforest;
        } else {
            return (BiomeGenBase) (this.field_76756_M != BiomeGenBase.field_150583_P.field_76756_M && this.field_76756_M != BiomeGenBase.field_150582_Q.field_76756_M ? new BiomeGenForest$2(this, this.field_76756_M + 128, this) : new BiomeGenForest$1(this, this.field_76756_M + 128, this));
        }
    }
}
