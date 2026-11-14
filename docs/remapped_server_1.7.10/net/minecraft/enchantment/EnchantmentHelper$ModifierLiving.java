package net.minecraft.enchantment;

import net.minecraft.entity.EnumCreatureAttribute;

final class EnchantmentHelper$ModifierLiving implements EnchantmentHelper$IModifier {

    public float field_77495_a;
    public EnumCreatureAttribute field_77494_b;

    private EnchantmentHelper$ModifierLiving() {}

    @Override
    public void func_77493_a(Enchantment enchantment, int i) {
        this.field_77495_a += enchantment.func_152376_a(i, this.field_77494_b);
    }
}
