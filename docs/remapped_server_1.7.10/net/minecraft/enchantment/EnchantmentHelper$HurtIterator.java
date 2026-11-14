package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

final class EnchantmentHelper$HurtIterator implements EnchantmentHelper$IModifier {

    public EntityLivingBase field_151364_a;
    public Entity field_151363_b;

    private EnchantmentHelper$HurtIterator() {}

    @Override
    public void func_77493_a(Enchantment enchantment, int i) {
        enchantment.func_151367_b(this.field_151364_a, this.field_151363_b, i);
    }
}
