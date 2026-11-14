package net.minecraft.item;

public class ItemBook extends Item {

    public ItemBook() {}

    @Override
    public boolean func_77616_k(ItemStack itemstack) {
        return itemstack.field_77994_a == 1;
    }

    @Override
    public int func_77619_b() {
        return 1;
    }
}
