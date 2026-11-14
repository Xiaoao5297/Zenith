package net.minecraft.world.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class MapData extends WorldSavedData {

    public int field_76201_a;
    public int field_76199_b;
    public byte field_76200_c;
    public byte field_76197_d;
    public byte[] field_76198_e = new byte[16384];
    public List field_76196_g = new ArrayList();
    private Map field_76202_j = new HashMap();
    public Map field_76203_h = new LinkedHashMap();

    public MapData(String s) {
        super(s);
    }

    @Override
    public void func_76184_a(NBTTagCompound nbttagcompound) {
        this.field_76200_c = nbttagcompound.func_74771_c("dimension");
        this.field_76201_a = nbttagcompound.func_74762_e("xCenter");
        this.field_76199_b = nbttagcompound.func_74762_e("zCenter");
        this.field_76197_d = nbttagcompound.func_74771_c("scale");
        if (this.field_76197_d < 0) {
            this.field_76197_d = 0;
        }

        if (this.field_76197_d > 4) {
            this.field_76197_d = 4;
        }

        short short0 = nbttagcompound.func_74765_d("width");
        short short1 = nbttagcompound.func_74765_d("height");

        if (short0 == 128 && short1 == 128) {
            this.field_76198_e = nbttagcompound.func_74770_j("colors");
        } else {
            byte[] abyte = nbttagcompound.func_74770_j("colors");

            this.field_76198_e = new byte[16384];
            int i = (128 - short0) / 2;
            int j = (128 - short1) / 2;

            for (int k = 0; k < short1; ++k) {
                int l = k + j;

                if (l >= 0 || l < 128) {
                    for (int i1 = 0; i1 < short0; ++i1) {
                        int j1 = i1 + i;

                        if (j1 >= 0 || j1 < 128) {
                            this.field_76198_e[j1 + l * 128] = abyte[i1 + k * short0];
                        }
                    }
                }
            }
        }

    }

    @Override
    public void func_76187_b(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74774_a("dimension", this.field_76200_c);
        nbttagcompound.func_74768_a("xCenter", this.field_76201_a);
        nbttagcompound.func_74768_a("zCenter", this.field_76199_b);
        nbttagcompound.func_74774_a("scale", this.field_76197_d);
        nbttagcompound.func_74777_a("width", (short) 128);
        nbttagcompound.func_74777_a("height", (short) 128);
        nbttagcompound.func_74773_a("colors", this.field_76198_e);
    }

    public void func_76191_a(EntityPlayer entityplayer, ItemStack itemstack) {
        if (!this.field_76202_j.containsKey(entityplayer)) {
            MapData$MapInfo mapdata$mapinfo = new MapData$MapInfo(this, entityplayer);

            this.field_76202_j.put(entityplayer, mapdata$mapinfo);
            this.field_76196_g.add(mapdata$mapinfo);
        }

        if (!entityplayer.field_71071_by.func_70431_c(itemstack)) {
            this.field_76203_h.remove(entityplayer.func_70005_c_());
        }

        for (int i = 0; i < this.field_76196_g.size(); ++i) {
            MapData$MapInfo mapdata$mapinfo1 = (MapData$MapInfo) this.field_76196_g.get(i);

            if (!mapdata$mapinfo1.field_76211_a.field_70128_L && (mapdata$mapinfo1.field_76211_a.field_71071_by.func_70431_c(itemstack) || itemstack.func_82839_y())) {
                if (!itemstack.func_82839_y() && mapdata$mapinfo1.field_76211_a.field_71093_bK == this.field_76200_c) {
                    this.func_82567_a(0, mapdata$mapinfo1.field_76211_a.field_70170_p, mapdata$mapinfo1.field_76211_a.func_70005_c_(), mapdata$mapinfo1.field_76211_a.field_70165_t, mapdata$mapinfo1.field_76211_a.field_70161_v, (double) mapdata$mapinfo1.field_76211_a.field_70177_z);
                }
            } else {
                this.field_76202_j.remove(mapdata$mapinfo1.field_76211_a);
                this.field_76196_g.remove(mapdata$mapinfo1);
            }
        }

        if (itemstack.func_82839_y()) {
            this.func_82567_a(1, entityplayer.field_70170_p, "frame-" + itemstack.func_82836_z().func_145782_y(), (double) itemstack.func_82836_z().field_146063_b, (double) itemstack.func_82836_z().field_146062_d, (double) (itemstack.func_82836_z().field_82332_a * 90));
        }

    }

    private void func_82567_a(int i, World world, String s, double d0, double d1, double d2) {
        int j = 1 << this.field_76197_d;
        float f = (float) (d0 - (double) this.field_76201_a) / (float) j;
        float f1 = (float) (d1 - (double) this.field_76199_b) / (float) j;
        byte b0 = (byte) ((int) ((double) (f * 2.0F) + 0.5D));
        byte b1 = (byte) ((int) ((double) (f1 * 2.0F) + 0.5D));
        byte b2 = 63;
        byte b3;

        if (f >= (float) (-b2) && f1 >= (float) (-b2) && f <= (float) b2 && f1 <= (float) b2) {
            d2 += d2 < 0.0D ? -8.0D : 8.0D;
            b3 = (byte) ((int) (d2 * 16.0D / 360.0D));
            if (this.field_76200_c < 0) {
                int k = (int) (world.func_72912_H().func_76073_f() / 10L);

                b3 = (byte) (k * k * 34187121 + k * 121 >> 15 & 15);
            }
        } else {
            if (Math.abs(f) >= 320.0F || Math.abs(f1) >= 320.0F) {
                this.field_76203_h.remove(s);
                return;
            }

            i = 6;
            b3 = 0;
            if (f <= (float) (-b2)) {
                b0 = (byte) ((int) ((double) (b2 * 2) + 2.5D));
            }

            if (f1 <= (float) (-b2)) {
                b1 = (byte) ((int) ((double) (b2 * 2) + 2.5D));
            }

            if (f >= (float) b2) {
                b0 = (byte) (b2 * 2 + 1);
            }

            if (f1 >= (float) b2) {
                b1 = (byte) (b2 * 2 + 1);
            }
        }

        this.field_76203_h.put(s, new MapData$MapCoord(this, (byte) i, b0, b1, b3));
    }

    public byte[] func_76193_a(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        MapData$MapInfo mapdata$mapinfo = (MapData$MapInfo) this.field_76202_j.get(entityplayer);

        return mapdata$mapinfo == null ? null : mapdata$mapinfo.func_76204_a(itemstack);
    }

    public void func_76194_a(int i, int j, int k) {
        super.func_76185_a();

        for (int l = 0; l < this.field_76196_g.size(); ++l) {
            MapData$MapInfo mapdata$mapinfo = (MapData$MapInfo) this.field_76196_g.get(l);

            if (mapdata$mapinfo.field_76209_b[i] < 0 || mapdata$mapinfo.field_76209_b[i] > j) {
                mapdata$mapinfo.field_76209_b[i] = j;
            }

            if (mapdata$mapinfo.field_76210_c[i] < 0 || mapdata$mapinfo.field_76210_c[i] < k) {
                mapdata$mapinfo.field_76210_c[i] = k;
            }
        }

    }

    public MapData$MapInfo func_82568_a(EntityPlayer entityplayer) {
        MapData$MapInfo mapdata$mapinfo = (MapData$MapInfo) this.field_76202_j.get(entityplayer);

        if (mapdata$mapinfo == null) {
            mapdata$mapinfo = new MapData$MapInfo(this, entityplayer);
            this.field_76202_j.put(entityplayer, mapdata$mapinfo);
            this.field_76196_g.add(mapdata$mapinfo);
        }

        return mapdata$mapinfo;
    }
}
