package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class ItemWritableBook extends Item {

    public ItemWritableBook() {
        this.func_77625_d(1);
    }

    @Override
    public ItemStack func_77659_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        entityplayer.func_71048_c(itemstack);
        return itemstack;
    }

    @Override
    public boolean func_77651_p() {
        return true;
    }

    public static boolean func_150930_a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound == null) {
            return false;
        } else if (!nbttagcompound.func_150297_b("pages", 9)) {
            return false;
        } else {
            NBTTagList nbttaglist = nbttagcompound.func_150295_c("pages", 8);

            for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
                String s = nbttaglist.func_150307_f(i);

                if (s == null) {
                    return false;
                }

                if (s.length() > 256) {
                    return false;
                }
            }

            return true;
        }
    }
}
