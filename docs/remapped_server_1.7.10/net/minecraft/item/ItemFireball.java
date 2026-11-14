package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemFireball extends Item {

    public ItemFireball() {
        this.func_77637_a(CreativeTabs.field_78026_f);
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (world.field_72995_K) {
            return true;
        } else {
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

            if (!entityplayer.func_82247_a(i, j, k, l, itemstack)) {
                return false;
            } else {
                if (world.func_147439_a(i, j, k).func_149688_o() == Material.field_151579_a) {
                    world.func_72908_a((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D, "fire.ignite", 1.0F, ItemFireball.field_77697_d.nextFloat() * 0.4F + 0.8F);
                    world.func_147449_b(i, j, k, Blocks.field_150480_ab);
                }

                if (!entityplayer.field_71075_bZ.field_75098_d) {
                    --itemstack.field_77994_a;
                }

                return true;
            }
        }
    }
}
