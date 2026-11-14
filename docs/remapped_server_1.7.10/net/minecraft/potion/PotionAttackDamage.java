package net.minecraft.potion;

import net.minecraft.entity.ai.attributes.AttributeModifier;

public class PotionAttackDamage extends Potion {

    protected PotionAttackDamage(int i, boolean flag, int j) {
        super(i, flag, j);
    }

    @Override
    public double func_111183_a(int i, AttributeModifier attributemodifier) {
        return this.field_76415_H == Potion.field_76437_t.field_76415_H ? (double) (-0.5F * (float) (i + 1)) : 1.3D * (double) (i + 1);
    }
}
