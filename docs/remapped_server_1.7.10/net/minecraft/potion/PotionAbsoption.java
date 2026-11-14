package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;

public class PotionAbsoption extends Potion {

    protected PotionAbsoption(int i, boolean flag, int j) {
        super(i, flag, j);
    }

    @Override
    public void func_111187_a(EntityLivingBase entitylivingbase, BaseAttributeMap baseattributemap, int i) {
        entitylivingbase.func_110149_m(entitylivingbase.func_110139_bj() - (float) (4 * (i + 1)));
        super.func_111187_a(entitylivingbase, baseattributemap, i);
    }

    @Override
    public void func_111185_a(EntityLivingBase entitylivingbase, BaseAttributeMap baseattributemap, int i) {
        entitylivingbase.func_110149_m(entitylivingbase.func_110139_bj() + (float) (4 * (i + 1)));
        super.func_111185_a(entitylivingbase, baseattributemap, i);
    }
}
