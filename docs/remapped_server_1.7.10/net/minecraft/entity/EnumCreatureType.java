package net.minecraft.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;

public enum EnumCreatureType {

    monster(IMob.class, 70, Material.field_151579_a, false, false), creature(EntityAnimal.class, 10, Material.field_151579_a, true, true), ambient(EntityAmbientCreature.class, 15, Material.field_151579_a, true, false), waterCreature(EntityWaterMob.class, 5, Material.field_151586_h, true, false);

    private final Class field_75605_d;
    private final int field_75606_e;
    private final Material field_75603_f;
    private final boolean field_75604_g;
    private final boolean field_82707_i;

    private EnumCreatureType(Class oclass, int i, Material material, boolean flag, boolean flag1) {
        this.field_75605_d = oclass;
        this.field_75606_e = i;
        this.field_75603_f = material;
        this.field_75604_g = flag;
        this.field_82707_i = flag1;
    }

    public Class func_75598_a() {
        return this.field_75605_d;
    }

    public int func_75601_b() {
        return this.field_75606_e;
    }

    public Material func_75600_c() {
        return this.field_75603_f;
    }

    public boolean func_75599_d() {
        return this.field_75604_g;
    }

    public boolean func_82705_e() {
        return this.field_82707_i;
    }
}
