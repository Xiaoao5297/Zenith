package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemAppleGold extends ItemFood {

    public ItemAppleGold(int i, float f, boolean flag) {
        super(i, f, flag);
        this.func_77627_a(true);
    }

    @Override
    public EnumRarity func_77613_e(ItemStack itemstack) {
        return itemstack.func_77960_j() == 0 ? EnumRarity.rare : EnumRarity.epic;
    }

    @Override
    protected void func_77849_c(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (!world.field_72995_K) {
            entityplayer.func_70690_d(new PotionEffect(Potion.field_76444_x.field_76415_H, 2400, 0));
        }

        if (itemstack.func_77960_j() > 0) {
            if (!world.field_72995_K) {
                entityplayer.func_70690_d(new PotionEffect(Potion.field_76428_l.field_76415_H, 600, 4));
                entityplayer.func_70690_d(new PotionEffect(Potion.field_76429_m.field_76415_H, 6000, 0));
                entityplayer.func_70690_d(new PotionEffect(Potion.field_76426_n.field_76415_H, 6000, 0));
            }
        } else {
            super.func_77849_c(itemstack, world, entityplayer);
        }

    }
}
