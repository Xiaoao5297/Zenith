package net.minecraft.entity.monster;

import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityPigZombie extends EntityZombie {

    private static final UUID field_110189_bq = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final AttributeModifier field_110190_br = (new AttributeModifier(EntityPigZombie.field_110189_bq, "Attacking speed boost", 0.45D, 0)).func_111168_a(false);
    private int field_70837_d;
    private int field_70838_e;
    private Entity field_110191_bu;

    public EntityPigZombie(World world) {
        super(world);
        this.field_70178_ae = true;
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(EntityPigZombie.field_110186_bp).func_111128_a(0.0D);
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.5D);
        this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(5.0D);
    }

    @Override
    protected boolean func_70650_aV() {
        return false;
    }

    @Override
    public void func_70071_h_() {
        if (this.field_110191_bu != this.field_70789_a && !this.field_70170_p.field_72995_K) {
            IAttributeInstance iattributeinstance = this.func_110148_a(SharedMonsterAttributes.field_111263_d);

            iattributeinstance.func_111124_b(EntityPigZombie.field_110190_br);
            if (this.field_70789_a != null) {
                iattributeinstance.func_111121_a(EntityPigZombie.field_110190_br);
            }
        }

        this.field_110191_bu = this.field_70789_a;
        if (this.field_70838_e > 0 && --this.field_70838_e == 0) {
            this.func_85030_a("mob.zombiepig.zpigangry", this.func_70599_aP() * 2.0F, ((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F) * 1.8F);
        }

        super.func_70071_h_();
    }

    @Override
    public boolean func_70601_bi() {
        return this.field_70170_p.field_73013_u != EnumDifficulty.PEACEFUL && this.field_70170_p.func_72855_b(this.field_70121_D) && this.field_70170_p.func_72945_a(this, this.field_70121_D).isEmpty() && !this.field_70170_p.func_72953_d(this.field_70121_D);
    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        nbttagcompound.func_74777_a("Anger", (short) this.field_70837_d);
    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        this.field_70837_d = nbttagcompound.func_74765_d("Anger");
    }

    @Override
    protected Entity func_70782_k() {
        return this.field_70837_d == 0 ? null : super.func_70782_k();
    }

    @Override
    public boolean func_70097_a(DamageSource damagesource, float f) {
        if (this.func_85032_ar()) {
            return false;
        } else {
            Entity entity = damagesource.func_76346_g();

            if (entity instanceof EntityPlayer) {
                List list = this.field_70170_p.func_72839_b(this, this.field_70121_D.func_72314_b(32.0D, 32.0D, 32.0D));

                for (int i = 0; i < list.size(); ++i) {
                    Entity entity1 = (Entity) list.get(i);

                    if (entity1 instanceof EntityPigZombie) {
                        EntityPigZombie entitypigzombie = (EntityPigZombie) entity1;

                        entitypigzombie.func_70835_c(entity);
                    }
                }

                this.func_70835_c(entity);
            }

            return super.func_70097_a(damagesource, f);
        }
    }

    private void func_70835_c(Entity entity) {
        this.field_70789_a = entity;
        this.field_70837_d = 400 + this.field_70146_Z.nextInt(400);
        this.field_70838_e = this.field_70146_Z.nextInt(40);
    }

    @Override
    protected String func_70639_aQ() {
        return "mob.zombiepig.zpig";
    }

    @Override
    protected String func_70621_aR() {
        return "mob.zombiepig.zpighurt";
    }

    @Override
    protected String func_70673_aS() {
        return "mob.zombiepig.zpigdeath";
    }

    @Override
    protected void func_70628_a(boolean flag, int i) {
        int j = this.field_70146_Z.nextInt(2 + i);

        int k;

        for (k = 0; k < j; ++k) {
            this.func_145779_a(Items.field_151078_bh, 1);
        }

        j = this.field_70146_Z.nextInt(2 + i);

        for (k = 0; k < j; ++k) {
            this.func_145779_a(Items.field_151074_bl, 1);
        }

    }

    @Override
    public boolean func_70085_c(EntityPlayer entityplayer) {
        return false;
    }

    @Override
    protected void func_70600_l(int i) {
        this.func_145779_a(Items.field_151043_k, 1);
    }

    @Override
    protected void func_82164_bB() {
        this.func_70062_b(0, new ItemStack(Items.field_151010_B));
    }

    @Override
    public IEntityLivingData func_110161_a(IEntityLivingData ientitylivingdata) {
        super.func_110161_a(ientitylivingdata);
        this.func_82229_g(false);
        return ientitylivingdata;
    }
}
