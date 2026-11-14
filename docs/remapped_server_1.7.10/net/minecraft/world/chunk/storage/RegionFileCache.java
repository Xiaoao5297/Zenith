package net.minecraft.world.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegionFileCache {

    private static final Map field_76553_a = new HashMap();

    public static synchronized RegionFile func_76550_a(File file, int i, int j) {
        File file1 = new File(file, "region");
        File file2 = new File(file1, "r." + (i >> 5) + "." + (j >> 5) + ".mca");
        RegionFile regionfile = (RegionFile) RegionFileCache.field_76553_a.get(file2);

        if (regionfile != null) {
            return regionfile;
        } else {
            if (!file1.exists()) {
                file1.mkdirs();
            }

            if (RegionFileCache.field_76553_a.size() >= 256) {
                func_76551_a();
            }

            RegionFile regionfile1 = new RegionFile(file2);

            RegionFileCache.field_76553_a.put(file2, regionfile1);
            return regionfile1;
        }
    }

    public static synchronized void func_76551_a() {
        Iterator iterator = RegionFileCache.field_76553_a.values().iterator();

        while (iterator.hasNext()) {
            RegionFile regionfile = (RegionFile) iterator.next();

            try {
                if (regionfile != null) {
                    regionfile.func_76708_c();
                }
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }

        RegionFileCache.field_76553_a.clear();
    }

    public static DataInputStream func_76549_c(File file, int i, int j) {
        RegionFile regionfile = func_76550_a(file, i, j);

        return regionfile.func_76704_a(i & 31, j & 31);
    }

    public static DataOutputStream func_76552_d(File file, int i, int j) {
        RegionFile regionfile = func_76550_a(file, i, j);

        return regionfile.func_76710_b(i & 31, j & 31);
    }
}
