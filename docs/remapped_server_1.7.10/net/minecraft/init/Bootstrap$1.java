package net.minecraft.init;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;

final class Bootstrap$1 extends BehaviorProjectileDispense {

    Bootstrap$1() {}

    @Override
    protected IProjectile func_82499_a(World world, IPosition iposition) {
        EntityArrow entityarrow = new EntityArrow(world, iposition.func_82615_a(), iposition.func_82617_b(), iposition.func_82616_c());

        entityarrow.field_70251_a = 1;
        return entityarrow;
    }
}
