package net.minecraft.block;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

public class BlockDropper extends BlockDispenser {

    private final IBehaviorDispenseItem field_149947_P = new BehaviorDefaultDispenseItem();

    public BlockDropper() {}

    @Override
    protected IBehaviorDispenseItem func_149940_a(ItemStack itemstack) {
        return this.field_149947_P;
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        return new TileEntityDropper();
    }

    @Override
    protected void func_149941_e(World world, int i, int j, int k) {
        BlockSourceImpl blocksourceimpl = new BlockSourceImpl(world, i, j, k);
        TileEntityDispenser tileentitydispenser = (TileEntityDispenser) blocksourceimpl.func_150835_j();

        if (tileentitydispenser != null) {
            int l = tileentitydispenser.func_146017_i();

            if (l < 0) {
                world.func_72926_e(1001, i, j, k, 0);
            } else {
                ItemStack itemstack = tileentitydispenser.func_70301_a(l);
                int i1 = world.func_72805_g(i, j, k) & 7;
                IInventory iinventory = TileEntityHopper.func_145893_b(world, (double) (i + Facing.field_71586_b[i1]), (double) (j + Facing.field_71587_c[i1]), (double) (k + Facing.field_71585_d[i1]));
                ItemStack itemstack1;

                if (iinventory != null) {
                    itemstack1 = TileEntityHopper.func_145889_a(iinventory, itemstack.func_77946_l().func_77979_a(1), Facing.field_71588_a[i1]);
                    if (itemstack1 == null) {
                        itemstack1 = itemstack.func_77946_l();
                        if (--itemstack1.field_77994_a == 0) {
                            itemstack1 = null;
                        }
                    } else {
                        itemstack1 = itemstack.func_77946_l();
                    }
                } else {
                    itemstack1 = this.field_149947_P.func_82482_a(blocksourceimpl, itemstack);
                    if (itemstack1 != null && itemstack1.field_77994_a == 0) {
                        itemstack1 = null;
                    }
                }

                tileentitydispenser.func_70299_a(l, itemstack1);
            }

        }
    }
}
