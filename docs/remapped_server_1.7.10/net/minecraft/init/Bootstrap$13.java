package net.minecraft.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

final class Bootstrap$13 extends BehaviorDefaultDispenseItem {

    private boolean field_150838_b = true;

    Bootstrap$13() {}

    @Override
    protected ItemStack func_82487_b(IBlockSource iblocksource, ItemStack itemstack) {
        if (itemstack.func_77960_j() == 15) {
            EnumFacing enumfacing = BlockDispenser.func_149937_b(iblocksource.func_82620_h());
            World world = iblocksource.func_82618_k();
            int i = iblocksource.func_82623_d() + enumfacing.func_82601_c();
            int j = iblocksource.func_82622_e() + enumfacing.func_96559_d();
            int k = iblocksource.func_82621_f() + enumfacing.func_82599_e();

            if (ItemDye.func_150919_a(itemstack, world, i, j, k)) {
                if (!world.field_72995_K) {
                    world.func_72926_e(2005, i, j, k, 0);
                }
            } else {
                this.field_150838_b = false;
            }

            return itemstack;
        } else {
            return super.func_82487_b(iblocksource, itemstack);
        }
    }

    @Override
    protected void func_82485_a(IBlockSource iblocksource) {
        if (this.field_150838_b) {
            iblocksource.func_82618_k().func_72926_e(1000, iblocksource.func_82623_d(), iblocksource.func_82622_e(), iblocksource.func_82621_f(), 0);
        } else {
            iblocksource.func_82618_k().func_72926_e(1001, iblocksource.func_82623_d(), iblocksource.func_82622_e(), iblocksource.func_82621_f(), 0);
        }

    }
}
