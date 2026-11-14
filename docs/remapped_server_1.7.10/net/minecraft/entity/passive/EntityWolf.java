package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBeg;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityWolf extends EntityTameable {

    private float field_70926_e;
    private float field_70924_f;
    private boolean field_70925_g;
    private boolean field_70928_h;
    private float field_70929_i;
    private float field_70927_j;

    public EntityWolf(World world) {
        super(world);
        this.func_70105_a(0.6F, 0.8F);
        this.func_70661_as().func_75491_a(true);
        this.field_70714_bg.func_75776_a(1, new EntityAISwimming(this));
        this.field_70714_bg.func_75776_a(2, this.field_70911_d);
        this.field_70714_bg.func_75776_a(3, new EntityAILeapAtTarget(this, 0.4F));
        this.field_70714_bg.func_75776_a(4, new EntityAIAttackOnCollide(this, 1.0D, true));
        this.field_70714_bg.func_75776_a(5, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.field_70714_bg.func_75776_a(6, new EntityAIMate(this, 1.0D));
        this.field_70714_bg.func_75776_a(7, new EntityAIWander(this, 1.0D));
        this.field_70714_bg.func_75776_a(8, new EntityAIBeg(this, 8.0F));
        this.field_70714_bg.func_75776_a(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.field_70714_bg.func_75776_a(9, new EntityAILookIdle(this));
        this.field_70715_bh.func_75776_a(1, new EntityAIOwnerHurtByTarget(this));
        this.field_70715_bh.func_75776_a(2, new EntityAIOwnerHurtTarget(this));
        this.field_70715_bh.func_75776_a(3, new EntityAIHurtByTarget(this, true));
        this.field_70715_bh.func_75776_a(4, new EntityAITargetNonTamed(this, EntitySheep.class, 200, false));
        this.func_70903_f(false);
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.30000001192092896D);
        if (this.func_70909_n()) {
            this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(20.0D);
        } else {
            this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(8.0D);
        }

    }

    @Override
    public boolean func_70650_aV() {
        return true;
    }

    @Override
    public void func_70624_b(EntityLivingBase entitylivingbase) {
        super.func_70624_b(entitylivingbase);
        if (entitylivingbase == null) {
            this.func_70916_h(false);
        } else if (!this.func_70909_n()) {
            this.func_70916_h(true);
        }

    }

    @Override
    protected void func_70629_bd() {
        this.field_70180_af.func_75692_b(18, this.func_110143_aJ());
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_75682_a(18, new Float(this.func_110143_aJ()));
        this.field_70180_af.func_75682_a(19, new Byte((byte) 0));
        this.field_70180_af.func_75682_a(20, new Byte((byte) BlockColored.func_150032_b(1)));
    }

    @Override
    protected void func_145780_a(int i, int j, int k, Block block) {
        this.func_85030_a("mob.wolf.step", 0.15F, 1.0F);
    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        nbttagcompound.func_74757_a("Angry", this.func_70919_bu());
        nbttagcompound.func_74774_a("CollarColor", (byte) this.func_82186_bH());
    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        this.func_70916_h(nbttagcompound.func_74767_n("Angry"));
        if (nbttagcompound.func_150297_b("CollarColor", 99)) {
            this.func_82185_r(nbttagcompound.func_74771_c("CollarColor"));
        }

    }

    @Override
    protected String func_70639_aQ() {
        return this.func_70919_bu() ? "mob.wolf.growl" : (this.field_70146_Z.nextInt(3) == 0 ? (this.func_70909_n() && this.field_70180_af.func_111145_d(18) < 10.0F ? "mob.wolf.whine" : "mob.wolf.panting") : "mob.wolf.bark");
    }

    @Override
    protected String func_70621_aR() {
        return "mob.wolf.hurt";
    }

    @Override
    protected String func_70673_aS() {
        return "mob.wolf.death";
    }

    @Override
    protected float func_70599_aP() {
        return 0.4F;
    }

    @Override
    protected Item func_146068_u() {
        return Item.func_150899_d(-1);
    }

    @Override
    public void func_70636_d() {
        super.func_70636_d();
        if (!this.field_70170_p.field_72995_K && this.field_70925_g && !this.field_70928_h && !this.func_70781_l() && this.field_70122_E) {
            this.field_70928_h = true;
            this.field_70929_i = 0.0F;
            this.field_70927_j = 0.0F;
            this.field_70170_p.func_72960_a(this, (byte) 8);
        }

    }

    @Override
    public void func_70071_h_() {
        super.func_70071_h_();
        this.field_70924_f = this.field_70926_e;
        if (this.func_70922_bv()) {
            this.field_70926_e += (1.0F - this.field_70926_e) * 0.4F;
        } else {
            this.field_70926_e += (0.0F - this.field_70926_e) * 0.4F;
        }

        if (this.func_70922_bv()) {
            this.field_70700_bx = 10;
        }

        if (this.func_70026_G()) {
            this.field_70925_g = true;
            this.field_70928_h = false;
            this.field_70929_i = 0.0F;
            this.field_70927_j = 0.0F;
        } else if ((this.field_70925_g || this.field_70928_h) && this.field_70928_h) {
            if (this.field_70929_i == 0.0F) {
                this.func_85030_a("mob.wolf.shake", this.func_70599_aP(), (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
            }

            this.field_70927_j = this.field_70929_i;
            this.field_70929_i += 0.05F;
            if (this.field_70927_j >= 2.0F) {
                this.field_70925_g = false;
                this.field_70928_h = false;
                this.field_70927_j = 0.0F;
                this.field_70929_i = 0.0F;
            }

            if (this.field_70929_i > 0.4F) {
                float f = (float) this.field_70121_D.field_72338_b;
                int i = (int) (MathHelper.func_76126_a((this.field_70929_i - 0.4F) * 3.1415927F) * 7.0F);

                for (int j = 0; j < i; ++j) {
                    float f1 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N * 0.5F;
                    float f2 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N * 0.5F;

                    this.field_70170_p.func_72869_a("splash", this.field_70165_t + (double) f1, (double) (f + 0.8F), this.field_70161_v + (double) f2, this.field_70159_w, this.field_70181_x, this.field_70179_y);
                }
            }
        }

    }

    @Override
    public float func_70047_e() {
        return this.field_70131_O * 0.8F;
    }

    @Override
    public int func_70646_bf() {
        return this.func_70906_o() ? 20 : super.func_70646_bf();
    }

    @Override
    public boolean func_70097_a(DamageSource damagesource, float f) {
        if (this.func_85032_ar()) {
            return false;
        } else {
            Entity entity = damagesource.func_76346_g();

            this.field_70911_d.func_75270_a(false);
            if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow)) {
                f = (f + 1.0F) / 2.0F;
            }

            return super.func_70097_a(damagesource, f);
        }
    }

    @Override
    public boolean func_70652_k(Entity entity) {
        int i = this.func_70909_n() ? 4 : 2;

        return entity.func_70097_a(DamageSource.func_76358_a(this), (float) i);
    }

    @Override
    public void func_70903_f(boolean flag) {
        super.func_70903_f(flag);
        if (flag) {
            this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(20.0D);
        } else {
            this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(8.0D);
        }

    }

    @Override
    public boolean func_70085_c(EntityPlayer entityplayer) {
        ItemStack itemstack = entityplayer.field_71071_by.func_70448_g();

        if (this.func_70909_n()) {
            if (itemstack != null) {
                if (itemstack.func_77973_b() instanceof ItemFood) {
                    ItemFood itemfood = (ItemFood) itemstack.func_77973_b();

                    if (itemfood.func_77845_h() && this.field_70180_af.func_111145_d(18) < 20.0F) {
                        if (!entityplayer.field_71075_bZ.field_75098_d) {
                            --itemstack.field_77994_a;
                        }

                        this.func_70691_i((float) itemfood.func_150905_g(itemstack));
                        if (itemstack.field_77994_a <= 0) {
                            entityplayer.field_71071_by.func_70299_a(entityplayer.field_71071_by.field_70461_c, (ItemStack) null);
                        }

                        return true;
                    }
                } else if (itemstack.func_77973_b() == Items.field_151100_aR) {
                    int i = BlockColored.func_150032_b(itemstack.func_77960_j());

                    if (i != this.func_82186_bH()) {
                        this.func_82185_r(i);
                        if (!entityplayer.field_71075_bZ.field_75098_d && --itemstack.field_77994_a <= 0) {
                            entityplayer.field_71071_by.func_70299_a(entityplayer.field_71071_by.field_70461_c, (ItemStack) null);
                        }

                        return true;
                    }
                }
            }

            if (this.func_152114_e(entityplayer) && !this.field_70170_p.field_72995_K && !this.func_70877_b(itemstack)) {
                this.field_70911_d.func_75270_a(!this.func_70906_o());
                this.field_70703_bu = false;
                this.func_70778_a((PathEntity) null);
                this.func_70784_b((Entity) null);
                this.func_70624_b((EntityLivingBase) null);
            }
        } else if (itemstack != null && itemstack.func_77973_b() == Items.field_151103_aS && !this.func_70919_bu()) {
            if (!entityplayer.field_71075_bZ.field_75098_d) {
                --itemstack.field_77994_a;
            }

            if (itemstack.field_77994_a <= 0) {
                entityplayer.field_71071_by.func_70299_a(entityplayer.field_71071_by.field_70461_c, (ItemStack) null);
            }

            if (!this.field_70170_p.field_72995_K) {
                if (this.field_70146_Z.nextInt(3) == 0) {
                    this.func_70903_f(true);
                    this.func_70778_a((PathEntity) null);
                    this.func_70624_b((EntityLivingBase) null);
                    this.field_70911_d.func_75270_a(true);
                    this.func_70606_j(20.0F);
                    this.func_152115_b(entityplayer.func_110124_au().toString());
                    this.func_70908_e(true);
                    this.field_70170_p.func_72960_a(this, (byte) 7);
                } else {
                    this.func_70908_e(false);
                    this.field_70170_p.func_72960_a(this, (byte) 6);
                }
            }

            return true;
        }

        return super.func_70085_c(entityplayer);
    }

    @Override
    public boolean func_70877_b(ItemStack itemstack) {
        return itemstack == null ? false : (!(itemstack.func_77973_b() instanceof ItemFood) ? false : ((ItemFood) itemstack.func_77973_b()).func_77845_h());
    }

    @Override
    public int func_70641_bl() {
        return 8;
    }

    public boolean func_70919_bu() {
        return (this.field_70180_af.func_75683_a(16) & 2) != 0;
    }

    public void func_70916_h(boolean flag) {
        byte b0 = this.field_70180_af.func_75683_a(16);

        if (flag) {
            this.field_70180_af.func_75692_b(16, (byte) (b0 | 2));
        } else {
            this.field_70180_af.func_75692_b(16, (byte) (b0 & -3));
        }

    }

    public int func_82186_bH() {
        return this.field_70180_af.func_75683_a(20) & 15;
    }

    public void func_82185_r(int i) {
        this.field_70180_af.func_75692_b(20, (byte) (i & 15));
    }

    @Override
    public EntityWolf func_90011_a(EntityAgeable entityageable) {
        EntityWolf entitywolf = new EntityWolf(this.field_70170_p);
        String s = this.func_152113_b();

        if (s != null && s.trim().length() > 0) {
            entitywolf.func_152115_b(s);
            entitywolf.func_70903_f(true);
        }

        return entitywolf;
    }

    public void func_70918_i(boolean flag) {
        if (flag) {
            this.field_70180_af.func_75692_b(19, (byte) 1);
        } else {
            this.field_70180_af.func_75692_b(19, (byte) 0);
        }

    }

    @Override
    public boolean func_70878_b(EntityAnimal entityanimal) {
        if (entityanimal == this) {
            return false;
        } else if (!this.func_70909_n()) {
            return false;
        } else if (!(entityanimal instanceof EntityWolf)) {
            return false;
        } else {
            EntityWolf entitywolf = (EntityWolf) entityanimal;

            return !entitywolf.func_70909_n() ? false : (entitywolf.func_70906_o() ? false : this.func_70880_s() && entitywolf.func_70880_s());
        }
    }

    public boolean func_70922_bv() {
        return this.field_70180_af.func_75683_a(19) == 1;
    }

    @Override
    protected boolean func_70692_ba() {
        return !this.func_70909_n() && this.field_70173_aa > 2400;
    }

    @Override
    public boolean func_142018_a(EntityLivingBase entitylivingbase, EntityLivingBase entitylivingbase1) {
        if (!(entitylivingbase instanceof EntityCreeper) && !(entitylivingbase instanceof EntityGhast)) {
            if (entitylivingbase instanceof EntityWolf) {
                EntityWolf entitywolf = (EntityWolf) entitylivingbase;

                if (entitywolf.func_70909_n() && entitywolf.func_70902_q() == entitylivingbase1) {
                    return false;
                }
            }

            return entitylivingbase instanceof EntityPlayer && entitylivingbase1 instanceof EntityPlayer && !((EntityPlayer) entitylivingbase1).func_96122_a((EntityPlayer) entitylivingbase) ? false : !(entitylivingbase instanceof EntityHorse) || !((EntityHorse) entitylivingbase).func_110248_bS();
        } else {
            return false;
        }
    }
}
