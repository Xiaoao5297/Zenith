package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class NBTTagShort extends NBTBase$NBTPrimitive {

    private short field_74752_a;

    public NBTTagShort() {}

    public NBTTagShort(short short0) {
        this.field_74752_a = short0;
    }

    @Override
    void func_74734_a(DataOutput dataoutput) {
        dataoutput.writeShort(this.field_74752_a);
    }

    @Override
    void func_152446_a(DataInput datainput, int i, NBTSizeTracker nbtsizetracker) {
        nbtsizetracker.func_152450_a(16L);
        this.field_74752_a = datainput.readShort();
    }

    @Override
    public byte func_74732_a() {
        return 2;
    }

    @Override
    public String toString() {
        return "" + this.field_74752_a + "s";
    }

    @Override
    public NBTBase func_74737_b() {
        return new NBTTagShort(this.field_74752_a);
    }

    @Override
    public boolean equals(Object object) {
        if (super.equals(object)) {
            NBTTagShort nbttagshort = (NBTTagShort) object;

            return this.field_74752_a == nbttagshort.field_74752_a;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.field_74752_a;
    }

    @Override
    public long func_150291_c() {
        return (long) this.field_74752_a;
    }

    @Override
    public int func_150287_d() {
        return this.field_74752_a;
    }

    @Override
    public short func_150289_e() {
        return this.field_74752_a;
    }

    @Override
    public byte func_150290_f() {
        return (byte) (this.field_74752_a & 255);
    }

    @Override
    public double func_150286_g() {
        return (double) this.field_74752_a;
    }

    @Override
    public float func_150288_h() {
        return (float) this.field_74752_a;
    }
}
