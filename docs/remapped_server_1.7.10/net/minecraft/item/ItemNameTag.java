package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class ItemNameTag extends Item {

    public ItemNameTag() {
        this.func_77637_a(CreativeTabs.field_78040_i);
    }

    @Override
    public boolean func_111207_a(ItemStack itemstack, EntityPlayer entityplayer, EntityLivingBase entitylivingbase) {
        if (!itemstack.func_82837_s()) {
            return false;
        } else if (entitylivingbase instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) entitylivingbase;

            entityliving.func_94058_c(itemstack.func_82833_r());
            entityliving.func_110163_bv();
            --itemstack.field_77994_a;
            return true;
        } else {
            return super.func_111207_a(itemstack, entityplayer, entitylivingbase);
        }
    }
}
