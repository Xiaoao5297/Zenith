package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenJungle;
import net.minecraft.world.biome.BiomeGenMesa;

public class GenLayerShore extends GenLayer {

    public GenLayerShore(long i, GenLayer genlayer) {
        super(i);
        this.field_75909_a = genlayer;
    }

    @Override
    public int[] func_75904_a(int i, int j, int k, int l) {
        int[] aint = this.field_75909_a.func_75904_a(i - 1, j - 1, k + 2, l + 2);
        int[] aint1 = IntCache.func_76445_a(k * l);

        for (int i1 = 0; i1 < l; ++i1) {
            for (int j1 = 0; j1 < k; ++j1) {
                this.func_75903_a((long) (j1 + i), (long) (i1 + j));
                int k1 = aint[j1 + 1 + (i1 + 1) * (k + 2)];
                BiomeGenBase biomegenbase = BiomeGenBase.func_150568_d(k1);
                int l1;
                int i2;
                int j2;
                int k2;

                if (k1 == BiomeGenBase.field_76789_p.field_76756_M) {
                    l1 = aint[j1 + 1 + (i1 + 1 - 1) * (k + 2)];
                    i2 = aint[j1 + 1 + 1 + (i1 + 1) * (k + 2)];
                    j2 = aint[j1 + 1 - 1 + (i1 + 1) * (k + 2)];
                    k2 = aint[j1 + 1 + (i1 + 1 + 1) * (k + 2)];
                    if (l1 != BiomeGenBase.field_76771_b.field_76756_M && i2 != BiomeGenBase.field_76771_b.field_76756_M && j2 != BiomeGenBase.field_76771_b.field_76756_M && k2 != BiomeGenBase.field_76771_b.field_76756_M) {
                        aint1[j1 + i1 * k] = k1;
                    } else {
                        aint1[j1 + i1 * k] = BiomeGenBase.field_76788_q.field_76756_M;
                    }
                } else if (biomegenbase != null && biomegenbase.func_150562_l() == BiomeGenJungle.class) {
                    l1 = aint[j1 + 1 + (i1 + 1 - 1) * (k + 2)];
                    i2 = aint[j1 + 1 + 1 + (i1 + 1) * (k + 2)];
                    j2 = aint[j1 + 1 - 1 + (i1 + 1) * (k + 2)];
                    k2 = aint[j1 + 1 + (i1 + 1 + 1) * (k + 2)];
                    if (this.func_151631_c(l1) && this.func_151631_c(i2) && this.func_151631_c(j2) && this.func_151631_c(k2)) {
                        if (!func_151618_b(l1) && !func_151618_b(i2) && !func_151618_b(j2) && !func_151618_b(k2)) {
                            aint1[j1 + i1 * k] = k1;
                        } else {
                            aint1[j1 + i1 * k] = BiomeGenBase.field_76787_r.field_76756_M;
                        }
                    } else {
                        aint1[j1 + i1 * k] = BiomeGenBase.field_150574_L.field_76756_M;
                    }
                } else if (k1 != BiomeGenBase.field_76770_e.field_76756_M && k1 != BiomeGenBase.field_150580_W.field_76756_M && k1 != BiomeGenBase.field_76783_v.field_76756_M) {
                    if (biomegenbase != null && biomegenbase.func_150559_j()) {
                        this.func_151632_a(aint, aint1, j1, i1, k, k1, BiomeGenBase.field_150577_O.field_76756_M);
                    } else if (k1 != BiomeGenBase.field_150589_Z.field_76756_M && k1 != BiomeGenBase.field_150607_aa.field_76756_M) {
                        if (k1 != BiomeGenBase.field_76771_b.field_76756_M && k1 != BiomeGenBase.field_150575_M.field_76756_M && k1 != BiomeGenBase.field_76781_i.field_76756_M && k1 != BiomeGenBase.field_76780_h.field_76756_M) {
                            l1 = aint[j1 + 1 + (i1 + 1 - 1) * (k + 2)];
                            i2 = aint[j1 + 1 + 1 + (i1 + 1) * (k + 2)];
                            j2 = aint[j1 + 1 - 1 + (i1 + 1) * (k + 2)];
                            k2 = aint[j1 + 1 + (i1 + 1 + 1) * (k + 2)];
                            if (!func_151618_b(l1) && !func_151618_b(i2) && !func_151618_b(j2) && !func_151618_b(k2)) {
                                aint1[j1 + i1 * k] = k1;
                            } else {
                                aint1[j1 + i1 * k] = BiomeGenBase.field_76787_r.field_76756_M;
                            }
                        } else {
                            aint1[j1 + i1 * k] = k1;
                        }
                    } else {
                        l1 = aint[j1 + 1 + (i1 + 1 - 1) * (k + 2)];
                        i2 = aint[j1 + 1 + 1 + (i1 + 1) * (k + 2)];
                        j2 = aint[j1 + 1 - 1 + (i1 + 1) * (k + 2)];
                        k2 = aint[j1 + 1 + (i1 + 1 + 1) * (k + 2)];
                        if (!func_151618_b(l1) && !func_151618_b(i2) && !func_151618_b(j2) && !func_151618_b(k2)) {
                            if (this.func_151633_d(l1) && this.func_151633_d(i2) && this.func_151633_d(j2) && this.func_151633_d(k2)) {
                                aint1[j1 + i1 * k] = k1;
                            } else {
                                aint1[j1 + i1 * k] = BiomeGenBase.field_76769_d.field_76756_M;
                            }
                        } else {
                            aint1[j1 + i1 * k] = k1;
                        }
                    }
                } else {
                    this.func_151632_a(aint, aint1, j1, i1, k, k1, BiomeGenBase.field_150576_N.field_76756_M);
                }
            }
        }

        return aint1;
    }

    private void func_151632_a(int[] aint, int[] aint1, int i, int j, int k, int l, int i1) {
        if (func_151618_b(l)) {
            aint1[i + j * k] = l;
        } else {
            int j1 = aint[i + 1 + (j + 1 - 1) * (k + 2)];
            int k1 = aint[i + 1 + 1 + (j + 1) * (k + 2)];
            int l1 = aint[i + 1 - 1 + (j + 1) * (k + 2)];
            int i2 = aint[i + 1 + (j + 1 + 1) * (k + 2)];

            if (!func_151618_b(j1) && !func_151618_b(k1) && !func_151618_b(l1) && !func_151618_b(i2)) {
                aint1[i + j * k] = l;
            } else {
                aint1[i + j * k] = i1;
            }

        }
    }

    private boolean func_151631_c(int i) {
        return BiomeGenBase.func_150568_d(i) != null && BiomeGenBase.func_150568_d(i).func_150562_l() == BiomeGenJungle.class ? true : i == BiomeGenBase.field_150574_L.field_76756_M || i == BiomeGenBase.field_76782_w.field_76756_M || i == BiomeGenBase.field_76792_x.field_76756_M || i == BiomeGenBase.field_76767_f.field_76756_M || i == BiomeGenBase.field_76768_g.field_76756_M || func_151618_b(i);
    }

    private boolean func_151633_d(int i) {
        return BiomeGenBase.func_150568_d(i) != null && BiomeGenBase.func_150568_d(i) instanceof BiomeGenMesa;
    }
}
