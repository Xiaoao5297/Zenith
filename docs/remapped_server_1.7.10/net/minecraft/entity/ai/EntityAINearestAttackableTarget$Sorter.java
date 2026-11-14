package net.minecraft.entity.ai;

import java.util.Comparator;
import net.minecraft.entity.Entity;

public class EntityAINearestAttackableTarget$Sorter implements Comparator {

    private final Entity field_75459_b;

    public EntityAINearestAttackableTarget$Sorter(Entity entity) {
        this.field_75459_b = entity;
    }

    public int compare(Entity entity, Entity entity1) {
        double d0 = this.field_75459_b.func_70068_e(entity);
        double d1 = this.field_75459_b.func_70068_e(entity1);

        return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
    }
}
