package net.minecraft.enchantment;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class EnchantmentDigging extends Enchantment {

    protected EnchantmentDigging(int i, int j) {
        super(i, j, EnumEnchantmentType.digger);
        this.func_77322_b("digging");
    }

    @Override
    public int func_77321_a(int i) {
        return 1 + 10 * (i - 1);
    }

    @Override
    public int func_77317_b(int i) {
        return super.func_77321_a(i) + 50;
    }

    @Override
    public int func_77325_b() {
        return 5;
    }

    @Override
    public boolean func_92089_a(ItemStack itemstack) {
        return itemstack.func_77973_b() == Items.field_151097_aZ ? true : super.func_92089_a(itemstack);
    }
}
