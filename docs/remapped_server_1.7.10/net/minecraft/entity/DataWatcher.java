package net.minecraft.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ReportedException;
import org.apache.commons.lang3.ObjectUtils;

public class DataWatcher {

    private final Entity field_151511_a;
    private boolean field_92086_a = true;
    private static final HashMap field_75697_a = new HashMap();
    private final Map field_75695_b = new HashMap();
    private boolean field_75696_c;
    private ReadWriteLock field_75694_d = new ReentrantReadWriteLock();

    public DataWatcher(Entity entity) {
        this.field_151511_a = entity;
    }

    public void func_75682_a(int i, Object object) {
        Integer integer = (Integer) DataWatcher.field_75697_a.get(object.getClass());

        if (integer == null) {
            throw new IllegalArgumentException("Unknown data type: " + object.getClass());
        } else if (i > 31) {
            throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 31 + ")");
        } else if (this.field_75695_b.containsKey(i)) {
            throw new IllegalArgumentException("Duplicate id value for " + i + "!");
        } else {
            DataWatcher$WatchableObject datawatcher$watchableobject = new DataWatcher$WatchableObject(integer, i, object);

            this.field_75694_d.writeLock().lock();
            this.field_75695_b.put(i, datawatcher$watchableobject);
            this.field_75694_d.writeLock().unlock();
            this.field_92086_a = false;
        }
    }

    public void func_82709_a(int i, int j) {
        DataWatcher$WatchableObject datawatcher$watchableobject = new DataWatcher$WatchableObject(j, i, (Object) null);

        this.field_75694_d.writeLock().lock();
        this.field_75695_b.put(i, datawatcher$watchableobject);
        this.field_75694_d.writeLock().unlock();
        this.field_92086_a = false;
    }

    public byte func_75683_a(int i) {
        return (Byte) this.func_75691_i(i).func_75669_b();
    }

    public short func_75693_b(int i) {
        return (Short) this.func_75691_i(i).func_75669_b();
    }

    public int func_75679_c(int i) {
        return (Integer) this.func_75691_i(i).func_75669_b();
    }

    public float func_111145_d(int i) {
        return (Float) this.func_75691_i(i).func_75669_b();
    }

    public String func_75681_e(int i) {
        return (String) this.func_75691_i(i).func_75669_b();
    }

    public ItemStack func_82710_f(int i) {
        return (ItemStack) this.func_75691_i(i).func_75669_b();
    }

    private DataWatcher$WatchableObject func_75691_i(int i) {
        this.field_75694_d.readLock().lock();

        DataWatcher$WatchableObject datawatcher$watchableobject;

        try {
            datawatcher$watchableobject = (DataWatcher$WatchableObject) this.field_75695_b.get(i);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.func_85055_a(throwable, "Getting synched entity data");
            CrashReportCategory crashreportcategory = crashreport.func_85058_a("Synched entity data");

            crashreportcategory.func_71507_a("Data ID", i);
            throw new ReportedException(crashreport);
        }

        this.field_75694_d.readLock().unlock();
        return datawatcher$watchableobject;
    }

    public void func_75692_b(int i, Object object) {
        DataWatcher$WatchableObject datawatcher$watchableobject = this.func_75691_i(i);

        if (ObjectUtils.notEqual(object, datawatcher$watchableobject.func_75669_b())) {
            datawatcher$watchableobject.func_75673_a(object);
            this.field_151511_a.func_145781_i(i);
            datawatcher$watchableobject.func_75671_a(true);
            this.field_75696_c = true;
        }

    }

    public void func_82708_h(int i) {
        DataWatcher$WatchableObject.access$002(this.func_75691_i(i), true);
        this.field_75696_c = true;
    }

    public boolean func_75684_a() {
        return this.field_75696_c;
    }

    public static void func_151507_a(List list, PacketBuffer packetbuffer) {
        if (list != null) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                DataWatcher$WatchableObject datawatcher$watchableobject = (DataWatcher$WatchableObject) iterator.next();

                func_151510_a(packetbuffer, datawatcher$watchableobject);
            }
        }

        packetbuffer.writeByte(127);
    }

    public List func_75688_b() {
        ArrayList arraylist = null;

        if (this.field_75696_c) {
            this.field_75694_d.readLock().lock();
            Iterator iterator = this.field_75695_b.values().iterator();

            while (iterator.hasNext()) {
                DataWatcher$WatchableObject datawatcher$watchableobject = (DataWatcher$WatchableObject) iterator.next();

                if (datawatcher$watchableobject.func_75670_d()) {
                    datawatcher$watchableobject.func_75671_a(false);
                    if (arraylist == null) {
                        arraylist = new ArrayList();
                    }

                    arraylist.add(datawatcher$watchableobject);
                }
            }

            this.field_75694_d.readLock().unlock();
        }

        this.field_75696_c = false;
        return arraylist;
    }

    public void func_151509_a(PacketBuffer packetbuffer) {
        this.field_75694_d.readLock().lock();
        Iterator iterator = this.field_75695_b.values().iterator();

        while (iterator.hasNext()) {
            DataWatcher$WatchableObject datawatcher$watchableobject = (DataWatcher$WatchableObject) iterator.next();

            func_151510_a(packetbuffer, datawatcher$watchableobject);
        }

        this.field_75694_d.readLock().unlock();
        packetbuffer.writeByte(127);
    }

    public List func_75685_c() {
        ArrayList arraylist = null;

        this.field_75694_d.readLock().lock();

        DataWatcher$WatchableObject datawatcher$watchableobject;

        for (Iterator iterator = this.field_75695_b.values().iterator(); iterator.hasNext(); arraylist.add(datawatcher$watchableobject)) {
            datawatcher$watchableobject = (DataWatcher$WatchableObject) iterator.next();
            if (arraylist == null) {
                arraylist = new ArrayList();
            }
        }

        this.field_75694_d.readLock().unlock();
        return arraylist;
    }

    private static void func_151510_a(PacketBuffer packetbuffer, DataWatcher$WatchableObject datawatcher$watchableobject) {
        int i = (datawatcher$watchableobject.func_75674_c() << 5 | datawatcher$watchableobject.func_75672_a() & 31) & 255;

        packetbuffer.writeByte(i);
        switch (datawatcher$watchableobject.func_75674_c()) {
            case 0:
                packetbuffer.writeByte((Byte) datawatcher$watchableobject.func_75669_b());
                break;
            case 1:
                packetbuffer.writeShort((Short) datawatcher$watchableobject.func_75669_b());
                break;
            case 2:
                packetbuffer.writeInt((Integer) datawatcher$watchableobject.func_75669_b());
                break;
            case 3:
                packetbuffer.writeFloat((Float) datawatcher$watchableobject.func_75669_b());
                break;
            case 4:
                packetbuffer.func_150785_a((String) datawatcher$watchableobject.func_75669_b());
                break;
            case 5:
                ItemStack itemstack = (ItemStack) datawatcher$watchableobject.func_75669_b();

                packetbuffer.func_150788_a(itemstack);
                break;
            case 6:
                ChunkCoordinates chunkcoordinates = (ChunkCoordinates) datawatcher$watchableobject.func_75669_b();

                packetbuffer.writeInt(chunkcoordinates.field_71574_a);
                packetbuffer.writeInt(chunkcoordinates.field_71572_b);
                packetbuffer.writeInt(chunkcoordinates.field_71573_c);
        }

    }

    public static List func_151508_b(PacketBuffer packetbuffer) {
        ArrayList arraylist = null;

        for (byte b0 = packetbuffer.readByte(); b0 != 127; b0 = packetbuffer.readByte()) {
            if (arraylist == null) {
                arraylist = new ArrayList();
            }

            int i = (b0 & 224) >> 5;
            int j = b0 & 31;
            DataWatcher$WatchableObject datawatcher$watchableobject = null;

            switch (i) {
                case 0:
                    datawatcher$watchableobject = new DataWatcher$WatchableObject(i, j, packetbuffer.readByte());
                    break;
                case 1:
                    datawatcher$watchableobject = new DataWatcher$WatchableObject(i, j, packetbuffer.readShort());
                    break;
                case 2:
                    datawatcher$watchableobject = new DataWatcher$WatchableObject(i, j, packetbuffer.readInt());
                    break;
                case 3:
                    datawatcher$watchableobject = new DataWatcher$WatchableObject(i, j, packetbuffer.readFloat());
                    break;
                case 4:
                    datawatcher$watchableobject = new DataWatcher$WatchableObject(i, j, packetbuffer.func_150789_c(32767));
                    break;
                case 5:
                    datawatcher$watchableobject = new DataWatcher$WatchableObject(i, j, packetbuffer.func_150791_c());
                    break;
                case 6:
                    int k = packetbuffer.readInt();
                    int l = packetbuffer.readInt();
                    int i1 = packetbuffer.readInt();

                    datawatcher$watchableobject = new DataWatcher$WatchableObject(i, j, new ChunkCoordinates(k, l, i1));
            }

            arraylist.add(datawatcher$watchableobject);
        }

        return arraylist;
    }

    public boolean func_92085_d() {
        return this.field_92086_a;
    }

    public void func_111144_e() {
        this.field_75696_c = false;
    }

    static {
        DataWatcher.field_75697_a.put(Byte.class, 0);
        DataWatcher.field_75697_a.put(Short.class, 1);
        DataWatcher.field_75697_a.put(Integer.class, 2);
        DataWatcher.field_75697_a.put(Float.class, 3);
        DataWatcher.field_75697_a.put(String.class, 4);
        DataWatcher.field_75697_a.put(ItemStack.class, 5);
        DataWatcher.field_75697_a.put(ChunkCoordinates.class, 6);
    }
}
