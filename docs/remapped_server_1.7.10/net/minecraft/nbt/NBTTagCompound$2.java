package net.minecraft.nbt;

import java.util.concurrent.Callable;

class NBTTagCompound$2 implements Callable {

    NBTTagCompound$2(NBTTagCompound nbttagcompound, int i) {
        this.field_82587_b = nbttagcompound;
        this.field_82588_a = i;
    }

    public String call() {
        return NBTBase.field_82578_b[this.field_82588_a];
    }
}
