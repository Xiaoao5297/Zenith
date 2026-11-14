package net.minecraft.world.biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class WorldChunkManager {

    private GenLayer field_76944_d;
    private GenLayer field_76945_e;
    private BiomeCache field_76942_f;
    private List field_76943_g;

    protected WorldChunkManager() {
        this.field_76942_f = new BiomeCache(this);
        this.field_76943_g = new ArrayList();
        this.field_76943_g.add(BiomeGenBase.field_76767_f);
        this.field_76943_g.add(BiomeGenBase.field_76772_c);
        this.field_76943_g.add(BiomeGenBase.field_76768_g);
        this.field_76943_g.add(BiomeGenBase.field_76784_u);
        this.field_76943_g.add(BiomeGenBase.field_76785_t);
        this.field_76943_g.add(BiomeGenBase.field_76782_w);
        this.field_76943_g.add(BiomeGenBase.field_76792_x);
    }

    public WorldChunkManager(long i, WorldType worldtype) {
        this();
        GenLayer[] agenlayer = GenLayer.func_75901_a(i, worldtype);

        this.field_76944_d = agenlayer[0];
        this.field_76945_e = agenlayer[1];
    }

    public WorldChunkManager(World world) {
        this(world.func_72905_C(), world.func_72912_H().func_76067_t());
    }

    public List func_76932_a() {
        return this.field_76943_g;
    }

    public BiomeGenBase func_76935_a(int i, int j) {
        return this.field_76942_f.func_76837_b(i, j);
    }

    public float[] func_76936_a(float[] afloat, int i, int j, int k, int l) {
        IntCache.func_76446_a();
        if (afloat == null || afloat.length < k * l) {
            afloat = new float[k * l];
        }

        int[] aint = this.field_76945_e.func_75904_a(i, j, k, l);

        for (int i1 = 0; i1 < k * l; ++i1) {
            try {
                float f = (float) BiomeGenBase.func_150568_d(aint[i1]).func_76744_g() / 65536.0F;

                if (f > 1.0F) {
                    f = 1.0F;
                }

                afloat[i1] = f;
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.func_85055_a(throwable, "Invalid Biome id");
                CrashReportCategory crashreportcategory = crashreport.func_85058_a("DownfallBlock");

                crashreportcategory.func_71507_a("biome id", i1);
                crashreportcategory.func_71507_a("downfalls[] size", afloat.length);
                crashreportcategory.func_71507_a("x", i);
                crashreportcategory.func_71507_a("z", j);
                crashreportcategory.func_71507_a("w", k);
                crashreportcategory.func_71507_a("h", l);
                throw new ReportedException(crashreport);
            }
        }

        return afloat;
    }

    public BiomeGenBase[] func_76937_a(BiomeGenBase[] abiomegenbase, int i, int j, int k, int l) {
        IntCache.func_76446_a();
        if (abiomegenbase == null || abiomegenbase.length < k * l) {
            abiomegenbase = new BiomeGenBase[k * l];
        }

        int[] aint = this.field_76944_d.func_75904_a(i, j, k, l);

        try {
            for (int i1 = 0; i1 < k * l; ++i1) {
                abiomegenbase[i1] = BiomeGenBase.func_150568_d(aint[i1]);
            }

            return abiomegenbase;
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.func_85055_a(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.func_85058_a("RawBiomeBlock");

            crashreportcategory.func_71507_a("biomes[] size", abiomegenbase.length);
            crashreportcategory.func_71507_a("x", i);
            crashreportcategory.func_71507_a("z", j);
            crashreportcategory.func_71507_a("w", k);
            crashreportcategory.func_71507_a("h", l);
            throw new ReportedException(crashreport);
        }
    }

    public BiomeGenBase[] func_76933_b(BiomeGenBase[] abiomegenbase, int i, int j, int k, int l) {
        return this.func_76931_a(abiomegenbase, i, j, k, l, true);
    }

    public BiomeGenBase[] func_76931_a(BiomeGenBase[] abiomegenbase, int i, int j, int k, int l, boolean flag) {
        IntCache.func_76446_a();
        if (abiomegenbase == null || abiomegenbase.length < k * l) {
            abiomegenbase = new BiomeGenBase[k * l];
        }

        if (flag && k == 16 && l == 16 && (i & 15) == 0 && (j & 15) == 0) {
            BiomeGenBase[] abiomegenbase1 = this.field_76942_f.func_76839_e(i, j);

            System.arraycopy(abiomegenbase1, 0, abiomegenbase, 0, k * l);
            return abiomegenbase;
        } else {
            int[] aint = this.field_76945_e.func_75904_a(i, j, k, l);

            for (int i1 = 0; i1 < k * l; ++i1) {
                abiomegenbase[i1] = BiomeGenBase.func_150568_d(aint[i1]);
            }

            return abiomegenbase;
        }
    }

    public boolean func_76940_a(int i, int j, int k, List list) {
        IntCache.func_76446_a();
        int l = i - k >> 2;
        int i1 = j - k >> 2;
        int j1 = i + k >> 2;
        int k1 = j + k >> 2;
        int l1 = j1 - l + 1;
        int i2 = k1 - i1 + 1;
        int[] aint = this.field_76944_d.func_75904_a(l, i1, l1, i2);

        try {
            for (int j2 = 0; j2 < l1 * i2; ++j2) {
                BiomeGenBase biomegenbase = BiomeGenBase.func_150568_d(aint[j2]);

                if (!list.contains(biomegenbase)) {
                    return false;
                }
            }

            return true;
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.func_85055_a(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.func_85058_a("Layer");

            crashreportcategory.func_71507_a("Layer", this.field_76944_d.toString());
            crashreportcategory.func_71507_a("x", i);
            crashreportcategory.func_71507_a("z", j);
            crashreportcategory.func_71507_a("radius", k);
            crashreportcategory.func_71507_a("allowed", list);
            throw new ReportedException(crashreport);
        }
    }

    public ChunkPosition func_150795_a(int i, int j, int k, List list, Random random) {
        IntCache.func_76446_a();
        int l = i - k >> 2;
        int i1 = j - k >> 2;
        int j1 = i + k >> 2;
        int k1 = j + k >> 2;
        int l1 = j1 - l + 1;
        int i2 = k1 - i1 + 1;
        int[] aint = this.field_76944_d.func_75904_a(l, i1, l1, i2);
        ChunkPosition chunkposition = null;
        int j2 = 0;

        for (int k2 = 0; k2 < l1 * i2; ++k2) {
            int l2 = l + k2 % l1 << 2;
            int i3 = i1 + k2 / l1 << 2;
            BiomeGenBase biomegenbase = BiomeGenBase.func_150568_d(aint[k2]);

            if (list.contains(biomegenbase) && (chunkposition == null || random.nextInt(j2 + 1) == 0)) {
                chunkposition = new ChunkPosition(l2, 0, i3);
                ++j2;
            }
        }

        return chunkposition;
    }

    public void func_76938_b() {
        this.field_76942_f.func_76838_a();
    }
}
