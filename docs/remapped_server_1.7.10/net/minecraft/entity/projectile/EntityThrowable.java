package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition$MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class EntityThrowable extends Entity implements IProjectile {

    private int field_145788_c = -1;
    private int field_145786_d = -1;
    private int field_145787_e = -1;
    private Block field_145785_f;
    protected boolean field_70193_a;
    public int field_70191_b;
    private EntityLivingBase field_70192_c;
    private String field_85053_h;
    private int field_70194_h;
    private int field_70195_i;

    public EntityThrowable(World world) {
        super(world);
        this.func_70105_a(0.25F, 0.25F);
    }

    @Override
    protected void func_70088_a() {}

    public EntityThrowable(World world, EntityLivingBase entitylivingbase) {
        super(world);
        this.field_70192_c = entitylivingbase;
        this.func_70105_a(0.25F, 0.25F);
        this.func_70012_b(entitylivingbase.field_70165_t, entitylivingbase.field_70163_u + (double) entitylivingbase.func_70047_e(), entitylivingbase.field_70161_v, entitylivingbase.field_70177_z, entitylivingbase.field_70125_A);
        this.field_70165_t -= (double) (MathHelper.func_76134_b(this.field_70177_z / 180.0F * 3.1415927F) * 0.16F);
        this.field_70163_u -= 0.10000000149011612D;
        this.field_70161_v -= (double) (MathHelper.func_76126_a(this.field_70177_z / 180.0F * 3.1415927F) * 0.16F);
        this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        this.field_70129_M = 0.0F;
        float f = 0.4F;

        this.field_70159_w = (double) (-MathHelper.func_76126_a(this.field_70177_z / 180.0F * 3.1415927F) * MathHelper.func_76134_b(this.field_70125_A / 180.0F * 3.1415927F) * f);
        this.field_70179_y = (double) (MathHelper.func_76134_b(this.field_70177_z / 180.0F * 3.1415927F) * MathHelper.func_76134_b(this.field_70125_A / 180.0F * 3.1415927F) * f);
        this.field_70181_x = (double) (-MathHelper.func_76126_a((this.field_70125_A + this.func_70183_g()) / 180.0F * 3.1415927F) * f);
        this.func_70186_c(this.field_70159_w, this.field_70181_x, this.field_70179_y, this.func_70182_d(), 1.0F);
    }

    public EntityThrowable(World world, double d0, double d1, double d2) {
        super(world);
        this.field_70194_h = 0;
        this.func_70105_a(0.25F, 0.25F);
        this.func_70107_b(d0, d1, d2);
        this.field_70129_M = 0.0F;
    }

    protected float func_70182_d() {
        return 1.5F;
    }

    protected float func_70183_g() {
        return 0.0F;
    }

    @Override
    public void func_70186_c(double d0, double d1, double d2, float f, float f1) {
        float f2 = MathHelper.func_76133_a(d0 * d0 + d1 * d1 + d2 * d2);

        d0 /= (double) f2;
        d1 /= (double) f2;
        d2 /= (double) f2;
        d0 += this.field_70146_Z.nextGaussian() * 0.007499999832361937D * (double) f1;
        d1 += this.field_70146_Z.nextGaussian() * 0.007499999832361937D * (double) f1;
        d2 += this.field_70146_Z.nextGaussian() * 0.007499999832361937D * (double) f1;
        d0 *= (double) f;
        d1 *= (double) f;
        d2 *= (double) f;
        this.field_70159_w = d0;
        this.field_70181_x = d1;
        this.field_70179_y = d2;
        float f3 = MathHelper.func_76133_a(d0 * d0 + d2 * d2);

        this.field_70126_B = this.field_70177_z = (float) (Math.atan2(d0, d2) * 180.0D / 3.1415927410125732D);
        this.field_70127_C = this.field_70125_A = (float) (Math.atan2(d1, (double) f3) * 180.0D / 3.1415927410125732D);
        this.field_70194_h = 0;
    }

    @Override
    public void func_70071_h_() {
        this.field_70142_S = this.field_70165_t;
        this.field_70137_T = this.field_70163_u;
        this.field_70136_U = this.field_70161_v;
        super.func_70071_h_();
        if (this.field_70191_b > 0) {
            --this.field_70191_b;
        }

        if (this.field_70193_a) {
            if (this.field_70170_p.func_147439_a(this.field_145788_c, this.field_145786_d, this.field_145787_e) == this.field_145785_f) {
                ++this.field_70194_h;
                if (this.field_70194_h == 1200) {
                    this.func_70106_y();
                }

                return;
            }

            this.field_70193_a = false;
            this.field_70159_w *= (double) (this.field_70146_Z.nextFloat() * 0.2F);
            this.field_70181_x *= (double) (this.field_70146_Z.nextFloat() * 0.2F);
            this.field_70179_y *= (double) (this.field_70146_Z.nextFloat() * 0.2F);
            this.field_70194_h = 0;
            this.field_70195_i = 0;
        } else {
            ++this.field_70195_i;
        }

        Vec3 vec3 = Vec3.func_72443_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        Vec3 vec31 = Vec3.func_72443_a(this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y);
        MovingObjectPosition movingobjectposition = this.field_70170_p.func_72933_a(vec3, vec31);

        vec3 = Vec3.func_72443_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        vec31 = Vec3.func_72443_a(this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y);
        if (movingobjectposition != null) {
            vec31 = Vec3.func_72443_a(movingobjectposition.field_72307_f.field_72450_a, movingobjectposition.field_72307_f.field_72448_b, movingobjectposition.field_72307_f.field_72449_c);
        }

        if (!this.field_70170_p.field_72995_K) {
            Entity entity = null;
            List list = this.field_70170_p.func_72839_b(this, this.field_70121_D.func_72321_a(this.field_70159_w, this.field_70181_x, this.field_70179_y).func_72314_b(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;
            EntityLivingBase entitylivingbase = this.func_85052_h();

            for (int i = 0; i < list.size(); ++i) {
                Entity entity1 = (Entity) list.get(i);

                if (entity1.func_70067_L() && (entity1 != entitylivingbase || this.field_70195_i >= 5)) {
                    float f = 0.3F;
                    AxisAlignedBB axisalignedbb = entity1.field_70121_D.func_72314_b((double) f, (double) f, (double) f);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb.func_72327_a(vec3, vec31);

                    if (movingobjectposition1 != null) {
                        double d1 = vec3.func_72438_d(movingobjectposition1.field_72307_f);

                        if (d1 < d0 || d0 == 0.0D) {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }

            if (entity != null) {
                movingobjectposition = new MovingObjectPosition(entity);
            }
        }

        if (movingobjectposition != null) {
            if (movingobjectposition.field_72313_a == MovingObjectPosition$MovingObjectType.BLOCK && this.field_70170_p.func_147439_a(movingobjectposition.field_72311_b, movingobjectposition.field_72312_c, movingobjectposition.field_72309_d) == Blocks.field_150427_aO) {
                this.func_70063_aa();
            } else {
                this.func_70184_a(movingobjectposition);
            }
        }

        this.field_70165_t += this.field_70159_w;
        this.field_70163_u += this.field_70181_x;
        this.field_70161_v += this.field_70179_y;
        float f1 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);

        this.field_70177_z = (float) (Math.atan2(this.field_70159_w, this.field_70179_y) * 180.0D / 3.1415927410125732D);

        for (this.field_70125_A = (float) (Math.atan2(this.field_70181_x, (double) f1) * 180.0D / 3.1415927410125732D); this.field_70125_A - this.field_70127_C < -180.0F; this.field_70127_C -= 360.0F) {
            ;
        }

        while (this.field_70125_A - this.field_70127_C >= 180.0F) {
            this.field_70127_C += 360.0F;
        }

        while (this.field_70177_z - this.field_70126_B < -180.0F) {
            this.field_70126_B -= 360.0F;
        }

        while (this.field_70177_z - this.field_70126_B >= 180.0F) {
            this.field_70126_B += 360.0F;
        }

        this.field_70125_A = this.field_70127_C + (this.field_70125_A - this.field_70127_C) * 0.2F;
        this.field_70177_z = this.field_70126_B + (this.field_70177_z - this.field_70126_B) * 0.2F;
        float f2 = 0.99F;
        float f3 = this.func_70185_h();

        if (this.func_70090_H()) {
            for (int j = 0; j < 4; ++j) {
                float f4 = 0.25F;

                this.field_70170_p.func_72869_a("bubble", this.field_70165_t - this.field_70159_w * (double) f4, this.field_70163_u - this.field_70181_x * (double) f4, this.field_70161_v - this.field_70179_y * (double) f4, this.field_70159_w, this.field_70181_x, this.field_70179_y);
            }

            f2 = 0.8F;
        }

        this.field_70159_w *= (double) f2;
        this.field_70181_x *= (double) f2;
        this.field_70179_y *= (double) f2;
        this.field_70181_x -= (double) f3;
        this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
    }

    protected float func_70185_h() {
        return 0.03F;
    }

    protected abstract void func_70184_a(MovingObjectPosition movingobjectposition);

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74777_a("xTile", (short) this.field_145788_c);
        nbttagcompound.func_74777_a("yTile", (short) this.field_145786_d);
        nbttagcompound.func_74777_a("zTile", (short) this.field_145787_e);
        nbttagcompound.func_74774_a("inTile", (byte) Block.func_149682_b(this.field_145785_f));
        nbttagcompound.func_74774_a("shake", (byte) this.field_70191_b);
        nbttagcompound.func_74774_a("inGround", (byte) (this.field_70193_a ? 1 : 0));
        if ((this.field_85053_h == null || this.field_85053_h.length() == 0) && this.field_70192_c != null && this.field_70192_c instanceof EntityPlayer) {
            this.field_85053_h = this.field_70192_c.func_70005_c_();
        }

        nbttagcompound.func_74778_a("ownerName", this.field_85053_h == null ? "" : this.field_85053_h);
    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        this.field_145788_c = nbttagcompound.func_74765_d("xTile");
        this.field_145786_d = nbttagcompound.func_74765_d("yTile");
        this.field_145787_e = nbttagcompound.func_74765_d("zTile");
        this.field_145785_f = Block.func_149729_e(nbttagcompound.func_74771_c("inTile") & 255);
        this.field_70191_b = nbttagcompound.func_74771_c("shake") & 255;
        this.field_70193_a = nbttagcompound.func_74771_c("inGround") == 1;
        this.field_85053_h = nbttagcompound.func_74779_i("ownerName");
        if (this.field_85053_h != null && this.field_85053_h.length() == 0) {
            this.field_85053_h = null;
        }

    }

    public EntityLivingBase func_85052_h() {
        if (this.field_70192_c == null && this.field_85053_h != null && this.field_85053_h.length() > 0) {
            this.field_70192_c = this.field_70170_p.func_72924_a(this.field_85053_h);
        }

        return this.field_70192_c;
    }
}
