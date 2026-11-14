package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;

public abstract class EntityAITarget extends EntityAIBase {

    protected EntityCreature field_75299_d;
    protected boolean field_75297_f;
    private boolean field_75303_a;
    private int field_75301_b;
    private int field_75302_c;
    private int field_75298_g;

    public EntityAITarget(EntityCreature entitycreature, boolean flag) {
        this(entitycreature, flag, false);
    }

    public EntityAITarget(EntityCreature entitycreature, boolean flag, boolean flag1) {
        this.field_75299_d = entitycreature;
        this.field_75297_f = flag;
        this.field_75303_a = flag1;
    }

    @Override
    public boolean func_75253_b() {
        EntityLivingBase entitylivingbase = this.field_75299_d.func_70638_az();

        if (entitylivingbase == null) {
            return false;
        } else if (!entitylivingbase.func_70089_S()) {
            return false;
        } else {
            double d0 = this.func_111175_f();

            if (this.field_75299_d.func_70068_e(entitylivingbase) > d0 * d0) {
                return false;
            } else {
                if (this.field_75297_f) {
                    if (this.field_75299_d.func_70635_at().func_75522_a(entitylivingbase)) {
                        this.field_75298_g = 0;
                    } else if (++this.field_75298_g > 60) {
                        return false;
                    }
                }

                return !(entitylivingbase instanceof EntityPlayerMP) || !((EntityPlayerMP) entitylivingbase).field_71134_c.func_73083_d();
            }
        }
    }

    protected double func_111175_f() {
        IAttributeInstance iattributeinstance = this.field_75299_d.func_110148_a(SharedMonsterAttributes.field_111265_b);

        return iattributeinstance == null ? 16.0D : iattributeinstance.func_111126_e();
    }

    @Override
    public void func_75249_e() {
        this.field_75301_b = 0;
        this.field_75302_c = 0;
        this.field_75298_g = 0;
    }

    @Override
    public void func_75251_c() {
        this.field_75299_d.func_70624_b((EntityLivingBase) null);
    }

    protected boolean func_75296_a(EntityLivingBase entitylivingbase, boolean flag) {
        if (entitylivingbase == null) {
            return false;
        } else if (entitylivingbase == this.field_75299_d) {
            return false;
        } else if (!entitylivingbase.func_70089_S()) {
            return false;
        } else if (!this.field_75299_d.func_70686_a(entitylivingbase.getClass())) {
            return false;
        } else {
            if (this.field_75299_d instanceof IEntityOwnable && StringUtils.isNotEmpty(((IEntityOwnable) this.field_75299_d).func_152113_b())) {
                if (entitylivingbase instanceof IEntityOwnable && ((IEntityOwnable) this.field_75299_d).func_152113_b().equals(((IEntityOwnable) entitylivingbase).func_152113_b())) {
                    return false;
                }

                if (entitylivingbase == ((IEntityOwnable) this.field_75299_d).func_70902_q()) {
                    return false;
                }
            } else if (entitylivingbase instanceof EntityPlayer && !flag && ((EntityPlayer) entitylivingbase).field_71075_bZ.field_75102_a) {
                return false;
            }

            if (!this.field_75299_d.func_110176_b(MathHelper.func_76128_c(entitylivingbase.field_70165_t), MathHelper.func_76128_c(entitylivingbase.field_70163_u), MathHelper.func_76128_c(entitylivingbase.field_70161_v))) {
                return false;
            } else if (this.field_75297_f && !this.field_75299_d.func_70635_at().func_75522_a(entitylivingbase)) {
                return false;
            } else {
                if (this.field_75303_a) {
                    if (--this.field_75302_c <= 0) {
                        this.field_75301_b = 0;
                    }

                    if (this.field_75301_b == 0) {
                        this.field_75301_b = this.func_75295_a(entitylivingbase) ? 1 : 2;
                    }

                    if (this.field_75301_b == 2) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    private boolean func_75295_a(EntityLivingBase entitylivingbase) {
        this.field_75302_c = 10 + this.field_75299_d.func_70681_au().nextInt(5);
        PathEntity pathentity = this.field_75299_d.func_70661_as().func_75494_a(entitylivingbase);

        if (pathentity == null) {
            return false;
        } else {
            PathPoint pathpoint = pathentity.func_75870_c();

            if (pathpoint == null) {
                return false;
            } else {
                int i = pathpoint.field_75839_a - MathHelper.func_76128_c(entitylivingbase.field_70165_t);
                int j = pathpoint.field_75838_c - MathHelper.func_76128_c(entitylivingbase.field_70161_v);

                return (double) (i * i + j * j) <= 2.25D;
            }
        }
    }
}
