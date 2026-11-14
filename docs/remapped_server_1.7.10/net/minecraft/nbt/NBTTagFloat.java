package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import net.minecraft.util.MathHelper;

public class NBTTagFloat extends NBTBase$NBTPrimitive {

    private float field_74750_a;

    NBTTagFloat() {}

    public NBTTagFloat(float f) {
        this.field_74750_a = f;
    }

    @Override
    void func_74734_a(DataOutput dataoutput) {
        dataoutput.writeFloat(this.field_74750_a);
    }

    @Override
    void func_152446_a(DataInput datainput, int i, NBTSizeTracker nbtsizetracker) {
        nbtsizetracker.func_152450_a(32L);
        this.field_74750_a = datainput.readFloat();
    }

    @Override
    public byte func_74732_a() {
        return 5;
    }

    @Override
    public String toString() {
        return "" + this.field_74750_a + "f";
    }

    @Override
    public NBTBase func_74737_b() {
        return new NBTTagFloat(this.field_74750_a);
    }

    @Override
    public boolean equals(Object object) {
        if (super.equals(object)) {
            NBTTagFloat nbttagfloat = (NBTTagFloat) object;

            return this.field_74750_a == nbttagfloat.field_74750_a;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Float.floatToIntBits(this.field_74750_a);
    }

    @Override
    public long func_150291_c() {
        return (long) this.field_74750_a;
    }

    @Override
    public int func_150287_d() {
        return MathHelper.func_76141_d(this.field_74750_a);
    }

    @Override
    public short func_150289_e() {
        return (short) (MathHelper.func_76141_d(this.field_74750_a) & '\uffff');
    }

    @Override
    public byte func_150290_f() {
        return (byte) (MathHelper.func_76141_d(this.field_74750_a) & 255);
    }

    @Override
    public double func_150286_g() {
        return (double) this.field_74750_a;
    }

    @Override
    public float func_150288_h() {
        return this.field_74750_a;
    }
}
