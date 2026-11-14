package net.minecraft.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

final class Bootstrap$10 extends BehaviorDefaultDispenseItem {

    private final BehaviorDefaultDispenseItem field_150841_b = new BehaviorDefaultDispenseItem();

    Bootstrap$10() {}

    @Override
    public ItemStack func_82487_b(IBlockSource iblocksource, ItemStack itemstack) {
        ItemBucket itembucket = (ItemBucket) itemstack.func_77973_b();
        int i = iblocksource.func_82623_d();
        int j = iblocksource.func_82622_e();
        int k = iblocksource.func_82621_f();
        EnumFacing enumfacing = BlockDispenser.func_149937_b(iblocksource.func_82620_h());

        if (itembucket.func_77875_a(iblocksource.func_82618_k(), i + enumfacing.func_82601_c(), j + enumfacing.func_96559_d(), k + enumfacing.func_82599_e())) {
            itemstack.func_150996_a(Items.field_151133_ar);
            itemstack.field_77994_a = 1;
            return itemstack;
        } else {
            return this.field_150841_b.func_82482_a(iblocksource, itemstack);
        }
    }
}
