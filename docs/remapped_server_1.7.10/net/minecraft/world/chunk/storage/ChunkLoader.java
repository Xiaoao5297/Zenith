package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.NibbleArray;

public class ChunkLoader {

    public static ChunkLoader$AnvilConverterData func_76691_a(NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.func_74762_e("xPos");
        int j = nbttagcompound.func_74762_e("zPos");
        ChunkLoader$AnvilConverterData chunkloader$anvilconverterdata = new ChunkLoader$AnvilConverterData(i, j);

        chunkloader$anvilconverterdata.field_76693_g = nbttagcompound.func_74770_j("Blocks");
        chunkloader$anvilconverterdata.field_76692_f = new NibbleArrayReader(nbttagcompound.func_74770_j("Data"), 7);
        chunkloader$anvilconverterdata.field_76695_e = new NibbleArrayReader(nbttagcompound.func_74770_j("SkyLight"), 7);
        chunkloader$anvilconverterdata.field_76694_d = new NibbleArrayReader(nbttagcompound.func_74770_j("BlockLight"), 7);
        chunkloader$anvilconverterdata.field_76697_c = nbttagcompound.func_74770_j("HeightMap");
        chunkloader$anvilconverterdata.field_76696_b = nbttagcompound.func_74767_n("TerrainPopulated");
        chunkloader$anvilconverterdata.field_76702_h = nbttagcompound.func_150295_c("Entities", 10);
        chunkloader$anvilconverterdata.field_151564_i = nbttagcompound.func_150295_c("TileEntities", 10);
        chunkloader$anvilconverterdata.field_151563_j = nbttagcompound.func_150295_c("TileTicks", 10);

        try {
            chunkloader$anvilconverterdata.field_76698_a = nbttagcompound.func_74763_f("LastUpdate");
        } catch (ClassCastException classcastexception) {
            chunkloader$anvilconverterdata.field_76698_a = (long) nbttagcompound.func_74762_e("LastUpdate");
        }

        return chunkloader$anvilconverterdata;
    }

    public static void func_76690_a(ChunkLoader$AnvilConverterData chunkloader$anvilconverterdata, NBTTagCompound nbttagcompound, WorldChunkManager worldchunkmanager) {
        nbttagcompound.func_74768_a("xPos", chunkloader$anvilconverterdata.field_76701_k);
        nbttagcompound.func_74768_a("zPos", chunkloader$anvilconverterdata.field_76699_l);
        nbttagcompound.func_74772_a("LastUpdate", chunkloader$anvilconverterdata.field_76698_a);
        int[] aint = new int[chunkloader$anvilconverterdata.field_76697_c.length];

        for (int i = 0; i < chunkloader$anvilconverterdata.field_76697_c.length; ++i) {
            aint[i] = chunkloader$anvilconverterdata.field_76697_c[i];
        }

        nbttagcompound.func_74783_a("HeightMap", aint);
        nbttagcompound.func_74757_a("TerrainPopulated", chunkloader$anvilconverterdata.field_76696_b);
        NBTTagList nbttaglist = new NBTTagList();

        int j;

        for (int k = 0; k < 8; ++k) {
            boolean flag = true;

            int l;

            for (j = 0; j < 16 && flag; ++j) {
                int i1 = 0;

                while (i1 < 16 && flag) {
                    int j1 = 0;

                    while (true) {
                        if (j1 < 16) {
                            int k1 = j << 11 | j1 << 7 | i1 + (k << 4);

                            l = chunkloader$anvilconverterdata.field_76693_g[k1];
                            if (l == 0) {
                                ++j1;
                                continue;
                            }

                            flag = false;
                        }

                        ++i1;
                        break;
                    }
                }
            }

            if (!flag) {
                byte[] abyte = new byte[4096];
                NibbleArray nibblearray = new NibbleArray(abyte.length, 4);
                NibbleArray nibblearray1 = new NibbleArray(abyte.length, 4);
                NibbleArray nibblearray2 = new NibbleArray(abyte.length, 4);

                for (l = 0; l < 16; ++l) {
                    for (int l1 = 0; l1 < 16; ++l1) {
                        for (int i2 = 0; i2 < 16; ++i2) {
                            int j2 = l << 11 | i2 << 7 | l1 + (k << 4);
                            byte b0 = chunkloader$anvilconverterdata.field_76693_g[j2];

                            abyte[l1 << 8 | i2 << 4 | l] = (byte) (b0 & 255);
                            nibblearray.func_76581_a(l, l1, i2, chunkloader$anvilconverterdata.field_76692_f.func_76686_a(l, l1 + (k << 4), i2));
                            nibblearray1.func_76581_a(l, l1, i2, chunkloader$anvilconverterdata.field_76695_e.func_76686_a(l, l1 + (k << 4), i2));
                            nibblearray2.func_76581_a(l, l1, i2, chunkloader$anvilconverterdata.field_76694_d.func_76686_a(l, l1 + (k << 4), i2));
                        }
                    }
                }

                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.func_74774_a("Y", (byte) (k & 255));
                nbttagcompound1.func_74773_a("Blocks", abyte);
                nbttagcompound1.func_74773_a("Data", nibblearray.field_76585_a);
                nbttagcompound1.func_74773_a("SkyLight", nibblearray1.field_76585_a);
                nbttagcompound1.func_74773_a("BlockLight", nibblearray2.field_76585_a);
                nbttaglist.func_74742_a(nbttagcompound1);
            }
        }

        nbttagcompound.func_74782_a("Sections", nbttaglist);
        byte[] abyte1 = new byte[256];

        for (int k2 = 0; k2 < 16; ++k2) {
            for (j = 0; j < 16; ++j) {
                abyte1[j << 4 | k2] = (byte) (worldchunkmanager.func_76935_a(chunkloader$anvilconverterdata.field_76701_k << 4 | k2, chunkloader$anvilconverterdata.field_76699_l << 4 | j).field_76756_M & 255);
            }
        }

        nbttagcompound.func_74773_a("Biomes", abyte1);
        nbttagcompound.func_74782_a("Entities", chunkloader$anvilconverterdata.field_76702_h);
        nbttagcompound.func_74782_a("TileEntities", chunkloader$anvilconverterdata.field_151564_i);
        if (chunkloader$anvilconverterdata.field_151563_j != null) {
            nbttagcompound.func_74782_a("TileTicks", chunkloader$anvilconverterdata.field_151563_j);
        }

    }
}
