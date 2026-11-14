package net.minecraft.enchantment;

public class EnchantmentArrowFire extends Enchantment {

    public EnchantmentArrowFire(int i, int j) {
        super(i, j, EnumEnchantmentType.bow);
        this.func_77322_b("arrowFire");
    }

    @Override
    public int func_77321_a(int i) {
        return 20;
    }

    @Override
    public int func_77317_b(int i) {
        return 50;
    }

    @Override
    public int func_77325_b() {
        return 1;
    }
}
