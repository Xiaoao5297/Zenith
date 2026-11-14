package net.minecraft.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityWitherSkull extends EntityFireball {

    public EntityWitherSkull(World world) {
        super(world);
        this.func_70105_a(0.3125F, 0.3125F);
    }

    public EntityWitherSkull(World world, EntityLivingBase entitylivingbase, double d0, double d1, double d2) {
        super(world, entitylivingbase, d0, d1, d2);
        this.func_70105_a(0.3125F, 0.3125F);
    }

    @Override
    protected float func_82341_c() {
        return this.func_82342_d() ? 0.73F : super.func_82341_c();
    }

    @Override
    public boolean func_70027_ad() {
        return false;
    }

    @Override
    public float func_145772_a(Explosion explosion, World world, int i, int j, int k, Block block) {
        float f = super.func_145772_a(explosion, world, i, j, k, block);

        if (this.func_82342_d() && block != Blocks.field_150357_h && block != Blocks.field_150384_bq && block != Blocks.field_150378_br && block != Blocks.field_150483_bI) {
            f = Math.min(0.8F, f);
        }

        return f;
    }

    @Override
    protected void func_70227_a(MovingObjectPosition movingobjectposition) {
        if (!this.field_70170_p.field_72995_K) {
            if (movingobjectposition.field_72308_g != null) {
                if (this.field_70235_a != null) {
                    if (movingobjectposition.field_72308_g.func_70097_a(DamageSource.func_76358_a(this.field_70235_a), 8.0F) && !movingobjectposition.field_72308_g.func_70089_S()) {
                        this.field_70235_a.func_70691_i(5.0F);
                    }
                } else {
                    movingobjectposition.field_72308_g.func_70097_a(DamageSource.field_76376_m, 5.0F);
                }

                if (movingobjectposition.field_72308_g instanceof EntityLivingBase) {
                    byte b0 = 0;

                    if (this.field_70170_p.field_73013_u == EnumDifficulty.NORMAL) {
                        b0 = 10;
                    } else if (this.field_70170_p.field_73013_u == EnumDifficulty.HARD) {
                        b0 = 40;
                    }

                    if (b0 > 0) {
                        ((EntityLivingBase) movingobjectposition.field_72308_g).func_70690_d(new PotionEffect(Potion.field_82731_v.field_76415_H, 20 * b0, 1));
                    }
                }
            }

            this.field_70170_p.func_72885_a(this, this.field_70165_t, this.field_70163_u, this.field_70161_v, 1.0F, false, this.field_70170_p.func_82736_K().func_82766_b("mobGriefing"));
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

    @Override
    protected void func_70088_a() {
        this.field_70180_af.func_75682_a(10, (byte) 0);
    }

    public boolean func_82342_d() {
        return this.field_70180_af.func_75683_a(10) == 1;
    }

    public void func_82343_e(boolean flag) {
        this.field_70180_af.func_75692_b(10, Byte.valueOf((byte) (flag ? 1 : 0)));
    }
}
