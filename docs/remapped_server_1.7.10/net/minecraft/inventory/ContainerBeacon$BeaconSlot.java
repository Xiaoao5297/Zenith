package net.minecraft.inventory;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

class ContainerBeacon$BeaconSlot extends Slot {

    public ContainerBeacon$BeaconSlot(ContainerBeacon containerbeacon, IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
        this.field_82876_a = containerbeacon;
    }

    @Override
    public boolean func_75214_a(ItemStack itemstack) {
        return itemstack == null ? false : itemstack.func_77973_b() == Items.field_151166_bC || itemstack.func_77973_b() == Items.field_151045_i || itemstack.func_77973_b() == Items.field_151043_k || itemstack.func_77973_b() == Items.field_151042_j;
    }

    @Override
    public int func_75219_a() {
        return 1;
    }
}
