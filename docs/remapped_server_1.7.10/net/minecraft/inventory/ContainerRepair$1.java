package net.minecraft.inventory;

class ContainerRepair$1 extends InventoryBasic {

    ContainerRepair$1(ContainerRepair containerrepair, String s, boolean flag, int i) {
        super(s, flag, i);
        this.field_135010_a = containerrepair;
    }

    @Override
    public void func_70296_d() {
        super.func_70296_d();
        this.field_135010_a.func_75130_a(this);
    }
}
