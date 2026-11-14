package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class IEntitySelector$ArmoredMob implements IEntitySelector {

    private final ItemStack field_96567_c;

    public IEntitySelector$ArmoredMob(ItemStack itemstack) {
        this.field_96567_c = itemstack;
    }

    @Override
    public boolean func_82704_a(Entity entity) {
        if (!entity.func_70089_S()) {
            return false;
        } else if (!(entity instanceof EntityLivingBase)) {
            return false;
        } else {
            EntityLivingBase entitylivingbase = (EntityLivingBase) entity;

            return entitylivingbase.func_71124_b(EntityLiving.func_82159_b(this.field_96567_c)) != null ? false : (entitylivingbase instanceof EntityLiving ? ((EntityLiving) entitylivingbase).func_98052_bS() : entitylivingbase instanceof EntityPlayer);
        }
    }
}
