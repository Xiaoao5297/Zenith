package net.minecraft.inventory;

import net.minecraft.item.ItemStack;

class ContainerBrewingStand$Ingredient extends Slot {

    public ContainerBrewingStand$Ingredient(ContainerBrewingStand containerbrewingstand, IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
        this.field_75226_a = containerbrewingstand;
    }

    @Override
    public boolean func_75214_a(ItemStack itemstack) {
        return itemstack != null ? itemstack.func_77973_b().func_150892_m(itemstack) : false;
    }

    @Override
    public int func_75219_a() {
        return 64;
    }
}
