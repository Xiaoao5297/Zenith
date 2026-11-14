package net.minecraft.enchantment;

public class EnchantmentArrowKnockback extends Enchantment {

    public EnchantmentArrowKnockback(int i, int j) {
        super(i, j, EnumEnchantmentType.bow);
        this.func_77322_b("arrowKnockback");
    }

    @Override
    public int func_77321_a(int i) {
        return 12 + (i - 1) * 20;
    }

    @Override
    public int func_77317_b(int i) {
        return this.func_77321_a(i) + 25;
    }

    @Override
    public int func_77325_b() {
        return 2;
    }
}
