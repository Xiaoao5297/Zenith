package net.minecraft.entity.monster;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityWitch extends EntityMob implements IRangedAttackMob {

    private static final UUID field_110184_bp = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier field_110185_bq = (new AttributeModifier(EntityWitch.field_110184_bp, "Drinking speed penalty", -0.25D, 0)).func_111168_a(false);
    private static final Item[] field_82199_d = new Item[]{Items.field_151114_aO, Items.field_151102_aT, Items.field_151137_ax, Items.field_151070_bp, Items.field_151069_bo, Items.field_151016_H, Items.field_151055_y, Items.field_151055_y};
    private int field_82200_e;

    public EntityWitch(World world) {
        super(world);
        this.field_70714_bg.func_75776_a(1, new EntityAISwimming(this));
        this.field_70714_bg.func_75776_a(2, new EntityAIArrowAttack(this, 1.0D, 60, 10.0F));
        this.field_70714_bg.func_75776_a(2, new EntityAIWander(this, 1.0D));
        this.field_70714_bg.func_75776_a(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.field_70714_bg.func_75776_a(3, new EntityAILookIdle(this));
        this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, false));
        this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.func_70096_w().func_75682_a(21, (byte) 0);
    }

    @Override
    protected String func_70639_aQ() {
        return "mob.witch.idle";
    }

    @Override
    protected String func_70621_aR() {
        return "mob.witch.hurt";
    }

    @Override
    protected String func_70673_aS() {
        return "mob.witch.death";
    }

    public void func_82197_f(boolean flag) {
        this.func_70096_w().func_75692_b(21, Byte.valueOf((byte) (flag ? 1 : 0)));
    }

    public boolean func_82198_m() {
        return this.func_70096_w().func_75683_a(21) == 1;
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(26.0D);
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.25D);
    }

    @Override
    public boolean func_70650_aV() {
        return true;
    }

    @Override
    public void func_70636_d() {
        if (!this.field_70170_p.field_72995_K) {
            if (this.func_82198_m()) {
                if (this.field_82200_e-- <= 0) {
                    this.func_82197_f(false);
                    ItemStack itemstack = this.func_70694_bm();

                    this.func_70062_b(0, (ItemStack) null);
                    if (itemstack != null && itemstack.func_77973_b() == Items.field_151068_bn) {
                        List list = Items.field_151068_bn.func_77832_l(itemstack);

                        if (list != null) {
                            Iterator iterator = list.iterator();

                            while (iterator.hasNext()) {
                                PotionEffect potioneffect = (PotionEffect) iterator.next();

                                this.func_70690_d(new PotionEffect(potioneffect));
                            }
                        }
                    }

                    this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111124_b(EntityWitch.field_110185_bq);
                }
            } else {
                short short0 = -1;

                if (this.field_70146_Z.nextFloat() < 0.15F && this.func_70055_a(Material.field_151586_h) && !this.func_70644_a(Potion.field_76427_o)) {
                    short0 = 8237;
                } else if (this.field_70146_Z.nextFloat() < 0.15F && this.func_70027_ad() && !this.func_70644_a(Potion.field_76426_n)) {
                    short0 = 16307;
                } else if (this.field_70146_Z.nextFloat() < 0.05F && this.func_110143_aJ() < this.func_110138_aP()) {
                    short0 = 16341;
                } else if (this.field_70146_Z.nextFloat() < 0.25F && this.func_70638_az() != null && !this.func_70644_a(Potion.field_76424_c) && this.func_70638_az().func_70068_e(this) > 121.0D) {
                    short0 = 16274;
                } else if (this.field_70146_Z.nextFloat() < 0.25F && this.func_70638_az() != null && !this.func_70644_a(Potion.field_76424_c) && this.func_70638_az().func_70068_e(this) > 121.0D) {
                    short0 = 16274;
                }

                if (short0 > -1) {
                    this.func_70062_b(0, new ItemStack(Items.field_151068_bn, 1, short0));
                    this.field_82200_e = this.func_70694_bm().func_77988_m();
                    this.func_82197_f(true);
                    IAttributeInstance iattributeinstance = this.func_110148_a(SharedMonsterAttributes.field_111263_d);

                    iattributeinstance.func_111124_b(EntityWitch.field_110185_bq);
                    iattributeinstance.func_111121_a(EntityWitch.field_110185_bq);
                }
            }

            if (this.field_70146_Z.nextFloat() < 7.5E-4F) {
                this.field_70170_p.func_72960_a(this, (byte) 15);
            }
        }

        super.func_70636_d();
    }

    @Override
    protected float func_70672_c(DamageSource damagesource, float f) {
        f = super.func_70672_c(damagesource, f);
        if (damagesource.func_76346_g() == this) {
            f = 0.0F;
        }

        if (damagesource.func_82725_o()) {
            f = (float) ((double) f * 0.15D);
        }

        return f;
    }

    @Override
    protected void func_70628_a(boolean flag, int i) {
        int j = this.field_70146_Z.nextInt(3) + 1;

        for (int k = 0; k < j; ++k) {
            int l = this.field_70146_Z.nextInt(3);
            Item item = EntityWitch.field_82199_d[this.field_70146_Z.nextInt(EntityWitch.field_82199_d.length)];

            if (i > 0) {
                l += this.field_70146_Z.nextInt(i + 1);
            }

            for (int i1 = 0; i1 < l; ++i1) {
                this.func_145779_a(item, 1);
            }
        }

    }

    @Override
    public void func_82196_d(EntityLivingBase entitylivingbase, float f) {
        if (!this.func_82198_m()) {
            EntityPotion entitypotion = new EntityPotion(this.field_70170_p, this, 32732);

            entitypotion.field_70125_A -= -20.0F;
            double d0 = entitylivingbase.field_70165_t + entitylivingbase.field_70159_w - this.field_70165_t;
            double d1 = entitylivingbase.field_70163_u + (double) entitylivingbase.func_70047_e() - 1.100000023841858D - this.field_70163_u;
            double d2 = entitylivingbase.field_70161_v + entitylivingbase.field_70179_y - this.field_70161_v;
            float f1 = MathHelper.func_76133_a(d0 * d0 + d2 * d2);

            if (f1 >= 8.0F && !entitylivingbase.func_70644_a(Potion.field_76421_d)) {
                entitypotion.func_82340_a(32698);
            } else if (entitylivingbase.func_110143_aJ() >= 8.0F && !entitylivingbase.func_70644_a(Potion.field_76436_u)) {
                entitypotion.func_82340_a(32660);
            } else if (f1 <= 3.0F && !entitylivingbase.func_70644_a(Potion.field_76437_t) && this.field_70146_Z.nextFloat() < 0.25F) {
                entitypotion.func_82340_a(32696);
            }

            entitypotion.func_70186_c(d0, d1 + (double) (f1 * 0.2F), d2, 0.75F, 8.0F);
            this.field_70170_p.func_72838_d(entitypotion);
        }
    }
}
