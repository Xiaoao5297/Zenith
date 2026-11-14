package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.MathHelper;

public class EntityMoveHelper {

    private EntityLiving field_75648_a;
    private double field_75646_b;
    private double field_75647_c;
    private double field_75644_d;
    private double field_75645_e;
    private boolean field_75643_f;

    public EntityMoveHelper(EntityLiving entityliving) {
        this.field_75648_a = entityliving;
        this.field_75646_b = entityliving.field_70165_t;
        this.field_75647_c = entityliving.field_70163_u;
        this.field_75644_d = entityliving.field_70161_v;
    }

    public boolean func_75640_a() {
        return this.field_75643_f;
    }

    public double func_75638_b() {
        return this.field_75645_e;
    }

    public void func_75642_a(double d0, double d1, double d2, double d3) {
        this.field_75646_b = d0;
        this.field_75647_c = d1;
        this.field_75644_d = d2;
        this.field_75645_e = d3;
        this.field_75643_f = true;
    }

    public void func_75641_c() {
        this.field_75648_a.func_70657_f(0.0F);
        if (this.field_75643_f) {
            this.field_75643_f = false;
            int i = MathHelper.func_76128_c(this.field_75648_a.field_70121_D.field_72338_b + 0.5D);
            double d0 = this.field_75646_b - this.field_75648_a.field_70165_t;
            double d1 = this.field_75644_d - this.field_75648_a.field_70161_v;
            double d2 = this.field_75647_c - (double) i;
            double d3 = d0 * d0 + d2 * d2 + d1 * d1;

            if (d3 >= 2.500000277905201E-7D) {
                float f = (float) (Math.atan2(d1, d0) * 180.0D / 3.1415927410125732D) - 90.0F;

                this.field_75648_a.field_70177_z = this.func_75639_a(this.field_75648_a.field_70177_z, f, 30.0F);
                this.field_75648_a.func_70659_e((float) (this.field_75645_e * this.field_75648_a.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e()));
                if (d2 > 0.0D && d0 * d0 + d1 * d1 < 1.0D) {
                    this.field_75648_a.func_70683_ar().func_75660_a();
                }

            }
        }
    }

    private float func_75639_a(float f, float f1, float f2) {
        float f3 = MathHelper.func_76142_g(f1 - f);

        if (f3 > f2) {
            f3 = f2;
        }

        if (f3 < -f2) {
            f3 = -f2;
        }

        return f + f3;
    }
}
