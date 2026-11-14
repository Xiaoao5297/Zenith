package net.minecraft.inventory;

class ContainerEnchantment$1 extends InventoryBasic {

    ContainerEnchantment$1(ContainerEnchantment containerenchantment, String s, boolean flag, int i) {
        super(s, flag, i);
        this.field_70484_a = containerenchantment;
    }

    @Override
    public int func_70297_j_() {
        return 1;
    }

    @Override
    public void func_70296_d() {
        super.func_70296_d();
        this.field_70484_a.func_75130_a(this);
    }
}
