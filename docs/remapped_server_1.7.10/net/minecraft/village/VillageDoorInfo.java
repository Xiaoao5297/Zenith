package net.minecraft.village;

public class VillageDoorInfo {

    public final int field_75481_a;
    public final int field_75479_b;
    public final int field_75480_c;
    public final int field_75477_d;
    public final int field_75478_e;
    public int field_75475_f;
    public boolean field_75476_g;
    private int field_75482_h;

    public VillageDoorInfo(int i, int j, int k, int l, int i1, int j1) {
        this.field_75481_a = i;
        this.field_75479_b = j;
        this.field_75480_c = k;
        this.field_75477_d = l;
        this.field_75478_e = i1;
        this.field_75475_f = j1;
    }

    public int func_75474_b(int i, int j, int k) {
        int l = i - this.field_75481_a;
        int i1 = j - this.field_75479_b;
        int j1 = k - this.field_75480_c;

        return l * l + i1 * i1 + j1 * j1;
    }

    public int func_75469_c(int i, int j, int k) {
        int l = i - this.field_75481_a - this.field_75477_d;
        int i1 = j - this.field_75479_b;
        int j1 = k - this.field_75480_c - this.field_75478_e;

        return l * l + i1 * i1 + j1 * j1;
    }

    public int func_75471_a() {
        return this.field_75481_a + this.field_75477_d;
    }

    public int func_75473_b() {
        return this.field_75479_b;
    }

    public int func_75472_c() {
        return this.field_75480_c + this.field_75478_e;
    }

    public boolean func_75467_a(int i, int j) {
        int k = i - this.field_75481_a;
        int l = j - this.field_75480_c;

        return k * this.field_75477_d + l * this.field_75478_e >= 0;
    }

    public void func_75466_d() {
        this.field_75482_h = 0;
    }

    public void func_75470_e() {
        ++this.field_75482_h;
    }

    public int func_75468_f() {
        return this.field_75482_h;
    }
}
