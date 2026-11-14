package net.minecraft.nbt;

import java.util.concurrent.Callable;

class NBTTagCompound$1 implements Callable {

    NBTTagCompound$1(NBTTagCompound nbttagcompound, String s) {
        this.field_82584_b = nbttagcompound;
        this.field_82585_a = s;
    }

    public String call() {
        return NBTBase.field_82578_b[((NBTBase) NBTTagCompound.access$000(this.field_82584_b).get(this.field_82585_a)).func_74732_a()];
    }
}
