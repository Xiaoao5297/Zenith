package net.minecraft.init;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;

final class Bootstrap$5 implements IBehaviorDispenseItem {

    private final BehaviorDefaultDispenseItem field_150843_b = new BehaviorDefaultDispenseItem();

    Bootstrap$5() {}

    @Override
    public ItemStack func_82482_a(IBlockSource iblocksource, ItemStack itemstack) {
        return ItemPotion.func_77831_g(itemstack.func_77960_j()) ? (new Bootstrap$5$1(this, itemstack)).func_82482_a(iblocksource, itemstack) : this.field_150843_b.func_82482_a(iblocksource, itemstack);
    }
}
