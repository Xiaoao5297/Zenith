package net.minecraft.world.gen.structure;

import net.minecraft.nbt.NBTTagIntArray;

public class StructureBoundingBox {

    public int field_78897_a;
    public int field_78895_b;
    public int field_78896_c;
    public int field_78893_d;
    public int field_78894_e;
    public int field_78892_f;

    public StructureBoundingBox() {}

    public StructureBoundingBox(int[] aint) {
        if (aint.length == 6) {
            this.field_78897_a = aint[0];
            this.field_78895_b = aint[1];
            this.field_78896_c = aint[2];
            this.field_78893_d = aint[3];
            this.field_78894_e = aint[4];
            this.field_78892_f = aint[5];
        }

    }

    public static StructureBoundingBox func_78887_a() {
        return new StructureBoundingBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public static StructureBoundingBox func_78889_a(int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int j2) {
        switch (j2) {
            case 0:
                return new StructureBoundingBox(i + l, j + i1, k + j1, i + k1 - 1 + l, j + l1 - 1 + i1, k + i2 - 1 + j1);
            case 1:
                return new StructureBoundingBox(i - i2 + 1 + j1, j + i1, k + l, i + j1, j + l1 - 1 + i1, k + k1 - 1 + l);
            case 2:
                return new StructureBoundingBox(i + l, j + i1, k - i2 + 1 + j1, i + k1 - 1 + l, j + l1 - 1 + i1, k + j1);
            case 3:
                return new StructureBoundingBox(i + j1, j + i1, k + l, i + i2 - 1 + j1, j + l1 - 1 + i1, k + k1 - 1 + l);
            default:
                return new StructureBoundingBox(i + l, j + i1, k + j1, i + k1 - 1 + l, j + l1 - 1 + i1, k + i2 - 1 + j1);
        }
    }

    public StructureBoundingBox(StructureBoundingBox structureboundingbox) {
        this.field_78897_a = structureboundingbox.field_78897_a;
        this.field_78895_b = structureboundingbox.field_78895_b;
        this.field_78896_c = structureboundingbox.field_78896_c;
        this.field_78893_d = structureboundingbox.field_78893_d;
        this.field_78894_e = structureboundingbox.field_78894_e;
        this.field_78892_f = structureboundingbox.field_78892_f;
    }

    public StructureBoundingBox(int i, int j, int k, int l, int i1, int j1) {
        this.field_78897_a = i;
        this.field_78895_b = j;
        this.field_78896_c = k;
        this.field_78893_d = l;
        this.field_78894_e = i1;
        this.field_78892_f = j1;
    }

    public StructureBoundingBox(int i, int j, int k, int l) {
        this.field_78897_a = i;
        this.field_78896_c = j;
        this.field_78893_d = k;
        this.field_78892_f = l;
        this.field_78895_b = 1;
        this.field_78894_e = 512;
    }

    public boolean func_78884_a(StructureBoundingBox structureboundingbox) {
        return this.field_78893_d >= structureboundingbox.field_78897_a && this.field_78897_a <= structureboundingbox.field_78893_d && this.field_78892_f >= structureboundingbox.field_78896_c && this.field_78896_c <= structureboundingbox.field_78892_f && this.field_78894_e >= structureboundingbox.field_78895_b && this.field_78895_b <= structureboundingbox.field_78894_e;
    }

    public boolean func_78885_a(int i, int j, int k, int l) {
        return this.field_78893_d >= i && this.field_78897_a <= k && this.field_78892_f >= j && this.field_78896_c <= l;
    }

    public void func_78888_b(StructureBoundingBox structureboundingbox) {
        this.field_78897_a = Math.min(this.field_78897_a, structureboundingbox.field_78897_a);
        this.field_78895_b = Math.min(this.field_78895_b, structureboundingbox.field_78895_b);
        this.field_78896_c = Math.min(this.field_78896_c, structureboundingbox.field_78896_c);
        this.field_78893_d = Math.max(this.field_78893_d, structureboundingbox.field_78893_d);
        this.field_78894_e = Math.max(this.field_78894_e, structureboundingbox.field_78894_e);
        this.field_78892_f = Math.max(this.field_78892_f, structureboundingbox.field_78892_f);
    }

    public void func_78886_a(int i, int j, int k) {
        this.field_78897_a += i;
        this.field_78895_b += j;
        this.field_78896_c += k;
        this.field_78893_d += i;
        this.field_78894_e += j;
        this.field_78892_f += k;
    }

    public boolean func_78890_b(int i, int j, int k) {
        return i >= this.field_78897_a && i <= this.field_78893_d && k >= this.field_78896_c && k <= this.field_78892_f && j >= this.field_78895_b && j <= this.field_78894_e;
    }

    public int func_78883_b() {
        return this.field_78893_d - this.field_78897_a + 1;
    }

    public int func_78882_c() {
        return this.field_78894_e - this.field_78895_b + 1;
    }

    public int func_78880_d() {
        return this.field_78892_f - this.field_78896_c + 1;
    }

    public int func_78881_e() {
        return this.field_78897_a + (this.field_78893_d - this.field_78897_a + 1) / 2;
    }

    public int func_78879_f() {
        return this.field_78895_b + (this.field_78894_e - this.field_78895_b + 1) / 2;
    }

    public int func_78891_g() {
        return this.field_78896_c + (this.field_78892_f - this.field_78896_c + 1) / 2;
    }

    public String toString() {
        return "(" + this.field_78897_a + ", " + this.field_78895_b + ", " + this.field_78896_c + "; " + this.field_78893_d + ", " + this.field_78894_e + ", " + this.field_78892_f + ")";
    }

    public NBTTagIntArray func_151535_h() {
        return new NBTTagIntArray(new int[]{this.field_78897_a, this.field_78895_b, this.field_78896_c, this.field_78893_d, this.field_78894_e, this.field_78892_f});
    }
}
