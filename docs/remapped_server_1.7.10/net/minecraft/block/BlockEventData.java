package net.minecraft.block;

public class BlockEventData {

    private int field_151348_a;
    private int field_151346_b;
    private int field_151347_c;
    private Block field_151344_d;
    private int field_151345_e;
    private int field_151343_f;

    public BlockEventData(int i, int j, int k, Block block, int l, int i1) {
        this.field_151348_a = i;
        this.field_151346_b = j;
        this.field_151347_c = k;
        this.field_151345_e = l;
        this.field_151343_f = i1;
        this.field_151344_d = block;
    }

    public int func_151340_a() {
        return this.field_151348_a;
    }

    public int func_151342_b() {
        return this.field_151346_b;
    }

    public int func_151341_c() {
        return this.field_151347_c;
    }

    public int func_151339_d() {
        return this.field_151345_e;
    }

    public int func_151338_e() {
        return this.field_151343_f;
    }

    public Block func_151337_f() {
        return this.field_151344_d;
    }

    public boolean equals(Object object) {
        if (!(object instanceof BlockEventData)) {
            return false;
        } else {
            BlockEventData blockeventdata = (BlockEventData) object;

            return this.field_151348_a == blockeventdata.field_151348_a && this.field_151346_b == blockeventdata.field_151346_b && this.field_151347_c == blockeventdata.field_151347_c && this.field_151345_e == blockeventdata.field_151345_e && this.field_151343_f == blockeventdata.field_151343_f && this.field_151344_d == blockeventdata.field_151344_d;
        }
    }

    public String toString() {
        return "TE(" + this.field_151348_a + "," + this.field_151346_b + "," + this.field_151347_c + ")," + this.field_151345_e + "," + this.field_151343_f + "," + this.field_151344_d;
    }
}
