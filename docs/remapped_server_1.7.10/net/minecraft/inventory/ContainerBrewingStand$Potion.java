package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

class ContainerBrewingStand$Potion extends Slot {

    private EntityPlayer field_75244_a;

    public ContainerBrewingStand$Potion(EntityPlayer entityplayer, IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
        this.field_75244_a = entityplayer;
    }

    @Override
    public boolean func_75214_a(ItemStack itemstack) {
        return func_75243_a_(itemstack);
    }

    @Override
    public int func_75219_a() {
        return 1;
    }

    @Override
    public void func_82870_a(EntityPlayer entityplayer, ItemStack itemstack) {
        if (itemstack.func_77973_b() == Items.field_151068_bn && itemstack.func_77960_j() > 0) {
            this.field_75244_a.func_71064_a(AchievementList.field_76001_A, 1);
        }

        super.func_82870_a(entityplayer, itemstack);
    }

    public static boolean func_75243_a_(ItemStack itemstack) {
        return itemstack != null && (itemstack.func_77973_b() == Items.field_151068_bn || itemstack.func_77973_b() == Items.field_151069_bo);
    }
}
