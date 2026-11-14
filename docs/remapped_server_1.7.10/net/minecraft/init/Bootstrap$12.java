package net.minecraft.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

final class Bootstrap$12 extends BehaviorDefaultDispenseItem {

    private boolean field_150839_b = true;

    Bootstrap$12() {}

    @Override
    protected ItemStack func_82487_b(IBlockSource iblocksource, ItemStack itemstack) {
        EnumFacing enumfacing = BlockDispenser.func_149937_b(iblocksource.func_82620_h());
        World world = iblocksource.func_82618_k();
        int i = iblocksource.func_82623_d() + enumfacing.func_82601_c();
        int j = iblocksource.func_82622_e() + enumfacing.func_96559_d();
        int k = iblocksource.func_82621_f() + enumfacing.func_82599_e();

        if (world.func_147437_c(i, j, k)) {
            world.func_147449_b(i, j, k, Blocks.field_150480_ab);
            if (itemstack.func_96631_a(1, world.field_73012_v)) {
                itemstack.field_77994_a = 0;
            }
        } else if (world.func_147439_a(i, j, k) == Blocks.field_150335_W) {
            Blocks.field_150335_W.func_149664_b(world, i, j, k, 1);
            world.func_147468_f(i, j, k);
        } else {
            this.field_150839_b = false;
        }

        return itemstack;
    }

    @Override
    protected void func_82485_a(IBlockSource iblocksource) {
        if (this.field_150839_b) {
            iblocksource.func_82618_k().func_72926_e(1000, iblocksource.func_82623_d(), iblocksource.func_82622_e(), iblocksource.func_82621_f(), 0);
        } else {
            iblocksource.func_82618_k().func_72926_e(1001, iblocksource.func_82623_d(), iblocksource.func_82622_e(), iblocksource.func_82621_f(), 0);
        }

    }
}
