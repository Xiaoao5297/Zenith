package net.minecraft.world;

import java.util.concurrent.Callable;

class World$4 implements Callable {

    World$4(World world) {
        this.field_151308_a = world;
    }

    public String call() {
        return this.field_151308_a.field_73020_y.func_73148_d();
    }
}
