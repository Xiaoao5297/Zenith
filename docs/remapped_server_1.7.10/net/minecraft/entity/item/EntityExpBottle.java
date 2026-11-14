package net.minecraft.entity.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityExpBottle extends EntityThrowable {

    public EntityExpBottle(World world) {
        super(world);
    }

    public EntityExpBottle(World world, EntityLivingBase entitylivingbase) {
        super(world, entitylivingbase);
    }

    public EntityExpBottle(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    protected float func_70185_h() {
        return 0.07F;
    }

    @Override
    protected float func_70182_d() {
        return 0.7F;
    }

    @Override
    protected float func_70183_g() {
        return -20.0F;
    }

    @Override
    protected void func_70184_a(MovingObjectPosition movingobjectposition) {
        if (!this.field_70170_p.field_72995_K) {
            this.field_70170_p.func_72926_e(2002, (int) Math.round(this.field_70165_t), (int) Math.round(this.field_70163_u), (int) Math.round(this.field_70161_v), 0);
            int i = 3 + this.field_70170_p.field_73012_v.nextInt(5) + this.field_70170_p.field_73012_v.nextInt(5);

            while (i > 0) {
                int j = EntityXPOrb.func_70527_a(i);

                i -= j;
                this.field_70170_p.func_72838_d(new EntityXPOrb(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, j));
            }

            this.func_70106_y();
        }

    }
}
