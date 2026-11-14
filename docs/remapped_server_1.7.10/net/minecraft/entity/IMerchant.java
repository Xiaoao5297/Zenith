package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public interface IMerchant {

    void func_70932_a_(EntityPlayer entityplayer);

    EntityPlayer func_70931_l_();

    MerchantRecipeList func_70934_b(EntityPlayer entityplayer);

    void func_70933_a(MerchantRecipe merchantrecipe);

    void func_110297_a_(ItemStack itemstack);
}
