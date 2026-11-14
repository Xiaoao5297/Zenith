package net.minecraft.inventory;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.ItemStack;

class ContainerHorseInventory$2 extends Slot {

    ContainerHorseInventory$2(ContainerHorseInventory containerhorseinventory, IInventory iinventory, int i, int j, int k, EntityHorse entityhorse) {
        super(iinventory, i, j, k);
        this.field_111240_b = containerhorseinventory;
        this.field_111241_a = entityhorse;
    }

    @Override
    public boolean func_75214_a(ItemStack itemstack) {
        return super.func_75214_a(itemstack) && this.field_111241_a.func_110259_cr() && EntityHorse.func_146085_a(itemstack.func_77973_b());
    }
}
