package net.minecraft.enchantment;

public class EnchantmentOxygen extends Enchantment {

    public EnchantmentOxygen(int i, int j) {
        super(i, j, EnumEnchantmentType.armor_head);
        this.func_77322_b("oxygen");
    }

    @Override
    public int func_77321_a(int i) {
        return 10 * i;
    }

    @Override
    public int func_77317_b(int i) {
        return this.func_77321_a(i) + 30;
    }

    @Override
    public int func_77325_b() {
        return 3;
    }
}
