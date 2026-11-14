package net.minecraft.world.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilSaveConverter extends SaveFormatOld {

    private static final Logger field_151480_b = LogManager.getLogger();

    public AnvilSaveConverter(File file) {
        super(file);
    }

    protected int func_75812_c() {
        return 19133;
    }

    @Override
    public void func_75800_d() {
        RegionFileCache.func_76551_a();
    }

    @Override
    public ISaveHandler func_75804_a(String s, boolean flag) {
        return new AnvilSaveHandler(this.field_75808_a, s, flag);
    }

    @Override
    public boolean func_75801_b(String s) {
        WorldInfo worldinfo = this.func_75803_c(s);

        return worldinfo != null && worldinfo.func_76088_k() != this.func_75812_c();
    }

    @Override
    public boolean func_75805_a(String s, IProgressUpdate iprogressupdate) {
        iprogressupdate.func_73718_a(0);
        ArrayList arraylist = new ArrayList();
        ArrayList arraylist1 = new ArrayList();
        ArrayList arraylist2 = new ArrayList();
        File file = new File(this.field_75808_a, s);
        File file1 = new File(file, "DIM-1");
        File file2 = new File(file, "DIM1");

        AnvilSaveConverter.field_151480_b.info("Scanning folders...");
        this.func_75810_a(file, arraylist);
        if (file1.exists()) {
            this.func_75810_a(file1, arraylist1);
        }

        if (file2.exists()) {
            this.func_75810_a(file2, arraylist2);
        }

        int i = arraylist.size() + arraylist1.size() + arraylist2.size();

        AnvilSaveConverter.field_151480_b.info("Total conversion count is " + i);
        WorldInfo worldinfo = this.func_75803_c(s);
        Object object = null;

        if (worldinfo.func_76067_t() == WorldType.field_77138_c) {
            object = new WorldChunkManagerHell(BiomeGenBase.field_76772_c, 0.5F);
        } else {
            object = new WorldChunkManager(worldinfo.func_76063_b(), worldinfo.func_76067_t());
        }

        this.func_75813_a(new File(file, "region"), arraylist, (WorldChunkManager) object, 0, i, iprogressupdate);
        this.func_75813_a(new File(file1, "region"), arraylist1, new WorldChunkManagerHell(BiomeGenBase.field_76778_j, 0.0F), arraylist.size(), i, iprogressupdate);
        this.func_75813_a(new File(file2, "region"), arraylist2, new WorldChunkManagerHell(BiomeGenBase.field_76779_k, 0.0F), arraylist.size() + arraylist1.size(), i, iprogressupdate);
        worldinfo.func_76078_e(19133);
        if (worldinfo.func_76067_t() == WorldType.field_77136_e) {
            worldinfo.func_76085_a(WorldType.field_77137_b);
        }

        this.func_75809_f(s);
        ISaveHandler isavehandler = this.func_75804_a(s, false);

        isavehandler.func_75761_a(worldinfo);
        return true;
    }

    private void func_75809_f(String s) {
        File file = new File(this.field_75808_a, s);

        if (!file.exists()) {
            AnvilSaveConverter.field_151480_b.warn("Unable to create level.dat_mcr backup");
        } else {
            File file1 = new File(file, "level.dat");

            if (!file1.exists()) {
                AnvilSaveConverter.field_151480_b.warn("Unable to create level.dat_mcr backup");
            } else {
                File file2 = new File(file, "level.dat_mcr");

                if (!file1.renameTo(file2)) {
                    AnvilSaveConverter.field_151480_b.warn("Unable to create level.dat_mcr backup");
                }

            }
        }
    }

    private void func_75813_a(File file, Iterable iterable, WorldChunkManager worldchunkmanager, int i, int j, IProgressUpdate iprogressupdate) {
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            File file1 = (File) iterator.next();

            this.func_75811_a(file, file1, worldchunkmanager, i, j, iprogressupdate);
            ++i;
            int k = (int) Math.round(100.0D * (double) i / (double) j);

            iprogressupdate.func_73718_a(k);
        }

    }

    private void func_75811_a(File file, File file1, WorldChunkManager worldchunkmanager, int i, int j, IProgressUpdate iprogressupdate) {
        try {
            String s = file1.getName();
            RegionFile regionfile = new RegionFile(file1);
            RegionFile regionfile1 = new RegionFile(new File(file, s.substring(0, s.length() - ".mcr".length()) + ".mca"));

            for (int k = 0; k < 32; ++k) {
                int l;

                for (l = 0; l < 32; ++l) {
                    if (regionfile.func_76709_c(k, l) && !regionfile1.func_76709_c(k, l)) {
                        DataInputStream datainputstream = regionfile.func_76704_a(k, l);

                        if (datainputstream == null) {
                            AnvilSaveConverter.field_151480_b.warn("Failed to fetch input stream");
                        } else {
                            NBTTagCompound nbttagcompound = CompressedStreamTools.func_74794_a(datainputstream);

                            datainputstream.close();
                            NBTTagCompound nbttagcompound1 = nbttagcompound.func_74775_l("Level");
                            ChunkLoader$AnvilConverterData chunkloader$anvilconverterdata = ChunkLoader.func_76691_a(nbttagcompound1);
                            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                            NBTTagCompound nbttagcompound3 = new NBTTagCompound();

                            nbttagcompound2.func_74782_a("Level", nbttagcompound3);
                            ChunkLoader.func_76690_a(chunkloader$anvilconverterdata, nbttagcompound3, worldchunkmanager);
                            DataOutputStream dataoutputstream = regionfile1.func_76710_b(k, l);

                            CompressedStreamTools.func_74800_a(nbttagcompound2, dataoutputstream);
                            dataoutputstream.close();
                        }
                    }
                }

                l = (int) Math.round(100.0D * (double) (i * 1024) / (double) (j * 1024));
                int i1 = (int) Math.round(100.0D * (double) ((k + 1) * 32 + i * 1024) / (double) (j * 1024));

                if (i1 > l) {
                    iprogressupdate.func_73718_a(i1);
                }
            }

            regionfile.func_76708_c();
            regionfile1.func_76708_c();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }

    }

    private void func_75810_a(File file, Collection collection) {
        File file1 = new File(file, "region");
        File[] afile = file1.listFiles(new AnvilSaveConverter$1(this));

        if (afile != null) {
            Collections.addAll(collection, afile);
        }

    }
}
