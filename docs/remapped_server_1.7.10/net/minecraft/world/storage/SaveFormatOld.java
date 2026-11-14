package net.minecraft.world.storage;

import java.io.File;
import java.io.FileInputStream;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormatOld implements ISaveFormat {

    private static final Logger field_151479_b = LogManager.getLogger();
    protected final File field_75808_a;

    public SaveFormatOld(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }

        this.field_75808_a = file;
    }

    @Override
    public void func_75800_d() {}

    public WorldInfo func_75803_c(String s) {
        File file = new File(this.field_75808_a, s);

        if (!file.exists()) {
            return null;
        } else {
            File file1 = new File(file, "level.dat");
            NBTTagCompound nbttagcompound;
            NBTTagCompound nbttagcompound1;

            if (file1.exists()) {
                try {
                    nbttagcompound = CompressedStreamTools.func_74796_a(new FileInputStream(file1));
                    nbttagcompound1 = nbttagcompound.func_74775_l("Data");
                    return new WorldInfo(nbttagcompound1);
                } catch (Exception exception) {
                    SaveFormatOld.field_151479_b.error("Exception reading " + file1, (Throwable) exception);
                }
            }

            file1 = new File(file, "level.dat_old");
            if (file1.exists()) {
                try {
                    nbttagcompound = CompressedStreamTools.func_74796_a(new FileInputStream(file1));
                    nbttagcompound1 = nbttagcompound.func_74775_l("Data");
                    return new WorldInfo(nbttagcompound1);
                } catch (Exception exception1) {
                    SaveFormatOld.field_151479_b.error("Exception reading " + file1, (Throwable) exception1);
                }
            }

            return null;
        }
    }

    @Override
    public boolean func_75802_e(String s) {
        File file = new File(this.field_75808_a, s);

        if (!file.exists()) {
            return true;
        } else {
            SaveFormatOld.field_151479_b.info("Deleting level " + s);

            for (int i = 1; i <= 5; ++i) {
                SaveFormatOld.field_151479_b.info("Attempt " + i + "...");
                if (func_75807_a(file.listFiles())) {
                    break;
                }

                SaveFormatOld.field_151479_b.warn("Unsuccessful in deleting contents.");
                if (i < 5) {
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException interruptedexception) {
                        ;
                    }
                }
            }

            return file.delete();
        }
    }

    protected static boolean func_75807_a(File[] afile) {
        for (int i = 0; i < afile.length; ++i) {
            File file = afile[i];

            SaveFormatOld.field_151479_b.debug("Deleting " + file);
            if (file.isDirectory() && !func_75807_a(file.listFiles())) {
                SaveFormatOld.field_151479_b.warn("Couldn't delete directory " + file);
                return false;
            }

            if (!file.delete()) {
                SaveFormatOld.field_151479_b.warn("Couldn't delete file " + file);
                return false;
            }
        }

        return true;
    }

    @Override
    public ISaveHandler func_75804_a(String s, boolean flag) {
        return new SaveHandler(this.field_75808_a, s, flag);
    }

    @Override
    public boolean func_75801_b(String s) {
        return false;
    }

    @Override
    public boolean func_75805_a(String s, IProgressUpdate iprogressupdate) {
        return false;
    }
}
