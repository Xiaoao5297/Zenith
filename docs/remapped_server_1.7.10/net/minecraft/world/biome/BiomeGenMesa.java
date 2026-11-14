package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenMesa extends BiomeGenBase {

    private byte[] field_150621_aC;
    private long field_150622_aD;
    private NoiseGeneratorPerlin field_150623_aE;
    private NoiseGeneratorPerlin field_150624_aF;
    private NoiseGeneratorPerlin field_150625_aG;
    private boolean field_150626_aH;
    private boolean field_150620_aI;

    public BiomeGenMesa(int i, boolean flag, boolean flag1) {
        super(i);
        this.field_150626_aH = flag;
        this.field_150620_aI = flag1;
        this.func_76745_m();
        this.func_76732_a(2.0F, 0.0F);
        this.field_76762_K.clear();
        this.field_76752_A = Blocks.field_150354_m;
        this.field_150604_aj = 1;
        this.field_76753_B = Blocks.field_150406_ce;
        this.field_76760_I.field_76832_z = -999;
        this.field_76760_I.field_76804_C = 20;
        this.field_76760_I.field_76799_E = 3;
        this.field_76760_I.field_76800_F = 5;
        this.field_76760_I.field_76802_A = 0;
        this.field_76762_K.clear();
        if (flag1) {
            this.field_76760_I.field_76832_z = 5;
        }

    }

    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return this.field_76757_N;
    }

    @Override
    public void func_76728_a(World world, Random random, int i, int j) {
        super.func_76728_a(world, random, i, j);
    }

    @Override
    public void func_150573_a(World world, Random random, Block[] ablock, byte[] abyte, int i, int j, double d0) {
        if (this.field_150621_aC == null || this.field_150622_aD != world.func_72905_C()) {
            this.func_150619_a(world.func_72905_C());
        }

        if (this.field_150623_aE == null || this.field_150624_aF == null || this.field_150622_aD != world.func_72905_C()) {
            Random random1 = new Random(this.field_150622_aD);

            this.field_150623_aE = new NoiseGeneratorPerlin(random1, 4);
            this.field_150624_aF = new NoiseGeneratorPerlin(random1, 1);
        }

        this.field_150622_aD = world.func_72905_C();
        double d1 = 0.0D;
        int k;
        int l;

        if (this.field_150626_aH) {
            k = (i & -16) + (j & 15);
            l = (j & -16) + (i & 15);
            double d2 = Math.min(Math.abs(d0), this.field_150623_aE.func_151601_a((double) k * 0.25D, (double) l * 0.25D));

            if (d2 > 0.0D) {
                double d3 = 0.001953125D;
                double d4 = Math.abs(this.field_150624_aF.func_151601_a((double) k * d3, (double) l * d3));

                d1 = d2 * d2 * 2.5D;
                double d5 = Math.ceil(d4 * 50.0D) + 14.0D;

                if (d1 > d5) {
                    d1 = d5;
                }

                d1 += 64.0D;
            }
        }

        k = i & 15;
        l = j & 15;
        boolean flag = true;
        Block block = Blocks.field_150406_ce;
        Block block1 = this.field_76753_B;
        int i1 = (int) (d0 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        boolean flag1 = Math.cos(d0 / 3.0D * Math.PI) > 0.0D;
        int j1 = -1;
        boolean flag2 = false;
        int k1 = ablock.length / 256;

        for (int l1 = 255; l1 >= 0; --l1) {
            int i2 = (l * 16 + k) * k1 + l1;

            if ((ablock[i2] == null || ablock[i2].func_149688_o() == Material.field_151579_a) && l1 < (int) d1) {
                ablock[i2] = Blocks.field_150348_b;
            }

            if (l1 <= 0 + random.nextInt(5)) {
                ablock[i2] = Blocks.field_150357_h;
            } else {
                Block block2 = ablock[i2];

                if (block2 != null && block2.func_149688_o() != Material.field_151579_a) {
                    if (block2 == Blocks.field_150348_b) {
                        byte b0;

                        if (j1 == -1) {
                            flag2 = false;
                            if (i1 <= 0) {
                                block = null;
                                block1 = Blocks.field_150348_b;
                            } else if (l1 >= 59 && l1 <= 64) {
                                block = Blocks.field_150406_ce;
                                block1 = this.field_76753_B;
                            }

                            if (l1 < 63 && (block == null || block.func_149688_o() == Material.field_151579_a)) {
                                block = Blocks.field_150355_j;
                            }

                            j1 = i1 + Math.max(0, l1 - 63);
                            if (l1 >= 62) {
                                if (this.field_150620_aI && l1 > 86 + i1 * 2) {
                                    if (flag1) {
                                        ablock[i2] = Blocks.field_150346_d;
                                        abyte[i2] = 1;
                                    } else {
                                        ablock[i2] = Blocks.field_150349_c;
                                    }
                                } else if (l1 > 66 + i1) {
                                    b0 = 16;
                                    if (l1 >= 64 && l1 <= 127) {
                                        if (!flag1) {
                                            b0 = this.func_150618_d(i, l1, j);
                                        }
                                    } else {
                                        b0 = 1;
                                    }

                                    if (b0 < 16) {
                                        ablock[i2] = Blocks.field_150406_ce;
                                        abyte[i2] = (byte) b0;
                                    } else {
                                        ablock[i2] = Blocks.field_150405_ch;
                                    }
                                } else {
                                    ablock[i2] = this.field_76752_A;
                                    abyte[i2] = (byte) this.field_150604_aj;
                                    flag2 = true;
                                }
                            } else {
                                ablock[i2] = block1;
                                if (block1 == Blocks.field_150406_ce) {
                                    abyte[i2] = 1;
                                }
                            }
                        } else if (j1 > 0) {
                            --j1;
                            if (flag2) {
                                ablock[i2] = Blocks.field_150406_ce;
                                abyte[i2] = 1;
                            } else {
                                b0 = this.func_150618_d(i, l1, j);
                                if (b0 < 16) {
                                    ablock[i2] = Blocks.field_150406_ce;
                                    abyte[i2] = b0;
                                } else {
                                    ablock[i2] = Blocks.field_150405_ch;
                                }
                            }
                        }
                    }
                } else {
                    j1 = -1;
                }
            }
        }

    }

    private void func_150619_a(long i) {
        this.field_150621_aC = new byte[64];
        Arrays.fill(this.field_150621_aC, (byte) 16);
        Random random = new Random(i);

        this.field_150625_aG = new NoiseGeneratorPerlin(random, 1);

        int j;

        for (j = 0; j < 64; ++j) {
            j += random.nextInt(5) + 1;
            if (j < 64) {
                this.field_150621_aC[j] = 1;
            }
        }

        j = random.nextInt(4) + 2;

        int k;
        int l;
        int i1;
        int j1;

        for (j1 = 0; j1 < j; ++j1) {
            k = random.nextInt(3) + 1;
            l = random.nextInt(64);

            for (i1 = 0; l + i1 < 64 && i1 < k; ++i1) {
                this.field_150621_aC[l + i1] = 4;
            }
        }

        j1 = random.nextInt(4) + 2;

        int k1;

        for (k = 0; k < j1; ++k) {
            l = random.nextInt(3) + 2;
            i1 = random.nextInt(64);

            for (k1 = 0; i1 + k1 < 64 && k1 < l; ++k1) {
                this.field_150621_aC[i1 + k1] = 12;
            }
        }

        k = random.nextInt(4) + 2;

        for (l = 0; l < k; ++l) {
            i1 = random.nextInt(3) + 1;
            k1 = random.nextInt(64);

            for (int l1 = 0; k1 + l1 < 64 && l1 < i1; ++l1) {
                this.field_150621_aC[k1 + l1] = 14;
            }
        }

        l = random.nextInt(3) + 3;
        i1 = 0;

        for (k1 = 0; k1 < l; ++k1) {
            byte b0 = 1;

            i1 += random.nextInt(16) + 4;

            for (int i2 = 0; i1 + i2 < 64 && i2 < b0; ++i2) {
                this.field_150621_aC[i1 + i2] = 0;
                if (i1 + i2 > 1 && random.nextBoolean()) {
                    this.field_150621_aC[i1 + i2 - 1] = 8;
                }

                if (i1 + i2 < 63 && random.nextBoolean()) {
                    this.field_150621_aC[i1 + i2 + 1] = 8;
                }
            }
        }

    }

    private byte func_150618_d(int i, int j, int k) {
        int l = (int) Math.round(this.field_150625_aG.func_151601_a((double) i * 1.0D / 512.0D, (double) i * 1.0D / 512.0D) * 2.0D);

        return this.field_150621_aC[(j + l + 64) % 64];
    }

    @Override
    protected BiomeGenBase func_150566_k() {
        boolean flag = this.field_76756_M == BiomeGenBase.field_150589_Z.field_76756_M;
        BiomeGenMesa biomegenmesa = new BiomeGenMesa(this.field_76756_M + 128, flag, this.field_150620_aI);

        if (!flag) {
            biomegenmesa.func_150570_a(BiomeGenMesa.field_150591_g);
            biomegenmesa.func_76735_a(this.field_76791_y + " M");
        } else {
            biomegenmesa.func_76735_a(this.field_76791_y + " (Bryce)");
        }

        biomegenmesa.func_150557_a(this.field_76790_z, true);
        return biomegenmesa;
    }
}
