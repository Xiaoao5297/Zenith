package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenBigTree extends WorldGenAbstractTree {

    static final byte[] field_76507_a = new byte[]{2, 0, 0, 1, 2, 1};
    Random field_76505_b = new Random();
    World field_76506_c;
    int[] field_76503_d = new int[]{0, 0, 0};
    int field_76504_e;
    int field_76501_f;
    double field_76502_g = 0.618D;
    double field_76514_h = 1.0D;
    double field_76515_i = 0.381D;
    double field_76512_j = 1.0D;
    double field_76513_k = 1.0D;
    int field_76510_l = 1;
    int field_76511_m = 12;
    int field_76508_n = 4;
    int[][] field_76509_o;

    public WorldGenBigTree(boolean flag) {
        super(flag);
    }

    void func_76489_a() {
        this.field_76501_f = (int) ((double) this.field_76504_e * this.field_76502_g);
        if (this.field_76501_f >= this.field_76504_e) {
            this.field_76501_f = this.field_76504_e - 1;
        }

        int i = (int) (1.382D + Math.pow(this.field_76513_k * (double) this.field_76504_e / 13.0D, 2.0D));

        if (i < 1) {
            i = 1;
        }

        int[][] aint = new int[i * this.field_76504_e][4];
        int j = this.field_76503_d[1] + this.field_76504_e - this.field_76508_n;
        int k = 1;
        int l = this.field_76503_d[1] + this.field_76501_f;
        int i1 = j - this.field_76503_d[1];

        aint[0][0] = this.field_76503_d[0];
        aint[0][1] = j;
        aint[0][2] = this.field_76503_d[2];
        aint[0][3] = l;
        --j;

        while (i1 >= 0) {
            int j1 = 0;
            float f = this.func_76490_a(i1);

            if (f < 0.0F) {
                --j;
                --i1;
            } else {
                for (double d0 = 0.5D; j1 < i; ++j1) {
                    double d1 = this.field_76512_j * (double) f * ((double) this.field_76505_b.nextFloat() + 0.328D);
                    double d2 = (double) this.field_76505_b.nextFloat() * 2.0D * 3.14159D;
                    int k1 = MathHelper.func_76128_c(d1 * Math.sin(d2) + (double) this.field_76503_d[0] + d0);
                    int l1 = MathHelper.func_76128_c(d1 * Math.cos(d2) + (double) this.field_76503_d[2] + d0);
                    int[] aint1 = new int[]{k1, j, l1};
                    int[] aint2 = new int[]{k1, j + this.field_76508_n, l1};

                    if (this.func_76496_a(aint1, aint2) == -1) {
                        int[] aint3 = new int[]{this.field_76503_d[0], this.field_76503_d[1], this.field_76503_d[2]};
                        double d3 = Math.sqrt(Math.pow((double) Math.abs(this.field_76503_d[0] - aint1[0]), 2.0D) + Math.pow((double) Math.abs(this.field_76503_d[2] - aint1[2]), 2.0D));
                        double d4 = d3 * this.field_76515_i;

                        if ((double) aint1[1] - d4 > (double) l) {
                            aint3[1] = l;
                        } else {
                            aint3[1] = (int) ((double) aint1[1] - d4);
                        }

                        if (this.func_76496_a(aint3, aint1) == -1) {
                            aint[k][0] = k1;
                            aint[k][1] = j;
                            aint[k][2] = l1;
                            aint[k][3] = aint3[1];
                            ++k;
                        }
                    }
                }

                --j;
                --i1;
            }
        }

        this.field_76509_o = new int[k][4];
        System.arraycopy(aint, 0, this.field_76509_o, 0, k);
    }

    void func_150529_a(int i, int j, int k, float f, byte b0, Block block) {
        int l = (int) ((double) f + 0.618D);
        byte b1 = WorldGenBigTree.field_76507_a[b0];
        byte b2 = WorldGenBigTree.field_76507_a[b0 + 3];
        int[] aint = new int[]{i, j, k};
        int[] aint1 = new int[]{0, 0, 0};
        int i1 = -l;
        int j1 = -l;

        for (aint1[b0] = aint[b0]; i1 <= l; ++i1) {
            aint1[b1] = aint[b1] + i1;
            j1 = -l;

            while (j1 <= l) {
                double d0 = Math.pow((double) Math.abs(i1) + 0.5D, 2.0D) + Math.pow((double) Math.abs(j1) + 0.5D, 2.0D);

                if (d0 > (double) (f * f)) {
                    ++j1;
                } else {
                    aint1[b2] = aint[b2] + j1;
                    Block block1 = this.field_76506_c.func_147439_a(aint1[0], aint1[1], aint1[2]);

                    if (block1.func_149688_o() != Material.field_151579_a && block1.func_149688_o() != Material.field_151584_j) {
                        ++j1;
                    } else {
                        this.func_150516_a(this.field_76506_c, aint1[0], aint1[1], aint1[2], block, 0);
                        ++j1;
                    }
                }
            }
        }

    }

    float func_76490_a(int i) {
        if ((double) i < (double) ((float) this.field_76504_e) * 0.3D) {
            return -1.618F;
        } else {
            float f = (float) this.field_76504_e / 2.0F;
            float f1 = (float) this.field_76504_e / 2.0F - (float) i;
            float f2;

            if (f1 == 0.0F) {
                f2 = f;
            } else if (Math.abs(f1) >= f) {
                f2 = 0.0F;
            } else {
                f2 = (float) Math.sqrt(Math.pow((double) Math.abs(f), 2.0D) - Math.pow((double) Math.abs(f1), 2.0D));
            }

            f2 *= 0.5F;
            return f2;
        }
    }

    float func_76495_b(int i) {
        return i >= 0 && i < this.field_76508_n ? (i != 0 && i != this.field_76508_n - 1 ? 3.0F : 2.0F) : -1.0F;
    }

    void func_76491_a(int i, int j, int k) {
        int l = j;

        for (int i1 = j + this.field_76508_n; l < i1; ++l) {
            float f = this.func_76495_b(l - j);

            this.func_150529_a(i, l, k, f, (byte) 1, Blocks.field_150362_t);
        }

    }

    void func_150530_a(int[] aint, int[] aint1, Block block) {
        int[] aint2 = new int[]{0, 0, 0};
        byte b0 = 0;

        byte b1;

        for (b1 = 0; b0 < 3; ++b0) {
            aint2[b0] = aint1[b0] - aint[b0];
            if (Math.abs(aint2[b0]) > Math.abs(aint2[b1])) {
                b1 = b0;
            }
        }

        if (aint2[b1] != 0) {
            byte b2 = WorldGenBigTree.field_76507_a[b1];
            byte b3 = WorldGenBigTree.field_76507_a[b1 + 3];
            byte b4;

            if (aint2[b1] > 0) {
                b4 = 1;
            } else {
                b4 = -1;
            }

            double d0 = (double) aint2[b2] / (double) aint2[b1];
            double d1 = (double) aint2[b3] / (double) aint2[b1];
            int[] aint3 = new int[]{0, 0, 0};
            int i = 0;

            for (int j = aint2[b1] + b4; i != j; i += b4) {
                aint3[b1] = MathHelper.func_76128_c((double) (aint[b1] + i) + 0.5D);
                aint3[b2] = MathHelper.func_76128_c((double) aint[b2] + (double) i * d0 + 0.5D);
                aint3[b3] = MathHelper.func_76128_c((double) aint[b3] + (double) i * d1 + 0.5D);
                byte b5 = 0;
                int k = Math.abs(aint3[0] - aint[0]);
                int l = Math.abs(aint3[2] - aint[2]);
                int i1 = Math.max(k, l);

                if (i1 > 0) {
                    if (k == i1) {
                        b5 = 4;
                    } else if (l == i1) {
                        b5 = 8;
                    }
                }

                this.func_150516_a(this.field_76506_c, aint3[0], aint3[1], aint3[2], block, b5);
            }

        }
    }

    void func_76498_b() {
        int i = 0;

        for (int j = this.field_76509_o.length; i < j; ++i) {
            int k = this.field_76509_o[i][0];
            int l = this.field_76509_o[i][1];
            int i1 = this.field_76509_o[i][2];

            this.func_76491_a(k, l, i1);
        }

    }

    boolean func_76493_c(int i) {
        return (double) i >= (double) this.field_76504_e * 0.2D;
    }

    void func_76499_c() {
        int i = this.field_76503_d[0];
        int j = this.field_76503_d[1];
        int k = this.field_76503_d[1] + this.field_76501_f;
        int l = this.field_76503_d[2];
        int[] aint = new int[]{i, j, l};
        int[] aint1 = new int[]{i, k, l};

        this.func_150530_a(aint, aint1, Blocks.field_150364_r);
        if (this.field_76510_l == 2) {
            int i1 = aint[0]++;

            i1 = aint1[0]++;
            this.func_150530_a(aint, aint1, Blocks.field_150364_r);
            i1 = aint[2]++;
            i1 = aint1[2]++;
            this.func_150530_a(aint, aint1, Blocks.field_150364_r);
            aint[0] += -1;
            aint1[0] += -1;
            this.func_150530_a(aint, aint1, Blocks.field_150364_r);
        }

    }

    void func_76494_d() {
        int i = 0;
        int j = this.field_76509_o.length;

        for (int[] aint = new int[]{this.field_76503_d[0], this.field_76503_d[1], this.field_76503_d[2]}; i < j; ++i) {
            int[] aint1 = this.field_76509_o[i];
            int[] aint2 = new int[]{aint1[0], aint1[1], aint1[2]};

            aint[1] = aint1[3];
            int k = aint[1] - this.field_76503_d[1];

            if (this.func_76493_c(k)) {
                this.func_150530_a(aint, aint2, Blocks.field_150364_r);
            }
        }

    }

    int func_76496_a(int[] aint, int[] aint1) {
        int[] aint2 = new int[]{0, 0, 0};
        byte b0 = 0;

        byte b1;

        for (b1 = 0; b0 < 3; ++b0) {
            aint2[b0] = aint1[b0] - aint[b0];
            if (Math.abs(aint2[b0]) > Math.abs(aint2[b1])) {
                b1 = b0;
            }
        }

        if (aint2[b1] == 0) {
            return -1;
        } else {
            byte b2 = WorldGenBigTree.field_76507_a[b1];
            byte b3 = WorldGenBigTree.field_76507_a[b1 + 3];
            byte b4;

            if (aint2[b1] > 0) {
                b4 = 1;
            } else {
                b4 = -1;
            }

            double d0 = (double) aint2[b2] / (double) aint2[b1];
            double d1 = (double) aint2[b3] / (double) aint2[b1];
            int[] aint3 = new int[]{0, 0, 0};
            int i = 0;

            int j;

            for (j = aint2[b1] + b4; i != j; i += b4) {
                aint3[b1] = aint[b1] + i;
                aint3[b2] = MathHelper.func_76128_c((double) aint[b2] + (double) i * d0);
                aint3[b3] = MathHelper.func_76128_c((double) aint[b3] + (double) i * d1);
                Block block = this.field_76506_c.func_147439_a(aint3[0], aint3[1], aint3[2]);

                if (!this.func_150523_a(block)) {
                    break;
                }
            }

            return i == j ? -1 : Math.abs(i);
        }
    }

    boolean func_76497_e() {
        int[] aint = new int[]{this.field_76503_d[0], this.field_76503_d[1], this.field_76503_d[2]};
        int[] aint1 = new int[]{this.field_76503_d[0], this.field_76503_d[1] + this.field_76504_e - 1, this.field_76503_d[2]};
        Block block = this.field_76506_c.func_147439_a(this.field_76503_d[0], this.field_76503_d[1] - 1, this.field_76503_d[2]);

        if (block != Blocks.field_150346_d && block != Blocks.field_150349_c && block != Blocks.field_150458_ak) {
            return false;
        } else {
            int i = this.func_76496_a(aint, aint1);

            if (i == -1) {
                return true;
            } else if (i < 6) {
                return false;
            } else {
                this.field_76504_e = i;
                return true;
            }
        }
    }

    @Override
    public void func_76487_a(double d0, double d1, double d2) {
        this.field_76511_m = (int) (d0 * 12.0D);
        if (d0 > 0.5D) {
            this.field_76508_n = 5;
        }

        this.field_76512_j = d1;
        this.field_76513_k = d2;
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        this.field_76506_c = world;
        long l = random.nextLong();

        this.field_76505_b.setSeed(l);
        this.field_76503_d[0] = i;
        this.field_76503_d[1] = j;
        this.field_76503_d[2] = k;
        if (this.field_76504_e == 0) {
            this.field_76504_e = 5 + this.field_76505_b.nextInt(this.field_76511_m);
        }

        if (!this.func_76497_e()) {
            return false;
        } else {
            this.func_76489_a();
            this.func_76498_b();
            this.func_76499_c();
            this.func_76494_d();
            return true;
        }
    }
}
