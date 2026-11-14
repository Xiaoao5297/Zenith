package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenLayerHills extends GenLayer {

    private static final Logger field_151629_c = LogManager.getLogger();
    private GenLayer field_151628_d;

    public GenLayerHills(long i, GenLayer genlayer, GenLayer genlayer1) {
        super(i);
        this.field_75909_a = genlayer;
        this.field_151628_d = genlayer1;
    }

    @Override
    public int[] func_75904_a(int i, int j, int k, int l) {
        int[] aint = this.field_75909_a.func_75904_a(i - 1, j - 1, k + 2, l + 2);
        int[] aint1 = this.field_151628_d.func_75904_a(i - 1, j - 1, k + 2, l + 2);
        int[] aint2 = IntCache.func_76445_a(k * l);

        for (int i1 = 0; i1 < l; ++i1) {
            for (int j1 = 0; j1 < k; ++j1) {
                this.func_75903_a((long) (j1 + i), (long) (i1 + j));
                int k1 = aint[j1 + 1 + (i1 + 1) * (k + 2)];
                int l1 = aint1[j1 + 1 + (i1 + 1) * (k + 2)];
                boolean flag = (l1 - 2) % 29 == 0;

                if (k1 > 255) {
                    GenLayerHills.field_151629_c.debug("old! " + k1);
                }

                if (k1 != 0 && l1 >= 2 && (l1 - 2) % 29 == 1 && k1 < 128) {
                    if (BiomeGenBase.func_150568_d(k1 + 128) != null) {
                        aint2[j1 + i1 * k] = k1 + 128;
                    } else {
                        aint2[j1 + i1 * k] = k1;
                    }
                } else if (this.func_75902_a(3) != 0 && !flag) {
                    aint2[j1 + i1 * k] = k1;
                } else {
                    int i2 = k1;
                    int j2;

                    if (k1 == BiomeGenBase.field_76769_d.field_76756_M) {
                        i2 = BiomeGenBase.field_76786_s.field_76756_M;
                    } else if (k1 == BiomeGenBase.field_76767_f.field_76756_M) {
                        i2 = BiomeGenBase.field_76785_t.field_76756_M;
                    } else if (k1 == BiomeGenBase.field_150583_P.field_76756_M) {
                        i2 = BiomeGenBase.field_150582_Q.field_76756_M;
                    } else if (k1 == BiomeGenBase.field_150585_R.field_76756_M) {
                        i2 = BiomeGenBase.field_76772_c.field_76756_M;
                    } else if (k1 == BiomeGenBase.field_76768_g.field_76756_M) {
                        i2 = BiomeGenBase.field_76784_u.field_76756_M;
                    } else if (k1 == BiomeGenBase.field_150578_U.field_76756_M) {
                        i2 = BiomeGenBase.field_150581_V.field_76756_M;
                    } else if (k1 == BiomeGenBase.field_150584_S.field_76756_M) {
                        i2 = BiomeGenBase.field_150579_T.field_76756_M;
                    } else if (k1 == BiomeGenBase.field_76772_c.field_76756_M) {
                        if (this.func_75902_a(3) == 0) {
                            i2 = BiomeGenBase.field_76785_t.field_76756_M;
                        } else {
                            i2 = BiomeGenBase.field_76767_f.field_76756_M;
                        }
                    } else if (k1 == BiomeGenBase.field_76774_n.field_76756_M) {
                        i2 = BiomeGenBase.field_76775_o.field_76756_M;
                    } else if (k1 == BiomeGenBase.field_76782_w.field_76756_M) {
                        i2 = BiomeGenBase.field_76792_x.field_76756_M;
                    } else if (k1 == BiomeGenBase.field_76771_b.field_76756_M) {
                        i2 = BiomeGenBase.field_150575_M.field_76756_M;
                    } else if (k1 == BiomeGenBase.field_76770_e.field_76756_M) {
                        i2 = BiomeGenBase.field_150580_W.field_76756_M;
                    } else if (k1 == BiomeGenBase.field_150588_X.field_76756_M) {
                        i2 = BiomeGenBase.field_150587_Y.field_76756_M;
                    } else if (func_151616_a(k1, BiomeGenBase.field_150607_aa.field_76756_M)) {
                        i2 = BiomeGenBase.field_150589_Z.field_76756_M;
                    } else if (k1 == BiomeGenBase.field_150575_M.field_76756_M && this.func_75902_a(3) == 0) {
                        j2 = this.func_75902_a(2);
                        if (j2 == 0) {
                            i2 = BiomeGenBase.field_76772_c.field_76756_M;
                        } else {
                            i2 = BiomeGenBase.field_76767_f.field_76756_M;
                        }
                    }

                    if (flag && i2 != k1) {
                        if (BiomeGenBase.func_150568_d(i2 + 128) != null) {
                            i2 += 128;
                        } else {
                            i2 = k1;
                        }
                    }

                    if (i2 == k1) {
                        aint2[j1 + i1 * k] = k1;
                    } else {
                        j2 = aint[j1 + 1 + (i1 + 1 - 1) * (k + 2)];
                        int k2 = aint[j1 + 1 + 1 + (i1 + 1) * (k + 2)];
                        int l2 = aint[j1 + 1 - 1 + (i1 + 1) * (k + 2)];
                        int i3 = aint[j1 + 1 + (i1 + 1 + 1) * (k + 2)];
                        int j3 = 0;

                        if (func_151616_a(j2, k1)) {
                            ++j3;
                        }

                        if (func_151616_a(k2, k1)) {
                            ++j3;
                        }

                        if (func_151616_a(l2, k1)) {
                            ++j3;
                        }

                        if (func_151616_a(i3, k1)) {
                            ++j3;
                        }

                        if (j3 >= 3) {
                            aint2[j1 + i1 * k] = i2;
                        } else {
                            aint2[j1 + i1 * k] = k1;
                        }
                    }
                }
            }
        }

        return aint2;
    }
}
