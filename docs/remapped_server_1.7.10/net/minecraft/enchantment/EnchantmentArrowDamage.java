package net.minecraft.enchantment;

public class EnchantmentArrowDamage extends Enchantment {

    public EnchantmentArrowDamage(int i, int j) {
        super(i, j, EnumEnchantmentType.bow);
        this.func_77322_b("arrowDamage");
    }

    @Override
    public int func_77321_a(int i) {
        return 1 + (i - 1) * 10;
    }

    @Override
    public int func_77317_b(int i) {
        return this.func_77321_a(i) + 15;
    }

    @Override
    public int func_77325_b() {
        return 5;
    }
}
