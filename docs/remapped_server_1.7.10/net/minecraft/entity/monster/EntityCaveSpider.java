package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityCaveSpider extends EntitySpider {

    public EntityCaveSpider(World world) {
        super(world);
        this.func_70105_a(0.7F, 0.5F);
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(12.0D);
    }

    @Override
    public boolean func_70652_k(Entity entity) {
        if (super.func_70652_k(entity)) {
            if (entity instanceof EntityLivingBase) {
                byte b0 = 0;

                if (this.field_70170_p.field_73013_u == EnumDifficulty.NORMAL) {
                    b0 = 7;
                } else if (this.field_70170_p.field_73013_u == EnumDifficulty.HARD) {
                    b0 = 15;
                }

                if (b0 > 0) {
                    ((EntityLivingBase) entity).func_70690_d(new PotionEffect(Potion.field_76436_u.field_76415_H, b0 * 20, 0));
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public IEntityLivingData func_110161_a(IEntityLivingData ientitylivingdata) {
        return ientitylivingdata;
    }
}
