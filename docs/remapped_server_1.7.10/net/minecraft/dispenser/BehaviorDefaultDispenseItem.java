package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorDefaultDispenseItem implements IBehaviorDispenseItem {

    public BehaviorDefaultDispenseItem() {}

    @Override
    public final ItemStack func_82482_a(IBlockSource iblocksource, ItemStack itemstack) {
        ItemStack itemstack1 = this.func_82487_b(iblocksource, itemstack);

        this.func_82485_a(iblocksource);
        this.func_82489_a(iblocksource, BlockDispenser.func_149937_b(iblocksource.func_82620_h()));
        return itemstack1;
    }

    protected ItemStack func_82487_b(IBlockSource iblocksource, ItemStack itemstack) {
        EnumFacing enumfacing = BlockDispenser.func_149937_b(iblocksource.func_82620_h());
        IPosition iposition = BlockDispenser.func_149939_a(iblocksource);
        ItemStack itemstack1 = itemstack.func_77979_a(1);

        func_82486_a(iblocksource.func_82618_k(), itemstack1, 6, enumfacing, iposition);
        return itemstack;
    }

    public static void func_82486_a(World world, ItemStack itemstack, int i, EnumFacing enumfacing, IPosition iposition) {
        double d0 = iposition.func_82615_a();
        double d1 = iposition.func_82617_b();
        double d2 = iposition.func_82616_c();
        EntityItem entityitem = new EntityItem(world, d0, d1 - 0.3D, d2, itemstack);
        double d3 = world.field_73012_v.nextDouble() * 0.1D + 0.2D;

        entityitem.field_70159_w = (double) enumfacing.func_82601_c() * d3;
        entityitem.field_70181_x = 0.20000000298023224D;
        entityitem.field_70179_y = (double) enumfacing.func_82599_e() * d3;
        entityitem.field_70159_w += world.field_73012_v.nextGaussian() * 0.007499999832361937D * (double) i;
        entityitem.field_70181_x += world.field_73012_v.nextGaussian() * 0.007499999832361937D * (double) i;
        entityitem.field_70179_y += world.field_73012_v.nextGaussian() * 0.007499999832361937D * (double) i;
        world.func_72838_d(entityitem);
    }

    protected void func_82485_a(IBlockSource iblocksource) {
        iblocksource.func_82618_k().func_72926_e(1000, iblocksource.func_82623_d(), iblocksource.func_82622_e(), iblocksource.func_82621_f(), 0);
    }

    protected void func_82489_a(IBlockSource iblocksource, EnumFacing enumfacing) {
        iblocksource.func_82618_k().func_72926_e(2000, iblocksource.func_82623_d(), iblocksource.func_82622_e(), iblocksource.func_82621_f(), this.func_82488_a(enumfacing));
    }

    private int func_82488_a(EnumFacing enumfacing) {
        return enumfacing.func_82601_c() + 1 + (enumfacing.func_82599_e() + 1) * 3;
    }
}
