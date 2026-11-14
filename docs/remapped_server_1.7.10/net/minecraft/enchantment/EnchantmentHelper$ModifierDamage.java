package net.minecraft.enchantment;

import net.minecraft.util.DamageSource;

final class EnchantmentHelper$ModifierDamage implements EnchantmentHelper$IModifier {

    public int field_77497_a;
    public DamageSource field_77496_b;

    private EnchantmentHelper$ModifierDamage() {}

    @Override
    public void func_77493_a(Enchantment enchantment, int i) {
        this.field_77497_a += enchantment.func_77318_a(i, this.field_77496_b);
    }
}
