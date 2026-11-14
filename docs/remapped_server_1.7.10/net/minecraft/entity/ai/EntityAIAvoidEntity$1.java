package net.minecraft.entity.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

class EntityAIAvoidEntity$1 implements IEntitySelector {

    EntityAIAvoidEntity$1(EntityAIAvoidEntity entityaiavoidentity) {
        this.field_98219_c = entityaiavoidentity;
    }

    @Override
    public boolean func_82704_a(Entity entity) {
        return entity.func_70089_S() && EntityAIAvoidEntity.access$000(this.field_98219_c).func_70635_at().func_75522_a(entity);
    }
}
