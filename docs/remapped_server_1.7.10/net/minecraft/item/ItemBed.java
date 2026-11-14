package net.minecraft.item;

import net.minecraft.block.BlockBed;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBed extends Item {

    public ItemBed() {
        this.func_77637_a(CreativeTabs.field_78031_c);
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        if (world.field_72995_K) {
            return true;
        } else if (l != 1) {
            return false;
        } else {
            ++j;
            BlockBed blockbed = (BlockBed) Blocks.field_150324_C;
            int i1 = MathHelper.func_76128_c((double) (entityplayer.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3;
            byte b0 = 0;
            byte b1 = 0;

            if (i1 == 0) {
                b1 = 1;
            }

            if (i1 == 1) {
                b0 = -1;
            }

            if (i1 == 2) {
                b1 = -1;
            }

            if (i1 == 3) {
                b0 = 1;
            }

            if (entityplayer.func_82247_a(i, j, k, l, itemstack) && entityplayer.func_82247_a(i + b0, j, k + b1, l, itemstack)) {
                if (world.func_147437_c(i, j, k) && world.func_147437_c(i + b0, j, k + b1) && World.func_147466_a(world, i, j - 1, k) && World.func_147466_a(world, i + b0, j - 1, k + b1)) {
                    world.func_147465_d(i, j, k, blockbed, i1, 3);
                    if (world.func_147439_a(i, j, k) == blockbed) {
                        world.func_147465_d(i + b0, j, k + b1, blockbed, i1 + 8, 3);
                    }

                    --itemstack.field_77994_a;
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
