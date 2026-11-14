package net.minecraft.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

final class Bootstrap$6 extends BehaviorDefaultDispenseItem {

    Bootstrap$6() {}

    @Override
    public ItemStack func_82487_b(IBlockSource iblocksource, ItemStack itemstack) {
        EnumFacing enumfacing = BlockDispenser.func_149937_b(iblocksource.func_82620_h());
        double d0 = iblocksource.func_82615_a() + (double) enumfacing.func_82601_c();
        double d1 = (double) ((float) iblocksource.func_82622_e() + 0.2F);
        double d2 = iblocksource.func_82616_c() + (double) enumfacing.func_82599_e();
        Entity entity = ItemMonsterPlacer.func_77840_a(iblocksource.func_82618_k(), itemstack.func_77960_j(), d0, d1, d2);

        if (entity instanceof EntityLivingBase && itemstack.func_82837_s()) {
            ((EntityLiving) entity).func_94058_c(itemstack.func_82833_r());
        }

        itemstack.func_77979_a(1);
        return itemstack;
    }
}
