package net.minecraft.world.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IPlayerFileData {

    void func_75753_a(EntityPlayer entityplayer);

    NBTTagCompound func_75752_b(EntityPlayer entityplayer);

    String[] func_75754_f();
}
