package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRiverMix extends GenLayer {

    private GenLayer field_75910_b;
    private GenLayer field_75911_c;

    public GenLayerRiverMix(long i, GenLayer genlayer, GenLayer genlayer1) {
        super(i);
        this.field_75910_b = genlayer;
        this.field_75911_c = genlayer1;
    }

    @Override
    public void func_75905_a(long i) {
        this.field_75910_b.func_75905_a(i);
        this.field_75911_c.func_75905_a(i);
        super.func_75905_a(i);
    }

    @Override
    public int[] func_75904_a(int i, int j, int k, int l) {
        int[] aint = this.field_75910_b.func_75904_a(i, j, k, l);
        int[] aint1 = this.field_75911_c.func_75904_a(i, j, k, l);
        int[] aint2 = IntCache.func_76445_a(k * l);

        for (int i1 = 0; i1 < k * l; ++i1) {
            if (aint[i1] != BiomeGenBase.field_76771_b.field_76756_M && aint[i1] != BiomeGenBase.field_150575_M.field_76756_M) {
                if (aint1[i1] == BiomeGenBase.field_76781_i.field_76756_M) {
                    if (aint[i1] == BiomeGenBase.field_76774_n.field_76756_M) {
                        aint2[i1] = BiomeGenBase.field_76777_m.field_76756_M;
                    } else if (aint[i1] != BiomeGenBase.field_76789_p.field_76756_M && aint[i1] != BiomeGenBase.field_76788_q.field_76756_M) {
                        aint2[i1] = aint1[i1] & 255;
                    } else {
                        aint2[i1] = BiomeGenBase.field_76788_q.field_76756_M;
                    }
                } else {
                    aint2[i1] = aint[i1];
                }
            } else {
                aint2[i1] = aint[i1];
            }
        }

        return aint2;
    }
}
