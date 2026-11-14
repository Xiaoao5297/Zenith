package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderEnd implements IChunkProvider {

    private Random field_73204_i;
    private NoiseGeneratorOctaves field_73201_j;
    private NoiseGeneratorOctaves field_73202_k;
    private NoiseGeneratorOctaves field_73199_l;
    public NoiseGeneratorOctaves field_73196_a;
    public NoiseGeneratorOctaves field_73194_b;
    private World field_73200_m;
    private double[] field_73197_n;
    private BiomeGenBase[] field_73198_o;
    double[] field_73195_c;
    double[] field_73192_d;
    double[] field_73193_e;
    double[] field_73190_f;
    double[] field_73191_g;
    int[][] field_73203_h = new int[32][32];

    public ChunkProviderEnd(World world, long i) {
        this.field_73200_m = world;
        this.field_73204_i = new Random(i);
        this.field_73201_j = new NoiseGeneratorOctaves(this.field_73204_i, 16);
        this.field_73202_k = new NoiseGeneratorOctaves(this.field_73204_i, 16);
        this.field_73199_l = new NoiseGeneratorOctaves(this.field_73204_i, 8);
        this.field_73196_a = new NoiseGeneratorOctaves(this.field_73204_i, 10);
        this.field_73194_b = new NoiseGeneratorOctaves(this.field_73204_i, 16);
    }

    public void func_147420_a(int i, int j, Block[] ablock, BiomeGenBase[] abiomegenbase) {
        byte b0 = 2;
        int k = b0 + 1;
        byte b1 = 33;
        int l = b0 + 1;

        this.field_73197_n = this.func_73187_a(this.field_73197_n, i * b0, 0, j * b0, k, b1, l);

        for (int i1 = 0; i1 < b0; ++i1) {
            for (int j1 = 0; j1 < b0; ++j1) {
                for (int k1 = 0; k1 < 32; ++k1) {
                    double d0 = 0.25D;
                    double d1 = this.field_73197_n[((i1 + 0) * l + j1 + 0) * b1 + k1 + 0];
                    double d2 = this.field_73197_n[((i1 + 0) * l + j1 + 1) * b1 + k1 + 0];
                    double d3 = this.field_73197_n[((i1 + 1) * l + j1 + 0) * b1 + k1 + 0];
                    double d4 = this.field_73197_n[((i1 + 1) * l + j1 + 1) * b1 + k1 + 0];
                    double d5 = (this.field_73197_n[((i1 + 0) * l + j1 + 0) * b1 + k1 + 1] - d1) * d0;
                    double d6 = (this.field_73197_n[((i1 + 0) * l + j1 + 1) * b1 + k1 + 1] - d2) * d0;
                    double d7 = (this.field_73197_n[((i1 + 1) * l + j1 + 0) * b1 + k1 + 1] - d3) * d0;
                    double d8 = (this.field_73197_n[((i1 + 1) * l + j1 + 1) * b1 + k1 + 1] - d4) * d0;

                    for (int l1 = 0; l1 < 4; ++l1) {
                        double d9 = 0.125D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;

                        for (int i2 = 0; i2 < 8; ++i2) {
                            int j2 = i2 + i1 * 8 << 11 | 0 + j1 * 8 << 7 | k1 * 4 + l1;
                            short short0 = 128;
                            double d14 = 0.125D;
                            double d15 = d10;
                            double d16 = (d11 - d10) * d14;

                            for (int k2 = 0; k2 < 8; ++k2) {
                                Block block = null;

                                if (d15 > 0.0D) {
                                    block = Blocks.field_150377_bs;
                                }

                                ablock[j2] = block;
                                j2 += short0;
                                d15 += d16;
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }

    }

    public void func_147421_b(int i, int j, Block[] ablock, BiomeGenBase[] abiomegenbase) {
        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                byte b0 = 1;
                int i1 = -1;
                Block block = Blocks.field_150377_bs;
                Block block1 = Blocks.field_150377_bs;

                for (int j1 = 127; j1 >= 0; --j1) {
                    int k1 = (l * 16 + k) * 128 + j1;
                    Block block2 = ablock[k1];

                    if (block2 != null && block2.func_149688_o() != Material.field_151579_a) {
                        if (block2 == Blocks.field_150348_b) {
                            if (i1 == -1) {
                                if (b0 <= 0) {
                                    block = null;
                                    block1 = Blocks.field_150377_bs;
                                }

                                i1 = b0;
                                if (j1 >= 0) {
                                    ablock[k1] = block;
                                } else {
                                    ablock[k1] = block1;
                                }
                            } else if (i1 > 0) {
                                --i1;
                                ablock[k1] = block1;
                            }
                        }
                    } else {
                        i1 = -1;
                    }
                }
            }
        }

    }

    @Override
    public Chunk func_73158_c(int i, int j) {
        return this.func_73154_d(i, j);
    }

    @Override
    public Chunk func_73154_d(int i, int j) {
        this.field_73204_i.setSeed((long) i * 341873128712L + (long) j * 132897987541L);
        Block[] ablock = new Block['\u8000'];

        this.field_73198_o = this.field_73200_m.func_72959_q().func_76933_b(this.field_73198_o, i * 16, j * 16, 16, 16);
        this.func_147420_a(i, j, ablock, this.field_73198_o);
        this.func_147421_b(i, j, ablock, this.field_73198_o);
        Chunk chunk = new Chunk(this.field_73200_m, ablock, i, j);
        byte[] abyte = chunk.func_76605_m();

        for (int k = 0; k < abyte.length; ++k) {
            abyte[k] = (byte) this.field_73198_o[k].field_76756_M;
        }

        chunk.func_76603_b();
        return chunk;
    }

    private double[] func_73187_a(double[] adouble, int i, int j, int k, int l, int i1, int j1) {
        if (adouble == null) {
            adouble = new double[l * i1 * j1];
        }

        double d0 = 684.412D;
        double d1 = 684.412D;

        this.field_73190_f = this.field_73196_a.func_76305_a(this.field_73190_f, i, k, l, j1, 1.121D, 1.121D, 0.5D);
        this.field_73191_g = this.field_73194_b.func_76305_a(this.field_73191_g, i, k, l, j1, 200.0D, 200.0D, 0.5D);
        d0 *= 2.0D;
        this.field_73195_c = this.field_73199_l.func_76304_a(this.field_73195_c, i, j, k, l, i1, j1, d0 / 80.0D, d1 / 160.0D, d0 / 80.0D);
        this.field_73192_d = this.field_73201_j.func_76304_a(this.field_73192_d, i, j, k, l, i1, j1, d0, d1, d0);
        this.field_73193_e = this.field_73202_k.func_76304_a(this.field_73193_e, i, j, k, l, i1, j1, d0, d1, d0);
        int k1 = 0;
        int l1 = 0;

        for (int i2 = 0; i2 < l; ++i2) {
            for (int j2 = 0; j2 < j1; ++j2) {
                double d2 = (this.field_73190_f[l1] + 256.0D) / 512.0D;

                if (d2 > 1.0D) {
                    d2 = 1.0D;
                }

                double d3 = this.field_73191_g[l1] / 8000.0D;

                if (d3 < 0.0D) {
                    d3 = -d3 * 0.3D;
                }

                d3 = d3 * 3.0D - 2.0D;
                float f = (float) (i2 + i - 0) / 1.0F;
                float f1 = (float) (j2 + k - 0) / 1.0F;
                float f2 = 100.0F - MathHelper.func_76129_c(f * f + f1 * f1) * 8.0F;

                if (f2 > 80.0F) {
                    f2 = 80.0F;
                }

                if (f2 < -100.0F) {
                    f2 = -100.0F;
                }

                if (d3 > 1.0D) {
                    d3 = 1.0D;
                }

                d3 /= 8.0D;
                d3 = 0.0D;
                if (d2 < 0.0D) {
                    d2 = 0.0D;
                }

                d2 += 0.5D;
                d3 = d3 * (double) i1 / 16.0D;
                ++l1;
                double d4 = (double) i1 / 2.0D;

                for (int k2 = 0; k2 < i1; ++k2) {
                    double d5 = 0.0D;
                    double d6 = ((double) k2 - d4) * 8.0D / d2;

                    if (d6 < 0.0D) {
                        d6 *= -1.0D;
                    }

                    double d7 = this.field_73192_d[k1] / 512.0D;
                    double d8 = this.field_73193_e[k1] / 512.0D;
                    double d9 = (this.field_73195_c[k1] / 10.0D + 1.0D) / 2.0D;

                    if (d9 < 0.0D) {
                        d5 = d7;
                    } else if (d9 > 1.0D) {
                        d5 = d8;
                    } else {
                        d5 = d7 + (d8 - d7) * d9;
                    }

                    d5 -= 8.0D;
                    d5 += (double) f2;
                    byte b0 = 2;
                    double d10;

                    if (k2 > i1 / 2 - b0) {
                        d10 = (double) ((float) (k2 - (i1 / 2 - b0)) / 64.0F);
                        if (d10 < 0.0D) {
                            d10 = 0.0D;
                        }

                        if (d10 > 1.0D) {
                            d10 = 1.0D;
                        }

                        d5 = d5 * (1.0D - d10) + -3000.0D * d10;
                    }

                    b0 = 8;
                    if (k2 < b0) {
                        d10 = (double) ((float) (b0 - k2) / ((float) b0 - 1.0F));
                        d5 = d5 * (1.0D - d10) + -30.0D * d10;
                    }

                    adouble[k1] = d5;
                    ++k1;
                }
            }
        }

        return adouble;
    }

    @Override
    public boolean func_73149_a(int i, int j) {
        return true;
    }

    @Override
    public void func_73153_a(IChunkProvider ichunkprovider, int i, int j) {
        BlockFalling.field_149832_M = true;
        int k = i * 16;
        int l = j * 16;
        BiomeGenBase biomegenbase = this.field_73200_m.func_72807_a(k + 16, l + 16);

        biomegenbase.func_76728_a(this.field_73200_m, this.field_73200_m.field_73012_v, k, l);
        BlockFalling.field_149832_M = false;
    }

    @Override
    public boolean func_73151_a(boolean flag, IProgressUpdate iprogressupdate) {
        return true;
    }

    @Override
    public void func_104112_b() {}

    @Override
    public boolean func_73156_b() {
        return false;
    }

    @Override
    public boolean func_73157_c() {
        return true;
    }

    @Override
    public String func_73148_d() {
        return "RandomLevelSource";
    }

    @Override
    public List func_73155_a(EnumCreatureType enumcreaturetype, int i, int j, int k) {
        BiomeGenBase biomegenbase = this.field_73200_m.func_72807_a(i, k);

        return biomegenbase.func_76747_a(enumcreaturetype);
    }

    @Override
    public ChunkPosition func_147416_a(World world, String s, int i, int j, int k) {
        return null;
    }

    @Override
    public int func_73152_e() {
        return 0;
    }

    @Override
    public void func_82695_e(int i, int j) {}
}
