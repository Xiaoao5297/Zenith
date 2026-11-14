package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NBTTagList extends NBTBase {

    private List field_74747_a = new ArrayList();
    private byte field_74746_b = 0;

    public NBTTagList() {}

    @Override
    void func_74734_a(DataOutput dataoutput) {
        if (!this.field_74747_a.isEmpty()) {
            this.field_74746_b = ((NBTBase) this.field_74747_a.get(0)).func_74732_a();
        } else {
            this.field_74746_b = 0;
        }

        dataoutput.writeByte(this.field_74746_b);
        dataoutput.writeInt(this.field_74747_a.size());

        for (int i = 0; i < this.field_74747_a.size(); ++i) {
            ((NBTBase) this.field_74747_a.get(i)).func_74734_a(dataoutput);
        }

    }

    @Override
    void func_152446_a(DataInput datainput, int i, NBTSizeTracker nbtsizetracker) {
        if (i > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        } else {
            nbtsizetracker.func_152450_a(8L);
            this.field_74746_b = datainput.readByte();
            int j = datainput.readInt();

            this.field_74747_a = new ArrayList();

            for (int k = 0; k < j; ++k) {
                NBTBase nbtbase = NBTBase.func_150284_a(this.field_74746_b);

                nbtbase.func_152446_a(datainput, i + 1, nbtsizetracker);
                this.field_74747_a.add(nbtbase);
            }

        }
    }

    @Override
    public byte func_74732_a() {
        return 9;
    }

    @Override
    public String toString() {
        String s = "[";
        int i = 0;

        for (Iterator iterator = this.field_74747_a.iterator(); iterator.hasNext(); ++i) {
            NBTBase nbtbase = (NBTBase) iterator.next();

            s = s + "" + i + ':' + nbtbase + ',';
        }

        return s + "]";
    }

    public void func_74742_a(NBTBase nbtbase) {
        if (this.field_74746_b == 0) {
            this.field_74746_b = nbtbase.func_74732_a();
        } else if (this.field_74746_b != nbtbase.func_74732_a()) {
            System.err.println("WARNING: Adding mismatching tag types to tag list");
            return;
        }

        this.field_74747_a.add(nbtbase);
    }

    public NBTTagCompound func_150305_b(int i) {
        if (i >= 0 && i < this.field_74747_a.size()) {
            NBTBase nbtbase = (NBTBase) this.field_74747_a.get(i);

            return nbtbase.func_74732_a() == 10 ? (NBTTagCompound) nbtbase : new NBTTagCompound();
        } else {
            return new NBTTagCompound();
        }
    }

    public int[] func_150306_c(int i) {
        if (i >= 0 && i < this.field_74747_a.size()) {
            NBTBase nbtbase = (NBTBase) this.field_74747_a.get(i);

            return nbtbase.func_74732_a() == 11 ? ((NBTTagIntArray) nbtbase).func_150302_c() : new int[0];
        } else {
            return new int[0];
        }
    }

    public double func_150309_d(int i) {
        if (i >= 0 && i < this.field_74747_a.size()) {
            NBTBase nbtbase = (NBTBase) this.field_74747_a.get(i);

            return nbtbase.func_74732_a() == 6 ? ((NBTTagDouble) nbtbase).func_150286_g() : 0.0D;
        } else {
            return 0.0D;
        }
    }

    public float func_150308_e(int i) {
        if (i >= 0 && i < this.field_74747_a.size()) {
            NBTBase nbtbase = (NBTBase) this.field_74747_a.get(i);

            return nbtbase.func_74732_a() == 5 ? ((NBTTagFloat) nbtbase).func_150288_h() : 0.0F;
        } else {
            return 0.0F;
        }
    }

    public String func_150307_f(int i) {
        if (i >= 0 && i < this.field_74747_a.size()) {
            NBTBase nbtbase = (NBTBase) this.field_74747_a.get(i);

            return nbtbase.func_74732_a() == 8 ? nbtbase.func_150285_a_() : nbtbase.toString();
        } else {
            return "";
        }
    }

    public int func_74745_c() {
        return this.field_74747_a.size();
    }

    @Override
    public NBTBase func_74737_b() {
        NBTTagList nbttaglist = new NBTTagList();

        nbttaglist.field_74746_b = this.field_74746_b;
        Iterator iterator = this.field_74747_a.iterator();

        while (iterator.hasNext()) {
            NBTBase nbtbase = (NBTBase) iterator.next();
            NBTBase nbtbase1 = nbtbase.func_74737_b();

            nbttaglist.field_74747_a.add(nbtbase1);
        }

        return nbttaglist;
    }

    @Override
    public boolean equals(Object object) {
        if (super.equals(object)) {
            NBTTagList nbttaglist = (NBTTagList) object;

            if (this.field_74746_b == nbttaglist.field_74746_b) {
                return this.field_74747_a.equals(nbttaglist.field_74747_a);
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.field_74747_a.hashCode();
    }

    public int func_150303_d() {
        return this.field_74746_b;
    }
}
