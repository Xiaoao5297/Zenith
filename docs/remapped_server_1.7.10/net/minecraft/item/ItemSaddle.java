package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;

public class ItemSaddle extends Item {

    public ItemSaddle() {
        this.field_77777_bU = 1;
        this.func_77637_a(CreativeTabs.field_78029_e);
    }

    @Override
    public boolean func_111207_a(ItemStack itemstack, EntityPlayer entityplayer, EntityLivingBase entitylivingbase) {
        if (entitylivingbase instanceof EntityPig) {
            EntityPig entitypig = (EntityPig) entitylivingbase;

            if (!entitypig.func_70901_n() && !entitypig.func_70631_g_()) {
                entitypig.func_70900_e(true);
                entitypig.field_70170_p.func_72956_a(entitypig, "mob.horse.leather", 0.5F, 1.0F);
                --itemstack.field_77994_a;
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean func_77644_a(ItemStack itemstack, EntityLivingBase entitylivingbase, EntityLivingBase entitylivingbase1) {
        this.func_111207_a(itemstack, (EntityPlayer) null, entitylivingbase);
        return true;
    }
}
