package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;

public class PotionHealthBoost extends Potion {

    public PotionHealthBoost(int i, boolean flag, int j) {
        super(i, flag, j);
    }

    @Override
    public void func_111187_a(EntityLivingBase entitylivingbase, BaseAttributeMap baseattributemap, int i) {
        super.func_111187_a(entitylivingbase, baseattributemap, i);
        if (entitylivingbase.func_110143_aJ() > entitylivingbase.func_110138_aP()) {
            entitylivingbase.func_70606_j(entitylivingbase.func_110138_aP());
        }

    }
}
