package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemRedstone extends Item {

    public ItemRedstone() {
        this.func_77637_a(CreativeTabs.field_78028_d);
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (world.func_147439_a(i, j, k) != Blocks.field_150431_aC) {
            if (l == 0) {
                --j;
            }

            if (l == 1) {
                ++j;
            }

            if (l == 2) {
                --k;
            }

            if (l == 3) {
                ++k;
            }

            if (l == 4) {
                --i;
            }

            if (l == 5) {
                ++i;
            }

            if (!world.func_147437_c(i, j, k)) {
                return false;
            }
        }

        if (!entityplayer.func_82247_a(i, j, k, l, itemstack)) {
            return false;
        } else {
            if (Blocks.field_150488_af.func_149742_c(world, i, j, k)) {
                --itemstack.field_77994_a;
                world.func_147449_b(i, j, k, Blocks.field_150488_af);
            }

            return true;
        }
    }
}
