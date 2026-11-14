package net.minecraft.world.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveHandler implements ISaveHandler, IPlayerFileData {

    private static final Logger field_151478_a = LogManager.getLogger();
    private final File field_75770_b;
    private final File field_75771_c;
    private final File field_75768_d;
    private final long field_75769_e = MinecraftServer.func_130071_aq();
    private final String field_75767_f;

    public SaveHandler(File file, String s, boolean flag) {
        this.field_75770_b = new File(file, s);
        this.field_75770_b.mkdirs();
        this.field_75771_c = new File(this.field_75770_b, "playerdata");
        this.field_75768_d = new File(this.field_75770_b, "data");
        this.field_75768_d.mkdirs();
        this.field_75767_f = s;
        if (flag) {
            this.field_75771_c.mkdirs();
        }

        this.func_75766_h();
    }

    private void func_75766_h() {
        try {
            File file = new File(this.field_75770_b, "session.lock");
            DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file));

            try {
                dataoutputstream.writeLong(this.field_75769_e);
            } finally {
                dataoutputstream.close();
            }

        } catch (IOException ioexception) {
            ioexception.printStackTrace();
            throw new RuntimeException("Failed to check session lock, aborting");
        }
    }

    @Override
    public File func_75765_b() {
        return this.field_75770_b;
    }

    @Override
    public void func_75762_c() {
        try {
            File file = new File(this.field_75770_b, "session.lock");
            DataInputStream datainputstream = new DataInputStream(new FileInputStream(file));

            try {
                if (datainputstream.readLong() != this.field_75769_e) {
                    throw new MinecraftException("The save is being accessed from another location, aborting");
                }
            } finally {
                datainputstream.close();
            }

        } catch (IOException ioexception) {
            throw new MinecraftException("Failed to check session lock, aborting");
        }
    }

    @Override
    public IChunkLoader func_75763_a(WorldProvider worldprovider) {
        throw new RuntimeException("Old Chunk Storage is no longer supported.");
    }

    @Override
    public WorldInfo func_75757_d() {
        File file = new File(this.field_75770_b, "level.dat");
        NBTTagCompound nbttagcompound;
        NBTTagCompound nbttagcompound1;

        if (file.exists()) {
            try {
                nbttagcompound = CompressedStreamTools.func_74796_a(new FileInputStream(file));
                nbttagcompound1 = nbttagcompound.func_74775_l("Data");
                return new WorldInfo(nbttagcompound1);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        file = new File(this.field_75770_b, "level.dat_old");
        if (file.exists()) {
            try {
                nbttagcompound = CompressedStreamTools.func_74796_a(new FileInputStream(file));
                nbttagcompound1 = nbttagcompound.func_74775_l("Data");
                return new WorldInfo(nbttagcompound1);
            } catch (Exception exception1) {
                exception1.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void func_75755_a(WorldInfo worldinfo, NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = worldinfo.func_76082_a(nbttagcompound);
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();

        nbttagcompound2.func_74782_a("Data", nbttagcompound1);

        try {
            File file = new File(this.field_75770_b, "level.dat_new");
            File file1 = new File(this.field_75770_b, "level.dat_old");
            File file2 = new File(this.field_75770_b, "level.dat");

            CompressedStreamTools.func_74799_a(nbttagcompound2, new FileOutputStream(file));
            if (file1.exists()) {
                file1.delete();
            }

            file2.renameTo(file1);
            if (file2.exists()) {
                file2.delete();
            }

            file.renameTo(file2);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    @Override
    public void func_75761_a(WorldInfo worldinfo) {
        NBTTagCompound nbttagcompound = worldinfo.func_76066_a();
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        nbttagcompound1.func_74782_a("Data", nbttagcompound);

        try {
            File file = new File(this.field_75770_b, "level.dat_new");
            File file1 = new File(this.field_75770_b, "level.dat_old");
            File file2 = new File(this.field_75770_b, "level.dat");

            CompressedStreamTools.func_74799_a(nbttagcompound1, new FileOutputStream(file));
            if (file1.exists()) {
                file1.delete();
            }

            file2.renameTo(file1);
            if (file2.exists()) {
                file2.delete();
            }

            file.renameTo(file2);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    @Override
    public void func_75753_a(EntityPlayer entityplayer) {
        try {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            entityplayer.func_70109_d(nbttagcompound);
            File file = new File(this.field_75771_c, entityplayer.func_110124_au().toString() + ".dat.tmp");
            File file1 = new File(this.field_75771_c, entityplayer.func_110124_au().toString() + ".dat");

            CompressedStreamTools.func_74799_a(nbttagcompound, new FileOutputStream(file));
            if (file1.exists()) {
                file1.delete();
            }

            file.renameTo(file1);
        } catch (Exception exception) {
            SaveHandler.field_151478_a.warn("Failed to save player data for " + entityplayer.func_70005_c_());
        }

    }

    @Override
    public NBTTagCompound func_75752_b(EntityPlayer entityplayer) {
        NBTTagCompound nbttagcompound = null;

        try {
            File file = new File(this.field_75771_c, entityplayer.func_110124_au().toString() + ".dat");

            if (file.exists() && file.isFile()) {
                nbttagcompound = CompressedStreamTools.func_74796_a(new FileInputStream(file));
            }
        } catch (Exception exception) {
            SaveHandler.field_151478_a.warn("Failed to load player data for " + entityplayer.func_70005_c_());
        }

        if (nbttagcompound != null) {
            entityplayer.func_70020_e(nbttagcompound);
        }

        return nbttagcompound;
    }

    @Override
    public IPlayerFileData func_75756_e() {
        return this;
    }

    @Override
    public String[] func_75754_f() {
        String[] astring = this.field_75771_c.list();

        for (int i = 0; i < astring.length; ++i) {
            if (astring[i].endsWith(".dat")) {
                astring[i] = astring[i].substring(0, astring[i].length() - 4);
            }
        }

        return astring;
    }

    @Override
    public void func_75759_a() {}

    @Override
    public File func_75758_b(String s) {
        return new File(this.field_75768_d, s + ".dat");
    }

    @Override
    public String func_75760_g() {
        return this.field_75767_f;
    }
}
