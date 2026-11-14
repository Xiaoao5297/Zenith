package net.minecraft.enchantment;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class EnchantmentUntouching extends Enchantment {

    protected EnchantmentUntouching(int i, int j) {
        super(i, j, EnumEnchantmentType.digger);
        this.func_77322_b("untouching");
    }

    @Override
    public int func_77321_a(int i) {
        return 15;
    }

    @Override
    public int func_77317_b(int i) {
        return super.func_77321_a(i) + 50;
    }

    @Override
    public int func_77325_b() {
        return 1;
    }

    @Override
    public boolean func_77326_a(Enchantment enchantment) {
        return super.func_77326_a(enchantment) && enchantment.field_77352_x != EnchantmentUntouching.field_77346_s.field_77352_x;
    }

    @Override
    public boolean func_92089_a(ItemStack itemstack) {
        return itemstack.func_77973_b() == Items.field_151097_aZ ? true : super.func_92089_a(itemstack);
    }
}
