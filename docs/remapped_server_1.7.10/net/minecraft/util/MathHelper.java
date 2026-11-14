package net.minecraft.util;

import java.util.Random;

public class MathHelper {

    private static float[] field_76144_a = new float[65536];
    private static final int[] field_151242_b;

    public static final float func_76126_a(float f) {
        return MathHelper.field_76144_a[(int) (f * 10430.378F) & '\uffff'];
    }

    public static final float func_76134_b(float f) {
        return MathHelper.field_76144_a[(int) (f * 10430.378F + 16384.0F) & '\uffff'];
    }

    public static final float func_76129_c(float f) {
        return (float) Math.sqrt((double) f);
    }

    public static final float func_76133_a(double d0) {
        return (float) Math.sqrt(d0);
    }

    public static int func_76141_d(float f) {
        int i = (int) f;

        return f < (float) i ? i - 1 : i;
    }

    public static int func_76128_c(double d0) {
        int i = (int) d0;

        return d0 < (double) i ? i - 1 : i;
    }

    public static long func_76124_d(double d0) {
        long i = (long) d0;

        return d0 < (double) i ? i - 1L : i;
    }

    public static float func_76135_e(float f) {
        return f >= 0.0F ? f : -f;
    }

    public static int func_76130_a(int i) {
        return i >= 0 ? i : -i;
    }

    public static int func_76123_f(float f) {
        int i = (int) f;

        return f > (float) i ? i + 1 : i;
    }

    public static int func_76143_f(double d0) {
        int i = (int) d0;

        return d0 > (double) i ? i + 1 : i;
    }

    public static int func_76125_a(int i, int j, int k) {
        return i < j ? j : (i > k ? k : i);
    }

    public static float func_76131_a(float f, float f1, float f2) {
        return f < f1 ? f1 : (f > f2 ? f2 : f);
    }

    public static double func_151237_a(double d0, double d1, double d2) {
        return d0 < d1 ? d1 : (d0 > d2 ? d2 : d0);
    }

    public static double func_151238_b(double d0, double d1, double d2) {
        return d2 < 0.0D ? d0 : (d2 > 1.0D ? d1 : d0 + (d1 - d0) * d2);
    }

    public static double func_76132_a(double d0, double d1) {
        if (d0 < 0.0D) {
            d0 = -d0;
        }

        if (d1 < 0.0D) {
            d1 = -d1;
        }

        return d0 > d1 ? d0 : d1;
    }

    public static int func_76136_a(Random random, int i, int j) {
        return i >= j ? i : random.nextInt(j - i + 1) + i;
    }

    public static float func_151240_a(Random random, float f, float f1) {
        return f >= f1 ? f : random.nextFloat() * (f1 - f) + f;
    }

    public static double func_82716_a(Random random, double d0, double d1) {
        return d0 >= d1 ? d0 : random.nextDouble() * (d1 - d0) + d0;
    }

    public static double func_76127_a(long[] along) {
        long i = 0L;
        long[] along1 = along;
        int j = along.length;

        for (int k = 0; k < j; ++k) {
            long l = along1[k];

            i += l;
        }

        return (double) i / (double) along.length;
    }

    public static float func_76142_g(float f) {
        f %= 360.0F;
        if (f >= 180.0F) {
            f -= 360.0F;
        }

        if (f < -180.0F) {
            f += 360.0F;
        }

        return f;
    }

    public static double func_76138_g(double d0) {
        d0 %= 360.0D;
        if (d0 >= 180.0D) {
            d0 -= 360.0D;
        }

        if (d0 < -180.0D) {
            d0 += 360.0D;
        }

        return d0;
    }

    public static int func_82715_a(String s, int i) {
        int j = i;

        try {
            j = Integer.parseInt(s);
        } catch (Throwable throwable) {
            ;
        }

        return j;
    }

    public static int func_82714_a(String s, int i, int j) {
        int k = i;

        try {
            k = Integer.parseInt(s);
        } catch (Throwable throwable) {
            ;
        }

        if (k < j) {
            k = j;
        }

        return k;
    }

    public static double func_82712_a(String s, double d0) {
        double d1 = d0;

        try {
            d1 = Double.parseDouble(s);
        } catch (Throwable throwable) {
            ;
        }

        return d1;
    }

    public static double func_82713_a(String s, double d0, double d1) {
        double d2 = d0;

        try {
            d2 = Double.parseDouble(s);
        } catch (Throwable throwable) {
            ;
        }

        if (d2 < d1) {
            d2 = d1;
        }

        return d2;
    }

    static {
        for (int i = 0; i < 65536; ++i) {
            MathHelper.field_76144_a[i] = (float) Math.sin((double) i * Math.PI * 2.0D / 65536.0D);
        }

        field_151242_b = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    }
}
