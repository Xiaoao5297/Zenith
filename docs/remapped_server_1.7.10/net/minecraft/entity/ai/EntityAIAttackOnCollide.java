package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIAttackOnCollide extends EntityAIBase {

    World field_75443_a;
    EntityCreature field_75441_b;
    int field_75439_d;
    double field_75440_e;
    boolean field_75437_f;
    PathEntity field_75438_g;
    Class field_75444_h;
    private int field_75445_i;
    private double field_151497_i;
    private double field_151495_j;
    private double field_151496_k;

    public EntityAIAttackOnCollide(EntityCreature entitycreature, Class oclass, double d0, boolean flag) {
        this(entitycreature, d0, flag);
        this.field_75444_h = oclass;
    }

    public EntityAIAttackOnCollide(EntityCreature entitycreature, double d0, boolean flag) {
        this.field_75441_b = entitycreature;
        this.field_75443_a = entitycreature.field_70170_p;
        this.field_75440_e = d0;
        this.field_75437_f = flag;
        this.func_75248_a(3);
    }

    @Override
    public boolean func_75250_a() {
        EntityLivingBase entitylivingbase = this.field_75441_b.func_70638_az();

        if (entitylivingbase == null) {
            return false;
        } else if (!entitylivingbase.func_70089_S()) {
            return false;
        } else if (this.field_75444_h != null && !this.field_75444_h.isAssignableFrom(entitylivingbase.getClass())) {
            return false;
        } else {
            this.field_75438_g = this.field_75441_b.func_70661_as().func_75494_a(entitylivingbase);
            return this.field_75438_g != null;
        }
    }

    @Override
    public boolean func_75253_b() {
        EntityLivingBase entitylivingbase = this.field_75441_b.func_70638_az();

        return entitylivingbase == null ? false : (!entitylivingbase.func_70089_S() ? false : (!this.field_75437_f ? !this.field_75441_b.func_70661_as().func_75500_f() : this.field_75441_b.func_110176_b(MathHelper.func_76128_c(entitylivingbase.field_70165_t), MathHelper.func_76128_c(entitylivingbase.field_70163_u), MathHelper.func_76128_c(entitylivingbase.field_70161_v))));
    }

    @Override
    public void func_75249_e() {
        this.field_75441_b.func_70661_as().func_75484_a(this.field_75438_g, this.field_75440_e);
        this.field_75445_i = 0;
    }

    @Override
    public void func_75251_c() {
        this.field_75441_b.func_70661_as().func_75499_g();
    }

    @Override
    public void func_75246_d() {
        EntityLivingBase entitylivingbase = this.field_75441_b.func_70638_az();

        this.field_75441_b.func_70671_ap().func_75651_a(entitylivingbase, 30.0F, 30.0F);
        double d0 = this.field_75441_b.func_70092_e(entitylivingbase.field_70165_t, entitylivingbase.field_70121_D.field_72338_b, entitylivingbase.field_70161_v);
        double d1 = (double) (this.field_75441_b.field_70130_N * 2.0F * this.field_75441_b.field_70130_N * 2.0F + entitylivingbase.field_70130_N);

        --this.field_75445_i;
        if ((this.field_75437_f || this.field_75441_b.func_70635_at().func_75522_a(entitylivingbase)) && this.field_75445_i <= 0 && (this.field_151497_i == 0.0D && this.field_151495_j == 0.0D && this.field_151496_k == 0.0D || entitylivingbase.func_70092_e(this.field_151497_i, this.field_151495_j, this.field_151496_k) >= 1.0D || this.field_75441_b.func_70681_au().nextFloat() < 0.05F)) {
            this.field_151497_i = entitylivingbase.field_70165_t;
            this.field_151495_j = entitylivingbase.field_70121_D.field_72338_b;
            this.field_151496_k = entitylivingbase.field_70161_v;
            this.field_75445_i = 4 + this.field_75441_b.func_70681_au().nextInt(7);
            if (d0 > 1024.0D) {
                this.field_75445_i += 10;
            } else if (d0 > 256.0D) {
                this.field_75445_i += 5;
            }

            if (!this.field_75441_b.func_70661_as().func_75497_a(entitylivingbase, this.field_75440_e)) {
                this.field_75445_i += 15;
            }
        }

        this.field_75439_d = Math.max(this.field_75439_d - 1, 0);
        if (d0 <= d1 && this.field_75439_d <= 20) {
            this.field_75439_d = 20;
            if (this.field_75441_b.func_70694_bm() != null) {
                this.field_75441_b.func_71038_i();
            }

            this.field_75441_b.func_70652_k(entitylivingbase);
        }

    }
}
