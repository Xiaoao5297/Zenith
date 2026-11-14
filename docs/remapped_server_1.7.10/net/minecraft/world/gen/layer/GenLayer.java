package net.minecraft.world.gen.layer;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;

public abstract class GenLayer {

    private long field_75907_b;
    protected GenLayer field_75909_a;
    private long field_75908_c;
    protected long field_75906_d;

    public static GenLayer[] func_75901_a(long i, WorldType worldtype) {
        boolean flag = false;
        GenLayerIsland genlayerisland = new GenLayerIsland(1L);
        GenLayerFuzzyZoom genlayerfuzzyzoom = new GenLayerFuzzyZoom(2000L, genlayerisland);
        GenLayerAddIsland genlayeraddisland = new GenLayerAddIsland(1L, genlayerfuzzyzoom);
        GenLayerZoom genlayerzoom = new GenLayerZoom(2001L, genlayeraddisland);

        genlayeraddisland = new GenLayerAddIsland(2L, genlayerzoom);
        genlayeraddisland = new GenLayerAddIsland(50L, genlayeraddisland);
        genlayeraddisland = new GenLayerAddIsland(70L, genlayeraddisland);
        GenLayerRemoveTooMuchOcean genlayerremovetoomuchocean = new GenLayerRemoveTooMuchOcean(2L, genlayeraddisland);
        GenLayerAddSnow genlayeraddsnow = new GenLayerAddSnow(2L, genlayerremovetoomuchocean);

        genlayeraddisland = new GenLayerAddIsland(3L, genlayeraddsnow);
        GenLayerEdge genlayeredge = new GenLayerEdge(2L, genlayeraddisland, GenLayerEdge$Mode.COOL_WARM);

        genlayeredge = new GenLayerEdge(2L, genlayeredge, GenLayerEdge$Mode.HEAT_ICE);
        genlayeredge = new GenLayerEdge(3L, genlayeredge, GenLayerEdge$Mode.SPECIAL);
        genlayerzoom = new GenLayerZoom(2002L, genlayeredge);
        genlayerzoom = new GenLayerZoom(2003L, genlayerzoom);
        genlayeraddisland = new GenLayerAddIsland(4L, genlayerzoom);
        GenLayerAddMushroomIsland genlayeraddmushroomisland = new GenLayerAddMushroomIsland(5L, genlayeraddisland);
        GenLayerDeepOcean genlayerdeepocean = new GenLayerDeepOcean(4L, genlayeraddmushroomisland);
        GenLayer genlayer = GenLayerZoom.func_75915_a(1000L, genlayerdeepocean, 0);
        byte b0 = 4;

        if (worldtype == WorldType.field_77135_d) {
            b0 = 6;
        }

        if (flag) {
            b0 = 4;
        }

        GenLayer genlayer1 = GenLayerZoom.func_75915_a(1000L, genlayer, 0);
        GenLayerRiverInit genlayerriverinit = new GenLayerRiverInit(100L, genlayer1);
        Object object = new GenLayerBiome(200L, genlayer, worldtype);

        if (!flag) {
            GenLayer genlayer2 = GenLayerZoom.func_75915_a(1000L, (GenLayer) object, 2);

            object = new GenLayerBiomeEdge(1000L, genlayer2);
        }

        GenLayer genlayer3 = GenLayerZoom.func_75915_a(1000L, genlayerriverinit, 2);
        GenLayerHills genlayerhills = new GenLayerHills(1000L, (GenLayer) object, genlayer3);

        genlayer1 = GenLayerZoom.func_75915_a(1000L, genlayerriverinit, 2);
        genlayer1 = GenLayerZoom.func_75915_a(1000L, genlayer1, b0);
        GenLayerRiver genlayerriver = new GenLayerRiver(1L, genlayer1);
        GenLayerSmooth genlayersmooth = new GenLayerSmooth(1000L, genlayerriver);

        object = new GenLayerRareBiome(1001L, genlayerhills);

        for (int j = 0; j < b0; ++j) {
            object = new GenLayerZoom((long) (1000 + j), (GenLayer) object);
            if (j == 0) {
                object = new GenLayerAddIsland(3L, (GenLayer) object);
            }

            if (j == 1) {
                object = new GenLayerShore(1000L, (GenLayer) object);
            }
        }

        GenLayerSmooth genlayersmooth1 = new GenLayerSmooth(1000L, (GenLayer) object);
        GenLayerRiverMix genlayerrivermix = new GenLayerRiverMix(100L, genlayersmooth1, genlayersmooth);
        GenLayerVoronoiZoom genlayervoronoizoom = new GenLayerVoronoiZoom(10L, genlayerrivermix);

        genlayerrivermix.func_75905_a(i);
        genlayervoronoizoom.func_75905_a(i);
        return new GenLayer[]{genlayerrivermix, genlayervoronoizoom, genlayerrivermix};
    }

