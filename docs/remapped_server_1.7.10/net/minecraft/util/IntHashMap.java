package net.minecraft.util;

import java.util.HashSet;
import java.util.Set;

public class IntHashMap {

    private transient IntHashMap$Entry[] field_76055_a = new IntHashMap$Entry[16];
    private transient int field_76053_b;
    private int field_76054_c = 12;
    private final float field_76051_d = 0.75F;
    private transient volatile int field_76052_e;
    private Set field_76050_f = new HashSet();

    public IntHashMap() {}

    private static int func_76044_g(int i) {
        i ^= i >>> 20 ^ i >>> 12;
        return i ^ i >>> 7 ^ i >>> 4;
    }

    private static int func_76043_a(int i, int j) {
        return i & j - 1;
    }

    public Object func_76041_a(int i) {
        int j = func_76044_g(i);

        for (IntHashMap$Entry inthashmap$entry = this.field_76055_a[func_76043_a(j, this.field_76055_a.length)]; inthashmap$entry != null; inthashmap$entry = inthashmap$entry.field_76034_c) {
            if (inthashmap$entry.field_76035_a == i) {
                return inthashmap$entry.field_76033_b;
            }
        }

        return null;
    }

    public boolean func_76037_b(int i) {
        return this.func_76045_c(i) != null;
    }

    final IntHashMap$Entry func_76045_c(int i) {
        int j = func_76044_g(i);

        for (IntHashMap$Entry inthashmap$entry = this.field_76055_a[func_76043_a(j, this.field_76055_a.length)]; inthashmap$entry != null; inthashmap$entry = inthashmap$entry.field_76034_c) {
            if (inthashmap$entry.field_76035_a == i) {
                return inthashmap$entry;
            }
        }

        return null;
    }

    public void func_76038_a(int i, Object object) {
        this.field_76050_f.add(i);
        int j = func_76044_g(i);
        int k = func_76043_a(j, this.field_76055_a.length);

        for (IntHashMap$Entry inthashmap$entry = this.field_76055_a[k]; inthashmap$entry != null; inthashmap$entry = inthashmap$entry.field_76034_c) {
            if (inthashmap$entry.field_76035_a == i) {
                inthashmap$entry.field_76033_b = object;
                return;
            }
        }

        ++this.field_76052_e;
        this.func_76040_a(j, i, object, k);
    }

    private void func_76047_h(int i) {
        IntHashMap$Entry[] ainthashmap$entry = this.field_76055_a;
        int j = ainthashmap$entry.length;

        if (j == 1073741824) {
            this.field_76054_c = Integer.MAX_VALUE;
        } else {
            IntHashMap$Entry[] ainthashmap$entry1 = new IntHashMap$Entry[i];

            this.func_76048_a(ainthashmap$entry1);
            this.field_76055_a = ainthashmap$entry1;
            this.field_76054_c = (int) ((float) i * this.field_76051_d);
        }
    }

    private void func_76048_a(IntHashMap$Entry[] ainthashmap$entry) {
        IntHashMap$Entry[] ainthashmap$entry1 = this.field_76055_a;
        int i = ainthashmap$entry.length;

        for (int j = 0; j < ainthashmap$entry1.length; ++j) {
            IntHashMap$Entry inthashmap$entry = ainthashmap$entry1[j];

            if (inthashmap$entry != null) {
                ainthashmap$entry1[j] = null;

                IntHashMap$Entry inthashmap$entry1;

                do {
                    inthashmap$entry1 = inthashmap$entry.field_76034_c;
                    int k = func_76043_a(inthashmap$entry.field_76032_d, i);

                    inthashmap$entry.field_76034_c = ainthashmap$entry[k];
                    ainthashmap$entry[k] = inthashmap$entry;
                    inthashmap$entry = inthashmap$entry1;
                } while (inthashmap$entry1 != null);
            }
        }

    }

    public Object func_76049_d(int i) {
        this.field_76050_f.remove(i);
        IntHashMap$Entry inthashmap$entry = this.func_76036_e(i);

        return inthashmap$entry == null ? null : inthashmap$entry.field_76033_b;
    }

    final IntHashMap$Entry func_76036_e(int i) {
        int j = func_76044_g(i);
        int k = func_76043_a(j, this.field_76055_a.length);
        IntHashMap$Entry inthashmap$entry = this.field_76055_a[k];

        IntHashMap$Entry inthashmap$entry1;
        IntHashMap$Entry inthashmap$entry2;

        for (inthashmap$entry2 = inthashmap$entry; inthashmap$entry2 != null; inthashmap$entry2 = inthashmap$entry1) {
            inthashmap$entry1 = inthashmap$entry2.field_76034_c;
            if (inthashmap$entry2.field_76035_a == i) {
                ++this.field_76052_e;
                --this.field_76053_b;
                if (inthashmap$entry == inthashmap$entry2) {
                    this.field_76055_a[k] = inthashmap$entry1;
                } else {
                    inthashmap$entry.field_76034_c = inthashmap$entry1;
                }

                return inthashmap$entry2;
            }

            inthashmap$entry = inthashmap$entry2;
        }

        return inthashmap$entry2;
    }

    public void func_76046_c() {
        ++this.field_76052_e;
        IntHashMap$Entry[] ainthashmap$entry = this.field_76055_a;

        for (int i = 0; i < ainthashmap$entry.length; ++i) {
            ainthashmap$entry[i] = null;
        }

        this.field_76053_b = 0;
    }

    private void func_76040_a(int i, int j, Object object, int k) {
        IntHashMap$Entry inthashmap$entry = this.field_76055_a[k];

        this.field_76055_a[k] = new IntHashMap$Entry(i, j, object, inthashmap$entry);
        if (this.field_76053_b++ >= this.field_76054_c) {
            this.func_76047_h(2 * this.field_76055_a.length);
        }

    }
}
