package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class NBTTagInt extends NBTBase$NBTPrimitive {

    private int field_74748_a;

    NBTTagInt() {}

    public NBTTagInt(int i) {
        this.field_74748_a = i;
    }

    @Override
    void func_74734_a(DataOutput dataoutput) {
        dataoutput.writeInt(this.field_74748_a);
    }

    @Override
    void func_152446_a(DataInput datainput, int i, NBTSizeTracker nbtsizetracker) {
        nbtsizetracker.func_152450_a(32L);
        this.field_74748_a = datainput.readInt();
    }

    @Override
    public byte func_74732_a() {
        return 3;
    }

    @Override
    public String toString() {
        return "" + this.field_74748_a;
    }

    @Override
    public NBTBase func_74737_b() {
        return new NBTTagInt(this.field_74748_a);
    }

    @Override
    public boolean equals(Object object) {
        if (super.equals(object)) {
            NBTTagInt nbttagint = (NBTTagInt) object;

            return this.field_74748_a == nbttagint.field_74748_a;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.field_74748_a;
    }

    @Override
    public long func_150291_c() {
        return (long) this.field_74748_a;
    }

    @Override
    public int func_150287_d() {
        return this.field_74748_a;
    }

    @Override
    public short func_150289_e() {
        return (short) (this.field_74748_a & '\uffff');
    }

    @Override
    public byte func_150290_f() {
        return (byte) (this.field_74748_a & 255);
    }

    @Override
    public double func_150286_g() {
        return (double) this.field_74748_a;
    }

    @Override
    public float func_150288_h() {
        return (float) this.field_74748_a;
    }
}
