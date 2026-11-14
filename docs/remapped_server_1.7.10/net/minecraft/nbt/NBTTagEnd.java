package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class NBTTagEnd extends NBTBase {

    NBTTagEnd() {}

    @Override
    void func_152446_a(DataInput datainput, int i, NBTSizeTracker nbtsizetracker) {}

    @Override
    void func_74734_a(DataOutput dataoutput) {}

    @Override
    public byte func_74732_a() {
        return 0;
    }

    @Override
    public String toString() {
        return "END";
    }

    @Override
    public NBTBase func_74737_b() {
        return new NBTTagEnd();
    }
}
