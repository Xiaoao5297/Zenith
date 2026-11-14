package net.minecraft.init;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.world.World;

final class Bootstrap$2 extends BehaviorProjectileDispense {

    Bootstrap$2() {}

    @Override
    protected IProjectile func_82499_a(World world, IPosition iposition) {
        return new EntityEgg(world, iposition.func_82615_a(), iposition.func_82617_b(), iposition.func_82616_c());
    }
}
