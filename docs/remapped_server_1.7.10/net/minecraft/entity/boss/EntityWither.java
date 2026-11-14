package net.minecraft.entity.boss;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityWither extends EntityMob implements IRangedAttackMob {

    private float[] field_82220_d = new float[2];
    private float[] field_82221_e = new float[2];
    private float[] field_82217_f = new float[2];
    private float[] field_82218_g = new float[2];
    private int[] field_82223_h = new int[2];
    private int[] field_82224_i = new int[2];
    private int field_82222_j;
    private static final IEntitySelector field_82219_bJ = new EntityWither$1();

    public EntityWither(World world) {
        super(world);
        this.func_70606_j(this.func_110138_aP());
        this.func_70105_a(0.9F, 4.0F);
        this.field_70178_ae = true;
        this.func_70661_as().func_75495_e(true);
        this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
        this.field_70714_bg.func_75776_a(2, new EntityAIArrowAttack(this, 1.0D, 40, 20.0F));
        this.field_70714_bg.func_75776_a(5, new EntityAIWander(this, 1.0D));
        this.field_70714_bg.func_75776_a(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.field_70714_bg.func_75776_a(7, new EntityAILookIdle(this));
        this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, false));
        this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, false, false, EntityWither.field_82219_bJ));
        this.field_70728_aV = 50;
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_75682_a(17, new Integer(0));
        this.field_70180_af.func_75682_a(18, new Integer(0));
        this.field_70180_af.func_75682_a(19, new Integer(0));
        this.field_70180_af.func_75682_a(20, new Integer(0));
    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        nbttagcompound.func_74768_a("Invul", this.func_82212_n());
    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        this.func_82215_s(nbttagcompound.func_74762_e("Invul"));
    }

    @Override
    protected String func_70639_aQ() {
        return "mob.wither.idle";
    }

    @Override
    protected String func_70621_aR() {
        return "mob.wither.hurt";
    }

    @Override
    protected String func_70673_aS() {
        return "mob.wither.death";
    }

    @Override
    public void func_70636_d() {
        this.field_70181_x *= 0.6000000238418579D;
        double d0;
        double d1;
        double d2;

        if (!this.field_70170_p.field_72995_K && this.func_82203_t(0) > 0) {
            Entity entity = this.field_70170_p.func_73045_a(this.func_82203_t(0));

            if (entity != null) {
                if (this.field_70163_u < entity.field_70163_u || !this.func_82205_o() && this.field_70163_u < entity.field_70163_u + 5.0D) {
                    if (this.field_70181_x < 0.0D) {
                        this.field_70181_x = 0.0D;
                    }

                    this.field_70181_x += (0.5D - this.field_70181_x) * 0.6000000238418579D;
                }

                double d3 = entity.field_70165_t - this.field_70165_t;

                d0 = entity.field_70161_v - this.field_70161_v;
                d1 = d3 * d3 + d0 * d0;
                if (d1 > 9.0D) {
                    d2 = (double) MathHelper.func_76133_a(d1);
                    this.field_70159_w += (d3 / d2 * 0.5D - this.field_70159_w) * 0.6000000238418579D;
                    this.field_70179_y += (d0 / d2 * 0.5D - this.field_70179_y) * 0.6000000238418579D;
                }
            }
        }

        if (this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y > 0.05000000074505806D) {
            this.field_70177_z = (float) Math.atan2(this.field_70179_y, this.field_70159_w) * 57.295776F - 90.0F;
        }

        super.func_70636_d();

        int i;

        for (i = 0; i < 2; ++i) {
            this.field_82218_g[i] = this.field_82221_e[i];
            this.field_82217_f[i] = this.field_82220_d[i];
        }

        int j;

        for (i = 0; i < 2; ++i) {
            j = this.func_82203_t(i + 1);
            Entity entity1 = null;

            if (j > 0) {
                entity1 = this.field_70170_p.func_73045_a(j);
            }

            if (entity1 != null) {
                d0 = this.func_82214_u(i + 1);
                d1 = this.func_82208_v(i + 1);
                d2 = this.func_82213_w(i + 1);
                double d4 = entity1.field_70165_t - d0;
                double d5 = entity1.field_70163_u + (double) entity1.func_70047_e() - d1;
                double d6 = entity1.field_70161_v - d2;
                double d7 = (double) MathHelper.func_76133_a(d4 * d4 + d6 * d6);
                float f = (float) (Math.atan2(d6, d4) * 180.0D / 3.1415927410125732D) - 90.0F;
                float f1 = (float) (-(Math.atan2(d5, d7) * 180.0D / 3.1415927410125732D));

                this.field_82220_d[i] = this.func_82204_b(this.field_82220_d[i], f1, 40.0F);
                this.field_82221_e[i] = this.func_82204_b(this.field_82221_e[i], f, 10.0F);
            } else {
                this.field_82221_e[i] = this.func_82204_b(this.field_82221_e[i], this.field_70761_aq, 10.0F);
            }
        }

        boolean flag = this.func_82205_o();

        for (j = 0; j < 3; ++j) {
            double d8 = this.func_82214_u(j);
            double d9 = this.func_82208_v(j);
            double d10 = this.func_82213_w(j);

            this.field_70170_p.func_72869_a("smoke", d8 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, d9 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, d10 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, 0.0D, 0.0D, 0.0D);
            if (flag && this.field_70170_p.field_73012_v.nextInt(4) == 0) {
                this.field_70170_p.func_72869_a("mobSpell", d8 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, d9 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, d10 + this.field_70146_Z.nextGaussian() * 0.30000001192092896D, 0.699999988079071D, 0.699999988079071D, 0.5D);
            }
        }

        if (this.func_82212_n() > 0) {
            for (j = 0; j < 3; ++j) {
                this.field_70170_p.func_72869_a("mobSpell", this.field_70165_t + this.field_70146_Z.nextGaussian() * 1.0D, this.field_70163_u + (double) (this.field_70146_Z.nextFloat() * 3.3F), this.field_70161_v + this.field_70146_Z.nextGaussian() * 1.0D, 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D);
            }
        }

    }

    @Override
    protected void func_70619_bc() {
        int i;

        if (this.func_82212_n() > 0) {
            i = this.func_82212_n() - 1;
            if (i <= 0) {
                this.field_70170_p.func_72885_a(this, this.field_70165_t, this.field_70163_u + (double) this.func_70047_e(), this.field_70161_v, 7.0F, false, this.field_70170_p.func_82736_K().func_82766_b("mobGriefing"));
                this.field_70170_p.func_82739_e(1013, (int) this.field_70165_t, (int) this.field_70163_u, (int) this.field_70161_v, 0);
            }

            this.func_82215_s(i);
            if (this.field_70173_aa % 10 == 0) {
                this.func_70691_i(10.0F);
            }

        } else {
            super.func_70619_bc();

            int j;

            for (i = 1; i < 3; ++i) {
                if (this.field_70173_aa >= this.field_82223_h[i - 1]) {
                    this.field_82223_h[i - 1] = this.field_70173_aa + 10 + this.field_70146_Z.nextInt(10);
                    if (this.field_70170_p.field_73013_u == EnumDifficulty.NORMAL || this.field_70170_p.field_73013_u == EnumDifficulty.HARD) {
                        int k = i - 1;
                        int l = this.field_82224_i[i - 1];

                        this.field_82224_i[k] = this.field_82224_i[i - 1] + 1;
                        if (l > 15) {
                            float f = 10.0F;
                            float f1 = 5.0F;
                            double d0 = MathHelper.func_82716_a(this.field_70146_Z, this.field_70165_t - (double) f, this.field_70165_t + (double) f);
                            double d1 = MathHelper.func_82716_a(this.field_70146_Z, this.field_70163_u - (double) f1, this.field_70163_u + (double) f1);
                            double d2 = MathHelper.func_82716_a(this.field_70146_Z, this.field_70161_v - (double) f, this.field_70161_v + (double) f);

                            this.func_82209_a(i + 1, d0, d1, d2, true);
                            this.field_82224_i[i - 1] = 0;
                        }
                    }

                    j = this.func_82203_t(i);
                    if (j > 0) {
                        Entity entity = this.field_70170_p.func_73045_a(j);

                        if (entity != null && entity.func_70089_S() && this.func_70068_e(entity) <= 900.0D && this.func_70685_l(entity)) {
                            this.func_82216_a(i + 1, (EntityLivingBase) entity);
                            this.field_82223_h[i - 1] = this.field_70173_aa + 40 + this.field_70146_Z.nextInt(20);
                            this.field_82224_i[i - 1] = 0;
                        } else {
                            this.func_82211_c(i, 0);
                        }
                    } else {
                        List list = this.field_70170_p.func_82733_a(EntityLivingBase.class, this.field_70121_D.func_72314_b(20.0D, 8.0D, 20.0D), EntityWither.field_82219_bJ);

                        for (int i1 = 0; i1 < 10 && !list.isEmpty(); ++i1) {
                            EntityLivingBase entitylivingbase = (EntityLivingBase) list.get(this.field_70146_Z.nextInt(list.size()));

                            if (entitylivingbase != this && entitylivingbase.func_70089_S() && this.func_70685_l(entitylivingbase)) {
                                if (entitylivingbase instanceof EntityPlayer) {
                                    if (!((EntityPlayer) entitylivingbase).field_71075_bZ.field_75102_a) {
                                        this.func_82211_c(i, entitylivingbase.func_145782_y());
                                    }
                                } else {
                                    this.func_82211_c(i, entitylivingbase.func_145782_y());
                                }
                                break;
                            }

                            list.remove(entitylivingbase);
                        }
                    }
                }
            }

            if (this.func_70638_az() != null) {
                this.func_82211_c(0, this.func_70638_az().func_145782_y());
            } else {
                this.func_82211_c(0, 0);
            }

            if (this.field_82222_j > 0) {
                --this.field_82222_j;
                if (this.field_82222_j == 0 && this.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
                    i = MathHelper.func_76128_c(this.field_70163_u);
                    j = MathHelper.func_76128_c(this.field_70165_t);
                    int j1 = MathHelper.func_76128_c(this.field_70161_v);
                    boolean flag = false;

                    for (int k1 = -1; k1 <= 1; ++k1) {
                        for (int l1 = -1; l1 <= 1; ++l1) {
                            for (int i2 = 0; i2 <= 3; ++i2) {
                                int j2 = j + k1;
                                int k2 = i + i2;
                                int l2 = j1 + l1;
                                Block block = this.field_70170_p.func_147439_a(j2, k2, l2);

                                if (block.func_149688_o() != Material.field_151579_a && block != Blocks.field_150357_h && block != Blocks.field_150384_bq && block != Blocks.field_150378_br && block != Blocks.field_150483_bI) {
                                    flag = this.field_70170_p.func_147480_a(j2, k2, l2, true) || flag;
                                }
                            }
                        }
                    }

                    if (flag) {
                        this.field_70170_p.func_72889_a((EntityPlayer) null, 1012, (int) this.field_70165_t, (int) this.field_70163_u, (int) this.field_70161_v, 0);
                    }
                }
            }

            if (this.field_70173_aa % 20 == 0) {
                this.func_70691_i(1.0F);
            }

        }
    }

    public void func_82206_m() {
        this.func_82215_s(220);
        this.func_70606_j(this.func_110138_aP() / 3.0F);
    }

    @Override
    public void func_70110_aj() {}

    @Override
    public int func_70658_aO() {
        return 4;
    }

    private double func_82214_u(int i) {
        if (i <= 0) {
            return this.field_70165_t;
        } else {
            float f = (this.field_70761_aq + (float) (180 * (i - 1))) / 180.0F * 3.1415927F;
            float f1 = MathHelper.func_76134_b(f);

            return this.field_70165_t + (double) f1 * 1.3D;
        }
    }

    private double func_82208_v(int i) {
        return i <= 0 ? this.field_70163_u + 3.0D : this.field_70163_u + 2.2D;
    }

    private double func_82213_w(int i) {
        if (i <= 0) {
            return this.field_70161_v;
        } else {
            float f = (this.field_70761_aq + (float) (180 * (i - 1))) / 180.0F * 3.1415927F;
            float f1 = MathHelper.func_76126_a(f);

            return this.field_70161_v + (double) f1 * 1.3D;
        }
    }

    private float func_82204_b(float f, float f1, float f2) {
        float f3 = MathHelper.func_76142_g(f1 - f);

        if (f3 > f2) {
            f3 = f2;
        }

        if (f3 < -f2) {
            f3 = -f2;
        }

        return f + f3;
    }

    private void func_82216_a(int i, EntityLivingBase entitylivingbase) {
        this.func_82209_a(i, entitylivingbase.field_70165_t, entitylivingbase.field_70163_u + (double) entitylivingbase.func_70047_e() * 0.5D, entitylivingbase.field_70161_v, i == 0 && this.field_70146_Z.nextFloat() < 0.001F);
    }

    private void func_82209_a(int i, double d0, double d1, double d2, boolean flag) {
        this.field_70170_p.func_72889_a((EntityPlayer) null, 1014, (int) this.field_70165_t, (int) this.field_70163_u, (int) this.field_70161_v, 0);
        double d3 = this.func_82214_u(i);
        double d4 = this.func_82208_v(i);
        double d5 = this.func_82213_w(i);
        double d6 = d0 - d3;
        double d7 = d1 - d4;
        double d8 = d2 - d5;
        EntityWitherSkull entitywitherskull = new EntityWitherSkull(this.field_70170_p, this, d6, d7, d8);

        if (flag) {
            entitywitherskull.func_82343_e(true);
        }

        entitywitherskull.field_70163_u = d4;
        entitywitherskull.field_70165_t = d3;
        entitywitherskull.field_70161_v = d5;
        this.field_70170_p.func_72838_d(entitywitherskull);
    }

    @Override
    public void func_82196_d(EntityLivingBase entitylivingbase, float f) {
        this.func_82216_a(0, entitylivingbase);
    }

    @Override
    public boolean func_70097_a(DamageSource damagesource, float f) {
        if (this.func_85032_ar()) {
            return false;
        } else if (damagesource == DamageSource.field_76369_e) {
            return false;
        } else if (this.func_82212_n() > 0) {
            return false;
        } else {
            Entity entity;

            if (this.func_82205_o()) {
                entity = damagesource.func_76364_f();
                if (entity instanceof EntityArrow) {
                    return false;
                }
            }

            entity = damagesource.func_76346_g();
            if (entity != null && !(entity instanceof EntityPlayer) && entity instanceof EntityLivingBase && ((EntityLivingBase) entity).func_70668_bt() == this.func_70668_bt()) {
                return false;
            } else {
                if (this.field_82222_j <= 0) {
                    this.field_82222_j = 20;
                }

                for (int i = 0; i < this.field_82224_i.length; ++i) {
                    this.field_82224_i[i] += 3;
                }

                return super.func_70097_a(damagesource, f);
            }
        }
    }

    @Override
    protected void func_70628_a(boolean flag, int i) {
        this.func_145779_a(Items.field_151156_bN, 1);
        if (!this.field_70170_p.field_72995_K) {
            Iterator iterator = this.field_70170_p.func_72872_a(EntityPlayer.class, this.field_70121_D.func_72314_b(50.0D, 100.0D, 50.0D)).iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                entityplayer.func_71029_a(AchievementList.field_150964_J);
            }
        }

    }

    @Override
    protected void func_70623_bb() {
        this.field_70708_bq = 0;
    }

    @Override
    protected void func_70069_a(float f) {}

    @Override
    public void func_70690_d(PotionEffect potioneffect) {}

    @Override
    protected boolean func_70650_aV() {
        return true;
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(300.0D);
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.6000000238418579D);
        this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(40.0D);
    }

    public int func_82212_n() {
        return this.field_70180_af.func_75679_c(20);
    }

    public void func_82215_s(int i) {
        this.field_70180_af.func_75692_b(20, i);
    }

    public int func_82203_t(int i) {
        return this.field_70180_af.func_75679_c(17 + i);
    }

    public void func_82211_c(int i, int j) {
        this.field_70180_af.func_75692_b(17 + i, j);
    }

    public boolean func_82205_o() {
        return this.func_110143_aJ() <= this.func_110138_aP() / 2.0F;
    }

    @Override
    public EnumCreatureAttribute func_70668_bt() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    public void func_70078_a(Entity entity) {
        this.field_70154_o = null;
    }
}
