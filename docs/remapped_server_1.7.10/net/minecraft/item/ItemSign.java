package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemSign extends Item {

    public ItemSign() {
        this.field_77777_bU = 16;
        this.func_77637_a(CreativeTabs.field_78031_c);
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (l == 0) {
            return false;
        } else if (!world.func_147439_a(i, j, k).func_149688_o().func_76220_a()) {
            return false;
        } else {
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

            if (!entityplayer.func_82247_a(i, j, k, l, itemstack)) {
                return false;
            } else if (!Blocks.field_150472_an.func_149742_c(world, i, j, k)) {
                return false;
            } else if (world.field_72995_K) {
                return true;
            } else {
                if (l == 1) {
                    int i1 = MathHelper.func_76128_c((double) ((entityplayer.field_70177_z + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;

                    world.func_147465_d(i, j, k, Blocks.field_150472_an, i1, 3);
                } else {
                    world.func_147465_d(i, j, k, Blocks.field_150444_as, l, 3);
                }

                --itemstack.field_77994_a;
                TileEntitySign tileentitysign = (TileEntitySign) world.func_147438_o(i, j, k);

                if (tileentitysign != null) {
                    entityplayer.func_146100_a(tileentitysign);
                }

                return true;
            }
        }
    }
}
