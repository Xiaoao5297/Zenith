package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BehaviorProjectileDispense extends BehaviorDefaultDispenseItem {

    public BehaviorProjectileDispense() {}

    @Override
    public ItemStack func_82487_b(IBlockSource iblocksource, ItemStack itemstack) {
        World world = iblocksource.func_82618_k();
        IPosition iposition = BlockDispenser.func_149939_a(iblocksource);
        EnumFacing enumfacing = BlockDispenser.func_149937_b(iblocksource.func_82620_h());
        IProjectile iprojectile = this.func_82499_a(world, iposition);

        iprojectile.func_70186_c((double) enumfacing.func_82601_c(), (double) ((float) enumfacing.func_96559_d() + 0.1F), (double) enumfacing.func_82599_e(), this.func_82500_b(), this.func_82498_a());
        world.func_72838_d((Entity) iprojectile);
        itemstack.func_77979_a(1);
        return itemstack;
    }

    @Override
    protected void func_82485_a(IBlockSource iblocksource) {
        iblocksource.func_82618_k().func_72926_e(1002, iblocksource.func_82623_d(), iblocksource.func_82622_e(), iblocksource.func_82621_f(), 0);
    }

    protected abstract IProjectile func_82499_a(World world, IPosition iposition);

    protected float func_82498_a() {
        return 6.0F;
    }

    protected float func_82500_b() {
        return 1.1F;
    }
}
