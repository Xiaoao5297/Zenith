package net.minecraft.util;

public class LongHashMap {

    private transient LongHashMap$Entry[] field_76169_a = new LongHashMap$Entry[16];
    private transient int field_76167_b;
    private int field_76168_c = 12;
    private final float field_76165_d = 0.75F;
    private transient volatile int field_76166_e;

    public LongHashMap() {}

    private static int func_76155_g(long i) {
        return func_76157_a((int) (i ^ i >>> 32));
    }

    private static int func_76157_a(int i) {
        i ^= i >>> 20 ^ i >>> 12;
        return i ^ i >>> 7 ^ i >>> 4;
    }

    private static int func_76158_a(int i, int j) {
        return i & j - 1;
    }

    public int func_76162_a() {
        return this.field_76167_b;
    }

    public Object func_76164_a(long i) {
        int j = func_76155_g(i);

        for (LongHashMap$Entry longhashmap$entry = this.field_76169_a[func_76158_a(j, this.field_76169_a.length)]; longhashmap$entry != null; longhashmap$entry = longhashmap$entry.field_76149_c) {
            if (longhashmap$entry.field_76150_a == i) {
                return longhashmap$entry.field_76148_b;
            }
        }

        return null;
    }

    public boolean func_76161_b(long i) {
        return this.func_76160_c(i) != null;
    }

    final LongHashMap$Entry func_76160_c(long i) {
        int j = func_76155_g(i);

        for (LongHashMap$Entry longhashmap$entry = this.field_76169_a[func_76158_a(j, this.field_76169_a.length)]; longhashmap$entry != null; longhashmap$entry = longhashmap$entry.field_76149_c) {
            if (longhashmap$entry.field_76150_a == i) {
                return longhashmap$entry;
            }
        }

        return null;
    }

    public void func_76163_a(long i, Object object) {
        int j = func_76155_g(i);
        int k = func_76158_a(j, this.field_76169_a.length);

        for (LongHashMap$Entry longhashmap$entry = this.field_76169_a[k]; longhashmap$entry != null; longhashmap$entry = longhashmap$entry.field_76149_c) {
            if (longhashmap$entry.field_76150_a == i) {
                longhashmap$entry.field_76148_b = object;
                return;
            }
        }

        ++this.field_76166_e;
        this.func_76156_a(j, i, object, k);
    }

    private void func_76153_b(int i) {
        LongHashMap$Entry[] alonghashmap$entry = this.field_76169_a;
        int j = alonghashmap$entry.length;

        if (j == 1073741824) {
            this.field_76168_c = Integer.MAX_VALUE;
        } else {
            LongHashMap$Entry[] alonghashmap$entry1 = new LongHashMap$Entry[i];

            this.func_76154_a(alonghashmap$entry1);
            this.field_76169_a = alonghashmap$entry1;
            this.field_76168_c = (int) ((float) i * this.field_76165_d);
        }
    }

    private void func_76154_a(LongHashMap$Entry[] alonghashmap$entry) {
        LongHashMap$Entry[] alonghashmap$entry1 = this.field_76169_a;
        int i = alonghashmap$entry.length;

        for (int j = 0; j < alonghashmap$entry1.length; ++j) {
            LongHashMap$Entry longhashmap$entry = alonghashmap$entry1[j];

            if (longhashmap$entry != null) {
                alonghashmap$entry1[j] = null;

                LongHashMap$Entry longhashmap$entry1;

                do {
                    longhashmap$entry1 = longhashmap$entry.field_76149_c;
                    int k = func_76158_a(longhashmap$entry.field_76147_d, i);

                    longhashmap$entry.field_76149_c = alonghashmap$entry[k];
                    alonghashmap$entry[k] = longhashmap$entry;
                    longhashmap$entry = longhashmap$entry1;
                } while (longhashmap$entry1 != null);
            }
        }

    }

    public Object func_76159_d(long i) {
        LongHashMap$Entry longhashmap$entry = this.func_76152_e(i);

        return longhashmap$entry == null ? null : longhashmap$entry.field_76148_b;
    }

    final LongHashMap$Entry func_76152_e(long i) {
        int j = func_76155_g(i);
        int k = func_76158_a(j, this.field_76169_a.length);
        LongHashMap$Entry longhashmap$entry = this.field_76169_a[k];

        LongHashMap$Entry longhashmap$entry1;
        LongHashMap$Entry longhashmap$entry2;

        for (longhashmap$entry2 = longhashmap$entry; longhashmap$entry2 != null; longhashmap$entry2 = longhashmap$entry1) {
            longhashmap$entry1 = longhashmap$entry2.field_76149_c;
            if (longhashmap$entry2.field_76150_a == i) {
                ++this.field_76166_e;
                --this.field_76167_b;
                if (longhashmap$entry == longhashmap$entry2) {
                    this.field_76169_a[k] = longhashmap$entry1;
                } else {
                    longhashmap$entry.field_76149_c = longhashmap$entry1;
                }

                return longhashmap$entry2;
            }

            longhashmap$entry = longhashmap$entry2;
        }

        return longhashmap$entry2;
    }

    private void func_76156_a(int i, long j, Object object, int k) {
        LongHashMap$Entry longhashmap$entry = this.field_76169_a[k];

        this.field_76169_a[k] = new LongHashMap$Entry(i, j, object, longhashmap$entry);
        if (this.field_76167_b++ >= this.field_76168_c) {
            this.func_76153_b(2 * this.field_76169_a.length);
        }

    }
}
