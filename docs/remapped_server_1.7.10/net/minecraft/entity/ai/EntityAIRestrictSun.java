package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;

public class EntityAIRestrictSun extends EntityAIBase {

    private EntityCreature field_75273_a;

    public EntityAIRestrictSun(EntityCreature entitycreature) {
        this.field_75273_a = entitycreature;
    }

    @Override
    public boolean func_75250_a() {
        return this.field_75273_a.field_70170_p.func_72935_r();
    }

    @Override
    public void func_75249_e() {
        this.field_75273_a.func_70661_as().func_75504_d(true);
    }

    @Override
    public void func_75251_c() {
        this.field_75273_a.func_70661_as().func_75504_d(false);
    }
}
