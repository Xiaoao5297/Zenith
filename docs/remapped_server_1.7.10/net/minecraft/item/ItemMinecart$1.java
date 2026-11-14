package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

final class ItemMinecart$1 extends BehaviorDefaultDispenseItem {

    private final BehaviorDefaultDispenseItem field_96465_b = new BehaviorDefaultDispenseItem();

    ItemMinecart$1() {}

    @Override
    public ItemStack func_82487_b(IBlockSource iblocksource, ItemStack itemstack) {
        EnumFacing enumfacing = BlockDispenser.func_149937_b(iblocksource.func_82620_h());
        World world = iblocksource.func_82618_k();
        double d0 = iblocksource.func_82615_a() + (double) ((float) enumfacing.func_82601_c() * 1.125F);
        double d1 = iblocksource.func_82617_b() + (double) ((float) enumfacing.func_96559_d() * 1.125F);
        double d2 = iblocksource.func_82616_c() + (double) ((float) enumfacing.func_82599_e() * 1.125F);
        int i = iblocksource.func_82623_d() + enumfacing.func_82601_c();
        int j = iblocksource.func_82622_e() + enumfacing.func_96559_d();
        int k = iblocksource.func_82621_f() + enumfacing.func_82599_e();
        Block block = world.func_147439_a(i, j, k);
        double d3;

        if (BlockRailBase.func_150051_a(block)) {
            d3 = 0.0D;
        } else {
            if (block.func_149688_o() != Material.field_151579_a || !BlockRailBase.func_150051_a(world.func_147439_a(i, j - 1, k))) {
                return this.field_96465_b.func_82482_a(iblocksource, itemstack);
            }

            d3 = -1.0D;
        }

        EntityMinecart entityminecart = EntityMinecart.func_94090_a(world, d0, d1 + d3, d2, ((ItemMinecart) itemstack.func_77973_b()).field_77841_a);

        if (itemstack.func_82837_s()) {
            entityminecart.func_96094_a(itemstack.func_82833_r());
        }

        world.func_72838_d(entityminecart);
        itemstack.func_77979_a(1);
        return itemstack;
    }

    @Override
    protected void func_82485_a(IBlockSource iblocksource) {
        iblocksource.func_82618_k().func_72926_e(1000, iblocksource.func_82623_d(), iblocksource.func_82622_e(), iblocksource.func_82621_f(), 0);
    }
}
