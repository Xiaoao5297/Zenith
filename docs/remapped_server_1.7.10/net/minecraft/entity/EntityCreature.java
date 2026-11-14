package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class EntityCreature extends EntityLiving {

    public static final UUID field_110179_h = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
    public static final AttributeModifier field_110181_i = (new AttributeModifier(EntityCreature.field_110179_h, "Fleeing speed bonus", 2.0D, 2)).func_111168_a(false);
    private PathEntity field_70786_d;
    protected Entity field_70789_a;
    protected boolean field_70787_b;
    protected int field_70788_c;
    private ChunkCoordinates field_70775_bC = new ChunkCoordinates(0, 0, 0);
    private float field_70772_bD = -1.0F;
    private EntityAIBase field_110178_bs = new EntityAIMoveTowardsRestriction(this, 1.0D);
    private boolean field_110180_bt;

    public EntityCreature(World world) {
        super(world);
    }

    protected boolean func_70780_i() {
        return false;
    }

    @Override
    protected void func_70626_be() {
        this.field_70170_p.field_72984_F.func_76320_a("ai");
        if (this.field_70788_c > 0 && --this.field_70788_c == 0) {
            IAttributeInstance iattributeinstance = this.func_110148_a(SharedMonsterAttributes.field_111263_d);

            iattributeinstance.func_111124_b(EntityCreature.field_110181_i);
        }

        this.field_70787_b = this.func_70780_i();
        float f = 16.0F;

        if (this.field_70789_a == null) {
            this.field_70789_a = this.func_70782_k();
            if (this.field_70789_a != null) {
                this.field_70786_d = this.field_70170_p.func_72865_a(this, this.field_70789_a, f, true, false, false, true);
            }
        } else if (this.field_70789_a.func_70089_S()) {
            float f1 = this.field_70789_a.func_70032_d(this);

            if (this.func_70685_l(this.field_70789_a)) {
                this.func_70785_a(this.field_70789_a, f1);
            }
        } else {
            this.field_70789_a = null;
        }

        if (this.field_70789_a instanceof EntityPlayerMP && ((EntityPlayerMP) this.field_70789_a).field_71134_c.func_73083_d()) {
            this.field_70789_a = null;
        }

        this.field_70170_p.field_72984_F.func_76319_b();
        if (!this.field_70787_b && this.field_70789_a != null && (this.field_70786_d == null || this.field_70146_Z.nextInt(20) == 0)) {
            this.field_70786_d = this.field_70170_p.func_72865_a(this, this.field_70789_a, f, true, false, false, true);
        } else if (!this.field_70787_b && (this.field_70786_d == null && this.field_70146_Z.nextInt(180) == 0 || this.field_70146_Z.nextInt(120) == 0 || this.field_70788_c > 0) && this.field_70708_bq < 100) {
            this.func_70779_j();
        }

        int i = MathHelper.func_76128_c(this.field_70121_D.field_72338_b + 0.5D);
        boolean flag = this.func_70090_H();
        boolean flag1 = this.func_70058_J();

        this.field_70125_A = 0.0F;
        if (this.field_70786_d != null && this.field_70146_Z.nextInt(100) != 0) {
            this.field_70170_p.field_72984_F.func_76320_a("followpath");
            Vec3 vec3 = this.field_70786_d.func_75878_a(this);
            double d0 = (double) (this.field_70130_N * 2.0F);

            while (vec3 != null && vec3.func_72445_d(this.field_70165_t, vec3.field_72448_b, this.field_70161_v) < d0 * d0) {
                this.field_70786_d.func_75875_a();
                if (this.field_70786_d.func_75879_b()) {
                    vec3 = null;
                    this.field_70786_d = null;
                } else {
                    vec3 = this.field_70786_d.func_75878_a(this);
                }
            }

            this.field_70703_bu = false;
            if (vec3 != null) {
                double d1 = vec3.field_72450_a - this.field_70165_t;
                double d2 = vec3.field_72449_c - this.field_70161_v;
                double d3 = vec3.field_72448_b - (double) i;
                float f2 = (float) (Math.atan2(d2, d1) * 180.0D / 3.1415927410125732D) - 90.0F;
                float f3 = MathHelper.func_76142_g(f2 - this.field_70177_z);

                this.field_70701_bs = (float) this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e();
                if (f3 > 30.0F) {
                    f3 = 30.0F;
                }

                if (f3 < -30.0F) {
                    f3 = -30.0F;
                }

                this.field_70177_z += f3;
                if (this.field_70787_b && this.field_70789_a != null) {
                    double d4 = this.field_70789_a.field_70165_t - this.field_70165_t;
                    double d5 = this.field_70789_a.field_70161_v - this.field_70161_v;
                    float f4 = this.field_70177_z;

                    this.field_70177_z = (float) (Math.atan2(d5, d4) * 180.0D / 3.1415927410125732D) - 90.0F;
                    f3 = (f4 - this.field_70177_z + 90.0F) * 3.1415927F / 180.0F;
                    this.field_70702_br = -MathHelper.func_76126_a(f3) * this.field_70701_bs * 1.0F;
                    this.field_70701_bs = MathHelper.func_76134_b(f3) * this.field_70701_bs * 1.0F;
                }

                if (d3 > 0.0D) {
                    this.field_70703_bu = true;
                }
            }

            if (this.field_70789_a != null) {
                this.func_70625_a(this.field_70789_a, 30.0F, 30.0F);
            }

            if (this.field_70123_F && !this.func_70781_l()) {
                this.field_70703_bu = true;
            }

            if (this.field_70146_Z.nextFloat() < 0.8F && (flag || flag1)) {
                this.field_70703_bu = true;
            }

            this.field_70170_p.field_72984_F.func_76319_b();
        } else {
            super.func_70626_be();
            this.field_70786_d = null;
        }
    }

    protected void func_70779_j() {
        this.field_70170_p.field_72984_F.func_76320_a("stroll");
        boolean flag = false;
        int i = -1;
        int j = -1;
        int k = -1;
        float f = -99999.0F;

        for (int l = 0; l < 10; ++l) {
            int i1 = MathHelper.func_76128_c(this.field_70165_t + (double) this.field_70146_Z.nextInt(13) - 6.0D);
            int j1 = MathHelper.func_76128_c(this.field_70163_u + (double) this.field_70146_Z.nextInt(7) - 3.0D);
            int k1 = MathHelper.func_76128_c(this.field_70161_v + (double) this.field_70146_Z.nextInt(13) - 6.0D);
            float f1 = this.func_70783_a(i1, j1, k1);

            if (f1 > f) {
                f = f1;
                i = i1;
                j = j1;
                k = k1;
                flag = true;
            }
        }

        if (flag) {
            this.field_70786_d = this.field_70170_p.func_72844_a(this, i, j, k, 10.0F, true, false, false, true);
        }

        this.field_70170_p.field_72984_F.func_76319_b();
    }

    protected void func_70785_a(Entity entity, float f) {}

    public float func_70783_a(int i, int j, int k) {
        return 0.0F;
    }

    protected Entity func_70782_k() {
        return null;
    }

    @Override
    public boolean func_70601_bi() {
        int i = MathHelper.func_76128_c(this.field_70165_t);
        int j = MathHelper.func_76128_c(this.field_70121_D.field_72338_b);
        int k = MathHelper.func_76128_c(this.field_70161_v);

        return super.func_70601_bi() && this.func_70783_a(i, j, k) >= 0.0F;
    }

    public boolean func_70781_l() {
        return this.field_70786_d != null;
    }

    public void func_70778_a(PathEntity pathentity) {
        this.field_70786_d = pathentity;
    }

    public Entity func_70777_m() {
        return this.field_70789_a;
    }

    public void func_70784_b(Entity entity) {
        this.field_70789_a = entity;
    }

    public boolean func_110173_bK() {
        return this.func_110176_b(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v));
    }

    public boolean func_110176_b(int i, int j, int k) {
        return this.field_70772_bD == -1.0F ? true : this.field_70775_bC.func_71569_e(i, j, k) < this.field_70772_bD * this.field_70772_bD;
    }

    public void func_110171_b(int i, int j, int k, int l) {
        this.field_70775_bC.func_71571_b(i, j, k);
        this.field_70772_bD = (float) l;
    }

    public ChunkCoordinates func_110172_bL() {
        return this.field_70775_bC;
    }

    public float func_110174_bM() {
        return this.field_70772_bD;
    }

    public void func_110177_bN() {
        this.field_70772_bD = -1.0F;
    }

    public boolean func_110175_bO() {
        return this.field_70772_bD != -1.0F;
    }

    @Override
    protected void func_110159_bB() {
        super.func_110159_bB();
        if (this.func_110167_bD() && this.func_110166_bE() != null && this.func_110166_bE().field_70170_p == this.field_70170_p) {
            Entity entity = this.func_110166_bE();

            this.func_110171_b((int) entity.field_70165_t, (int) entity.field_70163_u, (int) entity.field_70161_v, 5);
            float f = this.func_70032_d(entity);

            if (this instanceof EntityTameable && ((EntityTameable) this).func_70906_o()) {
                if (f > 10.0F) {
                    this.func_110160_i(true, true);
                }

                return;
            }

            if (!this.field_110180_bt) {
                this.field_70714_bg.func_75776_a(2, this.field_110178_bs);
                this.func_70661_as().func_75491_a(false);
                this.field_110180_bt = true;
            }

            this.func_142017_o(f);
            if (f > 4.0F) {
                this.func_70661_as().func_75497_a(entity, 1.0D);
            }

            if (f > 6.0F) {
                double d0 = (entity.field_70165_t - this.field_70165_t) / (double) f;
                double d1 = (entity.field_70163_u - this.field_70163_u) / (double) f;
                double d2 = (entity.field_70161_v - this.field_70161_v) / (double) f;

                this.field_70159_w += d0 * Math.abs(d0) * 0.4D;
                this.field_70181_x += d1 * Math.abs(d1) * 0.4D;
                this.field_70179_y += d2 * Math.abs(d2) * 0.4D;
            }

            if (f > 10.0F) {
                this.func_110160_i(true, true);
            }
        } else if (!this.func_110167_bD() && this.field_110180_bt) {
            this.field_110180_bt = false;
            this.field_70714_bg.func_85156_a(this.field_110178_bs);
            this.func_70661_as().func_75491_a(true);
            this.func_110177_bN();
        }

    }

    protected void func_142017_o(float f) {}
}
