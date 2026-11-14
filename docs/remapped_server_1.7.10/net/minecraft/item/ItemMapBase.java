package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class ItemMapBase extends Item {

    protected ItemMapBase() {}

    @Override
    public boolean func_77643_m_() {
        return true;
    }

    public Packet func_150911_c(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        return null;
    }
}
