package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityEgg extends EntityThrowable {

    public EntityEgg(World world) {
        super(world);
    }

    public EntityEgg(World world, EntityLivingBase entitylivingbase) {
        super(world, entitylivingbase);
    }

    public EntityEgg(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    protected void func_70184_a(MovingObjectPosition movingobjectposition) {
        if (movingobjectposition.field_72308_g != null) {
            movingobjectposition.field_72308_g.func_70097_a(DamageSource.func_76356_a(this, this.func_85052_h()), 0.0F);
        }

        int i;

        if (!this.field_70170_p.field_72995_K && this.field_70146_Z.nextInt(8) == 0) {
            i = 1;
            if (this.field_70146_Z.nextInt(32) == 0) {
                i = 4;
            }

            for (int j = 0; j < i; ++j) {
                EntityChicken entitychicken = new EntityChicken(this.field_70170_p);

                entitychicken.func_70873_a(-24000);
                entitychicken.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, 0.0F);
                this.field_70170_p.func_72838_d(entitychicken);
            }
        }

        for (i = 0; i < 8; ++i) {
            this.field_70170_p.func_72869_a("snowballpoof", this.field_70165_t, this.field_70163_u, this.field_70161_v, 0.0D, 0.0D, 0.0D);
        }

        if (!this.field_70170_p.field_72995_K) {
            this.func_70106_y();
        }

    }
}
