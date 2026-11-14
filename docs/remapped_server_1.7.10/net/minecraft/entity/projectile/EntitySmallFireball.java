package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySmallFireball extends EntityFireball {

    public EntitySmallFireball(World world) {
        super(world);
        this.func_70105_a(0.3125F, 0.3125F);
    }

    public EntitySmallFireball(World world, EntityLivingBase entitylivingbase, double d0, double d1, double d2) {
        super(world, entitylivingbase, d0, d1, d2);
        this.func_70105_a(0.3125F, 0.3125F);
    }

    public EntitySmallFireball(World world, double d0, double d1, double d2, double d3, double d4, double d5) {
        super(world, d0, d1, d2, d3, d4, d5);
        this.func_70105_a(0.3125F, 0.3125F);
    }

    @Override
    protected void func_70227_a(MovingObjectPosition movingobjectposition) {
        if (!this.field_70170_p.field_72995_K) {
            if (movingobjectposition.field_72308_g != null) {
                if (!movingobjectposition.field_72308_g.func_70045_F() && movingobjectposition.field_72308_g.func_70097_a(DamageSource.func_76362_a(this, this.field_70235_a), 5.0F)) {
                    movingobjectposition.field_72308_g.func_70015_d(5);
                }
            } else {
                int i = movingobjectposition.field_72311_b;
                int j = movingobjectposition.field_72312_c;
                int k = movingobjectposition.field_72309_d;

                switch (movingobjectposition.field_72310_e) {
                    case 0:
                        --j;
                        break;
                    case 1:
                        ++j;
                        break;
                    case 2:
                        --k;
                        break;
                    case 3:
                        ++k;
                        break;
                    case 4:
                        --i;
                        break;
                    case 5:
                        ++i;
                }

                if (this.field_70170_p.func_147437_c(i, j, k)) {
                    this.field_70170_p.func_147449_b(i, j, k, Blocks.field_150480_ab);
                }
            }

            this.func_70106_y();
        }

    }

    @Override
    public boolean func_70067_L() {
        return false;
    }

    @Override
    public boolean func_70097_a(DamageSource damagesource, float f) {
        return false;
    }
}
