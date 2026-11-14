package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBlaze extends EntityMob {

    private float field_70847_d = 0.5F;
    private int field_70848_e;
    private int field_70846_g;

    public EntityBlaze(World world) {
        super(world);
        this.field_70178_ae = true;
        this.field_70728_aV = 10;
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(6.0D);
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_75682_a(16, new Byte((byte) 0));
    }

    @Override
    protected String func_70639_aQ() {
        return "mob.blaze.breathe";
    }

    @Override
    protected String func_70621_aR() {
        return "mob.blaze.hit";
    }

    @Override
    protected String func_70673_aS() {
        return "mob.blaze.death";
    }

    @Override
    public float func_70013_c(float f) {
        return 1.0F;
    }

    @Override
    public void func_70636_d() {
        if (!this.field_70170_p.field_72995_K) {
            if (this.func_70026_G()) {
                this.func_70097_a(DamageSource.field_76369_e, 1.0F);
            }

            --this.field_70848_e;
            if (this.field_70848_e <= 0) {
                this.field_70848_e = 100;
                this.field_70847_d = 0.5F + (float) this.field_70146_Z.nextGaussian() * 3.0F;
            }

            if (this.func_70777_m() != null && this.func_70777_m().field_70163_u + (double) this.func_70777_m().func_70047_e() > this.field_70163_u + (double) this.func_70047_e() + (double) this.field_70847_d) {
                this.field_70181_x += (0.30000001192092896D - this.field_70181_x) * 0.30000001192092896D;
            }
        }

        if (this.field_70146_Z.nextInt(24) == 0) {
            this.field_70170_p.func_72908_a(this.field_70165_t + 0.5D, this.field_70163_u + 0.5D, this.field_70161_v + 0.5D, "fire.fire", 1.0F + this.field_70146_Z.nextFloat(), this.field_70146_Z.nextFloat() * 0.7F + 0.3F);
        }

        if (!this.field_70122_E && this.field_70181_x < 0.0D) {
            this.field_70181_x *= 0.6D;
        }

        for (int i = 0; i < 2; ++i) {
            this.field_70170_p.func_72869_a("largesmoke", this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * (double) this.field_70130_N, this.field_70163_u + this.field_70146_Z.nextDouble() * (double) this.field_70131_O, this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * (double) this.field_70130_N, 0.0D, 0.0D, 0.0D);
        }

        super.func_70636_d();
    }

    @Override
    protected void func_70785_a(Entity entity, float f) {
        if (this.field_70724_aR <= 0 && f < 2.0F && entity.field_70121_D.field_72337_e > this.field_70121_D.field_72338_b && entity.field_70121_D.field_72338_b < this.field_70121_D.field_72337_e) {
            this.field_70724_aR = 20;
            this.func_70652_k(entity);
        } else if (f < 30.0F) {
            double d0 = entity.field_70165_t - this.field_70165_t;
            double d1 = entity.field_70121_D.field_72338_b + (double) (entity.field_70131_O / 2.0F) - (this.field_70163_u + (double) (this.field_70131_O / 2.0F));
            double d2 = entity.field_70161_v - this.field_70161_v;

            if (this.field_70724_aR == 0) {
                ++this.field_70846_g;
                if (this.field_70846_g == 1) {
                    this.field_70724_aR = 60;
                    this.func_70844_e(true);
                } else if (this.field_70846_g <= 4) {
                    this.field_70724_aR = 6;
                } else {
                    this.field_70724_aR = 100;
                    this.field_70846_g = 0;
                    this.func_70844_e(false);
                }

                if (this.field_70846_g > 1) {
                    float f1 = MathHelper.func_76129_c(f) * 0.5F;

                    this.field_70170_p.func_72889_a((EntityPlayer) null, 1009, (int) this.field_70165_t, (int) this.field_70163_u, (int) this.field_70161_v, 0);

                    for (int i = 0; i < 1; ++i) {
                        EntitySmallFireball entitysmallfireball = new EntitySmallFireball(this.field_70170_p, this, d0 + this.field_70146_Z.nextGaussian() * (double) f1, d1, d2 + this.field_70146_Z.nextGaussian() * (double) f1);

                        entitysmallfireball.field_70163_u = this.field_70163_u + (double) (this.field_70131_O / 2.0F) + 0.5D;
                        this.field_70170_p.func_72838_d(entitysmallfireball);
                    }
                }
            }

            this.field_70177_z = (float) (Math.atan2(d2, d0) * 180.0D / 3.1415927410125732D) - 90.0F;
            this.field_70787_b = true;
        }

    }

    @Override
    protected void func_70069_a(float f) {}

    @Override
    protected Item func_146068_u() {
        return Items.field_151072_bj;
    }

    @Override
    public boolean func_70027_ad() {
        return this.func_70845_n();
    }

    @Override
    protected void func_70628_a(boolean flag, int i) {
        if (flag) {
            int j = this.field_70146_Z.nextInt(2 + i);

            for (int k = 0; k < j; ++k) {
                this.func_145779_a(Items.field_151072_bj, 1);
            }
        }

    }

    public boolean func_70845_n() {
        return (this.field_70180_af.func_75683_a(16) & 1) != 0;
    }

    public void func_70844_e(boolean flag) {
        byte b0 = this.field_70180_af.func_75683_a(16);

        if (flag) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 &= -2;
        }

        this.field_70180_af.func_75692_b(16, b0);
    }

    @Override
    protected boolean func_70814_o() {
        return true;
    }
}
