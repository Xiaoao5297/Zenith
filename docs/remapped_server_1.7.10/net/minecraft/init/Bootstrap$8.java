package net.minecraft.init;

import java.util.Random;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

final class Bootstrap$8 extends BehaviorDefaultDispenseItem {

    Bootstrap$8() {}

    @Override
    public ItemStack func_82487_b(IBlockSource iblocksource, ItemStack itemstack) {
        EnumFacing enumfacing = BlockDispenser.func_149937_b(iblocksource.func_82620_h());
        IPosition iposition = BlockDispenser.func_149939_a(iblocksource);
        double d0 = iposition.func_82615_a() + (double) ((float) enumfacing.func_82601_c() * 0.3F);
        double d1 = iposition.func_82617_b() + (double) ((float) enumfacing.func_82601_c() * 0.3F);
        double d2 = iposition.func_82616_c() + (double) ((float) enumfacing.func_82599_e() * 0.3F);
        World world = iblocksource.func_82618_k();
        Random random = world.field_73012_v;
        double d3 = random.nextGaussian() * 0.05D + (double) enumfacing.func_82601_c();
        double d4 = random.nextGaussian() * 0.05D + (double) enumfacing.func_96559_d();
        double d5 = random.nextGaussian() * 0.05D + (double) enumfacing.func_82599_e();

        world.func_72838_d(new EntitySmallFireball(world, d0, d1, d2, d3, d4, d5));
        itemstack.func_77979_a(1);
        return itemstack;
    }

    @Override
    protected void func_82485_a(IBlockSource iblocksource) {
        iblocksource.func_82618_k().func_72926_e(1009, iblocksource.func_82623_d(), iblocksource.func_82622_e(), iblocksource.func_82621_f(), 0);
    }
}
