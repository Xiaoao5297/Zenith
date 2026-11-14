package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

final class EnchantmentHelper$DamageIterator implements EnchantmentHelper$IModifier {

    public EntityLivingBase field_151366_a;
    public Entity field_151365_b;

    private EnchantmentHelper$DamageIterator() {}

    @Override
    public void func_77493_a(Enchantment enchantment, int i) {
        enchantment.func_151368_a(this.field_151366_a, this.field_151365_b, i);
    }
}
