package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

public class ItemEditableBook extends Item {

    public ItemEditableBook() {
        this.func_77625_d(1);
    }

    public static boolean func_77828_a(NBTTagCompound nbttagcompound) {
        if (!ItemWritableBook.func_150930_a(nbttagcompound)) {
            return false;
        } else if (!nbttagcompound.func_150297_b("title", 8)) {
            return false;
        } else {
            String s = nbttagcompound.func_74779_i("title");

            return s != null && s.length() <= 16 ? nbttagcompound.func_150297_b("author", 8) : false;
        }
    }

    @Override
    public String func_77653_i(ItemStack itemstack) {
        if (itemstack.func_77942_o()) {
            NBTTagCompound nbttagcompound = itemstack.func_77978_p();
            String s = nbttagcompound.func_74779_i("title");

            if (!StringUtils.func_151246_b(s)) {
                return s;
            }
        }

        return super.func_77653_i(itemstack);
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
}
