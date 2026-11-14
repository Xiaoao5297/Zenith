package net.minecraft.inventory;

import net.minecraft.item.ItemStack;

public interface ISidedInventory extends IInventory {

    int[] func_94128_d(int i);

    boolean func_102007_a(int i, ItemStack itemstack, int j);

    boolean func_102008_b(int i, ItemStack itemstack, int j);
}
