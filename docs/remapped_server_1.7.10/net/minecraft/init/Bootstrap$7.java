package net.minecraft.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

final class Bootstrap$7 extends BehaviorDefaultDispenseItem {

    Bootstrap$7() {}

    @Override
    public ItemStack func_82487_b(IBlockSource iblocksource, ItemStack itemstack) {
        EnumFacing enumfacing = BlockDispenser.func_149937_b(iblocksource.func_82620_h());
        double d0 = iblocksource.func_82615_a() + (double) enumfacing.func_82601_c();
        double d1 = (double) ((float) iblocksource.func_82622_e() + 0.2F);
        double d2 = iblocksource.func_82616_c() + (double) enumfacing.func_82599_e();
        EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(iblocksource.func_82618_k(), d0, d1, d2, itemstack);

        iblocksource.func_82618_k().func_72838_d(entityfireworkrocket);
        itemstack.func_77979_a(1);
        return itemstack;
    }

    @Override
    protected void func_82485_a(IBlockSource iblocksource) {
        iblocksource.func_82618_k().func_72926_e(1002, iblocksource.func_82623_d(), iblocksource.func_82622_e(), iblocksource.func_82621_f(), 0);
    }
}
