package net.minecraft.util;

public class Vec3 {

    public double field_72450_a;
    public double field_72448_b;
    public double field_72449_c;

    public static Vec3 func_72443_a(double d0, double d1, double d2) {
        return new Vec3(d0, d1, d2);
    }

    protected Vec3(double d0, double d1, double d2) {
        if (d0 == -0.0D) {
            d0 = 0.0D;
        }

        if (d1 == -0.0D) {
            d1 = 0.0D;
        }

        if (d2 == -0.0D) {
            d2 = 0.0D;
        }

        this.field_72450_a = d0;
        this.field_72448_b = d1;
        this.field_72449_c = d2;
    }

    protected Vec3 func_72439_b(double d0, double d1, double d2) {
        this.field_72450_a = d0;
        this.field_72448_b = d1;
        this.field_72449_c = d2;
        return this;
    }

    public Vec3 func_72432_b() {
        double d0 = (double) MathHelper.func_76133_a(this.field_72450_a * this.field_72450_a + this.field_72448_b * this.field_72448_b + this.field_72449_c * this.field_72449_c);

        return d0 < 1.0E-4D ? func_72443_a(0.0D, 0.0D, 0.0D) : func_72443_a(this.field_72450_a / d0, this.field_72448_b / d0, this.field_72449_c / d0);
    }

    public double func_72430_b(Vec3 vec3) {
        return this.field_72450_a * vec3.field_72450_a + this.field_72448_b * vec3.field_72448_b + this.field_72449_c * vec3.field_72449_c;
    }

    public Vec3 func_72441_c(double d0, double d1, double d2) {
        return func_72443_a(this.field_72450_a + d0, this.field_72448_b + d1, this.field_72449_c + d2);
    }

    public double func_72438_d(Vec3 vec3) {
        double d0 = vec3.field_72450_a - this.field_72450_a;
        double d1 = vec3.field_72448_b - this.field_72448_b;
        double d2 = vec3.field_72449_c - this.field_72449_c;

        return (double) MathHelper.func_76133_a(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public double func_72436_e(Vec3 vec3) {
        double d0 = vec3.field_72450_a - this.field_72450_a;
        double d1 = vec3.field_72448_b - this.field_72448_b;
        double d2 = vec3.field_72449_c - this.field_72449_c;

        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double func_72445_d(double d0, double d1, double d2) {
        double d3 = d0 - this.field_72450_a;
        double d4 = d1 - this.field_72448_b;
        double d5 = d2 - this.field_72449_c;

        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    public double func_72433_c() {
        return (double) MathHelper.func_76133_a(this.field_72450_a * this.field_72450_a + this.field_72448_b * this.field_72448_b + this.field_72449_c * this.field_72449_c);
    }

    public Vec3 func_72429_b(Vec3 vec3, double d0) {
        double d1 = vec3.field_72450_a - this.field_72450_a;
        double d2 = vec3.field_72448_b - this.field_72448_b;
        double d3 = vec3.field_72449_c - this.field_72449_c;

        if (d1 * d1 < 1.0000000116860974E-7D) {
            return null;
        } else {
            double d4 = (d0 - this.field_72450_a) / d1;

            return d4 >= 0.0D && d4 <= 1.0D ? func_72443_a(this.field_72450_a + d1 * d4, this.field_72448_b + d2 * d4, this.field_72449_c + d3 * d4) : null;
        }
    }

    public Vec3 func_72435_c(Vec3 vec3, double d0) {
        double d1 = vec3.field_72450_a - this.field_72450_a;
        double d2 = vec3.field_72448_b - this.field_72448_b;
        double d3 = vec3.field_72449_c - this.field_72449_c;

        if (d2 * d2 < 1.0000000116860974E-7D) {
            return null;
        } else {
            double d4 = (d0 - this.field_72448_b) / d2;

            return d4 >= 0.0D && d4 <= 1.0D ? func_72443_a(this.field_72450_a + d1 * d4, this.field_72448_b + d2 * d4, this.field_72449_c + d3 * d4) : null;
        }
    }

    public Vec3 func_72434_d(Vec3 vec3, double d0) {
        double d1 = vec3.field_72450_a - this.field_72450_a;
        double d2 = vec3.field_72448_b - this.field_72448_b;
        double d3 = vec3.field_72449_c - this.field_72449_c;

        if (d3 * d3 < 1.0000000116860974E-7D) {
            return null;
        } else {
            double d4 = (d0 - this.field_72449_c) / d3;

            return d4 >= 0.0D && d4 <= 1.0D ? func_72443_a(this.field_72450_a + d1 * d4, this.field_72448_b + d2 * d4, this.field_72449_c + d3 * d4) : null;
        }
    }

    public String toString() {
        return "(" + this.field_72450_a + ", " + this.field_72448_b + ", " + this.field_72449_c + ")";
    }

    public void func_72440_a(float f) {
        float f1 = MathHelper.func_76134_b(f);
        float f2 = MathHelper.func_76126_a(f);
        double d0 = this.field_72450_a;
        double d1 = this.field_72448_b * (double) f1 + this.field_72449_c * (double) f2;
        double d2 = this.field_72449_c * (double) f1 - this.field_72448_b * (double) f2;

        this.func_72439_b(d0, d1, d2);
    }

    public void func_72442_b(float f) {
        float f1 = MathHelper.func_76134_b(f);
        float f2 = MathHelper.func_76126_a(f);
        double d0 = this.field_72450_a * (double) f1 + this.field_72449_c * (double) f2;
        double d1 = this.field_72448_b;
        double d2 = this.field_72449_c * (double) f1 - this.field_72450_a * (double) f2;

        this.func_72439_b(d0, d1, d2);
    }
}
