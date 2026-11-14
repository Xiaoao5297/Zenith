package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagCompound extends NBTBase {

    private static final Logger field_150301_b = LogManager.getLogger();
    private Map field_74784_a = new HashMap();

    public NBTTagCompound() {}

    @Override
    void func_74734_a(DataOutput dataoutput) {
        Iterator iterator = this.field_74784_a.keySet().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            NBTBase nbtbase = (NBTBase) this.field_74784_a.get(s);

            func_150298_a(s, nbtbase, dataoutput);
        }

        dataoutput.writeByte(0);
    }

    @Override
    void func_152446_a(DataInput datainput, int i, NBTSizeTracker nbtsizetracker) {
        if (i > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        } else {
            this.field_74784_a.clear();

            byte b0;

            while ((b0 = func_152447_a(datainput, nbtsizetracker)) != 0) {
                String s = func_152448_b(datainput, nbtsizetracker);

                nbtsizetracker.func_152450_a((long) (16 * s.length()));
                NBTBase nbtbase = func_152449_a(b0, s, datainput, i + 1, nbtsizetracker);

                this.field_74784_a.put(s, nbtbase);
            }

        }
    }

    public Set func_150296_c() {
        return this.field_74784_a.keySet();
    }

    @Override
    public byte func_74732_a() {
        return 10;
    }

    public void func_74782_a(String s, NBTBase nbtbase) {
        this.field_74784_a.put(s, nbtbase);
    }

    public void func_74774_a(String s, byte b0) {
        this.field_74784_a.put(s, new NBTTagByte(b0));
    }

    public void func_74777_a(String s, short short0) {
        this.field_74784_a.put(s, new NBTTagShort(short0));
    }

    public void func_74768_a(String s, int i) {
        this.field_74784_a.put(s, new NBTTagInt(i));
    }

    public void func_74772_a(String s, long i) {
        this.field_74784_a.put(s, new NBTTagLong(i));
    }

    public void func_74776_a(String s, float f) {
        this.field_74784_a.put(s, new NBTTagFloat(f));
    }

    public void func_74780_a(String s, double d0) {
        this.field_74784_a.put(s, new NBTTagDouble(d0));
    }

    public void func_74778_a(String s, String s1) {
        this.field_74784_a.put(s, new NBTTagString(s1));
    }

    public void func_74773_a(String s, byte[] abyte) {
        this.field_74784_a.put(s, new NBTTagByteArray(abyte));
    }

    public void func_74783_a(String s, int[] aint) {
        this.field_74784_a.put(s, new NBTTagIntArray(aint));
    }

    public void func_74757_a(String s, boolean flag) {
        this.func_74774_a(s, (byte) (flag ? 1 : 0));
    }

    public NBTBase func_74781_a(String s) {
        return (NBTBase) this.field_74784_a.get(s);
    }

    public byte func_150299_b(String s) {
        NBTBase nbtbase = (NBTBase) this.field_74784_a.get(s);

        return nbtbase != null ? nbtbase.func_74732_a() : 0;
    }

    public boolean func_74764_b(String s) {
        return this.field_74784_a.containsKey(s);
    }

    public boolean func_150297_b(String s, int i) {
        byte b0 = this.func_150299_b(s);

        return b0 == i ? true : (i != 99 ? false : b0 == 1 || b0 == 2 || b0 == 3 || b0 == 4 || b0 == 5 || b0 == 6);
    }

    public byte func_74771_c(String s) {
        try {
            return !this.field_74784_a.containsKey(s) ? 0 : ((NBTBase$NBTPrimitive) this.field_74784_a.get(s)).func_150290_f();
        } catch (ClassCastException classcastexception) {
            return 0;
        }
    }

    public short func_74765_d(String s) {
        try {
            return !this.field_74784_a.containsKey(s) ? 0 : ((NBTBase$NBTPrimitive) this.field_74784_a.get(s)).func_150289_e();
        } catch (ClassCastException classcastexception) {
            return 0;
        }
    }

    public int func_74762_e(String s) {
        try {
            return !this.field_74784_a.containsKey(s) ? 0 : ((NBTBase$NBTPrimitive) this.field_74784_a.get(s)).func_150287_d();
        } catch (ClassCastException classcastexception) {
            return 0;
        }
    }

    public long func_74763_f(String s) {
        try {
            return !this.field_74784_a.containsKey(s) ? 0L : ((NBTBase$NBTPrimitive) this.field_74784_a.get(s)).func_150291_c();
        } catch (ClassCastException classcastexception) {
            return 0L;
        }
    }

    public float func_74760_g(String s) {
        try {
            return !this.field_74784_a.containsKey(s) ? 0.0F : ((NBTBase$NBTPrimitive) this.field_74784_a.get(s)).func_150288_h();
        } catch (ClassCastException classcastexception) {
            return 0.0F;
        }
    }

    public double func_74769_h(String s) {
        try {
            return !this.field_74784_a.containsKey(s) ? 0.0D : ((NBTBase$NBTPrimitive) this.field_74784_a.get(s)).func_150286_g();
        } catch (ClassCastException classcastexception) {
            return 0.0D;
        }
    }

    public String func_74779_i(String s) {
        try {
            return !this.field_74784_a.containsKey(s) ? "" : ((NBTBase) this.field_74784_a.get(s)).func_150285_a_();
        } catch (ClassCastException classcastexception) {
            return "";
        }
    }

    public byte[] func_74770_j(String s) {
        try {
            return !this.field_74784_a.containsKey(s) ? new byte[0] : ((NBTTagByteArray) this.field_74784_a.get(s)).func_150292_c();
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.func_82581_a(s, 7, classcastexception));
        }
    }

    public int[] func_74759_k(String s) {
        try {
            return !this.field_74784_a.containsKey(s) ? new int[0] : ((NBTTagIntArray) this.field_74784_a.get(s)).func_150302_c();
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.func_82581_a(s, 11, classcastexception));
        }
    }

    public NBTTagCompound func_74775_l(String s) {
        try {
            return !this.field_74784_a.containsKey(s) ? new NBTTagCompound() : (NBTTagCompound) this.field_74784_a.get(s);
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.func_82581_a(s, 10, classcastexception));
        }
    }

    public NBTTagList func_150295_c(String s, int i) {
        try {
            if (this.func_150299_b(s) != 9) {
                return new NBTTagList();
            } else {
                NBTTagList nbttaglist = (NBTTagList) this.field_74784_a.get(s);

                return nbttaglist.func_74745_c() > 0 && nbttaglist.func_150303_d() != i ? new NBTTagList() : nbttaglist;
            }
        } catch (ClassCastException classcastexception) {
            throw new ReportedException(this.func_82581_a(s, 9, classcastexception));
        }
    }

    public boolean func_74767_n(String s) {
        return this.func_74771_c(s) != 0;
    }

    public void func_82580_o(String s) {
        this.field_74784_a.remove(s);
    }

    @Override
    public String toString() {
        String s = "{";

        String s1;

        for (Iterator iterator = this.field_74784_a.keySet().iterator(); iterator.hasNext(); s = s + s1 + ':' + this.field_74784_a.get(s1) + ',') {
            s1 = (String) iterator.next();
        }

        return s + "}";
    }

    public boolean func_82582_d() {
        return this.field_74784_a.isEmpty();
    }

    private CrashReport func_82581_a(String s, int i, ClassCastException classcastexception) {
        CrashReport crashreport = CrashReport.func_85055_a(classcastexception, "Reading NBT data");
        CrashReportCategory crashreportcategory = crashreport.func_85057_a("Corrupt NBT tag", 1);

        crashreportcategory.func_71500_a("Tag type found", new NBTTagCompound$1(this, s));
        crashreportcategory.func_71500_a("Tag type expected", new NBTTagCompound$2(this, i));
        crashreportcategory.func_71507_a("Tag name", s);
        return crashreport;
    }

    @Override
    public NBTBase func_74737_b() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        Iterator iterator = this.field_74784_a.keySet().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            nbttagcompound.func_74782_a(s, ((NBTBase) this.field_74784_a.get(s)).func_74737_b());
        }

        return nbttagcompound;
    }

    @Override
    public boolean equals(Object object) {
        if (super.equals(object)) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) object;

            return this.field_74784_a.entrySet().equals(nbttagcompound.field_74784_a.entrySet());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.field_74784_a.hashCode();
    }

    private static void func_150298_a(String s, NBTBase nbtbase, DataOutput dataoutput) {
        dataoutput.writeByte(nbtbase.func_74732_a());
        if (nbtbase.func_74732_a() != 0) {
            dataoutput.writeUTF(s);
            nbtbase.func_74734_a(dataoutput);
        }
    }

    private static byte func_152447_a(DataInput datainput, NBTSizeTracker nbtsizetracker) {
        return datainput.readByte();
    }

    private static String func_152448_b(DataInput datainput, NBTSizeTracker nbtsizetracker) {
        return datainput.readUTF();
    }

    static NBTBase func_152449_a(byte b0, String s, DataInput datainput, int i, NBTSizeTracker nbtsizetracker) {
        NBTBase nbtbase = NBTBase.func_150284_a(b0);

        try {
            nbtbase.func_152446_a(datainput, i, nbtsizetracker);
            return nbtbase;
        } catch (IOException ioexception) {
            CrashReport crashreport = CrashReport.func_85055_a(ioexception, "Loading NBT data");
            CrashReportCategory crashreportcategory = crashreport.func_85058_a("NBT Tag");

            crashreportcategory.func_71507_a("Tag name", s);
            crashreportcategory.func_71507_a("Tag type", b0);
            throw new ReportedException(crashreport);
        }
    }
}
