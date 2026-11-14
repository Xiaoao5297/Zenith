package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class NBTTagString extends NBTBase {

    private String field_74751_a;

    public NBTTagString() {
        this.field_74751_a = "";
    }

    public NBTTagString(String s) {
        this.field_74751_a = s;
        if (s == null) {
            throw new IllegalArgumentException("Empty string not allowed");
        }
    }

    @Override
    void func_74734_a(DataOutput dataoutput) {
        dataoutput.writeUTF(this.field_74751_a);
    }

    @Override
    void func_152446_a(DataInput datainput, int i, NBTSizeTracker nbtsizetracker) {
        this.field_74751_a = datainput.readUTF();
        nbtsizetracker.func_152450_a((long) (16 * this.field_74751_a.length()));
    }

    @Override
    public byte func_74732_a() {
        return 8;
    }

    @Override
    public String toString() {
        return "\"" + this.field_74751_a + "\"";
    }

    @Override
    public NBTBase func_74737_b() {
        return new NBTTagString(this.field_74751_a);
    }

    @Override
    public boolean equals(Object object) {
        if (!super.equals(object)) {
            return false;
        } else {
            NBTTagString nbttagstring = (NBTTagString) object;

            return this.field_74751_a == null && nbttagstring.field_74751_a == null || this.field_74751_a != null && this.field_74751_a.equals(nbttagstring.field_74751_a);
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.field_74751_a.hashCode();
    }

    @Override
    public String func_150285_a_() {
        return this.field_74751_a;
    }
}
