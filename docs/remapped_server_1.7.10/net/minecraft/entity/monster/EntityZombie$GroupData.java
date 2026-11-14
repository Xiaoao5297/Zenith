package net.minecraft.entity.monster;

import net.minecraft.entity.IEntityLivingData;

class EntityZombie$GroupData implements IEntityLivingData {

    public boolean field_142048_a;
    public boolean field_142046_b;

    private EntityZombie$GroupData(EntityZombie entityzombie, boolean flag, boolean flag1) {
        this.field_142047_c = entityzombie;
        this.field_142048_a = false;
        this.field_142046_b = false;
        this.field_142048_a = flag;
        this.field_142046_b = flag1;
    }
}
