package net.minecraft.util;

public class AxisAlignedBB {

    public double field_72340_a;
    public double field_72338_b;
    public double field_72339_c;
    public double field_72336_d;
    public double field_72337_e;
    public double field_72334_f;

    public static AxisAlignedBB func_72330_a(double d0, double d1, double d2, double d3, double d4, double d5) {
        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    protected AxisAlignedBB(double d0, double d1, double d2, double d3, double d4, double d5) {
        this.field_72340_a = d0;
        this.field_72338_b = d1;
        this.field_72339_c = d2;
        this.field_72336_d = d3;
        this.field_72337_e = d4;
        this.field_72334_f = d5;
    }

    public AxisAlignedBB func_72324_b(double d0, double d1, double d2, double d3, double d4, double d5) {
        this.field_72340_a = d0;
        this.field_72338_b = d1;
        this.field_72339_c = d2;
        this.field_72336_d = d3;
        this.field_72337_e = d4;
        this.field_72334_f = d5;
        return this;
    }

    public AxisAlignedBB func_72321_a(double d0, double d1, double d2) {
        double d3 = this.field_72340_a;
        double d4 = this.field_72338_b;
        double d5 = this.field_72339_c;
        double d6 = this.field_72336_d;
        double d7 = this.field_72337_e;
        double d8 = this.field_72334_f;

        if (d0 < 0.0D) {
            d3 += d0;
        }

        if (d0 > 0.0D) {
            d6 += d0;
        }

        if (d1 < 0.0D) {
            d4 += d1;
        }

        if (d1 > 0.0D) {
            d7 += d1;
        }

        if (d2 < 0.0D) {
            d5 += d2;
        }

        if (d2 > 0.0D) {
            d8 += d2;
        }

        return func_72330_a(d3, d4, d5, d6, d7, d8);
    }

    public AxisAlignedBB func_72314_b(double d0, double d1, double d2) {
        double d3 = this.field_72340_a - d0;
        double d4 = this.field_72338_b - d1;
        double d5 = this.field_72339_c - d2;
        double d6 = this.field_72336_d + d0;
        double d7 = this.field_72337_e + d1;
        double d8 = this.field_72334_f + d2;

        return func_72330_a(d3, d4, d5, d6, d7, d8);
    }

    public AxisAlignedBB func_111270_a(AxisAlignedBB axisalignedbb) {
        double d0 = Math.min(this.field_72340_a, axisalignedbb.field_72340_a);
        double d1 = Math.min(this.field_72338_b, axisalignedbb.field_72338_b);
        double d2 = Math.min(this.field_72339_c, axisalignedbb.field_72339_c);
        double d3 = Math.max(this.field_72336_d, axisalignedbb.field_72336_d);
        double d4 = Math.max(this.field_72337_e, axisalignedbb.field_72337_e);
        double d5 = Math.max(this.field_72334_f, axisalignedbb.field_72334_f);

        return func_72330_a(d0, d1, d2, d3, d4, d5);
    }

    public AxisAlignedBB func_72325_c(double d0, double d1, double d2) {
        return func_72330_a(this.field_72340_a + d0, this.field_72338_b + d1, this.field_72339_c + d2, this.field_72336_d + d0, this.field_72337_e + d1, this.field_72334_f + d2);
    }

    public double func_72316_a(AxisAlignedBB axisalignedbb, double d0) {
        if (axisalignedbb.field_72337_e > this.field_72338_b && axisalignedbb.field_72338_b < this.field_72337_e) {
            if (axisalignedbb.field_72334_f > this.field_72339_c && axisalignedbb.field_72339_c < this.field_72334_f) {
                double d1;

                if (d0 > 0.0D && axisalignedbb.field_72336_d <= this.field_72340_a) {
                    d1 = this.field_72340_a - axisalignedbb.field_72336_d;
                    if (d1 < d0) {
                        d0 = d1;
                    }
                }

                if (d0 < 0.0D && axisalignedbb.field_72340_a >= this.field_72336_d) {
                    d1 = this.field_72336_d - axisalignedbb.field_72340_a;
                    if (d1 > d0) {
                        d0 = d1;
                    }
                }

                return d0;
            } else {
                return d0;
            }
        } else {
            return d0;
        }
    }

    public double func_72323_b(AxisAlignedBB axisalignedbb, double d0) {
        if (axisalignedbb.field_72336_d > this.field_72340_a && axisalignedbb.field_72340_a < this.field_72336_d) {
            if (axisalignedbb.field_72334_f > this.field_72339_c && axisalignedbb.field_72339_c < this.field_72334_f) {
                double d1;

                if (d0 > 0.0D && axisalignedbb.field_72337_e <= this.field_72338_b) {
                    d1 = this.field_72338_b - axisalignedbb.field_72337_e;
                    if (d1 < d0) {
                        d0 = d1;
                    }
                }

                if (d0 < 0.0D && axisalignedbb.field_72338_b >= this.field_72337_e) {
                    d1 = this.field_72337_e - axisalignedbb.field_72338_b;
                    if (d1 > d0) {
                        d0 = d1;
                    }
                }

                return d0;
            } else {
                return d0;
            }
        } else {
            return d0;
        }
    }

    public double func_72322_c(AxisAlignedBB axisalignedbb, double d0) {
        if (axisalignedbb.field_72336_d > this.field_72340_a && axisalignedbb.field_72340_a < this.field_72336_d) {
            if (axisalignedbb.field_72337_e > this.field_72338_b && axisalignedbb.field_72338_b < this.field_72337_e) {
                double d1;

                if (d0 > 0.0D && axisalignedbb.field_72334_f <= this.field_72339_c) {
                    d1 = this.field_72339_c - axisalignedbb.field_72334_f;
                    if (d1 < d0) {
                        d0 = d1;
                    }
                }

                if (d0 < 0.0D && axisalignedbb.field_72339_c >= this.field_72334_f) {
                    d1 = this.field_72334_f - axisalignedbb.field_72339_c;
                    if (d1 > d0) {
                        d0 = d1;
                    }
                }

                return d0;
            } else {
                return d0;
            }
        } else {
            return d0;
        }
    }

    public boolean func_72326_a(AxisAlignedBB axisalignedbb) {
        return axisalignedbb.field_72336_d > this.field_72340_a && axisalignedbb.field_72340_a < this.field_72336_d ? (axisalignedbb.field_72337_e > this.field_72338_b && axisalignedbb.field_72338_b < this.field_72337_e ? axisalignedbb.field_72334_f > this.field_72339_c && axisalignedbb.field_72339_c < this.field_72334_f : false) : false;
    }

    public AxisAlignedBB func_72317_d(double d0, double d1, double d2) {
        this.field_72340_a += d0;
        this.field_72338_b += d1;
        this.field_72339_c += d2;
        this.field_72336_d += d0;
        this.field_72337_e += d1;
        this.field_72334_f += d2;
        return this;
    }

    public boolean func_72318_a(Vec3 vec3) {
        return vec3.field_72450_a > this.field_72340_a && vec3.field_72450_a < this.field_72336_d ? (vec3.field_72448_b > this.field_72338_b && vec3.field_72448_b < this.field_72337_e ? vec3.field_72449_c > this.field_72339_c && vec3.field_72449_c < this.field_72334_f : false) : false;
    }

    public double func_72320_b() {
        double d0 = this.field_72336_d - this.field_72340_a;
        double d1 = this.field_72337_e - this.field_72338_b;
        double d2 = this.field_72334_f - this.field_72339_c;

        return (d0 + d1 + d2) / 3.0D;
    }

    public AxisAlignedBB func_72331_e(double d0, double d1, double d2) {
        double d3 = this.field_72340_a + d0;
        double d4 = this.field_72338_b + d1;
        double d5 = this.field_72339_c + d2;
        double d6 = this.field_72336_d - d0;
        double d7 = this.field_72337_e - d1;
        double d8 = this.field_72334_f - d2;

        return func_72330_a(d3, d4, d5, d6, d7, d8);
    }

    public AxisAlignedBB func_72329_c() {
        return func_72330_a(this.field_72340_a, this.field_72338_b, this.field_72339_c, this.field_72336_d, this.field_72337_e, this.field_72334_f);
    }

    public MovingObjectPosition func_72327_a(Vec3 vec3, Vec3 vec31) {
        Vec3 vec32 = vec3.func_72429_b(vec31, this.field_72340_a);
        Vec3 vec33 = vec3.func_72429_b(vec31, this.field_72336_d);
        Vec3 vec34 = vec3.func_72435_c(vec31, this.field_72338_b);
        Vec3 vec35 = vec3.func_72435_c(vec31, this.field_72337_e);
        Vec3 vec36 = vec3.func_72434_d(vec31, this.field_72339_c);
        Vec3 vec37 = vec3.func_72434_d(vec31, this.field_72334_f);

        if (!this.func_72333_b(vec32)) {
            vec32 = null;
        }

        if (!this.func_72333_b(vec33)) {
            vec33 = null;
        }

        if (!this.func_72315_c(vec34)) {
            vec34 = null;
        }

        if (!this.func_72315_c(vec35)) {
            vec35 = null;
        }

        if (!this.func_72319_d(vec36)) {
            vec36 = null;
        }

        if (!this.func_72319_d(vec37)) {
            vec37 = null;
        }

        Vec3 vec38 = null;

        if (vec32 != null && (vec38 == null || vec3.func_72436_e(vec32) < vec3.func_72436_e(vec38))) {
            vec38 = vec32;
        }

        if (vec33 != null && (vec38 == null || vec3.func_72436_e(vec33) < vec3.func_72436_e(vec38))) {
            vec38 = vec33;
        }

        if (vec34 != null && (vec38 == null || vec3.func_72436_e(vec34) < vec3.func_72436_e(vec38))) {
            vec38 = vec34;
        }

        if (vec35 != null && (vec38 == null || vec3.func_72436_e(vec35) < vec3.func_72436_e(vec38))) {
            vec38 = vec35;
        }

        if (vec36 != null && (vec38 == null || vec3.func_72436_e(vec36) < vec3.func_72436_e(vec38))) {
            vec38 = vec36;
        }

        if (vec37 != null && (vec38 == null || vec3.func_72436_e(vec37) < vec3.func_72436_e(vec38))) {
            vec38 = vec37;
        }

        if (vec38 == null) {
            return null;
        } else {
            byte b0 = -1;

            if (vec38 == vec32) {
                b0 = 4;
            }

            if (vec38 == vec33) {
                b0 = 5;
            }

            if (vec38 == vec34) {
                b0 = 0;
            }

            if (vec38 == vec35) {
                b0 = 1;
            }

            if (vec38 == vec36) {
                b0 = 2;
            }

            if (vec38 == vec37) {
                b0 = 3;
            }

            return new MovingObjectPosition(0, 0, 0, b0, vec38);
        }
    }

    private boolean func_72333_b(Vec3 vec3) {
        return vec3 == null ? false : vec3.field_72448_b >= this.field_72338_b && vec3.field_72448_b <= this.field_72337_e && vec3.field_72449_c >= this.field_72339_c && vec3.field_72449_c <= this.field_72334_f;
    }

    private boolean func_72315_c(Vec3 vec3) {
        return vec3 == null ? false : vec3.field_72450_a >= this.field_72340_a && vec3.field_72450_a <= this.field_72336_d && vec3.field_72449_c >= this.field_72339_c && vec3.field_72449_c <= this.field_72334_f;
    }

    private boolean func_72319_d(Vec3 vec3) {
        return vec3 == null ? false : vec3.field_72450_a >= this.field_72340_a && vec3.field_72450_a <= this.field_72336_d && vec3.field_72448_b >= this.field_72338_b && vec3.field_72448_b <= this.field_72337_e;
    }

    public void func_72328_c(AxisAlignedBB axisalignedbb) {
        this.field_72340_a = axisalignedbb.field_72340_a;
        this.field_72338_b = axisalignedbb.field_72338_b;
        this.field_72339_c = axisalignedbb.field_72339_c;
        this.field_72336_d = axisalignedbb.field_72336_d;
        this.field_72337_e = axisalignedbb.field_72337_e;
        this.field_72334_f = axisalignedbb.field_72334_f;
    }

    public String toString() {
        return "box[" + this.field_72340_a + ", " + this.field_72338_b + ", " + this.field_72339_c + " -> " + this.field_72336_d + ", " + this.field_72337_e + ", " + this.field_72334_f + "]";
    }
}
