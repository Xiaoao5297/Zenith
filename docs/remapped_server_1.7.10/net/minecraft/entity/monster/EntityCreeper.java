package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCreeper extends EntityMob {

    private int field_70834_e;
    private int field_70833_d;
    private int field_82225_f = 30;
    private int field_82226_g = 3;

    public EntityCreeper(World world) {
        super(world);
        this.field_70714_bg.func_75776_a(1, new EntityAISwimming(this));
        this.field_70714_bg.func_75776_a(2, new EntityAICreeperSwell(this));
        this.field_70714_bg.func_75776_a(3, new EntityAIAvoidEntity(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
        this.field_70714_bg.func_75776_a(4, new EntityAIAttackOnCollide(this, 1.0D, false));
        this.field_70714_bg.func_75776_a(5, new EntityAIWander(this, 0.8D));
        this.field_70714_bg.func_75776_a(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.field_70714_bg.func_75776_a(6, new EntityAILookIdle(this));
        this.field_70715_bh.func_75776_a(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        this.field_70715_bh.func_75776_a(2, new EntityAIHurtByTarget(this, false));
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.25D);
    }

    @Override
    public boolean func_70650_aV() {
        return true;
    }

    @Override
    public int func_82143_as() {
        return this.func_70638_az() == null ? 3 : 3 + (int) (this.func_110143_aJ() - 1.0F);
    }

    @Override
    protected void func_70069_a(float f) {
        super.func_70069_a(f);
        this.field_70833_d = (int) ((float) this.field_70833_d + f * 1.5F);
        if (this.field_70833_d > this.field_82225_f - 5) {
            this.field_70833_d = this.field_82225_f - 5;
        }

    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_75682_a(16, -1);
        this.field_70180_af.func_75682_a(17, (byte) 0);
        this.field_70180_af.func_75682_a(18, (byte) 0);
    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        if (this.field_70180_af.func_75683_a(17) == 1) {
            nbttagcompound.func_74757_a("powered", true);
        }

        nbttagcompound.func_74777_a("Fuse", (short) this.field_82225_f);
        nbttagcompound.func_74774_a("ExplosionRadius", (byte) this.field_82226_g);
        nbttagcompound.func_74757_a("ignited", this.func_146078_ca());
    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        this.field_70180_af.func_75692_b(17, (byte) (nbttagcompound.func_74767_n("powered") ? 1 : 0));
        if (nbttagcompound.func_150297_b("Fuse", 99)) {
            this.field_82225_f = nbttagcompound.func_74765_d("Fuse");
        }

        if (nbttagcompound.func_150297_b("ExplosionRadius", 99)) {
            this.field_82226_g = nbttagcompound.func_74771_c("ExplosionRadius");
        }

        if (nbttagcompound.func_74767_n("ignited")) {
            this.func_146079_cb();
        }

    }

    @Override
    public void func_70071_h_() {
        if (this.func_70089_S()) {
            this.field_70834_e = this.field_70833_d;
            if (this.func_146078_ca()) {
                this.func_70829_a(1);
            }

            int i = this.func_70832_p();

            if (i > 0 && this.field_70833_d == 0) {
                this.func_85030_a("creeper.primed", 1.0F, 0.5F);
            }

            this.field_70833_d += i;
            if (this.field_70833_d < 0) {
                this.field_70833_d = 0;
            }

            if (this.field_70833_d >= this.field_82225_f) {
                this.field_70833_d = this.field_82225_f;
                this.func_146077_cc();
            }
        }

        super.func_70071_h_();
    }

    @Override
    protected String func_70621_aR() {
        return "mob.creeper.say";
    }

    @Override
    protected String func_70673_aS() {
        return "mob.creeper.death";
    }

    @Override
    public void func_70645_a(DamageSource damagesource) {
        super.func_70645_a(damagesource);
        if (damagesource.func_76346_g() instanceof EntitySkeleton) {
            int i = Item.func_150891_b(Items.field_151096_cd);
            int j = Item.func_150891_b(Items.field_151084_co);
            int k = i + this.field_70146_Z.nextInt(j - i + 1);

            this.func_145779_a(Item.func_150899_d(k), 1);
        }

    }

    @Override
    public boolean func_70652_k(Entity entity) {
        return true;
    }

    public boolean func_70830_n() {
        return this.field_70180_af.func_75683_a(17) == 1;
    }

    @Override
    protected Item func_146068_u() {
        return Items.field_151016_H;
    }

    public int func_70832_p() {
        return this.field_70180_af.func_75683_a(16);
    }

    public void func_70829_a(int i) {
        this.field_70180_af.func_75692_b(16, (byte) i);
    }

    @Override
    public void func_70077_a(EntityLightningBolt entitylightningbolt) {
        super.func_70077_a(entitylightningbolt);
        this.field_70180_af.func_75692_b(17, (byte) 1);
    }

    @Override
    protected boolean func_70085_c(EntityPlayer entityplayer) {
        ItemStack itemstack = entityplayer.field_71071_by.func_70448_g();

        if (itemstack != null && itemstack.func_77973_b() == Items.field_151033_d) {
            this.field_70170_p.func_72908_a(this.field_70165_t + 0.5D, this.field_70163_u + 0.5D, this.field_70161_v + 0.5D, "fire.ignite", 1.0F, this.field_70146_Z.nextFloat() * 0.4F + 0.8F);
            entityplayer.func_71038_i();
            if (!this.field_70170_p.field_72995_K) {
                this.func_146079_cb();
                itemstack.func_77972_a(1, entityplayer);
                return true;
            }
        }

        return super.func_70085_c(entityplayer);
    }

    private void func_146077_cc() {
        if (!this.field_70170_p.field_72995_K) {
            boolean flag = this.field_70170_p.func_82736_K().func_82766_b("mobGriefing");

            if (this.func_70830_n()) {
                this.field_70170_p.func_72876_a(this, this.field_70165_t, this.field_70163_u, this.field_70161_v, (float) (this.field_82226_g * 2), flag);
            } else {
                this.field_70170_p.func_72876_a(this, this.field_70165_t, this.field_70163_u, this.field_70161_v, (float) this.field_82226_g, flag);
            }

            this.func_70106_y();
        }

    }

    public boolean func_146078_ca() {
        return this.field_70180_af.func_75683_a(18) != 0;
    }

    public void func_146079_cb() {
        this.field_70180_af.func_75692_b(18, (byte) 1);
    }
}
