package net.minecraft.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

final class Bootstrap$11 extends BehaviorDefaultDispenseItem {

    private final BehaviorDefaultDispenseItem field_150840_b = new BehaviorDefaultDispenseItem();

    Bootstrap$11() {}

    @Override
    public ItemStack func_82487_b(IBlockSource iblocksource, ItemStack itemstack) {
        EnumFacing enumfacing = BlockDispenser.func_149937_b(iblocksource.func_82620_h());
        World world = iblocksource.func_82618_k();
        int i = iblocksource.func_82623_d() + enumfacing.func_82601_c();
        int j = iblocksource.func_82622_e() + enumfacing.func_96559_d();
        int k = iblocksource.func_82621_f() + enumfacing.func_82599_e();
        Material material = world.func_147439_a(i, j, k).func_149688_o();
        int l = world.func_72805_g(i, j, k);
        Item item;

        if (Material.field_151586_h.equals(material) && l == 0) {
            item = Items.field_151131_as;
        } else {
            if (!Material.field_151587_i.equals(material) || l != 0) {
                return super.func_82487_b(iblocksource, itemstack);
            }

            item = Items.field_151129_at;
        }

        world.func_147468_f(i, j, k);
        if (--itemstack.field_77994_a == 0) {
            itemstack.func_150996_a(item);
            itemstack.field_77994_a = 1;
        } else if (((TileEntityDispenser) iblocksource.func_150835_j()).func_146019_a(new ItemStack(item)) < 0) {
            this.field_150840_b.func_82482_a(iblocksource, new ItemStack(item));
        }

        return itemstack;
    }
}
