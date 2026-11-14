package net.minecraft.world.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.world.WorldSavedData;

public class MapStorage {

    private ISaveHandler field_75751_a;
    private Map field_75749_b = new HashMap();
    private List field_75750_c = new ArrayList();
    private Map field_75748_d = new HashMap();

    public MapStorage(ISaveHandler isavehandler) {
        this.field_75751_a = isavehandler;
        this.func_75746_b();
    }

    public WorldSavedData func_75742_a(Class oclass, String s) {
        WorldSavedData worldsaveddata = (WorldSavedData) this.field_75749_b.get(s);

        if (worldsaveddata != null) {
            return worldsaveddata;
        } else {
            if (this.field_75751_a != null) {
                try {
                    File file = this.field_75751_a.func_75758_b(s);

                    if (file != null && file.exists()) {
                        try {
                            worldsaveddata = (WorldSavedData) oclass.getConstructor(String.class).newInstance(s);
                        } catch (Exception exception) {
                            throw new RuntimeException("Failed to instantiate " + oclass.toString(), exception);
                        }

                        FileInputStream fileinputstream = new FileInputStream(file);
                        NBTTagCompound nbttagcompound = CompressedStreamTools.func_74796_a(fileinputstream);

                        fileinputstream.close();
                        worldsaveddata.func_76184_a(nbttagcompound.func_74775_l("data"));
                    }
                } catch (Exception exception1) {
                    exception1.printStackTrace();
                }
            }

            if (worldsaveddata != null) {
                this.field_75749_b.put(s, worldsaveddata);
                this.field_75750_c.add(worldsaveddata);
            }

            return worldsaveddata;
        }
    }

    public void func_75745_a(String s, WorldSavedData worldsaveddata) {
        if (worldsaveddata == null) {
            throw new RuntimeException("Can't set null data");
        } else {
            if (this.field_75749_b.containsKey(s)) {
                this.field_75750_c.remove(this.field_75749_b.remove(s));
            }

            this.field_75749_b.put(s, worldsaveddata);
            this.field_75750_c.add(worldsaveddata);
        }
    }

    public void func_75744_a() {
        for (int i = 0; i < this.field_75750_c.size(); ++i) {
            WorldSavedData worldsaveddata = (WorldSavedData) this.field_75750_c.get(i);

            if (worldsaveddata.func_76188_b()) {
                this.func_75747_a(worldsaveddata);
                worldsaveddata.func_76186_a(false);
            }
        }

    }

    private void func_75747_a(WorldSavedData worldsaveddata) {
        if (this.field_75751_a != null) {
            try {
                File file = this.field_75751_a.func_75758_b(worldsaveddata.field_76190_i);

                if (file != null) {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();

                    worldsaveddata.func_76187_b(nbttagcompound);
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                    nbttagcompound1.func_74782_a("data", nbttagcompound);
                    FileOutputStream fileoutputstream = new FileOutputStream(file);

                    CompressedStreamTools.func_74799_a(nbttagcompound1, fileoutputstream);
                    fileoutputstream.close();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        }
    }

    private void func_75746_b() {
        try {
            this.field_75748_d.clear();
            if (this.field_75751_a == null) {
                return;
            }

            File file = this.field_75751_a.func_75758_b("idcounts");

            if (file != null && file.exists()) {
                DataInputStream datainputstream = new DataInputStream(new FileInputStream(file));
                NBTTagCompound nbttagcompound = CompressedStreamTools.func_74794_a(datainputstream);

                datainputstream.close();
                Iterator iterator = nbttagcompound.func_150296_c().iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();
                    NBTBase nbtbase = nbttagcompound.func_74781_a(s);

                    if (nbtbase instanceof NBTTagShort) {
                        NBTTagShort nbttagshort = (NBTTagShort) nbtbase;
                        short short0 = nbttagshort.func_150289_e();

                        this.field_75748_d.put(s, short0);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public int func_75743_a(String s) {
        Short oshort = (Short) this.field_75748_d.get(s);

        if (oshort == null) {
            oshort = Short.valueOf((short) 0);
        } else {
            oshort = (short) (oshort + 1);
        }

        this.field_75748_d.put(s, oshort);
        if (this.field_75751_a == null) {
            return oshort;
        } else {
            try {
                File file = this.field_75751_a.func_75758_b("idcounts");

                if (file != null) {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    Iterator iterator = this.field_75748_d.keySet().iterator();

                    while (iterator.hasNext()) {
                        String s1 = (String) iterator.next();
                        short short0 = (Short) this.field_75748_d.get(s1);

                        nbttagcompound.func_74777_a(s1, short0);
                    }

                    DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file));

                    CompressedStreamTools.func_74800_a(nbttagcompound, dataoutputstream);
                    dataoutputstream.close();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return oshort;
        }
    }
}
