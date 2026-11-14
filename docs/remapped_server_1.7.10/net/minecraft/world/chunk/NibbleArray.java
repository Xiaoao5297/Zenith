package net.minecraft.world.chunk;

public class NibbleArray {

    public final byte[] field_76585_a;
    private final int field_76583_b;
    private final int field_76584_c;

    public NibbleArray(int i, int j) {
        this.field_76585_a = new byte[i >> 1];
        this.field_76583_b = j;
        this.field_76584_c = j + 4;
    }

    public NibbleArray(byte[] abyte, int i) {
        this.field_76585_a = abyte;
        this.field_76583_b = i;
        this.field_76584_c = i + 4;
    }

    public int func_76582_a(int i, int j, int k) {
        int l = j << this.field_76584_c | k << this.field_76583_b | i;
        int i1 = l >> 1;
        int j1 = l & 1;

        return j1 == 0 ? this.field_76585_a[i1] & 15 : this.field_76585_a[i1] >> 4 & 15;
    }

    public void func_76581_a(int i, int j, int k, int l) {
        int i1 = j << this.field_76584_c | k << this.field_76583_b | i;
        int j1 = i1 >> 1;
        int k1 = i1 & 1;

        if (k1 == 0) {
            this.field_76585_a[j1] = (byte) (this.field_76585_a[j1] & 240 | l & 15);
        } else {
            this.field_76585_a[j1] = (byte) (this.field_76585_a[j1] & 15 | (l & 15) << 4);
        }

    }
}
