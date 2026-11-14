package net.minecraft.init;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

class Bootstrap$5$1 extends BehaviorProjectileDispense {

    Bootstrap$5$1(Bootstrap$5 bootstrap$5, ItemStack itemstack) {
        this.field_150837_c = bootstrap$5;
        this.field_150836_b = itemstack;
    }

    @Override
    protected IProjectile func_82499_a(World world, IPosition iposition) {
        return new EntityPotion(world, iposition.func_82615_a(), iposition.func_82617_b(), iposition.func_82616_c(), this.field_150836_b.func_77946_l());
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
