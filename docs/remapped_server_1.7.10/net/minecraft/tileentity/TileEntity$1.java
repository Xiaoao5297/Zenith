package net.minecraft.tileentity;

import java.util.concurrent.Callable;

class TileEntity$1 implements Callable {

    TileEntity$1(TileEntity tileentity) {
        this.field_150830_a = tileentity;
    }

    public String call() {
        return (String) TileEntity.access$000().get(this.field_150830_a.getClass()) + " // " + this.field_150830_a.getClass().getCanonicalName();
    }
}
