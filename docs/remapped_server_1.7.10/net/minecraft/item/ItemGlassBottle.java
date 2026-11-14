package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition$MovingObjectType;
import net.minecraft.world.World;

public class ItemGlassBottle extends Item {

    public ItemGlassBottle() {
        this.func_77637_a(CreativeTabs.field_78038_k);
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

                if (world.func_147439_a(i, j, k).func_149688_o() == Material.field_151586_h) {
                    --itemstack.field_77994_a;
                    if (itemstack.field_77994_a <= 0) {
                        return new ItemStack(Items.field_151068_bn);
                    }

                    if (!entityplayer.field_71071_by.func_70441_a(new ItemStack(Items.field_151068_bn))) {
                        entityplayer.func_71019_a(new ItemStack(Items.field_151068_bn, 1, 0), false);
                    }
                }
            }

            return itemstack;
        }
    }
}
