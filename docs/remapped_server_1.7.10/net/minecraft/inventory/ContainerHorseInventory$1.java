package net.minecraft.inventory;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

class ContainerHorseInventory$1 extends Slot {

    ContainerHorseInventory$1(ContainerHorseInventory containerhorseinventory, IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
        this.field_111239_a = containerhorseinventory;
    }

    @Override
    public boolean func_75214_a(ItemStack itemstack) {
        return super.func_75214_a(itemstack) && itemstack.func_77973_b() == Items.field_151141_av && !this.func_75216_d();
    }
}
