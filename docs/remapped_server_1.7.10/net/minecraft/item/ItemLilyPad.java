package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition$MovingObjectType;
import net.minecraft.world.World;

public class ItemLilyPad extends ItemColored {

    public ItemLilyPad(Block block) {
        super(block, false);
    }

    @Override
    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        MovingObjectPosition movingobjectposition = this.func_77621_a(world, entityplayer, true);

        if (movingobjectposition == null) {
            return itemstack;
        } else {
            if (movingobjectposition.field_72313_a == MovingObjectPosition$MovingObjectType.BLOCK) {
                int i = movingobjectposition.field_72311_b;
                int j = movingobjectposition.field_72312_c;
                int k = movingobjectposition.field_72309_d;

                if (!world.func_72962_a(entityplayer, i, j, k)) {
                    return itemstack;
                }

                if (!entityplayer.func_82247_a(i, j, k, movingobjectposition.field_72310_e, itemstack)) {
                    return itemstack;
                }

                if (world.func_147439_a(i, j, k).func_149688_o() == Material.field_151586_h && world.func_72805_g(i, j, k) == 0 && world.func_147437_c(i, j + 1, k)) {
                    world.func_147449_b(i, j + 1, k, Blocks.field_150392_bi);
                    if (!entityplayer.field_71075_bZ.field_75098_d) {
                        --itemstack.field_77994_a;
                    }
                }
            }

            return itemstack;
        }
    }
}
