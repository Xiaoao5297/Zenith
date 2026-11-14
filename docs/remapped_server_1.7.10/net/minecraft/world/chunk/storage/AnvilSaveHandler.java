package net.minecraft.world.chunk.storage;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldInfo;

public class AnvilSaveHandler extends SaveHandler {

    public AnvilSaveHandler(File file, String s, boolean flag) {
        super(file, s, flag);
    }

    @Override
    public IChunkLoader func_75763_a(WorldProvider worldprovider) {
        File file = this.func_75765_b();
        File file1;

        if (worldprovider instanceof WorldProviderHell) {
            file1 = new File(file, "DIM-1");
            file1.mkdirs();
            return new AnvilChunkLoader(file1);
        } else if (worldprovider instanceof WorldProviderEnd) {
            file1 = new File(file, "DIM1");
            file1.mkdirs();
            return new AnvilChunkLoader(file1);
        } else {
            return new AnvilChunkLoader(file);
        }
    }

    @Override
    public void func_75755_a(WorldInfo worldinfo, NBTTagCompound nbttagcompound) {
        worldinfo.func_76078_e(19133);
        super.func_75755_a(worldinfo, nbttagcompound);
    }

    @Override
    public void func_75759_a() {
        try {
            ThreadedFileIOBase.field_75741_a.func_75734_a();
        } catch (InterruptedException interruptedexception) {
            interruptedexception.printStackTrace();
        }

        RegionFileCache.func_76551_a();
    }
}
