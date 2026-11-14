package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import net.minecraft.util.MathHelper;

public class NBTTagDouble extends NBTBase$NBTPrimitive {

    private double field_74755_a;

    NBTTagDouble() {}

    public NBTTagDouble(double d0) {
        this.field_74755_a = d0;
    }

    @Override
    void func_74734_a(DataOutput dataoutput) {
        dataoutput.writeDouble(this.field_74755_a);
    }

    @Override
    void func_152446_a(DataInput datainput, int i, NBTSizeTracker nbtsizetracker) {
        nbtsizetracker.func_152450_a(64L);
        this.field_74755_a = datainput.readDouble();
    }

    @Override
    public byte func_74732_a() {
        return 6;
    }

    @Override
    public String toString() {
        return "" + this.field_74755_a + "d";
    }

    @Override
    public NBTBase func_74737_b() {
        return new NBTTagDouble(this.field_74755_a);
    }

    @Override
    public boolean equals(Object object) {
        if (super.equals(object)) {
            NBTTagDouble nbttagdouble = (NBTTagDouble) object;

            return this.field_74755_a == nbttagdouble.field_74755_a;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        long i = Double.doubleToLongBits(this.field_74755_a);

        return super.hashCode() ^ (int) (i ^ i >>> 32);
    }

    @Override
    public long func_150291_c() {
        return (long) Math.floor(this.field_74755_a);
    }

    @Override
    public int func_150287_d() {
        return MathHelper.func_76128_c(this.field_74755_a);
    }

    @Override
    public short func_150289_e() {
        return (short) (MathHelper.func_76128_c(this.field_74755_a) & '\uffff');
    }

    @Override
    public byte func_150290_f() {
        return (byte) (MathHelper.func_76128_c(this.field_74755_a) & 255);
    }

    @Override
    public double func_150286_g() {
        return this.field_74755_a;
    }

    @Override
    public float func_150288_h() {
        return (float) this.field_74755_a;
    }
}
