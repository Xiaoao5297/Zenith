package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase$TempCategory;

public class GenLayerBiomeEdge extends GenLayer {

    public GenLayerBiomeEdge(long i, GenLayer genlayer) {
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

                if (!this.func_151636_a(aint, aint1, j1, i1, k, k1, BiomeGenBase.field_76770_e.field_76756_M, BiomeGenBase.field_76783_v.field_76756_M) && !this.func_151635_b(aint, aint1, j1, i1, k, k1, BiomeGenBase.field_150607_aa.field_76756_M, BiomeGenBase.field_150589_Z.field_76756_M) && !this.func_151635_b(aint, aint1, j1, i1, k, k1, BiomeGenBase.field_150608_ab.field_76756_M, BiomeGenBase.field_150589_Z.field_76756_M) && !this.func_151635_b(aint, aint1, j1, i1, k, k1, BiomeGenBase.field_150578_U.field_76756_M, BiomeGenBase.field_76768_g.field_76756_M)) {
                    int l1;
                    int i2;
                    int j2;
                    int k2;

                    if (k1 == BiomeGenBase.field_76769_d.field_76756_M) {
                        l1 = aint[j1 + 1 + (i1 + 1 - 1) * (k + 2)];
                        i2 = aint[j1 + 1 + 1 + (i1 + 1) * (k + 2)];
                        j2 = aint[j1 + 1 - 1 + (i1 + 1) * (k + 2)];
                        k2 = aint[j1 + 1 + (i1 + 1 + 1) * (k + 2)];
                        if (l1 != BiomeGenBase.field_76774_n.field_76756_M && i2 != BiomeGenBase.field_76774_n.field_76756_M && j2 != BiomeGenBase.field_76774_n.field_76756_M && k2 != BiomeGenBase.field_76774_n.field_76756_M) {
                            aint1[j1 + i1 * k] = k1;
                        } else {
                            aint1[j1 + i1 * k] = BiomeGenBase.field_150580_W.field_76756_M;
                        }
                    } else if (k1 == BiomeGenBase.field_76780_h.field_76756_M) {
                        l1 = aint[j1 + 1 + (i1 + 1 - 1) * (k + 2)];
                        i2 = aint[j1 + 1 + 1 + (i1 + 1) * (k + 2)];
                        j2 = aint[j1 + 1 - 1 + (i1 + 1) * (k + 2)];
                        k2 = aint[j1 + 1 + (i1 + 1 + 1) * (k + 2)];
                        if (l1 != BiomeGenBase.field_76769_d.field_76756_M && i2 != BiomeGenBase.field_76769_d.field_76756_M && j2 != BiomeGenBase.field_76769_d.field_76756_M && k2 != BiomeGenBase.field_76769_d.field_76756_M && l1 != BiomeGenBase.field_150584_S.field_76756_M && i2 != BiomeGenBase.field_150584_S.field_76756_M && j2 != BiomeGenBase.field_150584_S.field_76756_M && k2 != BiomeGenBase.field_150584_S.field_76756_M && l1 != BiomeGenBase.field_76774_n.field_76756_M && i2 != BiomeGenBase.field_76774_n.field_76756_M && j2 != BiomeGenBase.field_76774_n.field_76756_M && k2 != BiomeGenBase.field_76774_n.field_76756_M) {
                            if (l1 != BiomeGenBase.field_76782_w.field_76756_M && k2 != BiomeGenBase.field_76782_w.field_76756_M && i2 != BiomeGenBase.field_76782_w.field_76756_M && j2 != BiomeGenBase.field_76782_w.field_76756_M) {
                                aint1[j1 + i1 * k] = k1;
                            } else {
                                aint1[j1 + i1 * k] = BiomeGenBase.field_150574_L.field_76756_M;
                            }
                        } else {
                            aint1[j1 + i1 * k] = BiomeGenBase.field_76772_c.field_76756_M;
                        }
                    } else {
                        aint1[j1 + i1 * k] = k1;
                    }
                }
            }
        }

        return aint1;
    }

    private boolean func_151636_a(int[] aint, int[] aint1, int i, int j, int k, int l, int i1, int j1) {
        if (!func_151616_a(l, i1)) {
            return false;
        } else {
            int k1 = aint[i + 1 + (j + 1 - 1) * (k + 2)];
            int l1 = aint[i + 1 + 1 + (j + 1) * (k + 2)];
            int i2 = aint[i + 1 - 1 + (j + 1) * (k + 2)];
            int j2 = aint[i + 1 + (j + 1 + 1) * (k + 2)];

            if (this.func_151634_b(k1, i1) && this.func_151634_b(l1, i1) && this.func_151634_b(i2, i1) && this.func_151634_b(j2, i1)) {
                aint1[i + j * k] = l;
            } else {
                aint1[i + j * k] = j1;
            }

            return true;
        }
    }

    private boolean func_151635_b(int[] aint, int[] aint1, int i, int j, int k, int l, int i1, int j1) {
        if (l != i1) {
            return false;
        } else {
            int k1 = aint[i + 1 + (j + 1 - 1) * (k + 2)];
            int l1 = aint[i + 1 + 1 + (j + 1) * (k + 2)];
            int i2 = aint[i + 1 - 1 + (j + 1) * (k + 2)];
            int j2 = aint[i + 1 + (j + 1 + 1) * (k + 2)];

            if (func_151616_a(k1, i1) && func_151616_a(l1, i1) && func_151616_a(i2, i1) && func_151616_a(j2, i1)) {
                aint1[i + j * k] = l;
            } else {
                aint1[i + j * k] = j1;
            }

            return true;
        }
    }

    private boolean func_151634_b(int i, int j) {
        if (func_151616_a(i, j)) {
            return true;
        } else if (BiomeGenBase.func_150568_d(i) != null && BiomeGenBase.func_150568_d(j) != null) {
            BiomeGenBase$TempCategory biomegenbase$tempcategory = BiomeGenBase.func_150568_d(i).func_150561_m();
            BiomeGenBase$TempCategory biomegenbase$tempcategory1 = BiomeGenBase.func_150568_d(j).func_150561_m();

            return biomegenbase$tempcategory == biomegenbase$tempcategory1 || biomegenbase$tempcategory == BiomeGenBase$TempCategory.MEDIUM || biomegenbase$tempcategory1 == BiomeGenBase$TempCategory.MEDIUM;
        } else {
            return false;
        }
    }
}
