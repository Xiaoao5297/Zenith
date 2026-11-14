package net.minecraft.pathfinding;

import net.minecraft.util.MathHelper;

public class PathPoint {

    public final int field_75839_a;
    public final int field_75837_b;
    public final int field_75838_c;
    private final int field_75840_j;
    int field_75835_d = -1;
    float field_75836_e;
    float field_75833_f;
    float field_75834_g;
    PathPoint field_75841_h;
    public boolean field_75842_i;

    public PathPoint(int i, int j, int k) {
        this.field_75839_a = i;
        this.field_75837_b = j;
        this.field_75838_c = k;
        this.field_75840_j = func_75830_a(i, j, k);
    }

    public static int func_75830_a(int i, int j, int k) {
        return j & 255 | (i & 32767) << 8 | (k & 32767) << 24 | (i < 0 ? Integer.MIN_VALUE : 0) | (k < 0 ? '\u8000' : 0);
    }

    public float func_75829_a(PathPoint pathpoint) {
        float f = (float) (pathpoint.field_75839_a - this.field_75839_a);
        float f1 = (float) (pathpoint.field_75837_b - this.field_75837_b);
        float f2 = (float) (pathpoint.field_75838_c - this.field_75838_c);

        return MathHelper.func_76129_c(f * f + f1 * f1 + f2 * f2);
    }

    public float func_75832_b(PathPoint pathpoint) {
        float f = (float) (pathpoint.field_75839_a - this.field_75839_a);
        float f1 = (float) (pathpoint.field_75837_b - this.field_75837_b);
        float f2 = (float) (pathpoint.field_75838_c - this.field_75838_c);

        return f * f + f1 * f1 + f2 * f2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof PathPoint)) {
            return false;
        } else {
            PathPoint pathpoint = (PathPoint) object;

            return this.field_75840_j == pathpoint.field_75840_j && this.field_75839_a == pathpoint.field_75839_a && this.field_75837_b == pathpoint.field_75837_b && this.field_75838_c == pathpoint.field_75838_c;
        }
    }

    public int hashCode() {
        return this.field_75840_j;
    }

    public boolean func_75831_a() {
        return this.field_75835_d >= 0;
    }

    public String toString() {
        return this.field_75839_a + ", " + this.field_75837_b + ", " + this.field_75838_c;
    }
}
