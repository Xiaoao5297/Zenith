package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class WorldInfo$1 implements Callable {

    WorldInfo$1(WorldInfo worldinfo) {
        this.field_85143_a = worldinfo;
    }

    public String call() {
        return String.valueOf(this.field_85143_a.func_76063_b());
    }
}
