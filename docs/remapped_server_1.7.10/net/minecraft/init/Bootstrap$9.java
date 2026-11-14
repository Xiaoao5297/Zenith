package net.minecraft.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

final class Bootstrap$9 extends BehaviorDefaultDispenseItem {

    private final BehaviorDefaultDispenseItem field_150842_b = new BehaviorDefaultDispenseItem();

    Bootstrap$9() {}

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
        Material material = world.func_147439_a(i, j, k).func_149688_o();
        double d3;

        if (Material.field_151586_h.equals(material)) {
            d3 = 1.0D;
        } else {
            if (!Material.field_151579_a.equals(material) || !Material.field_151586_h.equals(world.func_147439_a(i, j - 1, k).func_149688_o())) {
                return this.field_150842_b.func_82482_a(iblocksource, itemstack);
            }

            d3 = 0.0D;
        }

        EntityBoat entityboat = new EntityBoat(world, d0, d1 + d3, d2);

        world.func_72838_d(entityboat);
        itemstack.func_77979_a(1);
        return itemstack;
    }

    @Override
    protected void func_82485_a(IBlockSource iblocksource) {
        iblocksource.func_82618_k().func_72926_e(1000, iblocksource.func_82623_d(), iblocksource.func_82622_e(), iblocksource.func_82621_f(), 0);
    }
}
