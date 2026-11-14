package net.minecraft.enchantment;

public class EnchantmentKnockback extends Enchantment {

    protected EnchantmentKnockback(int i, int j) {
        super(i, j, EnumEnchantmentType.weapon);
        this.func_77322_b("knockback");
    }

    @Override
    public int func_77321_a(int i) {
        return 5 + 20 * (i - 1);
    }

    @Override
    public int func_77317_b(int i) {
        return super.func_77321_a(i) + 50;
    }

    @Override
    public int func_77325_b() {
        return 2;
    }
}
