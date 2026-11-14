package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.Arrays;

public class NBTTagIntArray extends NBTBase {

    private int[] field_74749_a;

    NBTTagIntArray() {}

    public NBTTagIntArray(int[] aint) {
        this.field_74749_a = aint;
    }

    @Override
    void func_74734_a(DataOutput dataoutput) {
        dataoutput.writeInt(this.field_74749_a.length);

        for (int i = 0; i < this.field_74749_a.length; ++i) {
            dataoutput.writeInt(this.field_74749_a[i]);
        }

    }

    @Override
    void func_152446_a(DataInput datainput, int i, NBTSizeTracker nbtsizetracker) {
        int j = datainput.readInt();

        nbtsizetracker.func_152450_a((long) (32 * j));
        this.field_74749_a = new int[j];

        for (int k = 0; k < j; ++k) {
            this.field_74749_a[k] = datainput.readInt();
        }

    }

    @Override
    public byte func_74732_a() {
        return 11;
    }

    @Override
    public String toString() {
        String s = "[";
        int[] aint = this.field_74749_a;
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int k = aint[j];

            s = s + k + ",";
        }

        return s + "]";
    }

    @Override
    public NBTBase func_74737_b() {
        int[] aint = new int[this.field_74749_a.length];

        System.arraycopy(this.field_74749_a, 0, aint, 0, this.field_74749_a.length);
        return new NBTTagIntArray(aint);
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object) ? Arrays.equals(this.field_74749_a, ((NBTTagIntArray) object).field_74749_a) : false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(this.field_74749_a);
    }

    public int[] func_150302_c() {
        return this.field_74749_a;
    }
}
