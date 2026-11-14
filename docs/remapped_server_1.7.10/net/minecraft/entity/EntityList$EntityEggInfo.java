package net.minecraft.entity;

import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class EntityList$EntityEggInfo {

    public final int field_75613_a;
    public final int field_75611_b;
    public final int field_75612_c;
    public final StatBase field_151512_d;
    public final StatBase field_151513_e;

    public EntityList$EntityEggInfo(int i, int j, int k) {
        this.field_75613_a = i;
        this.field_75611_b = j;
        this.field_75612_c = k;
        this.field_151512_d = StatList.func_151182_a(this);
        this.field_151513_e = StatList.func_151176_b(this);
    }
}
