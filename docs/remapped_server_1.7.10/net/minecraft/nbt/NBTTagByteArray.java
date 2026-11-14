package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.Arrays;

public class NBTTagByteArray extends NBTBase {

    private byte[] field_74754_a;

    NBTTagByteArray() {}

    public NBTTagByteArray(byte[] abyte) {
        this.field_74754_a = abyte;
    }

    @Override
    void func_74734_a(DataOutput dataoutput) {
        dataoutput.writeInt(this.field_74754_a.length);
        dataoutput.write(this.field_74754_a);
    }

    @Override
    void func_152446_a(DataInput datainput, int i, NBTSizeTracker nbtsizetracker) {
        int j = datainput.readInt();

        nbtsizetracker.func_152450_a((long) (8 * j));
        this.field_74754_a = new byte[j];
        datainput.readFully(this.field_74754_a);
    }

    @Override
    public byte func_74732_a() {
        return 7;
    }

    @Override
    public String toString() {
        return "[" + this.field_74754_a.length + " bytes]";
    }

    @Override
    public NBTBase func_74737_b() {
        byte[] abyte = new byte[this.field_74754_a.length];

        System.arraycopy(this.field_74754_a, 0, abyte, 0, this.field_74754_a.length);
        return new NBTTagByteArray(abyte);
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object) ? Arrays.equals(this.field_74754_a, ((NBTTagByteArray) object).field_74754_a) : false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(this.field_74754_a);
    }

    public byte[] func_150292_c() {
        return this.field_74754_a;
    }
}
