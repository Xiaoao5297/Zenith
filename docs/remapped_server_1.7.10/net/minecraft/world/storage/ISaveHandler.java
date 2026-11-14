package net.minecraft.world.storage;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;

public interface ISaveHandler {

    WorldInfo func_75757_d();

    void func_75762_c();

    IChunkLoader func_75763_a(WorldProvider worldprovider);

    void func_75755_a(WorldInfo worldinfo, NBTTagCompound nbttagcompound);

    void func_75761_a(WorldInfo worldinfo);

    IPlayerFileData func_75756_e();

    void func_75759_a();

    File func_75765_b();

    File func_75758_b(String s);

    String func_75760_g();
}
