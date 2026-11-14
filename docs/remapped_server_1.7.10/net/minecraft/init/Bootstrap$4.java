package net.minecraft.init;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.world.World;

final class Bootstrap$4 extends BehaviorProjectileDispense {

    Bootstrap$4() {}

    @Override
    protected IProjectile func_82499_a(World world, IPosition iposition) {
        return new EntityExpBottle(world, iposition.func_82615_a(), iposition.func_82617_b(), iposition.func_82616_c());
    }

    @Override
    protected float func_82498_a() {
        return super.func_82498_a() * 0.5F;
    }

    @Override
    protected float func_82500_b() {
        return super.func_82500_b() * 1.25F;
    }
}