    public GenLayer(long i) {
        this.field_75906_d = i;
        this.field_75906_d *= this.field_75906_d * 6364136223846793005L + 1442695040888963407L;
        this.field_75906_d += i;
        this.field_75906_d *= this.field_75906_d * 6364136223846793005L + 1442695040888963407L;
        this.field_75906_d += i;
        this.field_75906_d *= this.field_75906_d * 6364136223846793005L + 1442695040888963407L;
        this.field_75906_d += i;
    }

    public void func_75905_a(long i) {
        this.field_75907_b = i;
        if (this.field_75909_a != null) {
            this.field_75909_a.func_75905_a(i);
        }

        this.field_75907_b *= this.field_75907_b * 6364136223846793005L + 1442695040888963407L;
        this.field_75907_b += this.field_75906_d;
        this.field_75907_b *= this.field_75907_b * 6364136223846793005L + 1442695040888963407L;
        this.field_75907_b += this.field_75906_d;
        this.field_75907_b *= this.field_75907_b * 6364136223846793005L + 1442695040888963407L;
        this.field_75907_b += this.field_75906_d;
    }

    public void func_75903_a(long i, long j) {
        this.field_75908_c = this.field_75907_b;
        this.field_75908_c *= this.field_75908_c * 6364136223846793005L + 1442695040888963407L;
        this.field_75908_c += i;
        this.field_75908_c *= this.field_75908_c * 6364136223846793005L + 1442695040888963407L;
        this.field_75908_c += j;
        this.field_75908_c *= this.field_75908_c * 6364136223846793005L + 1442695040888963407L;
        this.field_75908_c += i;
        this.field_75908_c *= this.field_75908_c * 6364136223846793005L + 1442695040888963407L;
        this.field_75908_c += j;
    }

    protected int func_75902_a(int i) {
        int j = (int) ((this.field_75908_c >> 24) % (long) i);

        if (j < 0) {
            j += i;
        }

        this.field_75908_c *= this.field_75908_c * 6364136223846793005L + 1442695040888963407L;
        this.field_75908_c += this.field_75907_b;
        return j;
    }

    public abstract int[] func_75904_a(int i, int j, int k, int l);

    protected static boolean func_151616_a(int i, int j) {
        if (i == j) {
            return true;
        } else if (i != BiomeGenBase.field_150607_aa.field_76756_M && i != BiomeGenBase.field_150608_ab.field_76756_M) {
            try {
                return BiomeGenBase.func_150568_d(i) != null && BiomeGenBase.func_150568_d(j) != null ? BiomeGenBase.func_150568_d(i).func_150569_a(BiomeGenBase.func_150568_d(j)) : false;
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.func_85055_a(throwable, "Comparing biomes");
                CrashReportCategory crashreportcategory = crashreport.func_85058_a("Biomes being compared");

                crashreportcategory.func_71507_a("Biome A ID", i);
                crashreportcategory.func_71507_a("Biome B ID", j);
                crashreportcategory.func_71500_a("Biome A", new GenLayer$1(i));
                crashreportcategory.func_71500_a("Biome B", new GenLayer$2(j));
                throw new ReportedException(crashreport);
            }
        } else {
            return j == BiomeGenBase.field_150607_aa.field_76756_M || j == BiomeGenBase.field_150608_ab.field_76756_M;
        }
    }

    protected static boolean func_151618_b(int i) {
        return i == BiomeGenBase.field_76771_b.field_76756_M || i == BiomeGenBase.field_150575_M.field_76756_M || i == BiomeGenBase.field_76776_l.field_76756_M;
    }

    protected int func_151619_a(int... aint) {
        return aint[this.func_75902_a(aint.length)];
    }

    protected int func_151617_b(int i, int j, int k, int l) {
        return j == k && k == l ? j : (i == j && i == k ? i : (i == j && i == l ? i : (i == k && i == l ? i : (i == j && k != l ? i : (i == k && j != l ? i : (i == l && j != k ? i : (j == k && i != l ? j : (j == l && i != k ? j : (k == l && i != j ? k : this.func_151619_a(i, j, k, l))))))))));
    }
}
