package net.minecraft.inventory;

import net.minecraft.item.ItemStack;

class ContainerEnchantment$2 extends Slot {

    ContainerEnchantment$2(ContainerEnchantment containerenchantment, IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
        this.field_75227_a = containerenchantment;
    }

    @Override
    public boolean func_75214_a(ItemStack itemstack) {
        return true;
    }
}
