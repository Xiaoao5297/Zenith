package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemLead extends Item {

    public ItemLead() {
        this.func_77637_a(CreativeTabs.field_78040_i);
    }

    @Override
    public boolean func_77648_a(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        Block block = world.func_147439_a(i, j, k);

        if (block.func_149645_b() == 11) {
            if (world.field_72995_K) {
                return true;
            } else {
                func_150909_a(entityplayer, world, i, j, k);
                return true;
            }
        } else {
            return false;
        }
    }

    public static boolean func_150909_a(EntityPlayer entityplayer, World world, int i, int j, int k) {
        EntityLeashKnot entityleashknot = EntityLeashKnot.func_110130_b(world, i, j, k);
        boolean flag = false;
        double d0 = 7.0D;
        List list = world.func_72872_a(EntityLiving.class, AxisAlignedBB.func_72330_a((double) i - d0, (double) j - d0, (double) k - d0, (double) i + d0, (double) j + d0, (double) k + d0));

        if (list != null) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityLiving entityliving = (EntityLiving) iterator.next();

                if (entityliving.func_110167_bD() && entityliving.func_110166_bE() == entityplayer) {
                    if (entityleashknot == null) {
                        entityleashknot = EntityLeashKnot.func_110129_a(world, i, j, k);
                    }

                    entityliving.func_110162_b(entityleashknot, true);
                    flag = true;
                }
            }
        }

        return flag;
    }
}
