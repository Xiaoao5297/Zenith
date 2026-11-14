package net.minecraft.util;

public enum EnumFacing {

    DOWN(0, 1, 0, -1, 0), UP(1, 0, 0, 1, 0), NORTH(2, 3, 0, 0, -1), SOUTH(3, 2, 0, 0, 1), EAST(4, 5, -1, 0, 0), WEST(5, 4, 1, 0, 0);

    private final int field_82603_g;
    private final int field_82613_h;
    private final int field_82614_i;
    private final int field_82611_j;
    private final int field_82612_k;
    private static final EnumFacing[] field_82609_l = new EnumFacing[6];

    private EnumFacing(int i, int j, int k, int l, int i1) {
        this.field_82603_g = i;
        this.field_82613_h = j;
        this.field_82614_i = k;
        this.field_82611_j = l;
        this.field_82612_k = i1;
    }

    public int func_82601_c() {
        return this.field_82614_i;
    }

    public int func_96559_d() {
        return this.field_82611_j;
    }

    public int func_82599_e() {
        return this.field_82612_k;
    }

    public static EnumFacing func_82600_a(int i) {
        return EnumFacing.field_82609_l[i % EnumFacing.field_82609_l.length];
    }

    static {
        EnumFacing[] aenumfacing = values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing enumfacing = aenumfacing[j];

            EnumFacing.field_82609_l[enumfacing.field_82603_g] = enumfacing;
        }

    }
}
